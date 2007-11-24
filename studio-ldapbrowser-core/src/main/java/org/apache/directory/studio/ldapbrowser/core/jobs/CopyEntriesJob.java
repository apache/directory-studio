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

import javax.naming.InvalidNameException;

import org.apache.directory.shared.ldap.name.AttributeTypeAndValue;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.AliasDereferencingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Entry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;


/**
 * Job to copy entries asynchronously.
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


    /**
     * Creates a new instance of CopyEntriesJob.
     * 
     * @param parent the parent entry
     * @param entriesToCopy the entries to copy
     * @param scope the copy scope
     */
    public CopyEntriesJob( final IEntry parent, final IEntry[] entriesToCopy, SearchScope scope )
    {
        this.parent = parent;
        this.entriesToCopy = entriesToCopy;
        this.scope = scope;
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
            int num = 0;
            for ( int i = 0; !monitor.isCanceled() && i < entriesToCopy.length; i++ )
            {
                IEntry entryToCopy = entriesToCopy[i];

                if ( scope == SearchScope.OBJECT
                    || !parent.getDn().getNormName().endsWith( entryToCopy.getDn().getNormName() ) )
                {
                    num = copyEntryRecursive( entryToCopy, parent, scope, num, monitor );
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


    /**
     * Copies the entry recursive.
     * 
     * @param entryToCopy the entry to copy
     * @param parent the parent entry
     * @param scope the copy scope
     * @param num the number of copied entries
     * @param monitor the progress monitor
     * 
     * @return the number of copied entries
     */
    private int copyEntryRecursive( IEntry entryToCopy, IEntry parent, SearchScope scope, int num, StudioProgressMonitor monitor )
    {
        // TODO: use paged search
        try
        {
            // TODO: use JNDI here!!!
            SearchParameter param = new SearchParameter();
            param.setSearchBase( entryToCopy.getDn() );
            param.setFilter( ISearch.FILTER_TRUE );
            param.setScope( SearchScope.OBJECT );
            param.setAliasesDereferencingMethod( AliasDereferencingMethod.NEVER );
            param.setReferralsHandlingMethod( ReferralHandlingMethod.IGNORE );
            param.setReturningAttributes( new String[]
                { ISearch.ALL_USER_ATTRIBUTES, IAttribute.REFERRAL_ATTRIBUTE } );
            ISearch search = new Search( entryToCopy.getBrowserConnection(), param );
            
            SearchJob.searchAndUpdateModel( entryToCopy.getBrowserConnection(), search, monitor );

            ISearchResult[] srs = search.getSearchResults();
            if ( !monitor.isCanceled() && srs != null && srs.length == 1 )
            {
                entryToCopy = srs[0].getEntry();
                IAttribute[] attributesToCopy = entryToCopy.getAttributes();

                // create new entry
                Rdn rdn = entryToCopy.getRdn();
                IEntry newEntry = new Entry( parent, rdn );

                // change RDN if entry already exists
                StudioProgressMonitor testMonitor = new StudioProgressMonitor( monitor );
                IEntry testEntry = ReadEntryJob.getEntry( parent.getBrowserConnection(), newEntry.getDn(), testMonitor );
                if ( testEntry != null )
                {
                    Object rdnValue = rdn.getUpValue();
                    String newRdnValue = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, "", rdnValue ); //$NON-NLS-1$
                    Rdn newRdn = getNewRdn( rdn, newRdnValue );
                    newEntry = new Entry( parent, newRdn );
                    testEntry = ReadEntryJob.getEntry( parent.getBrowserConnection(), newEntry.getDn(), testMonitor );
                    for ( int i = 2; testEntry != null; i++ )
                    {
                        newRdnValue = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, i + " ", rdnValue ); //$NON-NLS-1$
                        newRdn = getNewRdn( rdn, newRdnValue );
                        newEntry = new Entry( parent, newRdn );
                        testEntry = ReadEntryJob.getEntry( parent.getBrowserConnection(), newEntry.getDn(), testMonitor );
                    }
                }

                // copy attributes
                for ( int i = 0; i < attributesToCopy.length; i++ )
                {
                    IAttribute attributeToCopy = attributesToCopy[i];

                    if ( SchemaUtils.isModifyable( attributeToCopy.getAttributeTypeDescription() )
                        || IAttribute.REFERRAL_ATTRIBUTE.equalsIgnoreCase( attributeToCopy.getDescription() ) )
                    {
                        IAttribute newAttribute = new Attribute( newEntry, attributeToCopy.getDescription() );
                        newEntry.addAttribute( newAttribute );
                        IValue[] valuesToCopy = attributeToCopy.getValues();
                        for ( int j = 0; j < valuesToCopy.length; j++ )
                        {
                            IValue valueToCopy = valuesToCopy[j];
                            IValue newValue = new Value( newAttribute, valueToCopy.getRawValue() );
                            newAttribute.addValue( newValue );
                        }
                    }
                }

                // check if RDN attributes ar present
                Rdn newRdn = newEntry.getRdn();
                Iterator<AttributeTypeAndValue> atavIterator = newRdn.iterator();
                while(atavIterator.hasNext())
                {
                    AttributeTypeAndValue atav = atavIterator.next();
                    IAttribute rdnAttribute = newEntry.getAttribute( atav.getUpType() );
                    if ( rdnAttribute != null )
                    {
                        IValue[] values = rdnAttribute.getValues();
                        for ( int ii = 0; ii < values.length; ii++ )
                        {
                            if ( atav.getUpValue().equals( values[ii].getRawValue() ) )
                            {
                                rdnAttribute.deleteValue( values[ii] );
                            }
                            if ( rdnAttribute.getValueSize() == 0 )
                            {
                                newEntry.deleteAttribute( rdnAttribute );
                            }
                        }
                    }
                }
                atavIterator = newRdn.iterator();
                while(atavIterator.hasNext())
                {
                    AttributeTypeAndValue atav = atavIterator.next();
                    IAttribute rdnAttribute = newEntry.getAttribute( atav.getUpType() );
                    if ( rdnAttribute == null )
                    {
                        rdnAttribute = new Attribute( newEntry, atav.getUpType() );
                        newEntry.addAttribute( rdnAttribute );
                        rdnAttribute.addValue( new Value( rdnAttribute, atav.getUpValue() ) );
                    }
                    else
                    {
                        boolean mustAdd = true;
                        IValue[] values = rdnAttribute.getValues();
                        for ( int ii = 0; ii < values.length; ii++ )
                        {
                            if ( atav.getUpValue().equals( values[ii].getStringValue() ) )
                            {
                                mustAdd = false;
                                break;
                            }
                        }
                        if ( mustAdd )
                        {
                            rdnAttribute.addValue( new Value( rdnAttribute, atav.getUpValue() ) );
                        }
                    }
                }

                CreateEntryJob.createEntry( newEntry.getBrowserConnection(), newEntry, monitor );
                newEntry.setHasChildrenHint( false );

                num++;
                monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.model__copied_n_entries,
                    new String[]
                        { Integer.toString( num ) } ) );

                // check for children
                if ( !monitor.isCanceled() && ( scope == SearchScope.ONELEVEL || scope == SearchScope.SUBTREE ) )
                {
                    // TODO: use JNDI here!!!
                    SearchParameter subParam = new SearchParameter();
                    subParam.setSearchBase( entryToCopy.getDn() );
                    subParam.setFilter( ISearch.FILTER_TRUE );
                    subParam.setScope( SearchScope.ONELEVEL );
                    subParam.setReturningAttributes( ISearch.NO_ATTRIBUTES );
                    ISearch subSearch = new Search( entryToCopy.getBrowserConnection(), subParam );
                    SearchJob.searchAndUpdateModel( entryToCopy.getBrowserConnection(), subSearch, monitor );

                    ISearchResult[] subSrs = subSearch.getSearchResults();
                    if ( !monitor.isCanceled() && subSrs != null && subSrs.length > 0 )
                    {
                        for ( int i = 0; i < subSrs.length; i++ )
                        {
                            ISearchResult subSearchResult = subSrs[i];
                            IEntry childEntry = subSearchResult.getEntry();

                            if ( scope == SearchScope.ONELEVEL )
                            {
                                num = this
                                    .copyEntryRecursive( childEntry, newEntry,SearchScope.OBJECT, num, monitor );
                            }
                            else if ( scope == SearchScope.SUBTREE )
                            {
                                num = this.copyEntryRecursive( childEntry, newEntry, SearchScope.SUBTREE, num,
                                    monitor );
                            }
                        }

                    }
                }
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
        return num;
    }


    /**
     * Gets the new rdn.
     * 
     * @param rdn the rdn
     * @param newRdnValue the new rdn value
     * 
     * @return the new rdn
     * @throws InvalidNameException 
     */
    private Rdn getNewRdn( Rdn rdn, String newRdnValue ) throws InvalidNameException
    {
        String[] rdnTypes = new String[rdn.size()];
        String[] rdnValues = new String[rdn.size()];
        int i = 0;
        Iterator<AttributeTypeAndValue> atavIterator = rdn.iterator();
        while(atavIterator.hasNext())
        {
            AttributeTypeAndValue atav = atavIterator.next();
            rdnTypes[i] = atav.getUpType();
            rdnValues[i] = ( String ) atav.getUpValue();
            i++;
        }
        rdnValues[0] = newRdnValue;
        Rdn newRdn = DnUtils.composeRdn( rdnTypes, rdnValues );
        return newRdn;
    }

}
