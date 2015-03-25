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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class implements a SyncRepl object.
 */
public class SyncRepl
{
    /** The replica ID */
    private String rid;

    /** The provider */
    private Provider provider;

    /** The search base */
    private String searchBase;

    /** The type */
    private Type type;

    /** The interval */
    private Interval interval;

    /** The retry */
    private Retry retry;

    /** The filter */
    private String filter;

    /** The scope */
    private Scope scope;

    /** The attributes */
    private List<String> attributes = new ArrayList<String>();

    /** The attrsonly flag */
    private boolean isAttrsOnly;

    /** The size limit */
    private int sizeLimit = -1;

    /** The time limit */
    private int timeLimit = -1;

    /** The schema checking */
    private SchemaChecking schemaChecking;

    /** The network timeout */
    private int networkTimeout = -1;

    /** The timeout */
    private int timeout = -1;

    /** The bind method */
    private BindMethod bindMethod;

    /** The bind dn */
    private String bindDn;

    /** The sasl mech */
    private String saslMech;

    /** The authentication id */
    private String authcid;

    /** The authorization id */
    private String authzid;

    /** The credentials */
    private String credentials;

    /** The realm */
    private String realm;

    /** The sec props */
    private String secProps;

    /** The keep alive */
    private KeepAlive keepAlive;

    /** The Start TLS */
    private StartTls startTls;

    /** The TLS cert */
    private String tlsCert;

    /** The TLS key */
    private String tlsKey;

    /** The TLS cacert */
    private String tlsCacert;

    /** The TLS cacert dir */
    private String tlsCacertDir;

    /** The TLS reqcert */
    private TlsReqCert tlsReqcert;

    /** The TLS cipher suite */
    private String tlsCipherSuite;

    /** The TLS crl check */
    private TlsCrlCheck tlsCrlcheck;

    /** The log base */
    private String logBase;

    /** The log filter */
    private String logFilter;

    /** The sync data */
    private SyncData syncData;


    public SyncRepl()
    {
        // TODO Auto-generated constructor stub
    }


    /**
     * Creates a default SyncRepl value.
     *
     * @return a default SyncRepl value
     */
    public static SyncRepl createDefault()
    {
        SyncRepl syncRepl = new SyncRepl();

        return syncRepl;
    }


    /**
     * Gets a copy of a SyncRepl object.
     *
     * @param syncRepl the initial SyncRepl object
     * @return a copy of the given SyncRepl object
     */
    public static SyncRepl copy( SyncRepl syncRepl )
    {
        if ( syncRepl != null )
        {
            SyncRepl syncReplCopy = new SyncRepl();

            syncReplCopy.setRid( syncRepl.getRid() );
            syncReplCopy.setProvider( Provider.copy( syncRepl.getProvider() ) );
            syncReplCopy.setSearchBase( syncRepl.getSearchBase() );
            syncReplCopy.setType( syncRepl.getType() );
            syncReplCopy.setInterval( Interval.copy( syncRepl.getInterval() ) );
            syncReplCopy.setRetry( Retry.copy( syncRepl.getRetry() ) );
            syncReplCopy.setFilter( syncRepl.getFilter() );
            syncReplCopy.setScope( syncRepl.getScope() );
            syncReplCopy.addAttribute( syncRepl.getAttributes() );
            syncReplCopy.setAttrsOnly( syncRepl.isAttrsOnly() );
            syncReplCopy.setSizeLimit( syncRepl.getSizeLimit() );
            syncReplCopy.setTimeLimit( syncRepl.getTimeLimit() );
            syncReplCopy.setSchemaChecking( syncRepl.getSchemaChecking() );
            syncReplCopy.setNetworkTimeout( syncRepl.getNetworkTimeout() );
            syncReplCopy.setTimeout( syncRepl.getTimeout() );
            syncReplCopy.setBindMethod( syncRepl.getBindMethod() );
            syncReplCopy.setBindDn( syncRepl.getBindDn() );
            syncReplCopy.setSaslMech( syncRepl.getSaslMech() );
            syncReplCopy.setAuthcid( syncRepl.getAuthcid() );
            syncReplCopy.setAuthzid( syncRepl.getAuthzid() );
            syncReplCopy.setCredentials( syncRepl.getCredentials() );
            syncReplCopy.setRealm( syncRepl.getRealm() );
            syncReplCopy.setSecProps( syncRepl.getSecProps() );
            syncReplCopy.setKeepAlive( KeepAlive.copy( syncRepl.getKeepAlive() ) );
            syncReplCopy.setStartTls( syncRepl.getStartTls() );
            syncReplCopy.setTlsCert( syncRepl.getTlsCert() );
            syncReplCopy.setTlsKey( syncRepl.getTlsKey() );
            syncReplCopy.setTlsCacert( syncRepl.getTlsCacert() );
            syncReplCopy.setTlsCacertDir( syncRepl.getTlsCacertDir() );
            syncReplCopy.setTlsReqcert( syncRepl.getTlsReqcert() );
            syncReplCopy.setTlsCipherSuite( syncRepl.getTlsCipherSuite() );
            syncReplCopy.setTlsCrlcheck( syncRepl.getTlsCrlcheck() );
            syncReplCopy.setLogBase( syncRepl.getLogBase() );
            syncReplCopy.setLogFilter( syncRepl.getLogFilter() );
            syncReplCopy.setSyncData( syncRepl.getSyncData() );

            return syncReplCopy;
        }

        return null;
    }


    /**
     * Gets a copy of the SyncRepl object.
     *
     * @return a copy of the SyncRepl object
     */
    public SyncRepl copy()
    {
        return SyncRepl.copy( this );
    }


    public String getRid()
    {
        return rid;
    }


    public String getSearchBase()
    {
        return searchBase;
    }


    public Type getType()
    {
        return type;
    }


    public Interval getInterval()
    {
        return interval;
    }


    public Retry getRetry()
    {
        return retry;
    }


    public String getFilter()
    {
        return filter;
    }


    public Scope getScope()
    {
        return scope;
    }


    public String[] getAttributes()
    {
        return attributes.toArray( new String[0] );
    }


    public boolean isAttrsOnly()
    {
        return isAttrsOnly;
    }


    public int getSizeLimit()
    {
        return sizeLimit;
    }


    public int getTimeLimit()
    {
        return timeLimit;
    }


    public SchemaChecking getSchemaChecking()
    {
        return schemaChecking;
    }


    public int getNetworkTimeout()
    {
        return networkTimeout;
    }


    public int getTimeout()
    {
        return timeout;
    }


    public BindMethod getBindMethod()
    {
        return bindMethod;
    }


    public String getBindDn()
    {
        return bindDn;
    }


    public String getSaslMech()
    {
        return saslMech;
    }


    public String getAuthcid()
    {
        return authcid;
    }


    public String getAuthzid()
    {
        return authzid;
    }


    public String getCredentials()
    {
        return credentials;
    }


    public String getRealm()
    {
        return realm;
    }


    public String getSecProps()
    {
        return secProps;
    }


    public KeepAlive getKeepAlive()
    {
        return keepAlive;
    }


    public StartTls getStartTls()
    {
        return startTls;
    }


    public String getTlsCert()
    {
        return tlsCert;
    }


    public String getTlsKey()
    {
        return tlsKey;
    }


    public String getTlsCacert()
    {
        return tlsCacert;
    }


    public String getTlsCacertDir()
    {
        return tlsCacertDir;
    }


    public TlsReqCert getTlsReqcert()
    {
        return tlsReqcert;
    }


    public String getTlsCipherSuite()
    {
        return tlsCipherSuite;
    }


    public TlsCrlCheck getTlsCrlcheck()
    {
        return tlsCrlcheck;
    }


    public String getLogBase()
    {
        return logBase;
    }


    public String getLogFilter()
    {
        return logFilter;
    }


    public SyncData getSyncData()
    {
        return syncData;
    }


    public void setRid( String rid )
    {
        this.rid = rid;
    }


    public Provider getProvider()
    {
        return provider;
    }


    public void setProvider( Provider provider )
    {
        this.provider = provider;
    }


    public void setSearchBase( String searchBase )
    {
        this.searchBase = searchBase;
    }


    public void setType( Type type )
    {
        this.type = type;
    }


    public void setInterval( Interval interval )
    {
        this.interval = interval;
    }


    public void setRetry( Retry retry )
    {
        this.retry = retry;
    }


    public void setFilter( String filter )
    {
        this.filter = filter;
    }


    public void setScope( Scope scope )
    {
        this.scope = scope;
    }


    public void addAttribute( String... attributes )
    {
        if ( attributes != null )
        {
            for ( String attribute : attributes )
            {
                this.attributes.add( attribute );
            }
        }
    }


    public void removeAttribute( String... attributes )
    {
        if ( attributes != null )
        {
            for ( String attribute : attributes )
            {
                this.attributes.remove( attribute );
            }
        }
    }


    public void setAttributes( String[] attributes )
    {
        this.attributes.clear();
        this.attributes.addAll( Arrays.asList( attributes ) );
    }


    public void setAttrsOnly( boolean isAttrsOnly )
    {
        this.isAttrsOnly = isAttrsOnly;
    }


    public void setSizeLimit( int sizeLimit )
    {
        this.sizeLimit = sizeLimit;
    }


    public void setTimeLimit( int timeLimit )
    {
        this.timeLimit = timeLimit;
    }


    public void setSchemaChecking( SchemaChecking schemaChecking )
    {
        this.schemaChecking = schemaChecking;
    }


    public void setNetworkTimeout( int networkTimeout )
    {
        this.networkTimeout = networkTimeout;
    }


    public void setTimeout( int timeout )
    {
        this.timeout = timeout;
    }


    public void setBindMethod( BindMethod bindMethod )
    {
        this.bindMethod = bindMethod;
    }


    public void setBindDn( String bindDn )
    {
        this.bindDn = bindDn;
    }


    public void setSaslMech( String saslMech )
    {
        this.saslMech = saslMech;
    }


    public void setAuthcid( String authcid )
    {
        this.authcid = authcid;
    }


    public void setAuthzid( String authzid )
    {
        this.authzid = authzid;
    }


    public void setCredentials( String credentials )
    {
        this.credentials = credentials;
    }


    public void setRealm( String realm )
    {
        this.realm = realm;
    }


    public void setSecProps( String secProps )
    {
        this.secProps = secProps;
    }


    public void setKeepAlive( KeepAlive keepAlive )
    {
        this.keepAlive = keepAlive;
    }


    public void setStartTls( StartTls startTls )
    {
        this.startTls = startTls;
    }


    public void setTlsCert( String tlsCert )
    {
        this.tlsCert = tlsCert;
    }


    public void setTlsKey( String tlsKey )
    {
        this.tlsKey = tlsKey;
    }


    public void setTlsCacert( String tlsCacert )
    {
        this.tlsCacert = tlsCacert;
    }


    public void setTlsCacertDir( String tlsCacertDir )
    {
        this.tlsCacertDir = tlsCacertDir;
    }


    public void setTlsReqcert( TlsReqCert tlsReqcert )
    {
        this.tlsReqcert = tlsReqcert;
    }


    public void setTlsCipherSuite( String tlsCipherSuite )
    {
        this.tlsCipherSuite = tlsCipherSuite;
    }


    public void setTlsCrlcheck( TlsCrlCheck tlsCrlcheck )
    {
        this.tlsCrlcheck = tlsCrlcheck;
    }


    public void setLogBase( String logBase )
    {
        this.logBase = logBase;
    }


    public void setLogFilter( String logFilter )
    {
        this.logFilter = logFilter;
    }


    public void setSyncData( SyncData syncData )
    {
        this.syncData = syncData;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // Replica ID
        if ( rid != null )
        {
            sb.append( "rid=" );
            sb.append( rid );
        }

        // Provider
        if ( provider != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "provider=" );
            sb.append( provider.toString() );
        }

        // Search Base
        if ( searchBase != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "searchbase=" );
            sb.append( '"' );
            sb.append( escapeDoubleQuotes( searchBase ) );
            sb.append( '"' );
        }

        // Type
        if ( type != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "type=" );
            sb.append( type );
        }

        // Interval
        if ( interval != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "interval=" );
            sb.append( interval );
        }

        // Retry
        if ( retry != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "retry=" );
            sb.append( '"' );
            sb.append( escapeDoubleQuotes( retry.toString() ) );
            sb.append( '"' );
        }

        // Filter
        if ( filter != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "filter=" );
            sb.append( '"' );
            sb.append( escapeDoubleQuotes( filter ) );
            sb.append( '"' );
        }

        // Scope
        if ( scope != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "scope=" );
            sb.append( scope );
        }

        // Attributes
        if ( ( attributes != null ) && ( attributes.size() > 0 ) )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "attrs=" );
            sb.append( '"' );

            // Looping on all attributes
            for ( int i = 0; i < attributes.size(); i++ )
            {
                // Adding the attribute
                sb.append( attributes.get( i ) );

                // Adding the separator (except for the last one)
                if ( i != attributes.size() - 1 )
                {
                    sb.append( ',' );
                }
            }

            sb.append( '"' );
        }

        // Attrsonly Flag
        if ( isAttrsOnly )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "attrsonly" );
        }

        // Size Limit
        if ( sizeLimit != -1 )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "sizelimit=" );
            sb.append( sizeLimit );
        }

        // Time Limit
        if ( timeLimit != -1 )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "timelimit=" );
            sb.append( timeLimit );
        }

        // Schema Checking
        if ( schemaChecking != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "schemachecking=" );
            sb.append( schemaChecking );
        }

        // Network Timeout
        if ( networkTimeout != -1 )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "network-timeout=" );
            sb.append( networkTimeout );
        }

        // Timeout
        if ( timeout != -1 )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "timeout=" );
            sb.append( timeout );
        }

        // Bind Method
        if ( bindMethod != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "bindmethod=" );
            sb.append( bindMethod );
        }

        // Bind DN
        if ( bindDn != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "binddn=" );
            sb.append( '"' );
            sb.append( bindDn );
            sb.append( '"' );
        }

        // SASL Mech
        if ( saslMech != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "saslmech=" );
            sb.append( saslMech );
        }

        // Authentication ID
        if ( authcid != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "authcid=" );
            sb.append( '"' );
            sb.append( authcid );
            sb.append( '"' );
        }

        // Authorization ID
        if ( authzid != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "authzid=" );
            sb.append( '"' );
            sb.append( authzid );
            sb.append( '"' );
        }

        // Credentials
        if ( credentials != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "credentials=" );
            sb.append( credentials );
        }

        // Realm
        if ( realm != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "realm=" );
            sb.append( realm );
        }

        // Sec Props
        if ( secProps != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "secProps=" );
            sb.append( secProps );
        }

        // Keep Alive
        if ( keepAlive != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "keepalive=" );
            sb.append( keepAlive );
        }

        // Start TLS
        if ( startTls != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "starttls=" );
            sb.append( startTls );
        }

        // TLS Cert
        if ( tlsCert != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_cert=" );
            sb.append( tlsCert );
        }

        // TLS Key
        if ( tlsKey != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_key=" );
            sb.append( tlsKey );
        }

        // TLS Cacert
        if ( tlsCacert != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_cacert=" );
            sb.append( tlsCacert );
        }

        // TLS Cacert Dir
        if ( tlsCacertDir != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_cacertdir=" );
            sb.append( tlsCacertDir );
        }

        // TLS Reqcert
        if ( tlsReqcert != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_reqcert=" );
            sb.append( tlsReqcert );
        }

        // TLS Cipher Suite
        if ( tlsCipherSuite != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_ciphersuite=" );
            sb.append( tlsCipherSuite );
        }

        //  TLS Crl Check
        if ( tlsCrlcheck != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "tls_crlcheck=" );
            sb.append( tlsCrlcheck );
        }

        // Log Base
        if ( logBase != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "logbase=" );
            sb.append( '"' );
            sb.append( escapeDoubleQuotes( logBase ) );
            sb.append( '"' );
        }

        // Log Filter
        if ( logFilter != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "logfilter=" );
            sb.append( '"' );
            sb.append( escapeDoubleQuotes( logFilter ) );
            sb.append( '"' );
        }

        // Sync Data
        if ( syncData != null )
        {
            appendSpaceIfNeeded( sb );
            sb.append( "syncdata=" );
            sb.append( syncData );
        }

        return sb.toString();
    }


    /**
     * Appends a space if the string is not empty.
     *
     * @param sb the string
     */
    private void appendSpaceIfNeeded( StringBuilder sb )
    {
        if ( ( sb != null ) && ( sb.length() > 0 ) )
        {
            sb.append( " " );
        }
    }


    /**
     * Escapes all double quotes (") found in the given text.
     *
     * @param text the text
     * @return a string where all double quotes are escaped
     */
    private String escapeDoubleQuotes( String text )
    {
        if ( text != null )
        {
            return text.replace( "\"", "\\\"" );
        }

        return null;
    }
}
