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
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.impl.SearchResult;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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


/**
 * The SearchResultEditor is an {@link IEditorPart} is used to display and edit 
 * the attributes of the results of a search.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEditor extends EditorPart implements INavigationLocationProvider, IReusableEditor,
    IPropertyChangeListener
{

    /** The configuration. */
    private SearchResultEditorConfiguration configuration;

    /** The action group. */
    private SearchResultEditorActionGroup actionGroup;

    /** The main widget. */
    private SearchResultEditorWidget mainWidget;

    /** The universal listener. */
    private SearchResultEditorUniversalListener universalListener;

    private ISearch workingCopy;

    protected EntryUpdateListener entryUpdateListener = new EntryUpdateListener()
    {
        public void entryUpdated( EntryModificationEvent event )
        {
            if ( workingCopy == null || mainWidget.getViewer() == null || mainWidget.getViewer().getInput() == null )
            {
                return;
            }

            IEntry modifiedEntry = event.getModifiedEntry();

            if ( workingCopy != null )
            {
                for ( ISearchResult sr : workingCopy.getSearchResults() )
                {
                    // check on object identity, nothing should be done for equal objects from other editors
                    if ( modifiedEntry == sr.getEntry() )
                    {
                        IEntry originalEntry = modifiedEntry.getBrowserConnection().getEntryFromCache(
                            modifiedEntry.getDn() );
                        LdifFile diff = Utils.computeDiff( originalEntry, modifiedEntry );
                        if ( diff != null )
                        {
                            // save
                            ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( originalEntry
                                .getBrowserConnection(), diff.toFormattedString( LdifFormatParameters.DEFAULT ), false,
                                false );
                            IStatus status = RunnableContextRunner.execute( runnable, null, true );
                            if ( status.isOK() )
                            {
                                EntryEditorUtils.ensureAttributesInitialized( originalEntry );
                                setSearchResultEditorWidgetInput( ( SearchResultEditorInput ) getEditorInput() );
                            }
                        }

                        return;
                    }
                }

                IEditorInput input = getEditorInput();
                if ( input instanceof SearchResultEditorInput )
                {
                    SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
                    for ( ISearchResult sr : srei.getSearch().getSearchResults() )
                    {
                        if ( modifiedEntry == sr.getEntry() )
                        {
                            // original entry has been updated, update widget input
                            setSearchResultEditorWidgetInput( srei );
                        }
                    }
                }
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
            SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
            ISearch search = srei.getSearch();

            setSearchResultEditorWidgetInput( srei );

            if ( search != null )
            {
                // disable one instance hack before firing the input change event 
                // otherwise the navigation history is cleared.
                // Note: seems this behavior has been changed with Eclipse 3.3
                SearchResultEditorInput.enableOneInstanceHack( false );
                firePropertyChange( IEditorPart.PROP_INPUT );

                // enable one instance hack for marking the location
                // Note: seems this behavior has been changed with Eclipse 3.3
                SearchResultEditorInput.enableOneInstanceHack( true );
                getSite().getPage().getNavigationHistory().markLocation( this );
            }
        }

        // finally enable the one instance hack 
        SearchResultEditorInput.enableOneInstanceHack( true );
    }


    private void setSearchResultEditorWidgetInput( SearchResultEditorInput srei )
    {
        // clone search, search results, entries
        ISearch search = srei.getSearch();
        workingCopy = search != null ? ( ISearch ) search.clone() : search;
        if ( search != null && search.getSearchResults() != null )
        {
            ISearchResult[] searchResults = search.getSearchResults();
            ISearchResult[] clonedSearchResults = new ISearchResult[searchResults.length];
            for ( int i = 0; i < searchResults.length; i++ )
            {
                IEntry entry = searchResults[i].getEntry();
                IEntry clonedEntry = new CompoundModification().cloneEntry( entry );
                clonedSearchResults[i] = new SearchResult( clonedEntry, workingCopy );
            }

            EventRegistry.suspendEventFiringInCurrentThread();
            workingCopy.setSearchResults( clonedSearchResults );
            EventRegistry.resumeEventFiringInCurrentThread();
        }

        universalListener.setInput( workingCopy );
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
        super.setSite( site );

        // mark dummy location, necessary because the first marked
        // location doesn't appear in history
        setInput( new SearchResultEditorInput( null ) );
        getSite().getPage().getNavigationHistory().markLocation( this );

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
            workingCopy = null;
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
