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


import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.editors.attributeType.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.objectClass.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.viewers.HierarchyView;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemaElementsView;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;


/**
 * This action opens the Type Hierarchy View on the selected item.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenTypeHierarchyAction extends Action implements IWorkbenchWindowActionDelegate
{
    /**
     * Creates a new instance of OpenSchemasViewPreferencesAction.
     */
    public OpenTypeHierarchyAction()
    {
        super( "Open Type Hierarchy" );
        setToolTipText( getText() );
        setId( PluginConstants.CMD_OPEN_TYPE_HIERARCHY );
        setActionDefinitionId( PluginConstants.CMD_OPEN_TYPE_HIERARCHY );
        setEnabled( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

        if ( part instanceof SchemasView )
        {
            openTypeHierarchyFromTreeViewer( ( ( SchemasView ) part ).getViewer() );
        }
        else if ( part instanceof SchemaElementsView )
        {
            openTypeHierarchyFromTreeViewer( ( ( SchemaElementsView ) part ).getViewer() );
        }
        else if ( part instanceof HierarchyView )
        {
            openTypeHierarchyFromTreeViewer( ( ( HierarchyView ) part ).getViewer() );
        }
        else if ( part instanceof AttributeTypeFormEditor )
        {
            openTypeHierarchy( SchemaPool.getInstance().getAttributeType( part.getTitle() ) );
        }
        else if ( part instanceof ObjectClassFormEditor )
        {
            openTypeHierarchy( SchemaPool.getInstance().getObjectClass( part.getTitle() ) );
        }
    }


    /**
     * Gets the selection of the TreeViewer and opens the selection in the Hierarchy View.
     *
     * @param treeViewer
     *      the Tree Viewer
     */
    private void openTypeHierarchyFromTreeViewer( TreeViewer treeViewer )
    {
        Object firstElement = ( ( StructuredSelection ) treeViewer.getSelection() ).getFirstElement();

        if ( firstElement instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper ocw = ( ObjectClassWrapper ) firstElement;
            openTypeHierarchy( ocw.getMyObjectClass() );
        }
        else if ( firstElement instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper atw = ( AttributeTypeWrapper ) firstElement;
            openTypeHierarchy( atw.getMyAttributeType() );
        }
    }


    /**
     * Opens the Type Hierarchy with the given element.
     *
     * @param element
     *      the element to open
     */
    private void openTypeHierarchy( SchemaElement element )
    {
        HierarchyView view = ( HierarchyView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( HierarchyView.ID );

        view.setInput( element );
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( view );
    }


    public void dispose()
    {
        // TODO Auto-generated method stub

    }


    public void run( IAction action )
    {
        System.err.println( "run" );

    }


    public void selectionChanged( IAction action, ISelection selection )
    {
        // TODO Auto-generated method stub

    }


    public void init( IWorkbenchWindow window )
    {
        // TODO Auto-generated method stub

    }
}
