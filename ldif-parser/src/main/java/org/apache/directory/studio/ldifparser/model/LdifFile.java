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

package org.apache.directory.studio.ldifparser.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;


public class LdifFile implements Serializable
{

    private static final long serialVersionUID = 846864138240517008L;

    private List containerList;


    public LdifFile()
    {
        super();
        this.containerList = new ArrayList( 1 );
    }


    public boolean isContentType()
    {
        for ( Iterator it = this.containerList.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifRecord )
            {
                return o instanceof LdifContentRecord;
            }
        }
        return false;
    }


    public boolean isChangeType()
    {
        for ( Iterator it = this.containerList.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifRecord )
            {
                return o instanceof LdifChangeRecord;
            }
        }
        return false;
    }


    public void addContainer( LdifContainer container )
    {
        this.containerList.add( container );
    }


    /**
     * 
     * @return all container, includes version, comments, records and
     *         unknown
     */
    public LdifContainer[] getContainers()
    {
        return ( LdifContainer[] ) this.containerList.toArray( new LdifContainer[this.containerList.size()] );
    }


    /**
     * 
     * @return only records (even invalid), no version, comments, and
     *         unknown
     */
    public LdifRecord[] getRecords()
    {
        List l = new ArrayList();
        for ( Iterator it = this.containerList.iterator(); it.hasNext(); )
        {
            Object o = it.next();
            if ( o instanceof LdifRecord )
            {
                l.add( o );
            }
        }
        return ( LdifRecord[] ) l.toArray( new LdifRecord[l.size()] );
    }


    /**
     * 
     * @return the last container or null
     */
    public LdifContainer getLastContainer()
    {
        if ( this.containerList.isEmpty() )
        {
            return null;
        }
        else
        {
            return ( LdifContainer ) this.containerList.get( this.containerList.size() - 1 );
        }
    }


    public String toRawString()
    {
        StringBuffer sb = new StringBuffer();

        LdifContainer[] containers = this.getContainers();
        for ( int i = 0; i < containers.length; i++ )
        {
            sb.append( containers[i].toRawString() );
        }

        return sb.toString();
    }


    public String toFormattedString( LdifFormatParameters formatParameters )
    {
        StringBuffer sb = new StringBuffer();

        LdifContainer[] containers = this.getContainers();
        for ( int i = 0; i < containers.length; i++ )
        {
            sb.append( containers[i].toFormattedString( formatParameters ) );
        }

        return sb.toString();
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        LdifContainer[] containers = this.getContainers();
        for ( int i = 0; i < containers.length; i++ )
        {
            sb.append( containers[i].toString() );
        }

        return sb.toString();
    }


    public static LdifContainer getContainer( LdifFile model, int offset )
    {
        if ( model == null || offset < 0 )
            return null;

        LdifContainer container = null;
        LdifContainer[] containers = model.getContainers();
        if ( containers.length > 0 )
        {
            for ( int i = 0; i < containers.length; i++ )
            {
                if ( containers[i].getOffset() <= offset
                    && offset < containers[i].getOffset() + containers[i].getLength() )
                {
                    container = containers[i];
                    break;
                }
            }
        }
        return container;
    }


    public static LdifModSpec getInnerContainer( LdifContainer container, int offset )
    {
        if ( container == null || offset < container.getOffset()
            || offset > container.getOffset() + container.getLength() )
            return null;

        LdifModSpec innerContainer = null;
        LdifPart[] parts = container.getParts();
        if ( parts.length > 0 )
        {
            int partIndex = -1;

            for ( int i = 0; i < parts.length; i++ )
            {
                int start = parts[i].getOffset();
                int end = parts[i].getOffset() + parts[i].getLength();
                if ( start <= offset && offset < end )
                {
                    partIndex = i;
                    break;
                }
            }

            if ( partIndex > -1 )
            {
                if ( parts[partIndex] instanceof LdifModSpec )
                {
                    innerContainer = ( LdifModSpec ) parts[partIndex];
                }
            }
        }
        return innerContainer;
    }


    public static LdifContainer[] getContainers( LdifFile model, int offset, int length )
    {
        if ( model == null || offset < 0 )
            return null;

        ArrayList containerList = new ArrayList();

        LdifContainer[] containers = model.getContainers();
        if ( containers.length > 0 )
        {
            for ( int i = 0; i < containers.length; i++ )
            {
                if ( offset < containers[i].getOffset() + containers[i].getLength()
                    && offset + length > containers[i].getOffset() )
                {
                    containerList.add( containers[i] );
                }
            }
        }

        return ( LdifContainer[] ) containerList.toArray( new LdifContainer[containerList.size()] );
    }


    public static LdifPart[] getParts( LdifFile model, int offset, int length )
    {
        if ( model == null || offset < 0 )
            return null;

        LdifContainer[] containers = model.getContainers();
        return getParts( containers, offset, length );

    }


    public static LdifPart[] getParts( LdifContainer[] containers, int offset, int length )
    {
        if ( containers == null || offset < 0 )
            return null;

        ArrayList partList = new ArrayList();

        for ( int i = 0; i < containers.length; i++ )
        {
            if ( offset < containers[i].getOffset() + containers[i].getLength()
                && offset + length >= containers[i].getOffset() )
            {
                LdifPart[] parts = containers[i].getParts();
                if ( parts.length > 0 )
                {
                    for ( int p = 0; p < parts.length; p++ )
                    {
                        if ( offset < parts[p].getOffset() + parts[p].getLength()
                            && offset + length >= parts[p].getOffset() )
                        {
                            LdifPart part = parts[p];

                            if ( part instanceof LdifModSpec )
                            {
                                LdifModSpec spec = ( LdifModSpec ) part;
                                partList.addAll( Arrays.asList( getParts( new LdifContainer[]
                                    { spec }, offset, length ) ) );
                            }
                            else
                            {
                                if ( part instanceof LdifInvalidPart && p > 0 )
                                {
                                    part = parts[p - 1];
                                }

                                partList.add( part );
                            }
                        }
                    }
                }
            }
        }

        return ( org.apache.directory.studio.ldifparser.model.LdifPart[] ) partList.toArray( new LdifPart[partList
            .size()] );
    }


    public static LdifPart getContainerContent( LdifContainer container, int offset )
    {
        if ( container == null || offset < container.getOffset()
            || offset > container.getOffset() + container.getLength() )
            return null;

        LdifPart part = null;
        LdifPart[] parts = container.getParts();
        if ( parts.length > 0 )
        {
            int partIndex = -1;

            for ( int i = 0; i < parts.length; i++ )
            {
                int start = parts[i].getOffset();
                int end = parts[i].getOffset() + parts[i].getLength();
                if ( start <= offset && offset < end )
                {
                    partIndex = i;
                    break;
                }
            }

            if ( partIndex > -1 )
            {
                part = parts[partIndex];
                if ( part instanceof LdifModSpec )
                {
                    part = getContainerContent( ( LdifModSpec ) part, offset );
                }
                // if(part instanceof LdifInvalidPart && partIndex > 0) {
                // partIndex--;
                // part = parts[partIndex];
                // }
            }
        }
        return part;
    }


    // public static LdifPart getPart(LdifContainer container, int offset) {
    // if(container == null || offset < 0)
    // return null;
    //		
    // LdifPart part = null;
    // LdifPart[] parts = container.getParts();
    // if(parts.length > 0) {
    // int partIndex = -1;
    //			
    // for (int i=0; i<parts.length; i++) {
    // int start = parts[i].getOffset();
    // int end = parts[i].getOffset()+parts[i].getLength();
    // if(start <= offset && offset < end) {
    // partIndex = i;
    // break;
    // }
    // }
    //			
    // if(partIndex > -1) {
    // part = parts[partIndex];
    //
    // if(part instanceof LdifUnknownPart && partIndex > 0) {
    // partIndex--;
    // part = parts[partIndex];
    // }
    //				
    // if(part instanceof LdifContainer) {
    // part = getPart((LdifContainer)part, offset);
    // }
    // }
    // }
    // return part;
    // }

    public void replace( LdifContainer[] oldContainers, LdifContainer[] newContainers )
    {

        // find index
        int index = 0;
        if ( oldContainers.length > 0 )
        {
            index = this.containerList.indexOf( oldContainers[0] );
        }

        // remove old containers
        int removeLength = 0;
        int removeOffset = 0;
        if ( oldContainers.length > 0 )
        {
            removeOffset = oldContainers[0].getOffset();
            for ( int i = 0; i < oldContainers.length; i++ )
            {
                this.containerList.remove( index );
                removeLength += oldContainers[i].getLength();
            }
        }

        // add new containers
        int insertLength = 0;
        for ( int i = 0; i < newContainers.length; i++ )
        {
            newContainers[i].adjustOffset( removeOffset );
            insertLength += newContainers[i].getLength();
            this.containerList.add( index + i, newContainers[i] );
        }

        // adjust offset of folling containers
        int adjust = insertLength - removeLength;
        for ( int i = index + newContainers.length; i < this.containerList.size(); i++ )
        {
            LdifContainer container = ( LdifContainer ) this.containerList.get( i );
            container.adjustOffset( adjust );
        }

    }

}
