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
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.SearchResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.ldap.codec.LdapResult;
import org.apache.directory.shared.ldap.codec.search.SearchResultDone;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.studio.dsmlv2.reponse.SearchResponseDsml;
import org.apache.directory.studio.dsmlv2.reponse.SearchResultDoneDsml;
import org.apache.directory.studio.dsmlv2.reponse.SearchResultEntryDsml;
import org.apache.directory.studio.dsmlv2.request.AddRequestDsml;
import org.apache.directory.studio.dsmlv2.request.BatchRequestDsml;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;


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

    /** The type of the export */
    private ExportDsmlJobType type = ExportDsmlJobType.RESPONSE;

    /**
     * This enum contains the two possible export types.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
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
            // Searching for the requested entries
            NamingEnumeration<SearchResult> ne = SearchRunnable.search( browserConnection, searchParameter, monitor );
            monitor.worked( 1 );

            // Getting the DSML string associated to the search
            // and the type of answer the user is expecting
            String dsmlExportString = null;
            switch ( type )
            {
                case RESPONSE:
                    dsmlExportString = processAsDsmlResponse( ne );
                    break;
                case REQUEST:
                    dsmlExportString = processAsDsmlRequest( ne );
                    break;
            }
            monitor.worked( 1 );

            // Writing the DSML string to the final destination file.
            if ( dsmlExportString != null )
            {
                FileOutputStream fos = new FileOutputStream( exportDsmlFilename );
                OutputStreamWriter osw = new OutputStreamWriter( fos );
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
     * @return
     *      the associated DSML
     * @throws InvalidNameException 
     * @throws InvalidAttributeIdentifierException 
     */
    private String processAsDsmlResponse( NamingEnumeration<SearchResult> ne )
        throws InvalidAttributeIdentifierException, InvalidNameException
    {
        // Creating the batch reponse
        BatchResponseDsml batchResponse = new BatchResponseDsml();

        // Creating and adding the search response
        SearchResponseDsml sr = new SearchResponseDsml();
        batchResponse.addResponse( sr );

        // Creating and adding a search result entry for each result
        while ( ne.hasMoreElements() )
        {
            SearchResult searchResult = ( SearchResult ) ne.nextElement();
            SearchResultEntryDsml sreDsml = convertToSearchResultEntryDsml( searchResult );
            sr.addResponse( sreDsml );
        }

        // Creating and adding a search result done at the end of the results
        SearchResultDone srd = new SearchResultDone();
        LdapResult ldapResult = new LdapResult();
        ldapResult.setResultCode( ResultCodeEnum.SUCCESS );
        srd.setLdapResult( ldapResult );
        sr.addResponse( new SearchResultDoneDsml( srd ) );

        // Returning the associated DSML
        return batchResponse.toDsml();
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
     */
    private SearchResultEntryDsml convertToSearchResultEntryDsml( SearchResult searchResult )
        throws InvalidAttributeIdentifierException, InvalidNameException
    {
        SearchResultEntryDsml sre = new SearchResultEntryDsml();
        Entry entry = AttributeUtils.toClientEntry( searchResult.getAttributes(), new LdapDN( searchResult
            .getNameInNamespace() ) );
        sre.setEntry( entry );

        return sre;
    }


    /**
     * Processes the {@link NamingEnumeration} as a DSML request.
     *
     * @param ne
     *      the naming enumeration
     * @return
     *      the associated DSML
     * @throws InvalidNameException 
     * @throws InvalidAttributeIdentifierException 
     */
    private String processAsDsmlRequest( NamingEnumeration<SearchResult> ne )
        throws InvalidAttributeIdentifierException, InvalidNameException
    {
        // Creating the batch request
        BatchRequestDsml batchRequest = new BatchRequestDsml();

        // Creating and adding an add request for each result
        while ( ne.hasMoreElements() )
        {
            SearchResult searchResult = ( SearchResult ) ne.nextElement();
            AddRequestDsml arDsml = convertToAddRequestDsml( searchResult );
            batchRequest.addRequest( arDsml );
        }

        // Returning the associated DSML
        return batchRequest.toDsml();
    }


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
