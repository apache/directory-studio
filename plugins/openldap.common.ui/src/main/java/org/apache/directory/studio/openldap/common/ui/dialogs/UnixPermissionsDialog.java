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
package org.apache.directory.studio.openldap.common.ui.dialogs;


import java.text.ParseException;

import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The UnixPermissionsDialog is used to edit a Unix Permissions value. Unix
 * permissions are stored using 3 sets of permissions for 3 different entities :
 * 
 * <ul>
 * <li>users</li>
 * <li>group</li>
 * <li>other</li>
 * </ul> 
 * 
 * with the following permissions :
 *
 * <ul>
 * <li>read</li>
 * <li>write</li>
 * <li>execute</li>
 * </ul> 
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UnixPermissionsDialog extends Dialog
{
    /** The octal value */
    private String value;

    // UI widgets
    private Button ownerReadCheckbox;
    private Button ownerWriteCheckbox;
    private Button ownerExecuteCheckbox;
    private Button groupReadCheckbox;
    private Button groupWriteCheckbox;
    private Button groupExecuteCheckbox;
    private Button othersReadCheckbox;
    private Button othersWriteCheckbox;
    private Button othersExecuteCheckbox;
    private Text octalNotationText;

    // The octal verifier only accepts values between 0 and 7.
    private VerifyListener octalNotationTextVerifyListener = new VerifyListener()
    {
        public void verifyText( VerifyEvent e )
        {
            if ( !e.text.matches( "[0-7]*" ) ) //$NON-NLS-1$
            {
                e.doit = false;
            }
        }
    };
    
    
    private ModifyListener octalNotationTextModifyListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            resetChecboxSelection();

            try
            {
                UnixPermissions perm = new UnixPermissions( octalNotationText.getText() );

                removeListeners();
                setCheckboxesValue( perm );
                addListeners();
            }
            catch ( ParseException e1 )
            {
                // Nothing to do
            }
        }
    };
    
    
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            UnixPermissions perm = new UnixPermissions();

            perm.setOwnerRead( ownerReadCheckbox.getSelection() );
            perm.setOwnerWrite( ownerWriteCheckbox.getSelection() );
            perm.setOwnerExecute( ownerExecuteCheckbox.getSelection() );
            perm.setGroupRead( groupReadCheckbox.getSelection() );
            perm.setGroupWrite( groupWriteCheckbox.getSelection() );
            perm.setGroupExecute( groupExecuteCheckbox.getSelection() );
            perm.setOthersRead( othersReadCheckbox.getSelection() );
            perm.setOthersWrite( othersWriteCheckbox.getSelection() );
            perm.setOthersExecute( othersExecuteCheckbox.getSelection() );

            removeListeners();
            setOctalValue( perm );
            addListeners();
        }
    };


    /**
     * Creates a new instance of UnixPermissionsDialog.
     * 
     * @param parentShell the parent shell
     */
    public UnixPermissionsDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Creates a new instance of UnixPermissionsDialog.
     * 
     * @param parentShell the parent shell
     * @param value the initial value
     */
    public UnixPermissionsDialog( Shell parentShell, String value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.value = value;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Unix Permissions Dialog" );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        try
        {
            UnixPermissions perm = new UnixPermissions( octalNotationText.getText() );
            value = perm.getOctalValue();
        }
        catch ( ParseException e )
        {
            value = "0000";
        }

        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createPermissionsArea( composite );
        createOctalNotationArea( composite );

        initialize();

        addListeners();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Initializes the dialog with the initial value
     */
    private void initialize()
    {
        if ( value != null )
        {
            try
            {
                UnixPermissions perm = new UnixPermissions( value );

                setCheckboxesValue( perm );
                setOctalValue( perm );
            }
            catch ( ParseException e )
            {
                resetChecboxSelection();
                setOctalValue( new UnixPermissions() );
            }
        }
        else
        {
            resetChecboxSelection();
            setOctalValue( new UnixPermissions() );
        }
    }


    /**
     * Sets the checkboxes value.
     *
     * @param perm the Unix permissions
     */
    private void setCheckboxesValue( UnixPermissions perm )
    {
        ownerReadCheckbox.setSelection( perm.isOwnerRead() );
        ownerWriteCheckbox.setSelection( perm.isOwnerWrite() );
        ownerExecuteCheckbox.setSelection( perm.isOwnerExecute() );
        groupReadCheckbox.setSelection( perm.isGroupRead() );
        groupWriteCheckbox.setSelection( perm.isGroupWrite() );
        groupExecuteCheckbox.setSelection( perm.isGroupExecute() );
        othersReadCheckbox.setSelection( perm.isOthersRead() );
        othersWriteCheckbox.setSelection( perm.isOthersWrite() );
        othersExecuteCheckbox.setSelection( perm.isOthersExecute() );
    }


    /**
     * Sets the octal value.
     *
     * @param perm the Unix permissions
     */
    private void setOctalValue( UnixPermissions perm )
    {
        octalNotationText.setText( perm.getOctalValue() );
    }


    /**
     * Resets the checkbox selection
     */
    private void resetChecboxSelection()
    {
        ownerReadCheckbox.setSelection( false );
        ownerWriteCheckbox.setSelection( false );
        ownerExecuteCheckbox.setSelection( false );
        groupReadCheckbox.setSelection( false );
        groupWriteCheckbox.setSelection( false );
        groupExecuteCheckbox.setSelection( false );
        othersReadCheckbox.setSelection( false );
        othersWriteCheckbox.setSelection( false );
        othersExecuteCheckbox.setSelection( false );
    }


    /**
     * Creates the permissions area.
     *
     * @param parent the parent composite
     */
    private void createPermissionsArea( Composite parent )
    {
        Group symbolicNotationGroup = BaseWidgetUtils.createGroup( parent, "Permissions", 1 );
        symbolicNotationGroup.setLayout( new GridLayout( 2, false ) );

        BaseWidgetUtils.createLabel( symbolicNotationGroup, "Owner:", 1 );
        Composite ownerComposite = BaseWidgetUtils.createColumnContainer( symbolicNotationGroup, 3, true, 1 );
        ownerReadCheckbox = BaseWidgetUtils.createCheckbox( ownerComposite, "Read", 1 );
        ownerWriteCheckbox = BaseWidgetUtils.createCheckbox( ownerComposite, "Write", 1 );
        ownerExecuteCheckbox = BaseWidgetUtils.createCheckbox( ownerComposite, "Execute", 1 );

        BaseWidgetUtils.createLabel( symbolicNotationGroup, "Group:", 1 );
        Composite groupComposite = BaseWidgetUtils.createColumnContainer( symbolicNotationGroup, 3, true, 1 );
        groupReadCheckbox = BaseWidgetUtils.createCheckbox( groupComposite, "Read", 1 );
        groupWriteCheckbox = BaseWidgetUtils.createCheckbox( groupComposite, "Write", 1 );
        groupExecuteCheckbox = BaseWidgetUtils.createCheckbox( groupComposite, "Execute", 1 );

        BaseWidgetUtils.createLabel( symbolicNotationGroup, "Others:", 1 );
        Composite othersComposite = BaseWidgetUtils.createColumnContainer( symbolicNotationGroup, 3, true, 1 );
        othersReadCheckbox = BaseWidgetUtils.createCheckbox( othersComposite, "Read", 1 );
        othersWriteCheckbox = BaseWidgetUtils.createCheckbox( othersComposite, "Write", 1 );
        othersExecuteCheckbox = BaseWidgetUtils.createCheckbox( othersComposite, "Execute", 1 );
    }


    /**
     * Creates the octal notation area.
     *
     * @param parent the parent composite
     */
    private void createOctalNotationArea( Composite parent )
    {
        Group octalNotationGroup = BaseWidgetUtils.createGroup( parent, "Octal Notation", 1 );
        octalNotationText = BaseWidgetUtils.createText( octalNotationGroup, "0000", 1 );
        octalNotationText.setTextLimit( 4 );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        ownerReadCheckbox.addSelectionListener( checkboxSelectionListener );
        ownerWriteCheckbox.addSelectionListener( checkboxSelectionListener );
        ownerExecuteCheckbox.addSelectionListener( checkboxSelectionListener );
        groupReadCheckbox.addSelectionListener( checkboxSelectionListener );
        groupWriteCheckbox.addSelectionListener( checkboxSelectionListener );
        groupExecuteCheckbox.addSelectionListener( checkboxSelectionListener );
        othersReadCheckbox.addSelectionListener( checkboxSelectionListener );
        othersWriteCheckbox.addSelectionListener( checkboxSelectionListener );
        othersExecuteCheckbox.addSelectionListener( checkboxSelectionListener );
        octalNotationText.addVerifyListener( octalNotationTextVerifyListener );
        octalNotationText.addModifyListener( octalNotationTextModifyListener );
    }


    /**
     * Remove listeners.
     */
    private void removeListeners()
    {
        ownerReadCheckbox.removeSelectionListener( checkboxSelectionListener );
        ownerWriteCheckbox.removeSelectionListener( checkboxSelectionListener );
        ownerExecuteCheckbox.removeSelectionListener( checkboxSelectionListener );
        groupReadCheckbox.removeSelectionListener( checkboxSelectionListener );
        groupWriteCheckbox.removeSelectionListener( checkboxSelectionListener );
        groupExecuteCheckbox.removeSelectionListener( checkboxSelectionListener );
        othersReadCheckbox.removeSelectionListener( checkboxSelectionListener );
        othersWriteCheckbox.removeSelectionListener( checkboxSelectionListener );
        othersExecuteCheckbox.removeSelectionListener( checkboxSelectionListener );
        octalNotationText.removeVerifyListener( octalNotationTextVerifyListener );
        octalNotationText.removeModifyListener( octalNotationTextModifyListener );
    }


    /**
     * Gets the symbolic value (no type included).
     * 
     * @return the symbolic value
     */
    public String getSymbolicValue()
    {
        
        UnixPermissions perm = null;
        try
        {
            perm = new UnixPermissions( value );
        }
        catch ( ParseException e )
        {
            perm = new UnixPermissions();
        }

        return perm.getSymbolicValue();
    }


    /**
     * Gets the octal value.
     * 
     * @return the octal value
     */
    public String getOctalValue()
    {
        return value;
    }


    /**
     * Gets the decimal value.
     *
     * @return the decimal value
     */
    public String getDecimalValue()
    {
        return "" + Integer.parseInt( value, 8 );
    }
}
