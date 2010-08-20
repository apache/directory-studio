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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This Action opens the Search Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenSearchAction extends Action implements IWorkbenchWindowActionDelegate
{
    /**
     * Creates a new instance of OpenSearchAction.
     */
    public OpenSearchAction()
    {
        super( Messages.getString( "OpenSearchAction.Search" ), Action.AS_PUSH_BUTTON ); //$NON-NLS-1$
        super.setText( Messages.getString( "OpenSearchAction.Search" ) ); //$NON-NLS-1$
        super.setToolTipText( Messages.getString( "OpenSearchAction.Search" ) ); //$NON-NLS-1$
        super.setImageDescriptor( BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_SEARCH ) );
        super.setEnabled( true );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
//        NewSearchUI.openSearchDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow(), SearchPage.getId() );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        this.run();
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }
}
