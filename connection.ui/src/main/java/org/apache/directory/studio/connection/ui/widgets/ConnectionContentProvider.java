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

package org.apache.directory.studio.connection.ui.widgets;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionFolderManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * The ConnectionContentProvider represents the content provider for
 * the connection widget. It accepts the ConnectionManager as input
 * and returns its connections as elements.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionContentProvider implements ITreeContentProvider
{

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }


    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }


    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement != null && inputElement instanceof ConnectionFolderManager )
        {
            ConnectionFolderManager cfm = ( ConnectionFolderManager ) inputElement;
            ConnectionFolder rootConnectionFolder = cfm.getRootConnectionFolder();
            Object[] elements = getChildren( rootConnectionFolder );
            return elements;
        }
        else
        {
            return getChildren( inputElement );
        }
    }


    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        if ( parentElement != null && parentElement instanceof ConnectionFolder )
        {
            List<Object> children = new ArrayList<Object>();

            ConnectionFolder folder = ( ConnectionFolder ) parentElement;
            List<String> subFolderIds = folder.getSubFolderIds();
            List<String> connectionIds = folder.getConnectionIds();

            for ( String subFolderId : subFolderIds )
            {
                ConnectionFolder subFolder = ConnectionCorePlugin.getDefault().getConnectionFolderManager()
                    .getConnectionFolderById( subFolderId );
                if ( subFolder != null )
                {
                    children.add( subFolder );
                }
            }
            for ( String connectionId : connectionIds )
            {
                Connection conn = ConnectionCorePlugin.getDefault().getConnectionManager().getConnectionById(
                    connectionId );
                if ( conn != null )
                {
                    children.add( conn );
                }
            }

            return children.toArray();
        }
        return null;
    }


    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof ConnectionFolder )
        {
            return ConnectionCorePlugin.getDefault().getConnectionFolderManager().getParentConnectionFolder(
                ( ConnectionFolder ) element );
        }
        else if ( element instanceof Connection )
        {
            return ConnectionCorePlugin.getDefault().getConnectionFolderManager().getParentConnectionFolder(
                ( Connection ) element );
        }
        else
        {
            return null;
        }
    }


    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        Object[] children = getChildren( element );
        return children != null && children.length > 0;
    }

}