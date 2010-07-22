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


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.dsmlv2.DsmlDecorator;
import org.apache.directory.shared.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResultDoneDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResultEntryDsml;
import org.apache.directory.shared.dsmlv2.reponse.SearchResultReferenceDsml;
import org.apache.directory.shared.dsmlv2.request.AddRequestDsml;
import org.apache.directory.shared.dsmlv2.request.BatchRequestDsml;
import org.apache.directory.shared.ldap.codec.LdapResultCodec;
import org.apache.directory.shared.ldap.codec.search.SearchResultDoneCodec;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.io.jndi.StudioNamingEnumeration;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;


/**
 * This class implements a Job for Exporting a part of a LDAP Server into a DSML File.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportDsmlJob extends AbstractEclipseJob
{
    private static final String OBJECTCLASS_OBJECTCLASS_OID = "objectClass";
    private static final String OBJECTCLASS_OBJECTCLASS_NAME = "2.5.4.0";

    private static final String REFERRAL_OBJECTCLASS_OID = "2.16.840.1.113730.3.2.6";
    private static final String REFERRAL_OBJECTCLASS_NAME = "referral";

    private static final String REF_ATTRIBUTETYPE_OID = "2.16.840.1.113730.3.1.34";
    private static final String REF_ATTRIBUTETYPE_NAME = "ref";

    /** The name of the DSML file to export to */
    private String exportDsmlFilename;

    /** The connection to use */
    private IBrowserConnection browserConnection;

    /** The Search Parameter of the export*/
    private SearchParameter searchParameter;

    /** The type of the export */
    private ExportDsmlJobType type = ExportDsmlJobType.RESPONSE;

    /**
     * This enum contains the two possible export types.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum ExportDsmlJobType
    {
        RESPONSE, REQUEST
    };


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
    public ExportDsmlJob( String exportDsmlFilename, IBrowserConnection connection, SearchParameter searchParameter,
        ExportDsmlJobType type )
    {
        this.exportDsmlFilename = exportDsmlFilename;
        this.browserConnection = connection;
        this.searchParameter = searchParameter;
        this.type = type;

        // Adding the name and OID of the 'ref' attribute to the list of returning attributes
        // for handling externals correctly
        List<String> returningAttributes = new ArrayList<String>( Arrays.asList( searchParameter
            .getReturningAttributes() ) );
        returningAttributes.add( REF_ATTRIBUTETYPE_NAME );
        returningAttributes.add( REF_ATTRIBUTETYPE_OID );
        searchParameter.setReturningAttributes( returningAttributes.toArray( new String[0] ) );

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
        monitor.beginTask( BrowserCoreMessages.jobs__export_dsml_task, 4 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {

            // Creating a dummy monitor that will be used to check if something
            // went wrong when executing the request
            StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

            // Searching for the requested entries
            StudioNamingEnumeration ne = SearchRunnable.search( browserConnection, searchParameter, dummyMonitor );
            monitor.worked( 1 );

            // Getting the DSML string associated to the search
            // and the type of answer the user is expecting
            String dsmlExportString = null;
            switch ( type )
            {
                case RESPONSE:
                    dsmlExportString = processAsDsmlResponse( ne, dummyMonitor );
                    break;
                case REQUEST:
                    dsmlExportString = processAsDsmlRequest( ne, dummyMonitor );
                    break;
            }
            monitor.worked( 1 );

            // Writing the DSML string to the final destination file.
            if ( dsmlExportString != null )
            {
                FileOutputStream fos = new FileOutputStream( exportDsmlFilename );
                OutputStreamWriter osw = new OutputStreamWriter( fos, "UTF-8" );
                BufferedWriter bufferedWriter = new BufferedWriter( osw );
                bufferedWriter.write( dsmlExportString );
                bufferedWriter.close();
                osw.close();
                fos.close();
            }
            monitor.worked( 1 );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * Processes the {@link NamingEnumeration} as a DSML response.
     *
     * @param ne
     *      the naming enumeration
     * @param monitor 
     *      the monitor
     * @return
     *      the associated DSML
     * @throws NamingException 
     * @throws LdapURLEncodingException 
     */
    private String processAsDsmlResponse( StudioNamingEnumeration ne, StudioProgressMonitor monitor )
        throws NamingException, LdapURLEncodingException
    {
        // Creating the batch reponse
        BatchResponseDsml batchResponse = new BatchResponseDsml();

        processAsDsmlResponse( ne, batchResponse, monitor, searchParameter );

        // Returning the associated DSML
        return batchResponse.toDsml();
    }


    /**
     * Processes the {@link NamingEnumeration} as a DSML response.
     *
     * @param ne
     *      the naming enumeration
     * @param monitor 
     *      the monitor
     * @param searchParameter 
     *      the search parameter
     * @throws NamingException 
     * @throws LdapURLEncodingException 
     */
    public static void processAsDsmlResponse( StudioNamingEnumeration ne, BatchResponseDsml batchResponse,
        StudioProgressMonitor monitor, SearchParameter searchParameter ) throws NamingException,
        LdapURLEncodingException
    {
        // Creating and adding the search response
        SearchResponseDsml sr = new SearchResponseDsml();
        batchResponse.addResponse( sr );

        if ( !monitor.errorsReported() )
        {
            // Creating and adding a search result entry or reference for each result
            while ( ne.hasMore() )
            {
                SearchResult searchResult = ( SearchResult ) ne.next();
                sr.addResponse( convertSearchResultToDsml( searchResult, searchParameter ) );
            }
        }

        // Creating and adding a search result done at the end of the results
        SearchResultDoneCodec srd = new SearchResultDoneCodec();
        LdapResultCodec ldapResult = new LdapResultCodec();
        if ( !monitor.errorsReported() )
        {
            ldapResult.setResultCode( ResultCodeEnum.SUCCESS );
        }
        else
        {
            // Getting the exception
            Throwable t = monitor.getException();

            // Setting the result code
            ldapResult.setResultCode( ResultCodeEnum.getBestEstimate( t, MessageTypeEnum.SEARCH_REQUEST ) );

            // Setting the error message if there's one
            if ( t.getMessage() != null )
            {
                ldapResult.setErrorMessage( t.getMessage() );
            }
        }
        srd.setLdapResult( ldapResult );
        sr.addResponse( new SearchResultDoneDsml( srd ) );
    }


    /**
     * Converts the given {@link SearchResult} to a {@link SearchResultEntryDsml}.
     *
     * @param searchResult
     *      the search result
     * @return
     *      the associated search result entry DSML
     * @throws InvalidNameException 
     * @throws InvalidAttributeIdentifierException 
     * @throws LdapURLEncodingException 
     */
    private static DsmlDecorator convertSearchResultToDsml( SearchResult searchResult, SearchParameter searchParameter )
        throws InvalidAttributeIdentifierException, InvalidNameException, LdapURLEncodingException
    {
        Entry entry = AttributeUtils.toClientEntry( searchResult.getAttributes(), new LdapDN( searchResult
            .getNameInNamespace() ) );

        if ( isReferral( entry ) )
        {
            // The search result is a referral
            SearchResultReferenceDsml srr = new SearchResultReferenceDsml();

            // Getting the 'ref' attribute
            EntryAttribute refAttribute = entry.get( ExportDsmlJob.REF_ATTRIBUTETYPE_NAME );
            if ( refAttribute == null )
            {
                // If we did not get it by its name, let's get it by its OID
                refAttribute = entry.get( ExportDsmlJob.REF_ATTRIBUTETYPE_OID );
            }

            // Adding references
            if ( refAttribute != null )
            {
                for ( Iterator<Value<?>> iterator = refAttribute.iterator(); iterator.hasNext(); )
                {
                    Value<?> value = ( Value<?> ) iterator.next();

                    srr.addSearchResultReference( new LdapURL( ( String ) value.get() ) );
                }
            }

            return srr;
        }
        else
        {
            // The search result is NOT a referral
            SearchResultEntryDsml sre = new SearchResultEntryDsml();
            sre.setEntry( entry );

            return sre;
        }
    }


    /**
     * Indicates if the given entry is a referral.
     *
     * @param entry
     *      the entry
     * @return
     *      <code>true</code> if the given entry is a referral, <code>false</code> if not
     */
    private static boolean isReferral( Entry entry )
    {
        if ( entry != null )
        {
            // Getting the 'objectClass' Attribute
            EntryAttribute objectClassAttribute = entry.get( ExportDsmlJob.OBJECTCLASS_OBJECTCLASS_NAME );
            if ( objectClassAttribute == null )
            {
                objectClassAttribute = entry.get( ExportDsmlJob.OBJECTCLASS_OBJECTCLASS_OID );
            }

            if ( objectClassAttribute != null )
            {
                // Checking if the 'objectClass' attribute contains the 
                // 'referral' object class as value
                return ( ( objectClassAttribute.contains( ExportDsmlJob.REFERRAL_OBJECTCLASS_NAME ) ) || ( objectClassAttribute
                    .contains( ExportDsmlJob.REFERRAL_OBJECTCLASS_OID ) ) );
            }
        }

        return false;
    }


    /**
     * Processes the {@link NamingEnumeration} as a DSML request.
     *
     * @param ne
     *      the naming enumeration
     * @param monitor 
     *      the monitor
     * @return
     *      the associated DSML
     * @throws NamingException 
     */
    private String processAsDsmlRequest( StudioNamingEnumeration ne, StudioProgressMonitor monitor )
        throws NamingException
    {
        // Creating the batch request
        BatchRequestDsml batchRequest = new BatchRequestDsml();

        if ( !monitor.errorsReported() )
        {
            // Creating and adding an add request for each result
            while ( ne.hasMore() )
            {
                SearchResult searchResult = ( SearchResult ) ne.next();
                AddRequestDsml arDsml = convertToAddRequestDsml( searchResult );
                batchRequest.addRequest( arDsml );
            }
        }

        // Returning the associated DSML
        return batchRequest.toDsml();
    }


    /**
     * Converts the given {@link SearchResult} to an {@link AddRequestDsml}.
     *
     * @param searchResult
     *      the {@link SearchResult}
     * @return
     *      the associated {@link AddRequestDsml}
     * @throws InvalidAttributeIdentifierException
     * @throws InvalidNameException
     */
    private AddRequestDsml convertToAddRequestDsml( SearchResult searchResult )
        throws InvalidAttributeIdentifierException, InvalidNameException
    {
        AddRequestDsml ar = new AddRequestDsml();
        Entry entry = AttributeUtils.toClientEntry( searchResult.getAttributes(), new LdapDN( searchResult
            .getNameInNamespace() ) );
        ar.setEntry( entry );

        return ar;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_dsml_error;
    }
}
