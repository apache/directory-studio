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

package org.apache.directory.studio.connection.ui.dnd;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.ui.ConnectionUIConstants;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;


/**
 * A {@link Transfer} that could be used to transfer {@link Connection} objects.
 * Note that only the connection id is converted to a platform specific 
 * representation, not the complete object. To convert it back to an {@link Connection} 
 * object the {@link ConnectionManager#getConnectionById(String)} method is invoked.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class ConnectionTransfer extends ByteArrayTransfer
{
    /** The Constant TYPENAME. */
    private static final String TYPENAME = ConnectionUIConstants.TYPENAME;

    /** The Constant TYPEID. */
    private static final int TYPEID = registerType( TYPENAME );

    /** The instance. */
    private static ConnectionTransfer instance = new ConnectionTransfer();


    /**
     * Creates a new instance of ConnectionTransfer.
     */
    private ConnectionTransfer()
    {
        super();
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
     * This implementation only accepts {@link Connection} and {@link ConnectionFolder} objects. 
     * It just converts the id of the connection or connection folder to the platform 
     * specific representation.
     */
    @Override
    public void javaToNative( Object object, TransferData transferData )
    {
        if ( !( object instanceof Object[] ) )
        {
            return;
        }

        if ( isSupportedType( transferData ) )
        {
            Object[] objects = ( Object[] ) object;
            
            try ( ByteArrayOutputStream out = new ByteArrayOutputStream() )
            {
                try ( DataOutputStream writeOut = new DataOutputStream( out ) )
                {
                    for ( int i = 0; i < objects.length; i++ )
                    {
                        if ( objects[i] instanceof Connection )
                        {
                            byte[] idBytes = ( ( Connection ) objects[i] ).getConnectionParameter().getId().getBytes();
                            writeOut.writeInt( idBytes.length );
                            writeOut.write( idBytes );
                        }
                        else if ( objects[i] instanceof ConnectionFolder )
                        {
                            byte[] idBytes = ( ( ConnectionFolder ) objects[i] ).getId().getBytes();
                            writeOut.writeInt( idBytes.length );
                            writeOut.write( idBytes );
                        }
                    }
        
                    byte[] buffer = out.toByteArray();
                    super.javaToNative( buffer, transferData );
                }
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
     * to the connection id or connection folder id and invokes 
     * {@link ConnectionManager#getConnectionById(String)} to get the
     * {@link Connection} object or {@link ConnectionFolderManager#getConnectionFolderById(String)}
     * to get the {@link ConnectionFolder} object.
     */
    @Override
    public Object nativeToJava( TransferData transferData )
    {
        if ( isSupportedType( transferData ) )
        {
            byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
            
            if ( buffer == null )
            {
                return null;
            }

            List<Object> objectList = new ArrayList<>();
            
            try ( ByteArrayInputStream in = new ByteArrayInputStream( buffer ) )
            {
                try ( DataInputStream readIn = new DataInputStream( in ) )
                {
                    do
                    {
                        if ( readIn.available() > 1 )
                        {
                            int size = readIn.readInt();
                            byte[] idBytes = new byte[size];
                            
                            if ( readIn.read( idBytes ) != size )
                            {
                                // We werev'nt able to read the full data : there is something wrong...
                                return null;
                            }
                            
                            Connection connection = ConnectionCorePlugin.getDefault().getConnectionManager()
                                .getConnectionById( new String( idBytes ) );
                            
                            if ( connection == null )
                            {
                                ConnectionFolder folder = ConnectionCorePlugin.getDefault().getConnectionFolderManager()
                                    .getConnectionFolderById( new String( idBytes ) );
                                
                                if ( folder != null )
                                {
                                    objectList.add( folder );
                                }
                            }
                            else
                            {
                                objectList.add( connection );
                            }
                        }
                    }
                    while ( readIn.available() > 1 );
                }
            }
            catch ( IOException ex )
            {
                return null;
            }

            return objectList.toArray();
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