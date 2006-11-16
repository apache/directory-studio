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

package org.apache.directory.ldapstudio.schemas.controller.actions;


import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.controller.ICommandIds;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.editors.SchemaFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.SchemaFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.viewers.PoolManager;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for deleting an element (object class or attribute type)
 */
@SuppressWarnings("unused")//$NON-NLS-1$
public class OpenSchemaSourceCode extends Action
{
    private static Logger logger = Logger.getLogger( OpenSchemaSourceCode.class );
    private final IWorkbenchWindow window;


    /**
     * Default constructor
     * @param window
     * @param label
     */
    public OpenSchemaSourceCode( IWorkbenchWindow window, String label )
    {
        this.window = window;
        setText( label );

        // The id is used to refer to the action in a menu or toolbar
        setId( ICommandIds.CMD_OPEN_SCHEMA_SOURCE_CODE );
        // Associate the action with a pre-defined command, to allow key bindings.
        setActionDefinitionId( ICommandIds.CMD_OPEN_SCHEMA_SOURCE_CODE );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID, IImageKeys.SCHEMA ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        PoolManager view = ( PoolManager ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( PoolManager.ID );

        Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

        if ( selection instanceof SchemaWrapper )
        {
            SchemaWrapper schemaWrapper = ( SchemaWrapper ) selection;
            try
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                page.openEditor( new SchemaFormEditorInput( schemaWrapper.getMySchema() ), SchemaFormEditor.ID );
            }
            catch ( PartInitException e )
            {
                logger.debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    }
}
