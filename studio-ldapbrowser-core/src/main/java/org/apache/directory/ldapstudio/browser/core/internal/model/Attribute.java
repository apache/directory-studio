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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.EmptyValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.ValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ValueDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.ValueModifiedEvent;
import org.apache.directory.ldapstudio.browser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.schema.AttributeTypeDescription;
import org.apache.directory.ldapstudio.browser.core.model.schema.SchemaUtils;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * Default implementation of IAttribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Attribute implements IAttribute
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = -5679384884002589786L;

    /** The attribute description */
    private AttributeDescription attributeDescription;

    /** The entry this attribute belongs to */
    private IEntry entry;

    /** The values */
    private List<IValue> valueList;


    /**
     * Creates an new instance of Attribute with the given description
     * and no value.
     * 
     * @param entry
     *                The entry of this attribute, mustn't be null
     * @param description
     *                The attribute descrption, mustn't be null.
     * @throws ModelModificationException
     *                 if the attribute name is null or empty.
     */
    public Attribute( IEntry entry, String description ) throws ModelModificationException
    {
        if ( entry == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_entry );
        }
        if ( description == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_attribute );
        }

        this.entry = entry;
        this.attributeDescription = new AttributeDescription( description );
        this.valueList = new ArrayList<IValue>();

    }


    /**
     * {@inheritDoc}
     */
    public IEntry getEntry()
    {
        return entry;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isConsistent()
    {
        if ( valueList.isEmpty() )
        {
            return false;
        }

        for ( Iterator it = valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            if ( value.isEmpty() )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isMustAttribute()
    {
        if ( isObjectClassAttribute() )
        {
            return true;
        }
        else
        {
            String[] mustAttributeNames = getEntry().getSubschema().getMustAttributeNames();
            for ( int i = 0; i < mustAttributeNames.length; i++ )
            {
                String must = mustAttributeNames[i];
                if ( must.equalsIgnoreCase( getType() ) )
                {
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean isMayAttribute()
    {
        return !isObjectClassAttribute() && !isMustAttribute() && !isOperationalAttribute();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isOperationalAttribute()
    {
        return getAttributeTypeDescription() == null || SchemaUtils.isOperational( getAttributeTypeDescription() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isObjectClassAttribute()
    {
        return OBJECTCLASS_ATTRIBUTE.equalsIgnoreCase( getDescription() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isString()
    {
        return !isBinary();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isBinary()
    {
        return getAttributeTypeDescription().isBinary();
    }


    /**
     * {@inheritDoc}
     */
    public void addEmptyValue()
    {
        try
        {
            IValue emptyValue = new Value( this );
            valueList.add( emptyValue );
            this.attributeModified( new EmptyValueAddedEvent( getEntry().getConnection(), getEntry(), this, emptyValue ) );
        }
        catch ( ModelModificationException mme )
        {
            // Shouldn't occur
        }
    }


    /**
     * {@inheritDoc}
     */
    public void deleteEmptyValue()
    {
        for ( Iterator it = this.valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            if ( value.isEmpty() )
            {
                it.remove();
                attributeModified( new EmptyValueDeletedEvent( getEntry().getConnection(), getEntry(), this, value ) );
                return;
            }
        }
    }


    /**
     * Fires an EntryModificationEvent.
     *
     * @param event the EntryModificationEvent
     */
    private void attributeModified( EntryModificationEvent event )
    {
        EventRegistry.fireEntryUpdated( event, getEntry() );
    }


    /**
     * Checks if the given value is valid.
     *
     * @param value the value to check
     * @throws ModelModificationException if the value is not valid
     */
    private void checkValue( IValue value ) throws ModelModificationException
    {
        if ( value == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_value );
        }
        if ( !value.getAttribute().equals( this ) )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__values_attribute_is_not_myself );
        }
    }


    /**
     * Deletes the given value from value list.
     *
     * @param valueToDelete the value to delete
     * @return true if deleted
     */
    private boolean internalDeleteValue( IValue valueToDelete )
    {
        for ( Iterator it = valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            if ( value.equals( valueToDelete ) )
            {
                it.remove();
                return true;
            }
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void addValue( IValue valueToAdd ) throws ModelModificationException
    {
        this.checkValue( valueToAdd );

        valueList.add( valueToAdd );
        attributeModified( new ValueAddedEvent( getEntry().getConnection(), getEntry(), this, valueToAdd ) );
    }


    /**
     * {@inheritDoc}
     */
    public void deleteValue( IValue valueToDelete ) throws ModelModificationException
    {
        this.checkValue( valueToDelete );

        if ( this.internalDeleteValue( valueToDelete ) )
        {
            this.attributeModified( new ValueDeletedEvent( getEntry().getConnection(), getEntry(), this, valueToDelete ) );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void modifyValue( IValue oldValue, IValue newValue ) throws ModelModificationException
    {
        this.checkValue( oldValue );
        this.checkValue( newValue );

        this.internalDeleteValue( oldValue );
        this.valueList.add( newValue );
        this.attributeModified( new ValueModifiedEvent( getEntry().getConnection(), getEntry(), this, oldValue,
            newValue ) );
    }


    /**
     * {@inheritDoc}
     */
    public IValue[] getValues()
    {
        return ( IValue[] ) valueList.toArray( new IValue[0] );
    }


    /**
     * {@inheritDoc}
     */
    public int getValueSize()
    {
        return this.valueList.size();
    }


    /**
     * {@inheritDoc}
     */
    public String getDescription()
    {
        return getAttributeDescription().getDescription();
    }


    /**
     * {@inheritDoc}
     */
    public String getType()
    {
        return getAttributeDescription().getParsedAttributeType();
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getDescription();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof IAttribute ) )
        {
            return false;
        }
        IAttribute a = ( IAttribute ) o;

        // compare entries
        if ( !getEntry().equals( a.getEntry() ) )
        {
            return false;
        }

        // compare attribute description
        return getDescription().equals( a.getDescription() );
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return getDescription().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public byte[][] getBinaryValues()
    {
        List<byte[]> binaryValueList = new ArrayList<byte[]>();

        IValue[] values = getValues();
        for ( int i = 0; i < values.length; i++ )
        {
            binaryValueList.add( values[i].getBinaryValue() );
        }

        return binaryValueList.toArray( new byte[0][] );
    }


    /**
     * {@inheritDoc}
     */
    public String getStringValue()
    {
        if ( getValueSize() > 0 )
        {
            return ( ( IValue ) valueList.get( 0 ) ).getStringValue();
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public String[] getStringValues()
    {
        List<String> stringValueList = new ArrayList<String>();

        IValue[] values = getValues();
        for ( int i = 0; i < values.length; i++ )
        {
            stringValueList.add( values[i].getStringValue() );
        }

        return stringValueList.toArray( new String[stringValueList.size()] );
    }


    /**
     * {@inheritDoc}
     */
    public AttributeTypeDescription getAttributeTypeDescription()
    {
        return getEntry().getConnection().getSchema().getAttributeTypeDescription( getType() );
    }


    /**
     * {@inheritDoc}
     */
    public Object getAdapter( Class adapter )
    {

        Class<?> clazz = ( Class<?> ) adapter;
        if ( clazz.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( clazz.isAssignableFrom( IConnection.class ) )
        {
            return getEntry().getConnection();
        }
        if ( clazz.isAssignableFrom( IEntry.class ) )
        {
            return getEntry();
        }
        if ( clazz.isAssignableFrom( IAttribute.class ) )
        {
            return this;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public AttributeDescription getAttributeDescription()
    {
        return attributeDescription;
    }

}
