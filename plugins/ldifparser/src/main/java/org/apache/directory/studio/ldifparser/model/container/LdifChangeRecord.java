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

package org.apache.directory.studio.ldifparser.model.container;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;


public class LdifChangeRecord extends LdifRecord
{

    private static final long serialVersionUID = 2995003778589275697L;


    protected LdifChangeRecord()
    {
    }


    public LdifChangeRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addControl( LdifControlLine controlLine )
    {
        if ( controlLine == null )
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        this.parts.add( controlLine );
    }


    public void setChangeType( LdifChangeTypeLine changeTypeLine )
    {
        if ( changeTypeLine == null )
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        if ( getChangeTypeLine() != null )
            throw new IllegalArgumentException( "changetype is already set" );
        this.parts.add( changeTypeLine );
    }


    public LdifControlLine[] getControls()
    {
        List l = new ArrayList();
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifControlLine )
            {
                l.add( o );
            }
        }
        return ( LdifControlLine[] ) l.toArray( new LdifControlLine[l.size()] );
    }


    public LdifChangeTypeLine getChangeTypeLine()
    {
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifChangeTypeLine )
            {
                return ( LdifChangeTypeLine ) o;
            }
        }

        return null;
    }


    protected boolean isAbstractValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }
        return getChangeTypeLine() != null;
    }


    public boolean isValid()
    {
        return this.isAbstractValid();
    }


    public String getInvalidString()
    {

        if ( getChangeTypeLine() == null )
            return "Missing changetype line";

        return super.getInvalidString();
    }

}
