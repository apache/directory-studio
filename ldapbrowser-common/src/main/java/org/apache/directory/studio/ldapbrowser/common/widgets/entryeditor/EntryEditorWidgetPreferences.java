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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is a wrapper for the preferences of the entry editor widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetPreferences
{

    /** The viewer. */
    protected Viewer viewer;


    /**
     * Creates a new instance of EntryEditorWidgetPreferences.
     */
    public EntryEditorWidgetPreferences()
    {
    }


    /**
     * Connects this preferences with the given viewer.
     * 
     * @param viewer the viewer
     */
    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
    }


    /**
     * Disposes this preferences.
     */
    public void dispose()
    {
        viewer = null;
    }


    /**
     * Checks if folding is enabled.
     * 
     * @return true, if folding is enabled
     */
    public boolean isUseFolding()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING );
    }


    /**
     * Gets the folding threshold.
     * 
     * @return the folding threshold
     */
    public int getFoldingThreshold()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD );
    }


    /**
     * Checks if is auto expand folded attributes.
     * 
     * @return true, if is auto expand folded attributes
     */
    public boolean isAutoExpandFoldedAttributes()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_AUTO_EXPAND_FOLDED_ATTRIBUTES );
    }


    /**
     * Checks if may attributes should be shown.
     * 
     * @return true, if may attributes should be shown
     */
    public boolean isShowMayAttributes()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES );
    }


    /**
     * Checks if must attributes should be shown.
     * 
     * @return true, if must attributes should be shown
     */
    public boolean isShowMustAttributes()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES );
    }


    /**
     * Checks if object class attribute should be shown.
     * 
     * @return true, if object class attribute should be shown
     */
    public boolean isShowObjectClassAttribute()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES );
    }


    /**
     * Checks if operational attributes should be shown.
     * 
     * @return true, if operational attributes should be shown
     */
    public boolean isShowOperationalAttributes()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );
    }


    /**
     * Checks if object class and must attributes should be 
     * grouped before may attributes.
     * 
     * @return true, if object class and must attributes first
     */
    public boolean isObjectClassAndMustAttributesFirst()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST );
    }


    /**
     * Checks if operational attributes should be grouped after may attributes.
     * 
     * @return true, if operational attributes last
     */
    public boolean isOperationalAttributesLast()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST );
    }


    /**
     * Gets the default sort property, one of 
     * {@link BrowserCoreConstants#SORT_BY_ATTRIBUTE_DESCRIPTION} or
     * {@link BrowserCoreConstants#SORT_BY_VALUE}.
     * 
     * @return the default sort property
     */
    public int getDefaultSortBy()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY );
    }


    /**
     * Gets the default sort property, one of 
     * {@link BrowserCoreConstants#SORT_ORDER_NONE},
     * {@link BrowserCoreConstants#SORT_ORDER_ASCENDING} or
     * {@link BrowserCoreConstants#SORT_ORDER_DESCENDING}.
     * 
     * @return the default sort property
     */
    public int getDefaultSortOrder()
    {
        return BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER );
    }

}
