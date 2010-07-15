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

package org.apache.directory.studio.ldapservers.apacheds;


import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.apache.directory.studio.ldapservers.model.LdapServerAdapter;


/**
 * The {@link ApacheDS157LdapServerAdapter} interface defines the required method
 * to implement an LDAP Server Adapter.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ApacheDS157LdapServerAdapter implements LdapServerAdapter
{
    public void add( LdapServer server ) throws Exception
    {
        System.out.println( "add " + server.getName() );
    }


    public void delete( LdapServer server ) throws Exception
    {
        System.out.println( "delete " + server.getName() );
    }


    public void start( LdapServer server ) throws Exception
    {
        System.out.println( "start " + server.getName() );
    }


    public void stop( LdapServer server ) throws Exception
    {
        System.out.println( "stop " + server.getName() );
    }


    public void restart( LdapServer server ) throws Exception
    {
        System.out.println( "restart " + server.getName() );
    }
}
