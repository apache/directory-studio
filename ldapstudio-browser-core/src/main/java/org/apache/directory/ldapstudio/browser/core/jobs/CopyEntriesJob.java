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

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.ChildrenInitializedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.Entry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Search;
import org.apache.directory.ldapstudio.browser.core.internal.model.Value;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.core.model.RDN;
import org.apache.directory.ldapstudio.browser.core.model.RDNPart;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;


public class CopyEntriesJob extends AbstractAsyncBulkJob
{

    private IEntry parent;

    private IEntry[] entriesToCopy;

    private int scope;


    public CopyEntriesJob( final IEntry parent, final IEntry[] entriesToCopy, int scope )
    {
        this.parent = parent;
        this.entriesToCopy = entriesToCopy;
        this.scope = scope;
        setName( entriesToCopy.length == 1 ? BrowserCoreMessages.jobs__copy_entries_name_1
            : BrowserCoreMessages.jobs__copy_entries_name_n );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { parent.getConnection() };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( parent );
        l.addAll( Arrays.asList( entriesToCopy ) );
        return l.toArray();
    }


    protected void executeBulkJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( entriesToCopy.length == 1 ? BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__copy_entries_task_1, new String[]
                { entriesToCopy[0].getDn().toString(), parent.getDn().toString() } ) : BrowserCoreMessages.bind(
            BrowserCoreMessages.jobs__copy_entries_task_n, new String[]
                { Integer.toString( entriesToCopy.length ), parent.getDn().toString() } ), 2 + entriesToCopy.length );

        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        if ( scope == ISearch.SCOPE_OBJECT || scope == ISearch.SCOPE_ONELEVEL || scope == ISearch.SCOPE_SUBTREE )
        {
            int num = 0;
            for ( int i = 0; !monitor.isCanceled() && i < entriesToCopy.length; i++ )
            {
                IEntry entryToCopy = entriesToCopy[i];

                if ( scope == ISearch.SCOPE_OBJECT
                    || !parent.getDn().toString().endsWith( entryToCopy.getDn().toString() ) )
                {
                    num = this.copyEntryRecursive( entryToCopy, parent, scope, num, monitor );
                }
                else
                {
                    monitor.reportError( BrowserCoreMessages.jobs__copy_entries_source_and_target_are_equal );
                }
            }
        }
    }


    protected void runNotification()
    {
        EventRegistry.fireEntryUpdated( new ChildrenInitializedEvent( parent ), this );
    }


    protected String getErrorMessage()
    {
        return entriesToCopy.length == 1 ? BrowserCoreMessages.jobs__copy_entries_error_1
            : BrowserCoreMessages.jobs__copy_entries_error_n;
    }


    private int copyEntryRecursive( IEntry entryToCopy, IEntry parent, int scope, int num,
        ExtendedProgressMonitor monitor )
    {
        try
        {
            SearchParameter param = new SearchParameter();
            param.setSearchBase( entryToCopy.getDn() );
            param.setFilter( ISearch.FILTER_TRUE );
            param.setScope( ISearch.SCOPE_OBJECT );
            param.setAliasesDereferencingMethod( IConnection.DEREFERENCE_ALIASES_NEVER );
            param.setReferralsHandlingMethod( IConnection.HANDLE_REFERRALS_IGNORE );
            param.setReturningAttributes( new String[]
                { ISearch.ALL_USER_ATTRIBUTES, IAttribute.REFERRAL_ATTRIBUTE } );
            ISearch search = new Search( entryToCopy.getConnection(), param );
            entryToCopy.getConnection().search( search, monitor );

            ISearchResult[] srs = search.getSearchResults();
            if ( !monitor.isCanceled() && srs != null && srs.length == 1 )
            {
                entryToCopy = srs[0].getEntry();
                IAttribute[] attributesToCopy = entryToCopy.getAttributes();

                // create new entry
                RDN rdn = entryToCopy.getRdn();
                IEntry newEntry = new Entry( parent, rdn );

                // change RDN if entry already exists
                ExtendedProgressMonitor testMonitor = new ExtendedProgressMonitor( monitor );
                IEntry testEntry = parent.getConnection().getEntry( newEntry.getDn(), testMonitor );
                if ( testEntry != null )
                {
                    String rdnValue = rdn.getValue();
                    String newRdnValue = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, "", rdnValue ); //$NON-NLS-1$
                    RDN newRdn = getNewRdn( rdn, newRdnValue );
                    newEntry = new Entry( parent, newRdn );
                    testEntry = parent.getConnection().getEntry( newEntry.getDn(), testMonitor );
                    for ( int i = 2; testEntry != null; i++ )
                    {
                        newRdnValue = BrowserCoreMessages.bind( BrowserCoreMessages.copy_n_of_s, i + " ", rdnValue ); //$NON-NLS-1$
                        newRdn = getNewRdn( rdn, newRdnValue );
                        newEntry = new Entry( parent, newRdn );
                        testEntry = parent.getConnection().getEntry( newEntry.getDn(), testMonitor );
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
                RDN newRdn = newEntry.getRdn();
                RDNPart[] oldRdnParts = rdn.getParts();
                for ( int i = 0; i < oldRdnParts.length; i++ )
                {
                    RDNPart part = oldRdnParts[i];
                    IAttribute rdnAttribute = newEntry.getAttribute( part.getType() );
                    if ( rdnAttribute != null )
                    {
                        IValue[] values = rdnAttribute.getValues();
                        for ( int ii = 0; ii < values.length; ii++ )
                        {
                            if ( part.getUnencodedValue().equals( values[ii].getRawValue() ) )
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
                RDNPart[] newRdnParts = newRdn.getParts();
                for ( int i = 0; i < newRdnParts.length; i++ )
                {
                    RDNPart part = newRdnParts[i];
                    IAttribute rdnAttribute = newEntry.getAttribute( part.getType() );
                    if ( rdnAttribute == null )
                    {
                        rdnAttribute = new Attribute( newEntry, part.getType() );
                        newEntry.addAttribute( rdnAttribute );
                        rdnAttribute.addValue( new Value( rdnAttribute, part.getUnencodedValue() ) );
                    }
                    else
                    {
                        boolean mustAdd = true;
                        IValue[] values = rdnAttribute.getValues();
                        for ( int ii = 0; ii < values.length; ii++ )
                        {
                            if ( part.getUnencodedValue().equals( values[ii].getStringValue() ) )
                            {
                                mustAdd = false;
                                break;
                            }
                        }
                        if ( mustAdd )
                        {
                            rdnAttribute.addValue( new Value( rdnAttribute, part.getUnencodedValue() ) );
                        }
                    }
                }

                newEntry.getConnection().create( newEntry, monitor );
                newEntry.getParententry().addChild( newEntry );
                newEntry.setHasChildrenHint( false );

                num++;
                monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.model__copied_n_entries,
                    new String[]
                        { Integer.toString( num ) } ) );

                // check for children
                if ( !monitor.isCanceled() && ( scope == ISearch.SCOPE_ONELEVEL || scope == ISearch.SCOPE_SUBTREE ) )
                {

                    SearchParameter subParam = new SearchParameter();
                    subParam.setSearchBase( entryToCopy.getDn() );
                    subParam.setFilter( ISearch.FILTER_TRUE );
                    subParam.setScope( ISearch.SCOPE_ONELEVEL );
                    subParam.setReturningAttributes( ISearch.NO_ATTRIBUTES );
                    ISearch subSearch = new Search( entryToCopy.getConnection(), subParam );
                    entryToCopy.getConnection().search( subSearch, monitor );

                    ISearchResult[] subSrs = subSearch.getSearchResults();
                    if ( !monitor.isCanceled() && subSrs != null && subSrs.length > 0 )
                    {

                        for ( int i = 0; i < subSrs.length; i++ )
                        {
                            ISearchResult subSearchResult = subSrs[i];
                            IEntry childEntry = subSearchResult.getEntry();

                            if ( scope == ISearch.SCOPE_ONELEVEL )
                            {
                                num = this
                                    .copyEntryRecursive( childEntry, newEntry, ISearch.SCOPE_OBJECT, num, monitor );
                            }
                            else if ( scope == ISearch.SCOPE_SUBTREE )
                            {
                                num = this.copyEntryRecursive( childEntry, newEntry, ISearch.SCOPE_SUBTREE, num,
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


    private RDN getNewRdn( RDN rdn, String newRdnValue ) throws NameException
    {
        String[] names = rdn.getTypes();
        String[] values = rdn.getValues();
        values[0] = newRdnValue;
        RDN newRdn = new RDN( names, values, true );
        return newRdn;
    }

}
