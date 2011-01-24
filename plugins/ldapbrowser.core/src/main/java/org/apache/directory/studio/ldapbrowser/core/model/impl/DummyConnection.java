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


import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


/**
 * Connection without any operation. It could be used to make model modifications
 * without committing these modifications to the directory.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DummyConnection implements IBrowserConnection
{

    private static final long serialVersionUID = 3671686808330691741L;

    /** The schema. */
    private Schema schema;


    /**
     * Creates a new instance of DummyConnection.
     * 
     * @param schema the schema
     */
    public DummyConnection( Schema schema )
    {
        this.schema = schema;
    }


    /** 
     * {@inheritDoc}
     */
    public Dn getBaseDN()
    {
        return Dn.EMPTY_DN;
    }


    /** 
     * {@inheritDoc}
     */
    public BookmarkManager getBookmarkManager()
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public int getCountLimit()
    {
        return 0;
    }


    /** 
     * {@inheritDoc}
     */
    public Connection.AliasDereferencingMethod getAliasesDereferencingMethod()
    {
        return Connection.AliasDereferencingMethod.NEVER;
    }


    /** 
     * {@inheritDoc}
     */
    public IEntry getEntryFromCache( Dn dn )
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public IRootDSE getRootDSE()
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public Schema getSchema()
    {
        return schema;
    }


    /** 
     * {@inheritDoc}
     */
    public SearchManager getSearchManager()
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public int getTimeLimit()
    {
        return 0;
    }


    /** 
     * {@inheritDoc}
     */
    public boolean isFetchBaseDNs()
    {
        return false;
    }


    /** 
     * {@inheritDoc}
     */
    public void setBaseDN( Dn baseDn)
    {
    }


    /** 
     * {@inheritDoc}
     */
    public void setCountLimit( int countLimit )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public void setAliasesDereferencingMethod( Connection.AliasDereferencingMethod aliasesDereferencingMethod )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    /** 
     * {@inheritDoc}
     */
    public void setTimeLimit( int timeLimit )
    {
    }


    /** 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public Object clone()
    {
        return this;
    }


    /** 
     * {@inheritDoc}
     */
    public Connection.ReferralHandlingMethod getReferralsHandlingMethod()
    {
        return Connection.ReferralHandlingMethod.IGNORE;
    }


    /** 
     * {@inheritDoc}
     */
    public void setReferralsHandlingMethod( Connection.ReferralHandlingMethod referralsHandlingMethod )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public boolean isManageDsaIT()
    {
        return false;
    }


    /** 
     * {@inheritDoc}
     */
    public void setManageDsaIT( boolean manageDsaIT )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public boolean isFetchSubentries()
    {
        return false;
    }


    /** 
     * {@inheritDoc}
     */
    public void setFetchSubentries( boolean fetchSubentries )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public boolean isFetchOperationalAttributes()
    {
        return false;
    }


    /** 
     * {@inheritDoc}
     */
    public void setFetchOperationalAttributes( boolean fetchOperationalAttributes )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public boolean isPagedSearch()
    {
        return false;
    }


    /** 
     * {@inheritDoc}
     */
    public void setPagedSearch( boolean pagedSearch )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public int getPagedSearchSize()
    {
        return 0;
    }


    /** 
     * {@inheritDoc}
     */
    public void setPagedSearchSize( int pagedSearchSize )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public ModifyMode getModifyMode()
    {
        return ModifyMode.DEFAULT;
    }


    /** 
     * {@inheritDoc}
     */
    public void setModifyMode( ModifyMode mode )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public ModifyMode getModifyModeNoEMR()
    {
        return ModifyMode.DEFAULT;
    }


    /** 
     * {@inheritDoc}
     */
    public void setModifyModeNoEMR( ModifyMode mode )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public ModifyOrder getModifyAddDeleteOrder()
    {
        return ModifyOrder.DELETE_FIRST;
    }


    /** 
     * {@inheritDoc}
     */
    public void setModifyAddDeleteOrder( ModifyOrder mode )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public boolean isPagedSearchScrollMode()
    {
        return false;
    }


    /** 
     * {@inheritDoc}
     */
    public void setPagedSearchScrollMode( boolean pagedSearchScrollMode )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public LdapURL getUrl()
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public Connection getConnection()
    {
        return null;
    }


    /** 
     * {@inheritDoc}
     */
    public void cacheEntry( IEntry entry )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public void uncacheEntryRecursive( IEntry entry )
    {
    }


    /** 
     * {@inheritDoc}
     */
    public void clearCaches()
    {
    }

}
