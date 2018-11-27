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


public class LdifDeloldrdnLine extends LdifValueLineBase
{
    public LdifDeloldrdnLine( int offset, String rawDeleteOldrdnSpec, String rawValueType, String rawDeleteOldrdn,
        String rawNewLine )
    {
        super( offset, rawDeleteOldrdnSpec, rawValueType, rawDeleteOldrdn, rawNewLine );
    }


    public String getRawDeleteOldrdnSpec()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedDeleteOldrdnSpec()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawDeleteOldrdn()
    {
        return super.getRawValue();
    }


    public String getUnfoldedDeleteOldrdn()
    {
        return super.getUnfoldedValue();
    }


    public boolean isDeleteOldRdn()
    {
        return "1".equals( getUnfoldedDeleteOldrdn() ); //$NON-NLS-1$
    }


    public boolean isValid()
    {
        if ( !super.isValid() )
        {
            return false;
        }

        return ( "0".equals( getUnfoldedDeleteOldrdn() ) || "1".equals( getUnfoldedDeleteOldrdn() ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    public String getInvalidString()
    {
        if ( getUnfoldedDeleteOldrdnSpec().length() == 0 )
        {
            return "Missing delete old Rdn spec 'deleteoldrdn'";
        }
        else if ( !"0".equals( getUnfoldedDeleteOldrdn() ) && !"1".equals( getUnfoldedDeleteOldrdn() ) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return "Invalid value of delete old Rdn, must be '0' or '1'";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifDeloldrdnLine create0()
    {
        return new LdifDeloldrdnLine( 0, "deleteoldrdn", ":", "0", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }


    public static LdifDeloldrdnLine create1()
    {
        return new LdifDeloldrdnLine( 0, "deleteoldrdn", ":", "1", LdifParserConstants.LINE_SEPARATOR ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
