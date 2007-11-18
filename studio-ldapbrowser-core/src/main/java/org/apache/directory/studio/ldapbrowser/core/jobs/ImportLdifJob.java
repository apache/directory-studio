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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.StudioProgressMonitor;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.ConnectionException;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.DnUtils;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecTypeLine;
import org.apache.directory.studio.ldifparser.parser.LdifParser;


/**
 * Job used to import an LDIF file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ImportLdifJob extends AbstractNotificationJob
{

    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The LDIF file. */
    private File ldifFile;

    /** The log file. */
    private File logFile;

    /** The continue on error flag. */
    private boolean continueOnError;


    /**
     * Creates a new instance of ImportLdifJob.
     * 
     * @param browserConnection the browser connection
     * @param ldifFile the LDIF file
     * @param logFile the log file
     * @param continueOnError the continue on error flag
     */
    public ImportLdifJob( IBrowserConnection browserConnection, File ldifFile, File logFile, boolean continueOnError )
    {
        this.browserConnection = browserConnection;
        this.ldifFile = ldifFile;
        this.logFile = logFile;
        this.continueOnError = continueOnError;

        setName( BrowserCoreMessages.jobs__import_ldif_name );
    }


    /**
     * Creates a new instance of ImportLdifJob.
     * 
     * @param connection the connection
     * @param ldifFile the LDIF file
     * @param continueOnError the continue on error
     */
    public ImportLdifJob( IBrowserConnection connection, File ldifFile, boolean continueOnError )
    {
        this( connection, ldifFile, null, continueOnError );
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getConnections()
     */
    protected Connection[] getConnections()
    {
        return new Connection[]
            { browserConnection.getConnection() };
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getLockedObjects()
     */
    protected Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( ldifFile.toString() ) );
        return l.toArray();
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#executeNotificationJob(org.apache.directory.studio.connection.core.StudioProgressMonitor)
     */
    protected void executeNotificationJob( StudioProgressMonitor monitor )
    {
        monitor.beginTask( BrowserCoreMessages.jobs__import_ldif_task, 2 );
        monitor.reportProgress( " " ); //$NON-NLS-1$
        monitor.worked( 1 );

        try
        {
            Reader ldifReader = new BufferedReader( new FileReader( this.ldifFile ) );
            LdifParser parser = new LdifParser();
            LdifEnumeration enumeration = parser.parse( ldifReader );

            Writer logWriter;
            if ( this.logFile != null )
            {
                logWriter = new BufferedWriter( new FileWriter( this.logFile ) );
            }
            else
            {
                logWriter = new Writer()
                {
                    public void close() throws IOException
                    {
                    }


                    public void flush() throws IOException
                    {
                    }


                    public void write( char[] cbuf, int off, int len ) throws IOException
                    {
                    }
                };
            }

            importLdif( browserConnection, enumeration, logWriter, continueOnError, monitor );

            logWriter.close();
            ldifReader.close();
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractEclipseJob#getErrorMessage()
     */
    protected String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_ldif_error;
    }


    /**
     * @see org.apache.directory.studio.ldapbrowser.core.jobs.AbstractNotificationJob#runNotification()
     */
    protected void runNotification()
    {
        EventRegistry.fireEntryUpdated( new BulkModificationEvent( browserConnection ), this );
    }


    /**
     * Imports the LDIF enumeration
     * 
     * @param browserConnection the browser connection
     * @param enumeration the LDIF enumeration
     * @param logWriter the log writer
     * @param continueOnError the continue on error flag
     * @param monitor the progress monitor
     */
    static void importLdif( IBrowserConnection browserConnection, LdifEnumeration enumeration, Writer logWriter,
        boolean continueOnError, StudioProgressMonitor monitor )
    {
        int importedCount = 0;
        int errorCount = 0;
        try
        {
            while ( !monitor.isCanceled() && enumeration.hasNext() )
            {
                LdifContainer container = enumeration.next();

                if ( container instanceof LdifRecord )
                {
                    LdifRecord record = ( LdifRecord ) container;
                    try
                    {
                        importLdifRecord( browserConnection, record, monitor );
                        logModification( browserConnection, logWriter, record, monitor );
                        importedCount++;

                        // update cache and adjust attribute/children initialization flags
                        LdapDN dn = new LdapDN( record.getDnLine().getValueAsString() );
                        IEntry entry = browserConnection.getEntryFromCache( dn );
                        LdapDN parentDn = DnUtils.getParent( dn );
                        IEntry parentEntry = parentDn != null ? browserConnection.getEntryFromCache( parentDn ) : null;

                        if ( record instanceof LdifChangeDeleteRecord )
                        {
                            if ( entry != null )
                            {
                                entry.setAttributesInitialized( false );
                                browserConnection.uncacheEntryRecursive( entry );
                            }
                            if ( parentEntry != null )
                            {
                                parentEntry.setChildrenInitialized( false );
                            }
                        }
                        else if ( record instanceof LdifChangeModDnRecord )
                        {
                            if ( entry != null )
                            {
                                entry.setAttributesInitialized( false );
                                browserConnection.uncacheEntryRecursive( entry );
                            }
                            if ( parentEntry != null )
                            {
                                parentEntry.setChildrenInitialized( false );
                            }
                            LdifChangeModDnRecord modDnRecord = ( LdifChangeModDnRecord ) record;
                            if ( modDnRecord.getNewsuperiorLine() != null )
                            {
                                LdapDN newSuperiorDn = new LdapDN( modDnRecord.getNewsuperiorLine().getValueAsString() );
                                IEntry newSuperiorEntry = browserConnection.getEntryFromCache( newSuperiorDn );
                                if ( newSuperiorEntry != null )
                                {
                                    newSuperiorEntry.setChildrenInitialized( false );
                                }
                            }
                        }
                        else if ( record instanceof LdifChangeAddRecord || record instanceof LdifContentRecord )
                        {
                            if ( parentEntry != null )
                            {
                                parentEntry.setChildrenInitialized( false );
                            }
                        }
                        else
                        {
                            if ( entry != null )
                            {
                                entry.setAttributesInitialized( false );
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        logModificationError( browserConnection, logWriter, record, e, monitor );
                        errorCount++;

                        if ( !continueOnError )
                        {
                            monitor.reportError( e );
                            return;
                        }
                    }

                    monitor.reportProgress( BrowserCoreMessages.bind(
                        BrowserCoreMessages.ldif__imported_n_entries_m_errors, new String[]
                            { "" + importedCount, "" + errorCount } ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
                else
                {
                    logWriter.write( container.toRawString() );
                }
            }

            if ( errorCount > 0 )
            {
                monitor.reportError( BrowserCoreMessages.bind( BrowserCoreMessages.ldif__n_errors_see_logfile,
                    new String[]
                        { "" + errorCount } ) ); //$NON-NLS-1$
            }
        }
        catch ( Exception e )
        {
            monitor.reportError( e );
        }
    }


    /**
     * Imports the LDIF record.
     * 
     * @param browserConnection the browser connection
     * @param record the LDIF record
     * @param monitor the progress monitor
     * 
     * @throws ConnectionException the connection exception
     */
    static void importLdifRecord( IBrowserConnection browserConnection, LdifRecord record, StudioProgressMonitor monitor )
        throws ConnectionException
    {
        if ( !record.isValid() )
        {
            throw new ConnectionException( BrowserCoreMessages.model__invalid_record );
        }

        String dn = record.getDnLine().getValueAsString();

        if ( record instanceof LdifContentRecord )
        {
            LdifContentRecord attrValRecord = ( LdifContentRecord ) record;
            LdifAttrValLine[] attrVals = attrValRecord.getAttrVals();
            Attributes jndiAttributes = new BasicAttributes();
            for ( int ii = 0; ii < attrVals.length; ii++ )
            {
                String attributeName = attrVals[ii].getUnfoldedAttributeDescription();
                Object realValue = attrVals[ii].getValueAsObject();

                if ( jndiAttributes.get( attributeName ) != null )
                {
                    jndiAttributes.get( attributeName ).add( realValue );
                }
                else
                {
                    jndiAttributes.put( attributeName, realValue );
                }
            }

            browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( dn, jndiAttributes,
                getControls( attrValRecord ), monitor );
        }
        else if ( record instanceof LdifChangeAddRecord )
        {
            LdifChangeAddRecord changeAddRecord = ( LdifChangeAddRecord ) record;
            LdifAttrValLine[] attrVals = changeAddRecord.getAttrVals();
            Attributes jndiAttributes = new BasicAttributes();
            for ( int ii = 0; ii < attrVals.length; ii++ )
            {
                String attributeName = attrVals[ii].getUnfoldedAttributeDescription();
                Object realValue = attrVals[ii].getValueAsObject();

                if ( jndiAttributes.get( attributeName ) != null )
                {
                    jndiAttributes.get( attributeName ).add( realValue );
                }
                else
                {
                    jndiAttributes.put( attributeName, realValue );
                }
            }

            browserConnection.getConnection().getJNDIConnectionWrapper().createEntry( dn, jndiAttributes,
                getControls( changeAddRecord ), monitor );
        }
        else if ( record instanceof LdifChangeDeleteRecord )
        {
            LdifChangeDeleteRecord changeDeleteRecord = ( LdifChangeDeleteRecord ) record;
            browserConnection.getConnection().getJNDIConnectionWrapper().deleteEntry( dn,
                getControls( changeDeleteRecord ), monitor );
        }
        else if ( record instanceof LdifChangeModifyRecord )
        {
            LdifChangeModifyRecord modifyRecord = ( LdifChangeModifyRecord ) record;
            LdifModSpec[] modSpecs = modifyRecord.getModSpecs();
            ModificationItem[] mis = new ModificationItem[modSpecs.length];
            for ( int ii = 0; ii < modSpecs.length; ii++ )
            {
                LdifModSpecTypeLine modSpecType = modSpecs[ii].getModSpecType();
                LdifAttrValLine[] attrVals = modSpecs[ii].getAttrVals();

                Attribute attribute = new BasicAttribute( modSpecType.getUnfoldedAttributeDescription() );
                for ( int x = 0; x < attrVals.length; x++ )
                {
                    attribute.add( attrVals[x].getValueAsObject() );
                }

                if ( modSpecType.isAdd() )
                {
                    mis[ii] = new ModificationItem( DirContext.ADD_ATTRIBUTE, attribute );
                }
                else if ( modSpecType.isDelete() )
                {
                    mis[ii] = new ModificationItem( DirContext.REMOVE_ATTRIBUTE, attribute );
                }
                else if ( modSpecType.isReplace() )
                {
                    mis[ii] = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, attribute );
                }
            }

            browserConnection.getConnection().getJNDIConnectionWrapper().modifyAttributes( dn, mis,
                getControls( modifyRecord ), monitor );
        }
        else if ( record instanceof LdifChangeModDnRecord )
        {
            LdifChangeModDnRecord modDnRecord = ( LdifChangeModDnRecord ) record;
            if ( modDnRecord.getNewrdnLine() != null && modDnRecord.getDeloldrdnLine() != null )
            {
                String newRdn = modDnRecord.getNewrdnLine().getValueAsString();
                boolean deleteOldRdn = modDnRecord.getDeloldrdnLine().isDeleteOldRdn();

                try
                {
                    LdapDN newDn;
                    if ( modDnRecord.getNewsuperiorLine() != null )
                        newDn = DnUtils.composeDn( newRdn, modDnRecord.getNewsuperiorLine().getValueAsString() );
                    else
                    {
                        LdapDN dnObject = new LdapDN( dn );
                        LdapDN parent = DnUtils.getParent( dnObject );
                        newDn = DnUtils.composeDn( newRdn, parent.getUpName() );
                    }

                    browserConnection.getConnection().getJNDIConnectionWrapper().rename( dn, newDn.toString(),
                        deleteOldRdn, getControls( modDnRecord ), monitor );
                }
                catch ( InvalidNameException ne )
                {
                    throw new ConnectionException( ne );
                }
            }
        }
    }


    /**
     * Gets the controls.
     * 
     * @param record the LDIF record
     * 
     * @return the controls
     */
    private static Control[] getControls( LdifRecord record )
    {
        Control[] controls = null;
        if ( record instanceof LdifChangeRecord )
        {
            LdifChangeRecord changeRecord = ( LdifChangeRecord ) record;
            LdifControlLine[] controlLines = changeRecord.getControls();
            controls = new Control[controlLines.length];
            for ( int i = 0; i < controlLines.length; i++ )
            {
                LdifControlLine line = controlLines[i];
                // TODO: encoded control value
                controls[i] = new BasicControl( line.getUnfoldedOid(), line.isCritical(), null );
            }
        }
        return controls;
    }


    /**
     * Log a modification error to the given writer.
     * 
     * @param browserConnection the browser connection
     * @param logWriter the log writer
     * @param record the record
     * @param exception the exception
     * @param monitor the progress monitor
     */
    private static void logModificationError( IBrowserConnection browserConnection, Writer logWriter,
        LdifRecord record, Exception exception, StudioProgressMonitor monitor )
    {
        try
        {
            LdifFormatParameters ldifFormatParameters = Utils.getLdifFormatParameters();
            DateFormat df = new SimpleDateFormat( BrowserCoreConstants.DATEFORMAT );

            String errorComment = "#!ERROR " + exception.getMessage(); //$NON-NLS-1$
            errorComment = errorComment.replaceAll( "\r", " " ); //$NON-NLS-1$ //$NON-NLS-2$
            errorComment = errorComment.replaceAll( "\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
            LdifCommentLine errorCommentLine = LdifCommentLine.create( errorComment );

            logWriter.write( LdifCommentLine.create( "#!RESULT ERROR" )
                .toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NL LdifFormatParameters.DEFAULTS-1$
            logWriter
                .write( LdifCommentLine
                    .create(
                        "#!CONNECTION ldap://" + browserConnection.getConnection().getHost() + ":" + browserConnection.getConnection().getPort() ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$ //$NON-NLS-2$
            logWriter.write( LdifCommentLine
                .create( "#!DATE " + df.format( new Date() ) ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$
            logWriter.write( errorCommentLine.toFormattedString( LdifFormatParameters.DEFAULT ) );
            logWriter.write( record.toFormattedString( ldifFormatParameters ) );
        }
        catch ( IOException ioe )
        {
            monitor.reportError( BrowserCoreMessages.model__error_logging_modification, ioe );
        }
    }


    /**
     * Log a modification to the given writer.
     * 
     * @param browserConnection the browser connection
     * @param logWriter the log writer
     * @param record the record
     * @param monitor the progress monitor
     */
    private static void logModification( IBrowserConnection browserConnection, Writer logWriter, LdifRecord record,
        StudioProgressMonitor monitor )
    {
        try
        {
            LdifFormatParameters ldifFormatParameters = Utils.getLdifFormatParameters();
            DateFormat df = new SimpleDateFormat( BrowserCoreConstants.DATEFORMAT );
            logWriter.write( LdifCommentLine.create( "#!RESULT OK" ).toFormattedString( ldifFormatParameters ) ); //$NON-NLS-1$
            logWriter
                .write( LdifCommentLine
                    .create(
                        "#!CONNECTION ldap://" + browserConnection.getConnection().getHost() + ":" + browserConnection.getConnection().getPort() ).toFormattedString( ldifFormatParameters ) ); //$NON-NLS-1$ //$NON-NLS-2$
            logWriter.write( LdifCommentLine
                .create( "#!DATE " + df.format( new Date() ) ).toFormattedString( ldifFormatParameters ) ); //$NON-NLS-1$
            logWriter.write( record.toFormattedString( ldifFormatParameters ) );
        }
        catch ( IOException ioe )
        {
            monitor.reportError( BrowserCoreMessages.model__error_logging_modification, ioe );
        }
    }
}
