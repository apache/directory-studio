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

package org.apache.directory.studio.ldapbrowser.common.dialogs.preferences;


import org.apache.directory.studio.common.ui.widgets.BaseWidgetUtils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The AttributesPreferencePage contains general settings for attributes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private Button showDecoratedValuesButton;

    private final String[] ATTRIBUTE_TYPES = new String[]
        {
            Messages.getString( "AttributesPreferencePage.ObjectClassAttribute" ), Messages.getString( "AttributesPreferencePage.MustAttributes" ), Messages.getString( "AttributesPreferencePage.MayAttributes" ), Messages.getString( "AttributesPreferencePage.OperationalAttributes" ) }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    private final String[] ATTRIBUTE_FONT_CONSTANTS = new String[]
        { BrowserCommonConstants.PREFERENCE_OBJECTCLASS_FONT, BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_FONT,
            BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_FONT,
            BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_FONT };

    private final String[] ATTRIBUTE_COLOR_CONSTANTS = new String[]
        { BrowserCommonConstants.PREFERENCE_OBJECTCLASS_COLOR, BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_COLOR,
            BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_COLOR,
            BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_COLOR };

    private Label[] attributeTypeLabels = new Label[ATTRIBUTE_TYPES.length];

    private ColorSelector[] attributeColorSelectors = new ColorSelector[ATTRIBUTE_TYPES.length];

    private Button[] attributeBoldButtons = new Button[ATTRIBUTE_TYPES.length];

    private Button[] attributeItalicButtons = new Button[ATTRIBUTE_TYPES.length];


    /**
     * Creates a new instance of AttributesPreferencePage.
     */
    public AttributesPreferencePage()
    {
        super( Messages.getString( "AttributesPreferencePage.Attributes" ) ); //$NON-NLS-1$
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( Messages.getString( "AttributesPreferencePage.GeneralSettings" ) ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public void init( IWorkbench workbench )
    {
    }


    /**
     * {@inheritDoc}
     */
    protected Control createContents( Composite parent )
    {
        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        composite.setLayout( layout );
        composite.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );

        // Show Decorated Values
        showDecoratedValuesButton = BaseWidgetUtils.createCheckbox( composite, Messages
            .getString( "AttributesPreferencePage.ShowDecoratedValues" ), 1 ); //$NON-NLS-1$
        showDecoratedValuesButton.setSelection( !getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );

        // Attributes Colors And Fonts
        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group colorsAndFontsGroup = BaseWidgetUtils.createGroup( composite, Messages
            .getString( "AttributesPreferencePage.AttributeColorsAndFonts" ), 1 ); //$NON-NLS-1$
        colorsAndFontsGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite colorsAndFontsComposite = BaseWidgetUtils.createColumnContainer( colorsAndFontsGroup, 4, 1 );
        for ( int i = 0; i < ATTRIBUTE_TYPES.length; i++ )
        {
            attributeTypeLabels[i] = BaseWidgetUtils.createLabel( colorsAndFontsComposite, ATTRIBUTE_TYPES[i], 1 );
            attributeTypeLabels[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            attributeColorSelectors[i] = new ColorSelector( colorsAndFontsComposite );
            attributeBoldButtons[i] = BaseWidgetUtils.createCheckbox( colorsAndFontsComposite, Messages
                .getString( "AttributesPreferencePage.Bold" ), 1 ); //$NON-NLS-1$
            attributeItalicButtons[i] = BaseWidgetUtils.createCheckbox( colorsAndFontsComposite, Messages
                .getString( "AttributesPreferencePage.Italic" ), 1 ); //$NON-NLS-1$

            FontData[] fontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ATTRIBUTE_FONT_CONSTANTS[i] );
            RGB rgb = PreferenceConverter.getColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_COLOR_CONSTANTS[i] );
            setColorsAndFonts( i, fontDatas, rgb );
        }

        applyDialogFont( composite );
        return composite;
    }


    private void setColorsAndFonts( int index, FontData[] fontDatas, RGB rgb )
    {
        boolean bold = isBold( fontDatas );
        boolean italic = isItalic( fontDatas );
        attributeColorSelectors[index].setColorValue( rgb );
        attributeBoldButtons[index].setSelection( bold );
        attributeItalicButtons[index].setSelection( italic );
    }


    private void setFontData( FontData[] fontDatas, Button boldButton, Button italicButton )
    {
        for ( FontData fontData : fontDatas )
        {
            int style = SWT.NORMAL;
            if ( boldButton.getSelection() )
            {
                style |= SWT.BOLD;
            }
            if ( italicButton.getSelection() )
            {
                style |= SWT.ITALIC;
            }
//            fontData.setStyle( style );
        }
    }


    private boolean isBold( FontData[] fontDatas )
    {
        boolean bold = false;
        for ( FontData fontData : fontDatas )
        {
            if ( ( fontData.getStyle() & SWT.BOLD ) != SWT.NORMAL )
            {
                bold = true;
            }
        }
        return bold;
    }


    private boolean isItalic( FontData[] fontDatas )
    {
        boolean italic = false;
        for ( FontData fontData : fontDatas )
        {
            if ( ( fontData.getStyle() & SWT.ITALIC ) != SWT.NORMAL )
            {
                italic = true;
            }
        }
        return italic;
    }


    /**
     * {@inheritDoc}
     */
    public boolean performOk()
    {
        // Show Decorated Values
        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES,
            !showDecoratedValuesButton.getSelection() );

        // Attributes Colors And Fonts
        for ( int i = 0; i < ATTRIBUTE_TYPES.length; i++ )
        {
            FontData[] fontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ATTRIBUTE_FONT_CONSTANTS[i] );
            setFontData( fontDatas, attributeBoldButtons[i], attributeItalicButtons[i] );
            RGB rgb = attributeColorSelectors[i].getColorValue();
            PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_FONT_CONSTANTS[i], fontDatas );
            PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_COLOR_CONSTANTS[i], rgb );
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    protected void performDefaults()
    {
        // Show Decorated Values
        showDecoratedValuesButton.setSelection( !getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );

        // Attributes Colors And Fonts
        for ( int i = 0; i < ATTRIBUTE_TYPES.length; i++ )
        {
            FontData[] fontDatas = PreferenceConverter.getDefaultFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ATTRIBUTE_FONT_CONSTANTS[i] );
            RGB rgb = PreferenceConverter.getDefaultColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_COLOR_CONSTANTS[i] );
            setColorsAndFonts( i, fontDatas, rgb );
        }

        super.performDefaults();
    }

}
