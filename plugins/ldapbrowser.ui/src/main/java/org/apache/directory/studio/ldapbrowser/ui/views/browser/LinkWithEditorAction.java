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
import org.apache.directory.studio.ldapbrowser.ui.editors.searchresult.SearchResultEditorInput;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;


/**
 * This class implements the Link With Editor Action for the Browser View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LinkWithEditorAction extends AbstractLinkWithEditorAction
{
    /** The browser view */
    private BrowserView browserView;


    /**
     * Creates a new instance of LinkWithEditorAction.
     *
     * @param browserView
     *      the associated view
     */
    public LinkWithEditorAction( BrowserView browserView )
    {
        super( browserView, Messages.getString( "LinkWithEditorAction.LinkWithEditor" ) ); //$NON-NLS-1$
        this.browserView = browserView;
        super.init();
    }


    /**
     * Links the view with the right editor
     *
     * @param partRef the part
     */
    protected void linkViewWithEditor( IWorkbenchPart part )
    {
        if ( part != null && browserView != null
            && part.getSite().getWorkbenchWindow() == browserView.getSite().getWorkbenchWindow() )
        {
            Object objectToSelect = null;

            if ( part instanceof IEditorPart )
            {
                IEditorPart editor = ( IEditorPart ) part;
                IEditorInput input = editor.getEditorInput();
                if ( input instanceof EntryEditorInput )
                {
                    EntryEditorInput eei = ( EntryEditorInput ) input;
                    objectToSelect = eei.getInput();
                }
                else if ( input instanceof SearchResultEditorInput )
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

}
