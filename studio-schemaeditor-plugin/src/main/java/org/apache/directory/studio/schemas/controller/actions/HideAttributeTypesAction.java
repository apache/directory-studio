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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Hide Object Classes Action for the Schema Elements View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HideAttributeTypesAction extends Action
{
    /** The String for storing the checked state of the action */
    public static final String HIDE_ATTRIBUTE_TYPES_DS_KEY = HideAttributeTypesAction.class.getName()
        + ".dialogsettingkey"; //$NON-NLS-1$

    /** The associated view */
    private TreeViewer viewer;


    /**
     * Creates a new instance of HideObjectClassesAction.
     *
     * @param viewer
     *      the associated viewer
     */
    public HideAttributeTypesAction( TreeViewer viewer )
    {
        super( Messages.getString( "HideAttributeTypesAction.Hide_Attribute_Types" ), AS_CHECK_BOX ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_HIDE_ATTRIBUTE_TYPES );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_HIDE_ATTRIBUTE_TYPES ) );
        setEnabled( true );
        this.viewer = viewer;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( HIDE_ATTRIBUTE_TYPES_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( HIDE_ATTRIBUTE_TYPES_DS_KEY, false );
        }

        // Setting state from the dialog settings
        super.setChecked( Activator.getDefault().getDialogSettings().getBoolean( HIDE_ATTRIBUTE_TYPES_DS_KEY ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        setChecked( isChecked() );
        Activator.getDefault().getDialogSettings().put( HIDE_ATTRIBUTE_TYPES_DS_KEY, isChecked() );

        viewer.refresh();
    }
}
