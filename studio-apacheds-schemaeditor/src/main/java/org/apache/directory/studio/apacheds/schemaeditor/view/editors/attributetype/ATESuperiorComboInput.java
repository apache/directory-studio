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
package org.apache.directory.studio.apacheds.schemaeditor.view.editors.attributetype;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;


/**
 * This class implements the Input of the Superior Combo of the Attribute Type Editor
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ATESuperiorComboInput
{
    /** The attribute type */
    private AttributeTypeImpl at;

    /** The children */
    private List<Object> children;


    /**
     * Creates a new instance of ATESuperiorComboInput.
     *
     * @param at
     *      the Attribute Type
     */
    public ATESuperiorComboInput( AttributeTypeImpl at )
    {
        this.at = at;
    }


    /**
     * Gets the Attribute Type.
     *
     * @return
     *      the attribute type
     */
    public AttributeTypeImpl getAttributeType()
    {
        return at;
    }


    /**
     * Adds a child.
     *
     * @param child
     *      the child to add
     */
    public void addChild( Object child )
    {
        if ( children == null )
        {
            children = new ArrayList<Object>();
        }

        children.add( child );
    }


    /**
     * Gets the children.
     *
     * @return
     *      the children
     */
    public List<Object> getChildren()
    {
        if ( children == null )
        {
            children = new ArrayList<Object>();
        }

        return children;
    }
}
