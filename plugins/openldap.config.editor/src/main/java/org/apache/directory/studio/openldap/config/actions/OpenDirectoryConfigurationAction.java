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
package org.apache.directory.studio.openldap.config.actions;


import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import org.apache.directory.studio.openldap.config.editor.DirectoryServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.OpenLDAPServerConfigurationEditor;


/**
 * This class implements the action that opens a directory based OpenLDAP configuration
 * (asking the user the location of the 'slapd.d' directory).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenDirectoryConfigurationAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The window */
    private IWorkbenchWindow window;


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        this.window = window;
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        // Creating a directory dialog for the user
        // to select the "slapd.d" directory
        DirectoryDialog dialog = new DirectoryDialog( window.getShell(), SWT.OPEN );
        dialog.setText( "Choose 'slapd.d' folder..." );
        dialog.setFilterPath( System.getProperty( "user.home" ) );

        // Getting the directory selected by the user
        String selectedDirectory = dialog.open();

        if ( selectedDirectory != null )
        {
            try
            {
                window.getActivePage().openEditor( new DirectoryServerConfigurationInput( new File(
                    selectedDirectory ) ), OpenLDAPServerConfigurationEditor.ID );
            }
            catch ( PartInitException e )
            {
                // Should never happen
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // TODO Auto-generated method stub

    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // TODO Auto-generated method stub

    }
}
