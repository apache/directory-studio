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

package org.apache.directory.ldapstudio.browser.ui.dialogs.preferences;


import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.ldapstudio.browser.ui.valueproviders.ValueProvider;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class AttributeValueProviderDialog extends Dialog
{

    private AttributeValueProviderRelation relation;

    private SortedMap class2ValueProviderMap;

    private String[] attributeTypesAndOids;

    private SortedMap vpName2classMap;

    private AttributeValueProviderRelation returnRelation;

    private Combo typeOrOidCombo;

    private Combo valueEditorCombo;


    public AttributeValueProviderDialog( Shell parentShell, AttributeValueProviderRelation relation,
        SortedMap class2ValueProviderMap, String[] attributeTypesAndOids )
    {
        super( parentShell );
        this.relation = relation;
        this.class2ValueProviderMap = class2ValueProviderMap;
        this.attributeTypesAndOids = attributeTypesAndOids;

        this.returnRelation = null;

        this.vpName2classMap = new TreeMap();
        for ( Iterator it = this.class2ValueProviderMap.values().iterator(); it.hasNext(); )
        {
            ValueProvider vp = ( ValueProvider ) it.next();
            vpName2classMap.put( vp.getCellEditorName(), vp.getClass().getName() );
        }
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Attribute Value Editor" );
    }


    protected void okPressed()
    {
        this.returnRelation = new AttributeValueProviderRelation( this.typeOrOidCombo.getText(),
            ( String ) this.vpName2classMap.get( this.valueEditorCombo.getText() ) );
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, "Attribute Type or OID:", 1 );
        this.typeOrOidCombo = BaseWidgetUtils.createCombo( c, this.attributeTypesAndOids, -1, 1 );
        if ( this.relation != null && this.relation.getAttributeNumericOidOrType() != null )
        {
            this.typeOrOidCombo.setText( this.relation.getAttributeNumericOidOrType() );
        }
        this.typeOrOidCombo.addModifyListener( new ModifyListener()
        {
            public void modifyText( ModifyEvent e )
            {
                validate();
            }
        } );

        BaseWidgetUtils.createLabel( c, "Value Editor:", 1 );
        this.valueEditorCombo = BaseWidgetUtils.createReadonlyCombo( c, ( String[] ) vpName2classMap.keySet().toArray(
            new String[0] ), -1, 1 );
        if ( this.relation != null && this.relation.getValueProviderClassname() != null
            && this.class2ValueProviderMap.containsKey( this.relation.getValueProviderClassname() ) )
        {
            this.valueEditorCombo.setText( ( ( ValueProvider ) this.class2ValueProviderMap.get( this.relation
                .getValueProviderClassname() ) ).getCellEditorName() );
        }
        this.valueEditorCombo.addModifyListener( new ModifyListener()
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
        super.getButton( IDialogConstants.OK_ID ).setEnabled(
            !"".equals( this.valueEditorCombo.getText() ) && !"".equals( this.typeOrOidCombo.getText() ) );
    }


    public AttributeValueProviderRelation getRelation()
    {
        return returnRelation;
    }

}
