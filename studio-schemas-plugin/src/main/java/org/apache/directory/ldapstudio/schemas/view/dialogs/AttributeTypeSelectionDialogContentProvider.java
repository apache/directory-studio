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
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Attribute Type Selection Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeSelectionDialogContentProvider implements IStructuredContentProvider
{
    /** The Schema Pool */
    private SchemaPool schemaPool;

    /** The hidden Object Classes */
    private List<AttributeType> hiddenAttributeTypes;


    /**
     * Creates a new instance of AttributeTypeSelectionDialogContentProvider.
     */
    public AttributeTypeSelectionDialogContentProvider( List<AttributeType> hiddenAttributeTypes )
    {
        schemaPool = SchemaPool.getInstance();
        this.hiddenAttributeTypes = hiddenAttributeTypes;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String )
        {
            ArrayList<AttributeTypeWrapper> results = new ArrayList<AttributeTypeWrapper>();

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

            List<AttributeType> atList = schemaPool.getAttributeTypes();

            // Sorting the list
            Collections.sort( atList, new Comparator<AttributeType>()
            {
                public int compare( AttributeType at1, AttributeType at2 )
                {
                    if ( ( at1.getNames() == null || at1.getNames().length == 0 )
                        && ( at2.getNames() == null || at2.getNames().length == 0 ) )
                    {
                        return 0;
                    }
                    else if ( ( at1.getNames() == null || at1.getNames().length == 0 )
                        && ( at2.getNames() != null && at2.getNames().length > 0 ) )
                    {
                        return "".compareToIgnoreCase( at2.getNames()[0] ); //$NON-NLS-1$
                    }
                    else if ( ( at1.getNames() != null && at1.getNames().length > 0 )
                        && ( at2.getNames() == null || at2.getNames().length == 0 ) )
                    {
                        return at1.getNames()[0].compareToIgnoreCase( "" ); //$NON-NLS-1$
                    }
                    else
                    {
                        return at1.getNames()[0].compareToIgnoreCase( at2.getNames()[0] );
                    }
                }
            } );

            // Searching for all matching elements
            for ( AttributeType at : atList )
            {
                for ( String name : at.getNames() )
                {
                    Matcher m = pattern.matcher( name );
                    if ( m.matches() )
                    {
                        if ( !hiddenAttributeTypes.contains( at ) )
                        {
                            AttributeTypeWrapper atw = new AttributeTypeWrapper( at, null );
                            if ( !results.contains( atw ) )
                            {
                                results.add( new AttributeTypeWrapper( at, null ) );
                            }
                        }
                        break;
                    }
                }
                Matcher m = pattern.matcher( at.getOid() );
                if ( m.matches() )
                {
                    if ( !hiddenAttributeTypes.contains( at ) )
                    {
                        AttributeTypeWrapper atw = new AttributeTypeWrapper( at, null );
                        if ( !results.contains( atw ) )
                        {
                            results.add( new AttributeTypeWrapper( at, null ) );
                        }
                    }
                }
            }

            // Returns the results
            return results.toArray();
        }

        // Default
        return new Object[0];
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