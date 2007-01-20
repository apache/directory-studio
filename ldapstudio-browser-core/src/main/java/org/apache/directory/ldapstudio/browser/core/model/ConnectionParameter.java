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


import java.io.Serializable;


/**
 * A Bean class to hold the connection parameters.
 * It is used to make connections persistent.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ConnectionParameter implements Serializable
{

    /** The serialVersionUID. */
    private static final long serialVersionUID = 3679909600732887964L;

    /** The symbolic name. */
    private String name;

    /** The host name or IP address of the LDAP server. */
    private String host;

    /** The encryption method, one of IConnection.ENCYRPTION_NONE, IConnection.ENCYRPTION_LDAPS or IConnection.ENCYRPTION_STARTTLS. */
    private int encryptionMethod;

    /** The port of the LDAP server. */
    private int port;

    /** Flag indicating if base DNs should be fetched from namingContexts attribute of the Root DSE. */
    private boolean fetchBaseDNs;

    /** The user provided base DN. */
    private DN baseDN;

    /** The time limit im milliseconds. */
    private int timeLimit;

    /** The count limit. */
    private int countLimit;

    /** The alias dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING or IConnection.DEREFERENCE_ALIASES_SEARCH. */
    private int aliasesDereferencingMethod;

    /** The referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE or IConnection.HANDLE_REFERRALS_FOLLOW. */
    private int referralsHandlingMethod;

    /** The authentication method, one of IConnection.AUTH_ANONYMOUS or IConnection.AUTH_SIMPLE */
    private int authMethod;

    /** The bind principal, typically a DN. */
    private String bindPrincipal;

    /** The bind password. */
    private String bindPassword;

    /** The connection provider class name. */
    private String connectionProviderClassName;


    /**
     * Creates a new instance of ConnectionParameter.
     */
    public ConnectionParameter()
    {
    }


    /**
     * Gets the user provided base DN.
     * 
     * @return the user provided base DN
     */
    public DN getBaseDN()
    {
        return baseDN;
    }


    /**
     * Sets the user provided base DN.
     * 
     * @param baseDN the user provided base DN
     */
    public void setBaseDN( DN baseDN )
    {
        this.baseDN = baseDN;
    }


    /**
     * Gets the count limit, 0 means no limit.
     * 
     * @return the count limit
     */
    public int getCountLimit()
    {
        return countLimit;
    }


    /**
     * Sets the count limit, 0 means no limit.
     * 
     * @param countLimit the count limit
     */
    public void setCountLimit( int countLimit )
    {
        this.countLimit = countLimit;
    }


    /**
     * Gets the host name or IP address of the LDAP server.
     * 
     * @return the host
     */
    public String getHost()
    {
        return host;
    }


    /**
     * Sets the host name or IP address of the LDAP server.
     * 
     * @param host the host
     */
    public void setHost( String host )
    {
        this.host = host;
    }


    /**
     * Gets the symbolic name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets the symbolic name.
     * 
     * @param name the name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort()
    {
        return port;
    }


    /**
     * Sets the port.
     * 
     * @param port the port
     */
    public void setPort( int port )
    {
        this.port = port;
    }


    /**
     * Gets the time limit in milliseconds, 0 means no limit.
     * 
     * @return the time limit
     */
    public int getTimeLimit()
    {
        return timeLimit;
    }


    /**
     * Sets the time limit in milliseconds, 0 means no limit.
     * 
     * @param timeLimit the time limit
     */
    public void setTimeLimit( int timeLimit )
    {
        this.timeLimit = timeLimit;
    }


    /**
     * Checks if base DNs should be fetched.
     * If true the base DNs are fetched from the namingContexts
     * attribute of the Root DSE.
     * 
     * @return true, if base DNs should be fetched
     */
    public boolean isFetchBaseDNs()
    {
        return fetchBaseDNs;
    }


    /**
     * Sets the fetch base DNs flag.
     * If true the base DNs are fetched from the namingContexts
     * attribute of the Root DSE.
     * 
     * @param fetchBaseDNs the fetch base DNs flag
     */
    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
        this.fetchBaseDNs = fetchBaseDNs;
    }


    /**
     * Sets the bind DN.
     * 
     * @param bindDN the bind DN
     * 
     * @deprecated use setBindPrincipal( String ) instead
     */
    public void setBindDN( DN bindDN )
    {
        this.setBindPrincipal( bindDN.toString() );
    }


    /**
     * Gets the bind principal, typically a DN.
     * 
     * @return the bind principal
     */
    public String getBindPrincipal()
    {
        return bindPrincipal;
    }


    /**
     * Sets the bind principal, typically a DN.
     * 
     * @param bindPrincipal the bind principal
     */
    public void setBindPrincipal( String bindPrincipal )
    {
        this.bindPrincipal = bindPrincipal;
    }


    /**
     * Gets the bind password.
     * 
     * @return the bind password
     */
    public String getBindPassword()
    {
        return bindPassword;
    }


    /**
     * Sets the bind password.
     * 
     * @param bindPassword the bind password
     */
    public void setBindPassword( String bindPassword )
    {
        this.bindPassword = bindPassword;
    }


    /**
     * Gets the auth method, one of IConnection.AUTH_ANONYMOUS
     * or IConnection.AUTH_SIMPLE.
     * 
     * @return the auth method
     */
    public int getAuthMethod()
    {
        return authMethod;
    }


    /**
     * Sets the auth method, one of IConnection.AUTH_ANONYMOUS
     * or IConnection.AUTH_SIMPLE.
     * 
     * @param authMethod the auth method
     */
    public void setAuthMethod( int authMethod )
    {
        this.authMethod = authMethod;
    }


    /**
     * Gets the connection provider class name.
     * 
     * @return the connection provider class name
     */
    public String getConnectionProviderClassName()
    {
        return connectionProviderClassName;
    }


    /**
     * Sets the connection provider class name.
     * 
     * @param connectionProviderClassName the connection provider class name
     */
    public void setConnectionProviderClassName( String connectionProviderClassName )
    {
        this.connectionProviderClassName = connectionProviderClassName;
    }


    /**
     * Gets the encryption method, one of IConnection.ENCYRPTION_NONE, 
     * IConnection.ENCYRPTION_LDAPS or IConnection.ENCYRPTION_STARTTLS.
     * 
     * @return the encryption method
     */
    public int getEncryptionMethod()
    {
        return encryptionMethod;
    }


    /**
     * Sets the encryption method, one of IConnection.ENCYRPTION_NONE, 
     * IConnection.ENCYRPTION_LDAPS or IConnection.ENCYRPTION_STARTTLS.
     * 
     * @param encryptionMethod the encryption method
     */
    public void setEncryptionMethod( int encryptionMethod )
    {
        this.encryptionMethod = encryptionMethod;
    }


    /**
     * Gets the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     * IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING 
     * or IConnection.DEREFERENCE_ALIASES_SEARCH.
     * 
     * @return the aliases dereferencing method
     */
    public int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingMethod;
    }


    /**
     * Sets the aliases dereferencing method, one of IConnection.DEREFERENCE_ALIASES_NEVER, 
     * IConnection.DEREFERENCE_ALIASES_ALWAYS, IConnection.DEREFERENCE_ALIASES_FINDING 
     * or IConnection.DEREFERENCE_ALIASES_SEARCH.
     * 
     * @param aliasesDereferencingMethod the aliases dereferencing method
     */
    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
    }


    /**
     * Gets the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE
     *  or IConnection.HANDLE_REFERRALS_FOLLOW.
     * 
     * @return the referrals handling method
     */
    public int getReferralsHandlingMethod()
    {
        return referralsHandlingMethod;
    }


    /**
     * Sets the referrals handling method, one of IConnection.HANDLE_REFERRALS_IGNORE or 
     * IConnection.HANDLE_REFERRALS_FOLLOW.
     * 
     * @param referralsHandlingMethod the referrals handling method
     */
    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.referralsHandlingMethod = referralsHandlingMethod;
    }

}
