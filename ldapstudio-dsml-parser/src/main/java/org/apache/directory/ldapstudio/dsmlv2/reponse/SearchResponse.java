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

package org.apache.directory.ldapstudio.dsmlv2.reponse;

import java.util.ArrayList;
import java.util.List;

import org.apache.directory.shared.ldap.codec.LdapResponse;
import org.apache.directory.shared.ldap.codec.search.SearchResultDone;
import org.apache.directory.shared.ldap.codec.search.SearchResultEntry;
import org.apache.directory.shared.ldap.codec.search.SearchResultReference;

public class SearchResponse extends LdapResponse
{
	private List<SearchResultEntry> searchResultEntryList;
	private List<SearchResultReference> searchResultReferenceList;
	private SearchResultDone searchResultDone;
	
	public SearchResponse()
	{
		searchResultEntryList = new ArrayList<SearchResultEntry>();
		searchResultReferenceList = new ArrayList<SearchResultReference>();
	}
	
	public boolean addSearchResultEntry(SearchResultEntry searchResultEntry)
    {
        return searchResultEntryList.add( searchResultEntry );
    }
    
    public SearchResultEntry getCurrentSearchResultEntry()
    {
        if ( searchResultEntryList.size() > 0 )
        {
            return searchResultEntryList.get( searchResultEntryList.size() - 1 );
        }
        else
        {
            return null;
        }
    }
	
	public boolean addSearchResultReference(SearchResultReference searchResultReference)
    {
        return searchResultReferenceList.add( searchResultReference );
    }
    
    public SearchResultReference getCurrentSearchResultReference()
    {
        return searchResultReferenceList.get( searchResultReferenceList.size() - 1 );
    }

	public SearchResultDone getSearchResultDone() {
		return searchResultDone;
	}

	public void setSearchResultDone(SearchResultDone searchResultDone) {
		this.searchResultDone = searchResultDone;
	}

	public List<SearchResultEntry> getSearchResultEntryList() {
		return searchResultEntryList;
	}

	public List<SearchResultReference> getSearchResultReferenceList() {
		return searchResultReferenceList;
	}

}
