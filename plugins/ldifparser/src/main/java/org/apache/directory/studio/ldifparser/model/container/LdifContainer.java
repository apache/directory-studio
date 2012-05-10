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


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.model.LdifInvalidPart;
import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.lines.LdifLineBase;


public abstract class LdifContainer implements Serializable
{

    protected List parts;


    protected LdifContainer()
    {
    }


    protected LdifContainer( LdifPart part )
    {
        this.parts = new ArrayList( 1 );
        if ( part == null )
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        this.parts.add( part );
    }


    public final int getOffset()
    {
        return ( ( LdifPart ) this.parts.get( 0 ) ).getOffset();
    }


    public final int getLength()
    {
        LdifPart lastPart = this.getLastPart();
        return lastPart.getOffset() + lastPart.getLength() - getOffset();
    }


    public final void addInvalid( LdifInvalidPart invalid )
    {
        if ( invalid == null )
            throw new IllegalArgumentException( "null argument" ); //$NON-NLS-1$
        this.parts.add( invalid );
    }


    public final LdifPart getLastPart()
    {
        return ( LdifPart ) parts.get( parts.size() - 1 );
    }


    public final LdifPart[] getParts()
    {
        return ( org.apache.directory.studio.ldifparser.model.LdifPart[] ) this.parts.toArray( new LdifPart[parts
            .size()] );
    }


    public final String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( getClass().getName() );
        sb.append( ":" ); //$NON-NLS-1$
        sb.append( LdifParserConstants.LINE_SEPARATOR );

        LdifPart[] parts = this.getParts();
        for ( int i = 0; i < parts.length; i++ )
        {
            sb.append( "    " ); //$NON-NLS-1$
            sb.append( parts[i].toString() );
            sb.append( LdifParserConstants.LINE_SEPARATOR );
        }

        return sb.toString();
    }


    public final String toRawString()
    {
        StringBuffer sb = new StringBuffer();

        LdifPart[] parts = this.getParts();
        for ( int i = 0; i < parts.length; i++ )
        {
            sb.append( parts[i].toRawString() );
        }

        return sb.toString();
    }


    public final String toFormattedString( LdifFormatParameters formatParameters )
    {
        StringBuffer sb = new StringBuffer();

        LdifPart[] parts = this.getParts();
        for ( int i = 0; i < parts.length; i++ )
        {
            sb.append( parts[i].toFormattedString( formatParameters ) );
        }

        return sb.toString();
    }


    public abstract boolean isValid();


    /**
     * true if
     * <ul>
     * <li>at least one line
     * <li>no LdifUnknownPart
     * <li>all parts are valid
     * </ul>
     */
    protected boolean isAbstractValid()
    {
        if ( this.parts.isEmpty() )
            return false;

        boolean containsLine = false;
        LdifPart[] parts = this.getParts();
        for ( int i = 0; i < parts.length; i++ )
        {
            if ( parts[i] instanceof LdifInvalidPart )
            {
                return false;
            }
            if ( !parts[i].isValid() )
            {
                return false;
            }
            if ( parts[i] instanceof LdifLineBase )
            {
                containsLine = true;
            }
        }
        return containsLine;
    }


    public String getInvalidString()
    {
        if ( this.parts.isEmpty() )
            return "Empty Container";

        LdifPart[] parts = this.getParts();
        for ( int i = 0; i < parts.length; i++ )
        {
            if ( !parts[i].isValid() )
            {
                return parts[i].getInvalidString();
            }
        }

        return null;
    }


    public final void adjustOffset( int adjust )
    {
        for ( Iterator it = this.parts.iterator(); it.hasNext(); )
        {
            LdifPart part = ( LdifPart ) it.next();
            part.adjustOffset( adjust );
        }
    }

}
