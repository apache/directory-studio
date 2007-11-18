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
package org.apache.directory.studio.ldapbrowser.core.model.ldif;


import org.apache.directory.studio.ldifparser.LdifParserConstants;


public class LdifFormatParameters
{

    /** The default LDIF format parameters */
    public static final LdifFormatParameters DEFAULT = new LdifFormatParameters();

    private boolean spaceAfterColon;
    private int lineWidth;
    private String lineSeparator;


    /**
     * Creates a new instance of LdifFormatParameters with the following parameters:
     * <ul>
     * <li>Space after colon
     * <li>Line width 78
     * <li>The system specific line separator
     * </ul>
     */
    private LdifFormatParameters()
    {
        this.spaceAfterColon = true;
        this.lineWidth = LdifParserConstants.LINE_WIDTH;
        this.lineSeparator = LdifParserConstants.LINE_SEPARATOR;
    }


    public LdifFormatParameters( boolean spaceAfterColon, int lineWidth, String lineSeparator )
    {
        this.spaceAfterColon = spaceAfterColon;
        this.lineWidth = lineWidth;
        this.lineSeparator = lineSeparator;
    }


    public boolean isSpaceAfterColon()
    {
        return spaceAfterColon;
    }


    public void setSpaceAfterColon( boolean spaceAfterColon )
    {
        this.spaceAfterColon = spaceAfterColon;
    }


    public int getLineWidth()
    {
        return lineWidth;
    }


    public void setLineWidth( int lineWidth )
    {
        this.lineWidth = lineWidth;
    }


    public String getLineSeparator()
    {
        return lineSeparator;
    }


    public void setLineSeparator( String lineSeparator )
    {
        this.lineSeparator = lineSeparator;
    }

}
