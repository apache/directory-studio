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
package org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker;


import org.apache.directory.shared.ldap.schema.SchemaObject;


/**
 * This class represents the DuplicateOidError.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DuplicateOidError implements SchemaError
{
    /** The source object */
    private SchemaObject source;

    /** The duplicated OID */
    private String oid;

    /** The duplicate */
    private SchemaObject duplicate;


    /**
     * Creates a new instance of DuplicateAliasError.
     *
     * @param source
     *      the source object
     * @param oid
     *      the duplicated alias
     * @param duplicate
     *      the duplicate object
     */
    public DuplicateOidError( SchemaObject source, String oid, SchemaObject duplicate )
    {
        this.source = source;
        this.oid = oid;
        this.duplicate = duplicate;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaError#getSource()
     */
    public SchemaObject getSource()
    {
        return source;
    }


    /**
     * Gets the duplicated OID.
     * 
     * @return
     *      the duplicated OID
     */
    public String getOid()
    {
        return oid;
    }


    /**
     * Gets the duplicate object.
     *
     * @return
     *      the duplicate object
     */
    public SchemaObject getDuplicate()
    {
        return duplicate;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[DuplicateOidError - Source: " + getSource() + " - OID: " + getOid() + " - Duplicate: "
            + getDuplicate() + "]";
    }
}
