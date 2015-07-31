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


import java.text.ParseException;

import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.openldap.config.acl.model.AclItem;
import org.apache.directory.studio.openldap.config.acl.model.OpenLdapAclParser;


/**
 * The OpenLdapAclValueWithContext is used to pass contextual
 * information to the opened OpenLdapAclDialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclValueWithContext
{
    /** The connection used to browse the directory */
    private IBrowserConnection connection;

    /** The entry */
    private IEntry entry;

    /** The precedence value, -1 means no precedence */
    private int precedence = -1;

    /** The ACL value */
    private String aclValue;
    
    /** The ACL instance */
    private AclItem aclItem;
    
    /** The ACL parser */
    private static final OpenLdapAclParser parser = new OpenLdapAclParser();

    static int nb = 0;

    /**
     * Creates a new instance of OpenLdapAclValueWithContext.
     * 
     * @param connection the connection
     * @param entry the entry
     * @param precedence the precedence
     * @param aclValue the ACL value
     */
    public OpenLdapAclValueWithContext( IBrowserConnection connection, IEntry entry, int precedence, String aclValue )
    {
        this.connection = connection;
        this.entry = entry;
        this.precedence = precedence;
        this.aclValue = aclValue;
        
        System.out.println( "OpenLdapAclValueWithContext" + nb++ );
        
        try
        {
            aclItem = parser.parse( aclValue );
        }
        catch ( ParseException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
     * @return the precedence
     */
    public int getPrecedence()
    {
        return precedence;
    }


    /**
     * @return whether precedence is used or not
     */
    public boolean hasPrecedence()
    {
        return precedence  != -1;
    }

    
    /**
     * @return The ACL Item being built
     */
    public AclItem getAclItem()
    {
        return aclItem;
    }
    

    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        if (  aclValue != null )
        {
            if ( precedence != -1 )
            {
                return "{" + precedence + "}" + aclValue;
            }
            else
            {
                return aclValue;
            }
        }
        
        return "";
    }
}
