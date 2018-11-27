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

package org.apache.directory.studio.schemaeditor.view.dialogs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Attribute Type Selection Dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeSelectionDialogContentProvider implements IStructuredContentProvider
{
    /** The Schema Pool */
    private SchemaHandler schemaHandler;

    /** The hidden Object Classes */
    private List<AttributeType> hiddenAttributeTypes;


    /**
     * Creates a new instance of AttributeTypeSelectionDialogContentProvider.
     */
    public AttributeTypeSelectionDialogContentProvider( List<AttributeType> hiddenAttributeTypes )
    {
        this.hiddenAttributeTypes = hiddenAttributeTypes;
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String )
        {
            ArrayList<AttributeType> results = new ArrayList<AttributeType>();

            String searchText = ( String ) inputElement;

            String searchRegexp;

            searchText += "*"; //$NON-NLS-1$
            searchRegexp = searchText.replaceAll( "\\*", "[\\\\S]*" ); //$NON-NLS-1$ //$NON-NLS-2$ 
            searchRegexp = searchRegexp.replaceAll( "\\?", "[\\\\S]" ); //$NON-NLS-1$ //$NON-NLS-2$ 

            Pattern pattern = Pattern.compile( searchRegexp, Pattern.CASE_INSENSITIVE );

            List<AttributeType> atList = schemaHandler.getAttributeTypes();

            // Sorting the list
            Collections.sort( atList, new Comparator<AttributeType>()
            {
                public int compare( AttributeType at1, AttributeType at2 )
                {
                    List<String> at1Names = ( ( AttributeType ) at1 ).getNames();
                    List<String> at2Names = ( ( AttributeType ) at2 ).getNames();

                    if ( ( at1Names == null || at1Names.size() == 0 ) && ( at2Names == null || at2Names.size() == 0 ) )
                    {
                        return 0;
                    }
                    else if ( ( at1Names == null || at1Names.size() == 0 )
                        && ( at2Names != null && at2Names.size() > 0 ) )
                    {
                        return "".compareToIgnoreCase( at2Names.get( 0 ) ); //$NON-NLS-1$
                    }
                    else if ( ( at1Names != null && at1Names.size() > 0 )
                        && ( at2Names == null || at2Names.size() == 0 ) )
                    {
                        return at1Names.get( 0 ).compareToIgnoreCase( "" ); //$NON-NLS-1$
                    }
                    else
                    {
                        return at1Names.get( 0 ).compareToIgnoreCase( at2Names.get( 0 ) );
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
                            if ( !results.contains( at ) )
                            {
                                results.add( at );
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
                        if ( !results.contains( at ) )
                        {
                            results.add( at );
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


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        // Nothing to do
    }
}
