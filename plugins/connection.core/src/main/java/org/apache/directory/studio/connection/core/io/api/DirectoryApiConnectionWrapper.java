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
import java.util.List;

import javax.naming.CommunicationException;
import javax.naming.ContextNotEmptyException;
import javax.naming.InsufficientResourcesException;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.codec.controls.ControlImpl;
import org.apache.directory.shared.ldap.cursor.SearchCursor;
import org.apache.directory.shared.ldap.entry.DefaultModification;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AddRequest;
import org.apache.directory.shared.ldap.message.AddRequestImpl;
import org.apache.directory.shared.ldap.message.AddResponse;
import org.apache.directory.shared.ldap.message.AliasDerefMode;
import org.apache.directory.shared.ldap.message.BindResponse;
import org.apache.directory.shared.ldap.message.DeleteRequest;
import org.apache.directory.shared.ldap.message.DeleteRequestImpl;
import org.apache.directory.shared.ldap.message.DeleteResponse;
import org.apache.directory.shared.ldap.message.LdapResult;
import org.apache.directory.shared.ldap.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.message.ModifyRequest;
import org.apache.directory.shared.ldap.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.message.ModifyResponse;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.message.ResultResponse;
import org.apache.directory.shared.ldap.message.SearchRequest;
import org.apache.directory.shared.ldap.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionParameter;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.IAuthHandler;
import org.apache.directory.studio.connection.core.ICredentials;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.connection.core.Messages;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.StudioTrustManager;
import org.apache.directory.studio.connection.core.io.jndi.CancelException;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;
import org.eclipse.osgi.util.NLS;


/**
 * A ConnectionWrapper is a wrapper for a real directory connection implementation.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DirectoryApiConnectionWrapper implements ConnectionWrapper
{
    /** The search request number */
    private static int SEARCH_RESQUEST_NUM = 0;

    /** The connection*/
    private Connection connection;

    /** The LDAP Connection */
    private LdapNetworkConnection ldapConnection;

    /** Indicates if the wrapper is connected */
    private boolean isConnected = false;

    /** The current job thread */
    private Thread jobThread;

    /** The bind principal */
    private String bindPrincipal;

    /** The bind password */
    private String bindPassword;


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
     * Gets the associated LDAP Connection.
     *
     * @return
     *      the associated LDAP Connection
     * @throws Exception 
     */
    private LdapNetworkConnection getLdapConnection()
    {
        if ( ldapConnection != null )
        {
            return ldapConnection;
        }

        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost( connection.getHost() );
        config.setLdapPort( connection.getPort() );
        config.setName( connection.getBindPrincipal() );
        config.setCredentials( connection.getBindPassword() );
        if ( ( connection.getEncryptionMethod() == EncryptionMethod.LDAPS )
            || ( connection.getEncryptionMethod() == EncryptionMethod.START_TLS ) )
        {
            config.setUseSsl( true );

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

                config.setTrustManagers( trustManagers );
            }
            catch ( Exception e )
            {
                // TODO: handle exception
            }
        }

        ldapConnection = new LdapNetworkConnection( config );

        return ldapConnection;
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

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Connecting
                    boolean connected = getLdapConnection().connect();
                    if ( !connected )
                    {
                        throw new Exception( "Unable to connect" );
                    }

                    // Start TLS
                    if ( connection.getConnectionParameter().getEncryptionMethod() == ConnectionParameter.EncryptionMethod.START_TLS )
                    {
                        getLdapConnection().startTls();
                    }
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


    /**
     * {@inheritDoc}
     */
    public void disconnect()
    {
        try
        {
            getLdapConnection().close();
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

                        // Simple Authentication
                        if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SIMPLE )
                        {
                            bindResponse = getLdapConnection().bind( bindPrincipal, bindPassword );
                        }
                        // CRAM-MD5 Authentication
                        else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_CRAM_MD5 )
                        {
                            bindResponse = getLdapConnection().bindCramMd5( bindPrincipal, bindPassword, null );
                        }
                        // DIGEST-MD5 Authentication
                        else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_DIGEST_MD5 )
                        {
                            bindResponse = getLdapConnection().bindDigestMd5( bindPrincipal, bindPassword, null,
                                connection.getConnectionParameter().getSaslRealm() );
                        }
                        // GSSAPI Authentication
                        else if ( connection.getConnectionParameter().getAuthMethod() == ConnectionParameter.AuthenticationMethod.SASL_GSSAPI )
                        {
                            bindResponse = getLdapConnection().bindGssApi( bindPrincipal, bindPassword,
                                connection.getConnectionParameter().getSaslRealm(),
                                connection.getConnectionParameter().getKrb5KdcHost(),
                                connection.getConnectionParameter().getKrb5KdcPort() );
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
            throw new Exception( "No Connection" );
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
        return getLdapConnection().isConnected();
    }


    /**
     * {@inheritDoc}
     */
    public void setBinaryAttributes( Collection<String> binaryAttributes )
    {
    }


    /**
     * {@inheritDoc}
     */
    public StudioNamingEnumeration search( final String searchBase, final String filter,
        final SearchControls searchControls, final AliasDereferencingMethod aliasesDereferencingMethod,
        final ReferralHandlingMethod referralsHandlingMethod, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        final long requestNum = SEARCH_RESQUEST_NUM++;

        InnerRunnable runnable = new InnerRunnable()
        {
            public void run()
            {
                try
                {
                    // Preparing the search request
                    SearchRequest request = new SearchRequestImpl();
                    request.setBase( new DN( searchBase ) );
                    request.setFilter( filter );
                    request.setScope( convertSearchScope( searchControls ) );
                    request.addAttributes( searchControls.getReturningAttributes() );
                    request.addAllControls( convertControls( controls ) );
                    request.setSizeLimit( searchControls.getCountLimit() );
                    request.setTimeLimit( searchControls.getTimeLimit() );
                    request.setDerefAliases( convertAliasDerefMode( aliasesDereferencingMethod ) );

                    // Performing the search operation
                    SearchCursor cursor = getLdapConnection().search( request );

                    // Returning the result of the search
                    namingEnumeration = new CursorStudioNamingEnumeration( connection, cursor, searchBase, filter,
                        searchControls,
                        aliasesDereferencingMethod, referralsHandlingMethod, controls, requestNum, monitor,
                        referralsInfo );
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
    private org.apache.directory.shared.ldap.message.control.Control[] convertControls( Control[] controls )
    {
        if ( controls != null )
        {
            org.apache.directory.shared.ldap.message.control.Control[] returningControls = new org.apache.directory.shared.ldap.message.control.Control[controls.length];

            for ( int i = 0; i < controls.length; i++ )
            {
                Control control = controls[i];
                org.apache.directory.shared.ldap.message.control.Control returningControl = new ControlImpl(
                    control.getID() );
                returningControl.setValue( control.getEncodedValue() );
                returningControl.setCritical( control.isCritical() );

                returningControls[i] = returningControl;
            }

            return returningControls;
        }
        else
        {
            return new org.apache.directory.shared.ldap.message.control.Control[0];
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
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
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
                    request.setName( new DN( dn ) );
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
                    ModifyResponse modifyResponse = getLdapConnection().modify( request );

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
     */
    private Modification[] convertModificationItems( ModificationItem[] modificationItems )
    {
        if ( modificationItems != null )
        {
            List<Modification> modifications = new ArrayList<Modification>();

            for ( ModificationItem modificationItem : modificationItems )
            {
                Modification modification = new DefaultModification();
                modification.setAttribute( AttributeUtils.toClientAttribute( modificationItem.getAttribute() ) );
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
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
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
                    request.setName( new DN( oldDn ) );
                    request.setDeleteOldRdn( deleteOldRdn );
                    DN newName = new DN( newDn );
                    request.setNewRdn( newName.getRdn() );
                    request.setNewSuperior( newName.getParent() );
                    request.addAllControls( convertControls( controls ) );

                    // Performing the rename operation
                    ModifyDnResponse modifyDnResponse = getLdapConnection().modifyDn( request );

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
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
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
                    request.setEntryDn( new DN( dn ) );
                    request.setEntry( AttributeUtils.toClientEntry( attributes, new DN( dn ) ) );
                    request.addAllControls( convertControls( controls ) );

                    // Performing the add operation
                    AddResponse addResponse = getLdapConnection().add( request );

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
            monitor.reportError( NLS.bind( Messages.error__connection_is_readonly, connection.getName() ) );
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
                    request.setName( new DN( dn ) );
                    request.addAllControls( convertControls( controls ) );

                    // Performing the delete operation
                    DeleteResponse deleteResponse = getLdapConnection().delete( request );

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
            throw new NamingException( "No Connection" );
        }

        // loop for reconnection
        for ( int i = 0; i <= 1; i++ )
        {
            runAndMonitor( runnable, monitor );

            // check reconnection
            if ( i == 0
                && runnable.getException() != null
                && ( ( runnable.getException() instanceof CommunicationException )
                    || ( runnable.getException() instanceof ServiceUnavailableException ) || ( runnable.getException() instanceof InsufficientResourcesException ) ) )
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
            StudioProgressMonitor.CancelListener listener = new StudioProgressMonitor.CancelListener()
            {
                public void cancelRequested( StudioProgressMonitor.CancelEvent event )
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
                            System.gc();
                        }
                        isConnected = false;
                    }
                }
            };
            monitor.addCancelListener( listener );
            jobThread = Thread.currentThread();

            // run
            try
            {
                // try {
                // Thread.sleep(5000);
                // } catch (InterruptedException e) {
                // System.out.println(System.currentTimeMillis() + ": sleep
                // interrupted!");
                // }
                // System.out.println(System.currentTimeMillis() + ": " +
                // runnable);

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
                // NOT_ALLOWED_ON_NON_LEAF error (thrown when deleting a entry with children
                if ( ResultCodeEnum.NOT_ALLOWED_ON_NON_LEAF.equals( ldapResult.getResultCode() ) )
                {
                    throw new ContextNotEmptyException( ldapResult.getErrorMessage() );
                }
                // Different from SUCCESS, we throw a generic exception
                else if ( !ResultCodeEnum.SUCCESS.equals( ldapResult.getResultCode() ) )
                {
                    throw new Exception( NLS.bind( "[LDAP: error code {0} - {1}]", new String[]
                        { ldapResult.getResultCode().getResultCode() + "", ldapResult.getErrorMessage() } ) );
                }
            }
        }
    }
}
