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

package org.apache.directory.studio.ldapservers.jobs;


import org.apache.directory.studio.common.core.jobs.StudioJob;
import org.apache.directory.studio.common.core.jobs.StudioRunnableWithProgress;
import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.eclipse.core.runtime.jobs.Job;


/**
 * This class implements a {@link Job} that is used for {@link StudioRunnableWithProgress} runnables on LDAP Servers.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioLdapServerJob extends StudioJob<StudioRunnableWithProgress>
{
    /**
    * Creates a new instance of StudioLdapServerJob.
    * 
    * @param runnables the runnables to run
    */
    public StudioLdapServerJob( StudioRunnableWithProgress... runnables )
    {
        super( runnables );
    }


    /**
    * {@inheritDoc}
    */
    protected String[] getLockIdentifiers( Object[] objects )
    {
        String[] identifiers = new String[objects.length];
        for ( int i = 0; i < identifiers.length; i++ )
        {
            Object o = objects[i];
            if ( o instanceof LdapServer )
            {
                identifiers[i] = getLockIdentifier( ( LdapServer ) o );
            }
            else
            {
                identifiers[i] = getLockIdentifier( objects[i] );
            }
        }
        return identifiers;
    }


    /**
     * Gets the lock identifier for an {@link LdapServer} object.
     *
     * @param server
     *      the server
     * @return
     *      the lock identifier for the server object
     */
    private String getLockIdentifier( LdapServer server )
    {
        return server.getId();
    }


    /**
     * Gets the generic lock identifier for an object.
     *
     * @param object
     *      the object
     * @return
     *      the lock identifier for the object
     */
    private String getLockIdentifier( Object object )
    {
        return ( object != null ? object.toString() : "null" ); //$NON-NLS-1$
    }
}
