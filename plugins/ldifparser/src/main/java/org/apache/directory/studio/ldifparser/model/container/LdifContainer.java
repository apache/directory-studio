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

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifLineBase;


/**
 * A base class for any LDIF container.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class LdifContainer implements LdifPart
{
    /** The contained Ldif Parts */
    protected List<LdifPart> ldifParts = new ArrayList<LdifPart>();


    protected LdifContainer( LdifPart part )
    {
        if ( part == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( part );
    }


    public final int getOffset()
    {
        return ldifParts.get( 0 ).getOffset();
    }


    public final int getLength()
    {
        LdifPart lastPart = getLastPart();

        return lastPart.getOffset() + lastPart.getLength() - getOffset();
    }


    public final void addInvalid( LdifInvalidPart invalid )
    {
        if ( invalid == null )
        {
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        }

        ldifParts.add( invalid );
    }


    public final LdifPart getLastPart()
    {
        return ldifParts.get( ldifParts.size() - 1 );
    }


    public final LdifPart[] getParts()
    {
        return ( LdifPart[] ) ldifParts.toArray( new LdifPart[ldifParts
            .size()] );
    }


    public final String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( getClass().getSimpleName() );
        sb.append( ":" ); //$NON-NLS-1$
        sb.append( LdifParserConstants.LINE_SEPARATOR );

        for ( LdifPart part : ldifParts )
        {
            sb.append( "    " ); //$NON-NLS-1$
            sb.append( part.toString() );
            sb.append( LdifParserConstants.LINE_SEPARATOR );
        }

        return sb.toString();
    }


    public final String toRawString()
    {
        StringBuilder sb = new StringBuilder();

        for ( LdifPart part : ldifParts )
        {
            sb.append( part.toRawString() );
        }

        return sb.toString();
    }


    public final String toFormattedString( LdifFormatParameters formatParameters )
    {
        StringBuilder sb = new StringBuilder();

        for ( LdifPart part : ldifParts )
        {
            sb.append( part.toFormattedString( formatParameters ) );
        }

        return sb.toString();
    }


    public abstract boolean isValid();


    /**
     * true if
     * <ul>
     * <li>at least one line
     * <li>no LdifInvalidPart
     * <li>all parts are valid
     * </ul>
     */
    protected boolean isAbstractValid()
    {
        if ( ldifParts.isEmpty() )
        {
            return false;
        }

        for ( LdifPart ldifPart : ldifParts )
        {
            if ( ( ldifPart instanceof LdifInvalidPart ) || ( !ldifPart.isValid() ) )
            {
                return false;
            }

            if ( ldifPart instanceof LdifLineBase )
            {
                return true;
            }
        }

        return false;
    }


    public String getInvalidString()
    {
        if ( ldifParts.isEmpty() )
        {
            return "Empty Container";
        }

        for ( LdifPart ldifPart : ldifParts )
        {
            if ( !ldifPart.isValid() )
            {
                return ldifPart.getInvalidString();
            }
        }

        return null;
    }


    public final void adjustOffset( int adjust )
    {
        for ( LdifPart ldifPart : ldifParts )
        {
            ldifPart.adjustOffset( adjust );
        }
    }
}
