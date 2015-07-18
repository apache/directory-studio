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

import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.common.ui.widgets.TableWidget;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyEvent;
import org.apache.directory.studio.common.ui.widgets.WidgetModifyListener;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.ldapbrowser.core.utils.SchemaObjectLoader;
import org.apache.directory.studio.openldap.common.ui.model.DnSpecStyleEnum;
import org.apache.directory.studio.openldap.common.ui.model.DnSpecTypeEnum;
import org.apache.directory.studio.openldap.common.ui.model.LimitSelectorEnum;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitDecorator;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitsWrapper;
import org.apache.directory.studio.openldap.config.model.database.OlcDatabaseConfig;


/**
 * The LimitsDialog is used to edit the Limits parameter<br/>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +-------------------------------------------------------+
 * | Limits                                                |
 * | .---------------------------------------------------. |
 * | | (o) Any                                           | |
 * | | (o) Anonymous                                     | |
 * | | (o) Users                                         | |
 * | |            .------------------------------------. | |
 * | |            | Type :    [--------------------|v] | | |
 * | | (o) DN     | Style :   [--------------------|v] | | |
 * | |            | Pattern : [----------------------] | | |
 * | |            '------------------------------------' | |
 * | |            .------------------------------------. | |
 * | |            | ObjectClass :   [--------------|v] | | |
 * | | (o) Group  | AttributeType : [--------------|v] | | |
 * | |            | Pattern :       [----------------] | | |
 * | |            '------------------------------------' | |
 * | |                                                   | |
 * | | Limits :                                          | |
 * | | +-------------------------------------+           | |
 * | | |{1}defxyz12                          | (Add...)  | |
 * | | |{2}aaa                               | (Edit...) | |
 * | | |                                     | (Delete)  | |
 * | | |                                     | --------- | |
 * | | |                                     | (Up...)   | |
 * | | |                                     | (Down...) | |
 * | | +-------------------------------------+           | |
 * | '---------------------------------------------------' |
 * | Resulting Limits                                      |
 * | .---------------------------------------------------. |
 * | | <///////////////////////////////////////////////> | |
 * | '---------------------------------------------------' |
 * |                                                       |
 * |  (Cancel)                                      (OK)   |
 * +-------------------------------------------------------+
 * </pre>
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LimitsDialog extends AddEditDialog<LimitsWrapper>
{
    /** The Any radio button */
    private Button anyButton;
    
    /** The Anonymous radio button */
    private Button anonymousButton;
    
    /** The Users radio button */
    private Button usersButton;
    
    /** The DNSpec radio button */
    private Button dnSpecButton;
    
    /** The DNSpec type */
    private Combo dnSpecTypeCombo;
    
    /** The DNSpec style */
    private Combo dnSpecStyleCombo;
    
    /** The DNSpec pattern */
    private Text dnSpecPatternText;
    
    /** The Group radio button */
    private Button groupButton;
    
    /** The Group ObjectClass type */
    private Combo groupObjectClassCombo;
    
    /** The Group AttributeType style */
    private Combo groupAttributeTypeCombo;
    
    /** The Group pattern */
    private Text groupPatternText;

    /** The (time/size)Limit parameter */
    private TableWidget<LimitWrapper> limitsTableWidget;

    /** The resulting Limits Text */
    private Text limitsText;
    
    /** The Attribute list loader */
    private SchemaObjectLoader schemaObjectLoader;

    /**
     * Disable the DnSpec and Group widgets
     */
    private void disableDnSpecGroupButtons()
    {
        dnSpecTypeCombo.setEnabled( false );
        dnSpecStyleCombo.setEnabled( false );
        dnSpecPatternText.setEnabled( false );
        groupAttributeTypeCombo.setEnabled( false );
        groupObjectClassCombo.setEnabled( false );
        groupPatternText.setEnabled( false );
    }
    
    
    /**
     * Disable or enable the DnSpec and Group widgets
     */
    private void setDnSpecGroupButtons( boolean dnSpecStatus, boolean groupStatus )
    {
        groupAttributeTypeCombo.setEnabled( groupStatus );
        groupObjectClassCombo.setEnabled( groupStatus );
        groupPatternText.setEnabled( groupStatus );
        dnSpecTypeCombo.setEnabled( dnSpecStatus );
        dnSpecStyleCombo.setEnabled( dnSpecStatus );
        dnSpecPatternText.setEnabled( dnSpecStatus  );
    }
    

    /**
     * Reset the content of the edited element
     */
    private void clearEditedElement()
    {
        getEditedElement().setDnSpecStyle( null );
        getEditedElement().setDnSpecType( null );
        getEditedElement().setAttributeType( null );
        getEditedElement().setObjectClass( null );
        getEditedElement().setSelectorPattern( null );
    }

    
    /**
     * Listeners for the Selector radioButtons. It will enable or disable the dnSpec or Group accordingly
     * to the selection.
     **/ 
    private SelectionListener selectorButtonsSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent event )
        {
            if ( event.getSource() instanceof Button )
            {
                Button button = (Button)event.getSource();
                
                if ( button == anyButton )
                {
                    if ( button.getSelection() )
                    {
                        disableDnSpecGroupButtons();
                        clearEditedElement();
                        getEditedElement().setSelector( LimitSelectorEnum.ANY );
                        limitsText.setText( getEditedElement().toString() );
                    }
                }
                else if ( button == anonymousButton )
                {
                    if ( button.getSelection() )
                    {
                        disableDnSpecGroupButtons();
                        clearEditedElement();
                        getEditedElement().setSelector( LimitSelectorEnum.ANONYMOUS );
                        limitsText.setText( getEditedElement().toString() );
                    }
                }
                else if ( button == usersButton )
                {
                    if ( button.getSelection() )
                    {
                        disableDnSpecGroupButtons();
                        clearEditedElement();
                        getEditedElement().setSelector( LimitSelectorEnum.USERS );
                        limitsText.setText( getEditedElement().toString() );
                    }
                }
                else if ( button == dnSpecButton )
                {
                    setDnSpecGroupButtons( dnSpecButton.getSelection(), false );
                    clearEditedElement();
                    getEditedElement().setSelector( LimitSelectorEnum.DNSPEC );
                    limitsText.setText( getEditedElement().toString() );
                }
                else if ( button == groupButton )
                {
                    setDnSpecGroupButtons( false, groupButton.getSelection() );
                    clearEditedElement();
                    getEditedElement().setSelector( LimitSelectorEnum.GROUP );
                    limitsText.setText( getEditedElement().toString() );
                }
            }
        }
    };
    
    
    /**
     * The dnSpecTypeCombo listener
     */
    private SelectionListener dnSpecTypeComboListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getEditedElement().setDnSpecType( DnSpecTypeEnum.getType( dnSpecTypeCombo.getText() ) );
            limitsText.setText( getEditedElement().toString() );
        }
    };
    
    
    /**
     * The dnSpecTypeCombo listener
     */
    private SelectionListener dnSpecStyleComboListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getEditedElement().setDnSpecStyle( DnSpecStyleEnum.getStyle( dnSpecStyleCombo.getText() ) );
            limitsText.setText( getEditedElement().toString() );
        }
    };

    
    /**
     * The groupAttributeTypeCombo listener
     */
    private SelectionListener groupAttributeTypeComboListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getEditedElement().setAttributeType( groupAttributeTypeCombo.getText() );
            limitsText.setText( getEditedElement().toString() );
        }
    };
    
    
    /**
     * The groupObjectClassCombo listener
     */
    private SelectionListener groupObjectClassComboListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            getEditedElement().setObjectClass( groupObjectClassCombo.getText() );
            limitsText.setText( getEditedElement().toString() );
        }
    };

    /**
     * The dnSpecPatternText and groupPatternText listener
     */
    private ModifyListener patternTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            if ( e.getSource() == dnSpecPatternText )
            {
                getEditedElement().setSelectorPattern( dnSpecPatternText.getText() );
            }
            else
            {
                getEditedElement().setSelectorPattern( groupPatternText.getText() );
            }
            
            limitsText.setText( getEditedElement().toString() );
        }
    };
    
    
    /**
     * The olcLimits listener
     */
    private WidgetModifyListener limitsTableWidgetListener = new WidgetModifyListener()
    {
        public void widgetModified( WidgetModifyEvent e )
        {
            getEditedElement().setLimits( limitsTableWidget.getElements() );
            limitsText.setText( getEditedElement().toString() );
        }
    };
    

    /**
     * Create a new instance of the LimitsDialog
     * 
     * @param parentShell The parent Shell
     */
    public LimitsDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        schemaObjectLoader = new SchemaObjectLoader();
    }


    /**
     * Create a new instance of the LimitsDialog
     * 
     * @param parentShell The parent Shell
     * @param timeLimitStr The instance containing the Limits data
     */
    public LimitsDialog( Shell parentShell, String limitsStr )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        schemaObjectLoader = new SchemaObjectLoader();

        setEditedElement( new LimitsWrapper( limitsStr ) );
    }
    
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Limits" );
    }


    /**
     * Create the Dialog for TimeLimit :
     * <pre>
     * +-------------------------------------------------------+
     * | Limits                                                |
     * | .---------------------------------------------------. |
     * | | (o) Any                                           | |
     * | | (o) Anonymous                                     | |
     * | | (o) Users                                         | |
     * | |            .------------------------------------. | |
     * | |            | Type :    [--------------------|v] | | |
     * | | (o) DN     | Style :   [--------------------|v] | | |
     * | |            | Pattern : [----------------------] | | |
     * | |            '------------------------------------' | |
     * | |            .------------------------------------. | |
     * | |            | ObjectClass :   [--------------|v] | | |
     * | | (o) Group  | AttributeType : [--------------|v] | | |
     * | |            | Pattern :       [----------------] | | |
     * | |            '------------------------------------' | |
     * | |                                                   | |
     * | | Limits :                                          | |
     * | | +-------------------------------------+           | |
     * | | |{1}defxyz12                          | (Add...)  | |
     * | | |{2}aaa                               | (Edit...) | |
     * | | |                                     | (Delete)  | |
     * | | |                                     | --------- | |
     * | | |                                     | (Up...)   | |
     * | | |                                     | (Down...) | |
     * | | +-------------------------------------+           | |
     * | '---------------------------------------------------' |
     * | Resulting Limits                                      |
     * | .---------------------------------------------------. |
     * | | Limits : <//////////////////////////////////////> | |
     * | '---------------------------------------------------' |
     * |                                                       |
     * |  (Cancel)                                      (OK)   |
     * +-------------------------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        
        createLimitsEditGroup( composite );
        createLimitsShowGroup( composite );

        initDialog();
        addListeners();
        
        applyDialogFont( composite );

        return composite;
    }


    /**
     * Creates the Limits input group.
     * 
     * <pre>
     * Limits
     * .---------------------------------------------------.
     * | (o) Any                                           |
     * | (o) Anonymous                                     |
     * | (o) Users                                         |
     * |            .------------------------------------. |
     * |            | Type :    [--------------------|v] | |
     * | (o) DN     | Style :   [--------------------|v] | |
     * |            | Pattern : [----------------------] | |
     * |            '------------------------------------' |
     * |            .------------------------------------. |
     * |            | ObjectClass :   [--------------|v] | |
     * | (o) Group  | AttributeType : [--------------|v] | |
     * |            | Pattern :       [----------------] | |
     * |            '------------------------------------' |
     * |                                                   |
     * | Limits :                                          |
     * | +-------------------------------------+           |
     * | |{1}defxyz12                          | (Add...)  |
     * | |{2}aaa                               | (Edit...) |
     * | |                                     | (Delete)  |
     * | |                                     | --------- |
     * | |                                     | (Up...)   |
     * | |                                     | (Down...) |
     * | +-------------------------------------+           |
     * '---------------------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createLimitsEditGroup( Composite parent )
    {
        // Selector Group
        Group selectorGroup = BaseWidgetUtils.createGroup( parent, "Limit input", 1 );
        GridLayout selectorGridLayout = new GridLayout( 2, false );
        selectorGroup.setLayout( selectorGridLayout );
        selectorGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Any button
        anyButton = BaseWidgetUtils.createRadiobutton( selectorGroup, "Any", 2 );
        anyButton.addSelectionListener( selectorButtonsSelectionListener );

        // Anonymous button
        anonymousButton = BaseWidgetUtils.createRadiobutton( selectorGroup, "Anonymous", 2 );
        anonymousButton.addSelectionListener( selectorButtonsSelectionListener );

        // Users button
        usersButton = BaseWidgetUtils.createRadiobutton( selectorGroup, "Users", 2 );
        usersButton.addSelectionListener( selectorButtonsSelectionListener );

        // DNSpec button
        dnSpecButton = BaseWidgetUtils.createRadiobutton( selectorGroup, "DN", 1 );
        dnSpecButton.addSelectionListener( selectorButtonsSelectionListener );

        // The group associated with the DN Sepc
        Group dnSpecGroup = BaseWidgetUtils.createGroup( selectorGroup, "", 2 );
        GridLayout dnSpecGridLayout = new GridLayout( 2, false );
        dnSpecGroup.setLayout( dnSpecGridLayout );
        dnSpecGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        
        // The DNSpec type Combo
        BaseWidgetUtils.createLabel( dnSpecGroup, "Type :", 1 );
        dnSpecTypeCombo = BaseWidgetUtils.createCombo( dnSpecGroup, DnSpecTypeEnum.getNames(), -1, 1 );
        dnSpecTypeCombo.setEnabled( false );
        dnSpecTypeCombo.addSelectionListener( dnSpecTypeComboListener );

        // The DNSpec style Combo
        BaseWidgetUtils.createLabel( dnSpecGroup, "Style :", 1 );
        dnSpecStyleCombo = BaseWidgetUtils.createCombo( dnSpecGroup, DnSpecStyleEnum.getNames(), -1, 1 );
        dnSpecStyleCombo.setEnabled( false );
        dnSpecStyleCombo.addSelectionListener( dnSpecStyleComboListener );
        
        // The DNSpec pattern Text
        BaseWidgetUtils.createLabel( dnSpecGroup, "Pattern :", 1 );
        dnSpecPatternText = BaseWidgetUtils.createText( dnSpecGroup, "", 1 );
        dnSpecPatternText.setEnabled( false );
        dnSpecPatternText.addModifyListener( patternTextListener );

        // Group button
        groupButton = BaseWidgetUtils.createRadiobutton( selectorGroup, "Group", 1 );
        groupButton.addSelectionListener( selectorButtonsSelectionListener );

        // The group associated with the Group
        Group groupGroup = BaseWidgetUtils.createGroup( selectorGroup, "", 2 );
        GridLayout groupGridLayout = new GridLayout( 2, false );
        groupGroup.setLayout( groupGridLayout );
        groupGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        schemaObjectLoader = new SchemaObjectLoader();

        // The ObjectClass Combo
        BaseWidgetUtils.createLabel( groupGroup, "ObjectClass :", 1 );
        groupObjectClassCombo = BaseWidgetUtils.createCombo( groupGroup, schemaObjectLoader.getObjectClassNamesAndOids(), -1, 1 );
        groupObjectClassCombo.setEnabled( false );
        groupObjectClassCombo.addSelectionListener( groupObjectClassComboListener );
        
        // The AttributeType Combo
        BaseWidgetUtils.createLabel( groupGroup, "Attribute Type :", 1 );
        groupAttributeTypeCombo = BaseWidgetUtils.createCombo( groupGroup, schemaObjectLoader.getAttributeNamesAndOids(), -1, 1 );
        groupAttributeTypeCombo.setEnabled( false );
        groupAttributeTypeCombo.addSelectionListener( groupAttributeTypeComboListener );

        // The Group pattern Text
        BaseWidgetUtils.createLabel( groupGroup, "Pattern :", 1 );
        groupPatternText = BaseWidgetUtils.createText( groupGroup, "", 1 );
        groupPatternText.setEnabled( false );
        dnSpecPatternText.addModifyListener( patternTextListener );

        // The Limits table
        BaseWidgetUtils.createLabel( selectorGroup, "Limits :", 1 );
        
        limitsTableWidget = new TableWidget<LimitWrapper>( 
            new LimitDecorator( parent.getShell() , "Limit") );

        limitsTableWidget.createWidgetWithEdit( selectorGroup, null );
        limitsTableWidget.getControl().setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false, 2, 1 ) );
        limitsTableWidget.addWidgetModifyListener( limitsTableWidgetListener );
    }


    /**
     * Creates the TimeLimit show group. This is the part of the dialog
     * where the real TimeLimit is shown, or an error message if the TimeLimit
     * is invalid.
     * 
     * <pre>
     * Resulting Limits
     * .------------------------------------.
     * | <////////////////////////////////> |
     * '------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createLimitsShowGroup( Composite parent )
    {
        // Limits Group
        Group limitsGroup = BaseWidgetUtils.createGroup( parent, "Resulting Limits", 1 );
        GridLayout limitsGroupGridLayout = new GridLayout( 2, false );
        limitsGroup.setLayout( limitsGroupGridLayout );
        limitsGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Limits Text
        limitsText = BaseWidgetUtils.createText( limitsGroup, "", 1 );
        limitsText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        limitsText.setEditable( false );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        /*
        softLimitText.addModifyListener( softLimitTextListener );
        softUnlimitedCheckbox.addSelectionListener( softUnlimitedCheckboxSelectionListener );
        hardLimitText.addModifyListener( hardLimitTextListener );
        hardUnlimitedCheckbox.addSelectionListener( hardUnlimitedCheckboxSelectionListener );
        hardSoftCheckbox.addSelectionListener( hardSoftCheckboxSelectionListener );
        globalLimitText.addModifyListener( globalLimitTextListener );
        globalUnlimitedCheckbox.addSelectionListener( globalUnlimitedCheckboxSelectionListener );
        */
    }


    @Override
    public void addNewElement()
    {
        setEditedElement( new LimitsWrapper( "" ) );
    }


    /**
     * Initializes the UI from the Limits
     */
    protected void initDialog()
    {
        LimitsWrapper editedElement = (LimitsWrapper)getEditedElement();
        
        if ( editedElement != null )
        {
            LimitSelectorEnum selector = editedElement.getSelector();
            
            if ( selector != null )
            {
                switch ( editedElement.getSelector() )
                {
                    case ANONYMOUS :
                        anonymousButton.setSelection( true );
                        dnSpecStyleCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( false );
                        dnSpecPatternText.setEnabled( false );
                        groupAttributeTypeCombo.setEnabled( false );
                        groupObjectClassCombo.setEnabled( false );
                        groupPatternText.setEnabled( false );
                        break;
                        
                    case ANY :
                        anyButton.setSelection( true );
                        dnSpecStyleCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( false );
                        dnSpecPatternText.setEnabled( false );
                        groupAttributeTypeCombo.setEnabled( false );
                        groupObjectClassCombo.setEnabled( false );
                        groupPatternText.setEnabled( false );
                        break;
                        
                    case USERS :
                        usersButton.setSelection( true );
                        dnSpecStyleCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( true );
                        dnSpecPatternText.setEnabled( false );
                        groupAttributeTypeCombo.setEnabled( false );
                        groupObjectClassCombo.setEnabled( false );
                        groupPatternText.setEnabled( false );
                        break;
                        
                    case DNSPEC :
                        dnSpecButton.setSelection( true );
                        dnSpecStyleCombo.setEnabled( true );
                        dnSpecTypeCombo.setEnabled( true );
                        dnSpecPatternText.setEnabled( true );
                        groupAttributeTypeCombo.setEnabled( false );
                        groupObjectClassCombo.setEnabled( false );
                        groupPatternText.setEnabled( false );
                        break;
                        
                    case GROUP :
                        groupButton.setSelection( true );
                        dnSpecStyleCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( false );
                        dnSpecPatternText.setEnabled( false );
                        groupAttributeTypeCombo.setEnabled( true );
                        groupObjectClassCombo.setEnabled( true );
                        groupPatternText.setEnabled( true );
                        break;
                        
                    default :
                        dnSpecStyleCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( false );
                        dnSpecTypeCombo.setEnabled( false );
                        dnSpecPatternText.setEnabled( false );
                        groupAttributeTypeCombo.setEnabled( false );
                        groupObjectClassCombo.setEnabled( false );
                        groupPatternText.setEnabled( false );
                        break;
                }
            }
            else
            {
                dnSpecStyleCombo.setEnabled( false );
                dnSpecTypeCombo.setEnabled( false );
                dnSpecTypeCombo.setEnabled( false );
                dnSpecPatternText.setEnabled( false );
                groupAttributeTypeCombo.setEnabled( false );
                groupObjectClassCombo.setEnabled( false );
                groupPatternText.setEnabled( false );
            }
        }
    }
}
