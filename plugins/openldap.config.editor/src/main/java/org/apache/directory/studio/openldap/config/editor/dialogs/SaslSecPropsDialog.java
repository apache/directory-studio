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
import org.apache.directory.studio.openldap.common.ui.model.SaslSecPropEnum;
import org.apache.directory.studio.openldap.config.editor.wrappers.SaslSecPropsWrapper;
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


/**
 * The SaslSecPropsDialog is used to edit the Sasl Security Properties. We manage
 * two kind of properties : with or without parameters. Here is the list of those properties
 * 
 * <ul>
 * <li>none : the  flag properties default, "noanonymous,noplain", will be cleared</li>
 * <li>noplain : disables mechanisms susceptible to simple passive attacks</li>
 * <li>noactive : disables mechanisms susceptible to active attacks</li>
 * <li>nodict : disables mechanisms  susceptible to passive dictionary attacks.</li>
 * <li>noanonymous : disables mechanisms which support anonymous login</li>
 * <li>forwardsec : requires forward secrecy between sessions.</li>
 * <li>passcred : requirse mechanisms which pass client credentials</li>
 * <li>minssf=&lt;factor&gt; : specifies the minimum acceptable security strength factor</li>
 * <li>maxssf=&lt;factor&gt; : specifies the maximum acceptable security strength factor</li>
 * <li>maxbufsize=&lt;size&gt;: specifies the maximum security layer receive buffer size</li>
 * </ul>
 * 
 * Here is what it looks like :
 * <pre>
 * +--------------------------------------+
 * | SASL Security Properties             |
 * | .----------------------------------. |
 * | | none        [ ]   noplain    [ ] | |
 * | | noactive    [ ]   nodict     [ ] | |
 * | | noanonymous [ ]   forwardsec [ ] | |
 * | | passcred    [ ]                  | |
 * | |                                  | |
 * | | minSSF        [      ]           | |
 * | | maxSSF        [      ]           | |
 * | | maxBufSizeSSF [      ]           | |
 * | '----------------------------------' |
 * | SASL security Properties Value       |
 * | .----------------------------------. |
 * | | [//////////////////////////////] | |
 * | '----------------------------------' |
 * |                                      |
 * |  (Cancel)                      (OK)  |
 * +--------------------------------------+
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SaslSecPropsDialog extends Dialog
{
    /** The SaslSecProps string value */
    private SaslSecPropsWrapper saslSecPropsWrapper;

    // UI widgets
    private Button noneCheckbox;
    private Button noPlainCheckbox;
    private Button noActiveCheckbox;
    private Button noDictCheckbox;
    private Button noAnonymousCheckbox;
    private Button forwardSecCheckbox;
    private Button passCredCheckbox;
    private Text minSsfText;
    private Text maxSsfText;
    private Text maxBufSizeText;
    
    /** An array of all the checkboxes */
    private Button[] buttons = new Button[7];
    
    // The resulting String
    private Text saslSecPropsText;
    
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
                
                // none
                if ( selectedButton.equals( noneCheckbox ) )
                {
                    if ( noneCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.NONE );
                        
                        // Uncheck the noplain and noanonymous checkboxes
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NO_PLAIN );
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NO_ANONYMOUS );
                        noPlainCheckbox.setSelection( false );
                        noAnonymousCheckbox.setSelection( false );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NONE );
                    }
                }
                // noplain
                else if ( selectedButton.equals( noPlainCheckbox ) )
                {
                    if ( noPlainCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.NO_PLAIN );
                        
                        // Uncheck the none checkbox
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NONE );
                        noneCheckbox.setSelection( false );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NO_PLAIN );
                    }
                }
                // noactive
                else if ( selectedButton.equals( noActiveCheckbox ) )
                {
                    if ( noActiveCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.NO_ACTIVE );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NO_ACTIVE );
                    }
                }
                // nodict
                else if ( selectedButton.equals( noDictCheckbox ) )
                {
                    if ( noDictCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.NO_DICT );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NO_DICT );
                    }
                }
                // noanonymous
                else if ( selectedButton.equals( noAnonymousCheckbox ) )
                {
                    if ( noAnonymousCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.NO_ANONYMOUS );
                        
                        // Uncheck the none checkbox
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NONE );
                        noneCheckbox.setSelection( false );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.NO_ANONYMOUS );
                    }
                }
                // forwardsec
                else if ( selectedButton.equals( forwardSecCheckbox ) )
                {
                    if ( forwardSecCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.FORWARD_SEC );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.FORWARD_SEC );
                    }
                }
                // passcred
                else if ( selectedButton.equals( passCredCheckbox ) )
                {
                    if ( passCredCheckbox.getSelection() )
                    {
                        saslSecPropsWrapper.addFlag( SaslSecPropEnum.PASS_CRED );
                    }
                    else
                    {
                        saslSecPropsWrapper.removeFlag( SaslSecPropEnum.PASS_CRED );
                    }
                }
            }
            
            setSaslSecPropsText();
        }
    };

    
    /**
     * The listener for the minSsf Limit Text
     */
    private ModifyListener minSsfListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = minSsfText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean valid = true;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The value must be an integer >= 0 
            String minSsfStr = minSsfText.getText();
            
            if ( Strings.isEmpty( minSsfStr ) )
            {
                saslSecPropsWrapper.setMinSsf( null );
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( minSsfStr );
                    
                    if ( value < 0 )
                    {
                        // The value must be >= 0
                        color = SWT.COLOR_RED;
                        valid = false;
                    }
                    else
                    {
                        saslSecPropsWrapper.setMinSsf( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                    valid = false;
                }
            }

            minSsfText.setForeground( display.getSystemColor( color ) );
            saslSecPropsText.setText( saslSecPropsWrapper.toString() );
            okButton.setEnabled( valid );
        }
    };

    
    /**
     * The listener for the maxSsf Limit Text
     */
    private ModifyListener maxSsfListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = maxSsfText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean valid = true;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The value must be an integer >= 0 
            String maxSsfStr = maxSsfText.getText();
            
            if ( Strings.isEmpty( maxSsfStr ) )
            {
                saslSecPropsWrapper.setMaxSsf( null );
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( maxSsfStr );
                    
                    if ( value < 0 )
                    {
                        // The value must be >= 0
                        color = SWT.COLOR_RED;
                        valid = false;
                    }
                    else
                    {
                        saslSecPropsWrapper.setMaxSsf( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                    valid = false;
                }
            }

            maxSsfText.setForeground( display.getSystemColor( color ) );
            saslSecPropsText.setText( saslSecPropsWrapper.toString() );
            okButton.setEnabled( valid );
        }
    };

    
    /**
     * The listener for the maxBufSize Limit Text
     */
    private ModifyListener maxBufSizeListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = maxBufSizeText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            boolean valid = true;
            int color = SWT.COLOR_BLACK;

            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            // The value must be an integer >= 0 
            String maxBufSizeStr = maxBufSizeText.getText();
            
            if ( Strings.isEmpty( maxBufSizeStr ) )
            {
                saslSecPropsWrapper.setMaxBufSize( null );
            }
            else
            {
                // An integer
                try
                {
                    int value = Integer.parseInt( maxBufSizeStr );
                    
                    if ( value < 0 )
                    {
                        // The value must be >= 0
                        color = SWT.COLOR_RED;
                        valid = false;
                    }
                    else
                    {
                        saslSecPropsWrapper.setMaxBufSize( value );
                    }
                }
                catch ( NumberFormatException nfe )
                {
                    // The value must be either -1 (unlimited) or a positive number
                    color = SWT.COLOR_RED;
                    valid = false;
                }
            }

            maxBufSizeText.setForeground( display.getSystemColor( color ) );
            saslSecPropsText.setText( saslSecPropsWrapper.toString() );
            okButton.setEnabled( valid );
        }
    };


    /**
     * Creates a new instance of SaslSecPropsDialog.
     * 
     * @param parentShell the parent shell
     */
    public SaslSecPropsDialog( Shell parentShell )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
    }


    /**
     * Creates a new instance of SaslSecPropsDialog.
     * 
     * @param parentShell the parent shell
     * @param value the initial value
     */
    public SaslSecPropsDialog( Shell parentShell, String value )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        saslSecPropsWrapper = new SaslSecPropsWrapper( value );
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( "OpenLDAP SASL Security Properties" );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
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

        createSaslSecPropsArea( composite );
        createSaslSecPropsValueArea( composite );
        initDialog();
        setSaslSecPropsText();
        addListeners();
        applyDialogFont( composite );
        
        return composite;
    }


    /**
     * Initializes the Dialog with the values 
     */
    private void initDialog()
    {
        // The checkboxes
        noneCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.NONE ) );
        noPlainCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.NO_PLAIN ) );
        noActiveCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.NO_ACTIVE ) );
        noDictCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.NO_DICT ) );
        noAnonymousCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.NO_ANONYMOUS ) );
        forwardSecCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.FORWARD_SEC ) );
        passCredCheckbox.setSelection( saslSecPropsWrapper.getFlags().contains( SaslSecPropEnum.PASS_CRED ) );
        
        // The properties with values
        if ( saslSecPropsWrapper.getMinSsf() != null )
        {
            minSsfText.setText( Integer.toString( saslSecPropsWrapper.getMinSsf() ) );
        }

        if ( saslSecPropsWrapper.getMaxSsf() != null )
        {
            maxSsfText.setText( Integer.toString( saslSecPropsWrapper.getMaxSsf() ) );
        }

        if ( saslSecPropsWrapper.getMaxBufSize() != null )
        {
            maxBufSizeText.setText( Integer.toString( saslSecPropsWrapper.getMaxBufSize() ) );
        }
    }


    /**
     * Sets the SaslSecProps value.
     */
    private void setSaslSecPropsText()
    {
        saslSecPropsText.setText( saslSecPropsWrapper.toString() );
    }


    /**
     * Creates the SaslSecProps area.
     * 
     * | SASL Security Properties             |
     * | .----------------------------------. |
     * | | none        [ ]   noplain    [ ] | |
     * | | noactive    [ ]   nodict     [ ] | |
     * | | noanonymous [ ]   forwardsec [ ] | |
     * | | passcred    [ ]                  | |
     * | |                                  | |
     * | | minSSF        [      ]           | |
     * | | maxSSF        [      ]           | |
     * | | maxBufSizeSSF [      ]           | |
     * | '----------------------------------' |
     *
     * @param parent the parent composite
     */
    private void createSaslSecPropsArea( Composite parent )
    {
        Group saslSecPropsGroup = BaseWidgetUtils.createGroup( parent, "SASL Security Properties", 1 );
        saslSecPropsGroup.setLayout( new GridLayout( 2, false ) );
        int pos = 0;

        // Line 1 : none and noplain
        noneCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "none", 1 );
        noPlainCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "noplain", 1 );
        buttons[pos++] = noneCheckbox;
        buttons[pos++] = noPlainCheckbox;

        // Line 2 : noactive and nodict
        noActiveCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "noactive", 1 );
        noDictCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "nodict", 1 );
        buttons[pos++] = noActiveCheckbox;
        buttons[pos++] = noDictCheckbox;

        // Line 2 : noanonymous and nforwardsec
        noAnonymousCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "noanonymous", 1 );
        forwardSecCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "forwardsec", 1 );
        buttons[pos++] = noAnonymousCheckbox;
        buttons[pos++] = forwardSecCheckbox;

        // Line 4 : passcred
        passCredCheckbox = BaseWidgetUtils.createCheckbox( saslSecPropsGroup, "passcred", 1 );
        BaseWidgetUtils.createLabel( saslSecPropsGroup, TABULATION, 1 );
        buttons[pos++] = passCredCheckbox;

        // A blank line
        BaseWidgetUtils.createLabel( saslSecPropsGroup, TABULATION, 1 );
        BaseWidgetUtils.createLabel( saslSecPropsGroup, TABULATION, 1 );

        // Min SSF
        BaseWidgetUtils.createLabel( saslSecPropsGroup, "minssf", 1 );
        minSsfText = BaseWidgetUtils.createText( saslSecPropsGroup, "", 1 );
        minSsfText.addModifyListener( minSsfListener );

        // Max SSF
        BaseWidgetUtils.createLabel( saslSecPropsGroup, "maxssf", 1 );
        maxSsfText = BaseWidgetUtils.createText( saslSecPropsGroup, "", 1 );
        maxSsfText.addModifyListener( maxSsfListener );
        
        // Max Buf Size
        BaseWidgetUtils.createLabel( saslSecPropsGroup, "maxbufsize", 1 );
        maxBufSizeText = BaseWidgetUtils.createText( saslSecPropsGroup, "", 1 );
        maxBufSizeText.addModifyListener( maxBufSizeListener );
    }


    /**
     * Creates the SASL Security Properties value area. It's not editable
     * 
     * <pre>
     * | SASL security Properties Value       |
     * | .----------------------------------. |
     * | | [//////////////////////////////] | |
     * | '----------------------------------' |
     * </pre>
     *
     * @param parent the parent composite
     */
    private void createSaslSecPropsValueArea( Composite parent )
    {
        Group saslSecPropsValueGroup = BaseWidgetUtils.createGroup( parent, "SASL Security Properties Value", 1 );
        saslSecPropsText = BaseWidgetUtils.createText( saslSecPropsValueGroup, saslSecPropsWrapper.toString(), 1 );
        saslSecPropsText.setEditable( false );
    }


    /**
     * Adds listeners.
     */
    private void addListeners()
    {
        for ( Button button : buttons )
        {
            button.addSelectionListener( checkboxSelectionListener );
        }
    }


    /**
     * @return The SASL Security Properties parameter value
     */
    public String getSaslSecPropsValue()
    {
        return saslSecPropsWrapper.toString();
    }
}
