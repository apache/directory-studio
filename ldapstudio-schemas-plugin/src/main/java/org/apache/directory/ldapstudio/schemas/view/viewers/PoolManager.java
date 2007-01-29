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
import org.apache.directory.ldapstudio.schemas.controller.PoolManagerController;
import org.apache.directory.ldapstudio.schemas.controller.actions.CollapseAllAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewAttributeTypeAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewObjectClassAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.CreateANewSchemaAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.DeleteAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.LinkWithEditorSchemasView;
import org.apache.directory.ldapstudio.schemas.controller.actions.OpenLocalFileAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.RemoveSchemaAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.SortPoolManagerAction;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public class PoolManager extends ViewPart implements PoolListener, ISaveablePart2
{
    public static final String ID = Application.PLUGIN_ID + ".view.PoolManager"; //$NON-NLS-1$

    private static Logger logger = Logger.getLogger( PoolManager.class );
    private TreeViewer viewer;
    private Composite parent;
    private PoolManagerContentProvider contentProvider;


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent )
    {
        this.parent = parent;
        initViewer();
        IToolBarManager toolbar = getViewSite().getActionBars().getToolBarManager();
        toolbar.add( new OpenLocalFileAction() );
        toolbar.add( new CreateANewSchemaAction() );
        toolbar.add( new RemoveSchemaAction() );
        toolbar.add( new Separator() );
        toolbar.add( new CreateANewObjectClassAction() );
        toolbar.add( new CreateANewAttributeTypeAction() );
        toolbar.add( new DeleteAction() );
        toolbar.add( new Separator() );
        toolbar.add( new SortPoolManagerAction( PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
            SortPoolManagerAction.SortType.alphabetical, Messages.getString( "PoolManager.Sort_alphabetically" ) ) ); //$NON-NLS-1$
        toolbar.add( new SortPoolManagerAction( PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
            SortPoolManagerAction.SortType.unalphabetical, Messages.getString( "PoolManager.Sort_unalphabetically" ) ) ); //$NON-NLS-1$
        toolbar.add( new Separator() );
        toolbar.add( new CollapseAllAction( getViewer() ) );
        toolbar.add( new LinkWithEditorSchemasView( this ) );

        // ContextMenu Creation
        createContextMenu();

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );
    }


    private MenuManager createContextMenu()
    {
        MenuManager menu = new MenuManager( "" ); //$NON-NLS-1$
        menu.setRemoveAllWhenShown( true );
        //contextual-menu handling via the singleton controller instance
        menu.addMenuListener( PoolManagerController.getInstance() );
        // set the context menu to the table viewer
        viewer.getControl().setMenu( menu.createContextMenu( viewer.getControl() ) );
        // register the context menu to enable extension actions
        getSite().registerContextMenu( menu, viewer );
        return menu;
    }


    private void initViewer()
    {
        SchemaPool pool = SchemaPool.getInstance();
        //we want to be notified if the pool has been modified
        pool.addListener( this );

        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new PoolManagerContentProvider();
        contentProvider.bindToTreeViewer( viewer );

        //double-click handling via the singleton controller instance
        viewer.addDoubleClickListener( PoolManagerController.getInstance() );

        //drag&drop handling via the singleton controller instance
        int operations = DND.DROP_COPY;
        DropTarget target = new DropTarget( viewer.getControl(), operations );
        //we only support file dropping on the viewer
        Transfer[] types = new Transfer[]
            { FileTransfer.getInstance() };
        target.setTransfer( types );
        target.addDropListener( PoolManagerController.getInstance() );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
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
     * We refresh the view only if the pool has been modified
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        //refresh the tree viewer
        viewer.refresh();
    }


    /**
     * @return the internal tree viewer
     */
    public TreeViewer getViewer()
    {
        return viewer;
    }


    public int promptToSaveOnClose()
    {
        // TODO Auto-generated method stub
        return ISaveablePart2.YES;
    }


    public void doSave( IProgressMonitor monitor )
    {
        // save schemas on disk
        try
        {
            SchemaPool.getInstance().saveAll( true );
        }
        catch ( Exception e )
        {
            logger.debug( "error when saving schemas on disk after asking for confirmation" ); //$NON-NLS-1$
        }
        //		
        //		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        //		IEditorPart[] editors = page.getDirtyEditors();
        //		for (IEditorPart part : editors) {
        //			if(part instanceof AttributeTypeFormEditor) {
        //				AttributeTypeFormEditor editor = (AttributeTypeFormEditor) part;
        //				editor.setDirty(false);
        //			}
        //			else if (part instanceof ObjectClassFormEditor) {
        //				ObjectClassFormEditor editor = (ObjectClassFormEditor) part;
        //				editor.setDirty(false);
        //			}
        //		}

    }


    public void doSaveAs()
    {
        // TODO Auto-generated method stub

    }


    public boolean isDirty()
    {
        // TODO Auto-generated method stub
        Schema[] schemas = SchemaPool.getInstance().getSchemas();
        for ( int i = 0; i < schemas.length; i++ )
        {
            Schema schema = schemas[i];
            if ( schema.type == Schema.SchemaType.userSchema )
            {
                if ( schema.hasBeenModified() || schema.hasPendingModification() )
                {
                    return true;
                }
            }
        }
        // Default value
        return false;
    }


    public boolean isSaveAsAllowed()
    {
        // TODO Auto-generated method stub
        return false;
    }


    public boolean isSaveOnCloseNeeded()
    {
        // TODO Auto-generated method stub
        return true;
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
