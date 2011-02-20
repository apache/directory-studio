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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ManageReferralControl;

import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.name.Ava;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.EntryExistsCopyStrategyDialog.EntryExistsCopyStrategy;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;


/**
 * Runnable to copy entries asynchronously.
 * 
 * TODO: implement overwrite strategy
 * TODO: implement remember selection
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CopyEntriesRunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The parent entry. */
    private IEntry parent;

    /** The entries to copy. */
    private IEntry[] entriesToCopy;

    /** The copy scope */
    private SearchScope scope;

    /** The dialog to ask for the strategy */
    private EntryExistsCopyStrategyDialog dialog;


    /**
     * Creates a new instance of CopyEntriesRunnable.
     * 
     * @param parent the parent entry
     * @param entriesToCopy the entries to copy
     * @param scope the copy scope
     * @param dialog the dialog
     */
    public CopyEntriesRunnable( final IEntry parent, final IEntry[] entriesToCopy, SearchScope scope,
        EntryExistsCopyStrategyDialog dialog )
    {
        this.parent = parent;
        this.entriesToCopy = entriesToCopy;
        this.scope = scope;
        this.dialog = dialog;
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return new Connection[]
            { parent.getBrowserConnection().getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return entriesToCopy.length == 1 ? BrowserCoreMessages.jobs__copy_entries_name_1
            : BrowserCoreMessages.jobs__copy_entries_name_n;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<IEntry> l = new ArrayList<IEntry>();
        l.add( parent );
        l.addAll( Arrays.asList( entriesToCopy ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return entriesToCopy.length == 1 ? BrowserCoreMessages.jobs__copy_entries_error_1
            : BrowserCoreMessages.jobs__copy_entries_error_n;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask(
            entriesToCopy.length == 1 ? BrowserCoreMessages.bind( BrowserCoreMessages.jobs__copy_entries_task_1,
                new String[]
                    { entriesToCopy[0].getDn().getName(), parent.getDn().getName() } ) : BrowserCoreMessages.bind(
                BrowserCoreMessages.jobs__copy_entries_task_n, new String[]
                    { Integer.toString( entriesToCopy.length ), parent.getDn().getName() } ),
            2 + entriesToCopy.length );

        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        if ( scope == SearchScope.OBJECT || scope == SearchScope.ONELEVEL || scope == SearchScope.SUBTREE )
        {
            StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
            int copyScope = scope == SearchScope.SUBTREE ? SearchControls.SUBTREE_SCOPE
                : scope == SearchScope.ONELEVEL ? SearchControls.ONELEVEL_SCOPE : SearchControls.OBJECT_SCOPE;

            int num = 0;
            for ( int i = 0; !monitor.isCanceled() && i < entriesToCopy.length; i++ )
            {
                IEntry entryToCopy = entriesToCopy[i];

                if ( scope == SearchScope.OBJECT
                    || !parent.getDn().getNormName().endsWith( entryToCopy.getDn().getNormName() ) )
                {
                    dummyMonitor.reset();
                    num = copyEntry( entryToCopy, parent, null, copyScope, num, dialog, dummyMonitor, monitor );
                }
                else
                {
                    monitor.reportError( BrowserCoreMessages.jobs__copy_entries_source_and_target_are_equal );
                }
            }

            parent.setChildrenInitialized( false );
            parent.setHasChildrenHint( true );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        // don't fire an EntryCreatedEvent for each created entry
        // that would cause massive UI updates
        // instead we fire a BulkModificationEvent
        EventRegistry.fireEntryUpdated( new BulkModificationEvent( parent.getBrowserConnection() ), this );
    }


    /**
     * Copy entry. If scope is SearchControls.SUBTREE_SCOPE the entry is copied
     * recursively.
     * 
     * @param browserConnection the browser connection
     * @param dnToCopy the Dn to copy
     * @param parentDn the parent Dn
     * @param newRdn the new Rdn, if null the Rdn of dnToCopy is used
     * @param scope the copy scope
     * @param numberOfCopiedEntries the number of copied entries
     * @param dialog the dialog to ask for the copy strategy, if null the user won't be
     *        asked instead the NameAlreadyBoundException it reported to the monitor
     * @param dummyMonitor the dummy monitor, used for I/O that causes exceptions that 
     *        should be handled
     * @param monitor the real monitor
     * 
     * @return the number of copied entries
     */
    static int copyEntry( IEntry entryToCopy, IEntry parent, Rdn newRdn, int scope, int numberOfCopiedEntries,
        EntryExistsCopyStrategyDialog dialog, StudioProgressMonitor dummyMonitor, StudioProgressMonitor monitor )
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setCountLimit( 1 );
        searchControls.setReturningAttributes( new String[]
            { SchemaConstants.ALL_USER_ATTRIBUTES, SchemaConstants.REF_AT } );
        searchControls.setSearchScope( SearchControls.OBJECT_SCOPE );

        // ManageDsaIT control
        Control[] controls = null;
        if ( entryToCopy.isReferral() )
        {
            controls = new Control[]
                { new ManageReferralControl( false ) };
        }

        NamingEnumeration<SearchResult> result = entryToCopy
            .getBrowserConnection()
            .getConnection()
            .getConnectionWrapper()
            .search( entryToCopy.getDn().getName(), ISearch.FILTER_TRUE, searchControls,
                AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, controls, monitor, null );

        // In case the parent is the RootDSE: use the parent Dn of the old entry
        Dn parentDn = parent.getDn();
        if ( parentDn.isEmpty() )
        {
            parentDn = entryToCopy.getDn().getParent();
        }
        numberOfCopiedEntries = copyEntryRecursive( entryToCopy.getBrowserConnection(), result,
            parent.getBrowserConnection(), parentDn, newRdn, scope, numberOfCopiedEntries, dialog, dummyMonitor,
            monitor );

        return numberOfCopiedEntries;
    }


    /**
     * Copy the entries. If scope is SearchControls.SUBTREE_SCOPE the entries are copied
     * recursively.
     * 
     * @param sourceBrowserConnection the source browser connection
     * @param entries the source entries to copy
     * @param targetBrowserConnection the target browser connection
     * @param parentDn the target parent Dn
     * @param newRdn the new Rdn, if null the original Rdn of each entry is used
     * @param scope the copy scope
     * @param numberOfCopiedEntries the number of copied entries
     * @param dialog the dialog to ask for the copy strategy, if null the user won't be
     *        asked instead the NameAlreadyBoundException it reported to the monitor
     * @param dummyMonitor the dummy monitor, used for I/O that causes exceptions that 
     *        should be handled
     * @param monitor the real monitor
     * 
     * @return the number of copied entries
     */
    static int copyEntryRecursive( IBrowserConnection sourceBrowserConnection, NamingEnumeration<SearchResult> entries,
        IBrowserConnection targetBrowserConnection, Dn parentDn, Rdn forceNewRdn, int scope,
        int numberOfCopiedEntries, EntryExistsCopyStrategyDialog dialog, StudioProgressMonitor dummyMonitor,
        StudioProgressMonitor monitor )
    {
        try
        {
            while ( !monitor.isCanceled() && entries.hasMore() )
            {
                // get next entry to copy
                SearchResult sr = entries.next();
                Dn oldLdapDn = JNDIUtils.getDn( sr );
                Rdn oldRdn = oldLdapDn.getRdn();

                // reuse attributes of the entry to copy
                Attributes newAttributes = sr.getAttributes();

                // compose new Dn
                Rdn newRdn = oldLdapDn.getRdn();
                if ( forceNewRdn != null )
                {
                    newRdn = forceNewRdn;
                }
                Dn newLdapDn = parentDn.add( newRdn );

                // apply new Rdn to the attributes
                applyNewRdn( newAttributes, oldRdn, newRdn );

                // ManageDsaIT control
                Control[] controls = null;
                if ( newAttributes.get( SchemaConstants.OBJECT_CLASS_AT ) != null
                    && newAttributes.get( SchemaConstants.OBJECT_CLASS_AT ).contains( SchemaConstants.REFERRAL_OC ) )
                {
                    controls = new Control[]
                        { new ManageReferralControl( false ) };
                }

                // create entry
                targetBrowserConnection.getConnection().getConnectionWrapper()
                    .createEntry( newLdapDn.getName(), newAttributes, controls, dummyMonitor, null );

                while ( dummyMonitor.errorsReported() )
                {
                    if ( dialog != null && dummyMonitor.getException() instanceof NameAlreadyBoundException )
                    {
                        // open dialog
                        dialog.setExistingEntry( targetBrowserConnection, newLdapDn );
                        dialog.open();
                        EntryExistsCopyStrategy strategy = dialog.getStrategy();
                        //                        boolean rememberSelection = dialog.isRememberSelection();
                        if ( strategy != null )
                        {
                            dummyMonitor.reset();

                            switch ( strategy )
                            {
                                case BREAK:
                                    monitor.setCanceled( true );
                                    break;

                                case IGNORE_AND_CONTINUE:
                                    break;

                                case OVERWRITE_AND_CONTINUE:
                                    // create modification items
                                    List<ModificationItem> mis = new ArrayList<ModificationItem>();
                                    NamingEnumeration<? extends Attribute> all = newAttributes.getAll();
                                    while ( all.hasMore() )
                                    {
                                        Attribute attribute = all.next();
                                        ModificationItem mi = new ModificationItem( DirContext.REPLACE_ATTRIBUTE,
                                            attribute );
                                        mis.add( mi );
                                    }

                                    // modify entry
                                    targetBrowserConnection
                                        .getConnection()
                                        .getConnectionWrapper()
                                        .modifyEntry( newLdapDn.getName(),
                                            mis.toArray( new ModificationItem[mis.size()] ), null, dummyMonitor, null );

                                    // force reloading of attributes
                                    IEntry newEntry = targetBrowserConnection.getEntryFromCache( newLdapDn );
                                    if ( newEntry != null )
                                    {
                                        newEntry.setAttributesInitialized( false );
                                    }

                                    break;

                                case RENAME_AND_CONTINUE:
                                    Rdn renamedRdn = dialog.getRdn();

                                    // apply renamed Rdn to the attributes
                                    applyNewRdn( newAttributes, newRdn, renamedRdn );

                                    // compose new Dn
                                    newLdapDn = parentDn.add( renamedRdn );

                                    // create entry
                                    targetBrowserConnection.getConnection().getConnectionWrapper()
                                        .createEntry( newLdapDn.getName(), newAttributes, null, dummyMonitor, null );

                                    break;
                            }
                        }
                        else
                        {
                            monitor.reportError( dummyMonitor.getException() );
                            break;
                        }
                    }
                    else
                    {
                        monitor.reportError( dummyMonitor.getException() );
                        break;
                    }
                }

                if ( !monitor.isCanceled() && !monitor.errorsReported() )
                {
                    numberOfCopiedEntries++;

                    monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.model__copied_n_entries,
                        new String[]
                            { "" + numberOfCopiedEntries } ) ); //$NON-NLS-1$

                    // copy recursively
                    if ( scope == SearchControls.ONELEVEL_SCOPE || scope == SearchControls.SUBTREE_SCOPE )
                    {
                        SearchControls searchControls = new SearchControls();
                        searchControls.setCountLimit( 0 );
                        searchControls.setReturningAttributes( new String[]
                            { SchemaConstants.ALL_USER_ATTRIBUTES, SchemaConstants.REF_AT } );
                        searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                        NamingEnumeration<SearchResult> childEntries = sourceBrowserConnection
                            .getConnection()
                            .getConnectionWrapper()
                            .search( oldLdapDn.getName(), ISearch.FILTER_TRUE, searchControls,
                                AliasDereferencingMethod.NEVER, ReferralHandlingMethod.IGNORE, null, monitor, null );

                        if ( scope == SearchControls.ONELEVEL_SCOPE )
                        {
                            scope = SearchControls.OBJECT_SCOPE;
                        }

                        numberOfCopiedEntries = copyEntryRecursive( sourceBrowserConnection, childEntries,
                            targetBrowserConnection, newLdapDn, null, scope, numberOfCopiedEntries, dialog,
                            dummyMonitor, monitor );
                    }
                }
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        return numberOfCopiedEntries;
    }


    private static void applyNewRdn( Attributes attributes, Rdn oldRdn, Rdn newRdn )
    {
        // remove old Rdn attributes and values
        for ( Iterator<Ava> it = oldRdn.iterator(); it.hasNext(); )
        {
            Ava atav = it.next();
            Attribute attribute = attributes.get( atav.getUpType() );
            if ( attribute != null )
            {
                attribute.remove( atav.getNormValue().getString() );
                if ( attribute.size() == 0 )
                {
                    attributes.remove( atav.getUpType() );
                }
            }
        }

        // add new Rdn attributes and values
        for ( Iterator<Ava> it = newRdn.iterator(); it.hasNext(); )
        {
            Ava atav = it.next();
            Attribute attribute = attributes.get( atav.getUpType() );
            if ( attribute == null )
            {
                attribute = new BasicAttribute( atav.getUpType() );
                attributes.put( attribute );
            }
            if ( !attribute.contains( atav.getNormValue().getString() ) )
            {
                attribute.add( atav.getNormValue().getString() );
            }
        }
    }
}
