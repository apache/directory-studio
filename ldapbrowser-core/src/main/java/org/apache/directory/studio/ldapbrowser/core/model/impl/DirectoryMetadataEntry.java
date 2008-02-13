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

package org.apache.directory.studio.ldapbrowser.core.model.impl;


import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * The DirectoryMetadataEntry class represents entries that are listed in the root DSE.
 * Examples are the schema sub-entry, the monitorContext or the configContext entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DirectoryMetadataEntry extends BaseDNEntry
{

    private static final long serialVersionUID = 1340597532850853276L;

    /** The schema entry flag. */
    private boolean schemaEntry;


    protected DirectoryMetadataEntry()
    {
    }


    /**
     * Creates a new instance of DirectoryMetadataEntry.
     * 
     * @param dn the DN
     * @param browserConnection the browser connection
     */
    public DirectoryMetadataEntry( LdapDN dn, IBrowserConnection browserConnection )
    {
        super();
        this.baseDn = dn;
        this.browserConnection = browserConnection;
        this.schemaEntry = false;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.model.impl.AbstractEntry#hasChildren()
     */
    public boolean hasChildren()
    {
        if ( getDn().equals( getBrowserConnection().getSchema().getDn() ) )
        {
            return false;
        }
        else
        {
            return super.hasChildren();
        }
    }


    /**
     * Checks if is schema entry.
     * 
     * @return true, if is schema entry
     */
    public boolean isSchemaEntry()
    {
        return schemaEntry;
    }


    /**
     * Sets the schema entry flag.
     * 
     * @param schemaEntry the schema entry flag
     */
    public void setSchemaEntry( boolean schemaEntry )
    {
        this.schemaEntry = schemaEntry;
    }

}
