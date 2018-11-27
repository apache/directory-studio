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


import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * Abstract base class for value editors that handle string values
 * in a dialog. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
        Object obj = getRawValue( value );
        return StringValueEditorUtils.getDisplayValue( obj );
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
        return StringValueEditorUtils.getRawValue( value );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always return the string value
     * as String.
     */
    public Object getStringOrBinaryValue( Object rawValue )
    {
        return StringValueEditorUtils.getStringOrBinaryValue( rawValue );
    }
}
