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


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
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
import org.eclipse.core.runtime.Preferences;
import org.openoffice.odf.doc.OdfFileDom;
import org.openoffice.odf.doc.OdfSpreadsheetDocument;
import org.openoffice.odf.doc.element.table.OdfTable;
import org.openoffice.odf.doc.element.table.OdfTableCell;
import org.openoffice.odf.doc.element.table.OdfTableRow;
import org.openoffice.odf.dom.OdfNamespace;
import org.openoffice.odf.dom.type.office.OdfValueType;
import org.w3c.dom.Element;


/**
 * Runnable to export directory content to an ODF file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportOdfRunnable implements StudioConnectionRunnableWithProgress
{
    /** The maximum count limit */
    public static final int MAX_COUNT_LIMIT = 65000;

    /** The filename of the ODF file. */
    private String exportOdfFilename;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The search parameter. */
    private SearchParameter searchParameter;

    /** The export dn flag. */
    private boolean exportDn;


    /**
     * Creates a new instance of ExportOdfRunnable.
     * 
     * @param exportOdfFilename the ODF filename
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     * @param exportDn true to export the Dn
     */
    public ExportOdfRunnable( String exportOdfFilename, IBrowserConnection browserConnection,
        SearchParameter searchParameter, boolean exportDn )
    {
        this.exportOdfFilename = exportOdfFilename;
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
        return BrowserCoreMessages.jobs__export_odf_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[]
            { browserConnection.getUrl() + "_" + DigestUtils.shaHex( exportOdfFilename ) }; //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_odf_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__export_odf_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        Preferences coreStore = BrowserCorePlugin.getDefault().getPluginPreferences();
        String valueDelimiter = coreStore.getString( BrowserCoreConstants.PREFERENCE_FORMAT_ODF_VALUEDELIMITER );
        int binaryEncoding = coreStore.getInt( BrowserCoreConstants.PREFERENCE_FORMAT_ODF_BINARYENCODING );

        // export
        try
        {
            OdfSpreadsheetDocument doc = OdfSpreadsheetDocument.createSpreadsheetDocument();
            OdfFileDom contentDoc = doc.getContentDom();

            // Remove the default table added in construction
            Element spreadsheetElement = ( Element ) contentDoc.getElementsByTagNameNS( OdfNamespace.OFFICE.getUri(),
                "spreadsheet" ).item( 0 ); //$NON-NLS-1$
            OdfTable table = ( OdfTable ) ( spreadsheetElement.getElementsByTagNameNS( OdfNamespace.TABLE.getUri(),
                "table" ).item( 0 ) ); //$NON-NLS-1$
            table.getParentNode().removeChild( table );

            // create the table
            table = new OdfTable( contentDoc );
            table.setName( "Export" ); //$NON-NLS-1$
            Element officeSpreadsheet = ( Element ) contentDoc.getElementsByTagNameNS( OdfNamespace.OFFICE.getUri(),
                "spreadsheet" ).item( 0 ); //$NON-NLS-1$
            officeSpreadsheet.appendChild( table );

            // header
            OdfTableRow headerRow = new OdfTableRow( contentDoc );
            table.appendChild( headerRow );
            LinkedHashMap<String, Short> attributeNameMap = new LinkedHashMap<String, Short>();
            if ( this.exportDn )
            {
                //                short cellNum = ( short ) 0;
                //attributeNameMap.put( "dn", new Short( cellNum ) ); //$NON-NLS-1$
                OdfTableCell cell = new OdfTableCell( contentDoc );
                cell.setValueType( OdfValueType.STRING );
                cell.setStringValue( "dn" ); //$NON-NLS-1$
                headerRow.appendCell( cell );
            }

            // max export
            if ( searchParameter.getCountLimit() < 1 || searchParameter.getCountLimit() > MAX_COUNT_LIMIT )
            {
                searchParameter.setCountLimit( MAX_COUNT_LIMIT );
            }

            int count = 0;
            exportToOdf( browserConnection, searchParameter, contentDoc, table, headerRow, count, monitor,
                attributeNameMap, valueDelimiter, binaryEncoding, this.exportDn );

            doc.save( exportOdfFilename );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * Exports to ODF.
     * 
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     * @param contentDoc the document
     * @param table the table
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
    private static void exportToOdf( IBrowserConnection browserConnection, SearchParameter searchParameter,
        OdfFileDom contentDoc, OdfTable table, OdfTableRow headerRow, int count, StudioProgressMonitor monitor,
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
                    recordToOdfRow( browserConnection, record, contentDoc, table, headerRow, attributeNameMap,
                        valueDelimiter, binaryEncoding, exportDn );

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
     * Transforms an LDIF record to an OdfTableRow.
     * 
     * @param browserConnection the browser connection
     * @param record the record
     * @param contentDoc the document
     * @param table the table
     * @param headerRow the header row
     * @param headerRowAttributeNameMap the header row attribute name map
     * @param valueDelimiter the value delimiter
     * @param binaryEncoding the binary encoding
     * @param exportDn the export dn
     */
    private static void recordToOdfRow( IBrowserConnection browserConnection, LdifContentRecord record,
        OdfFileDom contentDoc, OdfTable table, OdfTableRow headerRow, Map<String, Short> headerRowAttributeNameMap,
        String valueDelimiter, int binaryEncoding, boolean exportDn )
    {
        // group multi-valued attributes
        Map<String, String> attributeMap = ExportCsvRunnable.getAttributeMap( null, record, valueDelimiter, "UTF-16", //$NON-NLS-1$
            binaryEncoding );

        // output attributes
        OdfTableRow row = new OdfTableRow( contentDoc );
        table.appendChild( row );

        if ( exportDn )
        {
            OdfTableCell cell = new OdfTableCell( contentDoc );
            cell.setValueType( OdfValueType.STRING );
            cell.setStringValue( record.getDnLine().getValueAsString() );
            row.appendCell( cell );

        }
        for ( String attributeName : attributeMap.keySet() )
        {
            if ( !headerRowAttributeNameMap.containsKey( attributeName ) )
            {
                short cellNum = ( short ) headerRowAttributeNameMap.size();
                headerRowAttributeNameMap.put( attributeName, new Short( cellNum ) );

                OdfTableCell cell = new OdfTableCell( contentDoc );
                cell.setValueType( OdfValueType.STRING );
                cell.setStringValue( attributeName );
                headerRow.appendCell( cell );
            }

        }
        for ( String attributeName : headerRowAttributeNameMap.keySet() )
        {
            String value = attributeMap.get( attributeName );
            if ( value == null )
            {
                value = ""; //$NON-NLS-1$
            }
            OdfTableCell cell = new OdfTableCell( contentDoc );
            cell.setValueType( OdfValueType.STRING );
            cell.setStringValue( value );
            row.appendCell( cell );
        }
    }
}
