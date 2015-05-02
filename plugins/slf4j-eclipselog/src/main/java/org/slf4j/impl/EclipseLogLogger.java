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

package org.slf4j.impl;


import org.eclipse.core.runtime.Status;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;


/**
 * Adapts {@link org.slf4j.Logger} interface to Eclipse {@link org.eclipse.core.runtime.ILog}
 * which writes logs to <code>.metadata/.log</code> and show them in the Eclipse 'Error Log' view.
 * <p>
 * Only log levels 'error' and 'warn' are implemented. Log level 'info' would write too much
 * to the Eclipse log. There is no appropriate log level 'debug' and 'trace' in Eclipse log, thus
 * those levels are also not supported.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EclipseLogLogger extends MarkerIgnoringBase
{
    private static final long serialVersionUID = 1L;


    private void internalLog( int severity, String message, Throwable t )
    {
        Activator activator = Activator.getDefault();
        if ( activator != null )
        {
            String symbolicName = activator.getBundle().getSymbolicName();
            Status status = new Status( severity, symbolicName, message, t );
            activator.getLog().log( status );
        }
    }


    // ERROR

    public boolean isErrorEnabled()
    {
        return true;
    }


    public void error( String msg )
    {
        if ( isErrorEnabled() )
        {
            internalLog( Status.ERROR, msg, null );
        }
    }


    public void error( String format, Object arg )
    {
        if ( isErrorEnabled() )
        {
            String msgStr = MessageFormatter.format( format, arg ).getMessage();
            internalLog( Status.ERROR, msgStr, null );
        }
    }


    public void error( String format, Object arg1, Object arg2 )
    {
        if ( isErrorEnabled() )
        {
            String msgStr = MessageFormatter.format( format, arg1, arg2 ).getMessage();
            internalLog( Status.ERROR, msgStr, null );
        }
    }


    public void error( String format, Object... argArray )
    {
        if ( isErrorEnabled() )
        {
            String msgStr = MessageFormatter.arrayFormat( format, argArray ).getMessage();
            internalLog( Status.ERROR, msgStr, null );
        }
    }


    public void error( String msg, Throwable t )
    {
        if ( isErrorEnabled() )
        {
            internalLog( Status.ERROR, msg, t );
        }
    }


    // WARN

    public boolean isWarnEnabled()
    {
        return true;
    }


    public void warn( String msg )
    {
        if ( isWarnEnabled() )
        {
            internalLog( Status.WARNING, msg, null );
        }
    }


    public void warn( String format, Object arg )
    {
        if ( isWarnEnabled() )
        {
            String msgStr = MessageFormatter.format( format, arg ).getMessage();
            internalLog( Status.WARNING, msgStr, null );
        }
    }


    public void warn( String format, Object arg1, Object arg2 )
    {
        if ( isWarnEnabled() )
        {
            String msgStr = MessageFormatter.format( format, arg1, arg2 ).getMessage();
            internalLog( Status.WARNING, msgStr, null );
        }
    }


    public void warn( String format, Object... argArray )
    {
        if ( isWarnEnabled() )
        {
            String msgStr = MessageFormatter.arrayFormat( format, argArray ).getMessage();
            internalLog( Status.WARNING, msgStr, null );
        }
    }


    public void warn( String msg, Throwable t )
    {
        if ( isWarnEnabled() )
        {
            internalLog( Status.WARNING, msg, t );
        }
    }


    // INFO disabled, it would write too much logs

    public boolean isInfoEnabled()
    {
        return false;
    }


    public void info( String msg )
    {
    }


    public void info( String format, Object arg )
    {
    }


    public void info( String format, Object arg1, Object arg2 )
    {
    }


    public void info( String format, Object... argArray )
    {
    }


    public void info( String msg, Throwable t )
    {
    }


    // DEBUG disabled, there is no appropriate log level in Eclipse log

    public boolean isDebugEnabled()
    {
        return false;
    }


    public void debug( String msg )
    {
    }


    public void debug( String format, Object arg )
    {
    }


    public void debug( String format, Object arg1, Object arg2 )
    {
    }


    public void debug( String format, Object... argArray )
    {
    }


    public void debug( String msg, Throwable t )
    {
    }


    // TRACE disabled, there is no appropriate log level in Eclipse log

    public boolean isTraceEnabled()
    {
        return false;
    }


    public void trace( String msg )
    {
    }


    public void trace( String format, Object arg )
    {
    }


    public void trace( String format, Object arg1, Object arg2 )
    {
    }


    public void trace( String format, Object... argArray )
    {
    }


    public void trace( String msg, Throwable t )
    {
    }
}
