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


import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.editors.attributetype.AttributeTypeEditor;
import org.apache.directory.studio.schemaeditor.view.editors.objectclass.ObjectClassEditor;
import org.apache.directory.studio.schemaeditor.view.views.HierarchyView;
import org.apache.directory.studio.schemaeditor.view.views.SchemaView;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


/**
 * This action opens the selected element in the Viewer in the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenTypeHierarchyAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated viewer */
    private TreeViewer viewer;


    /**
     * Creates a new instance of DeleteSchemaElementAction.
     */
    public OpenTypeHierarchyAction( TreeViewer viewer )
    {
        super( Messages.getString("OpenTypeHierarchyAction.OpenTypeAction") ); //$NON-NLS-1$
        setToolTipText( Messages.getString("OpenTypeHierarchyAction.OpenTypeToolTip") ); //$NON-NLS-1$
        setId( PluginConstants.CMD_OPEN_TYPE_HIERARCHY );
        setActionDefinitionId( PluginConstants.CMD_OPEN_TYPE_HIERARCHY );
        setEnabled( true );
        this.viewer = viewer;
        this.viewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                StructuredSelection selection = ( StructuredSelection ) event.getSelection();
                if ( selection.size() == 1 )
                {
                    Object obj = selection.getFirstElement();
                    if ( obj instanceof AttributeTypeWrapper )
                    {
                        setEnabled( true );
                    }
                    else if ( obj instanceof ObjectClassWrapper )
                    {
                        setEnabled( true );
                    }
                    else
                    {
                        setEnabled( false );
                    }
                }
                else
                {
                    setEnabled( false );
                }
            }
        } );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

        if ( part instanceof SchemaView )
        {
            openTypeHierarchyFromTreeViewer( ( ( SchemaView ) part ).getViewer() );
        }
        else if ( part instanceof HierarchyView )
        {
            openTypeHierarchyFromTreeViewer( ( ( HierarchyView ) part ).getViewer() );
        }
        else if ( part instanceof AttributeTypeEditor )
        {
            openTypeHierarchy( ( ( AttributeTypeEditor ) part ).getOriginalAttributeType() );
        }
        else if ( part instanceof ObjectClassEditor )
        {
            openTypeHierarchy( ( ( ObjectClassEditor ) part ).getOriginalObjectClass() );
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
            openTypeHierarchy( ocw.getObjectClass() );
        }
        else if ( firstElement instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper atw = ( AttributeTypeWrapper ) firstElement;
            openTypeHierarchy( atw.getAttributeType() );
        }
    }


    /**
     * Opens the Type Hierarchy with the given element.
     *
     * @param element
     *      the element to open
     */
    private void openTypeHierarchy( SchemaObject element )
    {
        HierarchyView view = ( HierarchyView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .findView( HierarchyView.ID );

        if ( view == null )
        {
            try
            {
                view = ( HierarchyView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
                    HierarchyView.ID );
            }
            catch ( PartInitException e )
            {
                PluginUtils.logError( Messages.getString("OpenTypeHierarchyAction.ErrorOpeningView"), e ); //$NON-NLS-1$
                ViewUtils.displayErrorMessageBox( Messages.getString("OpenTypeHierarchyAction.Error"), Messages.getString("OpenTypeHierarchyAction.ErrorOpeningView") ); //$NON-NLS-1$
            }
        }

        if ( view != null )
        {
            view.setInput( element );
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().bringToTop( view );
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
