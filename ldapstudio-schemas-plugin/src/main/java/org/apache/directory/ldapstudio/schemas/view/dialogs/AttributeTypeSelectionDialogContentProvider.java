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

package org.apache.directory.ldapstudio.schemas.view.dialogs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class AttributeTypeSelectionDialogContentProvider implements IStructuredContentProvider
{

    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String )
        {
            String searchText = ( String ) inputElement;
            String searchRegexp;
            if ( searchText.length() == 0 )
            {
                searchRegexp = ".*"; //$NON-NLS-1$
            }
            else
            {
                searchRegexp = searchText + ".*"; //$NON-NLS-1$
            }

            Pattern pattern = Pattern.compile( searchRegexp, Pattern.CASE_INSENSITIVE );
            ArrayList resultsList = new ArrayList();

            SchemaPool schemaPool = SchemaPool.getInstance();

            List<AttributeType> atList = schemaPool.getAttributeTypes();

            // Sorting the list
            Collections.sort( atList, new Comparator<AttributeType>()
            {
                public int compare( AttributeType arg0, AttributeType arg1 )
                {
                    String oneName = arg0.getNames()[0];
                    String twoName = arg1.getNames()[0];
                    return oneName.compareTo( twoName );
                }
            } );

            //search for all matching elements
            for ( AttributeType element : atList )
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
            //returns the result list
            return resultsList.toArray();
        }
        return new Object[0];
    }


    public void dispose()
    {
    }


    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
    }

}
