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

package org.apache.directory.ldapstudio.browser.core.model.ldif.lines;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreConstants;
import org.apache.directory.ldapstudio.browser.core.utils.LdifUtils;


public class LdifNewrdnLine extends LdifValueLineBase
{

    private static final long serialVersionUID = -4393976438427677361L;


    protected LdifNewrdnLine()
    {
    }


    public LdifNewrdnLine( int offset, String rawNewrdnSpec, String rawValueType, String rawNewrdn, String rawNewLine )
    {
        super( offset, rawNewrdnSpec, rawValueType, rawNewrdn, rawNewLine );
    }


    public String getRawNewrdnSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedNewrdnSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawNewrdn()
    {
        return super.getRawValue();
    }


    public String getUnfoldedNewrdn()
    {
        return super.getUnfoldedValue();
    }


    public String toRawString()
    {
        return super.toRawString();
    }


    public boolean isValid()
    {
        return super.isValid();
    }


    public String getInvalidString()
    {
        if ( this.getUnfoldedNewrdnSpec().length() == 0 )
        {
            return "Missing new RDN spec 'newrdn'";
        }
        else if ( this.getUnfoldedNewrdn().length() == 0 )
        {
            return "Missing new RDN";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifNewrdnLine create( String newrdn )
    {
        if ( LdifUtils.mustEncode( newrdn ) )
        {
            return new LdifNewrdnLine( 0, "newrdn", "::", LdifUtils.base64encode( LdifUtils.utf8encode( newrdn ) ),
                BrowserCoreConstants.LINE_SEPARATOR );
        }
        else
        {
            return new LdifNewrdnLine( 0, "newrdn", ":", newrdn, BrowserCoreConstants.LINE_SEPARATOR );
        }

    }

}
