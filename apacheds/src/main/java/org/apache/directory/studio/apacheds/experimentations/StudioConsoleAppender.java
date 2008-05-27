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


import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;


/**
 * This class implements an {@link Appender} for the Console.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioConsoleAppender extends AppenderSkeleton
{
    /** The id of the server instance */
    private String serverInstanceId;


    /**
     * Creates a new instance of StudioConsoleAppender.
     */
    public StudioConsoleAppender()
    {
    }


    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    protected void append( LoggingEvent event )
    {
        LogMessageConsole console = ConsolesHandler.getDefault().getLogMessageConsole( serverInstanceId );

        Level level = event.getLevel();
        Object message = event.getMessage();

        if ( level == Level.INFO )
        {
            console.getInfoConsoleMessageStream().println( message.toString() );
        }
        else if ( level == Level.DEBUG )
        {
            console.getDebugConsoleMessageStream().println( message.toString() );
        }
        else if ( level == Level.WARN )
        {
            console.getWarnConsoleMessageStream().println( message.toString() );
        }
        else if ( level == Level.ERROR )
        {
            console.getErrorConsoleMessageStream().println( message.toString() );
        }
        else if ( level == Level.FATAL )
        {
            console.getFatalConsoleMessageStream().println( message.toString() );
        }
    }


    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#close()
     */
    public void close()
    {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
     */
    public boolean requiresLayout()
    {
        // TODO Auto-generated method stub
        return false;
    }


    /**
     * Gets the id of the server instance.
     *
     * @return
     *      the id of the server instance
     */
    public String getServerInstanceId()
    {
        return serverInstanceId;
    }


    /**
     * Sets the id of the server instance.
     *
     * @param serverInstanceId
     *      the id of the server instance
     */
    public void setServerInstanceId( String serverInstanceId )
    {
        this.serverInstanceId = serverInstanceId;
    }
}
