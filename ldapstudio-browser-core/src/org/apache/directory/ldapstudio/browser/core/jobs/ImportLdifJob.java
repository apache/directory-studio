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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifEnumeration;
import org.apache.directory.ldapstudio.browser.core.model.ldif.parser.LdifParser;


public class ImportLdifJob extends AbstractEclipseJob
{

    private IConnection connection;

    private File ldifFile;

    private File logFile;

    private boolean continueOnError;


    public ImportLdifJob( IConnection connection, File ldifFile, File logFile, boolean continueOnError )
    {
        this.connection = connection;
        this.ldifFile = ldifFile;
        this.logFile = logFile;
        this.continueOnError = continueOnError;

        setName( BrowserCoreMessages.jobs__import_ldif_name );
    }


    public ImportLdifJob( IConnection connection, File ldifFile, boolean continueOnError )
    {
        this( connection, ldifFile, null, continueOnError );
    }


    protected IConnection[] getConnections()
    {
        return new IConnection[]
            { connection };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection.getUrl() + "_" + DigestUtils.shaHex( ldifFile.toString() ) );
        return l.toArray();
    }


    protected void executeAsyncJob( ExtendedProgressMonitor monitor )
    {

        monitor.beginTask( BrowserCoreMessages.jobs__import_ldif_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            Reader ldifReader = new BufferedReader( new FileReader( this.ldifFile ) );
            LdifParser parser = new LdifParser();
            LdifEnumeration enumeration = parser.parse( ldifReader );

            Writer logWriter;
            if ( this.logFile != null )
            {
                logWriter = new BufferedWriter( new FileWriter( this.logFile ) );
            }
            else
            {
                logWriter = new Writer()
                {
                    public void close() throws IOException
                    {
                    }


                    public void flush() throws IOException
                    {
                    }


                    public void write( char[] cbuf, int off, int len ) throws IOException
                    {
                    }
                };
            }

            connection.importLdif( enumeration, logWriter, continueOnError, monitor );

            logWriter.close();
            ldifReader.close();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_ldif_error;
    }

}
