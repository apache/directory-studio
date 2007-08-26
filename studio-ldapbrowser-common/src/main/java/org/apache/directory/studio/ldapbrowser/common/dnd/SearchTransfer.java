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

package org.apache.directory.studio.ldapbrowser.common.dnd;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;


/**
 * A {@link Transfer} that could be used to transfer {@link ISearch} objects.
 * Note that only the connection name and search name is converted to a platform specific 
 * representation, not the complete object.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchTransfer extends ByteArrayTransfer
{

    /** The Constant TYPENAME. */
    private static final String TYPENAME = "org.apache.directory.studio.ldapbrowser.search";

    /** The Constant TYPEID. */
    private static final int TYPEID = registerType( TYPENAME );

    /** The instance. */
    private static SearchTransfer instance = new SearchTransfer();


    /**
     * Creates a new instance of SearchTransfer.
     */
    private SearchTransfer()
    {
    }


    /**
     * Gets the instance.
     * 
     * @return the instance
     */
    public static SearchTransfer getInstance()
    {
        return instance;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation only accepts {@link ISearch} objects. 
     * It just converts the name of the connection and the name of the search
     * to the platform specific representation.
     */
    public void javaToNative( Object object, TransferData transferData )
    {
        if ( object == null || !( object instanceof ISearch[] ) )
        {
            return;
        }

        if ( isSupportedType( transferData ) )
        {
            ISearch[] searches = ( ISearch[] ) object;
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                DataOutputStream writeOut = new DataOutputStream( out );

                for ( int i = 0; i < searches.length; i++ )
                {
                    byte[] connectionName = searches[i].getBrowserConnection().getName().getBytes();
                    writeOut.writeInt( connectionName.length );
                    writeOut.write( connectionName );
                    byte[] searchName = searches[i].getName().getBytes();
                    writeOut.writeInt( searchName.length );
                    writeOut.write( searchName );
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
     * to the connection name and search name and invokes 
     * {@link BrowserConnectionManager#getConnection(String)} to get the
     * {@link IBrowserConnection} object and {@link IBrowserConnection#getSearchManager()}
     * to get the {@link ISearch} object.
     */
    public Object nativeToJava( TransferData transferData )
    {
        try
        {
            if ( isSupportedType( transferData ) )
            {
                byte[] buffer = ( byte[] ) super.nativeToJava( transferData );
                if ( buffer == null )
                {
                    return null;
                }

                List<ISearch> searchList = new ArrayList<ISearch>();
                try
                {
                    IBrowserConnection connection = null;
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

                        ISearch search = null;
                        if ( readIn.available() > 1 && connection != null )
                        {
                            int size = readIn.readInt();
                            byte[] searchName = new byte[size];
                            readIn.read( searchName );
                            search = connection.getSearchManager().getSearch( new String( searchName ) );
                        }
                        else
                        {
                            return null;
                        }

                        if ( search != null )
                        {
                            searchList.add( search );
                        }
                    }
                    while ( readIn.available() > 1 );

                    readIn.close();
                }
                catch ( IOException ex )
                {
                    return null;
                }

                return searchList.isEmpty() ? null : searchList.toArray( new ISearch[0] );
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
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