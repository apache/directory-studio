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

package org.apache.directory.ldapstudio.browser.common.widgets.entryeditor;


import org.apache.directory.ldapstudio.valueeditors.ValueEditorManager;
import org.eclipse.jface.viewers.TreeViewer;


/**
 * The BrowserConfiguration contains the content provider, 
 * label provider, sorter, filter the context menu manager and the
 * preferences for the entry editor widget. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetConfiguration
{

    /** The disposed flag */
    private boolean disposed = false;

    /** The sorter. */
    protected EntryEditorWidgetSorter sorter;

    /** The filter. */
    protected EntryEditorWidgetFilter filter;

    /** The preferences. */
    protected EntryEditorWidgetPreferences preferences;

    /** The content provider. */
    protected EntryEditorWidgetContentProvider contentProvider;

    /** The label provider. */
    protected EntryEditorWidgetLabelProvider labelProvider;

    /** The cell modifier. */
    protected EntryEditorWidgetCellModifier cellModifier;

    /** The value editor manager. */
    protected ValueEditorManager valueEditorManager;


    /**
     * Creates a new instance of EntryEditorWidgetConfiguration.
     */
    public EntryEditorWidgetConfiguration()
    {
    }


    /**
     * Disposes this configuration.
     */
    public void dispose()
    {
        if ( !disposed )
        {
            if ( sorter != null )
            {
                sorter.dispose();
                sorter = null;
            }

            if ( filter != null )
            {
                filter.dispose();
                filter = null;
            }

            if ( preferences != null )
            {
                preferences.dispose();
                preferences = null;
            }

            if ( contentProvider != null )
            {
                contentProvider.dispose();
                contentProvider = null;
            }

            if ( labelProvider != null )
            {
                labelProvider.dispose();
                labelProvider = null;
            }

            if ( cellModifier != null )
            {
                cellModifier.dispose();
                cellModifier = null;
            }

            if ( valueEditorManager != null )
            {
                valueEditorManager.dispose();
                valueEditorManager = null;
            }

            disposed = true;
        }
    }


    /**
     * Gets the content provider.
     * 
     * @param mainWidget the main widget
     * 
     * @return the content provider
     */
    public EntryEditorWidgetContentProvider getContentProvider( EntryEditorWidget mainWidget )
    {
        if ( contentProvider == null )
        {
            contentProvider = new EntryEditorWidgetContentProvider( getPreferences(), mainWidget );
        }

        return contentProvider;
    }


    /**
     * Gets the label provider.
     * 
     * @param viewer the viewer
     * 
     * @return the label provider
     */
    public EntryEditorWidgetLabelProvider getLabelProvider( TreeViewer viewer )
    {
        if ( labelProvider == null )
        {
            labelProvider = new EntryEditorWidgetLabelProvider( getValueEditorManager( viewer ) );
        }

        return labelProvider;
    }


    /**
     * Gets the cell modifier.
     * 
     * @param viewer the viewer
     * 
     * @return the cell modifier
     */
    public EntryEditorWidgetCellModifier getCellModifier( TreeViewer viewer )
    {
        if ( cellModifier == null )
        {
            cellModifier = new EntryEditorWidgetCellModifier( getValueEditorManager( viewer ) );
        }

        return cellModifier;
    }


    /**
     * Gets the value editor manager.
     * 
     * @param viewer the viewer
     * 
     * @return the value editor manager
     */
    public ValueEditorManager getValueEditorManager( TreeViewer viewer )
    {
        if ( valueEditorManager == null )
        {
            valueEditorManager = new ValueEditorManager( viewer.getTree() );
        }

        return valueEditorManager;
    }


    /**
     * Gets the sorter.
     * 
     * @return the sorter
     */
    public EntryEditorWidgetSorter getSorter()
    {
        if ( sorter == null )
        {
            sorter = new EntryEditorWidgetSorter( getPreferences() );
        }

        return sorter;
    }


    /**
     * Gets the filter.
     * 
     * @return the filter
     */
    public EntryEditorWidgetFilter getFilter()
    {
        if ( filter == null )
        {
            filter = new EntryEditorWidgetFilter();
        }

        return filter;
    }


    /**
     * Gets the preferences.
     * 
     * @return the preferences
     */
    public EntryEditorWidgetPreferences getPreferences()
    {
        if ( preferences == null )
        {
            preferences = new EntryEditorWidgetPreferences();
        }

        return preferences;
    }

}
