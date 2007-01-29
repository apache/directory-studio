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
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.ViewPart;


/**
 * This class implements the Schemas View
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PoolManager extends ViewPart implements PoolListener, ISaveablePart2
{
    public static final String ID = Application.PLUGIN_ID + ".view.PoolManager"; //$NON-NLS-1$

    private static Logger logger = Logger.getLogger( PoolManager.class );
    private TreeViewer viewer;
    private Composite parent;
    private PoolManagerContentProvider contentProvider;


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        this.parent = parent;
        initViewer();

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        SchemaPool pool = SchemaPool.getInstance();
        //we want to be notified if the pool has been modified
        pool.addListener( this );

        // Adding the controller
        new PoolManagerController( this );
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer()
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new PoolManagerContentProvider();
        contentProvider.bindToTreeViewer( viewer );
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        viewer.getControl().setFocus();
    }


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


    /**
     * We refresh the view only if the pool has been modified
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        //refresh the tree viewer
        viewer.refresh();
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
     * {@inheritDoc}
     */
    public int promptToSaveOnClose()
    {
        return ISaveablePart2.YES;
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public void doSaveAs()
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDirty()
    {
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


    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSaveOnCloseNeeded()
    {
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
