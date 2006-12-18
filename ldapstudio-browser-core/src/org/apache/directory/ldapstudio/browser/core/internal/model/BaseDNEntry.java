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

package org.apache.directory.ldapstudio.browser.core.internal.model;


import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.events.ModelModifier;
import org.apache.directory.ldapstudio.browser.core.model.DN;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.IEntry;
import org.apache.directory.ldapstudio.browser.core.model.ModelModificationException;
import org.apache.directory.ldapstudio.browser.core.model.RDN;


public class BaseDNEntry extends AbstractEntry
{

    private static final long serialVersionUID = -5444229580355372176L;

    protected DN baseDn;

    // protected String connectionName;
    protected IConnection connection;


    protected BaseDNEntry()
    {
    }


    public BaseDNEntry( DN baseDn, IConnection connection, ModelModifier source ) throws ModelModificationException
    {
        super();

        if ( baseDn == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_dn );
        }
        if ( "".equals( baseDn.toString() ) ) { //$NON-NLS-1$
            throw new ModelModificationException( BrowserCoreMessages.model__empty_dn );
        }
        if ( connection == null )
        {
            throw new ModelModificationException( BrowserCoreMessages.model__empty_connection );
        }

        this.setDirectoryEntry( true );
        this.baseDn = baseDn;
        // this.connectionName = connection.getName();
        this.connection = connection;
    }


    public DN getDn()
    {
        return this.baseDn;
    }


    public IEntry getParententry()
    {
        return null;
    }


    public IConnection getConnection()
    {
        // return
        // BrowserCorePlugin.getDefault().getConnectionManager().getConnection(this.connectionName);
        return this.connection;
    }


    protected void setRdn( RDN newRdn )
    {
    }


    protected void setParent( IEntry newParent )
    {
    }

}
