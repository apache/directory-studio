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

package org.apache.directory.ldapstudio.schemas.view.editors;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.controller.SchemasViewController;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.IImageKeys;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is the Overview Page of the Object Class Editor
 */
public class ObjectClassFormEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = ObjectClassFormEditor.ID + "overviewPage";

    /** The page title*/
    public static final String TITLE = Messages.getString( "ObjectClassFormEditor.Overview" );

    /** The modified object class */
    private ObjectClass modifiedObjectClass;

    // UI fields
    private Text nameText;
    private String[] aliasesList;
    private Button aliasesButton;
    private Text oidText;
    private Hyperlink schemaLink;
    private Label schemaLabel;
    private Text descriptionText;
    private Hyperlink supLabel;
    private Combo supCombo;
    private Combo classTypeCombo;
    private Button obsoleteCheckbox;
    private Table mandatoryAttributesTable;
    private Button addButtonMandatoryTable;
    private Button removeButtonMandatoryTable;
    private Table optionalAttributesTable;
    private Button addButtonOptionalTable;
    private Button removeButtonOptionalTable;

    // Listeners
    /** The listener for Name Text Widget */
    private ModifyListener nameTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            ArrayList<String> names = new ArrayList<String>();
            names.add( nameText.getText() );
            for ( int i = 0; i < aliasesList.length; i++ )
            {
                names.add( aliasesList[i] );
            }
            modifiedObjectClass.setNames( names.toArray( new String[0] ) );
            setEditorDirty();
        }
    };

    /** The listener for Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ManageAliasesDialog manageDialog = new ManageAliasesDialog( null, aliasesList, ( modifiedObjectClass
                .getOriginatingSchema().type == Schema.SchemaType.coreSchema ) );
            if ( manageDialog.open() != Window.OK )
            {
                return;
            }
            if ( manageDialog.isDirty() )
            {
                aliasesList = manageDialog.getAliasesList();
                ArrayList<String> names = new ArrayList<String>();
                names.add( modifiedObjectClass.getNames()[0] );
                for ( int i = 0; i < aliasesList.length; i++ )
                {
                    names.add( aliasesList[i] );
                }
                modifiedObjectClass.setNames( names.toArray( new String[0] ) );
                setEditorDirty();
            }
        }
    };

    /** The listener for OID Text Widget */
    private ModifyListener oidTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            modifiedObjectClass.setOid( oidText.getText() );
            setEditorDirty();
        }
    };

    /** The listener for the Schema Hyperlink Widget*/
    private HyperlinkAdapter schemaLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            SchemaFormEditorInput input = new SchemaFormEditorInput( modifiedObjectClass.getOriginatingSchema() );
            String editorId = SchemaFormEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( AttributeTypeFormEditorInput.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };

    /** The listener for Description Text Widget */
    private ModifyListener descriptionTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            int caretPosition = descriptionText.getCaretPosition();
            modifiedObjectClass.setDescription( descriptionText.getText() );
            descriptionText.setSelection( caretPosition );
            setEditorDirty();
        }
    };

    /** The listener for Sup Label Widget */
    private HyperlinkAdapter supLabelListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            if ( !supCombo.getItem( supCombo.getSelectionIndex() ).equals(
                Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                SchemaPool pool = SchemaPool.getInstance();
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                ObjectClassFormEditorInput input = new ObjectClassFormEditorInput( pool.getObjectClass( supCombo
                    .getItem( supCombo.getSelectionIndex() ) ) );
                String editorId = ObjectClassFormEditor.ID;
                try
                {
                    page.openEditor( input, editorId );
                }
                catch ( PartInitException exception )
                {
                    Logger.getLogger( ObjectClassFormEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                }
            }
        }
    };

    /** The listener for Sup Combo Widget */
    private ModifyListener supComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( supCombo.getItem( supCombo.getSelectionIndex() ).equals(
                Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                modifiedObjectClass.setSuperiors( new String[0] );
            }
            else
            {
                modifiedObjectClass.setSuperiors( new String[]
                    { supCombo.getItem( supCombo.getSelectionIndex() ) } );
            }
            setEditorDirty();
        }
    };

    /** The listener for Class Type Widget */
    private ModifyListener classTypeListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( classTypeCombo.getSelectionIndex() == 0 )
            {
                modifiedObjectClass.setClassType( ObjectClassTypeEnum.ABSTRACT );
            }
            else if ( classTypeCombo.getSelectionIndex() == 1 )
            {
                modifiedObjectClass.setClassType( ObjectClassTypeEnum.AUXILIARY );
            }
            else if ( classTypeCombo.getSelectionIndex() == 2 )
            {
                modifiedObjectClass.setClassType( ObjectClassTypeEnum.STRUCTURAL );
            }
            setEditorDirty();
        }
    };

    /** The listener for Obsolete Widget */
    private SelectionAdapter obsoleteListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedObjectClass.setObsolete( obsoleteCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for Mandatory Attributes Table Widget */
    private MouseListener mandatoryAttributesTableListener = new MouseListener()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            SchemaPool pool = SchemaPool.getInstance();
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( pool
                .getAttributeType( mandatoryAttributesTable.getSelection()[0].getText() ) );
            String editorId = AttributeTypeFormEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemasViewController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }


        public void mouseDown( MouseEvent e )
        {
        }


        public void mouseUp( MouseEvent e )
        {
            if ( modifiedObjectClass.getOriginatingSchema().type != Schema.SchemaType.coreSchema )
            {
                removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
            }
        }
    };

    /** The listener for Add Button Widget of the Mandatory Attributes section */
    private SelectionAdapter addButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeTypeSelectionDialog selectionDialog = new AttributeTypeSelectionDialog( null );
            if ( selectionDialog.open() != Window.OK )
            {
                return;
            }
            if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(), optionalAttributesTable ) )
            {
                // The selected attribute type is already in the Optionnal Attributes Table
                MessageDialog
                    .openError(
                        null,
                        Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_Optionnal_Attributes_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(),
                    mandatoryAttributesTable ) )
                {
                    // The selected attribute type is already in the Mandatory Attributes Table
                    MessageDialog
                        .openError(
                            null,
                            Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_this_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    // The selected attribute is not in any table, so it can be added
                    ArrayList<String> mustList = new ArrayList<String>();
                    String[] mustATs = modifiedObjectClass.getMust();
                    for ( int i = 0; i < mustATs.length; i++ )
                    {
                        mustList.add( mustATs[i] );
                    }
                    mustList.add( selectionDialog.getSelectedAttributeType() );
                    modifiedObjectClass.setMust( mustList.toArray( new String[0] ) );
                    fillInMandatoryAttributesTable();
                    setEditorDirty();
                }
            }
        }
    };

    /** The listener for Remove Button Widget of the Mandatory Attributes section */
    private SelectionAdapter removeButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            String itemToRemove = mandatoryAttributesTable.getItem( mandatoryAttributesTable.getSelectionIndex() )
                .getText();

            ArrayList<String> mustList = new ArrayList<String>();
            String[] mustATs = modifiedObjectClass.getMust();
            for ( int i = 0; i < mustATs.length; i++ )
            {
                mustList.add( mustATs[i] );
            }
            mustList.remove( itemToRemove );
            modifiedObjectClass.setMust( mustList.toArray( new String[0] ) );
            removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
            fillInMandatoryAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Optional Attributes Table Widget */
    private MouseListener optionalAttributesTableListener = new MouseListener()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            SchemaPool pool = SchemaPool.getInstance();
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( pool
                .getAttributeType( optionalAttributesTable.getSelection()[0].getText() ) );
            String editorId = AttributeTypeFormEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( SchemasViewController.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }


        public void mouseDown( MouseEvent e )
        {
        }


        public void mouseUp( MouseEvent e )
        {
            if ( modifiedObjectClass.getOriginatingSchema().type != Schema.SchemaType.coreSchema )
            {
                removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
            }
        }
    };

    /** The listener for Add Button Widget of the Optional Attributes section */
    private SelectionAdapter addButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeTypeSelectionDialog selectionDialog = new AttributeTypeSelectionDialog( null );
            if ( selectionDialog.open() != Window.OK )
            {
                return;
            }
            if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(), mandatoryAttributesTable ) )
            {
                // The selected attribute type is already in the Mandatory Attributes Table
                MessageDialog
                    .openError(
                        null,
                        Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_Mandatory_Attributes_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                if ( isAttributeTypeAlreadySpecified( selectionDialog.getSelectedAttributeType(),
                    optionalAttributesTable ) )
                {
                    // The selected attribute type is already in the Optionnal Attributes Table
                    MessageDialog
                        .openError(
                            null,
                            Messages.getString( "ObjectClassFormEditorOverviewPage.Invalid_Selection" ), Messages.getString( "ObjectClassFormEditorOverviewPage.The_selected_attribute_type_is_already_in_the_this_section" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    // The selected attribute is not in any table, so it can be added
                    ArrayList<String> mayList = new ArrayList<String>();
                    String[] mayATs = modifiedObjectClass.getMay();
                    for ( int i = 0; i < mayATs.length; i++ )
                    {
                        mayList.add( mayATs[i] );
                    }
                    mayList.add( selectionDialog.getSelectedAttributeType() );
                    modifiedObjectClass.setMay( mayList.toArray( new String[0] ) );
                    fillInOptionalAttributesTable();
                    setEditorDirty();
                }

            }
        }
    };

    /** The listener for Remove Button Widget of the Optional Attributes section */
    private SelectionAdapter removeButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            String itemToRemove = optionalAttributesTable.getItem( optionalAttributesTable.getSelectionIndex() )
                .getText();

            ArrayList<String> mayList = new ArrayList<String>();
            String[] mayATs = modifiedObjectClass.getMay();
            for ( int i = 0; i < mayATs.length; i++ )
            {
                mayList.add( mayATs[i] );
            }
            mayList.remove( itemToRemove );
            modifiedObjectClass.setMay( mayList.toArray( new String[0] ) );
            removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
            fillInOptionalAttributesTable();
            setEditorDirty();
        }
    };


    /**
     * Default constructor
     * @param editor
     *      the associated editor
     */
    public ObjectClassFormEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the modified object class
        modifiedObjectClass = ( ( ObjectClassFormEditor ) getEditor() ).getModifiedObjectClass();

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout( 2, true );
        form.getBody().setLayout( layout );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        // Mandatory Attributes Section
        createMandatoryAttributesSection( form.getBody(), toolkit );

        // Optionnal Attributes Section
        createOptionalAttributesSection( form.getBody(), toolkit );

        // Enabling or disabling the fields
        setFieldsEditableState();

        // Filling the UI with values from the object class
        fillInUiFields();

        // Listeners initialization
        addListeners();
    }


    /**
     * Creates the General Information Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createGeneralInformationSection( Composite parent, FormToolkit toolkit )
    {
        // General Information Section
        Section section_general_information = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section_general_information.setDescription( Messages
            .getString( "ObjectClassFormEditorOverviewPage.General_Information_Section_Description" ) ); //$NON-NLS-1$
        section_general_information.setText( Messages
            .getString( "ObjectClassFormEditorOverviewPage.General_Information_Section_Text" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        GridLayout layout_general_information = new GridLayout();
        layout_general_information.numColumns = 2;
        client_general_information.setLayout( layout_general_information );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );
        section_general_information.setLayoutData( new GridData( GridData.FILL, GridData.FILL, true, true, 2, 1 ) );

        // NAME Field
        toolkit
            .createLabel( client_general_information, Messages.getString( "ObjectClassFormEditorOverviewPage.Name" ) ); //$NON-NLS-1$
        nameText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        nameText.setLayoutData( new GridData( GridData.FILL, SWT.NONE, true, false ) );

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        aliasesButton = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Manage_Aliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassFormEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, "Schema:", SWT.WRAP );
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        descriptionGridData.heightHint = 37;
        descriptionText.setLayoutData( descriptionGridData );

        // SUP Combo
        supLabel = toolkit.createHyperlink( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Superior_class" ), SWT.WRAP ); //$NON-NLS-1$
        supCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        supCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initSupCombo();

        // CLASS TYPE Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Class_type" ) ); //$NON-NLS-1$
        classTypeCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        classTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initClassTypeCombo();

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Mandatory Attributes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createMandatoryAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // MANDATORY ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassFormEditorOverviewPage.Mandatory_Attribute_Section_Text" ) ); //$NON-NLS-1$
        section.setDescription( Messages
            .getString( "ObjectClassFormEditorOverviewPage.Mandatory_Attribute_Section_Description" ) ); //$NON-NLS-1$
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( GridData.FILL, GridData.FILL, true, true );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        mandatoryAttributesTable = toolkit.createTable( client, SWT.NULL );
        gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.verticalSpan = 2;
        gd.heightHint = 100;
        mandatoryAttributesTable.setLayoutData( gd );

        addButtonMandatoryTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Add..." ), SWT.PUSH ); //$NON-NLS-1$
        removeButtonMandatoryTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        addButtonMandatoryTable.setLayoutData( gd );
        removeButtonMandatoryTable.setLayoutData( gd );

        // By default, no element is selected
        removeButtonMandatoryTable.setEnabled( false );
    }


    /**
     * Creates the Optional Attributes Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createOptionalAttributesSection( Composite parent, FormToolkit toolkit )
    {
        // OPTIONAL ATTRIBUTES Section
        Section section = toolkit.createSection( parent, Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText( Messages.getString( "ObjectClassFormEditorOverviewPage.Optionnal_Attributes_Section_Text" ) ); //$NON-NLS-1$
        section.setDescription( Messages
            .getString( "ObjectClassFormEditorOverviewPage.Optionnal_Attributes_Section_Description" ) ); //$NON-NLS-1$
        section.setExpanded( true );
        Composite client = toolkit.createComposite( section );
        section.setClient( client );
        GridData gd = new GridData( GridData.FILL, GridData.FILL, true, true );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        optionalAttributesTable = toolkit.createTable( client, SWT.SINGLE | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        gd.verticalSpan = 2;
        gd.heightHint = 100;
        optionalAttributesTable.setLayoutData( gd );

        addButtonOptionalTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Add..." ), SWT.PUSH ); //$NON-NLS-1$
        removeButtonOptionalTable = toolkit.createButton( client, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Remove" ), SWT.PUSH ); //$NON-NLS-1$
        gd = new GridData( GridData.VERTICAL_ALIGN_BEGINNING );
        addButtonOptionalTable.setLayoutData( gd );
        removeButtonOptionalTable.setLayoutData( gd );

        // By default, no element is selected
        removeButtonOptionalTable.setEnabled( false );
    }


    /**
     * Verifies if an attribute type is already present in a the given table
     *
     * @param name
     *      the name of the attribute type to search
     * @param table
     *      the table to search in
     * @return
     *      true if the attribute type is already present in the given table
     */
    private boolean isAttributeTypeAlreadySpecified( String name, Table table )
    {
        for ( int i = 0; i < table.getItemCount(); i++ )
        {
            if ( table.getItem( i ).getText().equals( name ) )
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Initializes the UI fields from the input.
     */
    private void fillInUiFields()
    {
        // NAME Field
        if ( modifiedObjectClass.getNames()[0] != null )
        {
            nameText.setText( modifiedObjectClass.getNames()[0] );
        }

        // ALIASES
        String[] names = modifiedObjectClass.getNames();
        ArrayList<String> aliases = new ArrayList<String>();
        for ( int i = 1; i < names.length; i++ )
        {
            String name = names[i];
            aliases.add( name );
        }
        aliasesList = aliases.toArray( new String[0] );

        // OID Field
        if ( modifiedObjectClass.getOid() != null )
        {
            oidText.setText( modifiedObjectClass.getOid() );
        }

        // SCHEMAS Field
        if ( modifiedObjectClass.getOriginatingSchema() != null )
        {
            this.schemaLabel.setText( modifiedObjectClass.getOriginatingSchema().getName() );
        }

        // DESCRIPTION Field
        if ( modifiedObjectClass.getDescription() != null )
        {
            descriptionText.setText( modifiedObjectClass.getDescription() );
        }

        // SUP Combo
        if ( modifiedObjectClass.getSuperiors().length == 0 )
        {
            fillSupCombo( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) );
        }
        else
        {
            fillSupCombo( modifiedObjectClass.getSuperiors()[0] );
        }

        // CLASS TYPE Combo
        fillInClassType();

        // OBSOLETE Checkbox
        obsoleteCheckbox.setSelection( modifiedObjectClass.isObsolete() );

        // MANDATORY ATTRIBUTES Table
        fillInMandatoryAttributesTable();

        // OPTIONNAL ATTRIBUTES Table
        fillInOptionalAttributesTable();
    }


    /**
     * Initializes the Superior Combo box.
     */
    private void initSupCombo()
    {
        SchemaPool pool = SchemaPool.getInstance();
        ArrayList<ObjectClass> ocList = new ArrayList<ObjectClass>( pool.getObjectClassesAsHashTableByName().values() );

        //remove duplicate entries
        HashSet<ObjectClass> set = new HashSet<ObjectClass>( ocList );
        ocList = new ArrayList<ObjectClass>( set );

        // Sorting the list
        Collections.sort( ocList, new Comparator<ObjectClass>()
        {
            public int compare( ObjectClass arg0, ObjectClass arg1 )
            {
                String oneName = arg0.getNames()[0];
                String twoName = arg1.getNames()[0];
                return oneName.compareTo( twoName );
            }
        } );

        // Creating the UI
        supCombo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        supCombo.select( 0 );
        int counter = 1;
        for ( ObjectClass oc : ocList )
        {
            supCombo.add( oc.getNames()[0], counter );
            counter++;
        }
    }


    /**
     * Initializes the Class Type Combo
     */
    private void initClassTypeCombo()
    {
        classTypeCombo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.Abstract" ), 0 ); //$NON-NLS-1$
        classTypeCombo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.Auxiliary" ), 1 ); //$NON-NLS-1$
        classTypeCombo.add( Messages.getString( "ObjectClassFormEditorOverviewPage.Structural" ), 2 ); //$NON-NLS-1$
    }


    /**
     * Fills in the Class Type Combo
     */
    private void fillInClassType()
    {
        if ( modifiedObjectClass.getClassType() == ObjectClassTypeEnum.ABSTRACT )
        {
            classTypeCombo.select( 0 );
        }
        else if ( modifiedObjectClass.getClassType() == ObjectClassTypeEnum.AUXILIARY )
        {
            classTypeCombo.select( 1 );
        }
        else if ( modifiedObjectClass.getClassType() == ObjectClassTypeEnum.STRUCTURAL )
        {
            classTypeCombo.select( 2 );
        }
    }


    /**
     * Initializes the Mandatory Attributes Table
     */
    private void fillInMandatoryAttributesTable()
    {
        int selectionIndex = mandatoryAttributesTable.getSelectionIndex();
        String selectAttribute = null;
        if ( selectionIndex != -1 )
        {
            selectAttribute = mandatoryAttributesTable.getItem( selectionIndex ).getText();
        }
        mandatoryAttributesTable.clearAll();
        mandatoryAttributesTable.setItemCount( 0 );
        String[] mustArray = modifiedObjectClass.getMust();
        Arrays.sort( mustArray );
        for ( int i = 0; i < mustArray.length; i++ )
        {
            TableItem item = new TableItem( mandatoryAttributesTable, SWT.NONE, i );
            item.setImage( AbstractUIPlugin
                .imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.ATTRIBUTE_TYPE ).createImage() );
            item.setText( mustArray[i] );
            if ( ( selectionIndex != -1 ) && ( mustArray[i].equals( selectAttribute ) ) )
            {
                mandatoryAttributesTable.select( i );
            }
        }
    }


    /**
     * Initializes the Optional Attributes Table
     */
    private void fillInOptionalAttributesTable()
    {
        int selectionIndex = optionalAttributesTable.getSelectionIndex();
        String selectAttribute = null;
        if ( selectionIndex != -1 )
        {
            selectAttribute = optionalAttributesTable.getItem( selectionIndex ).getText();
        }
        optionalAttributesTable.clearAll();
        optionalAttributesTable.setItemCount( 0 );
        String[] mayArray = modifiedObjectClass.getMay();
        Arrays.sort( mayArray );
        for ( int i = 0; i < mayArray.length; i++ )
        {
            TableItem item = new TableItem( optionalAttributesTable, SWT.NONE, i );
            item.setImage( AbstractUIPlugin
                .imageDescriptorFromPlugin( Activator.PLUGIN_ID, IImageKeys.ATTRIBUTE_TYPE ).createImage() );
            item.setText( mayArray[i] );
            if ( ( selectionIndex != -1 ) && ( mayArray[i].equals( selectAttribute ) ) )
            {
                optionalAttributesTable.select( i );
            }
        }
    }


    /**
     * Enalbes/Disables the UI fields
     */
    private void setFieldsEditableState()
    {
        if ( modifiedObjectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            nameText.setEditable( false );
            oidText.setEditable( false );
            descriptionText.setEditable( false );
            supCombo.setEnabled( false );
            classTypeCombo.setEnabled( false );
            obsoleteCheckbox.setEnabled( false );
            addButtonMandatoryTable.setEnabled( false );
            removeButtonMandatoryTable.setEnabled( false );
            addButtonOptionalTable.setEnabled( false );
            removeButtonOptionalTable.setEnabled( false );
        }
        else
        {
            removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelectionIndex() != -1 );
            removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelectionIndex() != -1 );
        }
    }


    /**
     * Adds listeners to UI fields
     */
    private void addListeners()
    {
        if ( modifiedObjectClass.getOriginatingSchema().type == Schema.SchemaType.userSchema )
        {
            nameText.addModifyListener( nameTextListener );
            oidText.addModifyListener( oidTextListener );
            descriptionText.addModifyListener( descriptionTextListener );
            supCombo.addModifyListener( supComboListener );
            classTypeCombo.addModifyListener( classTypeListener );
            obsoleteCheckbox.addSelectionListener( obsoleteListener );
            addButtonMandatoryTable.addSelectionListener( addButtonMandatoryTableListener );
            removeButtonMandatoryTable.addSelectionListener( removeButtonMandatoryTableListener );
            addButtonOptionalTable.addSelectionListener( addButtonOptionalTableListener );
            removeButtonOptionalTable.addSelectionListener( removeButtonOptionalTableListener );
        }

        // The user can always access to the Manage Aliases Window, but if the object class is in a core-schema file editing will be disabled
        aliasesButton.addSelectionListener( aliasesButtonListener );

        schemaLink.addHyperlinkListener( schemaLinkListener );
        supLabel.addHyperlinkListener( supLabelListener );

        // This listener needs to be outside of the 'if' so that attribute type editor can be opened from any object class (in a core or a user schema)
        mandatoryAttributesTable.addMouseListener( mandatoryAttributesTableListener );

        // This listener needs to be outside of the 'if' so that attribute type editor can be opened from any object class (in a core or a user schema)
        optionalAttributesTable.addMouseListener( optionalAttributesTableListener );
    }


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        nameText.removeModifyListener( nameTextListener );
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        oidText.removeModifyListener( oidTextListener );
        schemaLink.removeHyperlinkListener( schemaLinkListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        supLabel.removeHyperlinkListener( supLabelListener );
        supCombo.removeModifyListener( supComboListener );
        classTypeCombo.removeModifyListener( classTypeListener );
        obsoleteCheckbox.removeSelectionListener( obsoleteListener );
        mandatoryAttributesTable.removeMouseListener( mandatoryAttributesTableListener );
        addButtonMandatoryTable.removeSelectionListener( addButtonMandatoryTableListener );
        removeButtonMandatoryTable.removeSelectionListener( removeButtonMandatoryTableListener );
        optionalAttributesTable.removeMouseListener( optionalAttributesTableListener );
        addButtonOptionalTable.removeSelectionListener( addButtonOptionalTableListener );
        removeButtonOptionalTable.removeSelectionListener( removeButtonOptionalTableListener );
    }


    /**
     * Sets the editor as dirty
     */
    private void setEditorDirty()
    {
        ( ( ObjectClassFormEditor ) getEditor() ).setDirty( true );
    }


    /**
     * Fills the the Sup Combo with the correct value
     *
     * @param name
     *      the name to select
     */
    private void fillSupCombo( String name )
    {
        for ( int i = 0; i < supCombo.getItemCount(); i++ )
        {
            if ( name.equals( supCombo.getItem( i ) ) )
            {
                supCombo.select( i );
                return;
            }
        }
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        removeListeners();
        fillInUiFields();
        addListeners();
        setFieldsEditableState();
    }
}
