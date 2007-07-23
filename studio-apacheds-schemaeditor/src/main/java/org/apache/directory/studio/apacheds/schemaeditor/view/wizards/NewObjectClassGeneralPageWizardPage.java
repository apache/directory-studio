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
package org.apache.directory.studio.apacheds.schemaeditor.view.wizards;


import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.view.dialogs.EditAliasesDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class represents the General WizardPage of the NewObjectClassWizard.
 * <p>
 * It is used to let the user enter general information about the
 * attribute type he wants to create (schema, OID, aliases an description).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class NewObjectClassGeneralPageWizardPage extends WizardPage
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The aliases */
    private String[] aliases;

    /** The selected schema */
    private Schema selectedSchema;

    // UI fields
    private ComboViewer schemaComboViewer;
    private Text oidText;
    private Text aliasesText;
    private Button aliasesButton;
    private Text descriptionText;


    /**
     * Creates a new instance of NewObjectClassGeneralPageWizardPage.
     */
    protected NewObjectClassGeneralPageWizardPage()
    {
        super( "NewObjectClassGeneralPageWizardPage" );
        setTitle( "Object Class" );
        setDescription( "Create a new object class." );
        setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
            PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );

        schemaHandler = Activator.getDefault().getSchemaHandler();
        aliases = new String[0];
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NULL );
        GridLayout layout = new GridLayout();
        composite.setLayout( layout );

        // Schema Group
        Group schemaGroup = new Group( composite, SWT.NONE );
        schemaGroup.setText( "Schema" );
        schemaGroup.setLayout( new GridLayout( 2, false ) );
        schemaGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schema
        Label schemaLabel = new Label( schemaGroup, SWT.NONE );
        schemaLabel.setText( "Schema:" );
        Combo schemaCombo = new Combo( schemaGroup, SWT.READ_ONLY );
        schemaCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        schemaComboViewer = new ComboViewer( schemaCombo );
        schemaComboViewer.setContentProvider( new ArrayContentProvider() );
        schemaComboViewer.setLabelProvider( new LabelProvider()
        {
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            public String getText( Object element )
            {
                if ( element instanceof Schema )
                {
                    return ( ( Schema ) element ).getName();
                }

                // Default
                return super.getText( element );
            }
        } );
        schemaComboViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            public void selectionChanged( SelectionChangedEvent event )
            {
                dialogChanged();
            }
        } );

        // Naming and Description Group
        Group namingDescriptionGroup = new Group( composite, SWT.NONE );
        namingDescriptionGroup.setText( "Naming and Description" );
        namingDescriptionGroup.setLayout( new GridLayout( 3, false ) );
        namingDescriptionGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // OID
        Label oidLabel = new Label( namingDescriptionGroup, SWT.NONE );
        oidLabel.setText( "OID:" );
        oidText = new Text( namingDescriptionGroup, SWT.BORDER );
        oidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        oidText.addModifyListener( new ModifyListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            public void modifyText( ModifyEvent arg0 )
            {
                dialogChanged();
            }
        } );

        // Aliases
        Label aliasesLabel = new Label( namingDescriptionGroup, SWT.NONE );
        aliasesLabel.setText( "Aliases:" );
        aliasesText = new Text( namingDescriptionGroup, SWT.BORDER );
        aliasesText.setEnabled( false );
        aliasesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        aliasesButton = new Button( namingDescriptionGroup, SWT.PUSH );
        aliasesButton.setText( "Edit..." );
        aliasesButton.addSelectionListener( new SelectionAdapter()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected( SelectionEvent arg0 )
            {
                EditAliasesDialog dialog = new EditAliasesDialog( aliases );

                if ( ( dialog.open() == Dialog.OK ) && ( dialog.isDirty() ) )
                {
                    aliases = dialog.getAliases();
                    fillInAliasesLabel();
                    dialogChanged();
                }
            }
        } );

        // Description
        Label descriptionLabel = new Label( namingDescriptionGroup, SWT.NONE );
        descriptionLabel.setText( "Description:" );
        descriptionText = new Text( namingDescriptionGroup, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        descriptionGridData.heightHint = 67;
        descriptionText.setLayoutData( descriptionGridData );
        descriptionText.addModifyListener( new ModifyListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            public void modifyText( ModifyEvent arg0 )
            {
                dialogChanged();
            }
        } );

        initFields();

        setControl( composite );

        displayErrorMessage( null );
        setPageComplete( false );
    }


    /**
     * Initializes the UI fields.
     */
    private void initFields()
    {
        schemaComboViewer.setInput( schemaHandler.getSchemas() );

        if ( selectedSchema != null )
        {
            schemaComboViewer.setSelection( new StructuredSelection( selectedSchema ) );
        }
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        if ( schemaComboViewer.getSelection().isEmpty() )
        {
            displayErrorMessage( "A Schema must be specified." );
            return;
        }
        else if ( oidText.getText().equals( "" ) )
        {
            displayErrorMessage( "An OID must be specified." );
            return;
        }
        else if ( ( !oidText.getText().equals( "" ) ) && ( !OID.isOID( oidText.getText() ) ) )
        {
            displayErrorMessage( "Incorrect OID." );
            return;
        }
        else if ( aliases.length == 0 )
        {
            displayWarningMessage( "The attribute type does not have any name. It is recommanded to add at least one name." );
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
        setMessage( null, DialogPage.WARNING );
        setErrorMessage( message );
        setPageComplete( message == null );
    }


    /**
     * Displays a warning message and set the page status as complete.
     *
     * @param message
     *      the message to display
     */
    private void displayWarningMessage( String message )
    {
        setErrorMessage( null );
        setMessage( message, DialogPage.WARNING );
        setPageComplete( true );
    }


    /**
     * Fills in the Aliases Label.
     */
    private void fillInAliasesLabel()
    {
        StringBuffer sb = new StringBuffer();
        if ( aliases.length != 0 )
        {
            for ( String name : aliases )
            {
                sb.append( name );
                sb.append( ", " );
            }
            sb.deleteCharAt( sb.length() - 1 );
            sb.deleteCharAt( sb.length() - 1 );
        }
        aliasesText.setText( sb.toString() );
    }


    /**
     * Get the name of the schema.
     *
     * @return
     *      the name of the schema
     */
    public String getSchemaValue()
    {
        StructuredSelection selection = ( StructuredSelection ) schemaComboViewer.getSelection();
        if ( !selection.isEmpty() )
        {
            Schema schema = ( Schema ) selection.getFirstElement();

            return schema.getName();
        }
        else
        {
            return null;
        }
    }


    /**
     * Gets the value of the OID.
     *
     * @return
     *      the value of the OID
     */
    public String getOidValue()
    {
        return oidText.getText();
    }


    /**
     * Gets the value of the aliases.
     *
     * @return
     *      the value of the aliases
     */
    public String[] getAliasesValue()
    {
        return aliases;
    }


    /**
     * Gets the value of the description.
     *
     * @return
     *      the value of the description
     */
    public String getDescriptionValue()
    {
        return descriptionText.getText();
    }


    /**
     * Sets the selected schema.
     *
     * @param schema
     *      the selected schema
     */
    public void setSelectedSchema( Schema schema )
    {
        selectedSchema = schema;
    }
}
