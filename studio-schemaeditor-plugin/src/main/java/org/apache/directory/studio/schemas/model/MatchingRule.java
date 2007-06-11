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

package org.apache.directory.studio.schemas.model;


/**
 * This class is the model for 'Matching Rule' LDAP schema
 * elements. It is modeled after the RFC 2252 recommandation.
 *
 */
public class MatchingRule
{
    private String name;
    private String oid;
    private String syntax;


    /**
     * Default constructor
     * @param name the name of the matching rule
     * @param oid the oid of the matching rule
     * @param syntax the syntax of the matching rule
     */
    public MatchingRule( String name, String oid, String syntax )
    {
        this.name = name;
        this.oid = oid;
        this.syntax = syntax;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String getOid()
    {
        return oid;
    }


    public void setOid( String oid )
    {
        this.oid = oid;
    }


    public String getSyntax()
    {
        return syntax;
    }


    public void setSyntax( String syntax )
    {
        this.syntax = syntax;
    }
}
