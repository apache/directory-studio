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
package org.apache.directory.studio.apacheds.configuration.model.v152;


import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;


/**
 * This class represents a Partition.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Partition
{
    /** The ID of the partition */
    private String id;

    /** The cache size of the partition */
    private int cacheSize;

    /** The suffix of the partition */
    private String suffix;

    /** The Enable Optimizer flag */
    private boolean enableOptimizer;

    /** The Synchronization On Write flag */
    private boolean synchronizationOnWrite;

    /** The Context Entry */
    private Attributes contextEntry;

    /** The indexed attributes */
    private List<IndexedAttribute> indexedAttributes;

    /** The System Partition flag */
    private boolean systemPartition = false;


    /**
     * Creates a new instance of Partition.
     */
    public Partition()
    {
        indexedAttributes = new ArrayList<IndexedAttribute>();
        contextEntry = new BasicAttributes( true );
    }


    /**
     * Creates a new instance of Partition.
     *
     * @param id
     *      the id of the partition
     */
    public Partition( String id )
    {
        indexedAttributes = new ArrayList<IndexedAttribute>();
        contextEntry = new BasicAttributes( true );
        this.id = id;
    }


    /**
     * Gets the ID of the partition.
     *
     * @return
     *      the ID of the partition
     */
    public String getId()
    {
        return this.id;
    }


    /**
     * Sets the ID of the partition.
     *
     * @param id
     *      the new ID to set
     */
    public void setId( String id )
    {
        this.id = id;
    }


    /**
     * Gets the cache size.
     *
     * @return
     *      the cache size
     */
    public int getCacheSize()
    {
        return cacheSize;
    }


    /**
     * Sets the cache size.
     *
     * @param cacheSize
     *      the new cache size
     */
    public void setCacheSize( int cacheSize )
    {
        this.cacheSize = cacheSize;
    }


    /**
     * Gets the Context Entry.
     *
     * @return
     *      the Content Entry
     */
    public Attributes getContextEntry()
    {
        return contextEntry;
    }


    /**
     * Sets the Context Entry
     *
     * @param contextEntry
     *      the new Context Entry
     */
    public void setContextEntry( Attributes contextEntry )
    {
        this.contextEntry = contextEntry;
    }


    /**
     * Gets the Enable Optimizer flag.
     *
     * @return
     *      the Enable Optimizer flag
     */
    public boolean isEnableOptimizer()
    {
        return enableOptimizer;
    }


    /**
     * Sets the Enable Optimizer flag.
     *
     * @param enableOptimizer
     *      the new value for the Enable Optimizer flag
     */
    public void setEnableOptimizer( boolean enableOptimizer )
    {
        this.enableOptimizer = enableOptimizer;
    }


    /**
     * Get the Indexed Attributes List.
     *
     * @return
     *      the Indexed Attributes List
     */
    public List<IndexedAttribute> getIndexedAttributes()
    {
        return indexedAttributes;
    }


    /**
     * Set the Indexed Attributes List.
     *
     * @param indexedAttributes
     *      the new Indexed Attributes List
     */
    public void setIndexedAttributes( List<IndexedAttribute> indexedAttributes )
    {
        this.indexedAttributes = indexedAttributes;
    }


    /**
     * Adds an Indexed Attribute.
     *
     * @param indexedAttribute
     *      the Indexed Attribute to add
     * @return
     *      true (as per the general contract of the Collection.add method).
     */
    public boolean addIndexedAttribute( IndexedAttribute indexedAttribute )
    {
        return indexedAttributes.add( indexedAttribute );
    }


    /**
     * Removes a Indexed Attribute.
     *
     * @param indexedAttribute
     *      the Indexed Attribute to remove
     * @return
     *      true if this list contained the specified element.
     */
    public boolean removeIndexedAttribute( IndexedAttribute indexedAttribute )
    {
        return indexedAttributes.remove( indexedAttribute );
    }


    /**
     * Gets the suffix.
     *
     * @return
     *      the suffix
     */
    public String getSuffix()
    {
        return suffix;
    }


    /**
     * Sets the suffix.
     *
     * @param suffix
     *      the new suffix
     */
    public void setSuffix( String suffix )
    {
        this.suffix = suffix;
    }


    /**
     * Gets the Synchronization On Write flag.
     *
     * @return
     *      the Synchronization On Write flag
     */
    public boolean isSynchronizationOnWrite()
    {
        return synchronizationOnWrite;
    }


    /**
     * Sets the Synchronization On Write flag.
     *
     * @param synchronizationOnWrite
     *      the Synchronization On Write flag
     */
    public void setSynchronizationOnWrite( boolean synchronizationOnWrite )
    {
        this.synchronizationOnWrite = synchronizationOnWrite;
    }


    /**
     * Returns the System Partition flag.
     *
     * @return
     *      true if the partition is the System Partition
     */
    public boolean isSystemPartition()
    {
        return systemPartition;
    }


    /**
     * Sets the System Partition flag.
     *
     * @param systemPartition
     *      the System Partition flag
     */
    public void setSystemPartition( boolean systemPartition )
    {
        this.systemPartition = systemPartition;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return id;
    }
}
