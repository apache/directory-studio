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


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.openldap.common.ui.model.DatabaseTypeEnum;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


/**
 * A Dialog used to select the DatabaseType for newly created Database. It
 * offers a choice in :
 * <ul>
 * <li>None (not exposed)</li>
 * <li>Frontend DB</li>
 * <li>Config DB</li>
 * <li>BDB</li>
 * <li>DB Perl (not exposed)</li>
 * <li>DB_Socket</li>
 * <li>HDB</li>
 * <li>LDAP</li>
 * <li>LDIF</li>
 * <li>META</li>
 * <li>MDB</li>
 * <li>MONITOR</li>
 * <li>NDB (not exposed)</li>
 * <li>PASSWORD (not exposed)</li>
 * <li>RELAY</li>
 * <li>SHELL (not exposed)</li>
 * <li>SQL DB (not exposed)</li>
 * <li>NULL</li>
 * </ul>
 * 
 * Here is the layout :
 * <pre>
 * +-------------------------------------+
 * | Database Type                       |
 * | .---------------------------------. |
 * | |  [----------------------------] | |
 * | '---------------------------------' |
 * |                                     |
 * |  (Cancel)                    (OK)   |
 * +-------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DatabaseTypeDialog extends Dialog
{
    // UI widgets
    private Combo databaseTypeCombo;
    
    /** The selected Database type in the combo */
    private DatabaseTypeEnum selectedDatabaseType;
    
    /**
     * The listener in charge of exposing the changes when some buttons are checked
     */
    private SelectionListener databaseTypeSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            
            if ( object instanceof Combo )
            {
                Combo databaseTypeCombo = (Combo)object;
                Button okButton = getButton( IDialogConstants.OK_ID );
                
                DatabaseTypeEnum databaseType = DatabaseTypeEnum.getDatabaseType( databaseTypeCombo.getText() );
                selectedDatabaseType = databaseType;
                
                okButton.setEnabled( databaseType != DatabaseTypeEnum.NONE );
            }
        }
    };


    /**
     * Creates a new instance of DatabaseBaseDialog.
     * 
     * @param parentShell the parent shell
     */
    public DatabaseTypeDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Database Type" );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        // Do nothing if the selected feature is NONE
        if ( DatabaseTypeEnum.getDatabaseType( databaseTypeCombo.getText() ) != DatabaseTypeEnum.NONE )
        {
            super.okPressed();
        }
    }


    /**
     * Create the Dialog for the SSF :
     * <pre>
     * +-------------------------------------+
     * | Database Type                       |
     * | .---------------------------------. |
     * | |  [----------------------------] | |
     * | '---------------------------------' |
     * |                                     |
     * |  (Cancel)                    (OK)   |
     * +-------------------------------------+
     * </pre>
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        Group databaseTypeEditGroup = BaseWidgetUtils.createGroup( composite, "Database Type", 1 );
        databaseTypeEditGroup.setLayout( new GridLayout( 2, false ) );

        // The DatabaseTypes
        databaseTypeCombo = BaseWidgetUtils.createCombo( databaseTypeEditGroup, DatabaseTypeEnum.getNames(), 0, 2 );
        
        initDialog();
        addListeners();
        applyDialogFont( composite );
        
        return composite;
    }

    
    /**
     * Overriding the createButton method, so that we can disable the OK button if no feature is selected.
     * 
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) 
    {
        Button button = super.createButton( parent, id, label, defaultButton );
        
        // Disable the OK button at startup
        if ( id == IDialogConstants.OK_ID )
        {
            button.setEnabled( false );
        }
        
        return button;
    }
    
    
    /**
     * Initializes the Dialog with the values
     */
    protected void initDialog()
    {
        databaseTypeCombo.setText( DatabaseTypeEnum.NONE.getName() );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        databaseTypeCombo.addSelectionListener( databaseTypeSelectionListener );
    }
    
    
    /**
     * @return The selected DatabaseType
     */
    public DatabaseTypeEnum getDatabaseType()
    {
        return selectedDatabaseType;
    }
}
