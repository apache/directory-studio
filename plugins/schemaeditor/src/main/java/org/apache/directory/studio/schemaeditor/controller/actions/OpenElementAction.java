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
package org.apache.directory.studio.schemaeditor.controller.actions;


import java.util.Iterator;

import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditorInput;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditorInput;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
        super( Messages.getString( "OpenElementAction.OpenAction" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "OpenElementAction.OpenToolTip" ) ); //$NON-NLS-1$
        setId( PluginConstants.CMD_OPEN_ELEMENT );
        setActionDefinitionId( PluginConstants.CMD_OPEN_ELEMENT );
        setEnabled( false );
        this.viewer = viewer;
        this.viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();
                
                if ( selection.size() > 0 )
                {
                    boolean enabled = true;

                    for ( Iterator<?> iterator = selection.iterator(); iterator.hasNext(); )
                    {
                        Object selectedItem = iterator.next();

                        if ( !( selectedItem instanceof SchemaWrapper )
                            && !( selectedItem instanceof AttributeTypeWrapper )
                            && !( selectedItem instanceof ObjectClassWrapper ) )
                        {
                            enabled = false;
                            break;
                        }
                    }

                    setEnabled( enabled );
                }
                else
                {
                    setEnabled( false );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
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
                    PluginUtils.logError( Messages.getString( "OpenElementAction.ErrorOpeningEditor" ), e ); //$NON-NLS-1$
                    ViewUtils
                        .displayErrorMessageDialog(
                            Messages.getString( "OpenElementAction.Error" ), Messages.getString( "OpenElementAction.ErrorOpeningEditor" ) ); //$NON-NLS-1$ //$NON-NLS-2$
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
                    PluginUtils.logError( Messages.getString( "OpenElementAction.ErrorOpeningEditor" ), e ); //$NON-NLS-1$
                    ViewUtils
                        .displayErrorMessageDialog(
                            Messages.getString( "OpenElementAction.Error" ), Messages.getString( "OpenElementAction.ErrorOpeningEditor" ) ); //$NON-NLS-1$ //$NON-NLS-2$
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
                    PluginUtils.logError( Messages.getString( "OpenElementAction.ErrorOpeningEditor" ), e ); //$NON-NLS-1$
                    ViewUtils
                        .displayErrorMessageDialog(
                            Messages.getString( "OpenElementAction.Error" ), Messages.getString( "OpenElementAction.ErrorOpeningEditor" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            else if ( selectedItem instanceof Folder )
            {
                viewer.setExpandedState( selectedItem, true );
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
