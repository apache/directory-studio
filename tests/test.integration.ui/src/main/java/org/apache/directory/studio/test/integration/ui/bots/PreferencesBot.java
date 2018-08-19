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
package org.apache.directory.studio.test.integration.ui.bots;

import org.apache.directory.studio.test.integration.ui.bots.utils.TreeBot;


public class PreferencesBot extends DialogBot
{

    public PreferencesBot()
    {
        super( "Preferences" );
    }


    public CertificateValidationPreferencePageBot openCertificatValidationPage()
    {
        bot.tree().getTreeItem( "Apache Directory Studio" ).select().expand().getNode( "Connections" ).select()
            .expand().getNode( "Certificate Validation" ).select();
        return new CertificateValidationPreferencePageBot();
    }


    public boolean pageExists( String... path )
    {
        TreeBot treeBot = new TreeBot( bot.tree() );
        return treeBot.exists( path );
    }


    @Override
    public void clickOkButton()
    {
        super.clickButton( "Apply and Close" );
    }


    public PasswordsKeystorePreferencePageBot openPasswordsKeystorePage()
    {
        bot.tree().getTreeItem( "Apache Directory Studio" ).select().expand().getNode( "Connections" ).select()
            .expand().getNode( "Passwords Keystore" ).select();
        return new PasswordsKeystorePreferencePageBot();
    }

}
