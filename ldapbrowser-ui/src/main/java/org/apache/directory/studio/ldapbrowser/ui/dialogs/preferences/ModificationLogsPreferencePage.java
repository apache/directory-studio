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


import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The modification logs preference page contains settings of the 
 * modification logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModificationLogsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private Button enableModificationLogging;
    private Text logFileCountText;
    private Text logFileSizeText;
    private Text maskedAttributesText;


    /**
     * Creates a new instance of ModificationLogsPreferencePage.
     */
    public ModificationLogsPreferencePage()
    {
        super( Messages.getString( "ModificationLogsPreferencePage.ModificationLogs" ) ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserUIPlugin.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "ModificationLogsPreferencePage.GeneralSettingsModificationLogs" ) ); //$NON-NLS-1$
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
        enableModificationLogging = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "ModificationLogsPreferencePage.EnableModificationLogs" ), 1 ); //$NON-NLS-1$
        enableModificationLogging.setSelection( ConnectionCorePlugin.getDefault().getPluginPreferences().getBoolean(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE ) );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group maskedAttributesGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1,
            1 ), Messages.getString( "ModificationLogsPreferencePage.MaskedAttributes" ), 1 ); //$NON-NLS-1$
        Composite maskedAttributesComposite = BaseWidgetUtils.createColumnContainer( maskedAttributesGroup, 1, 1 );
        maskedAttributesText = BaseWidgetUtils.createText( maskedAttributesComposite, "", 1 ); //$NON-NLS-1$
        maskedAttributesText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getString(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES ) );
        String maskedAttributesHelp = Messages.getString( "ModificationLogsPreferencePage.CommaSeparatedList" ); //$NON-NLS-1$
        BaseWidgetUtils.createWrappedLabel( maskedAttributesComposite, maskedAttributesHelp, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        Group rotateGroup = BaseWidgetUtils.createGroup( BaseWidgetUtils.createColumnContainer( composite, 1, 1 ),
            Messages.getString( "ModificationLogsPreferencePage.LogFileRotation" ), 1 ); //$NON-NLS-1$
        Composite rotateComposite = BaseWidgetUtils.createColumnContainer( rotateGroup, 5, 1 );
        BaseWidgetUtils.createLabel( rotateComposite, Messages.getString( "ModificationLogsPreferencePage.Use" ), 1 ); //$NON-NLS-1$
        logFileCountText = BaseWidgetUtils.createText( rotateComposite, "", 3, 1 ); //$NON-NLS-1$
        logFileCountText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getString(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT ) );
        logFileCountText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
                if ( "".equals( logFileCountText.getText() ) && e.text.matches( "[0]" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    e.doit = false;
                }
            }
        } );
        BaseWidgetUtils.createLabel( rotateComposite, Messages
            .getString( "ModificationLogsPreferencePage.LogFilesEach" ), 1 ); //$NON-NLS-1$
        logFileSizeText = BaseWidgetUtils.createText( rotateComposite, "", 5, 1 ); //$NON-NLS-1$
        logFileSizeText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getString(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE ) );
        logFileSizeText.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
                if ( "".equals( logFileSizeText.getText() ) && e.text.matches( "[0]" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    e.doit = false;
                }
            }
        } );
        BaseWidgetUtils.createLabel( rotateComposite, Messages.getString( "ModificationLogsPreferencePage.KB" ), 1 ); //$NON-NLS-1$

        applyDialogFont( composite );
        return composite;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE, enableModificationLogging.getSelection() );
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT, logFileCountText.getText() );
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE, logFileSizeText.getText() );
        ConnectionCorePlugin.getDefault().getPluginPreferences().setValue(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES, maskedAttributesText.getText() );
        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        enableModificationLogging.setSelection( ConnectionCorePlugin.getDefault().getPluginPreferences()
            .getDefaultBoolean( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE ) );
        logFileCountText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultString(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT ) );
        logFileSizeText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultString(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE ) );
        maskedAttributesText.setText( ConnectionCorePlugin.getDefault().getPluginPreferences().getDefaultString(
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES ) );
        super.performDefaults();
    }

}
