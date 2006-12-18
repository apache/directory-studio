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
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.IValue;


public class ValueAddedEvent extends EntryModificationEvent
{

    private IAttribute modifiedAttribute;

    private IValue addedValue;


    public ValueAddedEvent( IConnection connection, IEntry modifiedEntry, IAttribute modifiedAttribute,
        IValue addedValue, ModelModifier source )
    {
        super( connection, modifiedEntry, source );
        this.modifiedAttribute = modifiedAttribute;
        this.addedValue = addedValue;
    }


    public IAttribute getModifiedAttribute()
    {
        return this.modifiedAttribute;
    }


    public IValue getAddedValue()
    {
        return this.addedValue;
    }


    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__added_val_to_att_at_dn, new String[]
            { getAddedValue().getStringValue(), getModifiedAttribute().getDescription(),
                getModifiedEntry().getDn().toString() } );
    }

}
