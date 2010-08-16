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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;


public class CertificateTrustDialogBot extends DialogBot
{

    public boolean isVisible()
    {
        return super.isVisible( "Certificate Trust" );
    }


    public boolean isSelfSigned()
    {
        return isErrorMessage( "self-signed" );
    }


    public boolean isHostNameMismatch()
    {
        return isErrorMessage( "host name" );
    }


    public boolean isExpired()
    {
        return isErrorMessage( "expired" );
    }


    public boolean isNotYetValid()
    {
        return isErrorMessage( "not yet valid" );
    }


    public boolean isIssuerUnkown()
    {
        return isErrorMessage( "issuer certificate is unknown" );
    }


    private boolean isErrorMessage( String needle )
    {
        List<String> errorMessages = getErrorMessages();
        for ( String string : errorMessages )
        {
            if ( string.contains( needle ) )
            {
                return true;
            }
        }
        return false;
    }


    private List<String> getErrorMessages()
    {
        List<String> messages = new ArrayList<String>();

        for ( int i = 1;; i++ )
        {
            SWTBotLabel label = bot.label( i );
            if ( label.getText().startsWith( "-" ) )
            {
                messages.add( label.getText() );
            }
            else
            {
                break;
            }
        }

        return messages;
    }


    public void clickOkButton()
    {
        super.clickButton( "OK" );
    }


    protected void clickViewCertificateButton()
    {
        super.clickButton( "View Certificate..." );
    }


    public void selectDontTrust()
    {
        bot.radio( "Don't trust this certificate." ).click();
    }


    public void selectTrustTemporary()
    {
        bot.radio( "Trust this certificate for this session." ).click();
    }


    public void selectTrustPermanent()
    {
        bot.radio( "Always trust this certificate." ).click();
    }


    public ErrorDialogBot clickOkButtonExpectingErrorDialog()
    {
        BotUtils.shell( new Runnable()
        {
            public void run()
            {
                clickOkButton();
            }
        }, "Error", "Problem Occurred" );

        return new ErrorDialogBot();
    }
}
