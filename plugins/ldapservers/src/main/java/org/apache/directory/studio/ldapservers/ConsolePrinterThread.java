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


public class ConsolePrinterThread extends Thread
{
    private boolean stop = false;
    private File file;
    private MessageConsoleStream consoleStream;


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
                BufferedReader reader = new BufferedReader( new FileReader( file ) );
                String line = null;

                while ( !stop )
                {
                    line = reader.readLine();
                    if ( line != null )
                    {
                        consoleStream.println( line );
                        continue;
                    }

                    sleep( 1000 );
                }

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
                // TODO Auto-generated catch block
                e.printStackTrace();
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
