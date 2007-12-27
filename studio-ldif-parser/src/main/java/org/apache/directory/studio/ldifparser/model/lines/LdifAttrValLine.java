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

package org.apache.directory.studio.ldifparser.model.lines;


import java.io.Serializable;

import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.LdifUtils;


public class LdifAttrValLine extends LdifValueLineBase implements Serializable
{

    private static final long serialVersionUID = 2818131653565822685L;


    protected LdifAttrValLine()
    {
    }


    public LdifAttrValLine( int offset, String attributeDescripton, String valueType, String value, String newLine )
    {
        super( offset, attributeDescripton, valueType, value, newLine );
    }


    public String getRawAttributeDescription()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedAttributeDescription()
    {
        return super.getUnfoldedLineStart();
    }


    public boolean isValid()
    {
        return super.isValid();
    }


    public String getInvalidString()
    {
        if ( this.getUnfoldedAttributeDescription().length() == 0 )
        {
            return "Missing attribute name";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifAttrValLine create( String name, String value )
    {
        if ( LdifUtils.mustEncode( value ) )
        {
            return create( name, LdifUtils.utf8encode( value ) );
        }
        else
        {
            return new LdifAttrValLine( 0, name, ":", value, LdifParserConstants.LINE_SEPARATOR );
        }
    }


    public static LdifAttrValLine create( String name, byte[] value )
    {
        return new LdifAttrValLine( 0, name, "::", LdifUtils.base64encode( value ), LdifParserConstants.LINE_SEPARATOR );
    }

}
