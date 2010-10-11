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
package org.apache.directory.studio.ldapservers;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.ldapservers.model.LdapServer;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;


/**
 * This class implements the consoles manager.
 * <p>
 * 
 * It is used to store all the consoles associated to servers.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConsolesManager
{
    /** The default instance */
    private static ConsolesManager instance;

    /** The map of consoles identified by server ID */
    private Map<LdapServer, MessageConsole> consolesMap;


    /**
     * Creates a new instance of ConsolesManager.
     */
    private ConsolesManager()
    {
        // Initialization of the map
        consolesMap = new HashMap<LdapServer, MessageConsole>();
    }


    /**
     * Gets the default consoles manager (singleton pattern).
     *
     * @return
     *      the default consoles manager
     */
    public static ConsolesManager getDefault()
    {
        if ( instance == null )
        {
            instance = new ConsolesManager();
        }

        return instance;
    }


    /**
     * Gets the message console associated with the if of the server.
     *
     * @param server
     *      the server
     * @return
     *      the associated message console.
     */
    public MessageConsole getMessageConsole( LdapServer server )
    {
        if ( consolesMap.containsKey( server ) )
        {
            return consolesMap.get( server );
        }
        else
        {
            MessageConsole messageConsole = new MessageConsole( server.getName()
                + " " + Messages.getString( "ConsolesManager.LdapServer" ), null ); //$NON-NLS-1$ //$NON-NLS-2$
            consolesMap.put( server, messageConsole );

            ConsolePlugin.getDefault().getConsoleManager().addConsoles( new IConsole[]
                { messageConsole } );

            return messageConsole;
        }
    }
}
