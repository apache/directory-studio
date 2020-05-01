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

package org.apache.directory.studio.common.ui;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;


/**
 * This class is used to set default preference values.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CommonUIPreferencesInitializer extends AbstractPreferenceInitializer
{
    /**
     * {@inheritDoc}
     */
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = CommonUIPlugin.getDefault().getPreferenceStore();

        // Actual colors are defined in default.css and dark.css
        RGB black = new RGB( 0, 0, 0 );
        PreferenceConverter.setDefault( store, CommonUIConstants.DEFAULT_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.DISABLED_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.ERROR_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.COMMENT_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.KEYWORD_1_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.KEYWORD_2_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.OBJECT_CLASS_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.ATTRIBUTE_TYPE_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.VALUE_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.OID_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.SEPARATOR_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.ADD_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.DELETE_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.MODIFY_COLOR, black );
        PreferenceConverter.setDefault( store, CommonUIConstants.RENAME_COLOR, black );
    }

}
