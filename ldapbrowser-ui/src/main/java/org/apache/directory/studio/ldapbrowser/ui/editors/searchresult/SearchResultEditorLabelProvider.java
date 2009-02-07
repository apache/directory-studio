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

package org.apache.directory.studio.ldapbrowser.ui.editors.searchresult;


import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.valueeditors.IValueEditor;
import org.apache.directory.studio.valueeditors.ValueEditorManager;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;


/**
 * The label provider for the search result editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SearchResultEditorLabelProvider extends LabelProvider implements ITableLabelProvider, ITableFontProvider,
    ITableColorProvider
{

    /** The value editor manager. */
    private ValueEditorManager valueEditorManager;

    /** The search. */
    private ISearch search;

    /** The show DN flag. */
    private boolean showDn;


    /**
     * Creates a new instance of SearchResultEditorLabelProvider.
     * 
     * @param viewer the viewer
     * @param valueEditorManager the value editor manager
     */
    public SearchResultEditorLabelProvider( ValueEditorManager valueEditorManager )
    {
        this.valueEditorManager = valueEditorManager;
    }


    /**
     * Called when the input of the viewer has been changed.
     * 
     * @param newSearch the new search
     * @param showDn the show DN flag
     */
    public void inputChanged( ISearch newSearch, boolean showDn )
    {
        this.search = newSearch;
        this.showDn = showDn;
    }


    /**
     * {@inheritDoc}
     */
    public final String getColumnText( Object obj, int index )
    {
        if ( obj != null && obj instanceof ISearchResult )
        {
            String property;
            try
            {
                ISearchResult result = ( ISearchResult ) obj;

                if ( showDn && index == 0 )
                {
                    property = BrowserUIConstants.DN;
                }
                else if ( showDn && index > 0 )
                {
                    property = search.getReturningAttributes()[index - 1];
                }
                else
                {
                    property = search.getReturningAttributes()[index];
                }

                if ( property == BrowserUIConstants.DN )
                {
                    return result.getDn().getUpName();
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
                return ""; //$NON-NLS-1$
            }

        }
        else if ( obj != null )
        {
            return obj.toString();
        }
        else
        {
            return ""; //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public final Image getColumnImage( Object obj, int index )
    {
        return null;
    }


    /**
     * Gets the display value.
     * 
     * @param ah the ah
     * 
     * @return the display value
     */
    private String getDisplayValue( AttributeHierarchy ah )
    {
        IValueEditor vp = valueEditorManager.getCurrentValueEditor( ah );
        if ( vp == null )
        {
            return ""; //$NON-NLS-1$
        }

        String value = vp.getDisplayValue( ah );
        if ( value.length() > 50 )
        {
            value = value.substring( 0, 47 ) + "..."; //$NON-NLS-1$
        }
        return value;
    }


    /**
     * {@inheritDoc}
     */
    public Font getFont( Object element, int index )
    {
        if ( element instanceof ISearchResult )
        {
            ISearchResult result = ( ISearchResult ) element;
            String property = null;

            if ( showDn && index == 0 )
            {
                property = BrowserUIConstants.DN;
            }
            else if ( showDn && index > 0 && index - 1 < result.getSearch().getReturningAttributes().length )
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
                            FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserCommonActivator
                                .getDefault().getPreferenceStore(), BrowserCommonConstants.PREFERENCE_OBJECTCLASS_FONT );
                            return BrowserCommonActivator.getDefault().getFont( fontData );
                        }
                        else if ( attribute.isMustAttribute() )
                        {
                            FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserCommonActivator
                                .getDefault().getPreferenceStore(),
                                BrowserCommonConstants.PREFERENCE_MUSTATTRIBUTE_FONT );
                            return BrowserCommonActivator.getDefault().getFont( fontData );
                        }
                        else if ( attribute.isOperationalAttribute() )
                        {
                            FontData[] fontData = PreferenceConverter.getFontDataArray( BrowserCommonActivator
                                .getDefault().getPreferenceStore(),
                                BrowserCommonConstants.PREFERENCE_OPERATIONALATTRIBUTE_FONT );
                            return BrowserCommonActivator.getDefault().getFont( fontData );
                        }
                        else
                        {
                            FontData[] fontData = PreferenceConverter
                                .getFontDataArray( BrowserCommonActivator.getDefault().getPreferenceStore(),
                                    BrowserCommonConstants.PREFERENCE_MAYATTRIBUTE_FONT );
                            return BrowserCommonActivator.getDefault().getFont( fontData );
                        }
                    }
                }
            }
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getForeground( Object element, int index )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public Color getBackground( Object element, int index )
    {
        return null;
    }

}