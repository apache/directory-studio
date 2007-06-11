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


import org.apache.directory.studio.schemas.Messages;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.view.preferences.HierarchyViewPreferencePage;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This action opens the Preference Page for the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenHierarchyViewPreferencesAction extends Action
{
    /**
     * Creates a new instance of OpenHierarchyViewPreferencesAction.
     */
    public OpenHierarchyViewPreferencesAction()
    {
        super( Messages.getString( "OpenHierarchyViewPreferencesAction.Preferences..." ) ); //$NON-NLS-1$
        setToolTipText( getText() );
        setId( PluginConstants.CMD_HIERARCHY_VIEW_PREFERENCES );
        setEnabled( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run()
    {
        Shell shell = Display.getCurrent().getActiveShell();
        PreferencesUtil.createPreferenceDialogOn( shell, HierarchyViewPreferencePage.ID, new String[]
            { HierarchyViewPreferencePage.ID }, null ).open();
    }
}
