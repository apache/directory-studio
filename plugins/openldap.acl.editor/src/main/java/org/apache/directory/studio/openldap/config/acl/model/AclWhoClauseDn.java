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


public class AclWhoClauseDn extends AbstractAclWhoClause
{
    /** The type */
    private AclWhoClauseDnTypeEnum type = AclWhoClauseDnTypeEnum.BASE;

    /** The modifier */
    private AclWhoClauseDnModifierEnum modifier;

    /** The pattern */
    private String pattern;


    /**
     * Gets the modifier.
     *
     * @return the modifier
     */
    public AclWhoClauseDnModifierEnum getModifier()
    {
        return modifier;
    }


    /**
     * Gets the pattern.
     * 
     * @return the pattern
     */
    public String getPattern()
    {
        return pattern;
    }


    /**
     * Gets the type.
     * 
     * @return the type
     */
    public AclWhoClauseDnTypeEnum getType()
    {
        return type;
    }


    /**
     * Sets the modifier.
     *
     * @param modifier the modifier
     */
    public void setModifier( AclWhoClauseDnModifierEnum modifier )
    {
        this.modifier = modifier;
    }


    /**
     * Sets the pattern
     * 
     * @param pattern the pattern to set
     */
    public void setPattern( String pattern )
    {
        this.pattern = pattern;
    }


    /**
     * Sets the type.
     * 
     * @param type the type to set
     */
    public void setType( AclWhoClauseDnTypeEnum type )
    {
        this.type = type;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // DN
        sb.append( "dn" );

        // Type
        if ( type != null )
        {
            sb.append( "." );
            sb.append( type );
        }

        // Modifier
        if ( modifier != null )
        {
            sb.append( "," );
            sb.append( modifier );
        }

        // Pattern
        sb.append( '=' );
        sb.append( '"' );
        sb.append( pattern );
        sb.append( '"' );

        return sb.toString();
    }
}
