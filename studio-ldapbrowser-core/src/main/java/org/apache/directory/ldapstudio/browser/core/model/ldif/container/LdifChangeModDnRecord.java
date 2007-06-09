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


import java.util.Iterator;

import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifChangeTypeLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifDeloldrdnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifDnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifNewrdnLine;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifNewsuperiorLine;


public class LdifChangeModDnRecord extends LdifChangeRecord
{

    private static final long serialVersionUID = 4439094400671169207L;


    protected LdifChangeModDnRecord()
    {
    }


    public LdifChangeModDnRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void setNewrdn( LdifNewrdnLine newrdn )
    {
        if ( newrdn == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( newrdn );
    }


    public void setDeloldrdn( LdifDeloldrdnLine deloldrdn )
    {
        if ( deloldrdn == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( deloldrdn );
    }


    public void setNewsuperior( LdifNewsuperiorLine newsuperior )
    {
        if ( newsuperior == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( newsuperior );
    }


    public LdifNewrdnLine getNewrdnLine()
    {
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifNewrdnLine )
            {
                return ( LdifNewrdnLine ) o;
            }
        }

        return null;
    }


    public LdifDeloldrdnLine getDeloldrdnLine()
    {
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifDeloldrdnLine )
            {
                return ( LdifDeloldrdnLine ) o;
            }
        }

        return null;
    }


    public LdifNewsuperiorLine getNewsuperiorLine()
    {
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifNewsuperiorLine )
            {
                return ( LdifNewsuperiorLine ) o;
            }
        }

        return null;
    }


    public static LdifChangeModDnRecord create( String dn )
    {
        LdifChangeModDnRecord record = new LdifChangeModDnRecord( LdifDnLine.create( dn ) );
        record.setChangeType( LdifChangeTypeLine.createModDn() );
        return record;
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }

        return this.getNewrdnLine() != null && this.getDeloldrdnLine() != null;
    }


    public String getInvalidString()
    {
        if ( this.getNewrdnLine() == null )
        {
            return "Missing new RDN";
        }
        else if ( this.getDeloldrdnLine() == null )
        {
            return "Missing delete old RDN";
        }
        else
        {
            return super.getInvalidString();
        }
    }

}
