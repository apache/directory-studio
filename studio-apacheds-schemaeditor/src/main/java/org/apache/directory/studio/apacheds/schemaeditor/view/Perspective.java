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
package org.apache.directory.studio.apacheds.schemaeditor.view;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.ProblemsView;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.ProjectsView;
import org.apache.directory.studio.apacheds.schemaeditor.view.views.SchemaView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * This class represents the Schema Editor Perspective.
 * <p>
 * It is composed of two views, the Schema View and the Problems View, and the editor part.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Perspective implements IPerspectiveFactory
{
    /** The ID of the view */
    public static final String ID = Activator.PLUGIN_ID + ".perspective"; //$NON-NLS-1$

    /** The ID of the top left folder */
    public static final String topLeftFolderId = "org.apache.directory.studio.apacheds.schemaeditor.topleftfolder"; //$NON-NLS-1$


    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout( IPageLayout layout )
    {
        // Allowing the Editor Area
        layout.setEditorAreaVisible( true );
        String editorAreaId = layout.getEditorArea();

        // Creating top left folder 
        IFolderLayout topLeftFolder = layout.createFolder( topLeftFolderId, IPageLayout.LEFT, 0.3f, editorAreaId );
        layout.addPlaceholder( topLeftFolderId, IPageLayout.LEFT, 0.3f, editorAreaId );

        // Adding Views
        topLeftFolder.addView( SchemaView.ID );
        layout.addStandaloneView( ProjectsView.ID, true, IPageLayout.BOTTOM, 0.7f, topLeftFolderId );
        layout.addStandaloneView( ProblemsView.ID, true, IPageLayout.BOTTOM, 0.7f, editorAreaId );

        // Setting up non-closeable views
        layout.getViewLayout( SchemaView.ID ).setCloseable( false );
        layout.getViewLayout( ProjectsView.ID ).setCloseable( false );

        // Adding Perspective shortcuts
        layout.addPerspectiveShortcut( "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective" ); //$NON-NLS-1$
        layout.addPerspectiveShortcut( Perspective.ID );

        // Adding View shortcuts
        layout.addShowViewShortcut( SchemaView.ID );
        layout.addShowViewShortcut( ProjectsView.ID );
        layout.addShowViewShortcut( ProblemsView.ID );
    }
}
