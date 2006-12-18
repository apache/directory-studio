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

package org.apache.directory.ldapstudio.browser.ui.perspective;


import org.apache.directory.ldapstudio.browser.ui.views.browser.BrowserView;
import org.apache.directory.ldapstudio.browser.ui.views.connection.ConnectionView;
import org.apache.directory.ldapstudio.browser.ui.views.modificationlogs.ModificationLogsView;
import org.apache.directory.ldapstudio.browser.ui.wizards.BatchOperationWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewBookmarkWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewConnectionWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewEntryWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewLdifFileWizard;
import org.apache.directory.ldapstudio.browser.ui.wizards.NewSearchWizard;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class BrowserPerspective implements IPerspectiveFactory
{

    public static String getId()
    {
        return BrowserPerspective.class.getName();
    }


    public void createInitialLayout( IPageLayout layout )
    {
        defineActions( layout );
        defineLayout( layout );
    }


    private void defineActions( IPageLayout layout )
    {

        // Add "new wizards".
        layout.addNewWizardShortcut( NewConnectionWizard.getId() );
        layout.addNewWizardShortcut( NewEntryWizard.getId() );
        layout.addNewWizardShortcut( NewSearchWizard.getId() );
        layout.addNewWizardShortcut( NewBookmarkWizard.getId() );
        layout.addNewWizardShortcut( BatchOperationWizard.getId() );
        layout.addNewWizardShortcut( NewLdifFileWizard.getId() );

        // Add "show views".
        layout.addShowViewShortcut( ConnectionView.getId() );
        layout.addShowViewShortcut( BrowserView.getId() );
        layout.addShowViewShortcut( ModificationLogsView.getId() );
        layout.addShowViewShortcut( IPageLayout.ID_RES_NAV );
        layout.addShowViewShortcut( IPageLayout.ID_OUTLINE );
        layout.addShowViewShortcut( "org.eclipse.ui.views.ProgressView" );
        // layout.addShowViewShortcut(IPageLayout.ID_PROGRESS_VIEW);
        // layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
    }


    private void defineLayout( IPageLayout layout )
    {

        // Editor area
        String editorArea = layout.getEditorArea();

        // Browser folder
        IFolderLayout browserFolder = layout.createFolder( "browserFolder", IPageLayout.LEFT, ( float ) 0.25,
            editorArea );
        browserFolder.addView( BrowserView.getId() );
        browserFolder.addView( IPageLayout.ID_RES_NAV );

        // Connection folder
        IFolderLayout connectionFolder = layout.createFolder( "connectionFolder", IPageLayout.BOTTOM, ( float ) 0.75,
            "browserFolder" );
        connectionFolder.addView( ConnectionView.getId() );

        // Outline folder
        IFolderLayout outlineFolder = layout.createFolder( "outlineFolder", IPageLayout.RIGHT, ( float ) 0.75,
            editorArea );
        outlineFolder.addView( IPageLayout.ID_OUTLINE );

        // Progress folder
        IFolderLayout progessFolder = layout.createFolder( "progressFolder", IPageLayout.BOTTOM, ( float ) 0.75,
            "outlineFolder" );
        progessFolder.addView( "org.eclipse.ui.views.ProgressView" );
        // progessFolder.addView(IPageLayout.ID_PROGRESS_VIEW);

        // Log folder
        IFolderLayout logFolder = layout.createFolder( "logFolder", IPageLayout.BOTTOM, ( float ) 0.75, editorArea );
        logFolder.addView( ModificationLogsView.getId() );
        // logFolder.addView("org.eclipse.pde.runtime.LogView");
        logFolder.addPlaceholder( "*" );
    }

}
