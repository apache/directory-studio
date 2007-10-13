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


import java.io.Writer;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BookmarkManager;
import org.apache.directory.studio.ldapbrowser.core.SearchManager;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.URL;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


public class DummyConnection implements IBrowserConnection
{

    private static final long serialVersionUID = 3671686808330691741L;

    private Schema schema;


    public DummyConnection( Schema schema )
    {
        this.schema = schema;
    }


    public void create( IValue[] valuesToCreate, StudioProgressMonitor monitor )
    {
    }


    public void delete( IEntry entryToDelete, StudioProgressMonitor monitor )
    {
    }


    public void delete( IValue[] valuesToDelete, StudioProgressMonitor monitor )
    {

    }


    public void delete( IAttribute[] attriubtesToDelete, StudioProgressMonitor monitor )
    {

    }


    public boolean existsEntry( DN dn, StudioProgressMonitor monitor )
    {
        return false;
    }


    public LdifEnumeration exportLdif( SearchParameter searchParameter, StudioProgressMonitor pm )
        throws ConnectionException
    {
        throw new UnsupportedOperationException();
    }


    public void fetchRootDSE( StudioProgressMonitor monitor )
    {
    }


    public DN getBaseDN()
    {
        return new DN();
    }


    public BookmarkManager getBookmarkManager()
    {
        return null;
    }


    public int getCountLimit()
    {
        return 0;
    }


    public int getAliasesDereferencingMethod()
    {
        return DEREFERENCE_ALIASES_NEVER;
    }


    public IEntry getEntry( DN dn, StudioProgressMonitor monitor )
    {
        return null;
    }


    public IEntry getEntryFromCache( DN dn )
    {
        return null;
    }


    public IRootDSE getRootDSE()
    {
        return null;
    }


    public Schema getSchema()
    {
        return schema;
    }


    public SearchManager getSearchManager()
    {
        return null;
    }


    public int getTimeLimit()
    {
        return 0;
    }


    public void importLdif( LdifEnumeration enumeration, Writer logWriter, boolean continueOnError,
        StudioProgressMonitor monitor )
    {

    }


    public boolean isFetchBaseDNs()
    {
        return false;
    }


    public void modify( IValue oldValue, IValue newVaue, StudioProgressMonitor monitor )
    {
    }


    public void reloadSchema( StudioProgressMonitor monitor )
    {
    }


    public void move( IEntry entryToMove, DN newSuperior, StudioProgressMonitor monitor )
    {
    }


    public void rename( IEntry entryToRename, DN newDn, boolean deleteOldRdn, StudioProgressMonitor monitor )
    {
    }


    public void search( ISearch searchRequest, StudioProgressMonitor monitor )
    {
    }


    public void setBaseDN( DN baseDN )
    {
    }


    public void setCountLimit( int countLimit )
    {
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
    }


    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
    }


    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    public void setTimeLimit( int timeLimit )
    {

    }


    public Object getAdapter( Class adapter )
    {
        return null;
    }


    public Object clone()
    {
        return this;
    }


    public ModificationLogger getModificationLogger()
    {
        return null;
    }


    public int getReferralsHandlingMethod()
    {
        return HANDLE_REFERRALS_IGNORE;
    }


    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
    }


    public URL getUrl()
    {
        return null;
    }


    public Connection getConnection()
    {
        return null;
    }

}
