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
package org.apache.directory.studio.apacheds.perspectives;


import org.apache.directory.studio.apacheds.views.ServersView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;


/**
 * This class implements the perspective of the plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Perspective implements IPerspectiveFactory
{
    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout( IPageLayout layout )
    {
        // Getting the editors area
        String editorArea = layout.getEditorArea();

        // Adding a Servers folder
        IFolderLayout serversFolder = layout.createFolder( "serversFolder", IPageLayout.LEFT, ( float ) 0.25,
            editorArea );
        // Adding the Servers view
        serversFolder.addView( ServersView.ID );

        // Adding a Console folder
        IFolderLayout conFolderLayoutFolder = layout.createFolder( "consoleFolder", IPageLayout.BOTTOM, 0.5f,
            editorArea );
        // Adding the Console view
        conFolderLayoutFolder.addView( IConsoleConstants.ID_CONSOLE_VIEW );
    }
}
