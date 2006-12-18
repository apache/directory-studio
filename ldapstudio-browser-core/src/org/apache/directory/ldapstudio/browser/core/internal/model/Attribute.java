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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.EmptyValueAddedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EmptyValueDeletedEvent;
import org.apache.directory.ldapstudio.browser.core.events.EntryModificationEvent;
import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
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


public class Attribute implements IAttribute
{

    private static final long serialVersionUID = -5679384884002589786L;

    private AttributeDescription attributeDescription;

    private IEntry entry;

    private List valueList;


    protected Attribute()
    {
    }


    /**
     * Creates an Attribute with the given description and no value for the
     * given entry.
     * 
     * @param entry
     *                The entry of this attribute, mustn't be null
     * @param description
     *                The attribute descrption, mustn't be null or empty.
     * @throws ModelModificationException
     *                 if the attribute name is null or empty.
     */
    public Attribute( IEntry entry, String description ) throws ModelModificationException
    {
        if ( entry == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_entry );
        }
        if ( description == null /* || "".equals(description) */)
        { //$NON-NLS-1$
            throw new ModelModificationException( BrowserCoreMessages.model__empty_attribute );
        }

        this.entry = entry;
        this.attributeDescription = new AttributeDescription( description );
        this.valueList = new ArrayList();
        // this.valueList = new LinkedHashSet();

    }


    public IEntry getEntry()
    {
        return this.entry;
    }


    public boolean isConsistent()
    {
        if ( this.valueList.isEmpty() )
        {
            return false;
        }

        for ( Iterator it = this.valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            if ( value.isEmpty() )
            {
                return false;
            }
        }

        return true;
    }


    public boolean isMustAttribute()
    {
        if ( this.isObjectClassAttribute() )
        {
            return true;
        }
        else
        {
            String[] mustAttributeNames = this.entry.getSubschema().getMustAttributeNames();
            for ( int i = 0; i < mustAttributeNames.length; i++ )
            {
                String must = mustAttributeNames[i];
                if ( must.equalsIgnoreCase( this.getType() ) )
                {
                    return true;
                }
            }
            return false;
        }
    }


    public boolean isMayAttribute()
    {
        // return
        // Arrays.asList(this.entry.getSubschema().getMayAttributeNames()).contains(this.getType());
        return !isObjectClassAttribute() && !isMustAttribute() && !isOperationalAttribute();
    }


    public boolean isOperationalAttribute()
    {
        return getAttributeTypeDescription() == null || SchemaUtils.isOperational( getAttributeTypeDescription() );
    }


    public boolean isObjectClassAttribute()
    {
        return OBJECTCLASS_ATTRIBUTE.equalsIgnoreCase( this.getDescription() );
    }


    public boolean isString()
    {
        return !this.isBinary();
    }


    public boolean isBinary()
    {
        return this.getAttributeTypeDescription().isBinary();
    }


    public void addEmptyValue( ModelModifier source )
    {
        try
        {
            IValue emptyValue = new Value( this );
            this.valueList.add( emptyValue );
            this.attributeModified( new EmptyValueAddedEvent( this.entry.getConnection(), this.entry, this, emptyValue,
                source ) );
        }
        catch ( ModelModificationException mme )
        {
            // Shouldn't occur
        }
    }


    public void deleteEmptyValue( ModelModifier source )
    {
        for ( Iterator it = this.valueList.iterator(); it.hasNext(); )
        {
            IValue value = ( IValue ) it.next();
            if ( value.isEmpty() )
            {
                it.remove();
                this.attributeModified( new EmptyValueDeletedEvent( this.entry.getConnection(), this.entry, this,
                    value, source ) );
                return;
            }
        }
    }


    private void attributeModified( EntryModificationEvent event )
    {
        EventRegistry.fireEntryUpdated( event, this.getEntry() );
    }


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


    private boolean deleteValue( IValue valueToDelete )
    {
        for ( Iterator it = this.valueList.iterator(); it.hasNext(); )
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


    public void addValue( IValue valueToAdd, ModelModifier source ) throws ModelModificationException
    {
        this.checkValue( valueToAdd );

        this.valueList.add( valueToAdd );
        this
            .attributeModified( new ValueAddedEvent( this.entry.getConnection(), this.entry, this, valueToAdd, source ) );
    }


    public void deleteValue( IValue valueToDelete, ModelModifier source ) throws ModelModificationException
    {
        this.checkValue( valueToDelete );

        if ( this.deleteValue( valueToDelete ) )
        {
            this.attributeModified( new ValueDeletedEvent( this.entry.getConnection(), this.entry, this, valueToDelete,
                source ) );
        }
    }


    public void modifyValue( IValue oldValue, IValue newValue, ModelModifier source ) throws ModelModificationException
    {
        this.checkValue( oldValue );
        this.checkValue( newValue );

        this.deleteValue( oldValue );
        this.valueList.add( newValue );
        this.attributeModified( new ValueModifiedEvent( this.entry.getConnection(), this.entry, this, oldValue,
            newValue, source ) );
    }


    public IValue[] getValues()
    {
        return ( IValue[] ) this.valueList.toArray( new IValue[0] );
    }


    public int getValueSize()
    {
        return this.valueList.size();
    }


    public String getDescription()
    {
        return this.attributeDescription.getDescription();
    }


    public String getType()
    {
        return this.attributeDescription.getParsedAttributeType();
    }


    public String toString()
    {
        return this.getDescription();
    }


    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof IAttribute ) )
        {
            return false;
        }
        IAttribute a = ( IAttribute ) o;

        // compare entries
        if ( !this.getEntry().equals( a.getEntry() ) )
        {
            return false;
        }

        // compare attribute description
        return this.getDescription().equals( a.getDescription() );
    }


    public int hashCode()
    {
        return this.getDescription().hashCode();
    }


    public byte[][] getBinaryValues()
    {
        List binaryValueList = new ArrayList();

        IValue[] values = this.getValues();
        for ( int i = 0; i < values.length; i++ )
        {
            binaryValueList.add( values[i].getBinaryValue() );
        }

        return ( byte[][] ) binaryValueList.toArray( new byte[0][] );
    }


    public String getStringValue()
    {
        if ( getValueSize() > 0 )
        {
            return ( ( IValue ) this.valueList.get( 0 ) ).getStringValue();
        }
        else
        {
            return null;
        }
    }


    public String[] getStringValues()
    {
        List stringValueList = new ArrayList();

        IValue[] values = this.getValues();
        for ( int i = 0; i < values.length; i++ )
        {
            stringValueList.add( values[i].getStringValue() );
        }

        return ( String[] ) stringValueList.toArray( new String[stringValueList.size()] );
    }


    public AttributeTypeDescription getAttributeTypeDescription()
    {
        return getEntry().getConnection().getSchema().getAttributeTypeDescription( this.getType() );
    }


    public Object getAdapter( Class adapter )
    {

        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IConnection.class )
        {
            return this.getConnection();
        }
        if ( adapter == IEntry.class )
        {
            return this.getEntry();
        }
        if ( adapter == IAttribute.class )
        {
            return this;
        }

        return null;
    }


    public IConnection getConnection()
    {
        return this.entry.getConnection();
    }


    public IAttribute getAttribute()
    {
        return this;
    }


    public AttributeDescription getAttributeDescription()
    {
        return attributeDescription;
    }

}
