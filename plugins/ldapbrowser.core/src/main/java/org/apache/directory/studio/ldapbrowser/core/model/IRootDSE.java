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

package org.apache.directory.studio.ldapbrowser.core.model;


/**
 * An IRootDSE represents a Root DSE of an LDAP server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface IRootDSE extends IEntry
{
    /**
     * Gets the supported extensions.
     * 
     * @return the OIDs of the supported extensions
     */
    String[] getSupportedExtensions();


    /**
     * Gets the supported controls.
     * 
     * @return the OIDs of the the supported controls
     */
    String[] getSupportedControls();


    /**
     * Gets the supported features.
     * 
     * @return the OIDs of the the supported features
     */
    String[] getSupportedFeatures();


    /**
     * Checks if control is supported.
     * 
     * @param oid the OID
     * 
     * @return true, if control is supported
     */
    boolean isControlSupported( String oid );


    /**
     * Checks if feature is supported.
     * 
     * @param oid the OID
     * 
     * @return true, if feature is supported
     */
    boolean isFeatureSupported( String oid );
}
