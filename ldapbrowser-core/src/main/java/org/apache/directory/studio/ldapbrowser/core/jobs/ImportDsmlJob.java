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

import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.ldap.codec.add.AddRequest;
import org.apache.directory.shared.ldap.codec.compare.CompareRequest;
import org.apache.directory.shared.ldap.codec.del.DelRequest;
import org.apache.directory.shared.ldap.codec.extended.ExtendedRequest;
import org.apache.directory.shared.ldap.codec.modify.ModifyRequest;
import org.apache.directory.shared.ldap.codec.modifyDn.ModifyDNRequest;
import org.apache.directory.shared.ldap.codec.search.SearchRequest;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.Modification;
import org.apache.directory.shared.ldap.entry.ModificationOperation;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.util.AttributeUtils;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.ReferralHandlingMethod;
import org.apache.directory.studio.connection.core.io.jndi.JNDIConnectionWrapper;
import org.apache.directory.studio.connection.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.dsmlv2.Dsmlv2Parser;
import org.apache.directory.studio.dsmlv2.reponse.BatchResponseDsml;
import org.apache.directory.studio.dsmlv2.request.BatchRequest;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;


/**
 * This class implements a Job for Importing a DSML File into a LDAP server
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportDsmlJob extends AbstractEclipseJob
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


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( dsmlFile.toString() ) );
        return l.toArray();
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#executeAsyncJob(org.apache.directory.studio.ldapbrowser.core.jobs.ExtendedProgressMonitor)
     */
    protected void executeAsyncJob( StudioProgressMonitor monitor )
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

            // Processing each request
            List<?> requests = batchRequest.getRequests();
            for ( Object request : requests )
            {
                processRequest( request, batchResponseDsml, monitor );
            }

            // Writing the DSML response file to its final destination file.
            if ( responseFile != null )
            {
                FileOutputStream fos = new FileOutputStream( responseFile );
                OutputStreamWriter osw = new OutputStreamWriter( fos );
                BufferedWriter bufferedWriter = new BufferedWriter( osw );
                bufferedWriter.write( batchResponseDsml.toDsml() );
                bufferedWriter.close();
                osw.close();
                fos.close();
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
     */
    private void processRequest( Object request, BatchResponseDsml batchResponseDsml, StudioProgressMonitor monitor )
    {
        if ( request instanceof AddRequest )
        {
            processAddRequest( ( AddRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof CompareRequest )
        {
            processCompareRequest( ( CompareRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof DelRequest )
        {
            processDelRequest( ( DelRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ExtendedRequest )
        {
            processExtendedRequest( ( ExtendedRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ModifyRequest )
        {
            processModifyRequest( ( ModifyRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof ModifyDNRequest )
        {
            processModifyDNRequest( ( ModifyDNRequest ) request, batchResponseDsml, monitor );
        }
        else if ( request instanceof SearchRequest )
        {
            processSearchRequest( ( SearchRequest ) request, batchResponseDsml, monitor );
        }

        System.out.println( request );
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
        // Creating a dummy monitor that will be used to check if something
        // went wrong when executing the request
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

        // Executing the add request
        Entry entry = request.getEntry();
        browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( entry.getDn().toString(),
            AttributeUtils.toAttributes( entry ), ReferralHandlingMethod.IGNORE, null, dummyMonitor, null );

        if ( dummyMonitor.errorsReported() )
        {
            dummyMonitor.getException().printStackTrace();
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
        // TODO Auto-generated method stub

    }


    /**
     * Processes a del request.
     *
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processDelRequest( DelRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Creating a dummy monitor that will be used to check if something
        // went wrong when executing the request
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

        // Executing the del request
        browserConnection.getConnection().getJNDIConnectionWrapper().deleteEntry( request.getEntry().toString(),
            ReferralHandlingMethod.IGNORE, null, dummyMonitor, null );

        if ( dummyMonitor.errorsReported() )
        {
            dummyMonitor.getException().printStackTrace();
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
        // TODO Auto-generated method stub

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
        // Creating a dummy monitor that will be used to check if something
        // went wrong when executing the request
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

        // Creating the modification items
        List<ModificationItem> modificationItems = new ArrayList<ModificationItem>();
        for ( Modification modification : request.getModifications() )
        {
            modificationItems.add( new ModificationItem( convertModificationOperation( modification.getOperation() ),
                AttributeUtils.toAttribute( modification.getAttribute() ) ) );
        }

        // Executing the modify request
        browserConnection.getConnection().getJNDIConnectionWrapper().modifyEntry( request.getObject().toString(),
            modificationItems.toArray( new ModificationItem[0] ), ReferralHandlingMethod.IGNORE, null, dummyMonitor,
            null );

        if ( dummyMonitor.errorsReported() )
        {
            dummyMonitor.getException().printStackTrace();
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
        }

        return 0;
    }


    /**
     * Processes a modify DN request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processModifyDNRequest( ModifyDNRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // Creating a dummy monitor that will be used to check if something
        // went wrong when executing the request
        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );

        // Executing the modify DN request
        browserConnection.getConnection().getJNDIConnectionWrapper().renameEntry( request.getEntry().toString(),
            request.getNewRDN().toString(), request.isDeleteOldRDN(), ReferralHandlingMethod.IGNORE, null,
            dummyMonitor, null );

        if ( dummyMonitor.errorsReported() )
        {
            dummyMonitor.getException().printStackTrace();
        }
    }


    /**
     * Processes a search request.
     * 
     * @param request
     *      the request
     * @param batchResponseDsml
     *      the DSML batch response (can be <code>null</code>)
     */
    private void processSearchRequest( SearchRequest request, BatchResponseDsml batchResponseDsml,
        StudioProgressMonitor monitor )
    {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_dsml_error;
    }

}
