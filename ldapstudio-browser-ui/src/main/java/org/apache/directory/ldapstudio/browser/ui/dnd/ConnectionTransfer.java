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
import org.apache.directory.ldapstudio.browser.core.ConnectionManager;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;


/**
 * A {@link Transfer} that could be used to transfer {@link IConnection} objects.
 * Note that only the connection name is converted to a platform specific 
 * representation, not the complete object. To convert it back to an {@link IConnection} 
 * object the {@link ConnectionManager#getConnection(String)} method is invoked.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionTransfer extends ByteArrayTransfer
{

    /** The Constant TYPENAME. */
    private static final String TYPENAME = "org.apache.directory.ldapstudio.browser.connection";

    /** The Constant TYPEID. */
    private static final int TYPEID = registerType( TYPENAME );

    /** The instance. */
    private static ConnectionTransfer instance = new ConnectionTransfer();


    /**
     * Creates a new instance of ConnectionTransfer.
     */
    private ConnectionTransfer()
    {
    }


    /**
     * Gets the instance.
     * 
     * @return the instance
     */
    public static ConnectionTransfer getInstance()
    {
        return instance;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation only accepts {@link IConnection} objects. 
     * It just converts the name of the connection to the platform 
     * specific representation.
     */
    public void javaToNative( Object object, TransferData transferData )
    {
        if ( object == null || !( object instanceof IConnection[] ) )
        {
            return;
        }

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


    /**
     * {@inheritDoc}
     * 
     * This implementation just converts the platform specific representation
     * to the connection name and invokes 
     * {@link ConnectionManager#getConnection(String)} to get the
     * {@link IConnection} object.
     */
    public Object nativeToJava( TransferData transferData )
    {
        if ( isSupportedType( transferData ) )
        {
            byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
            if ( buffer == null )
            {
                return null;
            }

            List<IConnection> connectionList = new ArrayList<IConnection>();
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


    /**
     * {@inheritDoc}
     */
    protected String[] getTypeNames()
    {
        return new String[]
            { TYPENAME };
    }


    /**
     * {@inheritDoc}
     */
    protected int[] getTypeIds()
    {
        return new int[]
            { TYPEID };
    }

}