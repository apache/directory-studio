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

package org.apache.directory.ldapstudio.browser.common.dialogs.preferences;


import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.common.widgets.BaseWidgetUtils;
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


public class AttributesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private final String[] ATTRIBUTE_TYPES = new String[]
        { "Objectclass attribute:", "Must attributes:", "May attributes:", "Operational attributes:" };

    private final String[] ATTRIBUTE_FONT_CONSTANTS = new String[]
        { BrowserCommonConstants.PREFERENCE_OBJECTCLASS_FONT, BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_FONT,
        BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_FONT, BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_FONT };

    private final String[] ATTRIBUTE_COLOR_CONSTANTS = new String[]
        { BrowserCommonConstants.PREFERENCE_OBJECTCLASS_COLOR, BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_COLOR,
        BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_COLOR, BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_COLOR };

    private Label[] attributeTypeLabels = new Label[ATTRIBUTE_TYPES.length];

    private ColorSelector[] attributeColorSelectors = new ColorSelector[ATTRIBUTE_TYPES.length];

    private Button[] attributeBoldButtons = new Button[ATTRIBUTE_TYPES.length];

    private Button[] attributeItalicButtons = new Button[ATTRIBUTE_TYPES.length];

    private Button showRawValuesButton;


    public AttributesPreferencePage()
    {
        super( "Attributes" );
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for attributes:" );
    }


    public void init( IWorkbench workbench )
    {
    }


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
        Group colorsAndFontsGroup = BaseWidgetUtils.createGroup( composite, "Attribute Colors and Fonts", 1 );
        colorsAndFontsGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite colorsAndFontsComposite = BaseWidgetUtils.createColumnContainer( colorsAndFontsGroup, 4, 1 );
        for ( int i = 0; i < ATTRIBUTE_TYPES.length; i++ )
        {
            final int index = i;

            attributeTypeLabels[i] = BaseWidgetUtils.createLabel( colorsAndFontsComposite, ATTRIBUTE_TYPES[i], 1 );
            attributeTypeLabels[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            attributeColorSelectors[i] = new ColorSelector( colorsAndFontsComposite );
            attributeBoldButtons[i] = BaseWidgetUtils.createCheckbox( colorsAndFontsComposite, "Bold", 1 );
            attributeItalicButtons[i] = BaseWidgetUtils.createCheckbox( colorsAndFontsComposite, "Italic", 1 );

            FontData[] fontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ATTRIBUTE_FONT_CONSTANTS[i] );
            RGB rgb = PreferenceConverter.getColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_COLOR_CONSTANTS[i] );
            setColorsAndFonts( index, fontDatas, rgb );
        }

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        showRawValuesButton = BaseWidgetUtils.createCheckbox( composite, "Show raw values", 1 );
        showRawValuesButton.setSelection( getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );

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
        for ( int j = 0; j < fontDatas.length; j++ )
        {
            int style = SWT.NORMAL;
            if ( boldButton.getSelection() )
                style |= SWT.BOLD;
            if ( italicButton.getSelection() )
                style |= SWT.ITALIC;
            fontDatas[j].setStyle( style );
        }
    }


    private boolean isBold( FontData[] fontDatas )
    {
        boolean bold = false;
        for ( int j = 0; j < fontDatas.length; j++ )
        {
            if ( ( fontDatas[j].getStyle() & SWT.BOLD ) != SWT.NORMAL )
                bold = true;
        }
        return bold;
    }


    private boolean isItalic( FontData[] fontDatas )
    {
        boolean italic = false;
        for ( int j = 0; j < fontDatas.length; j++ )
        {
            if ( ( fontDatas[j].getStyle() & SWT.ITALIC ) != SWT.NORMAL )
                italic = true;
        }
        return italic;
    }


    public boolean performOk()
    {

        for ( int i = 0; i < ATTRIBUTE_TYPES.length; i++ )
        {
            FontData[] fontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ATTRIBUTE_FONT_CONSTANTS[i] );
            setFontData( fontDatas, this.attributeBoldButtons[i], this.attributeItalicButtons[i] );
            RGB rgb = attributeColorSelectors[i].getColorValue();
            PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_FONT_CONSTANTS[i], fontDatas );
            PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_COLOR_CONSTANTS[i], rgb );
        }

        getPreferenceStore().setValue( BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES,
            this.showRawValuesButton.getSelection() );

        return true;
    }


    protected void performDefaults()
    {

        for ( int i = 0; i < ATTRIBUTE_TYPES.length; i++ )
        {
            FontData[] fontDatas = PreferenceConverter.getDefaultFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ATTRIBUTE_FONT_CONSTANTS[i] );
            RGB rgb = PreferenceConverter.getDefaultColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ATTRIBUTE_COLOR_CONSTANTS[i] );
            setColorsAndFonts( i, fontDatas, rgb );
        }

        showRawValuesButton.setSelection( getPreferenceStore().getDefaultBoolean(
            BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES ) );

        super.performDefaults();
    }

}
