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
package org.apache.directory.studio.openldap.config.editor.dialogs;


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.openldap.config.editor.wrappers.DnWrapper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * The DnValueDialog is used to edit a DN
 * 
 * <pre>
 * +---------------------------------------+
 * | .-----------------------------------. |
 * | | DN  : [                         ] | |
 * | '-----------------------------------' |
 * |                                       |
 * |  (cancel)                       (OK)  |
 * +---------------------------------------+
 * 
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DnValueDialog extends AddEditDialog<DnWrapper>
{
    // UI widgets
    /** The DN Text */
    private Text dnText;
    
    /**
     * Create a new instance of the String
     * 
     * @param parentShell The parent Shell
     */
    public DnValueDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }
    
    
    /**
     * The listener for the DN Text
     */
    private ModifyListener dnValueTextListener = event ->
        {
            Display display = dnText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            try
            {
                Dn dn = new Dn( dnText.getText() );
            
                getEditedElement().setDn( dn );
                okButton.setEnabled( true );
                dnText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            catch ( LdapInvalidDnException e1 )
            {
                okButton.setEnabled( false );
                dnText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
        };
    
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "DN" );
    }


    /**
     * Create the Dialog for DnValue :
     * <pre>
     * +---------------------------------------+
     * | .-----------------------------------. |
     * | | Dn  : [                         ] | |
     * | '-----------------------------------' |
     * |                                       |
     * |  (cancel)                       (OK)  |
     * +---------------------------------------+
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        // DnValue Group
        Group dnValueGroup = BaseWidgetUtils.createGroup( parent, null, 1 );
        GridLayout stringValueGroupGridLayout = new GridLayout( 2, false );
        dnValueGroup.setLayout( stringValueGroupGridLayout );
        dnValueGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // DN Text
        BaseWidgetUtils.createLabel( dnValueGroup, "DN :", 1 );
        dnText = BaseWidgetUtils.createText( dnValueGroup, "", 1 );
        dnText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        initDialog();
        addListeners();

        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Initializes the UI from the String
     */
    protected void initDialog()
    {
        DnWrapper editedElement = getEditedElement();
        
        if ( editedElement != null )
        {
            Dn dn = editedElement.getDn();
            
            if ( dn == null )
            {
                dnText.setText( "" );
            }
            else
            {
                dnText.setText( dn.toString() );
            }
        }
    }


    /**
     * Add a new Element that will be edited
     */
    public void addNewElement()
    {
        setEditedElement( new DnWrapper( Dn.EMPTY_DN ) );
    }


    public void addNewElement( DnWrapper editedElement )
    {
        // No need to clone, the Dn is immutable
        setEditedElement( editedElement );
    }

    
    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        dnText.addModifyListener( dnValueTextListener );
    }
}
