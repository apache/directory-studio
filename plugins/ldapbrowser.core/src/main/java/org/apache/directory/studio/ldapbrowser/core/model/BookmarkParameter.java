/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.ldapbrowser.core.model;


import java.io.Serializable;

import org.apache.directory.shared.ldap.name.Dn;


/**
 * A Bean class to hold the bookmark parameters. 
 * It is used to make bookmarks persistent.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BookmarkParameter implements Serializable
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = 105108281861642267L;

    /** The target Dn. */
    private Dn dn;

    /** The symbolic name. */
    private String name;


    /**
     * Creates a new instance of BookmarkParameter.
     */
    public BookmarkParameter()
    {
    }


    /**
     * Creates a new instance of BookmarkParameter.
     *
     * @param dn the target Dn
     * @param name the symbolic name
     */
    public BookmarkParameter( Dn dn, String name )
    {
        this.dn = dn;
        this.name = name;
    }


    /**
     * Gets the target Dn.
     * 
     * @return the target Dn
     */
    public Dn getDn()
    {
        return dn;
    }


    /**
     * Sets the target Dn.
     * 
     * @param dn the target Dn
     */
    public void setDn( Dn dn )
    {
        this.dn = dn;
    }


    /**
     * Gets the symbolic name.
     * 
     * @return the symbolic name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the symbolic name.
     * 
     * @param name the symbolic name
     */
    public void setName( String name )
    {
        this.name = name;
    }

}
