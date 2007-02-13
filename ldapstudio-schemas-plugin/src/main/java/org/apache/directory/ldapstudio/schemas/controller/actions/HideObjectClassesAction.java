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
import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.ldapstudio.schemas.view.viewers.HierarchicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the Hide Object Classes Action for the Hierarch View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class HideObjectClassesAction extends Action
{
    /** The String for storing the checked state of the action */
    public static final String HIDE_OBJECT_CLASSES_DS_KEY = HideObjectClassesAction.class.getName()
        + ".dialogsettingkey";

    /** The associated view */
    private HierarchicalViewer hierarchyView;


    /**
     * Creates a new instance of HideObjectClassesAction.
     *
     * @param view
     *      the associated view
     */
    public HideObjectClassesAction( HierarchicalViewer view )
    {
        super( "Hide Object Classes", AS_CHECK_BOX );
        super.setActionDefinitionId( Activator.PLUGIN_ID + "hideObjectClasses" );
        super.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Application.PLUGIN_ID,
            IImageKeys.HIDE_OBJECT_CLASSES ) );
        super.setEnabled( true );
        hierarchyView = view;

        // Setting up the default key value (if needed)
        if ( Activator.getDefault().getDialogSettings().get( HIDE_OBJECT_CLASSES_DS_KEY ) == null )
        {
            Activator.getDefault().getDialogSettings().put( HIDE_OBJECT_CLASSES_DS_KEY, false );
        }

        // Setting state from the dialog settings
        super.setChecked( Activator.getDefault().getDialogSettings().getBoolean( HIDE_OBJECT_CLASSES_DS_KEY ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        setChecked( isChecked() );
        Activator.getDefault().getDialogSettings().put( HIDE_OBJECT_CLASSES_DS_KEY, isChecked() );
        
        hierarchyView.getViewer().refresh();
    }
}
