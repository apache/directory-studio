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


import org.apache.directory.studio.connection.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class BatchOperationFinishWizardPage extends WizardPage
{

    public final static int EXECUTION_METHOD_NONE = -1;

    public final static int EXECUTION_METHOD_LDIF = 0;

    public final static int EXECUTION_METHOD_ONLINE = 1;

    private Button executeOnlineButton;

    private Button generateLdifButton;


    public BatchOperationFinishWizardPage( String pageName, BatchOperationWizard wizard )
    {
        super( pageName );
        super.setTitle( Messages.getString( "BatchOperationFinishWizardPage.SelectExecutionMethod" ) ); //$NON-NLS-1$
        super.setDescription( Messages.getString( "BatchOperationFinishWizardPage.PleaseSelectBatchOperation" ) ); //$NON-NLS-1$
        super.setPageComplete( false );
    }


    private void validate()
    {
        setPageComplete( getExecutionMethod() != EXECUTION_METHOD_NONE );
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        generateLdifButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "BatchOperationFinishWizardPage.GenerateLDIF" ), 1 ); //$NON-NLS-1$
        generateLdifButton.setSelection( true );
        generateLdifButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        executeOnlineButton = BaseWidgetUtils.createRadiobutton( composite, Messages
            .getString( "BatchOperationFinishWizardPage.ExecuteOnline" ), 1 ); //$NON-NLS-1$
        executeOnlineButton.setEnabled( false );
        executeOnlineButton.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        } );

        validate();

        setControl( composite );
    }


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

}
