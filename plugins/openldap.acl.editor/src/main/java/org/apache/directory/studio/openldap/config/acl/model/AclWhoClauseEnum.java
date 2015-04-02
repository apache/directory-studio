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


public enum AclWhoClauseEnum
{
    STAR,
    ANONYMOUS,
    USERS,
    SELF,
    DN,
    DNATTR,
    GROUP,
    SASL_SSF,
    SSF,
    TLS_SSF,
    TRANSPORT_SSF;

    /**
     * Gets the enum value associated with the given clause.
     *
     * @param clause the clause
     * @return the enum value associated with the given clause.
     */
    public static AclWhoClauseEnum get( AclWhoClause clause )
    {
        if ( clause instanceof AclWhoClauseStar )
        {
            return STAR;
        }
        else if ( clause instanceof AclWhoClauseAnonymous )
        {
            return ANONYMOUS;
        }
        else if ( clause instanceof AclWhoClauseUsers )
        {
            return USERS;
        }
        else if ( clause instanceof AclWhoClauseSelf )
        {
            return SELF;
        }
        else if ( clause instanceof AclWhoClauseDn )
        {
            return DN;
        }
        else if ( clause instanceof AclWhoClauseDnAttr )
        {
            return DNATTR;
        }
        else if ( clause instanceof AclWhoClauseGroup )
        {
            return GROUP;
        }
        else if ( clause instanceof AclWhoClauseSaslSsf )
        {
            return SASL_SSF;
        }
        else if ( clause instanceof AclWhoClauseSsf )
        {
            return SSF;
        }
        else if ( clause instanceof AclWhoClauseTlsSsf )
        {
            return TLS_SSF;
        }
        else if ( clause instanceof AclWhoClauseSaslSsf )
        {
            return SASL_SSF;
        }

        return null;
    }
}
