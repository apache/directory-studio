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

package org.apache.directory.studio.valueeditors.administrativerole;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.connection.ui.widgets.ExtendedContentAssistCommandAdapter;
import org.apache.directory.studio.ldapbrowser.common.widgets.ListContentProposalProvider;
import org.apache.directory.studio.valueeditors.ValueEditorsActivator;
import org.apache.directory.studio.valueeditors.ValueEditorsConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
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
 */
public class AdministrativeRoleDialog extends Dialog
{

    /** The possible administrative role values. */
    private static final String[] administrativeRoleValues = new String[]
        { "autonomousArea", "accessControlSpecificArea", "accessControlInnerArea", "subschemaAdminSpecificArea", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            "collectiveAttributeSpecificArea", "collectiveAttributeInnerArea", "triggerExecutionSpecificArea", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "triggerExecutionInnerArea" }; //$NON-NLS-1$

    /** The initial value. */
    private String initialValue;

    /** The administrative role combo. */
    private Combo administrativeRoleCombo;

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
        shell.setText( Messages.getString( "AdministrativeRoleDialog.AdministrativeRoleEditor" ) ); //$NON-NLS-1$
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

        // attribute combo with field decoration and content proposal
        administrativeRoleCombo = BaseWidgetUtils.createCombo( composite, new String[0], -1, 1 );
        administrativeRoleCombo.setVisibleItemCount( 20 );
        administrativeRoleCombo.setItems( administrativeRoleValues );
        administrativeRoleCombo.setText( initialValue );
        new ExtendedContentAssistCommandAdapter( administrativeRoleCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( administrativeRoleCombo.getItems() ), null, null, true );

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
