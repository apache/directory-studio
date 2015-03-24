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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
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
 */
public class AttributeDialog extends Dialog
{
    /** The possible attribute types */
    private String[] attributeTypes;

    /** The return attribute */
    private String returnAttribute;

    // UI widgets
    private Button okButton;
    private Combo combo;


    /**
     * Creates a new instance of AttributeDialog.
     * 
     * @param parentShell the parent shell
     * @param connection the connection
     */
    public AttributeDialog( Shell parentShell, IBrowserConnection browserConnection )
    {
        super( parentShell );
        init( browserConnection, null );
    }


    /**
     * Creates a new instance of AttributeDialog.
     * 
     * @param parentShell the parent shell
     * @param connection the connection
     * @param attribute the attribute
     */
    public AttributeDialog( Shell parentShell, IBrowserConnection browserConnection, String attribute )
    {
        super( parentShell );
        init( browserConnection, attribute );
    }


    /**
     * Initializes the object.
     * 
     * @param connection the connection
     * @param attribute the attribute
     */
    private void init( IBrowserConnection browserConnection, String attribute )
    {
        List<String> attributeTypes = new ArrayList<String>();

        if ( browserConnection != null )
        {
            Collection<AttributeType> atds = browserConnection.getSchema().getAttributeTypeDescriptions();

            for ( AttributeType atd : atds )
            {
                for ( String name : atd.getNames() )
                {
                    attributeTypes.add( name );
                }
            }

            Collections.sort( attributeTypes );
        }

        this.attributeTypes = attributeTypes.toArray( new String[attributeTypes.size()] );
        returnAttribute = attribute;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Select Attribute Type" );
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
        returnAttribute = combo.getText();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, "Attribute Type:", 1 );
        combo = BaseWidgetUtils.createCombo( c, attributeTypes, -1, 1 );

        if ( returnAttribute != null )
        {
            combo.setText( returnAttribute );
        }
        
        combo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        return composite;
    }


    /**
     * Validates the dialog.
     */
    private void validate()
    {
        okButton.setEnabled( !"".equals( combo.getText() ) ); //$NON-NLS-1$
    }


    /**
     * Gets the entered/selected attribute.
     * 
     * @return the attribute
     */
    public String getAttribute()
    {
        return returnAttribute;
    }
}
