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


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;


/**
 * This class is a wrapper for the preferences of the entry editor widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetPreferences implements IPropertyChangeListener
{

    /** The viewer. */
    protected Viewer viewer;


    /**
     * Creates a new instance of EntryEditorWidgetPreferences.
     */
    public EntryEditorWidgetPreferences()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
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
        BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
        viewer = null;
    }


    /**
     * Checks if folding is enabled.
     * 
     * @return true, if folding is enabled
     */
    public boolean isUseFolding()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING );
    }


    /**
     * Gets the folding threshold.
     * 
     * @return the folding threshold
     */
    public int getFoldingThreshold()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_FOLDING_THRESHOLD );
    }


    /**
     * Checks if may attributes should be shown.
     * 
     * @return true, if may attributes should be shown
     */
    public boolean isShowMayAttributes()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_MAY_ATTRIBUTES );
    }


    /**
     * Checks if must attributes should be shown.
     * 
     * @return true, if must attributes should be shown
     */
    public boolean isShowMustAttributes()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_MUST_ATTRIBUTES );
    }


    /**
     * Checks if object class attribute should be shown.
     * 
     * @return true, if object class attribute should be shown
     */
    public boolean isShowObjectClassAttribute()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OBJECTCLASS_ATTRIBUTES );
    }


    /**
     * Checks if operational attributes should be shown.
     * 
     * @return true, if operational attributes should be shown
     */
    public boolean isShowOperationalAttributes()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );
    }


    /**
     * Checks if object class and must attributes should be 
     * grouped before may attributes.
     * 
     * @return true, if object class and must attributes first
     */
    public boolean isObjectClassAndMustAttributesFirst()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_OBJECTCLASS_AND_MUST_ATTRIBUTES_FIRST );
    }


    /**
     * Checks if operational attributes should be grouped after may attributes.
     * 
     * @return true, if operational attributes last
     */
    public boolean isOperationalAttributesLast()
    {
        return BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_OPERATIONAL_ATTRIBUTES_LAST );
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
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_BY );
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
        return BrowserUIPlugin.getDefault().getPreferenceStore().getInt(
            BrowserUIConstants.PREFERENCE_ENTRYEDITOR_DEFAULT_SORT_ORDER );
    }


    /**
     * {@inheritDoc}
     */
    public void propertyChange( PropertyChangeEvent event )
    {
        if ( this.viewer != null )
        {
            this.viewer.refresh();
        }
    }

}
