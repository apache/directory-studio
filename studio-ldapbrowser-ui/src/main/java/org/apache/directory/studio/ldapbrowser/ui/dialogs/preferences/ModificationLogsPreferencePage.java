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

package org.apache.directory.studio.ldapbrowser.ui.dialogs.preferences;


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The modification logs preference page contains settings of the 
 * modification logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModificationLogsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button enableModificationLogging;


    /**
     * Creates a new instance of SearchResultEditorPreferencePage.
     */
    public ModificationLogsPreferencePage()
    {
        super( "Modification Logs" );
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for the modification logs view:" );
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        enableModificationLogging = BaseWidgetUtils.createCheckbox( composite, "Enable modification logs", 1 );
        enableModificationLogging.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE ) );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE,
            enableModificationLogging.getSelection() );
        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        enableModificationLogging.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE ) );
        super.performDefaults();
    }

}
