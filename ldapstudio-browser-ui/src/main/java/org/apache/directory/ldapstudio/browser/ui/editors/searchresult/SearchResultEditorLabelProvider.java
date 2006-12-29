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

package org.apache.directory.ldapstudio.browser.ui.editors.searchresult;


import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.ISearch;
import org.apache.directory.ldapstudio.browser.core.model.ISearchResult;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.IValueEditor;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.internal.ValueEditorManager;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;


public class SearchResultEditorLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider,
    ITableColorProvider
{

    private ValueEditorManager valueEditorManager;

    private ISearch search;

    private boolean showDn;


    public SearchResultEditorLabelProvider( TableViewer viewer, ValueEditorManager valueEditorManager )
    {
        this.valueEditorManager = valueEditorManager;
    }


    public void inputChanged( ISearch newSearch, boolean showDn )
    {
        this.search = newSearch;
        this.showDn = showDn;
    }


    public final String getColumnText( Object obj, int index )
    {

        if ( obj != null && obj instanceof ISearchResult )
        {
            String property;
            try
            {
                ISearchResult result = ( ISearchResult ) obj;

                if ( this.showDn && index == 0 )
                {
                    property = BrowserUIConstants.DN;
                }
                else if ( this.showDn && index > 0 )
                {
                    property = this.search.getReturningAttributes()[index - 1];
                }
                else
                {
                    property = this.search.getReturningAttributes()[index];
                }

                if ( property == BrowserUIConstants.DN )
                {
                    return result.getDn().toString();
                }
                else
                {
                    AttributeHierarchy ah = result.getAttributeWithSubtypes( property );
                    return getDisplayValue( ah );
                }

            }
            catch ( ArrayIndexOutOfBoundsException aioobe )
            {
                // occurs on "invisible" columns
                return "";
            }

        }
        else if ( obj != null )
        {
            return obj.toString();
        }
        else
        {
            return "";
        }
    }


    public final Image getColumnImage( Object obj, int index )
    {
        return null;
    }


    private String getDisplayValue( AttributeHierarchy ah )
    {

        IValueEditor vp = this.valueEditorManager.getCurrentValueEditor( ah );
        if ( vp == null )
        {
            return "";
        }

        String value = vp.getDisplayValue( ah );
        if ( value.length() > 50 )
        {
            value = value.substring( 0, 47 ) + "...";
        }
        return value;
    }


    public Font getFont( Object element, int index )
    {

        if ( element instanceof ISearchResult )
        {
            ISearchResult result = ( ISearchResult ) element;
            String property = null;

            if ( this.showDn && index == 0 )
            {
                property = BrowserUIConstants.DN;
            }
            else if ( this.showDn && index > 0 && index - 1 < result.getSearch().getReturningAttributes().length )
            {
                property = result.getSearch().getReturningAttributes()[index - 1];
            }
            else if ( index < result.getSearch().getReturningAttributes().length )
            {
                property = result.getSearch().getReturningAttributes()[index];
            }

            if ( property != null && property == BrowserUIConstants.DN )
            {
                return null;
            }
            else if ( property != null )
            {
                AttributeHierarchy ah = result.getAttributeWithSubtypes( property );
                if ( ah != null )
                {
                    for ( int i = 0; i < ah.getAttributes().length; i++ )
                    {
                        IAttribute attribute = ah.getAttributes()[i];
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
                }
            }
        }

        return null;
    }


    public Color getForeground( Object element, int index )
    {
        return null;
    }


    public Color getBackground( Object element, int index )
    {
        return null;
    }

}