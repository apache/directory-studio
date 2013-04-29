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

package org.apache.directory.studio.ldapservers;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.ui.CommonUIUtils;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerStatus;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.ui.console.MessageConsole;
import org.osgi.framework.Bundle;


/**
 * The helper class defines various utility methods for the LDAP Servers plugin.
 */
public class LdapServersUtils
{
    /** The ID of the launch configuration custom object */
    public static final String LAUNCH_CONFIGURATION_CUSTOM_OBJECT = "launchConfiguration"; //$NON-NLS-1$

    /** The ID of the console printer custom object */
    public static final String CONSOLE_PRINTER_CUSTOM_OBJECT = "consolePrinter"; //$NON-NLS-1$


    /**
     * Runs the startup listener watchdog.
     *
     * @param server
     *      the server
     * @param port
     *      the port
     * @throws Exception
     */
    public static void runStartupListenerWatchdog( LdapServer server, int port ) throws Exception
    {
        // Getting the current time
        long startTime = System.currentTimeMillis();

        // Calculating the watch dog time
        final long watchDog = startTime + ( 1000 * 60 * 3 ); // 3 minutes

        // Looping until the end of the watchdog if the server is still 'starting'
        while ( ( System.currentTimeMillis() < watchDog ) && ( LdapServerStatus.STARTING == server.getStatus() ) )
        {
            // Getting the port to test
            try
            {
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
                server.setStatus( LdapServerStatus.STARTED );

                // ... and we exit the thread
                return;
            }
            catch ( Exception e )
            {
                // If we get an exception, it means the server is not 
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

        // If, at the end of the watch dog, the state of the server is
        // still 'starting' then, we declare the server as 'stopped'
        if ( LdapServerStatus.STARTING == server.getStatus() )
        {
            server.setStatus( LdapServerStatus.STOPPED );
        }
    }


    /**
     * Starting the "terminate" listener thread.
     * 
     * @param server 
     *      the server
     * @param launch 
     *      the launch
     */
    public static void startTerminateListenerThread( final LdapServer server, final ILaunch launch )
    {
        // Creating the thread
        Thread thread = new Thread()
        {
            public void run()
            {
                // Adding the listener
                DebugPlugin.getDefault().addDebugEventListener( new IDebugEventSetListener()
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
                                        server.setStatus( LdapServerStatus.STOPPED );

                                        // Removing the listener
                                        DebugPlugin.getDefault().removeDebugEventListener( this );

                                        // ... and we exit the thread
                                        return;
                                    }
                                }
                            }
                        }
                    }
                } );
            }
        };

        // Starting the thread
        thread.start();
    }


    /**
     * Starts the console printer thread.
     *
     * @param server
     *      the server
     */
    public static void startConsolePrinterThread( LdapServer server )
    {
        startConsolePrinterThread( server, LdapServersManager.getServerFolder( server )
            .append( "log" ) //$NON-NLS-1$
            .append( "apacheds.log" ).toFile() );//$NON-NLS-1$
    }


    /**
     * Starts the console printer thread.
     *
     * @param server
     *      the server
     */
    public static void startConsolePrinterThread( LdapServer server, File serverLogsFile )
    {
        MessageConsole messageConsole = ConsolesManager.getDefault().getMessageConsole( server );
        ConsolePrinterThread consolePrinter = new ConsolePrinterThread( serverLogsFile,
            messageConsole.newMessageStream() );
        consolePrinter.start();

        // Storing the console printer as a custom object in the LDAP Server for later use
        server.putCustomObject( CONSOLE_PRINTER_CUSTOM_OBJECT, consolePrinter );
    }


    /**
     * Stops the console printer thread.
     *
     * @param server
     *      the server
     */
    public static void stopConsolePrinterThread( LdapServer server )
    {
        // Getting the console printer
        ConsolePrinterThread consolePrinter = ( ConsolePrinterThread ) server
            .removeCustomObject( CONSOLE_PRINTER_CUSTOM_OBJECT );
        if ( ( consolePrinter != null ) && ( consolePrinter.isAlive() ) )
        {
            // Closing the console printer
            consolePrinter.close();
        }
    }


    /**
     * Launches ApacheDS using a launch configuration.
     *
     * @param server
     *      the server
     * @param apacheDsLibrariesFolder
     *      the ApacheDS libraries folder
     * @param libraries
     *      the names of the libraries
     * @return
     *      the associated launch
     */
    public static ILaunch launchApacheDS( LdapServer server, IPath apacheDsLibrariesFolder, String[] libraries )
        throws Exception
    {
        // Getting the default VM installation
        IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();

        // Creating a new editable launch configuration
        ILaunchConfigurationType type = DebugPlugin.getDefault().getLaunchManager()
            .getLaunchConfigurationType( IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION );
        ILaunchConfigurationWorkingCopy workingCopy = type.newInstance( null, server.getId() );

        // Setting the JRE container path attribute
        workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, vmInstall
            .getInstallLocation().toString() );

        // Setting the main type attribute
        workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
            "org.apache.directory.studio.apacheds.Launcher" ); //$NON-NLS-1$

        // Creating the classpath list
        List<String> classpath = new ArrayList<String>();
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

        // Storing the launch configuration as a custom object in the LDAP Server for later use
        server.putCustomObject( LAUNCH_CONFIGURATION_CUSTOM_OBJECT, launch );

        return launch;
    }


    /**
     * Terminates the launch configuration.
     *
     * @param server
     *      the server
     * @throws Exception
     */
    public static void terminateLaunchConfiguration( LdapServer server ) throws Exception
    {
        // Getting the launch
        ILaunch launch = ( ILaunch ) server.removeCustomObject( LdapServersUtils.LAUNCH_CONFIGURATION_CUSTOM_OBJECT );
        if ( ( launch != null ) && ( !launch.isTerminated() ) )
        {
            // Terminating the launch
            launch.terminate();
        }
        else
        {
            throw new Exception(
                Messages.getString( "LdapServersUtils.AssociatedLaunchConfigurationCouldNotBeFoundOrTerminated" ) ); //$NON-NLS-1$
        }
    }


    /**
     * Verifies that the libraries folder exists and contains the jar files 
     * needed to launch the server.
     *
     * @param bundle
     *      the bundle
     * @param sourceLibrariesPath
     *      the path to the source libraries
     * @param destinationLibrariesPath
     *      the path to the destination libraries
     * @param libraries
     *      the names of the libraries
     */
    private static void verifyAndCopyLibraries( Bundle bundle, IPath sourceLibrariesPath,
        IPath destinationLibrariesPath, String[] libraries )
    {
        // Destination libraries folder
        File destinationLibraries = destinationLibrariesPath.toFile();
        if ( !destinationLibraries.exists() )
        {
            destinationLibraries.mkdir();
        }

        // Verifying and copying libraries (if needed)
        for ( String library : libraries )
        {
            File destinationLibraryFile = destinationLibrariesPath.append( library ).toFile();
            if ( !destinationLibraryFile.exists() )
            {
                try
                {
                    copyResource( bundle, sourceLibrariesPath.append( library ), destinationLibraryFile );
                }
                catch ( IOException e )
                {
                    CommonUIUtils.openErrorDialog( NLS.bind(
                        Messages.getString( "LdapServersUtils.ErrorCopyingLibrary" ), //$NON-NLS-1$
                        new String[]
                            { library, destinationLibraryFile.getAbsolutePath(), e.getMessage() } ) );
                }
            }
        }
    }


    /**
     * Verifies that the libraries folder exists and contains the jar files 
     * needed to launch the server.
     *
     * @param bundle
     *      the bundle
     * @param sourceLibrariesPath
     *      the path to the source libraries
     * @param destinationLibrariesPath
     *      the path to the destination libraries
     * @param libraries
     *      the names of the libraries
     * @param monitor the monitor
     * @param monitorTaskName the name of the task for the monitor
     */
    public static void verifyAndCopyLibraries( Bundle bundle, IPath sourceLibrariesPath,
        IPath destinationLibrariesPath, String[] libraries, StudioProgressMonitor monitor, String monitorTaskName )
    {
        // Creating the sub-task on the monitor
        monitor.subTask( monitorTaskName );

        // Verifying and copying the libraries
        verifyAndCopyLibraries( bundle, sourceLibrariesPath, destinationLibrariesPath, libraries );
    }


    /**
    * Copy the given resource.
    *
    * @param bundle
    *       the bundle
    * @param resource
    *      the path of the resource
    * @param destination
    *      the destination
    * @throws IOException
    *      if an error occurs when copying the jar file
    */
    public static void copyResource( Bundle bundle, IPath resource, File destination ) throws IOException
    {
        // Getting he URL of the resource within the bundle
        URL resourceUrl = FileLocator.find( bundle, resource, null );

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
