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


public abstract class LdifNonEmptyLineBase extends LdifLineBase
{

    private String rawLineStart;


    protected LdifNonEmptyLineBase()
    {
    }


    public LdifNonEmptyLineBase( int offset, String rawLineStart, String rawNewLine )
    {
        super( offset, rawNewLine );
        this.rawLineStart = rawLineStart;
    }


    public String getRawLineStart()
    {
        return getNonNull( this.rawLineStart );
    }


    public String getUnfoldedLineStart()
    {
        return unfold( this.getRawLineStart() );
    }


    public boolean isValid()
    {
        return super.isValid() && this.rawLineStart != null;
    }


    public String getInvalidString()
    {
        if ( this.rawLineStart == null )
        {
            return "Missing line start";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public String toRawString()
    {
        return this.getRawLineStart() + this.getRawNewLine();
    }


    public boolean isFolded()
    {
        String rawString = toRawString();
        return rawString.indexOf( "\n " ) > -1 || rawString.indexOf( "\r " ) > -1;
    }

}
