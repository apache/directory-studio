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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.LEADING_SHARP_DN_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.LEADING_SHARP_DN_HEX_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MULTI_VALUED_RDN_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.RDN_WITH_ESCAPED_CHARACTERS_DN_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.RDN_WITH_ESCAPED_CHARACTERS_DN_HEX_ESCAPED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.dn;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Dn oldDn = RDN_WITH_ESCAPED_CHARACTERS_DN_BACKSLASH_PREFIXED;
        Dn newDn = dn( "cn=\\#\\\\\\+\\, \\\"öé\\\" 2", oldDn.getParent() );
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            // OpenLDAP and 389ds escape all characters with hex digits 
            oldDn = RDN_WITH_ESCAPED_CHARACTERS_DN_HEX_ESCAPED;
            newDn = dn( "cn=\\23\\5C\\2B\\2C \\22öé\\22 2", oldDn.getParent() );
        }

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#\\+, \"öé\" 2" );
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
    public void testRenameRdnWithSharp( TestLdapServer server ) throws Exception
    {
        Dn oldDn = LEADING_SHARP_DN_BACKSLASH_PREFIXED;
        Dn newDn = dn( "cn=\\#ABCDEF", oldDn.getParent() );
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            // OpenLDAP and 389ds escape all characters with hex digits 
            oldDn = LEADING_SHARP_DN_HEX_ESCAPED;
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
     * Rename an entry with trailing space in RDN.
     */
    @ParameterizedTest
    @LdapServersSource(types =
        { LdapServerType.ApacheDS, LdapServerType.OpenLdap })
    public void testRenameRdnWithTrailingSpace( TestLdapServer server ) throws Exception
    {
        Dn oldDn = LEADING_SHARP_DN_BACKSLASH_PREFIXED;
        Dn newDn1 = dn( "cn=\\#ABCDEF\\ ", oldDn.getParent() );
        Dn newDn2 = dn( "cn=A\\ ", oldDn.getParent() );
        if ( server.getType() == LdapServerType.OpenLdap )
        {
            // OpenLDAP and 389ds escape all characters with hex digits 
            oldDn = LEADING_SHARP_DN_HEX_ESCAPED;
            newDn1 = dn( "cn=\\23ABCDEF\\20", oldDn.getParent() );
            newDn2 = dn( "cn=A\\20", oldDn.getParent() );
        }

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( oldDn ) );

        RenameEntryDialogBot renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "#ABCDEF " );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn1 ) ) );
        browserViewBot.selectEntry( path( newDn1 ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );

        renameDialogBot = browserViewBot.openRenameDialog();
        assertTrue( renameDialogBot.isVisible() );
        renameDialogBot.setRdnValue( 1, "A " );
        renameDialogBot.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newDn2 ) ) );
        browserViewBot.selectEntry( path( newDn2 ) );
        assertFalse( browserViewBot.existsEntry( path( newDn1 ) ) );
        assertFalse( browserViewBot.existsEntry( path( oldDn ) ) );
    }

}
