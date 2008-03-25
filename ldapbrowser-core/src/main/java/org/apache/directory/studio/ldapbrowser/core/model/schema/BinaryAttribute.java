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
 * Bean class to store the numeric OID or the name of a binary attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BinaryAttribute
{

    private String attributeNumericOidOrName;


    /**
     * Creates a new instance of BinaryAttribute.
     */
    public BinaryAttribute()
    {
    }


    /**
     * Creates a new instance of BinaryAttribute.
     * 
     * @param attributeNumericOidOrName the attribute numeric oid or name
     */
    public BinaryAttribute( String attributeNumericOidOrName )
    {
        this.attributeNumericOidOrName = attributeNumericOidOrName;
    }


    /**
     * Gets the attribute numeric OID or name.
     * 
     * @return the attribute numeric OID or name
     */
    public String getAttributeNumericOidOrName()
    {
        return attributeNumericOidOrName;
    }


    /**
     * Sets the attribute numeric OID or name.
     * 
     * @param attributeNumericOidOrName the new attribute numeric OID or name
     */
    public void setAttributeNumericOidOrName( String attributeNumericOidOrName )
    {
        this.attributeNumericOidOrName = attributeNumericOidOrName;
    }

}
