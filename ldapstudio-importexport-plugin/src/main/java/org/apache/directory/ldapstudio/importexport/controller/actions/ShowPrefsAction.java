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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the Show Preferences Action. It launches the Preferences Window
 * and displays the LDAP Server Configuration preference page.
 */
public class ShowPrefsAction implements IWorkbenchWindowActionDelegate
{
    // The logger
    private static Logger logger = LoggerFactory.getLogger( ShowPrefsAction.class );
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        logger.info( "Opening Import/Export preference page window" ); //$NON-NLS-1$
        
        PreferenceDialog pd = new PreferenceDialog(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
            PlatformUI.getWorkbench().getPreferenceManager() );
        pd.setSelectedNode( "org.apache.directory.ldapstudio.importexport.server" ); //$NON-NLS-1$
        pd.open();
        
        logger.info( "Closing Import/Export preference page window" ); //$NON-NLS-1$
    }

    public void selectionChanged( IAction action, ISelection selection )
    {
    }

    public void dispose()
    {
    }

    public void init( IWorkbenchWindow window )
    {
    }
}
