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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.controller.actions.HideAttributeTypesAction;
import org.apache.directory.ldapstudio.schemas.controller.actions.HideObjectClassesAction;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.FirstNameSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ITreeNode;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.OidSorter;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaElementsViewRoot;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the content provider for the Schema Elements View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaElementsContentProvider implements IStructuredContentProvider, ITreeContentProvider, PoolListener
{
    /** The Schema Pool holding all schemas */
    private SchemaPool schemaPool;

    /** The preferences store */
    IPreferenceStore store;

    /** The FirstName Sorter */
    private FirstNameSorter firstNameSorter;

    /** The OID Sorter */
    private OidSorter oidSorter;


    /**
     * Creates a new instance of SchemaElementsContentProvider.
     *
     * @param schemaPool
     *      the associated Schema Pool
     */
    public SchemaElementsContentProvider()
    {
        schemaPool = SchemaPool.getInstance();
        schemaPool.addListener( this );
        store = Activator.getDefault().getPreferenceStore();

        firstNameSorter = new FirstNameSorter();
        oidSorter = new OidSorter();
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
        if ( parentElement instanceof SchemaElementsViewRoot )
        {
            SchemaElementsViewRoot root = ( SchemaElementsViewRoot ) parentElement;

            List<ITreeNode> children = new ArrayList<ITreeNode>();

            int group = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING );
            int sortBy = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY );
            int sortOrder = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER );
            boolean hideAttributeTypes = Activator.getDefault().getDialogSettings().getBoolean(
                HideAttributeTypesAction.HIDE_ATTRIBUTE_TYPES_DS_KEY );
            boolean hideObjectClasses = Activator.getDefault().getDialogSettings().getBoolean(
                HideObjectClassesAction.HIDE_OBJECT_CLASSES_DS_KEY );

            if ( root.getChildren().isEmpty() )
            {
                // ATTRIBUTE TYPES
                List<AttributeType> attributeTypes = schemaPool.getAttributeTypes();
                for ( AttributeType attributeType : attributeTypes )
                {
                    root.addChild( new AttributeTypeWrapper( attributeType, root ) );
                }

                // OBJECT CLASSES
                List<ObjectClass> objectClasses = schemaPool.getObjectClasses();
                for ( ObjectClass objectClass : objectClasses )
                {
                    root.addChild( new ObjectClassWrapper( objectClass, root ) );
                }
            }

            List<AttributeTypeWrapper> atList = new ArrayList<AttributeTypeWrapper>();
            List<ObjectClassWrapper> ocList = new ArrayList<ObjectClassWrapper>();

            if ( !hideAttributeTypes )
            {
                atList = root.getAttributeTypes();
            }
            if ( !hideObjectClasses )
            {
                ocList = root.getObjectClasses();
            }

            if ( group == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_ATFIRST )
            {
                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( atList, firstNameSorter );
                    Collections.sort( ocList, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( atList, oidSorter );
                    Collections.sort( ocList, oidSorter );
                }

                // Sort Order
                if ( sortOrder == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( atList );
                    Collections.reverse( ocList );
                }

                // Group
                children.addAll( atList );
                children.addAll( ocList );
            }
            else if ( group == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_OCFIRST )
            {
                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( atList, firstNameSorter );
                    Collections.sort( ocList, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( atList, oidSorter );
                    Collections.sort( ocList, oidSorter );
                }

                // Sort Order
                if ( sortOrder == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( atList );
                    Collections.reverse( ocList );
                }

                // Group
                children.addAll( ocList );
                children.addAll( atList );
            }
            else if ( group == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_GROUPING_MIXED )
            {
                // Group
                children.addAll( atList );
                children.addAll( ocList );

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( children, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( children, oidSorter );
                }

                // Sort order
                if ( sortOrder == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( children );
                }
            }
            return children.toArray();
        }

        // Default
        return new Object[0];
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

        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren( Object element )
    {
        if ( element instanceof SchemaElementsViewRoot )
        {
            return true;
        }

        return false;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.model.PoolListener#poolChanged(org.apache.directory.ldapstudio.schemas.model.SchemaPool, org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        // TODO Auto-generated method stub

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
