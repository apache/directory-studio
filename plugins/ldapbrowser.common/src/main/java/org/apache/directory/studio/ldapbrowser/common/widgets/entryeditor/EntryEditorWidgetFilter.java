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


import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * The EntryEditorWidgetFilter implements the filter for
 * the entry editor widget.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorWidgetFilter extends ViewerFilter
{
    /** The viewer to filter. */
    protected TreeViewer viewer;

    /** The quick filter attribute. */
    protected String quickFilterAttribute;

    /** The quick filter value. */
    protected String quickFilterValue;


    /**
     * Creates a new instance of EntryEditorWidgetFilter.
     */
    public EntryEditorWidgetFilter()
    {
        quickFilterAttribute = ""; //$NON-NLS-1$
        quickFilterValue = ""; //$NON-NLS-1$
    }


    /**
     * Connects this filter with the given viewer.
     * 
     * @param viewer the viewer
     */
    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
        viewer.addFilter( this );
    }


    /**
     * {@inheritDoc}
     */
    public boolean select( Viewer viewer, Object parentElement, Object element )
    {
        if ( element instanceof IAttribute )
        {
            // check if one of the values goes through the quick filter
            IValue[] values = ( ( IAttribute ) element).getValues();
            
            for ( IValue value : values )
            {
                if ( goesThroughQuickFilter( value ) )
                {
                    return true;
                }
            }
            
            return false;
        }
        else if ( element instanceof IValue )
        {
            // check quick filter
            return goesThroughQuickFilter( ( IValue ) element );
        }

        return true;
    }


    /**
     * Checks if the given value goes through quick filter.
     * 
     * @param value the value
     * 
     * @return true, if goes through quick filter
     */
    private boolean goesThroughQuickFilter( IValue value )
    {
        // filter attribute description
        if ( !Strings.isEmpty( quickFilterAttribute ) )
        {
            if ( value.getAttribute().getDescription().toUpperCase().indexOf( quickFilterAttribute.toUpperCase() ) == -1 )
            {
                return false;
            }
        }

        // filter value
        if ( !Strings.isEmpty( quickFilterValue ) )
        {
            if ( value.isString()
                && value.getStringValue().toUpperCase().indexOf( quickFilterValue.toUpperCase() ) == -1 )
            {
                return false;
            }
            else if ( value.isBinary() )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * Disposes this filter.
     */
    public void dispose()
    {
        viewer = null;
    }


    /**
     * Gets the quick filter attribute.
     * 
     * @return the quick filter attribute
     */
    public String getQuickFilterAttribute()
    {
        return quickFilterAttribute;
    }


    /**
     * Sets the quick filter attribute.
     * 
     * @param quickFilterAttribute the quick filter attribute
     */
    public void setQuickFilterAttribute( String quickFilterAttribute )
    {
        if ( !this.quickFilterAttribute.equals( quickFilterAttribute ) )
        {
            String oldValue = this.quickFilterAttribute;
            this.quickFilterAttribute = quickFilterAttribute;
            BrowserCommonActivator.getDefault().getPreferenceStore()
                .firePropertyChangeEvent( "QuickFilterAttributeChanged", oldValue, quickFilterAttribute ); //$NON-NLS-1$
        }
    }


    /**
     * Gets the quick filter value.
     * 
     * @return the quick filter value
     */
    public String getQuickFilterValue()
    {
        return quickFilterValue;
    }


    /**
     * Sets the quick filter value.
     * 
     * @param quickFilterValue the quick filter value
     */
    public void setQuickFilterValue( String quickFilterValue )
    {
        if ( !this.quickFilterValue.equals( quickFilterValue ) )
        {
            String oldValue = this.quickFilterValue;
            this.quickFilterValue = quickFilterValue;
            BrowserCommonActivator.getDefault().getPreferenceStore()
                .firePropertyChangeEvent( "QuickFilterValueChanged", oldValue, quickFilterAttribute ); //$NON-NLS-1$
        }
    }
}
