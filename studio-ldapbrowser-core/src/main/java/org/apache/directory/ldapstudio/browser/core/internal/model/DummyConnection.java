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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import java.io.Writer;

import org.apache.directory.ldapstudio.browser.core.BookmarkManager;
import org.apache.directory.ldapstudio.browser.core.SearchManager;
import org.apache.directory.ldapstudio.browser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.ldapstudio.browser.core.model.ConnectionParameter;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IRootDSE;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.URL;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifEnumeration;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;


public class DummyConnection implements IConnection
{

    private static final long serialVersionUID = 3671686808330691741L;

    private Schema schema;


    public DummyConnection( Schema schema )
    {
        this.schema = schema;
    }


    public void bind( ExtendedProgressMonitor monitor )
    {
    }


    public boolean canClose()
    {
        return false;
    }


    public boolean canOpen()
    {
        return false;
    }


    public void close()
    {
    }


    public void connect( ExtendedProgressMonitor monitor )
    {
    }


    public void create( IEntry entryToCreate, ExtendedProgressMonitor monitor )
    {
    }


    public void create( IValue[] valuesToCreate, ExtendedProgressMonitor monitor )
    {
    }


    public void delete( IEntry entryToDelete, ExtendedProgressMonitor monitor )
    {
    }


    public void delete( IValue[] valuesToDelete, ExtendedProgressMonitor monitor )
    {

    }


    public void delete( IAttribute[] attriubtesToDelete, ExtendedProgressMonitor monitor )
    {

    }


    public boolean existsEntry( DN dn, ExtendedProgressMonitor monitor )
    {
        return false;
    }


    public LdifEnumeration exportLdif( SearchParameter searchParameter, ExtendedProgressMonitor pm )
        throws ConnectionException
    {
        throw new UnsupportedOperationException();
    }


    public void fetchRootDSE( ExtendedProgressMonitor monitor )
    {
    }


    public int getAuthMethod()
    {
        return AUTH_ANONYMOUS;
    }


    public DN getBaseDN()
    {
        return new DN();
    }


    public IEntry[] getBaseDNEntries()
    {
        return new IEntry[0];
    }


    public String getBindPrincipal()
    {
        return null;
    }


    public String getBindPassword()
    {
        return null;
    }


    public BookmarkManager getBookmarkManager()
    {
        return null;
    }


    public ConnectionParameter getConnectionParameter()
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


    public int getEncryptionMethod()
    {
        return ENCYRPTION_NONE;
    }


    public IEntry getEntry( DN dn, ExtendedProgressMonitor monitor )
    {
        return null;
    }


    public IEntry getEntryFromCache( DN dn )
    {
        return null;
    }


    public String getHost()
    {
        return null;
    }


    public IEntry[] getMetadataEntries()
    {
        return new IEntry[0];
    }


    public String getName()
    {
        return null;
    }


    public int getPort()
    {
        return -1;
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
        ExtendedProgressMonitor monitor )
    {

    }


    public boolean isFetchBaseDNs()
    {
        return false;
    }


    public boolean isOpened()
    {
        return true;
    }


    public boolean isSuspended()
    {
        return true;
    }


    public void modify( IValue oldValue, IValue newVaue, ExtendedProgressMonitor monitor )
    {
    }


    public void open( ExtendedProgressMonitor monitor )
    {
    }


    public void reloadSchema( ExtendedProgressMonitor monitor )
    {
    }


    public void move( IEntry entryToMove, DN newSuperior, ExtendedProgressMonitor monitor )
    {
    }


    public void rename( IEntry entryToRename, DN newDn, boolean deleteOldRdn, ExtendedProgressMonitor monitor )
    {
    }


    public void reset()
    {
    }


    public void resume( ExtendedProgressMonitor monitor )
    {
    }


    public void search( ISearch searchRequest, ExtendedProgressMonitor monitor )
    {
    }


    public void setAuthMethod( int authMethod )
    {
    }


    public void setBaseDN( DN baseDN )
    {
    }


    public void setBindPrincipal( String bindPrincipal )
    {
    }


    public void setBindPassword( String bindPassword )
    {
    }


    public void setConnectionParameter( ConnectionParameter connectionParameter )
    {
    }


    public void setCountLimit( int countLimit )
    {
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
    }


    public void setEncryptionMethod( int encryptionMethod )
    {
    }


    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
    }


    public void setHost( String host )
    {
    }


    public void setName( String name )
    {
    }


    public void setPort( int port )
    {
    }


    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    public void setTimeLimit( int timeLimit )
    {

    }


    public void suspend()
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

}
