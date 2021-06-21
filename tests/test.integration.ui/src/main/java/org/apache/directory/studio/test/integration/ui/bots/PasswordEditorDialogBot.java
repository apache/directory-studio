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


import org.apache.directory.api.ldap.model.constants.LdapSecurityConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;


public class PasswordEditorDialogBot extends DialogBot
{

    public PasswordEditorDialogBot()
    {
        super( "Password Editor" );
        super.setWaitAfterClickOkButton( true, BrowserCoreMessages.jobs__execute_ldif_name );
    }


    public void setNewPassword1( String password )
    {
        bot.text( 0 ).setText( password );
    }


    public void setNewPassword2( String password )
    {
        bot.text( 1 ).setText( password );
    }


    public String getPasswordPreview()
    {
        return bot.text( 2 ).getText();
    }


    public void selectHashMethod( LdapSecurityConstants hashMethod )
    {
        bot.comboBox().setSelection( hashMethod.getName() );
    }


    public String getPasswordHex()
    {
        return bot.text( 3 ).getText();
    }


    public String getSaltHex()
    {
        return bot.text( 4 ).getText();
    }


    public void setShowNewPasswordDetails( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox().select();
        }
        else
        {
            bot.checkBox().deselect();
        }
    }


    public void activateCurrentPasswordTab()
    {
        bot.tabItem( "Current Password" ).activate();
    }


    public void activateNewPasswordTab()
    {
        bot.tabItem( "New Password" ).activate();
    }


    public void setVerifyPassword( String password )
    {
        bot.text( 4 ).setText( password );
    }


    public String clickVerifyButton()
    {
        CheckResponse checkResponse = clickCheckButton( "Verify", "Password Verification" );
        return checkResponse.isError() ? checkResponse.getMessage() : null;
    }


    public String clickBindButton()
    {
        CheckResponse checkResponse = clickCheckButton( "Bind", "Check Authentication" );
        return checkResponse.isError() ? checkResponse.getMessage() : null;
    }

}
