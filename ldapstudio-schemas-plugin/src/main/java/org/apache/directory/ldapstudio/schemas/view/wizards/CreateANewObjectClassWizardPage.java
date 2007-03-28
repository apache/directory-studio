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

package org.apache.directory.ldapstudio.schemas.view.wizards;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.ViewUtils;
import org.apache.directory.shared.asn1.primitives.OID;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Default Page for new attribute type wizard
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class CreateANewObjectClassWizardPage extends WizardPage
{
    // UI Fields
    private Text oidField;
    private Text nameField;


    /**
     * Creates a new instance of CreateANewObjectClassWizardPage.
     */
    public CreateANewObjectClassWizardPage()
    {
        super( "CreateANewObjecClassWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "CreateANewObjectClassWizardPage.Page_Title" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "CreateANewObjectClassWizardPage.Page_Description" ) ); //$NON-NLS-1$
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );
    }


    /**
     * OID field getter
     * 
     * @return the value of the OID field
     */
    public String getOidField()
    {
        return this.oidField.getText();
    }


    /**
     * Name field getter
     * 
     * @return the value of the Name field
     */
    public String getNameField()
    {
        return this.nameField.getText();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 1;
        container.setLayout( layout );

        GridData gd = new GridData( GridData.FILL_HORIZONTAL );

        // Setting up the OID section
        new Label( container, SWT.NULL );

        final Button autoOID = new Button( container, SWT.CHECK );
        autoOID.setText( Messages.getString( "CreateANewObjectClassWizardPage.Prefix_with_the_default_OID" ) ); //$NON-NLS-1$

        autoOID.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                IPreferenceStore store = Activator.getDefault().getPreferenceStore();

                store.setValue( PluginConstants.PREFS_SCHEMAS_EDITOR_AUTO_OID, autoOID.getSelection() );
                if ( autoOID.getSelection() )
                {
                    String temp = store.getString( PluginConstants.PREFS_SCHEMAS_EDITOR_COMPANY_OID );
                    oidField.setText( temp + "." ); //$NON-NLS-1$
                }
                else
                {
                    oidField.setText( "" ); //$NON-NLS-1$
                }
            }
        } );

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        boolean auto_oid = store.getBoolean( PluginConstants.PREFS_SCHEMAS_EDITOR_AUTO_OID );
        autoOID.setSelection( auto_oid );

        Label label = new Label( container, SWT.NULL );
        label.setText( Messages.getString( "CreateANewObjectClassWizardPage.OID" ) ); //$NON-NLS-1$
        oidField = new Text( container, SWT.BORDER | SWT.SINGLE );
        if ( auto_oid )
        {
            String temp = store.getString( PluginConstants.PREFS_SCHEMAS_EDITOR_COMPANY_OID );
            oidField.setText( temp + "." ); //$NON-NLS-1$
        }
        oidField.setLayoutData( gd );
        oidField.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        oidField.addVerifyListener( new VerifyListener()
        {
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "([0-9]*\\.?)*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );

        // Setting up the Name section
        Label label2 = new Label( container, SWT.NULL );
        label2.setText( Messages.getString( "CreateANewObjectClassWizardPage.Name" ) ); //$NON-NLS-1$
        nameField = new Text( container, SWT.BORDER | SWT.SINGLE );
        nameField.setLayoutData( gd );
        nameField.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );
        dialogChanged();
        setControl( container );
        setErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        if ( getOidField().length() == 0 )
        {
            updateStatus( Messages.getString( "CreateANewObjectClassWizardPage.An_OID_must_be_specified" ) ); //$NON-NLS-1$
            return;
        }

        if ( !OID.isOID( getOidField() ) )
        {
            updateStatus( Messages.getString( "CreateANewObjectClassWizardPage.Malforme_OID" ) ); //$NON-NLS-1$
            return;
        }

        if ( getNameField().length() == 0 )
        {
            updateStatus( Messages.getString( "CreateANewObjectClassWizardPage.A_name_must_be_specified" ) ); //$NON-NLS-1$
            return;
        }

        if ( SchemaPool.getInstance().containsSchemaElement( getOidField() ) )
        {
            updateStatus( Messages
                .getString( "CreateANewObjectClassWizardPage.An_element_of_the_same_OID_already_exists" ) ); //$NON-NLS-1$
            return;
        }

        if ( !ViewUtils.verifyName( getNameField() ) )
        {
            updateStatus( "Name is not valid." );
            return;
        }

        if ( SchemaPool.getInstance().containsSchemaElement( getNameField() ) )
        {
            updateStatus( Messages
                .getString( "CreateANewObjectClassWizardPage.An_element_of_the_same_name_already_exists" ) ); //$NON-NLS-1$
            return;
        }

        updateStatus( null );
    }


    /**
     * Updates the status of the page.
     *
     * @param message
     *      the message to display
     */
    private void updateStatus( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }
}
