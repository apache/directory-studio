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

import java.util.List;

import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.studio.test.integration.ui.bots.NewSchemaProjectWizardBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaProjectsViewBot;
import org.apache.directory.studio.test.integration.ui.bots.SchemaSearchViewBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.apache.directory.studio.test.integration.ui.bots.utils.Assertions;
import org.apache.directory.studio.test.integration.ui.bots.utils.FrameworkRunnerWithScreenshotCaptureListener;
import org.junit.After;
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
public class SchemaEditorSearchTest extends AbstractLdapTestUnit
{
    private StudioBot studioBot;


    @Before
    public void setUp() throws Exception
    {
        studioBot = new StudioBot();
        studioBot.resetSchemaPerspective();
    }


    @After
    public void tearDown() throws Exception
    {
        Assertions.genericTearDownAssertions();
    }


    /**
     * DIRSTUDIO-1026: Searching for an AT or an OC using an alternate name does not find it
     */
    @Test
    public void testSearchForAliases() throws Exception
    {
        studioBot.resetSchemaPerspective();

        SchemaProjectsViewBot projectsView = studioBot.getSchemaProjectsView();
        NewSchemaProjectWizardBot wizard = projectsView.openNewSchemaProjectWizard();
        wizard.typeProjectName( "Project A" );
        wizard.clickNextButton();
        wizard.selectAllSchemas();
        wizard.clickFinishButton();

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

}
