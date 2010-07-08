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


import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.editors.searchresult.SearchResultEditorInput;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;


/**
 * This class implements the Link With Editor Action for the Browser View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LinkWithEditorAction extends Action
{
    /** The browser view */
    private BrowserView browserView;

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
            linkViewWithEditor( partRef.getPart( false ) );
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


    /**
     * Creates a new instance of LinkWithEditorAction.
     *
     * @param browserView
     *      the associated view
     */
    public LinkWithEditorAction( BrowserView browserView )
    {
        super( Messages.getString( "LinkWithEditorAction.LinkWithEditor" ), AS_CHECK_BOX ); //$NON-NLS-1$
        setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_LINK_WITH_EDITOR ) );
        setEnabled( true );
        setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR ) );
        this.browserView = browserView;

        // Enable the listeners
        if ( isChecked() )
        {
            browserView.getSite().getWorkbenchWindow().getPartService().addPartListener( editorListener );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        setChecked( isChecked() );
        BrowserUIPlugin.getDefault().getPreferenceStore().setValue(
            BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR, isChecked() );

        if ( isChecked() )
        {
            // Enable the listener
            browserView.getSite().getWorkbenchWindow().getPartService().addPartListener( editorListener );

            // link
            IEditorPart activeEditor = browserView.getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
            linkViewWithEditor( activeEditor );
        }
        else
        {
            // Disable the listener
            browserView.getSite().getWorkbenchWindow().getPartService().removePartListener( editorListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param partRef the part
     */
    private void linkViewWithEditor( IWorkbenchPart part )
    {
        if ( part != null && browserView != null
            && part.getSite().getWorkbenchWindow() == browserView.getSite().getWorkbenchWindow() )
        {
            Object objectToSelect = null;

            if ( part instanceof IEditorPart )
            {
                IEditorPart editor = ( IEditorPart ) part;
                IEditorInput input = editor.getEditorInput();
                if ( input != null && input instanceof EntryEditorInput )
                {
                    EntryEditorInput eei = ( EntryEditorInput ) input;
                    objectToSelect = eei.getInput();
                }
                else if ( input != null && input instanceof SearchResultEditorInput )
                {
                    SearchResultEditorInput srei = ( SearchResultEditorInput ) input;
                    objectToSelect = srei.getSearch();
                }
            }

            if ( objectToSelect != null )
            {
                // do not select if already selected!
                // necessary to avoid infinite loops!
                IStructuredSelection selection = ( IStructuredSelection ) browserView.getMainWidget().getViewer()
                    .getSelection();
                if ( selection.size() != 1 || !selection.getFirstElement().equals( objectToSelect ) )
                {
                    browserView.select( objectToSelect );
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
            browserView.getSite().getWorkbenchWindow().getPartService().removePartListener( editorListener );
            editorListener = null;
        }

        browserView = null;
    }

}
