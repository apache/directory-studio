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

package org.apache.directory.studio.apacheds.configuration.v2.jobs;


import java.io.File;
import java.util.List;

import org.apache.directory.server.config.ConfigPartitionReader;
import org.apache.directory.server.config.beans.ConfigBean;
import org.apache.directory.server.core.partition.ldif.SingleFileLdifPartition;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.loader.ldif.JarLdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.loader.ldif.SingleLdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;
import org.apache.directory.shared.ldap.util.LdapExceptionUtils;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;


/**
 * This class implements a {@link Job} that is used to delete an LDAP Server.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LoadConfigurationRunnable implements StudioRunnableWithProgress
{
    /** The associated editor */
    private ServerConfigurationEditor editor;

    /** The configuration bean */
    private ConfigBean configBean;


    /**
     * Creates a new instance of StartLdapServerRunnable.
     * 
     * @param server
     *            the LDAP Server
     */
    public LoadConfigurationRunnable( ServerConfigurationEditor editor )
    {
        super();
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return "Unable to load the configuration.";
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        return new Object[0];
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "Load configuration";
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        try
        {
            initConfigBean();

            Display.getDefault().asyncExec( new Runnable()
            {
                public void run()
                {
                    editor.configBeanLoaded( configBean );
                }
            } );
        }
        catch ( Exception e )
        {
            // Reporting the error to the monitor
            monitor.reportError( e );
        }
    }


    public void initConfigBean() throws Exception
    {
        long t1 = System.currentTimeMillis();
        
        SchemaManager schemaManager = ApacheDS2ConfigurationPlugin.getDefault().getSchemaManager();

        long t2 = System.currentTimeMillis();

        System.out.println( "Time = " + ( t2 - t1 ) + "ms" );

        List<Throwable> errors = schemaManager.getErrors();

        if ( errors.size() != 0 )
        {
            throw new Exception( "Schema load failed : " + LdapExceptionUtils.printErrors( errors ) );
        }

        SingleFileLdifPartition configPartition = new SingleFileLdifPartition(
            "/Users/pajbam/Development/Apache/ApacheDS/apacheds/server-config/src/main/resources/config.ldif" );
        configPartition.setId( "config" );
        configPartition.setSuffix( new DN( "ou=config" ) );
        configPartition.setSchemaManager( schemaManager );

        configPartition.initialize();
        ConfigPartitionReader cpReader = new ConfigPartitionReader( configPartition, new File(
            "/Users/pajbam/Development/Apache/ApacheDS/apacheds/server-config/src/main/resources/" ) );

        t1 = System.currentTimeMillis();

        configBean = cpReader.readConfig( new DN( "ou=config" ) );

        t2 = System.currentTimeMillis();

        System.out.println( "Time = " + ( t2 - t1 ) + "ms" );

        //            System.out.println( configBean );
    }
}
