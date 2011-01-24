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
package org.apache.directory.studio.schemaeditor.model.schemachecker;


import org.apache.directory.shared.ldap.model.schema.SchemaObject;


/**
 * This class represents the DifferentUsageAsSuperiorError.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DifferentUsageAsSuperiorError implements SchemaError
{
    /** The source object */
    private SchemaObject source;

    /** The superior */
    private SchemaObject superior;


    /**
     * Creates a new instance of ClassTypeHierarchyError.
     *
     * @param source
     *      the source object
     * @param superior
     *      the superior object
     */
    public DifferentUsageAsSuperiorError( SchemaObject source, SchemaObject duplicate )
    {
        this.source = source;
        this.superior = duplicate;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaError#getSource()
     */
    public SchemaObject getSource()
    {
        return source;
    }


    /**
     * Gets the superior object.
     *
     * @return
     *      the superior object
     */
    public SchemaObject getSuperior()
    {
        return superior;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[DifferentUsageAsSuperiorError - Source: " + getSource() + " - Superior: " + getSuperior() + "]";
    }
}
