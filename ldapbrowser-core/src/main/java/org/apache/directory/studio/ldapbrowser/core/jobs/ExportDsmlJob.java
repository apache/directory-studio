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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.codec.AttributeValueAssertion;
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.search.AndFilter;
import org.apache.directory.shared.ldap.codec.search.AttributeValueAssertionFilter;
import org.apache.directory.shared.ldap.codec.search.ExtensibleMatchFilter;
import org.apache.directory.shared.ldap.codec.search.Filter;
import org.apache.directory.shared.ldap.codec.search.NotFilter;
import org.apache.directory.shared.ldap.codec.search.OrFilter;
import org.apache.directory.shared.ldap.codec.search.PresentFilter;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;
import org.apache.directory.shared.ldap.codec.search.SubstringFilter;
import org.apache.directory.shared.ldap.filter.AndNode;
import org.apache.directory.shared.ldap.filter.ApproximateNode;
import org.apache.directory.shared.ldap.filter.BranchNode;
import org.apache.directory.shared.ldap.filter.EqualityNode;
import org.apache.directory.shared.ldap.filter.ExprNode;
import org.apache.directory.shared.ldap.filter.ExtensibleNode;
import org.apache.directory.shared.ldap.filter.FilterParser;
import org.apache.directory.shared.ldap.filter.GreaterEqNode;
import org.apache.directory.shared.ldap.filter.LessEqNode;
import org.apache.directory.shared.ldap.filter.NotNode;
import org.apache.directory.shared.ldap.filter.OrNode;
import org.apache.directory.shared.ldap.filter.PresenceNode;
import org.apache.directory.shared.ldap.filter.SimpleNode;
import org.apache.directory.shared.ldap.filter.SubstringNode;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.studio.dsmlv2.request.SearchRequestDsml;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
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
    private IBrowserConnection browserConnection;

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
    public ExportDsmlJob( String exportDsmlFilename, IBrowserConnection connection, SearchParameter searchParameter )
    {
        this.exportDsmlFilename = exportDsmlFilename;
        this.browserConnection = connection;
        this.searchParameter = searchParameter;

        setName( BrowserCoreMessages.jobs__export_dsml_name );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<String> l = new ArrayList<String>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( exportDsmlFilename ) );
        return l.toArray();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#executeAsyncJob(org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor)
     */
    protected void executeAsyncJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__export_dsml_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setProtocolOP( searchRequest );

            // DN
            searchRequest.setBaseObject( searchParameter.getSearchBase( ) );

            // Scope
            SearchScope scope = searchParameter.getScope();
            if ( scope == SearchScope.OBJECT )
            {
                searchRequest.setScope( org.apache.directory.shared.ldap.filter.SearchScope.OBJECT );
            }
            else if ( scope == SearchScope.ONELEVEL )
            {
                searchRequest.setScope( org.apache.directory.shared.ldap.filter.SearchScope.ONELEVEL );
            }
            else if ( scope == SearchScope.SUBTREE )
            {
                searchRequest.setScope( org.apache.directory.shared.ldap.filter.SearchScope.SUBTREE );
            }

            // DerefAliases
            Connection.AliasDereferencingMethod derefAliases = searchParameter.getAliasesDereferencingMethod();
            switch ( derefAliases )
            {
                case ALWAYS:
                    searchRequest.setDerefAliases( LdapConstants.DEREF_ALWAYS );
                    break;
                case FINDING:
                    searchRequest.setDerefAliases( LdapConstants.DEREF_FINDING_BASE_OBJ );
                    break;
                case NEVER:
                    searchRequest.setDerefAliases( LdapConstants.NEVER_DEREF_ALIASES );
                    break;
                case SEARCH:
                    searchRequest.setDerefAliases( LdapConstants.DEREF_IN_SEARCHING );
                    break;
                default:
                    break;
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
            searchRequest.setFilter( convertToSharedLdapFilter( searchParameter.getFilter() ) );

            // Attributes
            String[] returningAttributes = searchParameter.getReturningAttributes();
            for ( int i = 0; i < returningAttributes.length; i++ )
            {
                searchRequest.addAttribute( returningAttributes[i] );
            }

            // Controls
            List<org.apache.directory.shared.ldap.codec.Control> sharedLdapControls = convertToSharedLdapControls( searchParameter
                .getControls() );
            for ( int i = 0; i < sharedLdapControls.size(); i++ )
            {
                searchRequest.addControl( sharedLdapControls.get( i ) );
            }

            // Executing the request
            Document xmlRequest = DocumentHelper.createDocument();
            Element rootElement = xmlRequest.addElement( "batchRequest" );
            SearchRequestDsml searchRequestDsml = new SearchRequestDsml( searchRequest );
            searchRequestDsml.toDsml( rootElement );
            Dsmlv2Engine engine = new Dsmlv2Engine( browserConnection.getConnection().getHost(), browserConnection
                .getConnection().getPort(), browserConnection.getConnection().getBindPrincipal(), browserConnection
                .getConnection().getBindPassword() );
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
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
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
     * @throws ParseException 
     * @throws IOException 
     * @throws DecoderException 
     */
    public static Filter convertToSharedLdapFilter( String filter ) throws IOException, ParseException,
        DecoderException
    {
        ExprNode exprNode = FilterParser.parse( filter );
        return convertToSharedLdapFilter( exprNode );
    }


    /**
     * Converts a ExprNode Filter Model into a Shared LDAP Model.
     *
     * @param exprNode
     *      the filter
     * @return
     *      the corresponding filter in the Shared LDAP Model
     * @throws DecoderException 
     */
    public static Filter convertToSharedLdapFilter( ExprNode exprNode ) throws DecoderException
    {
        Filter sharedLdapFilter = null;

        if ( exprNode instanceof BranchNode )
        {
            BranchNode branchNode = ( BranchNode ) exprNode;

            if ( branchNode instanceof AndNode )
            {
                AndFilter andFilter = new AndFilter();
                sharedLdapFilter = andFilter;

                List<Filter> andFilters = iterateOnFilters( branchNode.getChildren() );
                for ( int i = 0; i < andFilters.size(); i++ )
                {
                    andFilter.addFilter( andFilters.get( i ) );
                }
            }
            else if ( branchNode instanceof OrNode )
            {
                OrFilter orFilter = new OrFilter();
                sharedLdapFilter = orFilter;

                List<Filter> orFilters = iterateOnFilters( branchNode.getChildren() );
                for ( int i = 0; i < orFilters.size(); i++ )
                {
                    orFilter.addFilter( orFilters.get( i ) );
                }
            }
            else if ( branchNode instanceof NotNode )
            {
                NotFilter notFilter = new NotFilter();
                sharedLdapFilter = notFilter;

                List<Filter> notFilters = iterateOnFilters( branchNode.getChildren() );
                notFilter.setNotFilter( notFilters.get( 0 ) );
            }
        }
        else if ( exprNode instanceof PresenceNode )
        {
            PresenceNode presenceNode = ( PresenceNode ) exprNode;

            PresentFilter presentFilter = new PresentFilter();
            sharedLdapFilter = presentFilter;

            presentFilter.setAttributeDescription( presenceNode.getAttribute() );
        }
        else if ( exprNode instanceof SimpleNode )
        {
            SimpleNode simpleNode = ( SimpleNode ) exprNode;

            if ( simpleNode instanceof ApproximateNode )
            {
                AttributeValueAssertionFilter approxMatchFilter = createAttributeValueAssertionFilter( simpleNode,
                    LdapConstants.APPROX_MATCH_FILTER );
                sharedLdapFilter = approxMatchFilter;
            }
            else if ( simpleNode instanceof EqualityNode )
            {
                AttributeValueAssertionFilter equalityMatchFilter = createAttributeValueAssertionFilter( simpleNode,
                    LdapConstants.EQUALITY_MATCH_FILTER );
                sharedLdapFilter = equalityMatchFilter;
            }
            else if ( simpleNode instanceof GreaterEqNode )
            {
                AttributeValueAssertionFilter greaterOrEqualFilter = createAttributeValueAssertionFilter( simpleNode,
                    LdapConstants.GREATER_OR_EQUAL_FILTER );
                sharedLdapFilter = greaterOrEqualFilter;
            }
            else if ( simpleNode instanceof LessEqNode )
            {
                AttributeValueAssertionFilter lessOrEqualFilter = createAttributeValueAssertionFilter( simpleNode,
                    LdapConstants.LESS_OR_EQUAL_FILTER );
                sharedLdapFilter = lessOrEqualFilter;
            }
        }
        else if ( exprNode instanceof ExtensibleNode )
        {
            ExtensibleNode extensibleNode = ( ExtensibleNode ) exprNode;

            ExtensibleMatchFilter extensibleMatchFilter = new ExtensibleMatchFilter();
            sharedLdapFilter = extensibleMatchFilter;

            extensibleMatchFilter.setDnAttributes( extensibleNode.hasDnAttributes() );
            extensibleMatchFilter.setMatchingRule( extensibleNode.getMatchingRuleId() );
            extensibleMatchFilter.setMatchValue( extensibleNode.getValue() );
            extensibleMatchFilter.setType( extensibleNode.getAttribute() );
        }
        else if ( exprNode instanceof SubstringNode )
        {
            SubstringNode substringNode = ( SubstringNode ) exprNode;

            SubstringFilter substringFilter = new SubstringFilter();
            sharedLdapFilter = substringFilter;

            substringFilter.setType( substringNode.getAttribute() );
            substringFilter.setInitialSubstrings( substringNode.getInitial() );
            substringFilter.setFinalSubstrings( substringNode.getFinal() );
            List<String> anys = substringNode.getAny();
            for ( int i = 0; i < anys.size(); i++ )
            {
                substringFilter.addAnySubstrings( anys.get( i ) );
            }
        }

        return sharedLdapFilter;
    }


    /**
     * Iterates the conversion on the given List of notdes.
     *
     * @param filters
     *      the List of nodes to convert
     * @return
     *      an array containing the conversion for each Ldap Filter into its Shared LDAP Model
     * @throws DecoderException 
     */
    private static List<Filter> iterateOnFilters( List<ExprNode> filters ) throws DecoderException
    {
        List<Filter> filtersList = new ArrayList<Filter>();

        for ( int c = 0; c < filters.size(); c++ )
        {
            filtersList.add( convertToSharedLdapFilter( filters.get( c ) ) );
        }

        return filtersList;
    }


    /**
     * Create and returns an Attribute Value Assertion Filter from the given SimpleNode ant the given type.
     *
     * @param node
     *      the filter to convert
     * @param type
     *      the type of the Attribute Value Assertion Filter
     * @return
     *      the corresponding Attribute Value Assertion Filter
     */
    private static AttributeValueAssertionFilter createAttributeValueAssertionFilter( SimpleNode node, int type )
    {
        AttributeValueAssertionFilter avaFilter = new AttributeValueAssertionFilter( type );

        AttributeValueAssertion assertion = new AttributeValueAssertion();
        avaFilter.setAssertion( assertion );
        assertion.setAttributeDesc( node.getAttribute() );
        assertion.setAssertionValue( node.getValue() );

        return avaFilter;
    }


    /**
     * Converts the given array of Controls into their corresponding representation in the Shared LDAP Model.
     *
     * @param controls
     *      the array of Controls to convert
     * @return
     *      a List of Shared LDAP Control Objects corresponding to the given Controls
     */
    private List<org.apache.directory.shared.ldap.codec.Control> convertToSharedLdapControls( List<StudioControl> controls )
    {
        List<org.apache.directory.shared.ldap.codec.Control> returnList = new ArrayList<org.apache.directory.shared.ldap.codec.Control>();

        if ( controls != null )
        {
            for ( StudioControl control : controls )
            {
                returnList.add( convertToSharedLDAP( control ) );
            }
        }

        return returnList;
    }


    /**
     * Converts the given Control into its corresponding representation in the Shared LDAP Model.
     *
     * @param control
     *      the Control to convert
     * @return
     *      the corresponding Control in the Shared LDAP Model
     */
    private static org.apache.directory.shared.ldap.codec.Control convertToSharedLDAP( StudioControl control )
    {
        org.apache.directory.shared.ldap.codec.Control sharedLdapControl = new org.apache.directory.shared.ldap.codec.Control();

        sharedLdapControl.setControlType( control.getOid() );
        sharedLdapControl.setControlValue( control.getControlValue() );

        return sharedLdapControl;
    }
}
