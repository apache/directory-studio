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


public class CertificateValidationPreferencePageBot extends DialogBot
{

    private static final String VALIDATE_CERTIFICATES_FOR_SECURE_LDAP_CONNECTIONS = "Validate certificates for secure LDAP connections";


    public void clickApplyButton()
    {
        super.clickButton( "Apply" );
    }


    public void clickRestoreDefaultsButton()
    {
        super.clickButton( "Restore Defaults" );
    }


    public boolean isValidateCertificatesSelected()
    {
        return bot.checkBox( VALIDATE_CERTIFICATES_FOR_SECURE_LDAP_CONNECTIONS ).isChecked();
    }


    public void setValidateCertificates( boolean b )
    {
        if ( b )
        {
            bot.checkBox( VALIDATE_CERTIFICATES_FOR_SECURE_LDAP_CONNECTIONS ).select();
        }
        else
        {
            bot.checkBox( VALIDATE_CERTIFICATES_FOR_SECURE_LDAP_CONNECTIONS ).deselect();
        }
    }

}
