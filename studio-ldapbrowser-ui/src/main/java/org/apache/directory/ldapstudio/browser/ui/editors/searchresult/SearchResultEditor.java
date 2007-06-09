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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.views.browser.BrowserView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.INavigationLocationProvider;
import org.eclipse.ui.IReusableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;


public class SearchResultEditor extends EditorPart implements INavigationLocationProvider, IReusableEditor,
    IPropertyChangeListener
{

    private SearchResultEditorConfiguration configuration;

    private SearchResultEditorActionGroup actionGroup;

    private SearchResultEditorWidget mainWidget;

    private SearchResultEditorUniversalListener universalListener;


    public static String getId()
    {
        return SearchResultEditor.class.getName();
    }

    
    public void setInput( IEditorInput input )
    {
        super.setInput( input );
        
        if ( input instanceof SearchResultEditorInput && this.universalListener != null )
        {
            SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
            ISearch search = srei.getSearch();
            this.universalListener.setInput( search );

            if ( search != null )
            {
                // enable one instance hack before fireing the input change event 
                // otherwise the navigation history is cleared.
                SearchResultEditorInput.enableOneInstanceHack( true );
                firePropertyChange( IEditorPart.PROP_INPUT );

                // disable one instance hack for marking the location
                SearchResultEditorInput.enableOneInstanceHack( false );
                getSite().getPage().getNavigationHistory().markLocation( this );
            }
        }

        // finally enable the one instance hack 
        SearchResultEditorInput.enableOneInstanceHack( true );
    }


    public void refresh()
    {
        if ( this.universalListener != null )
        {
            this.universalListener.refreshInput();
        }
    }


    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.setSite( site );

        // mark dummy location, necessary because the first marked
        // location doesn't appear in history
        this.setInput( new SearchResultEditorInput( null ) );
        getSite().getPage().getNavigationHistory().markLocation( this );

        this.setInput( input );
    }


    public void createPartControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        // layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        composite.setLayout( layout );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( composite,
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_search_result_editor" );

        // create configuration
        this.configuration = new SearchResultEditorConfiguration( this );

        // create main widget
        this.mainWidget = new SearchResultEditorWidget( this.configuration );
        this.mainWidget.createWidget( composite );

        // create actions and context menu (and register global actions)
        this.actionGroup = new SearchResultEditorActionGroup( this );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.enableGlobalActionHandlers( getEditorSite().getActionBars() );
        this.actionGroup.fillContextMenu( this.configuration.getContextMenuManager( this.mainWidget.getViewer() ) );

        // create the listener
        this.universalListener = new SearchResultEditorUniversalListener( this );
        getSite().setSelectionProvider( this.configuration.getCursor( this.mainWidget.getViewer() ) );
        this.setInput( getEditorInput() );

        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
    }


    public void setFocus()
    {
        this.mainWidget.setFocus();
    }


    public void dispose()
    {
        if ( this.configuration != null )
        {
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
            this.configuration.dispose();
            this.configuration = null;
            getSite().setSelectionProvider( null );
            BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
        }

        super.dispose();
    }


    public void doSave( IProgressMonitor monitor )
    {
    }


    public void doSaveAs()
    {
    }


    public boolean isDirty()
    {
        return false;
    }


    public boolean isSaveAsAllowed()
    {
        return false;
    }


    public INavigationLocation createEmptyNavigationLocation()
    {
        return null;
    }


    public INavigationLocation createNavigationLocation()
    {
        return new SearchResultEditorNavigationLocation( this );
    }


    public Object getAdapter( Class required )
    {

        if ( IShowInTargetList.class.equals( required ) )
        {
            return new IShowInTargetList()
            {
                public String[] getShowInTargetIds()
                {
                    return new String[]
                        { BrowserView.getId() };
                }
            };
        }

        if ( IShowInSource.class.equals( required ) )
        {
            return new IShowInSource()
            {
                public ShowInContext getShowInContext()
                {
                    ISelection selection = getConfiguration().getCursor( getMainWidget().getViewer() ).getSelection();
                    return new ShowInContext( getMainWidget().getViewer().getInput(), selection );
                }
            };
        }

        return super.getAdapter( required );
    }


    public SearchResultEditorActionGroup getActionGroup()
    {
        return actionGroup;
    }


    public SearchResultEditorConfiguration getConfiguration()
    {
        return configuration;
    }


    public SearchResultEditorWidget getMainWidget()
    {
        return mainWidget;
    }


    public SearchResultEditorUniversalListener getUniversalListener()
    {
        return universalListener;
    }


    public void propertyChange( PropertyChangeEvent event )
    {
        // if(this.mainWidget.getViewer() != null) {
        // this.mainWidget.getViewer().refresh();
        // }
        this.refresh();
    }

}
