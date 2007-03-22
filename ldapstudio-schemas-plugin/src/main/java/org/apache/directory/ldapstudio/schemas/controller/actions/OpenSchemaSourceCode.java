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


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditorInput;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditorSourceCodePage;
import org.apache.directory.ldapstudio.schemas.view.views.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.SchemaWrapper;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for deleting an element (object class or attribute type).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenSchemaSourceCode extends Action
{
    /**
     * Creates a new instance of OpenSchemaSourceCode.
     */
    public OpenSchemaSourceCode()
    {
        super( "View source code" );
        setToolTipText( getText() );
        setId( PluginConstants.CMD_OPEN_SCHEMA_SOURCE_CODE );
        setImageDescriptor( AbstractUIPlugin
            .imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA ) );
        setEnabled( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        SchemasView view = ( SchemasView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( SchemasView.ID );

        Object selection = ( ( TreeSelection ) view.getViewer().getSelection() ).getFirstElement();

        if ( selection instanceof SchemaWrapper )
        {
            SchemaWrapper schemaWrapper = ( SchemaWrapper ) selection;
            try
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                FormEditor editor = ( FormEditor ) page.openEditor( new SchemaEditorInput( schemaWrapper
                    .getMySchema() ), SchemaEditor.ID );
                editor.setActivePage( SchemaEditorSourceCodePage.ID );
            }
            catch ( PartInitException e )
            {
                Logger.getLogger( OpenSchemaSourceCode.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    }
}
