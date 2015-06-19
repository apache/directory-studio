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
import org.apache.directory.studio.openldap.common.ui.model.DisallowFeatureEnum;


/**
 * The DisallowFeatureDialog is used to select one feature to allow. The possible
 * features are :
 * <ul>
 * <li>bind_anon</li>
 * <li>bind_simple</li>
 * <li>tls_2_anon</li>
 * <li>tls_authc</li>
 * <li>proxy_authz_non_critical</li>
 * <li>dontusecopy_non_critical</li>
 * </ul>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +------------------------------------+
 * | Disallowed feature                 |
 * | .--------------------------------. |
 * | | bind_anon :                [ ] | |
 * | | bind_simple :              [ ] | |
 * | | tls_2_anon :               [ ] | |
 * | | tls_authc :                [ ] | |
 * | | proxy_authz_non_critical : [ ] | |
 * | | dontusecopy_non_critical : [ ] | |
 * | '--------------------------------' |
 * |                                    |
 * |  (Cancel)                    (OK)  |
 * +------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DisAllowFeatureDialog extends AddEditDialog<DisallowFeatureEnum>
{
    /** The array of buttons */
    private Button[] disallowFeatureCheckboxes = new Button[5];
    
    /** The already selected disaallowed features */
    List<DisallowFeatureEnum> features = new ArrayList<DisallowFeatureEnum>();
    
    /**
     * Create a new instance of the DisallowFeatureDialog
     * 
     * @param parentShell The parent Shell
     */
    public DisAllowFeatureDialog( Shell parentShell )
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
        shell.setText( Messages.getString( "DisallowFeature.Title" ) );
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
                
                for ( int i = 1; i < disallowFeatureCheckboxes.length; i++ )
                {
                    if ( selectedCheckbox == disallowFeatureCheckboxes[i] )
                    {
                        setEditedElement( DisallowFeatureEnum.getFeature( i ) );
                    }
                    else if ( disallowFeatureCheckboxes[i].isEnabled() )
                    {
                        disallowFeatureCheckboxes[i].setSelection( false );
                    }
                }
            }
        }
    };


    /**
     * Create the Dialog for DisallowFeature :
     * <pre>
     * +------------------------------------+
     * | Disallowed feature                 |
     * | .--------------------------------. |
     * | | bind_anon :                [ ] | |
     * | | bind_simple :              [ ] | |
     * | | tls_2_anon :               [ ] | |
     * | | tls_authc :                [ ] | |
     * | | proxy_authz_non_critical : [ ] | |
     * | | dontusecopy_non_critical : [ ] | |
     * | '--------------------------------' |
     * |                                    |
     * |  (Cancel)                    (OK)  |
     * +------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );
        
        createDisallowFeatureEditGroup( composite );
        initDialog();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the DisallowFeature input group. This is the part of the dialog
     * where one can insert the TimeLimit values
     * 
     * <pre>
     * Disallowed feature
     * .--------------------------------.
     * | bind_anon :                [ ] |
     * | bind_simple :              [ ] |
     * | tls_2_anon :               [ ] |
     * | tls_authc :                [ ] |
     * | proxy_authz_non_critical : [ ] |
     * | dontusecopy_non_critical : [ ] |
     * '--------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createDisallowFeatureEditGroup( Composite parent )
    {
        // Disallow Feature Group
        Group disallowFeatureGroup = BaseWidgetUtils.createGroup( parent, "", 1 );
        GridLayout disallowFeatureGridLayout = new GridLayout( 1, false );
        disallowFeatureGroup.setLayout( disallowFeatureGridLayout );
        disallowFeatureGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // The various buttons
        for ( int i = 1; i < disallowFeatureCheckboxes.length; i++ )
        {
            String disallowFeature = DisallowFeatureEnum.getFeature( i ).getName();
            disallowFeatureCheckboxes[i] = BaseWidgetUtils.createCheckbox( disallowFeatureGroup, disallowFeature, 1 );
            disallowFeatureCheckboxes[i].addSelectionListener( checkboxSelectionListener );
        }
    }
    
    
    protected void initDialog()
    {
        List<DisallowFeatureEnum> elements = getElements();
        
        for ( int i = 1; i < disallowFeatureCheckboxes.length; i++ )
        {
            DisallowFeatureEnum value = DisallowFeatureEnum.getFeature( disallowFeatureCheckboxes[i].getText() );
            
            // Disable the features already selected
            if ( elements.contains( value ) )
            {
                disallowFeatureCheckboxes[i].setSelection( true );
                disallowFeatureCheckboxes[i].setEnabled( false );
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
        setEditedElement( DisallowFeatureEnum.UNKNOWN );
    }
}
