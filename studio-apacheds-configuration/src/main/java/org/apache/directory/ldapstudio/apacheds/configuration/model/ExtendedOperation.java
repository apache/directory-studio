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
 * This class represents an Extended Operation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ExtendedOperation
{
    /** The class of the partition */
    private String classType;


    /**
     * Creates a new instance of Partition.
     *
     * @param classType
     *      the classType of the partition
     */
    public ExtendedOperation( String classType )
    {
        this.classType = classType;
    }


    /**
     * Gets the class type of the partition.
     *
     * @return
     *      the class type of the partition
     */
    public String getClassType()
    {
        return this.classType;
    }


    /**
     * Sets the class type of the partition.
     *
     * @param classType
     *      the new class type to set
     */
    public void setClassType( String classType )
    {
        this.classType = classType;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return classType;
    }
}
