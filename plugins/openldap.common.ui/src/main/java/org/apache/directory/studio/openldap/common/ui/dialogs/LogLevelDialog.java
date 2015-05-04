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
package org.apache.directory.studio.openldap.common.ui.dialogs;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.openldap.common.ui.LogLevel;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.widgets.Text;


/**
 * The LogLevelDialog is used to edit the LogLevel. Here are the possible values :
 * 
 * <ul>
 * <li>none        0</li>
 * <li>trace       1</li>
 * <li>packets     2</li>
 * <li>args        4</li>
 * <li>conns       8</li>
 * <li>BER        16</li>
 * <li>filter     32</li>
 * <li>config     64</li>
 * <li>ACL       128</li>
 * <li>stats     256</li>
 * <li>stats2    512</li>
 * <li>shell    1024</li>
 * <li>parse    2048</li>
 * <li>sync    16384</li>
 * <li>any       -1</li>
 * </ul>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LogLevelDialog extends Dialog
{
    /** The logLevel value */
    private int logLevelValue;

    // UI widgets
    private Button noneCheckbox;
    private Button traceCheckbox;
    private Button packetsCheckbox;
    private Button argsCheckbox;
    private Button connsCheckbox;
    private Button berCheckbox;
    private Button filterCheckbox;
    private Button configCheckbox;
    private Button aclCheckbox;
    private Button statsCheckbox;
    private Button stats2Checkbox;
    private Button shellCheckbox;
    private Button parseCheckbox;
    private Button syncCheckbox;
    private Button anyCheckbox;
    
    /** An array of all the checkboxes */
    private Button[] buttons = new Button[13];
    
    // The resulting integer
    private Text logLevelText;
    
    // An empty space
    protected static final String TABULATION = " ";

    /**
     * The listener in charge of exposing the changes when some buttons are checked
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            
            if ( object instanceof Button )
            {
                Button selectedButton = (Button)object;
                
                if ( selectedButton.equals( noneCheckbox ) )
                {
                    // None, we have to uncheck all the other checkbox
                    for ( Button button : buttons )
                    {
                        button.setSelection( false );
                    }
                    
                    // reset the Any button
                    anyCheckbox.setSelection( false );
                    
                    // set the None button
                    noneCheckbox.setSelection( true );
                }
                else if ( selectedButton.equals( anyCheckbox ) )
                {
                    // Any, we have to check all the buttons 
                    for ( Button button : buttons )
                    {
                        button.setSelection( true );
                    }
                    
                    // reset the None button
                    noneCheckbox.setSelection( false );
                    
                    // set the Any button
                    anyCheckbox.setSelection( true );
                }
                else
                {
                    // deselect the any and none button, unless we don't have any more
                    // selected button or all the button selected
                    int count = 0;
                    for ( Button button : buttons )
                    {
                        if ( button.getSelection() )
                        {
                            count++;
                        }
                    }
                    
                    if ( count == 0 )
                    {
                        anyCheckbox.setSelection( false );
                        noneCheckbox.setSelection( true );
                    }
                    else if ( count == buttons.length )
                    {
                        anyCheckbox.setSelection( true );
                        noneCheckbox.setSelection( false );
                    }
                    else
                    {
                        anyCheckbox.setSelection( false );
                        noneCheckbox.setSelection( false );
                    }
                }
            }
            
            computeLogValue();
            setLogLeveltext();
        }
    };


    /**
     * Creates a new instance of LogLevelDialog.
     * 
     * @param parentShell the parent shell
     */
    public LogLevelDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Creates a new instance of LogLevelDialog.
     * 
     * @param parentShell the parent shell
     * @param value the initial value
     */
    public LogLevelDialog( Shell parentShell, int value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.logLevelValue = value;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "OpenLDAP LogLevel" );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        computeLogValue();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createLogLevelArea( composite );
        createLogLevelValueArea( composite );
        setCheckboxesValue();
        addListeners();
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Sets the checkboxes value.
     *
     * @param perm the Unix permissions
     */
    private void setCheckboxesValue()
    {
        noneCheckbox.setSelection( logLevelValue == LogLevel.NONE.getValue() );
        traceCheckbox.setSelection( ( logLevelValue & LogLevel.TRACE.getValue() ) != 0 );
        packetsCheckbox.setSelection( ( logLevelValue & LogLevel.PACKETS.getValue() ) != 0 );
        argsCheckbox.setSelection( ( logLevelValue & LogLevel.ARGS.getValue() ) != 0 );
        connsCheckbox.setSelection( ( logLevelValue & LogLevel.CONNS.getValue() ) != 0 );
        berCheckbox.setSelection( ( logLevelValue & LogLevel.BER.getValue() ) != 0 );
        filterCheckbox.setSelection( ( logLevelValue & LogLevel.FILTER.getValue() ) != 0 );
        configCheckbox.setSelection( ( logLevelValue & LogLevel.CONFIG.getValue() ) != 0 );
        aclCheckbox.setSelection( ( logLevelValue & LogLevel.ACL.getValue() ) != 0 );
        statsCheckbox.setSelection( ( logLevelValue & LogLevel.STATS.getValue() ) != 0 );
        stats2Checkbox.setSelection( ( logLevelValue & LogLevel.STATS2.getValue() ) != 0 );
        shellCheckbox.setSelection( ( logLevelValue & LogLevel.SHELL.getValue() ) != 0 );
        parseCheckbox.setSelection( ( logLevelValue & LogLevel.PARSE.getValue() ) != 0 );
        syncCheckbox.setSelection( ( logLevelValue & LogLevel.SYNC.getValue() ) != 0 );
        anyCheckbox.setSelection( logLevelValue == LogLevel.ANY.getValue() );
    }


    /**
     * Sets the LogLevel value.
     */
    private void setLogLeveltext()
    {
        logLevelText.setText( Integer.toString( logLevelValue ) );
    }


    /**
     * Creates the LogLevel area.
     *
     * @param parent the parent composite
     */
    private void createLogLevelArea( Composite parent )
    {
        Group logLevelGroup = BaseWidgetUtils.createGroup( parent, "Log Levels", 1 );
        logLevelGroup.setLayout( new GridLayout( 5, false ) );
        int pos = 0;

        // None and any, centered
        BaseWidgetUtils.createLabel( logLevelGroup, TABULATION, 1 );
        noneCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "None", 1 );
        BaseWidgetUtils.createLabel( logLevelGroup, TABULATION, 1 );
        anyCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Any", 1 );
        BaseWidgetUtils.createLabel( logLevelGroup, TABULATION, 1 );

        // The first 5 options
        aclCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "ACL", 1 );
        buttons[pos++] = aclCheckbox;
        argsCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Args", 1 );
        buttons[pos++] = argsCheckbox;
        berCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "BER", 1 );
        buttons[pos++] = berCheckbox;
        configCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Config", 1 );
        buttons[pos++] = configCheckbox;
        connsCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Conns", 1 );
        buttons[pos++] = connsCheckbox;

        // The next 5 options
        filterCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Filter", 1 );
        buttons[pos++] = filterCheckbox;
        packetsCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Packets", 1 );
        buttons[pos++] = packetsCheckbox;
        parseCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Parses", 1 );
        buttons[pos++] = parseCheckbox;
        shellCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Shell", 1 );
        buttons[pos++] = shellCheckbox;
        statsCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Stats", 1 );
        buttons[pos++] = statsCheckbox;

        // The last 3 options, centered
        BaseWidgetUtils.createLabel( logLevelGroup, TABULATION, 1 );
        stats2Checkbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Stats2", 1 );
        buttons[pos++] = stats2Checkbox;
        traceCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Trace", 1 );
        buttons[pos++] = traceCheckbox;
        syncCheckbox = BaseWidgetUtils.createCheckbox( logLevelGroup, "Sync", 1 );
        buttons[pos++] = syncCheckbox;
        BaseWidgetUtils.createLabel( logLevelGroup, TABULATION, 1 );
    }


    /**
     * Creates the LogLevel value area. It's not editable
     *
     * @param parent the parent composite
     */
    private void createLogLevelValueArea( Composite parent )
    {
        Group logLevelValueGroup = BaseWidgetUtils.createGroup( parent, "LogLevel Value", 1 );
        logLevelText = BaseWidgetUtils.createText( logLevelValueGroup, Integer.toString( logLevelValue ), 1 );
        logLevelText.setTextLimit( 5 );
        logLevelText.setEditable( false );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        noneCheckbox.addSelectionListener( checkboxSelectionListener );
        
        for ( Button button : buttons )
        {
            button.addSelectionListener( checkboxSelectionListener );
        }

        anyCheckbox.addSelectionListener( checkboxSelectionListener );
    }


    private void computeLogValue()
    {
        if ( noneCheckbox.getSelection() )
        {
            logLevelValue = 0;
        }
        else if ( anyCheckbox.getSelection() )
        {
            logLevelValue = -1;
        }
        else
        {
            if ( logLevelValue == LogLevel.ANY.getValue() )
            {
                // We cancel the ANY selection, so we have to set the LogLevelValue
                // to 0, as it's currently -1
                logLevelValue = 0;
            }
            
            // Now, check all the checkBox selections
            if ( aclCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.ACL.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.ACL.getValue();
            }

            if ( argsCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.ARGS.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.ARGS.getValue();
            }

            if ( berCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.BER.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.BER.getValue();
            }

            if ( configCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.CONFIG.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.CONFIG.getValue();
            }

            if ( connsCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.CONNS.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.CONNS.getValue();
            }

            if ( filterCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.FILTER.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.FILTER.getValue();
            }

            if ( packetsCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.PACKETS.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.PACKETS.getValue();
            }

            if ( parseCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.PARSE.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.PARSE.getValue();
            }

            if ( shellCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.SHELL.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.SHELL.getValue();
            }

            if ( statsCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.STATS.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.STATS.getValue();
            }

            if ( stats2Checkbox.getSelection() )
            {
                logLevelValue |= LogLevel.STATS2.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.STATS2.getValue();
            }

            if ( syncCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.SYNC.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.SYNC.getValue();
            }
            
            if ( traceCheckbox.getSelection() )
            {
                logLevelValue |= LogLevel.TRACE.getValue();
            }
            else
            {
                logLevelValue &= ~LogLevel.TRACE.getValue();
            }
        }
    }
    
    
    public int getLogLevelValue()
    {
        return logLevelValue;
    }
}
