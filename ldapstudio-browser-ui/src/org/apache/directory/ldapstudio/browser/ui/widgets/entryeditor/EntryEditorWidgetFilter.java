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

import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


public class EntryEditorWidgetFilter extends ViewerFilter
{

    protected TreeViewer viewer;

    protected String quickFilterAttribute;

    protected String quickFilterValue;


    public EntryEditorWidgetFilter()
    {
        this.quickFilterAttribute = "";
        this.quickFilterValue = "";
    }


    public void connect( TreeViewer viewer )
    {
        this.viewer = viewer;
        this.viewer.addFilter( this );
    }


    public Object[] filter( Viewer viewer, Object parent, Object[] elements )
    {

        int size = elements.length;
        ArrayList out = new ArrayList( size );
        for ( int i = 0; i < size; ++i )
        {
            Object element = elements[i];
            if ( select( viewer, parent, element ) )
                out.add( element );
        }

        return out.toArray();
    }


    public boolean select( Viewer viewer, Object parentElement, Object element )
    {

        if ( element instanceof IAttribute )
        {
            IAttribute attribute = ( IAttribute ) element;

            // check if one of the values goes through the quick filter
            boolean oneGoesThrough = false;
            IValue[] values = attribute.getValues();
            for ( int i = 0; i < values.length; i++ )
            {
                if ( this.goesThroughQuickFilter( values[i] ) )
                {
                    oneGoesThrough = true;
                    break;
                }
            }
            if ( !oneGoesThrough )
            {
                return false;
            }

            return true;
        }
        else if ( element instanceof IValue )
        {
            IValue value = ( IValue ) element;

            // check quick filter
            if ( !this.goesThroughQuickFilter( value ) )
            {
                return false;
            }

            // filter attribute types
            if ( value.getAttribute().isObjectClassAttribute() )
                return this.isShowObjectClassAttribute();
            else if ( value.getAttribute().isMustAttribute() )
                return this.isShowMustAttributes();
            else if ( value.getAttribute().isMayAttribute() )
                return this.isShowMayAttributes();
            else if ( value.getAttribute().isOperationalAttribute() )
                return this.isShowOperationalAttributes();
            else
                return true;
        }
        else
        {
            return true;
        }
    }


    private boolean goesThroughQuickFilter( IValue value )
    {
        // filter attribute description
        if ( this.quickFilterAttribute != null && !"".equals( this.quickFilterAttribute ) )
        {
            if ( value.getAttribute().getDescription().toUpperCase().indexOf( this.quickFilterAttribute.toUpperCase() ) == -1 )
            {
                return false;
            }
        }

        // fitler value
        if ( this.quickFilterValue != null && !"".equals( this.quickFilterValue ) )
        {
            if ( value.isString()
                && value.getStringValue().toUpperCase().indexOf( this.quickFilterValue.toUpperCase() ) == -1 )
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


    public void dispose()
    {
        this.viewer = null;
    }


    public String getQuickFilterAttribute()
    {
        return quickFilterAttribute;
    }


    public void setQuickFilterAttribute( String quickFilterAttribute )
    {
        if ( !this.quickFilterAttribute.equals( quickFilterAttribute ) )
        {
            this.quickFilterAttribute = quickFilterAttribute;
            if ( this.viewer != null )
                this.viewer.refresh();
        }
    }


    public String getQuickFilterValue()
    {
        return quickFilterValue;
    }


    public void setQuickFilterValue( String quickFilterValue )
    {
        if ( !this.quickFilterValue.equals( quickFilterValue ) )
        {
            this.quickFilterValue = quickFilterValue;
            if ( this.viewer != null )
                this.viewer.refresh();
        }
    }


    public boolean isShowMayAttributes()
    {
        return true;
    }


    public boolean isShowMustAttributes()
    {
        return true;
    }


    public boolean isShowObjectClassAttribute()
    {
        return true;
    }


    public boolean isShowOperationalAttributes()
    {
        return true;
    }

}
