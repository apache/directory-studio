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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * The Entry class represents an entry with a logical parent entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Entry extends AbstractEntry
{

    private static final long serialVersionUID = -4718107307581983276L;

    /** The Rdn. */
    protected Rdn rdn;

    /** The parent entry. */
    protected IEntry parent;


    protected Entry()
    {
    }


    /**
     * Creates a new instance of Entry.
     * 
     * @param parent the parent entry
     * @param rdn the Rdn
     */
    public Entry( IEntry parent, Rdn rdn )
    {
        assert parent != null;
        assert rdn != null;
        assert !"".equals( rdn.toString() ); //$NON-NLS-1$

        this.parent = parent;
        this.rdn = rdn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#getRdn()
     */
    public Rdn getRdn()
    {
        // performance opt.
        return rdn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getDn()
     */
    public Dn getDn()
    {
        try
        {
            Dn dn = parent.getDn().add( rdn );
            
            return dn;
        }
        catch ( LdapInvalidDnException lide )
        {
            return null;
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getParententry()
     */
    public IEntry getParententry()
    {
        return parent;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getBrowserConnection()
     */
    public IBrowserConnection getBrowserConnection()
    {
        return getParententry().getBrowserConnection();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#setRdn(org.apache.directory.studio.ldapbrowser.core.model.RDN)
     */
    protected void setRdn( Rdn newRdn )
    {
        this.rdn = newRdn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#setParent(org.apache.directory.studio.ldapbrowser.core.model.IEntry)
     */
    protected void setParent( IEntry newParent )
    {
        this.parent = newParent;
    }

}
