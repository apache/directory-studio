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

package org.apache.directory.studio.schemas.view.views;


import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.controller.SchemaElementsController;
import org.apache.directory.studio.schemas.view.views.wrappers.ITreeNode;
import org.apache.directory.studio.schemas.view.views.wrappers.SchemaElementsViewRoot;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Schema Elements View where all the object classes and attribute types are displayed.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaElementsView extends ViewPart
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.SchemaElementsView"; //$NON-NLS-1$

    /** The tree viewer */
    private TreeViewer viewer;

    /** The content provider */
    private SchemaElementsViewContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        // Adding the controller
        new SchemaElementsController( this );
    }


    /**
     * Initializes the viewer.
     *
     * @param parent
     *      the parent element
     */
    private void initViewer( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new SchemaElementsViewContentProvider( viewer );
        viewer.setContentProvider( contentProvider );
        viewer.setLabelProvider( new DecoratingLabelProvider( new SchemaElementsViewLabelProvider(), Activator
            .getDefault().getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        viewer.setInput( new SchemaElementsViewRoot() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


    /**
     * Refresh the viewer
     */
    public void refresh()
    {
        viewer.refresh();
    }


    /**
     * Updates the viewer
     */
    public void update()
    {
        viewer.update( viewer.getInput(), null );
    }


    /**
     * Search for the given element in the Tree and returns it if it has been found.
     *
     * @param element
     *      the element to find
     * @return
     *      the element if it has been found, null if has not been found
     */
    public ITreeNode findElementInTree( ITreeNode element )
    {
        Object[] children = contentProvider.getChildren( ( ITreeNode ) getViewer().getInput() );

        for ( Object child : children )
        {
            ITreeNode item = ( ITreeNode ) child;
            if ( item.equals( element ) )
            {
                return item;
            }
        }

        return null;
    }


    /**
     * Gets the tree viewer.
     * 
     * @return
     *      the tree viewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }
}