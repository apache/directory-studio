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
package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaViewRoot;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder.FolderType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The associated viewer */
    private TreeViewer viewer;


    /**
     * Default constructor
     *
     * @param viewer
     *      the associated TreeViewer
     */
    public SchemaViewContentProvider( TreeViewer viewer )
    {
        this.viewer = viewer;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        return getChildren( inputElement );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object parentElement )
    {
        List<TreeNode> children = null;

        if ( parentElement instanceof SchemaViewRoot )
        {
            SchemaViewRoot root = ( SchemaViewRoot ) parentElement;

            if ( root.getChildren().isEmpty() )
            {
                List<Schema> schemas = SchemaHandler.getInstance().getSchemas();
                for ( Schema schema : schemas )
                {
                    root.addChild( new SchemaWrapper( schema, root ) );
                }
            }

            children = root.getChildren();
        }
        else if ( parentElement instanceof SchemaWrapper )
        {
            SchemaWrapper schemaWrapper = ( SchemaWrapper ) parentElement;

            if ( schemaWrapper.getChildren().isEmpty() )
            {
                Folder atFolder = new Folder( FolderType.ATTRIBUTE_TYPE, schemaWrapper );
                schemaWrapper.addChild( atFolder );
                Folder ocFolder = new Folder( FolderType.OBJECT_CLASS, schemaWrapper );
                schemaWrapper.addChild( ocFolder );

                List<AttributeTypeImpl> attributeTypes = schemaWrapper.getSchema().getAttributeTypes();
                for ( AttributeTypeImpl attributeType : attributeTypes )
                {
                    atFolder.addChild( new AttributeTypeWrapper( attributeType, atFolder ) );
                }

                List<ObjectClassImpl> objectClasses = schemaWrapper.getSchema().getObjectClasses();
                for ( ObjectClassImpl objectClass : objectClasses )
                {
                    ocFolder.addChild( new ObjectClassWrapper( objectClass, ocFolder ) );
                }
            }

            children = schemaWrapper.getChildren();
        }
        else if ( parentElement instanceof Folder )
        {
            Folder folder = ( Folder ) parentElement;

            children = folder.getChildren();
        }

        return children.toArray();
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
            return ( ( TreeNode ) element ).hasChildren();
        }

        // Default
        return false;
    }
}
