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
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The search logs preference page contains settings of the 
 * search logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchLogsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button enableSearchRequestLogging;
    private Button enableSearchResultEntryLogging;
    private Text logFileCountText;
    private Text logFileSizeText;

    /**
     * Creates a new instance of SearchResultEditorPreferencePage.
     */
    public SearchLogsPreferencePage()
    {
        super( Messages.getString( "SearchLogsPreferencePage.SearchLogs" ) ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "SearchLogsPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
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
        enableSearchRequestLogging = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "SearchLogsPreferencePage.EnableRequestLogs" ), 1 ); //$NON-NLS-1$
        enableSearchResultEntryLogging = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "SearchLogsPreferencePage.EnableResultLogs" ), 1 ); //$NON-NLS-1$

        Group rotateGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString( "SearchLogsPreferencePage.LogFileRotation" ), 1 ); //$NON-NLS-1$
        Composite rotateComposite = BaseWidgetUtils.createColumnContainer( rotateGroup, 5, 1 );
        BaseWidgetUtils.createLabel( rotateComposite, Messages.getString( "SearchLogsPreferencePage.Use" ), 1 ); //$NON-NLS-1$
        logFileCountText = BaseWidgetUtils.createText( rotateComposite, "", 3, 1 ); //$NON-NLS-1$
        logFileCountText.addVerifyListener( e -> {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
            if ( "".equals( logFileCountText.getText() ) && e.text.matches( "[0]" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                e.doit = false;
            }
        } );
        logFileCountText.addModifyListener( e -> validate() );
        BaseWidgetUtils.createLabel( rotateComposite, Messages.getString( "SearchLogsPreferencePage.LogFilesEach" ), //$NON-NLS-1$
            1 );
        logFileSizeText = BaseWidgetUtils.createText( rotateComposite, "", 5, 1 ); //$NON-NLS-1$
        logFileSizeText.addVerifyListener( e -> {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
            if ( "".equals( logFileSizeText.getText() ) && e.text.matches( "[0]" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                e.doit = false;
            }
        } );
        logFileSizeText.addModifyListener( e -> validate() );
        BaseWidgetUtils.createLabel( rotateComposite, Messages.getString( "SearchLogsPreferencePage.KB" ), 1 ); //$NON-NLS-1$

        setValues();

        applyDialogFont( composite );
        return composite;
    }


    private void setValues()
    {
        enableSearchRequestLogging.setSelection( ConnectionCorePlugin.getDefault().isSearchRequestLogsEnabled() );
        enableSearchResultEntryLogging
            .setSelection( ConnectionCorePlugin.getDefault().isSearchResultEntryLogsEnabled() );
        logFileCountText.setText( "" + ConnectionCorePlugin.getDefault().getSearchLogsFileCount() );
        logFileSizeText.setText( "" + ConnectionCorePlugin.getDefault().getSearchLogsFileSize() );
    }


    public void validate()
    {
        setValid( logFileCountText.getText().matches( "[0-9]+" ) && logFileSizeText.getText().matches( "[0-9]+" ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        IEclipsePreferences instancePreferences = ConnectionCorePlugin.getDefault().getInstanceScopePreferences();
        instancePreferences.putBoolean( ConnectionCoreConstants.PREFERENCE_SEARCHREQUESTLOGS_ENABLE,
            enableSearchRequestLogging.getSelection() );
        instancePreferences.putBoolean( ConnectionCoreConstants.PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE,
            enableSearchResultEntryLogging.getSelection() );
        instancePreferences.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT,
            Integer.parseInt( logFileCountText.getText() ) );
        instancePreferences.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE,
            Integer.parseInt( logFileSizeText.getText() ) );
        ConnectionCorePlugin.getDefault().flushInstanceScopePreferences();
        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        IEclipsePreferences instancePreferences = ConnectionCorePlugin.getDefault().getInstanceScopePreferences();
        instancePreferences.remove( ConnectionCoreConstants.PREFERENCE_SEARCHREQUESTLOGS_ENABLE );
        instancePreferences.remove( ConnectionCoreConstants.PREFERENCE_SEARCHRESULTENTRYLOGS_ENABLE );
        instancePreferences.remove( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT );
        instancePreferences.remove( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE );
        ConnectionCorePlugin.getDefault().flushInstanceScopePreferences();
        setValues();
        super.performDefaults();
    }

}
