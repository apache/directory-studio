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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.internal.model.ReferralException;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.Preferences;


public class ExportXlsJob extends AbstractEclipseJob
{

    public static final int MAX_COUNT_LIMIT = 65000;

    private String exportLdifFilename;

    private IBrowserConnection connection;

    private SearchParameter searchParameter;

    private boolean exportDn;


    public ExportXlsJob( String exportLdifFilename, IBrowserConnection connection, SearchParameter searchParameter,
        boolean exportDn )
    {
        this.exportLdifFilename = exportLdifFilename;
        this.connection = connection;
        this.searchParameter = searchParameter;
        this.exportDn = exportDn;

        setName( BrowserCoreMessages.jobs__export_xls_name );
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

        monitor.beginTask( BrowserCoreMessages.jobs__export_xls_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        String valueDelimiter = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_VALUEDELIMITER );
        int binaryEncoding = coreStore.getInt( BrowserCoreConstants.PREFERENCE_FORMAT_XLS_BINARYENCODING );

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet( "Export" ); //$NON-NLS-1$

        // header
        HSSFRow headerRow = sheet.createRow( 0 );
        LinkedHashMap attributeNameMap = new LinkedHashMap();
        if ( this.exportDn )
        {
            short cellNum = ( short ) 0;
            attributeNameMap.put( "dn", new Short( cellNum ) ); //$NON-NLS-1$
            headerRow.createCell( cellNum ).setCellValue( "dn" ); //$NON-NLS-1$
        }

        // String[] exportAttributes =
        // this.searchParameter.getReturningAttributes();
        // exportAttributes = null;
        // for (int i = 0; exportAttributes != null && i <
        // exportAttributes.length; i++) {
        // short cellNum = (short)attributeNameMap.size();
        // attributeNameMap.put(exportAttributes[i], new Short(cellNum));
        // headerRow.createCell(cellNum).setCellValue(exportAttributes[i]);
        // }

        // max export
        if ( searchParameter.getCountLimit() < 1 || searchParameter.getCountLimit() > MAX_COUNT_LIMIT )
        {
            searchParameter.setCountLimit( MAX_COUNT_LIMIT );
        }

        // export
        try
        {

            int count = 0;
            export( connection, searchParameter, sheet, headerRow, count, monitor, attributeNameMap, valueDelimiter,
                binaryEncoding, this.exportDn );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        // column width
        for ( int i = 0; i <= sheet.getLastRowNum(); i++ )
        {
            HSSFRow row = sheet.getRow( i );
            for ( short j = 0; row != null && j <= row.getLastCellNum(); j++ )
            {
                HSSFCell cell = row.getCell( j );
                if ( cell != null && cell.getCellType() == HSSFCell.CELL_TYPE_STRING )
                {
                    String value = cell.getStringCellValue();

                    if ( ( short ) ( value.length() * 256 * 1.1 ) > sheet.getColumnWidth( j ) )
                    {
                        sheet.setColumnWidth( j, ( short ) ( value.length() * 256 * 1.1 ) );
                    }
                }
            }
        }

        try
        {
            FileOutputStream fileOut = new FileOutputStream( exportLdifFilename );
            wb.write( fileOut );
            fileOut.close();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    private static void export( IBrowserConnection connection, SearchParameter searchParameter, HSSFSheet sheet,
        HSSFRow headerRow, int count, StudioProgressMonitor monitor, LinkedHashMap attributeNameMap,
        String valueDelimiter, int binaryEncoding, boolean exportDn ) throws IOException, ConnectionException
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
                    recordToHSSFRow( connection, record, sheet, headerRow, attributeNameMap, valueDelimiter, binaryEncoding,
                        exportDn );

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
                        export( referralSearch.getBrowserConnection(), referralSearch.getSearchParameter(), sheet, headerRow,
                            count, monitor, attributeNameMap, valueDelimiter, binaryEncoding, exportDn );
                    }
                }
            }
            else
            {
                monitor.reportError( ce );
            }
        }

    }


    private static void recordToHSSFRow( IBrowserConnection connection, LdifContentRecord record, HSSFSheet sheet, HSSFRow headerRow,
        Map headerRowAttributeNameMap, String valueDelimiter, int binaryEncoding, boolean exportDn )
    {

        // group multi-valued attributes
        Map attributeMap = ExportCsvJob.getAttributeMap( null, record, valueDelimiter, "UTF-16", binaryEncoding );

        // output attributes
        HSSFRow row = sheet.createRow( sheet.getLastRowNum() + 1 );
        if ( exportDn )
        {
            HSSFCell cell = row.createCell( ( short ) 0 );
            cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
            cell.setCellValue( record.getDnLine().getValueAsString() );
        }
        for ( Iterator it = attributeMap.keySet().iterator(); it.hasNext(); )
        {
            String attributeName = ( String ) it.next();
            String value = ( String ) attributeMap.get( attributeName );

            if ( !headerRowAttributeNameMap.containsKey( attributeName ) )
            {
                short cellNum = ( short ) headerRowAttributeNameMap.size();
                headerRowAttributeNameMap.put( attributeName, new Short( cellNum ) );
                HSSFCell cell = headerRow.createCell( cellNum );
                cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
                cell.setCellValue( attributeName );
            }

            if ( headerRowAttributeNameMap.containsKey( attributeName ) )
            {
                short cellNum = ( ( Short ) headerRowAttributeNameMap.get( attributeName ) ).shortValue();
                HSSFCell cell = row.createCell( cellNum );
                cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
                cell.setCellValue( value );
            }
        }

        // for (int i = 0; i < attributes.length; i++) {
        //			
        // String attributeName = attributes[i];
        // if (attributeMap.containsKey(attributeName)) {
        // String value = (String)attributeMap.get(attributeName);
        // short cellNum = (short)(i + (exportDn?1:0));
        // row.createCell(cellNum).setCellValue(value);
        // }
        // }

    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_xls_error;
    }

}
