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


import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.jobs.LaunchServerInstanceJob;
import org.apache.directory.studio.apacheds.model.ServerInstance;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This class implements the stop action for a server instance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StopAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of StopAction.
     */
    public StopAction( ServersView view )
    {
        super( "S&top" );
        setId( ApacheDsPluginConstants.CMD_STOP );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_STOP );
        setToolTipText( "Stop" );
        setImageDescriptor( ApacheDsPlugin.getDefault().getImageDescriptor( ApacheDsPluginConstants.IMG_STOP ) );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        // Getting the selection
        StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
        if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
        {
            // Getting the server
            ServerInstance server = ( ServerInstance ) selection.getFirstElement();

            // Setting the server of the server to 'stopping'
            server.setState( ServerStateEnum.STOPPING );

            // Getting the launch job
            LaunchServerInstanceJob launchJob = server.getLaunchJob();
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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
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
