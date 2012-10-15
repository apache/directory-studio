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
    public static final String CONTINUE_ON_ERROR_DIALOGSETTING_KEY = BatchOperationFinishWizardPage.class.getName()
        + ".continueOnError"; //$NON-NLS-1$

    public final static int EXECUTION_METHOD_NONE = -1;
    public final static int EXECUTION_METHOD_LDIF = 0;
    public final static int EXECUTION_METHOD_ONLINE = 1;

    // UI widgets
    private Button executeOnlineButton;
    private Button generateLdifButton;
    private Button continueOnErrorButton;


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
     * Validates the page.
     */
    private void validate()
    {
        continueOnErrorButton.setEnabled( getExecutionMethod() == EXECUTION_METHOD_ONLINE );
        setPageComplete( getExecutionMethod() != EXECUTION_METHOD_NONE );
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 2, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // Execute Online Button
        executeOnlineButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "BatchOperationFinishWizardPage.ExecuteOnline" ), 2 ); //$NON-NLS-1$
        executeOnlineButton.setSelection( true );
        executeOnlineButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        // Continue On Error Radio Button
        BaseWidgetUtils.createRadioIndent( composite, 1 );
        continueOnErrorButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "ImportLdifMainWizardPage.ContinueOnError" ), 1 ); //$NON-NLS-1$
        if ( BrowserUIPlugin.getDefault().getDialogSettings().get( CONTINUE_ON_ERROR_DIALOGSETTING_KEY ) == null )
        {
            BrowserUIPlugin.getDefault().getDialogSettings().put( CONTINUE_ON_ERROR_DIALOGSETTING_KEY, true );
        }
        continueOnErrorButton.setSelection( BrowserUIPlugin.getDefault().getDialogSettings()
            .getBoolean( CONTINUE_ON_ERROR_DIALOGSETTING_KEY ) );

        // Generate LDIF Button
        generateLdifButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "BatchOperationFinishWizardPage.GenerateLDIF" ), 2 ); //$NON-NLS-1$
        generateLdifButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        validate();

        setControl( composite );
    }


    /**
     * Gets the execution method.
     *
     * @return the execution method
     */
    public int getExecutionMethod()
    {
        if ( executeOnlineButton.getSelection() )
        {
            return EXECUTION_METHOD_ONLINE;
        }
        else if ( generateLdifButton.getSelection() )
        {
            return EXECUTION_METHOD_LDIF;
        }
        else
        {
            return EXECUTION_METHOD_NONE;
        }
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
        BrowserUIPlugin.getDefault().getDialogSettings().put( CONTINUE_ON_ERROR_DIALOGSETTING_KEY,
            continueOnErrorButton.getSelection() );
    }
}
