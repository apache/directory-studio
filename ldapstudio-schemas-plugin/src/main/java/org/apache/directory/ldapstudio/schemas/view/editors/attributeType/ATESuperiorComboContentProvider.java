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
package org.apache.directory.ldapstudio.schemas.view.editors.attributeType;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.editors.NonExistingAttributeType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Superior Combo of the Attribute Type Editor.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ATESuperiorComboContentProvider implements IStructuredContentProvider
{
    /** The Schema Pool */
    private SchemaPool schemaPool;


    /**
     * Creates a new instance of ATESuperiorComboContentProvider.
     */
    public ATESuperiorComboContentProvider()
    {
        schemaPool = SchemaPool.getInstance();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements( Object inputElement )
    {
        if ( inputElement instanceof ATESuperiorComboInput )
        {
            ATESuperiorComboInput input = ( ATESuperiorComboInput ) inputElement;

            if ( input.getChildren().isEmpty() )
            {
                AttributeType editorAT = input.getAttributeType();

                // Creating the '(None)' item
                input.addChild( new NonExistingAttributeType( NonExistingAttributeType.NONE ) );

                // Creating Children
                List<AttributeType> ats = schemaPool.getAttributeTypes();
                for ( AttributeType at : ats )
                {
                    if ( !isSubType( at, editorAT ) )
                    {
                        input.addChild( at );
                    }
                }
            }

            // Getting Children
            List<Object> children = input.getChildren();

            // Sorting Children
            Collections.sort( children, new Comparator<Object>()
            {
                public int compare( Object o1, Object o2 )
                {
                    if ( o1 instanceof AttributeType && o2 instanceof AttributeType )
                    {
                        return ( ( AttributeType ) o1 ).getNames()[0].compareToIgnoreCase( ( ( AttributeType ) o2 )
                            .getNames()[0] );
                    }
                    else if ( o1 instanceof AttributeType && o2 instanceof NonExistingAttributeType )
                    {
                        return ( ( AttributeType ) o1 ).getNames()[0]
                            .compareToIgnoreCase( ( ( NonExistingAttributeType ) o2 ).getName() );
                    }
                    else if ( o1 instanceof NonExistingAttributeType && o2 instanceof AttributeType )
                    {
                        return ( ( NonExistingAttributeType ) o1 ).getName().compareToIgnoreCase(
                            ( ( AttributeType ) o2 ).getNames()[0] );
                    }
                    else if ( o1 instanceof NonExistingAttributeType && o2 instanceof NonExistingAttributeType )
                    {
                        return ( ( NonExistingAttributeType ) o1 ).getName().compareToIgnoreCase(
                            ( ( NonExistingAttributeType ) o2 ).getName() );
                    }

                    return 0;
                }
            } );

            return children.toArray();
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


    /**
     * Checks id an Attribute Type is a sub type of another Attribute Type.
     *
     * @param at1
     *      the first Attribute Type
     * @param at2
     *      the second Attribute Type
     * @return
     *      true if at1 is a sub type of at2
     */
    private boolean isSubType( AttributeType at1, AttributeType at2 )
    {
        if ( at1.equals( at2 ) )
        {
            return true;
        }
        else
        {
            String sup = at1.getSuperior();

            if ( sup == null )
            {
                return false;
            }
            else
            {
                AttributeType supAT = schemaPool.getAttributeType( sup );
                if ( supAT == null )
                {
                    return false;
                }
                else
                {
                    return isSubType( supAT, at2 );
                }
            }
        }
    }
}
