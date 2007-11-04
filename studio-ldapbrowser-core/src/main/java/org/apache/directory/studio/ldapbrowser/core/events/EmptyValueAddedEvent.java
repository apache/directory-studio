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
 * An EmptyValueAddedEvent indicates that an empty {@link IValue} was added to an {@link IEntry}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class EmptyValueAddedEvent extends EntryModificationEvent
{

    /** The modified attribute. */
    private IAttribute modifiedAttribute;

    /** The added value. */
    private IValue addedValue;


    /**
     * Creates a new instance of EmptyValueAddedEvent.
     * 
     * @param connection the connection
     * @param modifiedEntry the modified entry
     * @param modifiedAttribute the modified attribute
     * @param addedValue the added value
     */
    public EmptyValueAddedEvent( IBrowserConnection connection, IEntry modifiedEntry, IAttribute modifiedAttribute,
        IValue addedValue )
    {
        super( connection, modifiedEntry );
        this.modifiedAttribute = modifiedAttribute;
        this.addedValue = addedValue;
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
     * Gets the added value.
     * 
     * @return the added value
     */
    public IValue getAddedValue()
    {
        return this.addedValue;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__empty_value_added_to_att_at_dn, new String[]
            { getModifiedAttribute().getDescription(), getModifiedEntry().getDn().getUpName() } );
    }

}
