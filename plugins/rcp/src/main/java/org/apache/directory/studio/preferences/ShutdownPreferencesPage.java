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
package org.apache.directory.studio.preferences;


import org.apache.directory.studio.Activator;
import org.apache.directory.studio.Messages;
import org.apache.directory.studio.PluginConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Shutdown Preferences Page.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ShutdownPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage
{
    // UI fields
    private Button confirmExitClosingLastWindowCheckbox;


    /**
     * Creates a new instance of EntryEditorsPreferencePage.
     */
    public ShutdownPreferencesPage()
    {
        super( Messages.getString( "ShutdownPreferencesPage.PageTitle" ) );
        super.setPreferenceStore( Activator.getDefault().getPreferenceStore() );
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
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout();
        gl.marginHeight = gl.marginWidth = 0;
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        confirmExitClosingLastWindowCheckbox = new Button( composite, SWT.CHECK );
        confirmExitClosingLastWindowCheckbox.setText( Messages
            .getString( "ShutdownPreferencesPage.ConfirmExitClosingLastWindow" ) ); //$NON-NLS-1$

        refreshUI();

        return composite;
    }


    /**
     * Refreshes the UI.
     */
    private void refreshUI()
    {
        confirmExitClosingLastWindowCheckbox.setSelection( getPreferenceStore().getBoolean(
            PluginConstants.PREFERENCE_EXIT_PROMPT_ON_CLOSE_LAST_WINDOW ) );
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue( PluginConstants.PREFERENCE_EXIT_PROMPT_ON_CLOSE_LAST_WINDOW,
            confirmExitClosingLastWindowCheckbox.getSelection() );

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        getPreferenceStore().setValue( PluginConstants.PREFERENCE_EXIT_PROMPT_ON_CLOSE_LAST_WINDOW,
            getPreferenceStore().getDefaultBoolean( PluginConstants.PREFERENCE_EXIT_PROMPT_ON_CLOSE_LAST_WINDOW ) );

        super.performDefaults();
    }


    /**
     * Prompts the user (or not, depending on the preferences) while exiting the application.
     *
     * @return <code>true</code> if the application needs to be exited,
     *         <code>false</code> if not.
     */
    public static boolean promptOnExit()
    {
        // Checking for multiple workbench windows
        if ( PlatformUI.getWorkbench().getWorkbenchWindowCount() > 1 )
        {
            return true;
        }

        // Getting the preferred exit mode from the preferences
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        boolean promptOnExit = store.getBoolean( PluginConstants.PREFERENCE_EXIT_PROMPT_ON_CLOSE_LAST_WINDOW );

        if ( promptOnExit )
        {
            MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm( PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(),
                Messages.getString( "ShutdownPreferencesPage.PromptOnExitTitle" ), //$NON-NLS-1$
                Messages.getString( "ShutdownPreferencesPage.PromptOnExitMessage" ), //$NON-NLS-1$
                Messages.getString( "ShutdownPreferencesPage.PromptOnExitToggleMessage" ), false, null, null ); //$NON-NLS-1$

            // Checking the dialog's return code
            if ( dialog.getReturnCode() != IDialogConstants.OK_ID )
            {
                return false;
            }

            // Saving the preferred exit mode value to the preferences
            if ( dialog.getToggleState() )
            {
                store.setValue( PluginConstants.PREFERENCE_EXIT_PROMPT_ON_CLOSE_LAST_WINDOW, false );
                Activator.getDefault().savePluginPreferences();
            }
        }

        return true;
    }
}