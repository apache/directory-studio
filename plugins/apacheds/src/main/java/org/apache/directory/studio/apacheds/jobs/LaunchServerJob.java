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

package org.apache.directory.studio.apacheds.jobs;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.apacheds.ApacheDsPluginUtils;
import org.apache.directory.studio.apacheds.ConsolesHandler;
import org.apache.directory.studio.apacheds.LogMessageConsole;
import org.apache.directory.studio.apacheds.configuration.model.ServerConfiguration;
import org.apache.directory.studio.apacheds.configuration.model.v153.ServerConfigurationV153;
import org.apache.directory.studio.apacheds.configuration.model.v154.ServerConfigurationV154;
import org.apache.directory.studio.apacheds.configuration.model.v155.ServerConfigurationV155;
import org.apache.directory.studio.apacheds.configuration.model.v156.ServerConfigurationV156;
import org.apache.directory.studio.apacheds.model.Server;
import org.apache.directory.studio.apacheds.model.ServerStateEnum;
import org.apache.log4j.net.SocketServer;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;


/**
 * This class implements a {@link Job} that is used to launch a server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LaunchServerJob extends Job
{
    /** The server */
    private Server server;

    /** The configuration */
    private ServerConfiguration configuration;

    /** The launch that will be created when running the server */
    private ILaunch launch;

    /** The minimum port number for the socket server */
    private static final int MIN_PORT = 1024;

    /** The logs level */
    private String logsLevel = "WARN"; //$NON-NLS-1$

    /** The logs pattern */
    private String logsPattern = "[%d{HH:mm:ss}] %p [%c] - %m%n"; //$NON-NLS-1$


    /**
     * Creates a new instance of LaunchServerJob.
     * 
     * @param server
     *            the server
     * @param configuration
     *            the configuration
     */
    public LaunchServerJob( Server server, ServerConfiguration configuration )
    {
        super( "" ); //$NON-NLS-1$
        this.server = server;
        this.configuration = configuration;
    }


    /**
     * {@inheritDoc}
     */
    protected IStatus run( IProgressMonitor monitor )
    {
        // Setting the name of the Job
        setName( NLS.bind( Messages.getString( "LaunchServerJob.Starting" ), new String[] { server.getName() } ) ); //$NON-NLS-1$

        // Setting the server in a "starting" state
        server.setState( ServerStateEnum.STARTING );
        writeToInfoConsoleMessageStream( Messages.getString( "LaunchServerJob.ServerStarting" ) ); //$NON-NLS-1$

        // Getting the first available port for the Log4J socket server
        int port = AvailablePortFinder.getNextAvailable( MIN_PORT );

        // Launching the socket server
        launchSocketServer( port );

        // Overwriting the server's log4j.properties file
        try
        {
            overwriteServersLog4jPropertiesFile( port );
        }
        catch ( IOException e )
        {
            ApacheDsPluginUtils.reportError( Messages.getString( "LaunchServerJob.ErrorOverwritingLog" ) //$NON-NLS-1$
                + e.getMessage() );
        }

        // Launching ApacheDS
        launchApacheDS();

        // Starting the startup listener thread
        startStartupListenerThread();

        // Starting the "terminate" listener thread
        startTerminateListenerThread();

        return Status.OK_STATUS;
    }


    /**
     * Starts the startup listener thread.
     */
    private void startStartupListenerThread()
    {
        // Getting the current time
        long startTime = System.currentTimeMillis();

        // Calculating the watch dog time
        final long watchDog = startTime + ( 1000 * 60 * 3 ); // 3 minutes

        // Creating the thread
        Thread thread = new Thread()
        {
            public void run()
            {
                // Looping until the end of the watchdog
                while ( ( System.currentTimeMillis() < watchDog ) && ( ServerStateEnum.STARTING == server.getState() ) )
                {
                    try
                    {
                        // Getting the port to test
                        int port = getTestingPort( configuration );

                        // If no protocol is enabled, we pass this and 
                        // declare the server as started
                        if ( port != 0 )
                        {
                            // Trying to see if the port is available
                            if ( AvailablePortFinder.available( port ) )
                            {
                                // The port is still available
                                throw new Exception();
                            }
                        }

                        // If we pass the creation of the context, it means
                        // the server is correctly started

                        // We set the state of the server to 'started'...
                        server.setState( ServerStateEnum.STARTED );
                        writeToInfoConsoleMessageStream( Messages.getString( "LaunchServerJob.ServerStarted" ) ); //$NON-NLS-1$

                        // ... and we exit the thread
                        return;
                    }
                    catch ( Exception e )
                    {
                        // If we get an exception,it means the server is not 
                        // yet started

                        // We just wait one second before starting the test once
                        // again
                        try
                        {
                            Thread.sleep( 1000 );
                        }
                        catch ( InterruptedException e1 )
                        {
                            // Nothing to do...
                        }
                    }
                }

                // If at the end of the watch dog the state of the server is
                // still 'starting' then, we declare the server as 'stopped'
                if ( ServerStateEnum.STARTING == server.getState() )
                {
                    server.setState( ServerStateEnum.STOPPED );
                    writeToInfoConsoleMessageStream( Messages.getString( "LaunchServerJob.ServerStopped" ) ); //$NON-NLS-1$
                }
            }


            /**
             * Gets the testing port.
             *
             * @param configuration
             *      the server configuration
             * @return
             *      the testing port
             */
            private int getTestingPort( ServerConfiguration configuration )
            {
                if ( configuration instanceof ServerConfigurationV156 )
                {
                    return getTestingPortVersion156( ( ServerConfigurationV156 ) configuration );
                }
                else if ( configuration instanceof ServerConfigurationV155 )
                {
                    return getTestingPortVersion155( ( ServerConfigurationV155 ) configuration );
                }
                else if ( configuration instanceof ServerConfigurationV154 )
                {
                    return getTestingPortVersion154( ( ServerConfigurationV154 ) configuration );
                }
                else if ( configuration instanceof ServerConfigurationV153 )
                {
                    return getTestingPortVersion153( ( ServerConfigurationV153 ) configuration );
                }
                else
                {
                    return 0;
                }
            }


            /**
             * Gets the testing port.
             *
             * @param configuration
             *      the 1.5.3 server configuration
             * @return
             *      the testing port
             */
            private int getTestingPortVersion153( ServerConfigurationV153 configuration )
            {
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
             * Gets the testing port.
             *
             * @param configuration
             *      the 1.5.4 server configuration
             * @return
             *      the testing port
             */
            private int getTestingPortVersion154( ServerConfigurationV154 configuration )
            {
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
             * Gets the testing port.
             *
             * @param configuration
             *      the 1.5.5 server configuration
             * @return
             *      the testing port
             */
            private int getTestingPortVersion155( ServerConfigurationV155 configuration )
            {
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
             * Gets the testing port.
             *
             * @param configuration
             *      the 1.5.6 server configuration
             * @return
             *      the testing port
             */
            private int getTestingPortVersion156( ServerConfigurationV156 configuration )
            {
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
        };

        // Starting the thread
        thread.start();
    }


    /**
     * Writes the given message to the Info console message stream.
     * 
     * @param message
     *            the message
     */
    private void writeToInfoConsoleMessageStream( final String message )
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                LogMessageConsole console = ConsolesHandler.getDefault().getLogMessageConsole( server.getId() );
                try
                {
                    console.getInfoConsoleMessageStream().write( message );
                }
                catch ( IOException e )
                {
                    ApacheDsPluginUtils.reportError( Messages.getString( "LaunchServerJob.ErrorWritingConsole" ) //$NON-NLS-1$
                        + e.getMessage() );
                }
            }
        } );
    }


    /**
     * Starting the "terminate" listener thread.
     */
    private void startTerminateListenerThread()
    {
        // Creating the thread
        Thread thread = new Thread()
        {
            /** The debug event listener */
            private IDebugEventSetListener debugEventSetListener;


            public void run()
            {
                // Creating the listener
                debugEventSetListener = new IDebugEventSetListener()
                {
                    public void handleDebugEvents( DebugEvent[] events )
                    {
                        // Looping on the debug events array
                        for ( DebugEvent debugEvent : events )
                        {
                            // We only care of event with kind equals to
                            // 'terminate'
                            if ( debugEvent.getKind() == DebugEvent.TERMINATE )
                            {
                                // Getting the source of the debug event
                                Object source = debugEvent.getSource();
                                if ( source instanceof RuntimeProcess )
                                {
                                    RuntimeProcess runtimeProcess = ( RuntimeProcess ) source;

                                    // Getting the associated launch
                                    ILaunch debugEventLaunch = runtimeProcess.getLaunch();
                                    if ( debugEventLaunch.equals( launch ) )
                                    {
                                        // The launch we had created is now terminated
                                        // The server is now stopped
                                        server.setState( ServerStateEnum.STOPPED );

                                        // Removing the listener
                                        DebugPlugin.getDefault().removeDebugEventListener( debugEventSetListener );
                                    }
                                }
                            }
                        }
                    }
                };

                // Adding the listener
                DebugPlugin.getDefault().addDebugEventListener( debugEventSetListener );
            }
        };

        // Starting the thread
        thread.start();
    }


    /**
     * Launches a Log4J {@link SocketServer} which will be used to redirect the
     * logs of ApacheDS to the console.
     * 
     * @param port
     *            the port
     * @param
     * 
     */
    private void launchSocketServer( int port )
    {
        final int finalPort = port;
        final IPath serverSocketFolderPath = ApacheDsPluginUtils.getApacheDsServersFolder().append( server.getId() )
            .append( "serverSocket" ); //$NON-NLS-1$
        final IPath log4jPropertiesFilePath = serverSocketFolderPath.append( "log4j.properties" ); //$NON-NLS-1$

        // Creating a new thread for the SocketServer
        Thread thread = new Thread()
        {
            public void run()
            {
                SocketServer.main( new String[]
                    { "" + finalPort, log4jPropertiesFilePath.toOSString(), serverSocketFolderPath.toOSString() } ); //$NON-NLS-1$
            }
        };

        // Launching the SocketServer
        thread.start();
    }


    /**
     * Overwrites the log4j.properties file of the server with the given port
     * number.
     * 
     * @param port
     *            the port
     * @throws IOException
     */
    private void overwriteServersLog4jPropertiesFile( int port ) throws IOException
    {
        IPath confFolderPath = ApacheDsPluginUtils.getApacheDsServersFolder().append( server.getId() ).append( "conf" ); //$NON-NLS-1$
        File confFolder = new File( confFolderPath.toOSString() );
        ApacheDsPluginUtils.createServersLog4jPropertiesFile( new FileOutputStream( new File( confFolder,
            "log4j.properties" ) ), port, logsLevel, logsPattern ); //$NON-NLS-1$
    }


    /**
     * Launches ApacheDS using a launch configuration.
     */
    private void launchApacheDS()
    {
        try
        {
            // Getting the default VM installation
            IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();

            // Creating a new editable launch configuration
            ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(
                IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION );
            ILaunchConfigurationWorkingCopy workingCopy = type.newInstance( null, NLS.bind( Messages
                .getString( "LaunchServerJob.StartingServer" ), new String[] { server.getName() } ) ); //$NON-NLS-1$

            // Setting the JRE container path attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, vmInstall
                .getInstallLocation().toString() );

            // Setting the main type attribute
            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
                "org.apache.directory.studio.apacheds.Launcher" ); //$NON-NLS-1$

            // Creating the classpath list
            List<String> classpath = new ArrayList<String>();
            IPath apacheDsLibrariesFolder = ApacheDsPluginUtils.getApacheDsLibrariesFolder( server );
            for ( String library : ApacheDsPluginUtils.getApacheDsLibraries( server ) )
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
            IPath serverFolderPath = ApacheDsPluginUtils.getApacheDsServersFolder().append( server.getId() );

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
            launch = configuration.launch( ILaunchManager.RUN_MODE, new NullProgressMonitor() );
        }
        catch ( CoreException e )
        {
            ApacheDsPluginUtils.reportError( Messages.getString( "LaunchServerJob.ErrorLaunching" ) + e.getMessage() ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the associated launch.
     * 
     * @return the associated launch
     */
    public ILaunch getLaunch()
    {
        return launch;
    }


    /**
     * Sets the logs level.
     * 
     * @param logsLevel
     *            the logs level
     */
    public void setLogsLevel( String logsLevel )
    {
        this.logsLevel = logsLevel;
    }


    /**
     * Sets the logs pattern.
     * 
     * @param logsPattern
     *            the logs pattern
     */
    public void setLogsPattern( String logsPattern )
    {
        this.logsPattern = logsPattern;
    }
}
