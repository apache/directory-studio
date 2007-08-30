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
 * This class represents the NonExistingOCSuperiorError.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NonExistingOCSuperiorError implements SchemaError
{
    /** The source object */
    private SchemaObject source;

    /** The superior's alias */
    private String supAlias;


    /**
     * Creates a new instance of NonExistingATSuperiorError.
     *
     * @param source
     *      the source object
     * @param supAlias
     *      the superior's alias
     */
    public NonExistingOCSuperiorError( SchemaObject source, String supAlias )
    {
        this.source = source;
        this.supAlias = supAlias;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaError#getSource()
     */
    public SchemaObject getSource()
    {
        return source;
    }


    /**
     * Gets the superior's alias.
     * 
     * @return
     *      the superior's alias
     */
    public String getSuperiorAlias()
    {
        return supAlias;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[NonExistingOCSuperiorError - Source: " + getSource() + " - supAlias: " + getSuperiorAlias() + "]";
    }
}
