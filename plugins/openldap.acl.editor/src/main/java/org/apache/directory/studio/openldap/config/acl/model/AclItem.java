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


/**
 * This class represents an ACL (Access Control List) Item for OpenLDAP. 
 * 
 * An AclItem contains an AclWhatClause and a list of AclWhoClause
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclItem
{
    /** The {@link AclWhatClause} element */
    private AclWhatClause whatClause;

    /** The {@link AclWhoClause} elements */
    private List<AclWhoClause> whoClauses = new ArrayList<AclWhoClause>();


    /**
     * Creates a new instance of AclItem.
     */
    public AclItem()
    {
        whatClause = new AclWhatClause();
    }


    /**
     * Creates a new instance of AclItem.
     *
     * @param whatClause the {@link AclWhatClause} element 
     * @param whoClauses the {@link AclWhoClause} elements
     */
    public AclItem( AclWhatClause whatClause, List<AclWhoClause> whoClauses )
    {
        this.whatClause = whatClause;
        this.whoClauses = whoClauses;
    }


    /**
     * Gets the {@link AclWhatClause} element.
     * 
     * @return the whatClauses the {@link AclWhatClause} element
     */
    public AclWhatClause getWhatClause()
    {
        return whatClause;
    }


    /**
     * Gets the {@link AclWhoClause} elements.
     * 
     * @return the whoClauses the {@link AclWhoClause} elements
     */
    public List<AclWhoClause> getWhoClauses()
    {
        return whoClauses;
    }


    /**
     * Sets the {@link AclWhatClause} element.
     * 
     * @param whatClause the {@link AclWhatClause} element to set
     */
    public void setWhatClause( AclWhatClause whatClause )
    {
        this.whatClause = whatClause;
    }


    /**
     * Adds an {@link AclWhoClause} element.
     * 
     * @param c the {@link AclWhoClause} element to add
     */
    public void addWhoClause( AclWhoClause c )
    {
        whoClauses.add( c );
    }


    /**
     * Adds a {@link Collection} of {@link AclWhoClause} element.
     * 
     * @param c the {@link Collection} of {@link AclWhoClause}
     */
    public void addAllWhoClause( Collection<? extends AclWhoClause> c )
    {
        whoClauses.addAll( c );
    }


    /**
     * Clears all {@link AclWhoClause} elements.
     */
    public void clearWhoClause()
    {
        whoClauses.clear();
    }


    /**
     * {@inheritDoc} 
     */
    public String toString()
    {
        return toString( false );
    }


    public String toString( boolean prependAccess )
    {
        return toString( prependAccess, false );
    }


    public String toString( boolean prependAccess, boolean prettyPrint )
    {

        StringBuilder sb = new StringBuilder();

        // Access (if needed)
        if ( prependAccess )
        {
            sb.append( "access " );
        }

        // To
        sb.append( "to " );

        // What Clause
        if ( whatClause != null )
        {
            sb.append( whatClause.toString() );
        }

        // Who Clauses
        if ( ( whoClauses != null ) && ( whoClauses.size() > 0 ) )
        {
            for ( AclWhoClause whoClause : whoClauses )
            {
                if ( prettyPrint )
                {
                    sb.append( "\n" );
                }
                else
                {
                    sb.append( " " );
                }
                sb.append( "by " );
                sb.append( whoClause.toString() );
            }
        }

        return sb.toString();
    }
}
