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


import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.LdifUtils;


public class LdifNewsuperiorLine extends LdifValueLineBase
{
    public LdifNewsuperiorLine( int offset, String rawNewSuperiorSpec, String rawValueType, String rawNewSuperiorDn,
        String rawNewLine )
    {
        super( offset, rawNewSuperiorSpec, rawValueType, rawNewSuperiorDn, rawNewLine );
    }


    public String getRawNewSuperiorSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedNewSuperiorSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawNewSuperiorDn()
    {
        return super.getRawValue();
    }


    public String getUnfoldedNewSuperiorDn()
    {
        return super.getUnfoldedValue();
    }


    public String getInvalidString()
    {
        if ( getUnfoldedNewSuperiorSpec().length() == 0 )
        {
            return "Missing new superior spec 'newsuperior'";
        }
        else if ( getUnfoldedNewSuperiorDn().length() == 0 )
        {
            return "Missing new superior Dn";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifNewsuperiorLine create( String newsuperior )
    {
        if ( LdifUtils.mustEncode( newsuperior ) )
        {
            return new LdifNewsuperiorLine( 0, "newsuperior", "::", LdifUtils.base64encode( LdifUtils //$NON-NLS-1$ //$NON-NLS-2$
                .utf8encode( newsuperior ) ), LdifParserConstants.LINE_SEPARATOR );
        }
        else
        {
            return new LdifNewsuperiorLine( 0, "newsuperior", ":", newsuperior, LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$
        }

    }

}
