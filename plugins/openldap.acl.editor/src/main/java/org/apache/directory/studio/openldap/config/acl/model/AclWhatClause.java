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
 * The ACL what clause.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclWhatClause
{
    /** The star (*) clause */
    private AclWhatClauseStar starClause;

    /** The DN clause */
    private AclWhatClauseDn dnClause;

    /** The filter clause */
    private AclWhatClauseFilter filterClause;

    /** The attributes clause */
    private AclWhatClauseAttributes attributesClause;


    public AclWhatClause()
    {
    }


    public AclWhatClause( AclWhatClauseStar starClause, AclWhatClauseDn dnClause, AclWhatClauseFilter filterClause,
        AclWhatClauseAttributes attributesClause )
    {
        this.starClause = starClause;
        this.dnClause = dnClause;
        this.filterClause = filterClause;
        this.attributesClause = attributesClause;
    }


    public AclWhatClause( AclWhatClauseStar starClause )
    {
        this.starClause = starClause;
    }


    public AclWhatClause( AclWhatClauseDn dnClause )
    {
        this.dnClause = dnClause;
    }


    public AclWhatClause( AclWhatClauseFilter filterClause )
    {
        this.filterClause = filterClause;
    }


    public AclWhatClause( AclWhatClauseAttributes attributesClause )
    {
        this.attributesClause = attributesClause;
    }


    /**
     * @return the attributesClause
     */
    public AclWhatClauseAttributes getAttributesClause()
    {
        return attributesClause;
    }


    /**
     * @return the dnClause
     */
    public AclWhatClauseDn getDnClause()
    {
        return dnClause;
    }


    /**
     * @return the filterClause
     */
    public AclWhatClauseFilter getFilterClause()
    {
        return filterClause;
    }


    /**
     * @return the starClause
     */
    public AclWhatClauseStar getStarClause()
    {
        return starClause;
    }


    /**
     * @param attributesClause the attributesClause to set
     */
    public void setAttributesClause( AclWhatClauseAttributes attributesClause )
    {
        this.attributesClause = attributesClause;
    }


    /**
     * @param dnClause the dnClause to set
     */
    public void setDnClause( AclWhatClauseDn dnClause )
    {
        this.dnClause = dnClause;
    }


    /**
     * @param filterClause the filterClause to set
     */
    public void setFilterClause( AclWhatClauseFilter filterClause )
    {
        this.filterClause = filterClause;
    }


    /**
     * @param starClause the starClause to set
     */
    public void setStarClause( AclWhatClauseStar starClause )
    {
        this.starClause = starClause;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;

        // Star (*) clause
        if ( starClause != null )
        {
            isFirst = false;
            sb.append( starClause.toString() );
        }

        // DN clause
        if ( dnClause != null )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( " " );
            }

            sb.append( dnClause.toString() );
        }

        // Filter clause
        if ( filterClause != null )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( " " );
            }

            sb.append( filterClause.toString() );
        }

        // Attributes clause
        if ( attributesClause != null )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                sb.append( " " );
            }

            sb.append( attributesClause.toString() );
        }

        return sb.toString();
    }
}
