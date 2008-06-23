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

package org.apache.directory.studio.connection.ui.wizards;


import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionFolder;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.jobs.OpenConnectionsRunnable;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionJob;
import org.apache.directory.studio.connection.ui.ConnectionParameterPage;
import org.apache.directory.studio.connection.ui.ConnectionParameterPageManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * The NewConnectionWizard is used to create a new connection.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewConnectionWizard extends Wizard implements INewWizard
{

    /** The wizard pages. */
    private NewConnectionWizardPage[] wizardPages;

    /** The connection parameter pages. */
    private ConnectionParameterPage[] pages;

    /** The selected connection folder. */
    private ConnectionFolder selectedConnectionFolder;

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
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        Object firstElement = selection.getFirstElement();
        if ( firstElement instanceof ConnectionFolder )
        {
            selectedConnectionFolder = ( ConnectionFolder ) firstElement;
        }
        else if ( firstElement instanceof Connection )
        {
            Connection connection = ( Connection ) firstElement;
            selectedConnectionFolder = ConnectionCorePlugin.getDefault().getConnectionFolderManager()
                .getParentConnectionFolder( connection );
        }

        if ( selectedConnectionFolder == null )
        {
            selectedConnectionFolder = ConnectionCorePlugin.getDefault().getConnectionFolderManager()
                .getRootConnectionFolder();
        }
    }


    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        pages = ConnectionParameterPageManager.getConnectionParameterPages();

        wizardPages = new NewConnectionWizardPage[pages.length];
        for ( int i = 0; i < pages.length; i++ )
        {
            wizardPages[i] = new NewConnectionWizardPage( this, pages[i] );
            addPage( wizardPages[i] );
            pages[i].setRunnableContext( getContainer() );
        }
    }


    /**
     * @see org.eclipse.jface.wizard.Wizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        // TODO: set help context ID, move help from browser-help plugin to connections-help plugin 
        //        for ( NewConnectionWizardPage wizardPage : wizardPages )
        //        {
        //            PlatformUI.getWorkbench().getHelpSystem().setHelp( wizardPage.getControl(),
        //                ConnectionUIPlugin.PLUGIN_ID + "." + "tools_newconnection_wizard" );
        //        }
    }


    /**
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    public boolean canFinish()
    {
        for ( int i = 0; i < pages.length; i++ )
        {
            if ( !pages[i].isValid() )
            {
                return false;
            }
        }
        return true;
    }


    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        // get connection parameters from pages and save dialog settings 
        ConnectionParameter connectionParameter = new ConnectionParameter();
        for ( int i = 0; i < pages.length; i++ )
        {
            pages[i].saveParameters( connectionParameter );
            pages[i].saveDialogSettings();
        }

        // create persistent connection
        final Connection conn = new Connection( connectionParameter );
        ConnectionCorePlugin.getDefault().getConnectionManager().addConnection( conn );
        
        // add connection to folder
        selectedConnectionFolder.addConnectionId( conn.getId() );

        // open connection
        new StudioConnectionJob( new OpenConnectionsRunnable( conn ) ).execute();

        return true;
    }


    /**
     * Gets the test connection parameters.
     * 
     * @return the test connection parameters
     */
    public ConnectionParameter getTestConnectionParameters()
    {
        ConnectionParameter connectionParameter = new ConnectionParameter();
        for ( int i = 0; i < pages.length; i++ )
        {
            pages[i].saveParameters( connectionParameter );
        }
        return connectionParameter;
    }

}
