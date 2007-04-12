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
 * This class represents an Interceptor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Interceptor
{
    /** The name of the interceptor */
    private String name;

    /** The class of the class of the interceptor */
    private String classType;


    /**
     * Creates a new instance of Interceptor.
     *
     * @param name
     *      the name of the interceptor
     */
    public Interceptor( String name )
    {
        this.name = name;
    }


    /**
     * Gets the name of the interceptor.
     *
     * @return
     *      the name of the interceptor
     */
    public String getName()
    {
        return this.name;
    }


    /**
     * Sets the name of the interceptor.
     *
     * @param name
     *      the new name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the class type of the interceptor.
     *
     * @return
     *      the class type of the interceptor
     */
    public String getClassType()
    {
        return classType;
    }


    /**
     * Sets the class type of the interceptor.
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
        return name;
    }
}
