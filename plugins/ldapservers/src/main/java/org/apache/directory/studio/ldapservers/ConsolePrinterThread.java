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
