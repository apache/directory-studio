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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueMultiModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.jobs.UpdateEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifFile;
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
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;


/**
 * The SearchResultEditor is an {@link IEditorPart} is used to display and edit 
 * the attributes of the results of a search.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditor extends EditorPart implements INavigationLocationProvider, IReusableEditor,
    IShowEditorInput, IPropertyChangeListener
{

    /** The configuration. */
    private SearchResultEditorConfiguration configuration;

    /** The action group. */
    private SearchResultEditorActionGroup actionGroup;

    /** The main widget. */
    private SearchResultEditorWidget mainWidget;

    /** The universal listener. */
    private SearchResultEditorUniversalListener universalListener;

    protected EntryUpdateListener entryUpdateListener = new EntryUpdateListener()
    {
        public void entryUpdated( EntryModificationEvent event )
        {
            if ( mainWidget.getViewer() == null || mainWidget.getViewer().getInput() == null )
            {
                return;
            }

            IEntry modifiedEntry = event.getModifiedEntry();
            IEntry originalEntry = modifiedEntry.getBrowserConnection().getEntryFromCache( modifiedEntry.getDn() );
            ISearchResult referenceCopy = configuration.getCursor( mainWidget.getViewer() ).getSelectedReferenceCopy();
            ISearchResult workingCopy = configuration.getCursor( mainWidget.getViewer() ).getSelectedSearchResult();

            // check on object identity, nothing should be done for equal objects from other editors
            if ( workingCopy != null && workingCopy.getEntry() == modifiedEntry )
            {
                // only save if we receive a real value modification event
                if ( !( event instanceof ValueAddedEvent || event instanceof ValueDeletedEvent
                    || event instanceof ValueModifiedEvent || event instanceof ValueRenamedEvent || event instanceof ValueMultiModificationEvent ) )
                {
                    return;
                }
                // consistency check: don't save if there is an empty value, silently return in that case
                for ( IAttribute attribute : modifiedEntry.getAttributes() )
                {
                    for ( IValue value : attribute.getValues() )
                    {
                        if ( value.isEmpty() )
                        {
                            return;
                        }
                    }
                }

                LdifFile diff = Utils.computeDiff( referenceCopy.getEntry(), modifiedEntry );
                if ( diff != null )
                {
                    // save
                    UpdateEntryRunnable runnable = new UpdateEntryRunnable( originalEntry, diff
                        .toFormattedString( LdifFormatParameters.DEFAULT ) );
                    RunnableContextRunner.execute( runnable, null, true );
                }
                configuration.getCursor( mainWidget.getViewer() ).resetCopies();
            }
        }
    };


    /**
     * Gets the ID of the SearchResultEditor.
     * 
     * @return the id of the SearchResultEditor
     */
    public static String getId()
    {
        return BrowserUIConstants.EDITOR_SEARCH_RESULT;
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        if ( input instanceof SearchResultEditorInput && universalListener != null )
        {
            setPartName( input.getName() );

            SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
            setSearchResultEditorWidgetInput( srei );
        }
    }


    public void showEditorInput( IEditorInput input )
    {
        if ( input instanceof SearchResultEditorInput )
        {
            /*
             * Optimization: no need to set the input again if the same input is already set
             */
            if ( getEditorInput() != null && getEditorInput().equals( input ) )
            {
                return;
            }

            // now set the real input and mark history location
            setInput( input );
            getSite().getPage().getNavigationHistory().markLocation( this );
            firePropertyChange( BrowserUIConstants.INPUT_CHANGED );
        }
    }


    private void setSearchResultEditorWidgetInput( SearchResultEditorInput srei )
    {
        ISearch search = srei.getSearch();
        universalListener.setInput( search );
    }


    /**
     * Refreshes this search result editor.
     */
    public void refresh()
    {
        if ( universalListener != null )
        {
            universalListener.refreshInput();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );
        setInput( input );

        EventRegistry
            .addEntryUpdateListener( entryUpdateListener, BrowserCommonActivator.getDefault().getEventRunner() );
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
        // layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        composite.setLayout( layout );

        PlatformUI.getWorkbench().getHelpSystem().setHelp( composite,
            BrowserUIConstants.PLUGIN_ID + "." + "tools_search_result_editor" ); //$NON-NLS-1$ //$NON-NLS-2$

        // create configuration
        configuration = new SearchResultEditorConfiguration( this );

        // create main widget
        mainWidget = new SearchResultEditorWidget( configuration );
        mainWidget.createWidget( composite );

        // create actions and context menu (and register global actions)
        actionGroup = new SearchResultEditorActionGroup( this );
        actionGroup.fillToolBar( mainWidget.getToolBarManager() );
        actionGroup.fillMenu( mainWidget.getMenuManager() );
        actionGroup.enableGlobalActionHandlers( getEditorSite().getActionBars() );
        actionGroup.fillContextMenu( configuration.getContextMenuManager( mainWidget.getViewer() ) );

        // create the listener
        universalListener = new SearchResultEditorUniversalListener( this );
        getSite().setSelectionProvider( configuration.getCursor( mainWidget.getViewer() ) );
        this.setInput( getEditorInput() );

        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
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
    public void dispose()
    {
        if ( configuration != null )
        {
            EventRegistry.removeEntryUpdateListener( entryUpdateListener );
            actionGroup.dispose();
            actionGroup = null;
            universalListener.dispose();
            universalListener = null;
            mainWidget.dispose();
            mainWidget = null;
            configuration.dispose();
            configuration = null;
            getSite().setSelectionProvider( null );
            BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
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
        return new SearchResultEditorNavigationLocation( this );
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * Gets the action group.
     * 
     * @return the action group
     */
    public SearchResultEditorActionGroup getActionGroup()
    {
        return actionGroup;
    }


    /**
     * Gets the configuration.
     * 
     * @return the configuration
     */
    public SearchResultEditorConfiguration getConfiguration()
    {
        return configuration;
    }


    /**
     * Gets the main widget.
     * 
     * @return the main widget
     */
    public SearchResultEditorWidget getMainWidget()
    {
        return mainWidget;
    }


    /**
     * Gets the universal listener.
     * 
     * @return the universal listener
     */
    public SearchResultEditorUniversalListener getUniversalListener()
    {
        return universalListener;
    }


    /**
     * {@inheritDoc}
     */
    public void propertyChange( PropertyChangeEvent event )
    {
        refresh();
    }

}
