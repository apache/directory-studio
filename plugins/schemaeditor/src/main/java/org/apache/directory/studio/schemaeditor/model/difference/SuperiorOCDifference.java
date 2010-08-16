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
 * This class represents a difference of added superior object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SuperiorOCDifference extends AbstractPropertyDifference
{
    /**
     * Creates a new instance of SuperiorOCDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     * @param type
     *      the type
     */
    public SuperiorOCDifference( Object source, Object destination, DifferenceType type )
    {
        super( source, destination, type );
    }


    /**
     * Creates a new instance of SuperiorOCDifference.
     *
     * @param source
     *      the source Object
     * @param destination
     *      the destination Object
     */
    public SuperiorOCDifference( Object source, Object destination )
    {
        super( source, destination );
    }
}
