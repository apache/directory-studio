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

import org.apache.directory.api.util.Position;
import org.apache.directory.api.util.Strings;


/**
 * A parser of SyncRepl value.
 */
public class SyncReplParser
{
    private static final String KEYWORD_RID = "rid";
    private static final String KEYWORD_PROVIDER = "provider";
    private static final String KEYWORD_SEARCHBASE = "searchbase";
    private static final String KEYWORD_TYPE = "type";
    private static final String KEYWORD_INTERVAL = "interval";
    private static final String KEYWORD_RETRY = "retry";
    private static final String KEYWORD_FILTER = "filter";
    private static final String KEYWORD_SCOPE = "scope";
    private static final String KEYWORD_ATTRS = "attrs";
    private static final String KEYWORD_ATTRSONLY = "attrsonly";
    private static final String KEYWORD_SIZELIMIT = "sizelimit";
    private static final String KEYWORD_TIMELIMIT = "timelimit";
    private static final String KEYWORD_SCHEMACHECKING = "schemachecking";
    private static final String KEYWORD_NETWORK_TIMEOUT = "network-timeout";
    private static final String KEYWORD_TIMEOUT = "timeout";
    private static final String KEYWORD_BINDMETHOD = "bindmethod";
    private static final String KEYWORD_BINDDN = "binddn";
    private static final String KEYWORD_SASLMECH = "saslmech";
    private static final String KEYWORD_AUTHCID = "authcid";
    private static final String KEYWORD_AUTHZID = "authzid";
    private static final String KEYWORD_CREDENTIALS = "credentials";
    private static final String KEYWORD_REALM = "realm";
    private static final String KEYWORD_SECPROPS = "secprops";
    private static final String KEYWORD_KEEPALIVE = "keepalive";
    private static final String KEYWORD_STARTTLS = "starttls";
    private static final String KEYWORD_TLS_CERT = "tls_cert";
    private static final String KEYWORD_TLS_KEY = "tls_key";
    private static final String KEYWORD_TLS_CACERT = "tls_cacert";
    private static final String KEYWORD_TLS_CACERTDIR = "tls_cacertdir";
    private static final String KEYWORD_TLS_REQCERT = "tls_reqcert";
    private static final String KEYWORD_TLS_CIPHERSUITE = "tls_ciphersuite";
    private static final String KEYWORD_TLS_CRLCHECK = "tls_crlcheck";
    private static final String KEYWORD_LOGBASE = "logbase";
    private static final String KEYWORD_LOGFILTER = "logfilter";
    private static final String KEYWORD_SYNCDATA = "syncdata";


    /**
     * Parses a SyncRepl value.
     * 
     * @param s
     *            the string to be parsed
     * @return the associated SyncRepl object
     * @throws SyncReplParserException
     *             if there are any recognition errors (bad syntax)
     */
    public synchronized SyncRepl parse( String s ) throws SyncReplParserException
    {
        SyncReplParserException parserException = new SyncReplParserException();

        // Trimming the value
        s = Strings.trim( s );

        // Getting the chars of the string
        char[] chars = new char[s.length()];
        s.getChars( 0, s.length(), chars, 0 );

        // Creating the position
        Position pos = new Position();
        pos.start = 0;
        pos.end = 0;
        pos.length = chars.length;

        SyncRepl syncRepl = parseInternal( chars, pos, parserException );

        if ( parserException.size() > 0 )
        {
            throw parserException;
        }

        return syncRepl;
    }


    private SyncRepl parseInternal( char[] chars, Position pos, SyncReplParserException parserException )
    {
        SyncRepl syncRepl = new SyncRepl();
        boolean foundAtLeastOneProperty = false;

        char c = Strings.charAt( chars, pos.start );

        do
        {
            // Whitespace
            if ( Character.isWhitespace( c ) )
            {
                // We ignore all whitespaces
                pos.start++;
            }

            // rid
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_RID, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_RID.length();

                parseRid( chars, pos, syncRepl, parserException );
            }

            // provider
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_PROVIDER, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_PROVIDER.length();

                parseProvider( chars, pos, syncRepl, parserException );
            }

            // searchbase
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SEARCHBASE, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SEARCHBASE.length();

                parseSearchBase( chars, pos, syncRepl, parserException );
            }

            // type
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TYPE, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TYPE.length();

                parseType( chars, pos, syncRepl, parserException );
            }

            // interval
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_INTERVAL, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_INTERVAL.length();

                parseInterval( chars, pos, syncRepl, parserException );
            }

            // retry
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_RETRY, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_RETRY.length();

                parseRetry( chars, pos, syncRepl, parserException );
            }

            // filter
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_FILTER, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_FILTER.length();

                parseFilter( chars, pos, syncRepl, parserException );
            }

            // scope
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SCOPE, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SCOPE.length();

                parseScope( chars, pos, syncRepl, parserException );
            }

            // attrsonly
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_ATTRSONLY, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_ATTRSONLY.length();

                syncRepl.setAttrsOnly( true );
            }

            // attrs
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_ATTRS, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_ATTRS.length();

                parseAttrs( chars, pos, syncRepl, parserException );
            }

            // sizelimit
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SIZELIMIT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SIZELIMIT.length();

                parseSizeLimit( chars, pos, syncRepl, parserException );
            }

            // timelimit
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TIMELIMIT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TIMELIMIT.length();

                parseTimeLimit( chars, pos, syncRepl, parserException );
            }

            // schemachecking
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SCHEMACHECKING, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SCHEMACHECKING.length();

                parseSchemaChecking( chars, pos, syncRepl, parserException );
            }

            // network-timeout
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_NETWORK_TIMEOUT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_NETWORK_TIMEOUT.length();

                parseNetworkTimeout( chars, pos, syncRepl, parserException );
            }

            // timeout
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TIMEOUT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TIMEOUT.length();

                parseTimeout( chars, pos, syncRepl, parserException );
            }

            // bindmethod
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_BINDMETHOD, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_BINDMETHOD.length();

                parseBindMethod( chars, pos, syncRepl, parserException );
            }

            // binddn
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_BINDDN, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_BINDDN.length();

                parseBindDn( chars, pos, syncRepl, parserException );
            }

            // saslmech
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SASLMECH, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SASLMECH.length();

                parseSaslMech( chars, pos, syncRepl, parserException );
            }

            // authcid
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_AUTHCID, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_AUTHCID.length();

                parseAuthcId( chars, pos, syncRepl, parserException );
            }

            // authzid
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_AUTHZID, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_AUTHZID.length();

                parseAuthzId( chars, pos, syncRepl, parserException );
            }

            // credentials
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_CREDENTIALS, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_CREDENTIALS.length();

                parseCredentials( chars, pos, syncRepl, parserException );
            }

            // realm
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_REALM, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_REALM.length();

                parseRealm( chars, pos, syncRepl, parserException );
            }

            // secprops
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SECPROPS, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SECPROPS.length();

                parseSecProps( chars, pos, syncRepl, parserException );
            }

            // keepalive
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_KEEPALIVE, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_KEEPALIVE.length();

                parseKeepAlive( chars, pos, syncRepl, parserException );
            }

            // starttls
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_STARTTLS, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_STARTTLS.length();

                parseStartTls( chars, pos, syncRepl, parserException );
            }

            // tls_cert
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_CERT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_CERT.length();

                parseTlsCert( chars, pos, syncRepl, parserException );
            }

            // tls_key
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_KEY, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_KEY.length();

                parseTlsKey( chars, pos, syncRepl, parserException );
            }

            // tls_cacert
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_CACERT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_CACERT.length();

                parseTlsCacert( chars, pos, syncRepl, parserException );
            }

            // tls_cacertdir
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_CACERTDIR, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_CACERTDIR.length();

                parseTlsCacertDir( chars, pos, syncRepl, parserException );
            }

            // tls_reqcert
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_REQCERT, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_REQCERT.length();

                parseTlsReqCert( chars, pos, syncRepl, parserException );
            }

            // tls_ciphersuite
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_CIPHERSUITE, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_CIPHERSUITE.length();

                parseTlsCipherSuite( chars, pos, syncRepl, parserException );
            }

            // tls_crlcheck
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_TLS_CRLCHECK, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_TLS_CRLCHECK.length();

                parseTlsCrlCheck( chars, pos, syncRepl, parserException );
            }

            // logbase
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_LOGBASE, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_LOGBASE.length();

                parseLogBase( chars, pos, syncRepl, parserException );
            }

            // logfilter
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_LOGFILTER, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_LOGFILTER.length();

                parseLogFilter( chars, pos, syncRepl, parserException );
            }

            // syncdata
            else if ( Strings.areEquals( chars, pos.start, KEYWORD_SYNCDATA, false ) >= 0 )
            {
                foundAtLeastOneProperty = true;
                pos.start += KEYWORD_SYNCDATA.length();

                parseSyncData( chars, pos, syncRepl, parserException );
            }

            // We couldn't find the appropriate option
            else
            {
                pos.start++;
            }
        }
        while ( ( pos.start != pos.length ) && ( ( c = Strings.charAt( chars, pos.start ) ) != '\0' ) );

        if ( foundAtLeastOneProperty )
        {
            return syncRepl;
        }

        return null;
    }


    /**
     * Parses the 'rid' option.
     *
     * @param s the string
     * @param pos the position
     * @param syncRepl the SyncRepl object
     * @param parserException the parser exception
     */
    private void parseRid( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setRid( value );
        }
    }


    private void parseProvider( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setProvider( Provider.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseSearchBase( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setSearchBase( value );
        }
    }


    private void parseType( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setType( Type.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseInterval( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setInterval( Interval.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseRetry( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setRetry( Retry.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseFilter( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setFilter( value );
        }
    }


    private void parseScope( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setScope( Scope.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseAttrs( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            String[] attrs = value.split( ",( )*" );

            if ( ( attrs != null ) && ( attrs.length > 0 ) )
            {
                syncRepl.addAttribute( attrs );
            }
        }
    }


    private void parseSizeLimit( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setSizeLimit( Integer.parseInt( value ) );
            }
            catch ( NumberFormatException e )
            {
                parserException.addParseException( new ParseException( "Unable to convert size limit value '" + value
                    + "' as an integer.", 0 ) );
            }
        }
    }


    private void parseTimeLimit( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setTimeLimit( Integer.parseInt( value ) );
            }
            catch ( NumberFormatException e )
            {
                parserException.addParseException( new ParseException( "Unable to convert time limit value '" + value
                    + "' as an integer.", 0 ) );
            }
        }
    }


    private void parseSchemaChecking( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setSchemaChecking( SchemaChecking.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseNetworkTimeout( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setNetworkTimeout( Integer.parseInt( value ) );
            }
            catch ( NumberFormatException e )
            {
                parserException.addParseException( new ParseException( "Unable to convert network timeout value '"
                    + value
                    + "' as an integer.", 0 ) );
            }
        }
    }


    private void parseTimeout( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setTimeout( Integer.parseInt( value ) );
            }
            catch ( NumberFormatException e )
            {
                parserException.addParseException( new ParseException( "Unable to convert timeout value '" + value
                    + "' as an integer.", 0 ) );
            }
        }
    }


    private void parseBindMethod( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setBindMethod( BindMethod.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseBindDn( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setBindDn( value );
        }
    }


    private void parseSaslMech( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setSaslMech( value );
        }
    }


    private void parseAuthcId( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setAuthcid( value );
        }
    }


    private void parseAuthzId( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setAuthzid( value );
        }
    }


    private void parseCredentials( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setCredentials( value );
        }
    }


    private void parseRealm( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setRealm( value );
        }
    }


    private void parseSecProps( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setSecProps( value );
        }
    }


    private void parseKeepAlive( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setKeepAlive( KeepAlive.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseStartTls( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setStartTls( StartTls.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseTlsCert( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setTlsCert( value );
        }
    }


    private void parseTlsKey( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setTlsKey( value );
        }
    }


    private void parseTlsCacert( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setTlsCacert( value );
        }
    }


    private void parseTlsCacertDir( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setTlsCacertDir( value );
        }
    }


    private void parseTlsReqCert( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setTlsReqcert( TlsReqCert.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseTlsCipherSuite( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setTlsCipherSuite( value );
        }
    }


    private void parseTlsCrlCheck( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setTlsCrlcheck( TlsCrlCheck.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private void parseLogBase( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setLogBase( value );
        }
    }


    private void parseLogFilter( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            syncRepl.setLogFilter( value );
        }
    }


    private void parseSyncData( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        String value = getQuotedOrNotQuotedOptionValue( chars, pos, syncRepl, parserException );

        if ( value != null )
        {
            try
            {
                syncRepl.setSyncData( SyncData.parse( value ) );
            }
            catch ( ParseException e )
            {
                parserException.addParseException( e );
            }
        }
    }


    private boolean findEqual( char[] chars, Position pos, SyncRepl syncRepl, SyncReplParserException parserException )
    {
        char c = Strings.charAt( chars, pos.start );
        do
        {
            // Whitespace
            if ( Character.isWhitespace( c ) )
            {
                pos.start++;
            }
            // '=' char
            else if ( c == '=' )
            {
                pos.start++;
                return true;
            }
            else
            {
                return false;
            }
        }
        while ( ( c = Strings.charAt( chars, pos.start ) ) != '\0' );

        return false;
    }

    private String getQuotedOrNotQuotedOptionValue( char[] chars, Position pos, SyncRepl syncRepl,
        SyncReplParserException parserException )
    {
        if ( findEqual( chars, pos, syncRepl, parserException ) )
        {
            char quoteChar = '\0';
            boolean isInQuotes = false;
            char c = Strings.charAt( chars, pos.start );
            char[] v = new char[chars.length - pos.start];
            int current = 0;

            do
            {
                if ( ( current == 0 ) && !isInQuotes )
                {
                    // Whitespace
                    if ( Character.isWhitespace( c ) )
                    {
                        // We ignore all whitespaces until we find the start of the value
                        pos.start++;
                        continue;
                    }
                    // Double quotes (") or single quotes (')
                    else if ( ( c == '"' ) || ( c == '\'' ) )
                    {
                        isInQuotes = true;
                        quoteChar = c;
                        pos.start++;
                        continue;
                    }
                    // Any other char is part of a value
                    else
                    {
                        v[current++] = c;
                        pos.start++;
                    }
                }
                else
                {
                    if ( isInQuotes )
                    {
                        // Double quotes (") or single quotes (')
                        if ( quoteChar == c )
                        {
                            isInQuotes = false;
                            pos.start++;
                            continue;
                        }
                        // Checking for escaped quotes
                        else if ( c == '\\' )
                        {
                            // Double quotes (")
                            if ( ( quoteChar == '"' ) && ( Strings.areEquals( chars, pos.start, "\\\"" ) >= 0 ) )
                            {
                                v[current++] = '"';
                                pos.start += 2;
                                continue;
                            }
                            // Single quotes (')
                            else if ( ( quoteChar == '\'' ) && ( Strings.areEquals( chars, pos.start, "\\'" ) >= 0 ) )
                            {
                                v[current++] = '\'';
                                pos.start += 2;
                                continue;
                            }
                        }
                        // Any other char is part of a value
                        else
                        {
                            v[current++] = c;
                            pos.start++;
                        }
                    }
                    else
                    {
                        // Whitespace
                        if ( Character.isWhitespace( c ) )
                        {
                            // Once we have found the start of the value, the first whitespace is the exit
                            break;
                        }
                        // Any other char is part of a value
                        else
                        {
                            v[current++] = c;
                            pos.start++;
                        }
                    }
                }
            }
            while ( ( c = Strings.charAt( chars, pos.start ) ) != '\0' );

            // Checking the resulting value
            if ( current == 0 )
            {
                parserException.addParseException( new ParseException( "Couldn't find the value for option '"
                    + KEYWORD_RID
                    + "'.", pos.start ) );
                return null;
            }

            char[] value = new char[current];
            System.arraycopy( v, 0, value, 0, current );

            // Getting the value as a String
            return new String( value );
        }
        else
        {
            parserException.addParseException( new ParseException( "Couldn't find the value for option '" + KEYWORD_RID
                + "'.", pos.start ) );
            return null;
        }
    }
}
