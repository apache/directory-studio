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
package org.apache.directory.studio.apacheds.prefs;


import org.apache.directory.studio.apacheds.ApacheDsPlugin;
import org.apache.directory.studio.apacheds.ApacheDsPluginConstants;
import org.apache.directory.studio.apacheds.LogMessageConsole;
import org.apache.directory.studio.apacheds.PreferenceStoreUtils;
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
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
 * This class implements the Preference main page for the Apache Directory Server plugin.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ColorsAndFontsPage extends PreferencePage implements IWorkbenchPreferencePage
{
    // UI fields
    private ColorSelector debugColorButton;
    private Button debugBoldCheckbox;
    private Button debugItalicCheckbox;
    private ColorSelector infoColorButton;
    private Button infoBoldCheckbox;
    private Button infoItalicCheckbox;
    private ColorSelector warnColorButton;
    private Button warnBoldCheckbox;
    private Button warnItalicCheckbox;
    private ColorSelector errorColorButton;
    private Button errorBoldCheckbox;
    private Button errorItalicCheckbox;
    private ColorSelector fatalColorButton;
    private Button fatalBoldCheckbox;
    private Button fatalItalicCheckbox;


    /**
     * Creates a new instance of MainPage.
     */
    public ColorsAndFontsPage()
    {
        super( "Colors and Fonts" );
        setPreferenceStore( ApacheDsPlugin.getDefault().getPreferenceStore() );
        setDescription( "Colors and Fonts settings for the Apache Directory Server Plugin" );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents( Composite parent )
    {
        // Composite
        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout() );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        // Console Group
        Group consoleGroup = new Group( composite, SWT.NONE );
        consoleGroup.setText( "Console" );
        GridLayout consoleGroupLayout = new GridLayout( 4, false );
        consoleGroup.setLayout( consoleGroupLayout );
        consoleGroup.setLayoutData( new GridData( SWT.FILL, SWT.NONE, true, false ) );

        // Debug
        Label debugLabel = new Label( consoleGroup, SWT.NONE );
        debugLabel.setText( "Debug:" );
        debugColorButton = new ColorSelector( consoleGroup );
        debugColorButton.getButton().setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false ) );
        debugBoldCheckbox = new Button( consoleGroup, SWT.CHECK );
        debugBoldCheckbox.setText( "Bold" );
        debugItalicCheckbox = new Button( consoleGroup, SWT.CHECK );
        debugItalicCheckbox.setText( "Italic" );

        // Info
        Label infoLabel = new Label( consoleGroup, SWT.NONE );
        infoLabel.setText( "Info:" );
        infoColorButton = new ColorSelector( consoleGroup );
        infoColorButton.getButton().setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false ) );
        infoBoldCheckbox = new Button( consoleGroup, SWT.CHECK );
        infoBoldCheckbox.setText( "Bold" );
        infoItalicCheckbox = new Button( consoleGroup, SWT.CHECK );
        infoItalicCheckbox.setText( "Italic" );

        // Warn
        Label warnLabel = new Label( consoleGroup, SWT.NONE );
        warnLabel.setText( "Warn:" );
        warnColorButton = new ColorSelector( consoleGroup );
        warnColorButton.getButton().setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false ) );
        warnBoldCheckbox = new Button( consoleGroup, SWT.CHECK );
        warnBoldCheckbox.setText( "Bold" );
        warnItalicCheckbox = new Button( consoleGroup, SWT.CHECK );
        warnItalicCheckbox.setText( "Italic" );

        // Error
        Label errorLabel = new Label( consoleGroup, SWT.NONE );
        errorLabel.setText( "Error:" );
        errorColorButton = new ColorSelector( consoleGroup );
        errorColorButton.getButton().setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false ) );
        errorBoldCheckbox = new Button( consoleGroup, SWT.CHECK );
        errorBoldCheckbox.setText( "Bold" );
        errorItalicCheckbox = new Button( consoleGroup, SWT.CHECK );
        errorItalicCheckbox.setText( "Italic" );

        // Fatal
        Label fatalLabel = new Label( consoleGroup, SWT.NONE );
        fatalLabel.setText( "Fatal:" );
        fatalColorButton = new ColorSelector( consoleGroup );
        fatalColorButton.getButton().setLayoutData( new GridData( SWT.RIGHT, SWT.NONE, true, false ) );
        fatalBoldCheckbox = new Button( consoleGroup, SWT.CHECK );
        fatalBoldCheckbox.setText( "Bold" );
        fatalItalicCheckbox = new Button( consoleGroup, SWT.CHECK );
        fatalItalicCheckbox.setText( "Italic" );

        initFromPreferences();

        applyDialogFont( composite );
        return composite;
    }


    /**
     * Initializes the UI fields from the preferences values.
     */
    private void initFromPreferences()
    {
        // Debug
        FontData[] fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_FONT );
        RGB rgb = PreferenceConverter.getColor( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_COLOR );
        setDebug( fontDatas, rgb );

        // Info
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_FONT );
        rgb = PreferenceConverter.getColor( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_COLOR );
        setInfo( fontDatas, rgb );

        // Warn
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_FONT );
        rgb = PreferenceConverter.getColor( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_COLOR );
        setWarn( fontDatas, rgb );

        // Error
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_FONT );
        rgb = PreferenceConverter.getColor( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_COLOR );
        setError( fontDatas, rgb );

        // Fatal
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_FONT );
        rgb = PreferenceConverter.getColor( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_COLOR );
        setFatal( fontDatas, rgb );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults()
    {
        // Debug
        FontData[] fontDatas = PreferenceConverter.getDefaultFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_FONT );
        RGB rgb = PreferenceConverter.getDefaultColor( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_COLOR );
        setDebug( fontDatas, rgb );

        // Info
        fontDatas = PreferenceConverter.getDefaultFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_FONT );
        rgb = PreferenceConverter.getDefaultColor( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_COLOR );
        setInfo( fontDatas, rgb );

        // Warn
        fontDatas = PreferenceConverter.getDefaultFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_FONT );
        rgb = PreferenceConverter.getDefaultColor( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_COLOR );
        setWarn( fontDatas, rgb );

        // Error
        fontDatas = PreferenceConverter.getDefaultFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_FONT );
        rgb = PreferenceConverter.getDefaultColor( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_COLOR );
        setError( fontDatas, rgb );

        // Fatal
        fontDatas = PreferenceConverter.getDefaultFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_FONT );
        rgb = PreferenceConverter.getDefaultColor( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_COLOR );
        setFatal( fontDatas, rgb );

        super.performDefaults();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    public boolean performOk()
    {
        // Debug
        FontData[] fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_FONT );
        setFontData( fontDatas, debugBoldCheckbox, debugItalicCheckbox );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_FONT,
            fontDatas );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_COLOR,
            debugColorButton.getColorValue() );

        // Info
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_FONT );
        setFontData( fontDatas, infoBoldCheckbox, infoItalicCheckbox );
        PreferenceConverter
            .setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_FONT, fontDatas );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_COLOR,
            infoColorButton.getColorValue() );

        // Warn
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_FONT );
        setFontData( fontDatas, warnBoldCheckbox, warnItalicCheckbox );
        PreferenceConverter
            .setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_FONT, fontDatas );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_COLOR,
            warnColorButton.getColorValue() );

        // Error
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_FONT );
        setFontData( fontDatas, errorBoldCheckbox, errorItalicCheckbox );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_FONT,
            fontDatas );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_COLOR,
            errorColorButton.getColorValue() );

        // Error
        fontDatas = PreferenceConverter.getFontDataArray( getPreferenceStore(),
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_FONT );
        setFontData( fontDatas, fatalBoldCheckbox, fatalItalicCheckbox );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_FONT,
            fontDatas );
        PreferenceConverter.setValue( getPreferenceStore(), ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_COLOR,
            fatalColorButton.getColorValue() );

        LogMessageConsole logMessageConsole = new LogMessageConsole( "toto" );
        ConsolePlugin.getDefault().getConsoleManager().addConsoles( new IConsole[]
            { logMessageConsole } );

        RGB rgbBlue = new RGB( 0, 0, 192 );
        RGB rgbGreen = new RGB( 63, 127, 95 );
        RGB rgbOrange = new RGB( 255, 127, 0 );
        RGB rgbRed = new RGB( 255, 0, 0 );
        RGB rgbDarkRed = new RGB( 127, 0, 0 );

        MessageConsoleStream messageConsoleStream = logMessageConsole.getDebugConsoleMessageStream();
        messageConsoleStream
            .println( "[11:23:01] DEBUG [org.apache.directory.server.core.DefaultDirectoryService] - You didn't change the admin password of directory service instance 'default'.  Please update the admin password as soon as possible to prevent a possible security breach." );

        messageConsoleStream = logMessageConsole.getInfoConsoleMessageStream();
        messageConsoleStream
            .println( "[11:23:01] INFO [org.apache.directory.server.core.DefaultDirectoryService] - You didn't change the admin password of directory service instance 'default'.  Please update the admin password as soon as possible to prevent a possible security breach." );

        messageConsoleStream = logMessageConsole.getWarnConsoleMessageStream();
        messageConsoleStream
            .println( "[11:23:01] WARN [org.apache.directory.server.core.DefaultDirectoryService] - You didn't change the admin password of directory service instance 'default'.  Please update the admin password as soon as possible to prevent a possible security breach." );

        messageConsoleStream = logMessageConsole.getErrorConsoleMessageStream();
        messageConsoleStream
            .println( "[11:23:01] ERROR [org.apache.directory.server.core.DefaultDirectoryService] - You didn't change the admin password of directory service instance 'default'.  Please update the admin password as soon as possible to prevent a possible security breach." );

        messageConsoleStream = logMessageConsole.getFatalConsoleMessageStream();
        messageConsoleStream
            .println( "[11:23:01] FATAL [org.apache.directory.server.core.DefaultDirectoryService] - You didn't change the admin password of directory service instance 'default'.  Please update the admin password as soon as possible to prevent a possible security breach." );

        return super.performOk();
    }


    /**
     * Set the font, bold and italic values to the Debug UI fields.
     *
     * @param fontDatas
     *      the array of {@link FontData}
     * @param rgb
     *      the color
     */
    private void setDebug( FontData[] fontDatas, RGB rgb )
    {
        boolean bold = PreferenceStoreUtils.isBold( fontDatas );
        boolean italic = PreferenceStoreUtils.isItalic( fontDatas );
        debugColorButton.setColorValue( rgb );
        debugBoldCheckbox.setSelection( bold );
        debugItalicCheckbox.setSelection( italic );
    }


    /**
     * Set the font, bold and italic values to the Info UI fields.
     *
     * @param fontDatas
     *      the array of {@link FontData}
     * @param rgb
     *      the color
     */
    private void setInfo( FontData[] fontDatas, RGB rgb )
    {
        boolean bold = PreferenceStoreUtils.isBold( fontDatas );
        boolean italic = PreferenceStoreUtils.isItalic( fontDatas );
        infoColorButton.setColorValue( rgb );
        infoBoldCheckbox.setSelection( bold );
        infoItalicCheckbox.setSelection( italic );
    }


    /**
     * Set the font, bold and italic values to the Warn UI fields.
     *
     * @param fontDatas
     *      the array of {@link FontData}
     * @param rgb
     *      the color
     */
    private void setWarn( FontData[] fontDatas, RGB rgb )
    {
        boolean bold = PreferenceStoreUtils.isBold( fontDatas );
        boolean italic = PreferenceStoreUtils.isItalic( fontDatas );
        warnColorButton.setColorValue( rgb );
        warnBoldCheckbox.setSelection( bold );
        warnItalicCheckbox.setSelection( italic );
    }


    /**
     * Set the font, bold and italic values to the Info UI fields.
     *
     * @param fontDatas
     *      the array of {@link FontData}
     * @param rgb
     *      the color
     */
    private void setError( FontData[] fontDatas, RGB rgb )
    {
        boolean bold = PreferenceStoreUtils.isBold( fontDatas );
        boolean italic = PreferenceStoreUtils.isItalic( fontDatas );
        errorColorButton.setColorValue( rgb );
        errorBoldCheckbox.setSelection( bold );
        errorItalicCheckbox.setSelection( italic );
    }


    /**
     * Set the font, bold and italic values to the Fatal UI fields.
     *
     * @param fontDatas
     *      the array of {@link FontData}
     * @param rgb
     *      the color
     * @param bgRgb
     *      the background color
     */
    private void setFatal( FontData[] fontDatas, RGB rgb )
    {
        boolean bold = PreferenceStoreUtils.isBold( fontDatas );
        boolean italic = PreferenceStoreUtils.isItalic( fontDatas );
        fatalColorButton.setColorValue( rgb );
        fatalBoldCheckbox.setSelection( bold );
        fatalItalicCheckbox.setSelection( italic );
    }


    /**
     * Sets the bold and italic values to the array of {@link FontData}.
     *
     * @param fontDatas
     *      the array of {@link FontData}
     * @param boldButton
     *      the bold button
     * @param italicButton
     *      the italic button
     */
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

            fontData.setStyle( style );
        }
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench )
    {
        // Nothing to do
    }
}
