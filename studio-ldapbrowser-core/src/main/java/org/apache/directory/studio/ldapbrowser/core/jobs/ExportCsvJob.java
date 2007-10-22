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
import java.util.HashMap;
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
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;
import org.eclipse.core.runtime.Preferences;


/**
 * Job to export directory content to an CSV file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExportCsvJob extends AbstractEclipseJob
{

    /** The filename of the CSV file. */
    private String exportCsvFilename;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The search parameter. */
    private SearchParameter searchParameter;

    /** The export dn flag. */
    private boolean exportDn;


    /**
     * Creates a new instance of ExportCsvJob.
     * 
     * @param exportCsvFilename the filename of the csv file
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     * @param exportDn true to export the DN
     */
    public ExportCsvJob( String exportCsvFilename, IBrowserConnection browserConnection,
        SearchParameter searchParameter, boolean exportDn )
    {
        this.exportCsvFilename = exportCsvFilename;
        this.browserConnection = browserConnection;
        this.searchParameter = searchParameter;
        this.exportDn = exportDn;

        setName( BrowserCoreMessages.jobs__export_csv_name );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection.getUrl() + "_" + DigestUtils.shaHex( exportCsvFilename ) };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#executeAsyncJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
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
            FileOutputStream fos = new FileOutputStream( exportCsvFilename );
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
            exportToCsv( browserConnection, searchParameter, bufferedWriter, count, monitor, exportAttributes,
                attributeDelimiter, valueDelimiter, quoteCharacter, lineSeparator, encoding, binaryEncoding, exportDn );

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


    /**
     * Exports to CSV.
     * 
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     * @param bufferedWriter the buffered writer
     * @param count the count
     * @param monitor the monitor
     * @param attributes the attributes
     * @param attributeDelimiter the attribute delimiter
     * @param valueDelimiter the value delimiter
     * @param quoteCharacter the quote character
     * @param lineSeparator the line separator
     * @param encoding the encoding
     * @param binaryEncoding the binary encoding
     * @param exportDn the export dn
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ConnectionException the connection exception
     */
    private static void exportToCsv( IBrowserConnection browserConnection, SearchParameter searchParameter,
        BufferedWriter bufferedWriter, int count, StudioProgressMonitor monitor, String[] attributes,
        String attributeDelimiter, String valueDelimiter, String quoteCharacter, String lineSeparator, String encoding,
        int binaryEncoding, boolean exportDn ) throws IOException, ConnectionException
    {
        try
        {
            LdifEnumeration enumeration = ExportLdifJob.search( browserConnection, searchParameter, monitor );
            while ( !monitor.isCanceled() && enumeration.hasNext( monitor ) )
            {
                LdifContainer container = enumeration.next( monitor );

                if ( container instanceof LdifContentRecord )
                {

                    LdifContentRecord record = ( LdifContentRecord ) container;
                    bufferedWriter.write( recordToCsv( browserConnection, record, attributes, attributeDelimiter,
                        valueDelimiter, quoteCharacter, lineSeparator, encoding, binaryEncoding, exportDn ) );

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

                if ( searchParameter.getReferralsHandlingMethod() == ReferralHandlingMethod.FOLLOW )
                {
                    ReferralException re = ( ReferralException ) ce;
                    ISearch[] referralSearches = re.getReferralSearches();
                    for ( int i = 0; i < referralSearches.length; i++ )
                    {
                        ISearch referralSearch = referralSearches[i];

                        // export recursive
                        exportToCsv( referralSearch.getBrowserConnection(), referralSearch.getSearchParameter(),
                            bufferedWriter, count, monitor, attributes, attributeDelimiter, valueDelimiter,
                            quoteCharacter, lineSeparator, encoding, binaryEncoding, exportDn );
                    }
                }
            }
            else
            {
                monitor.reportError( ce );
            }
        }

    }


    /**
     * Transforms an LDIF rRecord to CSV.
     * 
     * @param browserConnection the browser connection
     * @param record the record
     * @param attributes the attributes
     * @param attributeDelimiter the attribute delimiter
     * @param valueDelimiter the value delimiter
     * @param quoteCharacter the quote character
     * @param lineSeparator the line separator
     * @param encoding the encoding
     * @param binaryEncoding the binary encoding
     * @param exportDn the export dn
     * 
     * @return the string
     */
    private static String recordToCsv( IBrowserConnection browserConnection, LdifContentRecord record,
        String[] attributes, String attributeDelimiter, String valueDelimiter, String quoteCharacter,
        String lineSeparator, String encoding, int binaryEncoding, boolean exportDn )
    {

        // group multi-valued attributes
        Map<String, String> attributeMap = getAttributeMap( browserConnection, record, valueDelimiter, encoding,
            binaryEncoding );

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
            String oidString = ad.toOidString( browserConnection.getSchema() );
            if ( attributeMap.containsKey( oidString ) )
            {
                String value = attributeMap.get( oidString );

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


    /**
     * Gets the attribute map.
     * 
     * @param browserConnection the browser connection
     * @param record the record
     * @param valueDelimiter the value delimiter
     * @param encoding the encoding
     * @param binaryEncoding the binary encoding
     * 
     * @return the attribute map
     */
    static Map<String, String> getAttributeMap( IBrowserConnection browserConnection, LdifContentRecord record,
        String valueDelimiter, String encoding, int binaryEncoding )
    {
        Map<String, String> attributeMap = new HashMap<String, String>();
        LdifAttrValLine[] lines = record.getAttrVals();
        for ( int i = 0; i < lines.length; i++ )
        {
            String attributeName = lines[i].getUnfoldedAttributeDescription();
            if ( browserConnection != null )
            {
                // convert attributeName to oid
                AttributeDescription ad = new AttributeDescription( attributeName );
                attributeName = ad.toOidString( browserConnection.getSchema() );
            }
            String value = lines[i].getValueAsString();
            if ( !Charset.forName( encoding ).newEncoder().canEncode( value ) )
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


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_cvs_error;
    }

}
