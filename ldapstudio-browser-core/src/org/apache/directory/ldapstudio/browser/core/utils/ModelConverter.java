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

package org.apache.directory.ldapstudio.browser.core.utils;


import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.internal.model.DummyEntry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Value;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.NameException;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifPart;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeAddRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifChangeRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifRecord;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifChangeTypeLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifCommentLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifControlLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifDnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifSepLine;


public class ModelConverter
{

    public static DummyEntry ldifContentRecordToEntry( LdifContentRecord ldifContentRecord, IConnection connection )
        throws NameException, ModelModificationException
    {
        return createIntern( ldifContentRecord, connection );
    }


    public static DummyEntry ldifChangeAddRecordToEntry( LdifChangeAddRecord ldifChangeAddRecord, IConnection connection )
        throws NameException, ModelModificationException
    {
        return createIntern( ldifChangeAddRecord, connection );
    }


    private static DummyEntry createIntern( LdifRecord ldifRecord, IConnection connection ) throws NameException,
        ModelModificationException
    {

        LdifPart[] parts = ldifRecord.getParts();

        EventRegistry.suspendEventFireingInCurrentThread();

        DummyEntry entry = new DummyEntry( new DN( ldifRecord.getDnLine().getValueAsString() ), connection );

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
                    entry.addAttribute( attribute, null );
                }
                attribute.addValue( new Value( attribute, value ), null );
            }
            else if ( !( parts[i] instanceof LdifDnLine ) && !( parts[i] instanceof LdifSepLine ) )
            {
                String name = parts[i].toRawString();
                name = name.replaceAll( "\n", "" );
                name = name.replaceAll( "\r", "" );
                IAttribute attribute = new Attribute( entry, name );
                attribute.addValue( new Value( attribute, parts[i] ), null );
                entry.addAttribute( attribute, null );
                // IAttribute attribute = entry.getAttribute("");
                // if(attribute == null) {
                // attribute = new Attribute(entry, "");
                // entry.addAttribute(attribute, null);
                // }
                // attribute.addValue(new Value(attribute, parts[i]), null);
            }
        }

        EventRegistry.resumeEventFireingInCurrentThread();

        return entry;
    }


    public static LdifChangeAddRecord entryToLdifChangeAddRecord( IEntry entry )
    {

        boolean mustCreateChangeTypeLine = true;
        IAttribute[] attributes = entry.getAttributes();
        for ( int i = 0; i < attributes.length; i++ )
        {
            IValue[] values = attributes[i].getValues();
            for ( int ii = 0; ii < values.length; ii++ )
            {
                IValue value = values[ii];
                if ( value.getRawValue() instanceof LdifPart )
                {
                    mustCreateChangeTypeLine = false;
                }
            }
        }

        // LdifChangeAddRecord record =
        // LdifChangeAddRecord.create(entry.getDn().toString());
        LdifChangeAddRecord record = new LdifChangeAddRecord( LdifDnLine.create( entry.getDn().toString() ) );
        if ( mustCreateChangeTypeLine )
        {
            addControls( record, entry );
            record.setChangeType( LdifChangeTypeLine.createAdd() );
        }

        for ( int i = 0; i < attributes.length; i++ )
        {
            String name = attributes[i].getDescription();
            IValue[] values = attributes[i].getValues();
            for ( int ii = 0; ii < values.length; ii++ )
            {
                IValue value = values[ii];
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

        record.finish( LdifSepLine.create() );

        return record;
    }


    public static LdifContentRecord entryToLdifContentRecord( IEntry entry )
    {

        LdifContentRecord record = LdifContentRecord.create( entry.getDn().toString() );

        IAttribute[] attributes = entry.getAttributes();
        for ( int i = 0; i < attributes.length; i++ )
        {
            String name = attributes[i].getDescription();
            IValue[] values = attributes[i].getValues();
            for ( int ii = 0; ii < values.length; ii++ )
            {
                IValue value = values[ii];
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


    public static LdifDnLine dnToLdifDnLine( DN dn )
    {
        LdifDnLine line = LdifDnLine.create( dn.toString() );
        return line;
    }


    public static void addControls( LdifChangeRecord cr, IEntry entry )
    {
        if ( entry.isReferral() )
        {
            cr.addControl( LdifControlLine.create( IConnection.CONTROL_MANAGEDSAIT, null, ( String ) null ) );
        }
    }

}
