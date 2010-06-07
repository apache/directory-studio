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


import java.io.Serializable;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * The ISearchResult represents a single search result.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ISearchResult extends Serializable, IAdaptable, EntryPropertyPageProvider,
    ConnectionPropertyPageProvider
{

    /**
     * Returns the DN of the search result entry.
     * 
     * @return the DN of the search result entry.
     */
    public LdapDN getDn();


    /**
     * Returns the attributes of the search result entry.
     * 
     * @return the attributes of the search result entry.
     */
    public IAttribute[] getAttributes();


    /**
     * Returns the attribute of the search result entry.
     * 
     * @param attributeDescription
     *                the attribute description of the attribute to return
     * @return the attribute with the given description or null.
     */
    public IAttribute getAttribute( String attributeDescription );


    /**
     * Returns the AttributeHierachie of the search result entry.
     * 
     * @param attributeDescription
     *                the description of the attribute to return
     * @return the AttributeHierachie with the given description or null.
     */
    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription );


    /**
     * Returns the entry of the search result.
     * 
     * @return the entry
     */
    public IEntry getEntry();


    /**
     * Return the search, the parent of this search result.
     * 
     * @return the search
     */
    public ISearch getSearch();


    /**
     * Sets the search.
     * 
     * @param search the search
     */
    public void setSearch( ISearch search );

}
