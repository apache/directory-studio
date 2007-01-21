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

package org.apache.directory.ldapstudio.browser.ui.valueeditors;


import org.apache.directory.ldapstudio.browser.core.events.EventRegistry;
import org.apache.directory.ldapstudio.browser.core.internal.model.Attribute;
import org.apache.directory.ldapstudio.browser.core.jobs.CreateValuesJob;
import org.apache.directory.ldapstudio.browser.core.jobs.DeleteAttributesValueJob;
import org.apache.directory.ldapstudio.browser.core.jobs.ModifyValueJob;
import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;


/**
 * 
 * Abstract base class for value editors that handle binary values
 * in a dialog. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractDialogBinaryValueEditor extends AbstractDialogValueEditor
{

    protected AbstractDialogBinaryValueEditor()
    {
        super();
    }


    /**
     * This implementation of getDisplayValue just returns a note,
     * that the value is binary and the size of the data.
     */
    public String getDisplayValue( IValue value )
    {
        if ( showRawValues() )
        {
            return getPrintableString( value );
        }
        else
        {
            if ( value == null )
            {
                return "NULL";
            }
            else if ( value.isBinary() )
            {
                byte[] data = value.getBinaryValue();
                return "Binary Data (" + data.length + " Bytes)";
            }
            else
            {
                return "Invalid Data";
            }
        }
    }


    /**
     * Help method, returns a printable string if the value 
     * is binary.
     */
    public static String getPrintableString( IValue value )
    {
        if ( value == null )
        {
            return "NULL";
        }
        else if ( value.isBinary() )
        {
            byte[] data = value.getBinaryValue();
            StringBuffer sb = new StringBuffer();
            for ( int i = 0; data != null && i < data.length && i < 128; i++ )
            {
                if ( data[i] > 32 && data[i] < 127 )
                    sb.append( ( char ) data[i] );
                else
                    sb.append( '.' );
            }
            return sb.toString();
        }
        else if ( value.isString() )
        {
            return value.getStringValue();
        }
        else
        {
            return "NULL";
        }
    }


    /**
     * This implementation returns IValue.EMPTY_BINARY_VALUE if
     * the attribute is binary.
     */
    protected Object getEmptyRawValue( IAttribute attribute )
    {
        if ( attribute.isBinary() )
        {
            return IValue.EMPTY_BINARY_VALUE;
        }
        else
        {
            return null;
        }
    }


    /**
     * This implementation returns the binary (byte[]) value 
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
            return value.getBinaryValue();
        }
        else if ( value.isBinary() )
        {
            return value.getBinaryValue();
        }
        else
        {
            return null;
        }
    }


    /**
     * This implementation returns the value itself if it is
     * of type byte[] or a byte[] with the UTF-8 encoded string 
     * value if it is of type String.  
     */
    public Object getRawValue( IConnection connection, Object value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value instanceof String )
        {
            return LdifUtils.utf8encode( ( String ) value );
        }
        else if ( value instanceof byte[] )
        {
            return value;
        }
        else
        {
            return null;
        }
    }


    /**
     * This implementation always return the binary value
     * as byte[].
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        if ( rawValue == null )
        {
            return null;
        }
        else if ( rawValue instanceof byte[] )
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
        if ( entry != null && attributeDescription != null && newRawValue != null && newRawValue instanceof byte[] )
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
                    newValue = LdifUtils.utf8decode( ( byte[] ) newRawValue );
                }
                else
                {
                    newValue = ( byte[] ) newRawValue;
                }

                new CreateValuesJob( attribute, newValue ).execute();
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    private final void modify( IAttribute attribute, Object newRawValue ) throws ModelModificationException
    {
        if ( attribute != null && newRawValue != null && newRawValue instanceof byte[] )
        {
            if ( attribute.getValueSize() == 0 )
            {
                byte[] newValue = ( byte[] ) newRawValue;
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
        if ( oldValue != null && newRawValue != null && newRawValue instanceof byte[] )
        {
            byte[] newValue = ( byte[] ) newRawValue;
            IAttribute attribute = oldValue.getAttribute();
            if ( !Utils.equals( oldValue.getBinaryValue(), newValue ) )
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
