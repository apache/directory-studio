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
package org.apache.directory.studio.apacheds.configuration.editor.v151.dialogs;


/**
 * This class implements an Attribute Value Object that is used in the PartitionDetailsPage.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeValueObject
{
    /** The attribute */
    private String attribute;

    /** The value */
    private String value;


    /**
     * Creates a new instance of AttributeValueObject.
     *
     * @param attribute
     *      the attribute
     * @param value
     *      the value
     */
    public AttributeValueObject( String attribute, String value )
    {
        this.attribute = attribute;
        this.value = value;
    }


    /**
     * Gets the attribute.
     *
     * @return
     *      the attribute.
     */
    public String getAttribute()
    {
        return attribute;
    }


    /**
     * Sets the attribute.
     *
     * @param attribute
     *      the new attribute
     */
    public void setId( String attribute )
    {
        this.attribute = attribute;
    }


    /**
     * Gets the value.
     *
     * @return
     *      the value
     */
    public String getValue()
    {
        return value;
    }


    /**
     * Sets the value.
     *
     * @param value
     *      the new value
     */
    public void setValue( String value )
    {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "Attribute=\"" + attribute + "\", Value=\"" + value + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
