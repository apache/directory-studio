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

package org.apache.directory.studio.schemaeditor.view.views;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Schemas View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HierarchyViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    List<AttributeTypeImpl> attributeTypes;
    List<ObjectClassImpl> objectClasses;


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
        List<TreeNode> children = null;

        schemaHandler = Activator.getDefault().getSchemaHandler();
        attributeTypes = schemaHandler.getAttributeTypes();
        objectClasses = schemaHandler.getObjectClasses();

        if ( parentElement instanceof ObjectClassImpl )
        {
            ObjectClassImpl oc = ( ObjectClassImpl ) parentElement;

            ObjectClassWrapper ocw = new ObjectClassWrapper( oc, null );
            children = new ArrayList<TreeNode>();
            children.add( ocw );
        }
        else if ( parentElement instanceof AttributeTypeImpl )
        {
            AttributeTypeImpl at = ( AttributeTypeImpl ) parentElement;

            AttributeTypeWrapper atw = new AttributeTypeWrapper( at, null );
            children = new ArrayList<TreeNode>();
            children.add( atw );
        }
        else if ( parentElement instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper ocw = ( ObjectClassWrapper ) parentElement;
            ObjectClassImpl oc = ocw.getObjectClass();

            if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE )
            {
                String[] superiors = oc.getSuperClassesNames();
                for ( String superior : superiors )
                {
                    if ( superior != null || "".equals( superior ) ) //$NON-NLS-1$
                    {
                        ObjectClassImpl supOC = schemaHandler.getObjectClass( superior );
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
                for ( ObjectClassImpl objectClass : objectClasses )
                {
                    String[] superiors = objectClass.getSuperClassesNames();
                    for ( String superior : superiors )
                    {
                        if ( superior != null || "".equals( superior ) ) //$NON-NLS-1$
                        {
                            ObjectClassImpl supOC = schemaHandler.getObjectClass( superior );
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
            AttributeTypeImpl at = atw.getAttributeType();

            if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE )
            {
                String superior = at.getSuperiorName();
                if ( superior != null || "".equals( superior ) ) //$NON-NLS-1$
                {
                    AttributeTypeImpl supAT = schemaHandler.getAttributeType( superior );
                    if ( supAT != null )
                    {
                        AttributeTypeWrapper supATW = new AttributeTypeWrapper( supAT, atw );
                        atw.addChild( supATW );
                    }
                }
            }
            else if ( mode == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUBTYPE )
            {
                for ( AttributeTypeImpl attributeType : attributeTypes )
                {
                    String superior = attributeType.getSuperiorName();
                    if ( superior != null && !"".equals( superior ) ) //$NON-NLS-1$
                    {
                        AttributeTypeImpl supAT = schemaHandler.getAttributeType( superior );
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
        if ( element instanceof TreeNode )
        {
            return ( ( TreeNode ) element ).getParent();
        }

        // Default
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof TreeNode )
        {
            return true;
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
}
