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

package org.apache.directory.studio.ldapbrowser.core.model;


public class ConnectionException extends Exception
{

    public static final int STAUS_CODE_TIMELIMIT_EXCEEDED = 3;
    public static final int STAUS_CODE_SIZELIMIT_EXCEEDED = 4;
    public static final int STAUS_CODE_ADMINLIMIT_EXCEEDED = 11;
    
    private static final long serialVersionUID = 1L;

    private int ldapStatusCode;

    private Throwable originalThrowable;


    public ConnectionException( int ldapStatusCode, String message, Throwable originalThrowable )
    {
        super( message );
        this.ldapStatusCode = ldapStatusCode;
        this.originalThrowable = originalThrowable;
    }


    public ConnectionException( String message )
    {
        this( -1, message, null );
    }


    public ConnectionException( Throwable t )
    {
        this( -1, t.getMessage(), t );
    }


    public int getLdapStatusCode()
    {
        return ldapStatusCode;
    }


    public Throwable getOriginalThrowable()
    {
        return originalThrowable;
    }

}
