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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.core.model.schema.BinarySyntax;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * The SyntaxDialog is used to enter/select a syntax OID.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyntaxDialog extends Dialog
{

    /** The initial syntax. */
    private BinarySyntax currentSyntax;

    /** The possible syntax OIDs. */
    private String[] syntaxOids;

    /** The selected syntax. */
    private BinarySyntax returnSyntax;

    /** The combo. */
    private Combo oidCombo;


    /**
     * Creates a new instance of SyntaxDialog.
     * 
     * @param parentShell the parent shell
     * @param currentSyntax the current syntax, null if none
     * @param syntaxOids the possible syntax OIDs
     */
    public SyntaxDialog( Shell parentShell, BinarySyntax currentSyntax, String[] syntaxOids )
    {
        super( parentShell );
        this.currentSyntax = currentSyntax;
        this.syntaxOids = syntaxOids;
        this.returnSyntax = null;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell newShell )
    {
        super.configureShell( newShell );
        newShell.setText( Messages.getString( "SyntaxDialog.SelectSyntaxOID" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnSyntax = new BinarySyntax( oidCombo.getText() );
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );

        Composite c = BaseWidgetUtils.createColumnContainer( composite, 2, 1 );
        BaseWidgetUtils.createLabel( c, Messages.getString( "SyntaxDialog.SyntaxOID" ), 1 ); //$NON-NLS-1$
        oidCombo = BaseWidgetUtils.createCombo( c, syntaxOids, -1, 1 );
        if ( currentSyntax != null )
        {
            oidCombo.setText( currentSyntax.getSyntaxNumericOid() );
        }
        oidCombo.addModifyListener( new ModifyListener()
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
        getButton( IDialogConstants.OK_ID ).setEnabled( !"".equals( oidCombo.getText() ) ); //$NON-NLS-1$
    }


    /**
     * Gets the entered/selected syntax.
     * 
     * @return the syntax
     */
    public BinarySyntax getSyntax()
    {
        return returnSyntax;
    }

}
