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


import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.common.ui.CommonUIPlugin;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.SizeLimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;


/**
 * The LimitDialog is used to edit the size and time limit parameter<br/>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +--------------------------------------------------------------------------+
 * | Limit                                                                    |
 * | .----------------------------------------------------------------------. |
 * | | (o) Size Limit  : [                                      ] (Edit...) | |
 * | | (o) TimeLimit :   [                                      ] (Edit...) | |
 * | '----------------------------------------------------------------------' |
 * |                                                                          |
 * |  (Cancel)                                                         (OK)   |
 * +--------------------------------------------------------------------------+
 * </pre>
 * 
 * A second option for the Dialog would be like :
 * 
 * +--------------------------------------------------------------------------+
 * | Limit                                                                    |
 * | .----------------------------------------------------------------------. |
 * | |   (o) Size Limit                  (o) TimeLimit                      | |
 * | '----------------------------------------------------------------------' |
 * ............................................................................
 * 
 * SizeLimit :
 * ............................................................................
 * | .----------------------------------------------------------------------. |
 * | | Soft Limit :          [----------]  [] Unlimited                     | |
 * | |                                                                      | |
 * | | Hard Limit :          [----------]  [] Unlimited [] Soft             | |
 * | |                                                                      | |
 * | | Global Limit :        [----------]  [] Unlimited                     | |
 * | |                                                                      | |
 * | | Unchecked Limit :     [----------]  [] Unlimited [] Disabled         | |
 * | |                                                                      | |
 * | | Paged Results Limit : [----------]  [] Unlimited [] No Estimate      | |
 * | |                                                                      | |
 * | | Paged Results Total : [----------]  [] Unlimited [] Disabled [] Hard | |
 * | '----------------------------------------------------------------------' |
 * ............................................................................
 * TimeLimit :
 * ............................................................................
 * | .----------------------------------------------------------------------. |
 * | | Soft Limit :  [----------]  [] Unlimited                             | |
 * | |                                                                      | |
 * | | Hard Limit :  [----------]  [] Unlimited  [] Soft                    | |
 * | |                                                                      | |
 * | | Global :      [----------]  [] Unlimited                             | |
 * | '----------------------------------------------------------------------' |
 * ............................................................................
 * End :
 * ............................................................................
 * | Resulting Limit                                                          |
 * | .----------------------------------------------------------------------. |
 * | | <//////////////////////////////////////////////////////////////////> | |
 * | '----------------------------------------------------------------------' |
 * |                                                                          |
 * |  (Cancel)                                                         (OK)   |
 * +--------------------------------------------------------------------------+
 * </pre>
 * 
 * But this would mean a duplication of code.
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SizeTimeLimitDialog extends AddEditDialog<LimitWrapper>
{
    /** The TimeLimit radio button */
    private Button timeLimitButton;
    
    /** The Text that contains the TimeLimit (either as typed or as built from the TimeLimitDialog) */
    private Text timeLimitText;
    
    /** A Button used to edit the TimeLimit value */
    private Button timeLimitEditButton;
    
    /** The SizeLimit radio button */
    private Button sizeLimitButton;
    
    /** The Text that contains the SizeLimit (either as typed or as built from the SizeLimitDialog) */
    private Text sizeLimitText;
    
    /** A Button used to edit the SizeLimit value */
    private Button sizeLimitEditButton;
    
    /**
     * Listeners for the Selector radioButtons. It will enable or disable the dnSpec or Group accordingly
     * to the selection.
     **/ 
    private SelectionListener sizeTimeButtonsSelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent event )
        {
            if ( event.getSource() instanceof Button )
            {
                Button button = (Button)event.getSource();
                
                if ( button == sizeLimitButton )
                {
                    if ( button.getSelection() )
                    {
                        setEditedElement( new SizeLimitWrapper( "" ) );

                        // Enable the SizeLimit elements, disable the TimeLimit ones
                        sizeLimitEditButton.setEnabled( true );
                        sizeLimitText.setEnabled( true );
                        timeLimitEditButton.setEnabled( false );
                        timeLimitText.setEnabled( false );
                        timeLimitText.clearSelection();
                    }
                }
                else
                {
                    setEditedElement( new TimeLimitWrapper( "" ) );
                    
                    // Enable the TimeLimit elements, disable the SizeLimit ones
                    timeLimitEditButton.setEnabled( true );
                    timeLimitText.setEnabled( true );
                    sizeLimitEditButton.setEnabled( false );
                    sizeLimitText.setEnabled( false );
                    sizeLimitText.clearSelection();
                }
            }
        }
    };
    
    
    /**
     * The listener for the sizeLimit Text
     */
    private SelectionListener sizeLimitEditSelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            SizeLimitDialog dialog = new SizeLimitDialog( sizeLimitText.getShell(), sizeLimitText.getText() );

            if ( dialog.open() == OverlayDialog.OK )
            {
                String newSizeLimitStr = dialog.getNewLimit();
                
                if ( newSizeLimitStr != null )
                {
                    sizeLimitText.setText( newSizeLimitStr );
                }
            }
        }
    };
    
    
    /**
     * The listener for the timeLimit Text
     */
    private SelectionListener timeLimitEditSelectionListener = new SelectionAdapter()
    {
        @Override
        public void widgetSelected( SelectionEvent e )
        {
            TimeLimitDialog dialog = new TimeLimitDialog( timeLimitText.getShell(), timeLimitText.getText() );

            if ( dialog.open() == OverlayDialog.OK )
            {
                String newTimeLimitStr = dialog.getNewLimit();
                
                if ( newTimeLimitStr != null )
                {
                    timeLimitText.setText( newTimeLimitStr );
                }
            }
        }
    };

    
    protected ModifyListener sizeLimitTextListener = event ->
        {
            Button okButton = getButton( IDialogConstants.OK_ID );

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The String must be a valid SizeLimit
            String sizeLimitStr = sizeLimitText.getText();
            
            SizeLimitWrapper sizeLimitWrapper = new SizeLimitWrapper( sizeLimitStr );
            
            if ( sizeLimitWrapper.isValid() )
            {
                sizeLimitText.setForeground( CommonUIPlugin.getDefault().getColor( CommonUIConstants.DEFAULT_COLOR ) );
                setEditedElement( sizeLimitWrapper );
                okButton.setEnabled( true );
            }
            else
            {
                sizeLimitText.setForeground( CommonUIPlugin.getDefault().getColor( CommonUIConstants.ERROR_COLOR ) );
                okButton.setEnabled( false );
            }
        };

    
    protected ModifyListener timeLimitTextListener = event ->
        {
            Button okButton = getButton( IDialogConstants.OK_ID );

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The String must be a valid TimeLimit
            String timeLimitStr = timeLimitText.getText();
            
            TimeLimitWrapper timeLimitWrapper = new TimeLimitWrapper( timeLimitStr );
            
            if ( timeLimitWrapper.isValid() )
            {
                timeLimitText.setForeground( CommonUIPlugin.getDefault().getColor( CommonUIConstants.DEFAULT_COLOR ) );
                setEditedElement( timeLimitWrapper );
                okButton.setEnabled( true );
            }
            else
            {
                timeLimitText.setForeground( CommonUIPlugin.getDefault().getColor( CommonUIConstants.ERROR_COLOR ) );
                okButton.setEnabled( false );
            }
        };
    
    /**
     * Create a new instance of the SizeTimeLimitsDialog
     * 
     * @param parentShell The parent Shell
     */
    public SizeTimeLimitDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Create a new instance of the SizeTimeLimitDialog
     * 
     * @param parentShell The parent Shell
     * @param timeLimitStr The instance containing the Limits data
     */
    public SizeTimeLimitDialog( Shell parentShell, String limitStr )
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
        shell.setText( "Size/Time Limit" );
    }
    
    
    /**
     * Create the Dialog for TimeLimit :
     * <pre>
     * Limit
     * .----------------------------------------------------------------------.
     * | (o) Size Limit  : [                                      ] (Edit...) |
     * | (o) TimeLimit :   [                                      ] (Edit...) |
     * '----------------------------------------------------------------------'
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );
        
        // Create the selection group
        Group selectionGroup = BaseWidgetUtils.createGroup( parent, "Limit selection", 1 );
        GridLayout selectionGridLayout = new GridLayout( 3, false );
        selectionGroup.setLayout( selectionGridLayout );
        selectionGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SizeLimit button
        sizeLimitButton = BaseWidgetUtils.createRadiobutton( selectionGroup, "SizeLimit", 1 );
        sizeLimitButton.addSelectionListener( sizeTimeButtonsSelectionListener );
        
        // SizeLimit Text
        sizeLimitText = BaseWidgetUtils.createText( selectionGroup, "", 1 );
        sizeLimitText.addModifyListener( sizeLimitTextListener );

        // SizeLimit Edit button
        sizeLimitEditButton = BaseWidgetUtils.createButton( selectionGroup, "Edit...", 1 );
        sizeLimitEditButton.addSelectionListener( sizeLimitEditSelectionListener );

        // TimeLimit button
        timeLimitButton = BaseWidgetUtils.createRadiobutton( selectionGroup, "TimeLimit", 1 );
        timeLimitButton.addSelectionListener( sizeTimeButtonsSelectionListener );

        // TimeLimit Text
        timeLimitText = BaseWidgetUtils.createText( selectionGroup, "", 1 );
        timeLimitText.addModifyListener( timeLimitTextListener );

        // TimeLimit Edit button
        timeLimitEditButton = BaseWidgetUtils.createButton( selectionGroup, "Edit...", 1 );
        timeLimitEditButton.addSelectionListener( timeLimitEditSelectionListener );

        // create the SizeLimit
        initDialog();
        addListeners();
        
        applyDialogFont( composite );

        return composite;
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        /*
        softLimitText.addModifyListener( softLimitTextListener );
        softUnlimitedCheckbox.addSelectionListener( softUnlimitedCheckboxSelectionListener );
        hardLimitText.addModifyListener( hardLimitTextListener );
        hardUnlimitedCheckbox.addSelectionListener( hardUnlimitedCheckboxSelectionListener );
        hardSoftCheckbox.addSelectionListener( hardSoftCheckboxSelectionListener );
        globalLimitText.addModifyListener( globalLimitTextListener );
        globalUnlimitedCheckbox.addSelectionListener( globalUnlimitedCheckboxSelectionListener );
        */
    }


    @Override
    public void addNewElement()
    {
        setEditedElement( null );
    }


    /**
     * Initializes the UI from the Limit
     */
    protected void initDialog()
    {
        LimitWrapper editedElement = getEditedElement();
        
        if ( editedElement != null )
        {
            if ( editedElement instanceof SizeLimitWrapper )
            {
                sizeLimitButton.setSelection( true );
                
                // Enable the SizeLimit elements, disable the TimeLimit ones
                sizeLimitEditButton.setEnabled( true );
                sizeLimitText.setEnabled( true );
                sizeLimitText.setText( editedElement.toString() );
                timeLimitEditButton.setEnabled( false );
                timeLimitText.setEnabled( false );
            }
            else
            {
                timeLimitButton.setSelection( true );
                
                // Enable the TimeLimit elements, disable the SizeLimit ones
                timeLimitEditButton.setEnabled( true );
                timeLimitText.setEnabled( true );
                timeLimitText.setText( editedElement.toString() );
                sizeLimitEditButton.setEnabled( false );
                sizeLimitText.setEnabled( false );
            }
        }
        else
        {
            // Nothing selected, disable the Text and Button
            timeLimitEditButton.setEnabled( false );
            timeLimitText.setEnabled( false );
            timeLimitText.clearSelection();
            sizeLimitEditButton.setEnabled( false );
            sizeLimitText.setEnabled( false );
            sizeLimitText.clearSelection();
        }
    }
}
