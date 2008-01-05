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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


/**
 * An AttributeValueEditorRelation is used to set the relation 
 * from an attribute type to its value editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeValueEditorRelation
{

    /** The attribute, either the numeric OID or type. */
    private String attributeNumericOidOrType;

    /** The value editor class name. */
    private String valueEditorClassName;


    /**
     * Creates a new instance of AttributeValueEditorRelation.
     */
    public AttributeValueEditorRelation()
    {
    }


    /**
     * Creates a new instance of AttributeValueEditorRelation.
     * 
     * @param attributeNumericOidOrName the attribute numeric OID or name
     * @param valueEditorClassName the value editor class name
     */
    public AttributeValueEditorRelation( String attributeNumericOidOrName, String valueEditorClassName )
    {
        this.attributeNumericOidOrType = attributeNumericOidOrName;
        this.valueEditorClassName = valueEditorClassName;
    }


    /**
     * Gets the attribute numeric OID or type.
     * 
     * @return the attribute numeric OID or type
     */
    public String getAttributeNumericOidOrType()
    {
        return attributeNumericOidOrType;
    }


    /**
     * Sets the attribute numeric OID or type.
     * 
     * @param attributeNumericOidOrType the new attribute numeric OID or type
     */
    public void setAttributeNumericOidOrType( String attributeNumericOidOrType )
    {
        this.attributeNumericOidOrType = attributeNumericOidOrType;
    }


    /**
     * Gets the value editor class name.
     * 
     * @return the value editor class name
     */
    public String getValueEditorClassName()
    {
        return valueEditorClassName;
    }


    /**
     * Sets the value editor class name.
     * 
     * @param valueEditorClassName the new value editor class name
     */
    public void setValueEditorClassName( String valueEditorClassName )
    {
        this.valueEditorClassName = valueEditorClassName;
    }

}
