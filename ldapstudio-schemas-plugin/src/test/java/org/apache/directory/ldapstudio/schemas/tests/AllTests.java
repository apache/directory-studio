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

package org.apache.directory.ldapstudio.schemas.tests;


import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Main test class -> activate all the other tests
 *
 */
public class AllTests
{

    public static Test suite()
    {
        TestSuite suite = new TestSuite( "Test for org.apache.directory.ldapstudio.tests" ); //$NON-NLS-1$
        //$JUnit-BEGIN$
        suite.addTestSuite( ObjectClassTest.class );
        suite.addTestSuite( AttributeTypeTest.class );
        suite.addTestSuite( SchemaPoolTest.class );
        suite.addTestSuite( PoolListenerTest.class );
        suite.addTestSuite( SchemaListenerTest.class );
        suite.addTestSuite( SchemaElementListenerTest.class );
        suite.addTestSuite( OIDTest.class );
        //$JUnit-END$
        return suite;
    }

}
