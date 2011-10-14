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
package org.apache.directory.studio.apacheds.configuration.editor.v155.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the Dialog for NTML Provider.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NtlmProviderDialog extends Dialog
{
    /** The initial value */
    private String initialValue;

    /** The return value */
    private String returnValue;

    /** The dirty flag */
    private boolean dirty = false;

    // UI Fields
    private Text ntlmProviderText;


    /**
     * Creates a new instance of SaslRealmDialog.
     */
    public NtlmProviderDialog( String initialValue )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.initialValue = initialValue;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "NtlmProviderDialog.NtlmProviderDialog" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        Label ntlmProviderLabel = new Label( composite, SWT.NONE );
        ntlmProviderLabel.setText( Messages.getString( "NtlmProviderDialog.NtlmProvider" ) ); //$NON-NLS-1$

        ntlmProviderText = new Text( composite, SWT.BORDER );
        ntlmProviderText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initFromInput();
        addListeners();

        return composite;
    }


    /**
     * Initializes the UI from the input.
     */
    private void initFromInput()
    {
        ntlmProviderText.setText( ( initialValue == null ) ? "" : initialValue ); //$NON-NLS-1$
    }


    /**
     * Adds listeners to the UI Fields.
     */
    private void addListeners()
    {
        ntlmProviderText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dirty = true;
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnValue = ntlmProviderText.getText();

        super.okPressed();
    }


    /**
     * Gets the NTLM Provider.
     *
     * @return
     *      the NTLM Provider
     */
    public String getNtlmProvider()
    {
        return returnValue;
    }


    /**
     * Returns the dirty flag of the dialog.
     *
     * @return
     *      the dirty flag of the dialog
     */
    public boolean isDirty()
    {
        return dirty;
    }
}
