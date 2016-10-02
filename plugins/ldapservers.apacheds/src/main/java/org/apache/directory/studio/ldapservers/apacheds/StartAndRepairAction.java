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

package org.apache.directory.studio.ldapservers.apacheds;


import org.apache.directory.studio.ldapservers.jobs.StudioLdapServerJob;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.views.ServersView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * This class implements the repair action for an ApacheDS 2.0.0 server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StartAndRepairAction implements IObjectActionDelegate
{
    /** The {@link ServersView} */
    private ServersView view;


    @Override
    public void run( IAction action )
    {
        if ( view != null )
        {
            // Getting the selection
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
            if ( ( !selection.isEmpty() ) && ( selection.size() == 1 ) )
            {
                // Getting the server
                LdapServer server = ( LdapServer ) selection.getFirstElement();

                // Checking that the server is really an ApacheDS 2.0.0 server
                if ( !ExtensionUtils.verifyApacheDs200OrPrintError( server, view ) )
                {
                    return;
                }

                // Creating and scheduling the job to start the server
                StudioLdapServerJob job = new StudioLdapServerJob( new StartAndRepairRunnable( server ) );
                job.schedule();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
        // Storing the Servers view
        if ( targetPart instanceof ServersView )
        {
            view = ( ServersView ) targetPart;
        }
    }

}
