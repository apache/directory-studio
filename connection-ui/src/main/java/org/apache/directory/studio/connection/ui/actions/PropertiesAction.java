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

package org.apache.directory.studio.connection.ui.actions;


import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.ui.ConnectionUIPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;


/**
 * This Action opens the Property Dialog for a given object.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PropertiesAction extends StudioAction
{
    /**
     * Creates a new instance of PropertiesAction.
     */
    public PropertiesAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        return "Properties";
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return IWorkbenchActionDefinitionIds.PROPERTIES;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {

        return getSelectedConnections().length == 1;

    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        IAdaptable element = null;
        String pageId = null;
        String title = null;

        if ( getSelectedConnections().length == 1 )
        {
            element = ( IAdaptable ) getSelectedConnections()[0];
            pageId = ConnectionUIPlugin.getDefault().getPluginProperties().getString( "Prop_ConnectionPropertyPage_id" );
            title = getSelectedConnections()[0].getName();
        }

        if ( element != null )
        {
            PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( getShell(), element, pageId, null, null );
            if ( dialog != null )
                title = Utils.shorten( title, 30 );
            dialog.getShell().setText( "Properties for '" + title + "'" );
            dialog.open();

        }
    }
}
