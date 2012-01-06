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


public final class LdifInvalidPart implements LdifPart
{

    private static final long serialVersionUID = 3107136058896890735L;

    private int offset;

    private String unknown;


    protected LdifInvalidPart()
    {
    }


    public LdifInvalidPart( int offset, String unknown )
    {
        this.offset = offset;
        this.unknown = unknown;
    }


    public final int getOffset()
    {
        return this.offset;
    }


    public final int getLength()
    {
        return this.toRawString().length();
    }


    public final String toRawString()
    {
        return this.unknown;
    }


    public final String toFormattedString( LdifFormatParameters formatParameters )
    {
        return this.unknown;
    }


    public final String toString()
    {
        String text = toRawString();
        text = text.replaceAll( "\n", "\\\\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        text = text.replaceAll( "\r", "\\\\r" ); //$NON-NLS-1$ //$NON-NLS-2$
        return getClass().getName() + " (" + getOffset() + "," + getLength() + "): '" + text + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }


    public final boolean isValid()
    {
        return false;
    }


    public String getInvalidString()
    {
        return "Unexpected Token";
    }


    public final void adjustOffset( int adjust )
    {
        this.offset += adjust;
    }

}
