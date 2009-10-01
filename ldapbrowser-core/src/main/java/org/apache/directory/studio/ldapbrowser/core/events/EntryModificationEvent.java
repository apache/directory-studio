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


import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;


/**
 * The root of all events that indecate an {@link IEntry} modification.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class EntryModificationEvent
{
    /** The event source */
    protected Object source;

    /** The connection. */
    protected IBrowserConnection connection;

    /** The entry. */
    protected IEntry modifiedEntry;


    /**
     * Creates a new instance of EntryModificationEvent.
     * 
     * @param modifiedEntry the modified entry
     * @param connection the connection
     */
    public EntryModificationEvent( IBrowserConnection connection, IEntry modifiedEntry )
    {
        this.connection = connection;
        this.modifiedEntry = modifiedEntry;
    }


    /**
     * Gets the connection.
     * 
     * @return the connection
     */
    public IBrowserConnection getConnection()
    {
        return connection;
    }


    /**
     * Gets the modified entry.
     * 
     * @return the modified entry
     */
    public IEntry getModifiedEntry()
    {
        return modifiedEntry;
    }


    /**
     * Gets the event source.
     * 
     * @return the event source, may be null
     */
    public Object getSource()
    {
        return source;
    }


    /**
     * Sets the source.
     * 
     * @param source the new source
     */
    public void setSource( Object source )
    {
        this.source = source;
    }

}
