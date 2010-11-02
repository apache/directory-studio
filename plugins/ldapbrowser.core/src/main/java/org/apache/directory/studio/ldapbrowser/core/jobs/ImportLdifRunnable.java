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

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.BasicControl;
import javax.naming.ldap.Control;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.common.core.jobs.StudioProgressMonitor;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.jobs.StudioConnectionBulkRunnableWithProgress;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreMessages;
import org.apache.directory.studio.ldapbrowser.core.events.BulkModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
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
 * Runnable used to import an LDIF file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ImportLdifRunnable implements StudioConnectionBulkRunnableWithProgress
{
    /** The browser connection. */
    private IBrowserConnection browserConnection;

    /** The LDIF file. */
    private File ldifFile;

    /** The log file. */
    private File logFile;

    /** The update if entry exists flag. */
    private boolean updateIfEntryExists;

    /** The continue on error flag. */
    private boolean continueOnError;


    /**
     * Creates a new instance of ImportLdifRunnable.
     * 
     * @param browserConnection the browser connection
     * @param ldifFile the LDIF file
     * @param logFile the log file
     * @param updateIfEntryExists the update if entry exists flag
     * @param continueOnError the continue on error flag
     */
    public ImportLdifRunnable( IBrowserConnection browserConnection, File ldifFile, File logFile,
        boolean updateIfEntryExists, boolean continueOnError )
    {
        this.browserConnection = browserConnection;
        this.ldifFile = ldifFile;
        this.logFile = logFile;
        this.continueOnError = continueOnError;
        this.updateIfEntryExists = updateIfEntryExists;
    }


    /**
     * Creates a new instance of ImportLdifRunnable.
     * 
     * @param connection the connection
     * @param ldifFile the LDIF file
     * @param updateIfEntryExists the update if entry exists flag
     * @param continueOnError the continue on error flag
     */
    public ImportLdifRunnable( IBrowserConnection connection, File ldifFile, boolean updateIfEntryExists,
        boolean continueOnError )
    {
        this( connection, ldifFile, null, updateIfEntryExists, continueOnError );
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
        return BrowserCoreMessages.jobs__import_ldif_name;
    }


    /**
     * {@inheritDoc}
     */
    public Object[] getLockedObjects()
    {
        List<Object> l = new ArrayList<Object>();
        l.add( browserConnection.getUrl() + "_" + DigestUtils.shaHex( ldifFile.toString() ) );
        return l.toArray();
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return BrowserCoreMessages.jobs__import_ldif_error;
    }


    /**
     * {@inheritDoc}
     */
    public void run( StudioProgressMonitor monitor )
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

            importLdif( browserConnection, enumeration, logWriter, updateIfEntryExists, continueOnError, monitor );

            logWriter.close();
            ldifReader.close();
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
     * Imports the LDIF enumeration
     * 
     * @param browserConnection the browser connection
     * @param enumeration the LDIF enumeration
     * @param logWriter the log writer
     * @param updateIfEntryExists the update if entry exists flag
     * @param continueOnError the continue on error flag
     * @param monitor the progress monitor
     */
    static void importLdif( IBrowserConnection browserConnection, LdifEnumeration enumeration, Writer logWriter,
        boolean updateIfEntryExists, boolean continueOnError, StudioProgressMonitor monitor )
    {
        if ( browserConnection == null )
        {
            return;
        }

        StudioProgressMonitor dummyMonitor = new StudioProgressMonitor( monitor );
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
                        dummyMonitor.reset();
                        importLdifRecord( browserConnection, record, updateIfEntryExists, dummyMonitor );
                        if ( dummyMonitor.errorsReported() )
                        {
                            errorCount++;
                            logModificationError( browserConnection, logWriter, record, dummyMonitor.getException(),
                                monitor );

                            if ( !continueOnError )
                            {
                                monitor.reportError( dummyMonitor.getException() );
                                return;
                            }
                        }
                        else
                        {
                            importedCount++;
                            logModification( browserConnection, logWriter, record, monitor );

                            // update cache and adjust attribute/children initialization flags
                            DN dn = new DN( record.getDnLine().getValueAsString() );
                            IEntry entry = browserConnection.getEntryFromCache( dn );
                            DN parentDn = DnUtils.getParent( dn );
                            IEntry parentEntry = null;
                            while ( parentEntry == null && parentDn != null )
                            {
                                parentEntry = browserConnection.getEntryFromCache( parentDn );
                                parentDn = DnUtils.getParent( parentDn );
                            }

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
                                    DN newSuperiorDn = new DN( modDnRecord.getNewsuperiorLine()
                                        .getValueAsString() );
                                    IEntry newSuperiorEntry = browserConnection.getEntryFromCache( newSuperiorDn );
                                    if ( newSuperiorEntry != null )
                                    {
                                        newSuperiorEntry.setChildrenInitialized( false );
                                    }
                                }
                            }
                            else if ( record instanceof LdifChangeAddRecord || record instanceof LdifContentRecord )
                            {
                                if ( entry != null )
                                {
                                    entry.setAttributesInitialized( false );
                                }
                                if ( parentEntry != null )
                                {
                                    parentEntry.setChildrenInitialized( false );
                                    parentEntry.setHasChildrenHint( true );
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
     * @param updateIfEntryExists the update if entry exists flag
     * @param monitor the progress monitor
     * 
     * @throws NamingException the naming exception
     * @throws LdapInvalidDnException
     */
    static void importLdifRecord( IBrowserConnection browserConnection, LdifRecord record, boolean updateIfEntryExists,
        StudioProgressMonitor monitor ) throws NamingException, LdapInvalidDnException
    {
        if ( !record.isValid() )
        {
            throw new NamingException( BrowserCoreMessages.model__invalid_record );
        }

        String dn = record.getDnLine().getValueAsString();

        if ( record instanceof LdifContentRecord || record instanceof LdifChangeAddRecord )
        {
            LdifAttrValLine[] attrVals;
            IEntry dummyEntry;
            if ( record instanceof LdifContentRecord )
            {
                LdifContentRecord attrValRecord = ( LdifContentRecord ) record;
                attrVals = attrValRecord.getAttrVals();
                try
                {
                    dummyEntry = ModelConverter.ldifContentRecordToEntry( attrValRecord, browserConnection );
                }
                catch ( LdapInvalidDnException e )
                {
                    monitor.reportError( e );
                    return;
                }
            }
            else
            {
                LdifChangeAddRecord changeAddRecord = ( LdifChangeAddRecord ) record;
                attrVals = changeAddRecord.getAttrVals();
                try
                {
                    dummyEntry = ModelConverter.ldifChangeAddRecordToEntry( changeAddRecord, browserConnection );
                }
                catch ( LdapInvalidDnException e )
                {
                    monitor.reportError( e );
                    return;
                }
            }

            Attributes jndiAttributes = new BasicAttributes();
            for ( LdifAttrValLine attrVal : attrVals )
            {
                String attributeName = attrVal.getUnfoldedAttributeDescription();
                Object realValue = attrVal.getValueAsObject();

                if ( jndiAttributes.get( attributeName ) != null )
                {
                    jndiAttributes.get( attributeName ).add( realValue );
                }
                else
                {
                    jndiAttributes.put( attributeName, realValue );
                }
            }

            browserConnection.getConnection().getConnectionWrapper()
                .createEntry( dn, jndiAttributes, getControls( record ), monitor, null );

            if ( monitor.errorsReported() && updateIfEntryExists
                && monitor.getException() instanceof NameAlreadyBoundException )
            {
                // creation failed with Error 68, now try to update the existing entry
                monitor.reset();

                ModificationItem[] mis = ModelConverter.entryToReplaceModificationItems( dummyEntry );
                browserConnection.getConnection().getConnectionWrapper()
                    .modifyEntry( dn, mis, getControls( record ), monitor, null );
            }
        }
        else if ( record instanceof LdifChangeDeleteRecord )
        {
            LdifChangeDeleteRecord changeDeleteRecord = ( LdifChangeDeleteRecord ) record;
            browserConnection.getConnection().getConnectionWrapper()
                .deleteEntry( dn, getControls( changeDeleteRecord ), monitor, null );
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

            browserConnection.getConnection().getConnectionWrapper()
                .modifyEntry( dn, mis, getControls( modifyRecord ), monitor, null );
        }
        else if ( record instanceof LdifChangeModDnRecord )
        {
            LdifChangeModDnRecord modDnRecord = ( LdifChangeModDnRecord ) record;
            if ( modDnRecord.getNewrdnLine() != null && modDnRecord.getDeloldrdnLine() != null )
            {
                String newRdn = modDnRecord.getNewrdnLine().getValueAsString();
                boolean deleteOldRdn = modDnRecord.getDeloldrdnLine().isDeleteOldRdn();

                DN newDn;
                if ( modDnRecord.getNewsuperiorLine() != null )
                {
                    newDn = DnUtils.composeDn( newRdn, modDnRecord.getNewsuperiorLine().getValueAsString() );
                }
                else
                {
                    DN dnObject = new DN( dn );
                    DN parent = DnUtils.getParent( dnObject );
                    newDn = DnUtils.composeDn( newRdn, parent.getName() );
                }

                browserConnection.getConnection().getConnectionWrapper()
                    .renameEntry( dn, newDn.toString(), deleteOldRdn, getControls( modDnRecord ), monitor, null );
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
                controls[i] = new BasicControl( line.getUnfoldedOid(), line.isCritical(),
                    line.getControlValueAsBinary() );
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
        LdifRecord record, Throwable exception, StudioProgressMonitor monitor )
    {
        try
        {
            LdifFormatParameters ldifFormatParameters = Utils.getLdifFormatParameters();
            DateFormat df = new SimpleDateFormat( ConnectionCoreConstants.DATEFORMAT );

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
            DateFormat df = new SimpleDateFormat( ConnectionCoreConstants.DATEFORMAT );
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
