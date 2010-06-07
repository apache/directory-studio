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
package org.apache.directory.studio.apacheds;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerXmlIOV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerXmlIOV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerXmlIOV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerXmlIOV156;
import org.apache.directory.studio.apacheds.model.Server;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;


/**
 * This class contains helpful methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDsPluginUtils
{
    private static final String RESOURCES = "resources";
    private static final String LIBS = "libs";
    private static final String SERVERS = "servers";
    /** The name of the libraries folder */
    private static final String LIBRARIES_FOLDER_NAME = "libs";
    private static final String APACHEDS = "apacheds";

    /** The line separator */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );


    /**
     * Verifies that the libraries folder exists and contains the jar files 
     * needed to launch the server.
     * 
     * @param server
     *      the server
     */
    public static void verifyLibrariesFolder( Server server )
    {
        IPath stateLocationPath = ApacheDsPlugin.getDefault().getStateLocation();

        // Libraries folder
        IPath librariesFolderPath = stateLocationPath.append( LIBRARIES_FOLDER_NAME );
        File librariesFolder = new File( librariesFolderPath.toOSString() );
        if ( !librariesFolder.exists() )
        {
            librariesFolder.mkdir();
        }

        // Specific Apache DS folder
        IPath apacheDsFolderPath = librariesFolderPath.append( APACHEDS + "-" + server.getVersion() );
        File apacheDsFolder = new File( apacheDsFolderPath.toOSString() );
        if ( !apacheDsFolder.exists() )
        {
            apacheDsFolder.mkdir();
        }

        // Jar libraries
        for ( String apachedsLibraryFilename : getApacheDsLibraries( server ) )
        {
            IPath apachedsLibraryPath = apacheDsFolderPath.append( apachedsLibraryFilename );
            File apachedsLibrary = new File( apachedsLibraryPath.toOSString() );
            if ( !apachedsLibrary.exists() )
            {
                try
                {
                    copyLibrary( apachedsLibraryFilename, apachedsLibrary, server );
                }
                catch ( IOException e )
                {
                    ApacheDsPluginUtils.reportError( "An error occurred when copying the library '"
                        + apachedsLibraryFilename + "' to the location '" + apachedsLibrary.getAbsolutePath()
                        + "'.\n\n" + e.getMessage() );
                }
            }
        }
    }


    /**
     * Gets an array containing the names of the Apache DS libraries, according
     * to the given server.
     *
     * @param server
     *      the server
     * @return
     *      an array containing the names of the Apache DS libraries, according
     * to the given server
     */
    public static String[] getApacheDsLibraries( Server server )
    {
        if ( server != null )
        {
            switch ( server.getVersion() )
            {
                case VERSION_1_5_6:
                    return new String[]
                        { "antlr-2.7.7.jar", "apacheds-avl-partition-1.5.6.jar", "apacheds-core-1.5.6.jar",
                            "apacheds-core-annotations-1.5.6.jar", "apacheds-core-api-1.5.6.jar",
                            "apacheds-core-avl-1.5.6.jar", "apacheds-core-constants-1.5.6.jar",
                            "apacheds-core-entry-1.5.6.jar", "apacheds-core-jndi-1.5.6.jar",
                            "apacheds-core-mock-1.5.6.jar", "apacheds-http-integration-1.5.6.jar",
                            "apacheds-i18n-1.5.6.jar", "apacheds-interceptor-kerberos-1.5.6.jar",
                            "apacheds-jdbm-1.5.6.jar", "apacheds-jdbm-partition-1.5.6.jar",
                            "apacheds-jdbm-store-1.5.6.jar", "apacheds-kerberos-shared-1.5.6.jar",
                            "apacheds-launcher-1.5.0.jar", "apacheds-ldif-partition-1.5.6.jar",
                            "apacheds-protocol-changepw-1.5.6.jar", "apacheds-protocol-dns-1.5.6.jar",
                            "apacheds-protocol-kerberos-1.5.6.jar", "apacheds-protocol-ldap-1.5.6.jar",
                            "apacheds-protocol-ntp-1.5.6.jar", "apacheds-protocol-shared-1.5.6.jar",
                            "apacheds-server-jndi-1.5.6.jar", "apacheds-server-xml-1.5.6.jar",
                            "apacheds-utils-1.5.6.jar", "apacheds-xbean-spring-1.5.6.jar",
                            "apacheds-xdbm-base-1.5.6.jar", "apacheds-xdbm-search-1.5.6.jar",
                            "apacheds-xdbm-tools-1.5.6.jar", "bcprov-jdk15-140.jar", "commons-cli-1.2.jar",
                            "commons-collections-3.2.1.jar", "commons-daemon-1.0.1.jar", "commons-io-1.4.jar",
                            "commons-lang-2.5.jar", "daemon-bootstrappers-1.1.7.jar", "dom4j-1.6.1.jar",
                            "jcl-over-slf4j-1.5.10.jar", "jetty-6.1.14.jar", "jetty-util-6.1.14.jar", "junit-4.7.jar",
                            "log4j-1.2.14.jar", "mina-core-2.0.0-RC1.jar", "servlet-api-2.5-6.1.14.jar",
                            "shared-asn1-0.9.18.jar", "shared-asn1-codec-0.9.18.jar", "shared-cursor-0.9.18.jar",
                            "shared-dsml-parser-0.9.18.jar", "shared-i18n-0.9.18.jar", "shared-ldap-0.9.18.jar",
                            "shared-ldap-constants-0.9.18.jar", "shared-ldap-converter-0.9.18.jar",
                            "shared-ldap-jndi-0.9.18.jar", "shared-ldap-schema-0.9.18.jar",
                            "shared-ldap-schema-dao-0.9.18.jar", "shared-ldap-schema-loader-0.9.18.jar",
                            "shared-ldap-schema-manager-0.9.18.jar", "shared-ldif-0.9.18.jar", "slf4j-api-1.5.10.jar",
                            "slf4j-log4j12-1.5.10.jar", "spring-beans-2.5.6.SEC01.jar",
                            "spring-context-2.5.6.SEC01.jar", "spring-core-2.5.6.SEC01.jar", "xbean-spring-3.5.jar",
                            "xercesImpl-2.9.1.jar", "xpp3-1.1.4c.jar" };
                case VERSION_1_5_5:
                    return new String[]
                        { "antlr-2.7.7.jar", "apacheds-bootstrap-extract-1.5.5.jar",
                            "apacheds-bootstrap-partition-1.5.5.jar", "apacheds-core-1.5.5.jar",
                            "apacheds-core-avl-1.5.5.jar", "apacheds-core-constants-1.5.5.jar",
                            "apacheds-core-entry-1.5.5.jar", "apacheds-core-jndi-1.5.5.jar",
                            "apacheds-core-shared-1.5.5.jar", "apacheds-interceptor-kerberos-1.5.5.jar",
                            "apacheds-jdbm-1.5.5.jar", "apacheds-jdbm-store-1.5.5.jar",
                            "apacheds-kerberos-shared-1.5.5.jar", "apacheds-launcher-1.5.0.jar",
                            "apacheds-protocol-changepw-1.5.5.jar", "apacheds-protocol-dns-1.5.5.jar",
                            "apacheds-protocol-kerberos-1.5.5.jar", "apacheds-protocol-ldap-1.5.5.jar",
                            "apacheds-protocol-ntp-1.5.5.jar", "apacheds-protocol-shared-1.5.5.jar",
                            "apacheds-schema-bootstrap-1.5.5.jar", "apacheds-schema-extras-1.5.5.jar",
                            "apacheds-schema-registries-1.5.5.jar", "apacheds-server-jndi-1.5.5.jar",
                            "apacheds-server-xml-1.5.5.jar", "apacheds-utils-1.5.5.jar",
                            "apacheds-xbean-spring-1.5.5.jar", "apacheds-xdbm-base-1.5.5.jar",
                            "apacheds-xdbm-search-1.5.5.jar", "apacheds-xdbm-tools-1.5.5.jar", "bcprov-jdk15-140.jar",
                            "commons-cli-1.2.jar", "commons-collections-3.2.1.jar", "commons-daemon-1.0.1.jar",
                            "commons-io-1.4.jar", "commons-lang-2.4.jar", "daemon-bootstrappers-1.1.6.jar",
                            "jcl-over-slf4j-1.5.6.jar", "log4j-1.2.14.jar", "mina-core-2.0.0-M6.jar",
                            "shared-asn1-0.9.15.jar", "shared-asn1-codec-0.9.15.jar", "shared-cursor-0.9.15.jar",
                            "shared-ldap-0.9.15.jar", "shared-ldap-constants-0.9.15.jar", "slf4j-api-1.5.6.jar",
                            "slf4j-log4j12-1.5.6.jar", "spring-beans-2.5.6.SEC01.jar",
                            "spring-context-2.5.6.SEC01.jar", "spring-core-2.5.6.SEC01.jar", "xbean-spring-3.5.jar" };
                case VERSION_1_5_4:
                    return new String[]
                        { "antlr-2.7.7.jar", "xbean-spring-3.3.jar", "apacheds-bootstrap-extract-1.5.4.jar",
                            "apacheds-bootstrap-partition-1.5.4.jar", "apacheds-core-1.5.4.jar",
                            "apacheds-core-avl-1.5.4.jar", "apacheds-core-constants-1.5.4.jar",
                            "apacheds-core-cursor-1.5.4.jar", "apacheds-core-entry-1.5.4.jar",
                            "apacheds-core-shared-1.5.4.jar", "apacheds-jdbm-1.5.4.jar",
                            "apacheds-jdbm-store-1.5.4.jar", "apacheds-kerberos-shared-1.5.4.jar",
                            "apacheds-launcher-1.2.0.jar", "apacheds-protocol-changepw-1.5.4.jar",
                            "apacheds-protocol-dns-1.5.4.jar", "apacheds-protocol-kerberos-1.5.4.jar",
                            "apacheds-protocol-ldap-1.5.4.jar", "apacheds-protocol-ntp-1.5.4.jar",
                            "apacheds-protocol-shared-1.5.4.jar", "apacheds-schema-bootstrap-1.5.4.jar",
                            "apacheds-schema-extras-1.5.4.jar", "apacheds-schema-registries-1.5.4.jar",
                            "apacheds-server-jndi-1.5.4.jar", "apacheds-server-xml-1.5.4.jar",
                            "apacheds-utils-1.5.4.jar", "apacheds-xbean-spring-1.5.4.jar",
                            "apacheds-xdbm-base-1.5.4.jar", "apacheds-xdbm-search-1.5.4.jar",
                            "apacheds-xdbm-tools-1.5.4.jar", "commons-cli-1.1.jar", "commons-collections-3.2.jar",
                            "commons-daemon-1.0.1.jar", "commons-lang-2.3.jar", "daemon-bootstrappers-1.1.4.jar",
                            "jcl104-over-slf4j-1.4.3.jar", "log4j-1.2.14.jar", "mina-core-1.1.6.jar",
                            "mina-filter-ssl-1.1.6.jar", "shared-asn1-0.9.12.jar", "shared-asn1-codec-0.9.12.jar",
                            "shared-bouncycastle-reduced-0.9.12.jar", "shared-ldap-0.9.12.jar",
                            "shared-ldap-constants-0.9.12.jar", "slf4j-api-1.4.3.jar", "slf4j-log4j12-1.4.3.jar",
                            "spring-beans-2.0.6.jar", "spring-context-2.0.6.jar", "spring-core-2.0.6.jar" };
                case VERSION_1_5_3:
                    return new String[]
                        { "antlr-2.7.7.jar", "apacheds-bootstrap-extract-1.5.3.jar",
                            "apacheds-bootstrap-partition-1.5.3.jar", "apacheds-btree-base-1.5.3.jar",
                            "apacheds-core-1.5.3.jar", "apacheds-core-constants-1.5.3.jar",
                            "apacheds-core-entry-1.5.3.jar", "apacheds-core-shared-1.5.3.jar",
                            "apacheds-jdbm-1.5.3.jar", "apacheds-jdbm-store-1.5.3.jar",
                            "apacheds-kerberos-shared-1.5.3.jar", "apacheds-launcher-1.2.0.jar",
                            "apacheds-protocol-changepw-1.5.3.jar", "apacheds-protocol-dns-1.5.3.jar",
                            "apacheds-protocol-kerberos-1.5.3.jar", "apacheds-protocol-ldap-1.5.3.jar",
                            "apacheds-protocol-ntp-1.5.3.jar", "apacheds-protocol-shared-1.5.3.jar",
                            "apacheds-schema-bootstrap-1.5.3.jar", "apacheds-schema-extras-1.5.3.jar",
                            "apacheds-schema-registries-1.5.3.jar", "apacheds-server-jndi-1.5.3.jar",
                            "apacheds-server-xml-1.5.3.jar", "apacheds-utils-1.5.3.jar",
                            "apacheds-xbean-spring-1.5.3.jar", "commons-cli-1.1.jar", "commons-collections-3.2.jar",
                            "commons-daemon-1.0.1.jar", "commons-lang-2.3.jar", "daemon-bootstrappers-1.1.3.jar",
                            "jcl104-over-slf4j-1.4.3.jar", "log4j-1.2.14.jar", "mina-core-1.1.6.jar",
                            "mina-filter-ssl-1.1.6.jar", "shared-asn1-0.9.11.jar", "shared-asn1-codec-0.9.11.jar",
                            "shared-bouncycastle-reduced-0.9.11.jar", "shared-ldap-0.9.11.jar",
                            "shared-ldap-constants-0.9.11.jar", "slf4j-api-1.4.3.jar", "slf4j-log4j12-1.4.3.jar",
                            "spring-beans-2.0.6.jar", "spring-context-2.0.6.jar", "spring-core-2.0.6.jar",
                            "xbean-spring-3.3.jar" };
            };
        }

        return new String[0];
    }


    /**
     * Copy the given library.
     *
     * @param library
     *      the name of the library
     * @param destination
     *      the destination
     * @param server
     *      the server
     * @throws IOException
     *      if an error occurrs when copying the jar file
     */
    private static void copyLibrary( String library, File destination, Server server ) throws IOException
    {
        // Getting he URL of the library within the bundle
        URL libraryUrl = FileLocator.find( ApacheDsPlugin.getDefault().getBundle(), new Path( RESOURCES
            + IPath.SEPARATOR + LIBS + IPath.SEPARATOR + APACHEDS + "-" + server.getVersion().toString()
            + IPath.SEPARATOR + library ), null );

        // Creating the input and output streams
        InputStream libraryInputStream = libraryUrl.openStream();
        FileOutputStream libraryOutputStream = new FileOutputStream( destination );

        // Copying the library
        copyFile( libraryInputStream, libraryOutputStream );

        // Closing the streams
        libraryInputStream.close();
        libraryOutputStream.close();
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


    /**
     * Get the path to the Apache DS libraries folder.
     *
     * @param server
     *      the server
     * @return
     *      the path to the Apache DS libraries folder
     */
    public static IPath getApacheDsLibrariesFolder( Server server )
    {
        return ApacheDsPlugin.getDefault().getStateLocation().append( LIBRARIES_FOLDER_NAME ).append(
            APACHEDS + "-" + server.getVersion() );
    }


    /**
     * Get the path to the Apache DS servers folder.
     *
     * @return
     *      the path to the Apache DS server folder
     */
    public static IPath getApacheDsServersFolder()
    {
        return ApacheDsPlugin.getDefault().getStateLocation().append( SERVERS );
    }


    /**
     * Creates a new server folder for the given id.
     *
     * @param id
     *      the id of the server
     */
    public static void createNewServerFolder( String id )
    {
        // Checking if the Apache DS servers folder exists
        checkApacheDsServersFolder();

        // Creating the server folder
        IPath serverFolderPath = getApacheDsServersFolder().append( id );
        File serverFolder = new File( serverFolderPath.toOSString() );
        serverFolder.mkdir();

        // Creating the server sub folders
        File confFolder = new File( serverFolder, "conf" );
        confFolder.mkdir();
        File serverSocketFolder = new File( serverFolder, "serverSocket" );
        serverSocketFolder.mkdir();
        new File( serverFolder, "ldif" ).mkdir();
        new File( serverFolder, "log" ).mkdir();
        new File( serverFolder, "partitions" ).mkdir();

        // Copying default configuration files
        try
        {
            // Creating log4j.properties file
            File log4jPropertiesFile = new File( confFolder, "log4j.properties" );
            createServersLog4jPropertiesFile( new FileOutputStream( log4jPropertiesFile ), 1024, getServerLogsLevel(), // Setting 1024 as default port
                getServerLogsPattern() );

            // Copying server.xml file
            File serverXmlFile = new File( confFolder, "server.xml" );
            copyConfigurationFile( "server-1.5.6.xml", serverXmlFile );

            // Creating log4j.properties file to the serverSocket folder
            File log4jPropertiesServerSocketFile = new File( serverSocketFolder, "log4j.properties" );
            createServerSocketLog4jPropertiesFile( new FileOutputStream( log4jPropertiesServerSocketFile ), id );
        }
        catch ( IOException e )
        {
            ApacheDsPluginUtils
                .reportError( "An error occurred when copying the default configuration files to the server's folder '"
                    + serverFolder.getAbsolutePath() + "'.\n\n" + e.getMessage() );
        }
    }


    /**
     * Create the log4j.properties file for the server.
     *
     * @param os
     *      the {@link OutputStream} to write to
     * @param port
     *      the port
     * @param logsLevel
     *      the logs level
     * @param logsLevel
     *      the logs pattern
     * @throws IOException
     *      if an error occurs when writing to the file
     */
    public static void createServersLog4jPropertiesFile( OutputStream os, int port, String logsLevel, String logsPattern )
        throws IOException
    {
        // Creating the file content in a StringBuffer
        StringBuffer sb = new StringBuffer();
        sb.append( "#############################################################################" ).append( "\n" );
        sb.append( "#    Licensed to the Apache Software Foundation (ASF) under one or more" ).append( "\n" );
        sb.append( "#    contributor license agreements.  See the NOTICE file distributed with" ).append( "\n" );
        sb.append( "#    this work for additional information regarding copyright ownership." ).append( "\n" );
        sb.append( "#    The ASF licenses this file to You under the Apache License, Version 2.0" ).append( "\n" );
        sb.append( "#    (the \"License\"); you may not use this file except in compliance with" ).append( "\n" );
        sb.append( "#    the License.  You may obtain a copy of the License at" ).append( "\n" );
        sb.append( "#" ).append( "\n" );
        sb.append( "#       http://www.apache.org/licenses/LICENSE-2.0" ).append( "\n" );
        sb.append( "#" ).append( "\n" );
        sb.append( "#    Unless required by applicable law or agreed to in writing, software" ).append( "\n" );
        sb.append( "#    distributed under the License is distributed on an \"AS IS\" BASIS," ).append( "\n" );
        sb.append( "#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." ).append( "\n" );
        sb.append( "#    See the License for the specific language governing permissions and" ).append( "\n" );
        sb.append( "#    limitations under the License." ).append( "\n" );
        sb.append( "#############################################################################" ).append( "\n" );
        sb.append( "log4j.rootCategory=" + logsLevel + ", socketAppender, rollingFileAppender" ).append( "\n" );
        sb.append( "" ).append( "\n" );
        sb.append( "# The Socket Appender (used to send the logs to Apache Directory Studio)" ).append( "\n" );
        sb.append( "log4j.appender.socketAppender=org.apache.log4j.net.SocketAppender" ).append( "\n" );
        sb.append( "log4j.appender.socketAppender.RemoteHost=localhost" ).append( "\n" );
        sb.append( "log4j.appender.socketAppender.Port=" ).append( port ).append( "\n" );
        sb.append( "" ).append( "\n" );
        sb.append( "# The Rolling File Appender" ).append( "\n" );
        sb.append( "log4j.appender.rollingFileAppender=org.apache.log4j.RollingFileAppender" ).append( "\n" );
        sb.append( "log4j.appender.rollingFileAppender.File=${apacheds.log.dir}/apacheds-rolling.log" ).append( "\n" );
        sb.append( "log4j.appender.rollingFileAppender.MaxFileSize=1024KB" ).append( "\n" );
        sb.append( "log4j.appender.rollingFileAppender.MaxBackupIndex=5" ).append( "\n" );
        sb.append( "log4j.appender.rollingFileAppender.layout=org.apache.log4j.PatternLayout" ).append( "\n" );
        sb.append( "log4j.appender.rollingFileAppender.layout.ConversionPattern=[%d{HH:mm:ss}] %p [%c] - %m%n" )
            .append( "\n" );
        sb.append( "" ).append( "\n" );
        sb.append( "# with these we'll not get innundated when switching to DEBUG" ).append( "\n" );
        sb.append( "log4j.logger.org.apache.directory.shared.ldap.name=WARN" ).append( "\n" );
        sb.append( "log4j.logger.org.springframework=WARN" ).append( "\n" );
        sb.append( "log4j.logger.org.apache.directory.shared.codec=WARN" ).append( "\n" );
        sb.append( "log4j.logger.org.apache.directory.shared.asn1=WARN" ).append( "\n" );

        // Writing the content to the file
        os.write( sb.toString().getBytes() );
    }


    /**
     * Create the log4j.properties file for the server socket logger.
     *
     * @param os
     *      the {@link OutputStream} to write to
     * @param id
     *      the id of the server
     * @throws IOException
     *      if an error occurs when writing to the file
     */
    private static void createServerSocketLog4jPropertiesFile( OutputStream os, String id ) throws IOException
    {
        // Creating the file content in a StringBuffer
        StringBuffer sb = new StringBuffer();
        sb.append( "#############################################################################" ).append( "\n" );
        sb.append( "#    Licensed to the Apache Software Foundation (ASF) under one or more" ).append( "\n" );
        sb.append( "#    contributor license agreements.  See the NOTICE file distributed with" ).append( "\n" );
        sb.append( "#    this work for additional information regarding copyright ownership." ).append( "\n" );
        sb.append( "#    The ASF licenses this file to You under the Apache License, Version 2.0" ).append( "\n" );
        sb.append( "#    (the \"License\"); you may not use this file except in compliance with" ).append( "\n" );
        sb.append( "#    the License.  You may obtain a copy of the License at" ).append( "\n" );
        sb.append( "#" ).append( "\n" );
        sb.append( "#       http://www.apache.org/licenses/LICENSE-2.0" ).append( "\n" );
        sb.append( "#" ).append( "\n" );
        sb.append( "#    Unless required by applicable law or agreed to in writing, software" ).append( "\n" );
        sb.append( "#    distributed under the License is distributed on an \"AS IS\" BASIS," ).append( "\n" );
        sb.append( "#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied." ).append( "\n" );
        sb.append( "#    See the License for the specific language governing permissions and" ).append( "\n" );
        sb.append( "#    limitations under the License." ).append( "\n" );
        sb.append( "#############################################################################" ).append( "\n" );
        sb.append( "log4j.rootCategory=ALL, studioConsoleAppender" ).append( "\n" );
        sb.append( "" ).append( "\n" );
        sb.append( "# Studio Console Appender (identified with the server id)" ).append( "\n" );
        sb.append( "log4j.appender.studioConsoleAppender=org.apache.directory.studio.apacheds.StudioConsoleAppender" )
            .append( "\n" );
        sb.append( "log4j.appender.studioConsoleAppender.ServerId=" ).append( id ).append( "\n" );
        sb.append( "log4j.appender.studioConsoleAppender.layout=org.apache.log4j.PatternLayout" ).append( "\n" );
        sb.append( "" ).append( "\n" );
        sb.append( "# Hiding logs from log4j" ).append( "\n" );
        sb.append( "log4j.logger.org.apache.log4j.net.SocketServer=OFF" ).append( "\n" );
        sb.append( "log4j.logger.org.apache.log4j.net.SocketNode=OFF" ).append( "\n" );

        // Writing the content to the file
        os.write( sb.toString().getBytes() );
    }


    /**
     * Copies a configuration file.
     *
     * @param inputFilename
     *      the filename
     * @param ouputFile
     *      the output file
     * @throws IOException
     *      if an error occurs when copying
     */
    private static void copyConfigurationFile( String inputFilename, File ouputFile ) throws IOException
    {
        // Getting he URL of the file within the bundle
        URL inputFilenameUrl = FileLocator.find( ApacheDsPlugin.getDefault().getBundle(), new Path( RESOURCES
            + IPath.SEPARATOR + "conf" + IPath.SEPARATOR + inputFilename ), null );

        // Creating the input and output streams
        InputStream inputStream = inputFilenameUrl.openStream();
        OutputStream outputStream = new FileOutputStream( ouputFile );

        // Copying the file
        copyFile( inputStream, outputStream );

        // Closing the streams
        inputStream.close();
        outputStream.close();
    }


    /**
     * Verifies that the Apache DS server folder exists.
     * If it does not exists, it creates it.
     */
    private static void checkApacheDsServersFolder()
    {
        File apacheDsserversFolder = new File( getApacheDsServersFolder().toOSString() );
        if ( !apacheDsserversFolder.exists() )
        {
            apacheDsserversFolder.mkdir();
        }
    }


    /**
     * Gets the server logs level.
     *
     * @return
     *      the server logs level
     */
    public static String getServerLogsLevel()
    {
        String level = ApacheDsPlugin.getDefault().getPreferenceStore().getString(
            ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL );
        if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_DEBUG.equalsIgnoreCase( level ) )
        {
            return "DEBUG";
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_INFO.equalsIgnoreCase( level ) )
        {
            return "INFO";
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_WARN.equalsIgnoreCase( level ) )
        {
            return "WARN";
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_ERROR.equalsIgnoreCase( level ) )
        {
            return "ERROR";
        }
        else if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_LEVEL_FATAL.equalsIgnoreCase( level ) )
        {
            return "FATAL";
        }

        return "";
    }


    /**
     * Gets the server logs pattern.
     *
     * @return
     *      the server logs pattern
     */
    public static String getServerLogsPattern()
    {
        return ApacheDsPlugin.getDefault().getPreferenceStore().getString(
            ApacheDsPluginConstants.PREFS_SERVER_LOGS_PATTERN );
    }


    /**
     * Reports an error.
     *
     * @param message
     *      the message
     */
    public static void reportError( String message )
    {

        MessageDialog dialog = new MessageDialog( ApacheDsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
            .getShell(), "Error!", null, message, MessageDialog.ERROR, new String[]
            { IDialogConstants.OK_LABEL }, MessageDialog.OK );
        dialog.open();
    }


    /**
     * Gets the server configuration.
     *
     * @param server
     *      the server
     * @return
     *      the associated server configuration
     * @throws ServerXmlIOException 
     * @throws ServerXmlIOException
     * @throws IOException 
     */
    public static ServerConfiguration getServerConfiguration( Server server ) throws ServerXmlIOException, IOException
    {
        InputStream fis = new FileInputStream( new File( ApacheDsPluginUtils.getApacheDsServersFolder().append(
            server.getId() ).append( "conf" ).append( "server.xml" ).toOSString() ) );

        // Parsing and returning the server configuration
        switch ( server.getVersion() )
        {
            case VERSION_1_5_6:
                ServerXmlIOV156 serverXmlIOV156 = new ServerXmlIOV156();
                return serverXmlIOV156.parse( fis );
            case VERSION_1_5_5:
                ServerXmlIOV155 serverXmlIOV155 = new ServerXmlIOV155();
                return serverXmlIOV155.parse( fis );
            case VERSION_1_5_4:
                ServerXmlIOV154 serverXmlIOV154 = new ServerXmlIOV154();
                return serverXmlIOV154.parse( fis );
            case VERSION_1_5_3:
                ServerXmlIOV153 serverXmlIOV153 = new ServerXmlIOV153();
                return serverXmlIOV153.parse( fis );
        }

        // No corresponding reader has been found, we return null
        return null;
    }
}
