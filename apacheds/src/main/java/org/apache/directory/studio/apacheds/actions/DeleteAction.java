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
package org.apache.directory.studio.apacheds.actions;


import java.io.File;

import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.dialogs.DeleteServerDialog;
import org.apache.directory.studio.apacheds.jobs.LaunchServerJob;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
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
        setId( ApacheDsPluginConstants.CMD_DELETE );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_DELETE );
        setToolTipText( Messages.getString( "DeleteAction.DeleteToolTip" ) ); //$NON-NLS-1$
        setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_TOOL_DELETE ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        if ( view != null )
        {
            // What we get from the TableViewer is a StructuredSelection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();

            // Here's the real object
            Server server = ( Server ) selection.getFirstElement();

            // Asking for confirmation
            DeleteServerDialog dsd = new DeleteServerDialog( view.getSite().getShell(), server );
            if ( dsd.open() == DeleteServerDialog.OK )
            {
                // Checking if the server is running
                // If yes, we need to shut it down before removing its data
                if ( server.getState() == ServerStateEnum.STARTED )
                {
                    // Setting the server of the server to 'stopping'
                    server.setState( ServerStateEnum.STOPPING );

                    // Getting the launch job
                    LaunchServerJob launchJob = server.getLaunchJob();
                    if ( launchJob != null )
                    {
                        // Getting the launch
                        ILaunch launch = launchJob.getLaunch();
                        if ( ( launch != null ) && ( !launch.isTerminated() ) )
                        {
                            // Terminating the launch
                            try
                            {
                                launch.terminate();
                            }
                            catch ( DebugException e )
                            {
                                ApacheDsPluginUtils.reportError( Messages.getString( "DeleteAction.ErrorWhileStopping" ) //$NON-NLS-1$
                                    + e.getMessage() );
                            }
                        }
                    }
                }

                // Removing the server
                ServersHandler.getDefault().removeServer( server );

                // Deleting the associated directory on disk
                deleteDirectory( new File( ApacheDsPluginUtils.getApacheDsServersFolder().append( server.getId() )
                    .toOSString() ) );
            }
        }
    }


    /**
     * Deletes the given directory
     *
     * @param path
     *      the directory
     * @return
     *      <code>true</code> if and only if the directory is 
     *      successfully deleted; <code>false</code> otherwise
     */
    private boolean deleteDirectory( File path )
    {
        if ( path.exists() )
        {
            File[] files = path.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].isDirectory() )
                {
                    deleteDirectory( files[i] );
                }
                else
                {
                    files[i].delete();
                }
            }
        }
        return ( path.delete() );
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
