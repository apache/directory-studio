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


import org.apache.directory.ldapstudio.schemas.controller.Application;
import org.apache.directory.ldapstudio.schemas.view.viewers.HierarchicalViewer;
import org.apache.directory.ldapstudio.schemas.view.viewers.PoolManager;
import org.apache.directory.ldapstudio.schemas.view.viewers.SearchViewer;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * This is the standard perspective with hierarchical viewer on the top left, pool manager on bottom left,
 * editor in top right, search field on bottom right.
 *
 */
public class StandardPerspective implements IPerspectiveFactory
{
    public static final String ID = Application.PLUGIN_ID + ".perspective"; //$NON-NLS-1$


    public void createInitialLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible( true );

        layout.addStandaloneView( HierarchicalViewer.ID, true, IPageLayout.LEFT, 0.3f, editorArea );
        layout.getViewLayout( HierarchicalViewer.ID ).setCloseable( false );

        layout.addStandaloneView( PoolManager.ID, true, IPageLayout.BOTTOM, 0.5f, HierarchicalViewer.ID );
        layout.getViewLayout( PoolManager.ID ).setCloseable( false );

        layout.addStandaloneView( SearchViewer.ID, true, IPageLayout.BOTTOM, 0.7f, editorArea );
        layout.getViewLayout( SearchViewer.ID ).setCloseable( false );

        layout.addPerspectiveShortcut( "org.apache.directory.ldapstudio.browser.ui.perspective.BrowserPerspective" ); //$NON-NLS-1$
        layout.addPerspectiveShortcut( StandardPerspective.ID );
        //layout.addPerspectiveShortcut(TreePerspective.ID);	

    }
}
