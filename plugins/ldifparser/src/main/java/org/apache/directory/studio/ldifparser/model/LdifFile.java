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
import java.util.List;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;


/**
 * A LDIF file, as we manipulate it in Studio.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifFile implements Serializable
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 846864138240517008L;

    /** The list of container constituting this LDIF file */
    private List<LdifContainer> containerList = new ArrayList<LdifContainer>();


    /**
     * Create an instance of a LdifFile.
     */
    public LdifFile()
    {
    }


    /**
     * Tells if the LDIF file is a Content LDIF (there will be no changes)
     *
     * @return true if the LDIF file does not contain any change
     */
    public boolean isContentType()
    {
        for ( LdifContainer container : containerList )
        {
            if ( container instanceof LdifRecord )
            {
                return container instanceof LdifContentRecord;
            }
        }

        return false;
    }


    /**
     * Tells if the LDIF file is a Change LDIF (there will be no changes)
     *
     * @return true if the LDIF file is a Change LDIF
     */
    public boolean isChangeType()
    {
        for ( LdifContainer container : containerList )
        {
            if ( container instanceof LdifRecord )
            {
                return container instanceof LdifChangeRecord;
            }
        }

        return false;
    }


    public void addContainer( LdifContainer container )
    {
        containerList.add( container );
    }


    /**
     * 
     * @return all container, includes version, comments, records and
     *         unknown
     */
    public LdifContainer[] getContainers()
    {
        return containerList.toArray( new LdifContainer[containerList.size()] );
    }


    /**
     * 
     * @return all container, includes version, comments, records and
     *         unknown
     */
    private List<LdifContainer> getContainerList()
    {
        return containerList;
    }


    /**
     * @return only records (even invalid), no version, comments, and
     *         unknown
     */
    public LdifRecord[] getRecords()
    {
        List<LdifRecord> recordList = new ArrayList<LdifRecord>();

        for ( LdifContainer container : containerList )
        {
            if ( container instanceof LdifRecord )
            {
                recordList.add( ( LdifRecord ) container );
            }
        }

        return recordList.toArray( new LdifRecord[recordList.size()] );
    }


    /**
     * 
     * @return the last container or null
     */
    public LdifContainer getLastContainer()
    {
        if ( containerList.isEmpty() )
        {
            return null;
        }
        else
        {
            return containerList.get( containerList.size() - 1 );
        }
    }


    public String toRawString()
    {
        StringBuilder sb = new StringBuilder();

        for ( LdifContainer container : containerList )
        {
            sb.append( container.toRawString() );
        }

        return sb.toString();
    }


    public String toFormattedString( LdifFormatParameters formatParameters )
    {
        StringBuilder sb = new StringBuilder();

        for ( LdifContainer ldifContainer : containerList )
        {
            sb.append( ldifContainer.toFormattedString( formatParameters ) );
        }

        return sb.toString();
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for ( LdifContainer ldifContainer : containerList )
        {
            sb.append( ldifContainer );
        }

        return sb.toString();
    }


    /**
     * Retrieve the container at the given offset
     *
     * @param model The Ldif file containing the containers
     * @param offset The position in the file
     * @return The container if we found one
     */
    public static LdifContainer getContainer( LdifFile model, int offset )
    {
        if ( ( model == null ) || ( offset < 0 ) )
        {
            return null;
        }

        List<LdifContainer> containers = model.getContainerList();

        if ( containers.size() > 0 )
        {
            for ( LdifContainer ldifContainer : containers )
            {
                if ( ( ldifContainer.getOffset() <= offset ) &&
                    ( offset < ldifContainer.getOffset() + ldifContainer.getLength() ) )
                {
                    return ldifContainer;
                }
            }
        }

        return null;
    }


    public static LdifModSpec getInnerContainer( LdifContainer container, int offset )
    {
        if ( ( container == null ) ||
            ( offset < container.getOffset() ) ||
            ( offset > container.getOffset() + container.getLength() ) )
        {
            return null;
        }

        LdifModSpec innerContainer = null;
        LdifPart[] parts = container.getParts();

        if ( parts.length > 0 )
        {
            for ( LdifPart ldifPart : parts )
            {
                int start = ldifPart.getOffset();
                int end = ldifPart.getOffset() + ldifPart.getLength();

                if ( ( start <= offset ) && ( offset < end ) && ( ldifPart instanceof LdifModSpec ) )
                {
                    innerContainer = ( LdifModSpec ) ldifPart;
                    break;
                }
            }
        }

        return innerContainer;
    }


    public static LdifContainer[] getContainers( LdifFile model, int offset, int length )
    {
        if ( ( model == null ) || ( offset < 0 ) )
        {
            return null;
        }

        List<LdifContainer> containerList = new ArrayList<LdifContainer>();
        List<LdifContainer> containers = model.getContainerList();

        if ( containers.size() > 0 )
        {
            for ( LdifContainer container : containers )
            {
                int containerOffset = container.getOffset();

                if ( ( offset < containerOffset + container.getLength() ) &&
                    ( offset + length > containerOffset ) )
                {
                    containerList.add( container );
                }
            }
        }

        return containerList.toArray( new LdifContainer[containerList.size()] );
    }


    public static LdifPart[] getParts( LdifFile model, int offset, int length )
    {
        if ( ( model == null ) || ( offset < 0 ) )
        {
            return null;
        }

        LdifContainer[] containers = model.getContainers();

        return getParts( containers, offset, length );

    }


    public static LdifPart[] getParts( LdifContainer[] containers, int offset, int length )
    {
        if ( ( containers == null ) || ( offset < 0 ) )
        {
            return null;
        }

        List<LdifPart> partList = new ArrayList<LdifPart>();

        for ( LdifContainer ldifContainer : containers )
        {
            int ldifContainerOffset = ldifContainer.getOffset();

            if ( ( offset < ldifContainerOffset + ldifContainer.getLength() )
                && ( offset + length >= ldifContainerOffset ) )
            {
                LdifPart[] ldifParts = ldifContainer.getParts();
                LdifPart previousLdifPart = null;

                for ( LdifPart ldifPart : ldifParts )
                {
                    int ldifPartOffset = ldifPart.getOffset();

                    if ( ( offset < ldifPartOffset + ldifPart.getLength() ) && ( offset + length >= ldifPartOffset ) )
                    {
                        if ( ldifPart instanceof LdifModSpec )
                        {
                            LdifModSpec spec = ( LdifModSpec ) ldifPart;
                            partList.addAll( Arrays.asList( getParts( new LdifContainer[]
                                { spec }, offset, length ) ) );
                        }
                        else
                        {
                            if ( ( ldifPart instanceof LdifInvalidPart ) && ( previousLdifPart != null ) )
                            {
                                ldifPart = previousLdifPart;
                            }

                            partList.add( ldifPart );
                        }

                        previousLdifPart = ldifPart;
                    }

                }
            }
        }

        return partList.toArray( new LdifPart[partList.size()] );
    }


    public static LdifPart getContainerContent( LdifContainer container, int offset )
    {
        int containerOffset = container.getOffset();

        if ( ( container == null ) || ( offset < containerOffset ) ||
            ( offset > containerOffset + container.getLength() ) )
        {
            return null;
        }

        LdifPart part = null;
        LdifPart[] parts = container.getParts();

        if ( parts.length > 0 )
        {
            for ( LdifPart ldifPart : parts )
            {
                int start = ldifPart.getOffset();
                int end = ldifPart.getOffset() + ldifPart.getLength();

                if ( ( start <= offset ) && ( offset < end ) )
                {
                    if ( ldifPart instanceof LdifModSpec )
                    {
                        part = getContainerContent( ( LdifModSpec ) ldifPart, offset );
                    }

                    break;
                }
            }
        }

        return part;
    }


    public void replace( LdifContainer[] oldContainers, LdifContainer[] newContainers )
    {
        // find index
        int index = 0;

        if ( oldContainers.length > 0 )
        {
            index = containerList.indexOf( oldContainers[0] );
        }

        // remove old containers
        int removeLength = 0;
        int removeOffset = 0;

        if ( oldContainers.length > 0 )
        {
            removeOffset = oldContainers[0].getOffset();

            for ( int i = 0; i < oldContainers.length; i++ )
            {
                containerList.remove( index );
                removeLength += oldContainers[i].getLength();
            }
        }

        // add new containers
        int insertLength = 0;

        for ( int i = 0; i < newContainers.length; i++ )
        {
            newContainers[i].adjustOffset( removeOffset );
            insertLength += newContainers[i].getLength();
            containerList.add( index + i, newContainers[i] );
        }

        // adjust offset of following containers
        int adjust = insertLength - removeLength;

        for ( int i = index + newContainers.length; i < containerList.size(); i++ )
        {
            LdifContainer container = containerList.get( i );
            container.adjustOffset( adjust );
        }
    }
}
