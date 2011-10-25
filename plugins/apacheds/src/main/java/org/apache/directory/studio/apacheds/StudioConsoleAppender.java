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
package org.apache.directory.studio.apacheds;


import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;


/**
 * This class implements an {@link Appender} for the Console.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class StudioConsoleAppender extends AppenderSkeleton
{
    /** The id of the server */
    private String serverId;


    /**
     * Creates a new instance of StudioConsoleAppender.
     */
    public StudioConsoleAppender()
    {
        super();
        // We need to set the layout asynchronously to avoid UI thread exception
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                setLayout( new PatternLayout( ApacheDsPluginUtils.getServerLogsPattern() ) );
            }
        } );
        ApacheDsPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( new IPropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( ApacheDsPluginConstants.PREFS_SERVER_LOGS_PATTERN.equals( event.getProperty() ) )
                {
                    // We need to set the layout asynchronously to avoid UI thread exception
                    Display.getDefault().asyncExec( new Runnable()
                    {
                        public void run()
                        {
                            setLayout( new PatternLayout( ApacheDsPluginUtils.getServerLogsPattern() ) );
                        }
                    } );
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    protected void append( LoggingEvent event )
    {
        final LoggingEvent logEvent = event;

        // We need to print the message on console asynchronously to avoid UI thread exception
        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {
                LogMessageConsole console = ConsolesHandler.getDefault().getLogMessageConsole( serverId );
                if ( console != null )
                {
                    // Formatting the message with the layout
                    String message = layout.format( logEvent );

                    // Switching dependening on the level
                    Level level = logEvent.getLevel();
                    if ( level == Level.INFO )
                    {
                        console.getInfoConsoleMessageStream().print( message );
                    }
                    else if ( level == Level.DEBUG )
                    {
                        console.getDebugConsoleMessageStream().print( message );
                    }
                    else if ( level == Level.WARN )
                    {
                        console.getWarnConsoleMessageStream().print( message );
                    }
                    else if ( level == Level.ERROR )
                    {
                        console.getErrorConsoleMessageStream().print( message );
                    }
                    else if ( level == Level.FATAL )
                    {
                        console.getFatalConsoleMessageStream().print( message );
                    }
                }
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void close()
    {
        // Nothing to do
    }


    /**
     * {@inheritDoc}
     */
    public boolean requiresLayout()
    {
        return false;
    }


    /**
     * Gets the id of the server.
     *
     * @return
     *      the id of the server
     */
    public String getServerId()
    {
        return serverId;
    }


    /**
     * Sets the id of the server.
     *
     * @param serverId
     *      the id of the server
     */
    public void setServerId( String serverId )
    {
        this.serverId = serverId;
    }
}
