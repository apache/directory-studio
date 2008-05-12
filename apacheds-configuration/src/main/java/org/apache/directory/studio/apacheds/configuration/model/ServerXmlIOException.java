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
package org.apache.directory.studio.apacheds.configuration.model;


/**
 * This class represents the Server Configuration Parser Exception, that can be thrown
 * when an error is detected when reading the Server Configuration file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ServerXmlIOException extends Exception
{
    /** The Serial Version UID */
    private static final long serialVersionUID = 7274953743060751973L;


    /**
     * Creates a new instance of ServerConfigurationParserException.
     *
     * @param message
     *      the detail message (which is saved for later retrieval by the 
     *      getMessage() method).
     */
    public ServerXmlIOException( String message )
    {
        super( message );
    }


    /**
     * Creates a new instance of ServerConfigurationParserException.
     *
     * @param message
     *      the detail message (which is saved for later retrieval by the 
     *      getMessage() method).
     * @param cause
     *      the cause (which is saved for later retrieval by the getCause() 
     *      method). (A null value is permitted, and indicates that the cause 
     *      is nonexistent or unknown.)
     */
    public ServerXmlIOException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
