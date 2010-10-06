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
 * An AttributeDeletedEvent indicates that an {@link IAttribute} was deleted from an {@link IEntry}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeDeletedEvent extends EntryModificationEvent
{

    /** The deleted attribute. */
    private IAttribute deletedAttribute;


    /**
     * Creates a new instance of AttributeDeletedEvent.
     * 
     * @param connection the connection
     * @param modifiedEntry the modified entry
     * @param deletedAttribute the deleted attribute
     */
    public AttributeDeletedEvent( IBrowserConnection connection, IEntry modifiedEntry, IAttribute deletedAttribute )
    {
        super( connection, modifiedEntry );
        this.deletedAttribute = deletedAttribute;
    }


    /**
     * Gets the deleted attribute.
     * 
     * @return the deleted attribute
     */
    public IAttribute getDeletedAttribute()
    {
        return deletedAttribute;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return BrowserCoreMessages.bind( BrowserCoreMessages.event__deleted_att_from_dn, new String[]
            { getDeletedAttribute().getDescription(), getModifiedEntry().getDn().getName() } );
    }

}
