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


/**
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class LdifEOFPart implements LdifPart
{
    /** The offset of this part */
    private int offset;


    public LdifEOFPart( int offset )
    {
        this.offset = offset;
    }


    public final int getOffset()
    {
        return offset;
    }


    public final int getLength()
    {
        return 0;
    }


    /**
     * @return The raw version of a EOF part : an empty String
     */
    public final String toRawString()
    {
        return ""; //$NON-NLS-1$
    }


    /**
     * @return The formatted version of a EOF part : an empty String
     */
    public final String toFormattedString( LdifFormatParameters formatParameters )
    {
        return ""; //$NON-NLS-1$
    }


    public final String toString()
    {
        return getClass().getName() + " (" + getOffset() + "," + getLength() + "): ''"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }


    public final boolean isValid()
    {
        return true;
    }


    public final String getInvalidString()
    {
        return ""; //$NON-NLS-1$
    }


    public final void adjustOffset( int adjust )
    {
        offset += adjust;
    }
}
