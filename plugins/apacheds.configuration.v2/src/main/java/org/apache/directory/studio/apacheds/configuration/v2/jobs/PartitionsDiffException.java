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
package org.apache.directory.studio.apacheds.configuration.v2.jobs;


/**
 * This exception can be raised when an error occurs when computing the diff
 * between two partitions.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PartitionsDiffException extends Exception
{
    private static final long serialVersionUID = 1L;


    /**
     * Constructs a new PartitionsDiffException with <code>null</code> as its detail message.
     */
    public PartitionsDiffException()
    {
        super();
    }


    /**
     * Constructs a new PartitionsDiffException with the specified detail message and cause.
     *
     * @param message the message
     * @param cause the cause
     */
    public PartitionsDiffException( String message, Throwable cause )
    {
        super( message, cause );
    }


    /**
     * Constructs a new PartitionsDiffException with the specified detail message.
     *
     * @param message the message
     */
    public PartitionsDiffException( String message )
    {
        super( message );
    }


    /**
     * Constructs a new exception with the specified cause and a detail message 
     * of <code>(cause==null ? null : cause.toString())</code>
     *
     * @param cause the cause
     */
    public PartitionsDiffException( Throwable cause )
    {
        super( cause );
    }
}
