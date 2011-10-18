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

package org.apache.directory.studio.ldapservers.apacheds.v156;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerConfigurationV156;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerXmlIOV156;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.filesystem.PathEditorInput;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.LdapServersUtils;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;


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
    public void add( LdapServer server, StudioProgressMonitor monitor ) throws Exception
    {
        // Getting the bundle associated with the plugin
        Bundle bundle = ApacheDS156Plugin.getDefault().getBundle();

        // Verifying and copying ApacheDS 1.5.6 libraries
        monitor.subTask( "verifying and copying ApacheDS 1.5.6 libraries" );
        LdapServersUtils.verifyAndCopyLibraries( bundle, new Path( RESOURCES ).append( LIBS ),
            getServerLibrariesFolder(), libraries );

        // Creating server folder structure
        monitor.subTask( "creating server folder structure" );
        File serverFolder = LdapServersManager.getServerFolder( server ).toFile();
        File confFolder = new File( serverFolder, CONF );
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
        LdapServersUtils.copyResource( bundle, resourceConfFolderPath.append( SERVER_XML ), new File( confFolder,
            SERVER_XML ) );
        LdapServersUtils.copyResource( bundle, resourceConfFolderPath.append( LOG4J_PROPERTIES ), new File( confFolder,
            LOG4J_PROPERTIES ) );

        // Creating an empty log file
        new File( logFolder, "apacheds.log" ).createNewFile();
    }


    /**
     * {@inheritDoc}
     */
    public void delete( LdapServer server, StudioProgressMonitor monitor ) throws Exception
    {
        // Nothing to do (nothing more than the default behavior of 
        // the delete action before this method is called)
    }


    /**
     * {@inheritDoc}
     */
    public void openConfiguration( final LdapServer server, final StudioProgressMonitor monitor ) throws Exception
    {
        // Opening the editor
        Display.getDefault().syncExec( new Runnable()
        {
            public void run()
            {
                try
                {
                    PathEditorInput input = new PathEditorInput( LdapServersManager.getServerFolder( server )
                        .append( CONF ).append( SERVER_XML ) );
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .openEditor( input, ServerConfigurationEditor.ID );
                }
                catch ( PartInitException e )
                {
                    monitor.reportError( e );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void start( LdapServer server, StudioProgressMonitor monitor ) throws Exception
    {
        // Starting the console printer thread
        LdapServersUtils.startConsolePrinterThread( server );

        // Launching ApacheDS
        ILaunch launch = LdapServersUtils.launchApacheDS( server, getServerLibrariesFolder(), libraries );

        // Starting the "terminate" listener thread
        LdapServersUtils.startTerminateListenerThread( server, launch );

        // Running the startup listener watchdog
        LdapServersUtils.runStartupListenerWatchdog( server, getTestingPort( server ) );
    }


    /**
     * {@inheritDoc}
     */
    public void stop( LdapServer server, StudioProgressMonitor monitor ) throws Exception
    {
        // Stopping the console printer thread
        LdapServersUtils.stopConsolePrinterThread( server );

        // Terminating the launch configuration
        LdapServersUtils.terminateLaunchConfiguration( server );
    }


    /**
     * Gets the path to the server libraries folder.
     *
     * @return
     *      the path to the server libraries folder
     */
    private static IPath getServerLibrariesFolder()
    {
        return ApacheDS156Plugin.getDefault().getStateLocation().append( LIBS );
    }


    /**
    * Gets the server configuration.
    *
    * @param server
    *      the server
    * @return
    *      the associated server configuration
    * @throws ServerXmlIOException 
    * @throws FileNotFoundException 
    */
    public static ServerConfigurationV156 getServerConfiguration( LdapServer server ) throws ServerXmlIOException,
        FileNotFoundException
    {
        InputStream fis = new FileInputStream( LdapServersManager.getServerFolder( server ).append( CONF )
            .append( SERVER_XML ).toFile() );

        ServerXmlIOV156 serverXmlIOV156 = new ServerXmlIOV156();
        return ( ServerConfigurationV156 ) serverXmlIOV156.parse( fis );
    }


    /**
     * Gets the testing port.
     *
     * @param configuration
     *      the 1.5.6 server configuration
     * @return
     *      the testing port
     * @throws IOException 
     * @throws ServerXmlIOException 
     */
    private int getTestingPort( LdapServer server ) throws ServerXmlIOException, IOException
    {
        ServerConfigurationV156 configuration = getServerConfiguration( server );

        // LDAP
        if ( configuration.isEnableLdap() )
        {
            return configuration.getLdapPort();
        }
        // LDAPS
        else if ( configuration.isEnableLdaps() )
        {
            return configuration.getLdapsPort();
        }
        // Kerberos
        else if ( configuration.isEnableKerberos() )
        {
            return configuration.getKerberosPort();
        }
        // DNS
        else if ( configuration.isEnableDns() )
        {
            return configuration.getDnsPort();
        }
        // NTP
        else if ( configuration.isEnableNtp() )
        {
            return configuration.getNtpPort();
        }
        // ChangePassword
        else if ( configuration.isEnableChangePassword() )
        {
            return configuration.getChangePasswordPort();
        }
        else
        {
            return 0;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String[] checkPortsBeforeServerStart( LdapServer server )
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        try
        {
            ServerConfigurationV156 configuration = getServerConfiguration( server );

            // LDAP
            if ( configuration.isEnableLdap() )
            {
                if ( !AvailablePortFinder.available( configuration.getLdapPort() ) )
                {
                    alreadyInUseProtocolPortsList
                        .add( NLS.bind(
                            Messages.getString( "ApacheDS156LdapServerAdapter.LDAPPort" ), new Object[] { configuration.getLdapPort() } ) ); //$NON-NLS-1$
                }
            }

            // LDAPS
            if ( configuration.isEnableLdaps() )
            {
                if ( !AvailablePortFinder.available( configuration.getLdapsPort() ) )
                {
                    alreadyInUseProtocolPortsList
                        .add( NLS.bind(
                            Messages.getString( "ApacheDS156LdapServerAdapter.LDAPSPort" ), new Object[] { configuration.getLdapsPort() } ) ); //$NON-NLS-1$
                }
            }

            // Kerberos
            if ( configuration.isEnableKerberos() )
            {
                if ( !AvailablePortFinder.available( configuration.getKerberosPort() ) )
                {
                    alreadyInUseProtocolPortsList
                        .add( NLS
                            .bind(
                                Messages.getString( "ApacheDS156LdapServerAdapter.KerberosPort" ), new Object[] { configuration.getKerberosPort() } ) ); //$NON-NLS-1$
                }
            }

            // DNS
            if ( configuration.isEnableDns() )
            {
                if ( !AvailablePortFinder.available( configuration.getDnsPort() ) )
                {
                    alreadyInUseProtocolPortsList
                        .add( NLS.bind(
                            Messages.getString( "ApacheDS156LdapServerAdapter.DNSPort" ), new Object[] { configuration.getDnsPort() } ) ); //$NON-NLS-1$
                }
            }

            // NTP
            if ( configuration.isEnableNtp() )
            {
                if ( !AvailablePortFinder.available( configuration.getNtpPort() ) )
                {
                    alreadyInUseProtocolPortsList.add( NLS.bind(
                        Messages.getString( "ApacheDS156LdapServerAdapter.NTPPort" ), new Object[] //$NON-NLS-1$
                        { configuration.getNtpPort() } ) );
                }
            }

            // Change Password
            if ( configuration.isEnableChangePassword() )
            {
                if ( !AvailablePortFinder.available( configuration.getChangePasswordPort() ) )
                {
                    alreadyInUseProtocolPortsList
                        .add( NLS
                            .bind(
                                Messages.getString( "ApacheDS156LdapServerAdapter.ChangePasswordPort" ), new Object[] { configuration.getChangePasswordPort() } ) ); //$NON-NLS-1$
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( e );
        }

        return alreadyInUseProtocolPortsList.toArray( new String[0] );
    }
}
