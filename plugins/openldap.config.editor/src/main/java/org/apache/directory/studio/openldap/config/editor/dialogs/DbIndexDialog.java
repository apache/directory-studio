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
import java.util.Set;

import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.apache.directory.studio.openldap.common.ui.model.DbIndexTypeEnum;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPluginConstants;
import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.StringValueDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.StringValueWrapper;


/**
 * The IndexDialog is used to edit an index configuration.
 * <pre>
 * +--------------------------------------------------+
 * |  Attributes                                      |
 * | .----------------------------------------------. |
 * | |     +----------------------------+           | |
 * | | (o) |                            | (Add...)  | |
 * | |     |                            | (Delete)  | |
 * | |     +----------------------------+           | |
 * | | (o) Default                                  | |
 * | '----------------------------------------------' |
 * |  Indices                                         |
 * | .----------------------------------------------. |
 * | | [] pres        [] eq          [] approx      | |
 * | | [] nolang      [] noSubtypes  [] notags      | |
 * | | [] sub                                       | |
 * | |    [] subinitial                             | |
 * | |    [] subany                                 | |
 * | |    [] subfinal                               | |
 * | '----------------------------------------------' |
 * +--------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DbIndexDialog extends AddEditDialog<DbIndexWrapper>
{
    /** The attributes list */
    private List<String> attributes = new ArrayList<String>();

    // UI widgets
    // The attribute's group
    private Button attributesCheckbox;
    private TableWidget<StringValueWrapper> attributeTable;
    private Button defaultCheckbox;
    
    // The index type section
    private Button presCheckbox;
    private Button eqCheckbox;
    private Button approxCheckbox;
    private Button subCheckbox;
    private Button noLangCheckbox;
    private Button noSubtypesCheckbox;
    private Button noTagsCheckbox;
    private Button subInitialCheckbox;
    private Button subAnyCheckbox;
    private Button subFinalCheckbox;
    
    // The list of all the type buttons
    private Button[] typeButtons = new Button[10];
    
    /**
     * Listeners for the Attributes radioButton. It will enable the Attributes table.
     * */ 
    private SelectionListener attributesCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            attributeTable.enable();
            getEditedElement().setDefault( false );
            checkAndUpdateOkButtonEnableState();
        }
    };
    
    
    /**
     * The attribute table listener
     */
    private WidgetModifyListener attributeTableListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            getEditedElement().getAttributes().clear();
            
            for ( StringValueWrapper attribute : attributeTable.getElements() )
            {
                getEditedElement().getAttributes().add( attribute.getValue() );
            }
        }
    };

    
    /**
     * A listener on the Default radio button. It will disable the Attributes table
     * and the associated buttons.
     */
    private SelectionListener defaultCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            attributeTable.disable();
            getEditedElement().setDefault( true );
            checkAndUpdateOkButtonEnableState();
        }
    };

    
    /**
     * A listener on one of the indexType checkboxes (but SUB and SUBxxx)
     */
    private SelectionListener typeButtonSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            checkAndUpdateOkButtonEnableState();
            DbIndexWrapper indexWrapper = getEditedElement();
            
            // Update the edited element
            Button selectedCheckbox = (Button)e.getSource();
            
            for ( int i = 0; i < typeButtons.length; i++ )
            {
                if ( typeButtons[i] == selectedCheckbox )
                {
                    DbIndexTypeEnum indexType = DbIndexTypeEnum.getIndexType( i );
                    
                    if ( selectedCheckbox.getSelection() )
                    {
                        indexWrapper.getTypes().add( indexType );
                    }
                    else
                    {
                        indexWrapper.getTypes().remove( indexType );
                    }
                    
                    System.out.println( indexWrapper );
                }
            }
        };
    };

    
    /**
     * A listener on the SUB indice check box. If it's selected, we will grey all the sub-sub indexes.
     * If it's delected, we will remove all the sub-sub indexes
     */
    private SelectionListener subCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            DbIndexWrapper indexWrapper = getEditedElement();
            
            if ( subCheckbox.getSelection() )
            {
                indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUB );
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBINITIAL );
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBANY );
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBFINAL );
                
                subInitialCheckbox.setSelection( true );
                subAnyCheckbox.setSelection( true );
                subFinalCheckbox.setSelection( true );
            }
            else
            {
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBINITIAL );
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBANY );
                indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBFINAL );
                
                subInitialCheckbox.setSelection( false );
                subAnyCheckbox.setSelection( false );
                subFinalCheckbox.setSelection( false );
            }
            
            System.out.println( indexWrapper );
            
            checkAndUpdateOkButtonEnableState();
        }
    };

    
    /**
     * A listener on the SUB related indices check boxes. We will disable the SUB checkbox, no matter what
     */
    private SelectionListener subSubCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            // Check that we aren't coming from a modification of the SUB button
            DbIndexWrapper indexWrapper = getEditedElement();
            
            Button button = (Button)e.getSource();
            
            // First, update the indexTypes set
            if ( button == subAnyCheckbox )
            {
                if ( button.getSelection() )
                {
                    if ( subInitialCheckbox.getSelection() && subFinalCheckbox.getSelection() )
                    {
                        // The three subXXX indexes are selected : select the SUB checkbox
                        subCheckbox.setSelection( true );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBINITIAL );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBFINAL );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUB );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                    }
                    else if ( subInitialCheckbox.getSelection() || subFinalCheckbox.getSelection() )
                    {
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBANY );
                    }
                    else
                    {
                        // Gray the sub checkbox, and select it
                        subCheckbox.setSelection( true );
                        subCheckbox.setGrayed( true );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                    }
                }
                else
                {
                    if ( !subInitialCheckbox.getSelection() && !subFinalCheckbox.getSelection() )
                    {
                        subCheckbox.setGrayed( false );
                        subCheckbox.setSelection( false );
                    }
                    else if ( subInitialCheckbox.getSelection() && subFinalCheckbox.getSelection() )
                    {
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBINITIAL );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBFINAL );
                        subCheckbox.setGrayed( true );
                    }

                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBANY );
                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                }
            }
            else if ( button == subInitialCheckbox )
            {
                if ( button.getSelection() )
                {
                    if ( subAnyCheckbox.getSelection() && subFinalCheckbox.getSelection() )
                    {
                        // The three subXXX indexes are selected : select the SUB checkbox
                        subCheckbox.setSelection( true );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBINITIAL );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBFINAL );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUB );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                    }
                    else if ( subAnyCheckbox.getSelection() || subFinalCheckbox.getSelection() )
                    {
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBINITIAL );
                    }
                    else
                    {
                        // Gray the sub checkbox, and select it
                        subCheckbox.setSelection( true );
                        subCheckbox.setGrayed( true );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                    }
                }
                else
                {
                    if ( !subAnyCheckbox.getSelection() && !subFinalCheckbox.getSelection() )
                    {
                        subCheckbox.setGrayed( false );
                        subCheckbox.setSelection( false );
                    }
                    else if ( subAnyCheckbox.getSelection() && subFinalCheckbox.getSelection() )
                    {
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBFINAL );
                        subCheckbox.setGrayed( true );
                    }

                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBINITIAL );
                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                }
            }
            else if ( button == subFinalCheckbox )
            {
                if ( button.getSelection() )
                {
                    if ( subAnyCheckbox.getSelection() && subInitialCheckbox.getSelection() )
                    {
                        // The three subXXX indexes are selected : select the SUB checkbox
                        subCheckbox.setSelection( true );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBINITIAL );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBFINAL );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUB );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                    }
                    else if ( subAnyCheckbox.getSelection() || subInitialCheckbox.getSelection() )
                    {
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBFINAL );
                    }
                    else
                    {
                        // Gray the sub checkbox, and select it
                        subCheckbox.setSelection( true );
                        subCheckbox.setGrayed( true );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBFINAL );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                        indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                    }
                }
                else
                {
                    if ( !subAnyCheckbox.getSelection() && !subInitialCheckbox.getSelection() )
                    {
                        subCheckbox.setGrayed( false );
                        subCheckbox.setSelection( false );
                    }
                    else if ( subAnyCheckbox.getSelection() && subInitialCheckbox.getSelection() )
                    {
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBANY );
                        indexWrapper.getIndexTypes().add( DbIndexTypeEnum.SUBINITIAL );
                        subCheckbox.setGrayed( true );
                    }

                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBFINAL );
                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUB );
                    indexWrapper.getIndexTypes().remove( DbIndexTypeEnum.SUBSTR );
                }
            }
            
            System.out.println( indexWrapper );

            // Last, update the subCheckbox state
            checkAndUpdateOkButtonEnableState();
        }
    };


    /**
     * Creates a new instance of OverlayDialog.
     * 
     * @param parentShell the parent shell
     * @param browserConnection the connection
     */
    public DbIndexDialog( Shell parentShell, IBrowserConnection browserConnection )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
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
     * Creates the IndexDialog, which has two groups : attributes and indices.
     * <pre>
     * +--------------------------------------------------+
     * |  Attributes                                      |
     * | .----------------------------------------------. |
     * | |     +----------------------------+           | |
     * | | (o) |                            | (Add...)  | |
     * | |     |                            | (Delete)  | |
     * | |     +----------------------------+           | |
     * | | (o) Default                                  | |
     * | '----------------------------------------------' |
     * |  Indices                                         |
     * | .----------------------------------------------. |
     * | | [] pres        [] eq          [] approx      | |
     * | | [] nolang      [] noSubtypes  [] notags      | |
     * | | [] sub                                       | |
     * | |    [] subinitial                             | |
     * | |    [] subany                                 | |
     * | |    [] subfinal                               | |
     * | '----------------------------------------------' |
     * +--------------------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // The attributes group
        createAttributesGroup( composite );
        
        // The indices grouo
        createIndicesGroup( composite );

        // Load the dialog if this was an Edit call
        initDialog();
        //addListeners();

        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the attributes group.
     * <pre>
     *   Attributes
     *  .----------------------------------------------.
     *  |     +----------------------------+           |
     *  | (o) |                            | (Add...)  |
     *  |     |                            | (Delete)  |
     *  |     +----------------------------+           |
     *  | (o) Default                                  |
     *  '----------------------------------------------'
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

        // Attributes table
        StringValueDecorator decorator = new StringValueDecorator( parent.getShell(), "Attribute" );
        decorator.setImage( OpenLdapConfigurationPlugin.getDefault().getImage(
                    OpenLdapConfigurationPluginConstants.IMG_ATTRIBUTE ) );
        attributeTable = new TableWidget<StringValueWrapper>( decorator );

        attributeTable.createWidgetNoEdit( attributesGroup, null );
        attributeTable.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 1, 1 ) );
        attributeTable.addWidgetModifyListener( attributeTableListener );

        // Attributes Checkbox
        defaultCheckbox = BaseWidgetUtils.createRadiobutton( attributesGroup, "  Default", 2 );
        defaultCheckbox.addSelectionListener( defaultCheckboxSelectionListener );
    }


    /**
     * Creates the indices group.
     * <pre>
     *  Indices
     * .----------------------------------------------.
     * | [] pres        [] eq          [] approx      |
     * | [] nolang      [] noSubtypes  [] notags      |
     * | [] sub                                       |
     * |    [] subinitial                             |
     * |    [] subany                                 |
     * |    [] subfinal                               |
     * '----------------------------------------------'
     * </pre>
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
        presCheckbox.addSelectionListener( typeButtonSelectionListener );
        typeButtons[DbIndexTypeEnum.PRES.getNumber()] = presCheckbox;

        // Eq Checkbox
        eqCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "eq", 1 );
        eqCheckbox.addSelectionListener( typeButtonSelectionListener );
        typeButtons[DbIndexTypeEnum.EQ.getNumber()] = eqCheckbox;

        // Approx Checkbox
        approxCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "approx", 1 );
        approxCheckbox.addSelectionListener( typeButtonSelectionListener );
        typeButtons[DbIndexTypeEnum.APPROX.getNumber()] = approxCheckbox;

        // NoLang Checkbox
        noLangCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "nolang", 1 );
        noLangCheckbox.addSelectionListener( typeButtonSelectionListener );
        typeButtons[DbIndexTypeEnum.NOLANG.getNumber()] = noLangCheckbox;

        // NoSybtypes Checkbox
        noSubtypesCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "nosubtypes", 1 );
        noSubtypesCheckbox.addSelectionListener( typeButtonSelectionListener );
        typeButtons[DbIndexTypeEnum.NOSUBTYPES.getNumber()] = noSubtypesCheckbox;

        // NoTags Checkbox
        noTagsCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "notags", 1 );
        noTagsCheckbox.addSelectionListener( typeButtonSelectionListener );
        typeButtons[DbIndexTypeEnum.NOTAGS.getNumber()] = noTagsCheckbox;

        // Sub Checkbox
        subCheckbox = BaseWidgetUtils.createCheckbox( indicesGroup, "sub", 1 );
        subCheckbox.addSelectionListener( subCheckboxSelectionListener );
        typeButtons[DbIndexTypeEnum.SUB.getNumber()] = subCheckbox;

        // Sub Composite
        Composite subComposite = new Composite( indicesGroup, SWT.NONE );
        GridLayout subCompositeGridLayout = new GridLayout( 2, false );
        subCompositeGridLayout.marginHeight = subCompositeGridLayout.marginWidth = 0;
        subCompositeGridLayout.verticalSpacing = subCompositeGridLayout.horizontalSpacing = 0;
        subComposite.setLayout( subCompositeGridLayout );

        // SubInitial Checkbox
        BaseWidgetUtils.createRadioIndent( subComposite, 1 );
        subInitialCheckbox = BaseWidgetUtils.createCheckbox( subComposite, "subinitial", 1 );
        subInitialCheckbox.addSelectionListener( subSubCheckboxSelectionListener );
        typeButtons[DbIndexTypeEnum.SUBINITIAL.getNumber()] = subInitialCheckbox;

        // SubAny Checkbox
        BaseWidgetUtils.createRadioIndent( subComposite, 1 );
        subAnyCheckbox = BaseWidgetUtils.createCheckbox( subComposite, "subany", 1 );
        subAnyCheckbox.addSelectionListener( subSubCheckboxSelectionListener );
        typeButtons[DbIndexTypeEnum.SUBANY.getNumber()] = subAnyCheckbox;

        // SubFinal Checkbox
        BaseWidgetUtils.createRadioIndent( subComposite, 1 );
        subFinalCheckbox = BaseWidgetUtils.createCheckbox( subComposite, "subfinal", 1 );
        subFinalCheckbox.addSelectionListener( subSubCheckboxSelectionListener );
        typeButtons[DbIndexTypeEnum.SUBFINAL.getNumber()] = subFinalCheckbox;
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

    
    private void initAttributeTable( Set<String> attributes )
    {
        List<StringValueWrapper> attributeWrappers = new ArrayList<StringValueWrapper>();

        if ( attributes != null )
        {
            for ( String attribute : attributes )
            {
                attributeWrappers.add( new StringValueWrapper( attribute, false ) );
            }
        }

        attributeTable.setElements( attributeWrappers );
    }


    /**
     * Inits the UI from the DbIndexWrapper
     */
    protected void initDialog()
    {
        DbIndexWrapper editedElement = (DbIndexWrapper)getEditedElement();
        
        if ( editedElement != null )
        {
            // Attributes
            initAttributeTable( editedElement.getAttributes() );

            // Default
            if ( editedElement.isDefault() )
            {
                attributesCheckbox.setSelection( false );
                attributeTable.disable();
                defaultCheckbox.setSelection( true );
            }

            // Index types
            Set<DbIndexTypeEnum> indexTypes = editedElement.getTypes();

            if ( ( indexTypes != null ) && ( indexTypes.size() > 0 ) )
            {
                presCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.PRES ) );
                eqCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.EQ ) );
                approxCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.APPROX ) );
                
                if ( indexTypes.contains( DbIndexTypeEnum.SUB ) )
                {
                    subCheckbox.setSelection( true );
                    setSelectionForSubCheckboxes( indexTypes.contains( DbIndexTypeEnum.SUB ) );
                }
                else
                {
                    subInitialCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.SUBINITIAL ) );
                    subAnyCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.SUBANY ) );
                    subFinalCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.SUBFINAL ) );
                    checkAndUpdateSubCheckboxSelectionState();
                }
                
                noLangCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.NOLANG ) );
                noSubtypesCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.NOSUBTYPES ) );
                noTagsCheckbox.setSelection( indexTypes.contains( DbIndexTypeEnum.NOTAGS ) );
            }
        }
    }

    
    /**
     * Add a new Element that will be edited
     */
    protected void addNewElement( DbIndexWrapper editedElement )
    {
        DbIndexWrapper newElement = (DbIndexWrapper)editedElement.clone();
        setEditedElement( newElement );
    }


    /**
     * Add a new Element that will be edited
     */
    public void addNewElement()
    {
        setEditedElement( new DbIndexWrapper( "" ) );
    }

    
    /**
     * Overriding the createButton method. The OK button is not enabled until 
     * 
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) 
    {
        Button button = super.createButton(parent, id, label, defaultButton);

        if ( id == IDialogConstants.OK_ID ) 
        {
            DbIndexWrapper dbIndexWrapper = (DbIndexWrapper)getEditedElement();

            if ( ( dbIndexWrapper == null ) || ( dbIndexWrapper.getAttributes().size() == 0 ) )
            {
                button.setEnabled( false );
            }
        }
        
        return button;
    }


    /**
     * Checks and updates the OK button 'enable' state. For the OK button to be enabled, either
     * the default checkbox has to be selected, and one selection has to be made on the indices,
     * or the attributes table should not be empty.
     */
    private void checkAndUpdateOkButtonEnableState()
    {
        Button okButton = getButton( IDialogConstants.OK_ID );

        if ( defaultCheckbox.getSelection() )
        {
            okButton.setEnabled(
                presCheckbox.getSelection() || 
                eqCheckbox.getSelection() || 
                approxCheckbox.getSelection() ||
                subCheckbox.getSelection() || 
                subInitialCheckbox.getSelection() || 
                subAnyCheckbox.getSelection() ||
                subFinalCheckbox.getSelection() || 
                noLangCheckbox.getSelection() || 
                noLangCheckbox.getSelection() ||
                noSubtypesCheckbox.getSelection() );
        }
        else
        {
            okButton.setEnabled( attributes.size() > 0 );
        }
    }
}
