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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.ALIAS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.CONTEXT_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_IP_HOST_NUMBER;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_TRAILING_EQUALS_CHARACTER;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_TRAILING_EQUALS_CHARACTER_HEX_PAIR_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MULTI_VALUED_RDN_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.REFERRAL_TO_USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER2_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER3_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.USERS_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Bookmark;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldapbrowser.ui.editors.entry.EntryEditor;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.DeleteDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.EntryEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ReferralDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PlatformUI;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the LDAP browser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class BrowserTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-463.
     *
     * When expanding an entry in the browser only one search request
     * should be send to the server
     */
    @ParameterizedTest
    @LdapServersSource
    public void testOnlyOneSearchRequestWhenExpandingEntry( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( CONTEXT_DN ) );

        // get number of search requests before expanding the entry
        String text = searchLogsViewBot.getSearchLogsText();
        int countMatchesBefore = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        // expand
        browserViewBot.expandEntry( path( CONTEXT_DN ) );
        browserViewBot.waitForEntry( path( USERS_DN ) );

        // get number of search requests after expanding the entry
        text = searchLogsViewBot.getSearchLogsText();
        int countMatchesAfter = StringUtils.countMatches( text, "#!SEARCH REQUEST" );

        assertEquals( "Expected exactly 1 search request", 1, countMatchesAfter - countMatchesBefore );

        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-512.
     *
     * Verify minimum UI updates when deleting multiple entries.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testDeleteDoesNotUpdateUI( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USERS_DN ) );
        browserViewBot.expandEntry( path( USERS_DN ) );

        long fireCount0 = EventRegistry.getFireCount();

        // delete
        String[] children = new String[]
            {
                "uid=user.1",
                "uid=user.2",
                "uid=user.3",
                "uid=user.4",
                "uid=user.5",
                "uid=user.6",
                "uid=user.7",
                "uid=user.8" };
        browserViewBot.selectChildrenOfEntry( children, path( USERS_DN ) );
        DeleteDialogBot deleteDialog = browserViewBot.openDeleteDialog();
        deleteDialog.clickOkButton();
        browserViewBot.selectEntry( path( USERS_DN ) );

        long fireCount1 = EventRegistry.getFireCount();

        // verify that only two events were fired during deletion
        long fireCount = fireCount1 - fireCount0;
        assertEquals( "Only 2 event firings expected when deleting multiple entries.", 2, fireCount );
    }


    /**
     * Test for DIRSTUDIO-575.
     *
     * When opening a bookmark the entry editor should be opened and the
     * bookmark entry's attributes should be displayed.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBookmark( TestLdapServer server ) throws Exception
    {
        // create a bookmark
        Connection connection = connectionsViewBot.createTestConnection( server );
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.getBookmarkManager().addBookmark(
            new Bookmark( browserConnection, USER1_DN, "Existing Bookmark" ) );

        // select the bookmark
        browserViewBot.selectEntry( "Bookmarks", "Existing Bookmark" );

        // check that entry editor was opened and attributes are visible
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        String dn = entryEditorBot.getDnText();
        assertEquals( "DN: " + USER1_DN.getName(), dn );
        List<String> attributeValues = entryEditorBot.getAttributeValues();
        assertEquals( 23, attributeValues.size() );
        assertTrue( attributeValues.contains( "uid: user.1" ) );

        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRefreshParent( TestLdapServer server ) throws Exception
    {
        // check the entry doesn't exist yet
        connectionsViewBot.createTestConnection( server );
        browserViewBot.expandEntry( path( MISC_DN ) );
        Dn refreshDn = dn( "cn=refresh", MISC_DN );
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );

        // add the entry directly in the server
        server.withAdminConnection( conn -> {
            Entry entry = new DefaultEntry( conn.getSchemaManager() );
            entry.setDn( refreshDn );
            entry.add( "objectClass", "top", "person" );
            entry.add( "cn", "refresh" );
            entry.add( "sn", "refresh" );
            conn.add( entry );
        } );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );

        // refresh parent
        browserViewBot.selectEntry( path( MISC_DN ) );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( path( MISC_DN ) );
        assertTrue( browserViewBot.existsEntry( path( refreshDn ) ) );
        browserViewBot.selectEntry( path( refreshDn ) );

        // delete the entry directly in the server
        server.withAdminConnection( conn -> {
            conn.delete( refreshDn );
        } );

        // check the entry still is visible in the tree
        assertTrue( browserViewBot.existsEntry( path( refreshDn ) ) );

        // refresh parent
        browserViewBot.selectEntry( path( MISC_DN ) );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( path( MISC_DN ) );
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     *
     * @throws Exception
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRefreshContextEntry( TestLdapServer server ) throws Exception
    {
        // check the entry doesn't exist yet
        connectionsViewBot.createTestConnection( server );
        browserViewBot.expandEntry( path( MISC_DN ) );
        Dn refreshDn = dn( "cn=refresh", MISC_DN );
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );

        // add the entry directly in the server
        server.withAdminConnection( conn -> {
            Entry entry = new DefaultEntry( conn.getSchemaManager() );
            entry.setDn( refreshDn );
            entry.add( "objectClass", "top", "person" );
            entry.add( "cn", "refresh" );
            entry.add( "sn", "refresh" );
            conn.add( entry );
        } );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );

        // refresh context entry
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( path( MISC_DN ) );
        assertTrue( browserViewBot.existsEntry( path( refreshDn ) ) );
        browserViewBot.selectEntry( path( refreshDn ) );

        // delete the entry directly in the server
        server.withAdminConnection( connection -> {
            connection.delete( refreshDn );
        } );

        // check the entry still is visible in the tree
        assertTrue( browserViewBot.existsEntry( path( refreshDn ) ) );

        // refresh context entry
        browserViewBot.selectEntry( path( CONTEXT_DN ) );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( path( MISC_DN ) );
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRefreshRootDSE( TestLdapServer server ) throws Exception
    {
        // check the entry doesn't exist yet
        connectionsViewBot.createTestConnection( server );
        browserViewBot.expandEntry( path( MISC_DN ) );
        Dn refreshDn = dn( "cn=refresh", MISC_DN );
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );

        // add the entry directly in the server
        server.withAdminConnection( connection -> {
            Entry entry = new DefaultEntry( connection.getSchemaManager() );
            entry.setDn( refreshDn );
            entry.add( "objectClass", "top", "person" );
            entry.add( "cn", "refresh" );
            entry.add( "sn", "refresh" );
            connection.add( entry );
        } );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );

        // refresh Root DSE
        browserViewBot.selectEntry( ROOT_DSE_PATH );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( path( MISC_DN ) );
        assertTrue( browserViewBot.existsEntry( path( refreshDn ) ) );
        browserViewBot.selectEntry( path( refreshDn ) );

        // delete the entry directly in the server
        server.withAdminConnection( connection -> {
            connection.delete( refreshDn );
        } );

        // check the entry still is now visible in the tree
        assertTrue( browserViewBot.existsEntry( path( refreshDn ) ) );

        // refresh Root DSE
        browserViewBot.selectEntry( ROOT_DSE_PATH );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( path( MISC_DN ) );
        assertFalse( browserViewBot.existsEntry( path( refreshDn ) ) );
    }


    /**
     * Test for DIRSTUDIO-481.
     *
     * Check proper operation of refresh action.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRefreshSearchContinuation( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        Dn refreshDn = dn( "cn=refresh", MISC_DN );
        String[] pathToReferral = pathWithRefLdapUrl( server, MISC_DN );
        String[] pathToRefreshViaReferral = path( pathToReferral, "cn=refresh" );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_REFERRALS_HANDLING_METHOD,
            ReferralHandlingMethod.FOLLOW_MANUALLY.ordinal() );
        browserViewBot.selectEntry( ROOT_DSE_PATH );
        browserViewBot.refresh();

        // check the entry doesn't exist yet
        ReferralDialogBot refDialog = browserViewBot.expandEntryExpectingReferralDialog( pathToReferral );
        refDialog.clickOkButton();
        assertFalse( browserViewBot.existsEntry( pathToRefreshViaReferral ) );

        // add the entry directly in the server
        server.withAdminConnection( conn -> {
            Entry entry = new DefaultEntry( conn.getSchemaManager() );
            entry.setDn( refreshDn );
            entry.add( "objectClass", "top", "person" );
            entry.add( "cn", "refresh" );
            entry.add( "sn", "refresh" );
            conn.add( entry );
        } );

        // check the entry still isn't visible in the tree
        assertFalse( browserViewBot.existsEntry( pathToRefreshViaReferral ) );

        // refresh search continuation
        browserViewBot.selectEntry( pathToReferral );
        browserViewBot.refresh();

        // check the entry exists now
        browserViewBot.expandEntry( pathToReferral );
        assertTrue( browserViewBot.existsEntry( pathToRefreshViaReferral ) );
        browserViewBot.selectEntry( pathToRefreshViaReferral );

        // delete the entry directly in the server
        server.withAdminConnection( conn -> {
            conn.delete( refreshDn );
        } );

        // check the entry still is visible in the tree
        assertTrue( browserViewBot.existsEntry( pathToRefreshViaReferral ) );

        // refresh search continuation
        browserViewBot.selectEntry( pathToReferral );
        browserViewBot.refresh();

        // check the entry doesn't exist now
        browserViewBot.expandEntry( pathToReferral );
        assertFalse( browserViewBot.existsEntry( pathToRefreshViaReferral ) );
    }


    /**
     * Test for DIRSTUDIO-591.
     * (Error reading objects with # in DN)
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseDnWithSharpAndHexSequence( TestLdapServer server ) throws Exception
    {
        Dn dn = DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            dn = DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
        }

        connectionsViewBot.createTestConnection( server );
        assertTrue( browserViewBot.existsEntry( path( dn ) ) );
        browserViewBot.selectEntry( path( dn ) );

        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-1172: Studio doesn't display entries with trailing =.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseDnWithTrailingEqualsCharacter( TestLdapServer server ) throws Exception
    {
        Dn dn = DN_WITH_TRAILING_EQUALS_CHARACTER;
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            dn = DN_WITH_TRAILING_EQUALS_CHARACTER_HEX_PAIR_ESCAPED;
        }

        connectionsViewBot.createTestConnection( server );

        assertTrue( browserViewBot.existsEntry( path( dn ) ) );
        browserViewBot.selectEntry( path( dn ) );
    }


    /**
     * Test for DIRSTUDIO-1172: Studio doesn't display entries with trailing =.
     */
    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    // Empty RDN value is not supported by OpenLDAP and 389ds
    public void testBrowseDnWithEmptyRdnValue( TestLdapServer server ) throws Exception
    {

        Dn dn = dn( "cn=nghZwwtHgxgyvVbTQCYyeY+email=", MISC_DN );

        server.withAdminConnection( connection -> {
            Entry entry = new DefaultEntry( connection.getSchemaManager() );
            entry.setDn( dn );
            entry.add( "objectClass", "top", "person", "extensibleObject" );
            entry.add( "cn", "nghZwwtHgxgyvVbTQCYyeY" );
            entry.add( "sn", "nghZwwtHgxgyvVbTQCYyeY" );
            entry.add( "email", "" );
            connection.add( entry );
        } );

        connectionsViewBot.createTestConnection( server );

        assertTrue( browserViewBot.existsEntry( path( dn ) ) );
        browserViewBot.selectEntry( path( dn ) );
    }


    /**
     * Test for DIRSTUDIO-1151: DN with backslash not displayed
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseDnWithBackslash( TestLdapServer server ) throws Exception
    {
        Dn dn = DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED;
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            dn = DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED;
        }

        connectionsViewBot.createTestConnection( server );

        // expand parent and verify entry is visible
        browserViewBot.expandEntry( path( dn.getParent() ) );
        assertTrue( browserViewBot.existsEntry( path( dn ) ) );
        browserViewBot.selectEntry( path( dn ) );

        // refresh entry and verify child is still visible
        browserViewBot.selectEntry( path( dn.getParent() ) );
        browserViewBot.refresh();
        assertTrue( browserViewBot.existsEntry( path( dn ) ) );
    }


    /**
     * Test for DIRSTUDIO-597.
     * (Modification sent to the server while browsing through the DIT and refreshing entries)
     *
     * @throws Exception
     */
    @ParameterizedTest
    @LdapServersSource
    public void testNoModificationWhileBrowsingAndRefreshing( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        boolean errorDialogAutomatedMode = ErrorDialog.AUTOMATED_MODE;
        ErrorDialog.AUTOMATED_MODE = false;

        String text = modificationLogsViewBot.getModificationLogsText();
        assertEquals( "", text );

        try
        {
            assertTrue( browserViewBot.existsEntry( path( MULTI_VALUED_RDN_DN ) ) );

            for ( int i = 0; i < 5; i++ )
            {
                // select entry and refresh
                browserViewBot.selectEntry( path( MULTI_VALUED_RDN_DN ) );
                browserViewBot.refresh();

                // select parent and refresh
                browserViewBot.selectEntry( path( MULTI_VALUED_RDN_DN.getParent() ) );
                browserViewBot.refresh();
            }
        }
        finally
        {
            // reset flag
            ErrorDialog.AUTOMATED_MODE = errorDialogAutomatedMode;
        }

        // check that modification logs is still empty
        // to ensure that no modification was sent to the server
        assertEquals( "No modification expected", "", modificationLogsViewBot.getModificationLogsText() );
    }


    /**
     * Test for DIRSTUDIO-603, DIRSHARED-41.
     * (Error browsing/entering rfc2307 compliant host entry.)
     */
    @ParameterizedTest
    @LdapServersSource
    public void testBrowseDnWithIpHostNumber( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        assertTrue( browserViewBot.existsEntry( path( DN_WITH_IP_HOST_NUMBER ) ) );
        browserViewBot.selectEntry( path( DN_WITH_IP_HOST_NUMBER ) );
    }


    /**
     * DIRSTUDIO-637: copy/paste of attributes no longer works.
     * Test copy/paste of a value to a bookmark.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testCopyPasteValueToBookmark( TestLdapServer server ) throws Exception
    {
        // create a bookmark
        Connection connection = connectionsViewBot.createTestConnection( server );
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.getBookmarkManager().addBookmark(
            new Bookmark( browserConnection, MULTI_VALUED_RDN_DN, "My Bookmark" ) );

        // copy a value
        browserViewBot.selectEntry( path( USER1_DN ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        entryEditorBot.activate();
        entryEditorBot.copyValue( "uid", "user.1" );

        // select the bookmark
        browserViewBot.selectEntry( "Bookmarks", "My Bookmark" );
        entryEditorBot = studioBot.getEntryEditorBot( MULTI_VALUED_RDN_DN.getName() );
        entryEditorBot.activate();
        assertEquals( 8, entryEditorBot.getAttributeValues().size() );

        // paste the value
        JobWatcher watcher = new JobWatcher( BrowserCoreMessages.jobs__execute_ldif_name );
        browserViewBot.paste();
        watcher.waitUntilDone();

        // assert pasted value visible in editor
        assertEquals( 9, entryEditorBot.getAttributeValues().size() );
        entryEditorBot.getAttributeValues().contains( "uid: user.1" );

        // assert pasted value was written to directory
        server.withAdminConnection( conn -> {
            Entry entry = conn.lookup( MULTI_VALUED_RDN_DN );
            assertTrue( entry.contains( "uid", "user.1" ) );
        } );
    }


    /**
     * Test for DIRSTUDIO-1121.
     *
     * Verify input is set only once when entry is selected.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testSetInputOnlyOnce( TestLdapServer server ) throws Exception
    {
        /*
         * This test fails on Jenkins Windows Server, to be investigated...
         */
        // Assume.assumeFalse( StudioSystemUtils.IS_OS_WINDOWS_SERVER );
        connectionsViewBot.createTestConnection( server );

        browserViewBot.selectEntry( path( USERS_DN ) );
        browserViewBot.expandEntry( path( USERS_DN ) );

        // verify link-with-editor is enabled
        assertTrue( BrowserUIPlugin.getDefault().getPreferenceStore()
            .getBoolean( BrowserUIConstants.PREFERENCE_BROWSER_LINK_WITH_EDITOR ) );

        // setup counter and listener to record entry editor input changes
        final AtomicInteger counter = new AtomicInteger();
        UIThreadRunnable.syncExec( new VoidResult()
        {
            public void run()
            {
                try
                {
                    IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .getActiveEditor();
                    editor.addPropertyListener( new IPropertyListener()
                    {
                        @Override
                        public void propertyChanged( Object source, int propId )
                        {
                            if ( source instanceof EntryEditor && propId == BrowserUIConstants.INPUT_CHANGED )
                            {
                                counter.incrementAndGet();
                            }
                        }
                    } );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            }
        } );

        // select 3 different entries, select one twice should not set the input again
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.selectEntry( path( USER1_DN ) );
        browserViewBot.selectEntry( path( USER2_DN ) );
        browserViewBot.selectEntry( path( USER2_DN ) );
        browserViewBot.selectEntry( path( USER3_DN ) );
        browserViewBot.selectEntry( path( USER3_DN ) );

        // verify that input was only set 3 times.
        assertEquals( "Only 3 input changes expected.", 3, counter.get() );

        // reset counter
        counter.set( 0 );

        // use navigation history to go back and forth, each step should set input only once
        studioBot.navigationHistoryBack();
        browserViewBot.waitUntilEntryIsSelected( USER2_DN.getRdn().getName() );
        studioBot.navigationHistoryBack();
        browserViewBot.waitUntilEntryIsSelected( USER1_DN.getRdn().getName() );
        studioBot.navigationHistoryForward();
        browserViewBot.waitUntilEntryIsSelected( USER2_DN.getRdn().getName() );
        studioBot.navigationHistoryForward();
        browserViewBot.waitUntilEntryIsSelected( USER3_DN.getRdn().getName() );

        // verify that input was only set 4 times.
        assertEquals( "Only 4 input changes expected.", 4, counter.get() );
    }


    /**
     * Test for DIRSTUDIO-987, DIRSTUDIO-271.
     *
     * Browse and refresh entry with multi-valued RDN with same attribute type.
     */
    @ParameterizedTest
    @LdapServersSource(types =
        { LdapServerType.ApacheDS, LdapServerType.Fedora389ds })
    // Multi-valued RDN with same attribute is not suupported by OpenLDAP
    public void testBrowseAndRefreshEntryWithMvRdn( TestLdapServer server ) throws Exception
    {
        Dn entryDn = dn( "l=Berlin+l=Brandenburger Tor+l=de+l=eu", MISC_DN );
        Dn childDn = dn( "cn=A", entryDn );

        server.withAdminConnection( connection -> {
            Entry entry1 = new DefaultEntry( connection.getSchemaManager() );
            entry1.setDn( entryDn );
            entry1.add( "objectClass", "top", "locality" );
            entry1.add( "l", "eu", "de", "Berlin", "Brandenburger Tor" );
            connection.add( entry1 );

            Entry entry2 = new DefaultEntry( connection.getSchemaManager() );
            entry2.setDn( childDn );
            entry2.add( "objectClass", "top", "person" );
            entry2.add( "cn", "A" );
            entry2.add( "sn", "A" );
            connection.add( entry2 );
        } );

        String[] pathToParent = path( entryDn.getParent() );
        String[] pathToEntry = path( entryDn );
        String[] pathToChild = path( childDn );

        connectionsViewBot.createTestConnection( server );

        // expand parent and verify entry is visible
        browserViewBot.expandEntry( pathToParent );
        assertTrue( browserViewBot.existsEntry( pathToEntry ) );
        browserViewBot.selectEntry( pathToEntry );

        // expand entry and verify child is visible
        browserViewBot.expandEntry( pathToEntry );
        assertTrue( browserViewBot.existsEntry( pathToChild ) );
        browserViewBot.selectEntry( pathToChild );

        // refresh entry and verify child is still visible
        browserViewBot.selectEntry( pathToEntry );
        browserViewBot.refresh();
        assertTrue( browserViewBot.existsEntry( pathToChild ) );

        // refresh parent and verify entry is still visible
        browserViewBot.selectEntry( pathToParent );
        browserViewBot.refresh();
        assertTrue( browserViewBot.existsEntry( pathToEntry ) );

        // expand entry and verify child is visible
        browserViewBot.expandEntry( pathToEntry );
        assertTrue( browserViewBot.existsEntry( pathToChild ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testBrowseAliasEntry( TestLdapServer server ) throws Exception
    {
        // disable alias dereferencing
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_ALIASES_DEREFERENCING_METHOD,
            AliasDereferencingMethod.NEVER.ordinal() );

        browserViewBot.expandEntry( path( ALIAS_DN.getParent() ) );
        assertTrue( browserViewBot.existsEntry( path( ALIAS_DN ) ) );
        browserViewBot.selectEntry( path( ALIAS_DN ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testBrowseReferralEntry( TestLdapServer server ) throws Exception
    {
        // enable ManageDsaIT control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_MANAGE_DSA_IT, true );

        browserViewBot.expandEntry( path( REFERRAL_TO_USERS_DN.getParent() ) );
        assertTrue( browserViewBot.existsEntry( path( REFERRAL_TO_USERS_DN ) ) );
        browserViewBot.selectEntry( path( REFERRAL_TO_USERS_DN ) );
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.ApacheDS)
    public void testBrowseSubEntry( TestLdapServer server ) throws Exception
    {
        Dn subentryDn = dn( "cn=subentry", MISC_DN );

        // enable Subentries control
        Connection connection = connectionsViewBot.createTestConnection( server );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_FETCH_SUBENTRIES, true );

        browserViewBot.expandEntry( path( subentryDn.getParent() ) );
        assertTrue( browserViewBot.existsEntry( path( subentryDn ) ) );
        browserViewBot.selectEntry( path( subentryDn ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testBrowseWithPagingWithScrollMode( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USERS_DN ) );

        // enable Simple Paged Results control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH, true );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE, 3 );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE, true );

        // 1st page
        browserViewBot.expandEntry( path( USERS_DN ) );
        assertFalse( browserViewBot.existsEntry( path( USERS_DN, "--- Top Page ---" ) ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "--- Next Page ---" ) ) );

        // next page
        browserViewBot.selectEntry( path( USERS_DN, "--- Next Page ---" ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "--- Top Page ---" ) ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "--- Next Page ---" ) ) );

        // last page
        browserViewBot.selectEntry( path( USERS_DN, "--- Next Page ---" ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "--- Top Page ---" ) ) );
        assertFalse( browserViewBot.existsEntry( path( USERS_DN, "--- Next Page ---" ) ) );

        // back to top
        browserViewBot.selectEntry( path( USERS_DN, "--- Top Page ---" ) );
        assertFalse( browserViewBot.existsEntry( path( USERS_DN, "--- Top Page ---" ) ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "--- Next Page ---" ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testBrowseWithPagingWithoutScrollMode( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( USERS_DN ) );

        // enable Simple Paged Results control
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH, true );
        connection.getConnectionParameter().setExtendedIntProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SIZE, 3 );
        connection.getConnectionParameter().setExtendedBoolProperty(
            IBrowserConnection.CONNECTION_PARAMETER_PAGED_SEARCH_SCROLL_MODE, false );

        browserViewBot.expandEntry( path( USERS_DN ) );
        assertFalse( browserViewBot.existsEntry( path( USERS_DN, "--- Top Page ---" ) ) );
        assertFalse( browserViewBot.existsEntry( path( USERS_DN, "--- Next Page ---" ) ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN ) ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "uid=user.1" ) ) );
        assertTrue( browserViewBot.existsEntry( path( USERS_DN, "uid=user.8" ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void x( TestLdapServer server ) throws Exception
    {
        connectionsViewBot.createTestConnection( server );

        browserViewBot.selectEntry( path( USERS_DN ) );
        browserViewBot.expandEntry( path( USERS_DN ) );

        browserViewBot.selectEntry( path( USER1_DN ) );
        EntryEditorBot entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        List<String> attributeValues = entryEditorBot.getAttributeValues();
        assertEquals( 23, attributeValues.size() );
        assertTrue( attributeValues.contains( "uid: user.1" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "initials: AA" ) );

        DeleteDialogBot deleteDialog = browserViewBot.openDeleteDialog();
        deleteDialog.clickOkButton();
        browserViewBot.selectEntry( path( USERS_DN ) );
        assertFalse( browserViewBot.existsEntry( path( USER1_DN ) ) );

        server.withAdminConnection( conn -> {
            Entry entry = new DefaultEntry( conn.getSchemaManager() );
            entry.setDn( USER1_DN );
            entry.add( "objectClass", "top", "person", "organizationalPerson", "inetOrgPerson" );
            entry.add( "uid", "user.1" );
            entry.add( "givenName", "Foo" );
            entry.add( "sn", "Bar" );
            entry.add( "cn", "Foo Bar" );
            entry.add( "initials", "FB" );
            conn.add( entry );
        } );

        browserViewBot.refresh();
        assertTrue( browserViewBot.existsEntry( path( USER1_DN ) ) );

        browserViewBot.selectEntry( path( USER1_DN ) );
        entryEditorBot = studioBot.getEntryEditorBot( USER1_DN.getName() );
        attributeValues = entryEditorBot.getAttributeValues();
        assertEquals( 9, attributeValues.size() );
        assertTrue( attributeValues.contains( "uid: user.1" ) );
        assertTrue( entryEditorBot.getAttributeValues().contains( "initials: FB" ) );
    }

}
