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

package org.apache.directory.studio.ldapbrowser.core.utils;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.shared.ldap.filter.LdapURL;
import org.apache.directory.shared.ldap.name.AVA;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyMode;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection.ModifyOrder;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;
import org.eclipse.core.runtime.Preferences;


public class Utils
{

    /**
     * Transforms the given DN into a normalized String, usable by the schema cache.
     * The following transformations are performed:
     * <ul>
     *   <li>The attribute type is replaced by the OID
     *   <li>The attribute value is trimmed and lowercased
     * </ul> 
     * Example: the surname=Bar will be transformed to
     * 2.5.4.4=bar
     * 
     * 
     * @param dn the DN
     * @param schema the schema
     * 
     * @return the oid string
     */
    public static String getNormalizedOidString( DN dn, Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        Iterator<RDN> it = dn.getRdns().iterator();
        while ( it.hasNext() )
        {
            RDN rdn = it.next();
            sb.append( getOidString( rdn, schema ) );
            if ( it.hasNext() )
            {
                sb.append( ',' );
            }
        }

        return sb.toString();
    }


    private static String getOidString( RDN rdn, Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        Iterator<AVA> it = rdn.iterator();
        while ( it.hasNext() )
        {
            AVA ava = it.next();
            sb.append( getOidString( ava, schema ) );
            if ( it.hasNext() )
            {
                sb.append( '+' );
            }
        }

        return sb.toString();
    }


    private static String getOidString( AVA ava, Schema schema )
    {
        String oid = schema != null ? schema.getAttributeTypeDescription( ava.getNormType() ).getOid() : ava
            .getNormType();
        return oid.trim().toLowerCase() + "=" + ava.getUpValue().getString().trim().toLowerCase(); //$NON-NLS-1$
    }


    public static String arrayToString( String[] array )
    {
        if ( array == null || array.length == 0 )
        {
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer( array[0] );
            for ( int i = 1; i < array.length; i++ )
            {
                sb.append( ", " );
                sb.append( array[i] );
            }
            return sb.toString();
        }
    }


    public static boolean equals( byte[] data1, byte[] data2 )
    {
        if ( data1 == data2 )
            return true;
        if ( data1 == null || data2 == null )
            return false;
        if ( data1.length != data2.length )
            return false;
        for ( int i = 0; i < data1.length; i++ )
        {
            if ( data1[i] != data2[i] )
                return false;
        }
        return true;
    }


    public static String getShortenedString( String value, int length )
    {

        if ( value == null )
            return "";

        if ( value.length() > length )
        {
            value = value.substring( 0, length ) + "...";
        }

        return value;
    }


    public static String serialize( Object o )
    {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( Utils.class.getClassLoader() );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder( baos );
            encoder.writeObject( o );
            encoder.close();
            String s = LdifUtils.utf8decode( baos.toByteArray() );
            return s;
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( ccl );
        }
    }


    public static Object deserialize( String s )
    {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader( Utils.class.getClassLoader() );
            ByteArrayInputStream bais = new ByteArrayInputStream( LdifUtils.utf8encode( s ) );
            XMLDecoder decoder = new XMLDecoder( bais );
            Object o = decoder.readObject();
            decoder.close();
            return o;
        }
        finally
        {
            Thread.currentThread().setContextClassLoader( ccl );
        }
    }


    public static String getNonNullString( Object o )
    {
        return o == null ? "-" : o.toString();
    }


    public static String formatBytes( long bytes )
    {
        String size = "";
        if ( bytes > 1024 * 1024 )
            size += ( bytes / 1024 / 1024 ) + " MB (" + bytes + " Bytes)";
        else if ( bytes > 1024 )
            size += ( bytes / 1024 ) + " KB (" + bytes + " Bytes)";
        else if ( bytes > 1 )
            size += bytes + " Bytes";
        else
            size += bytes + " Byte";
        return size;
    }


    public static boolean containsIgnoreCase( Collection<String> c, String s )
    {
        if ( c == null || s == null )
        {
            return false;
        }

        for ( String string : c )
        {
            if ( string.equalsIgnoreCase( s ) )
            {
                return true;
            }
        }

        return false;
    }


    public static LdifFormatParameters getLdifFormatParameters()
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        boolean spaceAfterColon = store.getBoolean( BrowserCoreConstants.PREFERENCE_LDIF_SPACE_AFTER_COLON );
        int lineWidth = store.getInt( BrowserCoreConstants.PREFERENCE_LDIF_LINE_WIDTH );
        String lineSeparator = store.getString( BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR );
        LdifFormatParameters ldifFormatParameters = new LdifFormatParameters( spaceAfterColon, lineWidth, lineSeparator );
        return ldifFormatParameters;
    }


    /**
     * Transforms an IBrowserConnection to an LdapURL. The following parameters are
     * used to create the LDAP URL:
     * <ul>
     * <li>scheme
     * <li>host
     * <li>port
     * </ul>
     *
     * @param entry the entry
     * @return the LDAP URL
     */
    public static LdapURL getLdapURL( IBrowserConnection browserConnection )
    {
        LdapURL url = new LdapURL();
        if ( browserConnection.getConnection() != null )
        {
            if ( browserConnection.getConnection().getEncryptionMethod() == EncryptionMethod.LDAPS )
            {
                url.setScheme( LdapURL.LDAPS_SCHEME );
            }
            else
            {
                url.setScheme( LdapURL.LDAP_SCHEME );
            }
            url.setHost( browserConnection.getConnection().getHost() );
            url.setPort( browserConnection.getConnection().getPort() );
        }
        return url;
    }


    /**
     * Transforms an IEntry to an LdapURL. The following parameters are
     * used to create the LDAP URL:
     * <ul>
     * <li>scheme
     * <li>host
     * <li>port
     * <li>dn
     * </ul>
     *
     * @param entry the entry
     * @return the LDAP URL
     */
    public static LdapURL getLdapURL( IEntry entry )
    {
        LdapURL url = getLdapURL( entry.getBrowserConnection() );
        url.setDn( entry.getDn() );
        return url;
    }


    /**
     * Transforms an ISearch to an LdapURL. The following search parameters are
     * used to create the LDAP URL:
     * <ul>
     * <li>scheme
     * <li>host
     * <li>port
     * <li>search base
     * <li>returning attributes
     * <li>scope
     * <li>filter
     * </ul>
     *
     * @param search the search
     * @return the LDAP URL
     */
    public static LdapURL getLdapURL( ISearch search )
    {
        LdapURL url = getLdapURL( search.getBrowserConnection() );
        url.setDn( search.getSearchBase() );
        if ( search.getReturningAttributes() != null )
        {
            url.setAttributes( Arrays.asList( search.getReturningAttributes() ) );
        }
        url.setScope( search.getScope().getOrdinal() );
        url.setFilter( search.getFilter() );
        return url;
    }


    /**
     * Computes the difference between the old and the new entry
     * and returns an LDIF that could be applied to the old entry
     * to get new entry.
     *
     * @param oldEntry the old entry
     * @param newEntry the new entry
     * @return the change modify record or null if there is no difference
     *         between the two entries
     */
    public static LdifFile computeDiff( IEntry oldEntry, IEntry newEntry )
    {
        // get connection parameters
        ModifyMode modifyMode = oldEntry.getBrowserConnection().getModifyMode();
        ModifyMode modifyModeNoEMR = oldEntry.getBrowserConnection().getModifyModeNoEMR();
        ModifyOrder modifyAddDeleteOrder = oldEntry.getBrowserConnection().getModifyAddDeleteOrder();

        // get all attribute descriptions
        Set<String> attributeDescriptions = new HashSet<String>();
        for ( IAttribute oldAttr : oldEntry.getAttributes() )
        {
            attributeDescriptions.add( oldAttr.getDescription() );
        }
        for ( IAttribute newAttr : newEntry.getAttributes() )
        {
            attributeDescriptions.add( newAttr.getDescription() );
        }

        // prepare the LDIF record containing the modifications
        LdifChangeModifyRecord record = new LdifChangeModifyRecord( LdifDnLine.create( newEntry.getDn().getName() ) );
        if ( newEntry.isReferral() )
        {
            record.addControl( LdifControlLine.create( StudioControl.MANAGEDSAIT_CONTROL.getOid(),
                StudioControl.MANAGEDSAIT_CONTROL.isCritical(), StudioControl.MANAGEDSAIT_CONTROL.getControlValue() ) );
        }
        record.setChangeType( LdifChangeTypeLine.createModify() );

        // check all the attributes
        for ( String attributeDescription : attributeDescriptions )
        {
            // get attribute type schema information
            Schema schema = oldEntry.getBrowserConnection().getSchema();
            AttributeType atd = schema.getAttributeTypeDescription( attributeDescription );
            boolean hasEMR = SchemaUtils.getEqualityMatchingRuleNameOrNumericOidTransitive( atd, schema ) != null;
            boolean isReplaceForced = ( hasEMR && modifyMode == ModifyMode.REPLACE )
                || ( !hasEMR && modifyModeNoEMR == ModifyMode.REPLACE );
            boolean isAddDelForced = ( hasEMR && modifyMode == ModifyMode.ADD_DELETE )
                || ( !hasEMR && modifyModeNoEMR == ModifyMode.ADD_DELETE );
            boolean isOrderedValue = atd.getExtensions().containsKey( "X-ORDERED" )
                && atd.getExtensions().get( "X-ORDERED" ).contains( "VALUES" );

            // get old an new values for comparison
            IAttribute oldAttribute = oldEntry.getAttribute( attributeDescription );
            Set<String> oldValues = new HashSet<String>();
            Map<String, LdifAttrValLine> oldAttrValLines = new LinkedHashMap<String, LdifAttrValLine>();
            if ( oldAttribute != null )
            {
                for ( IValue value : oldAttribute.getValues() )
                {
                    LdifAttrValLine attrValLine = computeDiffCreateAttrValLine( value );
                    oldValues.add( attrValLine.getUnfoldedValue() );
                    oldAttrValLines.put( attrValLine.getUnfoldedValue(), attrValLine );
                }
            }
            IAttribute newAttribute = newEntry.getAttribute( attributeDescription );
            Set<String> newValues = new HashSet<String>();
            Map<String, LdifAttrValLine> newAttrValLines = new LinkedHashMap<String, LdifAttrValLine>();
            if ( newAttribute != null )
            {
                for ( IValue value : newAttribute.getValues() )
                {
                    LdifAttrValLine attrValLine = computeDiffCreateAttrValLine( value );
                    newValues.add( attrValLine.getUnfoldedValue() );
                    newAttrValLines.put( attrValLine.getUnfoldedValue(), attrValLine );
                }
            }

            // check what to do
            if ( oldAttribute != null && newAttribute == null )
            {
                // attribute only exists in the old entry: delete all values
                LdifModSpec modSpec;
                if ( isReplaceForced )
                {
                    // replace (empty value list)
                    modSpec = LdifModSpec.createReplace( attributeDescription );
                }
                else
                // addDelForced or default
                {
                    // delete all
                    modSpec = LdifModSpec.createDelete( attributeDescription );
                }
                modSpec.finish( LdifModSpecSepLine.create() );
                record.addModSpec( modSpec );
            }
            else if ( oldAttribute == null && newAttribute != null )
            {
                // attribute only exists in the new entry: add all values
                LdifModSpec modSpec;
                if ( isReplaceForced )
                {
                    // replace (all values)
                    modSpec = LdifModSpec.createReplace( attributeDescription );
                }
                else
                // addDelForced or default 
                {
                    // add (all new values)
                    modSpec = LdifModSpec.createAdd( attributeDescription );
                }
                for ( IValue value : newAttribute.getValues() )
                {
                    modSpec.addAttrVal( computeDiffCreateAttrValLine( value ) );
                }
                modSpec.finish( LdifModSpecSepLine.create() );
                record.addModSpec( modSpec );
            }
            else if ( oldAttribute != null && newAttribute != null && !oldValues.equals( newValues ) )
            {
                // attribute exists in both entries, check modifications
                if ( isReplaceForced )
                {
                    // replace (all new values)
                    LdifModSpec modSpec = LdifModSpec.createReplace( attributeDescription );
                    for ( IValue value : newAttribute.getValues() )
                    {
                        modSpec.addAttrVal( computeDiffCreateAttrValLine( value ) );
                    }
                    modSpec.finish( LdifModSpecSepLine.create() );
                    record.addModSpec( modSpec );
                }
                else
                {
                    // compute diff
                    List<LdifAttrValLine> toDel = new ArrayList<LdifAttrValLine>();
                    List<LdifAttrValLine> toAdd = new ArrayList<LdifAttrValLine>();

                    for ( Map.Entry<String, LdifAttrValLine> entry : oldAttrValLines.entrySet() )
                    {
                        if ( !newValues.contains( entry.getKey() ) )
                        {
                            toDel.add( entry.getValue() );
                        }
                    }
                    for ( Map.Entry<String, LdifAttrValLine> entry : newAttrValLines.entrySet() )
                    {
                        if ( !oldValues.contains( entry.getKey() ) )
                        {
                            toAdd.add( entry.getValue() );
                        }
                    }

                    /*
                     *  we use add/del in the following cases:
                     *  - add/del is forced in the connection configuration
                     *  - only values to add
                     *  - only values to delete
                     *  - the sum of adds and deletes is smaller or equal than the number of replaces
                     *  
                     *  we use replace in the following cases:
                     *  - the number of replaces is smaller to the sum of adds and deletes
                     *  - for attributes with X-ORDERED 'VALUES'
                     */
                    if ( isAddDelForced || ( toAdd.size() + toDel.size() <= newAttrValLines.size() && !isOrderedValue )
                        || ( !toDel.isEmpty() && toAdd.isEmpty() ) || ( !toAdd.isEmpty() && toDel.isEmpty() ) )
                    {
                        // add/del del/add
                        LdifModSpec addModSpec = LdifModSpec.createAdd( attributeDescription );
                        for ( LdifAttrValLine attrValLine : toAdd )
                        {
                            addModSpec.addAttrVal( attrValLine );
                        }
                        addModSpec.finish( LdifModSpecSepLine.create() );
                        LdifModSpec delModSpec = LdifModSpec.createDelete( attributeDescription );
                        for ( LdifAttrValLine attrValLine : toDel )
                        {
                            delModSpec.addAttrVal( attrValLine );
                        }
                        delModSpec.finish( LdifModSpecSepLine.create() );

                        if ( modifyAddDeleteOrder == ModifyOrder.DELETE_FIRST )
                        {
                            if ( delModSpec.getAttrVals().length > 0 )
                            {
                                record.addModSpec( delModSpec );
                            }
                            if ( addModSpec.getAttrVals().length > 0 )
                            {
                                record.addModSpec( addModSpec );
                            }
                        }
                        else
                        {
                            if ( addModSpec.getAttrVals().length > 0 )
                            {
                                record.addModSpec( addModSpec );
                            }
                            if ( delModSpec.getAttrVals().length > 0 )
                            {
                                record.addModSpec( delModSpec );
                            }
                        }
                    }
                    else
                    {
                        // replace (all new values)
                        LdifModSpec modSpec = LdifModSpec.createReplace( attributeDescription );
                        for ( LdifAttrValLine attrValLine : newAttrValLines.values() )
                        {
                            modSpec.addAttrVal( attrValLine );
                        }
                        modSpec.finish( LdifModSpecSepLine.create() );
                        record.addModSpec( modSpec );
                    }
                }
            }

        }

        record.finish( LdifSepLine.create() );

        LdifFile model = new LdifFile();
        if ( record.isValid() && record.getModSpecs().length > 0 )
        {
            model.addContainer( record );
        }
        return model.getRecords().length > 0 ? model : null;
    }


    private static LdifAttrValLine computeDiffCreateAttrValLine( IValue value )
    {
        IAttribute attribute = value.getAttribute();
        if ( attribute.isBinary() )
        {
            return LdifAttrValLine.create( attribute.getDescription(), value.getBinaryValue() );
        }
        else
        {
            return LdifAttrValLine.create( attribute.getDescription(), value.getStringValue() );
        }
    }
}
