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

package org.apache.directory.studio.ldapbrowser.common;


import java.util.ArrayList;
import java.util.Collection;

import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager;
import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager.ValueEditorExtension;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.schema.AttributeValueProviderRelation;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SyntaxValueProviderRelation;
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
public class BrowserCommonPreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {

        IPreferenceStore store = BrowserCommonActivator.getDefault().getPreferenceStore();

        // Common
        store.setDefault( BrowserCommonConstants.PREFERENCE_COUNT_LIMIT, 1000 );
        store.setDefault( BrowserCommonConstants.PREFERENCE_TIME_LIMIT, 0 );

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
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_OBJECTCLASS_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_OBJECTCLASS_FONT, fontDataBoldItalic );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_FONT, fontDataBold );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_FONT, fontDataNormal );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_COLOR, rgbBlack );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_FONT, fontDataItalic );
        // Error/Warning colors and fonts
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_WARNING_FONT, fontDataBoldItalic );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_WARNING_COLOR, rgbYellow );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_ERROR_FONT, fontDataBoldItalic );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_ERROR_COLOR, rgbRed );
        // Quick filter background colors and fonts
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_QUICKFILTER_FONT, fontDataBold );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_QUICKFILTER_BACKGROUND_COLOR, rgbYellow );
        PreferenceConverter.setDefault( store, BrowserCommonConstants.PREFERENCE_QUICKFILTER_FOREGROUND_COLOR, rgbBlack );

        // Attributes
        store.setDefault( BrowserCommonConstants.PREFERENCE_SHOW_RAW_VALUES, false );

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
        BrowserCommonActivator.getDefault().getValueEditorsPreferences().setDefaultAttributeValueProviderRelations(
            avprs.toArray( new AttributeValueProviderRelation[0] ) );
        BrowserCommonActivator.getDefault().getValueEditorsPreferences().setDefaultSyntaxValueProviderRelations(
            svprs.toArray( new SyntaxValueProviderRelation[0] ) );

        // Browser
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_EXPAND_BASE_ENTRIES, false );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_ENABLE_FOLDING, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_FOLDING_SIZE, 100 );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_LABEL, BrowserCommonConstants.SHOW_RDN );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_ENTRY_ABBREVIATE_MAX_LENGTH, 50 );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_LABEL, BrowserCommonConstants.SHOW_DN );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SEARCH_RESULT_ABBREVIATE_MAX_LENGTH, 50 );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_DIT, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_SEARCHES, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_BOOKMARKS, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SHOW_DIRECTORY_META_ENTRIES, false );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_BY, BrowserCoreConstants.SORT_BY_RDN_VALUE );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_ORDER, BrowserCoreConstants.SORT_ORDER_ASCENDING );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_SORT_LIMIT, 10000 );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_LEAF_ENTRIES_FIRST, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_BROWSER_META_ENTRIES_LAST, true );

        // Entry Editor
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD, 10 );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES, false );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST, true );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY,
            BrowserCoreConstants.SORT_BY_ATTRIBUTE_DESCRIPTION );
        store.setDefault( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER,
            BrowserCoreConstants.SORT_ORDER_ASCENDING );

        

        // Text Format
        store.setDefault( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER, "\t" );
        store.setDefault( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_VALUEDELIMITER, "|" );
        store.setDefault( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_QUOTECHARACTER, "\"" );
        store
            .setDefault( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_LINESEPARATOR, BrowserCoreConstants.LINE_SEPARATOR );
        store.setDefault( BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_BINARYENCODING,
            BrowserCoreConstants.BINARYENCODING_IGNORE );
    }

}
