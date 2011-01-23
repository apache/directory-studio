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


import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.LdifUtils;


public class LdifDnLine extends LdifValueLineBase
{

    private static final long serialVersionUID = 6180172049870560007L;


    protected LdifDnLine()
    {
    }


    public LdifDnLine( int offset, String rawDnSpec, String rawValueType, String rawDn, String rawNewLine )
    {
        super( offset, rawDnSpec, rawValueType, rawDn, rawNewLine );
    }


    public String getRawDnSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedDnSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawDn()
    {
        return super.getRawValue();
    }


    public String getUnfoldedDn()
    {
        return super.getUnfoldedValue();
    }


    public String toRawString()
    {
        return super.toRawString();
    }


    public boolean isValid()
    {
        return super.isValid() && Dn.isValid(getValueAsString());
    }


    public String getInvalidString()
    {
        if ( this.getUnfoldedDnSpec().length() == 0 )
        {
            return "Missing Dn spec 'dn'";
        }
        else if ( this.getUnfoldedDn().length() == 0 )
        {
            return "Missing Dn";
        }
        else if ( !Dn.isValid(getValueAsString()) )
        {
            return "Invalid Dn";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifDnLine create( String dn )
    {
        if ( LdifUtils.mustEncode( dn ) )
        {
            return new LdifDnLine( 0, "dn", "::", LdifUtils.base64encode( LdifUtils.utf8encode( dn ) ),
                LdifParserConstants.LINE_SEPARATOR );
        }
        else
        {
            return new LdifDnLine( 0, "dn", ":", dn, LdifParserConstants.LINE_SEPARATOR );
        }
    }

}
