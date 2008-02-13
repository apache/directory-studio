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

package org.apache.directory.studio.connection.ui.actions;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;


/**
 * The SelectionUtils are used to extract specific beans from the current
 * selection (org.eclipse.jface.viewers.ISelection).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class SelectionUtils
{

    /**
     * Gets the Strings contained in the given selection.
     *
     * @param selection the selection
     * @return an array with Strings, may be empty.
     */
    public static String[] getProperties( ISelection selection )
    {
        List<Object> list = getTypes( selection, String.class );
        return list.toArray( new String[list.size()] );
    }


    /**
     * Gets all beans of the requested type contained in the given selection.
     *
     * @param selection the selection
     * @param type the requested type
     * @return a list containg beans of the requesten type
     */
    private static List<Object> getTypes( ISelection selection, Class<?> type )
    {
        List<Object> list = new ArrayList<Object>();
        if ( selection instanceof IStructuredSelection )
        {
            IStructuredSelection structuredSelection = ( IStructuredSelection ) selection;
            Iterator<?> it = structuredSelection.iterator();
            while ( it.hasNext() )
            {
                Object o = it.next();
                if ( type.isInstance( o ) )
                {
                    list.add( o );
                }
            }
        }
        return list;
    }


    /**
     * Gets the Connection beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with Connection beans, may be empty.
     */
    public static Connection[] getConnections( ISelection selection )
    {
        List<Object> list = getTypes( selection, Connection.class );
        return list.toArray( new Connection[list.size()] );
    }


    /**
     * Gets the ConnectionFolder beans contained in the given selection.
     *
     * @param selection the selection
     * @return an array with ConnectionFolder beans, may be empty.
     */
    public static ConnectionFolder[] getConnectionFolders( ISelection selection )
    {
        List<Object> list = getTypes( selection, ConnectionFolder.class );
        return list.toArray( new ConnectionFolder[list.size()] );
    }
    
    
    /**
     * Gets the objects contained in the given selection.
     *
     * @param selection the selection
     * @return an array with object, may be empty.
     */
    public static Object[] getObjects( ISelection selection )
    {
        List<Object> list = getTypes( selection, Object.class );
        return list.toArray( new Object[list.size()] );
    }
}
