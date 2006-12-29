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

package org.apache.directory.ldapstudio.browser.ui.valueeditors.internal;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.directory.ldapstudio.browser.core.model.AttributeHierarchy;
import org.apache.directory.ldapstudio.browser.core.model.IValue;
import org.apache.directory.ldapstudio.browser.ui.valueeditors.AbstractInPlaceStringValueEditor;


public class InPlaceGeneralizedTimeValueEditor extends AbstractInPlaceStringValueEditor
{

    public InPlaceGeneralizedTimeValueEditor()
    {
        super();
    }


    public String getDisplayValue( AttributeHierarchy ah )
    {
        if ( ah == null )
        {
            return "NULL";
        }
        else if ( ah.size() == 1 && ah.getAttribute().getValueSize() == 1 )
        {
            return getDisplayValue( ah.getAttribute().getValues()[0] );
        }
        else
        {
            return "not displayable";
        }
    }


    public String getDisplayValue( IValue value )
    {
        String displayValue = super.getDisplayValue( value );

        if ( !showRawValues() )
        {
            DateFormat ldapFormat = new SimpleDateFormat( "yyyyMMddHHmmssZ" );
            DateFormat activeDirectoryFormat = new SimpleDateFormat( "yyyyMMddHHmmss'.'SSSZ" );
            DateFormat targetFormat = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.LONG );

            String s = displayValue;
            if ( s.matches( "[\\.0-9]+Z" ) )
            {
                s = s.replaceAll( "Z", "GMT" );
            }

            try
            {
                Date date = ldapFormat.parse( s );
                displayValue = targetFormat.format( date ) + " (" + displayValue + ")";
            }
            catch ( ParseException e1 )
            {
                try
                {
                    Date date = activeDirectoryFormat.parse( s );
                    displayValue = targetFormat.format( date ) + " (" + displayValue + ")";
                }
                catch ( ParseException e2 )
                {
                }
            }
        }

        return displayValue;
    }

}
