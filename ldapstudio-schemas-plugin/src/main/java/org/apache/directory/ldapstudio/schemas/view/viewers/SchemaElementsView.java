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

package org.apache.directory.ldapstudio.schemas.view.viewers;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.controller.SchemaElementsController;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemasViewRoot;
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
public class SchemaElementsView extends ViewPart implements PoolListener
{
    /** The view's ID */
    public static final String ID = Activator.PLUGIN_ID + ".view.SchemaElementsView"; //$NON-NLS-1$

    /** The tree viewer */
    private TreeViewer viewer;

    /** The content provider */
    private SchemaElementsContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        SchemaPool pool = SchemaPool.getInstance();
        //we want to be notified if the pool has been modified
        pool.addListener( this );

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
        contentProvider = new SchemaElementsContentProvider();
        contentProvider.bindToTreeViewer( viewer );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }

    
    /**
     * Refreshes completely the view (reload model and re-display).
     * 
     * @see refresh() for refreshing only the display.
     */
    public void completeRefresh()
    {
        Object[] exp = viewer.getExpandedElements();

        // Refresh the tree viewer
        viewer.setInput( new SchemasViewRoot() );

        // Expand all the previsouly expanded elements
        for ( Object object : exp )
        {
            viewer.setExpandedState( object, true );
        }
    }
    

    /**
     * Refresh the viewer
     */
    public void refresh()
    {
        Object[] exp = viewer.getExpandedElements();

        // Refresh the tree viewer
        viewer.refresh();

        // Expand all the previsouly expanded elements
        for ( Object object : exp )
        {
            viewer.setExpandedState( object, true );
        }
    }


    /**
     * refresh the view if the pool has been modified
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        refresh();
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
        ITreeNode input = ( ITreeNode ) getViewer().getInput();

        return findElementInTree( element, input );
    }


    /**
     * Search for the given element in the Tree and returns it if it has been found.
     *
     * @param element
     *      the element to find
     * @param current
     *      the current element
     * @return
     */
    private ITreeNode findElementInTree( ITreeNode element, ITreeNode current )
    {
        if ( element.equals( current ) )
        {
            return current;
        }
        else
        {
            Object[] children = contentProvider.getChildren( current );

            for ( int i = 0; i < children.length; i++ )
            {
                ITreeNode item = ( ITreeNode ) children[i];
                ITreeNode foundElement = findElementInTree( element, item );
                if ( foundElement != null )
                {
                    return foundElement;
                }
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