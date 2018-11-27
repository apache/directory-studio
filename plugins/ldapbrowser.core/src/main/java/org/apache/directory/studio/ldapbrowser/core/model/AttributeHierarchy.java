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

package org.apache.directory.studio.ldapbrowser.core.model;


import java.util.Arrays;
import java.util.Iterator;


/**
 * An AttributeHierarchy is a container for an attribute including all its subtypes. 
 * <p>
 * Example:
 * <ul>
 * <li>attributeDescription is <code>name</code>
 * <li>attributes contains <code>cn:test1</code>, <code>sn:test2</code> and <code>givenName:test3</code>
 * </ul>
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeHierarchy implements Iterable<IAttribute>
{

    /** The entry */
    private IEntry entry;

    /** The attribute description */
    private String attributeDescription;

    /** The attributes */
    private IAttribute[] attributes;


    /**
     * Creates a new instance of AttributeHierarchy.
     *
     * @param entry the entry
     * @param attributeDescription the attribute description
     * @param attributes the attributes
     */
    public AttributeHierarchy( IEntry entry, String attributeDescription, IAttribute[] attributes )
    {
        if ( entry == null || attributeDescription == null || attributes == null || attributes.length < 1
            || attributes[0] == null )
        {
            throw new IllegalArgumentException( "Empty AttributeHierachie" ); //$NON-NLS-1$
        }
        this.entry = entry;
        this.attributeDescription = attributeDescription;
        this.attributes = attributes;
    }


    /**
     * Gets the attributes.
     *
     * @return the attributes
     */
    public IAttribute[] getAttributes()
    {
        return attributes;
    }


    /**
     * Checks whether the given attribute is contained 
     * in this attribute hierarchy. 
     *
     * @param attribute the attribute to check
     * @return true if the attribute is contained in this attribute hierarchy
     */
    public boolean contains( IAttribute attribute )
    {
        return Arrays.asList( attributes ).contains( attribute );
    }


    /**
     * Returns an iterator over the elements of this attribute hierarchy.
     *
     * @return an iterator over the elements of this attribute hierarchy
     */
    public Iterator<IAttribute> iterator()
    {
        return Arrays.asList( attributes ).iterator();
    }


    /**
     * Gets the first attribute.
     *
     * @return the first attribute
     */
    public IAttribute getAttribute()
    {
        return attributes[0];
    }


    /**
     * Gets the number of attributes.
     *
     * @return the number of attributes
     */
    public int size()
    {
        return attributes.length;
    }


    /**
     * Gets the number of all values in all attributes.
     *
     * @return the number of all values in all attributes
     */
    public int getValueSize()
    {
        int size = 0;
        for ( IAttribute attribute : attributes )
        {
            size += attribute.getValueSize();
        }
        return size;
    }


    /**
     * Gets the attribute description.
     *
     * @return the attribute description
     */
    public String getAttributeDescription()
    {
        return attributeDescription;
    }


    /**
     * Gets the entry.
     *
     * @return the entry
     */
    public IEntry getEntry()
    {
        return entry;
    }

}
