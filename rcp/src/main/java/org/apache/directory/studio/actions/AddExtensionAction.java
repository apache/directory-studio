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

package org.apache.directory.studio.actions;


import java.net.MalformedURLException;
import java.net.URL;

import org.apache.directory.studio.Messages;
import org.apache.directory.studio.PluginConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.search.BackLevelFilter;
import org.eclipse.update.search.EnvironmentFilter;
import org.eclipse.update.search.UpdateSearchRequest;
import org.eclipse.update.search.UpdateSearchScope;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;


/**
 * This class implements the Add Extension Action.
 * It uses Eclipse built-in extension system to allow users
 * to add extensions to Apache Directory Studio.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddExtensionAction extends Action implements IAction
{
    private IWorkbenchWindow window;


    /**
     * Default constructor
     * @param window
     *          the window it is attached to
     */
    public AddExtensionAction( IWorkbenchWindow window )
    {
        this.window = window;
        setId( PluginConstants.ACTION_ADD_EXTENSION_ID ); //$NON-NLS-1$
        setText( Messages.getString( "AddExtensionAction.Add_Extensions" ) ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "AddExtensionAction.Search_for_new_extensions" ) ); //$NON-NLS-1$
    }


    /**
     * Runs the action
     */
    public void run()
    {
        BusyIndicator.showWhile( window.getShell().getDisplay(), new Runnable()
        {
            public void run()
            {
                UpdateJob job = new UpdateJob(
                    Messages.getString( "AddExtensionAction.Searching_new_extensions" ), getSearchRequest() ); //$NON-NLS-1$
                UpdateManagerUI.openInstaller( window.getShell(), job );
            }


            private UpdateSearchRequest getSearchRequest()
            {
                UpdateSearchRequest result = new UpdateSearchRequest( UpdateSearchRequest
                    .createDefaultSiteSearchCategory(), new UpdateSearchScope() );
                result.addFilter( new BackLevelFilter() );
                result.addFilter( new EnvironmentFilter() );
                UpdateSearchScope scope = new UpdateSearchScope();
                try
                {
                    String homeBase = System
                        .getProperty(
                            "studio.homebase", Messages.getString( "AddExtensionAction.Apache_Directory_Studio_Home_Base" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    URL url = new URL( homeBase );
                    scope.addSearchSite(
                        Messages.getString( "AddExtensionAction.Apache_Directory_Studio_Site" ), url, null ); //$NON-NLS-1$
                }
                catch ( MalformedURLException e )
                {
                    // TODO: handle exception
                }
                result.setScope( scope );
                return result;
            }
        } );
    }
}