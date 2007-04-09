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


import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager.ValueEditorExtension;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class AttributeValueEditorDialog extends Dialog
{

    private AttributeValueProviderRelation relation;

    private SortedMap<String, ValueEditorExtension> class2ValueEditorProxyMap;

    private String[] attributeTypesAndOids;

    private SortedMap<String, String> vpName2classMap;

    private AttributeValueProviderRelation returnRelation;

    private Combo typeOrOidCombo;

    private Combo valueEditorCombo;


    public AttributeValueEditorDialog( Shell parentShell, AttributeValueProviderRelation relation,
        SortedMap<String, ValueEditorExtension> class2ValueEditorProxyMap, String[] attributeTypesAndOids )
    {
        super( parentShell );
        this.relation = relation;
        this.class2ValueEditorProxyMap = class2ValueEditorProxyMap;
        this.attributeTypesAndOids = attributeTypesAndOids;

        this.returnRelation = null;

        this.vpName2classMap = new TreeMap<String, String>();
        for ( Iterator<ValueEditorExtension> it = this.class2ValueEditorProxyMap.values().iterator(); it.hasNext(); )
        {
            ValueEditorExtension vp = it.next();
            vpName2classMap.put( vp.name, vp.className );
        }
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Attribute Value Editor" );
    }


    protected void okPressed()
    {
        this.returnRelation = new AttributeValueProviderRelation( this.typeOrOidCombo.getText(), this.vpName2classMap
            .get( this.valueEditorCombo.getText() ) );
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
        this.valueEditorCombo = BaseWidgetUtils.createReadonlyCombo( c, vpName2classMap.keySet()
            .toArray( new String[0] ), -1, 1 );
        if ( this.relation != null && this.relation.getValueProviderClassname() != null
            && this.class2ValueEditorProxyMap.containsKey( this.relation.getValueProviderClassname() ) )
        {
            this.valueEditorCombo.setText( ( this.class2ValueEditorProxyMap.get( this.relation
                .getValueProviderClassname() ) ).name );
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
