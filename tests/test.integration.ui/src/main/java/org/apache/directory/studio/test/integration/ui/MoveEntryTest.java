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
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC111_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.MISC_DN;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED;
import static org.apache.directory.studio.test.integration.junit5.TestFixture.DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.MoveEntriesDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SelectDnDialogBot;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests entry move (moddn) and the move dialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MoveEntryTest extends AbstractTestBase
{

    @ParameterizedTest
    @LdapServersSource
    public void testMoveUp( TestLdapServer server ) throws Exception
    {
        Dn dnToMove = MISC111_DN;
        Dn newParentDn = MISC_DN;

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( dnToMove ) );

        MoveEntriesDialogBot moveEntryDialog = browserViewBot.openMoveEntryDialog();
        assertTrue( moveEntryDialog.isVisible() );
        moveEntryDialog.setParentText( newParentDn.getName() );
        moveEntryDialog.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newParentDn, dnToMove.getRdn() ) ) );
        browserViewBot.selectEntry( path( newParentDn, dnToMove.getRdn() ) );
        assertFalse( browserViewBot.existsEntry( path( dnToMove ) ) );
    }


    @ParameterizedTest
    @LdapServersSource
    public void testMoveDown( TestLdapServer server ) throws Exception
    {
        Dn dnToMove = DN_WITH_ESCAPED_CHARACTERS_BACKSLASH_PREFIXED;
        Dn newParentDn = DN_WITH_LEADING_SHARP_BACKSLASH_PREFIXED;
        if ( server.getType() == LdapServerType.OpenLdap || server.getType() == LdapServerType.Fedora389ds )
        {
            // OpenLDAP and 389ds escape all characters with hex digits 
            dnToMove = DN_WITH_ESCAPED_CHARACTERS_HEX_PAIR_ESCAPED;
            newParentDn = DN_WITH_LEADING_SHARP_HEX_PAIR_ESCAPED;
        }

        connectionsViewBot.createTestConnection( server );
        browserViewBot.selectEntry( path( dnToMove ) );

        MoveEntriesDialogBot moveEntryDialog = browserViewBot.openMoveEntryDialog();
        assertTrue( moveEntryDialog.isVisible() );
        SelectDnDialogBot selectDnBot = moveEntryDialog.clickBrowseButtonExpectingSelectDnDialog();
        assertTrue( selectDnBot.isVisible() );
        selectDnBot.selectEntry( ArrayUtils.remove( path( newParentDn ), 0 ) );
        selectDnBot.clickOkButton();
        moveEntryDialog.activate();
        assertEquals( newParentDn.getName(), moveEntryDialog.getParentText() );
        moveEntryDialog.clickOkButton();

        assertTrue( browserViewBot.existsEntry( path( newParentDn, dnToMove.getRdn() ) ) );
        browserViewBot.selectEntry( path( newParentDn, dnToMove.getRdn() ) );
    }

}
