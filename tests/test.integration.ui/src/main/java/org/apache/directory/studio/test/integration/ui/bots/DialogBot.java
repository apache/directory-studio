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


import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;


public abstract class DialogBot
{

    protected SWTWorkbenchBot bot = new SWTWorkbenchBot();
    protected String title;


    protected DialogBot( String title )
    {
        this.title = title;
    }


    public void activate()
    {
        bot.shell( title ).setFocus();
    }


    public boolean isVisible()
    {
        return bot.shell( title ).isVisible();
    }


    public void clickOkButton()
    {
        clickButton( "OK" );
    }


    public void clickCancelButton()
    {
        clickButton( "Cancel" );
    }


    public void waitForDialog()
    {
        bot.waitUntil( new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                return isVisible();
            }


            @Override
            public String getFailureMessage()
            {
                return "Dialog did not appear: " + title;
            }
        }, SWTBotPreferences.TIMEOUT * 4 );
    }


    protected void clickButton( final String buttonTitle )
    {
        activate();
        final SWTBotButton button = bot.button( buttonTitle );
        button.click();
    }


    protected String clickCheckButton( final String label, final String title )
    {
        SWTBotShell parentShell = bot.activeShell();
        SWTBotShell shell = BotUtils.shell( new Runnable()
        {
            public void run()
            {
                bot.button( label ).click();
            }
        }, "Error", title );

        String shellText = shell.getText();
        String labelText = bot.label( 1 ).getText(); // label(0) is the image
        bot.button( "OK" ).click();
        parentShell.activate();

        if ( shellText.equals( title ) )
        {
            return null;
        }
        else
        {
            return labelText;
        }
    }
}
