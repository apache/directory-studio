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


import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandler;
import org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.dialogs.EditAliasesDialog;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingAttributeType;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingMatchingRule;
import org.apache.directory.studio.schemaeditor.view.editors.NonExistingSyntax;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditor;
import org.apache.directory.studio.schemaeditor.view.editors.schema.SchemaEditorInput;
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
public class AttributeTypeEditorOverviewPage extends FormPage
{
    /** The page ID*/
    public static final String ID = AttributeTypeEditor.ID + ".overviewPage"; //$NON-NLS-1$

    /** The original object class */
    private AttributeTypeImpl originalAttributeType;

    /** The modified object class */
    private AttributeTypeImpl modifiedAttributeType;

    /** The original schema */
    private Schema originalSchema;

    /** The SchemaHandler */
    private SchemaHandler schemaHandler;

    /** The SchemaHandler Listener */
    private SchemaHandlerListener schemaHandlerListener = new SchemaHandlerListener()
    {
        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeAdded( AttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeModified( AttributeTypeImpl at )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
         */
        public void attributeTypeRemoved( AttributeTypeImpl at )
        {
            if ( !at.equals( originalAttributeType ) )
            {
                refreshUI();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleAdded(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleAdded( MatchingRuleImpl mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleModified(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleModified( MatchingRuleImpl mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleRemoved(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
         */
        public void matchingRuleRemoved( MatchingRuleImpl mr )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassAdded( ObjectClassImpl oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassModified( ObjectClassImpl oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
         */
        public void objectClassRemoved( ObjectClassImpl oc )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaAdded( Schema schema )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.schemaeditor.model.Schema)
         */
        public void schemaRemoved( Schema schema )
        {
            if ( !schema.equals( originalSchema ) )
            {
                refreshUI();
            }
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxAdded(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxAdded( SyntaxImpl syntax )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxModified(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxModified( SyntaxImpl syntax )
        {
            refreshUI();
        }


        /* (non-Javadoc)
         * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxRemoved(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
         */
        public void syntaxRemoved( SyntaxImpl syntax )
        {
            refreshUI();
        }
    };

    // UI Fields
    private Label aliasesLabel;
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
    /** The listener for the Edit Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            EditAliasesDialog editDialog = new EditAliasesDialog( modifiedAttributeType.getNamesRef() );
            if ( editDialog.open() != Window.OK )
            {
                return;
            }
            if ( editDialog.isDirty() )
            {
                modifiedAttributeType.setNames( editDialog.getAliases() );
                if ( ( modifiedAttributeType.getNamesRef() != null )
                    && ( modifiedAttributeType.getNamesRef().length != 0 ) )
                {
                    aliasesLabel.setText( ViewUtils.concateAliases( modifiedAttributeType.getNamesRef() ) );
                }
                else
                {
                    aliasesLabel.setText( Messages.getString( "AttributeTypeEditorOverviewPage.None" ) );
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

            if ( OID.isOID( oid ) )
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
                    oidText.setToolTipText( Messages.getString( "AttributeTypeEditorOverviewPage.ElementOIDExists" ) );
                }
            }
            else
            {
                oidText.setForeground( ViewUtils.COLOR_RED );
                oidText.setToolTipText( Messages.getString( "AttributeTypeEditorOverviewPage.MalformedOID" ) );
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
                .getSchema( modifiedAttributeType.getSchema() ) );
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

            if ( selectedItem instanceof AttributeTypeImpl )
            {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

                AttributeTypeEditorInput input = new AttributeTypeEditorInput( ( AttributeTypeImpl ) selectedItem );
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

            if ( selectedItem instanceof AttributeTypeImpl )
            {
                AttributeTypeImpl at = ( AttributeTypeImpl ) selectedItem;
                String[] names = at.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    modifiedAttributeType.setSuperiorName( names[0] );
                }
                else
                {
                    modifiedAttributeType.setSuperiorName( at.getOid() );
                }
            }
            else if ( selectedItem instanceof NonExistingAttributeType )
            {
                NonExistingAttributeType neat = ( NonExistingAttributeType ) selectedItem;

                if ( NonExistingAttributeType.NONE.equals( neat.getName() ) )
                {
                    modifiedAttributeType.setSuperiorName( null );
                }
                else
                {
                    modifiedAttributeType.setSuperiorName( ( ( NonExistingAttributeType ) selectedItem ).getName() );
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
            Object selectedItem = ( ( StructuredSelection ) syntaxComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof SyntaxImpl )
            {
                modifiedAttributeType.setSyntaxOid( ( ( SyntaxImpl ) selectedItem ).getOid() );
            }
            else if ( selectedItem instanceof NonExistingSyntax )
            {
                NonExistingSyntax nes = ( NonExistingSyntax ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nes.getName() ) )
                {
                    modifiedAttributeType.setSyntaxOid( null );
                }
                else
                {
                    modifiedAttributeType.setSyntaxOid( ( ( NonExistingSyntax ) selectedItem ).getName() );
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
            modifiedAttributeType.setCanUserModify( !noUserModificationCheckbox.getSelection() );
            setEditorDirty();
        }
    };

    /** The listener for the Equality Combo Widget */
    private ModifyListener equalityComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) equalityComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof MatchingRuleImpl )
            {
                modifiedAttributeType.setEqualityName( ( ( MatchingRuleImpl ) selectedItem ).getName() );
            }
            else if ( selectedItem instanceof NonExistingMatchingRule )
            {
                NonExistingMatchingRule nemr = ( NonExistingMatchingRule ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nemr.getName() ) )
                {
                    modifiedAttributeType.setEqualityName( null );
                }
                else
                {
                    modifiedAttributeType.setEqualityName( ( ( NonExistingMatchingRule ) selectedItem ).getName() );
                }
            }
            setEditorDirty();
        }
    };

    /** The listener for the Ordering Combo Widget */
    private ModifyListener orderingComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) orderingComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof MatchingRuleImpl )
            {
                modifiedAttributeType.setOrderingName( ( ( MatchingRuleImpl ) selectedItem ).getName() );
            }
            else if ( selectedItem instanceof NonExistingMatchingRule )
            {
                NonExistingMatchingRule nemr = ( NonExistingMatchingRule ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nemr.getName() ) )
                {
                    modifiedAttributeType.setOrderingName( null );
                }
                else
                {
                    modifiedAttributeType.setOrderingName( ( ( NonExistingMatchingRule ) selectedItem ).getName() );
                }
            }
            setEditorDirty();
        }
    };

    /** The listener for the Substring Combo Widget */
    private ModifyListener substringComboListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Object selectedItem = ( ( StructuredSelection ) substringComboViewer.getSelection() ).getFirstElement();

            if ( selectedItem instanceof MatchingRuleImpl )
            {
                modifiedAttributeType.setSubstrName( ( ( MatchingRuleImpl ) selectedItem ).getName() );
            }
            else if ( selectedItem instanceof NonExistingMatchingRule )
            {
                NonExistingMatchingRule nemr = ( NonExistingMatchingRule ) selectedItem;

                if ( NonExistingMatchingRule.NONE.equals( nemr.getName() ) )
                {
                    modifiedAttributeType.setSubstrName( null );
                }
                else
                {
                    modifiedAttributeType.setSubstrName( ( ( NonExistingMatchingRule ) selectedItem ).getName() );
                }
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
    public AttributeTypeEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, Messages.getString( "AttributeTypeEditorOverviewPage.Overview" ) );
        schemaHandler = Activator.getDefault().getSchemaHandler();
        schemaHandler.addListener( schemaHandlerListener );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the original and modified attribute types
        modifiedAttributeType = ( ( AttributeTypeEditor ) getEditor() ).getModifiedAttributeType();
        originalAttributeType = ( ( AttributeTypeEditor ) getEditor() ).getOriginalAttributeType();
        originalSchema = schemaHandler.getSchema( originalAttributeType.getSchema() );

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        GridLayout layout = new GridLayout();
        form.getBody().setLayout( layout );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        // Matching Rules Section
        createMatchingRulesSection( form.getBody(), toolkit );

        //        // Enabling or disabling the fields
        //        setFieldsEditableState();

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
            .getString( "AttributeTypeEditorOverviewPage.SpecifyGeneralInformation" ) );
        section_general_information
            .setText( Messages.getString( "AttributeTypeEditorOverviewPage.GeneralInformation" ) );

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
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.Aliases" ) );
        aliasesLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        aliasesLabel.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 2, 1 ) );
        toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        aliasesButton = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.EditAliases" ), SWT.PUSH );
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.OID" ) );
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, 0, true, false, 2, 1 ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.Description" ) );
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        descriptionGridData.heightHint = 42;
        descriptionText.setLayoutData( descriptionGridData );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.Schema" ), SWT.WRAP );
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SUP Combo
        supLabel = toolkit.createHyperlink( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.SuperiorType" ), SWT.WRAP );
        supCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        supCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        supComboViewer = new ComboViewer( supCombo );
        supComboViewer.setContentProvider( new ATESuperiorComboContentProvider() );
        supComboViewer.setLabelProvider( new ATESuperiorComboLabelProvider() );

        // USAGE Combo
        toolkit.createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.Usage" ) );
        usageCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        usageCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        initUsageCombo();

        // SYNTAX Combo
        toolkit
            .createLabel( client_general_information, Messages.getString( "AttributeTypeEditorOverviewPage.Syntax" ) );
        syntaxCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        syntaxCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        syntaxComboViewer = new ComboViewer( syntaxCombo );
        syntaxComboViewer.setContentProvider( new ATESyntaxComboContentProvider() );
        syntaxComboViewer.setLabelProvider( new ATESyntaxComboLabelProvider() );

        // SYNTAX LENGTH Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.SyntaxLength" ) );
        syntaxLengthText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        syntaxLengthText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.Obsolete" ), SWT.CHECK );
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SINGLE-VALUE Checkbox
        singleValueCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.SingleValue" ), SWT.CHECK );
        singleValueCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // COLLECTIVE Checkbox
        toolkit.createLabel( client_general_information, "" ); // Filling the first column //$NON-NLS-1$
        collectiveCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "AttributeTypeEditorOverviewPage.Collective" ), SWT.CHECK );
        collectiveCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // NO-USER-MODIFICATION Checkbox
        noUserModificationCheckbox = toolkit.createButton( client_general_information,
            "No-User-Modification", SWT.CHECK ); //$NON-NLS-1$
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
            .getString( "AttributeTypeEditorOverviewPage.SpecifyMatchingRules" ) );
        section_matching_rules.setText( Messages.getString( "AttributeTypeEditorOverviewPage.MatchingRules" ) );

        // Creating the layout of the section
        Composite client_matching_rules = toolkit.createComposite( section_matching_rules );
        GridLayout layout_matching_rules = new GridLayout();
        layout_matching_rules.numColumns = 2;
        client_matching_rules.setLayout( layout_matching_rules );
        toolkit.paintBordersFor( client_matching_rules );
        section_matching_rules.setClient( client_matching_rules );
        section_matching_rules.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // EQUALITY Combo
        toolkit.createLabel( client_matching_rules, Messages.getString( "AttributeTypeEditorOverviewPage.Equality" ) );
        equalityCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        equalityCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        equalityComboViewer = new ComboViewer( equalityCombo );
        equalityComboViewer.setContentProvider( new ATEMatchingRulesComboContentProvider() );
        equalityComboViewer.setLabelProvider( new ATEMatchingRulesComboLabelProvider() );

        // ORDERING Combo
        toolkit.createLabel( client_matching_rules, Messages.getString( "AttributeTypeEditorOverviewPage.Ordering" ) );
        orderingCombo = new Combo( client_matching_rules, SWT.READ_ONLY | SWT.SINGLE );
        orderingCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        orderingComboViewer = new ComboViewer( orderingCombo );
        orderingComboViewer.setContentProvider( new ATEMatchingRulesComboContentProvider() );
        orderingComboViewer.setLabelProvider( new ATEMatchingRulesComboLabelProvider() );

        // SUBSTRING Combo
        toolkit.createLabel( client_matching_rules, Messages.getString( "AttributeTypeEditorOverviewPage.Substring" ) );
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
        // ALIASES Label
        if ( ( modifiedAttributeType.getNamesRef() != null ) && ( modifiedAttributeType.getNamesRef().length != 0 ) )
        {
            aliasesLabel.setText( ViewUtils.concateAliases( modifiedAttributeType.getNamesRef() ) );
        }
        else
        {
            aliasesLabel.setText( "(None)" ); //$NON-NLS-1$
        }

        // OID Field
        if ( modifiedAttributeType.getOid() != null )
        {
            oidText.setText( modifiedAttributeType.getOid() );
        }

        // SCHEMA Field
        schemaLabel.setText( modifiedAttributeType.getSchema() );

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
        if ( modifiedAttributeType.getLength() != -1 )
        {
            syntaxLengthText.setText( modifiedAttributeType.getLength() + "" ); //$NON-NLS-1$
        }

        // OBSOLETE Checkbox
        obsoleteCheckbox.setSelection( modifiedAttributeType.isObsolete() );

        // SINGLE-VALUE Checkbox
        singleValueCheckbox.setSelection( modifiedAttributeType.isSingleValue() );

        // COLLECTIVE Checkbox
        collectiveCheckbox.setSelection( modifiedAttributeType.isCollective() );

        // NO-USER-MODIFICATION Checkbox
        noUserModificationCheckbox.setSelection( !modifiedAttributeType.isCanUserModify() );

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

        String supAtName = modifiedAttributeType.getSuperiorName();
        if ( supAtName == null )
        {
            supComboViewer.setSelection( new StructuredSelection( new NonExistingAttributeType(
                NonExistingAttributeType.NONE ) ), true );
        }
        else
        {
            AttributeTypeImpl supAT = schemaHandler.getAttributeType( supAtName );
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
            SyntaxImpl syntax = schemaHandler.getSyntax( syntaxOID );
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

        String equalityName = modifiedAttributeType.getEqualityName();
        if ( equalityName == null )
        {
            equalityComboViewer.setSelection( new StructuredSelection( new NonExistingMatchingRule(
                NonExistingMatchingRule.NONE ) ), true );
        }
        else
        {
            MatchingRuleImpl matchingRule = schemaHandler.getMatchingRule( equalityName );
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

        String orderingName = modifiedAttributeType.getOrderingName();
        if ( orderingName == null )
        {
            orderingComboViewer.setSelection( new StructuredSelection( new NonExistingMatchingRule(
                NonExistingMatchingRule.NONE ) ), true );
        }
        else
        {
            MatchingRuleImpl matchingRule = schemaHandler.getMatchingRule( orderingName );
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

        String substringName = modifiedAttributeType.getSubstrName();
        if ( substringName == null )
        {
            substringComboViewer.setSelection( new StructuredSelection( new NonExistingMatchingRule(
                NonExistingMatchingRule.NONE ) ), true );
        }
        else
        {
            MatchingRuleImpl matchingRule = schemaHandler.getMatchingRule( substringName );
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
        aliasesButton.addSelectionListener( aliasesButtonListener );
        oidText.addModifyListener( oidTextModifyListener );
        oidText.addVerifyListener( oidTextVerifyListener );
        schemaLink.addHyperlinkListener( schemaLinkListener );
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


    /**
     * Removes listeners from UI fields
     */
    private void removeListeners()
    {
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        oidText.removeModifyListener( oidTextModifyListener );
        oidText.removeVerifyListener( oidTextVerifyListener );
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
     * @see org.eclipse.ui.forms.editor.FormPage#dispose()
     */
    public void dispose()
    {
        schemaHandler.removeListener( schemaHandlerListener );
        super.dispose();
    }
}
