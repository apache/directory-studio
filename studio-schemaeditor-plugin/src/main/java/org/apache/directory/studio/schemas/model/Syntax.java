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
 * This class is the model for 'Syntax' LDAP schema
 * elements. It is modeled after the RFC 2252 recommandation.
 *
 */
public class Syntax
{
    private String name;
    private String oid;
    private boolean humanReadable;


    /**
     * Default constructor
     * @param name the name of the syntax
     * @param oid the oid of the syntax
     * @param humanReadable a boolean specifying if the syntax is human readabe
     */
    public Syntax( String name, String oid, boolean humanReadable )
    {
        this.name = name;
        this.oid = oid;
        this.humanReadable = humanReadable;
    }


    public boolean isHumanReadable()
    {
        return humanReadable;
    }


    public void setHumanReadable( boolean humanReadable )
    {
        this.humanReadable = humanReadable;
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
}
