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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.test.integration.ui.bots.BrowserViewBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests performance.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class PerformanceTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private ConnectionsViewBot connectionsViewBot;
    private BrowserViewBot browserViewBot;

    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetLdapPerspective();
        connectionsViewBot = studioBot.getConnectionView();
        connectionsViewBot.createTestConnection( "EntryEditorTest", ldapServer.getPort() );
        browserViewBot = studioBot.getBrowserView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        Assertions.genericTearDownAssertions();
    }


    /**
     * Test for DIRSTUDIO-1119 (Group with over 1000 members crashes)
     */
    @Test
    public void testEditLargeGroup() throws Exception
    {
        BrowserCommonActivator.getDefault()
            .getPluginPreferences().setValue( BrowserCommonConstants.PREFERENCE_ENTRYEDITOR_ENABLE_FOLDING, false );

        int n = 5000;
        String memberAt = "member";
        List<String> memberDns = new ArrayList<>( n );

        Entry entry = new DefaultEntry( service.getSchemaManager() );
        entry.setDn( new Dn( "cn=Large Group,ou=system" ) );
        entry.add( "objectClass", "top", "groupOfNames" );
        entry.add( "cn", "Large Group" );
        for ( int i = 0; i < n; i++ )
        {
            String memberDn = "cn=user." + String.format( "%04d", i );
            memberDns.add( memberDn );
            entry.add( memberAt, memberDn );
        }
        service.getAdminSession().add( entry );

        String first = memberDns.get( 0 );
        String second = memberDns.get( 1 );
        String middle = memberDns.get( n / 2 );
        String secondToLast = memberDns.get( n - 2 );
        String last = memberDns.get( n - 1 );

        browserViewBot.selectEntry( "DIT", "Root DSE", "ou=system", "cn=Large Group" );

        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( "cn=Large Group,ou=system" );
        entryEditorBot.activate();

        // edit some values
        entryEditorBot.editValueExpectingDnEditor( memberAt, first ).clickCancelButton();
        entryEditorBot.editValueExpectingDnEditor( memberAt, last ).clickCancelButton();
        entryEditorBot.editValueExpectingDnEditor( memberAt, middle ).clickCancelButton();
        entryEditorBot.editValueExpectingDnEditor( memberAt, second ).clickCancelButton();
        entryEditorBot.editValueExpectingDnEditor( memberAt, secondToLast ).clickCancelButton();

        // delete some values
        entryEditorBot.deleteValue( memberAt, second );
        entryEditorBot.deleteValue( memberAt, secondToLast );

        // edit some value after deletion
        entryEditorBot.editValueExpectingDnEditor( memberAt, first ).clickCancelButton();
        entryEditorBot.editValueExpectingDnEditor( memberAt, last ).clickCancelButton();
        entryEditorBot.editValueExpectingDnEditor( memberAt, middle ).clickCancelButton();
    }

}
