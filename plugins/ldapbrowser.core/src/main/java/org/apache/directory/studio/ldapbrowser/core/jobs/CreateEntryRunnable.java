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


import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
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
 */
public class CreateEntryRunnable implements StudioConnectionBulkRunnableWithProgress
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
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__create_entry_error_1;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__create_entry_task_1, new String[]
            { entryToCreate.getDn().getName() } ), 2 + 1 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        createEntry( browserConnection, entryToCreate, monitor );

        if ( !monitor.errorsReported() && !monitor.isCanceled() )
        {
            List<StudioControl> controls = new ArrayList<StudioControl>();
            if ( entryToCreate.isReferral() )
            {
                controls.add( StudioControl.MANAGEDSAIT_CONTROL );
            }

            // Here we try to read the created entry to be able to send the right event notification.
            // In some cases this don't work:
            // - if there was a referral and the entry was created on another (master) server and not yet sync'ed to the current server
            // So we use a dummy monitor to no bother the user with an error message.
            StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
            createdEntry = ReadEntryRunnable
                .getEntry( browserConnection, entryToCreate.getDn(), controls, dummyMonitor );
            dummyMonitor.done();
            if ( createdEntry != null )
            {
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
     * Creates the entry using the underlying connection wrapper.
     * 
     * @param browserConnection the browser connection
     * @param entryToCreate the entry to create
     * @param monitor the monitor
     */
    static void createEntry( IBrowserConnection browserConnection, IEntry entryToCreate, StudioProgressMonitor monitor )
    {
        // dn
        String dn = entryToCreate.getDn().getName();

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

        // ManageDsaIT control
        Control[] controls = null;
        if ( entryToCreate.isReferral() )
        {
            controls = new Control[]
                { new ManageReferralControl( false ) };
        }

        browserConnection.getConnection().getConnectionWrapper()
            .createEntry( dn, jndiAttributes, controls, monitor, null );
    }
}
