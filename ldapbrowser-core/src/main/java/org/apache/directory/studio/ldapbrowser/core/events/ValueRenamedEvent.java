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
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * An ValueRenamedEvent indicates that an {@link IValue} was renamed. This
 * means that the attribute type was modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ValueRenamedEvent extends EntryModificationEvent
{

    /** The old value with the old attribute type. */
    private IValue oldValue;

    /** The new value with the new attribute type. */
    private IValue newValue;


    /**
     * Creates a new instance of ValueRenamedEvent.
     *
     * @param connection the connection
     * @param modifiedEntry the modified entry
     * @param oldValue the old value with the old attribute type
     * @param newValue the new value with the new attribute type
     */
    public ValueRenamedEvent( IBrowserConnection connection, IEntry modifiedEntry, IValue oldValue, IValue newValue )
    {
        super( connection, modifiedEntry );
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    /**
     * Gets the new value with the new attribute type.
     *
     * @return the new value with the new attribute type
     */
    public IValue getNewValue()
    {
        return newValue;
    }


    /**
     * Gets the old value with the old attribute type.
     *
     * @return the old value with the old attribute type
     */
    public IValue getOldValue()
    {
        return oldValue;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__renamed_oldval_by_newval_at_dn, new String[]
            { getOldValue().toString(), getNewValue().toString(), getModifiedEntry().getDn().getUpName() } );
    }

}
