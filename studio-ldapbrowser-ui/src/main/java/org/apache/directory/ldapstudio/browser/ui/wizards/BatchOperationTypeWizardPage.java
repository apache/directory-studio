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

package org.apache.directory.ldapstudio.browser.ui.wizards;


import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;


public class BatchOperationTypeWizardPage extends WizardPage
{

    public final static int OPERATION_TYPE_NONE = -1;

    public final static int OPERATION_TYPE_MODIFY = 0;

    public final static int OPERATION_TYPE_DELETE = 1;

    public final static int OPERATION_TYPE_CREATE_LDIF = 2;

    private final static String[] OPERATION_TYPES =
        { "Modify entries", "Delete entries", "Execute LDIF changetype fragment on each entry" };

    private Button[] operationTypeButtons;


    public BatchOperationTypeWizardPage( String pageName, BatchOperationWizard wizard )
    {
        super( pageName );
        super.setTitle( "Select Operation Type" );
        super.setDescription( "Please select the batch operation type." );
        super.setPageComplete( false );
    }


    private void validate()
    {
        setPageComplete( getOperationType() != OPERATION_TYPE_NONE );
    }


    public void createControl( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout gl = new GridLayout( 1, false );
        composite.setLayout( gl );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        operationTypeButtons = new Button[OPERATION_TYPES.length];
        for ( int i = 0; i < operationTypeButtons.length; i++ )
        {
            operationTypeButtons[i] = BaseWidgetUtils.createRadiobutton( composite, OPERATION_TYPES[i], 1 );
            operationTypeButtons[i].addSelectionListener( new SelectionListener()
            {
                public void widgetDefaultSelected( SelectionEvent e )
                {
                    validate();
                }


                public void widgetSelected( SelectionEvent e )
                {
                    validate();
                }
            } );
        }
        operationTypeButtons[0].setSelection( true );

        validate();

        setControl( composite );

    }


    public int getOperationType()
    {

        for ( int i = 0; i < operationTypeButtons.length; i++ )
        {
            if ( operationTypeButtons[i].getSelection() )
            {
                return i;
            }
        }

        return OPERATION_TYPE_NONE;
    }

}