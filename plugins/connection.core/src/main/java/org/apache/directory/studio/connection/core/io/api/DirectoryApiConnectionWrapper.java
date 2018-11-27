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
package org.apache.directory.studio.connection.core.io.api;


import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.naming.ContextNotEmptyException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.codec.api.DefaultConfigurableBinaryAttributeDetector;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.entry.AttributeUtils;
import org.apache.directory.api.ldap.model.entry.DefaultModification;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.ModificationOperation;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.api.ldap.model.filter.ExprNode;
import org.apache.directory.api.ldap.model.filter.FilterParser;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.api.ldap.model.message.AliasDerefMode;
import org.apache.directory.api.ldap.model.message.BindRequest;
import org.apache.directory.api.ldap.model.message.BindRequestImpl;
import org.apache.directory.api.ldap.model.message.BindResponse;
import org.apache.directory.api.ldap.model.message.DeleteRequest;
import org.apache.directory.api.ldap.model.message.DeleteRequestImpl;
import org.apache.directory.api.ldap.model.message.DeleteResponse;
import org.apache.directory.api.ldap.model.message.LdapResult;
import org.apache.directory.api.ldap.model.message.ModifyDnRequest;
import org.apache.directory.api.ldap.model.message.ModifyDnRequestImpl;
import org.apache.directory.api.ldap.model.message.ModifyDnResponse;
import org.apache.directory.api.ldap.model.message.ModifyRequest;
import org.apache.directory.api.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.api.ldap.model.message.ModifyResponse;
import org.apache.directory.api.ldap.model.message.Referral;
import org.apache.directory.api.ldap.model.message.ResultCodeEnum;
import org.apache.directory.api.ldap.model.message.ResultResponse;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.api.ldap.model.message.SearchRequestImpl;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.ldap.client.api.SaslCramMd5Request;
import org.apache.directory.ldap.client.api.SaslDigestMd5Request;
import org.apache.directory.ldap.client.api.SaslGssApiRequest;
import org.apache.directory.ldap.client.api.SaslPlainRequest;
import org.apache.directory.ldap.client.api.exception.InvalidConnectionException;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.ConnectionWrapperUtils;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.StudioTrustManager;
import org.apache.directory.studio.connection.core.io.jndi.CancelException;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.osgi.util.NLS;


/**
 * A ConnectionWrapper is a wrapper for a real directory connection implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DirectoryApiConnectionWrapper implements ConnectionWrapper
{
    /** The search request number */
    private static int searchRequestNum = 0;

    /** The connection*/
    private Connection connection;

    /** The LDAP Connection Configuration */
    private LdapConnectionConfig ldapConnectionConfig;

    /** The LDAP Connection */
    private LdapNetworkConnection ldapConnection;

    /** The binary attribute detector */
    private DefaultConfigurableBinaryAttributeDetector binaryAttributeDetector;

    /** Indicates if the wrapper is connected */
    private boolean isConnected = false;

    /** The current job thread */
    private Thread jobThread;

    /** The bind principal */
    private String bindPrincipal;

    /** The bind password */
    private String bindPassword;

    /** The SASL PLAIN authzid */
    private String authzId;


    /**
     * Creates a new instance of JNDIConnectionContext.
     * 
     * @param connection the connection
     */
    public DirectoryApiConnectionWrapper( Connection connection )
    {
        this.connection = connection;
    }


    /**
     * {@inheritDoc}
     */
    public void connect( StudioProgressMonitor monitor )
    {
        ldapConnection = null;
        isConnected = false;
        jobThread = null;

        try
        {
            doConnect( monitor );
        }
        catch ( Exception e )
        {
            disconnect();
            monitor.reportError( e );
        }
    }


    private void doConnect( final StudioProgressMonitor monitor ) throws Exception
    {
        ldapConnection = null;
        isConnected = true;

        ldapConnectionConfig = new LdapConnectionConfig();
        ldapConnectionConfig.setLdapHost( connection.getHost() );
        ldapConnectionConfig.setLdapPort( connection.getPort() );
        
        long timeout = connection.getTimeout();
        
        if ( timeout < 0 ) 
        {
            timeout = 30000L;
        }
        
        ldapConnectionConfig.setTimeout( timeout );
        
        binaryAttributeDetector = new DefaultConfigurableBinaryAttributeDetector();
        ldapConnectionConfig.setBinaryAttributeDetector( binaryAttributeDetector );
        
        if ( ( connection.getEncryptionMethod() == EncryptionMethod.LDAPS )
            || ( connection.getEncryptionMethod() == EncryptionMethod.START_TLS ) )
        {
            ldapConnectionConfig.setUseSsl( connection.getEncryptionMethod() == EncryptionMethod.LDAPS );
            ldapConnectionConfig.setUseTls( connection.getEncryptionMethod() == EncryptionMethod.START_TLS );

            try
            {
                // get default trust managers (using JVM "cacerts" key store)
                TrustManagerFactory factory = TrustManagerFactory.getInstance( TrustManagerFactory
                    .getDefaultAlgorithm() );
                factory.init( ( KeyStore ) null );
                TrustManager[] defaultTrustManagers = factory.getTrustManagers();

                // create wrappers around the trust managers
                StudioTrustManager[] trustManagers = new StudioTrustManager[defaultTrustManagers.length];
        
                for ( int i = 0; i < defaultTrustManagers.length; i++ )
                {
                    trustManagers[i] = new StudioTrustManager( ( X509TrustManager ) defaultTrustManagers[i] );
                    trustManagers[i].setHost( connection.getHost() );
                }

                ldapConnectionConfig.setTrustManagers( trustManagers );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Set lower timeout for connecting
                    long oldTimeout = ldapConnectionConfig.getTimeout();
                    ldapConnectionConfig.setTimeout( Math.min( oldTimeout, 5000L ) );

                    // Connecting
                    ldapConnection = new LdapNetworkConnection( ldapConnectionConfig );
                    boolean connected = ldapConnection.connect();
                    
                    if ( !connected )
                    {
                        throw new Exception( Messages.DirectoryApiConnectionWrapper_UnableToConnect );
                    }

                    // Set old timeout again
                    ldapConnectionConfig.setTimeout( oldTimeout );
                }
                catch ( Exception e )
                {
                    exception = e;
                    
                    try
                    {
                        if ( ldapConnection != null )
                        {
                            ldapConnection.close();
                        }
                    }
                    catch ( Exception exception )
                    {
                        // Nothing to do
                    }
                    finally
                    {
                        ldapConnection = null;
                        binaryAttributeDetector = null;
                    }
                }
            }
        };

        runAndMonitor( runnable, monitor );

        if ( runnable.getException() != null )
        {
            throw runnable.getException();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void disconnect()
    {
        if ( jobThread != null )
        {
            Thread t = jobThread;
            jobThread = null;
            t.interrupt();
        }
        if ( ldapConnection != null )
        {
            try
            {
                ldapConnection.close();
            }
            catch ( Exception e )
            {
                // ignore
            }
            ldapConnection = null;
            binaryAttributeDetector = null;
        }
        isConnected = false;
    }


    /**
     * {@inheritDoc}
     */
    public void bind( StudioProgressMonitor monitor )
    {
        try
        {
            doBind( monitor );
        }
        catch ( Exception e )
        {
            disconnect();
            monitor.reportError( e );
        }
    }

    
    private BindResponse bindSimple( String bindPrincipal, String bindPassword ) throws LdapException
    {
        BindRequest bindRequest = new BindRequestImpl();
        bindRequest.setName( bindPrincipal );
        bindRequest.setCredentials( bindPassword );
        
        return ldapConnection.bind( bindRequest );
    }
    
    
    private BindResponse bindSaslPlain() throws LdapException
    {
        SaslPlainRequest saslPlainRequest = new SaslPlainRequest();
        saslPlainRequest.setUsername( bindPrincipal );
        saslPlainRequest.setCredentials( bindPassword );
        saslPlainRequest.setAuthorizationId( authzId );
        saslPlainRequest
            .setQualityOfProtection( connection.getConnectionParameter().getSaslQop() );
        saslPlainRequest.setSecurityStrength( connection.getConnectionParameter()
            .getSaslSecurityStrength() );
        saslPlainRequest.setMutualAuthentication( connection.getConnectionParameter()
            .isSaslMutualAuthentication() );
        
        return ldapConnection.bindSaslPlain( bindPrincipal, bindPassword, authzId );
    }

    private void doBind( final StudioProgressMonitor monitor ) throws Exception
    {
        if ( ldapConnection != null && isConnected )
        {
            InnerRunnable runnable = new InnerRunnable()
            {
                public void run()
                {
                    try
                    {
                        BindResponse bindResponse = null;

                        // No Authentication
                        if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.NONE )
                        {
                            BindRequest bindRequest = new BindRequestImpl();
                            bindResponse = ldapConnection.bind( bindRequest );
                        }
                        else
                        {
                            // Setup credentials
                            IAuthHandler authHandler = ConnectionCorePlugin.getDefault().getAuthHandler();
                            if ( authHandler == null )
                            {
                                Exception exception = new Exception( Messages.model__no_auth_handler );
                                monitor.setCanceled( true );
                                monitor.reportError( Messages.model__no_auth_handler, exception );
                                throw exception;
                            }
                            ICredentials credentials = authHandler.getCredentials( connection.getConnectionParameter() );
                            if ( credentials == null )
                            {
                                Exception exception = new Exception();
                                monitor.setCanceled( true );
                                monitor.reportError( Messages.model__no_credentials, exception );
                                throw exception;
                            }
                            if ( credentials.getBindPrincipal() == null || credentials.getBindPassword() == null )
                            {
                                Exception exception = new Exception( Messages.model__no_credentials );
                                monitor.reportError( Messages.model__no_credentials, exception );
                                throw exception;
                            }
                            bindPrincipal = credentials.getBindPrincipal();
                            bindPassword = credentials.getBindPassword();

                            switch ( connection.getConnectionParameter().getAuthMethod() )
                            {
                                case SIMPLE :
                                    // Simple Authentication
                                    bindResponse = bindSimple( bindPrincipal, bindPassword );
                                    
                                    break;
                                    
                                case SASL_PLAIN :
                                    // SASL Plain authentication
                                    bindResponse = bindSaslPlain();

                                    break;
                                    
                                case SASL_CRAM_MD5 :
                                    // CRAM-MD5 Authentication
                                    SaslCramMd5Request cramMd5Request = new SaslCramMd5Request();
                                    cramMd5Request.setUsername( bindPrincipal );
                                    cramMd5Request.setCredentials( bindPassword );
                                    cramMd5Request
                                        .setQualityOfProtection( connection.getConnectionParameter().getSaslQop() );
                                    cramMd5Request.setSecurityStrength( connection.getConnectionParameter()
                                        .getSaslSecurityStrength() );
                                    cramMd5Request.setMutualAuthentication( connection.getConnectionParameter()
                                        .isSaslMutualAuthentication() );

                                    bindResponse = ldapConnection.bind( cramMd5Request );
                                    break;
                                    
                                case SASL_DIGEST_MD5 :
                                    // DIGEST-MD5 Authentication
                                    SaslDigestMd5Request digestMd5Request = new SaslDigestMd5Request();
                                    digestMd5Request.setUsername( bindPrincipal );
                                    digestMd5Request.setCredentials( bindPassword );
                                    digestMd5Request.setRealmName( connection.getConnectionParameter().getSaslRealm() );
                                    digestMd5Request.setQualityOfProtection( connection.getConnectionParameter()
                                        .getSaslQop() );
                                    digestMd5Request.setSecurityStrength( connection.getConnectionParameter()
                                        .getSaslSecurityStrength() );
                                    digestMd5Request.setMutualAuthentication( connection.getConnectionParameter()
                                        .isSaslMutualAuthentication() );

                                    bindResponse = ldapConnection.bind( digestMd5Request );
                                    break;
                                    
                                case SASL_GSSAPI :
                                    // GSSAPI Authentication
                                    SaslGssApiRequest gssApiRequest = new SaslGssApiRequest();

                                    Preferences preferences = ConnectionCorePlugin.getDefault().getPluginPreferences();
                                    boolean useKrb5SystemProperties = preferences
                                        .getBoolean( ConnectionCoreConstants.PREFERENCE_USE_KRB5_SYSTEM_PROPERTIES );
                                    String krb5LoginModule = preferences
                                        .getString( ConnectionCoreConstants.PREFERENCE_KRB5_LOGIN_MODULE );

                                    if ( !useKrb5SystemProperties )
                                    {
                                        gssApiRequest.setUsername( bindPrincipal );
                                        gssApiRequest.setCredentials( bindPassword );
                                        gssApiRequest.setQualityOfProtection( connection
                                            .getConnectionParameter().getSaslQop() );
                                        gssApiRequest.setSecurityStrength( connection
                                            .getConnectionParameter()
                                            .getSaslSecurityStrength() );
                                        gssApiRequest.setMutualAuthentication( connection
                                            .getConnectionParameter()
                                            .isSaslMutualAuthentication() );
                                        gssApiRequest
                                            .setLoginModuleConfiguration( new InnerConfiguration(
                                                krb5LoginModule ) );

                                        switch ( connection.getConnectionParameter().getKrb5Configuration() )
                                        {
                                            case FILE:
                                                gssApiRequest.setKrb5ConfFilePath( connection.getConnectionParameter()
                                                    .getKrb5ConfigurationFile() );
                                                break;
                                            case MANUAL:
                                                gssApiRequest.setRealmName( connection.getConnectionParameter()
                                                    .getKrb5Realm() );
                                                gssApiRequest.setKdcHost( connection.getConnectionParameter()
                                                    .getKrb5KdcHost() );
                                                gssApiRequest.setKdcPort( connection.getConnectionParameter()
                                                    .getKrb5KdcPort() );
                                                break;
                                            default:
                                                break;
                                        }
                                    }

                                    bindResponse = ldapConnection.bind( gssApiRequest );
                                    break;
                            }
                        }

                        checkResponse( bindResponse );
                    }
                    catch ( Exception e )
                    {
                        exception = e;
                    }
                }
            };

            runAndMonitor( runnable, monitor );

            if ( runnable.getException() != null )
            {
                throw runnable.getException();
            }
        }
        else
        {
            throw new Exception( Messages.DirectoryApiConnectionWrapper_NoConnection );
        }
    }


    /***
     * {@inheritDoc}
     */
    public void unbind()
    {
        disconnect();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isConnected()
    {
        return ( ldapConnection != null && ldapConnection.isConnected() );
    }


    /**
     * {@inheritDoc}
     */
    public void setBinaryAttributes( Collection<String> binaryAttributes )
    {
        if ( binaryAttributeDetector != null )
        {
            // Clear the initial list
            binaryAttributeDetector.setBinaryAttributes();

            // Add each binary attribute
            for ( String binaryAttribute : binaryAttributes )
            {
                binaryAttributeDetector.addBinaryAttribute( binaryAttribute );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public StudioNamingEnumeration search( final String searchBase, final String filter,
        final SearchControls searchControls, final AliasDereferencingMethod aliasesDereferencingMethod,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        final long requestNum = searchRequestNum++;

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Preparing the search request
                    SearchRequest request = new SearchRequestImpl();
                    request.setBase( new Dn( searchBase ) );
                    ExprNode node = FilterParser.parse( filter, true );
                    request.setFilter( node );
                    request.setScope( convertSearchScope( searchControls ) );
                    if ( searchControls.getReturningAttributes() != null )
                    {
                        request.addAttributes( searchControls.getReturningAttributes() );
                    }
                    request.addAllControls( convertControls( controls ) );
                    request.setSizeLimit( searchControls.getCountLimit() );
                    request.setTimeLimit( searchControls.getTimeLimit() );
                    request.setDerefAliases( convertAliasDerefMode( aliasesDereferencingMethod ) );

                    // Performing the search operation
                    SearchCursor cursor = ldapConnection.search( request );

                    // Returning the result of the search
                    namingEnumeration = new CursorStudioNamingEnumeration( connection, cursor, searchBase, filter,
                        searchControls, aliasesDereferencingMethod, referralsHandlingMethod, controls, requestNum,
                        monitor, referralsInfo );
                }
                catch ( Exception e )
                {
                    exception = e;
                }

                NamingException ne = null;
                if ( exception != null )
                {
                    ne = new NamingException( exception.getMessage() );
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    if ( namingEnumeration != null )
                    {
                        logger.logSearchRequest( connection, searchBase, filter, searchControls,
                            aliasesDereferencingMethod, controls, requestNum, ne );
                    }
                    else
                    {
                        logger.logSearchRequest( connection, searchBase, filter, searchControls,
                            aliasesDereferencingMethod, controls, requestNum, ne );
                        logger.logSearchResultDone( connection, 0, requestNum, ne );
                    }
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
            return null;
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
            return null;
        }
        else
        {
            return runnable.getResult();
        }
    }


    /**
     * Converts the search scope.
     *
     * @param searchControls
     *      the search controls
     * @return
     *      the associated search scope
     */
    private SearchScope convertSearchScope( SearchControls searchControls )
    {
        int scope = searchControls.getSearchScope();
        if ( scope == SearchControls.OBJECT_SCOPE )
        {
            return SearchScope.OBJECT;
        }
        else if ( scope == SearchControls.ONELEVEL_SCOPE )
        {
            return SearchScope.ONELEVEL;
        }
        else if ( scope == SearchControls.SUBTREE_SCOPE )
        {
            return SearchScope.SUBTREE;
        }
        else
        {
            return SearchScope.SUBTREE;
        }
    }


    /**
     * Converts the controls.
     *
     * @param controls
     *      an array of controls
     * @return
     *      an array of converted controls
     */
    private org.apache.directory.api.ldap.model.message.Control[] convertControls( Control[] controls )
        throws Exception
    {
        if ( controls != null )
        {
            org.apache.directory.api.ldap.model.message.Control[] returningControls =
                new org.apache.directory.api.ldap.model.message.Control[controls.length];

            for ( int i = 0; i < controls.length; i++ )
            {
                returningControls[i] = ldapConnection.getCodecService().fromJndiControl( controls[i] );
            }

            return returningControls;
        }
        else
        {
            return new org.apache.directory.api.ldap.model.message.Control[0];
        }
    }


    /**
     * Converts the Alias Dereferencing method.
     *
     * @param aliasesDereferencingMethod
     *      the Alias Dereferencing method.
     * @return
     *      the converted Alias Dereferencing method.
     */
    private AliasDerefMode convertAliasDerefMode( AliasDereferencingMethod aliasesDereferencingMethod )
    {
        switch ( aliasesDereferencingMethod )
        {
            case ALWAYS:
                return AliasDerefMode.DEREF_ALWAYS;
            case FINDING:
                return AliasDerefMode.DEREF_FINDING_BASE_OBJ;
            case NEVER:
                return AliasDerefMode.NEVER_DEREF_ALIASES;
            case SEARCH:
                return AliasDerefMode.DEREF_IN_SEARCHING;
            default:
                return AliasDerefMode.DEREF_ALWAYS;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void modifyEntry( final String dn, final ModificationItem[] modificationItems, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor
                .reportError( new Exception( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Preparing the modify request
                    ModifyRequest request = new ModifyRequestImpl();
                    request.setName( new Dn( dn ) );
                    Modification[] modifications = convertModificationItems( modificationItems );
                    if ( modifications != null )
                    {
                        for ( Modification modification : modifications )
                        {
                            request.addModification( modification );
                        }
                    }
                    request.addAllControls( convertControls( controls ) );

                    // Performing the modify operation
                    ModifyResponse modifyResponse = ldapConnection.modify( request );

                    // Handle referral
                    Consumer<ReferralHandlingData> consumer = referralHandlingData -> 
                        referralHandlingData.connectionWrapper.modifyEntry( referralHandlingData.referralDn,
                            modificationItems, controls, monitor, referralHandlingData.newReferralsInfo );
                    
                    if ( checkAndHandleReferral( modifyResponse, monitor, referralsInfo, consumer ) )
                    {
                        return;
                    }

                    // Checking the response
                    checkResponse( modifyResponse );
                }
                catch ( Exception e )
                {
                    exception = e;
                }

                NamingException ne = null;
                if ( exception != null )
                {
                    ne = new NamingException( exception.getMessage() );
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeModify( connection, dn, modificationItems, controls, ne );
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    /**
     * Converts modification items.
     *
     * @param modificationItems
     *      an array of modification items
     * @return
     *      an array of converted modifications
     * @throws LdapInvalidAttributeValueException 
     */
    private Modification[] convertModificationItems( ModificationItem[] modificationItems )
        throws LdapInvalidAttributeValueException
    {
        if ( modificationItems != null )
        {
            List<Modification> modifications = new ArrayList<>();

            for ( ModificationItem modificationItem : modificationItems )
            {
                Modification modification = new DefaultModification();

                modification.setAttribute( AttributeUtils.toApiAttribute( modificationItem.getAttribute() ) );
                modification.setOperation( convertModificationOperation( modificationItem.getModificationOp() ) );
                modifications.add( modification );
            }

            return modifications.toArray( new Modification[0] );
        }
        else
        {
            return null;
        }
    }


    /**
     * Converts a modification operation.
     *
     * @param modificationOp
     *      a modification operation
     * @return
     *      the converted modification operation
     */
    private ModificationOperation convertModificationOperation( int modificationOp )
    {
        if ( modificationOp == DirContext.ADD_ATTRIBUTE )
        {
            return ModificationOperation.ADD_ATTRIBUTE;
        }
        else if ( modificationOp == DirContext.REPLACE_ATTRIBUTE )
        {
            return ModificationOperation.REPLACE_ATTRIBUTE;
        }
        else if ( modificationOp == DirContext.REMOVE_ATTRIBUTE )
        {
            return ModificationOperation.REMOVE_ATTRIBUTE;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void renameEntry( final String oldDn, final String newDn, final boolean deleteOldRdn,
        final Control[] controls, final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor
                .reportError( new Exception( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Preparing the rename request
                    ModifyDnRequest request = new ModifyDnRequestImpl();
                    request.setName( new Dn( oldDn ) );
                    request.setDeleteOldRdn( deleteOldRdn );
                    Dn newName = new Dn( newDn );
                    request.setNewRdn( newName.getRdn() );
                    request.setNewSuperior( newName.getParent() );
                    request.addAllControls( convertControls( controls ) );

                    // Performing the rename operation
                    ModifyDnResponse modifyDnResponse = ldapConnection.modifyDn( request );

                    // Handle referral
                    Consumer<ReferralHandlingData> consumer = referralHandlingData ->
                        referralHandlingData.connectionWrapper.renameEntry( oldDn, newDn, deleteOldRdn, controls,
                            monitor, referralHandlingData.newReferralsInfo );
                    
                    if ( checkAndHandleReferral( modifyDnResponse, monitor, referralsInfo, consumer ) )
                    {
                        return;
                    }

                    // Checking the response
                    checkResponse( modifyDnResponse );
                }
                catch ( Exception e )
                {
                    exception = e;
                }

                NamingException ne = null;
                if ( exception != null )
                {
                    ne = new NamingException( exception.getMessage() );
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeModDn( connection, oldDn, newDn, deleteOldRdn, controls, ne );
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createEntry( final String dn, final Attributes attributes, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor
                .reportError( new Exception( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Preparing the add request
                    AddRequest request = new AddRequestImpl();
                    request.setEntryDn( new Dn( dn ) );
                    request.setEntry( AttributeUtils.toEntry( attributes, new Dn( dn ) ) );
                    request.addAllControls( convertControls( controls ) );

                    // Performing the add operation
                    AddResponse addResponse = ldapConnection.add( request );

                    // Handle referral
                    Consumer<ReferralHandlingData> consumer = referralHandlingData ->
                        referralHandlingData.connectionWrapper.createEntry( referralHandlingData.referralDn, attributes,
                            controls, monitor, referralHandlingData.newReferralsInfo );
                        
                    if ( checkAndHandleReferral( addResponse, monitor, referralsInfo, consumer ) )
                    {
                        return;
                    }

                    // Checking the response
                    checkResponse( addResponse );
                }
                catch ( Exception e )
                {
                    exception = e;
                }

                NamingException ne = null;
                if ( exception != null )
                {
                    ne = new NamingException( exception.getMessage() );
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeAdd( connection, dn, attributes, controls, ne );
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void deleteEntry( final String dn, final Control[] controls, final StudioProgressMonitor monitor,
        final ReferralsInfo referralsInfo )
    {
        if ( connection.isReadOnly() )
        {
            monitor
                .reportError( new Exception( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) ) );
            return;
        }

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Preparing the delete request
                    DeleteRequest request = new DeleteRequestImpl();
                    request.setName( new Dn( dn ) );
                    request.addAllControls( convertControls( controls ) );

                    // Performing the delete operation
                    DeleteResponse deleteResponse = ldapConnection.delete( request );

                    // Handle referral
                    Consumer<ReferralHandlingData> consumer = referralHandlingData -> 
                        referralHandlingData.connectionWrapper.deleteEntry( referralHandlingData.referralDn, controls,
                            monitor, referralHandlingData.newReferralsInfo );
                    
                    if ( checkAndHandleReferral( deleteResponse, monitor, referralsInfo, consumer ) )
                    {
                        return;
                    }

                    // Checking the response
                    checkResponse( deleteResponse );
                }
                catch ( Exception e )
                {
                    exception = e;
                }

                NamingException ne = null;
                if ( exception != null )
                {
                    ne = new NamingException( exception.getMessage() );
                }

                for ( IJndiLogger logger : getJndiLoggers() )
                {
                    logger.logChangetypeDelete( connection, dn, controls, ne );
                }
            }
        };

        try
        {
            checkConnectionAndRunAndMonitor( runnable, monitor );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }

        if ( runnable.isCanceled() )
        {
            monitor.setCanceled( true );
        }
        if ( runnable.getException() != null )
        {
            monitor.reportError( runnable.getException() );
        }
    }

    /**
     * Inner runnable used in connection wrapper operations.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    abstract class InnerRunnable implements Runnable
    {
        protected StudioNamingEnumeration namingEnumeration = null;
        protected Exception exception = null;
        protected boolean canceled = false;


        /**
         * Gets the exception.
         * 
         * @return the exception
         */
        public Exception getException()
        {
            return exception;
        }


        /**
         * Gets the result.
         * 
         * @return the result
         */
        public StudioNamingEnumeration getResult()
        {
            return namingEnumeration;
        }


        /**
         * Checks if is canceled.
         * 
         * @return true, if is canceled
         */
        public boolean isCanceled()
        {
            return canceled;
        }


        /**
         * Reset.
         */
        public void reset()
        {
            namingEnumeration = null;
            exception = null;
            canceled = false;
        }
    }


    private boolean checkAndHandleReferral( ResultResponse response, StudioProgressMonitor monitor,
        ReferralsInfo referralsInfo, Consumer<ReferralHandlingData> consumer ) throws NamingException, LdapURLEncodingException
    {
        if ( response == null )
        {
            return false;
        }

        LdapResult ldapResult = response.getLdapResult();
        if ( ldapResult == null || !ResultCodeEnum.REFERRAL.equals( ldapResult.getResultCode() ) )
        {
            return false;
        }

        if ( referralsInfo == null )
        {
            referralsInfo = new ReferralsInfo( true );
        }

        Referral referral = ldapResult.getReferral();
        referralsInfo.addReferral( referral );
        Referral nextReferral = referralsInfo.getNextReferral();

        Connection referralConnection = ConnectionWrapperUtils.getReferralConnection( nextReferral, monitor, this );
        if ( referralConnection == null )
        {
            monitor.setCanceled( true );
            return true;
        }

        List<String> urls = new ArrayList<>( referral.getLdapUrls() );
        String referralDn = new LdapUrl( urls.get( 0 ) ).getDn().getName();
        ReferralHandlingData referralHandlingData = new ReferralHandlingData( referralConnection.getConnectionWrapper(),
            referralDn, referralsInfo );
        consumer.accept( referralHandlingData );

        return true;
    }


    static class ReferralHandlingData
    {
        ConnectionWrapper connectionWrapper;
        String referralDn;
        ReferralsInfo newReferralsInfo;


        ReferralHandlingData( ConnectionWrapper connectionWrapper, String referralDn, ReferralsInfo newReferralsInfo )
        {
            this.connectionWrapper = connectionWrapper;
            this.referralDn = referralDn;
            this.newReferralsInfo = newReferralsInfo;
        }
    }


    private void checkConnectionAndRunAndMonitor( final InnerRunnable runnable, final StudioProgressMonitor monitor )
        throws Exception
    {
        // check connection
        if ( !isConnected || ldapConnection == null )
        {
            doConnect( monitor );
            doBind( monitor );
        }
        if ( ldapConnection == null )
        {
            throw new NamingException( Messages.DirectoryApiConnectionWrapper_NoConnection );
        }

        // loop for reconnection
        for ( int i = 0; i <= 1; i++ )
        {
            runAndMonitor( runnable, monitor );

            // check reconnection
            if ( ( i == 0 ) && ( runnable.getException() instanceof InvalidConnectionException ) )
            {
                doConnect( monitor );
                doBind( monitor );
                runnable.reset();
            }
            else
            {
                break;
            }
        }
    }


    private void runAndMonitor( final InnerRunnable runnable, final StudioProgressMonitor monitor )
        throws CancelException
    {
        if ( !monitor.isCanceled() )
        {
            // monitor
            StudioProgressMonitor.CancelListener listener = event ->
                {
                    if ( monitor.isCanceled() )
                    {
                        if ( jobThread != null && jobThread.isAlive() )
                        {
                            jobThread.interrupt();
                        }
                        
                        if ( ldapConnection != null )
                        {
                            try
                            {
                                ldapConnection.close();
                            }
                            catch ( Exception e )
                            {
                            }
                            
                            isConnected = false;
                            ldapConnection = null;
                        }
                        
                        isConnected = false;
                    }
                };

            monitor.addCancelListener( listener );
            jobThread = Thread.currentThread();

            // run
            try
            {
                runnable.run();
            }
            finally
            {
                monitor.removeCancelListener( listener );
                jobThread = null;
            }

            if ( monitor.isCanceled() )
            {
                throw new CancelException();
            }
        }
    }

    private final class InnerConfiguration extends Configuration
    {
        private String krb5LoginModule;
        private AppConfigurationEntry[] configList = null;


        public InnerConfiguration( String krb5LoginModule )
        {
            this.krb5LoginModule = krb5LoginModule;
        }


        public AppConfigurationEntry[] getAppConfigurationEntry( String applicationName )
        {
            if ( configList == null )
            {
                HashMap<String, Object> options = new HashMap<>();

                // TODO: this only works for Sun JVM
                options.put( "refreshKrb5Config", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                switch ( connection.getConnectionParameter().getKrb5CredentialConfiguration() )
                {
                    case USE_NATIVE:
                        options.put( "useTicketCache", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                        options.put( "doNotPrompt", "true" ); //$NON-NLS-1$ //$NON-NLS-2$
                        break;
                    case OBTAIN_TGT:
                        options.put( "doNotPrompt", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
                        break;
                }

                configList = new AppConfigurationEntry[1];
                configList[0] = new AppConfigurationEntry( krb5LoginModule, LoginModuleControlFlag.REQUIRED, options );
            }
            return configList;
        }


        @Override
        public void refresh()
        {
        }
    }


    private List<IJndiLogger> getJndiLoggers()
    {
        return ConnectionCorePlugin.getDefault().getJndiLoggers();
    }


    /**
     * Checks the given response.
     *
     * @param response
     *      the response
     * @throws Exception
     *      if the LDAP result associated with the response is not a success
     */
    private void checkResponse( ResultResponse response ) throws Exception
    {
        if ( response != null )
        {
            LdapResult ldapResult = response.getLdapResult();
            if ( ldapResult != null )
            {
                // NOT_ALLOWED_ON_NON_LEAF error (thrown when deleting an entry with children)
                if ( ResultCodeEnum.NOT_ALLOWED_ON_NON_LEAF.equals( ldapResult.getResultCode() ) )
                {
                    throw new ContextNotEmptyException( ldapResult.getDiagnosticMessage() );
                }
                // ENTRY_ALREADY_EXISTS error
                // (We need this conversion in the case where this error is thrown during an LDIF
                // import with the "Update existing entries" flag turned on)
                else if ( ResultCodeEnum.ENTRY_ALREADY_EXISTS.equals( ldapResult.getResultCode() ) )
                {
                    throw new NameAlreadyBoundException( ldapResult.getDiagnosticMessage() );
                }
                // Different from SUCCESS, we throw a generic exception
                else if ( !ResultCodeEnum.SUCCESS.equals( ldapResult.getResultCode() ) )
                {
                    int code = ldapResult.getResultCode().getResultCode();
                    String message = ldapResult.getDiagnosticMessage();

                    // Checking if we got a message from the LDAP result
                    if ( StringUtils.isEmpty( message ) )
                    {
                        // Assigning the generic result code description
                        message = Utils.getResultCodeDescription( code );
                    }

                    throw new Exception( NLS.bind( "[LDAP: error code {0} - {1}]", new String[] //$NON-NLS-1$
                        { Integer.toString( code ), message } ) ); //$NON-NLS-1$
                }
            }
        }
    }
}
