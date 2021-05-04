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

package org.apache.directory.studio.test.integration.ui;


import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.ProgressViewBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the Progress view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ProgressViewTest extends AbstractTestBase
{

    @Test
    public void testRemoveAllFinishedOperations() throws Exception
    {
        ProgressViewBot view = studioBot.getProgressView();
        view.removeAllFinishedOperations();
    }


    @ParameterizedTest
    @LdapServersSource
    public void testNoRemainingOpenConnectionJobs( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        connectionsViewBot.createTestConnection( server );
        connectionsViewBot.createTestConnection( server );

        // actual assertion is done in Assertions.genericTearDownAssertions()
    }

}
