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
import org.apache.directory.ldapstudio.schemas.controller.SchemasViewController;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemasViewRoot;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
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
public class SchemasView extends ViewPart implements ISaveablePart2
{
    /** The ID of the View */
    public static final String ID = Activator.PLUGIN_ID + ".view.SchemasView"; //$NON-NLS-1$

    /** The logger*/
    private static Logger logger = Logger.getLogger( SchemasView.class );

    /** The viewer */
    private TreeViewer viewer;

    /** The content provider of the viewer */
    private SchemasViewContentProvider contentProvider;


    /**
     * {@inheritDoc}
     */
    public void createPartControl( Composite parent )
    {
        initViewer( parent );

        // Registering the Viewer, so other views can be notified when the viewer selection changes
        getSite().setSelectionProvider( viewer );

        // Adding the controller
        new SchemasViewController( this );
    }


    /**
     * Initializes the Viewer
     */
    private void initViewer( Composite parent )
    {
        viewer = new TreeViewer( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        contentProvider = new SchemasViewContentProvider( viewer );
        viewer.setContentProvider( contentProvider );
        viewer.setLabelProvider( new DecoratingLabelProvider( new SchemasViewLabelProvider(), Activator.getDefault()
            .getWorkbench().getDecoratorManager().getLabelDecorator() ) );
        viewer.setInput( new SchemasViewRoot() );
    }


    /**
     * {@inheritDoc}
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
     * Refreshes the view (re-display only).
     * 
     * @see completeRefresh() for a complete refresh.
     */
    public void refresh()
    {
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
}
