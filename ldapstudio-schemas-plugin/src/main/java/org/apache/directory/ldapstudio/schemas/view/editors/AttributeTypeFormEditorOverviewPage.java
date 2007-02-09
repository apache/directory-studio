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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.MatchingRule;
import org.apache.directory.ldapstudio.schemas.model.MatchingRules;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.model.Syntax;
import org.apache.directory.ldapstudio.schemas.model.Syntaxes;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.log4j.Logger;
import org.eclipse.jface.window.Window;
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
public class AttributeTypeFormEditorOverviewPage extends FormPage
{
    /** The page ID*/
    public static final String ID = AttributeTypeFormEditor.ID + "overviewPage";

    /** The page title */
    public static String TITLE = Messages.getString( "AttributeTypeFormEditor.Overview" );

    /** The modified object class */
    private AttributeType modifiedAttributeType;

    // UI Fields
    private Text nameText;
    private String[] aliasesList;
    private Button aliasesButton;
    private Text oidText;
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

    /** The listener for the Name Text Widget*/
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
            modifiedAttributeType.setNames( names.toArray( new String[0] ) );
            setEditorDirty();
        }
    };

    /** The listener for the Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ManageAliasesDialog manageDialog = new ManageAliasesDialog( null, aliasesList, ( modifiedAttributeType
                .getOriginatingSchema().type == Schema.SchemaType.coreSchema ) );
            if ( manageDialog.open() != Window.OK )
            {
                return;
            }
            if ( manageDialog.isDirty() )
            {
                aliasesList = manageDialog.getAliasesList();
                ArrayList<String> names = new ArrayList<String>();
                names.add( modifiedAttributeType.getNames()[0] );
                for ( int i = 0; i < aliasesList.length; i++ )
                {
                    names.add( aliasesList[i] );
                }
                modifiedAttributeType.setNames( names.toArray( new String[0] ) );
                setEditorDirty();
            }
        }
    };

    /** The listener for the OID Text Widget */
    //    private Object oidTextListener;
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
            if ( !supCombo.getItem( supCombo.getSelectionIndex() ).equals(
                Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                SchemaPool pool = SchemaPool.getInstance();
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( pool.getAttributeType( supCombo
                    .getItem( supCombo.getSelectionIndex() ) ) );
                String editorId = AttributeTypeFormEditor.ID;
                try
                {
                    page.openEditor( input, editorId );
                }
                catch ( PartInitException exception )
                {
                    Logger.getLogger( AttributeTypeFormEditorInput.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                }
            }
        }
    };

    /** The listener for the Sup Combo Widget */
    private ModifyListener supComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( supCombo.getItem( supCombo.getSelectionIndex() ).equals(
                Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                modifiedAttributeType.setSuperior( "" ); //$NON-NLS-1$
            }
            else
            {
                modifiedAttributeType.setSuperior( supCombo.getItem( supCombo.getSelectionIndex() ) );
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

    /** The listener for the Syntax Length Text Widget */
    private ModifyListener syntaxLengthTextListener = new ModifyListener()
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


    /**
     * Default constructor.
     * 
     * @param editor
     *      the associated editor
     */
    public AttributeTypeFormEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the modified attribute type and listening to its modifications
        modifiedAttributeType = ( ( AttributeTypeFormEditor ) getEditor() ).getModifiedAttributeType();

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
        section_general_information.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // Adding elements to the section
        // NAME Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Name" ) ); //$NON-NLS-1$
        nameText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        nameText.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        aliasesButton = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Manage_aliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false, 2, 1 ) );

        // OID Field
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeFormEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        descriptionText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );

        // SUP Combo
        supLabel = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Superior_type" ), SWT.WRAP ); //$NON-NLS-1$
        supCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        supCombo.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );
        initSupCombo();

        // USAGE Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Usage" ) ); //$NON-NLS-1$
        usageCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        usageCombo.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );
        initUsageCombo();

        // SYNTAX Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Synatx" ) ); //$NON-NLS-1$
        syntaxCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );
        initSyntaxCombo();

        // SYNTAX LENGTH Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Syntax_length" ) ); //$NON-NLS-1$
        syntaxLengthText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        syntaxLengthText.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // SINGLE-VALUE Checkbox
        singleValueCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Single-Value" ), SWT.CHECK ); //$NON-NLS-1$
        singleValueCheckbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // COLLECTIVE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        collectiveCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Collective" ), SWT.CHECK ); //$NON-NLS-1$
        collectiveCheckbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // NO-USER-MODIFICATION Checkbox
        noUserModificationCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.No-User-Modification" ), SWT.CHECK ); //$NON-NLS-1$
        noUserModificationCheckbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );
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
        section_matching_rules.setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // EQUALITY Combo
        toolkit
            .createLabel( client_matching_rules, Messages.getString( "AttributeTypeFormEditorOverviewPage.Equility" ) ); //$NON-NLS-1$
        equalityCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        equalityCombo.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );
        initEqualityCombo();

        // ORDERING Combo
        toolkit
            .createLabel( client_matching_rules, Messages.getString( "AttributeTypeFormEditorOverviewPage.Ordering" ) ); //$NON-NLS-1$
        orderingCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        orderingCombo.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );
        initOrderingCombo();

        // SUBSTRING Combo
        toolkit.createLabel( client_matching_rules, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Substring" ) ); //$NON-NLS-1$
        substringCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        substringCombo.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );
        initSubstringCombo();
    }


    private void initSupCombo()
    {
        SchemaPool pool = SchemaPool.getInstance();
        ArrayList<AttributeType> atList = new ArrayList<AttributeType>( pool.getAttributeTypesAsHashTableByName()
            .values() );

        // Remove duplicate entries
        HashSet<AttributeType> set = new HashSet<AttributeType>( atList );
        atList = new ArrayList<AttributeType>( set );

        // Sorting the list
        Collections.sort( atList, new Comparator<AttributeType>()
        {
            public int compare( AttributeType arg0, AttributeType arg1 )
            {
                String oneName = arg0.getNames()[0];
                String twoName = arg1.getNames()[0];
                return oneName.compareTo( twoName );
            }
        } );

        // Creating the UI
        supCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        supCombo.select( 0 );
        int counter = 1;
        for ( AttributeType at : atList )
        {
            // TODO : Ajouter une verification pour qu'on ne puisse pas ajouter en sup l'objectclass lui meme et ses alias
            supCombo.add( at.getNames()[0], counter );
            if ( ( ( modifiedAttributeType.getSuperior() != null ) || ( modifiedAttributeType.getSuperior() != "" ) ) && ( at.getNames()[0].equals( modifiedAttributeType.getSuperior() ) ) ) { //$NON-NLS-1$
                // We select the right superior
                supCombo.select( counter );
            }
            counter++;
        }
    }


    private void initUsageCombo()
    {
        usageCombo.add( "directoryOperation", 0 ); //$NON-NLS-1$
        usageCombo.add( "distributedOperation", 1 ); //$NON-NLS-1$
        usageCombo.add( "DSAOperation", 2 ); //$NON-NLS-1$
        usageCombo.add( "userApplications", 3 ); //$NON-NLS-1$
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


    private void initSyntaxCombo()
    {
        syntaxCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ), 0 ); //$NON-NLS-1$
        syntaxCombo.select( 0 );
        ArrayList<Syntax> syntaxes = Syntaxes.getSyntaxes();
        int counter = 1;
        for ( Syntax syntax : syntaxes )
        {
            syntaxCombo.add( syntax.getName() );
            if ( ( modifiedAttributeType.getSyntax() != null )
                && ( modifiedAttributeType.getSyntax().equals( syntax.getOid() ) ) )
            {
                syntaxCombo.select( counter );
            }
            counter++;
        }
    }


    private void initEqualityCombo()
    {
        equalityCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        equalityCombo.select( 0 );
        int counter = 1;
        ArrayList<MatchingRule> equalityMatchingRules = MatchingRules.getEqualityMatchingRules();
        for ( MatchingRule matchingRule : equalityMatchingRules )
        {
            equalityCombo.add( matchingRule.getName() );
            if ( ( modifiedAttributeType.getEquality() != null )
                && modifiedAttributeType.getEquality().equals( matchingRule.getName() ) )
            {
                equalityCombo.select( counter );
            }
            counter++;
        }
    }


    private void initOrderingCombo()
    {
        orderingCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        orderingCombo.select( 0 );
        int counter = 1;
        ArrayList<MatchingRule> orderingMatchingRules = MatchingRules.getOrderingMatchingRules();
        for ( MatchingRule matchingRule : orderingMatchingRules )
        {
            orderingCombo.add( matchingRule.getName() );
            if ( ( modifiedAttributeType.getOrdering() != null )
                && modifiedAttributeType.getOrdering().equals( matchingRule.getName() ) )
            {
                orderingCombo.select( counter );
            }
            counter++;
        }
    }


    private void initSubstringCombo()
    {
        substringCombo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        substringCombo.select( 0 );
        int counter = 1;
        ArrayList<MatchingRule> substringMatchingRules = MatchingRules.getSubstringMatchingRules();
        for ( MatchingRule matchingRule : substringMatchingRules )
        {
            substringCombo.add( matchingRule.getName() );
            if ( ( modifiedAttributeType.getSubstr() != null )
                && modifiedAttributeType.getSubstr().equals( matchingRule.getName() ) )
            {
                substringCombo.select( counter );
            }
            counter++;
        }
    }


    private void fillInUiFields()
    {
        // NAME Field
        if ( modifiedAttributeType.getNames()[0] != null )
        {
            this.nameText.setText( modifiedAttributeType.getNames()[0] );
        }

        // ALIASES
        String[] names = modifiedAttributeType.getNames();
        ArrayList<String> aliases = new ArrayList<String>();
        for ( int i = 1; i < names.length; i++ )
        {
            String name = names[i];
            aliases.add( name );
        }
        this.aliasesList = aliases.toArray( new String[0] );

        // OID Field
        if ( modifiedAttributeType.getOid() != null )
        {
            this.oidText.setText( modifiedAttributeType.getOid() );
        }

        // DESCRIPTION Field
        if ( modifiedAttributeType.getDescription() != null )
        {
            this.descriptionText.setText( modifiedAttributeType.getDescription() );
        }

        // SUP Combo
        //initSup_combo();
        // TODO

        // USAGE Combo
        //        initUsage_combo();
        // TODO

        // SYNTAX Combo
        //        initSyntax_combo();
        // TODO

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
        //        initEqualityCombo();
        // TODO

        // ORDERING Combo
        //        initOrderingCombo();
        // TODO

        // SUBSTRING
        //        initSubstringCombo();
        // TODO
    }


    /**
     * Enalbes/Disables the UI fields
     */
    private void setFieldsEditableState()
    {
        if ( modifiedAttributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            nameText.setEditable( false );
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

        // AT THE MOMENT, WE CANNOT SET A NEW OID TO THE ATTRIBUTE TYPE, SO WE DISABLE THIS FUNCTIONNALITY
        oidText.setEditable( false );
    }


    /**
     * Adds listeners to UI fields
     */
    private void addListeners()
    {
        if ( modifiedAttributeType.getOriginatingSchema().type == Schema.SchemaType.userSchema )
        {
            nameText.addModifyListener( nameTextListener );
            descriptionText.addModifyListener( descriptionTextListener );
            supLabel.addHyperlinkListener( supLabelListener );
            supCombo.addModifyListener( supComboListener );
            usageCombo.addModifyListener( usageComboListener );
            syntaxCombo.addModifyListener( syntaxComboListener );
            syntaxLengthText.addModifyListener( syntaxLengthTextListener );
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

        supLabel.addHyperlinkListener( supLabelListener );
    }


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        nameText.removeModifyListener( nameTextListener );
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        supLabel.removeHyperlinkListener( supLabelListener );
        supCombo.removeModifyListener( supComboListener );
        usageCombo.removeModifyListener( usageComboListener );
        syntaxCombo.removeModifyListener( syntaxComboListener );
        syntaxLengthText.removeModifyListener( syntaxLengthTextListener );
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
        ( ( AttributeTypeFormEditor ) getEditor() ).setDirty( true );
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
}
