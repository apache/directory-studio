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
import org.eclipse.jface.dialogs.Dialog;
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
public class SizeLimitDialog extends Dialog
{
    // UI widgets
    /** The SoftLimit Text and checkboxes */
    private Text softLimitText;
    private Button softUnlimitedCheckbox;
    
    /** The HardLimit Text and checkboxes */
    private Text hardLimitText;
    private Button hardUnlimitedCheckbox;
    private Button hardSoftCheckbox;
    
    /** The GlobalLimit Text and checkboxes */
    private Text globalLimitText;
    private Button globalUnlimitedCheckbox;
    
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

    /** The resulting SizeLimit Text, or an error message */
    private Text sizeLimitText;
    
    /** The SizeLimitWrapper */
    private SizeLimitWrapper sizeLimitWrapper;
    
    /** The modified SizeLimit, as a String */
    private String newSizeLimitStr;
    
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
        
        sizeLimitWrapper = new SizeLimitWrapper( sizeLimitStr );
    }
    
    
    /**
     * Check if the global SizeLimit is valid : 
     * the values must be numeric, or "unlimited" or "none" or "soft" (for the hard limit). They
     * also have to be >=0
     */
    private boolean isValid()
    {
        String softLimitStr = softLimitText.getText();
        String hardLimitStr = hardLimitText.getText();
        String globalLimitStr = globalLimitText.getText();
        
        if ( !Strings.isEmpty( softLimitStr ) )
        {
            if ( !SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( softLimitStr ) && 
                !SizeLimitWrapper.NONE_STR.equals( softLimitStr ) )
            {
                try
                {
                    if ( Integer.valueOf( softLimitStr ) < -1 )
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
        
        if ( !Strings.isEmpty( hardLimitStr ) )
        {
            if ( !SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( hardLimitStr ) && 
                !SizeLimitWrapper.NONE_STR.equals( hardLimitStr ) && 
                !SizeLimitWrapper.SOFT_STR.equalsIgnoreCase( hardLimitStr ) )
            {
                try
                {
                    if ( Integer.valueOf( hardLimitStr ) < -1 )
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
        
        if ( !Strings.isEmpty( globalLimitStr ) )
        {
            if ( !SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( globalLimitStr ) && 
                !SizeLimitWrapper.NONE_STR.equals( globalLimitStr ) )
            {
                try
                {
                    if ( Integer.valueOf( globalLimitStr ) < -1 )
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
     * The listener for the Soft Limit Text
     */
    private ModifyListener softLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = softLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean unlimited = false;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | INT | -1
            String softLimitStr = softLimitText.getText();

            if ( Strings.isEmpty( softLimitStr ) )
            {
                // Check the case we don't have anything
                sizeLimitWrapper.setSoftLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( softLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( softLimitStr ) ) 
            {
                sizeLimitWrapper.setSoftLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( softLimitStr );
                    
                    if ( value < SizeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED ;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        sizeLimitWrapper.setSoftLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        sizeLimitWrapper.setSoftLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED ;
                }
            }

            softUnlimitedCheckbox.setSelection( unlimited );
            softLimitText.setForeground( display.getSystemColor( color ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            
            // Update the Hard limit if the hardSoft checkbox is set
            if ( hardSoftCheckbox.getSelection() )
            {
                if ( Strings.isEmpty( softLimitStr ) )
                {
                    hardLimitText.setText( "" );
                }
                else
                {
                    hardLimitText.setText( softLimitStr );
                }
            }
            
            okButton.setEnabled( isValid() );
        }
    };
    
    
    
    
    /**
     * The listener for the Hard Limit Text
     */
    private ModifyListener hardLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = hardLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean unlimited = false;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | 'soft' | INT | -1
            String hardLimitStr = hardLimitText.getText();

            if ( Strings.isEmpty( hardLimitStr ) )
            {
                // Check the case we don't have anything
                sizeLimitWrapper.setHardLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( hardLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( hardLimitStr ) ) 
            {
                sizeLimitWrapper.setHardLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else if ( SizeLimitWrapper.SOFT_STR.equalsIgnoreCase( hardLimitStr ) ) 
            {
                sizeLimitWrapper.setHardLimit( sizeLimitWrapper.getSoftLimit() );
                unlimited = softUnlimitedCheckbox.getSelection();
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( hardLimitStr );
                    
                    if ( value < SizeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        sizeLimitWrapper.setHardLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        sizeLimitWrapper.setHardLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            // Udate the Soft checkbox
            if ( sizeLimitWrapper.getHardLimit() == null )
            {
                hardSoftCheckbox.setSelection( false );
            }
            else
            {
                hardSoftCheckbox.setSelection( sizeLimitWrapper.getHardLimit().equals( sizeLimitWrapper.getSoftLimit() ) );
            }
            
            hardUnlimitedCheckbox.setSelection( unlimited );
            hardLimitText.setForeground( display.getSystemColor( color ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener for the Global Limit Text
     */
    private ModifyListener globalLimitTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = globalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean unlimited = false;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | INT | -1
            String globalLimitStr = globalLimitText.getText();
            
            if ( Strings.isEmpty( globalLimitStr ) )
            {
                // Check the case we don't have anything
                sizeLimitWrapper.setGlobalLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( globalLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( globalLimitStr ) ) 
            {
                sizeLimitWrapper.setGlobalLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( globalLimitStr );
                    
                    if ( value < SizeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        sizeLimitWrapper.setGlobalLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        sizeLimitWrapper.setGlobalLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            sizeLimitText.setText( sizeLimitWrapper.toString() );
            globalLimitText.setForeground( display.getSystemColor( color ) );
            globalUnlimitedCheckbox.setSelection( unlimited );
            okButton.setEnabled( isValid() );
        }
    };
    
    

    
    
    
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
                sizeLimitWrapper.setUncheckedLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( uncheckedLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( uncheckedLimitStr ) ) 
            {
                sizeLimitWrapper.setUncheckedLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else if ( SizeLimitWrapper.DISABLED_STR.equalsIgnoreCase( uncheckedLimitStr ) )
            {
                sizeLimitWrapper.setUncheckedLimit( SizeLimitWrapper.PR_DISABLED );
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
                        sizeLimitWrapper.setUncheckedLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else if ( value == SizeLimitWrapper.UC_DISABLED )
                    {
                        sizeLimitWrapper.setUncheckedLimit( SizeLimitWrapper.UC_DISABLED );
                        disabled = true;
                    }
                    else
                    {
                        sizeLimitWrapper.setUncheckedLimit( value );
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
            sizeLimitText.setText( sizeLimitWrapper.toString() );
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
                sizeLimitWrapper.setPrLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( prLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( prLimitStr ) ) 
            {
                sizeLimitWrapper.setPrLimit( SizeLimitWrapper.UNLIMITED );
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
                        sizeLimitWrapper.setPrLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        sizeLimitWrapper.setPrLimit( value );
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
            sizeLimitText.setText( sizeLimitWrapper.toString() );
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
                sizeLimitWrapper.setPrTotalLimit( null );
            }
            else if ( SizeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( prTotalLimitStr ) || 
                SizeLimitWrapper.NONE_STR.equalsIgnoreCase( prTotalLimitStr ) ) 
            {
                sizeLimitWrapper.setPrTotalLimit( SizeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else if ( SizeLimitWrapper.HARD_STR.equalsIgnoreCase( prTotalLimitStr ) ) 
            {
                sizeLimitWrapper.setPrTotalLimit( SizeLimitWrapper.PR_HARD );
                hard = true;
            }
            else if ( SizeLimitWrapper.DISABLED_STR.equalsIgnoreCase( prTotalLimitStr ) )
            {
                sizeLimitWrapper.setPrTotalLimit( SizeLimitWrapper.PR_DISABLED );
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
                        sizeLimitWrapper.setPrTotalLimit( SizeLimitWrapper.PR_DISABLED );
                        disabled = true;
                    }
                    else if ( value == SizeLimitWrapper.UNLIMITED )
                    {
                        sizeLimitWrapper.setPrTotalLimit( SizeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else if ( value == SizeLimitWrapper.PR_HARD )
                    {
                        sizeLimitWrapper.setPrTotalLimit( SizeLimitWrapper.PR_HARD );
                    }
                    else
                    {
                        sizeLimitWrapper.setPrTotalLimit( value );
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
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };

    
    /**
     * The listener in charge of exposing the changes when the soft unlimited button is checked
     */
    private SelectionListener softUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = softLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( softUnlimitedCheckbox.getSelection() )
            {
                softLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                sizeLimitWrapper.setSoftLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                softLimitText.setText( "" );
                sizeLimitWrapper.setSoftLimit( null );
            }

            softLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the hard unlimited button is checked
     */
    private SelectionListener hardUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = hardLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( hardUnlimitedCheckbox.getSelection() )
            {
                hardLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                sizeLimitWrapper.setHardLimit( SizeLimitWrapper.UNLIMITED );
                hardSoftCheckbox.setSelection( false );
            }
            else
            {
                hardLimitText.setText( "" );
                sizeLimitWrapper.setHardLimit( null );
            }

            hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the hard unlimited button is checked
     */
    private SelectionListener hardSoftCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = hardLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( hardSoftCheckbox.getSelection() )
            {
                String softStr = softLimitText.getText();
                
                if ( softStr != null )
                {
                    hardLimitText.setText( softStr );
                }
                else
                {
                    hardLimitText.setText( "" );
                }
                
                sizeLimitWrapper.setHardLimit( sizeLimitWrapper.getSoftLimit() );
                hardUnlimitedCheckbox.setSelection( SizeLimitWrapper.UNLIMITED.equals( sizeLimitWrapper.getSoftLimit() ) );
            }
            else
            {
                hardLimitText.setText( "" );
                sizeLimitWrapper.setHardLimit( null );
            }

            hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the global unlimited button is checked
     */
    private SelectionListener globalUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = globalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( globalUnlimitedCheckbox.getSelection() )
            {
                globalLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                sizeLimitWrapper.setGlobalLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                globalLimitText.setText( "" );
                sizeLimitWrapper.setGlobalLimit( null );
            }

            globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
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
                sizeLimitWrapper.setUncheckedLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                uncheckedLimitText.setText( "" );
                sizeLimitWrapper.setUncheckedLimit( null );
            }

            uncheckedLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
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
                sizeLimitWrapper.setUncheckedLimit( SizeLimitWrapper.UC_DISABLED );
            }
            else
            {
                uncheckedLimitText.setText( "" );
                sizeLimitWrapper.setUncheckedLimit( null );
            }

            uncheckedLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the pr unlimted button is checked
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
                sizeLimitWrapper.setPrLimit( SizeLimitWrapper.UNLIMITED );
            }
            else
            {
                prLimitText.setText( "" );
                sizeLimitWrapper.setPrLimit( null );
            }

            prLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            sizeLimitText.setText( sizeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
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
     * Construct the new SizeLimit from what we have in the dialog
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        setNewSizeLimit( sizeLimitWrapper.toString() );
        super.okPressed();
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

        initFromSizeLimit();
        
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
        //prNoEstimateCheckbox.addSelectionListener( prNoEstimateCheckboxSelectionListener );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( sizeLimitGroup, "", 2 );

        //------------------------------------------------------------------------------------------------
        // Paged Results Search Text
        BaseWidgetUtils.createLabel( sizeLimitGroup, "Paged Results Total :", 1 );
        prTotalLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        prTotalLimitText.addModifyListener( prTotalLimitTextListener );

        // Paged Results Limit unlimited checkbox Button
        prTotalUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Unlimited", 2 );
        //prTotalUnlimitedCheckbox.addSelectionListener( prUnlimitedCheckboxSelectionListener );

        // Paged Results Limit disabled checkbox Button
        prTotalDisabledCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Disabled", 2 );
        //prTotalDisabledCheckbox.addSelectionListener( prDisabledCheckboxSelectionListener );

        // Paged Results Limit hard checkbox Button
        prTotalHardCheckbox = BaseWidgetUtils.createCheckbox( sizeLimitGroup, "Hard", 2 );
        //prTotalHardCheckbox.addSelectionListener( prDisabledCheckboxSelectionListener );
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
        sizeLimitText = BaseWidgetUtils.createText( sizeLimitGroup, "", 1 );
        sizeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        sizeLimitText.setEditable( false );
    }


    /**
     * Initializes the UI from the SizeLimit
     */
    private void initFromSizeLimit()
    {
        if ( sizeLimitWrapper != null )
        {
            // The SoftLimit
            Integer softLimit = sizeLimitWrapper.getSoftLimit();
            
            if ( softLimit == null )
            {
                softLimitText.setText( "" );
                softUnlimitedCheckbox.setSelection( false );
            }
            else if ( softLimit.equals( SizeLimitWrapper.UNLIMITED ) )
            {
                softLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                softUnlimitedCheckbox.setSelection( true );
            }
            else
            {
                softLimitText.setText( softLimit.toString() );
                softUnlimitedCheckbox.setSelection( false );
            }
            
            // The HardLimit
            Integer hardLimit = sizeLimitWrapper.getHardLimit();
            
            if ( hardLimit == null )
            {
                hardLimitText.setText( "" );
                hardUnlimitedCheckbox.setSelection( false );
                hardSoftCheckbox.setSelection( false );
            }
            else if ( hardLimit.equals( SizeLimitWrapper.UNLIMITED ) )
            {
                hardLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                hardUnlimitedCheckbox.setSelection( true );
                hardSoftCheckbox.setSelection( false );
            }
            else if ( hardLimit.equals( SizeLimitWrapper.HARD_SOFT ) )
            {
                hardLimitText.setText( SizeLimitWrapper.SOFT_STR );
                hardUnlimitedCheckbox.setSelection( false );
                hardSoftCheckbox.setSelection( true );
            }
            else
            {
                hardLimitText.setText( hardLimit.toString() );
                hardUnlimitedCheckbox.setSelection( false );
                hardSoftCheckbox.setSelection( false );
            }
            
            // The GlobalLimit
            Integer globalLimit = sizeLimitWrapper.getGlobalLimit();
            
            if ( globalLimit == null )
            {
                globalLimitText.setText( "" );
                globalUnlimitedCheckbox.setSelection( false );
            }
            else if ( globalLimit.equals( SizeLimitWrapper.UNLIMITED ) )
            {
                globalLimitText.setText( SizeLimitWrapper.UNLIMITED_STR );
                globalUnlimitedCheckbox.setSelection( true );
            }
            else
            {
                globalLimitText.setText( globalLimit.toString() );
                globalUnlimitedCheckbox.setSelection( false );
            }
            
            sizeLimitText.setText( sizeLimitWrapper.toString() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getNewSizeLimit()
    {
        return newSizeLimitStr;
    }


    /**
     * {@inheritDoc}
     */
    public void setNewSizeLimit( String newSizeLimitStr )
    {
        this.newSizeLimitStr = newSizeLimitStr;
    }
}
