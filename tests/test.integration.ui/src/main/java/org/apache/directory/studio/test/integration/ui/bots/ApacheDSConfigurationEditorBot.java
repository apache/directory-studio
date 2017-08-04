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


import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.test.integration.ui.bots.utils.JobWatcher;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotMultiPageEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;


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


    public void setAvailablePorts()
    {
        int port = 1023;

        if ( isLdapServerEnabled() )
        {
            port = AvailablePortFinder.getNextAvailable( port + 1 );
            setLdapPort( port );
        }
        if ( isLdapsServerEnabled() )
        {
            port = AvailablePortFinder.getNextAvailable( port + 1 );
            setLdapsPort( port );
        }
        if ( isKerberosServerEnabled() )
        {
            port = AvailablePortFinder.getNextAvailable( port + 1 );
            setKerberosPort( port );
        }

    }


    public boolean isLdapServerEnabled()
    {
        activateLdapLdapsServersPage();
        return editor.bot().checkBox( 0 ).isChecked();
    }


    public void setLdapPort( int port )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 0 ).setText( "" + port );
    }


    public int getLdapPort()
    {
        activateLdapLdapsServersPage();
        return Integer.parseInt( editor.bot().text( 0 ).getText() );
    }


    public void setLdapAddress( String address )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 1 ).setText( address );
    }


    public boolean isLdapsServerEnabled()
    {
        activateLdapLdapsServersPage();
        return editor.bot().checkBox( 1 ).isChecked();
    }


    public void setLdapsPort( int port )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 4 ).setText( "" + port );
    }


    public int getLdapsPort()
    {
        activateLdapLdapsServersPage();
        return Integer.parseInt( editor.bot().text( 4 ).getText() );
    }


    public void setLdapsAddress( String address )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 5 ).setText( address );
    }


    public void setKeystore( String keyStoreFilePath, String keyStorePassword )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 11 ).setText( keyStoreFilePath );
        editor.bot().text( 12 ).setText( keyStorePassword );
    }


    public void setSaslHost( String saslHost )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 17 ).setText( saslHost );
    }


    public void setSaslPrincipal( String saslPrincipal )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 18 ).setText( saslPrincipal );
    }


    public void setSaslSearchBase( String saslSearchBase )
    {
        activateLdapLdapsServersPage();
        editor.bot().text( 19 ).setText( saslSearchBase );
    }


    public void enableKerberosServer()
    {
        activateKerberosServerPage();
        editor.bot().checkBox( 0 ).select();
    }


    public boolean isKerberosServerEnabled()
    {
        activateKerberosServerPage();
        return editor.bot().checkBox( 0 ).isChecked();
    }


    public void setKerberosPort( int port )
    {
        activateKerberosServerPage();
        editor.bot().text( 0 ).setText( "" + port );
    }


    public void setKerberosAddress( String address )
    {
        activateKerberosServerPage();
        editor.bot().text( 1 ).setText( address );
    }


    public int getKerberosPort()
    {
        activateKerberosServerPage();
        return Integer.parseInt( editor.bot().text( 0 ).getText() );
    }


    public void setKdcRealm( String kdcRealm )
    {
        activateKerberosServerPage();
        editor.bot().text( 4 ).setText( kdcRealm );
    }


    public void setKdcSearchBase( String kdcSearchBase )
    {
        activateKerberosServerPage();
        editor.bot().text( 5 ).setText( kdcSearchBase );
    }


    public void setRequirePreAuthenticationByEncryptedTimestamp( boolean enable )
    {
        activateKerberosServerPage();
        SWTBotCheckBox checkBox = editor.bot().checkBox( 5 );
        if ( enable )
        {
            checkBox.select();
        }
        else
        {
            checkBox.deselect();
        }
    }


    public void save()
    {
        JobWatcher watcher = new JobWatcher( "Save Configuration" );
        editor.save();
        watcher.waitUntilDone();
    }


    public void close()
    {
        editor.close();
    }


    private void activateLdapLdapsServersPage()
    {
        editor.activatePage( "LDAP/LDAPS Servers" );
    }


    private void activateKerberosServerPage()
    {
        editor.activatePage( "Kerberos Server" );
    }

}
