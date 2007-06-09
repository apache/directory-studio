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
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * An ValueModifiedEvent indicates that an {@link IValue} was modified.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ValueModifiedEvent extends EntryModificationEvent
{

    /** The modified attribute. */
    private IAttribute modifiedAttribute;

    /** The old value. */
    private IValue oldValue;

    /** The new value. */
    private IValue newValue;


    /**
     * Creates a new instance of ValueModifiedEvent.
     *
     * @param connection the connection
     * @param modifiedEntry the modified entry
     * @param modifiedAttribute the modified attribute
     * @param oldValue the old value
     * @param newValue the new value
     */
    public ValueModifiedEvent( IConnection connection, IEntry modifiedEntry, IAttribute modifiedAttribute,
        IValue oldValue, IValue newValue )
    {
        super( connection, modifiedEntry );
        this.modifiedAttribute = modifiedAttribute;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    /**
     * Gets the modified attribute.
     *
     * @return the modified attribute
     */
    public IAttribute getModifiedAttribute()
    {
        return modifiedAttribute;
    }


    /**
     * Gets the old value.
     *
     * @return the old value
     */
    public IValue getOldValue()
    {
        return oldValue;
    }


    /**
     * Gets the new value.
     *
     * @return the new value
     */
    public IValue getNewValue()
    {
        return newValue;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__replaced_oldval_by_newval_at_att_at_dn,
            new String[]
                { getOldValue().getStringValue(), getNewValue().getStringValue(),
                    getModifiedAttribute().getDescription(), getModifiedEntry().getDn().toString() } );
    }

}
