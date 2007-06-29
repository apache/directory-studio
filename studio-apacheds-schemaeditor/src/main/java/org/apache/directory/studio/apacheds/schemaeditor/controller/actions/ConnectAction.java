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
package org.apache.directory.studio.apacheds.schemaeditor.controller.actions;


import java.util.List;

import javax.naming.NamingException;

import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.SchemaImporter;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SchemaView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This Action is used to connect to Apache Directory Server and retrieve the Schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectAction extends Action implements IWorkbenchWindowActionDelegate
{
    /** The associated View */
    private SchemaView view;
    
    /**
     * Creates a new instance of ConnectAction.
     *
     * @param view
     *      the associated View
     */
    public ConnectAction( SchemaView view )
    {
        super( "Connect" ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_CONNECT );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_CONNECT ) );
        setEnabled( true );
        this.view = view;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose()
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window )
    {
        // Nothing to do
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action )
    {
        run();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        try
        {
            SchemaHandler schemaHandler = SchemaHandler.getInstance();
            SchemaImporter schemaImporter = new SchemaImporter();
            List<Schema> schemas = schemaImporter.getServerSchema();
            for ( Schema schema : schemas )
            {
                schemaHandler.addSchema( schema );
            }
            view.reloadViewer();
        }
        catch ( NamingException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection )
    {
        // Nothing to do
    }
}
