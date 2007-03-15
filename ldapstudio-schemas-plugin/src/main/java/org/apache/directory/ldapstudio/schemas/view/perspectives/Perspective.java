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

package org.apache.directory.ldapstudio.schemas.view.perspectives;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.view.viewers.HierarchyView;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemaElementsView;
import org.apache.directory.ldapstudio.schemas.view.viewers.SchemasView;
import org.apache.directory.ldapstudio.schemas.view.viewers.SearchView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * This is the standard perspective with Schema Elements View on the top left, Schemas View on bottom left,
 * editor in top right, Search View on bottom right.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Perspective implements IPerspectiveFactory
{
    /** The ID of the view */
    public static final String ID = Activator.PLUGIN_ID + ".perspective"; //$NON-NLS-1$


    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible( true );

        IFolderLayout topLeftFolder = layout.createFolder( "placeholder", IPageLayout.LEFT, 0.3f, editorArea );

        layout.addPlaceholder( "placeholder", IPageLayout.LEFT, 0.3f, editorArea );
        topLeftFolder.addView( SchemaElementsView.ID );
        topLeftFolder.addView( HierarchyView.ID );

        layout.addStandaloneView( SchemasView.ID, true, IPageLayout.BOTTOM, 0.5f, "placeholder" );
        layout.addStandaloneView( SearchView.ID, true, IPageLayout.BOTTOM, 0.7f, editorArea );

        layout.getViewLayout( SchemaElementsView.ID ).setCloseable( false );
        layout.getViewLayout( SchemasView.ID ).setCloseable( false );
        layout.getViewLayout( SearchView.ID ).setCloseable( false );

        layout.addPerspectiveShortcut( "org.apache.directory.ldapstudio.browser.ui.perspective.BrowserPerspective" ); //$NON-NLS-1$
        layout.addPerspectiveShortcut( Perspective.ID );

        // Adding View shortcuts
        layout.addShowViewShortcut( HierarchyView.ID );
        layout.addShowViewShortcut( SchemaElementsView.ID );
        layout.addShowViewShortcut( SchemasView.ID );
        layout.addShowViewShortcut( SearchView.ID );
    }
}
