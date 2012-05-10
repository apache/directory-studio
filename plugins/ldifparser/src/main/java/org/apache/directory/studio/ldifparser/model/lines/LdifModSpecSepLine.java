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


public class LdifModSpecSepLine extends LdifNonEmptyLineBase
{

    private static final long serialVersionUID = -6411592502825895865L;


    protected LdifModSpecSepLine()
    {
    }


    public LdifModSpecSepLine( int offset, String rawMinus, String rawNewLine )
    {
        super( offset, rawMinus, rawNewLine );
    }


    public String getRawMinus()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedMinus()
    {
        return super.getUnfoldedLineStart();
    }


    public String toRawString()
    {
        return super.toRawString();
    }


    public boolean isValid()
    {
        return super.isValid() && this.getUnfoldedMinus().equals( "-" ); //$NON-NLS-1$
    }


    public String getInvalidString()
    {
        if ( !this.getUnfoldedMinus().equals( "-" ) ) //$NON-NLS-1$
        {
            return "Missing '-'";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifModSpecSepLine create()
    {
        return new LdifModSpecSepLine( 0, "-", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$
    }

}
