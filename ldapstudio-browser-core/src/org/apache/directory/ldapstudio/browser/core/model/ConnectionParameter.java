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


public class ConnectionParameter implements Serializable
{

    private static final long serialVersionUID = 3679909600732887964L;

    private String name;

    private String host;

    private int encryptionMethod;

    private int port;

    private boolean fetchBaseDNs;

    private DN baseDN;

    private int timeLimit;

    private int countLimit;

    private int aliasesDereferencingMethod;

    private int referralsHandlingMethod;

    private int authMethod;

    private String bindPrincipal;

    private String bindPassword;

    private String connectionProviderClassName;


    public ConnectionParameter()
    {
    }


    public DN getBaseDN()
    {
        return baseDN;
    }


    public void setBaseDN( DN baseDN )
    {
        this.baseDN = baseDN;
    }


    public int getCountLimit()
    {
        return countLimit;
    }


    public void setCountLimit( int countLimit )
    {
        this.countLimit = countLimit;
    }


    public String getHost()
    {
        return host;
    }


    public void setHost( String host )
    {
        this.host = host;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public int getPort()
    {
        return port;
    }


    public void setPort( int port )
    {
        this.port = port;
    }


    public int getTimeLimit()
    {
        return timeLimit;
    }


    public void setTimeLimit( int timeLimit )
    {
        this.timeLimit = timeLimit;
    }


    public boolean isFetchBaseDNs()
    {
        return fetchBaseDNs;
    }


    public void setFetchBaseDNs( boolean fetchBaseDNs )
    {
        this.fetchBaseDNs = fetchBaseDNs;
    }


    public void setBindDN( DN bindDN )
    {
        this.setBindPrincipal( bindDN.toString() );
    }


    public String getBindPrincipal()
    {
        return bindPrincipal;
    }


    public void setBindPrincipal( String bindPrincipal )
    {
        this.bindPrincipal = bindPrincipal;
    }


    public String getBindPassword()
    {
        return bindPassword;
    }


    public void setBindPassword( String bindPassword )
    {
        this.bindPassword = bindPassword;
    }


    public int getAuthMethod()
    {
        return authMethod;
    }


    public void setAuthMethod( int authMethod )
    {
        this.authMethod = authMethod;
    }


    public String getConnectionProviderClassName()
    {
        return connectionProviderClassName;
    }


    public void setConnectionProviderClassName( String connectionProviderClassName )
    {
        this.connectionProviderClassName = connectionProviderClassName;
    }


    public int getEncryptionMethod()
    {
        return encryptionMethod;
    }


    public void setEncryptionMethod( int encryptionMethod )
    {
        this.encryptionMethod = encryptionMethod;
    }


    public int getAliasesDereferencingMethod()
    {
        return aliasesDereferencingMethod;
    }


    public void setAliasesDereferencingMethod( int aliasesDereferencingMethod )
    {
        this.aliasesDereferencingMethod = aliasesDereferencingMethod;
    }


    public int getReferralsHandlingMethod()
    {
        return referralsHandlingMethod;
    }


    public void setReferralsHandlingMethod( int referralsHandlingMethod )
    {
        this.referralsHandlingMethod = referralsHandlingMethod;
    }

}
