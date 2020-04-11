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


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.test.integration.ui.bots.AttributeTypeEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.ConnectionsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.NewSchemaProjectWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.ObjectClassEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaEditorBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaProjectsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaSearchViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.apache.directory.studio.test.integration.ui.bots.utils.StudioSystemUtils;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests search in the schema editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(FrameworkRunnerWithScreenshotCaptureListener.class)
@CreateLdapServer(transports =
    { @CreateTransport(protocol = "LDAP") })
public class SchemaEditorTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;
    private SchemaProjectsViewBot projectsView;
    private SchemaViewBot schemaView;
    private ConnectionsViewBot connectionsViewBot;
    private Connection connection;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetSchemaPerspective();
        projectsView = studioBot.getSchemaProjectsView();
        schemaView = studioBot.getSchemaView();
        connectionsViewBot = studioBot.getConnectionView();
    }


    @After
    public void tearDown() throws Exception
    {
        connectionsViewBot.deleteTestConnections();
        projectsView.deleteAllProjects();
        Assertions.genericTearDownAssertions();
    }


    /**
     * DIRSTUDIO-1026: Searching for an AT or an OC using an alternate name does not find it
     */
    @Test
    public void testSearchForAliases() throws Exception
    {
        /*
         * This test fails on Jenkins Windows Server, to be investigated...
         */
        // Assume.assumeFalse( StudioSystemUtils.IS_OS_WINDOWS_SERVER );

        createProject( "Project Search For Aliases" );

        SchemaSearchViewBot searchView = studioBot.getSchemaSearchView();

        searchView.search( "cn" );
        List<String> results = searchView.getResults();
        assertThat( results.size(), equalTo( 1 ) );
        assertThat( results.get( 0 ), containsString( "cn, commonName [2.5.4.3]" ) );

        searchView.search( "doest not exist" );
        results = searchView.getResults();
        assertThat( results.size(), equalTo( 0 ) );

        searchView.search( "cOmmOnnAmE" );
        results = searchView.getResults();
        assertThat( results.size(), equalTo( 1 ) );
        assertThat( results.get( 0 ), containsString( "cn, commonName [2.5.4.3]" ) );

        searchView.search( "doest not exist" );
        results = searchView.getResults();
        assertThat( results.size(), equalTo( 0 ) );

        searchView.search( "2.5.4.3" );
        results = searchView.getResults();
        assertThat( results.size(), equalTo( 1 ) );
        assertThat( results.get( 0 ), containsString( "cn, commonName [2.5.4.3]" ) );
    }


    @Test
    public void testCreateSchemaOfflineApacheDS() throws Exception
    {
        SchemaProjectsViewBot projectsView = studioBot.getSchemaProjectsView();
        NewSchemaProjectWizardBot wizard = projectsView.openNewSchemaProjectWizard();
        wizard.typeProjectName( "Project Offline ApacheDS" );
        wizard.selectOfflineSchema();
        wizard.clickNextButton();
        wizard.selectApacheDS();
        wizard.selectAllSchemas();
        wizard.clickFinishButton();

        assertTrue( schemaView.existsSchema( "adsconfig" ) );
        assertTrue( schemaView.existsSchema( "apache" ) );
        assertTrue( schemaView.existsSchema( "apachedns" ) );
        assertTrue( schemaView.existsSchema( "apachemeta" ) );
        assertTrue( schemaView.existsSchema( "core" ) );
        assertTrue( schemaView.existsSchema( "system" ) );

        assertTrue( schemaView.existsObjectClass( "system", "top" ) );
        assertTrue( schemaView.existsObjectClass( "adsconfig", "ads-ldapServer" ) );

        assertTrue( schemaView.existsAttributeType( "system", "objectClass" ) );
        assertTrue( schemaView.existsAttributeType( "adsconfig", "ads-maxTimeLimit" ) );
    }


    @Test
    public void testCreateSchemaOfflineOpenLDAP() throws Exception
    {
        SchemaProjectsViewBot projectsView = studioBot.getSchemaProjectsView();
        NewSchemaProjectWizardBot wizard = projectsView.openNewSchemaProjectWizard();
        wizard.typeProjectName( "Project Offline OpenLDAP" );
        wizard.selectOfflineSchema();
        wizard.clickNextButton();
        wizard.selectOpenLDAP();
        wizard.selectAllSchemas();
        wizard.clickFinishButton();

        assertTrue( schemaView.existsSchema( "collective" ) );
        assertTrue( schemaView.existsSchema( "dyngroup" ) );
        assertTrue( schemaView.existsSchema( "core" ) );
        assertTrue( schemaView.existsSchema( "system" ) );

        assertTrue( schemaView.existsObjectClass( "system", "top" ) );
        assertTrue( schemaView.existsObjectClass( "dyngroup", "groupOfURLs" ) );

        assertTrue( schemaView.existsAttributeType( "system", "objectClass" ) );
        assertTrue( schemaView.existsAttributeType( "dyngroup", "memberURL" ) );
    }


    @Test
    public void testCreateSchemaOnlineApacheDS() throws Exception
    {
        studioBot.resetLdapPerspective();
        connection = connectionsViewBot.createTestConnection( "SchemaEditorTest", ldapServer.getPort() );
        studioBot.resetSchemaPerspective();

        SchemaProjectsViewBot projectsView = studioBot.getSchemaProjectsView();
        NewSchemaProjectWizardBot wizard = projectsView.openNewSchemaProjectWizard();
        wizard.typeProjectName( "Project Online ApacheDS" );
        wizard.selectOnlineSchema();
        wizard.clickNextButton();
        wizard.selectConnection( connection.getName() );
        wizard.clickFinishButton();

        assertTrue( schemaView.existsSchema( "adsconfig" ) );
        assertTrue( schemaView.existsSchema( "apache" ) );
        assertTrue( schemaView.existsSchema( "apachedns" ) );
        assertTrue( schemaView.existsSchema( "apachemeta" ) );
        assertTrue( schemaView.existsSchema( "core" ) );
        assertTrue( schemaView.existsSchema( "system" ) );
        assertTrue( schemaView.existsSchema( "rfc2307bis" ) );

        assertTrue( schemaView.existsObjectClass( "system", "top" ) );
        assertTrue( schemaView.existsObjectClass( "adsconfig", "ads-ldapServer" ) );

        assertTrue( schemaView.existsAttributeType( "system", "objectClass" ) );
        assertTrue( schemaView.existsAttributeType( "adsconfig", "ads-maxTimeLimit" ) );
    }


    @Test
    public void testOpenObjectClassEditor() throws Exception
    {
        createProject( "Project Open Object Class Editor" );

        ObjectClassEditorBot objectClassEditor = schemaView.openObjectClassEditor( "system", "top" );
        assertNotNull( objectClassEditor );

        objectClassEditor.activateSourceCodeTab();
        String sourceCode = objectClassEditor.getSourceCode();
        assertTrue( sourceCode.contains( "objectclass ( 2.5.6.0 NAME 'top'" ) );
    }


    @Test
    public void testOpenAttributeTypEditor() throws Exception
    {
        createProject( "Project Open Attribute Type Editor" );

        AttributeTypeEditorBot attributeTypeEditor = schemaView.openAttributeTypeEditor( "system", "objectClass" );
        assertNotNull( attributeTypeEditor );

        attributeTypeEditor.activateSourceCodeTab();
        String sourceCode = attributeTypeEditor.getSourceCode();
        assertTrue( sourceCode.contains( "attributetype ( 2.5.4.0 NAME 'objectClass'" ) );
    }


    @Test
    public void testOpenSchemaEditor() throws Exception
    {
        createProject( "Project Open Schema Editor" );

        SchemaEditorBot schemaEditor = schemaView.openSchemaEditor( "system" );
        assertNotNull( schemaEditor );

        schemaEditor.activateSourceCodeTab();
        String sourceCode = schemaEditor.getSourceCode();
        assertTrue( sourceCode.contains( "objectclass ( 2.5.6.0 NAME 'top'" ) );
        assertTrue( sourceCode.contains( "attributetype ( 2.5.4.0 NAME 'objectClass'" ) );
    }


    private void createProject( String projectName )
    {
        SchemaProjectsViewBot projectsView = studioBot.getSchemaProjectsView();
        NewSchemaProjectWizardBot wizard = projectsView.openNewSchemaProjectWizard();
        wizard.typeProjectName( projectName );
        wizard.selectOfflineSchema();
        wizard.clickNextButton();
        wizard.selectApacheDS();
        wizard.selectAllSchemas();
        wizard.clickFinishButton();
    }

}
