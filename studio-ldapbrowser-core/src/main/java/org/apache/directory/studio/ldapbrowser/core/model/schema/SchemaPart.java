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


import java.io.Serializable;

import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;


public abstract class SchemaPart implements Comparable, Serializable
{

    protected LdifAttrValLine line;

    protected Schema schema;

    protected String numericOID;

    protected String desc;


    protected SchemaPart()
    {
        this.schema = null;
        this.numericOID = null;
        this.desc = null;
    }


    /**
     * 
     * @return the schema
     */
    public Schema getSchema()
    {
        return schema;
    }


    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    /**
     * 
     * @return the numeric OID
     */
    public String getNumericOID()
    {
        return numericOID;
    }


    public void setNumericOID( String numericOID )
    {
        this.numericOID = numericOID;
    }


    /**
     * 
     * @return true if this syntax description is part of the default schema
     */
    public boolean isDefault()
    {
        return this.schema.isDefault();
    }


    /**
     * 
     * @return the desc, may be null
     */
    public String getDesc()
    {
        return desc;
    }


    public void setDesc( String desc )
    {
        this.desc = desc;
    }


    public LdifAttrValLine getLine()
    {
        return line;
    }


    public void setLine( LdifAttrValLine line )
    {
        this.line = line;
    }

    
    public boolean equals( Object obj )
    {
        if ( obj instanceof SchemaPart )
        {
            return this.getClass() == obj.getClass() && this.toString().equals( obj.toString() );
        }
        else
        {
            return false;
        }
    }
    
    
    public int hashCode()
    {
        return toString().hashCode();
    }
    
}
