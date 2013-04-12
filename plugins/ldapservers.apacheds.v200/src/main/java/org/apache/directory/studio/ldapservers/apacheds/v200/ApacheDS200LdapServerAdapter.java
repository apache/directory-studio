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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.server.config.beans.ChangePasswordServerBean;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.config.beans.DirectoryServiceBean;
import org.apache.directory.server.config.beans.DnsServerBean;
import org.apache.directory.server.config.beans.KdcServerBean;
import org.apache.directory.server.config.beans.LdapServerBean;
import org.apache.directory.server.config.beans.NtpServerBean;
import org.apache.directory.server.config.beans.TransportBean;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.apacheds.configuration.v2.jobs.LoadConfigurationRunnable;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.filesystem.PathEditorInput;
import org.apache.directory.studio.ldapservers.LdapServersManager;
import org.apache.directory.studio.ldapservers.LdapServersUtils;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;
import org.apache.mina.util.AvailablePortFinder;
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
    private static final String CONFIG_LDIF = "config.ldif"; //$NON-NLS-1$
    private static final String LOG4J_PROPERTIES = "log4j.properties"; //$NON-NLS-1$
    private static final String RESOURCES = "resources"; //$NON-NLS-1$
    private static final String LIBS = "libs"; //$NON-NLS-1$
    private static final String CONF = "conf"; //$NON-NLS-1$

    /** The array of libraries names */
    private static final String[] libraries = new String[]
        { "apacheds-service-2.0.0-M12-SNAPSHOT.jar" }; //$NON-NLS-1$


    /**
     * {@inheritDoc}
     */
    public void add( LdapServer server, StudioProgressMonitor monitor ) throws Exception
    {
        // Getting the bundle associated with the plugin
        Bundle bundle = ApacheDS200Plugin.getDefault().getBundle();

        // Verifying and copying ApacheDS 2.0.0 libraries
        LdapServersUtils.verifyAndCopyLibraries( bundle, new Path( RESOURCES ).append( LIBS ),
            getServerLibrariesFolder(), libraries, monitor,
            Messages.getString( "ApacheDS200LdapServerAdapter.VerifyingAndCopyingLibraries" ) ); //$NON-NLS-1$

        // Creating server folder structure
        monitor.subTask( Messages.getString( "ApacheDS200LdapServerAdapter.CreatingServerFolderStructure" ) ); //$NON-NLS-1$
        File serverFolder = LdapServersManager.getServerFolder( server ).toFile();
        File confFolder = new File( serverFolder, CONF );
        confFolder.mkdir();
        File ldifFolder = new File( serverFolder, "ldif" ); //$NON-NLS-1$
        ldifFolder.mkdir();
        File logFolder = new File( serverFolder, "log" ); //$NON-NLS-1$
        logFolder.mkdir();
        File partitionFolder = new File( serverFolder, "partitions" ); //$NON-NLS-1$
        partitionFolder.mkdir();

        // Copying configuration files
        monitor.subTask( Messages.getString( "ApacheDS200LdapServerAdapter.CopyingConfigurationFiles" ) ); //$NON-NLS-1$
        IPath resourceConfFolderPath = new Path( RESOURCES ).append( CONF );
        LdapServersUtils.copyResource( bundle, resourceConfFolderPath.append( CONFIG_LDIF ), new File( confFolder,
            CONFIG_LDIF ) );
        LdapServersUtils.copyResource( bundle, resourceConfFolderPath.append( LOG4J_PROPERTIES ), new File( confFolder,
            LOG4J_PROPERTIES ) );

        // Creating an empty log file
        new File( logFolder, "apacheds.log" ).createNewFile(); //$NON-NLS-1$
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
                        .append( CONF ).append( CONFIG_LDIF ) );
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
        // Getting the bundle associated with the plugin
        Bundle bundle = ApacheDS200Plugin.getDefault().getBundle();

        // Verifying and copying ApacheDS 2.0.0 libraries
        LdapServersUtils.verifyAndCopyLibraries( bundle, new Path( RESOURCES ).append( LIBS ),
            getServerLibrariesFolder(), libraries, monitor,
            Messages.getString( "ApacheDS200LdapServerAdapter.VerifyingAndCopyingLibraries" ) ); //$NON-NLS-1$

        // Starting the console printer thread
        LdapServersUtils.startConsolePrinterThread( server );

        // Launching ApacheDS
        ILaunch launch = launchApacheDS( server );

        // Starting the "terminate" listener thread
        LdapServersUtils.startTerminateListenerThread( server, launch );

        // Running the startup listener watchdog
        LdapServersUtils.runStartupListenerWatchdog( server, getTestingPort( server ) );
    }


    /**
     * Launches ApacheDS using a launch configuration.
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
        ILaunchConfigurationWorkingCopy workingCopy = type.newInstance( null,
            NLS.bind( Messages.getString( "ApacheDS200LdapServerAdapter.Starting" ), new String[] //$NON-NLS-1$
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
            + serverFolderPath.append( CONF ).append( LOG4J_PROPERTIES ).toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments.append( "-Dapacheds.var.dir=\"" + serverFolderPath.toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments.append( "-Dapacheds.log.dir=\"" + serverFolderPath.append( "log" ).toOSString() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments.append( "-Dapacheds.instance=\"" + server.getName() + "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Ddefault.controls=org.apache.directory.api.ldap.codec.controls.cascade.CascadeFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.codec.controls.manageDsaIT.ManageDsaITFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.codec.controls.search.entryChange.EntryChangeFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.codec.controls.search.pagedSearch.PagedResultsFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.codec.controls.search.persistentSearch.PersistentSearchFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.codec.controls.search.subentries.SubentriesFactory" ); //$NON-NLS-1$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Dextra.controls=org.apache.directory.api.ldap.extras.controls.ppolicy_impl.PasswordPolicyFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.controls.syncrepl_impl.SyncDoneValueFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.controls.syncrepl_impl.SyncInfoValueFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.controls.syncrepl_impl.SyncRequestValueFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.controls.syncrepl_impl.SyncStateValueFactory" ); //$NON-NLS-1$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Ddefault.extendedOperation.requests=org.apache.directory.api.ldap.extras.extended.ads_impl.cancel.CancelFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.extended.ads_impl.certGeneration.CertGenerationFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.extended.ads_impl.gracefulShutdown.GracefulShutdownFactory," + //$NON-NLS-1$
                "org.apache.directory.api.ldap.extras.extended.ads_impl.storedProcedure.StoredProcedureFactory" ); //$NON-NLS-1$
        vmArguments.append( " " ); //$NON-NLS-1$
        vmArguments
            .append( "-Ddefault.extendedOperation.responses=org.apache.directory.api.ldap.extras.extended.ads_impl.gracefulDisconnect.GracefulDisconnectFactory" ); //$NON-NLS-1$

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
     * @throws Exception 
    * @throws ServerXmlIOException 
    * @throws FileNotFoundException 
    */
    public static ConfigBean getServerConfiguration( LdapServer server ) throws Exception
    {
        InputStream fis = new FileInputStream( LdapServersManager.getServerFolder( server ).append( CONF )
            .append( CONFIG_LDIF ).toFile() );

        return LoadConfigurationRunnable.readConfiguration( fis );
    }


    /**
     * Gets the testing port.
     *
     * @param configuration
     *      the 1.5.6 server configuration
     * @return
     *      the testing port
     * @throws Exception 
     * @throws ServerXmlIOException 
     */
    private int getTestingPort( LdapServer server ) throws Exception
    {
        ConfigBean configuration = getServerConfiguration( server );

        // LDAP
        if ( isEnableLdap( configuration ) )
        {
            return getLdapPort( configuration );
        }
        // LDAPS
        else if ( isEnableLdaps( configuration ) )
        {
            return getLdapsPort( configuration );
        }
        // Kerberos
        else if ( isEnableKerberos( configuration ) )
        {
            return getKerberosPort( configuration );
        }
        // DNS
        else if ( isEnableDns( configuration ) )
        {
            return getDnsPort( configuration );
        }
        // NTP
        else if ( isEnableNtp( configuration ) )
        {
            return getNtpPort( configuration );
        }
        // ChangePassword
        else if ( isEnableChangePassword( configuration ) )
        {
            return getChangePasswordPort( configuration );
        }
        else
        {
            return 0;
        }
    }


    /**
     * Indicates if the LDAP Server is enabled.
     *
     * @param configuration the configuration
     * @return <code>true</code> if the LDAP Server is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isEnableLdap( ConfigBean configuration )
    {
        TransportBean ldapServerTransportBean = getLdapServerTransportBean( configuration );

        if ( ldapServerTransportBean != null )
        {
            return ldapServerTransportBean.isEnabled();
        }

        return false;
    }


    /**
     * Indicates if the LDAPS Server is enabled.
     *
     * @param configuration the configuration
     * @return <code>true</code> if the LDAPS Server is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isEnableLdaps( ConfigBean configuration )
    {
        TransportBean ldapsServerTransportBean = getLdapsServerTransportBean( configuration );

        if ( ldapsServerTransportBean != null )
        {
            return ldapsServerTransportBean.isEnabled();
        }

        return false;
    }


    /**
     * Gets the LDAP Server transport bean.
     *
     * @param configuration the configuration
     * @return the LDAP Server transport bean.
     */
    private static TransportBean getLdapServerTransportBean( ConfigBean configuration )
    {
        return getLdapServerTransportBean( configuration, "ldap" ); //$NON-NLS-1$
    }


    /**
     * Gets the LDAPS Server transport bean.
     *
     * @param configuration the configuration
     * @return the LDAPS Server transport bean.
     */
    private static TransportBean getLdapsServerTransportBean( ConfigBean configuration )
    {
        return getLdapServerTransportBean( configuration, "ldaps" ); //$NON-NLS-1$
    }


    /**
     * Gets the corresponding LDAP Server transport bean.
     *
     * @param configuration the configuration
     * @param id the id
     * @return the corresponding LDAP Server transport bean.
     */
    private static TransportBean getLdapServerTransportBean( ConfigBean configuration, String id )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            LdapServerBean ldapServerBean = directoryServiceBean.getLdapServerBean();

            if ( ldapServerBean != null )
            {
                // Looking for the transport in the list
                TransportBean[] ldapServerTransportBeans = ldapServerBean.getTransports();
                if ( ldapServerTransportBeans != null )
                {
                    for ( TransportBean ldapServerTransportBean : ldapServerTransportBeans )
                    {
                        if ( id.equals( ldapServerTransportBean.getTransportId() ) )
                        {
                            return ldapServerTransportBean;
                        }
                    }
                }
            }
        }

        return null;
    }


    /**
     * Indicates if the Kerberos Server is enabled.
     *
     * @param configuration the configuration
     * @return <code>true</code> if the Kerberos Server is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isEnableKerberos( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            KdcServerBean kdcServerBean = directoryServiceBean.getKdcServerBean();

            if ( kdcServerBean != null )
            {
                kdcServerBean.isEnabled();
            }
        }

        return false;
    }


    /**
     * Indicates if the DNS Server is enabled.
     *
     * @param configuration the configuration
     * @return <code>true</code> if the DNS Server is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isEnableDns( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            DnsServerBean dnsServerBean = directoryServiceBean.getDnsServerBean();

            if ( dnsServerBean != null )
            {
                dnsServerBean.isEnabled();
            }
        }

        return false;
    }


    /**
     * Indicates if the NTP Server is enabled.
     *
     * @param configuration the configuration
     * @return <code>true</code> if the NTP Server is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isEnableNtp( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            NtpServerBean ntpServerBean = directoryServiceBean.getNtpServerBean();

            if ( ntpServerBean != null )
            {
                ntpServerBean.isEnabled();
            }
        }

        return false;
    }


    /**
     * Indicates if the Change Password Server is enabled.
     *
     * @param configuration the configuration
     * @return <code>true</code> if the Change Password Server is enabled,
     *         <code>false</code> if not.
     */
    public static boolean isEnableChangePassword( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            ChangePasswordServerBean changePasswordServerBean = directoryServiceBean.getChangePasswordServerBean();

            if ( changePasswordServerBean != null )
            {
                changePasswordServerBean.isEnabled();
            }
        }

        return false;
    }


    /**
     * Gets the LDAP port.
     *
     * @param configuration the configuration
     * @return the LDAP port
     */
    public static int getLdapPort( ConfigBean configuration )
    {
        TransportBean ldapServerTransportBean = getLdapServerTransportBean( configuration );

        if ( ldapServerTransportBean != null )
        {
            return ldapServerTransportBean.getSystemPort();
        }

        return 0;
    }


    /**
     * Gets the LDAPS port.
     *
     * @param configuration the configuration
     * @return the LDAPS port
     */
    public static int getLdapsPort( ConfigBean configuration )
    {
        TransportBean ldapsServerTransportBean = getLdapsServerTransportBean( configuration );

        if ( ldapsServerTransportBean != null )
        {
            return ldapsServerTransportBean.getSystemPort();
        }

        return 0;
    }


    /**
     * Gets the Kerberos port.
     *
     * @param configuration the configuration
     * @return the Kerberos port
     */
    public static int getKerberosPort( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            KdcServerBean kdcServerBean = directoryServiceBean.getKdcServerBean();

            if ( kdcServerBean != null )
            {
                // Looking for the transport in the list
                TransportBean[] kdcServerTransportBeans = kdcServerBean.getTransports();

                if ( kdcServerTransportBeans != null )
                {
                    for ( TransportBean kdcServerTransportBean : kdcServerTransportBeans )
                    {
                        if ( ( "tcp".equals( kdcServerTransportBean.getTransportId() ) ) //$NON-NLS-1$
                            || ( "udp".equals( kdcServerTransportBean.getTransportId() ) ) ) //$NON-NLS-1$
                        {
                            return kdcServerTransportBean.getSystemPort();
                        }
                    }
                }
            }
        }

        return 0;
    }


    /**
     * Gets the DNS port.
     *
     * @param configuration the configuration
     * @return the DNS port
     */
    public static int getDnsPort( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            DnsServerBean dnsServerBean = directoryServiceBean.getDnsServerBean();

            if ( dnsServerBean != null )
            {
                // Looking for the transport in the list
                TransportBean[] dnsServerTransportBeans = dnsServerBean.getTransports();

                if ( dnsServerTransportBeans != null )
                {
                    for ( TransportBean dnsServerTransportBean : dnsServerTransportBeans )
                    {
                        if ( ( "tcp".equals( dnsServerTransportBean.getTransportId() ) ) //$NON-NLS-1$
                            || ( "udp".equals( dnsServerTransportBean.getTransportId() ) ) ) //$NON-NLS-1$
                        {
                            return dnsServerTransportBean.getSystemPort();
                        }
                    }
                }
            }
        }

        return 0;
    }


    /**
     * Gets the NTP port.
     *
     * @param configuration the configuration
     * @return the NTP port
     */
    public static int getNtpPort( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            NtpServerBean ntpServerBean = directoryServiceBean.getNtpServerBean();

            if ( ntpServerBean != null )
            {
                // Looking for the transport in the list
                TransportBean[] ntpServerTransportBeans = ntpServerBean.getTransports();

                if ( ntpServerTransportBeans != null )
                {
                    for ( TransportBean ntpServerTransportBean : ntpServerTransportBeans )
                    {
                        if ( ( "tcp".equals( ntpServerTransportBean.getTransportId() ) ) //$NON-NLS-1$
                            || ( "udp".equals( ntpServerTransportBean.getTransportId() ) ) ) //$NON-NLS-1$
                        {
                            return ntpServerTransportBean.getSystemPort();
                        }
                    }
                }
            }
        }

        return 0;
    }


    /**
     * Gets the Change Password port.
     *
     * @param configuration the configuration
     * @return the Change Password port
     */
    public static int getChangePasswordPort( ConfigBean configuration )
    {
        DirectoryServiceBean directoryServiceBean = configuration.getDirectoryServiceBean();

        if ( directoryServiceBean != null )
        {
            ChangePasswordServerBean changePasswordServerBean = directoryServiceBean.getChangePasswordServerBean();

            if ( changePasswordServerBean != null )
            {
                // Looking for the transport in the list
                TransportBean[] changePasswordServerTransportBeans = changePasswordServerBean.getTransports();

                if ( changePasswordServerTransportBeans != null )
                {
                    for ( TransportBean changePasswordServerTransportBean : changePasswordServerTransportBeans )
                    {
                        if ( ( "tcp".equals( changePasswordServerTransportBean.getTransportId() ) ) //$NON-NLS-1$
                            || ( "udp".equals( changePasswordServerTransportBean.getTransportId() ) ) ) //$NON-NLS-1$
                        {
                            return changePasswordServerTransportBean.getSystemPort();
                        }
                    }
                }
            }
        }

        return 0;
    }


    /**
     * {@inheritDoc}
     */
    public String[] checkPortsBeforeServerStart( LdapServer server ) throws Exception
    {
        List<String> alreadyInUseProtocolPortsList = new ArrayList<String>();

        ConfigBean configuration = getServerConfiguration( server );

        // LDAP
        if ( isEnableLdap( configuration ) )
        {
            if ( !AvailablePortFinder.available( getLdapPort( configuration ) ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS.bind(
                        Messages.getString( "ApacheDS200LdapServerAdapter.LDAPPort" ), new Object[] { getLdapPort( configuration ) } ) ); //$NON-NLS-1$
            }
        }

        // LDAPS
        if ( isEnableLdaps( configuration ) )
        {
            if ( !AvailablePortFinder.available( getLdapsPort( configuration ) ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS.bind(
                        Messages.getString( "ApacheDS200LdapServerAdapter.LDAPSPort" ), new Object[] { getLdapsPort( configuration ) } ) ); //$NON-NLS-1$
            }
        }

        // Kerberos
        if ( isEnableKerberos( configuration ) )
        {
            if ( !AvailablePortFinder.available( getKerberosPort( configuration ) ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "ApacheDS200LdapServerAdapter.KerberosPort" ), new Object[] { getKerberosPort( configuration ) } ) ); //$NON-NLS-1$
            }
        }

        // DNS
        if ( isEnableDns( configuration ) )
        {
            if ( !AvailablePortFinder.available( getDnsPort( configuration ) ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS.bind(
                        Messages.getString( "ApacheDS200LdapServerAdapter.DNSPort" ), new Object[] { getDnsPort( configuration ) } ) ); //$NON-NLS-1$
            }
        }

        // NTP
        if ( isEnableNtp( configuration ) )
        {
            if ( !AvailablePortFinder.available( getNtpPort( configuration ) ) )
            {
                alreadyInUseProtocolPortsList.add( NLS.bind(
                    Messages.getString( "ApacheDS200LdapServerAdapter.NTPPort" ), new Object[] //$NON-NLS-1$
                    { getNtpPort( configuration ) } ) );
            }
        }

        // Change Password
        if ( isEnableChangePassword( configuration ) )
        {
            if ( !AvailablePortFinder.available( getChangePasswordPort( configuration ) ) )
            {
                alreadyInUseProtocolPortsList
                    .add( NLS
                        .bind(
                            Messages.getString( "ApacheDS200LdapServerAdapter.ChangePasswordPort" ), new Object[] { getChangePasswordPort( configuration ) } ) ); //$NON-NLS-1$
            }
        }

        return alreadyInUseProtocolPortsList.toArray( new String[0] );
    }
}
