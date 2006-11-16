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
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * Shows only the tree view
 * WARNING: This is a test perspective
 *
 */
public class TreePerspective implements IPerspectiveFactory
{

    public static final String ID = Application.PLUGIN_ID + ".treeperspective"; //$NON-NLS-1$


    public void createInitialLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible( false );

        layout.addStandaloneView( HierarchicalViewer.ID, true, IPageLayout.LEFT, 1f, editorArea );
        layout.getViewLayout( HierarchicalViewer.ID ).setCloseable( false );
    }
}
