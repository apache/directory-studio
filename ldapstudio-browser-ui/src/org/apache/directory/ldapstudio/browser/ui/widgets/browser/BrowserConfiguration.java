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

package org.apache.directory.ldapstudio.browser.ui.widgets.browser;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;


public class BrowserConfiguration
{

    private boolean disposed = false;

    protected BrowserSorter sorter;

    protected BrowserPreferences preferences;

    protected BrowserContentProvider contentProvider;

    protected BrowserLabelProvider labelProvider;

    protected DecoratingLabelProvider decoratingLabelProvider;

    protected MenuManager contextMenuManager;


    public BrowserConfiguration()
    {
    }


    public void dispose()
    {
        if ( !this.disposed )
        {

            if ( this.sorter != null )
                this.sorter.dispose();
            this.sorter = null;

            if ( this.preferences != null )
                this.preferences.dispose();
            this.preferences = null;

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


    public IMenuManager getContextMenuManager( TreeViewer viewer )
    {
        if ( this.contextMenuManager == null )
        {
            this.contextMenuManager = new MenuManager();
            Menu menu = this.contextMenuManager.createContextMenu( viewer.getControl() );
            viewer.getControl().setMenu( menu );
        }
        return this.contextMenuManager;
    }


    public BrowserContentProvider getContentProvider( TreeViewer viewer )
    {
        if ( this.contentProvider == null )
            this.contentProvider = new BrowserContentProvider( this.getPreferences(), this.getSorter() );

        return contentProvider;
    }


    public DecoratingLabelProvider getLabelProvider( TreeViewer viewer )
    {
        if ( this.labelProvider == null )
        {
            this.labelProvider = new BrowserLabelProvider( this.getPreferences() );
            this.decoratingLabelProvider = new DecoratingLabelProvider( this.labelProvider, BrowserUIPlugin
                .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() );
        }

        return decoratingLabelProvider;
    }


    public BrowserSorter getSorter()
    {
        if ( this.sorter == null )
            this.sorter = new BrowserSorter( getPreferences() );

        return sorter;
    }


    public BrowserPreferences getPreferences()
    {
        if ( this.preferences == null )
            this.preferences = new BrowserPreferences();

        return preferences;
    }

}
