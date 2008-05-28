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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * This class contains helpful methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ApacheDsPluginUtils
{
    private static final String RESOURCES = "resources";
    private static final String LIBS = "libs";
    private static final String INSTANCES = "instances";
    /** The name of the libraries folder */
    private static final String LIBRARIES_FOLDER_NAME = "libs";
    private static final String APACHEDS = "apacheds";
    /** The version of ApacheDS bundle with the plugin */
    private static final String APACHEDS_VERSION = "1.5.2";

    /** The line separator */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    /** This array of String contains all filenames of the needed libraries to launch Apache DS */
    public static final String[] apachedsLibraries = new String[]
        { "antlr-2.7.7.jar", "apacheds-bootstrap-extract-1.5.2.jar", "apacheds-bootstrap-partition-1.5.2.jar",
            "apacheds-btree-base-1.5.2.jar", "apacheds-core-1.5.2.jar", "apacheds-core-constants-1.5.2.jar",
            "apacheds-core-entry-1.5.2.jar", "apacheds-core-shared-1.5.2.jar", "apacheds-jdbm-store-1.5.2.jar",
            "apacheds-kerberos-shared-1.5.2.jar", "apacheds-noarch-installer-1.5.2.jar",
            "apacheds-protocol-changepw-1.5.2.jar", "apacheds-protocol-dns-1.5.2.jar",
            "apacheds-protocol-kerberos-1.5.2.jar", "apacheds-protocol-ldap-1.5.2.jar",
            "apacheds-protocol-ntp-1.5.2.jar", "apacheds-protocol-shared-1.5.2.jar",
            "apacheds-schema-bootstrap-1.5.2.jar", "apacheds-schema-extras-1.5.2.jar",
            "apacheds-schema-registries-1.5.2.jar", "apacheds-server-jndi-1.5.2.jar", "apacheds-server-xml-1.5.2.jar",
            "apacheds-utils-1.5.2.jar", "apacheds-xbean-spring-1.5.2.jar", "commons-cli-1.1.jar",
            "commons-collections-3.2.jar", "commons-daemon-1.0.1.jar", "commons-lang-2.3.jar",
            "daemon-bootstrappers-1.1.3.jar", "jcl104-over-slf4j-1.4.3.jar", "jdbm-1.0.jar", "log4j-1.2.14.jar",
            "mina-core-1.1.2.jar", "mina-filter-ssl-1.1.2.jar", "shared-asn1-0.9.10.jar",
            "shared-asn1-codec-0.9.10.jar", "shared-bouncycastle-reduced-0.9.10.jar", "shared-ldap-0.9.10.jar",
            "shared-ldap-constants-0.9.10.jar", "slf4j-api-1.4.3.jar", "slf4j-log4j12-1.4.3.jar",
            "spring-beans-2.0.6.jar", "spring-context-2.0.6.jar", "spring-core-2.0.6.jar", "xbean-spring-3.3.jar" };


    /**
     * Verifies that the libraries folder exists and contains the jar files 
     * needed to launch the server.
     */
    public static void verifyLibrariesFolder()
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
        IPath apacheDsFolderPath = librariesFolderPath.append( APACHEDS + "-" + APACHEDS_VERSION );
        File apacheDsFolder = new File( apacheDsFolderPath.toOSString() );
        if ( !apacheDsFolder.exists() )
        {
            apacheDsFolder.mkdir();
        }

        // Jar libraries
        for ( String apachedsLibraryFilename : apachedsLibraries )
        {
            IPath apachedsLibraryPath = apacheDsFolderPath.append( apachedsLibraryFilename );
            File apachedsLibrary = new File( apachedsLibraryPath.toOSString() );
            if ( !apachedsLibrary.exists() )
            {
                try
                {
                    copyLibrary( apachedsLibraryFilename, apachedsLibrary );
                }
                catch ( IOException e )
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Copy the given library.
     *
     * @param library
     *      the name of the library
     * @param destination
     *      the destination
     * @throws IOException
     *      if an error occurrs when copying the jar file
     */
    private static void copyLibrary( String library, File destination ) throws IOException
    {
        // Getting he URL of the library within the bundle
        URL libraryUrl = FileLocator.find( ApacheDsPlugin.getDefault().getBundle(), new Path( RESOURCES
            + IPath.SEPARATOR + LIBS + IPath.SEPARATOR + library ), null );

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
     * @return
     *      the path to the Apache DS libraries folder
     */
    public static IPath getApacheDsLibrariesFolder()
    {
        return ApacheDsPlugin.getDefault().getStateLocation().append( LIBRARIES_FOLDER_NAME ).append(
            APACHEDS + "-" + APACHEDS_VERSION );
    }


    /**
     * Get the path to the Apache DS instances folder.
     *
     * @return
     *      the path to the Apache DS instances folder
     */
    public static IPath getApacheDsInstancesFolder()
    {
        return ApacheDsPlugin.getDefault().getStateLocation().append( INSTANCES );
    }


    /**
     * Creates a new instance folder for the given id.
     *
     * @param id
     *      the id of the instance
     */
    public static void createNewInstanceFolder( String id )
    {
        // Checking if the Apache DS instance folder exists
        checkApacheDsInstancesFolder();

        // Creating the instance folder
        IPath instanceFolderPath = getApacheDsInstancesFolder().append( id );
        File instanceFolder = new File( instanceFolderPath.toOSString() );
        instanceFolder.mkdir();

        // Creating the instance sub folders
        File confFolder = new File( instanceFolder, "conf" );
        confFolder.mkdir();
        File serverSocketFolder = new File( instanceFolder, "serverSocket" );
        serverSocketFolder.mkdir();
        new File( instanceFolder, "ldif" ).mkdir();
        new File( instanceFolder, "log" ).mkdir();
        new File( instanceFolder, "partitions" ).mkdir();

        // Copying default configuration files
        try
        {
            // Copying apacheds.conf file
            File apachedsConfFile = new File( confFolder, "apacheds.conf" );
            copyConfigurationFile( "apacheds.conf", apachedsConfFile );

            // Creating log4j.properties file
            File log4jPropertiesFile = new File( confFolder, "log4j.properties" );
            createServerInstanceLog4jPropertiesFile( new FileOutputStream( log4jPropertiesFile ), 1024 ); // Setting 1024 as default port

            // Copying server.xml file
            File serverXmlFile = new File( confFolder, "server.xml" );
            copyConfigurationFile( "server.xml", serverXmlFile );

            // Creating log4j.properties file to the serverSocket folder
            File log4jPropertiesServerSocketFile = new File( serverSocketFolder, "log4j.properties" );
            createServerSocketLog4jPropertiesFile( new FileOutputStream( log4jPropertiesServerSocketFile ), id );
        }
        catch ( IOException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Create the log4j.properties file for the server socket logger.
     *
     * @param os
     *      the {@link OutputStream} to write to
     * @throws IOException
     *      if an error occurs when writing to the file
     */
    public static void createServerInstanceLog4jPropertiesFile( OutputStream os, int port ) throws IOException
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
        sb.append( "log4j.rootCategory=WARN, socketAppender" ).append( "\n" );
        sb.append( "" ).append( "\n" );
        sb.append( "log4j.appender.socketAppender=org.apache.log4j.net.SocketAppender" ).append( "\n" );
        sb.append( "log4j.appender.socketAppender.RemoteHost=localhost" ).append( "\n" );
        sb.append( "log4j.appender.socketAppender.Port=" ).append( port ).append( "\n" );
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
     *      the id of the server instance
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
        sb.append( "# Studio Console Appender (identified with the server instance id)" ).append( "\n" );
        sb
            .append(
                "log4j.appender.studioConsoleAppender=org.apache.directory.studio.apacheds.StudioConsoleAppender" )
            .append( "\n" );
        sb.append( "log4j.appender.studioConsoleAppender.ServerInstanceId=" ).append( id ).append( "\n" );
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
     * Verifies that the Apache DS instances folder exists.
     * If it does not exists, it creates it.
     */
    private static void checkApacheDsInstancesFolder()
    {
        File apacheDsInstancesFolder = new File( getApacheDsInstancesFolder().toOSString() );
        if ( !apacheDsInstancesFolder.exists() )
        {
            apacheDsInstancesFolder.mkdir();
        }
    }
}
