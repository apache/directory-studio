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
import java.util.List;

import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.PagedResultsResponseControl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.connection.core.StudioPagedResultsControl;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.AttributeComparator;
import org.apache.directory.studio.ldapbrowser.core.utils.JNDIUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifVersionLine;


/**
 * Runnable to export directory content to an LDIF file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExportLdifRunnable implements StudioConnectionRunnableWithProgress
{
    /** The filename of the LDIF file. */
    private String exportLdifFilename;

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The search parameter. */
    private SearchParameter searchParameter;


    /**
     * Creates a new instance of ExportLdifRunnable.
     * 
     * @param exportLdifFilename the filename of the LDIF file
     * @param browserConnection the browser connection
     * @param searchParameter the search parameter
     */
    public ExportLdifRunnable( String exportLdifFilename, IBrowserConnection browserConnection,
        SearchParameter searchParameter )
    {
        this.exportLdifFilename = exportLdifFilename;
        this.browserConnection = browserConnection;
        this.searchParameter = searchParameter;
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
        return BrowserCoreMessages.jobs__export_ldif_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( exportLdifFilename ) ); //$NON-NLS-1$
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__export_ldif_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
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
            export( browserConnection, searchParameter, bufferedWriter, count, monitor );

            // close file
            bufferedWriter.close();
            fileWriter.close();

        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    private static void export( IBrowserConnection browserConnection, SearchParameter searchParameter,
        BufferedWriter bufferedWriter, int count, StudioProgressMonitor monitor ) throws IOException
    {
        try
        {
            JndiLdifEnumeration enumeration = search( browserConnection, searchParameter, monitor );
            LdifFormatParameters ldifFormatParameters = Utils.getLdifFormatParameters();

            // add version spec
            if ( BrowserCorePlugin.getDefault().getPluginPreferences()
                .getBoolean( BrowserCoreConstants.PREFERENCE_LDIF_INCLUDE_VERSION_LINE ) )
            {
                LdifVersionLine ldifVersionLine = LdifVersionLine.create();
                String ldifVersionLineString = ldifVersionLine.toFormattedString( ldifFormatParameters );
                bufferedWriter.write( ldifVersionLineString );
                LdifSepLine ldifSepLine = LdifSepLine.create();
                String ldifSepLineString = ldifSepLine.toFormattedString( ldifFormatParameters );
                bufferedWriter.write( ldifSepLineString );
            }

            // add the records
            while ( !monitor.isCanceled() && !monitor.errorsReported() && enumeration.hasNext() )
            {
                LdifContainer container = enumeration.next();

                if ( container instanceof LdifContentRecord )
                {
                    LdifContentRecord record = ( LdifContentRecord ) container;
                    LdifDnLine dnLine = record.getDnLine();
                    LdifSepLine sepLine = record.getSepLine();

                    // sort and format
                    DummyEntry entry = ModelConverter.ldifContentRecordToEntry( record, browserConnection );
                    List<IValue> sortedValues = AttributeComparator.toSortedValues( entry );
                    LdifContentRecord newRecord = new LdifContentRecord( dnLine );
                    for ( IValue value : sortedValues )
                    {
                        newRecord.addAttrVal( ModelConverter.valueToLdifAttrValLine( value ) );
                    }
                    newRecord.finish( sepLine );
                    String s = newRecord.toFormattedString( ldifFormatParameters );

                    // String s = record.toFormattedString();
                    bufferedWriter.write( s );

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
                // ignore
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


    static JndiLdifEnumeration search( IBrowserConnection browserConnection, SearchParameter parameter,
        StudioProgressMonitor monitor )
    {
        StudioNamingEnumeration result = SearchRunnable.search( browserConnection, parameter, monitor );
        return new JndiLdifEnumeration( result, browserConnection, parameter, monitor );
    }

    static class JndiLdifEnumeration implements LdifEnumeration
    {

        private StudioNamingEnumeration enumeration;

        private IBrowserConnection browserConnection;

        private SearchParameter parameter;

        private StudioProgressMonitor monitor;


        public JndiLdifEnumeration( StudioNamingEnumeration enumeration, IBrowserConnection browserConnection,
            SearchParameter parameter, StudioProgressMonitor monitor )
        {
            this.enumeration = enumeration;
            this.browserConnection = browserConnection;
            this.parameter = parameter;
            this.monitor = monitor;
        }


        public boolean hasNext() throws NamingException
        {
            if ( enumeration != null )
            {
                if ( enumeration.hasMore() )
                {
                    return true;
                }

                Control[] jndiControls = enumeration.getResponseControls();
                if ( jndiControls != null )
                {
                    for ( Control jndiControl : jndiControls )
                    {
                        if ( jndiControl instanceof PagedResultsResponseControl )
                        {
                            PagedResultsResponseControl prrc = ( PagedResultsResponseControl ) jndiControl;
                            byte[] cookie = prrc.getCookie();
                            if ( cookie != null )
                            {
                                // search again: pass the response cookie to the request control
                                for ( StudioControl studioControl : parameter.getControls() )
                                {
                                    if ( studioControl instanceof StudioPagedResultsControl )
                                    {
                                        StudioPagedResultsControl sprc = ( StudioPagedResultsControl ) studioControl;
                                        sprc.setCookie( cookie );
                                    }
                                }
                                enumeration = SearchRunnable.search( browserConnection, parameter, monitor );
                                return enumeration != null && enumeration.hasMore();
                            }
                        }
                    }
                }
            }

            return false;
        }


        public LdifContainer next() throws NamingException, LdapInvalidDnException
        {
            Entry entry = enumeration.next().getEntry();
            Dn dn = entry.getDn();
            LdifContentRecord record = LdifContentRecord.create( dn.getName() );

            for ( Attribute attribute : entry )
            {
                String attributeName = attribute.getUpId();
                for ( Value value : attribute )
                {
                    if ( value.isHumanReadable() )
                    {
                        record.addAttrVal( LdifAttrValLine.create( attributeName, value.getValue() ) );
                    }
                    else
                    {
                        record.addAttrVal( LdifAttrValLine.create( attributeName, value.getBytes() ) );
                    }
                }
            }

            record.finish( LdifSepLine.create() );

            return record;
        }

    }
}
