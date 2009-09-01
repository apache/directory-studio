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

package org.apache.directory.studio.ldapbrowser.ui.views.connection;


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.editors.entry.EntryEditor;
import org.apache.directory.studio.ldapbrowser.ui.editors.searchresult.SearchResultEditor;
import org.apache.directory.studio.ldapbrowser.ui.editors.searchresult.SearchResultEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;


/**
 * This class implements the Link With Editor Action for the Connection View.
 * 
 * This action is not visible to the user, but it listens to to the link 
 * with editor property of the browser view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LinkWithEditorAction extends Action
{
    /** The connection view */
    private ConnectionView connectionView;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {
        /**
         * {@inheritDoc}
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /**
         * {@inheritDoc}
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
            linkViewWithEditor( partRef.getPart( false ) );
        }
    };

    /** The listener listening on changes of the link with editor action of the browser view */
    private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener()
    {

        /**
         * {@inheritDoc}
         */
        public void propertyChange( PropertyChangeEvent event )
        {
            if ( BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR.equals( event.getProperty() ) )
            {
                run();
            }
        }
    };


    /**
     * Creates a new instance of LinkWithEditorAction.
     *
     * @param connectionView
     *      the associated view
     */
    public LinkWithEditorAction( ConnectionView connectionView )
    {
        super( Messages.getString( "LinkWithEditorAction.LinkWithEditor" ), AS_CHECK_BOX ); //$NON-NLS-1$

        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_LINK_WITH_EDITOR ) );
        super.setEnabled( true );
        this.connectionView = connectionView;

        super.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR ) );

        // enable the listeners
        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( propertyChangeListener );
        if ( isChecked() )
        {
            connectionView.getSite().getWorkbenchWindow().getPartService().addPartListener( editorListener );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR ) );

        if ( isChecked() )
        {
            // enable the listeners
            connectionView.getSite().getWorkbenchWindow().getPartService().addPartListener( editorListener );

            // link
            IEditorPart activeEditor = connectionView.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
            linkViewWithEditor( activeEditor );
        }
        else
        {
            // dsable the listeners
            connectionView.getSite().getWorkbenchWindow().getPartService().removePartListener( editorListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param partRef the part
     */
    private void linkViewWithEditor( IWorkbenchPart part )
    {
        if ( part != null && connectionView != null
            && part.getSite().getWorkbenchWindow() == connectionView.getSite().getWorkbenchWindow() )
        {
            Object objectToSelect = null;

            if ( part instanceof EntryEditor )
            {
                EntryEditor editor = ( EntryEditor ) part;
                IEditorInput input = editor.getEditorInput();
                if ( input != null && input instanceof EntryEditorInput )
                {
                    EntryEditorInput eei = ( EntryEditorInput ) input;
                    IEntry entry = eei.getResolvedEntry();
                    if ( entry != null )
                    {
                        objectToSelect = entry.getBrowserConnection().getConnection();
                    }
                }
            }
            else if ( part instanceof SearchResultEditor )
            {
                SearchResultEditor editor = ( SearchResultEditor ) part;
                IEditorInput input = editor.getEditorInput();
                if ( input != null && input instanceof SearchResultEditorInput )
                {
                    SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
                    ISearch search = srei.getSearch();
                    if ( search != null )
                    {
                        objectToSelect = search.getBrowserConnection().getConnection();
                    }
                }
            }

            if ( objectToSelect != null )
            {
                // do not select if already selected!
                // necessary to avoid infinite loops!
                IStructuredSelection selection = ( IStructuredSelection ) connectionView.getMainWidget().getViewer()
                    .getSelection();
                if ( selection.size() != 1 || !selection.getFirstElement().equals( objectToSelect ) )
                {
                    connectionView.select( objectToSelect );
                }
            }
        }
    }


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        if ( editorListener != null )
        {
            connectionView.getSite().getWorkbenchWindow().getPartService().removePartListener( editorListener );
            BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( propertyChangeListener );
            editorListener = null;
        }

        connectionView = null;
    }

}
