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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.model.AttributeType;
import org.apache.directory.studio.schemas.model.LDAPModelEvent;
import org.apache.directory.studio.schemas.model.ObjectClass;
import org.apache.directory.studio.schemas.model.PoolListener;
import org.apache.directory.studio.schemas.model.SchemaPool;
import org.apache.directory.studio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemas.view.views.wrappers.ITreeNode;
import org.apache.directory.studio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Schemas View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchyViewContentProvider implements IStructuredContentProvider, ITreeContentProvider, PoolListener
{
    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The associated viewer */
    private TreeViewer viewer;

    List<AttributeType> attributeTypes;
    List<ObjectClass> objectClasses;


    /**
     * Default constructor
     */
    public HierarchyViewContentProvider( TreeViewer viewer )
    {
        this.viewer = viewer;
        schemaPool = SchemaPool.getInstance();
        schemaPool.addListener( this );
        attributeTypes = schemaPool.getAttributeTypes();
        objectClasses = schemaPool.getObjectClasses();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        int mode = Activator.getDefault().getDialogSettings().getInt( PluginConstants.PREFS_HIERARCHY_VIEW_MODE );
        List<ITreeNode> children = null;

        if ( parentElement instanceof ObjectClass )
        {
            ObjectClass oc = ( ObjectClass ) parentElement;

            ObjectClassWrapper ocw = new ObjectClassWrapper( oc, null );
            children = new ArrayList<ITreeNode>();
            children.add( ocw );
        }
        else if ( parentElement instanceof AttributeType )
        {
            AttributeType at = ( AttributeType ) parentElement;

            AttributeTypeWrapper atw = new AttributeTypeWrapper( at, null );
            children = new ArrayList<ITreeNode>();
            children.add( atw );
        }
        else if ( parentElement instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper ocw = ( ObjectClassWrapper ) parentElement;
            ObjectClass oc = ocw.getMyObjectClass();

            if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE )
            {
                String[] superiors = oc.getSuperiors();
                for ( String superior : superiors )
                {
                    if ( superior != null || "".equals( superior ) ) //$NON-NLS-1$
                    {
                        ObjectClass supOC = schemaPool.getObjectClass( superior );
                        if ( supOC != null )
                        {
                            ObjectClassWrapper supOCW = new ObjectClassWrapper( supOC, ocw );
                            ocw.addChild( supOCW );
                        }
                    }
                }
            }
            else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUBTYPE )
            {
                for ( ObjectClass objectClass : objectClasses )
                {
                    String[] superiors = objectClass.getSuperiors();
                    for ( String superior : superiors )
                    {
                        if ( superior != null || "".equals( superior ) ) //$NON-NLS-1$
                        {
                            ObjectClass supOC = schemaPool.getObjectClass( superior );
                            if ( supOC != null && oc.equals( supOC ) )
                            {
                                ObjectClassWrapper supOCW = new ObjectClassWrapper( objectClass, ocw );
                                ocw.addChild( supOCW );
                            }
                        }
                    }
                }
            }

            children = ocw.getChildren();
        }
        else if ( parentElement instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper atw = ( AttributeTypeWrapper ) parentElement;
            AttributeType at = atw.getMyAttributeType();

            if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE )
            {
                String superior = at.getSuperior();
                if ( superior != null || "".equals( superior ) ) //$NON-NLS-1$
                {
                    AttributeType supAT = schemaPool.getAttributeType( superior );
                    if ( supAT != null )
                    {
                        AttributeTypeWrapper supATW = new AttributeTypeWrapper( supAT, atw );
                        atw.addChild( supATW );
                    }
                }
            }
            else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUBTYPE )
            {
                for ( AttributeType attributeType : attributeTypes )
                {
                    String superior = attributeType.getSuperior();
                    if ( superior != null && !"".equals( superior ) ) //$NON-NLS-1$
                    {
                        AttributeType supAT = schemaPool.getAttributeType( superior );
                        if ( supAT != null && at.equals( supAT ) )
                        {
                            AttributeTypeWrapper supATW = new AttributeTypeWrapper( attributeType, atw );
                            atw.addChild( supATW );
                        }
                    }
                }
            }

            children = atw.getChildren();
        }

        if ( children == null )
        {
            return new Object[0];
        }
        else
        {
            return children.toArray();
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent( Object element )
    {
        if ( element instanceof ITreeNode )
        {
            return ( ( ITreeNode ) element ).getParent();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof ITreeNode )
        {
            return getChildren( element ).length > 0;
        }

        // Default
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemas.model.PoolListener#poolChanged(org.apache.directory.studio.schemas.model.SchemaPool, org.apache.directory.studio.schemas.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        viewer.refresh();
        viewer.expandAll();
    }
}
