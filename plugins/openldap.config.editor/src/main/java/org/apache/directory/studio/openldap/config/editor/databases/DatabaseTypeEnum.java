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
package org.apache.directory.studio.openldap.config.editor.databases;


/**
 * The various Database types. One of :
 * <ul>
 * <li>DBD</li>
 * <li>HDB</li>
 * <li>MDB</li>
 * <li>LDAP</li>
 * <li>LDIF</li>
 * <li>MONITOR</li>
 * <li>RELAY</li>
 * <li>NULL</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum DatabaseTypeEnum
{
    /** None */
    NONE( "None" ),

    /** Frontend DB */
    FRONTEND( "Frontend DB" ),

    /** Config DB */
    CONFIG( "Config DB" ),

    /** Berkeley DB */
    BDB( "BDB (Berkeley DB)" ),

    /** Hierarchical Berkeley DB */
    HDB( "HDB (Hierarchical Berkeley DB)" ),

    /** Memory-Mapped DB */
    MDB( "MDB (Memory-Mapped DB)" ),

    /** LDAP DB*/
    LDAP( "LDAP DB" ),

    /** LDIF DB*/
    LDIF( "LDIF DB" ),

    /** Null DB*/
    NULL( "Null DB" ),

    /** Relay DB*/
    RELAY( "Relay DB" );

    /** The internal name of the database */
    private String name;

    /** A private constructor with the name as a parameter */
    private DatabaseTypeEnum( String name )
    {
        this.name= name;
    }


    /**
     * @return the DatabaseType name
     */
    public String getName()
    {
        return name;
    }
}