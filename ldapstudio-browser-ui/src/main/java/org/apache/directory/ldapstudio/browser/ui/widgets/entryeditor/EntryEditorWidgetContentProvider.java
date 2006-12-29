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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.jobs.InitializeAttributesJob;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class EntryEditorWidgetContentProvider implements ITreeContentProvider
{

    protected EntryEditorWidgetPreferences preferences;

    protected EntryEditorWidget mainWidget;


    public EntryEditorWidgetContentProvider( EntryEditorWidgetPreferences preferences, EntryEditorWidget mainWidget )
    {
        this.preferences = preferences;
        this.mainWidget = mainWidget;
    }


    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {

        String dn = "";
        boolean enabled = true;

        if ( newInput != null && newInput instanceof IEntry )
        {
            IEntry entry = ( IEntry ) newInput;
            dn = "DN: " + entry.getDn().toString();
        }
        else if ( newInput != null && newInput instanceof AttributeHierarchy )
        {
            AttributeHierarchy ah = ( AttributeHierarchy ) newInput;
            dn = "DN: " + ah.getAttribute().getEntry().getDn().toString();
        }
        else
        {
            dn = "No entry selected";
            enabled = false;
        }

        if ( this.mainWidget.getInfoText() != null && !this.mainWidget.getInfoText().isDisposed() )
        {
            this.mainWidget.getInfoText().setText( dn );
        }
        if ( this.mainWidget.getQuickFilterWidget() != null )
        {
            this.mainWidget.getQuickFilterWidget().setEnabled( enabled );
        }
        if ( this.mainWidget.getViewer() != null && !this.mainWidget.getViewer().getTree().isDisposed() )
        {
            this.mainWidget.getViewer().getTree().setEnabled( enabled );
        }
    }


    public void dispose()
    {
    }


    public Object[] getElements( Object inputElement )
    {

        if ( inputElement != null && inputElement instanceof IEntry )
        {
            IEntry entry = ( IEntry ) inputElement;

            if ( !entry.isAttributesInitialized() && entry.isDirectoryEntry() )
            {
                boolean soa = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                    BrowserUIConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );
                InitializeAttributesJob job = new InitializeAttributesJob( new IEntry[]
                    { entry }, soa );
                job.execute();
                return new Object[]
                    {};
            }
            else
            {
                IAttribute[] attributes = entry.getAttributes();
                Object[] values = getValues( attributes );
                return values;
            }
        }
        else if ( inputElement != null && inputElement instanceof AttributeHierarchy )
        {
            AttributeHierarchy ah = ( AttributeHierarchy ) inputElement;
            IAttribute[] attributes = ah.getAttributes();
            Object[] values = getValues( attributes );
            return values;
        }
        else
        {
            return new Object[]
                {};
        }
    }


    private Object[] getValues( IAttribute[] attributes )
    {
        List valueList = new ArrayList();
        for ( int i = 0; attributes != null && i < attributes.length; i++ )
        {
            IValue[] values = attributes[i].getValues();
            if ( this.preferences == null || !this.preferences.isUseFolding()
                || ( values.length <= this.preferences.getFoldingThreshold() ) )
            {
                for ( int j = 0; j < values.length; j++ )
                {
                    valueList.add( values[j] );
                }
            }
            else
            {
                valueList.add( attributes[i] );
            }
        }
        return valueList.toArray();
    }


    public Object[] getChildren( Object parentElement )
    {
        if ( parentElement instanceof IAttribute )
        {
            IAttribute attribute = ( IAttribute ) parentElement;
            IValue[] values = attribute.getValues();
            return values;
        }
        return null;
    }


    public Object getParent( Object element )
    {
        if ( element instanceof IValue )
        {
            return ( ( IValue ) element ).getAttribute();
        }
        return null;
    }


    public boolean hasChildren( Object element )
    {
        return ( element instanceof IAttribute );
    }

}