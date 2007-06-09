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

package org.apache.directory.ldapstudio.valueeditors.administrativerole;


import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.common.widgets.ListContentProposalProvider;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsActivator;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * This class provides a dialog to enter or select an administrative role.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AdministrativeRoleDialog extends Dialog
{

    /** The dialog title */
    public static final String DIALOG_TITLE = "Administrative Role Editor";

    /** The possible administrative role values. */
    private static final String[] administrativeRoleValues = new String[]
        { "autonomousArea", "accessControlSpecificArea", "accessControlInnerArea", "subschemaAdminSpecificArea",
            "collectiveAttributeSpecificArea", "collectiveAttributeInnerArea", "triggerExecutionSpecificArea",
            "triggerExecutionInnerArea" };

    /** The initial value. */
    private String initialValue;

    /** The administrative role combo field. */
    private DecoratedField administrativeRoleComboField;

    /** The administrative role combo. */
    private Combo administrativeRoleCombo;

    /** The administrative role content proposal adapter */
    private ContentProposalAdapter administrativeRoleCPA;

    /** The return value. */
    private String returnValue;


    /**
     * Creates a new instance of AdministrativeRoleDialog.
     * 
     * @param parentShell the parent shell
     * @param initialValue the initial value
     */
    public AdministrativeRoleDialog( Shell parentShell, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.returnValue = null;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( ValueEditorsActivator.getDefault()
            .getImage( ValueEditorsConstants.IMG_ADMINISTRATIVEROLEEDITOR ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        super.createButtonsForButtonBar( parent );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnValue = administrativeRoleCombo.getText();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // combo widget
        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_CONTENT_PROPOSAL );
        administrativeRoleComboField = new DecoratedField( composite, SWT.NONE, new IControlCreator()
        {
            public Control createControl( Composite parent, int style )
            {
                Combo combo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
                combo.setVisibleItemCount( 20 );
                return combo;
            }
        } );
        administrativeRoleComboField.addFieldDecoration( fieldDecoration, SWT.TOP | SWT.LEFT, true );
        administrativeRoleComboField.getLayoutControl()
            .setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        administrativeRoleCombo = ( Combo ) administrativeRoleComboField.getControl();
        administrativeRoleCombo.setItems( administrativeRoleValues );
        administrativeRoleCombo.setText( initialValue );

        // content proposal adapter
        administrativeRoleCPA = new ContentProposalAdapter( administrativeRoleCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( administrativeRoleCombo.getItems() ), null, null );
        administrativeRoleCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
        administrativeRoleCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE );

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Gets the administrative role.
     * 
     * @return the administrative role
     */
    public String getAdministrativeRole()
    {
        return returnValue;
    }
}
