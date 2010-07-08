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
package org.apache.directory.studio.schemaeditor.view.editors.objectclass;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is the Content Provider for the Attributes Table of the Object Class Editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ObjectClassEditorAttributesTableContentProvider implements IStructuredContentProvider
{
    /** The Schema Pool */
    private SchemaHandler schemaHandler;


    /**
     * Creates a new instance of ObjectClassEditorAttributesTableContentProvider.
     */
    public ObjectClassEditorAttributesTableContentProvider()
    {
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof String[] )
        {
            List<Object> results = new ArrayList<Object>();

            String[] attributes = ( String[] ) inputElement;
            for ( String attribute : attributes )
            {
                AttributeTypeImpl at = schemaHandler.getAttributeType( attribute );
                if ( at != null )
                {
                    results.add( at );
                }
                else
                {
                    results.add( new NonExistingAttributeType( attribute ) );
                }
            }

            // Sorting Elements
            Collections.sort( results, new Comparator<Object>()
            {
                public int compare( Object o1, Object o2 )
                {
                    if ( o1 instanceof AttributeTypeImpl && o2 instanceof AttributeTypeImpl )
                    {
                        String[] at1Names = ( ( AttributeTypeImpl ) o1 ).getNamesRef();
                        String[] at2Names = ( ( AttributeTypeImpl ) o2 ).getNamesRef();

                        if ( ( at1Names != null ) && ( at2Names != null ) && ( at1Names.length > 0 )
                            && ( at2Names.length > 0 ) )
                        {
                            return at1Names[0].compareToIgnoreCase( at2Names[0] );
                        }
                    }
                    else if ( o1 instanceof AttributeTypeImpl && o2 instanceof NonExistingAttributeType )
                    {
                        String[] at1Names = ( ( AttributeTypeImpl ) o1 ).getNamesRef();
                        String at2Name = ( ( NonExistingAttributeType ) o2 ).getName();

                        if ( ( at1Names != null ) && ( at2Name != null ) && ( at1Names.length > 0 ) )
                        {
                            return at1Names[0].compareToIgnoreCase( at2Name );
                        }
                    }
                    else if ( o1 instanceof NonExistingAttributeType && o2 instanceof AttributeTypeImpl )
                    {
                        String at1Name = ( ( NonExistingAttributeType ) o1 ).getName();
                        String[] at2Names = ( ( AttributeTypeImpl ) o2 ).getNamesRef();

                        if ( ( at1Name != null ) && ( at2Names != null ) && ( at2Names.length > 0 ) )
                        {
                            return at1Name.compareToIgnoreCase( at2Names[0] );
                        }
                    }
                    else if ( o1 instanceof NonExistingAttributeType && o2 instanceof NonExistingAttributeType )
                    {
                        String at1Name = ( ( NonExistingAttributeType ) o1 ).getName();
                        String at2Name = ( ( NonExistingAttributeType ) o2 ).getName();

                        if ( ( at1Name != null ) && ( at2Name != null ) )
                        {
                            return at1Name.compareToIgnoreCase( at2Name );
                        }
                    }

                    return 0;
                }
            } );

            return results.toArray();
        }

        // Default
        return null;
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
