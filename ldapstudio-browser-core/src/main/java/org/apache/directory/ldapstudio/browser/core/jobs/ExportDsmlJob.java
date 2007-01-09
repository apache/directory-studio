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
import org.apache.directory.ldapstudio.dsmlv2.engine.Dsmlv2Engine;


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
            // Getting and preparing each parameter for the request        
            String requestDN = searchParameter.getSearchBase().toString();

            String requestScope = null;
            int scope = searchParameter.getScope();
            if ( scope == ISearch.SCOPE_OBJECT )
            {
                requestScope = "baseObject";
            }
            else if ( scope == ISearch.SCOPE_ONELEVEL )
            {
                requestScope = "singleLevel";
            }
            else if ( scope == ISearch.SCOPE_SUBTREE )
            {
                requestScope = "wholeSubtree";
            }

            String requestDerefAliases = null;
            int derefAliases = searchParameter.getAliasesDereferencingMethod();
            if ( derefAliases == IConnection.DEREFERENCE_ALIASES_ALWAYS )
            {
                requestDerefAliases = "derefAlways";
            }
            else if ( derefAliases == IConnection.DEREFERENCE_ALIASES_FINDING )
            {
                requestDerefAliases = "derefFindingBaseObj";
            }
            else if ( derefAliases == IConnection.DEREFERENCE_ALIASES_NEVER )
            {
                requestDerefAliases = "neverDerefAliases";
            }
            else if ( derefAliases == IConnection.DEREFERENCE_ALIASES_SEARCH )
            {
                requestDerefAliases = "derefInSearching";
            }

            String requestTimeLimit = null;
            int timeLimit = searchParameter.getTimeLimit();
            if ( timeLimit != 0 )
                ;
            {
                requestTimeLimit = "" + timeLimit;
            }

            String requestSizeLimit = null;
            int countLimit = searchParameter.getCountLimit();
            if ( countLimit != 0 )
                ;
            {
                requestSizeLimit = "" + countLimit;
            }

            // Constructing the request
            StringBuffer sb = new StringBuffer();
            sb.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
            sb.append( "<batchRequest>" );
            sb.append( "<searchRequest dn=\"" + requestDN + "\"" );
            sb.append( " scope=\"" + requestScope + "\" " );
            sb.append( " derefAliases=\"" + requestDerefAliases + "\"" );
            if ( requestTimeLimit != null )
            {
                sb.append( " timeLimit=\"" + requestTimeLimit + "\"" );
            }
            if ( requestSizeLimit != null )
            {
                sb.append( " sizeLimit=\"" + requestSizeLimit + "\"" );
            }
            sb.append( ">" );
            sb.append( "<filter><present name=\"objectclass\"></present></filter>" );
            sb.append( "<attributes>" );
            String[] returningAttributes = searchParameter.getReturningAttributes();
            for ( int i = 0; i < returningAttributes.length; i++ )
            {
                sb.append( "<attribute name=\"" + returningAttributes[i] + "\"/>" );
            }
            sb.append( "</attributes>" );
            sb.append( "</searchRequest>" );
            sb.append( "</batchRequest>" );

            // Executing the request
            Dsmlv2Engine engine = new Dsmlv2Engine( connection.getHost(), connection.getPort(), connection
                .getBindPrincipal(), connection.getBindPassword() );
            String response = engine.processDSML( sb.toString() );

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
}
