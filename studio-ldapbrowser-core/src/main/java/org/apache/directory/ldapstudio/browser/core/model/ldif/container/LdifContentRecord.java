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

package org.apache.directory.ldapstudio.browser.core.model.ldif.container;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifDnLine;


public class LdifContentRecord extends LdifRecord
{

    private static final long serialVersionUID = -1410857864284794069L;


    protected LdifContentRecord()
    {
    }


    public LdifContentRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addAttrVal( LdifAttrValLine attrVal )
    {
        if ( attrVal == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( attrVal );
    }


    public LdifAttrValLine[] getAttrVals()
    {
        List l = new ArrayList();
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifAttrValLine )
            {
                l.add( o );
            }
        }
        return ( LdifAttrValLine[] ) l.toArray( new LdifAttrValLine[l.size()] );
    }


    public static LdifContentRecord create( String dn )
    {
        return new LdifContentRecord( LdifDnLine.create( dn ) );
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }
        return getAttrVals().length > 0;

    }


    public String getInvalidString()
    {
        if ( !( getAttrVals().length > 0 ) )
            return "Record must contain attribute value lines";
        else
            return super.getInvalidString();
    }

}
