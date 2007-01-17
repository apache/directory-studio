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

package org.apache.directory.ldapstudio.view;


import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * This class defines LDAP Studio Main perspective.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Perspective implements IPerspectiveFactory
{

    /**
     * Creates the initial layout for a page.
     */
    public void createInitialLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible( false );

        layout.addStandaloneView( WelcomeView.ID, false, IPageLayout.LEFT, 1.0f, editorArea );
        layout.getViewLayout( WelcomeView.ID ).setCloseable( false );

        layout.addPerspectiveShortcut( "org.apache.directory.ldapstudio.browser.ui.perspective.BrowserPerspective" ); //$NON-NLS-1$
        layout.addPerspectiveShortcut( "org.apache.directory.ldapstudio.schemas.perspective" ); //$NON-NLS-1$ 
    }

}
