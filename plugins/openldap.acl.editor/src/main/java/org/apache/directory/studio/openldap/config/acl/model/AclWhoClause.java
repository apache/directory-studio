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
 * The AclWhoClause.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface AclWhoClause
{
    /**
     * Gets the access level.
     *
     * @return the access level
     */
    public AclAccessLevel getAccessLevel();


    /**
     * Gets the control.
     *
     * @return the control
     */
    public AclControlEnum getControl();


    /**
     * Sets access level.
     *
     * @param accessLevel the access level
     */
    public void setAccessLevel( AclAccessLevel accessLevel );


    /**
     * Sets control.
     *
     * @param accessLevel the control
     */
    public void setControl( AclControlEnum control );
}
