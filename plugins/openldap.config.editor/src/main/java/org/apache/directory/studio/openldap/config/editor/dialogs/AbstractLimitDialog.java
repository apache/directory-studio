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
import org.apache.directory.studio.common.ui.AddEditDialog;
import org.apache.directory.studio.openldap.config.editor.wrappers.AbstractLimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.LimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.SizeLimitWrapper;
import org.apache.directory.studio.openldap.config.editor.wrappers.TimeLimitWrapper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A class that share elements of configuration between the SizeLimitDialog
 * and TimeLimitDialog.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractLimitDialog<E> extends AddEditDialog<E>
{
    // UI widgets
    /** The SoftLimit Text and checkboxes */
    protected Text softLimitText;
    protected Button softUnlimitedCheckbox;
    
    /** The HardLimit Text and checkboxes */
    protected Text hardLimitText;
    protected Button hardUnlimitedCheckbox;
    protected Button hardSoftCheckbox;
    
    /** The GlobalLimit Text and checkboxes */
    protected Text globalLimitText;
    protected Button globalUnlimitedCheckbox;
    
    /** The resulting Limit Text, or an error message */
    protected Text limitText;
    
    /** The modified Limit, as a String */
    protected String newLimitStr;
    
    /** The wrapper (either time or size) used to store the parameters */
    //protected AbstractLimitWrapper limitWrapper;


    /**
     * Create a new instance of the TimeSizeLimitDialog
     * 
     * @param parentShell The parent Shell
     */
    protected AbstractLimitDialog( Shell parentShell )
    {
        super( parentShell );
    }
    
    
    /**
     * Check if the soft value is valid or not
     */
    protected boolean isValidSoft()
    {
        String softLimitStr = softLimitText.getText();
        
        if ( !Strings.isEmpty( softLimitStr ) )
        {
            if ( !TimeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( softLimitStr ) && 
                !TimeLimitWrapper.NONE_STR.equals( softLimitStr ) )
            {
                try
                {
                    if ( Integer.parseInt( softLimitStr ) < -1 )
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
     * Check if the hard value is valid or not
     */
    protected boolean isValidHard()
    {
        String hardLimitStr = hardLimitText.getText();
        
        if ( !Strings.isEmpty( hardLimitStr ) )
        {
            if ( !TimeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( hardLimitStr ) && 
                 !TimeLimitWrapper.NONE_STR.equals( hardLimitStr ) && 
                 !TimeLimitWrapper.SOFT_STR.equalsIgnoreCase( hardLimitStr ) )
            {
                try
                {
                    if ( Integer.parseInt( hardLimitStr ) < -1 )
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
     * Check if the global value is valid or not
     */
    protected boolean isValidGlobal()
    {
        String globalLimitStr = hardLimitText.getText();
        
        if ( !Strings.isEmpty( globalLimitStr ) )
        {
            if ( !TimeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( globalLimitStr ) && 
                 !TimeLimitWrapper.NONE_STR.equals( globalLimitStr ) )
            {
                try
                {
                    if ( Integer.parseInt( globalLimitStr ) < -1 )
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
     * Check if the global TimeLimit is valid : 
     * the values must be numeric, or "unlimited" or "none" or "soft" (for the hard limit). They
     * also have to be >=0
     */
    protected boolean isValid()
    {
        return isValidSoft() && isValidHard() && isValidGlobal();
    }
    
    
    /**
     * The listener for the Soft Limit Text
     */
    protected ModifyListener softLimitTextListener = new ModifyListener()
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
            LimitWrapper limitWrapper = ((LimitWrapper)getEditedElement());

            if ( Strings.isEmpty( softLimitStr ) )
            {
                // Check the case we don't have anything
                limitWrapper.setSoftLimit( null );
            }
            else if ( TimeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( softLimitStr ) || 
                TimeLimitWrapper.NONE_STR.equalsIgnoreCase( softLimitStr ) ) 
            {
                limitWrapper.setSoftLimit( TimeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( softLimitStr );
                    
                    if ( value < TimeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED ;
                    }
                    else if ( value == TimeLimitWrapper.UNLIMITED )
                    {
                        limitWrapper.setSoftLimit( TimeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        limitWrapper.setSoftLimit( value );
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
            limitText.setText( limitWrapper.toString() );
            
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
                    
                    // Use the same color than for the soft
                    Display displayHard = softLimitText.getDisplay();
                    hardLimitText.setForeground( displayHard.getSystemColor( color ) );
                }
            }
            
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener for the Hard Limit Text
     */
    protected ModifyListener hardLimitTextListener = new ModifyListener()
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
            LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();

            if ( Strings.isEmpty( hardLimitStr ) )
            {
                // Check the case we don't have anything
                limitWrapper.setHardLimit( null );
            }
            else if ( TimeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( hardLimitStr ) ||
                TimeLimitWrapper.NONE_STR.equalsIgnoreCase( hardLimitStr ) ) 
            {
                limitWrapper.setHardLimit( TimeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else if ( TimeLimitWrapper.SOFT_STR.equalsIgnoreCase( hardLimitStr ) ) 
            {
                limitWrapper.setHardLimit( limitWrapper.getSoftLimit() );
                unlimited = softUnlimitedCheckbox.getSelection();
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( hardLimitStr );
                    
                    if ( value < TimeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == TimeLimitWrapper.UNLIMITED )
                    {
                        limitWrapper.setHardLimit( TimeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        limitWrapper.setHardLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            hardUnlimitedCheckbox.setSelection( unlimited );
            hardLimitText.setForeground( display.getSystemColor( color ) );
            limitText.setText( limitWrapper.toString() );
            
            if ( isValidSoft() )
            { 
                okButton.setEnabled( true );
            }
            else
            {
                okButton.setEnabled( isValid() );
            }
        }
    };
    
    
    /**
     * The listener for the Global Limit Text
     */
    protected ModifyListener globalLimitTextListener = new ModifyListener()
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
            LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();

            if ( Strings.isEmpty( globalLimitStr ) )
            {
                // Check the case we don't have anything
                limitWrapper.setGlobalLimit( null );
            }
            else if ( TimeLimitWrapper.UNLIMITED_STR.equalsIgnoreCase( globalLimitStr ) || 
                TimeLimitWrapper.NONE_STR.equalsIgnoreCase( globalLimitStr ) ) 
            {
                limitWrapper.setGlobalLimit( TimeLimitWrapper.UNLIMITED );
                unlimited = true;
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( globalLimitStr );
                    
                    if ( value < TimeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        color = SWT.COLOR_RED;
                    }
                    else if ( value == TimeLimitWrapper.UNLIMITED )
                    {
                        limitWrapper.setGlobalLimit( TimeLimitWrapper.UNLIMITED );
                        unlimited = true;
                    }
                    else
                    {
                        limitWrapper.setGlobalLimit( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                }
            }

            limitText.setText( limitWrapper.toString() );
            globalLimitText.setForeground( display.getSystemColor( color ) );
            globalUnlimitedCheckbox.setSelection( unlimited );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the soft unlimited button is checked
     */
    protected SelectionListener softUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = softLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();

            if ( softUnlimitedCheckbox.getSelection() )
            {
                softLimitText.setText( TimeLimitWrapper.UNLIMITED_STR );
                limitWrapper.setSoftLimit( TimeLimitWrapper.UNLIMITED );
            }
            else
            {
                softLimitText.setText( "" );
                limitWrapper.setSoftLimit( null );
            }

            if ( isValidSoft() )
            {
                softLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else
            {
                softLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
            }
            
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the hard unlimited button is checked
     */
    protected SelectionListener hardUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = hardLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();

            if ( hardUnlimitedCheckbox.getSelection() )
            {
                hardLimitText.setText( TimeLimitWrapper.UNLIMITED_STR );
                limitWrapper.setHardLimit( TimeLimitWrapper.UNLIMITED );
                hardSoftCheckbox.setSelection( false );
                hardLimitText.setEnabled( true );
            }
            else
            {
                hardLimitText.setText( "" );
                limitWrapper.setHardLimit( null );
            }

            hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the hard unlimited button is checked.
     * We will disable the hardLimitText.
     */
    protected SelectionListener hardSoftCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = hardLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();

            if ( hardSoftCheckbox.getSelection() )
            {
                hardLimitText.setEnabled( false );
                String softStr = softLimitText.getText();
                
                if ( softStr != null )
                {
                    hardLimitText.setText( softStr );
                }
                else
                {
                    hardLimitText.setText( "" );
                }
                
                limitWrapper.setHardLimit( limitWrapper.getSoftLimit() );
                hardUnlimitedCheckbox.setSelection( TimeLimitWrapper.UNLIMITED.equals( limitWrapper.getSoftLimit() ) );

                if ( isValidSoft() )
                { 
                    hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                }
                else
                {
                    hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                }
            }
            else
            {
                hardLimitText.setText( "" );
                hardLimitText.setEnabled( true );
                limitWrapper.setHardLimit( null );
            }

            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the global unlimited button is checked
     */
    protected SelectionListener globalUnlimitedCheckboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = globalLimitText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();

            if ( globalUnlimitedCheckbox.getSelection() )
            {
                globalLimitText.setText( TimeLimitWrapper.UNLIMITED_STR );
                limitWrapper.setGlobalLimit( TimeLimitWrapper.UNLIMITED );
            }
            else
            {
                globalLimitText.setText( "" );
                limitWrapper.setGlobalLimit( null );
            }

            globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            limitText.setText( limitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * Construct the new TimeLimit from what we have in the dialog
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        newLimitStr = getEditedElement().toString();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    public String getNewLimit()
    {
        return newLimitStr;
    }


    /**
     * Initializes the UI from the Limit
     */
    protected void initDialog()
    {
        LimitWrapper limitWrapper = (LimitWrapper)getEditedElement();
        
        if ( limitWrapper != null )
        {
            // The SoftLimit
            Integer softLimit = limitWrapper.getSoftLimit();
            
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
            Integer hardLimit = limitWrapper.getHardLimit();
            
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
            Integer globalLimit = limitWrapper.getGlobalLimit();
            
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
            
            limitText.setText( limitWrapper.toString() );
        }
    }
}
