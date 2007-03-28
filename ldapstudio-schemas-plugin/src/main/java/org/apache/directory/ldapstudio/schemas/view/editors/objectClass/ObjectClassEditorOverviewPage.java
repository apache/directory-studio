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

package org.apache.directory.ldapstudio.schemas.view.editors.objectClass;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.ldapstudio.schemas.model.AttributeType;
import org.apache.directory.ldapstudio.schemas.model.ObjectClass;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.directory.ldapstudio.schemas.model.SchemaPool;
import org.apache.directory.ldapstudio.schemas.view.ViewUtils;
import org.apache.directory.ldapstudio.schemas.view.dialogs.AttributeTypeSelectionDialog;
import org.apache.directory.ldapstudio.schemas.view.dialogs.EditAliasesDialog;
import org.apache.directory.ldapstudio.schemas.view.dialogs.ObjectClassSelectionDialog;
import org.apache.directory.ldapstudio.schemas.view.editors.NonExistingAttributeType;
import org.apache.directory.ldapstudio.schemas.view.editors.NonExistingObjectClass;
import org.apache.directory.ldapstudio.schemas.view.editors.attributeType.AttributeTypeEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.attributeType.AttributeTypeEditorInput;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditor;
import org.apache.directory.ldapstudio.schemas.view.editors.schema.SchemaEditorInput;
import org.apache.directory.shared.asn1.primitives.OID;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.Table;
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
 * This class is the Overview Page of the Object Class Editor
 */
public class ObjectClassEditorOverviewPage extends FormPage
{
    /** The page ID */
    public static final String ID = ObjectClassEditor.ID + "overviewPage";

    /** The page title*/
    public static final String TITLE = Messages.getString( "ObjectClassFormEditor.Overview" );

    /** The original object class */
    private ObjectClass originalObjectClass;

    /** The modified object class */
    private ObjectClass modifiedObjectClass;

    /** The Schema Pool */
    private SchemaPool schemaPool;

    // UI fields
    private Label aliasesLabel;
    private Button aliasesButton;
    private Text oidText;
    private Hyperlink schemaLink;
    private Label schemaLabel;
    private Text descriptionText;
    private Table superiorsTable;
    private TableViewer superiorsTableViewer;
    private Button addButtonSuperiorsTable;
    private Button removeButtonSuperiorsTable;
    private Combo classTypeCombo;
    private Button obsoleteCheckbox;
    private Table mandatoryAttributesTable;
    private TableViewer mandatoryAttributesTableViewer;
    private Button addButtonMandatoryTable;
    private Button removeButtonMandatoryTable;
    private Table optionalAttributesTable;
    private TableViewer optionalAttributesTableViewer;
    private Button addButtonOptionalTable;
    private Button removeButtonOptionalTable;

    // Listeners
    /** The listener for Aliases Button Widget */
    private SelectionAdapter aliasesButtonListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            EditAliasesDialog editDialog = new EditAliasesDialog( modifiedObjectClass.getNames() );
            if ( editDialog.open() != Window.OK )
            {
                return;
            }
            if ( editDialog.isDirty() )
            {
                modifiedObjectClass.setNames( editDialog.getAliases() );
                if ( ( modifiedObjectClass.getNames() != null ) && ( modifiedObjectClass.getNames().length != 0 ) )
                {
                    aliasesLabel.setText( ViewUtils.concateAliases( modifiedObjectClass.getNames() ) );
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
                if ( ( originalObjectClass.getOid().equals( oid ) ) || !( schemaPool.containsSchemaElement( oid ) ) )
                {
                    modifiedObjectClass.setOid( oid );
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

            SchemaEditorInput input = new SchemaEditorInput( modifiedObjectClass.getOriginatingSchema() );
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
            StructuredSelection selection = ( StructuredSelection ) mandatoryAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof AttributeType )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new AttributeTypeEditorInput( ( AttributeType ) selectedElement ),
                            AttributeTypeEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
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
            AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
            List<AttributeType> hiddenATs = new ArrayList<AttributeType>();
            for ( String must : modifiedObjectClass.getMust() )
            {
                AttributeType at = schemaPool.getAttributeType( must );
                if ( at != null )
                {
                    hiddenATs.add( at );
                }
            }
            for ( String may : modifiedObjectClass.getMay() )
            {
                AttributeType at = schemaPool.getAttributeType( may );
                if ( at != null )
                {
                    hiddenATs.add( at );
                }
            }
            dialog.setHiddenAttributeTypes( hiddenATs.toArray( new AttributeType[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            AttributeType at = dialog.getSelectedAttributeType();
            if ( at == null )
            {
                return;
            }

            List<String> newMusts = new ArrayList<String>();
            String[] musts = modifiedObjectClass.getMust();
            for ( String must : musts )
            {
                newMusts.add( must );
            }
            newMusts.add( at.getNames()[0] );
            modifiedObjectClass.setMust( newMusts.toArray( new String[0] ) );

            fillInMandatoryAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Mandatory Attributes section */
    private SelectionAdapter removeButtonMandatoryTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) mandatoryAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> newMusts = new ArrayList<String>();
                String[] musts = modifiedObjectClass.getMust();
                for ( String must : musts )
                {
                    newMusts.add( must );
                }

                if ( selectedElement instanceof AttributeType )
                {
                    for ( String name : ( ( AttributeType ) selectedElement ).getNames() )
                    {
                        newMusts.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingAttributeType )
                {
                    newMusts.remove( ( ( NonExistingAttributeType ) selectedElement ).getName() );
                }

                modifiedObjectClass.setMust( newMusts.toArray( new String[0] ) );

                fillInMandatoryAttributesTable();
                addButtonMandatoryTable.setFocus();
                removeButtonMandatoryTable.setEnabled( mandatoryAttributesTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The listener for Optional Attributes Table Widget */
    private MouseListener optionalAttributesTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) optionalAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof AttributeType )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new AttributeTypeEditorInput( ( AttributeType ) selectedElement ),
                            AttributeTypeEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
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
            AttributeTypeSelectionDialog dialog = new AttributeTypeSelectionDialog();
            List<AttributeType> hiddenATs = new ArrayList<AttributeType>();
            for ( String must : modifiedObjectClass.getMust() )
            {
                AttributeType at = schemaPool.getAttributeType( must );
                if ( at != null )
                {
                    hiddenATs.add( at );
                }
            }
            for ( String may : modifiedObjectClass.getMay() )
            {
                AttributeType at = schemaPool.getAttributeType( may );
                if ( at != null )
                {
                    hiddenATs.add( at );
                }
            }
            dialog.setHiddenAttributeTypes( hiddenATs.toArray( new AttributeType[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            AttributeType at = dialog.getSelectedAttributeType();
            if ( at == null )
            {
                return;
            }

            List<String> newMays = new ArrayList<String>();
            String[] mays = modifiedObjectClass.getMay();
            for ( String may : mays )
            {
                newMays.add( may );
            }
            newMays.add( at.getNames()[0] );
            modifiedObjectClass.setMay( newMays.toArray( new String[0] ) );

            fillInOptionalAttributesTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Optional Attributes section */
    private SelectionAdapter removeButtonOptionalTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) optionalAttributesTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> newMays = new ArrayList<String>();
                String[] mays = modifiedObjectClass.getMay();
                for ( String may : mays )
                {
                    newMays.add( may );
                }

                if ( selectedElement instanceof AttributeType )
                {
                    for ( String name : ( ( AttributeType ) selectedElement ).getNames() )
                    {
                        newMays.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingAttributeType )
                {
                    newMays.remove( ( ( NonExistingAttributeType ) selectedElement ).getName() );
                }

                modifiedObjectClass.setMay( newMays.toArray( new String[0] ) );

                fillInOptionalAttributesTable();
                addButtonOptionalTable.setFocus();
                removeButtonOptionalTable.setEnabled( optionalAttributesTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };

    /** The listener for Superiors Table Widget */
    private MouseListener superiorsTableListener = new MouseAdapter()
    {
        public void mouseDoubleClick( MouseEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                if ( selectedElement instanceof ObjectClass )
                {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    try
                    {
                        page.openEditor( new ObjectClassEditorInput( ( ObjectClass ) selectedElement ),
                            ObjectClassEditor.ID );
                    }
                    catch ( PartInitException exception )
                    {
                        Logger.getLogger( ObjectClassEditorOverviewPage.class ).debug( "error when opening the editor" ); //$NON-NLS-1$
                    }
                }
            }
        }


        public void mouseUp( MouseEvent e )
        {
            if ( modifiedObjectClass.getOriginatingSchema().type != Schema.SchemaType.coreSchema )
            {
                removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelection().length != 0 );
            }
        }
    };

    /** The listener for Add Button Widget of the Superiors Table */
    private SelectionAdapter addButtonSuperiorsTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            ObjectClassSelectionDialog dialog = new ObjectClassSelectionDialog();
            List<ObjectClass> hiddenOCs = new ArrayList<ObjectClass>();
            for ( String sup : modifiedObjectClass.getSuperiors() )
            {
                ObjectClass oc = schemaPool.getObjectClass( sup );
                if ( oc != null )
                {
                    hiddenOCs.add( oc );
                }
            }
            hiddenOCs.add( originalObjectClass );
            dialog.setHiddenObjectClasses( hiddenOCs.toArray( new ObjectClass[0] ) );

            if ( dialog.open() != Window.OK )
            {
                return;
            }

            ObjectClass oc = dialog.getSelectedObjectClass();
            if ( oc == null )
            {
                return;
            }

            List<String> superiors = new ArrayList<String>();
            String[] sups = modifiedObjectClass.getSuperiors();
            for ( String sup : sups )
            {
                superiors.add( sup );
            }
            superiors.add( oc.getNames()[0] );
            modifiedObjectClass.setSuperiors( superiors.toArray( new String[0] ) );

            fillInSuperiorsTable();
            setEditorDirty();
        }
    };

    /** The listener for Remove Button Widget of the Superiors Table */
    private SelectionAdapter removeButtonSuperiorsTableListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) superiorsTableViewer.getSelection();
            if ( selection.isEmpty() )
            {
                return;
            }

            Object selectedElement = selection.getFirstElement();
            if ( selectedElement != null )
            {
                List<String> superiors = new ArrayList<String>();
                String[] sups = modifiedObjectClass.getSuperiors();
                for ( String sup : sups )
                {
                    superiors.add( sup );
                }

                if ( selectedElement instanceof ObjectClass )
                {
                    for ( String name : ( ( ObjectClass ) selectedElement ).getNames() )
                    {
                        superiors.remove( name );
                    }
                }
                else if ( selectedElement instanceof NonExistingObjectClass )
                {
                    superiors.remove( ( ( NonExistingObjectClass ) selectedElement ).getName() );
                }

                modifiedObjectClass.setSuperiors( superiors.toArray( new String[0] ) );

                fillInSuperiorsTable();
                addButtonSuperiorsTable.setFocus();
                removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelection().length != 0 );
                setEditorDirty();
            }
        }
    };


    /**
     * Default constructor
     * @param editor
     *      the associated editor
     */
    public ObjectClassEditorOverviewPage( FormEditor editor )
    {
        super( editor, ID, TITLE );
        schemaPool = SchemaPool.getInstance();
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createFormContent( IManagedForm managedForm )
    {
        // Getting the original and modified object classes
        modifiedObjectClass = ( ( ObjectClassEditor ) getEditor() ).getModifiedObjectClass();
        originalObjectClass = ( ( ObjectClassEditor ) getEditor() ).getOriginalObjectClass();

        // Creating the base UI
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        form.getBody().setLayout( new GridLayout() );

        // General Information Section
        createGeneralInformationSection( form.getBody(), toolkit );

        Composite bottomComposite = toolkit.createComposite( form.getBody() );
        bottomComposite.setLayout( new GridLayout( 2, true ) );
        bottomComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Mandatory Attributes Section
        createMandatoryAttributesSection( bottomComposite, toolkit );

        // Optionnal Attributes Section
        createOptionalAttributesSection( bottomComposite, toolkit );

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
        section_general_information.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, true ) );

        // Creating the layout of the section
        Composite client_general_information = toolkit.createComposite( section_general_information );
        client_general_information.setLayout( new GridLayout( 3, false ) );
        toolkit.paintBordersFor( client_general_information );
        section_general_information.setClient( client_general_information );

        // ALIASES Button
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Aliases" ) ); //$NON-NLS-1$
        aliasesLabel = toolkit.createLabel( client_general_information, "" );
        aliasesLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        toolkit.createLabel( client_general_information, "" );
        aliasesButton = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Edit_Aliases" ), SWT.PUSH ); //$NON-NLS-1$
        aliasesButton.setLayoutData( new GridData( SWT.NONE, SWT.NONE, false, false, 2, 1 ) );

        // OID Field
        toolkit.createLabel( client_general_information, Messages.getString( "ObjectClassFormEditorOverviewPage.OID" ) ); //$NON-NLS-1$
        oidText = toolkit.createText( client_general_information, "" ); //$NON-NLS-1$
        oidText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // SCHEMA Field
        schemaLink = toolkit.createHyperlink( client_general_information, "Schema:", SWT.WRAP );
        schemaLabel = toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        schemaLabel.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );

        // DESCRIPTION Field
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Description" ) ); //$NON-NLS-1$
        descriptionText = toolkit.createText( client_general_information, "", SWT.MULTI | SWT.V_SCROLL ); //$NON-NLS-1$
        GridData descriptionGridData = new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 );
        descriptionGridData.heightHint = 42;
        descriptionText.setLayoutData( descriptionGridData );

        // SUPERIORS Table
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Superior_classes" ) ); //$NON-NLS-1$
        superiorsTable = toolkit.createTable( client_general_information, SWT.SINGLE | SWT.V_SCROLL );
        GridData gridData = new GridData( SWT.FILL, SWT.NONE, true, false );
        gridData.heightHint = 45;
        gridData.minimumHeight = 45;
        superiorsTable.setLayoutData( gridData );
        superiorsTableViewer = new TableViewer( superiorsTable );
        superiorsTableViewer.setContentProvider( new ObjectClassEditorSuperiorsTableContentProvider() );
        superiorsTableViewer.setLabelProvider( new ObjectClassEditorSuperiorsTableLabelProvider() );
        Composite superiorsButtonComposite = toolkit.createComposite( client_general_information );
        superiorsButtonComposite.setLayout( new GridLayout() );
        addButtonSuperiorsTable = toolkit.createButton( superiorsButtonComposite, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Add..." ), SWT.PUSH );
        addButtonSuperiorsTable.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );
        removeButtonSuperiorsTable = toolkit.createButton( superiorsButtonComposite, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Remove" ), SWT.PUSH );
        removeButtonSuperiorsTable.setLayoutData( new GridData( SWT.FILL, SWT.NONE, false, false ) );

        // CLASS TYPE Combo
        toolkit.createLabel( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Class_type" ) ); //$NON-NLS-1$
        classTypeCombo = new Combo( client_general_information, SWT.READ_ONLY | SWT.SINGLE );
        classTypeCombo.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        initClassTypeCombo();

        // OBSOLETE Checkbox
        toolkit.createLabel( client_general_information, "" ); //$NON-NLS-1$
        obsoleteCheckbox = toolkit.createButton( client_general_information, Messages
            .getString( "ObjectClassFormEditorOverviewPage.Obsolete" ), SWT.CHECK ); //$NON-NLS-1$
        obsoleteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
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
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        mandatoryAttributesTable = toolkit.createTable( client, SWT.NULL );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.verticalSpan = 2;
        gd.heightHint = 108;
        mandatoryAttributesTable.setLayoutData( gd );
        mandatoryAttributesTableViewer = new TableViewer( mandatoryAttributesTable );
        mandatoryAttributesTableViewer.setContentProvider( new ObjectClassEditorAttributesTableContentProvider() );
        mandatoryAttributesTableViewer.setLabelProvider( new ObjectClassEditorAttributesTableLabelProvider() );

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
        GridData gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        section.setLayoutData( gd );
        toolkit.paintBordersFor( client );

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        client.setLayout( layout );

        optionalAttributesTable = toolkit.createTable( client, SWT.SINGLE | SWT.V_SCROLL );
        gd = new GridData( SWT.FILL, SWT.NONE, true, false );
        gd.verticalSpan = 2;
        gd.heightHint = 108;
        optionalAttributesTable.setLayoutData( gd );
        optionalAttributesTableViewer = new TableViewer( optionalAttributesTable );
        optionalAttributesTableViewer.setContentProvider( new ObjectClassEditorAttributesTableContentProvider() );
        optionalAttributesTableViewer.setLabelProvider( new ObjectClassEditorAttributesTableLabelProvider() );

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
     * Initializes the UI fields from the input.
     */
    private void fillInUiFields()
    {
        // ALIASES Label
        if ( ( modifiedObjectClass.getNames() != null ) && ( modifiedObjectClass.getNames().length != 0 ) )
        {
            aliasesLabel.setText( ViewUtils.concateAliases( modifiedObjectClass.getNames() ) );
        }
        else
        {
            aliasesLabel.setText( Messages.getString( "AttributeTypeFormEditorOverviewPage.(None)" ) );
        }

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

        // SUPERIORS Table
        fillInSuperiorsTable();

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
     * Fills in the Superiors Table.
     */
    private void fillInSuperiorsTable()
    {
        superiorsTableViewer.setInput( modifiedObjectClass.getSuperiors() );
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
        mandatoryAttributesTableViewer.setInput( modifiedObjectClass.getMust() );
    }


    /**
     * Initializes the Optional Attributes Table
     */
    private void fillInOptionalAttributesTable()
    {
        optionalAttributesTableViewer.setInput( modifiedObjectClass.getMay() );
    }


    /**
     * Enalbes/Disables the UI fields
     */
    private void setFieldsEditableState()
    {
        if ( modifiedObjectClass.getOriginatingSchema().type == Schema.SchemaType.coreSchema )
        {
            // If the object class is in a core-schema file, we disable editing
            aliasesButton.setEnabled( false );
            oidText.setEditable( false );
            descriptionText.setEditable( false );
            addButtonSuperiorsTable.setEnabled( false );
            classTypeCombo.setEnabled( false );
            obsoleteCheckbox.setEnabled( false );
            addButtonMandatoryTable.setEnabled( false );
            removeButtonMandatoryTable.setEnabled( false );
            addButtonOptionalTable.setEnabled( false );
            removeButtonOptionalTable.setEnabled( false );
        }
        else
        {
            removeButtonSuperiorsTable.setEnabled( superiorsTable.getSelectionIndex() != -1 );
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
            aliasesButton.addSelectionListener( aliasesButtonListener );
            oidText.addModifyListener( oidTextModifyListener );
            oidText.addVerifyListener( oidTextVerifyListener );
            descriptionText.addModifyListener( descriptionTextListener );
            addButtonSuperiorsTable.addSelectionListener( addButtonSuperiorsTableListener );
            removeButtonSuperiorsTable.addSelectionListener( removeButtonSuperiorsTableListener );
            classTypeCombo.addModifyListener( classTypeListener );
            obsoleteCheckbox.addSelectionListener( obsoleteListener );
            addButtonMandatoryTable.addSelectionListener( addButtonMandatoryTableListener );
            removeButtonMandatoryTable.addSelectionListener( removeButtonMandatoryTableListener );
            addButtonOptionalTable.addSelectionListener( addButtonOptionalTableListener );
            removeButtonOptionalTable.addSelectionListener( removeButtonOptionalTableListener );
        }

        schemaLink.addHyperlinkListener( schemaLinkListener );

        // This listener needs to be outside of the 'if' so that attribute type editor can be opened from any object class (in a core or a user schema)
        superiorsTable.addMouseListener( superiorsTableListener );

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
        aliasesButton.removeSelectionListener( aliasesButtonListener );
        oidText.removeModifyListener( oidTextModifyListener );
        oidText.removeVerifyListener( oidTextVerifyListener );
        schemaLink.removeHyperlinkListener( schemaLinkListener );
        descriptionText.removeModifyListener( descriptionTextListener );
        superiorsTable.removeMouseListener( superiorsTableListener );
        addButtonSuperiorsTable.removeSelectionListener( addButtonSuperiorsTableListener );
        removeButtonSuperiorsTable.removeSelectionListener( removeButtonSuperiorsTableListener );
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
        ( ( ObjectClassEditor ) getEditor() ).setDirty( true );
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
