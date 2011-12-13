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

package org.apache.directory.studio.schemaeditor.view.editors.attributetype;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.asn1.util.Oid;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.alias.Alias;
import org.apache.directory.studio.schemaeditor.model.alias.AliasWithError;
import org.apache.directory.studio.schemaeditor.model.alias.AliasesStringParser;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.dialogs.EditAliasesDialog;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingMatchingRule;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingSyntax;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditorInput;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
public class AttributeTypeEditorOverviewPage extends FormPage
{
    /** The page ID*/
    public static final String ID = AttributeTypeEditor.ID + ".overviewPage"; //$NON-NLS-1$

    /** The original attribute type */
    private AttributeType originalAttributeType;

    /** The modified attribute type */
    private AttributeType modifiedAttributeType;

    /** The original schema */
    private Schema originalSchema;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaHandler Listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerListener()
    {
        public void attributeTypeAdded( AttributeType at )
        {
            refreshUI();
        }


        public void attributeTypeModified( AttributeType at )
        {
            refreshUI();
        }


        public void attributeTypeRemoved( AttributeType at )
        {
            if ( !at.equals( originalAttributeType ) )
            {
                refreshUI();
            }
        }


        public void matchingRuleAdded( MatchingRule mr )
        {
            refreshUI();
        }


        public void matchingRuleModified( MatchingRule mr )
        {
            refreshUI();
        }


        public void matchingRuleRemoved( MatchingRule mr )
        {
            refreshUI();
        }


        public void objectClassAdded( ObjectClass oc )
        {
            refreshUI();
        }


        public void objectClassModified( ObjectClass oc )
        {
            refreshUI();
        }


        public void objectClassRemoved( ObjectClass oc )
        {
            refreshUI();
        }


        public void schemaAdded( Schema schema )
        {
            refreshUI();
        }


        public void schemaRemoved( Schema schema )
        {
            if ( !schema.equals( originalSchema ) )
            {
                refreshUI();
            }
        }


        public void schemaRenamed( Schema schema )
        {
            refreshUI();
        }


        public void syntaxAdded( LdapSyntax syntax )
        {
            refreshUI();
        }


        public void syntaxModified( LdapSyntax syntax )
        {
            refreshUI();
        }


        public void syntaxRemoved( LdapSyntax syntax )
        {
            refreshUI();
        }
    };

    // UI Fields
    private Text aliasesText;
    private Button aliasesButton;
    private Text oidText;
    private Hyperlink schemaLink;
    private Label schemaLabel;
    private Text descriptionText;
    private Hyperlink supLabel;
    private Combo supCombo;
    private ComboViewer supComboViewer;
    private Combo usageCombo;
    private Combo syntaxCombo;
    private ComboViewer syntaxComboViewer;
    private Text syntaxLengthText;
    private Button obsoleteCheckbox;
    private Button singleValueCheckbox;
    private Button collectiveCheckbox;
    private Button noUserModificationCheckbox;
    private Combo equalityCombo;
    private ComboViewer equalityComboViewer;
    private Combo orderingCombo;
    private ComboViewer orderingComboViewer;
    private Combo substringCombo;
    private ComboViewer substringComboViewer;

    // Listeners

    /** The listener for the Aliases Text Widget */
    private ModifyListener aliasesTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            AliasesStringParser parser = new AliasesStringParser();
            parser.parse( aliasesText.getText() );
            List<Alias> parsedAliases = parser.getAliases();
            modifiedAttributeType.setNames( new String[0] );
            List<String> aliasesList = new ArrayList<String>();
            for ( Alias parsedAlias : parsedAliases )
            {
                if ( !( parsedAlias instanceof AliasWithError ) )
                {
                    aliasesList.add( parsedAlias.getAlias() );
                }
            }
            modifiedAttributeType.setNames( aliasesList.toArray( new String[0] ) );
            setEditorDirty();
        }
    };

    /** The listener for the Edit Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            EditAliasesDialog editDialog = new EditAliasesDialog( modifiedAttributeType.getNames() );
            if ( editDialog.open() != EditAliasesDialog.OK )
            {
                return;
            }
            if ( editDialog.isDirty() )
            {
                modifiedAttributeType.setNames( editDialog.getAliases() );
                if ( ( modifiedAttributeType.getNames() != null )
                    && ( modifiedAttributeType.getNames().size() != 0 ) )
                {
                    aliasesText.setText( ViewUtils.concateAliases( modifiedAttributeType.getNames() ) );
                }
                else
                {
                    aliasesText.setText( "" ); //$NON-NLS-1$
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
            oidText.setToolTipText( "" ); //$NON-NLS-1$

            String oid = oidText.getText();

            if ( Oid.isOid( oid ) )
            {
                if ( ( originalAttributeType.getOid().equals( oid ) )
                    || !( schemaHandler.isAliasOrOidAlreadyTaken( oid ) ) )
                {
                    modifiedAttributeType.setOid( oid );
                    setEditorDirty();
                }
                else
                {
                    oidText.setForeground( ViewUtils.COLOR_RED );
                    oidText.setToolTipText( Messages.getString( "AttributeTypeEditorOverviewPage.ElementOIDExists" ) ); //$NON-NLS-1$
                }
            }
            else
            {
                oidText.setForeground( ViewUtils.COLOR_RED );
                oidText.setToolTipText( Messages.getString( "AttributeTypeEditorOverviewPage.MalformedOID" ) ); //$NON-NLS-1$
            }
        }
    };

    /** The Verify listener for the OID Text Widget */
    private VerifyListener oidTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "([0-9]*\\.?)*" ) ) //$NON-NLS-1$
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

            SchemaEditorInput input = new SchemaEditorInput( schemaHandler
                .getSchema( modifiedAttributeType.getSchemaName() ) );
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
    private ISelectionChangedListener supComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Object selectedItem = ( ( StructuredSelection ) supComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof AttributeType )
            {
                AttributeType at = ( AttributeType ) selectedItem;
                List<String> names = at.getNames();
                if ( ( names != null ) && ( names.size() > 0 ) )
                {
                    modifiedAttributeType.setSuperiorOid( names.get( 0 ) );
                }
                else
                {
                    modifiedAttributeType.setSuperiorOid( at.getOid() );
                }
            }
            else if ( selectedItem instanceof NonExistingAttributeType )
            {
                NonExistingAttributeType neat = ( NonExistingAttributeType ) selectedItem;

                if ( NonExistingAttributeType.NONE.equals( neat.getName() ) )
                {
                    modifiedAttributeType.setSuperiorOid( null );
                }
                else
                {
                    modifiedAttributeType.setSuperiorOid( ( ( NonExistingAttributeType ) selectedItem ).getName() );
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
    private ISelectionChangedListener syntaxComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Object selectedItem = ( ( StructuredSelection ) syntaxComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof LdapSyntax )
            {
                modifiedAttributeType.setSyntaxOid( ( ( LdapSyntax ) selectedItem ).getOid() );
            }
            else if ( selectedItem instanceof NonExistingSyntax )
            {
                NonExistingSyntax nes = ( NonExistingSyntax ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nes.getDescription() ) )
                {
                    modifiedAttributeType.setSyntaxOid( null );
                }
                else
                {
                    modifiedAttributeType.setSyntaxOid( ( ( NonExistingSyntax ) selectedItem ).getDescription() );
                }
            }
            setEditorDirty();
        }
    };

    /** The Modify listener for the Syntax Length Text Widget */
    private ModifyListener syntaxLengthTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( syntaxLengthText.getText().length() <= 0 )
            {
                modifiedAttributeType.setSyntaxLength( -1 );
            }
            else
            {
                modifiedAttributeType.setSyntaxLength( Integer.parseInt( syntaxLengthText.getText() ) );
            }
            setEditorDirty();
        }
    };

    /** The Verify listener for the Syntax Length Text Widget */
    private VerifyListener syntaxLengthTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-9]*" ) ) //$NON-NLS-1$
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
            modifiedAttributeType.setSingleValued( singleValueCheckbox.getSelection() );
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
            modifiedAttributeType.setUserModifiable( !noUserModificationCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for the Equality Combo Widget */
    private ISelectionChangedListener equalityComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Object selectedItem = ( ( StructuredSelection ) equalityComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof MatchingRule )
            {
                modifiedAttributeType.setEqualityOid( ( ( MatchingRule ) selectedItem ).getName() );
            }
            else if ( selectedItem instanceof NonExistingMatchingRule )
            {
                NonExistingMatchingRule nemr = ( NonExistingMatchingRule ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nemr.getName() ) )
                {
                    modifiedAttributeType.setEqualityOid( null );
                }
                else
                {
                    modifiedAttributeType.setEqualityOid( ( ( NonExistingMatchingRule ) selectedItem ).getName() );
                }
            }
            setEditorDirty();
        }
    };

    /** The listener for the Ordering Combo Widget */
    private ISelectionChangedListener orderingComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Object selectedItem = ( ( StructuredSelection ) orderingComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof MatchingRule )
            {
                modifiedAttributeType.setOrderingOid( ( ( MatchingRule ) selectedItem ).getName() );
            }
            else if ( selectedItem instanceof NonExistingMatchingRule )
            {
                NonExistingMatchingRule nemr = ( NonExistingMatchingRule ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nemr.getName() ) )
                {
                    modifiedAttributeType.setOrderingOid( null );
                }
                else
                {
                    modifiedAttributeType.setOrderingOid( ( ( NonExistingMatchingRule ) selectedItem ).getName() );
                }
            }
            setEditorDirty();
        }
    };

    /** The listener for the Substring Combo Widget */
    private ISelectionChangedListener substringComboViewerListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            Object selectedItem = ( ( StructuredSelection ) substringComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof MatchingRule )
            {
                modifiedAttributeType.setSubstringOid( ( ( MatchingRule ) selectedItem ).getName() );
            }
            else if ( selectedItem instanceof NonExistingMatchingRule )
            {
                NonExistingMatchingRule nemr = ( NonExistingMatchingRule ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nemr.getName() ) )
                {
                    modifiedAttributeType.setSubstringOid( null );
                }
                else
                {
                    modifiedAttributeType.setSubstringOid( ( ( NonExistingMatchingRule ) selectedItem ).getName() );
                }
            }
            setEditorDirty();
        }
    };

    /** The filter listener for Mouse Wheel events */
    private Listener mouseWheelFilter = new Listener()
    {
        public void handleEvent( Event event )
        {
            // Hiding Mouse Wheel events for Combo widgets
            if ( event.widget instanceof Combo )
            {
                event.doit = false;
            }
        }
    };


    /**
     * Default constructor.
     * 
     * @param editor
     *      the associated editor
     */
    public AttributeTypeEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, Messages.getString( "AttributeTypeEditorOverviewPage.Overview" ) ); //$NON-NLS-1$
        schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaHandler.addListener( schemaHandlerListener );
    }


    /**
     * {@inheritDoc}
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        form.getBody().setLayout( layout );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        // Matching Rules Section
        createMatchingRulesSection( form.getBody(), toolkit );

        // Filling the UI with values from the attribute type
        fillInUiFields();

        // Listeners initialization
        addListeners();

        // Help Context for Dynamic Help
        PlatformUI.getWorkbench().getHelpSystem().setHelp( form,
            PluginConstants.PLUGIN_ID + "." + "attribute_type_editor" ); //$NON-NLS-1$ //$NON-NLS-2$
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
            .getString( "AttributeTypeEditorOverviewPage.SpecifyGeneralInformation" ) ); //$NON-NLS-1$
        section_general_information
            .setText( Messages.getString( "AttributeTypeEditorOverviewPage.GeneralInformation" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        client_general_information.setLayout( new GridLayout( 2, false ) );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Adding elements to the section

        // ALIASES Field
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        Composite aliasComposite = toolkit.createComposite( client_general_information );
        GridLayout aliasCompositeGridLayout = new GridLayout( 2, false );
        toolkit.paintBordersFor( aliasComposite );
        aliasCompositeGridLayout.marginHeight = 1;
        aliasCompositeGridLayout.marginWidth = 1;
        aliasComposite.setLayout( aliasCompositeGridLayout );
        aliasComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        aliasesText = toolkit.createText( aliasComposite, "" ); //$NON-NLS-1$
        aliasesText.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        aliasesButton = toolkit.createButton( aliasComposite, Messages
            .getString( "AttributeTypeEditorOverviewPage.EditAliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, 0, true, false ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        descriptionGridData.heightHint = 42;
        descriptionText.setLayoutData( descriptionGridData );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.Schema" ), SWT.WRAP ); //$NON-NLS-1$
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SUP Combo
        supLabel = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.SuperiorType" ), SWT.WRAP ); //$NON-NLS-1$
        supCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        supCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        supComboViewer = new ComboViewer( supCombo );
        supComboViewer.setContentProvider( new ATESuperiorComboContentProvider() );
        supComboViewer.setLabelProvider( new ATESuperiorComboLabelProvider() );

        // USAGE Combo
        toolkit.createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.Usage" ) ); //$NON-NLS-1$
        usageCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        usageCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        initUsageCombo();

        // SYNTAX Combo
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.Syntax" ) ); //$NON-NLS-1$
        syntaxCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        syntaxComboViewer = new ComboViewer( syntaxCombo );
        syntaxComboViewer.setContentProvider( new ATESyntaxComboContentProvider() );
        syntaxComboViewer.setLabelProvider( new ATESyntaxComboLabelProvider() );

        // SYNTAX LENGTH Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.SyntaxLength" ) ); //$NON-NLS-1$
        syntaxLengthText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        syntaxLengthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // PROPERTIES composite
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        Composite propertiesComposite = toolkit.createComposite( client_general_information );
        GridLayout propertiesCompositeGridLayout = new GridLayout( 2, true );
        propertiesCompositeGridLayout.horizontalSpacing = 0;
        propertiesCompositeGridLayout.verticalSpacing = 0;
        propertiesCompositeGridLayout.marginHeight = 0;
        propertiesCompositeGridLayout.marginWidth = 0;
        propertiesComposite.setLayout( propertiesCompositeGridLayout );
        propertiesComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // OBSOLETE Checkbox
        obsoleteCheckbox = toolkit.createButton( propertiesComposite, Messages
            .getString( "AttributeTypeEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SINGLE-VALUE Checkbox
        singleValueCheckbox = toolkit.createButton( propertiesComposite, Messages
            .getString( "AttributeTypeEditorOverviewPage.SingleValue" ), SWT.CHECK ); //$NON-NLS-1$
        singleValueCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // COLLECTIVE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        collectiveCheckbox = toolkit.createButton( propertiesComposite, Messages
            .getString( "AttributeTypeEditorOverviewPage.Collective" ), SWT.CHECK ); //$NON-NLS-1$
        collectiveCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // NO-USER-MODIFICATION Checkbox
        noUserModificationCheckbox = toolkit.createButton( propertiesComposite, "No-User-Modification", SWT.CHECK ); //$NON-NLS-1$
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
            .getString( "AttributeTypeEditorOverviewPage.SpecifyMatchingRules" ) ); //$NON-NLS-1$
        section_matching_rules.setText( Messages.getString( "AttributeTypeEditorOverviewPage.MatchingRules" ) ); //$NON-NLS-1$

        // Creating the layout of the section
        Composite client_matching_rules = toolkit.createComposite( section_matching_rules );
        GridLayout layout_matching_rules = new GridLayout();
        layout_matching_rules.numColumns = 2;
        client_matching_rules.setLayout( layout_matching_rules );
        toolkit.paintBordersFor( client_matching_rules );
        section_matching_rules.setClient( client_matching_rules );
        section_matching_rules.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // EQUALITY Combo
        toolkit.createLabel( client_matching_rules, Messages.getString( "AttributeTypeEditorOverviewPage.Equality" ) ); //$NON-NLS-1$
        equalityCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        equalityCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        equalityComboViewer = new ComboViewer( equalityCombo );
        equalityComboViewer.setContentProvider( new ATEMatchingRulesComboContentProvider() );
        equalityComboViewer.setLabelProvider( new ATEMatchingRulesComboLabelProvider() );

        // ORDERING Combo
        toolkit.createLabel( client_matching_rules, Messages.getString( "AttributeTypeEditorOverviewPage.Ordering" ) ); //$NON-NLS-1$
        orderingCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        orderingCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        orderingComboViewer = new ComboViewer( orderingCombo );
        orderingComboViewer.setContentProvider( new ATEMatchingRulesComboContentProvider() );
        orderingComboViewer.setLabelProvider( new ATEMatchingRulesComboLabelProvider() );

        // SUBSTRING Combo
        toolkit.createLabel( client_matching_rules, Messages.getString( "AttributeTypeEditorOverviewPage.Substring" ) ); //$NON-NLS-1$
        substringCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        substringCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        substringComboViewer = new ComboViewer( substringCombo );
        substringComboViewer.setContentProvider( new ATEMatchingRulesComboContentProvider() );
        substringComboViewer.setLabelProvider( new ATEMatchingRulesComboLabelProvider() );
    }


    /**
     * Initializes the Usage Combo.
     */
    private void initUsageCombo()
    {
        usageCombo.add( "Directory Operation", 0 ); //$NON-NLS-1$
        usageCombo.add( "Distributed Operation", 1 ); //$NON-NLS-1$
        usageCombo.add( "DSA Operation", 2 ); //$NON-NLS-1$
        usageCombo.add( "User Applications", 3 ); //$NON-NLS-1$
    }


    /**
     * Fills in the User Interface fields.
     */
    private void fillInUiFields()
    {
        // Getting the original and modified attribute types
        modifiedAttributeType = ( ( AttributeTypeEditor ) getEditor() ).getModifiedAttributeType();
        originalAttributeType = ( ( AttributeTypeEditor ) getEditor() ).getOriginalAttributeType();
        originalSchema = schemaHandler.getSchema( originalAttributeType.getSchemaName() );

        // ALIASES Label
        if ( ( modifiedAttributeType.getNames() != null ) && ( modifiedAttributeType.getNames().size() != 0 ) )
        {
            aliasesText.setText( ViewUtils.concateAliases( modifiedAttributeType.getNames() ) );
        }
        else
        {
            aliasesText.setText( "" ); //$NON-NLS-1$
        }

        // OID Field
        if ( modifiedAttributeType.getOid() != null )
        {
            oidText.setText( modifiedAttributeType.getOid() );
        }

        // SCHEMA Field
        schemaLabel.setText( modifiedAttributeType.getSchemaName() );

        // DESCRIPTION Field
        if ( modifiedAttributeType.getDescription() != null )
        {
            descriptionText.setText( modifiedAttributeType.getDescription() );
        }

        // SUP Combo
        fillSupCombo();

        // USAGE Combo
        fillInUsageCombo();

        // SYNTAX Combo
        fillSyntaxCombo();

        // SYNTAX LENGTH Field
        if ( modifiedAttributeType.getSyntaxLength() > 0 )
        {
            syntaxLengthText.setText( modifiedAttributeType.getSyntaxLength() + "" ); //$NON-NLS-1$
        }

        // OBSOLETE Checkbox
        obsoleteCheckbox.setSelection( modifiedAttributeType.isObsolete() );

        // SINGLE-VALUE Checkbox
        singleValueCheckbox.setSelection( modifiedAttributeType.isSingleValued() );

        // COLLECTIVE Checkbox
        collectiveCheckbox.setSelection( modifiedAttributeType.isCollective() );

        // NO-USER-MODIFICATION Checkbox
        noUserModificationCheckbox.setSelection( !modifiedAttributeType.isUserModifiable() );

        // EQUALITY Combo
        fillEqualityCombo();

        // ORDERING Combo
        fillOrderingCombo();

        // SUBSTRING Combo
        fillSubstringCombo();
    }


    /**
     * Fills the the Sup Combo with the correct value.
     */
    private void fillSupCombo()
    {
        supComboViewer.setInput( new ATESuperiorComboInput( originalAttributeType ) );

        String supAtName = modifiedAttributeType.getSuperiorOid();
        if ( supAtName == null )
        {
            supComboViewer.setSelection( new StructuredSelection( new NonExistingAttributeType(
                NonExistingAttributeType.NONE ) ), true );
        }
        else
        {
            AttributeType supAT = schemaHandler.getAttributeType( supAtName );
            if ( supAT != null )
            {
                supComboViewer.setSelection( new StructuredSelection( supAT ), true );
            }
            else
            {
                ATESuperiorComboInput input = ( ATESuperiorComboInput ) supComboViewer.getInput();
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
     * Fills the the Syntax Combo with the correct value.
     */
    private void fillSyntaxCombo()
    {
        syntaxComboViewer.setInput( new ATESyntaxComboInput() );

        String syntaxOID = modifiedAttributeType.getSyntaxOid();
        if ( syntaxOID == null )
        {
            syntaxComboViewer.setSelection( new StructuredSelection( new NonExistingSyntax( NonExistingSyntax.NONE ) ),
                true );
        }
        else
        {
            LdapSyntax syntax = schemaHandler.getSyntax( syntaxOID );
            if ( syntax != null )
            {
                syntaxComboViewer.setSelection( new StructuredSelection( syntax ), true );
            }
            else
            {
                ATESyntaxComboInput input = ( ATESyntaxComboInput ) syntaxComboViewer.getInput();
                NonExistingSyntax nes = new NonExistingSyntax( syntaxOID );
                if ( !input.getChildren().contains( nes ) )
                {
                    input.addChild( nes );
                }
                syntaxComboViewer.refresh();
                syntaxComboViewer.setSelection( new StructuredSelection( nes ), true );
            }
        }
    }


    /**
     * Fills the the Equality Combo with the correct value.
     */
    private void fillEqualityCombo()
    {
        equalityComboViewer.setInput( new ATEMatchingRulesComboInput() );

        String equalityName = modifiedAttributeType.getEqualityOid();
        if ( equalityName == null )
        {
            equalityComboViewer.setSelection( new StructuredSelection( new NonExistingMatchingRule(
                NonExistingMatchingRule.NONE ) ), true );
        }
        else
        {
            MatchingRule matchingRule = schemaHandler.getMatchingRule( equalityName );
            if ( matchingRule != null )
            {
                equalityComboViewer.setSelection( new StructuredSelection( matchingRule ), true );
            }
            else
            {
                ATEMatchingRulesComboInput input = ( ATEMatchingRulesComboInput ) equalityComboViewer.getInput();
                NonExistingMatchingRule nemr = new NonExistingMatchingRule( equalityName );
                if ( !input.getChildren().contains( nemr ) )
                {
                    input.addChild( nemr );
                }
                equalityComboViewer.refresh();
                equalityComboViewer.setSelection( new StructuredSelection( nemr ), true );
            }
        }
    }


    /**
     * Fills the the Ordering Combo with the correct value.
     */
    private void fillOrderingCombo()
    {
        orderingComboViewer.setInput( new ATEMatchingRulesComboInput() );

        String orderingName = modifiedAttributeType.getOrderingOid();
        if ( orderingName == null )
        {
            orderingComboViewer.setSelection( new StructuredSelection( new NonExistingMatchingRule(
                NonExistingMatchingRule.NONE ) ), true );
        }
        else
        {
            MatchingRule matchingRule = schemaHandler.getMatchingRule( orderingName );
            if ( matchingRule != null )
            {
                orderingComboViewer.setSelection( new StructuredSelection( matchingRule ), true );
            }
            else
            {
                ATEMatchingRulesComboInput input = ( ATEMatchingRulesComboInput ) orderingComboViewer.getInput();
                NonExistingMatchingRule nemr = new NonExistingMatchingRule( orderingName );
                if ( !input.getChildren().contains( nemr ) )
                {
                    input.addChild( nemr );
                }
                orderingComboViewer.refresh();
                orderingComboViewer.setSelection( new StructuredSelection( nemr ), true );
            }
        }

    }


    /**
     * Fills the the Substring Combo with the correct value.
     */
    private void fillSubstringCombo()
    {
        substringComboViewer.setInput( new ATEMatchingRulesComboInput() );

        String substringName = modifiedAttributeType.getSubstringOid();
        if ( substringName == null )
        {
            substringComboViewer.setSelection( new StructuredSelection( new NonExistingMatchingRule(
                NonExistingMatchingRule.NONE ) ), true );
        }
        else
        {
            MatchingRule matchingRule = schemaHandler.getMatchingRule( substringName );
            if ( matchingRule != null )
            {
                substringComboViewer.setSelection( new StructuredSelection( matchingRule ), true );
            }
            else
            {
                ATEMatchingRulesComboInput input = ( ATEMatchingRulesComboInput ) substringComboViewer.getInput();
                NonExistingMatchingRule nemr = new NonExistingMatchingRule( substringName );
                if ( !input.getChildren().contains( nemr ) )
                {
                    input.addChild( nemr );
                }
                substringComboViewer.refresh();
                substringComboViewer.setSelection( new StructuredSelection( nemr ), true );
            }
        }
    }


    /**
     * Adds listeners to UI fields
     */
    private void addListeners()
    {
        aliasesText.addModifyListener( aliasesTextModifyListener );
        aliasesButton.addSelectionListener( aliasesButtonListener );
        oidText.addModifyListener( oidTextModifyListener );
        oidText.addVerifyListener( oidTextVerifyListener );
        schemaLink.addHyperlinkListener( schemaLinkListener );
        descriptionText.addModifyListener( descriptionTextListener );
        supLabel.addHyperlinkListener( supLabelListener );
        supComboViewer.addSelectionChangedListener( supComboViewerListener );
        usageCombo.addModifyListener( usageComboListener );
        syntaxComboViewer.addSelectionChangedListener( syntaxComboViewerListener );
        syntaxLengthText.addModifyListener( syntaxLengthTextModifyListener );
        syntaxLengthText.addVerifyListener( syntaxLengthTextVerifyListener );
        obsoleteCheckbox.addSelectionListener( obsoleteCheckboxListener );
        singleValueCheckbox.addSelectionListener( singleValueCheckboxListener );
        collectiveCheckbox.addSelectionListener( collectiveCheckboxListener );
        noUserModificationCheckbox.addSelectionListener( noUserModificationCheckboxListener );
        equalityComboViewer.addSelectionChangedListener( equalityComboViewerListener );
        orderingComboViewer.addSelectionChangedListener( orderingComboViewerListener );
        substringComboViewer.addSelectionChangedListener( substringComboViewerListener );

        Display.getCurrent().addFilter( SWT.MouseWheel, mouseWheelFilter );
    }


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        aliasesText.removeModifyListener( aliasesTextModifyListener );
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        oidText.removeModifyListener( oidTextModifyListener );
        oidText.removeVerifyListener( oidTextVerifyListener );
        schemaLink.removeHyperlinkListener( schemaLinkListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        supLabel.removeHyperlinkListener( supLabelListener );
        supComboViewer.removeSelectionChangedListener( supComboViewerListener );
        usageCombo.removeModifyListener( usageComboListener );
        syntaxComboViewer.removeSelectionChangedListener( syntaxComboViewerListener );
        syntaxLengthText.removeModifyListener( syntaxLengthTextModifyListener );
        syntaxLengthText.removeVerifyListener( syntaxLengthTextVerifyListener );
        obsoleteCheckbox.removeSelectionListener( obsoleteCheckboxListener );
        singleValueCheckbox.removeSelectionListener( singleValueCheckboxListener );
        collectiveCheckbox.removeSelectionListener( collectiveCheckboxListener );
        noUserModificationCheckbox.removeSelectionListener( noUserModificationCheckboxListener );
        equalityComboViewer.removeSelectionChangedListener( equalityComboViewerListener );
        orderingComboViewer.removeSelectionChangedListener( orderingComboViewerListener );
        substringComboViewer.removeSelectionChangedListener( substringComboViewerListener );

        Display.getCurrent().removeFilter( SWT.MouseWheel, mouseWheelFilter );
    }


    /**
     * Sets the dirty state of the editor to dirty
     */
    private void setEditorDirty()
    {
        ( ( AttributeTypeEditor ) getEditor() ).setDirty( true );
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        removeListeners();

        schemaHandler.removeListener( schemaHandlerListener );

        super.dispose();
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
