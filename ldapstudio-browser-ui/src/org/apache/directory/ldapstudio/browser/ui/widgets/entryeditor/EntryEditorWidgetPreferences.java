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

package org.apache.directory.ldapstudio.browser.ui.widgets.entryeditor;


import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


public class EntryEditorWidgetPreferences implements IPropertyChangeListener
{

    protected Viewer viewer;


    public EntryEditorWidgetPreferences()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
    }


    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
    }


    public void dispose()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
        this.viewer = null;
    }


    public boolean isUseFolding()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING );
    }


    public int getFoldingThreshold()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD );
    }


    public boolean isShowMayAttributes()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES );
    }


    public boolean isShowMustAttributes()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES );
    }


    public boolean isShowObjectClassAttribute()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES );
    }


    public boolean isShowOperationalAttributes()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );
    }


    public boolean isObjectClassAndMustAttributesFirst()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST );
    }


    public boolean isOperationalAttributesLast()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST );
    }


    public int getDefaultSortBy()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY );
    }


    public int getDefaultSortOrder()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER );
    }


    public void propertyChange( PropertyChangeEvent event )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }

}
