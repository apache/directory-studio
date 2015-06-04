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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;


/**
 * The TimeLimitDialog is used to edit the TimeLimit parameter<br/>
 * The TimeLimit grammar is :
 * <pre>
 * time      ::= 'time' timeLimit time-e
 * time-e    ::= 'time' timeLimit time-e | e
 * timeLimit ::= '.soft=' limit | '.hard=' hardLimit | '=' limit
 * limit     ::= 'unlimited' | 'none' | INT
 * hardLimit ::= 'soft' | limit
 * </pre>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +-------------------------------------------------------+
 * | Time Limit                                            |
 * | .---------------------------------------------------. |
 * | | Soft Limit :  [----------]  [] Unlimited          | |
 * | |                                                   | |
 * | | Hard Limit :  [----------]  [] Unlimited  [] Soft | |
 * | |                                                   | |
 * | | Global :      [----------]  [] Unlimited          | |
 * | '---------------------------------------------------' |
 * | Resulting Time Limit                                  |
 * | .---------------------------------------------------. |
 * | | Time Limit  : </////////////////////////////////> | |
 * | '---------------------------------------------------' |
 * |                                                       |
 * |  (Cancel)                                      (OK)   |
 * +-------------------------------------------------------+
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
public class TimeLimitDialog extends AbstractLimitDialog
{
    /**
     * Create a new instance of the TimeLimitDialog
     * 
     * @param parentShell The parent Shell
     */
    public TimeLimitDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Create a new instance of the TimeLimitDialog
     * 
     * @param parentShell The parent Shell
     * @param timeLimitStr The instance containing the timeLimit data
     */
    public TimeLimitDialog( Shell parentShell, String timeLimitStr )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        
        limitWrapper = new TimeLimitWrapper( timeLimitStr );
    }
    
    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Time Limit" );
    }


    /**
     * Create the Dialog for TimeLimit :
     * <pre>
     * +-------------------------------------------------------+
     * | Time Limit                                            |
     * | .---------------------------------------------------. |
     * | | Soft Limit :  [----------]  [] Unlimited          | |
     * | |                                                   | |
     * | | Hard Limit :  [----------]  [] Unlimited  [] Soft | |
     * | |                                                   | |
     * | | Global :      [----------]  [] Unlimited          | |
     * | '---------------------------------------------------' |
     * | Resulting Time Limit                                  |
     * | .---------------------------------------------------. |
     * | | Time Limit  : </////////////////////////////////> | |
     * | '---------------------------------------------------' |
     * |                                                       |
     * |  (Cancel)                                      (OK)   |
     * +-------------------------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createTimeLimitEditGroup( composite );
        createTimeLimitShowGroup( composite );

        initFromLimit();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the TimeLimit input group. This is the part of the dialog
     * where one can insert the TimeLimit values
     * 
     * <pre>
     *  TcpBuffer Input
     * .---------------------------------------------------.
     * | Soft Limit :  [----------]  [] Unlimited          |
     * |                                                   |
     * | Hard Limit :  [----------]  [] Unlimited  [] Soft |
     * |                                                   |
     * | Global :      [----------]  [] Unlimited          |
     * '---------------------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createTimeLimitEditGroup( Composite parent )
    {
        // TimeLimit Group
        Group timeLimitGroup = BaseWidgetUtils.createGroup( parent, "Time Limit input", 1 );
        GridLayout timeLimitGridLayout = new GridLayout( 6, false );
        timeLimitGroup.setLayout( timeLimitGridLayout );
        timeLimitGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SoftLimit Text
        BaseWidgetUtils.createLabel( timeLimitGroup, "Soft Limit :", 1 );
        softLimitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        softLimitText.addModifyListener( softLimitTextListener );

        // Soft Limit unlimited checkbox Button
        softUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Unlimited", 2 );
        softUnlimitedCheckbox.addSelectionListener( softUnlimitedCheckboxSelectionListener );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( timeLimitGroup, "", 2 );

        // HardLimit Text
        BaseWidgetUtils.createLabel( timeLimitGroup, "Hard Limit :", 1 );
        hardLimitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        hardLimitText.addModifyListener( hardLimitTextListener );

        // Hard Limit unlimited checkbox Button
        hardUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Unlimited", 2 );
        hardUnlimitedCheckbox.addSelectionListener( hardUnlimitedCheckboxSelectionListener );

        // HardLimit soft checkbox Button
        hardSoftCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Soft", 2 );
        hardSoftCheckbox.addSelectionListener( hardSoftCheckboxSelectionListener );

        // GlobalLimit Text
        BaseWidgetUtils.createLabel( timeLimitGroup, "Global Limit :", 1 );
        globalLimitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        globalLimitText.addModifyListener( globalLimitTextListener );

        // GLobal Limit unlimited checkbox Button
        globalUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Unlimited", 2 );
        globalUnlimitedCheckbox.addSelectionListener( globalUnlimitedCheckboxSelectionListener );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( timeLimitGroup, "", 2 );
    }


    /**
     * Creates the TimeLimit show group. This is the part of the dialog
     * where the real TimeLimit is shown, or an error message if the TimeLimit
     * is invalid.
     * 
     * <pre>
     * Resulting Time Limit
     * .------------------------------------.
     * | Time Limit : <///////////////////> |
     * '------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createTimeLimitShowGroup( Composite parent )
    {
        // TimeLimit Group
        Group timeLimitGroup = BaseWidgetUtils.createGroup( parent, "Resulting Time Limit", 1 );
        GridLayout timeLimitGroupGridLayout = new GridLayout( 2, false );
        timeLimitGroup.setLayout( timeLimitGroupGridLayout );
        timeLimitGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // TimeLimit Text
        limitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        limitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        limitText.setEditable( false );
    }
}
