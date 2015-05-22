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
package org.apache.directory.studio.openldap.config.editor.wrappers;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class wraps the TCPBuffer parameter :
 * <pre>
 * [listener=<URL>] [{read|write}=]<size>
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TcpBufferWrapper
{
    /** The two kind of TCP buffer we can configure */
    public enum TcpType
    {
        READ( "read" ),
        WRITE( "write" ),
        BOTH( "" );
        
        private String value;
        
        private TcpType( String value )
        {
            this.value = value;
        }
        
        private String getValue()
        {
            return value;
        }
    }
    
    /** The TCP listener (optional) */
    private URL listener;

    /** The type of TCP buffer (either read or write, or both ) (optional) */
    private TcpType tcpType;
    
    /** The TCP Buffer size (between 0 and 65535) */
    private int size; 
    
    
    /**
     * Create a TcpBufferWrapper instance
     * 
     * @param size The TcpBuffer size
     * @param tcpType read or write, but can be null for both
     * @param url The listener
     */
    public TcpBufferWrapper( int size, TcpType tcpType, String url )
    {
        this.size = size;
        this.tcpType = tcpType;
        try
        {
            listener = new URL( url );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Create a TcpBufferWrapper instance from a String
     * 
     * @param tcpBufferStr The String that contain the value
     */
    public TcpBufferWrapper( String tcpBufferStr )
    {
        if ( tcpBufferStr != null )
        {
            // use a lowercase version of the string
            String lowerCaseTcpBuffer = tcpBufferStr.toLowerCase();
            int pos = 0;
            
            if ( lowerCaseTcpBuffer.startsWith( "listener=" ) )
            {
                // Fine, we have an URL, it's before the first space
                int spacePos = lowerCaseTcpBuffer.indexOf( ' ' );
                
                if ( spacePos == -1 )
                {
                    // This is wrong...
                }
                else
                {
                    String urlStr = tcpBufferStr.substring( 9, spacePos );
                    
                    try
                    {
                        this.setListener( new URL( urlStr ) );
                    }
                    catch ( MalformedURLException e )
                    {
                        e.printStackTrace();
                    }
                    
                    // Get rid of the following spaces
                    pos = spacePos;

                    while ( pos < lowerCaseTcpBuffer.length() )
                    {
                        if ( lowerCaseTcpBuffer.charAt( pos ) != ' ' )
                        {
                            break;
                        }
                        
                        pos++;
                    }
                }
                    
                // We might have a 'read' or 'write' prefix
                if ( lowerCaseTcpBuffer.startsWith( "read=", pos ) )
                {
                    tcpType = TcpType.READ;
                    pos += 5;
                }
                else if ( lowerCaseTcpBuffer.startsWith( "write=", pos ) )
                {
                    tcpType = TcpType.WRITE;
                    pos += 6;
                }
                
                // get the integer
                String sizeStr = lowerCaseTcpBuffer.substring( pos );
                
                size = Integer.valueOf( sizeStr );
                
                if ( ( size < 0 ) ||( size > 65535 ) )
                {
                    // This is wrong
                }
            }
        }
    }

    /**
     * @return the listener
     */
    public URL getListener()
    {
        return listener;
    }

    
    /**
     * @param listener the listener to set
     */
    public void setListener( URL listener )
    {
        this.listener = listener;
    }

    
    /**
     * @return the tcpType
     */
    public TcpType getTcpType()
    {
        return tcpType;
    }

    
    /**
     * @param tcpType the tcpType to set
     */
    public void setTcpType( TcpType tcpType )
    {
        this.tcpType = tcpType;
    }

    
    /**
     * @return the size
     */
    public int getSize()
    {
        return size;
    }

    
    /**
     * @param size the size to set
     */
    public void setSize( int size )
    {
        this.size = size;
    }
    
    
    /**
     * Tells if the TcpBuffer element is valid or not
     * @param sizeStr the TCP buffer size
     * @param urlStr The listener as a String
     * @return true if the value are correct, false otherwise
     */
    public static boolean isValid( String sizeStr, String urlStr )
    {
        // the size must be positive and below 2^32-1
        if ( ( sizeStr != null) && ( sizeStr.length() > 0 ) )
        {
            try
            {
                int size = Integer.valueOf( sizeStr );
            
                if ( ( size < 0 ) || ( size > 65535 ) )
                {
                    return false;
                }
            }
            catch ( NumberFormatException nfe )
            {
                return false;
            }
        }
        
        // Check the URL
        if ( ( urlStr != null ) && ( urlStr.length() > 0 ) )
        {
            try
            {
                new URL( urlStr );
            }
            catch ( MalformedURLException mue )
            {
                return false;
            }
        }
        
        return true;
    }
    
    
    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        if ( listener != null )
        {
            sb.append( "listener=" ).append( listener ).append( " ");
        }
        
        if ( ( tcpType != null ) && ( tcpType != TcpType.BOTH ) )
        {
            sb.append( tcpType.getValue() ).append( "=" );
        }
        
        sb.append( size );
        
        return sb.toString();
    }
}
