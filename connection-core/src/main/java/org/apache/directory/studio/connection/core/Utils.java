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


import java.util.Arrays;

import javax.naming.InvalidNameException;
import javax.naming.directory.SearchControls;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.shared.ldap.util.StringTools;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;


/**
 * Some utils.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Utils
{

    private static final String DOT_DOT_DOT = "..."; //$NON-NLS-1$


    /**
     * Shortens the given label to the given maximum length
     * and filters non-printable characters.
     * 
     * @param label the label
     * @param maxLength the max length
     * 
     * @return the shortened label
     */
    public static String shorten( String label, int maxLength )
    {
        if ( label == null )
        {
            return null;
        }

        // shorten label
        if ( maxLength < 3 )
        {
            return DOT_DOT_DOT;
        }
        if ( label.length() > maxLength )
        {
            label = label.substring( 0, maxLength / 2 ) + DOT_DOT_DOT
                + label.substring( label.length() - maxLength / 2, label.length() );

        }

        // filter non-printable characters
        StringBuffer sb = new StringBuffer( maxLength + 3 );
        for ( int i = 0; i < label.length(); i++ )
        {
            char c = label.charAt( i );
            if ( Character.isISOControl( c ) )
            {
                sb.append( '.' );
            }
            else
            {
                sb.append( c );
            }
        }

        return sb.toString();
    }


    /**
     * Converts a String into a String that could be used as a filename.
     *
     * @param s
     *      the String to convert
     * @return
     *      the converted String
     */
    public static String getFilenameString( String s )
    {
        if ( s == null )
        {
            return null;
        }

        byte[] b = StringTools.getBytesUtf8( s );
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < b.length; i++ )
        {

            if ( b[i] == '-' || b[i] == '_' || ( '0' <= b[i] && b[i] <= '9' ) || ( 'A' <= b[i] && b[i] <= 'Z' )
                || ( 'a' <= b[i] && b[i] <= 'z' ) )
            {
                sb.append( ( char ) b[i] );
            }
            else
            {
                int x = ( int ) b[i];
                if ( x < 0 )
                    x = 256 + x;
                String t = Integer.toHexString( x );
                if ( t.length() == 1 )
                    t = '0' + t; //$NON-NLS-1$
                sb.append( t );
            }
        }

        return sb.toString();
    }


    /**
     * Transforms the given search parameters into an LDAP URL.
     *
     * @param connection the connection
     * @param searchBase the search base
     * @param scope the search scope
     * @param filter the search filter
     * @param attributes the returning attributes
     * 
     * @return the LDAP URL for the given search parameters
     */
    public static LdapURL getLdapURL( Connection connection, String searchBase, int scope, String filter,
        String[] attributes )
    {
        LdapURL url = new LdapURL();
        url.setScheme( connection.getEncryptionMethod() == EncryptionMethod.LDAPS ? LdapURL.LDAPS_SCHEME
            : LdapURL.LDAP_SCHEME );
        url.setHost( connection.getHost() );
        url.setPort( connection.getPort() );
        try
        {
            url.setDn( new LdapDN( searchBase ) );
        }
        catch ( InvalidNameException e )
        {
        }
        if ( attributes != null )
        {
            url.setAttributes( Arrays.asList( attributes ) );
        }
        url.setScope( scope );
        url.setFilter( filter );
        return url;
    }


    /**
     * Transforms the given search parameters into an ldapsearch command line.
     *
     * @param connection the connection
     * @param searchBase the search base
     * @param scope the search scope
     * @param aliasesDereferencingMethod the aliases dereferencing method
     * @param sizeLimit the size limit
     * @param timeLimit the time limit
     * @param filter the search filter
     * @param attributes the returning attributes
     * 
     * @return the ldapsearch command line for the given search parameters
     */
    public static String getLdapSearchCommandLine( Connection connection, String searchBase, int scope,
        AliasDereferencingMethod aliasesDereferencingMethod, long sizeLimit, long timeLimit, String filter,
        String[] attributes )
    {
        StringBuilder cmdLine = new StringBuilder();

        cmdLine.append( "ldapsearch" ); //$NON-NLS-1$

        cmdLine.append( " -H " ).append( //$NON-NLS-1$
            connection.getEncryptionMethod() == EncryptionMethod.LDAPS ? LdapURL.LDAPS_SCHEME : LdapURL.LDAP_SCHEME )
            .append( connection.getHost() ).append( ":" ).append( connection.getPort() ); //$NON-NLS-1$

        if ( connection.getEncryptionMethod() == EncryptionMethod.START_TLS )
        {
            cmdLine.append( " -ZZ" ); //$NON-NLS-1$
        }

        switch ( connection.getAuthMethod() )
        {
            case SIMPLE:
                cmdLine.append( " -x" ); //$NON-NLS-1$
                cmdLine.append( " -D \"" ).append( connection.getBindPrincipal() ).append( "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
                cmdLine.append( " -W" ); //$NON-NLS-1$
                break;
            case SASL_CRAM_MD5:
                cmdLine.append( " -U \"" ).append( connection.getBindPrincipal() ).append( "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
                cmdLine.append( " -Y \"CRAM-MD5\"" ); //$NON-NLS-1$
                break;
            case SASL_DIGEST_MD5:
                cmdLine.append( " -U \"" ).append( connection.getBindPrincipal() ).append( "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
                cmdLine.append( " -Y \"DIGEST-MD5\"" ); //$NON-NLS-1$
                break;
            case SASL_GSSAPI:
                cmdLine.append( " -Y \"GSSAPI\"" ); //$NON-NLS-1$
                break;
        }

        cmdLine.append( " -b \"" ).append( searchBase ).append( "\"" ); //$NON-NLS-1$ //$NON-NLS-2$

        String scopeAsString = scope == SearchControls.SUBTREE_SCOPE ? "sub" //$NON-NLS-1$
            : scope == SearchControls.ONELEVEL_SCOPE ? "one" : "base"; //$NON-NLS-1$ //$NON-NLS-2$
        cmdLine.append( " -s " ).append( scopeAsString ); //$NON-NLS-1$

        if ( aliasesDereferencingMethod != AliasDereferencingMethod.NEVER )
        {
            String aliasAsString = aliasesDereferencingMethod == AliasDereferencingMethod.ALWAYS ? "always" //$NON-NLS-1$
                : aliasesDereferencingMethod == AliasDereferencingMethod.FINDING ? "find" //$NON-NLS-1$
                    : aliasesDereferencingMethod == AliasDereferencingMethod.SEARCH ? "search" : "never"; //$NON-NLS-1$ //$NON-NLS-2$
            cmdLine.append( " -a " ).append( aliasAsString ); //$NON-NLS-1$
        }

        if ( sizeLimit > 0 )
        {
            cmdLine.append( " -z " ).append( sizeLimit ); //$NON-NLS-1$
        }
        if ( timeLimit > 0 )
        {
            cmdLine.append( " -l " ).append( timeLimit ); //$NON-NLS-1$
        }

        cmdLine.append( " \"" ).append( filter ).append( "\"" ); //$NON-NLS-1$ //$NON-NLS-2$

        if ( attributes != null )
        {
            if ( attributes.length == 0 )
            {
                cmdLine.append( " \"1.1\"" ); //$NON-NLS-1$
            }
            for ( String attribute : attributes )
            {
                cmdLine.append( " \"" ).append( attribute ).append( "\"" ); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return cmdLine.toString();
    }


    /**
     * Gets the LdapDN from the given String or null if the 
     * String can't be parsed.
     * 
     * @param dn the DN as String
     * 
     * @return the DN as LdapDN
     */
    public static LdapDN getLdapDn( String dn )
    {
        if ( dn == null )
        {
            return null;
        }
        try
        {
            return new LdapDN( dn );
        }
        catch ( InvalidNameException e )
        {
            return null;
        }
    }

}
