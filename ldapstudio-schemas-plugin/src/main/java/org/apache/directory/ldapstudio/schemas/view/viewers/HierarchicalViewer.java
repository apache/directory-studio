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


import java.util.Comparator;

import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.controller.HierarchicalViewerController;
import org.apache.directory.ldapstudio.schemas.controller.actions.CollapseAllAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.LinkWithEditorHierarchyView;
import org.apache.directory.ldapstudio.schemas.controller.actions.SortHierarchicalViewAction;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class HierarchicalViewer extends ViewPart implements PoolListener
{
    public static final String ID = Application.PLUGIN_ID + ".view.HierarchicalViewer"; //$NON-NLS-1$
    private TreeViewer viewer;
    private Composite parent;
    private HierarchicalContentProvider contentProvider;


    /**
     * @return the internal tree viewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    /******************************************
     *        Interfaces Implementation       *
     ******************************************/

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent )
    {
        this.parent = parent;
        initViewer();
        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add( new SortHierarchicalViewAction( PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
            SortHierarchicalViewAction.SortType.alphabetical, Messages
                .getString( "HierarchicalViewer.Sort_alphabetically" ) ) ); //$NON-NLS-1$
        toolbar.add( new SortHierarchicalViewAction( PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
            SortHierarchicalViewAction.SortType.unalphabetical, Messages
                .getString( "HierarchicalViewer.Sort_unalphabetically" ) ) ); //$NON-NLS-1$
        toolbar.add( new Separator() );
        toolbar.add( new CollapseAllAction( getViewer() ) );
        toolbar.add( new LinkWithEditorHierarchyView( this ) );
    }


    private void initViewer()
    {
        SchemaPool pool = SchemaPool.getInstance();
        //we want to be notified if the pool has been modified
        pool.addListener( this );

        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new HierarchicalContentProvider( pool );
        contentProvider.bindToTreeViewer( viewer );
        viewer.addDoubleClickListener( HierarchicalViewerController.getInstance() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


    /******************************************
     *                 Logic                  *
     ******************************************/

    /**
     * Refresh the entire view
     */
    public void refresh()
    {
        //it seems there is a bug with the default element expanding system
        Object[] exp = viewer.getExpandedElements();

        //refresh the tree viewer
        viewer.refresh();

        //expand all the previsouly expanded elements
        for ( Object object : exp )
        {
            viewer.setExpandedState( object, true );
        }
    }


    /**
     * Specify the comparator that will be used to sort the elements in that viewer
     * @param order the comparator
     */
    public void setOrder( Comparator order )
    {
        contentProvider.setOrder( order );
        refresh();
    }


    /******************************************
     *            Pool Listener Impl          *
     ******************************************/

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
    public DisplayableTreeElement findElementInTree( DisplayableTreeElement element )
    {
        DisplayableTreeElement input = ( DisplayableTreeElement ) getViewer().getInput();

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
    private DisplayableTreeElement findElementInTree( DisplayableTreeElement element, DisplayableTreeElement current )
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
                DisplayableTreeElement item = ( DisplayableTreeElement ) children[i];
                DisplayableTreeElement foundElement = findElementInTree( element, item );
                if ( foundElement != null )
                {
                    return foundElement;
                }
            }
        }
        return null;
    }
}