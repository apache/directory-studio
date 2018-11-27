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


/**
 * Utilities to convert between models
 */
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.StudioControl;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;


public class ModelConverter
{

    /**
     * Converts the given {@link LdifContentRecord} to an {@link DummyEntry}.
     *
     * @param ldifContentRecord the ldif content record to convert
     * @param connection the connection
     *
     * @return the resulting dummy entry
     *
     * @throws org.apache.directory.api.ldap.model.exception.LdapInvalidDnException
     */
    public static DummyEntry ldifContentRecordToEntry( LdifContentRecord ldifContentRecord,
        IBrowserConnection connection ) throws LdapInvalidDnException
    {
        return createIntern( ldifContentRecord, connection );
    }


    /**
     * Converts the given {@link LdifChangeAddRecord} to an {@link DummyEntry}.
     *
     * @param ldifChangeAddRecord the ldif change add record to convert
     * @param connection the connection
     *
     * @return the resulting dummy entry
     *
     * @throws org.apache.directory.api.ldap.model.exception.LdapInvalidDnException
     */
    public static DummyEntry ldifChangeAddRecordToEntry( LdifChangeAddRecord ldifChangeAddRecord,
        IBrowserConnection connection ) throws LdapInvalidDnException
    {
        return createIntern( ldifChangeAddRecord, connection );
    }


    /**
     * Creates an {@link DummyEntry} from the given {@link LdifRecord}.
     *
     * @param connection the connection
     * @param ldifRecord the ldif record
     *
     * @return the dummy entry
     *
     * @throws LdapInvalidDnException
     */
    private static DummyEntry createIntern( LdifRecord ldifRecord, IBrowserConnection connection )
        throws LdapInvalidDnException
    {
        LdifPart[] parts = ldifRecord.getParts();

        EventRegistry.suspendEventFiringInCurrentThread();

        DummyEntry entry = new DummyEntry( new Dn( ldifRecord.getDnLine().getValueAsString() ), connection );

        for ( int i = 0; i < parts.length; i++ )
        {
            if ( parts[i] instanceof LdifAttrValLine )
            {
                LdifAttrValLine line = ( LdifAttrValLine ) parts[i];
                String attributeName = line.getUnfoldedAttributeDescription();
                Object value = line.getValueAsObject();
                IAttribute attribute = entry.getAttribute( attributeName );
                if ( attribute == null )
                {
                    attribute = new Attribute( entry, attributeName );
                    entry.addAttribute( attribute );
                }
                attribute.addValue( new Value( attribute, value ) );
            }
            else if ( !( parts[i] instanceof LdifDnLine ) && !( parts[i] instanceof LdifSepLine ) )
            {
                String name = parts[i].toRawString();
                name = name.replaceAll( "\n", "" ); //$NON-NLS-1$ //$NON-NLS-2$
                name = name.replaceAll( "\r", "" ); //$NON-NLS-1$ //$NON-NLS-2$
                IAttribute attribute = new Attribute( entry, name );
                attribute.addValue( new Value( attribute, parts[i] ) );
                entry.addAttribute( attribute );
            }
        }

        EventRegistry.resumeEventFiringInCurrentThread();

        return entry;
    }


    public static LdifChangeAddRecord entryToLdifChangeAddRecord( IEntry entry )
    {
        boolean mustCreateChangeTypeLine = true;
        for ( IAttribute attribute : entry.getAttributes() )
        {
            for ( IValue value : attribute.getValues() )
            {
                if ( value.getRawValue() instanceof LdifPart )
                {
                    mustCreateChangeTypeLine = false;
                }
            }
        }

        LdifChangeAddRecord record = new LdifChangeAddRecord( LdifDnLine.create( entry.getDn().getName() ) );
        if ( mustCreateChangeTypeLine )
        {
            addControls( record, entry );
            record.setChangeType( LdifChangeTypeLine.createAdd() );
        }

        for ( IAttribute attribute : entry.getAttributes() )
        {
            String name = attribute.getDescription();
            for ( IValue value : attribute.getValues() )
            {
                if ( !value.isEmpty() )
                {
                    if ( value.getRawValue() instanceof LdifPart )
                    {
                        LdifPart part = ( LdifPart ) value.getRawValue();
                        if ( part instanceof LdifChangeTypeLine )
                        {
                            record.setChangeType( ( LdifChangeTypeLine ) part );
                        }
                        else if ( part instanceof LdifCommentLine )
                        {
                            record.addComment( ( LdifCommentLine ) part );
                        }
                        else if ( part instanceof LdifControlLine )
                        {
                            record.addControl( ( LdifControlLine ) part );
                        }
                    }
                    else if ( value.isString() )
                    {
                        record.addAttrVal( LdifAttrValLine.create( name, value.getStringValue() ) );
                    }
                    else
                    {
                        record.addAttrVal( LdifAttrValLine.create( name, value.getBinaryValue() ) );
                    }
                }
            }
        }

        record.finish( LdifSepLine.create() );

        return record;
    }


    public static LdifContentRecord entryToLdifContentRecord( IEntry entry )
    {
        LdifContentRecord record = LdifContentRecord.create( entry.getDn().getName() );

        if ( entry.getAttributes() != null )
        {
            for ( IAttribute attribute : entry.getAttributes() )
            {
                String name = attribute.getDescription();
                for ( IValue value : attribute.getValues() )
                {
                    if ( !value.isEmpty() )
                    {
                        if ( value.getRawValue() instanceof LdifPart )
                        {
                            LdifPart part = ( LdifPart ) value.getRawValue();
                            if ( part instanceof LdifCommentLine )
                            {
                                record.addComment( ( LdifCommentLine ) part );
                            }
                        }
                        else if ( value.isString() )
                        {
                            record.addAttrVal( LdifAttrValLine.create( name, value.getStringValue() ) );
                        }
                        else
                        {
                            record.addAttrVal( LdifAttrValLine.create( name, value.getBinaryValue() ) );
                        }
                    }
                }
            }
        }

        record.finish( LdifSepLine.create() );

        return record;
    }


    public static LdifAttrValLine valueToLdifAttrValLine( IValue value )
    {

        LdifAttrValLine line;
        if ( value.isString() )
        {
            line = LdifAttrValLine.create( value.getAttribute().getDescription(), value.getStringValue() );
        }
        else
        {
            line = LdifAttrValLine.create( value.getAttribute().getDescription(), value.getBinaryValue() );
        }
        return line;
    }


    public static IValue ldifAttrValLineToValue( LdifAttrValLine line, IEntry entry )
    {
        try
        {
            IAttribute attribute = new Attribute( entry, line.getUnfoldedAttributeDescription() );
            IValue value = new Value( attribute, line.getValueAsObject() );
            return value;
        }
        catch ( Exception e )
        {
            return null;
        }
    }


    public static LdifDnLine dnToLdifDnLine( Dn dn )
    {
        LdifDnLine line = LdifDnLine.create( dn.getName() );
        return line;
    }


    public static void addControls( LdifChangeRecord cr, IEntry entry )
    {
        if ( entry.isReferral() )
        {
            cr.addControl( LdifControlLine.create( StudioControl.MANAGEDSAIT_CONTROL.getOid(),
                StudioControl.MANAGEDSAIT_CONTROL.isCritical(), StudioControl.MANAGEDSAIT_CONTROL.getControlValue() ) );
        }
    }


    /**
     * Gets the string value from the given {@link IValue}. If the given
     * {@link IValue} is binary is is encoded according to the regquested
     * encoding type.
     *
     * @param value the value
     * @param binaryEncoding the binary encoding type
     *
     * @return the string value
     */
    public static String getStringValue( IValue value, int binaryEncoding )
    {
        String s = value.getStringValue();
        if ( value.isBinary() && LdifUtils.mustEncode( s ) )
        {
            byte[] binary = value.getBinaryValue();
            if ( binaryEncoding == BrowserCoreConstants.BINARYENCODING_BASE64 )
            {
                s = LdifUtils.base64encode( binary );
            }
            else if ( binaryEncoding == BrowserCoreConstants.BINARYENCODING_HEX )
            {
                s = LdifUtils.hexEncode( binary );
            }
            else
            {
                s = BrowserCoreConstants.BINARY;
            }
        }
        return s;
    }


    /**
     * Converts the entry to JNDI replace modification items. Each attribute
     * of the entry will become a replace modification item. 
     * 
     * @param entry the entry to convert
     * 
     * @return the modification items
     */
    public static ModificationItem[] entryToReplaceModificationItems( IEntry entry )
    {
        List<ModificationItem> mis = new ArrayList<ModificationItem>();

        for ( IAttribute attribute : entry.getAttributes() )
        {
            BasicAttribute jndiAttribute = new BasicAttribute( attribute.getDescription() );
            boolean isLdifPart = false;
            for ( IValue value : attribute.getValues() )
            {
                if ( value.getRawValue() instanceof LdifPart )
                {
                    isLdifPart = true;
                    break;
                }
                jndiAttribute.add( value.getRawValue() );
            }
            if ( !isLdifPart )
            {
                ModificationItem mi = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, jndiAttribute );
                mis.add( mi );
            }
        }

        return mis.toArray( new ModificationItem[mis.size()] );
    }
}
