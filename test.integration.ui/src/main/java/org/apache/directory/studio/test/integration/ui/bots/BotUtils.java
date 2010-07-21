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


import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;


public class BotUtils
{
    private static SWTBot bot = new SWTBot();


    /**
     * Waits for a shell with any of the given labels.
     *
     * @param labels
     * @return
     */
    static SWTBotShell shell( final Runnable runnable, final String... labels )
    {
        // we expect the error dialog here, so set flag to false
        boolean errorDialogAutomatedMode = ErrorDialog.AUTOMATED_MODE;
        ErrorDialog.AUTOMATED_MODE = false;

        try
        {
            runnable.run();
            bot.waitUntil( new DefaultCondition()
            {
                public boolean test() throws Exception
                {
                    String shellText = bot.activeShell().getText();
                    for ( String label : labels )
                    {
                        if ( shellText.equals( label ) && bot.button( "OK" ) != null )
                        {
                            return true;
                        }
                    }
                    return false;
                }


                public String getFailureMessage()
                {
                    List<String> asList = Arrays.asList( labels );
                    return "Expected a dialog with any label " + asList + " with an 'OK' button.";
                }
            } );
        }
        finally
        {
            // reset flag
            ErrorDialog.AUTOMATED_MODE = errorDialogAutomatedMode;
        }

        return bot.activeShell();
    }


    public static void sleep( long millis )
    {
        bot.sleep( millis );
    }
}
