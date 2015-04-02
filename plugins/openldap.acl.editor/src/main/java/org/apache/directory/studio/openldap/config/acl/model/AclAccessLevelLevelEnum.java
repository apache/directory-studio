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
 * TODO AclAccessLevelLevelEnum.
 */
public enum AclAccessLevelLevelEnum
{
    MANAGE,
    WRITE,
    READ,
    SEARCH,
    COMPARE,
    AUTH,
    DISCLOSE,
    NONE;

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        switch ( this )
        {
            case MANAGE:
                return "manage";
            case WRITE:
                return "write";
            case READ:
                return "read";
            case SEARCH:
                return "search";
            case COMPARE:
                return "compare";
            case AUTH:
                return "auth";
            case DISCLOSE:
                return "disclose";
            case NONE:
                return "none";
        }

        return super.toString();
    }
}
