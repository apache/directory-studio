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

import org.apache.directory.ldapstudio.browser.core.model.schema.SyntaxValueProviderRelation;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager.ValueEditorExtension;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class SyntaxValueEditorDialog extends Dialog
{

    private SyntaxValueProviderRelation relation;

    private SortedMap<String, ValueEditorExtension> class2ValueEditorProxyMap;

    private String[] syntaxOids;

    private SortedMap<String, String> vpName2classMap;

    private SyntaxValueProviderRelation returnRelation;

    private Combo oidCombo;

    private Combo valueEditorCombo;


    public SyntaxValueEditorDialog( Shell parentShell, SyntaxValueProviderRelation relation,
        SortedMap<String, ValueEditorExtension> class2ValueEditorProxyMap, String[] syntaxOids )
    {
        super( parentShell );
        this.relation = relation;
        this.class2ValueEditorProxyMap = class2ValueEditorProxyMap;
        this.syntaxOids = syntaxOids;

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
        this.returnRelation = new SyntaxValueProviderRelation( this.oidCombo.getText(), ( String ) this.vpName2classMap
            .get( this.valueEditorCombo.getText() ) );
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, "Syntax OID:", 1 );
        this.oidCombo = BaseWidgetUtils.createCombo( c, this.syntaxOids, -1, 1 );
        if ( this.relation != null && this.relation.getSyntaxOID() != null )
        {
            this.oidCombo.setText( this.relation.getSyntaxOID() );
        }
        this.oidCombo.addModifyListener( new ModifyListener()
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
            !"".equals( this.valueEditorCombo.getText() ) && !"".equals( this.oidCombo.getText() ) );
    }


    public SyntaxValueProviderRelation getRelation()
    {
        return returnRelation;
    }

}
