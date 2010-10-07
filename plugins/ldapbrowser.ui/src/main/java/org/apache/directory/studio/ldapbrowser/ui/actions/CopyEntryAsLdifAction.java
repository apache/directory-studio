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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.utils.AttributeComparator;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;


/**
 * This Action copies entry(ies) as LDIF.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CopyEntryAsLdifAction extends CopyEntryAsAction
{

    /**
     * Creates a new instance of CopyEntryAsLdifAction.
     *
     * @param mode
     *      the copy Mode
     */
    public CopyEntryAsLdifAction( int mode )
    {
        super( Messages.getString( "CopyEntryAsLdifAction.LDIF" ), mode ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public ImageDescriptor getImageDescriptor()
    {
        if ( this.mode == MODE_DN_ONLY )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_LDIF );
        }
        else if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_LDIF_SEARCHRESULT );
        }
        else if ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_LDIF_OPERATIONAL );
        }
        else
        {
            return BrowserUIPlugin.getDefault().getImageDescriptor( BrowserUIConstants.IMG_COPY_LDIF_USER );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void serialializeEntries( IEntry[] entries, StringBuffer text )
    {

        String lineSeparator = BrowserCorePlugin.getDefault().getPluginPreferences().getString(
            BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR );

        Set returningAttributesSet = null;
        if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY && getSelectedSearchResults().length > 0
            && getSelectedEntries().length + getSelectedBookmarks().length + getSelectedSearches().length == 0 )
        {
            returningAttributesSet = new HashSet( Arrays.asList( getSelectedSearchResults()[0].getSearch()
                .getReturningAttributes() ) );
        }
        else if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY && getSelectedSearches().length == 1 )
        {
            returningAttributesSet = new HashSet( Arrays.asList( getSelectedSearches()[0].getReturningAttributes() ) );
        }

        for ( int e = 0; entries != null && e < entries.length; e++ )
        {

            serializeDn( entries[e].getDn(), text );

            if ( this.mode != MODE_DN_ONLY )
            {

                List valueList = new ArrayList();
                IAttribute[] attributes = entries[e].getAttributes();
                if ( attributes != null )
                {
                    for ( int i = 0; i < attributes.length; i++ )
                    {

                        if ( returningAttributesSet != null
                            && !returningAttributesSet.contains( attributes[i].getType() ) )
                            continue;

                        if ( attributes[i].isOperationalAttribute() && this.mode != MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
                            continue;

                        IValue[] values = attributes[i].getValues();
                        for ( int k = 0; k < values.length; k++ )
                        {
                            valueList.add( values[k] );
                        }
                    }
                }
                IValue[] values = ( IValue[] ) valueList.toArray( new IValue[valueList.size()] );

                AttributeComparator comparator = new AttributeComparator( entries[e] );
                Arrays.sort( values, comparator );

                for ( int i = 0; i < values.length; i++ )
                {
                    serializeValue( values[i], text );
                }
            }
            if ( e < entries.length )
            {
                text.append( lineSeparator );
            }
        }
    }


    /**
     * Serializes a Value.
     *
     * @param value
     *      the Value to serialize
     * @param text
     *      the StringBuffer to serialize to
     */
    protected void serializeValue( IValue value, StringBuffer text )
    {
        text
            .append( ModelConverter.valueToLdifAttrValLine( value ).toFormattedString( Utils.getLdifFormatParameters() ) );
    }


    /**
     * Serialize a DN.
     *
     * @param dn
     *      the DN to serialize
     * @param text
     *      the StringBuffer to serialize to
     */
    protected void serializeDn( DN dn, StringBuffer text )
    {
        text.append( ModelConverter.dnToLdifDnLine( dn ).toFormattedString( Utils.getLdifFormatParameters() ) );
    }
}
