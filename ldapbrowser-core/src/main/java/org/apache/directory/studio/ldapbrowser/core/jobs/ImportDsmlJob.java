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
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.ExtendedRequest;

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
import org.apache.directory.shared.ldap.codec.LdapConstants;
import org.apache.directory.shared.ldap.codec.LdapResultCodec;
import org.apache.directory.shared.ldap.codec.add.AddRequestCodec;
import org.apache.directory.shared.ldap.codec.bind.BindRequestCodec;
import org.apache.directory.shared.ldap.codec.compare.CompareRequestCodec;
import org.apache.directory.shared.ldap.codec.del.DelRequestCodec;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequestCodec;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequestCodec;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequestCodec;
import org.apache.directory.shared.ldap.codec.search.SearchRequestCodec;
import org.apache.directory.shared.ldap.codec.util.LdapURLEncodingException;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.jndi.StudioNamingEnumeration;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;


/**
 * This class implements a Job for Importing a DSML File into a LDAP server
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportDsmlJob extends AbstractNotificationJob
{
    /** The connection to use */
    private IBrowserConnection browserConnection;

    /** The DSML file to use */
    private File dsmlFile;

    /** The Save file to use */
    private File responseFile;


    /**
     * Creates a new instance of ImportDsmlJob.
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
    public ImportDsmlJob( IBrowserConnection connection, File dsmlFile, File saveFile )
    {
        this.browserConnection = connection;
        this.dsmlFile = dsmlFile;
        this.responseFile = saveFile;

        setName( BrowserCoreMessages.jobs__import_dsml_name );
    }


    /**
     * Creates a new instance of ImportDsmlJob.
     *
     * @param connection
     *          The Connection to use
     * @param dsmlFile
     *          The DSML file to read from
     * @param continueOnError
     *          The ContinueOnError flag
     */
    public ImportDsmlJob( IBrowserConnection connection, File dsmlFile )
    {
        this( connection, dsmlFile, null );
    }


    /**
     * {@inheritDoc}
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * {@inheritDoc}
     */
    protected Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( dsmlFile.toString() ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
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
     * Processes the request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     * @throws NamingException 
     * @throws LdapURLEncodingException 
     */
    private void processRequest( Object request, BatchResponseDsml batchResponseDsml, StudioProgressMonitor monitor )
        throws NamingException, LdapURLEncodingException
    {
        if ( request instanceof BindRequestCodec )
        {
            processBindRequest( ( BindRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof AddRequestCodec )
        {
            processAddRequest( ( AddRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof CompareRequestCodec )
        {
            processCompareRequest( ( CompareRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof DelRequestCodec )
        {
            processDelRequest( ( DelRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ExtendedRequestCodec )
        {
            processExtendedRequest( ( ExtendedRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ModifyRequestCodec )
        {
            processModifyRequest( ( ModifyRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ModifyDNRequestCodec )
        {
            processModifyDNRequest( ( ModifyDNRequestCodec ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof SearchRequestCodec )
        {
            processSearchRequest( ( SearchRequestCodec ) request, batchResponseDsml, monitor );
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
    private void processBindRequest( BindRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // We can not support extended requests at the moment,
        // we need a more advanced connection wrapper.

        // Creating the response
        if ( batchResponseDsml != null )
        {
            AuthResponseDsml authResponseDsml = new AuthResponseDsml();
            LdapResultCodec ldapResult = new LdapResultCodec();
            ldapResult.setResultCode( ResultCodeEnum.UNWILLING_TO_PERFORM );
            ldapResult.setErrorMessage( "This kind of request is not yet supported." );
            authResponseDsml.setLdapResult( ldapResult );
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
    private void processAddRequest( AddRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Executing the add request
        Entry entry = request.getEntry();
        browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( entry.getDn().getUpName(),
            AttributeUtils.toAttributes( entry ), ReferralHandlingMethod.IGNORE, null, monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            AddResponseDsml addResponseDsml = new AddResponseDsml();
            addResponseDsml.setLdapResult( getLdapResult( monitor, MessageTypeEnum.ADD_REQUEST ) );
            addResponseDsml.getLdapResult().setMatchedDN( entry.getDn() );
            batchResponseDsml.addResponse( addResponseDsml );
        }

        // Update cached entries
        LdapDN dn = entry.getDn();
        IEntry e = browserConnection.getEntryFromCache( dn );
        LdapDN parentDn = DnUtils.getParent( dn );
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
    private void processCompareRequest( CompareRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // We can not support extended requests at the moment,
        // we need a more advanced connection wrapper.

        // Creating the response
        if ( batchResponseDsml != null )
        {
            CompareResponseDsml compareResponseDsml = new CompareResponseDsml();
            LdapResultCodec ldapResult = new LdapResultCodec();
            ldapResult.setResultCode( ResultCodeEnum.UNWILLING_TO_PERFORM );
            ldapResult.setErrorMessage( "This kind of request is not yet supported." );
            compareResponseDsml.setLdapResult( ldapResult );
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
    private void processDelRequest( DelRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Executing the del request
        browserConnection.getConnection().getJNDIConnectionWrapper().deleteEntry( request.getEntry().getUpName(),
            ReferralHandlingMethod.IGNORE, null, monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            DelResponseDsml delResponseDsml = new DelResponseDsml();
            delResponseDsml.setLdapResult( getLdapResult( monitor, MessageTypeEnum.DEL_REQUEST ) );
            delResponseDsml.getLdapResult().setMatchedDN( request.getEntry() );
            batchResponseDsml.addResponse( delResponseDsml );
        }
        
        // Update cached entries
        LdapDN dn = request.getEntry();
        IEntry e = browserConnection.getEntryFromCache( dn );
        LdapDN parentDn = DnUtils.getParent( dn );
        IEntry parentEntry = parentDn != null ? browserConnection.getEntryFromCache( parentDn )
            : null;
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
    private void processExtendedRequest( ExtendedRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // We can not support extended requests at the moment,
        // we need a more advanced connection wrapper.

        // Creating the response
        if ( batchResponseDsml != null )
        {
            ExtendedResponseDsml extendedResponseDsml = new ExtendedResponseDsml();
            LdapResultCodec ldapResult = new LdapResultCodec();
            ldapResult.setResultCode( ResultCodeEnum.UNWILLING_TO_PERFORM );
            ldapResult.setErrorMessage( "This kind of request is not yet supported." );
            extendedResponseDsml.setLdapResult( ldapResult );
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
    private void processModifyRequest( ModifyRequestCodec request, BatchResponseDsml batchResponseDsml,
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
        browserConnection.getConnection().getJNDIConnectionWrapper().modifyEntry( request.getObject().getUpName(),
            modificationItems.toArray( new ModificationItem[0] ), ReferralHandlingMethod.IGNORE, null, monitor, null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            ModifyResponseDsml modifyResponseDsml = new ModifyResponseDsml();
            modifyResponseDsml.setLdapResult( getLdapResult( monitor, MessageTypeEnum.MODIFY_REQUEST ) );
            modifyResponseDsml.getLdapResult().setMatchedDN( request.getObject() );
            batchResponseDsml.addResponse( modifyResponseDsml );
        }
        
        LdapDN dn = request.getObject();
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
     * Processes a modify DN request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processModifyDNRequest( ModifyDNRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Executing the modify DN request
        browserConnection.getConnection().getJNDIConnectionWrapper().renameEntry( request.getEntry().getUpName(),
            request.getNewRDN().getUpName(), request.isDeleteOldRDN(), ReferralHandlingMethod.IGNORE, null, monitor,
            null );

        // Creating the response
        if ( batchResponseDsml != null )
        {
            ModDNResponseDsml modDNResponseDsml = new ModDNResponseDsml();
            modDNResponseDsml.setLdapResult( getLdapResult( monitor, MessageTypeEnum.MOD_DN_REQUEST ) );
            modDNResponseDsml.getLdapResult().setMatchedDN( request.getEntry() );
            batchResponseDsml.addResponse( modDNResponseDsml );
        }
        
        // Update cached entries
        LdapDN dn = request.getEntry();
        IEntry e = browserConnection.getEntryFromCache( dn );
        LdapDN parentDn = DnUtils.getParent( dn );
        IEntry parentEntry = parentDn != null ? browserConnection.getEntryFromCache( parentDn )
            : null;
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
            LdapDN newSuperiorDn = request.getNewSuperior();
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
     * @throws LdapURLEncodingException 
     */
    private void processSearchRequest( SearchRequestCodec request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor ) throws NamingException, LdapURLEncodingException
    {
        // Creating the response
        if ( batchResponseDsml != null )
        {
            // [Optimization] We're only searching if we need to produce a response
            StudioNamingEnumeration ne = browserConnection.getConnection().getJNDIConnectionWrapper().search(
                request.getBaseObject().getUpName(), request.getFilter().toString(), getSearchControls( request ),
                getAliasDereferencingMethod( request ), ReferralHandlingMethod.IGNORE, null, monitor, null );

            SearchParameter sp = new SearchParameter();
            sp.setReferralsHandlingMethod( browserConnection.getReferralsHandlingMethod() );
            ExportDsmlJob.processAsDsmlResponse( ne, batchResponseDsml, monitor, sp );
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
    private SearchControls getSearchControls( SearchRequestCodec request )
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
        for ( EntryAttribute entryAttribute : request.getAttributes() )
        {
            returningAttributes.add( entryAttribute.getId() );
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
    private AliasDereferencingMethod getAliasDereferencingMethod( SearchRequestCodec request )
    {
        switch ( request.getDerefAliases() )
        {
            case LdapConstants.NEVER_DEREF_ALIASES:
                return AliasDereferencingMethod.NEVER;
            case LdapConstants.DEREF_ALWAYS:
                return AliasDereferencingMethod.ALWAYS;
            case LdapConstants.DEREF_FINDING_BASE_OBJ:
                return AliasDereferencingMethod.FINDING;
            case LdapConstants.DEREF_IN_SEARCHING:
                return AliasDereferencingMethod.SEARCH;
            default:
                return AliasDereferencingMethod.NEVER;
        }
    }


    /**
     * Get the LDAP Result corresponding to the given monitor
     *
     * @param monitor
     *      the progress monitor
     * @return
     *      the corresponding LDAP Result
     */
    private LdapResultCodec getLdapResult( StudioProgressMonitor monitor, MessageTypeEnum messageType )
    {
        LdapResultCodec ldapResult = new LdapResultCodec();

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

        return ldapResult;
    }


    /**
     * {@inheritDoc}
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_dsml_error;
    }


    /**
     * {@inheritDoc}
     */
    protected void runNotification()
    {
        EventRegistry.fireEntryUpdated( new BulkModificationEvent( browserConnection ), this );
    }

}
