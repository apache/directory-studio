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
        return getNonNull( rawLineStart );
    }


    public String getUnfoldedLineStart()
    {
        return unfold( getRawLineStart() );
    }


    public boolean isValid()
    {
        return super.isValid() && rawLineStart != null;
    }


    public String getInvalidString()
    {
        if ( rawLineStart == null )
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
        return getRawLineStart() + getRawNewLine();
    }


    public boolean isFolded()
    {
        String rawString = toRawString();
        return rawString.indexOf( "\n " ) > -1 || rawString.indexOf( "\r " ) > -1; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
