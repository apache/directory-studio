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
package org.apache.directory.studio.connection.core;


import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;


/**
 * Callback interface to log modifications
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IJndiLogger
{

    /**
     * Logs a changetype:add.
     * 
     * @param connection the connection
     * @param dn the DN
     * @param attributes the attributes
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     */
    public void logChangetypeAdd( Connection connection, final String dn, final Attributes attributes, final Control[] controls, NamingException ex );


    /**
     * Logs a changetype:delete.
     * 
     * @param connection the connection
     * @param dn the DN
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     * 
     */
    public void logChangetypeDelete( Connection connection, final String dn, final Control[] controls, NamingException ex );


    /**
     * Logs a changetype:modify.
     * 
     * @param connection the connection
     * @param dn the DN
     * @param modificationItems the modification items
     * @param ex the naming exception if an error occurred, null otherwise
     * @param controls the controls
     */
    public void logChangetypeModify( Connection connection, final String dn, final ModificationItem[] modificationItems, final Control[] controls, NamingException ex );


    /**
     * Logs a changetype:moddn.
     * 
     * @param connection the connection
     * @param oldDn the old DN
     * @param newDn the new DN
     * @param deleteOldRdn the delete old RDN
     * @param controls the controls
     * @param ex the naming exception if an error occurred, null otherwise
     */
    public void logChangetypeModDn( Connection connection, final String oldDn, final String newDn, final boolean deleteOldRdn, final Control[] controls, NamingException ex );


    /**
     * Sets the logger ID.
     * 
     * @param id the new logger ID
     */
    public void setId( String id );


    /**
     * Gets the logger ID.
     * 
     * @return the logger ID
     */
    public String getId();
    
    
    /**
     * Sets the logger name.
     * 
     * @param name the new logger name
     */
    public void setName( String name );


    /**
     * Gets the logger name.
     * 
     * @return the logger name
     */
    public String getName();
    
    
    /**
     * Sets the logger description.
     * 
     * @param description the new logger description
     */
    public void setDescription( String description );
    
    
    /**
     * Gets the logger description.
     * 
     * @return the logger description
     */
    public String getDescription();

}
