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

package org.apache.directory.ldapstudio.browser.ui.dialogs.preferences;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyEvent;
import org.apache.directory.ldapstudio.browser.ui.widgets.WidgetModifyListener;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class SearchResultEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
    WidgetModifyListener
{

    private Button showDnButton;

    private Button showLinksButton;


    public SearchResultEditorPreferencePage()
    {
        super();
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for the LDAP search result editor:" );
    }


    public void init( IWorkbench workbench )
    {
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        showDnButton = BaseWidgetUtils.createCheckbox( composite, "Show DN as first column", 1 );
        showDnButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) );
        showLinksButton = BaseWidgetUtils.createCheckbox( composite, "Show DN a link", 1 );
        showLinksButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS ) );

        updateEnabled();
        validate();

        applyDialogFont( composite );
        return composite;
    }


    private void updateEnabled()
    {

    }


    public boolean performOk()
    {

        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN,
            this.showDnButton.getSelection() );
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS,
            this.showLinksButton.getSelection() );

        updateEnabled();
        validate();

        return true;
    }


    protected void performDefaults()
    {

        this.showDnButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) );
        this.showLinksButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS ) );

        updateEnabled();
        validate();

        super.performDefaults();
    }


    public void widgetModified( WidgetModifyEvent event )
    {
        updateEnabled();
        validate();
    }


    protected void validate()
    {

    }

}
