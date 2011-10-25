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


/**
 * This class represents the AbstractDifference.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AbstractDifference implements Difference
{
    /** The source Object */
    private Object source;

    /** The destination Object */
    private Object destination;

    /** The type of difference */
    private DifferenceType type;


    /**
     * Creates a new instance of AbstractDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public AbstractDifference( Object source, Object destination )
    {
        this.source = source;
        this.destination = destination;
    }


    /**
     * Creates a new instance of AbstractDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param type
     *      the type
     */
    public AbstractDifference( Object source, Object destination, DifferenceType type )
    {
        this.source = source;
        this.destination = destination;
        this.type = type;
    }


    /**
     * {@inheritDoc}
     */
    public Object getDestination()
    {
        return destination;
    }


    /**
     * {@inheritDoc}
     */
    public void setDestination( Object destination )
    {
        this.destination = destination;
    }


    /**
     * {@inheritDoc}
     */
    public Object getSource()
    {
        return source;
    }


    /**
     * {@inheritDoc}
     */
    public void setSource( Object source )
    {
        this.source = source;
    }


    /**
     * {@inheritDoc}
     */
    public DifferenceType getType()
    {
        return type;
    }


    /**
     * {@inheritDoc}
     */
    public void setType( DifferenceType type )
    {
        this.type = type;
    }
}
