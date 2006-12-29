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


import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;
import org.eclipse.jface.viewers.TreeViewer;


public class EntryEditorWidgetConfiguration
{

    private boolean disposed = false;

    protected EntryEditorWidgetSorter sorter;

    protected EntryEditorWidgetFilter filter;

    protected EntryEditorWidgetPreferences preferences;

    protected EntryEditorWidgetContentProvider contentProvider;

    protected EntryEditorWidgetLabelProvider labelProvider;

    protected EntryEditorWidgetCellModifier cellModifier;

    protected ValueEditorManager valueEditorManager;


    public EntryEditorWidgetConfiguration()
    {
    }


    public void dispose()
    {
        if ( !this.disposed )
        {

            if ( this.sorter != null )
                this.sorter.dispose();
            this.sorter = null;

            if ( this.filter != null )
                this.filter.dispose();
            this.filter = null;

            if ( this.preferences != null )
                this.preferences.dispose();
            this.preferences = null;

            if ( this.contentProvider != null )
                this.contentProvider.dispose();
            this.contentProvider = null;

            if ( this.labelProvider != null )
                this.labelProvider.dispose();
            this.labelProvider = null;

            if ( this.cellModifier != null )
                this.cellModifier.dispose();
            this.cellModifier = null;

            if ( this.valueEditorManager != null )
                this.valueEditorManager.dispose();
            this.valueEditorManager = null;

            this.disposed = true;
        }
    }


    public EntryEditorWidgetContentProvider getContentProvider( EntryEditorWidget mainWidget )
    {
        if ( this.contentProvider == null )
            this.contentProvider = new EntryEditorWidgetContentProvider( this.getPreferences(), mainWidget );

        return contentProvider;
    }


    public EntryEditorWidgetLabelProvider getLabelProvider( TreeViewer viewer )
    {
        if ( this.labelProvider == null )
            this.labelProvider = new EntryEditorWidgetLabelProvider( this.getValueEditorManager( viewer ) );

        return labelProvider;
    }


    public EntryEditorWidgetCellModifier getCellModifier( TreeViewer viewer )
    {
        if ( this.cellModifier == null )
            this.cellModifier = new EntryEditorWidgetCellModifier( this.getValueEditorManager( viewer ) );

        return cellModifier;
    }


    public ValueEditorManager getValueEditorManager( TreeViewer viewer )
    {
        if ( this.valueEditorManager == null )
            this.valueEditorManager = new ValueEditorManager( viewer.getTree() );

        return valueEditorManager;
    }


    public EntryEditorWidgetSorter getSorter()
    {
        if ( this.sorter == null )
            this.sorter = new EntryEditorWidgetSorter( getPreferences() );

        return sorter;
    }


    public EntryEditorWidgetFilter getFilter()
    {
        if ( this.filter == null )
            this.filter = new EntryEditorWidgetFilter();

        return filter;
    }


    public EntryEditorWidgetPreferences getPreferences()
    {
        if ( this.preferences == null )
            this.preferences = new EntryEditorWidgetPreferences();

        return preferences;
    }

}
