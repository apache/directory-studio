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

package org.apache.directory.ldapstudio.browser.view.perspectives;


import org.apache.directory.ldapstudio.browser.Activator;
import org.apache.directory.ldapstudio.browser.view.views.AttributesView;
import org.apache.directory.ldapstudio.browser.view.views.BrowserView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class LdapBrowserPerspective implements IPerspectiveFactory
{

    /** The perspective's ID */
    public static final String ID = Activator.PLUGIN_ID + ".ldapBrowserPerspective";


    public void createInitialLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible( false );

        layout.addStandaloneView( BrowserView.ID, true, IPageLayout.LEFT, 1f, editorArea );
        layout.getViewLayout( BrowserView.ID ).setCloseable( false );

        layout.addStandaloneView( AttributesView.ID, true, IPageLayout.RIGHT, 0.6f, BrowserView.ID );
        layout.getViewLayout( AttributesView.ID ).setCloseable( false );
    }

}
