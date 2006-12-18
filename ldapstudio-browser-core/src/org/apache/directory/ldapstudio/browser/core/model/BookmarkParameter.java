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


public class BookmarkParameter implements Serializable
{

    private static final long serialVersionUID = 105108281861642267L;

    private DN dn;

    private String name;


    public BookmarkParameter()
    {
    }


    public BookmarkParameter( DN dn, String name )
    {
        this.dn = dn;
        this.name = name;
    }


    public DN getDn()
    {
        return dn;
    }


    public void setDn( DN dn )
    {
        this.dn = dn;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }

}
