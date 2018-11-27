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
 * The  Acl what-dn clause. It has a type and a pattern.
 * The type is one of :
 * <ul>
 *   <li>base : AclWhatClauseDnTypeEnum.BASE</li>
 *   <li>baseObject : AclWhatClauseDnTypeEnum.BASE_OBJECT</li>
 *   <li>one : AclWhatClauseDnTypeEnum.ONE</li>
 *   <li>oneLevel : AclWhatClauseDnTypeEnum.ONE_LEVEL</li>
 *   <li>sub/subtree : AclWhatClauseDnTypeEnum.SUB</li>
 *   <li>subtree : AclWhatClauseDnTypeEnum.SUBTREE</li>
 *   <li>children : AclWhatClauseDnTypeEnum.CHILDREN</li>
 *   <li>exact : AclWhatClauseDnTypeEnum.EXACT</li>
 *   <li>regex : AclWhatClauseDnTypeEnum.REGEX</li>
 * </ul>
 * 
 * The pattern can be a DN or a regexp, depending on the type.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclWhatClauseDn extends AclWhatClause
{
    /** The type, default to BASE */
    private AclWhatClauseDnTypeEnum type;

    /** The pattern */
    private String pattern;


    /**
     * Gets the type.
     * 
     * @return the type
     */
    public AclWhatClauseDnTypeEnum getType()
    {
        return type;
    }


    /**
     * Sets the type.
     * 
     * @param type the type to set
     */
    public void setType( AclWhatClauseDnTypeEnum type )
    {
        this.type = type;
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
     * Sets the pattern
     * 
     * @param pattern the pattern to set
     */
    public void setPattern( String pattern )
    {
        this.pattern = pattern;
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

        // Pattern
        sb.append( '=' );
        sb.append( '"' );
        sb.append( pattern );
        sb.append( '"' );

        return sb.toString();
    }
}
