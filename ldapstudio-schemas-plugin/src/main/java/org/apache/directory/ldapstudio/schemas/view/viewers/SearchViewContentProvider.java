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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.SchemaElement;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;


/**
 * Content provider for the search view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchViewContentProvider implements IStructuredContentProvider/*, PoolListener*/
{
    /** The object classes Map */
    private Map<String, ObjectClass> objectClassTable;

    /** The attribute types Map*/
    private Map<String, AttributeType> attributeTypeTable;


    /**
     * Creates a new instance of SearchViewContentProvider.
     */
    public SearchViewContentProvider()
    {
        SchemaPool schemaPool = SchemaPool.getInstance();
        objectClassTable = schemaPool.getObjectClassesAsMap();
        attributeTypeTable = schemaPool.getAttributeTypesAsMap();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object parent )
    {
        if ( parent instanceof String )
        {
            String searchText = ( String ) parent;

            //reset the view title
            SearchView view = ( SearchView ) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .findView( SearchView.ID );
            view.setPartName( Messages.getString( "SearchContentProvider.Search" ) ); //$NON-NLS-1$

            if ( searchText.length() > 0 )
            {
                String searchRegexp = searchText + ".*"; //$NON-NLS-1$
                Pattern pattern = Pattern.compile( searchRegexp, Pattern.CASE_INSENSITIVE );
                List<SchemaElement> resultsList = new ArrayList<SchemaElement>();

                Collection<ObjectClass> OCs = objectClassTable.values();
                Collection<AttributeType> ATs = attributeTypeTable.values();

                List<SchemaElement> allList = new ArrayList<SchemaElement>();
                allList.addAll( OCs );
                allList.addAll( ATs );

                //search for all matching elements
                for ( SchemaElement element : allList )
                {

                    if ( SearchView.currentSearchScope.equals( SearchView.SEARCH_NAME )
                        || SearchView.currentSearchScope.equals( SearchView.SEARCH_ALL ) )
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

                    if ( SearchView.currentSearchScope.equals( SearchView.SEARCH_OID )
                        || SearchView.currentSearchScope.equals( SearchView.SEARCH_ALL ) )
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

                    if ( SearchView.currentSearchScope.equals( SearchView.SEARCH_DESC )
                        || SearchView.currentSearchScope.equals( SearchView.SEARCH_ALL ) )
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
    }
}
