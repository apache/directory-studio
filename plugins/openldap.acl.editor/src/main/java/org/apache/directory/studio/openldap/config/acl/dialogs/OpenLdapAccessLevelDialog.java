/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.dialogs;


import java.util.List;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPlugin;
import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPluginConstants;
import org.apache.directory.studio.openldap.config.acl.model.AclAccessLevel;
import org.apache.directory.studio.openldap.config.acl.model.AclAccessLevelLevelEnum;
import org.apache.directory.studio.openldap.config.acl.model.AclAccessLevelPrivModifierEnum;
import org.apache.directory.studio.openldap.config.acl.model.AclAccessLevelPrivilegeEnum;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAccessLevelDialog extends Dialog
{
    /** The array of access levels */
    private Object[] levels = new Object[]
        {
            new AccessLevelComboViewerName(),
            AclAccessLevelLevelEnum.MANAGE,
            AclAccessLevelLevelEnum.WRITE,
            AclAccessLevelLevelEnum.READ,
            AclAccessLevelLevelEnum.SEARCH,
            AclAccessLevelLevelEnum.COMPARE,
            AclAccessLevelLevelEnum.AUTH,
            AclAccessLevelLevelEnum.DISCLOSE,
            AclAccessLevelLevelEnum.NONE,
    };

    /** The access level */
    private AclAccessLevel accessLevel;

    // UI widgets
    private Button okButton;
    private Button selfCheckbox;
    private Button levelRadioButton;
    private ComboViewer levelComboViewer;
    private Button customPrivilegesRadioButton;
    private Button privilegeModifierEqualRadioButton;
    private Button privilegeModifierPlusRadioButton;
    private Button privilegeModifierMinusRadioButton;
    private Button privilegeAuthCheckbox;
    private Button privilegeCompareCheckbox;
    private Button privilegeSearchCheckbox;
    private Button privilegeReadCheckbox;
    private Button privilegeWriteCheckbox;


    /**
     * Creates a new instance of OpenLdapAccessLevelDialog.
     *
     * @param accessLevel the access level
     */
    public OpenLdapAccessLevelDialog( AclAccessLevel accessLevel )
    {
        super( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );
        this.accessLevel = accessLevel;
    }


    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Access Level Editor" );
        shell.setImage( OpenLdapAclEditorPlugin.getDefault().getImage( OpenLdapAclEditorPluginConstants.IMG_EDITOR ) );
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );
    }


    protected Control createContents( Composite parent )
    {
        Control control = super.createContents( parent );

        // Validating the dialog
        validate();

        return control;
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        // Self
        accessLevel.setSelf( selfCheckbox.getSelection() );

        // Level
        if ( levelRadioButton.getSelection() )
        {
            Object levelSelection = ( ( StructuredSelection ) levelComboViewer.getSelection() ).getFirstElement();
            if ( ( levelSelection != null ) && ( levelSelection instanceof AclAccessLevelLevelEnum ) )
            {
                accessLevel.setLevel( ( AclAccessLevelLevelEnum ) levelSelection );
            }
            else
            {
                accessLevel.setLevel( null );
            }
        }
        else
        {
            accessLevel.setLevel( null );
        }

        // Custom privileges
        if ( customPrivilegesRadioButton.getSelection() )
        {
            // Privilege modifier
            accessLevel.setPrivilegeModifier( getPrivilegeModifier() );

            // Privileges
            accessLevel.clearPrivileges();
            addPrivileges();
        }
        else
        {
            accessLevel.setPrivilegeModifier( null );
            accessLevel.clearPrivileges();
        }

        super.okPressed();
    }


    /**
     * Gets the privilege modifier.
     *
     * @return the privilege modifier
     */
    private AclAccessLevelPrivModifierEnum getPrivilegeModifier()
    {
        if ( privilegeModifierEqualRadioButton.getSelection() )
        {
            return AclAccessLevelPrivModifierEnum.EQUAL;
        }
        else if ( privilegeModifierPlusRadioButton.getSelection() )
        {
            return AclAccessLevelPrivModifierEnum.PLUS;
        }
        else if ( privilegeModifierMinusRadioButton.getSelection() )
        {
            return AclAccessLevelPrivModifierEnum.MINUS;
        }

        return null;
    }


    /**
     * Adds privileges.
     */
    private void addPrivileges()
    {
        // Auth checkbox
        if ( privilegeAuthCheckbox.getSelection() )
        {
            accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.AUTHENTICATION );
        }

        // Compare checkbox
        if ( privilegeCompareCheckbox.getSelection() )
        {
            accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.COMPARE );
        }

        // Read checkbox
        if ( privilegeReadCheckbox.getSelection() )
        {
            accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.READ );
        }

        // Search checkbox
        if ( privilegeSearchCheckbox.getSelection() )
        {
            accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.SEARCH );
        }

        // Write checkbox
        if ( privilegeWriteCheckbox.getSelection() )
        {
            accessLevel.addPrivilege( AclAccessLevelPrivilegeEnum.WRITE );
        }

    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
        //        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        //        gd.heightHint = convertVerticalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH ) * 4 / 3;
        composite.setLayoutData( gd );

        // Creating UI
        createSelfGroup( composite );
        createLevelAndPrivilegesGroup( composite );

        // Initializing the UI with the access level
        initWithAccessLevel();

        // Adding listeners
        addListeners();

        // Setting default focus on the composite
        composite.setFocus();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Validates the dialog.
     */
    private void validate()
    {
        if ( levelRadioButton.getSelection() )
        {
            // Getting the selection of the level combo viewer
            Object levelSelection = ( ( StructuredSelection ) levelComboViewer.getSelection() ).getFirstElement();

            // Enabling the OK button only when the selection is a 'real' level
            okButton.setEnabled( levelSelection instanceof AclAccessLevelLevelEnum );
            return;
        }
        else if ( customPrivilegesRadioButton.getSelection() )
        {
            // Enabling the OK button only when at least one of privileges is checked
            okButton.setEnabled( privilegeAuthCheckbox.getSelection() || privilegeCompareCheckbox.getSelection()
                || privilegeSearchCheckbox.getSelection() || privilegeReadCheckbox.getSelection()
                || privilegeWriteCheckbox.getSelection() );
            return;
        }

        // Default case
        okButton.setEnabled( true );
    }


    /**
     * Creates the self group.
     *
     * @param parent the parent composite
     */
    private void createSelfGroup( Composite parent )
    {
        Group selfGroup = BaseWidgetUtils.createGroup( parent, "", 1 );
        selfGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Self Checkbox
        selfCheckbox = new Button( selfGroup, SWT.CHECK );
        selfCheckbox.setText( "Self" ); //$NON-NLS-1$
    }


    /**
     * Creates the level and privileges group.
     *
     * @param parent the parent composite
     */
    private void createLevelAndPrivilegesGroup( Composite parent )
    {
        // Access level and privileges group
        Group levelAndPrivilegesGroup = BaseWidgetUtils.createGroup( parent, "Access Level and Privilege(s)", 1 );
        levelAndPrivilegesGroup.setLayout( new GridLayout( 2, false ) );
        levelAndPrivilegesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Level label and radio button
        levelRadioButton = BaseWidgetUtils.createRadiobutton( levelAndPrivilegesGroup, "Level:", 1 );
        levelComboViewer = new ComboViewer( BaseWidgetUtils.createReadonlyCombo( levelAndPrivilegesGroup,
            new String[0], -1, 1 ) );
        levelComboViewer.setContentProvider( new ArrayContentProvider() );
        levelComboViewer.setLabelProvider( new LabelProvider()
        {
            public String getText( Object element )
            {
                if ( element instanceof AccessLevelComboViewerName )
                {
                    return "< Access Level >";
                }
                else if ( element instanceof AclAccessLevelLevelEnum )
                {
                    AclAccessLevelLevelEnum value = ( AclAccessLevelLevelEnum ) element;
                    switch ( value )
                    {
                        case MANAGE:
                            return "Manage";
                        case WRITE:
                            return "Write";
                        case READ:
                            return "Read";
                        case SEARCH:
                            return "Search";
                        case COMPARE:
                            return "Compare";
                        case AUTH:
                            return "Auth";
                        case DISCLOSE:
                            return "Disclose";
                        case NONE:
                            return "None";
                    }
                }

                return super.getText( element );
            }
        } );
        levelComboViewer.setInput( levels );
        //        levelComboViewer.setSelection( new StructuredSelection( currentClauseSelection ) ); TODO

        // Custom privileges radio button
        customPrivilegesRadioButton = BaseWidgetUtils.createRadiobutton( levelAndPrivilegesGroup,
            "Custom Privilege(s):", 2 );

        // Custom privileges composite
        Composite privilegesTabComposite = BaseWidgetUtils.createColumnContainer( levelAndPrivilegesGroup, 2, 2 );

        // Custom privileges modifier group
        createRadioIndent( privilegesTabComposite );
        Group modifierGroup = BaseWidgetUtils.createGroup( privilegesTabComposite, "Modifier", 1 );
        modifierGroup.setLayout( new GridLayout( 3, true ) );
        modifierGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Custom privileges modifier radio buttons
        privilegeModifierEqualRadioButton = BaseWidgetUtils.createRadiobutton( modifierGroup, "Equal (=)", 1 );
        privilegeModifierEqualRadioButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        privilegeModifierPlusRadioButton = BaseWidgetUtils.createRadiobutton( modifierGroup, "Add (+)", 1 );
        privilegeModifierPlusRadioButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        privilegeModifierMinusRadioButton = BaseWidgetUtils.createRadiobutton( modifierGroup, "Delete (-)", 1 );
        privilegeModifierMinusRadioButton.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Custom privileges group
        createRadioIndent( privilegesTabComposite );
        Group privilegesGroup = BaseWidgetUtils.createGroup( privilegesTabComposite, "Privileges", 1 );
        privilegesGroup.setLayout( new GridLayout( 3, true ) );
        privilegesGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Custom privileges checkboxes
        privilegeAuthCheckbox = BaseWidgetUtils.createCheckbox( privilegesGroup, "Auth", 1 );
        privilegeAuthCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        privilegeCompareCheckbox = BaseWidgetUtils.createCheckbox( privilegesGroup, "Compare", 1 );
        privilegeCompareCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        privilegeSearchCheckbox = BaseWidgetUtils.createCheckbox( privilegesGroup, "Search", 1 );
        privilegeSearchCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        privilegeReadCheckbox = BaseWidgetUtils.createCheckbox( privilegesGroup, "Read", 1 );
        privilegeReadCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        privilegeWriteCheckbox = BaseWidgetUtils.createCheckbox( privilegesGroup, "Write", 1 );
        privilegeWriteCheckbox.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
    }


    /**
     * Adds some space to indent radio buttons.
     *
     * @param parent the parent
     * @param span the horizontal span
     */
    public static void createRadioIndent( Composite parent )
    {
        Label l = new Label( parent, SWT.NONE );
        GridData gd = new GridData();
        gd.horizontalIndent = 10;
        l.setLayoutData( gd );
    }


    private void initWithAccessLevel()
    {
        // Creating a boolean to indicate if the level is used (rather than the privileges)
        boolean isLevelUsed = true;

        if ( accessLevel == null )
        {
            // Access level can't be null, creating a new one
            accessLevel = new AclAccessLevel();
        }

        // Self
        selfCheckbox.setSelection( accessLevel.isSelf() );

        // Level
        AclAccessLevelLevelEnum level = accessLevel.getLevel();
        if ( level != null )
        {
            levelComboViewer.setSelection( new StructuredSelection( level ) );
        }
        else
        {
            // Default
            levelComboViewer.setSelection( new StructuredSelection( levels[0] ) );
        }

        // Privilege Modifier
        AclAccessLevelPrivModifierEnum privilegeModifier = accessLevel.getPrivilegeModifier();
        if ( privilegeModifier != null )
        {
            // Level is not used in that case
            isLevelUsed = false;

            privilegeModifierEqualRadioButton.setSelection( AclAccessLevelPrivModifierEnum.EQUAL
                .equals( privilegeModifier ) );
            privilegeModifierPlusRadioButton.setSelection( AclAccessLevelPrivModifierEnum.PLUS
                .equals( privilegeModifier ) );
            privilegeModifierMinusRadioButton.setSelection( AclAccessLevelPrivModifierEnum.MINUS
                .equals( privilegeModifier ) );
        }
        else
        {
            // Default
            privilegeModifierEqualRadioButton.setSelection( true );
            privilegeModifierPlusRadioButton.setSelection( false );
            privilegeModifierMinusRadioButton.setSelection( false );
        }

        // Privileges
        List<AclAccessLevelPrivilegeEnum> privileges = accessLevel.getPrivileges();
        privilegeAuthCheckbox.setSelection( privileges.contains( AclAccessLevelPrivilegeEnum.AUTHENTICATION ) );
        privilegeCompareCheckbox.setSelection( privileges.contains( AclAccessLevelPrivilegeEnum.COMPARE ) );
        privilegeSearchCheckbox.setSelection( privileges.contains( AclAccessLevelPrivilegeEnum.SEARCH ) );
        privilegeReadCheckbox.setSelection( privileges.contains( AclAccessLevelPrivilegeEnum.READ ) );
        privilegeWriteCheckbox.setSelection( privileges.contains( AclAccessLevelPrivilegeEnum.WRITE ) );

        // Setting choice buttons
        levelRadioButton.setSelection( isLevelUsed );
        customPrivilegesRadioButton.setSelection( !isLevelUsed );

        // Setting the enable/disable state for buttons
        setButtonsEnableDisableState();
    }


    /**
     * Sets the enable/disable state for buttons
     */
    private void setButtonsEnableDisableState()
    {
        boolean isLevelUsed = levelRadioButton.getSelection();

        levelComboViewer.getCombo().setEnabled( isLevelUsed );
        privilegeModifierEqualRadioButton.setEnabled( !isLevelUsed );
        privilegeModifierPlusRadioButton.setEnabled( !isLevelUsed );
        privilegeModifierMinusRadioButton.setEnabled( !isLevelUsed );
        privilegeAuthCheckbox.setEnabled( !isLevelUsed );
        privilegeCompareCheckbox.setEnabled( !isLevelUsed );
        privilegeSearchCheckbox.setEnabled( !isLevelUsed );
        privilegeReadCheckbox.setEnabled( !isLevelUsed );
        privilegeWriteCheckbox.setEnabled( !isLevelUsed );
    }


    /**
     * Adds listeners to the UI widgets.
     */
    private void addListeners()
    {
        // Level and custom privileges radio buttons
        SelectionAdapter enableDisableStateAndValidateSelectionAdapter = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                setButtonsEnableDisableState();
                validate();
            }
        };
        levelRadioButton.addSelectionListener( enableDisableStateAndValidateSelectionAdapter );
        customPrivilegesRadioButton.addSelectionListener( enableDisableStateAndValidateSelectionAdapter );

        // Level combo viewer
        levelComboViewer.addSelectionChangedListener( new ISelectionChangedListener()
        {
            public void selectionChanged( SelectionChangedEvent event )
            {
                validate();
            }
        } );

        // Privilege modifier and privileges radio buttons
        SelectionAdapter validateSelectionAdapter = new SelectionAdapter()
        {
            public void widgetSelected( SelectionEvent e )
            {
                validate();
            }
        };
        privilegeModifierEqualRadioButton.addSelectionListener( validateSelectionAdapter );
        privilegeModifierPlusRadioButton.addSelectionListener( validateSelectionAdapter );
        privilegeModifierMinusRadioButton.addSelectionListener( validateSelectionAdapter );
        privilegeAuthCheckbox.addSelectionListener( validateSelectionAdapter );
        privilegeCompareCheckbox.addSelectionListener( validateSelectionAdapter );
        privilegeSearchCheckbox.addSelectionListener( validateSelectionAdapter );
        privilegeReadCheckbox.addSelectionListener( validateSelectionAdapter );
        privilegeWriteCheckbox.addSelectionListener( validateSelectionAdapter );
    }


    /**
     * Gets the ACL Access Level value.
     * 
     * @return the ACL Access Level value
     */
    public AclAccessLevel getAccessLevel()
    {
        return accessLevel;
    }

    /**
     * A private object for the first row of the access level combo viewer.
     */
    private class AccessLevelComboViewerName
    {
    }
}
