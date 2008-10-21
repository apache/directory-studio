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

package org.apache.directory.studio.ldapbrowser.ui.views.browser;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserConfiguration;
import org.apache.directory.studio.ldapbrowser.common.widgets.browser.BrowserWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the browser view. It displays the DIT, the searches and the bookmarks.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserView extends ViewPart
{

    /** The configuration */
    private BrowserConfiguration configuration;

    /** The listeners */
    private BrowserViewUniversalListener universalListener;

    /** The actions */
    private BrowserViewActionGroup actionGroup;

    /** The browser's main widget */
    private BrowserWidget mainWidget;


    // private DragAction dragAction;
    // private DropAction dropAction;

    /**
     * Returns the browser view ID.
     * 
     * @return the browser view ID.
     */
    public static String getId()
    {
        return BrowserView.class.getName();
    }


    /**
     * Creates a new instance of BrowserView.
     */
    public BrowserView()
    {
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation sets focus to the viewer's control.
     */
    public void setFocus()
    {
        mainWidget.getViewer().getControl().setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( configuration != null )
        {
            actionGroup.dispose();
            actionGroup = null;
            universalListener.dispose();
            universalListener = null;
            configuration.dispose();
            configuration = null;
            mainWidget.dispose();
            mainWidget = null;
            getSite().setSelectionProvider( null );
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout( layout );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( composite,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_browser_view" );

        // create configuration
        configuration = new BrowserConfiguration();

        // create main widget
        mainWidget = new BrowserWidget( configuration, getViewSite().getActionBars() );
        mainWidget.createWidget( composite );
        mainWidget.setInput( getSite() );

        // create actions and context menu (and register global actions)
        actionGroup = new BrowserViewActionGroup( this );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.enableGlobalActionHandlers( getViewSite().getActionBars() );
        actionGroup.fillContextMenu( mainWidget.getContextMenuManager() );

        // create the listener
        getSite().setSelectionProvider( mainWidget.getViewer() );
        universalListener = new BrowserViewUniversalListener( this );

        // DND support
        // int ops = DND.DROP_COPY | DND.DROP_MOVE;
        // viewer.addDragSupport(ops, new Transfer[]{TextTransfer.getInstance(),
        // BrowserTransfer.getInstance()}, this.dragAction);
        // viewer.addDropSupport(ops, new
        // Transfer[]{BrowserTransfer.getInstance()}, this.dropAction);
    }


    /**
     * Selects the given object in the tree. The object
     * must be an IEntry, ISearch, ISearchResult or IBookmark.
     * 
     * @param obj the object to select
     */
    public void select( Object obj )
    {
        Object objectToSelect = null;

        if ( obj instanceof ISearch )
        {
            ISearch search = ( ISearch ) obj;

            universalListener.setInput( search.getBrowserConnection() );

            mainWidget.getViewer().expandToLevel( search, 0 );

            objectToSelect = search;
        }
        if ( obj instanceof ISearchResult )
        {
            ISearchResult searchResult = ( ISearchResult ) obj;
            ISearch search = searchResult.getSearch();

            universalListener.setInput( search.getBrowserConnection() );

            mainWidget.getViewer().expandToLevel( search, 1 );

            objectToSelect = searchResult;
        }
        if ( obj instanceof IBookmark )
        {
            IBookmark bookmark = ( IBookmark ) obj;

            universalListener.setInput( bookmark.getBrowserConnection() );

            mainWidget.getViewer().expandToLevel( bookmark, 0 );

            objectToSelect = bookmark;
        }
        if ( obj instanceof IEntry )
        {
            IEntry entry = ( IEntry ) obj;

            universalListener.setInput( entry.getBrowserConnection() );

            List<IEntry> entryList = new ArrayList<IEntry>();
            IEntry tempEntry = entry;
            while ( tempEntry.getParententry() != null )
            {
                IEntry parentEntry = tempEntry.getParententry();
                entryList.add( parentEntry );
                tempEntry = parentEntry;
            }

            IEntry[] parentEntries = ( IEntry[] ) entryList.toArray( new IEntry[0] );
            for ( int i = parentEntries.length - 1; i >= 0; i-- )
            {

                if ( !parentEntries[i].isChildrenInitialized() )
                {
                    parentEntries[i].setChildrenInitialized( true );
                    parentEntries[i].setHasMoreChildren( true );
                }
            }

            objectToSelect = entry;
        }

        if ( objectToSelect != null )
        {
            mainWidget.getViewer().reveal( objectToSelect );
            mainWidget.getViewer().refresh( objectToSelect, true );
            mainWidget.getViewer().setSelection( new StructuredSelection( objectToSelect ), true );

        }
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class required )
    {
        if ( IShowInTarget.class.equals( required ) )
        {
            return new IShowInTarget()
            {
                public boolean show( ShowInContext context )
                {
                    StructuredSelection selection = ( StructuredSelection ) context.getSelection();
                    Object obj = selection.getFirstElement();
                    if ( obj instanceof IValue )
                    {
                        IValue value = ( IValue ) obj;
                        IEntry entry = value.getAttribute().getEntry();
                        select( entry );
                    }
                    else if ( obj instanceof IAttribute )
                    {
                        IAttribute attribute = ( IAttribute ) obj;
                        IEntry entry = attribute.getEntry();
                        select( entry );

                    }
                    else if ( obj instanceof ISearchResult )
                    {
                        ISearchResult sr = ( ISearchResult ) obj;
                        ISearch search = sr.getSearch();
                        select( search );
                    }
                    return true;
                }
            };
        }

        return null;
    }


    /**
     * Gets the action group.
     *
     * @return the action group
     */
    public BrowserViewActionGroup getActionGroup()
    {
        return actionGroup;
    }


    /**
     * Gets the configuration.
     *
     * @return the configuration
     */
    public BrowserConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * Gets the main widget.
     * 
     * @return the main widget
     */
    public BrowserWidget getMainWidget()
    {
        return mainWidget;
    }


    /**
     * Gets the universal listener.
     * 
     * @return the universal listener
     */
    public BrowserViewUniversalListener getUniversalListener()
    {
        return universalListener;
    }

}
