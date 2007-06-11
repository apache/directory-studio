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

package org.apache.directory.studio.schemas.controller.actions;


import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.model.SchemaPool;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for saving all schemas
 */
public class SaveAllAction extends Action implements IWorkbenchWindowActionDelegate, IViewActionDelegate
{
    private static Logger logger = Logger.getLogger( SaveAllAction.class );


    /**
     * Default constructor
     * @param window
     * @param label
     */
    public SaveAllAction()
    {
        super( Messages.getString( "SaveAllAction.Save_all_schemas" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_SAVE_ALL );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SAVE_ALL ) );
        setEnabled( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        SchemaPool schemaPool = SchemaPool.getInstance();
        try
        {
            schemaPool.saveAll();
        }
        catch ( Exception e )
        {
            ErrorDialog
                .openError(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    Messages.getString( "SaveAllAction.Error" ), Messages.getString( "SaveAllAction.An_error_occured_when_saving_schemas" ), new Status( IStatus.ERROR, Activator.PLUGIN_ID, 0, //$NON-NLS-1$ //$NON-NLS-2$
                        "Status Error Message", null ) ); //$NON-NLS-1$
            logger.debug( "An error occured when saving schemas" ); //$NON-NLS-1$
        }
    }


    public void run( IAction action )
    {
        this.run();
    }


    public void dispose()
    {
    }


    public void init( IWorkbenchWindow window )
    {
    }


    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    public void init( IViewPart view )
    {
    }
}
