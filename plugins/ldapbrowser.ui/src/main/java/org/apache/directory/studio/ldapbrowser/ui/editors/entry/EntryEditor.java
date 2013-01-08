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


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.entryeditors.IEntryEditor;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidget;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * The EntryEditor is an {@link IEditorPart} is used to display and edit the attributes of an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class EntryEditor extends EditorPart implements IEntryEditor, INavigationLocationProvider,
    IReusableEditor, IShowEditorInput
{

    /** The editor configuration. */
    protected EntryEditorConfiguration configuration;

    /** The action group. */
    protected EntryEditorActionGroup actionGroup;

    /** The main widget. */
    protected EntryEditorWidget mainWidget;

    /** The universal listener. */
    protected EntryEditorUniversalListener universalListener;

    /** The outline page. */
    protected EntryEditorOutlinePage outlinePage;

    IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
    {
        public void propertyChange( org.eclipse.jface.util.PropertyChangeEvent event )
        {
            // set the input again if the auto-save option has been changed
            if ( event.getProperty() != null )
            {
                if ( event.getProperty().equals( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTOSAVE_SINGLE_TAB )
                    || event.getProperty().equals( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTOSAVE_MULTI_TAB ) )
                {
                    setInput( getEditorInput() );
                }
            }
        }
    };


    /**
     * {@inheritDoc}
     */
    public void init( IEditorSite site, IEditorInput input ) throws PartInitException
    {
        setSite( site );
        setInput( input );
        BrowserCommonActivator.getDefault().getPreferenceStore().addPropertyChangeListener( propertyChangeListener );
    }


    /**
     * {@inheritDoc}
     */
    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        EntryEditorInput eei = getEntryEditorInput();
        setEntryEditorWidgetInput( eei );
        setEditorName( eei );

        // refresh outline
        if ( outlinePage != null )
        {
            outlinePage.refresh();
        }
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
            BrowserUIConstants.PLUGIN_ID + "." + "tools_table_entry_editor" ); //$NON-NLS-1$ //$NON-NLS-2$

        // create configuration
        configuration = new EntryEditorConfiguration( this );

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
            BrowserCommonActivator.getDefault().getPreferenceStore().removePropertyChangeListener(
                propertyChangeListener );
        }

        super.dispose();
    }


    /**
     * {@inheritDoc}
     */
    public void doSave( final IProgressMonitor monitor )
    {
        if ( !isAutoSave() )
        {
            EntryEditorInput eei = getEntryEditorInput();
            eei.saveSharedWorkingCopy( true, this );
        }
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
        return getEntryEditorInput().isSharedWorkingCopyDirty( this );
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


    /**
     * Sets the editor name.
     * 
     * @param input the new editor name
     */
    protected void setEditorName( EntryEditorInput input )
    {
        setPartName( input.getName() );
    }


    /**
     * This implementation returns always true.
     * 
     * {@inheritDoc}
     */
    public boolean canHandle( IEntry entry )
    {
        return true;
    }


    /**
     * {@inheritDoc}
     */
    public EntryEditorInput getEntryEditorInput()
    {
        return EntryEditorUtils.getEntryEditorInput( getEditorInput() );
    }


    /**
     * {@inheritDoc}
     */
    public void workingCopyModified( Object source )
    {
        if ( mainWidget != null && !mainWidget.getViewer().isCellEditorActive() )
        {
            ISelection selection = mainWidget.getViewer().getSelection();
            mainWidget.getViewer().refresh();
            mainWidget.getViewer().setSelection( selection );
        }

        if ( !isAutoSave() )
        {
            // mark as dirty
            firePropertyChange( PROP_DIRTY );
        }
    }


    /**
     * Sets the entry editor widget input. A clone of the real entry
     * with a read-only connection is used for that.
     * @param eei 
     */
    private void setEntryEditorWidgetInput( EntryEditorInput eei )
    {
        if ( mainWidget != null )
        {
            universalListener.setInput( eei.getSharedWorkingCopy( this ) );

            /*
             * Explicitly deselect previously selected attributes and values.
             * This avoids disabled actions if the new input is equal but not
             * identical to the previous input. This happens for example if
             * an ISearchResult or IBookmark object is open and afterwards 
             * the IEntry object is opened.
             */
            mainWidget.getViewer().setSelection( StructuredSelection.EMPTY );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void showEditorInput( IEditorInput input )
    {
        if ( input instanceof EntryEditorInput )
        {
            // If the editor is dirty, let's ask for a save before changing the input
            if ( isDirty() )
            {
                if ( !EntryEditorUtils.askSaveSharedWorkingCopyBeforeInputChange( this ) )
                {
                    return;
                }
            }

            /*
             * Workaround to make link-with-editor working for the single-tab editor:
             * The call of firePropertyChange is used to inform the link-with-editor action.
             * However firePropertyChange also modifies the navigation history.
             * Thus, a dummy input with the real entry but a null extension is set.
             * This avoids to modification of the navigation history.
             * Afterwards the real input is set.
             */
            EntryEditorInput eei = ( EntryEditorInput ) input;
            IEntry entry = eei.getEntryInput();
            ISearchResult searchResult = eei.getSearchResultInput();
            IBookmark bookmark = eei.getBookmarkInput();
            EntryEditorInput dummyInput;
            if ( entry != null )
            {
                dummyInput = new EntryEditorInput( entry, null );
            }
            else if ( searchResult != null )
            {
                dummyInput = new EntryEditorInput( searchResult, null );
            }
            else
            {
                dummyInput = new EntryEditorInput( bookmark, null );
            }
            setInput( dummyInput );
            firePropertyChange( IEditorPart.PROP_INPUT );

            // now set the real input and mark history location
            setInput( input );
            getSite().getPage().getNavigationHistory().markLocation( this );
        }
    }

}
