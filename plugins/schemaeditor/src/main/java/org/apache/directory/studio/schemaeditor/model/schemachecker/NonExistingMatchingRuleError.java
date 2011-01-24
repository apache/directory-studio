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
 * This class represents the NonExistingMatchingRuleError.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NonExistingMatchingRuleError implements SchemaError
{
    /**
     * This enum represents the different types of NonExistingMatchingRuleError.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum NonExistingMatchingRuleErrorEnum
    {
        EQUALITY, ORDERING, SUBSTRING
    }

    /** The source object */
    private SchemaObject source;

    /** The matching rule's alias */
    private String mrAlias;

    /** The type */
    private NonExistingMatchingRuleErrorEnum type;


    /**
     * Creates a new instance of NonExistingMatchingRuleError.
     *
     * @param source
     *      the source object
     * @param mrAlias
     *      the matching rule alias
     */
    public NonExistingMatchingRuleError( SchemaObject source, String mrAlias, NonExistingMatchingRuleErrorEnum type )
    {
        this.source = source;
        this.mrAlias = mrAlias;
        this.type = type;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaError#getSource()
     */
    public SchemaObject getSource()
    {
        return source;
    }


    /**
     * Gets the matching rule alias.
     * 
     * @return
     *      the matching rule alias
     */
    public String getMatchingRuleAlias()
    {
        return mrAlias;
    }


    /**
     * Gets the type of NonExistingMatchingRuleError.
     *
     * @return
     *      the type of NonExistingMatchingRuleError
     */
    public NonExistingMatchingRuleErrorEnum getType()
    {
        return type;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "[NonExistingMatchingRuleError - Source: " + getSource() + " - mrAlias: " + getMatchingRuleAlias()
            + " - Type: " + getType() + "]";
    }
}