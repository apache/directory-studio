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
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.parser.LdifParser;


public class ExecuteLdifJob extends AbstractEclipseJob
{

    private IBrowserConnection connection;

    private String ldif;

    private boolean continueOnError;


    public ExecuteLdifJob( IBrowserConnection connection, String ldif, boolean continueOnError )
    {
        this.connection = connection;
        this.ldif = ldif;
        this.continueOnError = continueOnError;

        setName( BrowserCoreMessages.jobs__execute_ldif_name );
    }


    protected Connection[] getConnections()
    {
        return new Connection[]
            { connection.getConnection() };
    }


    protected Object[] getLockedObjects()
    {
        List l = new ArrayList();
        l.add( connection.getUrl() + "_" + DigestUtils.shaHex( ldif ) );
        return l.toArray();
    }


    protected void executeAsyncJob( StudioProgressMonitor monitor )
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
        return BrowserCoreMessages.jobs__execute_ldif_error;
    }

}
