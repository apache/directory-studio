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

package org.apache.directory.ldapstudio.browser.ui.views.browser;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;


public class ShowBookmarksAction extends Action
{

    public ShowBookmarksAction()
    {
        super( "Show Bookmarks", IAction.AS_CHECK_BOX );
        super.setEnabled( true );
        super.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_BROWSER_SHOW_BOOKMARKS ) );
    }


    public void run()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().setValue(
            BrowserUIConstants.PREFERENCE_BROWSER_SHOW_BOOKMARKS, super.isChecked() );
    }


    public void dispose()
    {
    }

}
