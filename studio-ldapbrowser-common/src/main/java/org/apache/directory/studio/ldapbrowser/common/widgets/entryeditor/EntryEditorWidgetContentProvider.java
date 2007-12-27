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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesJob;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * The EntryEditorWidgetContentProvider implements the content provider for
 * the entry editor widget. It accepts an {@link IEntry} or an 
 * {@link AttributeHierarchy} as input.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryEditorWidgetContentProvider implements ITreeContentProvider
{

    /** The preferences. */
    protected EntryEditorWidgetPreferences preferences;

    /** The main widget. */
    protected EntryEditorWidget mainWidget;


    /**
     * Creates a new instance of EntryEditorWidgetContentProvider.
     * 
     * @param preferences the preferences
     * @param mainWidget the main widget
     */
    public EntryEditorWidgetContentProvider( EntryEditorWidgetPreferences preferences, EntryEditorWidget mainWidget )
    {
        this.preferences = preferences;
        this.mainWidget = mainWidget;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementations updates the enabled state and the info text.
     */
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        if ( mainWidget != null )
        {
            String dn = "";
            boolean enabled = true;

            if ( newInput != null && newInput instanceof IEntry )
            {
                IEntry entry = ( IEntry ) newInput;
                dn = "DN: " + entry.getDn().getUpName();
            }
            else if ( newInput != null && newInput instanceof AttributeHierarchy )
            {
                AttributeHierarchy ah = ( AttributeHierarchy ) newInput;
                dn = "DN: " + ah.getAttribute().getEntry().getDn().getUpName();
            }
            else
            {
                dn = "No entry selected";
                enabled = false;
            }

            if ( mainWidget.getInfoText() != null && !mainWidget.getInfoText().isDisposed() )
            {
                mainWidget.getInfoText().setText( dn );
            }
            if ( mainWidget.getQuickFilterWidget() != null )
            {
                mainWidget.getQuickFilterWidget().setEnabled( enabled );
            }
            if ( mainWidget.getViewer() != null && !mainWidget.getViewer().getTree().isDisposed() )
            {
                mainWidget.getViewer().getTree().setEnabled( enabled );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        preferences = null;
        mainWidget = null;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getElements( Object inputElement )
    {

        if ( inputElement != null && inputElement instanceof IEntry )
        {
            IEntry entry = ( IEntry ) inputElement;

            if ( !entry.isAttributesInitialized() && entry.isDirectoryEntry() )
            {
                boolean soa = BrowserCommonActivator.getDefault().getPreferenceStore().getBoolean(
                    BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_SHOW_OPERATIONAL_ATTRIBUTES );
                InitializeAttributesJob job = new InitializeAttributesJob( new IEntry[]
                    { entry }, soa );
                job.execute();
                return new Object[0];
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
            return new Object[0];
        }
    }


    /**
     * Gets the values of the given attributes.
     * 
     * @param attributes the attributes
     * 
     * @return the values
     */
    private Object[] getValues( IAttribute[] attributes )
    {
        List<Object> valueList = new ArrayList<Object>();
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
                // if folding threshold is exceeded then return the attribute itself
                valueList.add( attributes[i] );
            }
        }
        return valueList.toArray();
    }


    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public Object getParent( Object element )
    {
        if ( element instanceof IValue )
        {
            return ( ( IValue ) element ).getAttribute();
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasChildren( Object element )
    {
        return ( element instanceof IAttribute );
    }

}