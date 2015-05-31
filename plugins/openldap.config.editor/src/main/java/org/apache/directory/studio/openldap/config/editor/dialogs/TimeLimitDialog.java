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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
 * | TimeLimit                                             |
 * | .---------------------------------------------------. |
 * | | Soft Limit :  [----------]  [] Unlimited          | |
 * | |                                                   | |
 * | | Hard Limit :  [----------]  [] Unlimited  [] Soft | |
 * | |                                                   | |
 * | | Global :      [----------]  [] Unlimited          | |
 * | '---------------------------------------------------' |
 * | Resulting TimeLimit                                   |
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
public class TimeLimitDialog extends Dialog
{
    // UI widgets
    /** The SoftLimit Text */
    private Text softLimitText;
    
    /** The HardLimit Text */
    private Text hardLimitText;
    
    /** The GlobalLimit Text */
    private Text globalLimitText;
    
    /** The unlimited checkboxes */
    private Button softUnlimitedCheckbox;
    private Button hardUnlimitedCheckbox;
    private Button globalUnlimitedCheckbox;
    
    /** The hard limit Soft checkbox */
    private Button hardSoftCheckbox;
    
    /** The resulting TimeLimit Text, or an error message */
    private Text timeLimitText;
    
    /** The TimeLimitWrapper */
    private TimeLimitWrapper timeLimitWrapper;
    
    /** The original TimeLimit, as a String */
    private String editedTimeLimitStr;
    
    /** The modified TimeLimit, as a String */
    private String newTimeLimitStr;
    
    // Some constants
    private static final String UNLIMITED_STR = "unlimited";
    private static final String NONE_STR = "none";
    private static final String SOFT_STR = "soft";


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
        
        setEditedTimeLimit( timeLimitStr );
    }
    
    
    /**
     * Check if the global TimeLimit is valid : 
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
            if ( !UNLIMITED_STR.equalsIgnoreCase( softLimitStr ) && !NONE_STR.equals( softLimitStr ) )
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
            if ( !UNLIMITED_STR.equalsIgnoreCase( hardLimitStr ) && !NONE_STR.equals( hardLimitStr ) && !SOFT_STR.equalsIgnoreCase( hardLimitStr ) )
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
            if ( !UNLIMITED_STR.equalsIgnoreCase( globalLimitStr ) && !NONE_STR.equals( globalLimitStr ) )
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
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The possible values are : 'unlimited' | 'none' | INT | -1
            String softLimitStr = softLimitText.getText();

            if ( UNLIMITED_STR.equalsIgnoreCase( softLimitStr ) || NONE_STR.equalsIgnoreCase( softLimitStr ) ) 
            {
                timeLimitWrapper.setSoftLimit( TimeLimitWrapper.UNLIMITED );
                softUnlimitedCheckbox.setSelection( true );
                softLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else
            {
                // An integer
                try
                {
                    Integer value = Integer.parseInt( softLimitStr );
                    
                    if ( value < TimeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        softLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    }
                    else if ( value == TimeLimitWrapper.UNLIMITED )
                    {
                        timeLimitWrapper.setSoftLimit( TimeLimitWrapper.UNLIMITED );
                        softUnlimitedCheckbox.setSelection( true );
                        softLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                    else
                    {
                        timeLimitWrapper.setSoftLimit( value );
                        softUnlimitedCheckbox.setSelection( false );
                        softLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    softLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                }
            }

            timeLimitText.setText( timeLimitWrapper.toString() );
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
                timeLimitWrapper.setHardLimit( null );
                hardUnlimitedCheckbox.setSelection( false );
                hardSoftCheckbox.setSelection( false );
                hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else if ( UNLIMITED_STR.equalsIgnoreCase( hardLimitStr ) || NONE_STR.equalsIgnoreCase( hardLimitStr ) ) 
            {
                timeLimitWrapper.setHardLimit( TimeLimitWrapper.UNLIMITED );
                hardUnlimitedCheckbox.setSelection( true );
                hardSoftCheckbox.setSelection( false );
                hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else if ( SOFT_STR.equalsIgnoreCase( hardLimitStr ) ) 
            {
                timeLimitWrapper.setHardLimit( timeLimitWrapper.getSoftLimit() );
                hardSoftCheckbox.setSelection( true );
                hardUnlimitedCheckbox.setSelection( softUnlimitedCheckbox.getSelection() );
                hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else
            {
                // An integer
                try
                {
                    Integer value = Integer.parseInt( hardLimitStr );
                    
                    if ( value < TimeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        hardUnlimitedCheckbox.setSelection( false );
                        hardSoftCheckbox.setSelection( false );
                        hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    }
                    else if ( value == TimeLimitWrapper.UNLIMITED )
                    {
                        timeLimitWrapper.setHardLimit( TimeLimitWrapper.UNLIMITED );
                        hardUnlimitedCheckbox.setSelection( true );
                        hardSoftCheckbox.setSelection( false );
                        hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                    else
                    {
                        timeLimitWrapper.setHardLimit( value );
                        hardUnlimitedCheckbox.setSelection( false );
                        hardSoftCheckbox.setSelection( false );
                        hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    hardLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    hardUnlimitedCheckbox.setSelection( false );
                    hardSoftCheckbox.setSelection( false );
                }
            }

            timeLimitText.setText( timeLimitWrapper.toString() );
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
                timeLimitWrapper.setGlobalLimit( null );
                globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else if ( UNLIMITED_STR.equalsIgnoreCase( globalLimitStr ) || NONE_STR.equalsIgnoreCase( globalLimitStr ) ) 
            {
                timeLimitWrapper.setGlobalLimit( TimeLimitWrapper.UNLIMITED );
                globalUnlimitedCheckbox.setSelection( true );
                globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
            }
            else
            {
                // An integer
                try
                {
                    Integer value = Integer.parseInt( globalLimitStr );
                    
                    if ( value < TimeLimitWrapper.UNLIMITED )
                    {
                        // The value must be either -1 (unlimited) or a positive number
                        globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    }
                    else if ( value == TimeLimitWrapper.UNLIMITED )
                    {
                        timeLimitWrapper.setGlobalLimit( TimeLimitWrapper.UNLIMITED );
                        globalUnlimitedCheckbox.setSelection( true );
                        globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                    else
                    {
                        timeLimitWrapper.setGlobalLimit( value );
                        globalUnlimitedCheckbox.setSelection( false );
                        globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    globalLimitText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                }
            }

            timeLimitText.setText( timeLimitWrapper.toString() );
            okButton.setEnabled( isValid() );
        }
    };
    
    
    /**
     * The listener in charge of exposing the changes when the read or write buttons are checked
     *
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Display display = tcpBufferText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( readCheckbox.getSelection() )
            {
                if ( writeCheckbox.getSelection())
                {
                    getNewElement().setTcpType( TcpType.BOTH );
                }
                else
                {
                    getNewElement().setTcpType( TcpType.READ );
                }
            }
            else if ( writeCheckbox.getSelection() )
            {
                if ( readCheckbox.getSelection() )
                {
                    getNewElement().setTcpType( TcpType.BOTH );
                }
                else
                {
                    getNewElement().setTcpType( TcpType.WRITE );
                }
            }
            else
            {
                getNewElement().setTcpType( TcpType.BOTH );
            }
            
            // Set the TcpBuffer into the text box
            tcpBufferText.setText( getNewElement().toString() );

            if ( TcpBufferWrapper.isValid( sizeText.getText(), listenerText.getText() ) )
            {
                tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                okButton.setEnabled( true );
            }
            else
            {
                tcpBufferText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                okButton.setEnabled( false );
            }
        }
    };

    
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "TimeLimit" );
    }


    /**
     * Construct the new TimeLimit from what we have in the dialog
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        // Creating the new TimeLimitWrapper
        String globalLimitStr = globalLimitText.getText();
        String hardLimitStr = hardLimitText.getText();
        String softLimitStr = softLimitText.getText();
        
        Integer globalLimit = null;
        Integer hardLimit = null;
        Integer softLimit = null;

        if ( !Strings.isEmpty( globalLimitStr ) )
        {
            try
            {
                globalLimit = Integer.valueOf( globalLimitStr );
            }
            catch ( NumberFormatException nfe )
            {
                // Nothing we can do...
            }
        }

        if ( !Strings.isEmpty( hardLimitStr ) )
        {
            try
            {
                hardLimit = Integer.valueOf( hardLimitStr );
            }
            catch ( NumberFormatException nfe )
            {
                // Nothing we can do...
            }
        }

        if ( !Strings.isEmpty( softLimitStr ) )
        {
            try
            {
                softLimit = Integer.valueOf( softLimitStr );
            }
            catch ( NumberFormatException nfe )
            {
                // Nothing we can do...
            }
        }
        
        setNewTimeLimit( new TimeLimitWrapper( globalLimit , hardLimit, softLimit ).toString() );
        super.okPressed();
    }


    /**
     * Create the Dialog for TimeLimit :
     * <pre>
     * +-------------------------------------------------------+
     * | TimeLimit                                             |
     * | .---------------------------------------------------. |
     * | | Soft Limit :  [----------]  [] Unlimited          | |
     * | |                                                   | |
     * | | Hard Limit :  [----------]  [] Unlimited  [] Soft | |
     * | |                                                   | |
     * | | Global :      [----------]  [] Unlimited          | |
     * | '---------------------------------------------------' |
     * | Resulting TimeLimit                                   |
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

        initFromTimeLimit();
        addListeners();
        
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

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( timeLimitGroup, "", 2 );

        // HardLimit Text
        BaseWidgetUtils.createLabel( timeLimitGroup, "Hard Limit :", 1 );
        hardLimitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        hardLimitText.addModifyListener( hardLimitTextListener );

        // Hard Limit unlimited checkbox Button
        hardUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Unlimited", 2 );

        // HardLimit soft checkbox Button
        hardSoftCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Soft", 2 );

        // GlobalLimit Text
        BaseWidgetUtils.createLabel( timeLimitGroup, "Global Limit :", 1 );
        globalLimitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        globalLimitText.addModifyListener( globalLimitTextListener );

        // GLobal Limit unlimited checkbox Button
        globalUnlimitedCheckbox = BaseWidgetUtils.createCheckbox( timeLimitGroup, "Unlimited", 2 );

        // 2 tabs to fill the line
        BaseWidgetUtils.createLabel( timeLimitGroup, "", 2 );
    }


    /**
     * Creates the TimeLimit show group. This is the part of the dialog
     * where the real TimeLimit is shown, or an error message if the TimeLimit
     * is invalid.
     * 
     * <pre>
     * Resulting TimeLimit
     * .------------------------------------.
     * | Time Limit : <///////////////////> |
     * '------------------------------------'
     * </pre>
     * @param parent the parent composite
     */
    private void createTimeLimitShowGroup( Composite parent )
    {
        // TimeLimit Group
        Group timeLimitGroup = BaseWidgetUtils.createGroup( parent, "Resulting TimeLimit", 1 );
        GridLayout timeLimitGroupGridLayout = new GridLayout( 2, false );
        timeLimitGroup.setLayout( timeLimitGroupGridLayout );
        timeLimitGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // TimeLimit Text
        timeLimitText = BaseWidgetUtils.createText( timeLimitGroup, "", 1 );
        timeLimitText.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );
        timeLimitText.setEditable( false );
    }


    /**
     * Initializes the UI from the TimeLimit
     */
    private void initFromTimeLimit()
    {
        if ( timeLimitWrapper != null )
        {
            // The SoftLimit
            Integer softLimit = timeLimitWrapper.getSoftLimit();
            
            if ( softLimit == null )
            {
                softLimitText.setText( "" );
                softUnlimitedCheckbox.setSelection( false );
            }
            else if ( softLimit.equals( TimeLimitWrapper.UNLIMITED ) )
            {
                softLimitText.setText( "" );
                softUnlimitedCheckbox.setSelection( true );
            }
            else
            {
                softLimitText.setText( softLimit.toString() );
                softUnlimitedCheckbox.setSelection( false );
            }
            
            // The HardLimit
            Integer hardLimit = timeLimitWrapper.getHardLimit();
            
            if ( hardLimit == null )
            {
                hardLimitText.setText( "" );
                hardUnlimitedCheckbox.setSelection( false );
                hardSoftCheckbox.setSelection( false );
            }
            else if ( hardLimit.equals( TimeLimitWrapper.UNLIMITED ) )
            {
                hardLimitText.setText( "" );
                hardUnlimitedCheckbox.setSelection( true );
                hardSoftCheckbox.setSelection( false );
            }
            else if ( hardLimit.equals( TimeLimitWrapper.HARD_SOFT ) )
            {
                hardLimitText.setText( "" );
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
            Integer globalLimit = timeLimitWrapper.getGlobalLimit();
            
            if ( globalLimit == null )
            {
                globalLimitText.setText( "" );
                globalUnlimitedCheckbox.setSelection( false );
            }
            else if ( globalLimit.equals( TimeLimitWrapper.UNLIMITED ) )
            {
                globalLimitText.setText( "" );
                globalUnlimitedCheckbox.setSelection( true );
            }
            else
            {
                globalLimitText.setText( globalLimit.toString() );
                globalUnlimitedCheckbox.setSelection( false );
            }
            
            timeLimitText.setText( editedTimeLimitStr );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getNewTimeLimit()
    {
        return newTimeLimitStr;
    }


    /**
     * {@inheritDoc}
     */
    public void setEditedTimeLimit( String editedTimeLimitStr )
    {
        timeLimitWrapper = new TimeLimitWrapper( editedTimeLimitStr );
        this.editedTimeLimitStr = editedTimeLimitStr;
    }


    /**
     * {@inheritDoc}
     */
    public void setNewTimeLimit( String newTimeLimitStr )
    {
        this.newTimeLimitStr = newTimeLimitStr;
    }

    
    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        /*
        globalUnlimitedCheckbox.addSelectionListener( checkboxSelectionListener );
        hardUnlimitedCheckbox.addSelectionListener( checkboxSelectionListener );
        hardSoftCheckbox.addSelectionListener( checkboxSelectionListener );
        softUnlimitedCheckbox.addSelectionListener( checkboxSelectionListener );
        */
    }
}
