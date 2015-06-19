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
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;


/**
 * A LDIF container for LDIF modify change records
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifChangeModifyRecord extends LdifChangeRecord
{
    public LdifChangeModifyRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addModSpec( LdifModSpec modSpec )
    {
        if ( modSpec == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( modSpec );
    }


    public LdifModSpec[] getModSpecs()
    {
        List<LdifModSpec> ldifModSpecs = new ArrayList<LdifModSpec>();

        for ( LdifPart part : ldifParts )
        {
            if ( part instanceof LdifModSpec )
            {
                ldifModSpecs.add( ( LdifModSpec ) part );
            }
        }

        return ldifModSpecs.toArray( new LdifModSpec[ldifModSpecs.size()] );
    }


    public static LdifChangeModifyRecord create( String dn )
    {
        LdifChangeModifyRecord record = new LdifChangeModifyRecord( LdifDnLine.create( dn ) );
        record.setChangeType( LdifChangeTypeLine.createModify() );

        return record;
    }


    public boolean isValid()
    {
        return super.isAbstractValid();
    }
}
