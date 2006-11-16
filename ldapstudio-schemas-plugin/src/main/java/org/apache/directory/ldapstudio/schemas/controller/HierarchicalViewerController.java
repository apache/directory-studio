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

package org.apache.directory.ldapstudio.schemas.controller;


import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.AttributeTypeFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.ObjectClassFormEditorInput;
import org.apache.directory.ldapstudio.schemas.view.viewers.HierarchicalViewer;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.IntermediateNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;


public class HierarchicalViewerController implements IDoubleClickListener
{
    private static Logger logger = Logger.getLogger( HierarchicalViewerController.class );
    private static final HierarchicalViewerController instance_;

    // Static thread-safe singleton initializer
    static
    {
        try
        {
            instance_ = new HierarchicalViewerController();
        }
        catch ( Throwable e )
        {
            throw new RuntimeException( e.getMessage() );
        }
    }


    //Prevent direct access to the constructor
    private HierarchicalViewerController()
    {
    }


    /**
     * Use this method to get the singleton instance of the controller
     * @return
     */
    public static HierarchicalViewerController getInstance()
    {
        return instance_;
    }


    public void doubleClick( DoubleClickEvent event )
    {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        HierarchicalViewer view = ( HierarchicalViewer ) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().findView( HierarchicalViewer.ID );
        TreeViewer viewer = view.getViewer();

        // What we get from the treeViewer is a StructuredSelection
        StructuredSelection selection = ( StructuredSelection ) event.getSelection();

        // Here's the real object (an AttributeTypeWrapper, ObjectClassWrapper or IntermediateNode)
        Object objectSelection = selection.getFirstElement();
        IEditorInput input = null;
        String editorId = null;

        // Selecting the right editor and input
        if ( objectSelection instanceof AttributeTypeWrapper )
        {
            input = new AttributeTypeFormEditorInput( ( ( AttributeTypeWrapper ) objectSelection ).getMyAttributeType() );
            editorId = AttributeTypeFormEditor.ID;
        }
        else if ( objectSelection instanceof ObjectClassWrapper )
        {
            input = new ObjectClassFormEditorInput( ( ( ObjectClassWrapper ) objectSelection ).getMyObjectClass() );
            editorId = ObjectClassFormEditor.ID;
        }
        else if ( objectSelection instanceof IntermediateNode )
        {
            // Here we don't open an editor, we just expand the node.
            viewer.setExpandedState( objectSelection, !viewer.getExpandedState( objectSelection ) );
        }

        // Let's open the editor
        if ( input != null )
        {
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException e )
            {
                logger.debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    }

}
