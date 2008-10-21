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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import javax.naming.ldap.Control;


public class JNDIControl implements Control
{

    private static final long serialVersionUID = -2051120560938595303L;

    private String id;

    private boolean criticality;

    private byte[] encodedValue;


    public JNDIControl( String id )
    {
        this( id, false, null );
    }


    public JNDIControl( String id, boolean criticality, byte[] encodedValue )
    {
        this.id = id;
        this.criticality = criticality;
        this.encodedValue = encodedValue;
    }


    public byte[] getEncodedValue()
    {
        return encodedValue;
    }


    public String getID()
    {
        return id;
    }


    public boolean isCritical()
    {
        return criticality;
    }

}
