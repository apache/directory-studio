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

package org.apache.directory.studio.schemas.model;


/**
 * Umbrella for all the schema i/o related exceptions
 *
 */
public class SchemaCreationException extends Exception
{
    private static final long serialVersionUID = 1L;
    private Exception originatingException;


    /**
     * Creates a new schema i/o related exception
     * @param message the exception's message
     * @param originatingException the originating exception
     */
    public SchemaCreationException( String message, Exception originatingException )
    {
        super( message );
        this.originatingException = originatingException;
    }


    /**
     * @return the originating exception
     */
    public Exception getOriginatingException()
    {
        return originatingException;
    }

}
