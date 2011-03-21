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


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.directory.shared.asn1.util.Oid;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.PluginUtils;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.alias.Alias;
import org.apache.directory.studio.schemaeditor.model.alias.AliasWithPartError;
import org.apache.directory.studio.schemaeditor.model.alias.AliasWithStartError;
import org.apache.directory.studio.schemaeditor.model.alias.AliasesStringParser;
import org.apache.directory.studio.schemaeditor.view.dialogs.EditAliasesDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class represents the General WizardPage of the NewObjectClassWizard.
 * <p>
 * It is used to let the user enter general information about the
 * attribute type he wants to create (schema, OID, aliases an description).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class NewObjectClassGeneralPageWizardPage extends AbstractWizardPage
{
    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The aliases */
    private List<Alias> aliases;

    /** The selected schema */
    private Schema selectedSchema;

    // UI fields
    private ComboViewer schemaComboViewer;
    private Combo oidCombo;
    private Text aliasesText;
    private Button aliasesButton;
    private Text descriptionText;


    /**
     * Creates a new instance of NewObjectClassGeneralPageWizardPage.
     */
    protected NewObjectClassGeneralPageWizardPage()
    {
        super( "NewObjectClassGeneralPageWizardPage" ); //$NON-NLS-1$
        setTitle( Messages.getString( "NewObjectClassGeneralPageWizardPage.ObjectClass" ) ); //$NON-NLS-1$
        setDescription( Messages.getString( "NewObjectClassGeneralPageWizardPage.CreateObjectClass" ) ); //$NON-NLS-1$
        setImageDescriptor( Activator.getDefault().getImageDescriptor( PluginConstants.IMG_OBJECT_CLASS_NEW_WIZARD ) );

        schemaHandler = Activator.getDefault().getSchemaHandler();
        aliases = new ArrayList<Alias>();
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
        schemaGroup.setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.Schema" ) ); //$NON-NLS-1$
        schemaGroup.setLayout( new GridLayout( 2, false ) );
        schemaGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Schema
        Label schemaLabel = new Label( schemaGroup, SWT.NONE );
        schemaLabel.setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.SchemaColon" ) ); //$NON-NLS-1$
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
                    return ( ( Schema ) element ).getSchemaName();
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
        namingDescriptionGroup
            .setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.NamingAndDescription" ) ); //$NON-NLS-1$
        namingDescriptionGroup.setLayout( new GridLayout( 3, false ) );
        namingDescriptionGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // OID
        Label oidLabel = new Label( namingDescriptionGroup, SWT.NONE );
        oidLabel.setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.OID" ) ); //$NON-NLS-1$
        oidCombo = new Combo( namingDescriptionGroup, SWT.DROP_DOWN | SWT.BORDER );
        oidCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        oidCombo.addModifyListener( new ModifyListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            public void modifyText( ModifyEvent arg0 )
            {
                dialogChanged();
            }
        } );
        oidCombo.addVerifyListener( new VerifyListener()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
             */
            public void verifyText( VerifyEvent e )
            {
                if ( !e.text.matches( "([0-9]*\\.?)*" ) ) //$NON-NLS-1$
                {
                    e.doit = false;
                }
            }
        } );
        oidCombo.setItems( PluginUtils.loadDialogSettingsHistory( PluginConstants.DIALOG_SETTINGS_OID_HISTORY ) );

        // Aliases
        Label aliasesLabel = new Label( namingDescriptionGroup, SWT.NONE );
        aliasesLabel.setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.Aliases" ) ); //$NON-NLS-1$
        aliasesText = new Text( namingDescriptionGroup, SWT.BORDER );
        aliasesText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        aliasesText.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                AliasesStringParser parser = new AliasesStringParser();
                parser.parse( aliasesText.getText() );
                List<Alias> parsedAliases = parser.getAliases();
                aliases.clear();
                for ( Alias parsedAlias : parsedAliases )
                {
                    aliases.add( parsedAlias );
                }

                dialogChanged();
            }
        } );
        aliasesButton = new Button( namingDescriptionGroup, SWT.PUSH );
        aliasesButton.setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.Edit" ) ); //$NON-NLS-1$
        aliasesButton.addSelectionListener( new SelectionAdapter()
        {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected( SelectionEvent arg0 )
            {
                EditAliasesDialog dialog = new EditAliasesDialog( getAliasesValue() );

                if ( ( dialog.open() == Dialog.OK ) && ( dialog.isDirty() ) )
                {
                    String[] newAliases = dialog.getAliases();

                    StringBuffer sb = new StringBuffer();
                    for ( String newAlias : newAliases )
                    {
                        sb.append( newAlias );
                        sb.append( ", " ); //$NON-NLS-1$
                    }
                    sb.deleteCharAt( sb.length() - 1 );
                    sb.deleteCharAt( sb.length() - 1 );

                    AliasesStringParser parser = new AliasesStringParser();
                    parser.parse( sb.toString() );
                    List<Alias> parsedAliases = parser.getAliases();
                    aliases.clear();
                    for ( Alias parsedAlias : parsedAliases )
                    {
                        aliases.add( parsedAlias );
                    }

                    fillInAliasesLabel();
                    dialogChanged();
                }
            }
        } );

        // Description
        Label descriptionLabel = new Label( namingDescriptionGroup, SWT.NONE );
        descriptionLabel.setText( Messages.getString( "NewObjectClassGeneralPageWizardPage.Description" ) ); //$NON-NLS-1$
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
    }


    /**
     * Initializes the UI fields.
     */
    private void initFields()
    {
        if ( schemaHandler == null )
        {
            schemaComboViewer.getCombo().setEnabled( false );
            oidCombo.setEnabled( false );
            aliasesText.setEnabled( false );
            aliasesButton.setEnabled( false );
            descriptionText.setEnabled( false );

            displayErrorMessage( Messages.getString( "NewObjectClassGeneralPageWizardPage.ErrorNoSchemaProjectOpen" ) ); //$NON-NLS-1$
        }
        else
        {
            // Filling the Schemas table
            List<Schema> schemas = new ArrayList<Schema>();
            schemas.addAll( schemaHandler.getSchemas() );

            Collections.sort( schemas, new Comparator<Schema>()
            {
                public int compare( Schema o1, Schema o2 )
                {
                    return o1.getSchemaName().compareToIgnoreCase( o2.getSchemaName() );
                }
            } );

            schemaComboViewer.setInput( schemas );

            if ( selectedSchema != null )
            {
                schemaComboViewer.setSelection( new StructuredSelection( selectedSchema ) );
            }

            displayErrorMessage( null );
        }

        setPageComplete( false );
    }


    /**
     * This method is called when the user modifies something in the UI.
     */
    private void dialogChanged()
    {
        if ( schemaComboViewer.getSelection().isEmpty() )
        {
            displayErrorMessage( Messages.getString( "NewObjectClassGeneralPageWizardPage.ErrorNoSchemaSpecified" ) ); //$NON-NLS-1$
            return;
        }
        if ( oidCombo.getText().equals( "" ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "NewObjectClassGeneralPageWizardPage.ErrorNoOIDSpecified" ) ); //$NON-NLS-1$
            return;
        }
        if ( ( !oidCombo.getText().equals( "" ) ) && ( !Oid.isOid( oidCombo.getText() ) ) ) //$NON-NLS-1$
        {
            displayErrorMessage( Messages.getString( "NewObjectClassGeneralPageWizardPage.ErrorIncorrectOID" ) ); //$NON-NLS-1$
            return;
        }
        if ( ( !oidCombo.getText().equals( "" ) ) && ( Oid.isOid( oidCombo.getText() ) ) //$NON-NLS-1$
            && ( schemaHandler.isAliasOrOidAlreadyTaken( oidCombo.getText() ) ) )
        {
            displayErrorMessage( Messages.getString( "NewObjectClassGeneralPageWizardPage.ErrorObjectOIDExists" ) ); //$NON-NLS-1$
            return;
        }
        if ( aliases.size() == 0 )
        {
            displayWarningMessage( Messages.getString( "NewObjectClassGeneralPageWizardPage.ErrorObjectClassNoName" ) ); //$NON-NLS-1$
            return;
        }
        else
        {
            for ( Alias alias : aliases )
            {
                if ( alias instanceof AliasWithStartError )
                {
                    displayErrorMessage( NLS
                        .bind(
                            Messages.getString( "NewObjectClassGeneralPageWizardPage.AliasStartInvalid" ), new Object[] { alias, ( ( AliasWithStartError ) alias ).getErrorChar() } ) ); //$NON-NLS-1$
                    return;
                }
                else if ( alias instanceof AliasWithPartError )
                {
                    displayErrorMessage( NLS
                        .bind(
                            Messages.getString( "NewObjectClassGeneralPageWizardPage.AliasPartInvalid" ), new Object[] { alias, ( ( AliasWithPartError ) alias ).getErrorChar() } ) ); //$NON-NLS-1$
                    return;
                }
            }
        }

        displayErrorMessage( null );
    }


    /**
     * Fills in the Aliases Label.
     */
    private void fillInAliasesLabel()
    {
        StringBuffer sb = new StringBuffer();

        for ( Alias alias : aliases )
        {
            sb.append( alias );
            sb.append( ", " ); //$NON-NLS-1$
        }

        sb.deleteCharAt( sb.length() - 1 );
        sb.deleteCharAt( sb.length() - 1 );

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

            return schema.getSchemaName();
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
        return oidCombo.getText();
    }


    /**
     * Gets the value of the aliases.
     *
     * @return
     *      the value of the aliases
     */
    public List<String> getAliasesValue()
    {
        List<String> aliasesValue = new ArrayList<String>();

        for ( Alias alias : aliases )
        {
            aliasesValue.add( alias.toString() );
        }

        return aliasesValue;
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
