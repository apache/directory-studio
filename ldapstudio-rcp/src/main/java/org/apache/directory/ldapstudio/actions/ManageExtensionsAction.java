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
 * This class implements the Manage Extensions Action.
 * It uses Eclipse Plugin Manager to allow user to 
 * manager their plugin configuration.
 *
 */
public class ManageExtensionsAction extends Action implements IAction
{
    private IWorkbenchWindow window;


    /**
     * Default constructor
     * @param window
     */
    public ManageExtensionsAction( IWorkbenchWindow window )
    {
        this.window = window;
        setId( "org.apache.directory.ldapstudio.manageExtensions" ); //$NON-NLS-1$
        setText( Messages.getString( "ManageExtensionsAction.Manage_Extensions" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "ManageExtensionsAction.Manage_extensions_for_LDAP_Studio" ) ); //$NON-NLS-1$
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
