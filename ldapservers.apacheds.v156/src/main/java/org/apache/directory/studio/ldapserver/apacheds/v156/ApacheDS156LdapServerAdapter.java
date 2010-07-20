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
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.CommonUiUtils;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;


/**
 * This class implements an LDAP Server Adapter for ApacheDS version 1.5.6.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDS156LdapServerAdapter implements LdapServerAdapter
{
    // Various strings constants used in paths
    private static final String SERVER_XML = "server.xml";
    private static final String LOG4J_PROPERTIES = "log4j.properties";
    private static final String RESOURCES = "resources";
    private static final String LIBS = "libs";
    private static final String CONF = "conf";

    /** The array of libraries names */
    private static final String[] libraries = new String[]
        { "antlr-2.7.7.jar", "apacheds-avl-partition-1.5.6.jar", "apacheds-core-1.5.6.jar",
            "apacheds-core-annotations-1.5.6.jar", "apacheds-core-api-1.5.6.jar", "apacheds-core-avl-1.5.6.jar",
            "apacheds-core-constants-1.5.6.jar", "apacheds-core-entry-1.5.6.jar", "apacheds-core-jndi-1.5.6.jar",
            "apacheds-core-mock-1.5.6.jar", "apacheds-http-integration-1.5.6.jar", "apacheds-i18n-1.5.6.jar",
            "apacheds-interceptor-kerberos-1.5.6.jar", "apacheds-jdbm-1.5.6.jar", "apacheds-jdbm-partition-1.5.6.jar",
            "apacheds-jdbm-store-1.5.6.jar", "apacheds-kerberos-shared-1.5.6.jar", "apacheds-launcher-1.5.0.jar",
            "apacheds-ldif-partition-1.5.6.jar", "apacheds-protocol-changepw-1.5.6.jar",
            "apacheds-protocol-dns-1.5.6.jar", "apacheds-protocol-kerberos-1.5.6.jar",
            "apacheds-protocol-ldap-1.5.6.jar", "apacheds-protocol-ntp-1.5.6.jar",
            "apacheds-protocol-shared-1.5.6.jar", "apacheds-server-jndi-1.5.6.jar", "apacheds-server-xml-1.5.6.jar",
            "apacheds-utils-1.5.6.jar", "apacheds-xbean-spring-1.5.6.jar", "apacheds-xdbm-base-1.5.6.jar",
            "apacheds-xdbm-search-1.5.6.jar", "apacheds-xdbm-tools-1.5.6.jar", "bcprov-jdk15-140.jar",
            "commons-cli-1.2.jar", "commons-collections-3.2.1.jar", "commons-daemon-1.0.1.jar", "commons-io-1.4.jar",
            "commons-lang-2.5.jar", "daemon-bootstrappers-1.1.7.jar", "dom4j-1.6.1.jar", "jcl-over-slf4j-1.5.10.jar",
            "jetty-6.1.14.jar", "jetty-util-6.1.14.jar", "junit-4.7.jar", "log4j-1.2.14.jar",
            "mina-core-2.0.0-RC1.jar", "servlet-api-2.5-6.1.14.jar", "shared-asn1-0.9.18.jar",
            "shared-asn1-codec-0.9.18.jar", "shared-cursor-0.9.18.jar", "shared-dsml-parser-0.9.18.jar",
            "shared-i18n-0.9.18.jar", "shared-ldap-0.9.18.jar", "shared-ldap-constants-0.9.18.jar",
            "shared-ldap-converter-0.9.18.jar", "shared-ldap-jndi-0.9.18.jar", "shared-ldap-schema-0.9.18.jar",
            "shared-ldap-schema-dao-0.9.18.jar", "shared-ldap-schema-loader-0.9.18.jar",
            "shared-ldap-schema-manager-0.9.18.jar", "shared-ldif-0.9.18.jar", "slf4j-api-1.5.10.jar",
            "slf4j-log4j12-1.5.10.jar", "spring-beans-2.5.6.SEC01.jar", "spring-context-2.5.6.SEC01.jar",
            "spring-core-2.5.6.SEC01.jar", "xbean-spring-3.5.jar", "xercesImpl-2.9.1.jar", "xpp3-1.1.4c.jar" };


    /**
     * {@inheritDoc}
     */
    public void add( LdapServer server, IProgressMonitor monitor ) throws Exception
    {
        // Verifying and copying ApacheDS 1.5.6 libraries
        monitor.subTask( "verifying and copying ApacheDS 1.5.6 libraries" );
        verifyAndCopyLibraries();

        // Creating server folder structure
        monitor.subTask( "creating server folder structure" );
        File serverFolder = LdapServersManager.getServerFolder( server ).toFile();
        File confFolder = new File( serverFolder, "conf" );
        confFolder.mkdir();
        File ldifFolder = new File( serverFolder, "ldif" );
        ldifFolder.mkdir();
        File logFolder = new File( serverFolder, "log" );
        logFolder.mkdir();
        File partitionFolder = new File( serverFolder, "partitions" );
        partitionFolder.mkdir();

        // Copying configuration files
        monitor.subTask( "copying configuration files" );
        IPath resourceConfFolderPath = new Path( RESOURCES ).append( CONF );
        copyResource( resourceConfFolderPath.append( SERVER_XML ), new File( confFolder, SERVER_XML ) );
        copyResource( resourceConfFolderPath.append( LOG4J_PROPERTIES ), new File( confFolder, LOG4J_PROPERTIES ) );
    }


    /**
     * {@inheritDoc}
     */
    public void delete( LdapServer server ) throws Exception
    {
        System.out.println( "delete " + server.getName() );
    }


    /**
     * {@inheritDoc}
     */
    public void start( LdapServer server, IProgressMonitor monitor ) throws Exception
    {
        launchApacheDS( server );

        server.setStatus( LdapServerStatus.STARTED );
    }


    /**
     * Launches Apache DS using a launch configuration.
     */
    private void launchApacheDS( LdapServer server )
    {
        try
        {
            // Getting the default VM installation
            IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();

            // Creating a new editable launch configuration
            ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
                .getLaunchConfigurationType( IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION );
            ILaunchConfigurationWorkingCopy workingCopy = type.newInstance( null,
                NLS.bind( "Starting {0}", new String[]
                    { server.getName() } ) );

            // Setting the JRE container path attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, vmInstall
                .getInstallLocation().toString() );

            // Setting the main type attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
                "org.apache.directory.studio.apacheds.Launcher" ); //$NON-NLS-1$

            // Creating the classpath list
            List<String> classpath = new ArrayList<String>();
            IPath apacheDsLibrariesFolder = getServerLibrariesFolder();
            for ( String library : libraries )
            {
                IRuntimeClasspathEntry libraryClasspathEntry = JavaRuntime
                    .newArchiveRuntimeClasspathEntry( apacheDsLibrariesFolder.append( library ) );
                libraryClasspathEntry.setClasspathProperty( IRuntimeClasspathEntry.USER_CLASSES );

                classpath.add( libraryClasspathEntry.getMemento() );
            }

            // Setting the classpath type attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classpath );

            // Setting the default classpath type attribute to false
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false );

            // The server folder path
            IPath serverFolderPath = LdapServersManager.getServerFolder( server );

            // Setting the program arguments attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "\"" //$NON-NLS-1$
                + serverFolderPath.toOSString() + "\"" ); //$NON-NLS-1$

            // Creating the VM arguments string
            StringBuffer vmArguments = new StringBuffer();
            vmArguments.append( "-Dlog4j.configuration=file:\"" //$NON-NLS-1$
                + serverFolderPath.append( "conf" ).append( "log4j.properties" ).toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            vmArguments.append( " " ); //$NON-NLS-1$
            vmArguments.append( "-Dapacheds.var.dir=\"" + serverFolderPath.toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
            vmArguments.append( " " ); //$NON-NLS-1$
            vmArguments.append( "-Dapacheds.log.dir=\"" + serverFolderPath.append( "log" ).toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            vmArguments.append( " " ); //$NON-NLS-1$
            vmArguments.append( "-Dapacheds.run.dir=\"" + serverFolderPath.append( "run" ).toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            vmArguments.append( " " ); //$NON-NLS-1$
            vmArguments.append( "-Dapacheds.instance=\"" + server.getName() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$

            // Setting the VM arguments attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments.toString() );

            // Setting the launch configuration as private
            workingCopy.setAttribute( IDebugUIConstants.ATTR_PRIVATE, true );

            // Indicating that we don't want any console to show up
            workingCopy.setAttribute( DebugPlugin.ATTR_CAPTURE_OUTPUT, false );

            // Saving the launch configuration
            ILaunchConfiguration configuration = workingCopy.doSave();

            // Launching the launch configuration
            ILaunch launch = configuration.launch( ILaunchManager.RUN_MODE, new NullProgressMonitor() );
        }
        catch ( CoreException e )
        {
            CommonUiUtils.reportError( NLS.bind( "An error occurred when launching the server.\n\n{0}", new String[]
                { e.getMessage() } ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void stop( LdapServer server, IProgressMonitor monitor ) throws Exception
    {
        System.out.println( "stop " + server.getName() );

        Thread.sleep( 3000 );

        server.setStatus( LdapServerStatus.STOPPED );
    }


    /**
     * Verifies that the libraries folder exists and contains the jar files 
     * needed to launch the server.
     */
    public static void verifyAndCopyLibraries()
    {
        // Source libraries folder
        IPath sourceLibrariesPath = new Path( RESOURCES ).append( LIBS );

        // Destination libraries folder
        IPath destinationLibrariesFolderPath = getServerLibrariesFolder();
        File destinationLibrariesFolder = destinationLibrariesFolderPath.toFile();
        if ( !destinationLibrariesFolder.exists() )
        {
            destinationLibrariesFolder.mkdir();
        }

        // Verifying and copying libraries (if needed)
        for ( String library : libraries )
        {
            File destinationLibraryFile = destinationLibrariesFolderPath.append( library ).toFile();
            if ( !destinationLibraryFile.exists() )
            {
                try
                {
                    copyResource( sourceLibrariesPath.append( library ), destinationLibraryFile );
                }
                catch ( IOException e )
                {
                    CommonUiUtils.reportError( "An error occurred when copying the library '" + library
                        + "' to the location '" + destinationLibraryFile.getAbsolutePath() + "'.\n\n" + e.getMessage() );
                }
            }
        }
    }


    private static IPath getServerLibrariesFolder()
    {
        return ApacheDS156Plugin.getDefault().getStateLocation().append( LIBS );
    }


    /**
    * Copy the given resource.
    *
    * @param resource
    *      the path of the resource
    * @param destination
    *      the destination
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
