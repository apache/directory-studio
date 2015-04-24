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


import org.apache.directory.studio.connection.core.Connection;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.openldap.config.editor.ConnectionServerConfigurationInput;
import org.apache.directory.studio.openldap.config.editor.ServerConfigurationEditor;


/**
 * This class implements the action which open the OpenLDAP configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenConfigurationAction implements IObjectActionDelegate
{
    /** The selected connection */
    private Connection selectedConnection;


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        if ( selectedConnection != null )
        {
            try
            {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .openEditor( new ConnectionServerConfigurationInput( selectedConnection ),
                        ServerConfigurationEditor.ID );
            }
            catch ( Exception e )
            {
                // Will never occur.
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        StructuredSelection structuredSelection = ( StructuredSelection ) selection;
        
        if ( ( structuredSelection.size() == 1 ) && ( structuredSelection.getFirstElement() instanceof Connection ) )
        {
            selectedConnection = ( Connection ) structuredSelection.getFirstElement();
        }
        else
        {
            selectedConnection = null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
        // Nothing to do
    }
}
