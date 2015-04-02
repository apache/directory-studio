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


public class AclWhatClauseAttributes
{
    /** The attributes list */
    private List<String> attributes = new ArrayList<String>();


    /**
     * Gets the attributes list.
     *
     * @return the attributes list
     */
    public List<String> getAttributes()
    {
        return attributes;
    }


    /**
     * Adds an attribute.
     * 
     * @param attribute the attribute to add
     */
    public void addAttribute( String attribute )
    {
        attributes.add( attribute );
    }


    /**
     * Adds a {@link Collection} of attributes.
     * 
     * @param c the {@link Collection} of attributes
     */
    public void addAllAttributes( Collection<String> c )
    {
        attributes.addAll( c );
    }


    /**
     * Clears attributes.
     */
    public void clearAttributes()
    {
        attributes.clear();
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Attrs
        sb.append( "attrs=" );

        // Attributes
        if ( ( attributes != null ) && ( attributes.size() > 0 ) )
        {
            for ( String attribute : attributes )
            {
                sb.append( attribute );
                sb.append( "," );
            }

            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.toString();
    }
}
