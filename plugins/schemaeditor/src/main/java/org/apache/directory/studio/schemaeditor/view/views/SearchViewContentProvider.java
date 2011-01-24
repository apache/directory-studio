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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the ContentProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchViewContentProvider implements IStructuredContentProvider, ITreeContentProvider
{
    /** The preferences store */
    private IPreferenceStore store;

    /** The FirstName Sorter */
    private Comparator<SchemaObject> firstNameSorter;

    /** The OID Sorter */
    private Comparator<SchemaObject> oidSorter;


    /**
     * Creates a new instance of DifferencesWidgetSchemaContentProvider.
     */
    public SearchViewContentProvider()
    {
        store = Activator.getDefault().getPreferenceStore();

        firstNameSorter = new Comparator<SchemaObject>()
        {
            public int compare( SchemaObject o1, SchemaObject o2 )
            {
                List<String> o1Names = null;
                List<String> o2Names = null;

                if ( ( o1 instanceof AttributeTypeImpl ) && ( o2 instanceof AttributeTypeImpl ) )
                {
                    AttributeTypeImpl at1 = ( AttributeTypeImpl ) o1;
                    AttributeTypeImpl at2 = ( AttributeTypeImpl ) o2;

                    o1Names = at1.getNames();
                    o2Names = at2.getNames();
                }
                else if ( ( o1 instanceof ObjectClassImpl ) && ( o2 instanceof ObjectClassImpl ) )
                {
                    ObjectClassImpl oc1 = ( ObjectClassImpl ) o1;
                    ObjectClassImpl oc2 = ( ObjectClassImpl ) o2;

                    o1Names = oc1.getNames();
                    o2Names = oc2.getNames();
                }
                else if ( ( o1 instanceof AttributeTypeImpl ) && ( o2 instanceof ObjectClassImpl ) )
                {
                    AttributeTypeImpl at = ( AttributeTypeImpl ) o1;
                    ObjectClassImpl oc = ( ObjectClassImpl ) o2;

                    o1Names = at.getNames();
                    o2Names = oc.getNames();
                }
                else if ( ( o1 instanceof ObjectClassImpl ) && ( o2 instanceof AttributeTypeImpl ) )
                {
                    ObjectClassImpl oc = ( ObjectClassImpl ) o1;
                    AttributeTypeImpl at = ( AttributeTypeImpl ) o2;

                    o1Names = oc.getNames();
                    o2Names = at.getNames();
                }

                // Comparing the First Name
                if ( ( o1Names != null ) && ( o2Names != null ) )
                {
                    if ( ( o1Names.size() > 0 ) && ( o2Names.size() > 0 ) )
                    {
                        return o1Names.get(0).compareToIgnoreCase( o2Names.get(0) );
                    }
                    else if ( ( o1Names.size() == 0 ) && ( o2Names.size() > 0 ) )
                    {
                        return "".compareToIgnoreCase( o2Names.get(0) ); //$NON-NLS-1$
                    }
                    else if ( ( o1Names.size() > 0 ) && ( o2Names.size() == 0 ) )
                    {
                        return o1Names.get(0).compareToIgnoreCase( "" ); //$NON-NLS-1$
                    }
                }

                // Default
                return o1.toString().compareToIgnoreCase( o2.toString() );
            }
        };

        oidSorter = new Comparator<SchemaObject>()
        {
            public int compare( SchemaObject o1, SchemaObject o2 )
            {
                if ( ( o1 instanceof AttributeTypeImpl ) && ( o2 instanceof AttributeTypeImpl ) )
                {
                    AttributeTypeImpl at1 = ( AttributeTypeImpl ) o1;
                    AttributeTypeImpl at2 = ( AttributeTypeImpl ) o2;

                    return at1.getOid().compareToIgnoreCase( at2.getOid() );
                }
                else if ( ( o1 instanceof ObjectClassImpl ) && ( o2 instanceof ObjectClassImpl ) )
                {
                    ObjectClassImpl oc1 = ( ObjectClassImpl ) o1;
                    ObjectClassImpl oc2 = ( ObjectClassImpl ) o2;

                    return oc1.getOid().compareToIgnoreCase( oc2.getOid() );
                }
                else if ( ( o1 instanceof AttributeTypeImpl ) && ( o2 instanceof ObjectClassImpl ) )
                {
                    AttributeTypeImpl at = ( AttributeTypeImpl ) o1;
                    ObjectClassImpl oc = ( ObjectClassImpl ) o2;

                    return at.getOid().compareToIgnoreCase( oc.getOid() );
                }
                else if ( ( o1 instanceof ObjectClassImpl ) && ( o2 instanceof AttributeTypeImpl ) )
                {
                    ObjectClassImpl oc = ( ObjectClassImpl ) o1;
                    AttributeTypeImpl at = ( AttributeTypeImpl ) o2;

                    return oc.getOid().compareToIgnoreCase( at.getOid() );
                }

                // Default
                return o1.toString().compareToIgnoreCase( o2.toString() );
            }
        };
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
    @SuppressWarnings("unchecked")
    public Object[] getChildren( Object parentElement )
    {
        List<SchemaObject> children = new ArrayList<SchemaObject>();

        int group = store.getInt( PluginConstants.PREFS_SEARCH_VIEW_GROUPING );
        int sortBy = store.getInt( PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY );
        int sortOrder = store.getInt( PluginConstants.PREFS_SEARCH_VIEW_SORTING_ORDER );

        if ( parentElement instanceof List )
        {
            List<SchemaObject> searchResults = ( List<SchemaObject> ) parentElement;

            if ( group == PluginConstants.PREFS_SEARCH_VIEW_GROUPING_ATTRIBUTE_TYPES_FIRST )
            {
                List<AttributeTypeImpl> attributeTypes = new ArrayList<AttributeTypeImpl>();
                List<ObjectClassImpl> objectClasses = new ArrayList<ObjectClassImpl>();

                for ( SchemaObject searchResult : searchResults )
                {
                    if ( searchResult instanceof AttributeTypeImpl )
                    {
                        attributeTypes.add( ( AttributeTypeImpl ) searchResult );
                    }
                    else if ( searchResult instanceof ObjectClassImpl )
                    {
                        objectClasses.add( ( ObjectClassImpl ) searchResult );
                    }
                }

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( attributeTypes, firstNameSorter );
                    Collections.sort( objectClasses, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( attributeTypes, oidSorter );
                    Collections.sort( objectClasses, oidSorter );
                }

                // Sort Order
                if ( sortOrder == PluginConstants.PREFS_SEARCH_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( attributeTypes );
                    Collections.reverse( objectClasses );
                }

                children.addAll( attributeTypes );
                children.addAll( objectClasses );
            }
            else if ( group == PluginConstants.PREFS_SEARCH_VIEW_GROUPING_OBJECT_CLASSES_FIRST )
            {
                List<AttributeTypeImpl> attributeTypes = new ArrayList<AttributeTypeImpl>();
                List<ObjectClassImpl> objectClasses = new ArrayList<ObjectClassImpl>();

                for ( SchemaObject searchResult : searchResults )
                {
                    if ( searchResult instanceof AttributeTypeImpl )
                    {
                        attributeTypes.add( ( AttributeTypeImpl ) searchResult );
                    }
                    else if ( searchResult instanceof ObjectClassImpl )
                    {
                        objectClasses.add( ( ObjectClassImpl ) searchResult );
                    }
                }

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( attributeTypes, firstNameSorter );
                    Collections.sort( objectClasses, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( attributeTypes, oidSorter );
                    Collections.sort( objectClasses, oidSorter );
                }

                // Sort Order
                if ( sortOrder == PluginConstants.PREFS_SEARCH_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( attributeTypes );
                    Collections.reverse( objectClasses );
                }

                children.addAll( objectClasses );
                children.addAll( attributeTypes );
            }
            else if ( group == PluginConstants.PREFS_SEARCH_VIEW_GROUPING_MIXED )
            {
                children.addAll( searchResults );

                // Sort by
                if ( sortBy == PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY_FIRSTNAME )
                {
                    Collections.sort( children, firstNameSorter );
                }
                else if ( sortBy == PluginConstants.PREFS_SEARCH_VIEW_SORTING_BY_OID )
                {
                    Collections.sort( children, oidSorter );
                }

                // Sort Order
                if ( sortOrder == PluginConstants.PREFS_SEARCH_VIEW_SORTING_ORDER_DESCENDING )
                {
                    Collections.reverse( children );
                }
            }
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
