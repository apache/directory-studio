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
 * This class represents a schema difference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaDifference extends AbstractDifference
{
    /** The attribute types differences*/
    private List<AttributeTypeDifference> attributeTypesDifferences = new ArrayList<AttributeTypeDifference>();

    /** The object classes differences */
    private List<ObjectClassDifference> objectClassesDifferences = new ArrayList<ObjectClassDifference>();


    /**
     * Creates a new instance of AbstractSchemaDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param type
     *      the type
     */
    public SchemaDifference( Object source, Object destination, DifferenceType type )
    {
        super( source, destination, type );
    }


    /**
     * Creates a new instance of AbstractSchemaDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public SchemaDifference( Object source, Object destination )
    {
        super( source, destination );
    }


    /**
     * Gets the attribute types differences.
     *
     * @return
     *      the attribute types differences
     */
    public List<AttributeTypeDifference> getAttributeTypesDifferences()
    {
        return attributeTypesDifferences;
    }


    /**
     * Adds an attribute type difference.
     *
     * @param difference
     *      the attribute type difference
     */
    public void addAttributeTypeDifference( AttributeTypeDifference difference )
    {
        attributeTypesDifferences.add( difference );
    }


    /**
     * Removes an attribute type difference.
     *
     * @param difference
     *      the attribute type difference
     */
    public void removeAttributeTypeDifference( AttributeTypeDifference difference )
    {
        attributeTypesDifferences.remove( difference );
    }


    /**
     * Gets the object classes differences.
     *
     * @return
     *      the object classes differences
     */
    public List<ObjectClassDifference> getObjectClassesDifferences()
    {
        return objectClassesDifferences;
    }


    /**
     * Adds an object class difference.
     *
     * @param difference
     *      the object class difference
     */
    public void addObjectClassDifference( ObjectClassDifference difference )
    {
        objectClassesDifferences.add( difference );
    }


    /**
     * Removes an object class difference.
     *
     * @param difference
     *      the object class difference
     */
    public void removeObjectClassDifference( ObjectClassDifference difference )
    {
        objectClassesDifferences.remove( difference );
    }
}
