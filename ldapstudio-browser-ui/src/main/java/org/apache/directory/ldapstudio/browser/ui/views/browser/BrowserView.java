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

package org.apache.directory.ldapstudio.browser.ui.views.browser;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserConfiguration;
import org.apache.directory.ldapstudio.browser.ui.widgets.browser.BrowserWidget;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;


public class BrowserView extends ViewPart implements ModelModifier
{

    private BrowserConfiguration configuration;

    private BrowserViewUniversalListener universalListener;

    private BrowserViewActionGroup actionGroup;

    private BrowserWidget mainWidget;


    // private DragAction dragAction;
    // private DropAction dropAction;

    public static String getId()
    {
        return BrowserView.class.getName();
    }


    public BrowserView()
    {
        super();
    }


    public void setFocus()
    {
        mainWidget.getViewer().getControl().setFocus();
    }


    public void dispose()
    {
        if ( this.configuration != null )
        {
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.universalListener.dispose();
            this.universalListener = null;
            this.configuration.dispose();
            this.configuration = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
            getSite().setSelectionProvider( null );
        }

        super.dispose();
    }


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
        this.configuration = new BrowserConfiguration();

        // create main widget
        this.mainWidget = new BrowserWidget( this.configuration, getViewSite().getActionBars() );
        this.mainWidget.createWidget( composite );
        this.mainWidget.setInput( getSite() );

        // create actions and context menu (and register global actions)
        this.actionGroup = new BrowserViewActionGroup( this );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.enableGlobalActionHandlers( getViewSite().getActionBars() );
        this.actionGroup.fillContextMenu( this.mainWidget.getContextMenuManager() );

        // create the listener
        getSite().setSelectionProvider( this.mainWidget.getViewer() );
        this.universalListener = new BrowserViewUniversalListener( this );

        // DND support
        // int ops = DND.DROP_COPY | DND.DROP_MOVE;
        // viewer.addDragSupport(ops, new Transfer[]{TextTransfer.getInstance(),
        // BrowserTransfer.getInstance()}, this.dragAction);
        // viewer.addDropSupport(ops, new
        // Transfer[]{BrowserTransfer.getInstance()}, this.dropAction);
    }


    public void select( Object obj )
    {
        if ( obj instanceof ISearchResult )
        {
            ISearchResult searchResult = ( ISearchResult ) obj;
            ISearch search = searchResult.getSearch();

            this.mainWidget.getViewer().expandToLevel( search, 1 );

            this.mainWidget.getViewer().reveal( searchResult );
            this.mainWidget.getViewer().refresh( searchResult, true );
            this.mainWidget.getViewer().setSelection( new StructuredSelection( searchResult ), true );
            this.mainWidget.getViewer().setSelection( new StructuredSelection( searchResult ), true );
        }
        if ( obj instanceof IEntry )
        {
            IEntry entry = ( IEntry ) obj;

            List entryList = new ArrayList();
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
                    parentEntries[i].setChildrenInitialized( true, this );
                    parentEntries[i].setHasMoreChildren( true, this );
                }
            }

            this.mainWidget.getViewer().reveal( entry );
            this.mainWidget.getViewer().refresh( entry, true );
            this.mainWidget.getViewer().setSelection( new StructuredSelection( entry ), true );
            this.mainWidget.getViewer().setSelection( new StructuredSelection( entry ), true );
        }
    }


    public static void setInput( IConnection connection )
    {
        try
        {
            String targetId = BrowserView.getId();
            IViewPart targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
                targetId );

            if ( targetView == null && connection != null )
            {
                try
                {
                    targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                        targetId, null, IWorkbenchPage.VIEW_VISIBLE );
                }
                catch ( PartInitException e )
                {
                }
            }

            try
            {
                targetView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( targetId,
                    null, IWorkbenchPage.VIEW_VISIBLE );
            }
            catch ( PartInitException e )
            {
            }

            // set input
            if ( targetView != null && targetView instanceof BrowserView )
            {
                ( ( BrowserView ) targetView ).universalListener.setInput( connection );
            }
        }
        catch ( NullPointerException npe )
        {
        }
    }


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
                    System.out.println( "BrowserView: " + context.getInput() + "," + context.getSelection() );
                    return true;
                }
            };
        }

        return null;
    }


    public BrowserViewActionGroup getActionGroup()
    {
        return actionGroup;
    }


    public BrowserConfiguration getConfiguration()
    {
        return configuration;
    }


    public BrowserWidget getMainWidget()
    {
        return mainWidget;
    }


    public BrowserViewUniversalListener getUniversalListener()
    {
        return universalListener;
    }

}
