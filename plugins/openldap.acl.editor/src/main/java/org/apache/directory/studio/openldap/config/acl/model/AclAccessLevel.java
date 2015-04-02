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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AclAccessLevel
{
    /** The 'self' modifier' flag */
    private boolean isSelf = false;

    /** The level */
    private AclAccessLevelLevelEnum level;

    /** The privileges modifier */
    private AclAccessLevelPrivModifierEnum privilegesModifier;

    /** The privileges */
    private List<AclAccessLevelPrivilegeEnum> privileges = new ArrayList<AclAccessLevelPrivilegeEnum>();


    /**
     * Adds a privilege.
     * 
     * @param p the privilege to add
     */
    public void addPrivilege( AclAccessLevelPrivilegeEnum p )
    {
        privileges.add( p );
    }


    /**
     * Adds a {@link Collection} of privileges.
     * 
     * @param arg0 the privileges to add
     */
    public void addPrivileges( Collection<? extends AclAccessLevelPrivilegeEnum> c )
    {
        privileges.addAll( c );
    }


    /**
     * Clears the list of privileges.
     */
    public void clearPrivileges()
    {
        privileges.clear();
    }


    /**
     * Gets the level.
     * 
     * @return the level
     */
    public AclAccessLevelLevelEnum getLevel()
    {
        return level;
    }


    /**
     * Gets the privileges modifier.
     * 
     * @return the privilegeModifier the privilege modifier
     */
    public AclAccessLevelPrivModifierEnum getPrivilegeModifier()
    {
        return privilegesModifier;
    }


    /**
     * Gets the privileges.
     * 
     * @return the privileges
     */
    public List<AclAccessLevelPrivilegeEnum> getPrivileges()
    {
        return privileges;
    }


    /**
     * Gets the 'self' modifier' flag.
     * 
     * @return the isSelf the 'self' modifier' flag
     */
    public boolean isSelf()
    {
        return isSelf;
    }


    /**
     * Sets the level.
     * 
     * @param level the level to set
     */
    public void setLevel( AclAccessLevelLevelEnum level )
    {
        this.level = level;
    }


    /**
     * Sets the privileges modifier.
     * 
     * @param privilegeModifier the privilegeModifier to set
     */
    public void setPrivilegeModifier( AclAccessLevelPrivModifierEnum privilegeModifier )
    {
        this.privilegesModifier = privilegeModifier;
    }


    /**
     * Sets the 'self' modifier' flag.
     * 
     * @param isSelf the 'self' modifier' flag to set
     */
    public void setSelf( boolean isSelf )
    {
        this.isSelf = isSelf;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Self
        if ( isSelf )
        {
            sb.append( "self " );
        }

        // Level
        if ( level != null )
        {
            sb.append( level.toString() );
        }

        // Privilege Modifier
        if ( privilegesModifier != null )
        {
            sb.append( privilegesModifier.toString() );
        }

        // Privileges
        if ( ( privileges != null ) && ( privileges.size() > 0 ) )
        {
            for ( AclAccessLevelPrivilegeEnum privlege : privileges )
            {
                sb.append( privlege );
            }
        }

        return sb.toString();
    }
}
