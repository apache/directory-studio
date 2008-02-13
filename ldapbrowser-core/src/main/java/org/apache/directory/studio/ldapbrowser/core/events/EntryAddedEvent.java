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
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * An EntryAddedEvent indicates that an {@link IEntry} was added to the underlying directory.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EntryAddedEvent extends EntryModificationEvent
{

    /**
     * Creates a new instance of EntryAddedEvent.
     * 
     * @param connection the connection
     * @param addedEntry the added entry
     */
    public EntryAddedEvent( IBrowserConnection connection, IEntry addedEntry )
    {
        super( connection, addedEntry );
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__added_dn, new String[]
            { getModifiedEntry().getDn().getUpName() } );
    }

}
