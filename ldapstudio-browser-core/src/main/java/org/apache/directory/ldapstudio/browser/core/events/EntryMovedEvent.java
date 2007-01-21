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

package org.apache.directory.ldapstudio.browser.core.events;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;


public class EntryMovedEvent extends EntryModificationEvent
{

    private IEntry oldEntry;

    private IEntry newEntry;


    public EntryMovedEvent( IEntry oldEntry, IEntry newEntry )
    {
        super( newEntry.getConnection(), newEntry.getParententry() );
        this.oldEntry = oldEntry;
        this.newEntry = newEntry;
    }


    public IEntry getNewEntry()
    {
        return newEntry;
    }


    public IEntry getOldEntry()
    {
        return oldEntry;
    }


    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__moved_oldrdn_from_oldparent_to_newparent,
            new String[]
                { getOldEntry().getDn().getRdn().toString(), getOldEntry().getParententry().getDn().toString(),
                    getNewEntry().getParententry().getDn().toString() } );
    }

}
