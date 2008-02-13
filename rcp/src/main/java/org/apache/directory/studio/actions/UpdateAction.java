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

package org.apache.directory.studio.actions;


import org.apache.directory.studio.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;


/**
 * This class implements the Update Action.
 * It uses Eclipse Built-in system to search updates for all plugins
 * installed on Apache Directory Studio.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UpdateAction extends Action implements IAction
{
    private IWorkbenchWindow window;


    /**
     * Default constructor
     * @param window
     *          the window it is attached to
     */
    public UpdateAction( IWorkbenchWindow window )
    {
        this.window = window;
        setId( "org.apache.directory.studio.newUpdates" ); //$NON-NLS-1$
        setText( Messages.getString( "UpdateAction.Search_for_updates" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "UpdateAction.Search_for_updates_for_LDAP_Studio" ) ); //$NON-NLS-1$
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
                UpdateJob job = new UpdateJob( Messages.getString( "UpdateAction.Searching_for_updates" ), false, false ); //$NON-NLS-1$
                UpdateManagerUI.openInstaller( window.getShell(), job );
            }
        } );
    }

}
