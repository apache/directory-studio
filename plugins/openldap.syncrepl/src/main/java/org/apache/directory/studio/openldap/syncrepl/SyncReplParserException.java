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
package org.apache.directory.studio.openldap.syncrepl;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class implements the SyncRepl Parser Exception.
 * <p>
 * It is used to store all exceptions raised during the parsing of a SyncRepl value.
 */
public class SyncReplParserException extends Exception
{
    private static final long serialVersionUID = 1L;

    /** The list of exceptions*/
    private List<ParseException> exceptions = new ArrayList<ParseException>();


    /**
     * Creates a new instance of SyncReplParserException.
     */
    public SyncReplParserException()
    {
        super();
    }


    /**
     * Adds one or more {@link ParseException}.
     *
     * @param parseExceptions one or more {@link ParseException}
     */
    public void addParseException( ParseException... parseExceptions )
    {
        if ( parseExceptions != null )
        {
            for ( ParseException parseException : parseExceptions )
            {
                exceptions.add( parseException );
            }
        }
    }


    /**
     * Gets the number of {@link ParseException} stored.
     *
     * @return the number of {@link ParseException} stored
     */
    public int size()
    {
        return exceptions.size();
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( '[' );

        for ( int i = 0; i < exceptions.size(); i++ )
        {
            sb.append( exceptions.get( i ).toString() );

            if ( i != ( exceptions.size() - 1 ) )
            {
                sb.append( ", " );

            }
        }

        sb.append( ']' );

        return sb.toString();
    }
}
