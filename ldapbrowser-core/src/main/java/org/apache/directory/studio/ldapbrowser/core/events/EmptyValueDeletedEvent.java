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
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;


/**
 * An EmptyValueDeletedEvent indicates that an empty {@link IValue} was deleted from an {@link IEntry}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EmptyValueDeletedEvent extends EntryModificationEvent
{

    /** The modified attribute. */
    private IAttribute modifiedAttribute;

    /** The deleted value. */
    private IValue deletedValue;


    /**
     * Creates a new instance of EmptyValueDeletedEvent.
     * 
     * @param connection the connection
     * @param modifiedEntry the modified entry
     * @param modifiedAttribute the modified attribute
     * @param deletedValue the deleted value
     */
    public EmptyValueDeletedEvent( IBrowserConnection connection, IEntry modifiedEntry, IAttribute modifiedAttribute,
        IValue deletedValue )
    {
        super( connection, modifiedEntry );
        this.modifiedAttribute = modifiedAttribute;
        this.deletedValue = deletedValue;
    }


    /**
     * Gets the modified attribute.
     * 
     * @return the modified attribute
     */
    public IAttribute getModifiedAttribute()
    {
        return this.modifiedAttribute;
    }


    /**
     * Gets the deleted value.
     * 
     * @return the deleted value
     */
    public IValue getDeletedValue()
    {
        return this.deletedValue;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__empty_value_deleted_from_att_at_dn, new String[]
            { getModifiedAttribute().getDescription(), getModifiedEntry().getDn().getUpName() } );
    }

}
