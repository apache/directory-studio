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

package org.apache.directory.studio.connection.core;


import java.io.IOException;

import javax.naming.ldap.PagedResultsControl;


/**
 * Implementation of the RFC 2696 Paged Results Control.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StudioPagedResultsControl extends StudioControl
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = -6219375680879062812L;

    /** The OID of Simple Paged Search Control (1.2.840.113556.1.4.319) */
    public static final String OID = "1.2.840.113556.1.4.319";

    /** The name of Simple Paged Search Control. */
    public static final String NAME = "Simple Paged Results";

    /** The page size. */
    private int size;

    /** The cookie. */
    private byte[] cookie;

    /** The is scroll mode. */
    private boolean isScrollMode;


    /**
     * Creates a new instance of StudioPagedResultsControl.
     */
    public StudioPagedResultsControl()
    {
        super();
    }


    /**
     * Creates a new instance of SimplePagedSearchControl.
     * 
     * @param size the page size
     * @param cookie the cookie, may be null
     * @param critical the critical flag
     * @param isScrollMode the is scroll mode
     */
    public StudioPagedResultsControl( int size, byte[] cookie, boolean critical, boolean isScrollMode )
    {
        super( NAME, OID, critical, null );
        this.size = size;
        this.cookie = cookie;
        this.isScrollMode = isScrollMode;

        encode();
    }


    /**
     * Gets the size.
     * 
     * @return the size
     */
    public int getSize()
    {
        return size;
    }


    /**
     * Sets the size.
     * 
     * @param size the new size
     */
    public void setSize( int size )
    {
        this.size = size;
        encode();
    }


    /**
     * Gets the cookie.
     * 
     * @return the cookie
     */
    public byte[] getCookie()
    {
        return cookie;
    }


    /**
     * Sets the cookie.
     * 
     * @param cookie the new cookie
     */
    public void setCookie( byte[] cookie )
    {
        this.cookie = cookie;
        encode();
    }


    /**
     * Checks if is scroll mode.
     * 
     * @return true, if is scroll mode
     */
    public boolean isScrollMode()
    {
        return isScrollMode;
    }


    /**
     * Sets the scroll mode.
     * 
     * @param isScrollMode the new scroll mode
     */
    public void setScrollMode( boolean isScrollMode )
    {
        this.isScrollMode = isScrollMode;
    }


    /**
     * Encodes the size and cookie values.
     */
    private void encode()
    {
        try
        {
            controlValue = new PagedResultsControl( size, cookie, critical ).getEncodedValue();
        }
        catch ( IOException e )
        {
        }
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( isScrollMode ? 1231 : 1237 );
        return result;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj == null || !( obj instanceof StudioPagedResultsControl ) )
        {
            return false;
        }
        StudioPagedResultsControl other = ( StudioPagedResultsControl ) obj;

        return this.toString().equals( other.toString() ) && this.isScrollMode == other.isScrollMode;
    }

}
