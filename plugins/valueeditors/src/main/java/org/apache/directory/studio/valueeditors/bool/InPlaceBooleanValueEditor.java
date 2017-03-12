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

package org.apache.directory.studio.valueeditors.bool;


import org.apache.directory.api.ldap.model.schema.syntaxCheckers.BooleanSyntaxChecker;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.valueeditors.AbstractInPlaceStringValueEditor;


/**
 * Implementation of IValueEditor for syntax 1.3.6.1.4.1.1466.115.121.1.7 
 * (Boolean syntax).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class InPlaceBooleanValueEditor extends AbstractInPlaceStringValueEditor
{
    /** The 'TRUE' value */
    private static final String TRUE = "TRUE";

    /** The 'FALSE' value */
    private static final String FALSE = "FALSE";


    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doGetValue()
    {
        Object value = super.doGetValue();

        if ( value instanceof String )
        {
            String stringValue = ( ( String ) value ).toUpperCase();
            
            switch ( stringValue )
            {
                case "T" :
                case "TRUE" :
                case "Y" :
                case "YES" :
                case "1" :
                case "" :           // Special case : default to TRUE
                default :
                    return TRUE;
                    
                case "F" :
                case "FALSE" :
                case "N" :
                case "NO" :
                case "0" :
                    return FALSE;
            }
        }

        return value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSetValue( Object value )
    {
        Object v = value;
        
        if ( value instanceof IValue.EmptyValue )
        {
            v = ( ( IValue.EmptyValue ) value ).getStringValue();
        }
        
        if ( value == null )
        {
            v = "TRUE";
        }
        
        super.doSetValue( v );
    }


    @Override
    public Object getRawValue( IValue value )
    {
        Object rawValue = super.getRawValue( value );

        if ( rawValue instanceof String )
        {
            String stringValue = ( String ) rawValue;
            
            if ( ( stringValue.length() == 0 ) || ( BooleanSyntaxChecker.INSTANCE.isValidSyntax( stringValue ) ) )
            {
                return TRUE;
            }
            else
            {
                return null;
            }
        }
        else if ( rawValue == null )
        {
            return TRUE;
        }
        else
        {
            return null;
        }
    }
}
