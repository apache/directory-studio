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

package org.apache.directory.ldapstudio.schemas;


import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.views.SchemaCodeScanner;
import org.apache.directory.ldapstudio.schemas.view.views.SchemaTextAttributeProvider;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.apache.directory.ldapstudio.schemas"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private static ITokenScanner schemaCodeScanner;

    private static SchemaTextAttributeProvider schemaTextAttributeProvider;


    /**
     * The constructor
     */
    public Activator()
    {
        plugin = this;
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start( BundleContext context ) throws Exception
    {
        super.start( context );
    }


    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop( BundleContext context ) throws Exception
    {
        // Saving workspace configuration
        SchemaPool.getInstance().savePool();

        plugin = null;
        super.stop( context );
    }


    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault()
    {
        return plugin;
    }


    /**
     * Returns the Schema Code Scanner.
     *
     * @return
     *      the Schema Code Scanner
     */
    public static ITokenScanner getSchemaCodeScanner()
    {
        if ( schemaCodeScanner == null )
        {
            schemaCodeScanner = new SchemaCodeScanner( getSchemaTextAttributeProvider() );
        }

        return schemaCodeScanner;
    }


    /**
     * Returns the Schema Text Attribute Provider.
     *
     * @return
     *     the Schema Text Attribute Provider 
     */
    private static SchemaTextAttributeProvider getSchemaTextAttributeProvider()
    {
        if ( schemaTextAttributeProvider == null )
        {
            schemaTextAttributeProvider = new SchemaTextAttributeProvider();
        }

        return schemaTextAttributeProvider;
    }
}
