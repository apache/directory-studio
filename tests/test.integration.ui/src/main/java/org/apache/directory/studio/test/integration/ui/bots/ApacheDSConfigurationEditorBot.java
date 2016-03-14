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
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;


public class ApacheDSConfigurationEditorBot
{

    private SWTBotMultiPageEditor editor;


    public ApacheDSConfigurationEditorBot( String title )
    {
        SWTWorkbenchBot bot = new SWTWorkbenchBot();
        editor = bot.multipageEditorByTitle( title );
        bot.waitUntil( new DefaultCondition()
        {

            @Override
            public boolean test() throws Exception
            {
                return editor.getPageCount() > 5;
            }


            @Override
            public String getFailureMessage()
            {
                return "ApacheDS configuration editor not ready";
            }
        } );
    }


    public void setLdapPort( int port )
    {
        editor.activatePage( "LDAP/LDAPS Servers" );
        editor.bot().text( 0 ).setText( "" + port );
    }


    public int getLdapPort()
    {
        editor.activatePage( "LDAP/LDAPS Servers" );
        return Integer.parseInt( editor.bot().text( 0 ).getText() );
    }


    public void setLdapsPort( int port )
    {
        editor.activatePage( "LDAP/LDAPS Servers" );
        editor.bot().text( 4 ).setText( "" + port );
    }


    public int getLdapsPort()
    {
        editor.activatePage( "LDAP/LDAPS Servers" );
        return Integer.parseInt( editor.bot().text( 4 ).getText() );
    }


    public void save()
    {
        editor.save();
    }


    public void close()
    {
        editor.close();
    }

}
