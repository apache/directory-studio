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
package org.apache.directory.studio.apacheds.actions;


import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.model.ServersHandler;
import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * This class implements the open action for a server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RenameAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated view */
    private ServersView view;


    /**
     * Creates a new instance of RenameAction.
     * 
     * @param view
     *      the associated view
     */
    public RenameAction( ServersView view )
    {
        this.view = view;
        setText( Messages.getString( "RenameAction.Rename" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "RenameAction.RenameToolTip" ) ); //$NON-NLS-1$
        setId( ApacheDsPluginConstants.CMD_RENAME );
        setActionDefinitionId( ApacheDsPluginConstants.CMD_RENAME );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( view != null )
        {
            // Getting the selected server
            StructuredSelection selection = ( StructuredSelection ) view.getViewer().getSelection();
            final Server server = ( Server ) selection.getFirstElement();

            if ( server != null )
            {
                IInputValidator validator = new IInputValidator()
                {
                    public String isValid( String newName )
                    {
                        if ( server.getName().equals( newName ) )
                        {
                            return null;
                        }
                        else if ( !ServersHandler.getDefault().isNameAvailable( newName ) )
                        {
                            return Messages.getString( "RenameAction.ErrorNameInUse" ); //$NON-NLS-1$
                        }
                        else
                        {
                            return null;
                        }
                    }
                };

                // Opening a dialog to ask the user a new name for the server
                InputDialog dialog = new InputDialog( view.getSite().getShell(),
                    Messages.getString( "RenameAction.RenameServer" ), //$NON-NLS-1$ 
                    Messages.getString( "RenameAction.NewName" ), //$NON-NLS-1$ 
                    server.getName(), validator );
                dialog.open();

                String newName = dialog.getValue();
                if ( newName != null )
                {
                    server.setName( newName );
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
