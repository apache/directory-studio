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
package org.apache.directory.studio.openldap.common.ui.dialogs;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.apache.directory.studio.openldap.common.ui.model.UnixPermissions;

import org.junit.Test;


public class UnixPermissionsTest
{
    @Test
    public void testEmpty() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testFail() throws Exception
    {
        try
        {
            new UnixPermissions( "0123456789" );

            fail();
        }
        catch ( Exception e )
        {
            // Should happen
        }
    }


    @Test
    public void testOctal1() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0157" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal2() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0266" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal3() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0375" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal4() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0404" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal5() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0513" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal6() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0622" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal7() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0731" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testOctal8() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "0040" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal1() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "111" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal2() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "182" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal3() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "253" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal4() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "260" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal5() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "331" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal6() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "402" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal7() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "473" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testDecimal8() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "32" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic1() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "---xr-xrwx" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic2() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "--w-rw-rw-" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic3() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "--wxrwxr-x" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic4() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "-r-----r--" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertTrue( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic5() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "-r-x--x-wx" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic6() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "-rw--w--w-" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertTrue( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic7() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "-rwx-wx--x" );

        assertTrue( unixPermissions.isOwnerRead() );
        assertTrue( unixPermissions.isOwnerWrite() );
        assertTrue( unixPermissions.isOwnerExecute() );
        assertFalse( unixPermissions.isGroupRead() );
        assertTrue( unixPermissions.isGroupWrite() );
        assertTrue( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertTrue( unixPermissions.isOthersExecute() );
    }


    @Test
    public void testSymbolic8() throws Exception
    {
        UnixPermissions unixPermissions = new UnixPermissions( "----r-----" );

        assertFalse( unixPermissions.isOwnerRead() );
        assertFalse( unixPermissions.isOwnerWrite() );
        assertFalse( unixPermissions.isOwnerExecute() );
        assertTrue( unixPermissions.isGroupRead() );
        assertFalse( unixPermissions.isGroupWrite() );
        assertFalse( unixPermissions.isGroupExecute() );
        assertFalse( unixPermissions.isOthersRead() );
        assertFalse( unixPermissions.isOthersWrite() );
        assertFalse( unixPermissions.isOthersExecute() );
    }
}
