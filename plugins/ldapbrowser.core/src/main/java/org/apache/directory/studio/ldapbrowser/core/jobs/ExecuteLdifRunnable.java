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


import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.parser.LdifParser;


/**
 * Runnable to execute an LDIF.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExecuteLdifRunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The LDIF to execute. */
    private String ldif;

    /** The update if entry exists flag. */
    private boolean updateIfEntryExists;

    /** The continue on error flag. */
    private boolean continueOnError;


    /**
     * Creates a new instance of ExecuteLdifJob.
     * 
     * @param browserConnection the browser connection
     * @param ldif the LDIF to execute
     * @param continueOnError the continue on error flag
     */
    public ExecuteLdifRunnable( IBrowserConnection browserConnection, String ldif, boolean updateIfEntryExists,
        boolean continueOnError )
    {
        this.browserConnection = browserConnection;
        this.ldif = ldif;
        this.continueOnError = continueOnError;
        this.updateIfEntryExists = updateIfEntryExists;
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
        return BrowserCoreMessages.jobs__execute_ldif_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( ldif ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__execute_ldif_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__execute_ldif_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            Reader ldifReader = new StringReader( this.ldif );
            LdifParser parser = new LdifParser();
            LdifEnumeration enumeration = parser.parse( ldifReader );

            Writer logWriter = new Writer()
            {
                public void close()
                {
                }


                public void flush()
                {
                }


                public void write( char[] cbuf, int off, int len )
                {
                }
            };

            ImportLdifRunnable.importLdif( browserConnection, enumeration, logWriter, updateIfEntryExists,
                continueOnError, monitor );

            logWriter.close();
            ldifReader.close();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        EventRegistry.fireEntryUpdated( new BulkModificationEvent( browserConnection ), this );
    }
}
