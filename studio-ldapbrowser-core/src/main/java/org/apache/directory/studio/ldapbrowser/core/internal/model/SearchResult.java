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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import java.util.ArrayList;

import org.apache.directory.studio.ldapbrowser.core.internal.search.LdapSearchPageScoreComputer;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class SearchResult implements ISearchResult
{

    private static final long serialVersionUID = -5658803569872619432L;

    private ISearch search;

    private IEntry entry;


    protected SearchResult()
    {
    }


    public SearchResult( IEntry entry, ISearch search )
    {
        this.entry = entry;
        this.search = search;
    }


    public DN getDn()
    {
        return this.entry.getDn();
    }


    public IAttribute[] getAttributes()
    {
        ArrayList attributeList = new ArrayList();
        for ( int i = 0; i < this.search.getReturningAttributes().length; i++ )
        {
            if ( this.entry.getAttribute( this.search.getReturningAttributes()[i] ) != null )
            {
                attributeList.add( this.entry.getAttribute( this.search.getReturningAttributes()[i] ) );
            }
        }
        return ( IAttribute[] ) attributeList.toArray( new IAttribute[attributeList.size()] );
    }


    public IAttribute getAttribute( String attributeDescription )
    {
        return this.entry.getAttribute( attributeDescription );
    }


    public AttributeHierarchy getAttributeWithSubtypes( String attributeDescription )
    {
        return this.entry.getAttributeWithSubtypes( attributeDescription );
    }


    public IEntry getEntry()
    {
        return this.entry;
    }


    public Object getAdapter( Class adapter )
    {
        if ( adapter.isAssignableFrom( ISearchPageScoreComputer.class ) )
        {
            return new LdapSearchPageScoreComputer();
        }
        if ( adapter == IConnection.class )
        {
            return this.getConnection();
        }
        if ( adapter == IEntry.class )
        {
            return this.getEntry();
        }
        return null;
    }


    public IConnection getConnection()
    {
        return this.search.getConnection();
    }


    public ISearch getSearch()
    {
        return this.search;
    }


    public void setSearch( ISearch search )
    {
        this.search = search;
    }

}
