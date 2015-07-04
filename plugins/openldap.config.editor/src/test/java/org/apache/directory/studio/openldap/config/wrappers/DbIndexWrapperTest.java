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
package org.apache.directory.studio.openldap.config.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.apache.directory.studio.openldap.config.editor.wrappers.DbIndexWrapper;
import org.apache.directory.studio.openldap.common.ui.model.DbIndexTypeEnum;
import org.junit.Test;

/**
 * A test for the DbIndexWrapper class
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DbIndexWrapperTest
{
    @Test
    public void testClone()
    {
        DbIndexWrapper index1 = new DbIndexWrapper( "cn,sn,objectClass eq,sub,approx" );
        
        DbIndexWrapper index2 = index1.clone();
        
        index1.getAttributes().clear();
        
        assertFalse( index1.getAttributes().contains( "cn" ) );
        assertTrue( index2.getAttributes().contains( "cn" ) );
    }

    @Test
    public void testConstructor()
    {
        DbIndexWrapper index1 = new DbIndexWrapper( "cn,sn,objectClass,cn eq,sub,aprox,eq" );
        
        assertEquals( 3, index1.getAttributes().size() );
        assertEquals( 2, index1.getIndexTypes().size() );
        assertTrue( index1.getIndexTypes().contains( DbIndexTypeEnum.EQ ) );
        assertTrue( index1.getIndexTypes().contains( DbIndexTypeEnum.SUB ) );
        assertFalse( index1.getIndexTypes().contains( DbIndexTypeEnum.APPROX ) );
        
        assertEquals( "cn,objectclass,sn eq,sub", index1.toString() );
    }

    @Test
    public void testCompare()
    {
        DbIndexWrapper index = new DbIndexWrapper( "cn,sn,objectClass,cn eq,sub,aprox,eq" );
        DbIndexWrapper index2 = new DbIndexWrapper( "cn,sn,objectClass,a eq,sub,aprox,eq" );
        DbIndexWrapper index3 = new DbIndexWrapper( "cn,sn,objectClass,z eq,sub,aprox,eq" );
        DbIndexWrapper index4 = new DbIndexWrapper( "sn,objectClass,cn eq" );
        
        assertTrue( index.compareTo( index2 ) > 0 );
        assertTrue( index.compareTo( index3 ) < 0 );
        assertTrue( index.compareTo( index4 ) == 0 );
    }
}
