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


public class ConnectionConfiguration
{

    private boolean disposed = false;

    private ConnectionContentProvider contentProvider;

    private ConnectionLabelProvider labelProvider;

    private MenuManager contextMenuManager;


    public ConnectionConfiguration()
    {
    }


    public void dispose()
    {
        if ( !this.disposed )
        {

            if ( this.contentProvider != null )
                this.contentProvider.dispose();
            this.contentProvider = null;

            if ( this.labelProvider != null )
                this.labelProvider.dispose();
            this.labelProvider = null;

            if ( this.contextMenuManager != null )
                this.contextMenuManager.dispose();
            this.contextMenuManager = null;

            this.disposed = true;
        }
    }


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


    public ConnectionContentProvider getContentProvider( TableViewer viewer )
    {
        if ( this.contentProvider == null )
            this.contentProvider = new ConnectionContentProvider();

        return contentProvider;
    }


    public ConnectionLabelProvider getLabelProvider( TableViewer viewer )
    {
        if ( this.labelProvider == null )
        {
            this.labelProvider = new ConnectionLabelProvider();
        }

        return labelProvider;
    }

}
