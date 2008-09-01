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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
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


/**
 * The EntryEditor is an {@link IEditorPart} is used to display and edit the attributes of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditor extends EditorPart implements INavigationLocationProvider, IReusableEditor
{

    /** The editor configuration. */
    private EntryEditorConfiguration configuration;

    /** The action group. */
    private EntryEditorActionGroup actionGroup;

    /** The main widget. */
    private EntryEditorWidget mainWidget;

    /** The universal listener. */
    private EntryEditorUniversalListener universalListener;

    /** The outline page. */
    private EntryEditorOutlinePage outlinePage;


    /**
     * Gets the ID of the EntryEditor.
     * 
     * @return the id of the EntryEditor
     */
    public static String getId()
    {
        return EntryEditor.class.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        if ( input instanceof EntryEditorInput && universalListener != null )
        {
            EntryEditorInput eei = ( EntryEditorInput ) input;
            IEntry entry = eei.getResolvedEntry();

            // inform listener
            universalListener.setInput( entry );

            // mark location for back/forward history navigation
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

            // refresh outline
            if ( outlinePage != null )
            {
                outlinePage.refresh();
            }

            // finally enable the one instance hack 
            EntryEditorInput.enableOneInstanceHack( true );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        super.setSite( site );

        // mark dummy location, necessary because the first marked
        // location doesn't appear in history
        setInput( new EntryEditorInput( ( IEntry ) null ) );
        getSite().getPage().getNavigationHistory().markLocation( this );

        setInput( input );
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
        layout.verticalSpacing = 0;
        composite.setLayout( layout );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( composite,
            BrowserUIConstants.PLUGIN_ID + "." + "tools_entry_editor" );

        // create configuration
        configuration = new EntryEditorConfiguration();

        // create main widget
        mainWidget = new EntryEditorWidget( this.configuration );
        mainWidget.createWidget( composite );

        // create actions and context menu and register global actions
        actionGroup = new EntryEditorActionGroup( this );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.enableGlobalActionHandlers( getEditorSite().getActionBars() );
        actionGroup.fillContextMenu( mainWidget.getContextMenuManager() );

        // create the listener
        getSite().setSelectionProvider( mainWidget.getViewer() );
        universalListener = new EntryEditorUniversalListener( this );
        setInput( getEditorInput() );
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        mainWidget.setFocus();
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class required )
    {
        if ( IContentOutlinePage.class.equals( required ) )
        {
            if ( outlinePage == null || outlinePage.getControl() == null || outlinePage.getControl().isDisposed() )
            {
                outlinePage = new EntryEditorOutlinePage( this );
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


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        if ( configuration != null )
        {
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
            actionGroup.dispose();
            actionGroup = null;
            configuration.dispose();
            configuration = null;
            getSite().setSelectionProvider( null );
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( IProgressMonitor monitor )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /**
     * Gets the action group.
     * 
     * @return the action group
     */
    public EntryEditorActionGroup getActionGroup()
    {
        return actionGroup;
    }


    /**
     * Gets the configuration.
     * 
     * @return the configuration
     */
    public EntryEditorConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * Gets the main widget.
     * 
     * @return the main widget
     */
    public EntryEditorWidget getMainWidget()
    {
        return mainWidget;
    }


    /**
     * Gets the outline page.
     * 
     * @return the outline page
     */
    public EntryEditorOutlinePage getOutlinePage()
    {
        return outlinePage;
    }


    /**
     * Gets the universal listener.
     * 
     * @return the universal listener
     */
    public EntryEditorUniversalListener getUniversalListener()
    {
        return universalListener;
    }


    /**
     * {@inheritDoc}
     */
    public INavigationLocation createEmptyNavigationLocation()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public INavigationLocation createNavigationLocation()
    {
        return new EntryEditorNavigationLocation( this );
    }

}
