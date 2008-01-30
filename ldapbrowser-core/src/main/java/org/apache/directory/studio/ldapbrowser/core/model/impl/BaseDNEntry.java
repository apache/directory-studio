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


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * The BaseDNEntry class represents an entry without a logical parent entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BaseDNEntry extends AbstractEntry
{

    private static final long serialVersionUID = -5444229580355372176L;

    /** The base DN. */
    protected LdapDN baseDn;

    /** The browser connection. */
    protected IBrowserConnection browserConnection;


    protected BaseDNEntry()
    {
    }


    /**
     * Creates a new instance of BaseDNEntry.
     * 
     * @param baseDn the base DN
     * @param browserConnection the browser connection
     */
    public BaseDNEntry( LdapDN baseDn, IBrowserConnection browserConnection )
    {
        assert baseDn != null;
        assert browserConnection != null;

        this.setDirectoryEntry( true );
        this.baseDn = baseDn;
        this.browserConnection = browserConnection;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getDn()
     */
    public LdapDN getDn()
    {
        return baseDn;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getParententry()
     */
    public IEntry getParententry()
    {
        return getBrowserConnection().getRootDSE();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.IEntry#getBrowserConnection()
     */
    public IBrowserConnection getBrowserConnection()
    {
        return browserConnection;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#setRdn(org.apache.directory.studio.ldapbrowser.core.model.RDN)
     */
    protected void setRdn( Rdn newRdn )
    {
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#setParent(org.apache.directory.studio.ldapbrowser.core.model.IEntry)
     */
    protected void setParent( IEntry newParent )
    {
    }

}
