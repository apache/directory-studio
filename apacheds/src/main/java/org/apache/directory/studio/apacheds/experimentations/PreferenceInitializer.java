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
package org.apache.directory.studio.apacheds.experimentations;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


/**
 * This class is used to set default preference values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences()
    {
        // The preference store
        IPreferenceStore store = ApacheDsPlugin.getDefault().getPreferenceStore();

        // Fonts
        FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
        FontData fontDataNormal = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.NORMAL );
        FontData fontDataItalic = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC );
        FontData fontDataBold = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD );

        // Colors
        RGB rgbBlue = new RGB( 0, 0, 192 );
        RGB rgbGreen = new RGB( 63, 127, 95 );
        RGB rgbOrange = new RGB( 255, 127, 0 );
        RGB rgbRed = new RGB( 255, 0, 0 );
        RGB rgbDarkRed = new RGB( 127, 0, 0 );

        // Debug
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_FONT, fontDataItalic );
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_DEBUG_COLOR, rgbBlue );
        // Info
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_FONT, fontDataItalic );
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_INFO_COLOR, rgbGreen );
        // Warn
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_FONT, fontDataNormal );
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_WARN_COLOR, rgbOrange );
        // Error
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_FONT, fontDataBold );
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_ERROR_COLOR, rgbRed );
        // Fatal
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_FONT, fontDataBold );
        PreferenceConverter.setDefault( store, ApacheDsPluginConstants.PREFS_COLORS_AND_FONTS_FATAL_COLOR, rgbDarkRed );
    }
}
