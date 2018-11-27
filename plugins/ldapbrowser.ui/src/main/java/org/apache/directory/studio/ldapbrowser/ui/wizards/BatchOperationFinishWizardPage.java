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

package org.apache.directory.studio.ldapbrowser.ui.wizards;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


/**
 * This class implements the Finish page of the Batch Operation Wizard.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BatchOperationFinishWizardPage extends WizardPage
{
    /** The continue on error flag key */
    public static final String EXECUTE_METHOD_DIALOGSETTING_KEY = BatchOperationFinishWizardPage.class.getName()
        + ".executeMethod"; //$NON-NLS-1$

    /** The continue on error flag key */
    public static final String CONTINUE_ON_ERROR_DIALOGSETTING_KEY = BatchOperationFinishWizardPage.class.getName()
        + ".continueOnError"; //$NON-NLS-1$

    // Execution Method Values
    public final static int EXECUTION_METHOD_NONE = -1;
    public final static int EXECUTION_METHOD_ON_CONNECTION = 0;
    public final static int EXECUTION_METHOD_LDIF_EDITOR = 1;
    public final static int EXECUTION_METHOD_LDIF_FILE = 2;
    public final static int EXECUTION_METHOD_LDIF_CLIPBOARD = 3;

    // UI widgets
    private Button executeOnConnectionButton;
    private Button continueOnErrorButton;
    private Button generateLdifButton;
    private Button generateInLDIFEditorButton;
    private Button generateInFileButton;
    private Button generateInClipboardButton;

    // Listeners
    private SelectionListener validateSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            validate();
        }
    };


    /**
     * Creates a new instance of BatchOperationFinishWizardPage.
     *
     * @param pageName the page name
     */
    public BatchOperationFinishWizardPage( String pageName )
    {
        super( pageName );
        super.setTitle( Messages.getString( "BatchOperationFinishWizardPage.SelectExecutionMethod" ) ); //$NON-NLS-1$
        super.setDescription( Messages.getString( "BatchOperationFinishWizardPage.PleaseSelectBatchOperation" ) ); //$NON-NLS-1$
        super.setPageComplete( false );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout();
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // Execute On Connection Button
        executeOnConnectionButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "BatchOperationFinishWizardPage.ExecuteOnConnection" ), 1 ); //$NON-NLS-1$

        // Execute On Connection Composite
        Composite executeOnConnectionComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        // Continue On Error Radio Button
        BaseWidgetUtils.createRadioIndent( executeOnConnectionComposite, 1 );
        continueOnErrorButton = BaseWidgetUtils.createCheckbox( executeOnConnectionComposite, Messages
            .getString( "ImportLdifMainWizardPage.ContinueOnError" ), 1 ); //$NON-NLS-1$

        // Generate LDIF Button
        generateLdifButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "BatchOperationFinishWizardPage.GenerateLDIF" ), 1 ); //$NON-NLS-1$

        // Generate LDIF Button Composite
        Composite generateLdifButtonComposite = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );

        // In The LDIF Editor Button
        BaseWidgetUtils.createRadioIndent( generateLdifButtonComposite, 1 );
        generateInLDIFEditorButton = BaseWidgetUtils.createRadiobutton( generateLdifButtonComposite, Messages
            .getString( "BatchOperationFinishWizardPage.GenerateLDIFInLDIFEditor" ), 1 ); //$NON-NLS-1$

        // In A File Button
        BaseWidgetUtils.createRadioIndent( generateLdifButtonComposite, 1 );
        generateInFileButton = BaseWidgetUtils.createRadiobutton( generateLdifButtonComposite, Messages
            .getString( "BatchOperationFinishWizardPage.GenerateLDIFInFile" ), 1 ); //$NON-NLS-1$

        // In The Clipboard Button
        BaseWidgetUtils.createRadioIndent( generateLdifButtonComposite, 1 );
        generateInClipboardButton = BaseWidgetUtils.createRadiobutton( generateLdifButtonComposite, Messages
            .getString( "BatchOperationFinishWizardPage.GenerateLDIFInClipBoard" ), 1 ); //$NON-NLS-1$

        init();
        validate();
        addListeners();

        setControl( composite );
    }


    /**
     * Initializes the UI.
     */
    private void init()
    {
        try
        {
            // Default value for the 'Execute Method' dialog setting
            if ( BrowserUIPlugin.getDefault().getDialogSettings().get( EXECUTE_METHOD_DIALOGSETTING_KEY ) == null )
            {
                BrowserUIPlugin.getDefault().getDialogSettings()
                    .put( EXECUTE_METHOD_DIALOGSETTING_KEY, EXECUTION_METHOD_ON_CONNECTION );
            }

            // Default value for the 'Continue On Error' dialog setting
            if ( BrowserUIPlugin.getDefault().getDialogSettings().get( CONTINUE_ON_ERROR_DIALOGSETTING_KEY ) == null )
            {
                BrowserUIPlugin.getDefault().getDialogSettings().put( CONTINUE_ON_ERROR_DIALOGSETTING_KEY, true );
            }

            // Getting the 'Execute Method' dialog setting
            int executeMethod = BrowserUIPlugin.getDefault().getDialogSettings()
                .getInt( EXECUTE_METHOD_DIALOGSETTING_KEY );

            switch ( executeMethod )
            {
                case EXECUTION_METHOD_ON_CONNECTION:
                    executeOnConnectionButton.setSelection( true );
                    generateInLDIFEditorButton.setSelection( true );
                    break;
                case EXECUTION_METHOD_LDIF_EDITOR:
                    generateLdifButton.setSelection( true );
                    generateInLDIFEditorButton.setSelection( true );
                    break;
                case EXECUTION_METHOD_LDIF_FILE:
                    generateLdifButton.setSelection( true );
                    generateInFileButton.setSelection( true );
                    break;
                case EXECUTION_METHOD_LDIF_CLIPBOARD:
                    generateLdifButton.setSelection( true );
                    generateInClipboardButton.setSelection( true );
                    break;
            }

            // Getting the 'Continue On Error' dialog setting
            continueOnErrorButton.setSelection( BrowserUIPlugin.getDefault().getDialogSettings()
                .getBoolean( CONTINUE_ON_ERROR_DIALOGSETTING_KEY ) );
        }
        catch ( Exception e )
        {
            // Nothing to do
        }
    }


    /**
     * Validates the page.
     */
    private void validate()
    {
        continueOnErrorButton.setEnabled( executeOnConnectionButton.getSelection() );
        generateInLDIFEditorButton.setEnabled( generateLdifButton.getSelection() );
        generateInFileButton.setEnabled( generateLdifButton.getSelection() );
        generateInClipboardButton.setEnabled( generateLdifButton.getSelection() );

        setPageComplete( getExecutionMethod() != EXECUTION_METHOD_NONE );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        executeOnConnectionButton.addSelectionListener( validateSelectionListener );
        generateLdifButton.addSelectionListener( validateSelectionListener );
    }


    /**
     * Gets the execution method.
     *
     * @return the execution method
     */
    public int getExecutionMethod()
    {
        if ( executeOnConnectionButton.getSelection() )
        {
            return EXECUTION_METHOD_ON_CONNECTION;
        }
        else if ( generateLdifButton.getSelection() )
        {
            if ( generateInLDIFEditorButton.getSelection() )
            {
                return EXECUTION_METHOD_LDIF_EDITOR;
            }
            else if ( generateInFileButton.getSelection() )
            {
                return EXECUTION_METHOD_LDIF_FILE;
            }
            else if ( generateInClipboardButton.getSelection() )
            {
                return EXECUTION_METHOD_LDIF_CLIPBOARD;
            }
        }

        return EXECUTION_METHOD_NONE;
    }


    /**
     * Gets the continue on error flag.
     *
     * @return the continue on error flag
     */
    public boolean getContinueOnError()
    {
        return continueOnErrorButton.getSelection();
    }


    /**
     * Saves the dialog settings.
     */
    public void saveDialogSettings()
    {
        BrowserUIPlugin.getDefault().getDialogSettings().put( EXECUTE_METHOD_DIALOGSETTING_KEY, getExecutionMethod() );
        BrowserUIPlugin.getDefault().getDialogSettings()
            .put( CONTINUE_ON_ERROR_DIALOGSETTING_KEY, getContinueOnError() );
    }
}
