/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.model;


/**
 * This enum contains all possible values for the type of a DN What Clause.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum AclWhatClauseDnTypeEnum
{
    REGEX( "regex" ),
    BASE( "base" ),
    BASE_OBJECT( "baseobject" ),
    EXACT( "exact" ),
    ONE( "one" ),
    ONE_LEVEL( "onelevel" ),
    SUB( "sub" ),
    SUBTREE( "subtree" ),
    CHILDREN( "children" );

    /** The interned name */
    private String name;
    
    private AclWhatClauseDnTypeEnum( String name )
    {
        this.name = name;
    }
    
    
    /**
     * @return The interned name
     */
    public String getName()
    {
        return name;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return name;
    }
}
