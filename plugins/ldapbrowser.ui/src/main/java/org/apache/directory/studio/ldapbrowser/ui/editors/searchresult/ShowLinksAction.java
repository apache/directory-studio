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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;


/**
 * Action to enable/disable DNs as link.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ShowLinksAction extends Action
{

    /**
     * Creates a new instance of ShowLinksAction.
     */
    public ShowLinksAction()
    {
        super( Messages.getString( "ShowLinksAction.DNAsLink" ), AS_CHECK_BOX ); //$NON-NLS-1$
        super.setToolTipText( getText() );
        super.setEnabled( true );
        super.setChecked( BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS ) );
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().setValue(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS, super.isChecked() );
    }


    /**
     * Disposes this action.
     */
    public void dispose()
    {
    }

}
