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
package org.apache.directory.studio.schemas.controller.actions;


import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.view.views.HierarchyView;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Show Supertype Hierachy Action for the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ShowSupertypeHierarchyAction extends Action
{
    /** The associated view */
    private HierarchyView view;


    /**
     * Creates a new instance of ShowSubtypeHierarchyAction.
     *
     * @param view
     *      the associated view
     */
    public ShowSupertypeHierarchyAction( HierarchyView view )
    {
        super( Messages.getString( "ShowSupertypeHierarchyAction.Supertype_Hierarchy" ), AS_RADIO_BUTTON ); //$NON-NLS-1$
        setToolTipText( Messages.getString( "ShowSupertypeHierarchyAction.Show_the_Supertype_Hierarchy" ) ); //$NON-NLS-1$
        setId( PluginConstants.CMD_SHOW_SUPERTYPE_HIERARCHY );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SHOW_SUPERTYPE_HIERARCHY ) );
        setEnabled( true );
        this.view = view;

        // Setting state from the dialog settings
        setChecked( Activator.getDefault().getDialogSettings().getInt( PluginConstants.PREFS_HIERARCHY_VIEW_MODE ) == PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        Activator.getDefault().getDialogSettings().put( PluginConstants.PREFS_HIERARCHY_VIEW_MODE,
            PluginConstants.PREFS_HIERARCHY_VIEW_MODE_SUPERTYPE );

        view.refresh();
    }
}
