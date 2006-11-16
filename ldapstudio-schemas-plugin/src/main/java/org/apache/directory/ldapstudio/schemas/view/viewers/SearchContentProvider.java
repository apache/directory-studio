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
import java.util.Collection;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;


/**
 * Content provider for the search view
 *
 */
public class SearchContentProvider implements IStructuredContentProvider, PoolListener
{
    private SchemaPool schemaPool;
    private Hashtable<String, ObjectClass> objectClassTable;
    private Hashtable<String, AttributeType> attributeTypeTable;


    /**
     * Default constructor
     */
    public SearchContentProvider()
    {
        this.schemaPool = SchemaPool.getInstance();
        schemaPool.addListener( this );

        objectClassTable = schemaPool.getObjectClassesAsHashTableByName();

        attributeTypeTable = schemaPool.getAttributeTypesAsHashTableByName();
    }


    /**
     * returns the matched elements as an array of objects
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public Object[] getElements( Object parent )
    {
        if ( parent instanceof String )
        {
            String searchText = ( String ) parent;

            //reset the view title
            SearchViewer view = ( SearchViewer ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .findView( SearchViewer.ID );
            view.setPartName( Messages.getString( "SearchContentProvider.Search" ) ); //$NON-NLS-1$

            if ( searchText.length() > 0 )
            {
                String searchRegexp = searchText + ".*"; //$NON-NLS-1$
                Pattern pattern = Pattern.compile( searchRegexp, Pattern.CASE_INSENSITIVE );
                ArrayList resultsList = new ArrayList();

                Collection<ObjectClass> OCs = objectClassTable.values();
                Collection<AttributeType> ATs = attributeTypeTable.values();

                ArrayList<SchemaElement> allList = new ArrayList<SchemaElement>();
                allList.addAll( OCs );
                allList.addAll( ATs );

                //search for all matching elements
                for ( SchemaElement element : allList )
                {

                    if ( SearchViewer.searchType.equals( SearchViewer.SEARCH_NAME )
                        || SearchViewer.searchType.equals( SearchViewer.SEARCH_ALL ) )
                    {
                        String[] names = element.getNames();
                        for ( String name : names )
                        {
                            Matcher m = pattern.matcher( name );
                            if ( m.matches() )
                            {
                                if ( !resultsList.contains( element ) )
                                {
                                    resultsList.add( element );
                                }
                                break;
                            }
                        }
                    }

                    if ( SearchViewer.searchType.equals( SearchViewer.SEARCH_OID )
                        || SearchViewer.searchType.equals( SearchViewer.SEARCH_ALL ) )
                    {
                        String oid = element.getOid();
                        Matcher m = pattern.matcher( oid );
                        if ( m.matches() )
                        {
                            if ( !resultsList.contains( element ) )
                            {
                                resultsList.add( element );
                            }

                        }
                    }

                    if ( SearchViewer.searchType.equals( SearchViewer.SEARCH_DESC )
                        || SearchViewer.searchType.equals( SearchViewer.SEARCH_ALL ) )
                    {
                        String desc = element.getDescription();
                        if ( desc == null )
                            continue;
                        Matcher m = pattern.matcher( desc );
                        if ( m.matches() )
                        {
                            if ( !resultsList.contains( element ) )
                            {
                                resultsList.add( element );
                            }
                        }
                    }
                }

                //change the number of results in the view
                view
                    .setPartName( Messages.getString( "SearchContentProvider.Search_(" ) + resultsList.size() + Messages.getString( "SearchContentProvider.Results)" ) ); //$NON-NLS-1$ //$NON-NLS-2$

                //returns the result list
                return resultsList.toArray();
            }
        }

        return new Object[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {
        schemaPool.removeListener( this );
    }


    /**
     * Pool listener method
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        refresh();
    }


    /**
     * Refresh the content of the provider
     * (you can trigger it manually or it will be called when the pool has changed)
     */
    public void refresh()
    {
        objectClassTable = schemaPool.getObjectClassesAsHashTableByName();

        attributeTypeTable = schemaPool.getAttributeTypesAsHashTableByName();
    }

}
