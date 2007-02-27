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
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Default Page for new schema wizard
 */
public class CreateANewSchemaWizardPage extends WizardPage
{

    @SuppressWarnings("unused")//$NON-NLS-1$
    private ISelection selection;

    private Text nameField;


    /**
     * Default constructor
     * 
     * @param selection
     */
    public CreateANewSchemaWizardPage( ISelection selection )
    {
        super( "CreateANewSchemaWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "CreateANewSchemaWizardPage.Page_Title" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "CreateANewSchemaWizardPage.Page_Description" ) ); //$NON-NLS-1$
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA_NEW_WIZARD ) );
        this.selection = selection;
    }


    /**
     * Name field getter
     * 
     * @return
     */
    public String getNameField()
    {
        return this.nameField.getText();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite container = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        container.setLayout( layout );
        layout.numColumns = 2;
        layout.verticalSpacing = 1;
        Label label = new Label( container, SWT.NULL );
        label.setText( Messages.getString( "CreateANewSchemaWizardPage.Name" ) ); //$NON-NLS-1$
        nameField = new Text( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData( GridData.FILL_HORIZONTAL );
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


    private void dialogChanged()
    {
        if ( getNameField().length() == 0 )
        {
            updateStatus( Messages.getString( "CreateANewSchemaWizardPage.A_name_must_be_specified" ) ); //$NON-NLS-1$
            return;
        }

        if ( SchemaPool.getInstance().getSchema( getNameField() ) != null )
        {
            updateStatus( Messages
                .getString( "CreateANewSchemaWizardPage.A_schema_of_the_same_name_is_already_loaded_in_the_pool" ) ); //$NON-NLS-1$
            return;
        }

        updateStatus( null );
    }


    private void updateStatus( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }
}
