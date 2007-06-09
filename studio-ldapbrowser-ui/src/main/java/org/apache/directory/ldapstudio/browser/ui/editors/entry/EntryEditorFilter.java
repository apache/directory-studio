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

package org.apache.directory.ldapstudio.browser.ui.editors.entry;


import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetFilter;
import org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor.EntryEditorWidgetPreferences;


/**
 * The EntryEditorFilter implements the filter for
 * the entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorFilter extends EntryEditorWidgetFilter
{

    /** The preferences. */
    private EntryEditorWidgetPreferences preferences;


    /**
     * Creates a new instance of EntryEditorFilter.
     * 
     * @param preferences the preferences
     */
    public EntryEditorFilter( EntryEditorWidgetPreferences preferences )
    {
        this.preferences = preferences;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isShowMayAttributes()
    {
        return preferences.isShowMayAttributes();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isShowMustAttributes()
    {
        return preferences.isShowMustAttributes();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isShowObjectClassAttribute()
    {
        return preferences.isShowObjectClassAttribute();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isShowOperationalAttributes()
    {
        return preferences.isShowOperationalAttributes();
    }

}
