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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The search result editor preference page contains settings for the 
 * search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    /** The show DN button. */
    private Button showDnButton;

    /** The show links button. */
    private Button showLinksButton;


    /**
     * Creates a new instance of SearchResultEditorPreferencePage.
     */
    public SearchResultEditorPreferencePage()
    {
        super( Messages.getString( "SearchResultEditorPreferencePage.ResultEditor" ) ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "SearchResultEditorPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
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
        showDnButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "SearchResultEditorPreferencePage.DNAsFirst" ), 1 ); //$NON-NLS-1$
        showDnButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) );
        showLinksButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "SearchResultEditorPreferencePage.DNAsLink" ), 1 ); //$NON-NLS-1$
        showLinksButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS ) );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN,
            showDnButton.getSelection() );
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS,
            showLinksButton.getSelection() );
        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        showDnButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) );
        showLinksButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS ) );
        super.performDefaults();
    }

}
