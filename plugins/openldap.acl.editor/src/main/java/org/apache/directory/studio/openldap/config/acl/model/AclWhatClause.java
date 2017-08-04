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
        this.filterClause = filterClause;
        this.attributesClause = attributesClause;
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
     * @return the filterClause
     */
    public AclWhatClauseFilter getFilterClause()
    {
        return filterClause;
    }


    /**
     * @param attributesClause the attributesClause to set
     */
    public void setAttributesClause( AclWhatClauseAttributes attributesClause )
    {
        this.attributesClause = attributesClause;
    }


    /**
     * @param filterClause the filterClause to set
     */
    public void setFilterClause( AclWhatClauseFilter filterClause )
    {
        this.filterClause = filterClause;
    }
}
