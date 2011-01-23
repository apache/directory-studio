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


import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.ValueAddedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueDeletedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueModifiedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueMultiModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ValueRenamedEvent;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;


/**
 * Performs compound operations to model classes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompoundModification
{

    /**
     * Rename the values by removing the old attribute from the entry and adding 
     * a new attribute to the entry. Only one event (an {@link ValueRenamedEvent}) is fired.
     * 
     * @param oldValues the old values
     * @param newAttributeDescription the new attribute description
     */
    public void renameValues( IValue[] oldValues, String newAttributeDescription )
    {
        if ( ArrayUtils.isEmpty( oldValues ) )
        {
            throw new IllegalArgumentException( "Expected non-null and non-empty values array." ); //$NON-NLS-1$
        }
        if ( StringUtils.isEmpty( newAttributeDescription ) )
        {
            throw new IllegalArgumentException( "Expected non-null and non-empty attribute description." ); //$NON-NLS-1$
        }

        if ( newAttributeDescription != null && !"".equals( newAttributeDescription ) //$NON-NLS-1$
            && !newAttributeDescription.equals( oldValues[0].getAttribute().getDescription() ) )
        {
            ValueRenamedEvent event = null;
            try
            {
                EventRegistry.suspendEventFiringInCurrentThread();
                for ( IValue oldValue : oldValues )
                {
                    if ( !newAttributeDescription.equals( oldValue.getAttribute().getDescription() ) )
                    {
                        IAttribute oldAttribute = oldValue.getAttribute();
                        IEntry entry = oldAttribute.getEntry();
                        IValue newValue = null;

                        // delete old value
                        oldAttribute.deleteValue( oldValue );
                        if ( oldAttribute.getValueSize() == 0 )
                        {
                            entry.deleteAttribute( oldAttribute );
                        }

                        // add new value
                        IAttribute attribute = entry.getAttribute( newAttributeDescription );
                        if ( attribute == null )
                        {
                            attribute = new Attribute( entry, newAttributeDescription );
                            entry.addAttribute( attribute );
                        }
                        newValue = new Value( attribute, oldValue.getRawValue() );
                        attribute.addValue( newValue );

                        // prepare event
                        if ( event == null )
                        {
                            event = new ValueRenamedEvent( entry.getBrowserConnection(), entry, oldValue, newValue );
                        }
                    }
                }
            }
            finally
            {
                EventRegistry.resumeEventFiringInCurrentThread();
            }

            // fire events
            EventRegistry.fireEntryUpdated( event, this );
        }
    }


    /**
     * Deletes the values and the attribute if no values remain.
     * Only one event (an {@link ValueDeletedEvent}) is fired.
     *
     * @param values
     *      the Values to delete
     */
    public void deleteValues( Collection<IValue> values )
    {
        if ( CollectionUtils.isEmpty( values ) )
        {
            throw new IllegalArgumentException( "Expected non-null and non-empty values collection." ); //$NON-NLS-1$
        }

        ValueDeletedEvent event = null;
        try
        {
            EventRegistry.suspendEventFiringInCurrentThread();
            for ( IValue value : values )
            {
                IAttribute attribute = value.getAttribute();
                IEntry entry = attribute.getEntry();

                attribute.deleteValue( value );
                if ( event == null )
                {
                    event = new ValueDeletedEvent( entry.getBrowserConnection(), entry, attribute, value );
                }

                if ( attribute.getValueSize() == 0 )
                {
                    attribute.getEntry().deleteAttribute( attribute );
                }
            }
        }
        finally
        {
            EventRegistry.resumeEventFiringInCurrentThread();
        }

        // fire event
        EventRegistry.fireEntryUpdated( event, this );
    }


    /**
     * Modifies the value and sets the given raw value.
     * Only one event (an {@link ValueAddEvent} or an {@link ValueModifiedEvent}) is fired.
     * 
     * @param oldValue the old value
     * @param newRawValue the new raw value
     */
    public void modifyValue( IValue oldValue, Object newRawValue )
    {
        if ( oldValue == null || newRawValue == null )
        {
            throw new IllegalArgumentException( "Expected non-null value." ); //$NON-NLS-1$
        }

        IAttribute attribute = oldValue.getAttribute();

        boolean modify = false;
        if ( oldValue != null && newRawValue != null && newRawValue instanceof byte[] )
        {
            byte[] newValue = ( byte[] ) newRawValue;
            if ( !Utils.equals( oldValue.getBinaryValue(), newValue ) )
            {
                modify = true;
            }
        }
        else if ( oldValue != null && newRawValue != null && newRawValue instanceof String )
        {

            String newValue = ( String ) newRawValue;
            if ( !oldValue.getStringValue().equals( newValue ) )
            {
                modify = true;
            }
        }

        if ( modify )
        {
            if ( oldValue.isEmpty() )
            {
                EventRegistry.suspendEventFiringInCurrentThread();
                attribute.deleteEmptyValue();
                EventRegistry.resumeEventFiringInCurrentThread();

                Value value = new Value( attribute, newRawValue );
                attribute.addValue( value );
            }
            else
            {
                IValue newValue = new Value( attribute, newRawValue );
                attribute.modifyValue( oldValue, newValue );
            }
        }
    }


    /**
     * Creates the attribute with the given value in the entry.
     * Only one event (an {@link ValueAddedEvent}) is fired.
     * 
     * @param entry the entry
     * @param attributeDescription the attribute description
     * @param newRawValue the new raw value
     * 
     * @throws ModelModificationException the model modification exception
     */
    public void createValue( IEntry entry, String attributeDescription, Object newRawValue )
    {
        if ( entry == null )
        {
            throw new IllegalArgumentException( "Expected non-null entry." ); //$NON-NLS-1$
        }
        if ( StringUtils.isEmpty( attributeDescription ) )
        {
            throw new IllegalArgumentException( "Expected non-null and non-empty attribute description." ); //$NON-NLS-1$
        }
        if ( newRawValue == null )
        {
            throw new IllegalArgumentException( "Expected non-null value." ); //$NON-NLS-1$
        }

        IAttribute attribute = entry.getAttribute( attributeDescription );
        if ( attribute == null )
        {
            EventRegistry.suspendEventFiringInCurrentThread();
            attribute = new Attribute( entry, attributeDescription );
            entry.addAttribute( attribute );
            EventRegistry.resumeEventFiringInCurrentThread();
        }

        Value value = new Value( attribute, newRawValue );
        attribute.addValue( value );
    }


    /**
     * Creates the attribute with the given value in the entry.
     * Only one event (an {@link ValueAddedEvent}) is fired.
     * 
     * @param entry the entry
     * @param values the values
     * 
     * @throws ModelModificationException the model modification exception
     */
    public void createValues( IEntry entry, IValue... values )
    {
        if ( entry == null )
        {
            throw new IllegalArgumentException( "Expected non-null entry." ); //$NON-NLS-1$
        }
        if ( ArrayUtils.isEmpty( values ) )
        {
            throw new IllegalArgumentException( "Expected non-null and non-empty values array." ); //$NON-NLS-1$
        }

        ValueAddedEvent event = null;
        EventRegistry.suspendEventFiringInCurrentThread();
        for ( IValue value : values )
        {
            String attributeDescription = value.getAttribute().getDescription();
            IAttribute attribute = entry.getAttribute( attributeDescription );
            if ( attribute == null )
            {
                attribute = new Attribute( entry, attributeDescription );
                entry.addAttribute( attribute );
            }
            Value newValue = new Value( attribute, value.getRawValue() );
            attribute.addValue( newValue );
            if ( event == null )
            {
                event = new ValueAddedEvent( entry.getBrowserConnection(), entry, attribute, newValue );
            }
        }
        EventRegistry.resumeEventFiringInCurrentThread();

        // fire event
        EventRegistry.fireEntryUpdated( event, this );
    }


    /**
     * Copies all attributes and values from the 1st entry to the second entry.
     * Clears all existing attributes from the 2nd entry.
     * Only one event (an {@link ValueMultiModificationEvent}) is fired.
     *
     * @param fromEntry
     * @param toEntry
     */
    public void replaceAttributes( IEntry fromEntry, IEntry toEntry, Object source )
    {
        EventRegistry.suspendEventFiringInCurrentThread();
        for ( IAttribute attribute : toEntry.getAttributes() )
        {
            toEntry.deleteAttribute( attribute );
        }

        // create new attributes
        for ( IAttribute attribute : fromEntry.getAttributes() )
        {
            IAttribute newAttribute = new Attribute( toEntry, attribute.getDescription() );
            for ( IValue value : attribute.getValues() )
            {
                IValue newValue = new Value( newAttribute, value.getRawValue() );
                newAttribute.addValue( newValue );
            }
            toEntry.addAttribute( newAttribute );
        }
        EventRegistry.resumeEventFiringInCurrentThread();

        ValueMultiModificationEvent event = new ValueMultiModificationEvent( toEntry.getBrowserConnection(), toEntry );
        EventRegistry.fireEntryUpdated( event, source );
    }


    /**
     * Clones an entry, no event is fired.
     * 
     * @param entry the entry
     * 
     * @return the cloned entry
     */
    public IEntry cloneEntry( IEntry entry )
    {
        try
        {
            EventRegistry.suspendEventFiringInCurrentThread();
            IBrowserConnection browserConnection = entry.getBrowserConnection();
            LdifContentRecord record = ModelConverter.entryToLdifContentRecord( entry );
            IEntry clonedEntry = ModelConverter.ldifContentRecordToEntry( record, browserConnection );
            return clonedEntry;
        }
        catch ( LdapInvalidDnException e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            EventRegistry.resumeEventFiringInCurrentThread();
        }
    }

}
