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
package org.apache.directory.studio.openldap.config.acl;


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * The OpenLdapAclValueWithContext is used to pass contextual
 * information to the opened OpenLdapAclDialog.
 */
public class OpenLdapAclValueWithContext
{
    /** The connection used to browse the directory */
    private IBrowserConnection connection;

    /** The entry */
    private IEntry entry;

    /** A flag indicating if precedence is used */
    private boolean hasPrecedence = false;

    /** The precedence value */
    private int precedenceValue = -1;

    /** The ACL value */
    private String aclValue;


    /**
     * Creates a new instance of OpenLdapAclValueWithContext.
     * 
     * @param connection the connection
     * @param entry the entry
     * @param hasPrecedence a flag indicating if precedence is used
     * @param precedenceValue the precedence value
     * @param aclValue the ACL value
     */
    public OpenLdapAclValueWithContext( IBrowserConnection connection, IEntry entry, boolean hasPrecedence,
        int precedenceValue, String aclValue )
    {
        this.connection = connection;
        this.entry = entry;
        this.hasPrecedence = hasPrecedence;
        this.precedenceValue = precedenceValue;
        this.aclValue = aclValue;
    }


    /**
     * Gets the ACL value.
     * 
     * @return the ACL value
     */
    public String getAclValue()
    {
        return aclValue;
    }


    /**
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return connection;
    }


    /**
     * @return the entry
     */
    public IEntry getEntry()
    {
        return entry;
    }


    /**
     * @return the precedence value
     */
    public int getPrecedenceValue()
    {
        return precedenceValue;
    }


    /**
     * @return whether precedence is used or not
     */
    public boolean isHasPrecedence()
    {
        return hasPrecedence;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        if (  aclValue != null )
        {
            if ( hasPrecedence )
            {
                return "{" + precedenceValue + "}" + aclValue;
            }
            else
            {
                return aclValue;
            }
        }
        
        
        return "";
    }
}
