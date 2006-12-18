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

package org.apache.directory.ldapstudio.browser.core.model;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.core.BrowserCoreMessages;
import org.apache.directory.ldapstudio.browser.core.utils.Utils;


public class URL
{

    // ldap://host:port/dn?attributes?scope?filter?extensions
    // ldap://localhost:389/ou=Testdata100,dc=seelmann,dc=muc

    // ldapurl = scheme "://" [hostport] ["/" [dn ["?" [attributes] ["?"
    // [scope] ["?" [filter] ["?" extensions]]]]]]
    // scheme = "ldap"
    // attributes = attrdesc *("," attrdesc)
    // scope = "base" / "one" / "sub"
    // dn = distinguishedName from Section 3 of [1]
    // hostport = hostport from Section 5 of RFC 1738 [5]
    // attrdesc = AttributeDescription from Section 4.1.5 of [2]
    // filter = filter from Section 4 of [4]
    // extensions = extension *("," extension)
    // extension = ["!"] extype ["=" exvalue]
    // extype = token / xtoken
    // exvalue = LDAPString from section 4.1.2 of [2]
    // token = oid from section 4.1 of [3]
    // xtoken = ("X-" / "x-") token

    private String protocol = null;

    private String host = null;

    private String port = null;

    private String dn = null;

    private String attributes = null;

    private String scope = null;

    private String filter = null;

    private String extensions = null;


    public URL( String url )
    {
        if ( url == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_url );
        }

        this.parseUrl( url );
    }


    public URL( IConnection connection, DN dn )
    {
        this( connection );

        if ( dn == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_url );
        }

        this.dn = dn.toString();
    }


    public URL( IConnection connection )
    {
        if ( connection == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_url );
        }

        if ( connection.getEncryptionMethod() == IConnection.ENCYRPTION_LDAPS )
        {
            this.protocol = "ldaps";; //$NON-NLS-1$
        }
        else
        {
            this.protocol = "ldap"; //$NON-NLS-1$
        }
        this.host = connection.getHost();
        this.port = Integer.toString( connection.getPort() );
    }


    public URL( ISearch search )
    {
        this( search.getConnection(), search.getSearchBase() );

        if ( search == null )
        {
            throw new IllegalArgumentException( BrowserCoreMessages.model__empty_url );
        }

        this.attributes = Utils.arrayToString( search.getReturningAttributes() );
        this.scope = search.getScope() == ISearch.SCOPE_SUBTREE ? "sub" : //$NON-NLS-1$
            search.getScope() == ISearch.SCOPE_ONELEVEL ? "one" : //$NON-NLS-1$
                "base"; //$NON-NLS-1$
        this.filter = search.getFilter();
    }


    public boolean equals( Object o ) throws ClassCastException
    {
        if ( o instanceof URL )
        {
            return this.toString().equals( ( ( URL ) o ).toString() );
        }
        return false;
    }


    public int hashCode()
    {
        return this.toString().hashCode();
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if ( hasProtocol() )
            sb.append( protocol );

        sb.append( "://" ); //$NON-NLS-1$

        if ( hasHost() )
            sb.append( host );
        if ( hasPort() )
            sb.append( ":" ).append( port ); //$NON-NLS-1$

        if ( hasDn() || hasAttributes() || hasScope() || hasFilter() || hasExtensions() )
            sb.append( "/" ); //$NON-NLS-1$
        if ( hasDn() )
            sb.append( dn );

        if ( hasAttributes() || hasScope() || hasFilter() || hasExtensions() )
            sb.append( "?" ); //$NON-NLS-1$
        if ( hasAttributes() )
            sb.append( attributes );

        if ( hasScope() || hasFilter() || hasExtensions() )
            sb.append( "?" ); //$NON-NLS-1$
        if ( hasScope() )
            sb.append( scope );

        if ( hasFilter() || hasExtensions() )
            sb.append( "?" ); //$NON-NLS-1$
        if ( hasFilter() )
            sb.append( filter );

        if ( hasExtensions() )
            sb.append( "?" ); //$NON-NLS-1$
        if ( hasExtensions() )
            sb.append( extensions );

        return sb.toString();
    }


    private void parseUrl( String url )
    {

        try
        {
            url = URLDecoder.decode( url, "UTF-8" ); //$NON-NLS-1$

            // protocol
            String[] protocolAndRest = url.split( "://", 2 ); //$NON-NLS-1$
            if ( protocolAndRest.length > 0 )
            {
                if ( "ldap".equals( protocolAndRest[0] ) || "ldaps".equals( protocolAndRest[0] ) ) { //$NON-NLS-1$ //$NON-NLS-2$
                    this.protocol = protocolAndRest[0];
                }
            }
            if ( protocolAndRest.length < 2 )
            {
                return;
            }

            // host and port
            String[] hostportAndRest = protocolAndRest[1].split( "/", 2 ); //$NON-NLS-1$
            if ( hostportAndRest.length > 0 )
            {
                String[] hostAndPort = hostportAndRest[0].split( ":", 2 ); //$NON-NLS-1$
                if ( hostAndPort.length == 2 )
                {
                    this.host = hostAndPort[0];
                    this.port = hostAndPort[1];
                }
                else if ( hostAndPort.length == 1 && hostAndPort[0].length() > 0 )
                {
                    this.host = hostAndPort[0];
                    this.port = "389"; //$NON-NLS-1$
                }
            }
            if ( hostportAndRest.length < 2 )
            {
                return;
            }

            // dn
            String[] dnAndRest = hostportAndRest[1].split( "\\?", 2 ); //$NON-NLS-1$
            if ( dnAndRest.length > 0 && dnAndRest[0].length() > 0 )
            {
                this.dn = dnAndRest[0];
            }
            if ( dnAndRest.length < 2 )
            {
                return;
            }

            // attributes
            String[] attributesAndRest = dnAndRest[1].split( "\\?", 2 ); //$NON-NLS-1$
            if ( attributesAndRest.length > 0 && attributesAndRest[0].length() > 0 )
            {
                this.attributes = attributesAndRest[0];
            }
            if ( attributesAndRest.length < 2 )
            {
                return;
            }

            // scope
            String[] scopeAndRest = attributesAndRest[1].split( "\\?", 2 ); //$NON-NLS-1$
            if ( scopeAndRest.length > 0 && scopeAndRest[0].length() > 0 )
            {
                this.scope = scopeAndRest[0];
            }
            if ( scopeAndRest.length < 2 )
            {
                return;
            }

            // filter
            String[] filterAndRest = scopeAndRest[1].split( "\\?", 2 ); //$NON-NLS-1$
            if ( filterAndRest.length > 0 && filterAndRest[0].length() > 0 )
            {
                this.filter = filterAndRest[0];
            }
            if ( filterAndRest.length < 2 )
            {
                return;
            }

            if ( filterAndRest[1].length() > 0 )
            {
                this.extensions = filterAndRest[0];
            }

        }
        catch ( UnsupportedEncodingException e1 )
        {
        }

    }


    public static final void main( String[] args )
    {
        // String url = "ldap://";
        // String url = "ldap://localhost";
        // String url = "ldap://:389";
        // String url = "ldap://localhost:389";
        // String url = "ldap:///??one";
        // String url = "ldap://localhost:389/cn=abc??sub";

        // String url =
        // "ldap://localhost:389/cn=abc?givenName,sn,mail?sub?(objectClass=*)?ext";

        String url = "cn=abc"; //$NON-NLS-1$
        String[] protocolAndRest = url.split( "\\?", 2 ); //$NON-NLS-1$
        System.out.println( Arrays.asList( protocolAndRest ) );
        System.out.println( protocolAndRest.length );
    }


    public boolean hasProtocol()
    {
        try
        {
            getProtocol();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public String getProtocol() throws NoSuchFieldException
    {
        if ( protocol == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_protocol );
        }

        return protocol;
    }


    public boolean hasHost()
    {
        try
        {
            getHost();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public String getHost() throws NoSuchFieldException
    {
        if ( host == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_host );
        }

        return host;
    }


    public boolean hasPort()
    {
        try
        {
            getPort();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public String getPort() throws NoSuchFieldException
    {
        try
        {
            int p = Integer.parseInt( port );
            if ( p > 0 && p <= 65536 )
            {
                return port;
            }
            else
            {
                throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_port );
            }
        }
        catch ( NumberFormatException e )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_port );
        }
    }


    public boolean hasDn()
    {
        try
        {
            getDn();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public DN getDn() throws NoSuchFieldException
    {
        if ( dn == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_dn );
        }

        try
        {
            return new DN( dn );
        }
        catch ( NameException e )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_dn );
        }
    }


    public boolean hasAttributes()
    {
        try
        {
            getAttributes();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public String[] getAttributes() throws NoSuchFieldException
    {
        if ( attributes == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_attributes );
        }

        return Utils.stringToArray( attributes );
        // return attributes.split(",");
    }


    public boolean hasScope()
    {
        try
        {
            getScope();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public int getScope() throws NoSuchFieldException
    {
        if ( scope == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_scope );
        }

        if ( "base".equals( scope ) ) { //$NON-NLS-1$
            return ISearch.SCOPE_OBJECT;
        }
        else if ( "one".equals( scope ) ) { //$NON-NLS-1$
            return ISearch.SCOPE_ONELEVEL;
        }
        else if ( "sub".equals( scope ) ) { //$NON-NLS-1$
            return ISearch.SCOPE_SUBTREE;
        }
        else
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_scope );
        }
    }


    public boolean hasFilter()
    {
        try
        {
            getFilter();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public String getFilter() throws NoSuchFieldException
    {
        if ( filter == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_filter );
        }

        return filter;
    }


    public boolean hasExtensions()
    {
        try
        {
            getExtensions();
            return true;
        }
        catch ( NoSuchFieldException e )
        {
            return false;
        }
    }


    public String getExtensions() throws NoSuchFieldException
    {
        if ( extensions == null )
        {
            throw new NoSuchFieldException( BrowserCoreMessages.model__url_no_extensions );
        }

        return extensions;
    }

}
