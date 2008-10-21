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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.internal.model.AttributeDescription;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ReferralException;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;
import org.eclipse.core.runtime.Preferences;


public class ExportCsvJob extends AbstractEclipseJob
{

    private String exportLdifFilename;

    private IBrowserConnection connection;

    private SearchParameter searchParameter;

    private boolean exportDn;


    public ExportCsvJob( String exportLdifFilename, IBrowserConnection connection, SearchParameter searchParameter,
        boolean exportDn )
    {
        this.exportLdifFilename = exportLdifFilename;
        this.connection = connection;
        this.searchParameter = searchParameter;
        this.exportDn = exportDn;

        setName( BrowserCoreMessages.jobs__export_csv_name );
    }


    protected Connection[] getConnections()
    {
        return new Connection[]
            { connection.getConnection() };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection.getUrl() + "_" + DigestUtils.shaHex( exportLdifFilename ) );
        return l.toArray();
    }


    protected void executeAsyncJob( StudioProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.jobs__export_csv_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();

        String attributeDelimiter = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ATTRIBUTEDELIMITER );
        String valueDelimiter = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_VALUEDELIMITER );
        String quoteCharacter = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_QUOTECHARACTER );
        String lineSeparator = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_LINESEPARATOR );
        String encoding = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_ENCODING );
        int binaryEncoding = coreStore.getInt( BrowserCoreConstants.PREFERENCE_FORMAT_CSV_BINARYENCODING );
        String[] exportAttributes = this.searchParameter.getReturningAttributes();

        try
        {
            // open file
            FileOutputStream fos = new FileOutputStream( exportLdifFilename );
            OutputStreamWriter osw = new OutputStreamWriter( fos, encoding );
            BufferedWriter bufferedWriter = new BufferedWriter( osw );

            // header
            if ( this.exportDn )
            {
                bufferedWriter.write( "dn" ); //$NON-NLS-1$
                if ( exportAttributes == null || exportAttributes.length > 0 )
                    bufferedWriter.write( attributeDelimiter );
            }
            for ( int i = 0; i < exportAttributes.length; i++ )
            {
                bufferedWriter.write( exportAttributes[i] );
                if ( i + 1 < exportAttributes.length )
                    bufferedWriter.write( attributeDelimiter );
            }
            bufferedWriter.write( BrowserCoreConstants.LINE_SEPARATOR );
            
            // export
            int count = 0;
            export( connection, searchParameter, bufferedWriter, count, monitor, exportAttributes, attributeDelimiter,
                valueDelimiter, quoteCharacter, lineSeparator, encoding, binaryEncoding, exportDn );

            // close file
            bufferedWriter.close();
            osw.close();
            fos.close();

        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    private static void export( IBrowserConnection connection, SearchParameter searchParameter, BufferedWriter bufferedWriter,
        int count, StudioProgressMonitor monitor, String[] attributes, String attributeDelimiter,
        String valueDelimiter, String quoteCharacter, String lineSeparator, String encoding, int binaryEncoding, boolean exportDn )
        throws IOException, ConnectionException
    {
        try
        {

            LdifEnumeration enumeration = connection.exportLdif( searchParameter, monitor );
            while ( !monitor.isCanceled() && enumeration.hasNext( monitor ) )
            {
                LdifContainer container = enumeration.next( monitor );

                if ( container instanceof LdifContentRecord )
                {

                    LdifContentRecord record = ( LdifContentRecord ) container;
                    bufferedWriter.write( recordToCsv( connection, record, attributes, attributeDelimiter, valueDelimiter,
                        quoteCharacter, lineSeparator, encoding, binaryEncoding, exportDn ) );

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

                if ( searchParameter.getReferralsHandlingMethod() == IBrowserConnection.HANDLE_REFERRALS_FOLLOW )
                {

                    ReferralException re = ( ReferralException ) ce;
                    ISearch[] referralSearches = re.getReferralSearches();
                    for ( int i = 0; i < referralSearches.length; i++ )
                    {
                        ISearch referralSearch = referralSearches[i];

                        // export recursive
                        export( referralSearch.getBrowserConnection(), referralSearch.getSearchParameter(), bufferedWriter,
                            count, monitor, attributes, attributeDelimiter, valueDelimiter, quoteCharacter,
                            lineSeparator, encoding, binaryEncoding, exportDn );
                    }
                }
            }
            else
            {
                monitor.reportError( ce );
            }
        }

    }


    private static String recordToCsv( IBrowserConnection connection, LdifContentRecord record, String[] attributes, String attributeDelimiter,
        String valueDelimiter, String quoteCharacter, String lineSeparator, String encoding, int binaryEncoding, boolean exportDn )
    {

        // group multi-valued attributes
        Map attributeMap = getAttributeMap( connection, record, valueDelimiter, encoding, binaryEncoding );

        // print attributes
        StringBuffer sb = new StringBuffer();
        if ( exportDn )
        {
            sb.append( quoteCharacter );
            sb.append( record.getDnLine().getValueAsString() );
            sb.append( quoteCharacter );

            if ( attributes == null || attributes.length > 0 )
                sb.append( attributeDelimiter );
        }
        for ( int i = 0; i < attributes.length; i++ )
        {

            String attributeName = attributes[i];
            AttributeDescription ad = new AttributeDescription( attributeName );
            String oidString = ad.toOidString( connection.getSchema() );
            if ( attributeMap.containsKey( oidString ) )
            {
                String value = ( String ) attributeMap.get( oidString );

                // escape
                value = value.replaceAll( quoteCharacter, quoteCharacter + quoteCharacter );

                // always quote
                sb.append( quoteCharacter );
                sb.append( value );
                sb.append( quoteCharacter );
            }

            // delimiter
            if ( i + 1 < attributes.length )
            {
                sb.append( attributeDelimiter );
            }

        }
        sb.append( lineSeparator );

        return sb.toString();
    }


    static Map getAttributeMap( IBrowserConnection connection, LdifContentRecord record, String valueDelimiter, String encoding, int binaryEncoding )
    {
        Map attributeMap = new HashMap();
        LdifAttrValLine[] lines = record.getAttrVals();
        for ( int i = 0; i < lines.length; i++ )
        {
            String attributeName = lines[i].getUnfoldedAttributeDescription();
            if ( connection != null )
            {
                // convert attributeName to oid
                AttributeDescription ad = new AttributeDescription( attributeName );
                attributeName = ad.toOidString( connection.getSchema() );
            }
            String value = lines[i].getValueAsString();
            if ( ! Charset.forName( encoding ).newEncoder().canEncode( value ) )
            {
                if ( binaryEncoding == BrowserCoreConstants.BINARYENCODING_BASE64 )
                {
                    value = LdifUtils.base64encode( lines[i].getValueAsBinary() );
                }
                else if ( binaryEncoding == BrowserCoreConstants.BINARYENCODING_HEX )
                {
                    value = LdifUtils.hexEncode( lines[i].getValueAsBinary() );
                }
                else
                {
                    value = BrowserCoreConstants.BINARY;
                }

                if ( attributeMap.containsKey( attributeName ) )
                {
                    String oldValue = ( String ) attributeMap.get( attributeName );
                    attributeMap.put( attributeName, oldValue + valueDelimiter + value );
                }
                else
                {
                    attributeMap.put( attributeName, value );
                }
            }
            else
            {
                if ( attributeMap.containsKey( attributeName ) )
                {
                    String oldValue = ( String ) attributeMap.get( attributeName );
                    attributeMap.put( attributeName, oldValue + valueDelimiter + value );
                }
                else
                {
                    attributeMap.put( attributeName, value );
                }
            }
        }
        return attributeMap;
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_cvs_error;
    }

}
