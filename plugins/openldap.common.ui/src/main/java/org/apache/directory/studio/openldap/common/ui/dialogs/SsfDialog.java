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


import java.awt.datatransfer.StringSelection;

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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * A Dialog used to configure the OpenLDAP SSF. SSF (Security Strength Factors) associates
 * a strength to a feature. Here is the list of feature that support a SSF :
 * <ul>
 * <li>ssf : global</li>
 * <li>transport</li>
 * <li>tls</li>
 * <li>sasl</li>
 * <li>simple_bind</li>
 * <li>update_ssf</li>
 * <li>update_transport</li>
 * <li>update_tls</li>
 * <li>update_sasl</li>
 * </ul>
 * The ssf valus will generally depend on the number of bits used by the cipher to use,
 * with two special values : 0 and 1. Here is a set of possible values :
 * <ul>
 * <li>0 : no protection</li>
 * <li>1 : integrity check only</li>
 * <li>56 : DES (key length is 56 bits)</li>
 * <li>112 : 3DES (key length is 112 bits)</li>
 * <li>128 : RC4, Blowish, AES-128</li>
 * <li>256 : AES-256</li>
 * </ul>
 * 
 * We will allow the user to select the feature to configure, and the value to set. Here is 
 * the layout :
 * <pre>
 * +-----------------------------------------------+
 * | Security                                      |
 * | .-------------------------------------------. |
 * | | Feature :  [----------------------------] | |
 * | |   [ ] No protection                       | |
 * | |   [ ] Integrity                           | |
 * | |   [ ] 56 bits (DES)                       | |
 * | |   [ ] 112 bits (3DES)                     | |
 * | |   [ ] 128 bits (RC4, Blowfish...)         | |
 * | |   [ ] 256 bits (AES-256, ...)             | |
 * | |   [     ] Other value                     | |
 * | '-------------------------------------------' |
 * | Resulting Security                            |
 * | .-------------------------------------------. |
 * | | Security  : <///////////////////////////> | |
 * | '-------------------------------------------' |
 * |                                               |
 * |  (Cancel)                              (OK)   |
 *-------------------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SsfDialog extends Dialog
{
    /** The SSFl values */
    private String ssfValue;

    // UI widgets
    /** The feature combo */
    private Combo featureCombo;
    
    /** The strength list */
    private Button[] strengthCheckbox = new Button[6];
    
    /** The other strength list */
    private Text[] otherText;
    
    // The resulting SSF
    private Text ssfText;
    
    /**
     * The listener in charge of exposing the changes when some buttons are checked
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            /*
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
            */
        }
    };


    /**
     * Creates a new instance of SsfDialog.
     * 
     * @param parentShell the parent shell
     */
    public SsfDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Creates a new instance of SsfDialog.
     * 
     * @param parentShell the parent shell
     * @param value the initial value
     */
    public SsfDialog( Shell parentShell, String value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        ssfValue = value;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "OpenLDAP SSF" );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        computeSsfValue();
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
        /*
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
        */
    }


    /**
     * Sets the LogLevel value.
     */
    private void setSsftext()
    {
        //logLevelText.setText( Integer.toString( logLevelValue ) );
    }


    /**
     * Creates the LogLevel area.
     *
     * @param parent the parent composite
     */
    private void createLogLevelArea( Composite parent )
    {
        /*
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
        */
    }


    /**
     * Creates the LogLevel value area. It's not editable
     *
     * @param parent the parent composite
     */
    private void createLogLevelValueArea( Composite parent )
    {
        Group ssfValueGroup = BaseWidgetUtils.createGroup( parent, "SSF Value", 1 );
        ssfText = BaseWidgetUtils.createText( ssfValueGroup, "", 1 );
        ssfText.setEditable( false );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        /*
        noneCheckbox.addSelectionListener( checkboxSelectionListener );
        
        for ( Button button : buttons )
        {
            button.addSelectionListener( checkboxSelectionListener );
        }

        anyCheckbox.addSelectionListener( checkboxSelectionListener );
        */
    }


    private void computeSsfValue()
    {
        //TODO
    }
}
