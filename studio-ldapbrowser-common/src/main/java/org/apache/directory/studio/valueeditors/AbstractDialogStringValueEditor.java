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

package org.apache.directory.studio.valueeditors;


import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.internal.model.Attribute;
import org.apache.directory.studio.ldapbrowser.core.jobs.CreateValuesJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.DeleteAttributesValueJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.ModifyValueJob;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.ModelModificationException;
import org.apache.directory.studio.ldapbrowser.core.utils.LdifUtils;


/**
 * 
 * Abstract base class for value editors that handle string values
 * in a dialog. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractDialogStringValueEditor extends AbstractDialogValueEditor
{

    /**
     * Creates a new instance of AbstractDialogStringValueEditor.
     */
    protected AbstractDialogStringValueEditor()
    {
        super();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation just returns the raw value
     */
    public String getDisplayValue( IValue value )
    {
        Object obj = this.getRawValue( value );
        return obj == null ? "NULL" : obj.toString();
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns IValue.EMPTY_STRING_VALUE if
     * the attribute is string.
     */
    protected Object getEmptyRawValue( IAttribute attribute )
    {
        if ( attribute.isString() )
        {
            return IValue.EMPTY_STRING_VALUE;
        }
        else
        {
            return IValue.EMPTY_BINARY_VALUE;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns the string value 
     * of the given value. 
     */
    public Object getRawValue( IValue value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value.isString() )
        {
            return value.getStringValue();
        }
        else if ( value.isBinary() )
        {
            return isEditable( value.getBinaryValue() ) ? value.getStringValue() : null;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation returns the value itself if it is
     * of type String. If the value is of type byte[] then the binary
     * data is converted to a String using UTF-8 encoding.  
     */
    public Object getRawValue( IConnection connection, Object value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value instanceof String )
        {
            return value;
        }
        else if ( value instanceof byte[] )
        {
            String s = LdifUtils.utf8decode( ( byte[] ) value );
            for ( int i = 0; i < s.length(); i++ )
            {
                if ( Character.isISOControl( s.charAt( i ) ) && s.charAt( i ) != '\n' && s.charAt( i ) != '\r' )
                {
                    return null;
                }
            }
            return s;
        }
        else
        {
            return null;
        }
    }


    /**
     * Small helper.
     */
    private boolean isEditable( byte[] b )
    {
        if ( b == null )
        {
            return false;
        }

        for ( int i = 0; i < b.length; i++ )
        {
            if ( !( b[i] == '\n' || b[i] == '\r' || ( b[i] >= '\u0020' && b[i] <= '\u007F' ) ) )
            {
                return false;
            }
        }

        return true;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always return the string value
     * as String.
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        if ( rawValue == null )
        {
            return null;
        }
        else if ( rawValue instanceof String )
        {
            return rawValue;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public final void createValue( IEntry entry, String attributeDescription, Object newRawValue )
        throws ModelModificationException
    {
        if ( entry != null && attributeDescription != null && newRawValue != null && newRawValue instanceof String )
        {
            if ( entry.getAttribute( attributeDescription ) != null )
            {
                this.modify( entry.getAttribute( attributeDescription ), newRawValue );
            }
            else
            {
                EventRegistry.suspendEventFireingInCurrentThread();
                IAttribute attribute = new Attribute( entry, attributeDescription );
                entry.addAttribute( attribute );
                EventRegistry.resumeEventFireingInCurrentThread();

                Object newValue;
                if ( entry.getConnection().getSchema().getAttributeTypeDescription( attributeDescription )
                    .getSyntaxDescription().isString() )
                {
                    newValue = ( String ) newRawValue;
                }
                else
                {
                    newValue = LdifUtils.utf8encode( ( String ) newRawValue );
                }

                new CreateValuesJob( attribute, newValue ).execute();
            }
        }
    }


    private final void modify( IAttribute attribute, Object newRawValue ) throws ModelModificationException
    {
        if ( attribute != null && newRawValue != null && newRawValue instanceof String )
        {
            if ( attribute.getValueSize() == 0 )
            {
                String newValue = ( String ) newRawValue;
                new CreateValuesJob( attribute, newValue ).execute();
            }
            else if ( attribute.getValueSize() == 1 )
            {
                this.modifyValue( attribute.getValues()[0], newRawValue );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public final void modifyValue( IValue oldValue, Object newRawValue ) throws ModelModificationException
    {
        if ( oldValue != null && newRawValue != null && newRawValue instanceof String )
        {

            String newValue = ( String ) newRawValue;
            IAttribute attribute = oldValue.getAttribute();
            if ( !oldValue.getStringValue().equals( newValue ) )
            {
                if ( oldValue.isEmpty() )
                {
                    EventRegistry.suspendEventFireingInCurrentThread();
                    attribute.deleteEmptyValue();
                    EventRegistry.resumeEventFireingInCurrentThread();
                    new CreateValuesJob( attribute, newValue ).execute();
                }
                else
                {
                    new ModifyValueJob( attribute, oldValue, newValue ).execute();
                }
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public final void deleteAttribute( AttributeHierarchy ah ) throws ModelModificationException
    {
        if ( ah != null )
        {
            new DeleteAttributesValueJob( ah ).execute();
        }
    }


    /**
     * {@inheritDoc}
     */
    public final void deleteValue( IValue oldValue ) throws ModelModificationException
    {
        if ( oldValue != null )
        {
            new DeleteAttributesValueJob( oldValue ).execute();
        }
    }

}
