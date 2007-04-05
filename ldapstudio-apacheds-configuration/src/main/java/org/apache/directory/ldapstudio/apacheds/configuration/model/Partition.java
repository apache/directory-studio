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
package org.apache.directory.ldapstudio.apacheds.configuration.model;


/**
 * This class represents a Partition.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Partition
{
    /** The name of the partition */
    private String name;


    /**
     * Creates a new instance of Partition.
     *
     * @param name
     *      the name of the partition
     */
    public Partition( String name )
    {
        this.name = name;
    }


    /**
     * Gets the name of the partition.
     *
     * @return
     *      the name of the partition
     */
    public String getName()
    {
        return this.name;
    }


    /**
     * Sets the name of the partition.
     *
     * @param name
     *      the new name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }
}
