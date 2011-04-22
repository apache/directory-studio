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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
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
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.codec.api.LdapCodecServiceFactory;
import org.apache.directory.shared.ldap.model.entry.Attribute;
import org.apache.directory.shared.ldap.model.entry.AttributeUtils;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.Response;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.message.SearchResultDone;
import org.apache.directory.shared.ldap.model.message.SearchResultDoneImpl;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.url.LdapUrl;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;


/**
 * Runnable for Exporting a part of a LDAP Server into a DSML File.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportDsmlRunnable implements StudioConnectionRunnableWithProgress
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
     * The LDAP Codec - for now need by the DSML Parser 
     * @TODO - this should be removed - no reason why the DSML parser needs it
     * @TODO - hate to make it static like this but methods are static
     */
    private static LdapCodecService codec = LdapCodecServiceFactory.getSingleton();
    

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
     * Creates a new instance of ExportDsmlRunnable.
     *
     * @param exportDsmlFilename
     *          the name of the DSML file to export to
     * @param connection
     *          the connection to use
     * @param searchParameter
     *          the Search Parameter of the export
     */
    public ExportDsmlRunnable( String exportDsmlFilename, IBrowserConnection connection,
        SearchParameter searchParameter, ExportDsmlJobType type )
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
        return BrowserCoreMessages.jobs__export_dsml_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<String> l = new ArrayList<String>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( exportDsmlFilename ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_dsml_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
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
     * @throws LdapException
     */
    private String processAsDsmlResponse( StudioNamingEnumeration ne, StudioProgressMonitor monitor )
        throws NamingException, LdapURLEncodingException, LdapException
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
     * @throws LdapURLEncodingException 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    public static void processAsDsmlResponse( StudioNamingEnumeration ne, BatchResponseDsml batchResponse,
        StudioProgressMonitor monitor, SearchParameter searchParameter ) throws LdapURLEncodingException, LdapException
    {
        // Creating and adding the search response
        SearchResponseDsml sr = new SearchResponseDsml( codec );
        batchResponse.addResponse( sr );

        try
        {
            int count = 0;

            if ( !monitor.errorsReported() )
            {
                // Creating and adding a search result entry or reference for each result
                while ( ne.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) ne.next();
                    sr.addResponse( convertSearchResultToDsml( searchResult, searchParameter ) );

                    count++;
                    monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__export_progress,
                        new String[]
                        { Integer.toString( count ) } ) );
                }
            }
        }
        catch ( NamingException e )
        {
            int ldapStatusCode = JNDIUtils.getLdapStatusCode( e );
            if ( ldapStatusCode == 3 || ldapStatusCode == 4 || ldapStatusCode == 11 )
            {
                // ignore
            }
            else
            {
                monitor.reportError( e );
            }
        }

        // Creating and adding a search result done at the end of the results
        SearchResultDone srd = new SearchResultDoneImpl();
        LdapResult ldapResult = srd.getLdapResult();
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
                ldapResult.setDiagnosticMessage( t.getMessage() );
            }
        }
        sr.addResponse( new SearchResultDoneDsml( codec, srd ) );
    }


    /**
     * Converts the given {@link SearchResult} to a {@link SearchResultEntryDsml}.
     *
     * @param searchResult
     *      the search result
     * @return
     *      the associated search result entry DSML
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    private static DsmlDecorator<? extends Response> convertSearchResultToDsml( SearchResult searchResult, SearchParameter searchParameter )
        throws LdapException, LdapURLEncodingException
    {
        Entry entry = AttributeUtils.toEntry(searchResult.getAttributes(),
                new Dn(searchResult.getNameInNamespace()));

        if ( isReferral( entry ) )
        {
            // The search result is a referral
            SearchResultReferenceDsml srr = new SearchResultReferenceDsml( codec );

            // Getting the 'ref' attribute
            Attribute refAttribute = entry.get( ExportDsmlRunnable.REF_ATTRIBUTETYPE_NAME );
            if ( refAttribute == null )
            {
                // If we did not get it by its name, let's get it by its OID
                refAttribute = entry.get( ExportDsmlRunnable.REF_ATTRIBUTETYPE_OID );
            }

            // Adding references
            if ( refAttribute != null )
            {
                for ( Iterator<Value<?>> iterator = refAttribute.iterator(); iterator.hasNext(); )
                {
                    Value<?> value = ( Value<?> ) iterator.next();

                    srr.addSearchResultReference( new LdapUrl( ( String ) value.getValue() ) );
                }
            }

            return srr;
        }
        else
        {
            // The search result is NOT a referral
            SearchResultEntryDsml sre = new SearchResultEntryDsml( codec );
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
            Attribute objectClassAttribute = entry.get( ExportDsmlRunnable.OBJECTCLASS_OBJECTCLASS_NAME );
            if ( objectClassAttribute == null )
            {
                objectClassAttribute = entry.get( ExportDsmlRunnable.OBJECTCLASS_OBJECTCLASS_OID );
            }

            if ( objectClassAttribute != null )
            {
                // Checking if the 'objectClass' attribute contains the 
                // 'referral' object class as value
                return ( ( objectClassAttribute.contains( ExportDsmlRunnable.REFERRAL_OBJECTCLASS_NAME ) ) || ( objectClassAttribute
                    .contains( ExportDsmlRunnable.REFERRAL_OBJECTCLASS_OID ) ) );
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
     * @throws LdapException
     */
    private String processAsDsmlRequest( StudioNamingEnumeration ne, StudioProgressMonitor monitor )
        throws NamingException, LdapException
    {
        // Creating the batch request
        BatchRequestDsml batchRequest = new BatchRequestDsml();

        try
        {
            int count = 0;

            if ( !monitor.errorsReported() )
            {
                // Creating and adding an add request for each result
                while ( ne.hasMore() )
                {
                    SearchResult searchResult = ( SearchResult ) ne.next();
                    AddRequestDsml arDsml = convertToAddRequestDsml( searchResult );
                    batchRequest.addRequest( arDsml );

                    count++;
                    monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__export_progress,
                        new String[]
                    { Integer.toString( count ) } ) );
                }
            }
        }
        catch ( NamingException e )
        {
            int ldapStatusCode = JNDIUtils.getLdapStatusCode( e );
            if ( ldapStatusCode == 3 || ldapStatusCode == 4 || ldapStatusCode == 11 )
            {
                // ignore
            }
            else
            {
                monitor.reportError( e );
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
     * @throws LdapException
     */
    private AddRequestDsml convertToAddRequestDsml( SearchResult searchResult )
        throws LdapException
    {
        AddRequestDsml ar = new AddRequestDsml( codec );
        Entry entry = AttributeUtils.toEntry( searchResult.getAttributes(),
            new Dn( searchResult.getNameInNamespace() ) );
        ar.setEntry( entry );

        return ar;
    }
}
