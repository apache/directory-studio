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
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.directory.shared.ldap.name.AttributeTypeAndValue;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.EntryExistsCopyStrategyDialog.EntryExistsCopyStrategy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;


/**
 * Job to copy entries asynchronously.
 * 
 * TODO: implement overwrite strategy
 * TODO: implement remember selection
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CopyEntriesJob extends AbstractNotificationJob
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
     * Creates a new instance of CopyEntriesJob.
     * 
     * @param parent the parent entry
     * @param entriesToCopy the entries to copy
     * @param scope the copy scope
     * @param dialog the dialog
     */
    public CopyEntriesJob( final IEntry parent, final IEntry[] entriesToCopy, SearchScope scope,
        EntryExistsCopyStrategyDialog dialog )
    {
        this.parent = parent;
        this.entriesToCopy = entriesToCopy;
        this.scope = scope;
        this.dialog = dialog;
        setName( entriesToCopy.length == 1 ? BrowserCoreMessages.jobs__copy_entries_name_1
            : BrowserCoreMessages.jobs__copy_entries_name_n );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { parent.getBrowserConnection().getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<IEntry> l = new ArrayList<IEntry>();
        l.add( parent );
        l.addAll( Arrays.asList( entriesToCopy ) );
        return l.toArray();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( entriesToCopy.length == 1 ? BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__copy_entries_task_1, new String[]
                { entriesToCopy[0].getDn().getUpName(), parent.getDn().getUpName() } ) : BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__copy_entries_task_n, new String[]
                { Integer.toString( entriesToCopy.length ), parent.getDn().getUpName() } ), 2 + entriesToCopy.length );

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
                    num = copyEntry( entryToCopy.getBrowserConnection(), entryToCopy.getDn(), parent.getDn(),
                        copyScope, num, dummyMonitor, monitor );
                }
                else
                {
                    monitor.reportError( BrowserCoreMessages.jobs__copy_entries_source_and_target_are_equal );
                }
            }
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        parent.setChildrenInitialized( false );
        parent.setHasChildrenHint( true );
        EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( parent ), this );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return entriesToCopy.length == 1 ? BrowserCoreMessages.jobs__copy_entries_error_1
            : BrowserCoreMessages.jobs__copy_entries_error_n;
    }


    private int copyEntry( IBrowserConnection browserConnection, LdapDN dnToCopy, LdapDN parentDn, int scope,
        int numberOfCopiedEntries, StudioProgressMonitor dummyMonitor, StudioProgressMonitor monitor )
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setCountLimit( 1 );
        searchControls.setReturningAttributes( new String[]
            { ISearch.ALL_USER_ATTRIBUTES, IAttribute.REFERRAL_ATTRIBUTE } );
        searchControls.setSearchScope( SearchControls.OBJECT_SCOPE );
        NamingEnumeration<SearchResult> result = browserConnection.getConnection().getJNDIConnectionWrapper().search(
            dnToCopy.getUpName(), ISearch.FILTER_TRUE, searchControls, "never", JNDIConnectionWrapper.REFERRAL_IGNORE,
            null, monitor, null );

        numberOfCopiedEntries = copyEntryRecursive( browserConnection, result, parentDn, scope, numberOfCopiedEntries,
            dummyMonitor, monitor );

        return numberOfCopiedEntries;
    }


    private int copyEntryRecursive( IBrowserConnection browserConnection, NamingEnumeration<SearchResult> entries,
        LdapDN parentDn, int scope, int numberOfCopiedEntries, StudioProgressMonitor dummyMonitor,
        StudioProgressMonitor monitor )
    {
        try
        {
            while ( !monitor.isCanceled() && entries.hasMore() )
            {
                SearchResult sr = entries.next();

                // compose new DN
                LdapDN oldLdapDn = JNDIUtils.getDn( sr );
                String oldDn = oldLdapDn.getUpName();
                Rdn oldRdn = oldLdapDn.getRdn();
                LdapDN newLdapDn = DnUtils.composeDn( oldRdn, parentDn );
                String newDn = newLdapDn.getUpName();

                // copy attributes
                Attributes oldAttributes = sr.getAttributes();
                Attributes newAttributes = oldAttributes;

                // create entry
                browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( newDn, newAttributes, null,
                    dummyMonitor );

                while ( dummyMonitor.errorsReported() )
                {
                    if ( dummyMonitor.getException() instanceof NameAlreadyBoundException )
                    {
                        // open dialog
                        dialog.setExistingEntry( browserConnection, newLdapDn );
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
//                                case OVERWRITE_AND_CONTINUE:
//                                    break;
                                case RENAME_AND_CONTINUE:
                                    Rdn renamedRdn = dialog.getRdn();
                                    for ( Iterator<AttributeTypeAndValue> it = oldRdn.iterator(); it.hasNext(); )
                                    {
                                        AttributeTypeAndValue atav = it.next();
                                        Attribute attribute = newAttributes.get( atav.getUpType() );
                                        if ( attribute != null )
                                        {
                                            attribute.remove( atav.getUpValue() );
                                            if ( attribute.size() == 0 )
                                            {
                                                newAttributes.remove( atav.getUpType() );
                                            }
                                        }
                                    }
                                    for ( Iterator<AttributeTypeAndValue> it = renamedRdn.iterator(); it.hasNext(); )
                                    {
                                        AttributeTypeAndValue atav = it.next();
                                        Attribute attribute = newAttributes.get( atav.getUpType() );
                                        if ( attribute == null )
                                        {
                                            attribute = new BasicAttribute( atav.getUpType() );
                                            newAttributes.put( attribute );
                                        }
                                        if ( !attribute.contains( atav.getUpValue() ) )
                                        {
                                            attribute.add( atav.getUpValue() );
                                        }
                                    }

                                    newLdapDn = DnUtils.composeDn( renamedRdn, parentDn );
                                    newDn = newLdapDn.getUpName();

                                    browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( newDn,
                                        newAttributes, null, dummyMonitor );

                                    break;
                            }
                        }
                        else
                        {
                            monitor.reportError( dummyMonitor.getException() );
                            dummyMonitor.reset();
                        }
                    }
                    else
                    {
                        monitor.reportError( dummyMonitor.getException() );
                        dummyMonitor.reset();
                    }
                }

                if ( !monitor.isCanceled() && !monitor.errorsReported() )
                {
                    numberOfCopiedEntries++;

                    // copy recursively
                    if ( scope == SearchControls.ONELEVEL_SCOPE || scope == SearchControls.SUBTREE_SCOPE )
                    {
                        SearchControls searchControls = new SearchControls();
                        searchControls.setCountLimit( 0 );
                        searchControls.setReturningAttributes( new String[]
                            { ISearch.ALL_USER_ATTRIBUTES, IAttribute.REFERRAL_ATTRIBUTE } );
                        searchControls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                        NamingEnumeration<SearchResult> childEntries = browserConnection.getConnection()
                            .getJNDIConnectionWrapper().search( oldDn, ISearch.FILTER_TRUE, searchControls, "never",
                                JNDIConnectionWrapper.REFERRAL_IGNORE, null, monitor, null );

                        if ( scope == SearchControls.ONELEVEL_SCOPE )
                        {
                            scope = SearchControls.OBJECT_SCOPE;
                        }

                        numberOfCopiedEntries = copyEntryRecursive( browserConnection, childEntries, newLdapDn, scope,
                            numberOfCopiedEntries, dummyMonitor, monitor );
                    }
                }
            }
        }
        catch ( NamingException e )
        {
            monitor.reportError( e );
        }

        return numberOfCopiedEntries;
    }

}
