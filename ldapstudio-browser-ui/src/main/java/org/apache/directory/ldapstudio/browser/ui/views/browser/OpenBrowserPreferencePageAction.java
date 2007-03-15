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
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * The OpenBrowserPreferencePageAction is used to open the 
 * preference dialog with the browser's preference page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenBrowserPreferencePageAction extends Action
{

    /**
     * Creates a new instance of OpenBrowserPreferencePageAction.
     */
    public OpenBrowserPreferencePageAction()
    {
        super.setText( "Preferences..." );
        setToolTipText( "Preferences..." );
        setEnabled( true );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        Shell shell = Display.getCurrent().getActiveShell();
        String pageId = BrowserUIConstants.PREFERENCEPAGEID_BROWSER;
        PreferencesUtil.createPreferenceDialogOn( shell, pageId, new String[]
            { pageId }, null ).open();
    }

}
