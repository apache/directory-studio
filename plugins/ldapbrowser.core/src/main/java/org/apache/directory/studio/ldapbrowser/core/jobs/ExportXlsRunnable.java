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
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExportLdifRunnable.JndiLdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.Preferences;


/**
 * Runnable to export directory content to an XLS file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportXlsRunnable implements StudioConnectionRunnableWithProgress
{
    /** The maximum count limit */
    public static final int MAX_COUNT_LIMIT = 65000;

    /** The filename of the XLS file. */
    private String exportXlsFilename;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The search parameter. */
    private SearchParameter searchParameter;

    /** The export dn flag. */
    private boolean exportDn;


    /**
     * Creates a new instance of ExportXlsRunnable.
     * 
     * @param exportLdifFilename the export ldif filename
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     * @param exportDn true to export the Dn
     */
    public ExportXlsRunnable( String exportLdifFilename, IBrowserConnection browserConnection,
        SearchParameter searchParameter, boolean exportDn )
    {
        this.exportXlsFilename = exportLdifFilename;
        this.browserConnection = browserConnection;
        this.searchParameter = searchParameter;
        this.exportDn = exportDn;
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
        return BrowserCoreMessages.jobs__export_xls_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection.getUrl() + "_" + DigestUtils.shaHex( exportXlsFilename ) }; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_xls_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
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
        LinkedHashMap<String, Short> attributeNameMap = new LinkedHashMap<String, Short>();
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
            exportToXls( browserConnection, searchParameter, sheet, headerRow, count, monitor, attributeNameMap,
                valueDelimiter, binaryEncoding, this.exportDn );
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
            FileOutputStream fileOut = new FileOutputStream( exportXlsFilename );
            wb.write( fileOut );
            fileOut.close();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * Exports to XLS.
     * 
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     * @param sheet the sheet
     * @param headerRow the header row
     * @param count the count
     * @param monitor the monitor
     * @param attributeNameMap the attribute name map
     * @param valueDelimiter the value delimiter
     * @param binaryEncoding the binary encoding
     * @param exportDn the export dn
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private static void exportToXls( IBrowserConnection browserConnection, SearchParameter searchParameter,
        HSSFSheet sheet, HSSFRow headerRow, int count, StudioProgressMonitor monitor,
        LinkedHashMap<String, Short> attributeNameMap, String valueDelimiter, int binaryEncoding, boolean exportDn )
        throws IOException
    {
        try
        {
            JndiLdifEnumeration enumeration = ExportLdifRunnable.search( browserConnection, searchParameter, monitor );
            while ( !monitor.isCanceled() && !monitor.errorsReported() && enumeration.hasNext() )
            {
                LdifContainer container = enumeration.next();

                if ( container instanceof LdifContentRecord )
                {
                    LdifContentRecord record = ( LdifContentRecord ) container;
                    recordToHSSFRow( browserConnection, record, sheet, headerRow, attributeNameMap, valueDelimiter,
                        binaryEncoding, exportDn );

                    count++;
                    monitor.reportProgress( BrowserCoreMessages.bind( BrowserCoreMessages.jobs__export_progress,
                        new String[]
                            { Integer.toString( count ) } ) );
                }
            }

        }
        catch ( NamingException ne )
        {
            int ldapStatusCode = JNDIUtils.getLdapStatusCode( ne );
            if ( ldapStatusCode == 3 || ldapStatusCode == 4 || ldapStatusCode == 11 )
            {
                // nothing
            }
            else
            {
                monitor.reportError( ne );
            }
        }
        catch ( LdapInvalidDnException e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * Transforms an LDIF record to an HSSF row.
     * 
     * @param browserConnection the browser connection
     * @param record the record
     * @param sheet the sheet
     * @param headerRow the header row
     * @param headerRowAttributeNameMap the header row attribute name map
     * @param valueDelimiter the value delimiter
     * @param binaryEncoding the binary encoding
     * @param exportDn the export dn
     */
    private static void recordToHSSFRow( IBrowserConnection browserConnection, LdifContentRecord record,
        HSSFSheet sheet, HSSFRow headerRow, Map<String, Short> headerRowAttributeNameMap, String valueDelimiter,
        int binaryEncoding, boolean exportDn )
    {
        // group multi-valued attributes
        Map<String, String> attributeMap = ExportCsvRunnable.getAttributeMap( null, record, valueDelimiter, "UTF-16", //$NON-NLS-1$
            binaryEncoding );

        // output attributes
        HSSFRow row = sheet.createRow( sheet.getLastRowNum() + 1 );
        if ( exportDn )
        {
            HSSFCell cell = row.createCell( ( short ) 0 );
            cell.setEncoding( HSSFCell.ENCODING_UTF_16 );
            cell.setCellValue( record.getDnLine().getValueAsString() );
        }
        for ( String attributeName : attributeMap.keySet() )
        {
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
}
