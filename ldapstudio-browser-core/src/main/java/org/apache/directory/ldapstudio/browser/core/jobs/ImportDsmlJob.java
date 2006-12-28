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


import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.dsmlv2.Dsmlv2ResponseParser;
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;
import org.apache.directory.ldapstudio.dsmlv2.reponse.ErrorResponse;
import org.apache.directory.shared.ldap.codec.LdapResponse;


/**
 * This class implements a Job for Importing a DSML File into a LDAP server
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportDsmlJob extends AbstractEclipseJob
{

    /** The connection to use */
    private IConnection connection;

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
    public ImportDsmlJob( IConnection connection, File dsmlFile, File saveFile )
    {
        this.connection = connection;
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
    public ImportDsmlJob( IConnection connection, File dsmlFile )
    {
        this( connection, dsmlFile, null );
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
        List l = new ArrayList();
        l.add( connection.getUrl() + "_" + DigestUtils.shaHex( dsmlFile.toString() ) );
        return l.toArray();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.browser.core.jobs.AbstractEclipseJob#executeAsyncJob(org.apache.directory.ldapstudio.browser.core.jobs.ExtendedProgressMonitor)
     */
    protected void executeAsyncJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.jobs__import_dsml_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );
        
        Dsmlv2Engine engine = new Dsmlv2Engine( connection.getHost(), connection.getPort(), connection.getBindPrincipal(), connection.getBindPassword() );
        try
        {
            // Executing the DSML request and getting the response
            String response = engine.processDSMLFile( dsmlFile.getAbsolutePath() );
            
            // Saving Response if needed
            if ( responseFile != null )
            {
                FileOutputStream fout = new FileOutputStream( responseFile );
                new PrintStream( fout ).println( response );
                fout.close();
            }
            
            // Processing Reponse (Reading and displaying possible errors)
            int errorCount = 0;
            Dsmlv2ResponseParser responseParser = new Dsmlv2ResponseParser();
            responseParser.setInput( response );
            LdapResponse ldapResponse = responseParser.getNextResponse();
            while ( ldapResponse != null )
            {
                if ( ( ldapResponse instanceof ErrorResponse ) 
                     || ( ldapResponse.getLdapResult().getResultCode() != 0 ) )
                {
                    errorCount++;
                }
                ldapResponse = responseParser.getNextResponse();
            }
            
            if ( errorCount > 0 )
            {
                monitor.reportError( BrowserCoreMessages.bind(
                        BrowserCoreMessages.dsml__n_errors_see_responsefile, new String[]
                            { "" + errorCount } ) ); //$NON-NLS-1$
            }
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
        return BrowserCoreMessages.jobs__import_dsml_error;
    }

}
