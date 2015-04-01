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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import org.apache.directory.studio.openldap.common.ui.dialogs.AttributeDialog;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.model.OlcDbIndex;
import org.apache.directory.studio.openldap.config.model.OlcDbIndexTypeEnum;


/**
 * The IndexDialog is used to edit an index configuration.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IndexDialog extends Dialog
{
    /** The index */
    private OlcDbIndex index;

    /** The new index */
    private OlcDbIndex newIndex;

    /** The connection */
    private IBrowserConnection browserConnection;

    /** The attributes list */
    private List<String> attributes = new ArrayList<String>();

    // UI widgets
    private Button okButton;
    private Button attributesCheckbox;
    private Table table;
    private TableViewer tableViewer;
    private Button addButton;
    private Button deleteButton;
    private Button defaultCheckbox;
    private Button presCheckbox;
    private Button eqCheckbox;
    private Button approxCheckbox;
    private Button subCheckbox;
    private Button noLangCheckbox;
    private Button noSubtypesCheckbox;
    private Button subInitialCheckbox;
    private Button subAnyCheckbox;
    private Button subFinalCheckbox;

    // Listeners
    private SelectionListener attributesCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            table.setEnabled( true );
            addButton.setEnabled( true );
            deleteButton.setEnabled( !tableViewer.getSelection().isEmpty() );
            checkAndUpdateOkButtonEnableState();
        }
    };
    private ISelectionChangedListener tableViewerSelectionChangedListener = new ISelectionChangedListener()
    {
        public void selectionChanged( SelectionChangedEvent event )
        {
            deleteButton.setEnabled( !tableViewer.getSelection().isEmpty() );
        }
    };
    private SelectionListener addButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            AttributeDialog dialog = new AttributeDialog( addButton.getShell(), browserConnection );
            if ( dialog.open() == AttributeDialog.OK )
            {
                String attribute = dialog.getAttribute();

                if ( !attributes.contains( attribute ) )
                {
                    attributes.add( attribute );
                    tableViewer.refresh();
                    tableViewer.setSelection( new StructuredSelection( attribute ) );
                    checkAndUpdateOkButtonEnableState();
                }
            }
        }
    };
    private SelectionListener deleteButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            StructuredSelection selection = ( StructuredSelection ) tableViewer.getSelection();

            if ( !selection.isEmpty() )
            {
                String selectedAttribute = ( String ) selection.getFirstElement();

                attributes.remove( selectedAttribute );
                tableViewer.refresh();
                checkAndUpdateOkButtonEnableState();
            }
        }
    };
    private SelectionListener defaultCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            disableAttributesTableAndButtons();
            checkAndUpdateOkButtonEnableState();
        }
    };
    private SelectionListener subCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            setSelectionForSubCheckboxes( subCheckbox.getSelection() );
            checkAndUpdateOkButtonEnableState();
        }
    };
    private SelectionListener checkOkButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            checkAndUpdateOkButtonEnableState();
        };
    };
    private SelectionListener checkSubCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            checkAndUpdateSubCheckboxSelectionState();
            checkAndUpdateOkButtonEnableState();
        }
    };


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param index the index
     * @param browserConnection the connection
     */
    public IndexDialog( Shell parentShell, OlcDbIndex index, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.index = index;
        this.browserConnection = browserConnection;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Index" );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        checkAndUpdateOkButtonEnableState();
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        // Creating the new index
        newIndex = new OlcDbIndex();

        // Default
        if ( defaultCheckbox.getSelection() )
        {
            newIndex.setDefault( true );
        }
        else
        {
            // Attributes
            if ( attributes.size() > 0 )
            {
                for ( String attribute : attributes )
                {
                    newIndex.addAttribute( attribute );
                }
            }
        }

        // Index types
        if ( presCheckbox.getSelection() )
        {
            newIndex.addIndexType( OlcDbIndexTypeEnum.PRES );
        }
        if ( eqCheckbox.getSelection() )
        {
            newIndex.addIndexType( OlcDbIndexTypeEnum.EQ );
        }
        if ( approxCheckbox.getSelection() )
        {
            newIndex.addIndexType( OlcDbIndexTypeEnum.APPROX );
        }
        if ( ( subCheckbox.getSelection() ) && ( !subCheckbox.getGrayed() ) )
        {
            newIndex.addIndexType( OlcDbIndexTypeEnum.SUB );
        }
        else
        {
            if ( subInitialCheckbox.getSelection() )
            {
                newIndex.addIndexType( OlcDbIndexTypeEnum.SUBINITIAL );
            }
            if ( subAnyCheckbox.getSelection() )
            {
                newIndex.addIndexType( OlcDbIndexTypeEnum.SUBANY );
            }
            if ( subFinalCheckbox.getSelection() )
            {
                newIndex.addIndexType( OlcDbIndexTypeEnum.SUBFINAL );
            }
        }
        if ( noLangCheckbox.getSelection() )
        {
            newIndex.addIndexType( OlcDbIndexTypeEnum.NOLANG );
        }
        if ( noSubtypesCheckbox.getSelection() )
        {
            newIndex.addIndexType( OlcDbIndexTypeEnum.NOSUBTYPES );
        }

        super.okPressed();
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createAttributesGroup( composite );
        createIndicesGroup( composite );

        initFromIndex();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Creates the attributes group.
     *
     * @param parent the parent composite
     */
    private void createAttributesGroup( Composite parent )
    {
        // Attributes Group
        Group attributesGroup = BaseWidgetUtils.createGroup( parent, "Attributes", 1 );
        GridLayout attributesGroupGridLayout = new GridLayout( 2, false );
        attributesGroup.setLayout( attributesGroupGridLayout );
        attributesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Attributes Checkbox
        attributesCheckbox = BaseWidgetUtils.createRadiobutton( attributesGroup, "", 1 );
        attributesCheckbox.setLayoutData( new GridData( SWT.NONE, SWT.CENTER, false, false ) );
        attributesCheckbox.setSelection( true );
        attributesCheckbox.addSelectionListener( attributesCheckboxSelectionListener );

        Composite attributesComposite = new Composite( attributesGroup, SWT.NONE );
        GridLayout attributesCompositeGridLayout = new GridLayout( 2, false );
        attributesCompositeGridLayout.marginHeight = attributesCompositeGridLayout.marginWidth = 0;
        attributesCompositeGridLayout.verticalSpacing = attributesCompositeGridLayout.horizontalSpacing = 0;
        attributesComposite.setLayout( attributesCompositeGridLayout );
        attributesComposite.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Table and Table Viewer
        table = new Table( attributesComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true, 1, 3 );
        gd.heightHint = 20;
        gd.widthHint = 100;
        table.setLayoutData( gd );
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new ArrayContentProvider() );
        tableViewer.setLabelProvider( new LabelProvider()
        {
            public Image getImage( Object element )
            {
                return OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_ATTRIBUTE );
            }
        } );
        tableViewer.setInput( attributes );
        tableViewer.addSelectionChangedListener( tableViewerSelectionChangedListener );

        // Add Button
        addButton = BaseWidgetUtils.createButton( attributesComposite, "Add...", 1 );
        addButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        addButton.addSelectionListener( addButtonSelectionListener );

        // Delete Button
        deleteButton = BaseWidgetUtils.createButton( attributesComposite, "Delete", 1 );
        deleteButton.setEnabled( false );
        deleteButton.setLayoutData( new GridData( SWT.FILL, SWT.BEGINNING, false, false ) );
        deleteButton.addSelectionListener( deleteButtonSelectionListener );

        // Attributes Checkbox
        defaultCheckbox = BaseWidgetUtils.createRadiobutton( attributesGroup, "  Default", 2 );
        defaultCheckbox.addSelectionListener( defaultCheckboxSelectionListener );
    }


    /**
     * Creates the indices group.
     *
     * @param parent the parent composite
     */
    private void createIndicesGroup( Composite parent )
    {
        // Indices Group
        Group indicesGroup = BaseWidgetUtils.createGroup( parent, "Indices", 1 );
        GridLayout indicesGroupGridLayout = new GridLayout( 3, true );
        indicesGroupGridLayout.verticalSpacing = indicesGroupGridLayout.horizontalSpacing = 0;
        indicesGroup.setLayout( indicesGroupGridLayout );
        indicesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Pres Checkbox
        presCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "pres", 1 );
        presCheckbox.addSelectionListener( checkOkButtonSelectionListener );

        // Eq Checkbox
        eqCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "eq", 1 );
        eqCheckbox.addSelectionListener( checkOkButtonSelectionListener );

        // Approx Checkbox
        approxCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "approx", 1 );
        approxCheckbox.addSelectionListener( checkOkButtonSelectionListener );

        // Sub Checkbox
        subCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "sub", 1 );
        subCheckbox.addSelectionListener( subCheckboxSelectionListener );

        // NoLang Checkbox
        noLangCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "nolang", 1 );
        noLangCheckbox.addSelectionListener( checkOkButtonSelectionListener );

        // NoSybtypes Checkbox
        noSubtypesCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "nosubtypes", 1 );
        noSubtypesCheckbox.addSelectionListener( checkOkButtonSelectionListener );

        // Sub Composite
        Composite subComposite = new Composite( indicesGroup, SWT.NONE );
        GridLayout subCompositeGridLayout = new GridLayout( 2, false );
        subCompositeGridLayout.marginHeight = subCompositeGridLayout.marginWidth = 0;
        subCompositeGridLayout.verticalSpacing = subCompositeGridLayout.horizontalSpacing = 0;
        subComposite.setLayout( subCompositeGridLayout );

        // SubInitial Checkbox
        BaseWidgetUtils.createRadioIndent( subComposite, 1 );
        subInitialCheckbox = BaseWidgetUtils.createCheckbox( subComposite, "subinitial", 1 );
        subInitialCheckbox.addSelectionListener( checkSubCheckboxSelectionListener );

        // SubAny Checkbox
        BaseWidgetUtils.createRadioIndent( subComposite, 1 );
        subAnyCheckbox = BaseWidgetUtils.createCheckbox( subComposite, "subany", 1 );
        subAnyCheckbox.addSelectionListener( checkSubCheckboxSelectionListener );

        // SubFinal Checkbox
        BaseWidgetUtils.createRadioIndent( subComposite, 1 );
        subFinalCheckbox = BaseWidgetUtils.createCheckbox( subComposite, "subfinal", 1 );
        subFinalCheckbox.addSelectionListener( checkSubCheckboxSelectionListener );
    }


    /**
     * Disables the attributes table and buttons.
     */
    private void disableAttributesTableAndButtons()
    {
        table.setEnabled( false );
        addButton.setEnabled( false );
        deleteButton.setEnabled( false );
    }


    /**
     * Sets the selection for sub checkboxes.
     *
     * @param selection the selection
     */
    private void setSelectionForSubCheckboxes( boolean selection )
    {
        subCheckbox.setGrayed( false );
        subInitialCheckbox.setSelection( selection );
        subAnyCheckbox.setSelection( selection );
        subFinalCheckbox.setSelection( selection );
    }


    /**
     * Verifies and updates the selection state for the 'sub' checkbox.
     */
    private void checkAndUpdateSubCheckboxSelectionState()
    {
        boolean atLeastOneSelected = subInitialCheckbox.getSelection()
            || subAnyCheckbox.getSelection() || subFinalCheckbox.getSelection();
        boolean allSelected = subInitialCheckbox.getSelection()
            && subAnyCheckbox.getSelection() && subFinalCheckbox.getSelection();
        subCheckbox.setGrayed( atLeastOneSelected && !allSelected );
        subCheckbox.setSelection( atLeastOneSelected );
    }


    /**
     * Inits the UI from the index
     */
    private void initFromIndex()
    {
        if ( index != null )
        {
            // Attributes
            List<String> attributes = index.getAttributes();

            if ( ( attributes != null ) && ( attributes.size() > 0 ) )
            {
                this.attributes.addAll( attributes );
                tableViewer.refresh();
            }

            // Default
            if ( index.isDefault() )
            {
                attributesCheckbox.setSelection( false );
                disableAttributesTableAndButtons();
                defaultCheckbox.setSelection( true );
            }

            // Index types
            List<OlcDbIndexTypeEnum> indexTypes = index.getIndexTypes();

            if ( ( indexTypes != null ) && ( indexTypes.size() > 0 ) )
            {
                presCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.PRES ) );
                eqCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.EQ ) );
                approxCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.APPROX ) );
                if ( indexTypes.contains( OlcDbIndexTypeEnum.SUB ) )
                {
                    subCheckbox.setSelection( true );
                    setSelectionForSubCheckboxes( indexTypes.contains( OlcDbIndexTypeEnum.SUB ) );
                }
                else
                {
                    subInitialCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.SUBINITIAL ) );
                    subAnyCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.SUBANY ) );
                    subFinalCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.SUBFINAL ) );
                    checkAndUpdateSubCheckboxSelectionState();
                }
                noLangCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.NOLANG ) );
                noSubtypesCheckbox.setSelection( indexTypes.contains( OlcDbIndexTypeEnum.NOSUBTYPES ) );
            }
        }
    }


    /**
     * Checks and updates the OK button 'enable' state.
     */
    private void checkAndUpdateOkButtonEnableState()
    {
        boolean enableOkButton = true;

        if ( defaultCheckbox.getSelection() )
        {
            enableOkButton = presCheckbox.getSelection() || eqCheckbox.getSelection() || approxCheckbox.getSelection()
                || subCheckbox.getSelection() || subInitialCheckbox.getSelection() || subAnyCheckbox.getSelection()
                || subFinalCheckbox.getSelection() || noLangCheckbox.getSelection()
                || noSubtypesCheckbox.getSelection();
        }
        else
        {
            enableOkButton = attributes.size() > 0;
        }

        okButton.setEnabled( enableOkButton );
    }


    /**
     * Gets the new index.
     *
     * @return the new index
     */
    public OlcDbIndex getNewIndex()
    {
        return newIndex;
    }
}
