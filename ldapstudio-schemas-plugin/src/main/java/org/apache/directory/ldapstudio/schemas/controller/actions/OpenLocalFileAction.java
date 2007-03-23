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

package org.apache.directory.ldapstudio.schemas.controller.actions;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.model.Schema.SchemaType;
import org.apache.directory.ldapstudio.schemas.view.preferences.SchemasEditorPreferencePage;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Action for opening a local file schema
 */
public class OpenLocalFileAction extends Action
{
    private static Logger logger = Logger.getLogger( OpenLocalFileAction.class );


    /**
     * Creates a new instance of OpenLocalFileAction.
     */
    public OpenLocalFileAction()
    {
        super( Messages.getString( "OpenLocalFileAction.Open_a_schema_file" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_OPEN_LOCAL );
        setActionDefinitionId( PluginConstants.CMD_OPEN_LOCAL );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_OPEN ) );
        setEnabled( true );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        FileDialog fd = new FileDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
        fd.setText( Messages.getString( "OpenLocalFileAction.Open_a_schema_file" ) ); //$NON-NLS-1$

        IEclipsePreferences prefs = new ConfigurationScope().getNode( Activator.PLUGIN_ID );
        String defaultPath = prefs
            .get( SchemasEditorPreferencePage.DEFAULT_DIRECTORY, System.getProperty( "user.home" ) ); //$NON-NLS-1$
        fd.setFilterPath( defaultPath );
        String[] filterExt =
            { "*.schema", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$
        fd.setFilterExtensions( filterExt );
        String selected = fd.open();
        // selected == null if 'cancel' has been pushed
        if ( selected != null )
        {
            SchemaPool pool = SchemaPool.getInstance();
            try
            {
                pool.addAlreadyExistingSchema( selected, SchemaType.userSchema );
            }
            catch ( Exception e )
            {
                logger.debug( "Error when opening a schema file" ); //$NON-NLS-1$
            }
        }
    }
}
