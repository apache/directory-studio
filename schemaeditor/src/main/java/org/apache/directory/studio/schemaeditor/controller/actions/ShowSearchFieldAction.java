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
package org.apache.directory.studio.schemaeditor.controller.actions;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.views.SearchView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This action is used to show/hide the search field in the SearchView.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ShowSearchFieldAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The String for storing the checked state of the action */
    private static final String SHOW_SEARCH_FIELD_DS_KEY = ShowSearchFieldAction.class.getName() + ".dialogsettingkey"; //$NON-NLS-1$

    /** The associated view */
    private SearchView view;


    /**
     * Creates a new instance of ShowSearchFieldAction.
     */
    public ShowSearchFieldAction( SearchView view )
    {
        super( "Show Search Field", AS_CHECK_BOX );
        setToolTipText( getText() );
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SHOW_SEARCH_FIELD ) );
        setEnabled( true );
        this.view = view;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( SHOW_SEARCH_FIELD_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( SHOW_SEARCH_FIELD_DS_KEY, false );
        }

        // Setting state from the dialog settings
        setChecked( Activator.getDefault().getDialogSettings().getBoolean( SHOW_SEARCH_FIELD_DS_KEY ) );

        if ( isChecked() )
        {
            view.showSearchFieldSection();
        }
        else
        {
            view.hideSearchFieldSection();
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        setChecked( isChecked() );
        Activator.getDefault().getDialogSettings().put( SHOW_SEARCH_FIELD_DS_KEY, isChecked() );

        if ( isChecked() )
        {
            view.showSearchFieldSection();
        }
        else
        {
            view.hideSearchFieldSection();
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
