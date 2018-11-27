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


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;


/**
 * The ConnectionConfiguration contains the content provider, the
 * label provider and the context menu manager for the
 * connection widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConnectionConfiguration
{
    /** The disposed flag */
    private boolean disposed = false;

    /** The content provider. */
    private ConnectionContentProvider contentProvider;

    /** The label provider. */
    private ConnectionLabelProvider labelProvider;

    /** The sorter. */
    private ConnectionSorter sorter;

    /** The context menu manager. */
    private MenuManager contextMenuManager;


    /**
     * Disposes this configuration.
     */
    public void dispose()
    {
        if ( !disposed )
        {
            if ( contentProvider != null )
            {
                contentProvider.dispose();
                contentProvider = null;
            }

            if ( labelProvider != null )
            {
                labelProvider.dispose();
                labelProvider = null;
            }

            if ( contextMenuManager != null )
            {
                contextMenuManager.dispose();
                contextMenuManager = null;
            }

            disposed = true;
        }
    }


    /**
     * Gets the context menu manager.
     * 
     * @param viewer the connection widget's table viewer 
     * 
     * @return the context menu manager
     */
    public IMenuManager getContextMenuManager( TreeViewer viewer )
    {
        if ( contextMenuManager == null )
        {
            contextMenuManager = new MenuManager();
            Menu menu = contextMenuManager.createContextMenu( viewer.getControl() );
            viewer.getControl().setMenu( menu );
        }

        return contextMenuManager;
    }


    /**
     * Gets the content provider.
     * 
     * @param viewer the connection widget's table viewer
     * 
     * @return the content provider
     */
    public ConnectionContentProvider getContentProvider( TreeViewer viewer )
    {
        if ( contentProvider == null )
        {
            contentProvider = new ConnectionContentProvider();
        }

        return contentProvider;
    }


    /**
     * Gets the label provider.
     * 
     * @param viewer the connection widget's table viewer
     * 
     * @return the label provider
     */
    public ConnectionLabelProvider getLabelProvider( TreeViewer viewer )
    {
        if ( labelProvider == null )
        {
            labelProvider = new ConnectionLabelProvider();
        }

        return labelProvider;
    }


    /**
     * Gets the sorter.
     * 
     * @return the sorter
     */
    public ConnectionSorter getSorter()
    {
        if ( sorter == null )
        {
            sorter = new ConnectionSorter();
        }

        return sorter;
    }
}
