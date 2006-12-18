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
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public class EntryTransfer extends ByteArrayTransfer
{

    private static final String TYPENAME = "org.apache.directory.ldapstudio.browser.entry";

    private static final int TYPEID = registerType( TYPENAME );

    private static EntryTransfer instance = new EntryTransfer();


    public static EntryTransfer getInstance()
    {
        return instance;
    }


    private EntryTransfer()
    {
    }


    public void javaToNative( Object object, TransferData transferData )
    {
        if ( object == null || !( object instanceof IEntry[] ) )
            return;

        if ( isSupportedType( transferData ) )
        {
            IEntry[] entries = ( IEntry[] ) object;
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream writeOut = new DataOutputStream( out );

                for ( int i = 0; i < entries.length; i++ )
                {
                    byte[] connectionName = entries[i].getConnection().getName().getBytes();
                    writeOut.writeInt( connectionName.length );
                    writeOut.write( connectionName );
                    byte[] dn = entries[i].getDn().toString().getBytes();
                    writeOut.writeInt( dn.length );
                    writeOut.write( dn );
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

        try
        {

            if ( isSupportedType( transferData ) )
            {

                byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
                if ( buffer == null )
                    return null;

                List entryList = new ArrayList();
                try
                {
                    IConnection connection = null;
                    ByteArrayInputStream in = new ByteArrayInputStream( buffer );
                    DataInputStream readIn = new DataInputStream( in );

                    do
                    {
                        if ( readIn.available() > 1 )
                        {
                            int size = readIn.readInt();
                            byte[] connectionName = new byte[size];
                            readIn.read( connectionName );
                            connection = BrowserCorePlugin.getDefault().getConnectionManager().getConnection(
                                new String( connectionName ) );
                        }

                        IEntry entry = null;
                        if ( readIn.available() > 1 && connection != null )
                        {
                            int size = readIn.readInt();
                            byte[] dn = new byte[size];
                            readIn.read( dn );
                            entry = connection.getEntryFromCache( new DN( new String( dn ) ) );

                        }
                        else
                        {
                            return null;
                        }

                        if ( entry != null )
                        {
                            entryList.add( entry );
                        }
                    }
                    while ( readIn.available() > 1 );

                    readIn.close();
                }
                catch ( IOException ex )
                {
                    return null;
                }

                return entryList.isEmpty() ? null : entryList.toArray( new IEntry[0] );
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
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