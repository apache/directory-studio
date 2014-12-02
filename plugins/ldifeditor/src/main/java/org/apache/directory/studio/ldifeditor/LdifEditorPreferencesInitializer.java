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

package org.apache.directory.studio.ldifeditor;


import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;


/**
 * This class is used to set default preference values for the LDIF editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifEditorPreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {

        IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();

        // LDIF Editor
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FORMATTER_AUTOWRAP, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS, false );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_DOUBLECLICK_USELDIFDOUBLECLICK, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY, 200 );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_SMARTINSERTATTRIBUTEINMODSPEC, true );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_UPDATEIFENTRYEXISTS, false );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_OPTIONS_CONTINUEONERROR, true );

        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 64, 128, 96 ) );// green
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_COMMENT
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.NORMAL );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, CommonUIConstants.M_GREY );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_KEYWORD
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_DN
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, CommonUIConstants.BLACK );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_DN
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 128, 0, 96 ) );// violet
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_ATTRIBUTE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, CommonUIConstants.RED );
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUETYPE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, CommonUIConstants.L_BLUE );// blue
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_VALUE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.NORMAL );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 128, 160, 96 ) );// green
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEADD
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 224, 160, 96 ) );// yellow
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODIFY
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 255, 64, 96 ) );// red
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEDELETE
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
        PreferenceConverter.setDefault( store, LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_RGB_SUFFIX, new RGB( 128, 160, 192 ) );// bright
        // blue
        store.setDefault( LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_CHANGETYPEMODDN
            + LdifEditorConstants.PREFERENCE_LDIFEDITOR_SYNTAX_STYLE_SUFFIX, SWT.BOLD );
    }

}
