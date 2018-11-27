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


import java.nio.charset.StandardCharsets;

import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * Common code used by {@link AbstractInPlaceStringValueEditor} and 
 * {@link AbstractDialogStringValueEditor}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StringValueEditorUtils
{

    static String getDisplayValue( Object rawValue )
    {
        if ( rawValue == null )
        {
            return IValueEditor.NULL;
        }
        else
        {
            return rawValue.toString();
        }
    }


    static Object getRawValue( IValue value )
    {
        if ( value == null )
        {
            return null;
        }
        else if ( value.isString() )
        {
            return value.getStringValue();
        }
        else if ( value.isBinary() && StringValueEditorUtils.isEditable( value.getBinaryValue() ) )
        {
            return value.getStringValue();
        }
        else
        {
            return null;
        }
    }


    static Object getStringOrBinaryValue( Object rawValue )
    {
        if ( rawValue instanceof String )
        {
            return rawValue;
        }
        else
        {
            return null;
        }
    }


    static boolean isEditable( byte[] b )
    {
        if ( b == null )
        {
            return false;
        }

        return !( new String( b, StandardCharsets.UTF_8 ).contains( "\uFFFD" ) );
    }

}
