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
package org.apache.directory.studio.ldapservers.wizards;


import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapterExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * This class implements the new server wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewServerWizard extends Wizard implements INewWizard
{
    /** The wizard page */
    private NewServerWizardPage page;


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        page = new NewServerWizardPage();
        addPage( page );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        // Getting server name and adapter extension
        final String serverName = page.getServerName();
        final LdapServerAdapterExtension adapterExtension = page.getLdapServerAdapterExtension();

        try
        {
            getContainer().run( true, false, new IRunnableWithProgress()
            {
                public void run( IProgressMonitor monitor )
                {
                    // Setting the title
                    monitor.beginTask( "Creating LDAP Server: ", IProgressMonitor.UNKNOWN );
                    monitor.subTask( "creating server folder" );

                    // Creating the new server
                    LdapServer server = new LdapServer();
                    server.setName( serverName );
                    server.setLdapServerAdapterExtension( adapterExtension );

                    // Adding the new server to the servers handler
                    LdapServersManager.getDefault().addServer( server );

                    // Creating the folder for the new server
                    LdapServersManager.createNewServerFolder( server );

                    // Letting the LDAP Server Adapter finish the creation of the server
                    try
                    {
                        adapterExtension.getInstance().add( server, monitor );
                    }
                    catch ( Exception e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // Reporting to the monitor that we're done
                    monitor.done();
                }
            } );
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor( true );
    }
}
