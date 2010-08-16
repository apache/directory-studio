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
package org.apache.directory.studio.apacheds.configuration.editor.v154.dialogs;


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
 * This class implements the Dialog for SASL Realm.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslRealmDialog extends Dialog
{
    /** The initial value */
    private String initialValue;

    /** The return value */
    private String returnValue;

    /** The dirty flag */
    private boolean dirty = false;

    // UI Fields
    private Text saslRealmText;


    /**
     * Creates a new instance of SaslRealmDialog.
     */
    public SaslRealmDialog( String initialValue )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.initialValue = initialValue;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "SaslRealmDialog.SaslRealmDialog" ) ); //$NON-NLS-1$
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true ) );

        Label saslRealmLabel = new Label( composite, SWT.NONE );
        saslRealmLabel.setText( Messages.getString( "SaslRealmDialog.SaslRealm" ) ); //$NON-NLS-1$

        saslRealmText = new Text( composite, SWT.BORDER );
        saslRealmText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initFromInput();
        addListeners();

        return composite;
    }


    /**
     * Initializes the UI from the input.
     */
    private void initFromInput()
    {
        saslRealmText.setText( ( initialValue == null ) ? "" : initialValue ); //$NON-NLS-1$
    }


    /**
     * Adds listeners to the UI Fields.
     */
    private void addListeners()
    {
        saslRealmText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dirty = true;
            }
        } );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed()
    {
        returnValue = saslRealmText.getText();

        super.okPressed();
    }


    /**
     * Gets the SASL Realm.
     *
     * @return
     *      the SASL Realm
     */
    public String getSaslRealm()
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
