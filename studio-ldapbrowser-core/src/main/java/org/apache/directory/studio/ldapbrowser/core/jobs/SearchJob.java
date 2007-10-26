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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IRootDSE;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.ReferralException;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BaseDNEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Entry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldapbrowser.core.model.schema.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;


/**
 * Job to perform search operations. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchJob extends AbstractNotificationJob
{

    /** The searches. */
    private ISearch[] searches;


    /**
     * Creates a new instance of SearchJob.
     * 
     * @param searches the searches
     */
    public SearchJob( ISearch[] searches )
    {
        this.searches = searches;
        setName( BrowserCoreMessages.jobs__search_name );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        Connection[] connections = new Connection[searches.length];
        for ( int i = 0; i < connections.length; i++ )
        {
            connections[i] = searches[i].getBrowserConnection().getConnection();
        }
        return connections;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.addAll( Arrays.asList( searches ) );
        return l.toArray();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( " ", searches.length + 1 ); //$NON-NLS-1$
        monitor.reportProgress( " " ); //$NON-NLS-1$

        for ( int pi = 0; pi < searches.length; pi++ )
        {
            ISearch search = searches[pi];

            monitor.setTaskName( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__search_task, new String[]
                { search.getName() } ) );
            monitor.worked( 1 );

            if ( search.getBrowserConnection() != null )
            {
                searchAndUpdateModel( search.getBrowserConnection(), search, monitor );
            }
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        for ( int pi = 0; pi < searches.length; pi++ )
        {
            EventRegistry.fireSearchUpdated( new SearchUpdateEvent( searches[pi],
                SearchUpdateEvent.EventDetail.SEARCH_PERFORMED ), this );
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return searches.length == 1 ? BrowserCoreMessages.jobs__search_error_1
            : BrowserCoreMessages.jobs__search_error_n;
    }


    /**
     * Searches the directory and updates the browser model.
     * 
     * @param browserConnection the browser connection
     * @param search the search
     * @param monitor the progress monitor
     */
    public static void searchAndUpdateModel( IBrowserConnection browserConnection, ISearch search,
        StudioProgressMonitor monitor )
    {
        try
        {
            if ( !monitor.isCanceled() )
            {
                // add returning attributes for children and alias detection
                SearchParameter searchParameter = getSearchParameter( search );
                ArrayList<ISearchResult> searchResultList = new ArrayList<ISearchResult>();

                try
                {
                    // search
                    NamingEnumeration<SearchResult> enumeration = search( browserConnection, searchParameter, monitor );

                    // iterate through the search result
                    while ( !monitor.isCanceled() && enumeration.hasMore() )
                    {
                        SearchResult sr = enumeration.nextElement(); // TODO: referrals exception
                        DN dn = JNDIUtils.getDn( sr );

                        // get entry from cache or create it
                        IEntry entry = browserConnection.getEntryFromCache( dn );
                        if ( entry == null )
                        {
                            entry = createAndCacheEntry( browserConnection, dn );
                        }

                        // initialize special flags
                        initFlags( entry, sr, searchParameter );

                        // fill the attributes
                        fillAttributes( entry, sr, search.getSearchParameter() );

                        searchResultList
                            .add( new org.apache.directory.studio.ldapbrowser.core.model.impl.SearchResult( entry,
                                search ) );

                        monitor
                            .reportProgress( searchResultList.size() == 1 ? BrowserCoreMessages.model__retrieved_1_entry
                                : BrowserCoreMessages.bind( BrowserCoreMessages.model__retrieved_n_entries,
                                    new String[]
                                        { Integer.toString( searchResultList.size() ) } ) );
                    }
                }
                catch ( Exception e )
                {
                    ConnectionException ce = JNDIUtils.createConnectionException( searchParameter, e );

                    if ( ce.getLdapStatusCode() == 3 || ce.getLdapStatusCode() == 4 || ce.getLdapStatusCode() == 11 )
                    {
                        search.setCountLimitExceeded( true );
                    }
                    else if ( ce instanceof ReferralException )
                    {
                        if ( search.getReferralsHandlingMethod() == ReferralHandlingMethod.FOLLOW )
                        {
                            ReferralException re = ( ReferralException ) ce;
                            ISearch[] referralSearches = re.getReferralSearches();
                            for ( int i = 0; i < referralSearches.length; i++ )
                            {
                                ISearch referralSearch = referralSearches[i];

                                searchAndUpdateModel( referralSearch.getBrowserConnection(), referralSearch, monitor );

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


    static NamingEnumeration<SearchResult> search( IBrowserConnection browserConnection, SearchParameter parameter,
        StudioProgressMonitor monitor )
    {
        String searchBase = parameter.getSearchBase().toString();
        SearchControls controls = new SearchControls();
        switch ( parameter.getScope() )
        {
            case OBJECT:
                controls.setSearchScope( SearchControls.OBJECT_SCOPE );
                break;
            case ONELEVEL:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                break;
            case SUBTREE:
                controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
                break;
            default:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        }
        controls.setReturningAttributes( parameter.getReturningAttributes() );
        controls.setCountLimit( parameter.getCountLimit() );
        controls.setTimeLimit( parameter.getTimeLimit() );
        String filter = parameter.getFilter();
        String derefAliasMethod = getDerefAliasMethod( parameter );
        String handleReferralsMethod = getReferralsHandlingMethod( parameter );

        Control[] ldapControls = null;
        if ( parameter.getControls() != null )
        {
            org.apache.directory.studio.ldapbrowser.core.model.Control[] ctls = parameter.getControls();
            ldapControls = new Control[ctls.length];
            for ( int i = 0; i < ctls.length; i++ )
            {
                ldapControls[i] = new BasicControl( ctls[i].getOid(), ctls[i].isCritical(), ctls[i].getControlValue() );
            }
        }

        NamingEnumeration<SearchResult> result = browserConnection.getConnection().getJNDIConnectionWrapper().search(
            searchBase, filter, controls, derefAliasMethod, handleReferralsMethod, ldapControls, monitor );
        return result;
    }


    private static String getDerefAliasMethod( SearchParameter parameter )
    {
        String m = "always"; //$NON-NLS-1$

        switch ( parameter.getAliasesDereferencingMethod() )
        {
            case NEVER:
                m = "never"; //$NON-NLS-1$
                break;
            case ALWAYS:
                m = "always"; //$NON-NLS-1$
                break;
            case FINDING:
                m = "finding"; //$NON-NLS-1$
                break;
            case SEARCH:
                m = "searching"; //$NON-NLS-1$
                break;
        }

        return m;
    }


    private static String getReferralsHandlingMethod( SearchParameter parameter )
    {
        String m = "follow"; //$NON-NLS-1$

        switch ( parameter.getReferralsHandlingMethod() )
        {
            case IGNORE:
                m = "ignore"; //$NON-NLS-1$
                break;
            case FOLLOW:
                // m = "follow"; //$NON-NLS-1$
                m = "throw"; //$NON-NLS-1$
                break;
        }

        return m;
    }


    private static SearchParameter getSearchParameter( ISearch search )
    {
        SearchParameter searchParameter = ( SearchParameter ) search.getSearchParameter().clone();

        // add children detetion attributes
        if ( search.isInitHasChildrenFlag() )
        {
            if ( search.getBrowserConnection().getSchema().hasAttributeTypeDescription(
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
            else if ( search.getBrowserConnection().getSchema().hasAttributeTypeDescription(
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
            else if ( search.getBrowserConnection().getSchema().hasAttributeTypeDescription(
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

        // if returning attributes are requested but objectClass isn't included then add it
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
            IBrowserConnection connection = search.getBrowserConnection();
            Set<String> suppportedConrolSet = new HashSet<String>();
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

            org.apache.directory.studio.ldapbrowser.core.model.Control[] controls = searchParameter.getControls();
            List<org.apache.directory.studio.ldapbrowser.core.model.Control> controlList = new ArrayList<org.apache.directory.studio.ldapbrowser.core.model.Control>();
            for ( int i = 0; i < controls.length; i++ )
            {
                if ( suppportedConrolSet.contains( controls[i].getOid().toLowerCase() ) )
                {
                    controlList.add( controls[i] );
                }
            }
            searchParameter.setControls( controlList
                .toArray( new org.apache.directory.studio.ldapbrowser.core.model.Control[controlList.size()] ) );
        }

        return searchParameter;
    }


    /**
     * Creates the entry and puts it into the BrowserConnection's entry cache.
     * 
     * @param browserConnection the browser connection
     * @param dn the DN of the entry
     * 
     * @return the created entry
     */
    private static IEntry createAndCacheEntry( IBrowserConnection browserConnection, DN dn )
    {
        IEntry entry = null;

        // build tree to parent
        LinkedList<DN> parentDnList = new LinkedList<DN>();
        DN parentDN = dn;
        while ( parentDN != null && browserConnection.getEntryFromCache( parentDN ) == null )
        {
            parentDnList.addFirst( parentDN );
            parentDN = parentDN.getParentDn();
        }
        for ( DN aDN : parentDnList )
        {
            if ( aDN.getParentDn() == null )
            {
                entry = new BaseDNEntry( aDN, browserConnection );
                browserConnection.cacheEntry( entry );
            }
            else if ( browserConnection.getEntryFromCache( aDN.getParentDn() ) != null )
            {
                IEntry parentEntry = browserConnection.getEntryFromCache( aDN.getParentDn() );
                entry = new Entry( parentEntry, aDN.getRdn() );
                entry.setDirectoryEntry( true );

                parentEntry.addChild( entry );
                // parentEntry.setAttributesInitialized(false, this);

                parentEntry.setChildrenInitialized( true );
                parentEntry.setHasMoreChildren( true );
                parentEntry.setHasChildrenHint( true );

                browserConnection.cacheEntry( entry );
            }
            else
            {
                // ??
            }
        }

        return entry;
    }


    /**
     * Initializes the following flags of the entry:
     * <ul>
     * <li>hasChildren</li>
     * <li>isAlias</li>
     * <li>isReferral</li>
     * <li>isSubentry</li>
     * </ul>
     * 
     * @param entry the entry
     * @param sr the the JNDI search result
     * @param searchParameter the search parameters
     */
    private static void initFlags( IEntry entry, SearchResult sr, SearchParameter searchParameter )
        throws NamingException
    {
        NamingEnumeration<? extends Attribute> attributeEnumeration = sr.getAttributes().getAll();
        while ( attributeEnumeration.hasMore() )
        {
            Attribute attribute = attributeEnumeration.next();
            String attributeDescription = attribute.getID();
            NamingEnumeration<?> valueEnumeration = attribute.getAll();
            while ( valueEnumeration.hasMore() )
            {
                Object o = valueEnumeration.next();
                if ( o instanceof String )
                {
                    String value = ( String ) o;

                    if ( searchParameter.isInitHasChildrenFlag() )
                    {
                        // hasChildren flag
                        if ( IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( "FALSE".equalsIgnoreCase( value ) ) { //$NON-NLS-1$
                                entry.setHasChildrenHint( false );
                            }
                        }
                        if ( IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( "0".equalsIgnoreCase( value ) ) { //$NON-NLS-1$
                                entry.setHasChildrenHint( false );
                            }
                        }
                        if ( IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( "0".equalsIgnoreCase( value ) ) { //$NON-NLS-1$
                                entry.setHasChildrenHint( false );
                            }
                        }
                    }

                    if ( searchParameter.isInitAliasAndReferralFlag() )
                    {
                        if ( IAttribute.OBJECTCLASS_ATTRIBUTE.equalsIgnoreCase( attributeDescription ) )
                        {
                            if ( ObjectClassDescription.OC_ALIAS.equalsIgnoreCase( value ) )
                            {
                                entry.setAlias( true );
                            }
                            if ( ObjectClassDescription.OC_REFERRAL.equalsIgnoreCase( value ) )
                            {
                                entry.setReferral( true );
                            }
                        }
                    }
                }
            }
        }

        if ( ( searchParameter.getControls() != null && Arrays.asList( searchParameter.getControls() ).contains(
            org.apache.directory.studio.ldapbrowser.core.model.Control.SUBENTRIES_CONTROL ) )
            || ISearch.FILTER_SUBENTRY.equalsIgnoreCase( searchParameter.getFilter() ) )
        {
            entry.setSubentry( true );
            entry.setHasChildrenHint( false );
        }
    }


    /**
     * Fills the attributes and values of the search result into the entry.
     * Clears existing attributes and values in the entry.
     * 
     * @param entry the entry
     * @param sr the JNDI search result
     * @param searchParameter the search parameters
     */
    private static void fillAttributes( IEntry entry, SearchResult sr, SearchParameter searchParameter )
        throws NamingException
    {
        if ( searchParameter.getReturningAttributes() == null || searchParameter.getReturningAttributes().length > 0 )
        {
            // clear old attributes defined as returning attributes or clear all
            if ( searchParameter.getReturningAttributes() != null )
            {
                String[] ras = searchParameter.getReturningAttributes();

                // special case *
                if ( Arrays.asList( ras ).contains( ISearch.ALL_USER_ATTRIBUTES ) )
                {
                    // clear all user attributes
                    IAttribute[] oldAttributes = entry.getAttributes();
                    for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                    {
                        if ( !oldAttributes[i].isOperationalAttribute() )
                        {
                            entry.deleteAttribute( oldAttributes[i] );
                        }
                    }
                }

                // special case +
                if ( Arrays.asList( ras ).contains( ISearch.ALL_OPERATIONAL_ATTRIBUTES ) )
                {
                    // clear all operational attributes
                    IAttribute[] oldAttributes = entry.getAttributes();
                    for ( int i = 0; oldAttributes != null && i < oldAttributes.length; i++ )
                    {
                        if ( oldAttributes[i].isOperationalAttribute() )
                        {
                            entry.deleteAttribute( oldAttributes[i] );
                        }
                    }
                }

                for ( int r = 0; r < ras.length; r++ )
                {
                    // clear attributes requested from server, also include sub-types
                    AttributeHierarchy ah = entry.getAttributeWithSubtypes( ras[r] );
                    if ( ah != null )
                    {
                        for ( Iterator<IAttribute> it = ah.iterator(); it.hasNext(); )
                        {
                            IAttribute attribute = it.next();
                            entry.deleteAttribute( attribute );
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
                    entry.deleteAttribute( oldAttributes[i] );
                }
            }

            // additional clear old attributes if the record contains the attribute
            NamingEnumeration<? extends Attribute> attributeEnumeration = sr.getAttributes().getAll();
            while ( attributeEnumeration.hasMore() )
            {
                Attribute attribute = attributeEnumeration.next();
                String attributeDescription = attribute.getID();
                IAttribute oldAttribute = entry.getAttribute( attributeDescription );
                if ( oldAttribute != null )
                {
                    entry.deleteAttribute( oldAttribute );
                }
            }

            // set new attributes and values
            attributeEnumeration = sr.getAttributes().getAll();
            while ( attributeEnumeration.hasMore() )
            {
                Attribute attribute = attributeEnumeration.next();
                String attributeDescription = attribute.getID();

                IAttribute studioAttribute = null;
                if ( entry.getAttribute( attributeDescription ) == null )
                {
                    studioAttribute = new org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute( entry,
                        attributeDescription );
                    entry.addAttribute( studioAttribute );
                }
                else
                {
                    studioAttribute = entry.getAttribute( attributeDescription );
                }

                NamingEnumeration<?> valueEnumeration = attribute.getAll();
                while ( valueEnumeration.hasMore() )
                {
                    Object value = valueEnumeration.next();
                    studioAttribute.addValue( new Value( studioAttribute, value ) );
                }
            }
        }
    }

}
