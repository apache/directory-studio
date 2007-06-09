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
package org.apache.directory.ldapstudio.proxy.view;


import org.apache.directory.ldapstudio.proxy.Activator;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


/**
 * This class implements the default perpective for the Proxy Plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Perspective implements IPerspectiveFactory
{
    /** The Perspective ID */
    public static final String ID = Activator.PLUGIN_ID + ".perspective"; //$NON-NLS-1$


    /* (non-Javadoc)
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
     */
    public void createInitialLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible( false );

        layout.addStandaloneView( ProxyView.ID, true, IPageLayout.LEFT, 0.5f, editorArea );
        layout.getViewLayout( ProxyView.ID ).setCloseable( false );

        layout.addStandaloneView( LdapMessageView.ID, true, IPageLayout.RIGHT, 0.5f, ProxyView.ID );
        layout.getViewLayout( LdapMessageView.ID ).setCloseable( false );

        // Perspective shortcuts
        layout.addPerspectiveShortcut( "org.apache.directory.studio.ldapbrowser.ui.perspective.BrowserPerspective" );
        layout.addPerspectiveShortcut( "org.apache.directory.ldapstudio.schemas.perspective" );
        layout.addPerspectiveShortcut( Perspective.ID );

        // View shortcuts
        layout.addShowViewShortcut( ProxyView.ID );
        layout.addShowViewShortcut( LdapMessageView.ID );
    }
}
