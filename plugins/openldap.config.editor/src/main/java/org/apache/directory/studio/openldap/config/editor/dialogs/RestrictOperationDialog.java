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
import org.apache.directory.studio.openldap.common.ui.model.RestrictOperationEnum;


/**
 * The RestrictOperationDialog is used to select one restricted operation. The possible
 * operations are :
 * <ul>
 * <li>add</li>
 * <li>all</li>
 * <li>bind</li>
 * <li>compare</li>
 * <li>delete</li>
 * <li>extended</li>
 * <li>extended=1.3.6.1.4.1.1466.20037</li>
 * <li>extended=1.3.6.1.4.1.4203.1.11.1</li>
 * <li>extended=1.3.6.1.4.1.4203.1.11.3</li>
 * <li>extended=1.3.6.1.1.8</li>
 * <li>modify</li>
 * <li>modrdn</li>
 * <li>read</li>
 * <li>rename</li>
 * <li>search</li>
 * <li>write</li>
 * </ul>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +--------------------------------------------------------------------+
 * | Restricted Operation                                               |
 * | .----------------------------------------------------------------. |
 * | | add :    []  all :    []  bind :      []  compare :         [] | |
 * | | delete : []  extended []  START_TLS : []  MODIFY_PASSWORD : [] | |
 * | | WHOAMI : []  CANCEL : []  modify :    []  modrdn :          [] | |
 * | | read :   []  rename : []  search :    []  write :           [] | |
 * | '----------------------------------------------------------------' |
 * |                                                                    |
 * |  (Cancel)                                                    (OK)  |
 * +--------------------------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RestrictOperationDialog extends AddEditDialog<RestrictOperationEnum>
{
    /** The array of buttons */
    private Button[] restrictOperationCheckboxes = new Button[16];
    
    /** The already selected Restricted Operations */
    List<RestrictOperationEnum> operations = new ArrayList<>();
    
    /**
     * Create a new instance of the RestrictOperationDialog
     * 
     * @param parentShell The parent Shell
     */
    public RestrictOperationDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }
    
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( Messages.getString( "RestrictOperation.Title" ) );
    }
    

    /**
     * The listener in charge of exposing the changes when some checkbox is selected
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            
            if ( object instanceof Button )
            {
                Button selectedCheckbox = (Button)object;
                
                for ( int i = 1; i < restrictOperationCheckboxes.length; i++ )
                {
                    if ( selectedCheckbox == restrictOperationCheckboxes[i] )
                    {
                        setEditedElement( RestrictOperationEnum.getOperation( i ) );
                    }
                    else if ( restrictOperationCheckboxes[i].isEnabled() )
                    {
                        restrictOperationCheckboxes[i].setSelection( false );
                    }
                }
            }
        }
    };


    /**
     * Create the Dialog for RestrictOperation :
     * <pre>
     * +--------------------------------------------------------------------+
     * | Restricted Operation                                               |
     * | .----------------------------------------------------------------. |
     * | | add :    []  all :    []  bind :      []  compare :         [] | |
     * | | delete : []  extended []  START_TLS : []  MODIFY_PASSWORD : [] | |
     * | | WHOAMI : []  CANCEL : []  modify :    []  modrdn :          [] | |
     * | | read :   []  rename : []  search :    []  write :           [] | |
     * | '----------------------------------------------------------------' |
     * |                                                                    |
     * |  (Cancel)                                                    (OK)  |
     * +--------------------------------------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );
        
        createRestrictOperationEditGroup( composite );
        initDialog();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the RestrictOperation input group.
     * 
     * <pre>
     * Restricted Operation
     * .----------------------------------------------------------------.
     * | add :    []  all :    []  bind :      []  compare :         [] |
     * | delete : []  extended []  START_TLS : []  MODIFY_PASSWORD : [] |
     * | WHOAMI : []  CANCEL : []  modify :    []  modrdn :          [] |
     * | read :   []  rename : []  search :    []  write :           [] |
     * '----------------------------------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createRestrictOperationEditGroup( Composite parent )
    {
        // Require Condition Group
        Group restrictOperationGroup = BaseWidgetUtils.createGroup( parent, "", 2 );
        GridLayout restrictOperationGridLayout = new GridLayout( 2, false );
        restrictOperationGroup.setLayout( restrictOperationGridLayout );
        restrictOperationGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // The various buttons
        for ( int i = 1; i < restrictOperationCheckboxes.length; i++ )
        {
            String restrictOperation = RestrictOperationEnum.getOperation( i ).getExternalName();
            restrictOperationCheckboxes[i] = BaseWidgetUtils.createCheckbox( restrictOperationGroup, restrictOperation, 1 );
            restrictOperationCheckboxes[i].addSelectionListener( checkboxSelectionListener );
        }
    }
    
    
    protected void initDialog()
    {
        List<RestrictOperationEnum> elements = getElements();
        boolean allSelected = true;
        okDisabled = false;

        for ( int i = 1; i < restrictOperationCheckboxes.length; i++ )
        {
            RestrictOperationEnum value = RestrictOperationEnum.getRestrictOperation( restrictOperationCheckboxes[i].getText() );
            
            // Disable the Conditions already selected
            if ( elements.contains( value ) )
            {
                restrictOperationCheckboxes[i].setSelection( true );
                restrictOperationCheckboxes[i].setEnabled( false );
            }
            else
            {
                allSelected = false;
            }
        }
        
        if ( allSelected )
        {
            // Disable the OK button
            okDisabled = true;
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addNewElement()
    {
        // Default to none
        setEditedElement( RestrictOperationEnum.UNKNOWN );
    }
}
