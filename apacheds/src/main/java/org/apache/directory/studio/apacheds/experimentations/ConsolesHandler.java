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
package org.apache.directory.studio.apacheds.experimentations;


import java.util.HashMap;
import java.util.Map;

import org.apache.directory.studio.apacheds.experimentations.model.ServerInstance;
import org.apache.directory.studio.apacheds.experimentations.model.ServersHandler;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;


/**
 * This class implements the consoles handler.
 * <p>
 * 
 * It is used to store all the consoles associated to server instances.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConsolesHandler
{
    /** The default instance */
    private static ConsolesHandler instance;

    /** The map of consoles identified by server instance ID */
    private Map<String, LogMessageConsole> consolesMap;


    /**
     * Creates a new instance of ConsolesHandler.
     */
    private ConsolesHandler()
    {
        // Initialization of the map
        consolesMap = new HashMap<String, LogMessageConsole>();
    }


    /**
     * Gets the default consoles handler (singleton pattern).
     *
     * @return
     *      the default consoles handler
     */
    public static ConsolesHandler getDefault()
    {
        if ( instance == null )
        {
            instance = new ConsolesHandler();
        }

        return instance;
    }


    /**
     * Gets the log message console associated with the if of the server instance.
     *
     * @param serverInstanceId
     *      the id of the server instance
     * @return
     *      the associated log message console.
     */
    public LogMessageConsole getLogMessageConsole( String serverInstanceId )
    {
        if ( consolesMap.containsKey( serverInstanceId ) )
        {
            return consolesMap.get( serverInstanceId );
        }
        else
        {
            ServerInstance serverInstance = ServersHandler.getDefault().getServerInstanceById( serverInstanceId );

            LogMessageConsole logMessageConsole = new LogMessageConsole( serverInstance.getName() );

            consolesMap.put( serverInstanceId, logMessageConsole );

            ConsolePlugin.getDefault().getConsoleManager().addConsoles( new IConsole[]
                { logMessageConsole } );

            return logMessageConsole;
        }
    }
}
