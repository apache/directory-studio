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
import org.apache.directory.ldapstudio.schemas.view.views.SearchView;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This action collapses all nodes of the viewer's tree, starting with the root.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EraseSearchAction extends Action
{
    protected SearchView view;


    /**
     * Creates a new instance of CollapseAllAction.
     *
     * @param viewer
     *      the attached Viewer
     */
    public EraseSearchAction( SearchView view )
    {
        super( Messages.getString( "EraseSearchAction.Erase_Search" ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_ERASE_SEARCH );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_ERASE_SEARCH ) );
        setEnabled( true );

        this.view = view;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        view.setSearch( "", SearchView.SEARCH_ALL ); //$NON-NLS-1$
    }


    /**
     * Disposes the action delegate.
     */
    public void dispose()
    {
        this.view = null;
    }
}
