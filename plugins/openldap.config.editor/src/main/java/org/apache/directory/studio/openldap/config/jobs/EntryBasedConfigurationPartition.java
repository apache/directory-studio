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
package org.apache.directory.studio.openldap.config.jobs;


import java.util.UUID;

import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.server.core.api.interceptor.context.AddOperationContext;
import org.apache.directory.server.core.partition.ldif.AbstractLdifPartition;


/**
 * This class implements a read-only configuration partition.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryBasedConfigurationPartition extends AbstractLdifPartition
{
    /**
     * Creates a new instance of EntryBasedConfigurationPartition.
     *
     * @param schemaManager the schema manager
     * @param suffixDn the suffix DN
     */
    public EntryBasedConfigurationPartition( SchemaManager schemaManager, Dn suffixDn )
    {
        super( schemaManager );
        this.suffixDn = suffixDn;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void doInit() throws Exception
    {
        setId( "config" );
        setSuffixDn( new Dn( "cn=config" ) );
        
        super.doInit();
    }


    /**
     * Adds the given entry.
     *
     * @param entry
     *      the entry
     * @throws Exception
     */
    public void addEntry( Entry entry ) throws Exception
    {
        // Adding mandatory operational attributes
        addMandatoryOpAt( entry );

        // Storing the entry
        add( new AddOperationContext( null, entry ) );
    }


    /**
     * Adds the CSN and UUID attributes to the entry if they are not present.
     */
    private void addMandatoryOpAt( Entry entry ) throws LdapException
    {
        // entryCSN
        if ( entry.get( SchemaConstants.ENTRY_CSN_AT ) == null )
        {
            entry.add( SchemaConstants.ENTRY_CSN_AT, defaultCSNFactory.newInstance().toString() );
        }

        // entryUUID
        if ( entry.get( SchemaConstants.ENTRY_UUID_AT ) == null )
        {
            String uuid = UUID.randomUUID().toString();
            entry.add( SchemaConstants.ENTRY_UUID_AT, uuid );
        }
    }
}
