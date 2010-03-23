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
package org.apache.directory.studio.apacheds.configuration.wizards;


import org.apache.directory.studio.apacheds.configuration.ApacheDSConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.editor.NonExistingServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIO;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v150.ServerXmlIOV150;
import org.apache.directory.studio.apacheds.configuration.model.v151.ServerXmlIOV151;
import org.apache.directory.studio.apacheds.configuration.model.v152.ServerXmlIOV152;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerXmlIOV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerXmlIOV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerXmlIOV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerXmlIOV156;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the New ApacheDS Configuration File Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewApacheDSConfigurationFileWizard extends Wizard implements INewWizard
{
    /** The window */
    private IWorkbenchWindow window;

    /** The page */
    private NewApacheDSConfigurationFileWizardPage page;


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        page = new NewApacheDSConfigurationFileWizardPage();
        addPage( page );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        try
        {
            // Getting the default server configuration for the target version
            ServerConfiguration serverConfiguration = null;
            ServerXmlIO serverXmlIO = null;
            switch ( page.getTargetVersion() )
            {
                case VERSION_1_5_6:
                    serverXmlIO = new ServerXmlIOV156();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.6.xml" ) ); //$NON-NLS-1$
                    break;
                case VERSION_1_5_5:
                    serverXmlIO = new ServerXmlIOV155();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.5.xml" ) ); //$NON-NLS-1$
                    break;
                case VERSION_1_5_4:
                    serverXmlIO = new ServerXmlIOV154();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.4.xml" ) ); //$NON-NLS-1$
                    break;
                case VERSION_1_5_3:
                    serverXmlIO = new ServerXmlIOV153();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.3.xml" ) ); //$NON-NLS-1$
                    break;
                case VERSION_1_5_2:
                    serverXmlIO = new ServerXmlIOV152();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.2.xml" ) ); //$NON-NLS-1$
                    break;
                case VERSION_1_5_1:
                    serverXmlIO = new ServerXmlIOV151();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.1.xml" ) ); //$NON-NLS-1$
                    break;
                case VERSION_1_5_0:
                    serverXmlIO = new ServerXmlIOV150();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.0.xml" ) ); //$NON-NLS-1$
                    break;
                default:
                    serverXmlIO = new ServerXmlIOV156();
                    serverConfiguration = serverXmlIO.parse( ApacheDSConfigurationPlugin.class
                        .getResourceAsStream( "default-server-1.5.6.xml" ) ); //$NON-NLS-1$
                    break;
            }

            IWorkbenchPage page = window.getActivePage();
            page.openEditor( new NonExistingServerConfigurationInput( serverConfiguration ),
                ServerConfigurationEditor.ID );
        }
        catch ( PartInitException e )
        {
            return false;
        }
        catch ( ServerXmlIOException e )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( Messages.getString( "NewApacheDSConfigurationFileWizard.Error" ) ); //$NON-NLS-1$
            messageBox
                .setMessage( Messages.getString( "NewApacheDSConfigurationFileWizard.ErrorReadingFile" ) + e.getMessage() ); //$NON-NLS-1$
            messageBox.open();
            return false;
        }
        return true;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
        window = workbench.getActiveWorkbenchWindow();
    }
}
