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


import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;


public class EntryEditorWidgetLabelProvider extends LabelProvider implements ITableLabelProvider, IFontProvider,
    IColorProvider
{

    private ValueEditorManager valueEditorManager;


    public EntryEditorWidgetLabelProvider( ValueEditorManager valueEditorManager )
    {
        this.valueEditorManager = valueEditorManager;
    }


    public void dispose()
    {
        super.dispose();
        this.valueEditorManager = null;
    }


    public final String getColumnText( Object obj, int index )
    {

        if ( obj != null && obj instanceof IValue )
        {
            IValue value = ( IValue ) obj;
            switch ( index )
            {
                case EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX:
                    return value.getAttribute().getDescription();
                case EntryEditorWidgetTableMetadata.VALUE_COLUMN_INDEX:
                    IValueEditor vp = this.valueEditorManager.getCurrentValueEditor( value );
                    String dv = vp.getDisplayValue( value );
                    return dv;
                default:
                    return "";
            }
        }
        else if ( obj != null && obj instanceof IAttribute )
        {
            IAttribute attribute = ( IAttribute ) obj;
            if ( index == EntryEditorWidgetTableMetadata.KEY_COLUMN_INDEX )
            {
                return attribute.getDescription() + " (" + attribute.getValueSize() + " values)";
            }
            else
            {
                return "";
            }
        }
        else
        {
            return "";
        }
    }


    public final Image getColumnImage( Object element, int index )
    {
        return null;
    }


    public Font getFont( Object element )
    {

        IAttribute attribute = null;
        IValue value = null;
        if ( element instanceof IAttribute )
        {
            attribute = ( IAttribute ) element;
        }
        else if ( element instanceof IValue )
        {
            value = ( IValue ) element;
            attribute = value.getAttribute();
        }

        // inconsistent attributes and values
        if ( value != null )
        {
            if ( value.isEmpty() )
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_ERROR_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
        }
        if ( attribute != null && value == null )
        {
            if ( !attribute.isConsistent() )
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_ERROR_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
        }

        // attribute type
        if ( attribute != null )
        {
            if ( attribute.isObjectClassAttribute() )
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_OBJECTCLASS_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
            else if ( attribute.isMustAttribute() )
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_MUSTATTRIBUTE_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
            else if ( attribute.isOperationalAttribute() )
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_OPERATIONALATTRIBUTE_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
            else
            {
                FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserUIPlugin.getDefault()
                    .getPreferenceStore(), BrowserUIConstants.PREFERENCE_MAYATTRIBUTE_FONT );
                return BrowserUIPlugin.getDefault().getFont( fontData );
            }
        }
        else
        {
            return null;
        }
    }


    public Color getForeground( Object element )
    {

        IAttribute attribute = null;
        IValue value = null;
        if ( element instanceof IAttribute )
        {
            attribute = ( IAttribute ) element;
        }
        else if ( element instanceof IValue )
        {
            value = ( IValue ) element;
            attribute = value.getAttribute();
        }

        // inconsistent attributes and values
        if ( value != null )
        {
            if ( value.isEmpty() )
            {
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_ERROR_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
        }
        if ( attribute != null && value == null )
        {
            if ( !attribute.isConsistent() )
            {
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_ERROR_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
        }

        // attribute type
        if ( attribute != null )
        {
            if ( attribute.isObjectClassAttribute() )
            {
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_OBJECTCLASS_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
            else if ( attribute.isMustAttribute() )
            {
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_MUSTATTRIBUTE_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
            else if ( attribute.isOperationalAttribute() )
            {
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_OPERATIONALATTRIBUTE_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
            else
            {
                RGB rgb = PreferenceConverter.getColor( BrowserUIPlugin.getDefault().getPreferenceStore(),
                    BrowserUIConstants.PREFERENCE_MAYATTRIBUTE_COLOR );
                return BrowserUIPlugin.getDefault().getColor( rgb );
            }
        }
        else
        {
            return null;
        }
    }


    public Color getBackground( Object element )
    {
        return null;
    }

}
