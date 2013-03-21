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
    protected Object doGetValue()
    {
        Object value = super.doGetValue();

        if ( value instanceof String )
        {
            String stringValue = ( String ) value;

            if ( "".equals( stringValue ) )
            {
                return null;
            }
            else if ( "TRUE".equalsIgnoreCase( stringValue )
                || "T".equalsIgnoreCase( stringValue )
                || "YES".equalsIgnoreCase( stringValue )
                || "Y".equalsIgnoreCase( stringValue )
                || "1".equalsIgnoreCase( stringValue ) )
            {
                return TRUE;
            }
            else if ( "FALSE".equalsIgnoreCase( stringValue )
                || "F".equalsIgnoreCase( stringValue )
                || "NO".equalsIgnoreCase( stringValue )
                || "N".equalsIgnoreCase( stringValue )
                || "0".equalsIgnoreCase( stringValue ) )
            {
                return FALSE;
            }
        }

        return value;
    }
}
