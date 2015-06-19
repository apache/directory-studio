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


import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewsuperiorLine;


/**
 * A LDIF container for LDIF moddn change records
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifChangeModDnRecord extends LdifChangeRecord
{
    public LdifChangeModDnRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void setNewrdn( LdifNewrdnLine newrdn )
    {
        if ( newrdn == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( newrdn );
    }


    public void setDeloldrdn( LdifDeloldrdnLine deloldrdn )
    {
        if ( deloldrdn == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( deloldrdn );
    }


    public void setNewsuperior( LdifNewsuperiorLine newsuperior )
    {
        if ( newsuperior == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( newsuperior );
    }


    public LdifNewrdnLine getNewrdnLine()
    {
        for ( LdifPart part : ldifParts )
        {
            if ( part instanceof LdifNewrdnLine )
            {
                return ( LdifNewrdnLine ) part;
            }
        }

        return null;
    }


    public LdifDeloldrdnLine getDeloldrdnLine()
    {
        for ( Object part : ldifParts )
        {
            if ( part instanceof LdifDeloldrdnLine )
            {
                return ( LdifDeloldrdnLine ) part;
            }
        }

        return null;
    }


    public LdifNewsuperiorLine getNewsuperiorLine()
    {
        for ( Object part : ldifParts )
        {
            if ( part instanceof LdifNewsuperiorLine )
            {
                return ( LdifNewsuperiorLine ) part;
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

        return ( getNewrdnLine() != null ) && ( getDeloldrdnLine() != null );
    }


    public String getInvalidString()
    {
        if ( getNewrdnLine() == null )
        {
            return "Missing new Rdn";
        }
        else if ( getDeloldrdnLine() == null )
        {
            return "Missing delete old Rdn";
        }
        else
        {
            return super.getInvalidString();
        }
    }
}
