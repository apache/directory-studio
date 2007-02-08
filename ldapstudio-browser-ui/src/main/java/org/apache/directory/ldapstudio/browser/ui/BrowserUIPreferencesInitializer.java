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

package org.apache.directory.ldapstudio.browser.ui;


import java.util.ArrayList;
import java.util.Collection;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.ldapstudio.browser.core.model.schema.SyntaxValueProviderRelation;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager.ValueEditorExtension;
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
public class BrowserUIPreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {

        IPreferenceStore store = BrowserUIPlugin.getDefault().getPreferenceStore();

        // Common
        store.setDefault( BrowserUIConstants.PREFERENCE_COUNT_LIMIT, 1000 );
        store.setDefault( BrowserUIConstants.PREFERENCE_TIME_LIMIT, 0 );

        // Colors and Fonts
        RGB rgbBlack = Display.getDefault().getSystemColor( SWT.COLOR_BLACK ).getRGB();
        RGB rgbRed = Display.getDefault().getSystemColor( SWT.COLOR_RED ).getRGB();
        RGB rgbYellow = Display.getDefault().getSystemColor( SWT.COLOR_YELLOW ).getRGB();
        FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
        FontData fontDataNormal = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.NORMAL );
        FontData fontDataItalic = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC );
        FontData fontDataBold = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD );
        FontData fontDataBoldItalic = new FontData( fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD
            | SWT.ITALIC );
        // Attributes colors and fonts
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_OBJECTCLASS_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_OBJECTCLASS_FONT, fontDataBoldItalic );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_MUSTATTRIBUTE_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_MUSTATTRIBUTE_FONT, fontDataBold );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_MAYATTRIBUTE_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_MAYATTRIBUTE_FONT, fontDataNormal );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_OPERATIONALATTRIBUTE_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_OPERATIONALATTRIBUTE_FONT, fontDataItalic );
        // Error/Warning colors and fonts
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_WARNING_FONT, fontDataBoldItalic );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_WARNING_COLOR, rgbYellow );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_ERROR_FONT, fontDataBoldItalic );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_ERROR_COLOR, rgbRed );
        // Quick filter background colors and fonts
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_QUICKFILTER_FONT, fontDataBold );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR, rgbYellow );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR, rgbBlack );

        // Attributes
        store.setDefault( BrowserUIConstants.PREFERENCE_SHOW_RAW_VALUES, false );

        // Value Editors
        Collection<AttributeValueProviderRelation> avprs = new ArrayList<AttributeValueProviderRelation>();
        Collection<SyntaxValueProviderRelation> svprs = new ArrayList<SyntaxValueProviderRelation>();
        Collection<ValueEditorExtension> valueEditorProxys = ValueEditorManager.getValueEditorProxys();
        for ( ValueEditorExtension proxy : valueEditorProxys )
        {
            for ( String attributeType : proxy.attributeTypes )
            {
                AttributeValueProviderRelation avpr = new AttributeValueProviderRelation( attributeType,
                    proxy.className );
                avprs.add( avpr );
            }
            for ( String syntaxOid : proxy.syntaxOids )
            {
                SyntaxValueProviderRelation svpr = new SyntaxValueProviderRelation( syntaxOid, proxy.className );
                svprs.add( svpr );
            }
        }
        BrowserUIPlugin.getDefault().getUIPreferences().setDefaultAttributeValueProviderRelations(
            avprs.toArray( new AttributeValueProviderRelation[0] ) );
        BrowserUIPlugin.getDefault().getUIPreferences().setDefaultSyntaxValueProviderRelations(
            svprs.toArray( new SyntaxValueProviderRelation[0] ) );

        // Browser
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES, false );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_ENABLE_FOLDING, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_FOLDING_SIZE, 100 );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_ENTRY_LABEL, BrowserUIConstants.SHOW_RDN );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH, 50 );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL, BrowserUIConstants.SHOW_DN );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH, 50 );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SHOW_DIT, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SHOW_SEARCHES, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SHOW_BOOKMARKS, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES, false );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SORT_BY, BrowserCoreConstants.SORT_BY_RDN_VALUE );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SORT_ORDER, BrowserCoreConstants.SORT_ORDER_ASCENDING );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_SORT_LIMIT, 10000 );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_BROWSER_META_ENTRIES_LAST, true );

        // Entry Editor
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD, 10 );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES, false );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY,
            BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION );
        store.setDefault( BrowserUIConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER,
            BrowserCoreConstants.SORT_ORDER_ASCENDING );

        // Search Result Editor
        store.setDefault( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_LINKS, true );

        // Text Format
        store.setDefault( BrowserUIConstants.PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER, "\t" );
        store.setDefault( BrowserUIConstants.PREFERENCE_FORMAT_TABLE_VALUEDELIMITER, "|" );
        store.setDefault( BrowserUIConstants.PREFERENCE_FORMAT_TABLE_QUOTECHARACTER, "\"" );
        store
            .setDefault( BrowserUIConstants.PREFERENCE_FORMAT_TABLE_LINESEPARATOR, BrowserCoreConstants.LINE_SEPARATOR );
        store.setDefault( BrowserUIConstants.PREFERENCE_FORMAT_TABLE_BINARYENCODING,
            BrowserCoreConstants.BINARYENCODING_IGNORE );

        // LDIF Editor
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS, false );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION, true );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY, 200 );
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC, true );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 63, 127, 95 ) );// green
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.NORMAL );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 128, 128, 128 ) );// gray
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_DN
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 0, 0, 0 ) );// black
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_DN
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 127, 0, 85 ) );// violett
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 255, 0, 0 ) );// red
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 0, 0, 192 ) );// blue
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.NORMAL );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 123, 170, 91 ) );// green
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 214, 160, 100 ) );// yellow
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 242, 70, 86 ) );// red
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 127, 159, 191 ) );// bright
        // blue
        store.setDefault( BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN
            + BrowserUIConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
    }

}
