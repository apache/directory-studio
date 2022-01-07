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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.directory.api.util.Strings;

/**
 * This class wraps the TCPBuffer parameter :
 * <pre>
 * [listener=<URL>] [{read|write}=]<size>
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TcpBufferWrapper implements Cloneable, Comparable<TcpBufferWrapper>
{
    /** The maximum buffer size (2^32-1) */
    public static final long MAX_TCP_BUFFER_SIZE = 0xFFFFFFFFL;
    
    /** The two kind of TCP buffer we can configure */
    public enum TcpTypeEnum
    {
        READ( "read" ),
        WRITE( "write" ),
        BOTH( "" );
        
        private String value;
        
        private TcpTypeEnum( String value )
        {
            this.value = value;
        }
        
        private String getValue()
        {
            return value;
        }
    }
    
    /** The TCP listener (optional) */
    private URI listener;

    /** The type of TCP buffer (either read or write, or both ) (optional) */
    private TcpTypeEnum tcpType;
    
    /** The TCP Buffer size (between 0 and 2^32-1) */
    private long size; 
    
    
    /**
     * Create a TcpBufferWrapper instance
     * 
     * @param size The TcpBuffer size
     * @param tcpType read or write, but can be null for both
     * @param url The listener
     */
    public TcpBufferWrapper( long size, TcpTypeEnum tcpType, String url )
    {
        this.size = size;
        this.tcpType = tcpType;
        
        if ( !Strings.isEmpty( url ) )
        {
            try
            {
                listener = new URI( url );
            }
            catch ( URISyntaxException e )
            {
                e.printStackTrace();
            }
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
                        this.setListener( new URI( urlStr ) );
                    }
                    catch ( URISyntaxException e )
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
            }
                
            // We might have a 'read' or 'write' prefix
            if ( lowerCaseTcpBuffer.startsWith( "read=", pos ) )
            {
                tcpType = TcpTypeEnum.READ;
                pos += 5;
            }
            else if ( lowerCaseTcpBuffer.startsWith( "write=", pos ) )
            {
                tcpType = TcpTypeEnum.WRITE;
                pos += 6;
            }
            
            // get the integer
            String sizeStr = lowerCaseTcpBuffer.substring( pos );
            
            if ( !Strings.isEmpty( sizeStr ) )
            {
                size = Long.valueOf( sizeStr );
                
                if ( ( size < 0L ) || ( size > MAX_TCP_BUFFER_SIZE ) )
                {
                    // This is wrong
                }
            }
        }
    }
    

    /**
     * @return the listener
     */
    public URI getListener()
    {
        return listener;
    }

    
    /**
     * @param listener the listener to set
     */
    public void setListener( URI listener )
    {
        this.listener = listener;
    }

    
    /**
     * @return the tcpType
     */
    public TcpTypeEnum getTcpType()
    {
        return tcpType;
    }

    
    /**
     * @param tcpType the tcpType to set
     */
    public void setTcpType( TcpTypeEnum tcpType )
    {
        this.tcpType = tcpType;
    }

    
    /**
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    
    /**
     * @param size the size to set
     */
    public void setSize( long size )
    {
        this.size = size;
    }
    
    
    /**
     * Tells if the TcpBuffer element is valid or not
     * @param sizeStr the TCP buffer size
     * @param urlStr The listener as a String
     * @return true if the values are correct, false otherwise
     */
    public static boolean isValid( String sizeStr, String urlStr )
    {
        // the size must be positive and below 2^32-1
        if ( ( sizeStr != null ) && ( sizeStr.length() > 0 ) )
        {
            try
            {
                long size = Long.parseLong( sizeStr );
            
                if ( ( size < 0L ) || ( size > MAX_TCP_BUFFER_SIZE ) )
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
     * Clone the current object
     */
    public TcpBufferWrapper clone()
    {
        try
        {
            return (TcpBufferWrapper)super.clone();
        }
        catch ( CloneNotSupportedException e )
        {
            return null;
        }
    }
    
    
    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object that )
    {
        // Quick test
        if ( this == that )
        {
            return true;
        }
        
        if ( that instanceof TcpBufferWrapper )
        {
            TcpBufferWrapper thatInstance = (TcpBufferWrapper)that;
            
            if ( size != thatInstance.size )
            {
                return false;
            }
            
            if ( tcpType != thatInstance.tcpType )
            {
                return false;
            }
            
            if ( listener != null )
            {
                return listener.equals( thatInstance.listener );
            }
            else
            {
                return thatInstance.listener == null;
            }
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * @see Object#hashCode()
     */
    public int hashCode()
    {
        int h = 37;
        
        h += h*17 + Long.hashCode( size );
        h += h*17 + tcpType.hashCode();
        h += h*17 + listener.hashCode();
        
        return h;
    }


    /**
     * @see Comparable#compareTo()
     */
    public int compareTo( TcpBufferWrapper that )
    {
        // Compare by size first then by URL
        if ( that == null )
        {
            return 1;
        }
        
        if ( size > that.size )
        {
            return 1;
        }
        else if ( size < that.size )
        {
            return -1;
        }
        
        // The URL, as a String
        if ( listener == null )
        {
            if ( that.listener == null )
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        else
        {
           if ( that.listener == null )
           {
               return 1;
           }
           else
           {
               String thisListener = listener.toString();
               String thatListener = that.listener.toString();
               
               return thisListener.compareToIgnoreCase( thatListener );
           }
        }
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
        
        if ( ( tcpType != null ) && ( tcpType != TcpTypeEnum.BOTH ) )
        {
            sb.append( tcpType.getValue() ).append( "=" );
        }
        
        sb.append( size );
        
        return sb.toString();
    }
}
