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


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.directory.server.core.integ.Level;
import org.apache.directory.server.core.integ.annotations.CleanupLevel;
import org.apache.directory.server.integ.SiRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.studio.test.integration.ui.bots.CertificateValidationPreferencePageBot;
import org.apache.directory.studio.test.integration.ui.bots.PreferencesBot;
import org.apache.directory.studio.test.integration.ui.bots.StudioBot;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the preferences.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
@RunWith(SiRunner.class)
@CleanupLevel(Level.SUITE)
public class PreferencesTest
{
    public static LdapServer ldapServer;

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
    }


    /**
     * Test for DIRSTUDIO-580 
     * (Setting "Validate certificates for secure LDAP connections" is not saved).
     *
     * @throws Exception
     */
    @Test
    public void testCertificatValidationSettingsSaved() throws Exception
    {
        URL url = Platform.getInstanceLocation().getURL();
        File file = new File( url.getFile()
            + ".metadata/.plugins/org.eclipse.core.runtime/.settings/org.apache.directory.studio.connection.core.prefs" );
        assertFalse( file.exists() );

        // open preferences dialog
        PreferencesBot preferencesBot = studioBot.openPreferences();
        assertTrue( preferencesBot.isVisible() );

        // open certificate validation page
        CertificateValidationPreferencePageBot pageBot = preferencesBot.openCertificatValidationPage();
        assertTrue( pageBot.isValidateCertificatesSelected() );

        // deselect certificate validation
        pageBot.setValidateCertificates( false );
        assertFalse( pageBot.isValidateCertificatesSelected() );

        // click OK, this should write the property to the file
        preferencesBot.clickOkButton();
        assertTrue( file.exists() );
        List<String> lines = FileUtils.readLines( file );
        assertTrue( lines.contains( "validateCertificates=false" ) );

        // open dialog again, check that certificate validation checkbox is not selected
        preferencesBot = studioBot.openPreferences();
        pageBot = preferencesBot.openCertificatValidationPage();
        assertFalse( pageBot.isValidateCertificatesSelected() );

        // restore defaults, this should select the certificate validation
        pageBot.clickRestoreDefaultsButton();
        assertTrue( pageBot.isValidateCertificatesSelected() );

        // click OK, this should remove the property file as only defaults are set
        preferencesBot.clickOkButton();
        assertFalse( file.exists() );
    }

}
