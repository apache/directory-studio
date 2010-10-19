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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;

import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.directory.shared.ldap.codec.controls.ControlImpl;
import org.apache.directory.shared.ldap.cursor.Cursor;
import org.apache.directory.shared.ldap.entry.DefaultModification;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.filter.SearchScope;
import org.apache.directory.shared.ldap.message.AddRequest;
import org.apache.directory.shared.ldap.message.AddRequestImpl;
import org.apache.directory.shared.ldap.message.AliasDerefMode;
import org.apache.directory.shared.ldap.message.DeleteRequest;
import org.apache.directory.shared.ldap.message.DeleteRequestImpl;
import org.apache.directory.shared.ldap.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.message.ModifyRequest;
import org.apache.directory.shared.ldap.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.message.Response;
import org.apache.directory.shared.ldap.message.SearchRequest;
import org.apache.directory.shared.ldap.message.SearchRequestImpl;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.connection.core.io.ConnectionWrapper;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.io.jndi.ReferralsInfo;


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
        config.setUseSsl( connection.getEncryptionMethod() == EncryptionMethod.LDAPS );

        ldapConnection = new LdapNetworkConnection( config );

        return ldapConnection;
    }


    /**
     * {@inheritDoc}
     */
    public void connect( StudioProgressMonitor monitor )
    {
        try
        {
            getLdapConnection().connect();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
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
            getLdapConnection().bind( getLdapConnection().getConfig().getName(),
                getLdapConnection().getConfig().getCredentials() );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /***
     * {@inheritDoc}
     */
    public void unbind()
    {
        try
        {
            getLdapConnection().unBind();
        }
        catch ( LdapException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            Cursor<Response> cursor = getLdapConnection().search( request );

            // Returning the result of the search
            return new CursorStudioNamingEnumeration( connection, cursor, searchBase, filter, searchControls,
                aliasesDereferencingMethod, referralsHandlingMethod, controls, requestNum, monitor, referralsInfo );

        }
        catch ( Exception e )
        {
            monitor.reportError( e );
            return null;
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
            getLdapConnection().modify( request );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
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
            getLdapConnection().modifyDn( request );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void createEntry( final String dn, final Attributes attributes, final Control[] controls,
        final StudioProgressMonitor monitor, final ReferralsInfo referralsInfo )
    {
        try
        {
            // Preparing the add request
            AddRequest request = new AddRequestImpl();
            request.setEntryDn( new DN( dn ) );
            request.setEntry( AttributeUtils.toClientEntry( attributes, new DN( dn ) ) );
            request.addAllControls( convertControls( controls ) );

            // Performing the add operation
            getLdapConnection().add( request );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void deleteEntry( final String dn, final Control[] controls, final StudioProgressMonitor monitor,
        final ReferralsInfo referralsInfo )
    {
        try
        {
            // Preparing the delete request
            DeleteRequest request = new DeleteRequestImpl();
            request.setName( new DN( dn ) );
            request.addAllControls( convertControls( controls ) );

            // Performing the delete operation
            getLdapConnection().delete( request );
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }
}
