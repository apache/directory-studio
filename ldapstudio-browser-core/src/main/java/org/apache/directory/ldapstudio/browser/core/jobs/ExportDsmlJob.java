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


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.SearchParameter;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapAndFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapNotFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapOrFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterToken;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.request.SearchRequestDsml;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.AttributeValueAssertion;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.search.AndFilter;
import org.apache.directory.shared.ldap.codec.search.AttributeValueAssertionFilter;
import org.apache.directory.shared.ldap.codec.search.Filter;
import org.apache.directory.shared.ldap.codec.search.NotFilter;
import org.apache.directory.shared.ldap.codec.search.OrFilter;
import org.apache.directory.shared.ldap.codec.search.PresentFilter;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;
import org.apache.directory.shared.ldap.message.ScopeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * This class implements a Job for Exporting a part of a LDAP Server into a DSML File.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportDsmlJob extends AbstractEclipseJob
{
    /** The name of the DSML file to export to */
    private String exportDsmlFilename;

    /** The connection to use */
    private IConnection connection;

    /** The Search Parameter of the export*/
    private SearchParameter searchParameter;


    /**
     * Creates a new instance of ExportDsmlJob.
     *
     * @param exportDsmlFilename
     *          the name of the DSML file to export to
     * @param connection
     *          the connection to use
     * @param searchParameter
     *          the Search Parameter of the export
     */
    public ExportDsmlJob( String exportDsmlFilename, IConnection connection, SearchParameter searchParameter )
    {
        this.exportDsmlFilename = exportDsmlFilename;
        this.connection = connection;
        this.searchParameter = searchParameter;

        setName( BrowserCoreMessages.jobs__export_dsml_name );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { connection };
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<String> l = new ArrayList<String>();
        l.add( connection.getUrl() + "_" + DigestUtils.shaHex( exportDsmlFilename ) );
        return l.toArray();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.core.jobs.AbstractEclipseJob#executeAsyncJob(org.apache.directory.ldapstudio.browser.core.jobs.ExtendedProgressMonitor)
     */
    protected void executeAsyncJob( ExtendedProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__export_dsml_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setProtocolOP( searchRequest );

            // DN
            searchRequest.setBaseObject( new LdapDN( searchParameter.getSearchBase().toString() ) );

            // Scope
            int scope = searchParameter.getScope();
            if ( scope == ISearch.SCOPE_OBJECT )
            {
                searchRequest.setScope( ScopeEnum.BASE_OBJECT );
            }
            else if ( scope == ISearch.SCOPE_ONELEVEL )
            {
                searchRequest.setScope( ScopeEnum.SINGLE_LEVEL );
            }
            else if ( scope == ISearch.SCOPE_SUBTREE )
            {
                searchRequest.setScope( ScopeEnum.WHOLE_SUBTREE );
            }

            // DerefAliases
            int derefAliases = searchParameter.getAliasesDereferencingMethod();
            if ( derefAliases == IConnection.DEREFERENCE_ALIASES_ALWAYS )
            {
                searchRequest.setDerefAliases( LdapConstants.DEREF_ALWAYS );
            }
            else if ( derefAliases == IConnection.DEREFERENCE_ALIASES_FINDING )
            {
                searchRequest.setDerefAliases( LdapConstants.DEREF_FINDING_BASE_OBJ );
            }
            else if ( derefAliases == IConnection.DEREFERENCE_ALIASES_NEVER )
            {
                searchRequest.setDerefAliases( LdapConstants.NEVER_DEREF_ALIASES );
            }
            else if ( derefAliases == IConnection.DEREFERENCE_ALIASES_SEARCH )
            {
                searchRequest.setDerefAliases( LdapConstants.DEREF_IN_SEARCHING );
            }

            // Time Limit
            int timeLimit = searchParameter.getTimeLimit();
            if ( timeLimit != 0 )
            {
                searchRequest.setTimeLimit( timeLimit );
            }

            // Size Limit
            int countLimit = searchParameter.getCountLimit();
            if ( countLimit != 0 )
            {
                searchRequest.setSizeLimit( countLimit );
            }

            // Filter
            //            PresentFilter presentFilter = new PresentFilter();
            //            presentFilter.setAttributeDescription( "objectclass" );
            searchRequest.setFilter( convertToSharedLdapFilter( searchParameter.getFilter() ) );

            // Attributes
            String[] returningAttributes = searchParameter.getReturningAttributes();
            for ( int i = 0; i < returningAttributes.length; i++ )
            {
                searchRequest.addAttribute( returningAttributes[i] );
            }

            // Executing the request
            Document xmlRequest = DocumentHelper.createDocument();
            Element rootElement = xmlRequest.addElement( "batchRequest" );
            SearchRequestDsml searchRequestDsml = new SearchRequestDsml( searchRequest );
            searchRequestDsml.toDsml( rootElement );
            Dsmlv2Engine engine = new Dsmlv2Engine( connection.getHost(), connection.getPort(), connection
                .getBindPrincipal(), connection.getBindPassword() );
            String response = engine.processDSML( xmlRequest.asXML() );

            // Saving the response
            FileOutputStream fout = new FileOutputStream( exportDsmlFilename );
            new PrintStream( fout ).println( response );
            fout.close();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_dsml_error;
    }


    /**
     * Converts a String filter into a Shared LDAP Filter.
     *
     * @param filter
     *      the filter String to convert
     * @return
     *      the corresponding Shared LDAP Filter
     * @throws DecoderException
     */
    public static Filter convertToSharedLdapFilter( String filter ) throws DecoderException
    {
        LdapFilterParser ldapFilterParser = new LdapFilterParser();

        ldapFilterParser.parse( filter );

        return convertToSharedLdapFilter( ldapFilterParser.getModel() );
    }


    /**
     * Converts a Browser Core Filter Model into a Shared LDAP Model.
     *
     * @param filter
     *      the filter
     * @return
     *      the corresponding filter in the Shared LDAP Model
     * @throws DecoderException
     */
    public static Filter convertToSharedLdapFilter( LdapFilter filter ) throws DecoderException
    {
        Filter sharedLdapFilter = null;

        LdapFilterComponent filterComponent = filter.getFilterComponent();
        if ( filterComponent instanceof LdapAndFilterComponent )
        {
            LdapAndFilterComponent andFilterComponent = ( LdapAndFilterComponent ) filterComponent;

            AndFilter andFilter = new AndFilter();
            sharedLdapFilter = andFilter;

            Filter[] filters = iterateOnFilters( andFilterComponent.getFilters() );
            for ( int i = 0; i < filters.length; i++ )
            {
                andFilter.addFilter( filters[i] );
            }
        }
        else if ( filterComponent instanceof LdapOrFilterComponent )
        {
            LdapOrFilterComponent orFilterComponent = ( LdapOrFilterComponent ) filterComponent;

            OrFilter orFilter = new OrFilter();
            sharedLdapFilter = orFilter;

            Filter[] filters = iterateOnFilters( orFilterComponent.getFilters() );
            for ( int i = 0; i < filters.length; i++ )
            {
                orFilter.addFilter( filters[i] );
            }
        }
        else if ( filterComponent instanceof LdapNotFilterComponent )
        {
            LdapNotFilterComponent notFilterComponent = ( LdapNotFilterComponent ) filterComponent;

            NotFilter notFilter = new NotFilter();
            sharedLdapFilter = notFilter;

            Filter[] filters = iterateOnFilters( notFilterComponent.getFilters() );
            notFilter.setNotFilter( filters[0] );
        }
        else if ( filterComponent instanceof LdapFilterItemComponent )
        {
            LdapFilterItemComponent filterItemComponent = ( LdapFilterItemComponent ) filterComponent;

            int filterType = filterItemComponent.getFilterToken().getType();
            if ( filterType == LdapFilterToken.EQUAL )
            {
                AttributeValueAssertionFilter avaFilter = createAttributeValueAssertionFilter( filterItemComponent,
                    LdapConstants.EQUALITY_MATCH_FILTER );
                sharedLdapFilter = avaFilter;
            }
            else if ( filterType == LdapFilterToken.GREATER )
            {
                AttributeValueAssertionFilter avaFilter = createAttributeValueAssertionFilter( filterItemComponent,
                    LdapConstants.GREATER_OR_EQUAL_FILTER );
                sharedLdapFilter = avaFilter;
            }
            else if ( filterType == LdapFilterToken.LESS )
            {
                AttributeValueAssertionFilter avaFilter = createAttributeValueAssertionFilter( filterItemComponent,
                    LdapConstants.LESS_OR_EQUAL_FILTER );
                sharedLdapFilter = avaFilter;
            }
            else if ( filterType == LdapFilterToken.APROX )
            {
                AttributeValueAssertionFilter avaFilter = createAttributeValueAssertionFilter( filterItemComponent,
                    LdapConstants.APPROX_MATCH_FILTER );
                sharedLdapFilter = avaFilter;
            }
            else if ( filterType == LdapFilterToken.PRESENT )
            {
                PresentFilter presentFilter = new PresentFilter();
                sharedLdapFilter = presentFilter;

                presentFilter.setAttributeDescription( filterItemComponent.getAttributeToken().getValue() );
            }
        }

        return sharedLdapFilter;
    }


    /**
     * Iterates the conversion on the given array of Ldap Filters.
     *
     * @param filters
     *      the array of Ldap Filters to convert
     * @return
     *      an array containing the conversion for each Ldap Filter into its Shared LDAP Model
     * @throws DecoderException
     */
    private static Filter[] iterateOnFilters( LdapFilter[] filters ) throws DecoderException
    {
        List<Filter> filtersList = new ArrayList<Filter>();

        for ( int c = 0; c < filters.length; c++ )
        {
            filtersList.add( convertToSharedLdapFilter( filters[c] ) );
        }

        return filtersList.toArray( new Filter[0] );
    }


    /**
     * Create and returns an Attribute Value Assertion Filter from the given LdapFilterItemComponent ant the given type.
     *
     * @param filter
     *      the filter to convert
     * @param type
     *      the type of the Attribute Value Assertion Filter
     * @return
     *      the corresponding Attribute Value Assertion Filter
     */
    private static AttributeValueAssertionFilter createAttributeValueAssertionFilter( LdapFilterItemComponent filter,
        int type )
    {
        AttributeValueAssertionFilter avaFilter = new AttributeValueAssertionFilter( type );

        AttributeValueAssertion assertion = new AttributeValueAssertion();
        avaFilter.setAssertion( assertion );
        assertion.setAttributeDesc( filter.getAttributeToken().getValue() );
        assertion.setAssertionValue( filter.getValueToken().getValue() );

        return avaFilter;
    }
}
