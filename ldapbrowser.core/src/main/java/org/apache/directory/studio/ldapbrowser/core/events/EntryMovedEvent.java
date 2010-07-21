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
 * An EntryMovedEvent indicates that an {@link IEntry} was moved in the underlying directory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryMovedEvent extends EntryModificationEvent
{

    /** The old entry. */
    private IEntry oldEntry;

    /** The new entry. */
    private IEntry newEntry;


    /**
     * Creates a new instance of EntryMovedEvent.
     * 
     * @param oldEntry the old entry
     * @param newEntry the new entry
     */
    public EntryMovedEvent( IEntry oldEntry, IEntry newEntry )
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
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__moved_oldrdn_from_oldparent_to_newparent,
            new String[]
                { getOldEntry().getDn().getRdn().getUpName(), getOldEntry().getParententry().getDn().getUpName(),
                    getNewEntry().getParententry().getDn().getUpName() } );
    }

}
