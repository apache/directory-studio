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


public class AclWhoClauseGroup extends AbstractAclWhoClause
{
    /** The object class */
    private String objectclass;

    /** The attribute */
    private String attribute;

    /** The type */
    private AclWhoClauseGroupTypeEnum type;

    /** The pattern */
    private String pattern;


    /**
     * Gets the attribute.
     *
     * @return the attribute
     */
    public String getAttribute()
    {
        return attribute;
    }


    /**
     * Gets the objectclass.
     *
     * @return the objectclass
     */
    public String getObjectclass()
    {
        return objectclass;
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
    public AclWhoClauseGroupTypeEnum getType()
    {
        return type;
    }


    /**
     * Sets the attribute.
     *
     * @param attribute the attribute
     */
    public void setAttribute( String attribute )
    {
        this.attribute = attribute;
    }


    /**
     * Sets the objectclass.
     *
     * @param objectclass the objectclass
     */
    public void setObjectclass( String objectclass )
    {
        this.objectclass = objectclass;
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
    public void setType( AclWhoClauseGroupTypeEnum type )
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
        sb.append( "group" );

        // Object Class
        if ( objectclass != null )
        {
            sb.append( "/" );
            sb.append( objectclass );

            // Attribute Name
            if ( attribute != null )
            {
                sb.append( "/" );
                sb.append( attribute );
            }
        }

        // Type
        if ( type != null )
        {
            sb.append( "." );
            sb.append( type );
        }

        // Pattern
        sb.append( '=' );
        sb.append( '"' );
        sb.append( pattern );
        sb.append( '"' );

        return sb.toString();
    }
}
