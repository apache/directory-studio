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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.apache.directory.studio.openldap.common.ui.model.PasswordHashEnum;


/**
 * The PasswordHashDialog is used to select one hash method. The possible
 * hash methods are :
 * <ul>
 * <li>{CLEARTEXT}</li>
 * <li>{CRYPT}</li>
 * <li>{LANMAN}</li>
 * <li>{MD5}</li>
 * <li>{SMD5}</li>
 * <li>{SHA}</li>
 * <li>{SSHA}</li>
 * <li>{UNIX}</li>
 * <li></li>
 * <li></li>
 * </ul>
 * </pre>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +-----------------------------------------+
 * | Password hash                           |
 * | .-------------------------------------. |
 * | | CLear Text : [ ]   Crypt :      [ ] | |
 * | | LANMAN :     [ ]   MD5 :        [ ] | |
 * | | SMD5 :       [ ]   SHA :        [ ] | |
 * | | SSHA :       [ ]   Unix :       [ ] | |
 * | '-------------------------------------' |
 * |                                         |
 * |  (Cancel)                         (OK)  |
 * +-----------------------------------------+
 * </pre>
 * 
 * A few rules :
 * <ul>
 * <li>When the global limit is set, the soft and hard limits are not used</li>
 * <li>When the Unlimited button is checked, the integer value is discarded</li>
 * <li>When the Soft checkbox for the hard limit is checked, the Global value is used </li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordHashDialog extends AddEditDialog<PasswordHashEnum>
{
    /** The array of buttons */
    private Button[] passwordHashCheckboxes = new Button[9];
    
    /** The already selected hashes */
    List<PasswordHashEnum> hashes = new ArrayList<PasswordHashEnum>();
    
    /**
     * Create a new instance of the PasswordHashDialog
     * 
     * @param parentShell The parent Shell
     */
    public PasswordHashDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }
    
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Password Hash" );
    }

    /**
     * The listener in charge of exposing the changes when some checkbox is selectionned
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            
            if ( object instanceof Button )
            {
                Button selectedCheckbox = (Button)object;
                
                for ( int i = 1; i < passwordHashCheckboxes.length; i++ )
                {
                    if ( selectedCheckbox == passwordHashCheckboxes[i] )
                    {
                        setNewElement( PasswordHashEnum.getPasswordHash( i ) );
                    }
                    else
                    {
                        passwordHashCheckboxes[i].setSelection( false );
                    }
                }
            }
        }
    };


    /**
     * Create the Dialog for PasswordHash :
     * <pre>
     * +-----------------------------------------+
     * | Password hash                           |
     * | .-------------------------------------. |
     * | | CLear Text : [ ]   Crypt :      [ ] | |
     * | | LANMAN :     [ ]   MD5 :        [ ] | |
     * | | SMD5 :       [ ]   SHA :        [ ] | |
     * | | SSHA :       [ ]   Unix :       [ ] | |
     * | '-------------------------------------' |
     * |                                         |
     * |  (Cancel)                         (OK)  |
     * +-----------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );
        
        createPasswordHashEditGroup( composite );
        initDialog();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the PasswordHash input group. This is the part of the dialog
     * where one can insert the TimeLimit values
     * 
     * <pre>
     * Password hash
     * .------------------.
     * | CLear Text : [ ] |
     * | Crypt :      [ ] |
     * | LANMAN :     [ ] |
     * | MD5 :        [ ] |
     * | SMD5 :       [ ] |
     * | SHA :        [ ] |
     * | SSHA :       [ ] |
     * | Unix :       [ ] |
     * '------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createPasswordHashEditGroup( Composite parent )
    {
        // Password Hash Group
        Group passwordHashGroup = BaseWidgetUtils.createGroup( parent, "", 2 );
        GridLayout passwordHashGridLayout = new GridLayout( 2, false );
        passwordHashGroup.setLayout( passwordHashGridLayout );
        passwordHashGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // The various buttons
        for ( int i = 1; i < passwordHashCheckboxes.length; i++ )
        {
            PasswordHashEnum passwordHash = PasswordHashEnum.getPasswordHash( i );
            Button button = BaseWidgetUtils.createCheckbox( passwordHashGroup, passwordHash.getName(), 1 );
            passwordHashCheckboxes[i] = button;
            passwordHashCheckboxes[i].addSelectionListener( checkboxSelectionListener );
            
            // Disable the hashes already selected
            if ( hashes.contains( passwordHashCheckboxes[i] ) )
            {
                passwordHashCheckboxes[i].setEnabled( false );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewElement()
    {
        // Default to none
        setNewElement( PasswordHashEnum.NO_CHOICE );
    }
}
