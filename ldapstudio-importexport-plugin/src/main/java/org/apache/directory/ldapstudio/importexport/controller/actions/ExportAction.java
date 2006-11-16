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

package org.apache.directory.ldapstudio.importexport.controller.actions;

import org.apache.directory.ldapstudio.importexport.view.ExportWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the Export Action
 */
public class ExportAction implements IWorkbenchWindowActionDelegate
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ExportAction.class );
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    /**
     * This method is run when the menu item is clicked
     */
    public void run( IAction action )
    {
        logger.info( "Opening Export Wizard" ); //$NON-NLS-1$
        
        // Instantiates and initializes the wizard
        ExportWizard wizard = new ExportWizard();
        wizard.init(PlatformUI.getWorkbench(), StructuredSelection.EMPTY);
        // Instantiates the wizard container with the wizard and opens it
        WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
        dialog.create();
        dialog.open();
        
        logger.info( "Closing Export Wizard" ); //$NON-NLS-1$
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
        // This method does nothing, but is needed by the IWorkbenchWindowActionDelegate Interface
    }

    public void dispose()
    {
        // This method does nothing, but is needed by the IWorkbenchWindowActionDelegate Interface
    }

    public void init( IWorkbenchWindow window )
    {
        // This method does nothing, but is needed by the IWorkbenchWindowActionDelegate Interface
    }
}
