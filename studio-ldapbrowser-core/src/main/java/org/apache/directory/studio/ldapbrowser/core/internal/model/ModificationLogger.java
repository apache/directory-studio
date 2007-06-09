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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.directory.studio.ldapbrowser.core.ConnectionManager;


public class ModificationLogger
{

    private Connection connection;

    private FileHandler fileHandler;

    private Logger logger;


    public ModificationLogger( Connection connection )
    {
        this.connection = connection;
    }


    private void initModificationLogger()
    {
        this.logger = Logger.getAnonymousLogger();
        this.logger.setLevel( Level.ALL );

        String logfileName = ConnectionManager.getModificationLogFileName( connection.getName() );
        try
        {
            fileHandler = new FileHandler( logfileName, 100000, 10, true );
            fileHandler.setFormatter( new Formatter()
            {
                public String format( LogRecord record )
                {
                    return record.getMessage();
                }
            } );
            this.logger.addHandler( fileHandler );
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    public void dispose()
    {
        if ( this.logger != null )
        {
            Handler[] handlers = this.logger.getHandlers();
            for ( int i = 0; i < handlers.length; i++ )
            {
                handlers[i].close();
            }

            this.logger = null;
        }
    }


    public void log( String s )
    {
        if ( this.logger == null )
        {
            if ( connection.getName() != null )
            {
                this.initModificationLogger();
            }
        }

        if ( this.logger != null )
        {
            this.logger.log( Level.ALL, s );
        }
    }


    public File[] getFiles()
    {
        if ( this.logger == null )
        {
            if ( connection.getName() != null )
            {
                this.initModificationLogger();
            }
        }

        try
        {
            return getLogFiles( this.fileHandler );
        }
        catch ( Exception e )
        {
            return new File[0];
        }
    }


    private static File[] getLogFiles( FileHandler fileHandler ) throws Exception
    {
        Field field = getFieldFromClass( "java.util.logging.FileHandler", "files" ); //$NON-NLS-1$ //$NON-NLS-2$
        field.setAccessible( true );
        File[] files = ( File[] ) field.get( fileHandler );
        return files;
    }


    private static Field getFieldFromClass( String className, String fieldName ) throws Exception
    {
        Class clazz = Class.forName( className );
        Field[] fields = clazz.getDeclaredFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            if ( fields[i].getName().equals( fieldName ) )
                return fields[i];
        }
        return null;
    }

}
