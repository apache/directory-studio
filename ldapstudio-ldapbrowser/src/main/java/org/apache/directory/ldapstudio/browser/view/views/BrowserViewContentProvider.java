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

package org.apache.directory.ldapstudio.browser.view.views;


import org.apache.directory.ldapstudio.browser.view.views.wrappers.ConnectionWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.EntryWrapper;
import org.apache.directory.ldapstudio.browser.view.views.wrappers.TreeViewerRootNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Browser View
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        if ( parentElement instanceof TreeViewerRootNode ) // => launched by the Connection
        {
            // Getting the ConnectionWrapper and the Connection
            TreeViewerRootNode treeViewerRootNode = ( TreeViewerRootNode ) parentElement;

            return treeViewerRootNode.getChildren();
        }
        else if ( parentElement instanceof ConnectionWrapper ) // => launched by the Connection
        {
            // Getting the ConnectionWrapper and the Connection
            ConnectionWrapper connectionWrapper = ( ConnectionWrapper ) parentElement;

            return connectionWrapper.getChildren();
        }
        else if ( parentElement instanceof EntryWrapper ) // Any Other Node
        {
            // Getting the EntryWrapper and the connection
            EntryWrapper wrapper = ( EntryWrapper ) parentElement;

            return wrapper.getChildren();
        }

        // Default return (Should never be used)
        return new Object[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof EntryWrapper )
        {
            EntryWrapper wrapper = ( EntryWrapper ) element;
            return wrapper.hasChildren();
        }

        // Default return (Should never be used)
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do here but the method is needed 
        // by IContentProvider Interface
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do here but the method is needed 
        // by IContentProvider Interface
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        return null;
    }
}
