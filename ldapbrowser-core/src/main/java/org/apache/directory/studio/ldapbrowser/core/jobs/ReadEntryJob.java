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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;


/**
 * Job to read a single entry from directory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReadEntryJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The DN of the entry. */
    private LdapDN dn;

    /** The entry read from directory. */
    private IEntry readEntry;


    /**
     * Creates a new instance of ReadEntryJob.
     * 
     * @param browserConnection the browser connection
     * @param dn the DN of the entry
     */
    public ReadEntryJob( IBrowserConnection browserConnection, LdapDN dn )
    {
        this.browserConnection = browserConnection;
        this.dn = dn;
        this.readEntry = null;

        setName( BrowserCoreMessages.jobs__read_entry_name );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection };
    }


    /**
     * Gets the read entry.
     * 
     * @return the read entry
     */
    public IEntry getReadEntry()
    {
        return readEntry;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__read_entry_error;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor pm )
    {
        readEntry = browserConnection.getEntryFromCache( dn );
        if ( readEntry == null )
        {
            pm.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__read_entry_task, new String[]
                { dn.toString() } ), 2 );
            pm.reportProgress( " " ); //$NON-NLS-1$
            pm.worked( 1 );

            readEntry = getEntry( browserConnection, dn, pm );
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
    }


    /**
     * Gets the entry, either from the BrowserConnection's cache or from the directory.
     * 
     * @param browserConnection the browser connection
     * @param dn the DN of the entry
     * @param monitor the progress monitor
     * 
     * @return the read entry
     */
    static IEntry getEntry( IBrowserConnection browserConnection, LdapDN dn, StudioProgressMonitor monitor )
    {
        try
        {
            // first check cache
            IEntry entry = browserConnection.getEntryFromCache( dn );
            if ( entry != null )
            {
                return entry;
            }

            // search in directory
            ISearch search = new Search( null, browserConnection, dn, null, ISearch.NO_ATTRIBUTES, SearchScope.OBJECT,
                1, 0, browserConnection.getAliasesDereferencingMethod(),
                browserConnection.getReferralsHandlingMethod(), true, null );
            SearchJob.searchAndUpdateModel( browserConnection, search, monitor );
            ISearchResult[] srs = search.getSearchResults();
            if ( srs.length > 0 )
            {
                return srs[0].getEntry();
            }
            else
            {
                monitor.reportError( BrowserCoreMessages.bind( BrowserCoreMessages.model__no_such_entry, dn ) );
                return null;
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( e.getMessage(), e );
            return null;
        }
    }

}
