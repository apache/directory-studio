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
package org.apache.directory.studio.openldap.common.ui.model;


/**
 * The various Database types. One of :
 * <ul>
 * <li>None</li>
 * <li>Frontend DB</li>
 * <li>Config DB</li>
 * <li>BDB</li>
 * <li>DB Perl</li>
 * <li>DB_Socket</li>
 * <li>HDB</li>
 * <li>MDB</li>
 * <li>LDAP</li>
 * <li>LDIF</li>
 * <li>META</li>
 * <li>MONITOR</li>
 * <li>NDB</li>
 * <li>PASSWORD</li>
 * <li>RELAY</li>
 * <li>SHELL</li>
 * <li>SQL DB</li>
 * <li>NULL</li>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum DatabaseTypeEnum
{
    /** None */
    NONE("None"),

    /** Frontend DB */
    FRONTEND("Frontend DB"),

    /** Config DB */
    CONFIG("Config DB"),

    /** Berkeley DB */
    BDB("BDB (Berkeley DB)"),

    /** DB Perl */
    DB_PERL("DB Perl"),

    /** DB Socket */
    DB_SOCKET("DB Socket"),

    /** Hierarchical Berkeley DB */
    HDB("HDB (Hierarchical Berkeley DB)"),

    /** LDAP DB*/
    LDAP("LDAP DB"),

    /** LDIF DB*/
    LDIF("LDIF DB"),

    /** META DB*/
    META("META DB"),

    /** Memory-Mapped DB */
    MDB("MDB (Memory-Mapped DB)"),

    /** MONITOR DB*/
    MONITOR("MONITOR DB"),

    /** NDB DB*/
    NDB("NDB DB"),

    /** Null DB*/
    NULL("Null DB"),

    /** PASSWD DB */
    PASSWD("PASSWD DB"),

    /** Relay DB*/
    RELAY("Relay DB"),

    /** Shell DB*/
    SHELL("Shell DB"),

    /** SQL DB*/
    SQL("SQL DB");

    /** The internal name of the database */
    private String name;


    /** A private constructor with the name as a parameter */
    private DatabaseTypeEnum( String name )
    {
        this.name = name;
    }


    /**
     * @return the DatabaseType name
     */
    public String getName()
    {
        return name;
    }
    

    
    /**
     * @return An array with all the Enum value's name
     */
    public static String[] getNames()
    {
        String[] names = new String[values().length];
        int pos = 0;
    
        for ( DatabaseTypeEnum databaseType : values() )
        {
            names[pos] = databaseType.name;
            pos++;
        }
        
        return names;
    }
    
    

    
    /**
     * Retrieve the instance associated to a String. Return NONE if not found.
     * 
     * @param name The name to retrieve
     * @return The DatabaseTypeEnum instance found, or NONE.
     */
    public static DatabaseTypeEnum getDatabaseType( String name )
    {
        for ( DatabaseTypeEnum databaseType : values() )
        {
            if ( name.equalsIgnoreCase( databaseType.name() ) || name.equalsIgnoreCase( databaseType.getName() ) )
            {
                return databaseType;
            }
        }
        
        return NONE;
    }
}