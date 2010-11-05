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

package org.apache.directory.studio.apacheds.configuration.v2.actions;


import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationInput;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the create connection action for an ApacheDS 1.5.7 server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenConfigurationAction implements IObjectActionDelegate
{
    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        System.out.println( "Action!" );
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try
        {
            page.openEditor( new ServerConfigurationInput(),
                ServerConfigurationEditor.ID );
        }
        catch ( PartInitException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
    public void setActivePart( IAction action, IWorkbenchPart targetPart )
    {
        // TODO Auto-generated method stub

    }
}
