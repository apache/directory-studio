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

import org.apache.directory.studio.connection.core.ConnectionPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.BookmarkPropertyPageProvider;
import org.apache.directory.studio.ldapbrowser.core.propertypageproviders.EntryPropertyPageProvider;
import org.eclipse.core.runtime.IAdaptable;


/**
 * An IBookmark is used as shortcut to an entry in the DIT.
 * The target entry is defined by a connection a DN.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface IBookmark extends Serializable, IAdaptable, BookmarkPropertyPageProvider, EntryPropertyPageProvider,
    ConnectionPropertyPageProvider
{

    /**
     * Gets the target DN.
     * 
     * @return the DN
     */
    public DN getDn();


    /**
     * Sets the target DN.
     * 
     * @param dn the DN
     */
    public void setDn( DN dn );


    /**
     * Gets the symbolic name.
     * 
     * @return the name
     */
    public String getName();


    /**
     * Sets the symbolic name.
     * 
     * @param name the name
     */
    public void setName( String name );


    /**
     * Gets the browser connection.
     * 
     * @return the browser connection
     */
    public IBrowserConnection getBrowserConnection();


    /**
     * Gets the entry.
     * 
     * @return the entry
     */
    public IEntry getEntry();


    /**
     * Gets the bookmark parameter.
     * 
     * @return the bookmark parameter
     */
    public BookmarkParameter getBookmarkParameter();


    /**
     * Sets the bookmark parameter.
     * 
     * @param bookmarkParameter the bookmark parameter
     */
    public void setBookmarkParameter( BookmarkParameter bookmarkParameter );

}
