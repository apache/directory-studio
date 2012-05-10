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

import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;


public class LdifChangeAddRecord extends LdifChangeRecord
{

    private static final long serialVersionUID = -8976783000053951136L;


    protected LdifChangeAddRecord()
    {
    }


    public LdifChangeAddRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addAttrVal( LdifAttrValLine attrVal )
    {
        if ( attrVal == null )
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
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


    public static LdifChangeAddRecord create( String dn )
    {
        LdifChangeAddRecord record = new LdifChangeAddRecord( LdifDnLine.create( dn ) );
        record.setChangeType( LdifChangeTypeLine.createAdd() );
        return record;
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }
        return getAttrVals().length > 0;
    }

}
