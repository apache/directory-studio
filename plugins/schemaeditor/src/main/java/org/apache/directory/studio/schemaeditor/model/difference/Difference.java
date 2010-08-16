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
 * This interface defines a Difference between two objects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface Difference
{
    /**
     * Gets the source Object.
     *
     * @return
     *      the source Object
     */
    public Object getSource();


    /**
     * Sets the source Object.
     *
     * @param source
     *      the source Object
     */
    public void setSource( Object source );


    /**
     * Gets the destination Object.
     *
     * @return
     *      the destination Object
     */
    public Object getDestination();


    /**
     * Sets the destination Object.
     *
     * @param destination
     *      the destination Object
     */
    public void setDestination( Object destination );


    /**
     * Gets the type.
     *
     * @return
     *      the type
     */
    public DifferenceType getType();


    /**
     * Sets the type.
     *
     * @param type
     *      the type
     */
    public void setType( DifferenceType type );
}
