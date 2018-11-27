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
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
    /** The show Dn button. */
    private Button showDnButton;

    /** The show links button. */
    private Button showLinksButton;

    /** The sort/filter limit text */
    private Text sortFilterLimitText;


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
        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 2, 1 );

        BaseWidgetUtils.createSpacer( composite, 2 );

        // Show Dn
        showDnButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "SearchResultEditorPreferencePage.DNAsFirst" ), 2 ); //$NON-NLS-1$
        showDnButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) );

        // Show DN As Link
        showLinksButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "SearchResultEditorPreferencePage.DNAsLink" ), 2 ); //$NON-NLS-1$
        showLinksButton.setSelection( getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS ) );

        // Sort/Filter Limit
        String sortFilterLimitTooltip = Messages.getString( "SearchResultEditorPreferencePage.SortFilterLimitToolTip" ); //$NON-NLS-1$
        Label sortFilterLimitLabel = BaseWidgetUtils.createLabel( composite, Messages
            .getString( "SearchResultEditorPreferencePage.SortFilterLimitColon" ), 1 ); //$NON-NLS-1$
        sortFilterLimitLabel.setToolTipText( sortFilterLimitTooltip );
        sortFilterLimitText = BaseWidgetUtils.createText( composite, "" + getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SORT_FILTER_LIMIT ), 5, 1 ); //$NON-NLS-1$
        sortFilterLimitText.setToolTipText( sortFilterLimitTooltip );
        sortFilterLimitText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        // Show Dn
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN,
            showDnButton.getSelection() );

        // Show DN As Link
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS,
            showLinksButton.getSelection() );

        // Sort/Filter Limit
        int sortFilterLimit = getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SORT_FILTER_LIMIT );
        try
        {
            sortFilterLimit = Integer.parseInt( sortFilterLimitText.getText().trim() );
        }
        catch ( NumberFormatException nfe )
        {
        }
        getPreferenceStore().setValue( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SORT_FILTER_LIMIT,
            sortFilterLimit );

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
