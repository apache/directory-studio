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
package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.Collections;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class implements the Content Provider for the Superior Combo of the Attribute Type Editor.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ATESuperiorComboContentProvider implements IStructuredContentProvider
{
    /** The Schema Handler */
    private SchemaHandler schemaHandler;


    /**
     * Creates a new instance of ATESuperiorComboContentProvider.
     */
    public ATESuperiorComboContentProvider()
    {
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /**
     * {@inheritDoc}
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
                List<AttributeType> ats = schemaHandler.getAttributeTypes();
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
            Collections.sort( children, new ATESuperiorComboComparator() );

            return children.toArray();
        }

        // Default
        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
    }


    /**
     * {@inheritDoc}
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
            String sup = at1.getSuperiorOid();

            if ( sup == null )
            {
                return false;
            }
            else
            {
                AttributeType supAT = schemaHandler.getAttributeType( sup );
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
