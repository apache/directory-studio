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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * Runnable to update an entry using an LDIF fragment.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class UpdateEntryRunnable extends ExecuteLdifRunnable
{

    private IEntry entry;


    /**
     * Creates a new instance of UpdateEntryRunnable.
     * 
     * @param entry the entry
     * @param ldif the LDIF to execute
     */
    public UpdateEntryRunnable( IEntry entry, String ldif )
    {
        super( entry.getBrowserConnection(), ldif, false, false );
        this.entry = entry;
    }


    @Override
    public void run( StudioProgressMonitor monitor )
    {
        super.run( monitor );
        InitializeAttributesRunnable.initializeAttributes( entry, monitor );
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        EventRegistry.fireEntryUpdated( new EntryModificationEvent( entry.getBrowserConnection(), entry ), this );
    }
}
