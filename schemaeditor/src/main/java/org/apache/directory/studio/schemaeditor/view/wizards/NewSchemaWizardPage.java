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
package org.apache.directory.studio.schemaeditor.view.wizards;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
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
 * This class represents the WizardPage of the NewSchemaWizard.
 * <p>
 * It is used to let the user create a new Schema
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewSchemaWizardPage extends WizardPage
{
    /** The ProjectsHandler */
    private SchemaHandler schemaHandler;

    // UI Fields
    private Text nameText;


    /**
     * Creates a new instance of NewSchemaWizardPage.
     */
    protected NewSchemaWizardPage()
    {
        super( "NewSchemaWizardPage" );
        setTitle( "Create a Schema" );
        setDescription( "Please specify a name to create a new schema." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_SCHEMA_NEW_WIZARD ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );

        // Name
        Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setText( "Schema name:" );
        nameText = new Text( composite, SWT.BORDER );
        nameText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        nameText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                dialogChanged();
            }
        } );

        initFields();

        setControl( composite );
    }


    /**
     * Initializes the UI Fields.
     */
    private void initFields()
    {
        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Name
        if ( nameText.getText().equals( "" ) )
        {
            displayErrorMessage( "A name must be specified." );
            return;
        }
        else if ( schemaHandler.isSchemaNameAlreadyTaken( nameText.getText() ) )
        {
            displayErrorMessage( "A schema with this name already exists." );
            return;
        }

        displayErrorMessage( null );
    }


    /**
     * Displays an error message and set the page status as incomplete
     * if the message is not null.
     *
     * @param message
     *      the message to display
     */
    private void displayErrorMessage( String message )
    {
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * Gets the name of the schema.
     *
     * @return
     *      the name of the schema
     */
    public String getSchemaName()
    {
        return nameText.getText();
    }
}
