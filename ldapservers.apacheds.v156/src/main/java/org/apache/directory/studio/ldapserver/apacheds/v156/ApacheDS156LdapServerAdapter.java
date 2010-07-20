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

package org.apache.directory.studio.ldapserver.apacheds.v156;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.directory.studio.common.CommonUiUtils;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;


/**
 * This class implements an LDAP Server Adapter for ApacheDS version 1.5.6.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDS156LdapServerAdapter implements LdapServerAdapter
{
    public void add( LdapServer server, IProgressMonitor monitor ) throws Exception
    {
        //
        monitor.subTask( "verifying ApacheDS 1.5.6 libraries" );
        verifyLibrariesFolder();

        monitor.subTask( "copying ApacheDS 1.5.6 libraries" );

        monitor.subTask( "creating server folder struture" );
        File serverFolder = LdapServersManager.getServerFolder( server ).toFile();
        File confFolder = new File( serverFolder, "conf" );
        confFolder.mkdir();
        //        File serverSocketFolder = new File( serverFolder, "serverSocket" );
        //        serverSocketFolder.mkdir();
        File ldifFolder = new File( serverFolder, "ldif" );
        ldifFolder.mkdir();
        File logFolder = new File( serverFolder, "log" );
        logFolder.mkdir();
        File partitionFolder = new File( serverFolder, "partitions" );
        partitionFolder.mkdir();

        // Checking if the Apache DS servers folder exists

        // Creating the server folder
        //        IPath serverFolderPath = getApacheDsServersFolder().append( id );
        //        File serverFolder = new File( serverFolderPath.toOSString() );
        //        serverFolder.mkdir();
        //
        //        // Creating the server sub folders ('conf', 'ldif', 'log' and 'partitions')
        //
        //        // Copying default configuration files
        //        try
        //        {
        //            // Creating log4j.properties file
        //            File log4jPropertiesFile = new File( confFolder, "log4j.properties" );
        //            createServersLog4jPropertiesFile( new FileOutputStream( log4jPropertiesFile ), 1024, getServerLogsLevel(), // Setting 1024 as default port
        //                getServerLogsPattern() );
        //
        //            // Copying server.xml file
        //            File serverXmlFile = new File( confFolder, "server.xml" );
        //            copyConfigurationFile( "server-1.5.6.xml", serverXmlFile );
        //
        //            // Creating log4j.properties file to the serverSocket folder
        //            File log4jPropertiesServerSocketFile = new File( serverSocketFolder, "log4j.properties" );
        //            createServerSocketLog4jPropertiesFile( new FileOutputStream( log4jPropertiesServerSocketFile ), id );
        //        }
        //        catch ( IOException e )
        //        {
        //            ApacheDsPluginUtils
        //                .reportError( "An error occurred when copying the default configuration files to the server's folder '"
        //                    + serverFolder.getAbsolutePath() + "'.\n\n" + e.getMessage() );
        //        }
    }


    public void delete( LdapServer server ) throws Exception
    {
        System.out.println( "delete " + server.getName() );
    }


    public void start( LdapServer server, IProgressMonitor monitor ) throws Exception
    {
        System.out.println( "start " + server.getName() );

        Thread.sleep( 3000 );

        server.setStatus( LdapServerStatus.STARTED );
    }


    public void stop( LdapServer server, IProgressMonitor monitor ) throws Exception
    {
        System.out.println( "stop " + server.getName() );

        Thread.sleep( 3000 );

        server.setStatus( LdapServerStatus.STOPPED );
    }


    /**
     * TODO getLibraries.
     *
     * @return
     */
    private static String[] getLibraries()
    {
        return new String[]
            { "antlr-2.7.7.jar", "apacheds-avl-partition-1.5.6.jar", "apacheds-core-1.5.6.jar",
                "apacheds-core-annotations-1.5.6.jar", "apacheds-core-api-1.5.6.jar", "apacheds-core-avl-1.5.6.jar",
                "apacheds-core-constants-1.5.6.jar", "apacheds-core-entry-1.5.6.jar", "apacheds-core-jndi-1.5.6.jar",
                "apacheds-core-mock-1.5.6.jar", "apacheds-http-integration-1.5.6.jar", "apacheds-i18n-1.5.6.jar",
                "apacheds-interceptor-kerberos-1.5.6.jar", "apacheds-jdbm-1.5.6.jar",
                "apacheds-jdbm-partition-1.5.6.jar", "apacheds-jdbm-store-1.5.6.jar",
                "apacheds-kerberos-shared-1.5.6.jar", "apacheds-launcher-1.5.0.jar",
                "apacheds-ldif-partition-1.5.6.jar", "apacheds-protocol-changepw-1.5.6.jar",
                "apacheds-protocol-dns-1.5.6.jar", "apacheds-protocol-kerberos-1.5.6.jar",
                "apacheds-protocol-ldap-1.5.6.jar", "apacheds-protocol-ntp-1.5.6.jar",
                "apacheds-protocol-shared-1.5.6.jar", "apacheds-server-jndi-1.5.6.jar",
                "apacheds-server-xml-1.5.6.jar", "apacheds-utils-1.5.6.jar", "apacheds-xbean-spring-1.5.6.jar",
                "apacheds-xdbm-base-1.5.6.jar", "apacheds-xdbm-search-1.5.6.jar", "apacheds-xdbm-tools-1.5.6.jar",
                "bcprov-jdk15-140.jar", "commons-cli-1.2.jar", "commons-collections-3.2.1.jar",
                "commons-daemon-1.0.1.jar", "commons-io-1.4.jar", "commons-lang-2.5.jar",
                "daemon-bootstrappers-1.1.7.jar", "dom4j-1.6.1.jar", "jcl-over-slf4j-1.5.10.jar", "jetty-6.1.14.jar",
                "jetty-util-6.1.14.jar", "junit-4.7.jar", "log4j-1.2.14.jar", "mina-core-2.0.0-RC1.jar",
                "servlet-api-2.5-6.1.14.jar", "shared-asn1-0.9.18.jar", "shared-asn1-codec-0.9.18.jar",
                "shared-cursor-0.9.18.jar", "shared-dsml-parser-0.9.18.jar", "shared-i18n-0.9.18.jar",
                "shared-ldap-0.9.18.jar", "shared-ldap-constants-0.9.18.jar", "shared-ldap-converter-0.9.18.jar",
                "shared-ldap-jndi-0.9.18.jar", "shared-ldap-schema-0.9.18.jar", "shared-ldap-schema-dao-0.9.18.jar",
                "shared-ldap-schema-loader-0.9.18.jar", "shared-ldap-schema-manager-0.9.18.jar",
                "shared-ldif-0.9.18.jar", "slf4j-api-1.5.10.jar", "slf4j-log4j12-1.5.10.jar",
                "spring-beans-2.5.6.SEC01.jar", "spring-context-2.5.6.SEC01.jar", "spring-core-2.5.6.SEC01.jar",
                "xbean-spring-3.5.jar", "xercesImpl-2.9.1.jar", "xpp3-1.1.4c.jar" };
    }

    private static final String RESOURCES = "resources";
    private static final String LIBS = "libs";

    /** The name of the libraries folder */
    private static final String LIBRARIES_FOLDER_NAME = "libs";


    /**
     * Verifies that the libraries folder exists and contains the jar files 
     * needed to launch the server.
     * 
     * @param server
     *      the server
     */
    public static void verifyLibrariesFolder()
    {
        // Libraries folder
        IPath librariesFolderPath = ApacheDS156Plugin.getDefault().getStateLocation().append( LIBRARIES_FOLDER_NAME );
        File librariesFolder = new File( librariesFolderPath.toOSString() );
        if ( !librariesFolder.exists() )
        {
            librariesFolder.mkdir();
        }

        // Creating the path for the libs folder
        IPath libsFolderPath = new Path( RESOURCES ).append( LIBS );

        // Jar libraries
        for ( String libraryFilename : getLibraries() )
        {
            IPath apachedsLibraryPath = librariesFolderPath.append( libraryFilename );
            File apachedsLibrary = new File( apachedsLibraryPath.toOSString() );
            if ( !apachedsLibrary.exists() )
            {
                try
                {
                    copyResource( libsFolderPath.append( libraryFilename ), apachedsLibrary );
                }
                catch ( IOException e )
                {
                    CommonUiUtils.reportError( "An error occurred when copying the library '" + libraryFilename
                        + "' to the location '" + apachedsLibrary.getAbsolutePath() + "'.\n\n" + e.getMessage() );
                }
            }
        }
    }


    /**
    * Copy the given resource.
    *
    * @param resource
    *      the path of the resource
    * @param destination
    *      the destination
    * @param server
    *      the server
    * @throws IOException
    *      if an error occurs when copying the jar file
    */
    private static void copyResource( IPath resource, File destination ) throws IOException
    {
        // Getting he URL of the resource within the bundle
        URL resourceUrl = FileLocator.find( ApacheDS156Plugin.getDefault().getBundle(), resource, null );

        // Creating the input and output streams
        InputStream resourceInputStream = resourceUrl.openStream();
        FileOutputStream resourceOutputStream = new FileOutputStream( destination );

        // Copying the resource
        copyFile( resourceInputStream, resourceOutputStream );

        // Closing the streams
        resourceInputStream.close();
        resourceOutputStream.close();
    }


    /**
     * Copies a file from the given streams.
     *
     * @param inputStream
     *      the input stream
     * @param outputStream
     *      the output stream
     * @throws IOException
     *      if an error occurs when copying the file
     */
    private static void copyFile( InputStream inputStream, OutputStream outputStream ) throws IOException
    {
        byte[] buf = new byte[1024];
        int i = 0;
        while ( ( i = inputStream.read( buf ) ) != -1 )
        {
            outputStream.write( buf, 0, i );
        }
    }
}
