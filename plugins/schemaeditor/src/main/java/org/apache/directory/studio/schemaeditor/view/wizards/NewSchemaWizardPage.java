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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class represents the WizardPage of the NewSchemaWizard.
 * <p>
 * It is used to let the user create a new Schema
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewSchemaWizardPage extends AbstractWizardPage
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
        super( "NewSchemaWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewSchemaWizardPage.CreateSchema" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewSchemaWizardPage.PleaseSpecifiyName" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_SCHEMA_NEW_WIZARD ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
    }


    /**
     * {@inheritDoc}
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout( 2, false );
        composite.setLayout( layout );

        // Name
        Label nameLabel = new Label( composite, SWT.NONE );
        nameLabel.setText( Messages.getString( "NewSchemaWizardPage.SchemaName" ) ); //$NON-NLS-1$
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
        if ( Activator.getDefault().getSchemaHandler() == null )
        {
            nameText.setEnabled( false );
            displayErrorMessage( Messages.getString( "NewSchemaWizardPage.ErrorNoSchemaProjectOpen" ) ); //$NON-NLS-1$
        }
        else
        {
            displayErrorMessage( null );
            setPageComplete( false );
        }
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        // Name
        if ( nameText.getText().equals( "" ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "NewSchemaWizardPage.ErrorNoNameSpecified" ) ); //$NON-NLS-1$
            return;
        }
        else if ( schemaHandler.isSchemaNameAlreadyTaken( nameText.getText() ) )
        {
            displayErrorMessage( Messages.getString( "NewSchemaWizardPage.ErrorSchemaNameExists" ) ); //$NON-NLS-1$
            return;
        }

        displayErrorMessage( null );
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
