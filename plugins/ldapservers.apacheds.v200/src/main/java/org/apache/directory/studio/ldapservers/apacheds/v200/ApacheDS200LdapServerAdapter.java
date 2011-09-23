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

package org.apache.directory.studio.ldapservers.apacheds.v200;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.configuration.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.model.ServerXmlIOException;
import org.apache.directory.studio.apacheds.configuration.model.v157.ServerConfigurationV157;
import org.apache.directory.studio.apacheds.configuration.model.v157.ServerXmlIOV157;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.filesystem.PathEditorInput;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.LdapServersUtils;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;


/**
 * This class implements an LDAP Server Adapter for ApacheDS version 2.0.0.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDS200LdapServerAdapter implements LdapServerAdapter
{
    // Various strings constants used in paths
    private static final String CONFIG_LDIF = "config.ldif";
    private static final String LOG4J_PROPERTIES = "log4j.properties";
    private static final String RESOURCES = "resources";
    private static final String LIBS = "libs";
    private static final String CONF = "conf";

    /** The array of libraries names */
    private static final String[] libraries = new String[]
        { "apacheds-service-2.0.0-M3.jar" };


    /**
     * {@inheritDoc}
     */
    public void add( LdapServer server, StudioProgressMonitor monitor ) throws Exception
    {
        // Getting the bundle associated with the plugin
        Bundle bundle = ApacheDS200Plugin.getDefault().getBundle();

        // Verifying and copying ApacheDS 2.0.0 libraries
        monitor.subTask( "verifying and copying ApacheDS 2.0.0 libraries" );
        LdapServersUtils.verifyAndCopyLibraries( bundle, new Path( RESOURCES ).append( LIBS ),
            getServerLibrariesFolder(), libraries );

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
        LdapServersUtils.copyResource( bundle, resourceConfFolderPath.append( CONFIG_LDIF ), new File( confFolder,
            CONFIG_LDIF ) );
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
                        .append( "conf" ).append( "server.xml" ) ); //$NON-NLS-1$ //$NON-NLS-2$
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

        // Launching Apache DS
        ILaunch launch = launchApacheDS( server );

        // Starting the "terminate" listener thread
        LdapServersUtils.startTerminateListenerThread( server, launch );

        // Running the startup listener watchdog
//        LdapServersUtils.runStartupListenerWatchdog( server, getTestingPort( server ) );
    }


    /**
     * Launches Apache DS using a launch configuration.
     *
     * @param server
     *      the server
     * @return
     *      the associated launch
     */
    public static ILaunch launchApacheDS( LdapServer server )
        throws Exception
    {
        // Getting the default VM installation
        IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();

        // Creating a new editable launch configuration
        ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
            .getLaunchConfigurationType( IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION );
        ILaunchConfigurationWorkingCopy workingCopy = type.newInstance( null, NLS.bind( "Starting {0}", new String[]
            { server.getName() } ) );

        // Setting the JRE container path attribute
        workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, vmInstall
            .getInstallLocation().toString() );

        // Setting the main type attribute
        workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
            "org.apache.directory.server.UberjarMain" ); //$NON-NLS-1$

        // Creating the classpath list
        List<String> classpath = new ArrayList<String>();
        for ( String library : libraries )
        {
            IRuntimeClasspathEntry libraryClasspathEntry = JavaRuntime
                .newArchiveRuntimeClasspathEntry( getServerLibrariesFolder().append( library ) );
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
        vmArguments.append( "-Dapacheds.instance=\"" + server.getName() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Ddefault.controls=org.apache.directory.shared.ldap.codec.controls.cascade.CascadeFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.codec.controls.manageDsaIT.ManageDsaITFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.codec.controls.search.entryChange.EntryChangeFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.codec.controls.search.pagedSearch.PagedResultsFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.codec.controls.search.persistentSearch.PersistentSearchFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.codec.controls.search.subentries.SubentriesFactory" ); //$NON-NLS-1$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Dextra.controls=org.apache.directory.shared.ldap.extras.controls.ppolicy.PasswordPolicyFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncDoneValueFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncInfoValueFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncRequestValueFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.controls.syncrepl_impl.SyncStateValueFactory" ); //$NON-NLS-1$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Ddefault.extendedOperation.requests=org.apache.directory.shared.ldap.extras.extended.ads_impl.cancel.CancelFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.extended.ads_impl.certGeneration.CertGenerationFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.extended.ads_impl.gracefulShutdown.GracefulShutdownFactory," + //$NON-NLS-1$
                "org.apache.directory.shared.ldap.extras.extended.ads_impl.storedProcedure.StoredProcedureFactory" ); //$NON-NLS-1$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Ddefault.extendedOperation.responses=org.apache.directory.shared.ldap.extras.extended.ads_impl.gracefulDisconnect.GracefulDisconnectFactory" ); //$NON-NLS-1$

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

        // Storing the launch configuration as a custom object in the LDAP Server for later use
        server.putCustomObject( LdapServersUtils.LAUNCH_CONFIGURATION_CUSTOM_OBJECT, launch );

        return launch;
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
        return ApacheDS200Plugin.getDefault().getStateLocation().append( LIBS );
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
    public static ServerConfigurationV157 getServerConfiguration( LdapServer server ) throws ServerXmlIOException,
        FileNotFoundException
    {
        InputStream fis = new FileInputStream( LdapServersManager.getServerFolder( server ).append( "conf" )
            .append( "server.xml" ).toFile() );

        ServerXmlIOV157 serverXmlIOV157 = new ServerXmlIOV157();
        return ( ServerConfigurationV157 ) serverXmlIOV157.parse( fis );
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
        ServerConfigurationV157 configuration = getServerConfiguration( server );

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
}
