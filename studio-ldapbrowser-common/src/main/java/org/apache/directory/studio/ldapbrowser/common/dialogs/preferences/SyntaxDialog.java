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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinarySyntax;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class SyntaxDialog extends Dialog
{

    private BinarySyntax currentSyntax;

    private String[] syntaxOids;

    private BinarySyntax returnSyntax;

    private Combo oidCombo;


    public SyntaxDialog( Shell parentShell, BinarySyntax currentSyntax, String[] syntaxOids )
    {
        super( parentShell );
        this.currentSyntax = currentSyntax;
        this.syntaxOids = syntaxOids;

        this.returnSyntax = null;
    }


    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( "Select Syntax OID" );
    }


    protected void okPressed()
    {
        this.returnSyntax = new BinarySyntax( oidCombo.getText() );
        super.okPressed();
    }


    protected Control createDialogArea( Composite parent )
    {

        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, "Attribute Type or OID:", 1 );
        this.oidCombo = BaseWidgetUtils.createCombo( c, this.syntaxOids, -1, 1 );
        if ( this.currentSyntax != null )
        {
            this.oidCombo.setText( currentSyntax.getSyntaxNumericOid() );
        }
        this.oidCombo.addModifyListener( new ModifyListener()
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
        super.getButton( IDialogConstants.OK_ID ).setEnabled( !"".equals( this.oidCombo.getText() ) );
    }


    public BinarySyntax getSyntax()
    {
        return returnSyntax;
    }

}
