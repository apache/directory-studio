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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.internal.model.AttributeComparator;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ReferralException;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifDnLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifSepLine;


public class ExportLdifJob extends AbstractEclipseJob
{

    private String exportLdifFilename;

    private IConnection connection;

    private SearchParameter searchParameter;


    public ExportLdifJob( String exportLdifFilename, IConnection connection, SearchParameter searchParameter )
    {
        this.exportLdifFilename = exportLdifFilename;
        this.connection = connection;
        this.searchParameter = searchParameter;

        setName( BrowserCoreMessages.jobs__export_ldif_name );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { connection };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection.getUrl() + "_" + DigestUtils.shaHex( exportLdifFilename ) );
        return l.toArray();
    }


    protected void executeAsyncJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.jobs__export_ldif_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            // open file
            FileWriter fileWriter = new FileWriter( exportLdifFilename );
            BufferedWriter bufferedWriter = new BufferedWriter( fileWriter );

            // export
            int count = 0;
            export( connection, searchParameter, bufferedWriter, count, monitor );

            // close file
            bufferedWriter.close();
            fileWriter.close();

        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

    }


    private static void export( IConnection connection, SearchParameter searchParameter, BufferedWriter bufferedWriter,
        int count, ExtendedProgressMonitor monitor ) throws IOException, ConnectionException
    {
        try
        {

            AttributeComparator comparator = new AttributeComparator( connection );
            LdifEnumeration enumeration = connection.exportLdif( searchParameter, monitor );
            while ( !monitor.isCanceled() && enumeration.hasNext( monitor ) )
            {
                LdifContainer container = enumeration.next( monitor );

                if ( container instanceof LdifContentRecord )
                {
                    LdifContentRecord record = ( LdifContentRecord ) container;
                    LdifDnLine dnLine = record.getDnLine();
                    LdifAttrValLine[] attrValLines = record.getAttrVals();
                    LdifSepLine sepLine = record.getSepLine();

                    // sort and format
                    Arrays.sort( attrValLines, comparator );
                    LdifContentRecord newRecord = new LdifContentRecord( dnLine );
                    for ( int i = 0; i < attrValLines.length; i++ )
                    {
                        newRecord.addAttrVal( attrValLines[i] );
                    }
                    newRecord.finish( sepLine );
                    String s = newRecord.toFormattedString();

                    // String s = record.toFormattedString();
                    bufferedWriter.write( s );

                    count++;
                    monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__export_progress,
                        new String[]
                            { Integer.toString( count ) } ) );

                }

            }
        }
        catch ( ConnectionException ce )
        {

            if ( ce.getLdapStatusCode() == 3 || ce.getLdapStatusCode() == 4 || ce.getLdapStatusCode() == 11 )
            {
                // nothing
            }
            else if ( ce instanceof ReferralException )
            {

                if ( searchParameter.getReferralsHandlingMethod() == IConnection.HANDLE_REFERRALS_FOLLOW )
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

                        // export recursive
                        export( referralSearch.getConnection(), referralSearch.getSearchParameter(), bufferedWriter,
                            count, monitor );
                    }
                }
            }
            else
            {
                monitor.reportError( ce );
            }
        }

    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_ldif_error;
    }

}
