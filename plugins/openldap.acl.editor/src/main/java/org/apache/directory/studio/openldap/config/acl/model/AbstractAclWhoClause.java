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
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractAclWhoClause implements AclWhoClause
{
    /** The access level */
    protected AclAccessLevel accessLevel;

    /** The control */
    protected AclControlEnum control;


    /**
     * {@inheritDoc}
     */
    public AclAccessLevel getAccessLevel()
    {
        return accessLevel;
    }


    /**
     * {@inheritDoc}
     */
    public AclControlEnum getControl()
    {
        return control;
    }


    /**
     * {@inheritDoc}
     */
    public void setAccessLevel( AclAccessLevel accessLevel )
    {
        this.accessLevel = accessLevel;
    }


    /**
     * {@inheritDoc}
     */
    public void setControl( AclControlEnum control )
    {
        this.control = control;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Access Level
        if ( accessLevel != null )
        {
            sb.append( accessLevel );
        }

        // Control
        if ( control != null )
        {
            if ( sb.length() > 0 )
            {
                sb.append( " " );
            }

            sb.append( control );
        }

        return sb.toString();
    }
}
