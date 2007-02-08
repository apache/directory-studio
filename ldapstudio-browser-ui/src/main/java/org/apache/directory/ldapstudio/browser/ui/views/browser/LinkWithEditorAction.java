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


import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.editors.entry.EntryEditor;
import org.apache.directory.ldapstudio.browser.ui.editors.entry.EntryEditorManager;
import org.apache.directory.ldapstudio.browser.ui.editors.searchresult.SearchResultEditor;
import org.apache.directory.ldapstudio.browser.ui.editors.searchresult.SearchResultEditorManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Link With Editor Action for the Browser View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LinkWithEditorAction extends Action
{
    /** The browser view */
    private BrowserView browserView;

    /** The listener listening on changes on editors */
    private IPartListener2 editorListener = new IPartListener2()
    {
        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partBroughtToTop( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partActivated( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partClosed( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partDeactivated( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partHidden( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partInputChanged( IWorkbenchPartReference partRef )
        {
            linkViewWithEditor( partRef.getId() );
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partOpened( IWorkbenchPartReference partRef )
        {
        }


        /* (non-Javadoc)
         * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
         */
        public void partVisible( IWorkbenchPartReference partRef )
        {
        }
    };


    /**
     * Creates a new instance of LinkWithEditorAction.
     *
     * @param view
     *      the associated view
     */
    public LinkWithEditorAction( BrowserView browserView )
    {
        super( "Link with editor", AS_CHECK_BOX );
        //        super.setActionDefinitionId( Activator.PLUGIN_ID + "linkwitheditorschemasview" );

        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor(
            BrowserUIConstants.IMG_LINK_WITH_EDITOR ) );
        super.setEnabled( true );
        this.browserView = browserView;

        super.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR ) );

        // Enabling the listeners
        if ( isChecked() )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        setChecked( isChecked() );
        BrowserUIPlugin.getDefault().getPreferenceStore().setValue(
            BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR, isChecked() );

        if ( isChecked() )
        {
            // Enabling the listeners
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener( editorListener );

            IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
            if ( activeEditor instanceof EntryEditor )
            {
                linkViewWithEditor( EntryEditor.getId() );
            }
            else if ( activeEditor instanceof SearchResultEditor )
            {
                linkViewWithEditor( SearchResultEditor.getId() );
            }
        }
        else
        {
            // Disabling the listeners
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( editorListener );
        }
    }


    /**
     * Links the view with the right editor
     *
     * @param editorID
     *      the id of the editor
     */
    private void linkViewWithEditor( String editorID )
    {
        Object objectToSelect = null;

        // Only entry editor and search result editor are accepted
        if ( editorID.equals( EntryEditor.getId() ) )
        {
            Object input = EntryEditorManager.getInput();
            objectToSelect = input;

        }
        else if ( editorID.equals( SearchResultEditor.getId() ) )
        {
            ISearch search = SearchResultEditorManager.getInput();
            objectToSelect = search;
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


    /**
     * Disposes this action.
     */
    public void dispose()
    {
        if ( editorListener != null )
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener( editorListener );
            editorListener = null;
        }

        browserView = null;
    }

}
