/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.studio.ldapbrowser.core.model;


import junit.framework.TestCase;


/**
 * Test the DN class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DnTest extends TestCase
{

    /**
     * Tests the empty DN.
     * 
     * @throws NameException the name exception
     */
    public void testEmpty() throws NameException
    {
        DN dn = new DN( "" );

        assertEquals( "", dn.toString() );
    }


    /**
     * Tests a DN with one RDN.
     * 
     * @throws NameException the name exception
     */
    public void testSimpleOne() throws NameException
    {
        DN dn = new DN( "cn=test" );

        assertEquals( 1, dn.getRdns().length );
        assertEquals( "cn=test", dn.toString() );
    }

    /**
     * Tests a DN with two RDNs.
     * 
     * @throws NameException the name exception
     */
    public void testSimpleTwo() throws NameException
    {
        DN dn = new DN( "uid=admin,ou=system" );
        
        assertEquals( 2, dn.getRdns().length );
        assertEquals( "uid=admin", dn.getRdns()[0].toString() );
        assertEquals( "ou=system", dn.getRdns()[1].toString() );
        assertEquals( "uid=admin,ou=system", dn.toString() );
    }
    

    /**
     * Tests a DN with one RDN.
     * 
     * @throws NameException the name exception
     */
    public void testSlash() throws NameException
    {
        DN dn = new DN( "homeDirectory=/home/test,ou=system" );

        assertEquals( 2, dn.getRdns().length );
        assertEquals( "homeDirectory=/home/test,ou=system", dn.toString() );
    }
    
    
    
    
}
