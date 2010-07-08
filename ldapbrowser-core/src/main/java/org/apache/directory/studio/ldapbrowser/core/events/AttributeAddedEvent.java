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


/**
 * An AttributeAddedEvent indicates that an {@link IAttribute} was added to an {@link IEntry}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeAddedEvent extends EntryModificationEvent
{

    /** The added attribute. */
    private IAttribute addedAttribute;


    /**
     * Creates a new instance of AttributeAddedEvent.
     * 
     * @param connection the connection
     * @param modifiedEntry the modified entry
     * @param addedAttribute the added attribute
     */
    public AttributeAddedEvent( IBrowserConnection connection, IEntry modifiedEntry, IAttribute addedAttribute )
    {
        super( connection, modifiedEntry );
        this.addedAttribute = addedAttribute;
    }


    /**
     * Gets the added attribute.
     * 
     * @return the added attribute
     */
    public IAttribute getAddedAttribute()
    {
        return addedAttribute;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__added_att_to_dn, new String[]
            { getAddedAttribute().getDescription(), getModifiedEntry().getDn().getUpName() } );
    }

}
