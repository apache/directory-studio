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
import org.apache.directory.studio.openldap.common.ui.model.RequireConditionEnum;


/**
 * The RequireConditionDialog is used to select one required condition. The possible
 * features are :
 * <ul>
 * <li>authc</li>
 * <li>bind</li>
 * <li>LDAPv3</li>
 * <li>none</li>
 * <li>sasl</li>
 * <li>strong</li>
 * </ul>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +-----------------------------+
 * | Required condition          |
 * | .-------------------------. |
 * | | authc :  [ ]  bind : [] | |
 * | | LDAPv3 : [ ]  sasl : [] | |
 * | | strong : [ ]  none : [] | |
 * | '-------------------------' |
 * |                             |
 * |  (Cancel)            (OK)   |
 * +-----------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class RequireConditionDialog extends AddEditDialog<RequireConditionEnum>
{
    /** The array of buttons */
    private Button[] requireConditionCheckboxes = new Button[5];
    
    /** The already selected Required Conditions */
    List<RequireConditionEnum> features = new ArrayList<RequireConditionEnum>();
    
    /**
     * Create a new instance of the RequireConditionDialog
     * 
     * @param parentShell The parent Shell
     */
    public RequireConditionDialog( Shell parentShell )
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
        shell.setText( Messages.getString( "RequireCondition.Title" ) );
    }
    

    /**
     * The listener in charge of exposing the changes when some checkbox is selected
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            
            if ( object instanceof Button )
            {
                Button selectedCheckbox = (Button)object;
                
                for ( int i = 1; i < requireConditionCheckboxes.length; i++ )
                {
                    if ( selectedCheckbox == requireConditionCheckboxes[i] )
                    {
                        setEditedElement( RequireConditionEnum.getCondition( i ) );
                    }
                    else if ( requireConditionCheckboxes[i].isEnabled() )
                    {
                        requireConditionCheckboxes[i].setSelection( false );
                    }
                }
            }
        }
    };


    /**
     * Create the Dialog for RequireCondition :
     * <pre>
     * +-----------------------------+
     * | Required condition          |
     * | .-------------------------. |
     * | | authc :  [ ]  bind : [] | |
     * | | LDAPv3 : [ ]  sasl : [] | |
     * | | strong : [ ]  none : [] | |
     * | '-------------------------' |
     * |                             |
     * |  (Cancel)            (OK)   |
     * +-----------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );
        
        createRequireConditionEditGroup( composite );
        initDialog();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the RequireCondition input group.
     * 
     * <pre>
     * Required condition
     * .-------------------------.
     * | authc :  [ ]  bind : [] |
     * | LDAPv3 : [ ]  sasl : [] |
     * | strong : [ ]  none : [] |
     * '-------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createRequireConditionEditGroup( Composite parent )
    {
        // Require Condition Group
        Group requireConditionGroup = BaseWidgetUtils.createGroup( parent, "", 2 );
        GridLayout requireConditionGridLayout = new GridLayout( 2, false );
        requireConditionGroup.setLayout( requireConditionGridLayout );
        requireConditionGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // The various buttons
        for ( int i = 1; i < requireConditionCheckboxes.length; i++ )
        {
            String requireCondition = RequireConditionEnum.getCondition( i ).getName();
            requireConditionCheckboxes[i] = BaseWidgetUtils.createCheckbox( requireConditionGroup, requireCondition, 1 );
            requireConditionCheckboxes[i].addSelectionListener( checkboxSelectionListener );
        }
    }
    
    
    protected void initDialog()
    {
        List<RequireConditionEnum> elements = getElements();
        
        for ( int i = 1; i < requireConditionCheckboxes.length; i++ )
        {
            RequireConditionEnum value = RequireConditionEnum.getCondition( requireConditionCheckboxes[i].getText() );
            
            // Disable the Conditions already selected
            if ( elements.contains( value ) )
            {
                requireConditionCheckboxes[i].setSelection( true );
                requireConditionCheckboxes[i].setEnabled( false );
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
        setEditedElement( RequireConditionEnum.UNKNOWN );
    }
}
