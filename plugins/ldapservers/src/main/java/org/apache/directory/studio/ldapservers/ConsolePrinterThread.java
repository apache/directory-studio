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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.ui.console.MessageConsoleStream;


/**
 * This class implements a thread used to print in the console the contents of a file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ConsolePrinterThread extends Thread
{
    /** The flag to stop the console printer */
    private boolean stop = false;

    /** The file to read */
    private File file;

    /** The console stream */
    private MessageConsoleStream consoleStream;


    /**
     * Creates a new instance of ConsolePrinterThread.
     *
     * @param file the file to read
     * @param consoleStream the console stream
     */
    public ConsolePrinterThread( File file, MessageConsoleStream consoleStream )
    {
        this.file = file;
        this.consoleStream = consoleStream;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        if ( file.exists() && file.isFile() && file.canRead() )
        {
            try
            {
                // Opening the file reader
                BufferedReader reader = new BufferedReader( new FileReader( file ) );

                while ( !stop )
                {
                    // Checking if the console stream is closed
                    if ( consoleStream.isClosed() )
                    {
                        // We need to exit
                        break;
                    }
                    
                    // Getting the next line to print
                    String line = reader.readLine();

                    // Checking the line
                    if ( line != null )
                    {
                        // Writing the line to the console and moving the next
                        consoleStream.println( line );
                        continue;
                    }

                    // Waiting
                    sleep( 1000 );
                }

                // Closing the file reader
                reader.close();
            }
            catch ( FileNotFoundException e )
            {
                // Will never get thrown
            }
            catch ( InterruptedException e )
            {
                // Nothing to do
            }
            catch ( IOException e )
            {
                // Nothing to do
            }
        }
    }


    /**
     * Closes the console printer (makes the thread stop).
     */
    public void close()
    {
        stop = true;
    }
}
