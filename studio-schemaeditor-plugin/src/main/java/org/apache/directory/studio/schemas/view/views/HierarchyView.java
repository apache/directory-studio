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
import org.apache.directory.studio.schemas.controller.HierarchyViewController;
import org.apache.directory.studio.schemas.view.views.wrappers.ITreeNode;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchyView extends ViewPart
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.HierarchyView"; //$NON-NLS-1$

    /** The tree viewer */
    private TreeViewer viewer;

    /** The content provider */
    private HierarchyViewContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        new HierarchyViewController( this );
    }


    /**
     * Initializes the Viewer
     *
     * @param parent
     *      the parent Composite
     */
    private void initViewer( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new HierarchyViewContentProvider( viewer );
        viewer.setContentProvider( contentProvider );
        viewer.setLabelProvider( new DecoratingLabelProvider( new HierarchyViewLabelProvider(), Activator.getDefault()
            .getWorkbench().getDecoratorManager().getLabelDecorator() ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


    /**
     * Gets the TreeViewer
     *
     * @return
     *      the TreeViewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    /**
     * Refreshes the viewer.
     */
    public void refresh()
    {
        viewer.refresh();
        viewer.expandAll();
    }


    public void setInput( Object input )
    {
        viewer.setInput( input );
        viewer.expandAll();
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
        if ( element == null )
        {
            return null;
        }

        ITreeNode input = ( ITreeNode ) getViewer().getInput();

        return findElementInTree( element, input );
    }


    /**
     * Search for the given element in the Tree and returns it if it has been found.
     *
     * @param element
     *      the element to find
     * @param node
     *      the current element
     * @return
     */
    public ITreeNode findElementInTree( ITreeNode element, ITreeNode node )
    {
        if ( element.equals( node ) )
        {
            return node;
        }
        else
        {
            Object[] children = contentProvider.getChildren( node );
            for ( Object child : children )
            {
                ITreeNode foundElement = findElementInTree( element, ( ITreeNode ) child );
                if ( foundElement != null )
                {
                    return foundElement;
                }
            }
        }
        return null;
    }
}
