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

package org.apache.directory.studio.apacheds.configuration.v2.dialogs;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The AttributeDialog is used to enter/select an attribute type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeDialog extends Dialog
{
    /** The possible attribute types and OIDs. */
    private String[] attributeTypesAndOids;

    /** The attribute. */
    private String attribute;

    /** The combo. */
    private Combo typeOrOidCombo;

    /** The OK button of the dialog */
    private Button okButton;


    /**
     * Creates a new instance of AttributeDialog.
     * 
     * @param parentShell the parent shell
     * @param attribute the attribute, null if none 
     * @param attributeNamesAndOids the possible attribute names and OIDs
     */
    public AttributeDialog( Shell parentShell, String attribute, String[] attributeNamesAndOids )
    {
        super( parentShell );
        this.attribute = attribute;
        this.attributeTypesAndOids = attributeNamesAndOids;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "AttributeDialog.SelectAttributeTypeOrOID" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        okButton = createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true );
        createButton( parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false );

        validate();
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        attribute = typeOrOidCombo.getText();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, Messages.getString( "AttributeDialog.AttributeTypeOrOID" ), 1 ); //$NON-NLS-1$
        typeOrOidCombo = BaseWidgetUtils.createCombo( c, attributeTypesAndOids, -1, 1 );
        
        if ( attribute != null )
        {
            typeOrOidCombo.setText( attribute );
        }
        
        typeOrOidCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        return composite;
    }


    private void validate()
    {
        okButton.setEnabled( !"".equals( typeOrOidCombo.getText() ) ); //$NON-NLS-1$
    }


    /**
     * Gets the attribute.
     * 
     * @return the attribute
     */
    public String getAttribute()
    {
        return attribute;
    }
}
