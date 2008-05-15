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


import java.io.InputStream;
import java.io.Reader;


/**
 * This interface defines an object that implements a parser and a writer for 
 * the 'server.xml' file. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface ServerXmlIO
{
    /**
     * Indicates whether or not the given input stream is a valid 'server.xml' 
     * file.
     *
     * @param is
     *      the input stream to use
     * @return
     *      <code>true</code> if the given input stream is a valid 
     *      'server.xml' file, <code>false</code> if not
     */
    public boolean isValid( InputStream is );


    /**
     * Indicates whether or not the given reader is a valid 'server.xml' 
     * file.
     *
     * @param reader
     *      the reader to use
     * @return
     *      <code>true</code> if the given reader is a valid 
     *      'server.xml' file, <code>false</code> if not
     */
    public boolean isValid( Reader reader );


    /**
     * Parses the given input and returns the corresponding server configuration.
     *
     * @param is
     *      the input stream to use
     * @return
     *      the corresponding server configuration
     * @throws ServerXmlIOException
     *      if an error occurs when parsing the 
     */
    public ServerConfiguration parse( InputStream is ) throws ServerXmlIOException;


    /**
     * Converts the given server configuration to its corresponding XML representation.
     *
     * @param serverConfiguration
     *      the server configuration
     * @return
     *      the corresponding XML representation
     */
    public String toXml( ServerConfiguration serverConfiguration );
}
