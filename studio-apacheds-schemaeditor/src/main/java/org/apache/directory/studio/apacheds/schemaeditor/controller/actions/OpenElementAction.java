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
package org.apache.directory.studio.apacheds.schemaeditor.controller.actions;


import java.util.Iterator;

import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.PluginUtils;
import org.apache.directory.studio.apacheds.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.apacheds.schemaeditor.view.editors.schema.SchemaEditorInput;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This action opens the selected element in the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenElementAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated viewer */
    private TreeViewer viewer;


    /**
     * Creates a new instance of DeleteSchemaElementAction.
     */
    public OpenElementAction( TreeViewer viewer )
    {
        super( "&Open" );
        setToolTipText( "Open" );
        setId( PluginConstants.CMD_OPEN_ELEMENT );
        setEnabled( true );
        this.viewer = viewer;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        StructuredSelection selection = ( StructuredSelection ) viewer.getSelection();
        for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
        {
            Object selectedItem = iterator.next();
            if ( selectedItem instanceof AttributeTypeWrapper )
            {
                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new AttributeTypeEditorInput( ( ( AttributeTypeWrapper ) selectedItem ).getAttributeType() ),
                        AttributeTypeEditor.ID );
                }
                catch ( PartInitException e )
                {
                    PluginUtils.logError( "An error occured when opening the editor.", e );
                    ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
                }
            }
            else if ( selectedItem instanceof ObjectClassWrapper )
            {
                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new ObjectClassEditorInput( ( ( ObjectClassWrapper ) selectedItem ).getObjectClass() ),
                        ObjectClassEditor.ID );
                }
                catch ( PartInitException e )
                {
                    PluginUtils.logError( "An error occured when opening the editor.", e );
                    ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
                }
            }
            else if ( selectedItem instanceof SchemaWrapper )
            {
                try
                {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(
                        new SchemaEditorInput( ( ( SchemaWrapper ) selectedItem ).getSchema() ), SchemaEditor.ID );
                }
                catch ( PartInitException e )
                {
                    PluginUtils.logError( "An error occured when opening the editor.", e );
                    ViewUtils.displayErrorMessageBox( "Error", "An error occured when opening the editor." );
                }
            }
            else if ( selectedItem instanceof Folder )
            {
                viewer.setExpandedState( selectedItem, true );
            }
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
