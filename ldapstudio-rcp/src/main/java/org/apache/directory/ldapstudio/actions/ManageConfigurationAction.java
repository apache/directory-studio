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

package org.apache.directory.ldapstudio.actions;


import org.apache.directory.ldapstudio.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.ui.UpdateManagerUI;


/**
 * This class implements the Manage Configuration Action.
 * It uses Eclipse Plugin Manager to allow user to 
 * manager their plugin configuration.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ManageConfigurationAction extends Action implements IAction
{
    private IWorkbenchWindow window;


    /**
     * Default constructor
     * @param window
     *          the window it is attached to
     */
    public ManageConfigurationAction( IWorkbenchWindow window )
    {
        this.window = window;
        setId( "org.apache.directory.ldapstudio.manageConfiguration" ); //$NON-NLS-1$
        setText( Messages.getString( "ManageConfigurationAction.Manage_Configuration" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "ManageConfigurationAction.Manage_configuration_for_LDAP_Studio" ) ); //$NON-NLS-1$
    }


    /**
     * Runs the action
     */
    public void run()
    {
        BusyIndicator.showWhile( window.getShell().getDisplay(), new Runnable()
        {
            public void run()
            {
                UpdateManagerUI.openConfigurationManager( window.getShell() );
            }
        } );
    }
}
