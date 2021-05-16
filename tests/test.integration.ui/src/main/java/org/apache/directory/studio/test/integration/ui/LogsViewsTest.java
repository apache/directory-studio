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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.GROUPS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.TARGET_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER2_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER3_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER4_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER5_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource.Mode;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.SelectCopyDepthDialogBot;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the modification and search logs views.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LogsViewsTest extends AbstractTestBase
{

    @AfterEach
    public void reset() throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.remove( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT );
        prefs.remove( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE );
        prefs.remove( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT );
        prefs.remove( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE );
        prefs.remove( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES );
        searchLogsViewBot.enableSearchRequestLogs( true );
        searchLogsViewBot.enableSearchResultEntryLogs( false );
        modificationLogsViewBot.enableModificationLogs( true );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchLogsViewDefault( TestLdapServer server ) throws Exception
    {
        // create and open connection
        connectionsViewBot.createTestConnection( server );
        searchLogsViewBot.clear();

        // select groups entry
        browserViewBot.selectAndExpandEntry( path( USERS_DN ) );

        // assert content (ou=users)
        String text = searchLogsViewBot.getSearchLogsText();

        assertThat( text, containsString( "#!SEARCH REQUEST " ) );
        assertThat( text, containsString( "#!CONNECTION " + server.getLdapUrl() ) );
        assertThat( text, containsString( "#!DATE " ) );
        assertThat( text, containsString( "# LDAP URL     : " + server.getLdapUrl()
            + "/ou=users,dc=example,dc=org?hasSubordinates,objectClass?one?(objectClass=*)" ) );
        assertThat( text, containsString( "# command line : ldapsearch -H " + server.getLdapUrl() + " -x -D \""
            + server.getAdminDn()
            + "\" -W -b \"ou=users,dc=example,dc=org\" -s one -a always -z 1000 \"(objectClass=*)\" \"hasSubordinates\" \"objectClass\"" ) );
        assertThat( text, containsString( "# baseObject   : ou=users,dc=example,dc=org" ) );
        assertThat( text, containsString( "# scope        : singleLevel (1)" ) );
        assertThat( text, containsString( "# derefAliases : derefAlways (3)" ) );
        assertThat( text, containsString( "# sizeLimit    : 1000" ) );
        assertThat( text, containsString( "# timeLimit    : 0" ) );
        assertThat( text, containsString( "# typesOnly    : False" ) );
        assertThat( text, containsString( "# filter       : (objectClass=*)" ) );
        assertThat( text, containsString( "# attributes   : hasSubordinates objectClass" ) );

        assertThat( text, not( containsString( "#!SEARCH RESULT ENTRY" ) ) );

        assertThat( text, containsString( "#!SEARCH RESULT DONE " ) );
        assertThat( text, containsString( "# numEntries : 8" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchLogsViewWithSearchResultEntryLogsEnabled( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        searchLogsViewBot.clear();

        // enable search result entry logs
        searchLogsViewBot.enableSearchResultEntryLogs( true );

        // select entry (ou=groups)
        browserViewBot.selectEntry( path( GROUPS_DN ) );

        // assert content
        String text = searchLogsViewBot.getSearchLogsText();

        assertThat( text, containsString( "#!SEARCH REQUEST " ) );
        assertThat( text, containsString( "#!CONNECTION " + server.getLdapUrl() ) );
        assertThat( text, containsString( "#!DATE " ) );
        assertThat( text, containsString(
            "# LDAP URL     : " + server.getLdapUrl() + "/ou=groups,dc=example,dc=org?*??(objectClass=*)" ) );
        assertThat( text, containsString( "# command line : ldapsearch -H " + server.getLdapUrl() + " -x -D \""
            + server.getAdminDn()
            + "\" -W -b \"ou=groups,dc=example,dc=org\" -s base -a always \"(objectClass=*)\" \"*\"" ) );
        assertThat( text, containsString( "# baseObject   : ou=groups,dc=example,dc=org" ) );
        assertThat( text, containsString( "# scope        : baseObject (0)" ) );
        assertThat( text, containsString( "# derefAliases : derefAlways (3)" ) );
        assertThat( text, containsString( "# sizeLimit    : 0" ) );
        assertThat( text, containsString( "# timeLimit    : 0" ) );
        assertThat( text, containsString( "# typesOnly    : False" ) );
        assertThat( text, containsString( "# filter       : (objectClass=*)" ) );
        assertThat( text, containsString( "# attributes   : *" ) );

        assertThat( text, containsString( "#!SEARCH RESULT ENTRY" ) );
        assertThat( text, containsString( "dn: ou=groups,dc=example,dc=org" ) );
        assertThat( text, containsString( "objectClass: top" ) );
        assertThat( text, containsString( "objectClass: organizationalUnit" ) );
        assertThat( text, containsString( "ou: groups" ) );

        assertThat( text, containsString( "#!SEARCH RESULT DONE " ) );
        assertThat( text, containsString( "# numEntries : 1" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchLogsViewWithSearchResultLogsDisabled( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        searchLogsViewBot.clear();

        // disable search request logs
        searchLogsViewBot.enableSearchRequestLogs( false );

        // select entry (ou=groups)
        browserViewBot.selectEntry( path( GROUPS_DN ) );

        // assert content
        assertTrue( searchLogsViewBot.getSearchLogsText().isEmpty() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testSearchLogsViewClear( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        assertFalse( searchLogsViewBot.getSearchLogsText().isEmpty() );

        searchLogsViewBot.clear();

        assertTrue( searchLogsViewBot.getSearchLogsText().isEmpty() );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSearchLogsViewLogFileRotationAndNavigation( TestLdapServer server ) throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT, 4 );
        prefs.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE, 1 );
        searchLogsViewBot.enableSearchResultEntryLogs( true );

        connectionsViewBot.createTestConnection( server );
        searchLogsViewBot.clear();

        // initial state: empty log view and older/newer buttons are disabled
        assertTrue( searchLogsViewBot.getSearchLogsText().isEmpty() );
        assertFalse( searchLogsViewBot.isOlderButtonEnabled() );
        assertFalse( searchLogsViewBot.isNewerButtonEnabled() );

        // make some searches
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.selectEntry( path( USER2_DN ) );
        browserViewBot.selectEntry( path( USER3_DN ) );
        browserViewBot.selectEntry( path( USER4_DN ) );
        browserViewBot.selectEntry( path( USER5_DN ) );

        // assert status of newest page (1)
        assertTrue( searchLogsViewBot.isOlderButtonEnabled() );
        assertFalse( searchLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers1 = getRequestStats( searchLogsViewBot.getSearchLogsText() );

        // go to older page (2) and assert status
        searchLogsViewBot.clickOlderButton();

        assertTrue( searchLogsViewBot.isOlderButtonEnabled() );
        assertTrue( searchLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers2 = getRequestStats( searchLogsViewBot.getSearchLogsText() );
        assertTrue( requestNumbers1.getMin() >= requestNumbers2.getMin() );
        assertTrue( requestNumbers1.getMax() >= requestNumbers2.getMax() );

        // go to older page (3) and assert status
        searchLogsViewBot.clickOlderButton();

        assertTrue( searchLogsViewBot.isOlderButtonEnabled() );
        assertTrue( searchLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers3 = getRequestStats( searchLogsViewBot.getSearchLogsText() );
        assertTrue( requestNumbers2.getMin() >= requestNumbers3.getMin() );
        assertTrue( requestNumbers2.getMax() >= requestNumbers3.getMax() );

        // go to older page (4) and assert status
        searchLogsViewBot.clickOlderButton();

        assertFalse( searchLogsViewBot.isOlderButtonEnabled() );
        assertTrue( searchLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers4 = getRequestStats( searchLogsViewBot.getSearchLogsText() );
        assertTrue( requestNumbers3.getMin() >= requestNumbers4.getMin() );
        assertTrue( requestNumbers3.getMax() >= requestNumbers4.getMax() );

        // go back to newest page (1) and assert status
        searchLogsViewBot.clickNewerButton();
        searchLogsViewBot.clickNewerButton();
        searchLogsViewBot.clickNewerButton();

        assertTrue( searchLogsViewBot.isOlderButtonEnabled() );
        assertFalse( searchLogsViewBot.isNewerButtonEnabled() );

        // reduce file count and assert extra files were deleted
        prefs.putInt( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT, 2 );

        assertTrue( searchLogsViewBot.isOlderButtonEnabled() );
        assertFalse( searchLogsViewBot.isNewerButtonEnabled() );
        searchLogsViewBot.clickOlderButton();
        assertFalse( searchLogsViewBot.isOlderButtonEnabled() );
        assertTrue( searchLogsViewBot.isNewerButtonEnabled() );
        searchLogsViewBot.clickNewerButton();
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testSearchLogsViewLogMaskAttributes( TestLdapServer server ) throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.put( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES,
            "userPassword,employeeNumber" );
        searchLogsViewBot.enableSearchResultEntryLogs( true );

        connectionsViewBot.createTestConnection( server );
        searchLogsViewBot.clear();

        browserViewBot.selectEntry( path( USER1_DN ) );

        String text = searchLogsViewBot.getSearchLogsText();

        assertThat( text, containsString( "userPassword: **********" ) );
        assertThat( text, not( containsString( "userPassword:: " ) ) );
        assertThat( text, containsString( "employeeNumber: **********" ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModificationLogsViewModificationLogsDisabled( TestLdapServer server ) throws Exception
    {
        // create and open connection
        connectionsViewBot.createTestConnection( server );
        modificationLogsViewBot.clear();

        // disable modification logs
        modificationLogsViewBot.enableModificationLogs( false );

        makeModifications();

        // assert content
        assertTrue( modificationLogsViewBot.getModificationLogsText().isEmpty() );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModificationLogsViewDefault( TestLdapServer server ) throws Exception
    {
        // create and open connection
        connectionsViewBot.createTestConnection( server );
        modificationLogsViewBot.clear();

        makeModifications();

        // assert content (ou=users)
        String text = modificationLogsViewBot.getModificationLogsText();

        assertThat( text, containsString( "#!RESULT OK" ) );
        assertThat( text, containsString( "#!CONNECTION " + server.getLdapUrl() ) );
        assertThat( text, containsString( "#!DATE " ) );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( USERS_DN.getRdn(), TARGET_DN ), "changetype: add" );
        modificationLogsViewBot.assertContainsOk( "dn: " + dn( USERS_DN.getRdn(), TARGET_DN ), "changetype: delete" );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testModificationLogsViewClear( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        assertTrue( modificationLogsViewBot.getModificationLogsText().isEmpty() );

        makeModifications();

        assertFalse( modificationLogsViewBot.getModificationLogsText().isEmpty() );

        modificationLogsViewBot.clear();

        assertTrue( modificationLogsViewBot.getModificationLogsText().isEmpty() );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testModificationLogsViewLogFileRotationAndNavigation( TestLdapServer server ) throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.putInt( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT, 3 );
        prefs.putInt( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE, 2 );

        connectionsViewBot.createTestConnection( server );
        modificationLogsViewBot.clear();

        // initial state: empty log view and older/newer buttons are disabled
        assertTrue( modificationLogsViewBot.getModificationLogsText().isEmpty() );
        assertFalse( modificationLogsViewBot.isOlderButtonEnabled() );
        assertFalse( modificationLogsViewBot.isNewerButtonEnabled() );

        makeModifications();

        // assert status of newest page (1)
        assertTrue( modificationLogsViewBot.isOlderButtonEnabled() );
        assertFalse( modificationLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers1 = getRequestStats( modificationLogsViewBot.getModificationLogsText() );

        // go to older page (2) and assert status
        modificationLogsViewBot.clickOlderButton();

        assertTrue( modificationLogsViewBot.isOlderButtonEnabled() );
        assertTrue( modificationLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers2 = getRequestStats( modificationLogsViewBot.getModificationLogsText() );
        assertTrue( requestNumbers1.getMin() >= requestNumbers2.getMin() );
        assertTrue( requestNumbers1.getMax() >= requestNumbers2.getMax() );

        // go to older page (3) and assert status
        modificationLogsViewBot.clickOlderButton();

        assertFalse( modificationLogsViewBot.isOlderButtonEnabled() );
        assertTrue( modificationLogsViewBot.isNewerButtonEnabled() );
        IntSummaryStatistics requestNumbers3 = getRequestStats( modificationLogsViewBot.getModificationLogsText() );
        assertTrue( requestNumbers2.getMin() >= requestNumbers3.getMin() );
        assertTrue( requestNumbers2.getMax() >= requestNumbers3.getMax() );

        // go back to newest page (1) and assert status
        modificationLogsViewBot.clickNewerButton();
        modificationLogsViewBot.clickNewerButton();

        assertTrue( modificationLogsViewBot.isOlderButtonEnabled() );
        assertFalse( modificationLogsViewBot.isNewerButtonEnabled() );
    }


    @ParameterizedTest
    @LdapServersSource(mode = Mode.All)
    public void testModificationLogsViewMaskAttributes( TestLdapServer server ) throws Exception
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.put( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_MASKED_ATTRIBUTES,
            "userPassword,employeeNumber" );
        searchLogsViewBot.enableSearchResultEntryLogs( true );

        connectionsViewBot.createTestConnection( server );
        modificationLogsViewBot.clear();

        makeModifications();

        String text = modificationLogsViewBot.getModificationLogsText();

        assertThat( text, containsString( "userPassword: **********" ) );
        assertThat( text, not( containsString( "userPassword:: " ) ) );
        assertThat( text, containsString( "employeeNumber: **********" ) );
    }


    private void makeModifications()
    {
        browserViewBot.selectEntry( path( USERS_DN ) );
        browserViewBot.copy();
        browserViewBot.selectEntry( path( TARGET_DN ) );
        SelectCopyDepthDialogBot dialog = browserViewBot.pasteEntriesExpectingSelectCopyDepthDialog( 1 );
        dialog.selectSubTree();
        dialog.clickOkButton();
        browserViewBot.selectEntry( path( TARGET_DN, USERS_DN.getRdn() ) );
        browserViewBot.openDeleteDialog().clickOkButton();
    }


    private static IntSummaryStatistics getRequestStats( String text )
    {
        Pattern requestNumberPattern = Pattern.compile( "^#!.+\\((\\d+)\\) OK$", Pattern.MULTILINE );
        Matcher matcher = requestNumberPattern.matcher( text );
        List<String> matches = new ArrayList<>();
        while ( matcher.find() )
        {
            matches.add( matcher.group( 1 ) );
        }

        return matches.stream().mapToInt( Integer::parseInt ).summaryStatistics();
    }
}
