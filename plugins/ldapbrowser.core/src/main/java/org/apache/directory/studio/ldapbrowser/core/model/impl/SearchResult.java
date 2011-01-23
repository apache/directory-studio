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


import java.util.ArrayList;

import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.search.ui.ISearchPageScoreComputer;


/**
 * Default implementation of ISearchResult.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResult implements ISearchResult
{

    private static final long serialVersionUID = -5658803569872619432L;

    /** The search. */
    private ISearch search;

    /** The entry. */
    private IEntry entry;


    protected SearchResult()
    {
    }


    /**
     * Creates a new instance of SearchResult.
     * 
     * @param entry the entry
     * @param search the search
     */
    public SearchResult( IEntry entry, ISearch search )
    {
        this.entry = entry;
        this.search = search;
    }


    /**
     * {@inheritDoc}
     */
    public Dn getDn()
    {
        return entry.getDn();
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute[] getAttributes()
    {
        ArrayList<IAttribute> attributeList = new ArrayList<IAttribute>();
        for ( int i = 0; i < search.getReturningAttributes().length; i++ )
        {
            if ( entry.getAttribute( search.getReturningAttributes()[i] ) != null )
            {
                attributeList.add( entry.getAttribute( search.getReturningAttributes()[i] ) );
            }
        }
        return attributeList.toArray( new IAttribute[attributeList.size()] );
    }


    /**
     * {@inheritDoc}
     */
    public IAttribute getAttribute( String attributeDescription )
    {
        return entry.getAttribute( attributeDescription );
    }


    /**
     * {@inheritDoc}
     */
    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {
        return entry.getAttributeWithSubtypes( attributeDescription );
    }


    /**
     * {@inheritDoc}
     */
    public IEntry getEntry()
    {
        return entry;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter )
    {
        Class<?> clazz = ( Class<?> ) adapter;
        if ( clazz.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( clazz.isAssignableFrom( Connection.class ) )
        {
            return search.getBrowserConnection().getConnection();
        }
        if ( clazz.isAssignableFrom( IBrowserConnection.class ) )
        {
            return search.getBrowserConnection();
        }
        if ( clazz.isAssignableFrom( IEntry.class ) )
        {
            return getEntry();
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ISearch getSearch()
    {
        return search;
    }


    /**
     * {@inheritDoc}
     */
    public void setSearch( ISearch search )
    {
        this.search = search;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( entry == null ) ? 0 : entry.getDn().hashCode() );
        result = prime * result + ( ( search == null ) ? 0 : search.hashCode() );
        return result;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( !( obj instanceof SearchResult ) )
        {
            return false;
        }
        SearchResult other = ( SearchResult ) obj;
        if ( entry == null )
        {
            if ( other.entry != null )
            {
                return false;
            }
        }
        else if ( !entry.equals( other.entry ) )
        {
            return false;
        }
        if ( search == null )
        {
            if ( other.search != null )
            {
                return false;
            }
        }
        else if ( !search.equals( other.search ) )
        {
            return false;
        }
        return true;
    }

}
