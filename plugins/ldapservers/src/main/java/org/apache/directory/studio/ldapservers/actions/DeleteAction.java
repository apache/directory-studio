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
package org.apache.directory.studio.ldapservers.actions;


import org.apache.directory.studio.ldapservers.LdapServersPluginConstants;
import org.apache.directory.studio.ldapservers.dialogs.DeleteServerDialog;
import org.apache.directory.studio.ldapservers.jobs.DeleteLdapServerRunnable;
import org.apache.directory.studio.ldapservers.jobs.StudioLdapServerJob;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.views.ServersView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the delete action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of DeleteAction.
     */
    public DeleteAction()
    {
        super( Messages.getString( "DeleteAction.Delete" ) ); //$NON-NLS-1$
        init();
    }


    /**
     * Creates a new instance of DeleteAction.
     * 
     * @param view
     *      the associated view
     */
    public DeleteAction( ServersView view )
    {
        super( Messages.getString( "DeleteAction.Delete" ) ); //$NON-NLS-1$
        this.view = view;
        init();
    }


    /**
     * Initializes the action.
     */
    private void init()
    {
        setId( LdapServersPluginConstants.CMD_DELETE );
        setActionDefinitionId( LdapServersPluginConstants.CMD_DELETE );
        setToolTipText( Messages.getString( "DeleteAction.DeleteToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( PlatformUI.getWorkbench().getSharedImages()
            .getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( view != null )
        {
            // What we get from the TableViewer is a StructuredSelection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();

            // Here's the real object
            LdapServer server = ( LdapServer ) selection.getFirstElement();

            // Asking for confirmation
            DeleteServerDialog dsd = new DeleteServerDialog( view.getSite().getShell(), server );
            if ( dsd.open() == DeleteServerDialog.OK )
            {
                // Creating and scheduling the job to delete the server
                StudioLdapServerJob job = new StudioLdapServerJob( new DeleteLdapServerRunnable( server ) );
                job.schedule();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
