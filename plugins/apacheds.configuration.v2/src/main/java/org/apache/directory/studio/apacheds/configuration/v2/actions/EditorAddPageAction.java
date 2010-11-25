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

package org.apache.directory.studio.apacheds.configuration.v2.actions;


import java.io.File;

import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPlugin;
import org.apache.directory.studio.apacheds.configuration.v2.ApacheDS2ConfigurationPluginConstants;
import org.apache.directory.studio.apacheds.configuration.v2.editor.ServerConfigurationEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This class implements the create connection action for an ApacheDS 1.5.7 server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EditorAddPageAction extends Action
{
    private ServerConfigurationEditor editor;


    public EditorAddPageAction( ServerConfigurationEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return ApacheDS2ConfigurationPlugin.getDefault().getImageDescriptor(
            ApacheDS2ConfigurationPluginConstants.IMG_IMPORT );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Add Page";
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            long t1 = System.currentTimeMillis();
            //SchemaLoader schemaLoader = new SingleLdifSchemaLoader();
            SchemaLoader schemaLoader = new LdifSchemaLoader( new File(
                "/Users/pajbam/Development/Apache/ApacheDS/shared/ldap-schema/src/main/resources/schema" ) );
            SchemaManager schemaManager = new DefaultSchemaManager( schemaLoader );

            // We have to load the schema now, otherwise we won't be able
            // to initialize the Partitions, as we won't be able to parse 
            // and normalize their suffix DN
            schemaManager.loadAllEnabled();
            //        schemaManager.loadWithDeps( "adsconfig" );

            long t2 = System.currentTimeMillis();

            System.out.println( "Time = " + ( t2 - t1 ) + "ms" );
        }
        catch ( Exception e )
        {
            // TODO: handle exception
        }
    }
}
