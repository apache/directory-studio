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

package org.apache.directory.ldapstudio.browser.ui.widgets.connection;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Menu;


/**
 * The ConnectionConfiguration contains the content provider, the
 * label provider and the context menu manager for the
 * connection widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionConfiguration
{

    /** The disposed flag */
    private boolean disposed = false;

    /** The content provider. */
    private ConnectionContentProvider contentProvider;

    /** The label provider. */
    private ConnectionLabelProvider labelProvider;

    /** The context menu manager. */
    private MenuManager contextMenuManager;


    /**
     * Creates a new instance of ConnectionConfiguration.
     */
    public ConnectionConfiguration()
    {
    }


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
                contextMenuManager = null;
                contextMenuManager.dispose();
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
    public IMenuManager getContextMenuManager( TableViewer viewer )
    {
        if ( this.contextMenuManager == null )
        {
            this.contextMenuManager = new MenuManager();
            Menu menu = this.contextMenuManager.createContextMenu( viewer.getControl() );
            viewer.getControl().setMenu( menu );
        }
        return this.contextMenuManager;
    }


    /**
     * Gets the content provider.
     * 
     * @param viewer the connection widget's table viewer
     * 
     * @return the content provider
     */
    public ConnectionContentProvider getContentProvider( TableViewer viewer )
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
    public ConnectionLabelProvider getLabelProvider( TableViewer viewer )
    {
        if ( labelProvider == null )
        {
            labelProvider = new ConnectionLabelProvider();
        }

        return labelProvider;
    }

}
