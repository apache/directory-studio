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

package org.apache.directory.studio.ldapbrowser.core.model.ldif.lines;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;


public class LdifModSpecTypeLine extends LdifValueLineBase
{

    private static final long serialVersionUID = 82623220224991909L;


    protected LdifModSpecTypeLine()
    {
    }


    public LdifModSpecTypeLine( int offset, String rawModType, String rawValueType, String rawAttributeDescription,
        String rawNewLine )
    {
        super( offset, rawModType, rawValueType, rawAttributeDescription, rawNewLine );
    }


    public String getRawModType()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedModType()
    {
        return super.getUnfoldedLineStart();
    }


    public String getRawAttributeDescription()
    {
        return super.getRawValue();
    }


    public String getUnfoldedAttributeDescription()
    {
        return super.getUnfoldedValue();
    }


    public String toRawString()
    {
        return super.toRawString();
    }


    public boolean isAdd()
    {
        return this.getUnfoldedModType().equals( "add" );
    }


    public boolean isReplace()
    {
        return this.getUnfoldedModType().equals( "replace" );
    }


    public boolean isDelete()
    {
        return this.getUnfoldedModType().equals( "delete" );
    }


    public boolean isValid()
    {
        return super.isValid() && ( this.isAdd() || this.isReplace() || this.isDelete() );
    }


    public String getInvalidString()
    {
        if ( this.getUnfoldedModType().length() == 0 )
        {
            return "Missing modification type 'add', 'replace' or 'delete'";
        }
        else if ( !this.isAdd() && !this.isReplace() && !this.isDelete() )
        {
            return "Invalid modification type, expected 'add', 'replace' or 'delete'";
        }
        else if ( this.getUnfoldedAttributeDescription().length() == 0 )
        {
            return "Missing attribute";
        }
        else
        {
            return super.getInvalidString();
        }
    }


    public static LdifModSpecTypeLine createAdd( String attributeName )
    {
        return new LdifModSpecTypeLine( 0, "add", ":", attributeName, BrowserCoreConstants.LINE_SEPARATOR );
    }


    public static LdifModSpecTypeLine createReplace( String attributeName )
    {
        return new LdifModSpecTypeLine( 0, "replace", ":", attributeName, BrowserCoreConstants.LINE_SEPARATOR );
    }


    public static LdifModSpecTypeLine createDelete( String attributeName )
    {
        return new LdifModSpecTypeLine( 0, "delete", ":", attributeName, BrowserCoreConstants.LINE_SEPARATOR );
    }

}
