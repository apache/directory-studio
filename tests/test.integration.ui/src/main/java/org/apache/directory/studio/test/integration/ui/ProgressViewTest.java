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


import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ProgressViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the Progress view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class ProgressViewTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
    }


    @After
    public void tearDown() throws Exception
    {
        studioBot.getConnectionView().deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    @Test
    public void testRemoveAllFinishedOperations() throws Exception
    {
        ProgressViewBot view = studioBot.getProgressView();
        view.removeAllFinishedOperations();
    }


    @Test
    public void testNoRemainingOpenConnectionJobs() throws Exception
    {
        ConnectionsViewBot connectionView = studioBot.getConnectionView();

        connectionView.createTestConnection( "ProgressViewTest", ldapServer.getPort() );
        connectionView.createTestConnection( "ProgressViewTest", ldapServer.getPort() );
        connectionView.createTestConnection( "ProgressViewTest", ldapServer.getPort() );

        // actual assertion is done in Assertions.genericTearDownAssertions()
    }

}
