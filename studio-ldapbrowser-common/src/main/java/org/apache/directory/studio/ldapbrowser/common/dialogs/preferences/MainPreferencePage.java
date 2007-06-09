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


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.widgets.BaseWidgetUtils;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class MainPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private final String[] ERROR_TYPES = new String[]
        { "Warnings:", "Errors:" };

    private final String[] ERROR_FONT_CONSTANTS = new String[]
        { BrowserCommonConstants.PREFERENCE_WARNING_FONT, BrowserCommonConstants.PREFERENCE_ERROR_FONT };

    private final String[] ERROR_COLOR_CONSTANTS = new String[]
        { BrowserCommonConstants.PREFERENCE_WARNING_COLOR, BrowserCommonConstants.PREFERENCE_ERROR_COLOR };

    private Label[] errorTypeLabels = new Label[ERROR_TYPES.length];

    private ColorSelector[] errorColorSelectors = new ColorSelector[ERROR_TYPES.length];

    private Button[] errorBoldButtons = new Button[ERROR_TYPES.length];

    private Button[] errorItalicButtons = new Button[ERROR_TYPES.length];

    private Label quickfilterTypeLabel;

    private ColorSelector quickfilterForegroundColorSelector;;

    private ColorSelector quickfilterBackgroundColorSelector;;

    private Button quickfilterBoldButton;

    private Button quickfilterItalicButton;


    public MainPreferencePage()
    {
        super( "LDAP" );
        super.setPreferenceStore( BrowserCommonActivator.getDefault().getPreferenceStore() );
        super.setDescription( "General settings for the LDAP plugin:" );
    }


    public void init( IWorkbench workbench )
    {
    }


    protected Control createContents( Composite parent )
    {

        Composite composite = BaseWidgetUtils.createColumnContainer( parent, 1, 1 );

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group errorGroup = BaseWidgetUtils.createGroup( composite, "Warning and Error Colors and Fonts", 1 );
        errorGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite errorComposite = BaseWidgetUtils.createColumnContainer( errorGroup, 4, 1 );
        for ( int i = 0; i < ERROR_TYPES.length; i++ )
        {
            final int index = i;

            errorTypeLabels[i] = BaseWidgetUtils.createLabel( errorComposite, ERROR_TYPES[i], 1 );
            errorTypeLabels[i].setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
            errorColorSelectors[i] = new ColorSelector( errorComposite );
            errorBoldButtons[i] = BaseWidgetUtils.createCheckbox( errorComposite, "Bold", 1 );
            errorItalicButtons[i] = BaseWidgetUtils.createCheckbox( errorComposite, "Italic", 1 );

            FontData[] fontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ERROR_FONT_CONSTANTS[i] );
            RGB rgb = PreferenceConverter.getColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ERROR_COLOR_CONSTANTS[i] );
            setErrors( index, fontDatas, rgb );
        }

        BaseWidgetUtils.createSpacer( composite, 1 );
        BaseWidgetUtils.createSpacer( composite, 1 );
        Group otherGroup = BaseWidgetUtils.createGroup( composite, "Quick Filter Colors and Fonts", 1 );
        otherGroup.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        Composite otherComposite = BaseWidgetUtils.createColumnContainer( otherGroup, 4, 1 );
        quickfilterTypeLabel = BaseWidgetUtils.createLabel( otherComposite, "Quick Filter", 1 );
        quickfilterTypeLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        quickfilterForegroundColorSelector = new ColorSelector( otherComposite );
        quickfilterBoldButton = BaseWidgetUtils.createCheckbox( otherComposite, "Bold", 1 );
        quickfilterItalicButton = BaseWidgetUtils.createCheckbox( otherComposite, "Italic", 1 );
        Label quickfilterBgLabel = BaseWidgetUtils.createLabel( otherComposite, "Quick Filter Background", 1 );
        quickfilterBgLabel.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        quickfilterBackgroundColorSelector = new ColorSelector( otherComposite );
        FontData[] qfFontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
            .getPreferenceStore(), BrowserCommonConstants.PREFERENCE_QUICKFILTER_FONT );
        RGB qfBgRgb = PreferenceConverter.getColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR );
        RGB qfFgRgb = PreferenceConverter.getColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR );
        setQuickfilter( qfFontDatas, qfFgRgb, qfBgRgb );

        return composite;
    }


    private void setErrors( int index, FontData[] fontDatas, RGB rgb )
    {
        boolean bold = isBold( fontDatas );
        boolean italic = isItalic( fontDatas );
        errorColorSelectors[index].setColorValue( rgb );
        errorBoldButtons[index].setSelection( bold );
        errorItalicButtons[index].setSelection( italic );
    }


    private void setQuickfilter( FontData[] fontDatas, RGB fgRgb, RGB bgRgb )
    {
        boolean bold = isBold( fontDatas );
        boolean italic = isItalic( fontDatas );
        quickfilterBackgroundColorSelector.setColorValue( bgRgb );
        quickfilterForegroundColorSelector.setColorValue( fgRgb );
        quickfilterBoldButton.setSelection( bold );
        quickfilterItalicButton.setSelection( italic );
    }


    protected void performDefaults()
    {
        for ( int i = 0; i < ERROR_TYPES.length; i++ )
        {
            FontData[] fontDatas = PreferenceConverter.getDefaultFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ERROR_FONT_CONSTANTS[i] );
            RGB rgb = PreferenceConverter.getDefaultColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
                ERROR_COLOR_CONSTANTS[i] );
            setErrors( i, fontDatas, rgb );
        }

        FontData[] qfFontDatas = PreferenceConverter.getDefaultFontDataArray( BrowserCommonActivator.getDefault()
            .getPreferenceStore(), BrowserCommonConstants.PREFERENCE_QUICKFILTER_FONT );
        RGB qfBgRgb = PreferenceConverter.getDefaultColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR );
        RGB qfFgRgb = PreferenceConverter.getDefaultColor( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR );
        setQuickfilter( qfFontDatas, qfFgRgb, qfBgRgb );

        super.performDefaults();
    }


    public boolean performOk()
    {
        for ( int i = 0; i < ERROR_TYPES.length; i++ )
        {
            FontData[] fontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
                .getPreferenceStore(), ERROR_FONT_CONSTANTS[i] );
            setFontData( fontDatas, this.errorBoldButtons[i], this.errorBoldButtons[i] );
            RGB rgb = errorColorSelectors[i].getColorValue();
            PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(), ERROR_FONT_CONSTANTS[i],
                fontDatas );
            PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(), ERROR_COLOR_CONSTANTS[i],
                rgb );
        }

        FontData[] qfFontDatas = PreferenceConverter.getFontDataArray( BrowserCommonActivator.getDefault()
            .getPreferenceStore(), BrowserCommonConstants.PREFERENCE_QUICKFILTER_FONT );
        setFontData( qfFontDatas, this.quickfilterBoldButton, this.quickfilterItalicButton );
        RGB qfBgRgb = quickfilterBackgroundColorSelector.getColorValue();
        RGB qfFgRgb = quickfilterForegroundColorSelector.getColorValue();
        PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_FONT, qfFontDatas );
        PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR, qfBgRgb );
        PreferenceConverter.setValue( BrowserCommonActivator.getDefault().getPreferenceStore(),
            BrowserCommonConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR, qfFgRgb );

        return true;
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

}
