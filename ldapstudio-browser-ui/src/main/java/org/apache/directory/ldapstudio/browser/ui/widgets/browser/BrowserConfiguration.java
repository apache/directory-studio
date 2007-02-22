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


/**
 * The BrowserConfiguration contains the content provider, the
 * label provider, the sorter, the context menu manager and the
 * preferences for the browser widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserConfiguration
{

    /** The disposed flag */
    private boolean disposed = false;

    /** The sorter. */
    protected BrowserSorter sorter;

    /** The preferences. */
    protected BrowserPreferences preferences;

    /** The content provider. */
    protected BrowserContentProvider contentProvider;

    /** The label provider. */
    protected BrowserLabelProvider labelProvider;

    /** The decorating label provider. */
    protected DecoratingLabelProvider decoratingLabelProvider;

    /** The context menu manager. */
    protected MenuManager contextMenuManager;


    /**
     * Creates a new instance of BrowserConfiguration.
     */
    public BrowserConfiguration()
    {
    }


    /**
     * Disposes this configuration.
     */
    public void dispose()
    {
        if ( !disposed )
        {
            if ( sorter != null )
            {
                sorter.dispose();
                sorter = null;
            }

            if ( preferences != null )
            {
                preferences.dispose();
                preferences = null;
            }

            if ( contentProvider != null )
            {
                contentProvider.dispose();
                contentProvider = null;
            }

            if ( labelProvider != null )
            {
                labelProvider.dispose();
                labelProvider = null;
                decoratingLabelProvider.dispose();
                decoratingLabelProvider = null;
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
     * @param viewer the browser widget's tree viewer 
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
     * @param viewer the browser widget's tree viewer 
     * 
     * @return the content provider
     */
    public BrowserContentProvider getContentProvider( TreeViewer viewer )
    {
        if ( contentProvider == null )
        {
            contentProvider = new BrowserContentProvider( getPreferences(), getSorter() );
        }

        return contentProvider;
    }


    /**
     * Gets the label provider.
     * 
     * @param viewer the browser widget's tree viewer 
     * 
     * @return the label provider
     */
    public DecoratingLabelProvider getLabelProvider( TreeViewer viewer )
    {
        if ( labelProvider == null )
        {
            labelProvider = new BrowserLabelProvider( getPreferences() );
            decoratingLabelProvider = new DecoratingLabelProvider( labelProvider, BrowserUIPlugin.getDefault()
                .getWorkbench().getDecoratorManager().getLabelDecorator() );
        }

        return decoratingLabelProvider;
    }


    /**
     * Gets the sorter.
     * 
     * @return the sorter
     */
    public BrowserSorter getSorter()
    {
        if ( sorter == null )
        {
            sorter = new BrowserSorter( getPreferences() );
        }

        return sorter;
    }


    /**
     * Gets the preferences.
     * 
     * @return the preferences
     */
    public BrowserPreferences getPreferences()
    {
        if ( preferences == null )
        {
            preferences = new BrowserPreferences();
        }

        return preferences;
    }

}
