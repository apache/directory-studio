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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.jobs.ExtendedProgressMonitor;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.Control;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IRootDSE;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifEnumeration;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.ldapstudio.browser.core.model.schema.ObjectClassDescription;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;


public class ConnectionSearchHandler
{

    private Connection connection;


    ConnectionSearchHandler( Connection connection )
    {
        this.connection = connection;
    }


    void connectionClosed()
    {
    }


    void connectionOpened()
    {
    }


    boolean existsEntry( DN dn, ExtendedProgressMonitor monitor )
    {
        SearchParameter sp = new SearchParameter();
        sp.setSearchBase( dn );
        sp.setFilter( ISearch.FILTER_TRUE );
        sp.setScope( ISearch.SCOPE_OBJECT );
        sp.setReturningAttributes( ISearch.NO_ATTRIBUTES );

        try
        {
            LdifEnumeration le = connection.connectionProvider.search( sp, monitor );
            return le.hasNext( monitor );
        }
        catch ( ConnectionException e )
        {
            monitor.reportError( e );
            return false;
        }
    }


    IEntry getEntry( DN dn, ExtendedProgressMonitor monitor )
    {
        try
        {
            // first check cache
            IEntry entry = connection.getEntryFromCache( dn );
            if ( entry != null )
            {
                return entry;
            }

            // search in directory
            ISearch search = new Search( null, connection, dn, null, ISearch.NO_ATTRIBUTES, ISearch.SCOPE_OBJECT, 1, 0,
                IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.HANDLE_REFERRALS_IGNORE, true, true, null );
            this.search( search, monitor );
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


    void search( ISearch search, ExtendedProgressMonitor monitor )
    {

        try
        {

            if ( !monitor.isCanceled() )
            {

                // add returning attributes for children and alias detection
                SearchParameter searchParameter = getSearchParameter( search );
                ArrayList searchResultList = new ArrayList();

                try
                {

                    // search
                    LdifEnumeration enumeration = connection.connectionProvider.search( searchParameter, monitor );

                    // iterate through the search result
                    while ( !monitor.isCanceled() && enumeration.hasNext( monitor ) )
                    {
                        LdifContainer container = enumeration.next( monitor );
                        if ( container instanceof LdifContentRecord )
                        {

                            LdifContentRecord record = ( LdifContentRecord ) container;
                            DN dn = new DN( record.getDnLine().getValueAsString() );

                            // get entry from cache or create it
                            IEntry entry = connection.getEntryFromCache( dn );
                            if ( entry == null )
                            {
                                entry = createAndCacheEntry( dn );
                            }

                            // initialize special flags
                            initFlags( entry, record, searchParameter );

                            // fill the attributes
                            fillAttributes( entry, record, searchParameter );

                            searchResultList
                                .add( new org.apache.directory.ldapstudio.browser.core.internal.model.SearchResult(
                                    entry, search ) );

                            monitor
                                .reportProgress( searchResultList.size() == 1 ? BrowserCoreMessages.model__retrieved_1_entry
                                    : BrowserCoreMessages.bind( BrowserCoreMessages.model__retrieved_n_entries,
                                        new String[]
                                            { Integer.toString( searchResultList.size() ) } ) );
                        }
                    }
                }
                catch ( ConnectionException ce )
                {

                    if ( ce.getLdapStatusCode() == 3 || ce.getLdapStatusCode() == 4 || ce.getLdapStatusCode() == 11 )
                    {
                        search.setCountLimitExceeded( true );
                    }
                    else if ( ce instanceof ReferralException )
                    {

                        if ( search.getReferralsHandlingMethod() == IConnection.HANDLE_REFERRALS_FOLLOW )
                        {

                            ReferralException re = ( ReferralException ) ce;
                            ISearch[] referralSearches = re.getReferralSearches();
                            for ( int i = 0; i < referralSearches.length; i++ )
                            {
                                ISearch referralSearch = referralSearches[i];

                                // open connection
                                if ( !referralSearch.getConnection().isOpened() )
                                {
                                    referralSearch.getConnection().open( monitor );
                                }

                                referralSearch.getConnection().search( referralSearch, monitor );

                                ISearchResult[] referralSearchResults = referralSearch.getSearchResults();
                                for ( int j = 0; referralSearchResults != null && j < referralSearchResults.length; j++ )
                                {
                                    ISearchResult referralSearchResult = referralSearchResults[j];
                                    referralSearchResult.setSearch( search );
                                    searchResultList.add( referralSearchResult );
                                }
                            }
                        }
                    }
                    else
                    {
                        monitor.reportError( ce );
                    }
                }

                monitor.reportProgress( searchResultList.size() == 1 ? BrowserCoreMessages.model__retrieved_1_entry
                    : BrowserCoreMessages.bind( BrowserCoreMessages.model__retrieved_n_entries, new String[]
                        { Integer.toString( searchResultList.size() ) } ) );
                monitor.worked( 1 );

                search.setSearchResults( ( ISearchResult[] ) searchResultList
                    .toArray( new ISearchResult[searchResultList.size()] ) );
            }
        }
        catch ( Exception e )
        {
            if ( search != null )
            {
                search.setSearchResults( new ISearchResult[0] );
            }
            monitor.reportError( e );
        }
    }


    private IEntry createAndCacheEntry( DN dn ) throws ModelModificationException
    {
        IEntry entry = null;

        // build tree to parent
        LinkedList parentDnList = new LinkedList();
        DN parentDN = dn;
        while ( parentDN != null && connection.getEntryFromCache( parentDN ) == null )
        {
            parentDnList.addFirst( parentDN );
            parentDN = parentDN.getParentDn();
        }
        for ( Iterator it = parentDnList.iterator(); it.hasNext(); )
        {
            DN aDN = ( DN ) it.next();
            if ( aDN.getParentDn() == null )
            {
                entry = new BaseDNEntry( aDN, connection );
                connection.cacheEntry( entry );
            }
            else if ( connection.getEntryFromCache( aDN.getParentDn() ) != null )
            {
                IEntry parentEntry = connection.getEntryFromCache( aDN.getParentDn() );
                entry = new Entry( parentEntry, aDN.getRdn() );
                entry.setDirectoryEntry( true );

                parentEntry.addChild( entry );
                // parentEntry.setAttributesInitialized(false, this);

                parentEntry.setChildrenInitialized( true );
                parentEntry.setHasMoreChildren( true );
                parentEntry.setHasChildrenHint( true );

                connection.cacheEntry( entry );
            }
            else
            {
                // ??
            }
        }

        return entry;
    }


    private void initFlags( IEntry entry, LdifContentRecord record, SearchParameter search )
    {

        LdifAttrValLine[] lines = record.getAttrVals();
        for ( int i = 0; i < lines.length; i++ )
        {

            String attributeName = lines[i].getUnfoldedAttributeDescription();

            if ( search.isInitHasChildrenFlag() )
            {
                // hasChildren flag
                if ( IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES.equalsIgnoreCase( attributeName ) )
                {
                    if ( "FALSE".equalsIgnoreCase( lines[i].getValueAsString() ) ) { //$NON-NLS-1$
                        entry.setHasChildrenHint( false );
                    }
                }
                if ( IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES.equalsIgnoreCase( attributeName ) )
                {
                    if ( "0".equalsIgnoreCase( lines[i].getValueAsString() ) ) { //$NON-NLS-1$
                        entry.setHasChildrenHint( false );
                    }
                }
                if ( IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT.equalsIgnoreCase( attributeName ) )
                {
                    if ( "0".equalsIgnoreCase( lines[i].getValueAsString() ) ) { //$NON-NLS-1$
                        entry.setHasChildrenHint( false );
                    }
                }
            }

            if ( search.isInitAliasAndReferralFlag() )
            {
                if ( IAttribute.OBJECTCLASS_ATTRIBUTE.equalsIgnoreCase( attributeName ) )
                {
                    if ( ObjectClassDescription.OC_ALIAS.equalsIgnoreCase( lines[i].getValueAsString() ) )
                    {
                        entry.setAlias( true );
                    }
                    if ( ObjectClassDescription.OC_REFERRAL.equalsIgnoreCase( lines[i].getValueAsString() ) )
                    {
                        entry.setReferral( true );
                    }
                }
            }

        }

        if ( search.getControls() != null
            && Arrays.asList( search.getControls() ).contains( Control.SUBENTRIES_CONTROL ) )
        {
            entry.setSubentry( true );
            entry.setHasChildrenHint( false );
        }

    }


    private SearchParameter getSearchParameter( ISearch search )
    {
        SearchParameter searchParameter = ( SearchParameter ) search.getSearchParameter().clone();

        // add children detetion attributes
        if ( search.isInitHasChildrenFlag() )
        {
            if ( search.getConnection().getSchema().hasAttributeTypeDescription(
                IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES )
                && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                    IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0, searchParameter
                    .getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES;
                searchParameter.setReturningAttributes( returningAttributes );
            }
            else if ( search.getConnection().getSchema().hasAttributeTypeDescription(
                IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES )
                && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                    IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0, searchParameter
                    .getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES;
                searchParameter.setReturningAttributes( returningAttributes );
            }
            else if ( search.getConnection().getSchema().hasAttributeTypeDescription(
                IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT )
                && !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                    IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0, searchParameter
                    .getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT;
                searchParameter.setReturningAttributes( returningAttributes );
            }
        }

        // to init the alias/referral flag we need the objectClass
        if ( search.isInitAliasAndReferralFlag() )
        {
            if ( !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                IAttribute.OBJECTCLASS_ATTRIBUTE ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0, searchParameter
                    .getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = IAttribute.OBJECTCLASS_ATTRIBUTE;
                searchParameter.setReturningAttributes( returningAttributes );
            }
        }

        // if returning attributes are requested but objectClass isn't included
        // then add it
        if ( search.getReturningAttributes() == null || search.getReturningAttributes().length > 0 )
        {
            if ( !Utils.containsIgnoreCase( Arrays.asList( searchParameter.getReturningAttributes() ),
                IAttribute.OBJECTCLASS_ATTRIBUTE ) )
            {
                String[] returningAttributes = new String[searchParameter.getReturningAttributes().length + 1];
                System.arraycopy( searchParameter.getReturningAttributes(), 0, returningAttributes, 0, searchParameter
                    .getReturningAttributes().length );
                returningAttributes[returningAttributes.length - 1] = IAttribute.OBJECTCLASS_ATTRIBUTE;
                searchParameter.setReturningAttributes( returningAttributes );
            }
        }

        // filter controls if not supported by server
        if ( searchParameter.getControls() != null )
        {

            Set suppportedConrolSet = new HashSet();
            if ( connection.getRootDSE() != null
                && connection.getRootDSE().getAttribute( IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL ) != null )
            {
                IAttribute scAttribute = connection.getRootDSE().getAttribute(
                    IRootDSE.ROOTDSE_ATTRIBUTE_SUPPORTEDCONTROL );
                String[] supportedControls = scAttribute.getStringValues();
                for ( int i = 0; i < supportedControls.length; i++ )
                {
                    suppportedConrolSet.add( supportedControls[i].toLowerCase() );
                }
            }

            Control[] controls = searchParameter.getControls();
            List controlList = new ArrayList();
            for ( int i = 0; i < controls.length; i++ )
            {
                if ( suppportedConrolSet.contains( controls[i].getOid().toLowerCase() ) )
                {
                    controlList.add( controls[i] );
                }
            }
            searchParameter.setControls( ( Control[] ) controlList.toArray( new Control[controlList.size()] ) );
        }

        return searchParameter;
    }


    private void fillAttributes( IEntry entry, LdifContentRecord record, SearchParameter search )
        throws ModelModificationException
    {

        if ( search.getReturningAttributes() == null || search.getReturningAttributes().length > 0 )
        {
            LdifAttrValLine[] lines = record.getAttrVals();

            // clear old attributes defined as returing attributes or all
            if ( search.getReturningAttributes() != null )
            {
                String[] ras = search.getReturningAttributes();
                
                // special case *
                if( Arrays.asList( ras ).contains( ISearch.ALL_USER_ATTRIBUTES ) )
                {
                    // clear all user attributes
                    IAttribute[] oldAttributes = entry.getAttributes();
                    for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                    {
                        if( !oldAttributes[i].isOperationalAttribute() )
                        {
                            try
                            {
                                entry.deleteAttribute( oldAttributes[i] );
                            }
                            catch ( ModelModificationException e )
                            {
                            }
                        }
                    }
                }
                
                // special case +
                if( Arrays.asList( ras ).contains( ISearch.ALL_OPERATIONAL_ATTRIBUTES ) )
                {
                    // clear all operational attributes
                    IAttribute[] oldAttributes = entry.getAttributes();
                    for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                    {
                        if( oldAttributes[i].isOperationalAttribute() )
                        {
                            try
                            {
                                entry.deleteAttribute( oldAttributes[i] );
                            }
                            catch ( ModelModificationException e )
                            {
                            }
                        }
                    }
                }
                
                
                for ( int r = 0; r < ras.length; r++ )
                {
                    // clear attributes requested from server, also include subtypes
                    AttributeHierarchy ah = entry.getAttributeWithSubtypes( ras[r] );
                    if ( ah != null )
                    {
                        for ( Iterator it = ah.iterator(); it.hasNext(); )
                        {
                            IAttribute attribute = ( IAttribute ) it.next();
                            try
                            {
                                entry.deleteAttribute( attribute );
                            }
                            catch ( ModelModificationException e )
                            {
                            }
                        }
                    }
                }
            }
            else
            {
                // clear all
                IAttribute[] oldAttributes = entry.getAttributes();
                for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                {
                    try
                    {
                        entry.deleteAttribute( oldAttributes[i] );
                    }
                    catch ( ModelModificationException e )
                    {
                    }
                }
            }


            // additional clear old attributes if the record contains the
            // attribute
            for ( int i = 0; i < lines.length; i++ )
            {
                String attributeDesc = lines[i].getUnfoldedAttributeDescription();
                IAttribute oldAttribute = entry.getAttribute( attributeDesc );
                if ( oldAttribute != null )
                {
                    try
                    {
                        entry.deleteAttribute( oldAttribute );
                    }
                    catch ( ModelModificationException mme )
                    {
                    }
                }
            }

            // set new attributes and values
            for ( int i = 0; i < lines.length; i++ )
            {

                IAttribute attribute = null;
                String attributeName = lines[i].getUnfoldedAttributeDescription();
                // attributeName = attributeName.replaceAll(";binary", "");
                if ( entry.getAttribute( attributeName ) == null )
                {
                    attribute = new Attribute( entry, attributeName );
                    entry.addAttribute( attribute );
                }
                else
                {
                    attribute = entry.getAttribute( attributeName );
                }

                attribute.addValue( new Value( attribute, lines[i].getValueAsObject() ) );
            }
        }
    }

}
