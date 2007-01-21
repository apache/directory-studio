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

package org.apache.directory.ldapstudio.browser.core.jobs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.BrowserCorePlugin;
import org.apache.directory.ldapstudio.browser.core.events.ChildrenInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.AliasBaseEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.ReferralBaseEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Search;
import org.apache.directory.ldapstudio.browser.core.model.Control;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IRootDSE;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;


public class InitializeChildrenJob extends AbstractAsyncBulkJob
{

    private IEntry[] entries;


    public InitializeChildrenJob( IEntry[] entries )
    {
        this.entries = entries;
        setName( BrowserCoreMessages.jobs__init_entries_title_subonly );
    }


    protected IConnection[] getConnections()
    {
        IConnection[] connections = new IConnection[entries.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = entries[i].getConnection();
        }
        return connections;
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.addAll( Arrays.asList( entries ) );
        return l.toArray();
    }


    protected String getErrorMessage()
    {
        return entries.length == 1 ? BrowserCoreMessages.jobs__init_entries_error_1
            : BrowserCoreMessages.jobs__init_entries_error_n;
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {
        monitor.beginTask( " ", entries.length + 2 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < entries.length && !monitor.isCanceled(); pi++ )
        {

            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_task, new String[]
                { this.entries[pi].getDn().toString() } ) );
            monitor.worked( 1 );

            if ( entries[pi].getConnection() != null && entries[pi].getConnection().isOpened()
                && entries[pi].isDirectoryEntry() )
            {
                initializeChildren( entries[pi], monitor );
            }
        }
    }


    protected void runNotification()
    {
        for ( int pi = 0; pi < entries.length; pi++ )
        {
            IEntry parent = entries[pi];
            if ( parent.getConnection() != null && entries[pi].getConnection().isOpened() && parent.isDirectoryEntry() )
            {
                EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( parent ), this );
            }
        }
    }


    public static void initializeChildren( IEntry parent, ExtendedProgressMonitor monitor )
    {

        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_sub,
            new String[]
                { parent.getDn().toString() } ) );

        // root DSE has no children
        if ( parent instanceof IRootDSE )
        {
            parent.setChildrenInitialized( true );
            return;
        }

        // clear old children
        IEntry[] oldChildren = parent.getChildren();
        for ( int i = 0; oldChildren != null && i < oldChildren.length; i++ )
        {
            if ( oldChildren[i] != null )
            {
                parent.deleteChild( oldChildren[i] );
            }
        }
        parent.setChildrenInitialized( false );

        // determine alias and referral handling
        int scope = ISearch.SCOPE_ONELEVEL;
        int derefAliasMethod = parent.getConnection().getAliasesDereferencingMethod();
        int handleReferralsMethod = parent.getConnection().getReferralsHandlingMethod();
        if ( BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS ) )
        {
            scope = ( parent.isAlias() || parent.isReferral() ) ? ISearch.SCOPE_OBJECT : ISearch.SCOPE_ONELEVEL;
            derefAliasMethod = parent.isAlias() ? IConnection.DEREFERENCE_ALIASES_FINDING
                : IConnection.DEREFERENCE_ALIASES_NEVER;
            handleReferralsMethod = parent.isReferral() ? IConnection.HANDLE_REFERRALS_FOLLOW
                : IConnection.HANDLE_REFERRALS_IGNORE;
        }

        // get children,
        ISearch search = new Search( null, parent.getConnection(), parent.getDn(), parent.getChildrenFilter(),
            ISearch.NO_ATTRIBUTES, scope, parent.getConnection().getCountLimit(),
            parent.getConnection().getTimeLimit(), derefAliasMethod, handleReferralsMethod, BrowserCorePlugin
                .getDefault().getPluginPreferences().getBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ),
            BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
                BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS ), null );
        parent.getConnection().search( search, monitor );
        ISearchResult[] srs = search.getSearchResults();
        monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_subcount,
            new String[]
                { srs == null ? Integer.toString( 0 ) : Integer.toString( srs.length ), parent.getDn().toString() } ) );

        // fill children in search result
        if ( srs != null && srs.length > 0 )
        {

            /*
             * clearing old children before filling new subenties is
             * necessary to handle aliases and referrals.
             */
            IEntry[] connChildren = parent.getChildren();
            for ( int i = 0; connChildren != null && i < connChildren.length; i++ )
            {
                if ( connChildren[i] != null )
                {
                    parent.deleteChild( connChildren[i] );
                }
            }
            parent.setChildrenInitialized( false );

            for ( int i = 0; srs != null && i < srs.length; i++ )
            {
                if ( parent.isReferral() )
                {
                    ReferralBaseEntry referralBaseEntry = new ReferralBaseEntry( srs[i].getEntry().getConnection(),
                        srs[i].getEntry().getDn() );
                    parent.addChild( referralBaseEntry );
                    // System.out.println("Ref: " +
                    // referralBaseEntry.getUrl());
                }
                else if ( parent.isAlias() )
                {
                    AliasBaseEntry aliasBaseEntry = new AliasBaseEntry( srs[i].getEntry().getConnection(), srs[i]
                        .getEntry().getDn() );
                    parent.addChild( aliasBaseEntry );
                    // System.out.println("Ali: " +
                    // aliasBaseEntry.getUrl());
                }
                else
                {
                    parent.addChild( srs[i].getEntry() );
                }
            }
        }
        else
        {
            parent.setHasChildrenHint( false );
        }

        // get subentries
        ISearch subSearch = new Search( null, parent.getConnection(), parent.getDn(), parent.getChildrenFilter(),
            ISearch.NO_ATTRIBUTES, scope, parent.getConnection().getCountLimit(),
            parent.getConnection().getTimeLimit(), derefAliasMethod, handleReferralsMethod, BrowserCorePlugin
                .getDefault().getPluginPreferences().getBoolean( BrowserCoreConstants.PREFERENCE_CHECK_FOR_CHILDREN ),
            BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
                BrowserCoreConstants.PREFERENCE_SHOW_ALIAS_AND_REFERRAL_OBJECTS ), new Control[]
                { Control.SUBENTRIES_CONTROL } );
        if ( BrowserCorePlugin.getDefault().getPluginPreferences().getBoolean(
            BrowserCoreConstants.PREFERENCE_FETCH_SUBENTRIES ) )
        {
            parent.getConnection().search( subSearch, monitor );
            ISearchResult[] subSrs = subSearch.getSearchResults();
            monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__init_entries_progress_subcount,
                new String[]
                    { subSrs == null ? Integer.toString( 0 ) : Integer.toString( subSrs.length ),
                        parent.getDn().toString() } ) );
            // fill children in search result
            if ( subSrs != null && subSrs.length > 0 )
            {

                for ( int i = 0; subSrs != null && i < subSrs.length; i++ )
                {
                    parent.addChild( subSrs[i].getEntry() );
                }
            }
        }

        // check exceeded limits / canceled
        parent.setHasMoreChildren( search.isCountLimitExceeded() || subSearch.isCountLimitExceeded()
            || monitor.isCanceled() );

        // set initialized state
        parent.setChildrenInitialized( true );

    }

}
