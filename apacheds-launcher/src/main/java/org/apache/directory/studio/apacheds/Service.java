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

import org.apache.directory.daemon.DaemonApplication;
import org.apache.directory.daemon.InstallationLayout;
import org.apache.directory.server.configuration.ApacheDS;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.SocketAcceptor;
import org.apache.xbean.spring.context.FileSystemXmlApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DirectoryServer bean used by both the daemon code and by the ServerMain here.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 596797 $
 */
public class Service implements DaemonApplication
{
    private static final Logger LOG = LoggerFactory.getLogger( Service.class );
    private Thread workerThread;
    private SynchWorker worker = new SynchWorker();
    private ApacheDS apacheDS;
    private FileSystemXmlApplicationContext factory;


    public void init( InstallationLayout install, String[] args ) throws Exception
    {
        long startTime = System.currentTimeMillis();

        if ( args.length > 0 && new File( args[0] ).exists() ) // hack that takes server.xml file argument
        {
            LOG.info( "server: loading settings from ", args[0] );
            factory = new FileSystemXmlApplicationContext( new File( args[0] ).toURI().toURL().toString() );
            apacheDS = ( ApacheDS ) factory.getBean( "apacheDS" );
        }
        else
        {
            LOG.info( "server: using default settings ..." );
            DirectoryService directoryService = new DefaultDirectoryService();
            directoryService.startup();
            SocketAcceptor socketAcceptor = new SocketAcceptor( null );
            LdapServer ldapServer = new LdapServer();
            ldapServer.setSocketAcceptor( socketAcceptor );
            ldapServer.setDirectoryService( directoryService );
            ldapServer.start();
            LdapServer ldapsServer = new LdapServer();
            ldapsServer.setEnableLdaps( true );
            ldapsServer.setSocketAcceptor( socketAcceptor );
            ldapsServer.setDirectoryService( directoryService );
            ldapsServer.start();
            apacheDS = new ApacheDS( directoryService, ldapServer, ldapsServer );
        }

        if ( install != null )
        {
            apacheDS.getDirectoryService().setWorkingDirectory( install.getPartitionsDirectory() );
        }

        apacheDS.startup();

        if ( apacheDS.getSynchPeriodMillis() > 0 )
        {
            workerThread = new Thread( worker, "SynchWorkerThread" );
        }

        if ( LOG.isInfoEnabled() )
        {
            LOG.info( "server: started in {} milliseconds", ( System.currentTimeMillis() - startTime ) + "" );
        }
    }


    public DirectoryService getDirectoryService()
    {
        return apacheDS.getDirectoryService();
    }


    public void synch() throws Exception
    {
        apacheDS.getDirectoryService().sync();
    }


    public void start()
    {
        if ( workerThread != null )
        {
            workerThread.start();
        }
    }


    public void stop( String[] args ) throws Exception
    {
        if ( workerThread != null )
        {
            worker.stop = true;
            synchronized ( worker.lock )
            {
                worker.lock.notify();
            }

            while ( workerThread.isAlive() )
            {
                LOG.info( "Waiting for SynchWorkerThread to die." );
                workerThread.join( 500 );
            }
        }

        if ( factory != null )
        {
            factory.close();
        }
        apacheDS.shutdown();
    }


    public void destroy()
    {
    }

    class SynchWorker implements Runnable
    {
        final Object lock = new Object();
        boolean stop;


        public void run()
        {
            while ( !stop )
            {
                synchronized ( lock )
                {
                    try
                    {
                        lock.wait( apacheDS.getSynchPeriodMillis() );
                    }
                    catch ( InterruptedException e )
                    {
                        LOG.warn( "SynchWorker failed to wait on lock.", e );
                    }
                }

                try
                {
                    synch();
                }
                catch ( Exception e )
                {
                    LOG.error( "SynchWorker failed to synch directory.", e );
                }
            }
        }
    }
}
