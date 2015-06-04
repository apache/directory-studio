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


import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.apache.directory.studio.openldap.config.editor.wrappers.SizeLimitWrapper;


/**
 * The SizeLimitDialog is used to edit the SizeLimit parameter<br/>
 * The SizeLimit grammar is :
 * <pre>
 * size      ::= 'size' sizeLimit size-e
 * size-e    ::= ' size' sizeLimit size-e | e
 * sizeLimit ::= '.soft=' limit | '.hard=' hardLimit | '.pr=' prLimit | '.prtotal=' prTLimit
 *                  | '.unchecked=' uLimit | '=' limit
 * limit     ::= 'unlimited' | 'none' | INT
 * hardLimit ::= 'soft' | limit
 * ulimit    ::= 'disabled' | limit
 * prLimit   ::= 'noEstimate' | limit
 * prTLimit  ::= ulimit | 'hard'
 * </pre>
 * 
 * The dialog overlay is like :
 * 
 * <pre>
 * +--------------------------------------------------------------------------+
 * | Size Limit                                                               |
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
 * | Resulting Size Limit                                                     |
 * | .----------------------------------------------------------------------. |
 * | | Size Limit  : <////////////////////////////////////////////////////> | |
 * | '----------------------------------------------------------------------' |
 * |                                                                          |
 * |  (Cancel)                                                          (OK)  |
 * +--------------------------------------------------------------------------+
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
public class SizeLimitDialog extends AbstractLimitDialog
{
    /** The UncheckedLimit Text and checkboxes */
    private Text uncheckedLimitText;
    private Button uncheckedUnlimitedCheckbox;
    private Button uncheckedDisabledCheckbox;
    
    /** The prLimit Text and checkboxes */
    private Text prLimitText;
    private Button prUnlimitedCheckbox;
    private Button prNoEstimateCheckbox;
    
    /** The prTotalLimit Text and checkboxes */
    private Text prTotalLimitText;
    private Button prTotalUnlimitedCheckbox;
    private Button prTotalDisabledCheckbox;
    private Button prTotalHardCheckbox;
    
    /**
     * Create a new instance of the SizeLimitDialog
     * 
     * @param parentShell The parent Shell
     */
    public SizeLimitDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Create a new instance of the SizeLimitDialog
     * 
     * @param parentShell The parent Shell
     * @param sizeLimitStr The instance containing the sizeLimit data
     */
    public SizeLimitDialog( Shell parentShell, String sizeLimitStr )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        
        limitWrapper = new SizeLimitWrapper( sizeLimitStr );
    }
    
    
    /**
     * Check if the global SizeLimit is valid : 
     * the values must be numeric, or "unlimited" or "none" or "soft" (for the hard limit). They
     * also have to be >=0
     */
    protected boolean isValid()
    {
        return super.isValid() && isValidUnchecked() && isValidPr() && isValidPrTotal();
    }
    
    
    /**
     * Check if the unchecked value is valid or not
     */
    protected boolean isValidUnchecked()
    {
        String uncheckedlLimitStr = uncheckedLimitText.getText();
        
        if ( !Strings.isEmpty( uncheckedlLimitStr ) )
        {
            if ( !SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( uncheckedlLimitStr ) && 
                 !SizeLimitWrapper.NONE_STR.equalsIgnoreCase( uncheckedlLimitStr ) &&
                 !SizeLimitWrapper.DISABLED_STR.equalsIgnoreCase( uncheckedlLimitStr ) )
            {
                try
                {
                    if ( Integer.parseInt( uncheckedlLimitStr ) < SizeLimitWrapper.UNLIMITED.intValue() )
                    {
                       return false;
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    
    /**
     * Check if the pr value is valid or not
     */
    protected boolean isValidPr()
    {
        String prLimitStr = prLimitText.getText();
        
        if ( !Strings.isEmpty( prLimitStr ) )
        {
            if ( !SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( prLimitStr ) && 
                 !SizeLimitWrapper.NONE_STR.equalsIgnoreCase( prLimitStr ) )
            {
                try
                {
                    if ( Integer.parseInt( prLimitStr ) < SizeLimitWrapper.UNLIMITED.intValue() )
                    {
                       return false;
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    
    /**
     * Check if the prtotal value is valid or not
     */
    protected boolean isValidPrTotal()
    {
        String prTotalLimitStr = prTotalLimitText.getText();
        
        if ( !Strings.isEmpty( prTotalLimitStr ) )
        {
            if ( !SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( prTotalLimitStr ) && 
                 !SizeLimitWrapper.NONE_STR.equalsIgnoreCase( prTotalLimitStr ) &&
                 !SizeLimitWrapper.HARD_STR.equalsIgnoreCase( prTotalLimitStr ) &&
                 !SizeLimitWrapper.DISABLED_STR.equalsIgnoreCase( prTotalLimitStr ) )
            {
                try
                {
                    if ( Integer.parseInt( prTotalLimitStr ) < SizeLimitWrapper.PR_DISABLED.intValue() )
                    {
                       return false;
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    
    /**
     * The listener for the Unchecked Limit Text
     */
    private ModifyListener uncheckedLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = uncheckedLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean unlimited = false;
            boolean disabled = false;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | 'disabled' | INT
            String uncheckedLimitStr = uncheckedLimitText.getText();
            
            if ( Strings.isEmpty( uncheckedLimitStr ) )
            {
                // Check the case we don't have anything
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( uncheckedLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( uncheckedLimitStr ) ) 
            {
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else if ( SizeLimitWrapper.DISABLED_STR.equalsIgnoreCase( uncheckedLimitStr ) )
            {
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( SizeLimitWrapper.PR_DISABLED );
                disabled = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( uncheckedLimitStr );
                    
                    if ( value < SizeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else if ( value == SizeLimitWrapper.UC_DISABLED )
                    {
                        ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( SizeLimitWrapper.UC_DISABLED );
                        disabled = true;
                    }
                    else
                    {
                        ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            uncheckedLimitText.setForeground( display.getSystemColor( color ) );
            uncheckedUnlimitedCheckbox.setSelection( unlimited );
            uncheckedDisabledCheckbox.setSelection( disabled );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };

    
    /**
     * The listener for the pr Limit Text
     */
    private ModifyListener prLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = prLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean unlimited = false;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | 'noEstimate' | INT 
            String prLimitStr = prLimitText.getText();
            
            if ( Strings.isEmpty( prLimitStr ) )
            {
                // Check the case we don't have anything
                ((SizeLimitWrapper)limitWrapper).setPrLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( prLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( prLimitStr ) ) 
            {
                ((SizeLimitWrapper)limitWrapper).setPrLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( prLimitStr );
                    
                    if ( value < SizeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        ((SizeLimitWrapper)limitWrapper).setPrLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        ((SizeLimitWrapper)limitWrapper).setPrLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            prLimitText.setForeground( display.getSystemColor( color ) );
            prUnlimitedCheckbox.setSelection( unlimited );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };

    
    
    
    /**
     * The listener for the prTotal Limit Text
     */
    private ModifyListener prTotalLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = prTotalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean unlimited = false;
            boolean disabled = false;
            boolean hard = false;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | 'disabled' | 'hard' | INT 
            String prTotalLimitStr = prTotalLimitText.getText();
            
            if ( Strings.isEmpty( prTotalLimitStr ) )
            {
                // Check the case we don't have anything
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( prTotalLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( prTotalLimitStr ) ) 
            {
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else if ( SizeLimitWrapper.HARD_STR.equalsIgnoreCase( prTotalLimitStr ) ) 
            {
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.PR_HARD );
                hard = true;
            }
            else if ( SizeLimitWrapper.DISABLED_STR.equalsIgnoreCase( prTotalLimitStr ) )
            {
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.PR_DISABLED );
                disabled = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( prTotalLimitStr );
                    
                    if ( value < SizeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == SizeLimitWrapper.PR_DISABLED )
                    {
                        ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.PR_DISABLED );
                        disabled = true;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else if ( value == SizeLimitWrapper.PR_HARD )
                    {
                        ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.PR_HARD );
                    }
                    else
                    {
                        ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            prTotalLimitText.setForeground( display.getSystemColor( color ) );
            prTotalUnlimitedCheckbox.setSelection( unlimited );
            prTotalDisabledCheckbox.setSelection( disabled );
            prTotalHardCheckbox.setSelection( hard );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the unchecked unlimited button is checked
     */
    private SelectionListener uncheckedUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = uncheckedLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( uncheckedUnlimitedCheckbox.getSelection() )
            {
                uncheckedLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                uncheckedDisabledCheckbox.setSelection( false );
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                uncheckedLimitText.setText( "" );
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( null );
            }

            uncheckedLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the unchecked disabled button is checked
     */
    private SelectionListener uncheckedDisabledCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = uncheckedLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( uncheckedDisabledCheckbox.getSelection() )
            {
                uncheckedLimitText.setText( SizeLimitWrapper.DISABLED_STR );
                uncheckedUnlimitedCheckbox.setSelection( false );
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( SizeLimitWrapper.UC_DISABLED );
            }
            else
            {
                uncheckedLimitText.setText( "" );
                ((SizeLimitWrapper)limitWrapper).setUncheckedLimit( null );
            }

            uncheckedLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the pr unlimited button is checked
     */
    private SelectionListener prUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = prLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( prUnlimitedCheckbox.getSelection() )
            {
                prLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                ((SizeLimitWrapper)limitWrapper).setPrLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                prLimitText.setText( "" );
                ((SizeLimitWrapper)limitWrapper).setPrLimit( null );
            }

            prLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the pr noEstimate button is checked
     */
    private SelectionListener prNoEstimateCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Button okButton = getButton( IDialogConstants.OK_ID );

            ((SizeLimitWrapper)limitWrapper).setNoEstimate( prNoEstimateCheckbox.getSelection() );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the prTotal unlimited button is checked
     */
    private SelectionListener prTotalUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = prTotalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( prTotalUnlimitedCheckbox.getSelection() )
            {
                prTotalLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                prTotalLimitText.setText( "" );
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( null );
            }

            prTotalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            prTotalDisabledCheckbox.setSelection( false );
            prTotalHardCheckbox.setSelection( false );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the prTotal disabled button is checked
     */
    private SelectionListener prTotalDisabledCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = prTotalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( prTotalDisabledCheckbox.getSelection() )
            {
                prTotalLimitText.setText( SizeLimitWrapper.DISABLED_STR );
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.PR_DISABLED );
            }
            else
            {
                prTotalLimitText.setText( "" );
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( null );
            }

            prTotalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            prTotalUnlimitedCheckbox.setSelection( false );
            prTotalHardCheckbox.setSelection( false );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the prTotal hard button is checked
     */
    private SelectionListener prTotalHardCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = prTotalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( prTotalHardCheckbox.getSelection() )
            {
                String hardStr = hardLimitText.getText();
                
                if ( Strings.isEmpty( hardStr ) )
                {
                    prTotalLimitText.setText( "" );
                }
                else
                {
                    prTotalLimitText.setText( SizeLimitWrapper.HARD_STR );
                }

                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( SizeLimitWrapper.PR_HARD );
            }
            else
            {
                prTotalLimitText.setText( "" );
                ((SizeLimitWrapper)limitWrapper).setPrTotalLimit( null );
            }

            if ( isValid() )
            {
                okButton.setEnabled( true );
                prTotalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else
            {
                okButton.setEnabled( false );
                prTotalLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
            
            prTotalUnlimitedCheckbox.setSelection( false );
            prTotalDisabledCheckbox.setSelection( false );
            limitText.setText( limitWrapper.toString() );
        }
    };

    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "Size Limit" );
    }


    /**
     * Create the Dialog for SizeLimit :
     * <pre>
     * +--------------------------------------------------------------------------+
     * | SizeLimit                                                                |
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
     * | Resulting SizeLimit                                                      |
     * | .----------------------------------------------------------------------. |
     * | | Size Limit  : <////////////////////////////////////////////////////> | |
     * | '----------------------------------------------------------------------' |
     * |                                                                          |
     * |  (Cancel)                                                          (OK)  |
     * +--------------------------------------------------------------------------+
     * </pre>
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createSizeLimitEditGroup( composite );
        createSizeLimitShowGroup( composite );

        initFromLimit();
        
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Creates the SizeLimit input group. This is the part of the dialog
     * where one can insert the SizeLimit values
     * 
     * <pre>
     * Size Limit
     * .----------------------------------------------------------------------.
     * | Soft Limit :          [----------]  [] Unlimited                     |
     * |                                                                      |
     * | Hard Limit :          [----------]  [] Unlimited [] Soft             |
     * |                                                                      |
     * | Global Limit :        [----------]  [] Unlimited                     |
     * |                                                                      |
     * | Unchecked Limit :     [----------]  [] Unlimited [] Disabled         |
     * |                                                                      |
     * | Paged Results Limit : [----------]  [] Unlimited [] No Estimate      |
     * |                                                                      |
     * | Paged Results Total : [----------]  [] Unlimited [] Disabled [] Hard |
     * '----------------------------------------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createSizeLimitEditGroup( Composite parent )
    {
        // SizeLimit Group
        Group sizeLimitGroup = BaseWidgetUtils.createGroup( parent, "Size Limit input", 1 );
        GridLayout sizeLimitGridLayout = new GridLayout( 8, false );
        sizeLimitGroup.setLayout( sizeLimitGridLayout );
        sizeLimitGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        //------------------------------------------------------------------------------------------------
        // SoftLimit Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Soft Limit :", 1 );
        softLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        softLimitText.addModifyListener( softLimitTextListener );

        // Soft Limit unlimited checkbox Button
        softUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        softUnlimitedCheckbox.addSelectionListener( softUnlimitedCheckboxSelectionListener );

        // 4 tabs to fill the line
        BaseWidgetUtils.createLabel( sizeLimitGroup, "", 4 );

        //------------------------------------------------------------------------------------------------
        // HardLimit Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Hard Limit :", 1 );
        hardLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        hardLimitText.addModifyListener( hardLimitTextListener );

        // Hard Limit unlimited checkbox Button
        hardUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        hardUnlimitedCheckbox.addSelectionListener( hardUnlimitedCheckboxSelectionListener );

        // HardLimit soft checkbox Button
        hardSoftCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Soft", 2 );
        hardSoftCheckbox.addSelectionListener( hardSoftCheckboxSelectionListener );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( sizeLimitGroup, "", 2 );

        //------------------------------------------------------------------------------------------------
        // GlobalLimit Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Global Limit :", 1 );
        globalLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        globalLimitText.addModifyListener( globalLimitTextListener );

        // GLobal Limit unlimited checkbox Button
        globalUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        globalUnlimitedCheckbox.addSelectionListener( globalUnlimitedCheckboxSelectionListener );

        // 4 tabs to fill the line
        BaseWidgetUtils.createLabel( sizeLimitGroup, "", 4 );

        //------------------------------------------------------------------------------------------------
        // Unchecked Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Unchecked Limit :", 1 );
        uncheckedLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        uncheckedLimitText.addModifyListener( uncheckedLimitTextListener );

        // Unchecked Limit unlimited checkbox Button
        uncheckedUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        uncheckedUnlimitedCheckbox.addSelectionListener( uncheckedUnlimitedCheckboxSelectionListener );

        // Unchecked Limit unlimited checkbox Button
        uncheckedDisabledCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Disabled", 2 );
        uncheckedDisabledCheckbox.addSelectionListener( uncheckedDisabledCheckboxSelectionListener );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( sizeLimitGroup, "", 2 );

        //------------------------------------------------------------------------------------------------
        // Paged Results Search Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Paged Results Limit :", 1 );
        prLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        prLimitText.addModifyListener( prLimitTextListener );

        // Paged Results Limit unlimited checkbox Button
        prUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        prUnlimitedCheckbox.addSelectionListener( prUnlimitedCheckboxSelectionListener );

        // Paged Results Limit noEstimate checkbox Button
        prNoEstimateCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "No Estimate", 2 );
        prNoEstimateCheckbox.addSelectionListener( prNoEstimateCheckboxSelectionListener );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( sizeLimitGroup, "", 2 );

        //------------------------------------------------------------------------------------------------
        // Paged Results Search Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Paged Results Total :", 1 );
        prTotalLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        prTotalLimitText.addModifyListener( prTotalLimitTextListener );

        // Paged Results Limit unlimited checkbox Button
        prTotalUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        prTotalUnlimitedCheckbox.addSelectionListener( prTotalUnlimitedCheckboxSelectionListener );

        // Paged Results Limit disabled checkbox Button
        prTotalDisabledCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Disabled", 2 );
        prTotalDisabledCheckbox.addSelectionListener( prTotalDisabledCheckboxSelectionListener );

        // Paged Results Limit hard checkbox Button
        prTotalHardCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Hard", 2 );
        prTotalHardCheckbox.addSelectionListener( prTotalHardCheckboxSelectionListener );
    }


    /**
     * Creates the SizeLimit show group. This is the part of the dialog
     * where the real SizeLimit is shown, or an error message if the SizeLimit
     * is invalid.
     * 
     * <pre>
     * Resulting Size Limit
     * .------------------------------------.
     * | Size Limit : <///////////////////> |
     * '------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createSizeLimitShowGroup( Composite parent )
    {
        // SizeLimit Group
        Group sizeLimitGroup = BaseWidgetUtils.createGroup( parent, "Resulting Size Limit", 1 );
        GridLayout sizeLimitGroupGridLayout = new GridLayout( 2, false );
        sizeLimitGroup.setLayout( sizeLimitGroupGridLayout );
        sizeLimitGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // SizeLimit Text
        limitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        limitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        limitText.setEditable( false );
    }


    /**
     * Initializes the UI from the Limit
     */
    protected void initFromLimit()
    {
        super.initFromLimit();
        
        // Deal with specific SizeLimit fields
        if ( limitWrapper != null )
        {
            SizeLimitWrapper sizeLimitWrapper = (SizeLimitWrapper)limitWrapper;
            
            // The UncheckedLimit
            Integer uncheckedLimit = sizeLimitWrapper.getUncheckedLimit();
            
            if ( uncheckedLimit == null )
            {
                uncheckedLimitText.setText( "" );
                uncheckedUnlimitedCheckbox.setSelection( false );
                uncheckedDisabledCheckbox.setSelection( false );
            }
            else if ( uncheckedLimit.equals( SizeLimitWrapper.UNLIMITED ) )
            {
                uncheckedLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                uncheckedUnlimitedCheckbox.setSelection( true );
                uncheckedDisabledCheckbox.setSelection( false );
            }
            else if ( uncheckedLimit.equals( SizeLimitWrapper.UC_DISABLED ) )
            {
                uncheckedLimitText.setText( SizeLimitWrapper.DISABLED_STR );
                uncheckedUnlimitedCheckbox.setSelection( false );
                uncheckedDisabledCheckbox.setSelection( true );
            }
            else
            {
                uncheckedLimitText.setText( uncheckedLimit.toString() );
                uncheckedUnlimitedCheckbox.setSelection( false );
                uncheckedDisabledCheckbox.setSelection( false );
            }
            
            // The pr Limit
            Integer prLimit = sizeLimitWrapper.getPrLimit();
            
            if ( prLimit == null )
            {
                prLimitText.setText( "" );
                prUnlimitedCheckbox.setSelection( false );
            }
            else if ( prLimit.equals( SizeLimitWrapper.UNLIMITED ) )
            {
                prLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                prUnlimitedCheckbox.setSelection( true );
            }
            else
            {
                prLimitText.setText( prLimit.toString() );
                prUnlimitedCheckbox.setSelection( false );
            }

            // The NoEstimate flag
            prNoEstimateCheckbox.setSelection( sizeLimitWrapper.isNoEstimate() );
            
            // The prTotal limit
            Integer prTotalLimit = sizeLimitWrapper.getPrTotalLimit();
            
            if ( prTotalLimit == null )
            {
                prTotalLimitText.setText( "" );
                prUnlimitedCheckbox.setSelection( false );
                prTotalDisabledCheckbox.setSelection( false );
                prTotalHardCheckbox.setSelection( false );
            }
            else if ( prTotalLimit.equals( SizeLimitWrapper.UNLIMITED ) )
            {
                prTotalLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                prTotalUnlimitedCheckbox.setSelection( true );
                prTotalDisabledCheckbox.setSelection( false );
                prTotalHardCheckbox.setSelection( false );
            }
            else if ( prTotalLimit.equals( SizeLimitWrapper.PR_DISABLED ) )
            {
                prTotalLimitText.setText( SizeLimitWrapper.DISABLED_STR );
                prTotalDisabledCheckbox.setSelection( true );
                prTotalUnlimitedCheckbox.setSelection( false );
                prTotalHardCheckbox.setSelection( false );
            }
            else if ( prTotalLimit.equals( SizeLimitWrapper.PR_HARD))
            {
                prTotalLimitText.setText(SizeLimitWrapper.HARD_STR );
                prTotalUnlimitedCheckbox.setSelection( false );
                prTotalDisabledCheckbox.setSelection( false );
                prTotalHardCheckbox.setSelection( true );
            }
            else
            {
                prTotalLimitText.setText( prTotalLimit.toString() );
                prTotalUnlimitedCheckbox.setSelection( false );
                prTotalDisabledCheckbox.setSelection( false );
                prTotalHardCheckbox.setSelection( false );
            }
        }
    }
}
