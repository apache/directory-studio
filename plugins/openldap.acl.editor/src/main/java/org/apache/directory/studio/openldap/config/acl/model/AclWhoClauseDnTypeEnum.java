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
 * This enum contains all possible values for the type of a DN Who Clause.
 */
public enum AclWhoClauseDnTypeEnum
{
    REGEX,
    BASE,
    EXACT,
    ONE,
    SUBTREE,
    CHILDREN,
    LEVEL;

    /** The level*/
    private int level = -1;


    /**
     * Gets the level.
     * <p>
     * Only useful for the 'LEVEL' enum type.
     *
     * @return the level
     */
    public int getLevel()
    {
        return level;
    }


    /**
     * Sets the level.
     * <p>
     * Only useful for the 'LEVEL' enum type.
     *
     * @param level the level
     */
    public void setLevel( int level )
    {
        this.level = level;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        switch ( this )
        {
            case REGEX:
                return "regex";
            case BASE:
                return "base";
            case EXACT:
                return "exact";
            case ONE:
                return "one";
            case SUBTREE:
                return "subtree";
            case CHILDREN:
                return "children";
            case LEVEL:
                return "level{" + level + "}";
        }

        return super.toString();
    }
}
