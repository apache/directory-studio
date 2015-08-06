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

package org.apache.directory.studio.ldifparser.model;


import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifUtils;


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class LdifInvalidPart implements LdifPart
{
    private int offset;

    private String unknown;


    public LdifInvalidPart( int offset, String unknown )
    {
        this.offset = offset;
        this.unknown = unknown;
    }


    public int getOffset()
    {
        return offset;
    }


    public int getLength()
    {
        return toRawString().length();
    }


    /**
     * @return The raw version of a Invalid part : the invalid String, unchanged
     */
    public String toRawString()
    {
        return unknown;
    }


    public String toFormattedString( LdifFormatParameters formatParameters )
    {
        return unknown;
    }


    public String toString()
    {
        String text = toRawString();
        text = LdifUtils.convertNlRcToString( text );

        return getClass().getName() + " (" + getOffset() + "," + getLength() + "): '" + text + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }


    public boolean isValid()
    {
        return false;
    }


    public String getInvalidString()
    {
        return "Unexpected Token";
    }


    public void adjustOffset( int adjust )
    {
        offset += adjust;
    }
}
