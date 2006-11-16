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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
    private Text name_text;
    private Text oid_text;
    private Text description_text;
    private Hyperlink sup_label;
    private Combo sup_combo;
    private Combo usage_combo;
    private Combo syntax_combo;
    private Text syntaxLength_text;
    private Button obsolete_checkbox;
    private Button singleValue_checkbox;
    private Button collective_checkbox;
    private Button noUserModification_checkbox;
    private Combo equality_combo;
    private Combo ordering_combo;
    private Combo substring_combo;
    private AttributeType attributeType;
    private Button aliases_button;
    private String[] aliasesList;


    /**
     * Default constructor
     * @param editor
     * @param id
     * @param title
     */
    public AttributeTypeFormEditorOverviewPage( FormEditor editor, String id, String title )
    {
        super( editor, id, title );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        form.getBody().setLayout( layout );

        // Getting the input and the attributeType
        AttributeTypeFormEditorInput input = ( AttributeTypeFormEditorInput ) getEditorInput();
        attributeType = ( AttributeType ) input.getAttributeType();

        // General Information Section
        Section section_general_information = toolkit.createSection( form.getBody(), Section.DESCRIPTION
            | Section.EXPANDED | Section.TITLE_BAR );
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
        name_text = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        name_text.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        aliases_button = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Manage_aliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliases_button.setLayoutData( new GridData( SWT.NONE, SWT.BEGINNING, false, false, 2, 1 ) );

        // OID Field
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeFormEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oid_text = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oid_text.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        description_text = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        description_text.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ) );

        // SUP Combo
        sup_label = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Superior_type" ), SWT.WRAP ); //$NON-NLS-1$
        sup_combo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        sup_combo.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // USAGE Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Usage" ) ); //$NON-NLS-1$
        usage_combo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        usage_combo.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // SYNTAX Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Synatx" ) ); //$NON-NLS-1$
        syntax_combo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        syntax_combo.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // SYNTAX LENGTH Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Syntax_length" ) ); //$NON-NLS-1$
        syntaxLength_text = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        syntaxLength_text.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        obsolete_checkbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsolete_checkbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // SINGLE-VALUE Checkbox
        singleValue_checkbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Single-Value" ), SWT.CHECK ); //$NON-NLS-1$
        singleValue_checkbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // COLLECTIVE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        collective_checkbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Collective" ), SWT.CHECK ); //$NON-NLS-1$
        collective_checkbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // NO-USER-MODIFICATION Checkbox
        noUserModification_checkbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.No-User-Modification" ), SWT.CHECK ); //$NON-NLS-1$
        noUserModification_checkbox.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // Matching Rules Section
        Section section_matching_rules = toolkit.createSection( form.getBody(), Section.DESCRIPTION | Section.EXPANDED
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

        // Adding elements to the section
        // EQUALITY Combo
        toolkit
            .createLabel( client_matching_rules, Messages.getString( "AttributeTypeFormEditorOverviewPage.Equility" ) ); //$NON-NLS-1$
        equality_combo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        equality_combo.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // ORDERING Combo
        toolkit
            .createLabel( client_matching_rules, Messages.getString( "AttributeTypeFormEditorOverviewPage.Ordering" ) ); //$NON-NLS-1$
        ordering_combo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        ordering_combo.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // SUBSTRING Combo
        toolkit.createLabel( client_matching_rules, Messages
            .getString( "AttributeTypeFormEditorOverviewPage.Substring" ) ); //$NON-NLS-1$
        substring_combo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        substring_combo.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // Initialization from the "input" attribute type
        initFieldsContentFromInput();

        // Listeners initialization
        initListeners();
    }


    private void initFieldsContentFromInput()
    {
        // NAME Field
        if ( attributeType.getNames()[0] != null )
        {
            this.name_text.setText( attributeType.getNames()[0] );
        }

        // ALIASES
        String[] names = attributeType.getNames();
        ArrayList<String> aliases = new ArrayList<String>();
        for ( int i = 1; i < names.length; i++ )
        {
            String name = names[i];
            aliases.add( name );
        }
        this.aliasesList = aliases.toArray( new String[0] );

        // OID Field
        if ( attributeType.getOid() != null )
        {
            this.oid_text.setText( attributeType.getOid() );
        }

        // DESCRIPTION Field
        if ( attributeType.getDescription() != null )
        {
            this.description_text.setText( attributeType.getDescription() );
        }

        // SUP Combo
        initSup_combo();

        // USAGE Combo
        initUsage_combo();

        // SYNTAX Combo
        initSyntax_combo();

        // SYNTAX LENGTH Field
        if ( attributeType.getLength() != -1 )
        {
            this.syntaxLength_text.setText( attributeType.getLength() + "" ); //$NON-NLS-1$
        }

        // OBSOLETE Checkbox
        this.obsolete_checkbox.setSelection( attributeType.isObsolete() );

        // SINGLE-VALUE Checkbox
        this.singleValue_checkbox.setSelection( attributeType.isSingleValue() );

        // COLLECTIVE Checkbox
        this.collective_checkbox.setSelection( attributeType.isCollective() );

        // NO-USER-MODIFICATION Checkbox
        this.noUserModification_checkbox.setSelection( attributeType.isNoUserModification() );

        // EQUALITY Combo
        initEqualityCombo();

        // ORDERING Combo
        initOrderingCombo();

        // SUBSTRING
        initSubstringCombo();
    }


    private void initSup_combo()
    {
        SchemaPool pool = SchemaPool.getInstance();
        ArrayList<AttributeType> atList = new ArrayList<AttributeType>( pool.getAttributeTypesAsHashTableByName()
            .values() );

        //remove duplicate entries
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
        sup_combo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        sup_combo.select( 0 );
        int counter = 1;
        for ( AttributeType at : atList )
        {
            // TODO : Ajouter une verification pour qu'on ne puisse pas ajouter en sup l'objectclass lui meme et ses alias
            sup_combo.add( at.getNames()[0], counter );
            if ( ( ( attributeType.getSuperior() != null ) || ( attributeType.getSuperior() != "" ) ) && ( at.getNames()[0].equals( attributeType.getSuperior() ) ) ) { //$NON-NLS-1$
                // We select the right superior
                sup_combo.select( counter );
            }
            counter++;
        }
    }


    private void initUsage_combo()
    {
        usage_combo.add( "directoryOperation", 0 ); //$NON-NLS-1$
        usage_combo.add( "distributedOperation", 1 ); //$NON-NLS-1$
        usage_combo.add( "DSAOperation", 2 ); //$NON-NLS-1$
        usage_combo.add( "userApplications", 3 ); //$NON-NLS-1$
        if ( attributeType.getUsage() == UsageEnum.DIRECTORYOPERATION )
        {
            usage_combo.select( 0 );
        }
        else if ( attributeType.getUsage() == UsageEnum.DISTRIBUTEDOPERATION )
        {
            usage_combo.select( 1 );
        }
        else if ( attributeType.getUsage() == UsageEnum.DSAOPERATION )
        {
            usage_combo.select( 2 );
        }
        else if ( attributeType.getUsage() == UsageEnum.USERAPPLICATIONS )
        {
            usage_combo.select( 3 );
        }
    }


    private void initSyntax_combo()
    {
        syntax_combo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ), 0 ); //$NON-NLS-1$
        syntax_combo.select( 0 );
        ArrayList<Syntax> syntaxes = Syntaxes.getSyntaxes();
        int counter = 1;
        for ( Syntax syntax : syntaxes )
        {
            syntax_combo.add( syntax.getName() );
            if ( ( attributeType.getSyntax() != null ) && ( attributeType.getSyntax().equals( syntax.getOid() ) ) )
            {
                syntax_combo.select( counter );
            }
            counter++;
        }
    }


    private void initEqualityCombo()
    {
        equality_combo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        equality_combo.select( 0 );
        int counter = 1;
        ArrayList<MatchingRule> equalityMatchingRules = MatchingRules.getEqualityMatchingRules();
        for ( MatchingRule matchingRule : equalityMatchingRules )
        {
            equality_combo.add( matchingRule.getName() );
            if ( ( attributeType.getEquality() != null ) && attributeType.getEquality().equals( matchingRule.getName() ) )
            {
                equality_combo.select( counter );
            }
            counter++;
        }
    }


    private void initOrderingCombo()
    {
        ordering_combo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        ordering_combo.select( 0 );
        int counter = 1;
        ArrayList<MatchingRule> orderingMatchingRules = MatchingRules.getOrderingMatchingRules();
        for ( MatchingRule matchingRule : orderingMatchingRules )
        {
            ordering_combo.add( matchingRule.getName() );
            if ( ( attributeType.getOrdering() != null ) && attributeType.getOrdering().equals( matchingRule.getName() ) )
            {
                ordering_combo.select( counter );
            }
            counter++;
        }
    }


    private void initSubstringCombo()
    {
        substring_combo.add( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ); //$NON-NLS-1$
        substring_combo.select( 0 );
        int counter = 1;
        ArrayList<MatchingRule> substringMatchingRules = MatchingRules.getSubstringMatchingRules();
        for ( MatchingRule matchingRule : substringMatchingRules )
        {
            substring_combo.add( matchingRule.getName() );
            if ( ( attributeType.getSubstr() != null ) && attributeType.getSubstr().equals( matchingRule.getName() ) )
            {
                substring_combo.select( counter );
            }
            counter++;
        }
    }


    private void initListeners()
    {
        // NAME Field
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            name_text.setEditable( false );
        }
        else
        {
            // else we set the listener
            name_text.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // ALIASES Button
        // The user can always access to the Manage Aliases Window, but if the object class is in a core-schema file editing will be disabled
        aliases_button.addSelectionListener( new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                ManageAliasesDialog manageDialog = new ManageAliasesDialog( null, aliasesList, ( attributeType
                    .getOriginatingSchema().type == Schema.SchemaType.coreSchema ) );
                if ( manageDialog.open() != Window.OK )
                {
                    return;
                }
                if ( manageDialog.isDirty() )
                {
                    aliasesList = manageDialog.getAliasesList();
                    setEditorDirty();
                }
            }
        } );

        // OID Field
        // AT THE MOMENT, WE CANNOT SET A NEW OID TO THE ATTRIBUTE TYPE, SO WE DISABLE THIS FUNCTIONNALITY
        oid_text.setEditable( false );
        // AND WE REMOVE THE LISTENERS
        // if (attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema) {
        // 		// If the attribute type is in a core-schema file, we disable editing
        // 		oid_text.setEditable(false);
        // } else {
        // 		// else we set the listener
        // 		oid_text.addModifyListener(new ModifyListener() {
        //			public void modifyText(ModifyEvent e) { setEditorDirty(); }		
        //		});
        // }

        // DESCRIPTION Field
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            description_text.setEditable( false );
        }
        else
        {
            // else we set the listener
            description_text.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // SUP Combo
        sup_label.addHyperlinkListener( new HyperlinkAdapter()
        {
            public void linkActivated( HyperlinkEvent e )
            {
                if ( !sup_combo.getItem( sup_combo.getSelectionIndex() ).equals(
                    Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
                    SchemaPool pool = SchemaPool.getInstance();
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                    AttributeTypeFormEditorInput input = new AttributeTypeFormEditorInput( pool
                        .getAttributeType( sup_combo.getItem( sup_combo.getSelectionIndex() ) ) );
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
        } );
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            sup_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            sup_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // USAGE Combo
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            usage_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            usage_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // SYNTAX Combo
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            syntax_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            syntax_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // SYNTAX LENGTH Field
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            syntaxLength_text.setEditable( false );
        }
        else
        {
            // else we set the listener
            syntaxLength_text.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // OBSOLETE Checkbox
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            obsolete_checkbox.setEnabled( false );
        }
        else
        {
            // else we set the listener
            obsolete_checkbox.addSelectionListener( new SelectionListener()
            {
                public void widgetDefaultSelected( SelectionEvent e )
                {
                }


                public void widgetSelected( SelectionEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // SINGLE-VALUE Checkbox
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            singleValue_checkbox.setEnabled( false );
        }
        else
        {
            // else we set the listener
            singleValue_checkbox.addSelectionListener( new SelectionListener()
            {
                public void widgetDefaultSelected( SelectionEvent e )
                {
                }


                public void widgetSelected( SelectionEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // COLLECTIVE Checkbox
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            collective_checkbox.setEnabled( false );
        }
        else
        {
            // else we set the listener
            collective_checkbox.addSelectionListener( new SelectionListener()
            {
                public void widgetDefaultSelected( SelectionEvent e )
                {
                }


                public void widgetSelected( SelectionEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // NO-USER-MODIFICATION Checkbox
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            noUserModification_checkbox.setEnabled( false );
        }
        else
        {
            // else we set the listener
            noUserModification_checkbox.addSelectionListener( new SelectionListener()
            {
                public void widgetDefaultSelected( SelectionEvent e )
                {
                }


                public void widgetSelected( SelectionEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // EQUALITY Combo
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            equality_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            equality_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // ORDERING Combo
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            ordering_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            ordering_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }

        // SUBSTRING Combo
        if ( attributeType.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the attribute type is in a core-schema file, we disable editing
            substring_combo.setEnabled( false );
        }
        else
        {
            // else we set the listener
            substring_combo.addModifyListener( new ModifyListener()
            {
                public void modifyText( ModifyEvent e )
                {
                    setEditorDirty();
                }
            } );
        }
    }


    private void setEditorDirty()
    {
        ( ( AttributeTypeFormEditor ) getEditor() ).setDirty( true );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void doSave( IProgressMonitor monitor )
    {
        // NAME
        // TODO : Pour le moment on ne gere pas les alias, seul un nom est possible
        //		String[] oldNames = attributeType.getNames();
        //		oldNames[0] = name_text.getText();
        //		attributeType.setNames(oldNames);
        ArrayList<String> names = new ArrayList<String>();
        names.add( name_text.getText() );
        for ( int i = 0; i < this.aliasesList.length; i++ )
        {
            names.add( this.aliasesList[i] );
        }
        attributeType.setNames( names.toArray( new String[0] ) );

        // OID
        // TODO : Il n'y a pas de setOid sur l'attributeTypeLiteral
        //attributeType.setOid(oid_text.getText());

        // DESCRIPTION
        attributeType.setDescription( description_text.getText() );

        // SUP
        if ( sup_combo.getItem( sup_combo.getSelectionIndex() ).equals(
            Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
            attributeType.setSuperior( "" ); //$NON-NLS-1$
        }
        else
        {
            attributeType.setSuperior( sup_combo.getItem( sup_combo.getSelectionIndex() ) );
        }

        // USAGE
        if ( usage_combo.getSelectionIndex() == 0 )
        {
            attributeType.setUsage( UsageEnum.DIRECTORYOPERATION );
        }
        else if ( usage_combo.getSelectionIndex() == 1 )
        {
            attributeType.setUsage( UsageEnum.DISTRIBUTEDOPERATION );
        }
        else if ( usage_combo.getSelectionIndex() == 2 )
        {
            attributeType.setUsage( UsageEnum.DSAOPERATION );
        }
        else if ( usage_combo.getSelectionIndex() == 3 )
        {
            attributeType.setUsage( UsageEnum.USERAPPLICATIONS );
        }

        // SYNTAX
        if ( syntax_combo.getItem( syntax_combo.getSelectionIndex() ).equals(
            Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
            attributeType.setSyntax( "" ); //$NON-NLS-1$
        }
        else
        {
            attributeType.setSyntax( Syntaxes.getSyntax( syntax_combo.getItem( syntax_combo.getSelectionIndex() ) )
                .getOid() );
        }

        // SYNTAX LENGTH
        if ( syntaxLength_text.getText().length() == 0 )
        {
            attributeType.setLength( -1 );
        }
        else
        {
            attributeType.setLength( Integer.parseInt( syntaxLength_text.getText() ) );
        }

        // OBSOLETE
        attributeType.setObsolete( obsolete_checkbox.getSelection() );

        // SINGLE-VALUE
        attributeType.setSingleValue( singleValue_checkbox.getSelection() );

        // COLLECTIVE
        attributeType.setCollective( collective_checkbox.getSelection() );

        // NO-USE-MODIFICATION
        attributeType.setNoUserModification( noUserModification_checkbox.getSelection() );

        // EQUALITY
        if ( equality_combo.getItem( equality_combo.getSelectionIndex() ).equals(
            Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
            attributeType.setEquality( "" ); //$NON-NLS-1$
        }
        else
        {
            attributeType.setEquality( equality_combo.getItem( equality_combo.getSelectionIndex() ) );
        }

        // ORDERING
        if ( ordering_combo.getItem( ordering_combo.getSelectionIndex() ).equals(
            Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
            attributeType.setOrdering( "" ); //$NON-NLS-1$
        }
        else
        {
            attributeType.setOrdering( ordering_combo.getItem( ordering_combo.getSelectionIndex() ) );
        }

        // SUBSTRING
        if ( substring_combo.getItem( substring_combo.getSelectionIndex() ).equals(
            Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) ) ) { //$NON-NLS-1$
            attributeType.setSubstr( "" ); //$NON-NLS-1$
        }
        else
        {
            attributeType.setSubstr( substring_combo.getItem( substring_combo.getSelectionIndex() ) );
        }
    }
}
