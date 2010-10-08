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

package org.apache.directory.studio.ldapbrowser.core.events;


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * An EntryRenamedEvent indicates that an {@link IEntry} was renamed in the underlying directory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryRenamedEvent extends EntryModificationEvent
{

    /** The old entry. */
    private IEntry oldEntry;

    /** The new entry. */
    private IEntry newEntry;


    /**
     * Creates a new instance of EntryRenamedEvent.
     * 
     * @param oldEntry the old entry
     * @param newEntry the new entry
     */
    public EntryRenamedEvent( IEntry oldEntry, IEntry newEntry )
    {
        super( newEntry.getBrowserConnection(), newEntry.getParententry() );
        this.oldEntry = oldEntry;
        this.newEntry = newEntry;
    }


    /**
     * Gets the new entry with the new DN.
     * 
     * @return the new entry
     */
    public IEntry getNewEntry()
    {
        return newEntry;
    }


    /**
     * Gets the old entry with the old DN.
     * 
     * @return the old entry
     */
    public IEntry getOldEntry()
    {
        return oldEntry;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__renamed_olddn_to_newdn, new String[]
            { getOldEntry().getDn().getName(), getNewEntry().getDn().getName() } );
    }

}
