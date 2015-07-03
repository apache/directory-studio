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
import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.openldap.common.ui.model.SsfFeatureEnum;
import org.apache.directory.studio.openldap.common.ui.model.SsfStrengthEnum;
import org.apache.directory.studio.openldap.config.editor.wrappers.SsfWrapper;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
 * The ssf value will generally depend on the number of bits used by the cipher to use,
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
public class SsfDialog extends AddEditDialog<SsfWrapper>
{
    // UI widgets
    private Combo featureCombo;
    
    /** The strength list */
    private Button[] strengthCheckbox = new Button[6];
    
    /** The other strength list */
    private Text otherText;
    
    // The resulting SSF
    private Text ssfText;
    
    // The list of options in the combo
    private String[] features = new String[]
        {
            SsfFeatureEnum.NONE.getName(),
            SsfFeatureEnum.SASL.getName(),
            SsfFeatureEnum.SIMPLE_BIND.getName(),
            SsfFeatureEnum.SSF.getName(),
            SsfFeatureEnum.TLS.getName(),
            SsfFeatureEnum.TRANSPORT.getName(),
            SsfFeatureEnum.UPDATE_SASL.getName(),
            SsfFeatureEnum.UPDATE_SSF.getName(),
            SsfFeatureEnum.UPDATE_TLS.getName(),
            SsfFeatureEnum.UPDATE_TRANSPORT.getName(),
            
        };
    
    // An empty space
    protected static final String TABULATION = " ";

    /**
     * The listener in charge of exposing the changes when some buttons are checked
     */
    private SelectionListener featureSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            
            if ( object instanceof Combo )
            {
                Combo featureCombo = (Combo)object;
                Display display = ssfText.getDisplay();
                Button okButton = getButton( IDialogConstants.OK_ID );
                
                String feature = featureCombo.getText();
                SsfWrapper ssfWrapper = getEditedElement();
                
                SsfFeatureEnum ssfFeature = SsfFeatureEnum.getSsfFeature( feature );
                
                // Check if it's not already part of the SSF
                boolean present = false;
                
                for ( SsfWrapper ssf : getElements() )
                {
                    if ( ssfFeature == ssf.getFeature() )
                    {
                        present = true;
                        break;
                    }
                }
                
                if ( !present )
                {
                    ssfWrapper.setFeature( SsfFeatureEnum.getSsfFeature( feature ) );
                    
                    ssfText.setText( ssfWrapper.toString() );
                    
                    if ( ssfWrapper.isValid() )
                    {
                        okButton.setEnabled( true );
                        ssfText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                    }
                    else
                    {
                        okButton.setEnabled( false );
                        ssfText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    }
                }
                else
                {
                    // Come back to NONE
                    featureCombo.setText( SsfFeatureEnum.NONE.getName() );
                }
            }
        }
    };
    

    /**
     * The listener in charge of exposing the changes when some buttons are checked
     */
    private SelectionListener checkboxSelectionListener = new SelectionAdapter()
    {
        public void widgetSelected( SelectionEvent e )
        {
            Object object = e.getSource();
            Display display = ssfText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );

            if ( object instanceof Button )
            {
                Button selectedButton = (Button)object;
                
                SsfWrapper ssfWrapper = getEditedElement();

                if ( selectedButton.getSelection() == true )
                {
                    for ( int i = 0; i < strengthCheckbox.length; i++ )
                    {
                        if ( selectedButton.equals( strengthCheckbox[i] ) )
                        {
                            
                            switch ( i )
                            {
                                case 0 : 
                                    ssfWrapper.setNbBits( SsfStrengthEnum.NO_PROTECTION.getNbBits() );
                                    break;
                                    
                                case 1 :
                                    ssfWrapper.setNbBits( SsfStrengthEnum.INTEGRITY_CHECK.getNbBits() );
                                    break;
                                    
                                case 2 :
                                    ssfWrapper.setNbBits( SsfStrengthEnum.DES.getNbBits() );
                                    break;
                                    
                                case 3 :
                                    ssfWrapper.setNbBits( SsfStrengthEnum.THREE_DES.getNbBits() );
                                    break;
                                    
                                case 4 :
                                    ssfWrapper.setNbBits( SsfStrengthEnum.AES_128.getNbBits() );
                                    break;
                                    
                                case 5 :
                                    ssfWrapper.setNbBits( SsfStrengthEnum.AES_256.getNbBits() );
                                    break;
                            }
                        }
                        else
                        {
                            // Not selected, uncheck it.
                            strengthCheckbox[i].setSelection( false );
                        }
                    }
                    
                    // Erase the content of the Other text
                    otherText.setText( "" );
                }

                ssfText.setText( ssfWrapper.toString() );
                
                if ( ssfWrapper.isValid() )
                {
                    okButton.setEnabled( true );
                    ssfText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                }
                else
                {
                    okButton.setEnabled( false );
                    ssfText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                }
            }
        }
    };


    /**
     * The listener for the other Text
     */
    private ModifyListener otherTextListener = new ModifyListener()
    {
        public void modifyText( ModifyEvent e )
        {
            Display display = otherText.getDisplay();
            Button okButton = getButton( IDialogConstants.OK_ID );
            
            // This button might be null when the dialog is called.
            if ( okButton == null )
            {
                return;
            }

            try
            {
                int nbBits = Integer.parseInt( otherText.getText() );

                // The nbBits must be between >=0
                if ( nbBits < 0L )
                {
                    otherText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                    okButton.setEnabled( false );
                    return;
                }
                
                otherText.setForeground( display.getSystemColor( SWT.COLOR_BLACK ) );
                getEditedElement().setNbBits( nbBits );
                ssfText.setText( getEditedElement().toString() );
                okButton.setEnabled( true );
            }
            catch ( NumberFormatException nfe )
            {
                // Not even a number
                otherText.setForeground( display.getSystemColor( SWT.COLOR_RED ) );
                ssfText.setText( getEditedElement().toString() );
                okButton.setEnabled( false );
            }
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
        // Do nothing if the selected feature is NONE
        if ( getEditedElement().getFeature() != SsfFeatureEnum.NONE )
        {
            super.okPressed();
        }
    }


    /**
     * Create the Dialog for the SSF :
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
     */
    protected Control createDialogArea( Composite parent )
    {
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        composite.setLayoutData( gd );

        createSsfEditArea( composite );
        createSsfShowArea( composite );
        
        initDialog();
        addListeners();
        
        applyDialogFont( composite );
        
        return composite;
    }

    
    /**
     * Overriding the createButton method, so that we can disable the OK button if no feature is selected.
     * 
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) 
    {
        Button button = super.createButton( parent, id, label, defaultButton );

        if ( id == IDialogConstants.OK_ID ) 
        {
            SsfWrapper ssfWrapper = (SsfWrapper)getEditedElement();

            if ( ssfWrapper != null )
            {
                SsfFeatureEnum feature = ssfWrapper.getFeature();
                
                if ( feature == SsfFeatureEnum.NONE )
                {
                    button.setEnabled( false );
                }
            }
        }
        
        return button;
    }
    
    
    /**
     * Initializes the Dialog with the values
     */
    protected void initDialog()
    {
        SsfWrapper ssfWrapper = (SsfWrapper)getEditedElement();
        
        if ( ssfWrapper != null )
        {
            SsfFeatureEnum feature = ssfWrapper.getFeature();
            
            if ( feature == SsfFeatureEnum.NONE )
            {
                featureCombo.setText( SsfFeatureEnum.NONE.getName() );

                // Remove the feature that are already part of the list
                for ( SsfWrapper element : getElements() )
                {
                    featureCombo.remove( element.getFeature().getName() );
                }
            }
            else
            {
                // Remove all the other features, and inject the one being edited
                featureCombo.removeAll();
                featureCombo.add( feature.getName() );
                featureCombo.setText( feature.getName() );
                
                // Disable the combo
                featureCombo.setEnabled( false );
            }
            
            SsfStrengthEnum ssfStrength = SsfStrengthEnum.getSsfStrength( ssfWrapper.getNbBits() );
            
            switch ( ssfStrength )
            {
                case NO_PROTECTION :
                    strengthCheckbox[0].setSelection( true );
                    break;

                case INTEGRITY_CHECK :
                    strengthCheckbox[1].setSelection( true );
                    break;
                    
                case DES :
                    strengthCheckbox[2].setSelection( true );
                    break;
                    
                case THREE_DES :
                    strengthCheckbox[3].setSelection( true );
                    break;
                    
                case AES_128 :
                    strengthCheckbox[4].setSelection( true );
                    break;
                    
                case AES_256 :
                    strengthCheckbox[5].setSelection( true );
                    break;
                    
                default :
                    otherText.setText( Integer.toString( ssfWrapper.getNbBits() ) );
                    break;
            }
            
            ssfText.setText( ssfWrapper.toString() );
        }
    }


    /**
     * Creates the Ssf edit area.
     *
     * @param parent the parent composite
     */
    private void createSsfEditArea( Composite parent )
    {
        Group ssfEditGroup = BaseWidgetUtils.createGroup( parent, "Security Strength Factors", 1 );
        ssfEditGroup.setLayout( new GridLayout( 2, false ) );

        // The feature
        featureCombo = BaseWidgetUtils.createCombo( ssfEditGroup, features, 0, 2 );
        
        // No-protection checkbox
        strengthCheckbox[0] = BaseWidgetUtils.createCheckbox( ssfEditGroup, SsfStrengthEnum.NO_PROTECTION.getText(), 1 );
        BaseWidgetUtils.createLabel( ssfEditGroup, TABULATION, 1 );
        
        // Integrity checkbox
        strengthCheckbox[1] = BaseWidgetUtils.createCheckbox( ssfEditGroup, SsfStrengthEnum.INTEGRITY_CHECK.getText(), 1 );
        BaseWidgetUtils.createLabel( ssfEditGroup, TABULATION, 1 );
        
        // DES checkbox
        strengthCheckbox[2] = BaseWidgetUtils.createCheckbox( ssfEditGroup, SsfStrengthEnum.DES.getText(), 1 );
        BaseWidgetUtils.createLabel( ssfEditGroup, TABULATION, 1 );
        
        // 3DES checkbox
        strengthCheckbox[3] = BaseWidgetUtils.createCheckbox( ssfEditGroup, SsfStrengthEnum.THREE_DES.getText(), 1 );
        BaseWidgetUtils.createLabel( ssfEditGroup, TABULATION, 1 );
        
        // AES-128 checkbox
        strengthCheckbox[4] = BaseWidgetUtils.createCheckbox( ssfEditGroup, SsfStrengthEnum.AES_128.getText(), 1 );
        BaseWidgetUtils.createLabel( ssfEditGroup, TABULATION, 1 );
        
        // AES-256 checkbox
        strengthCheckbox[5] = BaseWidgetUtils.createCheckbox( ssfEditGroup, SsfStrengthEnum.AES_256.getText(), 1 );
        BaseWidgetUtils.createLabel( ssfEditGroup, TABULATION, 1 );
        
        // Other Text
        BaseWidgetUtils.createLabel( ssfEditGroup, "Other value :", 1 );
        otherText = BaseWidgetUtils.createText( ssfEditGroup, "", 1 );
        otherText.addModifyListener( otherTextListener );
    }


    /**
     * Creates the SSF show area. It's not editable
     *
     * @param parent the parent composite
     */
    private void createSsfShowArea( Composite parent )
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
        featureCombo.addSelectionListener( featureSelectionListener );
        
        for ( Button checkbox : strengthCheckbox )
        {
            checkbox.addSelectionListener( checkboxSelectionListener );
        }

        otherText.addModifyListener( otherTextListener );
    }


    @Override
    public void addNewElement()
    {
        setEditedElement( new SsfWrapper( "" ) );
    }
}
