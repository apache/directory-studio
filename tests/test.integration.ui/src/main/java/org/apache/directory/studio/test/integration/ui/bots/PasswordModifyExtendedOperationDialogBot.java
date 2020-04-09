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


import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;


public class PasswordModifyExtendedOperationDialogBot extends DialogBot
{
    public PasswordModifyExtendedOperationDialogBot()
    {
        super( "Password Modify Extended Operation (RFC 3062)" );
    }


    public String getUserIdentity()
    {
        return bot.comboBox().getText();
    }


    public void setUserIdentity( String text )
    {
        bot.comboBox().setText( text );
    }


    public boolean useBindUserIdentity()
    {
        return bot.checkBox( 0 ).isChecked();
    }


    public void useBindUserIdentity( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox( 0 ).select();
        }
        else
        {
            bot.checkBox( 0 ).deselect();
        }
    }


    public String getOldPassword()
    {
        return bot.text( 0 ).getText();
    }


    public void setOldPassword( String text )
    {
        bot.text( 0 ).setText( text );
    }


    public boolean noOldPassword()
    {
        return bot.checkBox( 1 ).isChecked();
    }


    public void noOldPassword( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox( 1 ).select();
        }
        else
        {
            bot.checkBox( 1 ).deselect();
        }
    }


    public String getNewPassword()
    {
        return bot.text( 1 ).getText();
    }


    public void setNewPassword( String text )
    {
        bot.text( 1 ).setText( text );
    }


    public boolean generateNewPassword()
    {
        return bot.checkBox( 2 ).isChecked();
    }


    public void generateNewPassword( boolean selected )
    {
        if ( selected )
        {
            bot.checkBox( 2 ).select();
        }
        else
        {
            bot.checkBox( 2 ).deselect();
        }
    }


    public ErrorDialogBot clickOkButtonExpectingErrorDialog()
    {
        SWTBotShell shell = BotUtils.shell( new Runnable()
        {
            public void run()
            {
                clickOkButton();
            }
        }, "Error" );
        String shellText = shell.getText();

        return new ErrorDialogBot( shellText );
    }

}
