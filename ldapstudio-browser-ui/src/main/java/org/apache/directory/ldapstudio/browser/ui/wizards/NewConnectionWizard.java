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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.jobs.OpenConnectionsJob;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.connection.ConnectionPageWrapper;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * The NewConnectionWizard is used to create a new connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewConnectionWizard extends Wizard implements INewWizard
{

    /** The connection page wrapper. */
    private ConnectionPageWrapper cpw;

    /** The main page. */
    private NewConnectionMainWizardPage mainPage;

    /** The auth page. */
    private NewConnectionAuthWizardPage authPage;

    /** The options page. */
    private NewConnectionOptionsWizardPage optionsPage;


    /**
     * Creates a new instance of NewConnectionWizard.
     */
    public NewConnectionWizard()
    {
        setWindowTitle( "New LDAP Connection" );
        setNeedsProgressMonitor( true );
    }


    /**
     * Gets the id.
     * 
     * @return the id
     */
    public static String getId()
    {
        return NewConnectionWizard.class.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void addPages()
    {
        cpw = new ConnectionPageWrapper( null, getContainer() );

        mainPage = new NewConnectionMainWizardPage( NewConnectionMainWizardPage.class.getName(), this );
        addPage( mainPage );

        authPage = new NewConnectionAuthWizardPage( NewConnectionAuthWizardPage.class.getName(), this );
        addPage( authPage );

        optionsPage = new NewConnectionOptionsWizardPage( NewConnectionOptionsWizardPage.class.getName(), this );
        addPage( optionsPage );
    }


    /**
     * {@inheritDoc}
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // set help context ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp( mainPage.getControl(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_newconnection_wizard" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( authPage.getControl(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_newconnection_wizard" );
        PlatformUI.getWorkbench().getHelpSystem().setHelp( optionsPage.getControl(),
            BrowserUIPlugin.PLUGIN_ID + "." + "tools_newconnection_wizard" );
    }


    /**
     * {@inheritDoc}
     */
    public boolean canFinish()
    {
        if ( cpw.getAuthenticationMethod() == IConnection.AUTH_ANONYMOUS )
        {
            return mainPage.isPageComplete() && optionsPage.isPageComplete();
        }
        else if ( cpw.getAuthenticationMethod() == IConnection.AUTH_SIMPLE )
        {
            return mainPage.isPageComplete() && authPage.isPageComplete() && optionsPage.isPageComplete();
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean performFinish()
    {
        final IConnection conn = cpw.getTestConnection();
        conn.setName( cpw.getName() );
        if ( conn != null )
        {
            BrowserCorePlugin.getDefault().getConnectionManager().addConnection( conn );
            if ( cpw.isOpenConnectionOnFinish() )
            {
                new OpenConnectionsJob( conn ).execute();
            }
            cpw.saveDialogSettings();
            return true;
        }
        return false;
    }


    /**
     * Gets the connection page wrapper.
     * 
     * @return the connection page wrapper
     */
    public ConnectionPageWrapper getCpw()
    {
        return cpw;
    }

}
