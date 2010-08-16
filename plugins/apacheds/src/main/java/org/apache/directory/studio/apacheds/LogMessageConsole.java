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
package org.apache.directory.studio.apacheds;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
 * A console that displays log messages.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LogMessageConsole extends MessageConsole
{
    /** The preference store*/
    private IPreferenceStore preferenceStore;

    /** The stream for Debug level */
    private MessageConsoleStream debugMessageConsoleStream;
    /** The stream for Info level */
    private MessageConsoleStream infoMessageConsoleStream;
    /** The stream for Warn level */
    private MessageConsoleStream warnMessageConsoleStream;
    /** The stream for Error level */
    private MessageConsoleStream errorMessageConsoleStream;
    /** The stream for Fatal level */
    private MessageConsoleStream fatalMessageConsoleStream;


    /**
     * Creates a new instance of LogMessageConsole.
     *
     * @param name
     *      console name
     * @param imageDescriptor
     *      console image descriptor or null
     */
    public LogMessageConsole( String name, ImageDescriptor imageDescriptor )
    {
        super( name, imageDescriptor );

        preferenceStore = ApacheDsPlugin.getDefault().getPreferenceStore();
    }


    /**
     * Creates a new instance of LogMessageConsole.
     *
     * @param name
     *      console name
     */
    public LogMessageConsole( String name )
    {
        super( name, null );

        preferenceStore = ApacheDsPlugin.getDefault().getPreferenceStore();
    }


    /**
     * Gets the Debug stream.
     *
     * @return
     *      the Debug stream
     */
    public MessageConsoleStream getDebugConsoleMessageStream()
    {
        if ( debugMessageConsoleStream == null )
        {
            createDebugMessageConsoleStream();
        }

        return debugMessageConsoleStream;
    }


    /**
     * Creates the Debug stream and set the Color and Font settings to it.
     */
    private void createDebugMessageConsoleStream()
    {
        // Creating the stream
        debugMessageConsoleStream = newMessageStream();

        // Setting the Color and Font settings
        setColorAndFontSettingsToMessageConsoleStream( debugMessageConsoleStream,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_COLOR,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_FONT );
    }


    /**
     * Gets the Info stream.
     *
     * @return
     *      the Info stream
     */
    public MessageConsoleStream getInfoConsoleMessageStream()
    {
        if ( infoMessageConsoleStream == null )
        {
            createInfoMessageConsoleStream();
        }

        return infoMessageConsoleStream;
    }


    /**
     * Creates the Info stream and set the Color and Font settings to it.
     */
    private void createInfoMessageConsoleStream()
    {
        // Creating the stream
        infoMessageConsoleStream = newMessageStream();

        // Setting the Color and Font settings
        setColorAndFontSettingsToMessageConsoleStream( infoMessageConsoleStream,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_COLOR,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_FONT );
    }


    /**
     * Gets the Warn stream.
     *
     * @return
     *      the Warn stream
     */
    public MessageConsoleStream getWarnConsoleMessageStream()
    {
        if ( warnMessageConsoleStream == null )
        {
            createWarnMessageConsoleStream();
        }

        return warnMessageConsoleStream;
    }


    /**
     * Creates the Warn stream and set the Color and Font settings to it.
     */
    private void createWarnMessageConsoleStream()
    {
        // Creating the stream
        warnMessageConsoleStream = newMessageStream();

        // Setting the Color and Font settings
        setColorAndFontSettingsToMessageConsoleStream( warnMessageConsoleStream,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_COLOR,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_FONT );
    }


    /**
     * Gets the Error stream.
     *
     * @return
     *      the Error stream
     */
    public MessageConsoleStream getErrorConsoleMessageStream()
    {
        if ( errorMessageConsoleStream == null )
        {
            createErrorMessageConsoleStream();
        }

        return errorMessageConsoleStream;
    }


    /**
     * Creates the Error stream and set the Color and Font settings to it.
     */
    private void createErrorMessageConsoleStream()
    {
        // Creating the stream
        errorMessageConsoleStream = newMessageStream();

        // Setting the Color and Font settings
        setColorAndFontSettingsToMessageConsoleStream( errorMessageConsoleStream,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_COLOR,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_FONT );
    }


    /**
     * Gets the Fatal stream.
     *
     * @return
     *      the Fatal stream
     */
    public MessageConsoleStream getFatalConsoleMessageStream()
    {
        if ( fatalMessageConsoleStream == null )
        {
            createFatalMessageConsoleStream();
        }

        return fatalMessageConsoleStream;
    }


    /**
     * Creates the Fatal stream and set the Color and Font settings to it.
     */
    private void createFatalMessageConsoleStream()
    {
        // Creating the stream
        fatalMessageConsoleStream = newMessageStream();

        // Setting the Color and Font settings
        setColorAndFontSettingsToMessageConsoleStream( fatalMessageConsoleStream,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_COLOR,
            ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_FONT );
    }


    /**
     * Sets the Color and Font settings to the given stream.
     *
     * @param messageConsoleStream
     *      the stream
     * @param colorPreferenceName
     *      the preference name for the color
     * @param fontPreferenceName
     *      the preference name for the font
     */
    private void setColorAndFontSettingsToMessageConsoleStream( MessageConsoleStream messageConsoleStream,
        String colorPreferenceName, String fontPreferenceName )
    {
        // Getting Color and Font settings from the preference store
        RGB rgb = PreferenceConverter.getColor( preferenceStore, colorPreferenceName );
        FontData[] fontDatas = PreferenceConverter.getFontDataArray( preferenceStore, fontPreferenceName );

        // Creating a style to apply to the font
        int style = SWT.NORMAL;
        if ( PreferenceStoreUtils.isBold( fontDatas ) )
        {
            style |= SWT.BOLD;
        }
        if ( PreferenceStoreUtils.isItalic( fontDatas ) )
        {
            style |= SWT.ITALIC;
        }

        // Applying settings to the stream
        messageConsoleStream.setColor( new Color( ApacheDsPlugin.getDefault().getWorkbench().getDisplay(), rgb ) );
        messageConsoleStream.setFontStyle( style );
    }
}
