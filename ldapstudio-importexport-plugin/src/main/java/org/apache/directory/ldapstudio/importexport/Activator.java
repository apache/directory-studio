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

package org.apache.directory.ldapstudio.importexport;

import org.apache.directory.ldapstudio.importexport.controller.actions.ExportAction;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ldapstudio_importexport_plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
    
	// The logger
    private static org.slf4j.Logger logger = LoggerFactory.getLogger( ExportAction.class );
	
	/**
	 * The constructor
	 */
	public Activator() {
        // Configuring NLog4J
        PropertyConfigurator.configure( Platform.getBundle(Plugin.ID).getResource("ressources/log4j.conf") ); //$NON-NLS-1$
        // Setting up logging level for Apache DS libraries
        Logger  loggerBis = Logger.getLogger("org.apache.directory"); //$NON-NLS-1$
        loggerBis.setLevel( Level.ERROR );
        
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
        logger.info( "Starting Import/Export plugin" ); //$NON-NLS-1$
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
        logger.info( "Stopping Import/Export plugin" ); //$NON-NLS-1$
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
