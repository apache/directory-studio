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

package org.apache.directory.ldapstudio.browser.common.dialogs.preferences;


import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinaryAttribute;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class AttributeDialog extends Dialog
{

    private BinaryAttribute currentAttribute;

    private String[] attributeTypesAndOids;

    private BinaryAttribute returnAttribute;

    private Combo typeOrOidCombo;


    public AttributeDialog( Shell parentShell, BinaryAttribute currentAttribute, String[] attributeNamesAndOids )
    {
        super( parentShell );
        this.currentAttribute = currentAttribute;
        this.attributeTypesAndOids = attributeNamesAndOids;

        this.returnAttribute = null;
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Select Attribute Type or OID" );
    }


    protected void okPressed()
    {
        this.returnAttribute = new BinaryAttribute( typeOrOidCombo.getText() );
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, "Attribute Type or OID:", 1 );
        this.typeOrOidCombo = BaseWidgetUtils.createCombo( c, this.attributeTypesAndOids, -1, 1 );
        if ( this.currentAttribute != null )
        {
            this.typeOrOidCombo.setText( currentAttribute.getAttributeNumericOidOrName() );
        }
        this.typeOrOidCombo.addModifyListener( new ModifyListener()
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
        super.getButton( IDialogConstants.OK_ID ).setEnabled( !"".equals( this.typeOrOidCombo.getText() ) );
    }


    public BinaryAttribute getAttribute()
    {
        return returnAttribute;
    }

}
