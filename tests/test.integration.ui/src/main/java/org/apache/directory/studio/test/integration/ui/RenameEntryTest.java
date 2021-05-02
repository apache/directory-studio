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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MULTI_VALUED_RDN_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.directory.api.ldap.model.name.Ava;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.RenameEntryDialogBot;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests entry renaming (modrdn) and the rename dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class RenameEntryTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-318.
     *
     * Renames a multi-valued RDN by changing both RDN attributes.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRenameMultiValuedRdn( TestLdapServer server ) throws Exception
    {
        Dn oldDn = MULTI_VALUED_RDN_DN;
        Rdn newRdn = new Rdn( new Ava( "cn", "Babs Jensen" ), new Ava( "uid", "dj" ) );
        Dn newDn = dn( newRdn, oldDn.getParent() );

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        for ( int i = 0; i < newRdn.size(); i++ )
        {
            renameDialogBot.setRdnType( i + 1, newRdn.getAva( i ).getType() );
            renameDialogBot.setRdnValue( i + 1, newRdn.getAva( i ).getValue().getString() );
        }
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn ) ) );
        browserViewBot.selectEntry( path( newDn ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );
    }


    /**
     * Test for DIRSTUDIO-484.
     *
     * Renames a RDN with escaped characters.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRenameRdnWithEscapedCharacters( TestLdapServer server ) throws Exception
    {
        Dn oldDn = DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED;
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            oldDn = DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED;
        }
        Dn newDn = dn( oldDn.getRdn().getName() + " renamed", oldDn.getParent() );

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, newDn.getRdn().getValue() );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn ) ) );
        browserViewBot.selectEntry( path( newDn ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );
    }


    /**
     * Test for DIRSTUDIO-589, DIRSTUDIO-591, DIRSHARED-38.
     *
     * Rename an entry with leading sharp in DN: cn=\#123456.
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRenameRdnWithLeadingSharp( TestLdapServer server ) throws Exception
    {
        Dn oldDn = DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
        Dn newDn = dn( "cn=\\#ABCDEF", oldDn.getParent() );
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            oldDn = DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
            newDn = dn( "cn=\\23ABCDEF", oldDn.getParent() );
        }

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#ABCDEF" );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn ) ) );
        browserViewBot.selectEntry( path( newDn ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );
    }


    /**
     * Test for DIRSHARED-39.
     *
     * Rename an entry with leading and trailing space in RDN.
     */
    @ParameterizedTest
    @LdapServersSource(except = LdapServerType.Fedora389ds, reason = "Leading and trailing space is trimmed by 389ds")
    public void testRenameRdnWithTrailingSpace( TestLdapServer server ) throws Exception
    {
        Dn oldDn = DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
        Dn newDn = dn( "cn=\\#ABCDEF\\ ", oldDn.getParent() );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            oldDn = DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
            newDn = dn( "cn=\\23ABCDEF\\20", oldDn.getParent() );
        }

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#ABCDEF " );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn ) ) );
        browserViewBot.selectEntry( path( newDn ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );

    }


    /**
     * Test for DIRSHARED-39.
     *
     * Rename an entry with leading and trailing space in RDN.
     */
    @ParameterizedTest
    @LdapServersSource(except = LdapServerType.Fedora389ds, reason = "Leading and trailing space is trimmed by 389ds")
    public void testRenameRdnWithLeadingAndTrailingSpace( TestLdapServer server ) throws Exception
    {
        Dn oldDn = DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
        Dn newDn = dn( "cn=\\  #ABCDEF \\ ", oldDn.getParent() );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            oldDn = DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
            newDn = dn( "cn=\\20 #ABCDEF \\20", oldDn.getParent() );
        }

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "  #ABCDEF  " );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn ) ) );
        browserViewBot.selectEntry( path( newDn ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );
    }

}
