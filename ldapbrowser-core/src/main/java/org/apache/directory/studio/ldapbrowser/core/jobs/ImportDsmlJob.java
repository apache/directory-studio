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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.ldap.codec.add.AddRequest;
import org.apache.directory.shared.ldap.codec.compare.CompareRequest;
import org.apache.directory.shared.ldap.codec.del.DelRequest;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequest;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequest;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequest;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.dsmlv2.Dsmlv2Parser;
import org.apache.directory.studio.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.studio.dsmlv2.request.BatchRequest;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * This class implements a Job for Importing a DSML File into a LDAP server
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportDsmlJob extends AbstractEclipseJob
{
    /** The connection to use */
    private IBrowserConnection browserConnection;

    /** The DSML file to use */
    private File dsmlFile;

    /** The Save file to use */
    private File responseFile;


    /**
     * Creates a new instance of ImportDsmlJob.
     *
     * @param connection
     *          The connection to use
     * @param dsmlFile
     *          The DSML file to read from
     * @param saveFile
     *          The Save file to use
     * @param continueOnError
     *          The ContinueOnError flag
     */
    public ImportDsmlJob( IBrowserConnection connection, File dsmlFile, File saveFile )
    {
        this.browserConnection = connection;
        this.dsmlFile = dsmlFile;
        this.responseFile = saveFile;

        setName( BrowserCoreMessages.jobs__import_dsml_name );
    }


    /**
     * Creates a new instance of ImportDsmlJob.
     *
     * @param connection
     *          The Connection to use
     * @param dsmlFile
     *          The DSML file to read from
     * @param continueOnError
     *          The ContinueOnError flag
     */
    public ImportDsmlJob( IBrowserConnection connection, File dsmlFile )
    {
        this( connection, dsmlFile, null );
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
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( dsmlFile.toString() ) );
        return l.toArray();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#executeAsyncJob(org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor)
     */
    protected void executeAsyncJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__import_dsml_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            // Parsing the file
            Dsmlv2Parser parser = new Dsmlv2Parser();
            parser.setInput( new FileInputStream( dsmlFile ), "UTF-8" );
            parser.parseAllRequests();

            // Getting the batch request
            BatchRequest batchRequest = parser.getBatchRequest();

            // Creating a DSML batch response (only if needed)
            BatchResponseDsml batchResponseDsml = null;
            if ( responseFile != null )
            {
                batchResponseDsml = new BatchResponseDsml();
            }

            // Processing each request
            List<?> requests = batchRequest.getRequests();
            for ( Object request : requests )
            {
                processRequest( request, batchResponseDsml );
            }

            // Writing the DSML response file to its final destination file.
            if ( responseFile != null )
            {
                FileOutputStream fos = new FileOutputStream( responseFile );
                OutputStreamWriter osw = new OutputStreamWriter( fos );
                BufferedWriter bufferedWriter = new BufferedWriter( osw );
                bufferedWriter.write( batchResponseDsml.toDsml() );
                bufferedWriter.close();
                osw.close();
                fos.close();
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    private void processRequest( Object request, BatchResponseDsml batchResponseDsml )
    {
        if ( request instanceof AddRequest )
        {
            AddRequest addRequest = ( AddRequest ) request;

        }
        else if ( request instanceof CompareRequest )
        {
            CompareRequest compareRequest = ( CompareRequest ) request;

        }
        else if ( request instanceof DelRequest )
        {
            DelRequest delRequest = ( DelRequest ) request;

        }
        else if ( request instanceof ExtendedRequest )
        {
            ExtendedRequest extendedRequest = ( ExtendedRequest ) request;

        }
        else if ( request instanceof ModifyRequest )
        {
            ModifyRequest modifyRequest = ( ModifyRequest ) request;

        }
        else if ( request instanceof ModifyDNRequest )
        {
            ModifyDNRequest modifyDNRequest = ( ModifyDNRequest ) request;

        }
        else if ( request instanceof SearchRequest )
        {
            SearchRequest searchRequest = ( SearchRequest ) request;

        }

        System.out.println( request );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_dsml_error;
    }

}
