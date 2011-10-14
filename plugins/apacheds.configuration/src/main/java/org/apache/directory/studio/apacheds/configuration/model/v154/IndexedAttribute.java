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
package org.apache.directory.studio.apacheds.configuration.model.v154;


/**
 * This class represents an Indexed Attribute.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class IndexedAttribute
{
    /** The attribute id */
    private String attributeId;

    /** The cache size */
    private int cacheSize;


    /**
     * Creates a new instance of IndexedAttribute.
     *
     * @param attributeId
     *      the attribute id
     * @param cacheSize
     *      the cache size
     */
    public IndexedAttribute( String attributeId, int cacheSize )
    {
        this.attributeId = attributeId;
        this.cacheSize = cacheSize;
    }


    /**
     * Gets the attribute id.
     *
     * @return
     *      the attribute id
     */
    public String getAttributeId()
    {
        return attributeId;
    }


    /**
     * Sets the attribute id.
     *
     * @param attributeId
     *      the new attribute id
     */
    public void setAttributeId( String attributeId )
    {
        this.attributeId = attributeId;
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
     * Gets the cache size.
     *
     * @param cacheSize
     *      the new cache size
     */
    public void setCacheSize( int cacheSize )
    {
        this.cacheSize = cacheSize;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return attributeId + " [" + cacheSize + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
