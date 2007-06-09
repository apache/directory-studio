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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.Serializable;


/**
 * A Bean class to hold the bookmark parameters. 
 * It is used to make bookmarks persistent.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BookmarkParameter implements Serializable
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = 105108281861642267L;

    /** The target DN. */
    private DN dn;

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
     * @param dn the target DN
     * @param name the symbolic name
     */
    public BookmarkParameter( DN dn, String name )
    {
        this.dn = dn;
        this.name = name;
    }


    /**
     * Gets the target DN.
     * 
     * @return the target DN
     */
    public DN getDn()
    {
        return dn;
    }


    /**
     * Sets the target DN.
     * 
     * @param dn the target DN
     */
    public void setDn( DN dn )
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
