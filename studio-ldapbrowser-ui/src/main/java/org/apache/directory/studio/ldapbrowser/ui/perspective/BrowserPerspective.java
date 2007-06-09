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

package org.apache.directory.studio.ldapbrowser.ui.perspective;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.wizards.NewConnectionWizard;
import org.apache.directory.studio.ldapbrowser.ui.views.browser.BrowserView;
import org.apache.directory.studio.ldapbrowser.ui.views.connection.ConnectionView;
import org.apache.directory.studio.ldapbrowser.ui.views.modificationlogs.ModificationLogsView;
import org.apache.directory.studio.ldapbrowser.ui.wizards.BatchOperationWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.NewBookmarkWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.NewEntryWizard;
import org.apache.directory.studio.ldapbrowser.ui.wizards.NewSearchWizard;
import org.apache.directory.studio.ldifeditor.wizards.NewLdifFileWizard;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * This class implements the {@link IPerspectiveFactory} for the browser
 * plugin. It is responsible for creating the perspective layout.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserPerspective implements IPerspectiveFactory
{

    /**
     * Gets the ID of the browser perspective.
     * 
     * @return the ID of the browser perspective
     */
    public static String getId()
    {
        return BrowserPerspective.class.getName();
    }


    /**
     * {@inheritDoc}
     */
    public void createInitialLayout( IPageLayout layout )
    {
        defineActions( layout );
        defineLayout( layout );

        layout.addPerspectiveShortcut( "org.apache.directory.ldapstudio.schemas.perspective" ); //$NON-NLS-1$ 
        layout.addPerspectiveShortcut( "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective" ); //$NON-NLS-1$ 
    }


    /**
     * Defines the actions in the "New..." menu and the "Show views..." menu.
     * 
     * @param layout the layout
     */
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
        layout.addShowViewShortcut( IPageLayout.ID_OUTLINE );
        layout.addShowViewShortcut( "org.eclipse.ui.views.ProgressView" );
    }


    /**
     * Defines the layout.
     * 
     * @param layout the layout
     */
    private void defineLayout( IPageLayout layout )
    {

        // Editor area
        String editorArea = layout.getEditorArea();

        // Browser folder
        IFolderLayout browserFolder = layout.createFolder( "browserFolder", IPageLayout.LEFT, ( float ) 0.25,
            editorArea );
        browserFolder.addView( BrowserView.getId() );

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

        // Log folder
        IFolderLayout logFolder = layout.createFolder( "logFolder", IPageLayout.BOTTOM, ( float ) 0.75, editorArea );
        logFolder.addView( ModificationLogsView.getId() );
        logFolder.addPlaceholder( "*" );

        // non-closable?
        boolean isIDE = BrowserCommonActivator.isIDEEnvironment();
        if ( !isIDE )
        {
            layout.getViewLayout( BrowserView.getId() ).setCloseable( false );
            layout.getViewLayout( ConnectionView.getId() ).setCloseable( false );
            layout.getViewLayout( IPageLayout.ID_OUTLINE ).setCloseable( false );
            layout.getViewLayout( "org.eclipse.ui.views.ProgressView" ).setCloseable( false );
            layout.getViewLayout( ModificationLogsView.getId() ).setCloseable( false );
        }
    }

}
