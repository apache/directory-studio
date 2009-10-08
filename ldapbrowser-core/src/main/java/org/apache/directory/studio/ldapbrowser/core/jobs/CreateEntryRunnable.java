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


import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioBulkRunnableWithProgress;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EntryAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * Runnable to create an entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CreateEntryRunnable implements StudioBulkRunnableWithProgress
{

    /** The entry to create. */
    private IEntry entryToCreate;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The created entry. */
    private IEntry createdEntry;


    /**
     * Creates a new instance of CreateEntryRunnable.
     * 
     * @param entryToCreate the entry to create
     * @param browserConnection the browser connection
     */
    public CreateEntryRunnable( IEntry entryToCreate, IBrowserConnection browserConnection )
    {
        this.entryToCreate = entryToCreate;
        this.browserConnection = browserConnection;
        this.createdEntry = null;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__create_entry_name_1;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection };
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__create_entry_task_1, new String[]
            { entryToCreate.getDn().getUpName() } ), 2 + 1 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        createEntry( browserConnection, entryToCreate, monitor );

        if ( !monitor.errorsReported() )
        {
            createdEntry = ReadEntryRunnable.getEntry( browserConnection, entryToCreate.getDn(), monitor );
            createdEntry.setHasChildrenHint( false );

            // set some flags at the parent
            if ( createdEntry.hasParententry() )
            {
                if ( createdEntry.isAlias() )
                {
                    createdEntry.getParententry().setFetchAliases( true );
                }
                if ( createdEntry.isReferral() )
                {
                    createdEntry.getParententry().setFetchReferrals( true );
                }
                if ( createdEntry.isSubentry() )
                {
                    createdEntry.getParententry().setFetchSubentries( true );
                }
            }
        }

        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        if ( createdEntry != null )
        {
            EventRegistry.fireEntryUpdated( new EntryAddedEvent( browserConnection, createdEntry ), this );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__create_entry_error_1;
    }


    /**
     * Creates the entry using the underlying JNDI connection wrapper.
     * 
     * @param browserConnection the browser connection
     * @param entryToCreate the entry to create
     * @param monitor the monitor
     */
    static void createEntry( IBrowserConnection browserConnection, IEntry entryToCreate, StudioProgressMonitor monitor )
    {
        // dn
        String dn = entryToCreate.getDn().getUpName();

        // attributes
        Attributes jndiAttributes = new BasicAttributes();
        IAttribute[] attributes = entryToCreate.getAttributes();
        for ( int i = 0; i < attributes.length; i++ )
        {
            String description = attributes[i].getDescription();
            IValue[] values = attributes[i].getValues();
            for ( int ii = 0; ii < values.length; ii++ )
            {
                IValue value = values[ii];
                Object rawValue = value.getRawValue();
                if ( jndiAttributes.get( description ) != null )
                {
                    jndiAttributes.get( description ).add( rawValue );
                }
                else
                {
                    jndiAttributes.put( description, rawValue );
                }
            }
        }

        // determine referrals handling method
        ReferralHandlingMethod referralsHandlingMethod = entryToCreate.isReferral() ? ReferralHandlingMethod.MANAGE
            : ReferralHandlingMethod.FOLLOW;

        browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( dn, jndiAttributes,
            referralsHandlingMethod, null, monitor, null );
    }

}
