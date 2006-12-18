/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.ldapstudio.browser.ui.dnd;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public class ConnectionTransfer extends ByteArrayTransfer
{

    private static final String TYPENAME = "org.apache.directory.ldapstudio.browser.connection";

    private static final int TYPEID = registerType( TYPENAME );

    private static ConnectionTransfer instance = new ConnectionTransfer();


    private ConnectionTransfer()
    {
    }


    public static ConnectionTransfer getInstance()
    {
        return instance;
    }


    public void javaToNative( Object object, TransferData transferData )
    {
        if ( object == null || !( object instanceof IConnection[] ) )
            return;

        if ( isSupportedType( transferData ) )
        {
            IConnection[] connections = ( IConnection[] ) object;
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream writeOut = new DataOutputStream( out );

                for ( int i = 0; i < connections.length; i++ )
                {
                    byte[] name = connections[i].getName().getBytes();
                    writeOut.writeInt( name.length );
                    writeOut.write( name );
                }

                byte[] buffer = out.toByteArray();
                writeOut.close();

                super.javaToNative( buffer, transferData );

            }
            catch ( IOException e )
            {
            }
        }
    }


    public Object nativeToJava( TransferData transferData )
    {

        if ( isSupportedType( transferData ) )
        {

            byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
            if ( buffer == null )
                return null;

            List connectionList = new ArrayList();
            try
            {
                ByteArrayInputStream in = new ByteArrayInputStream( buffer );
                DataInputStream readIn = new DataInputStream( in );

                do
                {
                    if ( readIn.available() > 1 )
                    {
                        int size = readIn.readInt();
                        byte[] connectionName = new byte[size];
                        readIn.read( connectionName );
                        IConnection connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection(
                            new String( connectionName ) );
                        connectionList.add( connection );
                    }
                }
                while ( readIn.available() > 1 );

                readIn.close();
            }
            catch ( IOException ex )
            {
                return null;
            }

            return connectionList.toArray( new IConnection[0] );
        }

        return null;
    }


    protected String[] getTypeNames()
    {
        return new String[]
            { TYPENAME };
    }


    protected int[] getTypeIds()
    {
        return new int[]
            { TYPEID };
    }

}