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

package org.apache.directory.studio.ldapbrowser.core.jobs;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.Control;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.dsmlv2.Dsmlv2Parser;
import org.apache.directory.shared.dsmlv2.reponse.AddResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.AuthResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.CompareResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.DelResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ExtendedResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ModDNResponseDsml;
import org.apache.directory.shared.dsmlv2.reponse.ModifyResponseDsml;
import org.apache.directory.shared.dsmlv2.request.BatchRequest;
import org.apache.directory.shared.ldap.model.message.*;
import org.apache.directory.shared.ldap.entry.AttributeUtils;
import org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.AddRequest;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.DeleteRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.SearchRequest;
import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.io.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;

import com.sun.jndi.ldap.BasicControl;


/**
 * Runnable to import a DSML File into a LDAP server
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportDsmlRunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The connection to use */
    private IBrowserConnection browserConnection;

    /** The DSML file to use */
    private File dsmlFile;

    /** The Save file to use */
    private File responseFile;


    /**
     * Creates a new instance of ImportDsmlRunnable.
     *
     * @param connection
     *          The connection to use
     * @param dsmlFile
     *          The DSML file to read from
     * @param saveFile
     *          The Save file to use
     * @param continueOnError
     *          The ContinueOnError flag
     */
    public ImportDsmlRunnable( IBrowserConnection connection, File dsmlFile, File saveFile )
    {
        this.browserConnection = connection;
        this.dsmlFile = dsmlFile;
        this.responseFile = saveFile;
    }


    /**
     * Creates a new instance of ImportDsmlRunnable.
     *
     * @param connection
     *          The Connection to use
     * @param dsmlFile
     *          The DSML file to read from
     * @param continueOnError
     *          The ContinueOnError flag
     */
    public ImportDsmlRunnable( IBrowserConnection connection, File dsmlFile )
    {
        this( connection, dsmlFile, null );
    }


    /**
     * {@inheritDoc}
     */
    public Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return BrowserCoreMessages.jobs__import_dsml_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( dsmlFile.toString() ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_dsml_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__import_dsml_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            // Parsing the file
            Dsmlv2Parser parser = new Dsmlv2Parser();
            parser.setInput( new FileInputStream( dsmlFile ), "UTF-8" );
            parser.parseAllRequests();

            // Getting the batch request
            BatchRequest batchRequest = parser.getBatchRequest();

            // Creating a DSML batch response (only if needed)
            BatchResponseDsml batchResponseDsml = null;
            if ( responseFile != null )
            {
                batchResponseDsml = new BatchResponseDsml();
            }

            // Setting the errors counter
            int errorsCount = 0;

            // Creating a dummy monitor that will be used to check if something
            // went wrong when executing the request
            StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

            // Processing each request
            List<?> requests = batchRequest.getRequests();
            for ( Object request : requests )
            {
                // Processing the request
                processRequest( request, batchResponseDsml, dummyMonitor );

                // Verifying if any error has been reported
                if ( dummyMonitor.errorsReported() )
                {
                    errorsCount++;
                }

                dummyMonitor.reset();
            }

            // Writing the DSML response file to its final destination file.
            if ( responseFile != null )
            {
                FileOutputStream fos = new FileOutputStream( responseFile );
                OutputStreamWriter osw = new OutputStreamWriter( fos, "UTF-8" );
                BufferedWriter bufferedWriter = new BufferedWriter( osw );
                bufferedWriter.write( batchResponseDsml.toDsml() );
                bufferedWriter.close();
                osw.close();
                fos.close();
            }

            // Displaying an error message if we've had some errors
            if ( errorsCount > 0 )
            {
                monitor.reportError( BrowserCoreMessages.bind(
                    "{0} errors occurred, see the response file for details", new String[]
                        { "" + errorsCount } ) );
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void runNotification( StudioProgressMonitor monitor )
    {
        EventRegistry.fireEntryUpdated( new BulkModificationEvent( browserConnection ), this );
    }


    /**
     * Processes the request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     * @throws NamingException 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException
     * @throws LdapException
     */
    private void processRequest( Object request, BatchResponseDsml batchResponseDsml, StudioProgressMonitor monitor )
        throws NamingException, LdapURLEncodingException, LdapException
    {
        if ( request instanceof BindRequest)
        {
            processBindRequest( ( BindRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof AddRequest )
        {
            processAddRequest( (AddRequest) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof CompareRequest )
        {
            processCompareRequest( ( CompareRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof DeleteRequest )
        {
            processDelRequest( ( DeleteRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ExtendedRequest )
        {
            processExtendedRequest( ( ExtendedRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ModifyRequest )
        {
            processModifyRequest( ( ModifyRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ModifyDnRequest )
        {
            processModifyDNRequest( ( ModifyDnRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof SearchRequest )
        {
            processSearchRequest( ( SearchRequest ) request, batchResponseDsml, monitor );
        }
    }


    /**
     * Processes an bind request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processBindRequest( BindRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // We can not support extended requests at the moment,
        // we need a more advanced connection wrapper.

        // Creating the response
        if ( batchResponseDsml != null )
        {
            AuthResponseDsml authResponseDsml = new AuthResponseDsml();
            LdapResult ldapResult = authResponseDsml.getLdapResult();
            ldapResult.setResultCode( ResultCodeEnum.UNWILLING_TO_PERFORM );
            ldapResult.setErrorMessage( "This kind of request is not yet supported." );
            batchResponseDsml.addResponse( authResponseDsml );
        }
    }


    /**
     * Processes an add request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processAddRequest( AddRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Executing the add request
        Entry entry = request.getEntry();
        browserConnection
            .getConnection()
            .getConnectionWrapper()
            .createEntry( entry.getDn().getName(), AttributeUtils.toAttributes(entry), getControls( request ),
                monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            AddResponseDsml addResponseDsml = new AddResponseDsml();
            LdapResult ldapResult = addResponseDsml.getLdapResult();
            setLdapResultValuesFromMonitor( ldapResult, monitor, MessageTypeEnum.ADD_REQUEST );
            ldapResult.setMatchedDn( entry.getDn() );
            batchResponseDsml.addResponse( addResponseDsml );
        }

        // Update cached entries
        Dn dn = entry.getDn();
        IEntry e = browserConnection.getEntryFromCache( dn );
        Dn parentDn = DnUtils.getParent( dn );
        IEntry parentEntry = parentDn != null ? browserConnection.getEntryFromCache( parentDn ) : null;
        if ( e != null )
        {
            e.setAttributesInitialized( false );
        }
        if ( parentEntry != null )
        {
            parentEntry.setChildrenInitialized( false );
        }
    }


    /**
     * Processes a compare request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processCompareRequest( CompareRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // We can not support extended requests at the moment,
        // we need a more advanced connection wrapper.

        // Creating the response
        if ( batchResponseDsml != null )
        {
            CompareResponseDsml compareResponseDsml = new CompareResponseDsml();
            LdapResult ldapResult = compareResponseDsml.getLdapResult();
            ldapResult.setResultCode( ResultCodeEnum.UNWILLING_TO_PERFORM );
            ldapResult.setErrorMessage( "This kind of request is not yet supported." );
            batchResponseDsml.addResponse( compareResponseDsml );
        }
    }


    /**
     * Processes a del request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processDelRequest( DeleteRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Executing the del request
        browserConnection.getConnection().getConnectionWrapper()
            .deleteEntry( request.getName().getName(), getControls( request ), monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            DelResponseDsml delResponseDsml = new DelResponseDsml();
            LdapResult ldapResult = delResponseDsml.getLdapResult();
            setLdapResultValuesFromMonitor( ldapResult, monitor, MessageTypeEnum.ADD_REQUEST );
            delResponseDsml.getLdapResult().setMatchedDn( request.getName() );
            batchResponseDsml.addResponse( delResponseDsml );
        }

        // Update cached entries
        Dn dn = request.getName();
        IEntry e = browserConnection.getEntryFromCache( dn );
        Dn parentDn = DnUtils.getParent( dn );
        IEntry parentEntry = parentDn != null ? browserConnection.getEntryFromCache( parentDn ) : null;
        if ( e != null )
        {
            e.setAttributesInitialized( false );
            browserConnection.uncacheEntryRecursive( e );
        }
        if ( parentEntry != null )
        {
            parentEntry.setChildrenInitialized( false );
        }
    }


    /**
     * Processes an extended request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processExtendedRequest( ExtendedRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // We can not support extended requests at the moment,
        // we need a more advanced connection wrapper.

        // Creating the response
        if ( batchResponseDsml != null )
        {
            ExtendedResponseDsml extendedResponseDsml = new ExtendedResponseDsml();
            LdapResult ldapResult = extendedResponseDsml.getLdapResult();
            ldapResult.setResultCode( ResultCodeEnum.UNWILLING_TO_PERFORM );
            ldapResult.setErrorMessage( "This kind of request is not yet supported." );
            batchResponseDsml.addResponse( extendedResponseDsml );
        }
    }


    /**
     * Processes a modify request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processModifyRequest( ModifyRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Creating the modification items
        List<ModificationItem> modificationItems = new ArrayList<ModificationItem>();
        for ( Modification modification : request.getModifications() )
        {
            modificationItems.add( new ModificationItem( convertModificationOperation( modification.getOperation() ),
                AttributeUtils.toAttribute( modification.getAttribute() ) ) );
        }

        // Executing the modify request
        browserConnection
            .getConnection()
            .getConnectionWrapper()
            .modifyEntry( request.getName().getName(), modificationItems.toArray( new ModificationItem[0] ),
                getControls( request ), monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            ModifyResponseDsml modifyResponseDsml = new ModifyResponseDsml();
            LdapResult ldapResult = modifyResponseDsml.getLdapResult();
            setLdapResultValuesFromMonitor( ldapResult, monitor, MessageTypeEnum.ADD_REQUEST );
            modifyResponseDsml.getLdapResult().setMatchedDn( request.getName() );
            batchResponseDsml.addResponse( modifyResponseDsml );
        }

        Dn dn = request.getName();
        IEntry e = browserConnection.getEntryFromCache( dn );
        if ( e != null )
        {
            e.setAttributesInitialized( false );
        }
    }


    /**
     * Converts the modification operation from Shared LDAP to JNDI
     *
     * @param operation
     *      the Shared LDAP modification operation
     * @return
     *      the equivalent modification operation in JNDI
     */
    private int convertModificationOperation( ModificationOperation operation )
    {
        switch ( operation )
        {
            case ADD_ATTRIBUTE:
                return DirContext.ADD_ATTRIBUTE;
            case REMOVE_ATTRIBUTE:
                return DirContext.REMOVE_ATTRIBUTE;
            case REPLACE_ATTRIBUTE:
                return DirContext.REPLACE_ATTRIBUTE;
            default:
                return 0;
        }
    }


    /**
     * Processes a modify Dn request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processModifyDNRequest( ModifyDnRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Executing the modify Dn request
        browserConnection
            .getConnection()
            .getConnectionWrapper()
            .renameEntry( request.getName().getName(), request.getNewRdn().getName(), request.getDeleteOldRdn(),
                getControls( request ), monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            ModDNResponseDsml modDNResponseDsml = new ModDNResponseDsml();
            LdapResult ldapResult = modDNResponseDsml.getLdapResult();
            setLdapResultValuesFromMonitor( ldapResult, monitor, MessageTypeEnum.ADD_REQUEST );
            modDNResponseDsml.getLdapResult().setMatchedDn( request.getName() );
            batchResponseDsml.addResponse( modDNResponseDsml );
        }

        // Update cached entries
        Dn dn = request.getName();
        IEntry e = browserConnection.getEntryFromCache( dn );
        Dn parentDn = DnUtils.getParent( dn );
        IEntry parentEntry = parentDn != null ? browserConnection.getEntryFromCache( parentDn ) : null;
        if ( e != null )
        {
            e.setAttributesInitialized( false );
            browserConnection.uncacheEntryRecursive( e );
        }
        if ( parentEntry != null )
        {
            parentEntry.setChildrenInitialized( false );
        }
        if ( request.getNewSuperior() != null )
        {
            Dn newSuperiorDn = request.getNewSuperior();
            IEntry newSuperiorEntry = browserConnection.getEntryFromCache( newSuperiorDn );
            if ( newSuperiorEntry != null )
            {
                newSuperiorEntry.setChildrenInitialized( false );
            }
        }
    }


    /**
     * Processes a search request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     * @throws NamingException 
     * @throws org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException
     * @throws org.apache.directory.shared.ldap.model.exception.LdapException
     */
    private void processSearchRequest( SearchRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor ) throws NamingException, LdapURLEncodingException, LdapException
    {
        // Creating the response
        if ( batchResponseDsml != null )
        {
            // [Optimization] We're only searching if we need to produce a response
            StudioNamingEnumeration ne = browserConnection
                .getConnection()
                .getConnectionWrapper()
                .search( request.getBase().getName(), request.getFilter().toString(),
                    getSearchControls( request ), getAliasDereferencingMethod( request ),
                    ReferralHandlingMethod.IGNORE, getControls( request ), monitor, null );

            SearchParameter sp = new SearchParameter();
            sp.setReferralsHandlingMethod( browserConnection.getReferralsHandlingMethod() );
            ExportDsmlRunnable.processAsDsmlResponse( ne, batchResponseDsml, monitor, sp );
        }
    }


    /**
     * Returns the {@link SearchControls} object associated with the request.
     *
     * @param request
     *      the search request
     * @return
     *      the associated {@link SearchControls} object
     */
    private SearchControls getSearchControls( SearchRequest request )
    {
        SearchControls controls = new SearchControls();

        // Scope
        switch ( request.getScope() )
        {
            case OBJECT:
                controls.setSearchScope( SearchControls.OBJECT_SCOPE );
                break;
            case ONELEVEL:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
                break;
            case SUBTREE:
                controls.setSearchScope( SearchControls.SUBTREE_SCOPE );
                break;
            default:
                controls.setSearchScope( SearchControls.ONELEVEL_SCOPE );
        }

        // Returning attributes
        List<String> returningAttributes = new ArrayList<String>();
        for ( String attribute : request.getAttributes() )
        {
            returningAttributes.add( attribute );
        }
        // If the returning attributes are empty, we need to return the user attributes
        // [Cf. RFC 2251 - "There are two special values which may be used: an empty 
        //  list with no attributes, and the attribute description string '*'.  Both of 
        //  these signify that all user attributes are to be returned."]
        if ( returningAttributes.size() == 0 )
        {
            returningAttributes.add( "*" );
        }

        controls.setReturningAttributes( returningAttributes.toArray( new String[0] ) );

        // Size Limit
        controls.setCountLimit( request.getSizeLimit() );

        // Time Limit
        controls.setTimeLimit( request.getTimeLimit() );

        return controls;
    }


    /**
     * Returns the {@link AliasDereferencingMethod} object associated with the request.
     *
     * @param request
     *      the search request
     * @return
     *      the associated {@link AliasDereferencingMethod} object
     */
    private AliasDereferencingMethod getAliasDereferencingMethod( SearchRequest request )
    {
        switch ( request.getDerefAliases() )
        {
            case NEVER_DEREF_ALIASES:
                return AliasDereferencingMethod.NEVER;
            case DEREF_ALWAYS:
                return AliasDereferencingMethod.ALWAYS;
            case DEREF_FINDING_BASE_OBJ:
                return AliasDereferencingMethod.FINDING;
            case DEREF_IN_SEARCHING:
                return AliasDereferencingMethod.SEARCH;
            default:
                return AliasDereferencingMethod.NEVER;
        }
    }


    private Control[] getControls( Message request )
    {
        Collection<org.apache.directory.shared.ldap.model.message.Control> controls = request.getControls().values();
        if ( controls != null )
        {
            List<Control> jndiControls = new ArrayList<Control>();
            for ( org.apache.directory.shared.ldap.model.message.Control control : controls )
            {
                Control jndiControl = new BasicControl( control.getOid(), control.isCritical(),
                    control.getValue() );
                jndiControls.add( jndiControl );
            }
            return jndiControls.toArray( new Control[jndiControls.size()] );
        }
        return null;
    }


    /**
     * Get the LDAP Result corresponding to the given monitor
     *
     * @param monitor
     *      the progress monitor
     * @return
     *      the corresponding LDAP Result
     */
    private void setLdapResultValuesFromMonitor( LdapResult ldapResult, StudioProgressMonitor monitor,
        MessageTypeEnum messageType )
    {
        if ( !monitor.errorsReported() )
        {
            ldapResult.setResultCode( ResultCodeEnum.SUCCESS );
        }
        else
        {
            // Getting the exception
            Throwable t = monitor.getException();

            // Setting the result code
            ldapResult.setResultCode( ResultCodeEnum.getBestEstimate( t, messageType ) );

            // Setting the error message if there's one
            if ( t.getMessage() != null )
            {
                ldapResult.setErrorMessage( t.getMessage() );
            }
        }
    }
}
