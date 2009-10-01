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

package org.apache.directory.studio.ldapbrowser.ui;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * This class is used to set default preference values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserUIPreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();

        // Browser
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR, true );

        // Search Result Editor
        store.setDefault( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS, true );

        // Entry Editors
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USE_USER_PRIORITIES, false );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITORS_USER_PRIORITIES, "" ); //$NON-NLS-1$
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE,
            BrowserUIConstants.PREFERENCE_ENTRYEDITORS_OPEN_MODE_HISTORICAL_BEHAVIOR );
    }

}
