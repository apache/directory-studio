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

package org.apache.directory.ldapstudio.schemas.view.editors.attributeType;


import java.util.ArrayList;

import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent;
import org.apache.directory.ldapstudio.schemas.model.MatchingRule;
import org.apache.directory.ldapstudio.schemas.model.MatchingRules;
import org.apache.directory.ldapstudio.schemas.model.PoolListener;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.model.Syntax;
import org.apache.directory.ldapstudio.schemas.model.Syntaxes;
import org.apache.directory.ldapstudio.schemas.view.ViewUtils;
import org.apache.directory.ldapstudio.schemas.view.dialogs.ManageAliasesDialog;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditorInput;
import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Label;
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


/**
 * This class is the Overview Page of the Attribute Type Editor
 */
public class AttributeTypeEditorOverviewPage extends FormPage implements PoolListener
{
    /** The page ID*/
    public static final String ID = AttributeTypeEditor.ID + "overviewPage";

    /** The page title */
    public static String TITLE = Messages.getString( "AttributeTypeFormEditor.Overview" );

    /** The original object class */
    private AttributeType originalAttributeType;

    /** The modified object class */
    private AttributeType modifiedAttributeType;

    // UI Fields
    private Label aliasesLabel;
    private Button aliasesButton;
    private Text oidText;
    private Hyperlink schemaLink;
    private Label schemaLabel;
    private Text descriptionText;
    private Hyperlink supLabel;
    private Combo supCombo;
    private Combo usageCombo;
    private Combo syntaxCombo;
    private Text syntaxLengthText;
    private Button obsoleteCheckbox;
    private Button singleValueCheckbox;
    private Button collectiveCheckbox;
    private Button noUserModificationCheckbox;
    private Combo equalityCombo;
    private Combo orderingCombo;
    private Combo substringCombo;

    // Listeners
    /** The listener for the Manage Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ManageAliasesDialog manageDialog = new ManageAliasesDialog( null, modifiedAttributeType.getNames(),
                ( modifiedAttributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema ) );
            if ( manageDialog.open() != Window.OK )
            {
                return;
            }
            if ( manageDialog.isDirty() )
            {
                modifiedAttributeType.setNames( manageDialog.getAliases() );
                if ( ( modifiedAttributeType.getNames() != null ) && ( modifiedAttributeType.getNames().length != 0 ) )
                {
                    aliasesLabel.setText( ViewUtils.concateAliases( modifiedAttributeType.getNames() ) );
                }
                else
                {
                    aliasesLabel.setText( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) );
                }
                setEditorDirty();
            }
        }
    };

    /** The Modify listener for the OID Text Widget */
    private ModifyListener oidTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            oidText.setForeground( ViewUtils.COLOR_BLACK );
            oidText.setToolTipText( "" );

            String oid = oidText.getText();

            if ( OID.isOID( oid ) )
            {
                if ( ( originalAttributeType.getOid().equals( oid ) )
                    || !( SchemaPool.getInstance().containsSchemaElement( oid ) ) )
                {
                    modifiedAttributeType.setOid( oid );
                    setEditorDirty();
                }
                else
                {
                    oidText.setForeground( ViewUtils.COLOR_RED );
                    oidText.setToolTipText( "An element with same oid already exists." );
                }
            }
            else
            {
                oidText.setForeground( ViewUtils.COLOR_RED );
                oidText.setToolTipText( "Malformed OID." );
            }
        }
    };

    /** The Verify listener for the OID Text Widget */
    private VerifyListener oidTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "([0-9]*\\.?)*" ) )
            {
                e.doit = false;
            }
        }
    };

    /** The listener for the Schema Hyperlink Widget*/
    private HyperlinkAdapter schemaLinkListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            SchemaEditorInput input = new SchemaEditorInput( modifiedAttributeType.getOriginatingSchema() );
            String editorId = SchemaEditor.ID;
            try
            {
                page.openEditor( input, editorId );
            }
            catch ( PartInitException exception )
            {
                Logger.getLogger( AttributeTypeEditorInput.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
            }
        }
    };

    /** The listener for the Description Text Widget */
    private ModifyListener descriptionTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            int caretPosition = descriptionText.getCaretPosition();
            modifiedAttributeType.setDescription( descriptionText.getText() );
            descriptionText.setSelection( caretPosition );
            setEditorDirty();
        }
    };

    /** The listener for the Sup Label Widget*/
    private HyperlinkAdapter supLabelListener = new HyperlinkAdapter()
    {
        public void linkActivated( HyperlinkEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) supComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof AttributeType )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                AttributeTypeEditorInput input = new AttributeTypeEditorInput( ( AttributeType ) selectedItem );
                try
                {
                    page.openEditor( input, AttributeTypeEditor.ID );
                }
                catch ( PartInitException exception )
                {
                    Logger.getLogger( AttributeTypeEditorInput.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                }
            }
        }
    };

    /** The listener for the Sup Combo Widget */
    private ModifyListener supComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) supComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof AttributeType )
            {
                modifiedAttributeType.setSuperior( ( ( AttributeType ) selectedItem ).getNames()[0] );
            }
            else if ( selectedItem instanceof NonExistingAttributeType )
            {
                NonExistingAttributeType neat = ( NonExistingAttributeType ) selectedItem;

                if ( NonExistingAttributeType.NONE.equals( neat.getName() ) )
                {
                    modifiedAttributeType.setSuperior( null );
                }
                else
                {
                    modifiedAttributeType.setSuperior( ( ( NonExistingAttributeType ) selectedItem ).getName() );
                }
            }
            setEditorDirty();
        }
    };

    /** The listener for the Usage Combo Widget */
    private ModifyListener usageComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( usageCombo.getSelectionIndex() == 0 )
            {
                modifiedAttributeType.setUsage( UsageEnum.DIRECTORY_OPERATION );
            }
            else if ( usageCombo.getSelectionIndex() == 1 )
            {
                modifiedAttributeType.setUsage( UsageEnum.DISTRIBUTED_OPERATION );
            }
            else if ( usageCombo.getSelectionIndex() == 2 )
            {
                modifiedAttributeType.setUsage( UsageEnum.DSA_OPERATION );
            }
            else if ( usageCombo.getSelectionIndex() == 3 )
            {
                modifiedAttributeType.setUsage( UsageEnum.USER_APPLICATIONS );
            }
            setEditorDirty();
        }
    };

    /** The listener for the Syntax Combo Widget */
    private ModifyListener syntaxComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( syntaxCombo.getItem( syntaxCombo.getSelectionIndex() ).equals(
                Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                modifiedAttributeType.setSyntax( "" ); //$NON-NLS-1$
            }
            else
            {
                modifiedAttributeType.setSyntax( Syntaxes.getSyntax(
                    syntaxCombo.getItem( syntaxCombo.getSelectionIndex() ) ).getOid() );
            }
            setEditorDirty();
        }
    };

    /** The Modify listener for the Syntax Length Text Widget */
    private ModifyListener syntaxLengthTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( syntaxLengthText.getText().length() == 0 )
            {
                modifiedAttributeType.setLength( -1 );
            }
            else
            {
                modifiedAttributeType.setLength( Integer.parseInt( syntaxLengthText.getText() ) );
            }
            setEditorDirty();
        }
    };

    /** The Verify listener for the Syntax Length Text Widget */
    private VerifyListener syntaxLengthTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-9]*" ) )
            {
                e.doit = false;
            }
        }
    };

    /** The listener for the Obsolete Checbox Widget */
    private SelectionAdapter obsoleteCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedAttributeType.setObsolete( obsoleteCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for the Single-Value Checkbox Widget */
    private SelectionAdapter singleValueCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedAttributeType.setSingleValue( singleValueCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for the Collective Checkbox Widget */
    private SelectionAdapter collectiveCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedAttributeType.setCollective( collectiveCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for the No-User-Modification Widget */
    private SelectionAdapter noUserModificationCheckboxListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            modifiedAttributeType.setNoUserModification( noUserModificationCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for the Equality Combo Widget */
    private ModifyListener equalityComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( equalityCombo.getItem( equalityCombo.getSelectionIndex() ).equals(
                Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                modifiedAttributeType.setEquality( "" ); //$NON-NLS-1$
            }
            else
            {
                modifiedAttributeType.setEquality( equalityCombo.getItem( equalityCombo.getSelectionIndex() ) );
            }
            setEditorDirty();
        }
    };

    /** The listener for the Ordering Combo Widget */
    private ModifyListener orderingComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( orderingCombo.getItem( orderingCombo.getSelectionIndex() ).equals(
                Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                modifiedAttributeType.setOrdering( "" ); //$NON-NLS-1$
            }
            else
            {
                modifiedAttributeType.setOrdering( orderingCombo.getItem( orderingCombo.getSelectionIndex() ) );
            }
            setEditorDirty();
        }
    };

    /** The listener for the Substring Combo Widget */
    private ModifyListener substringComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( substringCombo.getItem( substringCombo.getSelectionIndex() ).equals(
                Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                modifiedAttributeType.setSubstr( "" ); //$NON-NLS-1$
            }
            else
            {
                modifiedAttributeType.setSubstr( substringCombo.getItem( substringCombo.getSelectionIndex() ) );
            }
            setEditorDirty();
        }
    };

    private ComboViewer supComboViewer;


    /**
     * Default constructor.
     * 
     * @param editor
     *      the associated editor
     */
    public AttributeTypeEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
        SchemaPool.getInstance().addListener( this );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the original and modified attribute types
        modifiedAttributeType = ( ( AttributeTypeEditor ) getEditor() ).getModifiedAttributeType();
        originalAttributeType = ( ( AttributeTypeEditor ) getEditor() ).getOriginalAttributeType();

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        form.getBody().setLayout( layout );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        // Matching Rules Section
        createMatchingRulesSection( form.getBody(), toolkit );

        // Enabling or disabling the fields
        setFieldsEditableState();

        // Filling the UI with values from the attribute type
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
        Section section_general_information = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        section_general_information.setDescription( Messages
            .getString( "AttributeTypeFormEditorOverviewPage.General_Section_Description" ) ); //$NON-NLS-1$
        section_general_information.setText( Messages
            .getString( "AttributeTypeFormEditorOverviewPage.General_Section_Text" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        GridLayout layout_general_information = new GridLayout();
        layout_general_information.numColumns = 3;
        client_general_information.setLayout( layout_general_information );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Adding elements to the section
        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        aliasesLabel = toolkit.createLabel( client_general_information, "" );
        aliasesLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
        toolkit.createLabel( client_general_information, "" );
        aliasesButton = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Manage_Aliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );

        // OID Field
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeFormEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        descriptionGridData.heightHint = 42;
        descriptionText.setLayoutData( descriptionGridData );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, "Schema:", SWT.WRAP );
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SUP Combo
        supLabel = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Superior_type" ), SWT.WRAP ); //$NON-NLS-1$
        supCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        supCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        supComboViewer = new ComboViewer( supCombo );
        supComboViewer.setContentProvider( new AttributeTypeEditorSuperiorComboContentProvider() );
        supComboViewer.setLabelProvider( new AttributeTypeEditorSuperiorComboLabelProvider() );
        supComboViewer.setInput( new AttributeTypeEditorSuperiorComboInput( originalAttributeType ) );

        // USAGE Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Usage" ) ); //$NON-NLS-1$
        usageCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        usageCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        initUsageCombo();

        // SYNTAX Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Synatx" ) ); //$NON-NLS-1$
        syntaxCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        initSyntaxCombo();

        // SYNTAX LENGTH Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Syntax_length" ) ); //$NON-NLS-1$
        syntaxLengthText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        syntaxLengthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SINGLE-VALUE Checkbox
        singleValueCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Single-Value" ), SWT.CHECK ); //$NON-NLS-1$
        singleValueCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // COLLECTIVE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        collectiveCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Collective" ), SWT.CHECK ); //$NON-NLS-1$
        collectiveCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // NO-USER-MODIFICATION Checkbox
        noUserModificationCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.No-User-Modification" ), SWT.CHECK ); //$NON-NLS-1$
        noUserModificationCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Creates the Matching Rules Section.
     *
     * @param parent
     *      the parent composite
     * @param toolkit
     *      the FormToolKit to use
     */
    private void createMatchingRulesSection( Composite parent, FormToolkit toolkit )
    {
        // Matching Rules Section
        Section section_matching_rules = toolkit.createSection( parent, Section.DESCRIPTION | Section.EXPANDED
            | Section.TITLE_BAR );
        section_matching_rules.setDescription( Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Specify_matching_rules" ) ); //$NON-NLS-1$
        section_matching_rules.setText( Messages.getString( "AttributeTypeFormEditorOverviewPage.Matching_Rules" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite client_matching_rules = toolkit.createComposite( section_matching_rules );
        GridLayout layout_matching_rules = new GridLayout();
        layout_matching_rules.numColumns = 2;
        client_matching_rules.setLayout( layout_matching_rules );
        toolkit.paintBordersFor( client_matching_rules );
        section_matching_rules.setClient( client_matching_rules );
        section_matching_rules.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // EQUALITY Combo
        toolkit
            .createLabel( client_matching_rules, Messages.getString( "AttributeTypeFormEditorOverviewPage.Equility" ) ); //$NON-NLS-1$
        equalityCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        equalityCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initEqualityCombo();

        // ORDERING Combo
        toolkit
            .createLabel( client_matching_rules, Messages.getString( "AttributeTypeFormEditorOverviewPage.Ordering" ) ); //$NON-NLS-1$
        orderingCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        orderingCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initOrderingCombo();

        // SUBSTRING Combo
        toolkit.createLabel( client_matching_rules, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Substring" ) ); //$NON-NLS-1$
        substringCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        substringCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initSubstringCombo();
    }


    private void initUsageCombo()
    {
        usageCombo.add( "directoryOperation", 0 ); //$NON-NLS-1$
        usageCombo.add( "distributedOperation", 1 ); //$NON-NLS-1$
        usageCombo.add( "DSAOperation", 2 ); //$NON-NLS-1$
        usageCombo.add( "userApplications", 3 ); //$NON-NLS-1$
    }


    private void initSyntaxCombo()
    {
        syntaxCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ), 0 ); //$NON-NLS-1$
        syntaxCombo.select( 0 );
        ArrayList<Syntax> syntaxes = Syntaxes.getSyntaxes();
        for ( Syntax syntax : syntaxes )
        {
            syntaxCombo.add( syntax.getName() );
        }
    }


    private void initEqualityCombo()
    {
        equalityCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        equalityCombo.select( 0 );
        ArrayList<MatchingRule> equalityMatchingRules = MatchingRules.getEqualityMatchingRules();
        for ( MatchingRule matchingRule : equalityMatchingRules )
        {
            equalityCombo.add( matchingRule.getName() );
        }
    }


    private void initOrderingCombo()
    {
        orderingCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        orderingCombo.select( 0 );
        ArrayList<MatchingRule> orderingMatchingRules = MatchingRules.getOrderingMatchingRules();
        for ( MatchingRule matchingRule : orderingMatchingRules )
        {
            orderingCombo.add( matchingRule.getName() );
        }
    }


    private void initSubstringCombo()
    {
        substringCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        substringCombo.select( 0 );
        ArrayList<MatchingRule> substringMatchingRules = MatchingRules.getSubstringMatchingRules();
        for ( MatchingRule matchingRule : substringMatchingRules )
        {
            substringCombo.add( matchingRule.getName() );
        }
    }


    private void fillInUiFields()
    {
        // ALIASES Label
        if ( ( modifiedAttributeType.getNames() != null ) && ( modifiedAttributeType.getNames().length != 0 ) )
        {
            aliasesLabel.setText( ViewUtils.concateAliases( modifiedAttributeType.getNames() ) );
        }
        else
        {
            aliasesLabel.setText( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) );
        }

        // OID Field
        if ( modifiedAttributeType.getOid() != null )
        {
            this.oidText.setText( modifiedAttributeType.getOid() );
        }

        // SCHEMA Field
        if ( modifiedAttributeType.getOriginatingSchema() != null )
        {
            this.schemaLabel.setText( modifiedAttributeType.getOriginatingSchema().getName() );
        }

        // DESCRIPTION Field
        if ( modifiedAttributeType.getDescription() != null )
        {
            this.descriptionText.setText( modifiedAttributeType.getDescription() );
        }

        // SUP Combo
        fillSupCombo();

        // USAGE Combo
        fillInUsageCombo();

        // SYNTAX Combo
        if ( modifiedAttributeType.getSyntax() == null )
        {
            fillInSyntaxCombo( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) );
        }
        else
        {
            fillInSyntaxCombo( modifiedAttributeType.getSyntax() );
        }

        // SYNTAX LENGTH Field
        if ( modifiedAttributeType.getLength() != -1 )
        {
            this.syntaxLengthText.setText( modifiedAttributeType.getLength() + "" ); //$NON-NLS-1$
        }

        // OBSOLETE Checkbox
        this.obsoleteCheckbox.setSelection( modifiedAttributeType.isObsolete() );

        // SINGLE-VALUE Checkbox
        this.singleValueCheckbox.setSelection( modifiedAttributeType.isSingleValue() );

        // COLLECTIVE Checkbox
        this.collectiveCheckbox.setSelection( modifiedAttributeType.isCollective() );

        // NO-USER-MODIFICATION Checkbox
        this.noUserModificationCheckbox.setSelection( modifiedAttributeType.isNoUserModification() );

        // EQUALITY Combo
        if ( modifiedAttributeType.getEquality() == null )
        {
            fillInEqualityCombo( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) );
        }
        else
        {
            fillInEqualityCombo( modifiedAttributeType.getEquality() );
        }

        // ORDERING Combo
        if ( modifiedAttributeType.getOrdering() == null )
        {
            fillInOrderingCombo( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) );
        }
        else
        {
            fillInOrderingCombo( modifiedAttributeType.getOrdering() );
        }

        // SUBSTRING Combo
        if ( modifiedAttributeType.getSubstr() == null )
        {
            fillInSubstringCombo( Messages.getString( "ObjectClassFormEditorOverviewPage.(None)" ) );
        }
        else
        {
            fillInSubstringCombo( modifiedAttributeType.getSubstr() );
        }
    }


    /**
     * Fills the the Sup Combo with the correct value
     *
     * @param name
     *      the name to select
     */
    private void fillSupCombo()
    {
        if ( modifiedAttributeType.getSuperior() == null )
        {
            supComboViewer.setSelection( new StructuredSelection( new NonExistingAttributeType(
                NonExistingAttributeType.NONE ) ), true );
        }
        else
        {
            String supAtName = modifiedAttributeType.getSuperior();

            SchemaPool schemaPool = SchemaPool.getInstance();

            AttributeType supAT = schemaPool.getAttributeType( supAtName );
            if ( supAT != null )
            {
                supComboViewer.setSelection( new StructuredSelection( supAT ), true );
            }
            else
            {
                AttributeTypeEditorSuperiorComboInput input = ( AttributeTypeEditorSuperiorComboInput ) supComboViewer
                    .getInput();
                NonExistingAttributeType neat = new NonExistingAttributeType( supAtName );
                if ( !input.getChildren().contains( neat ) )
                {
                    input.addChild( neat );
                }
                supComboViewer.refresh();
                supComboViewer.setSelection( new StructuredSelection( neat ), true );
            }
        }
    }


    /**
     * Fills the Usage Combo from the attribute type value
     */
    private void fillInUsageCombo()
    {
        if ( modifiedAttributeType.getUsage() == UsageEnum.DIRECTORY_OPERATION )
        {
            usageCombo.select( 0 );
        }
        else if ( modifiedAttributeType.getUsage() == UsageEnum.DISTRIBUTED_OPERATION )
        {
            usageCombo.select( 1 );
        }
        else if ( modifiedAttributeType.getUsage() == UsageEnum.DSA_OPERATION )
        {
            usageCombo.select( 2 );
        }
        else if ( modifiedAttributeType.getUsage() == UsageEnum.USER_APPLICATIONS )
        {
            usageCombo.select( 3 );
        }
    }


    /**
     * Fills the Syntax Combo from the attribute type value
     *
     * @param name
     *      the name to select
     */
    private void fillInSyntaxCombo( String name )
    {
        if ( name.equals( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) )
        {
            syntaxCombo.select( 0 );
        }
        else
        {
            syntaxCombo.select( 0 );
            for ( int i = 1; i < syntaxCombo.getItemCount(); i++ )
            {
                if ( Syntaxes.getSyntax( syntaxCombo.getItem( i ) ).getOid().equals( name ) )
                {
                    syntaxCombo.select( i );
                }
            }
        }
    }


    /**
     * Fills the Equality Combo from the attribute type value
     *
     * @param name
     *      the name to select
     */
    private void fillInEqualityCombo( String name )
    {
        equalityCombo.select( 0 );
        for ( int i = 0; i < equalityCombo.getItemCount(); i++ )
        {
            if ( name.equals( equalityCombo.getItem( i ) ) )
            {
                equalityCombo.select( i );
                return;
            }
        }
    }


    /**
     * Fills the Ordering Combo from the attribute type value
     *
     * @param name
     *      the name to select
     */
    private void fillInOrderingCombo( String name )
    {
        orderingCombo.select( 0 );
        for ( int i = 0; i < orderingCombo.getItemCount(); i++ )
        {
            if ( name.equals( orderingCombo.getItem( i ) ) )
            {
                orderingCombo.select( i );
                return;
            }
        }
    }


    /**
     * Fills the Substring Combo from the attribute type value
     *
     * @param name
     *      the name to select
     */
    private void fillInSubstringCombo( String name )
    {
        substringCombo.select( 0 );
        for ( int i = 0; i < substringCombo.getItemCount(); i++ )
        {
            if ( name.equals( substringCombo.getItem( i ) ) )
            {
                substringCombo.select( i );
                return;
            }
        }
    }


    /**
     * Enalbes/Disables the UI fields
     */
    private void setFieldsEditableState()
    {
        if ( modifiedAttributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            oidText.setEditable( false );
            descriptionText.setEditable( false );
            supCombo.setEnabled( false );
            usageCombo.setEnabled( false );
            syntaxCombo.setEnabled( false );
            syntaxLengthText.setEditable( false );
            obsoleteCheckbox.setEnabled( false );
            singleValueCheckbox.setEnabled( false );
            collectiveCheckbox.setEnabled( false );
            noUserModificationCheckbox.setEnabled( false );
            equalityCombo.setEnabled( false );
            orderingCombo.setEnabled( false );
            substringCombo.setEnabled( false );
        }
    }


    /**
     * Adds listeners to UI fields
     */
    private void addListeners()
    {
        if ( modifiedAttributeType.getOriginatingSchema().type == Schema.SchemaType.userSchema )
        {
            oidText.addModifyListener( oidTextModifyListener );
            oidText.addVerifyListener( oidTextVerifyListener );
            descriptionText.addModifyListener( descriptionTextListener );
            supLabel.addHyperlinkListener( supLabelListener );
            supCombo.addModifyListener( supComboListener );
            usageCombo.addModifyListener( usageComboListener );
            syntaxCombo.addModifyListener( syntaxComboListener );
            syntaxLengthText.addModifyListener( syntaxLengthTextModifyListener );
            syntaxLengthText.addVerifyListener( syntaxLengthTextVerifyListener );
            obsoleteCheckbox.addSelectionListener( obsoleteCheckboxListener );
            singleValueCheckbox.addSelectionListener( singleValueCheckboxListener );
            collectiveCheckbox.addSelectionListener( collectiveCheckboxListener );
            noUserModificationCheckbox.addSelectionListener( noUserModificationCheckboxListener );
            equalityCombo.addModifyListener( equalityComboListener );
            orderingCombo.addModifyListener( orderingComboListener );
            substringCombo.addModifyListener( substringComboListener );
        }

        // ALIASES Button
        // The user can always access to the Manage Aliases Window, but if the object class is in a core-schema file editing will be disabled
        aliasesButton.addSelectionListener( aliasesButtonListener );
        schemaLink.addHyperlinkListener( schemaLinkListener );
        supLabel.addHyperlinkListener( supLabelListener );
    }


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        oidText.removeModifyListener( oidTextModifyListener );
        oidText.removeVerifyListener( oidTextVerifyListener );
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        schemaLink.removeHyperlinkListener( schemaLinkListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        supLabel.removeHyperlinkListener( supLabelListener );
        supCombo.removeModifyListener( supComboListener );
        usageCombo.removeModifyListener( usageComboListener );
        syntaxCombo.removeModifyListener( syntaxComboListener );
        syntaxLengthText.removeModifyListener( syntaxLengthTextModifyListener );
        syntaxLengthText.removeVerifyListener( syntaxLengthTextVerifyListener );
        obsoleteCheckbox.removeSelectionListener( obsoleteCheckboxListener );
        singleValueCheckbox.removeSelectionListener( singleValueCheckboxListener );
        collectiveCheckbox.removeSelectionListener( collectiveCheckboxListener );
        noUserModificationCheckbox.removeSelectionListener( noUserModificationCheckboxListener );
        equalityCombo.removeModifyListener( equalityComboListener );
        orderingCombo.removeModifyListener( orderingComboListener );
        substringCombo.removeModifyListener( substringComboListener );
    }


    /**
     * Sets the dirty state of the editor to dirty
     */
    private void setEditorDirty()
    {
        ( ( AttributeTypeEditor ) getEditor() ).setDirty( true );
    }


    /**
     * Refreshes the UI.
     */
    public void refreshUI()
    {
        removeListeners();
        fillInUiFields();
        addListeners();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.model.PoolListener#poolChanged(org.apache.directory.ldapstudio.schemas.model.SchemaPool, org.apache.directory.ldapstudio.schemas.model.LDAPModelEvent)
     */
    public void poolChanged( SchemaPool p, LDAPModelEvent e )
    {
        removeListeners();
        supComboViewer.setInput( new AttributeTypeEditorSuperiorComboInput( originalAttributeType ) );
        fillSupCombo();
        addListeners();
    }
}
