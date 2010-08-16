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
package org.apache.directory.studio.schemaeditor.model.difference;


import java.util.ArrayList;
import java.util.List;


/**
 * This class represents an attribute type difference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeDifference extends AbstractDifference
{
    /** The differences */
    private List<PropertyDifference> differences = new ArrayList<PropertyDifference>();


    /**
     * Creates a new instance of AttributeTypeDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param type
     *      the type
     */
    public AttributeTypeDifference( Object source, Object destination, DifferenceType type )
    {
        super( source, destination, type );
    }


    /**
     * Creates a new instance of AttributeTypeDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public AttributeTypeDifference( Object source, Object destination )
    {
        super( source, destination );
    }


    /**
     * Gets the property differences.
     *
     * @return
     *      the property differences
     */
    public List<PropertyDifference> getDifferences()
    {
        return differences;
    }


    /**
     * Adds a difference.
     *
     * @param difference
     *      the difference
     */
    public void addDifference( PropertyDifference difference )
    {
        differences.add( difference );
    }


    /**
     * Adds differences.
     *
     * @param differences
     *      the differences
     */
    public void addDifferences( List<PropertyDifference> differences )
    {
        this.differences.addAll( differences );
    }


    /**
     * Removes a difference.
     *
     * @param difference
     *      the difference
     */
    public void removeDifference( PropertyDifference difference )
    {
        differences.remove( difference );
    }
}
