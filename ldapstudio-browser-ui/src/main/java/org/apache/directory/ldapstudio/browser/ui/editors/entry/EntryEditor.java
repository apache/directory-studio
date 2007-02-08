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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifOutlinePage;
import org.apache.directory.ldapstudio.browser.ui.views.browser.BrowserView;
import org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor.EntryEditorWidget;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


public class EntryEditor extends EditorPart implements INavigationLocationProvider, IReusableEditor
{

    private EntryEditorConfiguration configuration;

    private EntryEditorActionGroup actionGroup;

    private EntryEditorWidget mainWidget;

    private EntryEditorUniversalListener universalListener;

    private LdifOutlinePage outlinePage;


    public static String getId()
    {
        return EntryEditor.class.getName();
    }


    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        if ( input instanceof EntryEditorInput && this.universalListener != null )
        {
            EntryEditorInput eei = ( EntryEditorInput ) input;
            IEntry entry = eei.getResolvedEntry();
            this.universalListener.setInput( entry );

            if ( entry != null )
            {
                // enable one instance hack before fireing the input change event 
                // otherwise the navigation history is cleared.
                EntryEditorInput.enableOneInstanceHack( true );
                firePropertyChange( IEditorPart.PROP_INPUT );

                // disable one instance hack for marking the location
                EntryEditorInput.enableOneInstanceHack( false );
                getSite().getPage().getNavigationHistory().markLocation( this );
            }

            if ( this.outlinePage != null )
            {
                this.outlinePage.refresh();
            }

            // finally enable the one instance hack 
            EntryEditorInput.enableOneInstanceHack( true );
        }
    }


    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.setSite( site );

        // mark dummy location, necessary because the first marked
        // location doesn't appear in history
        this.setInput( new EntryEditorInput( ( IEntry ) null ) );
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
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_entry_editor" );

        // create configuration
        this.configuration = new EntryEditorConfiguration();

        // create main widget
        this.mainWidget = new EntryEditorWidget( this.configuration );
        this.mainWidget.createWidget( composite );

        // create actions and context menu and register global actions
        this.actionGroup = new EntryEditorActionGroup( this );
        this.actionGroup.fillToolBar( this.mainWidget.getToolBarManager() );
        this.actionGroup.fillMenu( this.mainWidget.getMenuManager() );
        this.actionGroup.enableGlobalActionHandlers( getEditorSite().getActionBars() );
        this.actionGroup.fillContextMenu( this.mainWidget.getContextMenuManager() );

        // create the listener
        getSite().setSelectionProvider( this.mainWidget.getViewer() );
        this.universalListener = new EntryEditorUniversalListener( this );
        this.setInput( getEditorInput() );
    }


    public void setFocus()
    {
        this.mainWidget.setFocus();
    }


    public Object getAdapter( Class required )
    {
        if ( IContentOutlinePage.class.equals( required ) )
        {
            if ( outlinePage == null || outlinePage.getControl() == null || outlinePage.getControl().isDisposed() )
            {
                outlinePage = new LdifOutlinePage( this );
            }
            return outlinePage;
        }

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
                    return new ShowInContext( getMainWidget().getViewer().getInput(), getMainWidget().getViewer()
                        .getSelection() );
                }
            };
        }

        return super.getAdapter( required );
    }


    public void dispose()
    {
        if ( this.configuration != null )
        {
            this.universalListener.dispose();
            this.universalListener = null;
            this.mainWidget.dispose();
            this.mainWidget = null;
            this.actionGroup.dispose();
            this.actionGroup = null;
            this.configuration.dispose();
            this.configuration = null;
            getSite().setSelectionProvider( null );
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


    public EntryEditorActionGroup getActionGroup()
    {
        return actionGroup;
    }


    public EntryEditorConfiguration getConfiguration()
    {
        return configuration;
    }


    public EntryEditorWidget getMainWidget()
    {
        return mainWidget;
    }


    public LdifOutlinePage getOutlinePage()
    {
        return outlinePage;
    }


    public EntryEditorUniversalListener getUniversalListener()
    {
        return universalListener;
    }


    public INavigationLocation createEmptyNavigationLocation()
    {
        return null;
    }


    public INavigationLocation createNavigationLocation()
    {
        return new EntryEditorNavigationLocation( this );
    }

}
