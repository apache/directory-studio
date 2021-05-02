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


import static org.apache.directory.studio.test.integration.junit5.TestFixture.USER1_DN;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.test.integration.junit5.LdapServerType;
import org.apache.directory.studio.test.integration.junit5.LdapServersSource;
import org.apache.directory.studio.test.integration.junit5.TestLdapServer;
import org.apache.directory.studio.test.integration.ui.bots.ErrorDialogBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaBrowserBot;
import org.junit.jupiter.params.ParameterizedTest;


/**
 * Tests the Schema browser.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaBrowserTest extends AbstractTestBase
{

    /**
     * Test for DIRSTUDIO-1061 (RawSchemaDefinition always shows single hyphen/dash)
     */
    @ParameterizedTest
    @LdapServersSource
    public void testRawSchemaDefinitionIsFilled( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );
        connectionsViewBot.select( connection.getName() );
        SchemaBrowserBot schemaBrowser = connectionsViewBot.openSchemaBrowser();
        //schemaBrowser.activateObjectClassesTab();
        schemaBrowser.selectObjectClass( "account" );
        String rawSchemaDefinition = schemaBrowser.getRawSchemaDefinition();
        assertNotNull( rawSchemaDefinition );
        assertTrue( rawSchemaDefinition.contains( "account" ) );
    }


    @ParameterizedTest
    @LdapServersSource(types = LdapServerType.OpenLdap)
    public void testNoPermissionToReadSchema( TestLdapServer server ) throws Exception
    {
        Connection connection = connectionsViewBot.createTestConnection( server );

        // Close connection and reset cached schema
        connectionsViewBot.closeSelectedConnections();
        IBrowserConnection browserConnection = BrowserCorePlugin.getDefault().getConnectionManager()
            .getBrowserConnection( connection );
        browserConnection.setSchema( Schema.DEFAULT_SCHEMA );

        // Open connection as uid=user.1 which is not allowed to read cn=subschema
        connection.setBindPrincipal( USER1_DN.getName() );
        connection.setBindPassword( "password" );
        ErrorDialogBot errorDialog = connectionsViewBot.openSelectedConnectionExpectingNoSchemaProvidedErrorDialog();
        assertThat( errorDialog.getErrorDetails(),
            containsString( "No schema information returned by server, using default schema." ) );
        errorDialog.clickOkButton();

        // Verify default schema is used
        SchemaBrowserBot schemaBrowser = connectionsViewBot.openSchemaBrowser();
        schemaBrowser.selectObjectClass( "DEFAULTSCHEMA" );
        String rawSchemaDefinition = schemaBrowser.getRawSchemaDefinition();
        assertNotNull( rawSchemaDefinition );
        assertTrue( rawSchemaDefinition.contains( "This is the Default Schema" ) );

        // Verify browser
        browserViewBot.selectEntry( ROOT_DSE_PATH );
    }
}
