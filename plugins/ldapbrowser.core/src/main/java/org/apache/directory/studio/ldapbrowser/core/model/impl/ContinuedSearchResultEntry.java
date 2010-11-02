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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IContinuation;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;


/**
 * An {@link ContinuedSearchResultEntry} represents a result entry of a search continuation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ContinuedSearchResultEntry extends DelegateEntry implements IContinuation
{

    private static final long serialVersionUID = -6351277968774226912L;

    /** The search continuation URL. */
    private LdapURL url;

    /** The state. */
    private State state;

    /** The dummy connection. */
    private DummyConnection dummyConnection;


    protected ContinuedSearchResultEntry()
    {
    }


    /**
     * Creates a new instance of ContinuedSearchResultEntry.
     * 
     * Sets the internal state of the target connection to "resolved".
     * 
     * @param connection the connection of the continued search
     * @param dn the DN of the entry
     */
    public ContinuedSearchResultEntry( IBrowserConnection connection, DN dn )
    {
        super( connection, dn );
        this.state = State.RESOLVED;
    }


    /**
     * Sets the internal state of the target connection to "unresolved".
     * This means, when calling {@link #getAttributes()} or {@link #getChildren()}
     * the user is asked for the target connection to use. 
     * 
     * @param url the new unresolved
     */
    public void setUnresolved( LdapURL url )
    {
        this.state = State.UNRESOLVED;
        this.url = url;
        super.connectionId = null;
    }


    @Override
    public IBrowserConnection getBrowserConnection()
    {
        if ( state == State.RESOLVED )
        {
            return super.getBrowserConnection();
        }
        else
        {
            if ( dummyConnection == null )
            {
                dummyConnection = new DummyConnection( Schema.DEFAULT_SCHEMA );
            }
            return dummyConnection;
        }
    }


    @Override
    protected IEntry getDelegate()
    {
        if ( state == State.RESOLVED )
        {
            return super.getDelegate();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public State getState()
    {
        return state;
    }


    /**
     * {@inheritDoc}
     */
    public LdapURL getUrl()
    {
        return url != null ? url : super.getUrl();
    }


    /**
     * {@inheritDoc}
     */
    public void resolve()
    {
        // get referral connection, exit if canceled
        List<String> urls = new ArrayList<String>();
        urls.add( url.toString() );
        Connection referralConnection = ConnectionCorePlugin.getDefault().getReferralHandler().getReferralConnection(
            urls );
        if ( referralConnection == null )
        {
            state = State.CANCELED;
            entryDoesNotExist = true;
        }
        else
        {
            state = State.RESOLVED;
            super.connectionId = referralConnection.getId();

            InitializeAttributesRunnable iar = new InitializeAttributesRunnable( this );
            new StudioBrowserJob( iar ).execute();
        }
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return getDn().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof ContinuedSearchResultEntry ) )
        {
            return false;
        }
        ContinuedSearchResultEntry e = ( ContinuedSearchResultEntry ) o;

        // compare dn and connection
        return getDn() == null ? e.getDn() == null : ( getDn().equals( e.getDn() ) && getBrowserConnection().equals(
            e.getBrowserConnection() ) );
    }
}
