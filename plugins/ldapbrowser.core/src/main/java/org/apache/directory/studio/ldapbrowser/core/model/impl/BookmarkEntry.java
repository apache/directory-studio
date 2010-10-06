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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ICompareableEntry;


/**
 * An {@link BookmarkEntry} represents the target of a {@link Bookmark}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BookmarkEntry extends DelegateEntry implements ICompareableEntry
{

    private static final long serialVersionUID = -6351277968774226912L;


    protected BookmarkEntry()
    {
    }


    /**
     * Creates a new instance of BookmarkEntry.
     * 
     * @param connection the connection of the bookmark target
     * @param dn the DN of the bookmark target
     */
    public BookmarkEntry( IBrowserConnection connection, DN dn )
    {
        super( connection, dn );
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        return getDn().hashCode();
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object o )
    {
        // check argument
        if ( o == null || !( o instanceof ICompareableEntry ) )
        {
            return false;
        }
        ICompareableEntry e = ( ICompareableEntry ) o;

        // compare dn and connection
        return getDn() == null ? e.getDn() == null : ( getDn().equals( e.getDn() ) && getBrowserConnection().equals(
            e.getBrowserConnection() ) );
    }
}
