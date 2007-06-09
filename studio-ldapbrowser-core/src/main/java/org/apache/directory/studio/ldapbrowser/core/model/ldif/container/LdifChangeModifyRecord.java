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

package org.apache.directory.studio.ldapbrowser.core.model.ldif.container;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifDnLine;


public class LdifChangeModifyRecord extends LdifChangeRecord
{

    private static final long serialVersionUID = 6971543260694585796L;


    protected LdifChangeModifyRecord()
    {
    }


    public LdifChangeModifyRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addModSpec( LdifModSpec modSpec )
    {
        if ( modSpec == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( modSpec );
    }


    public LdifModSpec[] getModSpecs()
    {
        List l = new ArrayList();
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifModSpec )
            {
                l.add( o );
            }
        }
        return ( LdifModSpec[] ) l.toArray( new LdifModSpec[l.size()] );
    }


    public static LdifChangeModifyRecord create( String dn )
    {
        LdifChangeModifyRecord record = new LdifChangeModifyRecord( LdifDnLine.create( dn ) );
        record.setChangeType( LdifChangeTypeLine.createModify() );
        return record;
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }

        return this.getModSpecs().length > 0;
    }

}
