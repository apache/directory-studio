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


import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.model.SchemaElement;
import org.apache.directory.studio.schemas.model.SchemaPool;
import org.apache.directory.studio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.studio.schemas.view.editors.objectClass.ObjectClassEditor;
import org.apache.directory.studio.schemas.view.views.HierarchyView;
import org.apache.directory.studio.schemas.view.views.SchemaElementsView;
import org.apache.directory.studio.schemas.view.views.SchemasView;
import org.apache.directory.studio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemas.view.views.wrappers.ObjectClassWrapper;
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
        super( Messages.getString( "OpenTypeHierarchyAction.Open_Type_Hierarchy" ) ); //$NON-NLS-1$
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
        else if ( part instanceof AttributeTypeEditor )
        {
            openTypeHierarchy( SchemaPool.getInstance().getAttributeType( part.getTitle() ) );
        }
        else if ( part instanceof ObjectClassEditor )
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


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
    }
}
