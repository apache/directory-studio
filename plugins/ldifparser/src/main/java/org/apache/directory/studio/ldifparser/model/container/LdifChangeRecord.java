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

import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;


/**
 * A LDIF container for LDIF change records
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifChangeRecord extends LdifRecord
{
    public LdifChangeRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addControl( LdifControlLine controlLine )
    {
        if ( controlLine == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( controlLine );
    }


    public void setChangeType( LdifChangeTypeLine changeTypeLine )
    {
        if ( changeTypeLine == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        if ( getChangeTypeLine() != null )
        {
            throw new IllegalArgumentException( "changetype is already set" );
        }

        ldifParts.add( changeTypeLine );
    }


    public LdifControlLine[] getControls()
    {
        List<LdifControlLine> ldifControlLines = new ArrayList<LdifControlLine>();

        for ( Object part : ldifParts )
        {
            if ( part instanceof LdifControlLine )
            {
                ldifControlLines.add( ( LdifControlLine ) part );
            }
        }

        return ldifControlLines.toArray( new LdifControlLine[ldifControlLines.size()] );
    }


    public LdifChangeTypeLine getChangeTypeLine()
    {
        for ( Object part : ldifParts )
        {
            if ( part instanceof LdifChangeTypeLine )
            {
                return ( LdifChangeTypeLine ) part;
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
        {
            return "Missing changetype line";
        }

        return super.getInvalidString();
    }
}
