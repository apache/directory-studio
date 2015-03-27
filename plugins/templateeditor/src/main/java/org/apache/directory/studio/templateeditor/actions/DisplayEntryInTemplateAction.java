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
package org.apache.directory.studio.templateeditor.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.templateeditor.EntryTemplatePlugin;
import org.apache.directory.studio.templateeditor.EntryTemplatePluginConstants;
import org.apache.directory.studio.templateeditor.editor.TemplateEditorWidget;


/**
 * This action is used to display a drop-down menu with the available
 * templates for the entry in the editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DisplayEntryInTemplateAction extends Action
{
    /** The associated {@link TemplateEditorWidget} */
    private TemplateEditorWidget templateEditorPage;


    /**
     * Creates a new instance of DisplayEntryInTemplateAction.
     *
     * @param templateEditorPage
     *      the associated editor page
     */
    public DisplayEntryInTemplateAction( TemplateEditorWidget templateEditorPage )
    {
        super( Messages.getString( "DisplayEntryInTemplateAction.DisplayEntryIn" ), Action.AS_DROP_DOWN_MENU ); //$NON-NLS-1$
        setImageDescriptor( EntryTemplatePlugin.getDefault().getImageDescriptor(
            EntryTemplatePluginConstants.IMG_SWITCH_TEMPLATE ) );
        this.templateEditorPage = templateEditorPage;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        MenuManager menuManager = new MenuManager();
        DisplayEntryInTemplateMenuManager.fillInMenuManager( menuManager, templateEditorPage );

        menuManager.createContextMenu( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        menuManager.getMenu().setVisible( true );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        return true;
    }
}
