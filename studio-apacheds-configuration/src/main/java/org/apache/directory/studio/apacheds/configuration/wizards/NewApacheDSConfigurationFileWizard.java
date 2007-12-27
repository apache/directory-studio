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


import org.apache.directory.studio.apacheds.configuration.Activator;
import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.editor.NonExistingServerConfigurationInput;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationParser;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfigurationParserException;
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


    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        try
        {
            ServerConfigurationParser parser = new ServerConfigurationParser();
            ServerConfiguration serverConfiguration = parser.parse( Activator.class
                .getResourceAsStream( "default-server.xml" ) );

            IWorkbenchPage page = window.getActivePage();
            page.openEditor( new NonExistingServerConfigurationInput( serverConfiguration ), ServerConfigurationEditor.ID );
        }
        catch ( PartInitException e )
        {
            return false;
        }
        catch ( ServerConfigurationParserException e )
        {
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox.setText( "Error!" );
            messageBox.setMessage( "An error occurred when reading the file." + "\n" + e.getMessage() );
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
