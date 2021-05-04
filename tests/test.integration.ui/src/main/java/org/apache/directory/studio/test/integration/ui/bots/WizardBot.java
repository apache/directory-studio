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


import org.apache.directory.studio.test.integration.ui.utils.TreeBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;


public abstract class WizardBot extends DialogBot
{

    public WizardBot( String title )
    {
        super( title );
    }


    public boolean isBackButtonEnabled()
    {
        return isButtonEnabled( "< Back" );
    }


    public boolean isNextButtonEnabled()
    {
        return isButtonEnabled( "Next >" );
    }


    public boolean isFinishButtonEnabled()
    {
        return isButtonEnabled( "Finish" );
    }


    public boolean isCancelButtonEnabled()
    {
        return isButtonEnabled( "Cancel" );
    }


    protected boolean isButtonEnabled( String buttonTitle )
    {
        activate();
        return bot.button( buttonTitle ).isEnabled();
    }


    public void clickBackButton()
    {
        clickButton( "< Back" );
    }


    public void clickNextButton()
    {
        clickButton( "Next >" );
    }


    public void clickFinishButton()
    {
        SWTBotShell shell = null;
        if ( title != null )
        {
            shell = bot.shell( title );
        }

        clickButton( "Finish" );

        if ( shell != null )
        {
            bot.waitUntil( Conditions.shellCloses( shell ) );
        }
    }


    public void clickCancelButton()
    {
        clickButton( "Cancel" );
    }


    public boolean existsCategory( String category )
    {
        TreeBot treeBot = new TreeBot( bot.tree() );
        return treeBot.exists( category );
    }


    public boolean existsWizard( String category, String wizard )
    {
        TreeBot treeBot = new TreeBot( bot.tree() );
        return treeBot.exists( category, wizard );
    }

}
