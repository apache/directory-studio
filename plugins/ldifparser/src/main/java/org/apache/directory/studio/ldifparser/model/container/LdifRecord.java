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


import org.apache.directory.studio.ldifparser.model.LdifEOFPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;


/**
 * A LDIF container for a LDIF record (change or content)
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class LdifRecord extends LdifContainer
{
    protected LdifRecord( LdifDnLine dn )
    {
        super( dn );
    }


    public void addComment( LdifCommentLine comment )
    {
        if ( comment == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( comment );
    }


    public void finish( LdifSepLine sep )
    {
        if ( sep == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( sep );
    }


    public void finish( LdifEOFPart eof )
    {
        if ( eof == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( eof );
    }


    public LdifDnLine getDnLine()
    {
        return ( LdifDnLine ) ldifParts.get( 0 );
    }


    public LdifSepLine getSepLine()
    {
        for ( LdifPart ldifPart : ldifParts )
        {
            if ( ldifPart instanceof LdifSepLine )
            {
                return ( LdifSepLine ) ldifPart;
            }
        }

        return null;
    }


    public String getInvalidString()
    {
        LdifDnLine dnLine = getDnLine();
        LdifSepLine sepLine = getSepLine();

        if ( dnLine == null )
        {
            return "Record must start with Dn";
        }
        else if ( !dnLine.isValid() )
        {
            return dnLine.getInvalidString();
        }

        if ( sepLine == null )
        {
            return "Record must end with an empty line";
        }
        else if ( !sepLine.isValid() )
        {
            return sepLine.getInvalidString();
        }

        return super.getInvalidString();
    }


    protected boolean isAbstractValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }

        LdifPart lastPart = getLastPart();

        return getDnLine().isValid() && ( ( lastPart instanceof LdifSepLine ) || ( lastPart instanceof LdifEOFPart ) )
            && lastPart.isValid();
    }
}
