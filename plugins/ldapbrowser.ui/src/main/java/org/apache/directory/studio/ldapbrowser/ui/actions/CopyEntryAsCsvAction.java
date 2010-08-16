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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.AttributeComparator;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action copies entry(ies) as CSV.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CopyEntryAsCsvAction extends CopyEntryAsAction
{
    /**
     * Table Mode.
     */
    public static final int MODE_TABLE = 5;


    /**
     * Creates a new instance of CopyEntryAsCsvAction.
     *
     * @param mode
     *      the copy Mode
     */
    public CopyEntryAsCsvAction( int mode )
    {
        super( Messages.getString( "CopyEntryAsCsvAction.CSV" ), mode ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        if ( this.mode == MODE_DN_ONLY )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_CSV );
        }
        else if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_CSV_SEARCHRESULT );
        }
        else if ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_CSV_OPERATIONAL );
        }
        else if ( this.mode == MODE_NORMAL )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_CSV_USER );
        }
        else if ( this.mode == MODE_TABLE )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_TABLE );
        }
        else
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_CSV );
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( this.mode == MODE_TABLE )
        {
            return Messages.getString( "CopyEntryAsCsvAction.CopyTable" ); //$NON-NLS-1$
        }

        return super.getText();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( this.mode == MODE_TABLE )
        {
            return getInput() != null && getInput() instanceof ISearch
                && ( ( ISearch ) getInput() ).getSearchResults() != null
                && ( ( ISearch ) getInput() ).getSearchResults().length > 0;
        }

        return super.isEnabled();
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {

        if ( this.mode == MODE_TABLE )
        {
            if ( getInput() != null && getInput() instanceof ISearch
                && ( ( ISearch ) getInput() ).getSearchResults() != null
                && ( ( ISearch ) getInput() ).getSearchResults().length > 0 )
            {
                List<IEntry> entryList = new ArrayList<IEntry>();
                ISearchResult[] results = ( ( ISearch ) getInput() ).getSearchResults();
                for ( int k = 0; k < results.length; k++ )
                {
                    entryList.add( results[k].getEntry() );
                }
                IEntry[] entries = ( IEntry[] ) entryList.toArray( new IEntry[entryList.size()] );

                StringBuffer text = new StringBuffer();
                serialializeEntries( entries, text );
                copyToClipboard( text.toString() );
            }
        }
        else
        {
            super.run();
        }

    }


    /**
     * {@inheritDoc}
     */
    public void serialializeEntries( IEntry[] entries, StringBuffer text )
    {

        String attributeDelimiter = BrowserCommonActivator.getDefault().getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_ATTRIBUTEDELIMITER );
        String valueDelimiter = BrowserCommonActivator.getDefault().getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_VALUEDELIMITER );
        String quoteCharacter = BrowserCommonActivator.getDefault().getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_QUOTECHARACTER );
        String lineSeparator = BrowserCommonActivator.getDefault().getPreferenceStore().getString(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_LINESEPARATOR );
        int binaryEncoding = BrowserCommonActivator.getDefault().getPreferenceStore().getInt(
            BrowserCommonConstants.PREFERENCE_FORMAT_TABLE_BINARYENCODING );

        String[] returningAttributes = null;
        if ( this.mode == MODE_DN_ONLY )
        {
            returningAttributes = new String[0];
        }
        else if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY && getSelectedSearchResults().length > 0
            && getSelectedEntries().length + getSelectedBookmarks().length + getSelectedSearches().length == 0 )
        {
            returningAttributes = getSelectedSearchResults()[0].getSearch().getReturningAttributes();
        }
        else if ( ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY || this.mode == MODE_TABLE )
            && getSelectedSearches().length == 1 )
        {
            returningAttributes = getSelectedSearches()[0].getReturningAttributes();
        }
        else if ( ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY || this.mode == MODE_TABLE )
            && ( getInput() instanceof ISearch ) )
        {
            returningAttributes = ( ( ISearch ) ( getInput() ) ).getReturningAttributes();
        }
        else
        {
            Map<String, IAttribute> attributeMap = new HashMap<String, IAttribute>();
            for ( int e = 0; entries != null && e < entries.length; e++ )
            {
                IAttribute[] attributes = entries[e].getAttributes();
                for ( int a = 0; attributes != null && a < attributes.length; a++ )
                {

                    if ( attributes[a].isOperationalAttribute() && this.mode != MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
                        continue;

                    if ( !attributeMap.containsKey( attributes[a].getDescription() ) )
                    {
                        attributeMap.put( attributes[a].getDescription(), attributes[a] );
                    }
                }
            }
            IAttribute[] attributes = ( IAttribute[] ) attributeMap.values().toArray(
                new IAttribute[attributeMap.size()] );

            if ( attributes.length > 0 )
            {
                AttributeComparator comparator = new AttributeComparator( entries[0] );
                Arrays.sort( attributes, comparator );
            }

            returningAttributes = new String[attributes.length];
            for ( int i = 0; i < attributes.length; i++ )
            {
                returningAttributes[i] = attributes[i].getDescription();
            }
        }

        // header
        if ( this.mode != MODE_TABLE
            || BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) )
        {
            text.append( quoteCharacter );
            text.append( "DN" ); //$NON-NLS-1$
            text.append( quoteCharacter );
            text.append( attributeDelimiter );
        }
        for ( int a = 0; returningAttributes != null && a < returningAttributes.length; a++ )
        {
            text.append( quoteCharacter );
            text.append( returningAttributes[a] );
            text.append( quoteCharacter );
            if ( a + 1 < returningAttributes.length )
            {
                text.append( attributeDelimiter );
            }
        }
        text.append( lineSeparator );

        for ( int e = 0; entries != null && e < entries.length; e++ )
        {

            if ( this.mode != MODE_TABLE
                || BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
                    BrowserUIConstants.PREFERENCE_SEARCHRESULTEDITOR_SHOW_DN ) )
            {
                text.append( quoteCharacter );
                text.append( entries[e].getDn().getUpName() );
                text.append( quoteCharacter );
                text.append( attributeDelimiter );

            }
            for ( int a = 0; returningAttributes != null && a < returningAttributes.length; a++ )
            {

                AttributeComparator comparator = new AttributeComparator( entries[e] );
                AttributeHierarchy ah = entries[e].getAttributeWithSubtypes( returningAttributes[a] );
                if ( ah != null )
                {

                    StringBuffer valueSB = new StringBuffer();

                    for ( Iterator it = ah.iterator(); it.hasNext(); )
                    {
                        IAttribute attribute = ( IAttribute ) it.next();
                        if ( attribute != null )
                        {

                            IValue[] values = attribute.getValues();
                            Arrays.sort( values, comparator );

                            for ( int v = 0; v < values.length; v++ )
                            {
                                String val = ModelConverter.getStringValue( values[v], binaryEncoding );
                                valueSB.append( val );
                                if ( v + 1 < values.length )
                                {
                                    valueSB.append( valueDelimiter );
                                }
                            }
                        }

                        if ( it.hasNext() )
                        {
                            valueSB.append( valueDelimiter );
                        }
                    }

                    String value = valueSB.toString().replaceAll( quoteCharacter, quoteCharacter + quoteCharacter );
                    text.append( quoteCharacter );
                    text.append( value );
                    text.append( quoteCharacter );

                }

                // IAttribute attribute =
                // entries[e].getAttribute(returningAttributes[a]);
                // if (attribute != null) {
                //
                // IValue[] values = attribute.getValues();
                // Arrays.sort(values, comparator);
                //
                // StringBuffer valueSB = new StringBuffer();
                // for (int v = 0; v < values.length; v++) {
                // String val = LdifUtils.getStringValue(values[v],
                // binaryEncoding);
                // valueSB.append(val);
                // if (v + 1 < values.length) {
                // valueSB.append(valueDelimiter);
                // ;
                // }
                // }
                //
                // String value = valueSB.toString().replaceAll(quoteCharacter,
                // quoteCharacter + quoteCharacter);
                // text.append(quoteCharacter);
                // text.append(value);
                // text.append(quoteCharacter);
                //
                // }

                if ( a + 1 < returningAttributes.length )
                {
                    text.append( attributeDelimiter );
                }
            }

            if ( e < entries.length )
            {
                text.append( lineSeparator );
            }
        }
    }
}
