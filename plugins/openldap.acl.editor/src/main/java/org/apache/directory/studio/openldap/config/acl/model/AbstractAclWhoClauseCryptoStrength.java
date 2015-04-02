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
 * An abstract class for all Cryptographic Strength subclasses.
 */
public abstract class AbstractAclWhoClauseCryptoStrength extends AbstractAclWhoClause
{
    /** The strength */
    protected int strength;


    /**
     * Sets the cryptographic strength.
     *
     * @return the strength.
     */
    public int getStrength()
    {
        return strength;
    }


    /**
     * Sets the cryptographic strength.
     *
     * @param strength the strength
     */
    public void setStrength( int strength )
    {
        this.strength = strength;
    }
}
