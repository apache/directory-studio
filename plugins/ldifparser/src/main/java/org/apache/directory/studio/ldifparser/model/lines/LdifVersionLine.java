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


public class LdifVersionLine extends LdifValueLineBase
{
    public LdifVersionLine( int offset, String rawVersionSpec, String rawValueType, String rawVersion, String rawNewLine )
    {
        super( offset, rawVersionSpec, rawValueType, rawVersion, rawNewLine );
    }


    public String getRawVersionSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedVersionSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawVersion()
    {
        return super.getRawValue();
    }


    public String getUnfoldedVersion()
    {
        return super.getUnfoldedValue();
    }


    public String getInvalidString()
    {
        if ( getUnfoldedVersionSpec().length() == 0 )
        {
            return "Missing version spec";
        }
        else if ( getUnfoldedVersion().length() == 0 )
        {
            return "Missing version";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifVersionLine create()
    {
        return new LdifVersionLine( 0, "version", ":", "1", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
}
