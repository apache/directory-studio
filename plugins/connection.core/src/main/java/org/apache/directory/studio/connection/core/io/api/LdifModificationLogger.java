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


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Modification;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.name.Rdn;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.Controls;
import org.apache.directory.studio.connection.core.ILdapLogger;
import org.apache.directory.studio.connection.core.io.StudioLdapException;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifChangeTypeLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifControlLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewsuperiorLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;


/**
 * The LdifModificationLogger is used to log modifications in LDIF format into a file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifModificationLogger implements ILdapLogger
{

    /** The ID. */
    private String id;

    /** The name. */
    private String name;

    /** The description. */
    private String description;

    /** The file handlers. */
    private Map<String, FileHandler> fileHandlers = new HashMap<String, FileHandler>();

    /** The loggers. */
    private Map<String, Logger> loggers = new HashMap<String, Logger>();

    /**
     * Creates a new instance of LdifModificationLogger.
     */
    public LdifModificationLogger()
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.addPreferenceChangeListener( event -> {
            if ( ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT.equals( event.getKey() )
                || ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE.equals( event.getKey() ) )
            {
                // dispose all loggers/handlers
                for ( Logger logger : loggers.values() )
                {
                    for ( Handler handler : logger.getHandlers() )
                    {
                        handler.close();
                    }
                }

                // delete files with index greater than new file count
                Connection[] connections = ConnectionCorePlugin.getDefault().getConnectionManager().getConnections();
                for ( Connection connection : connections )
                {
                    try
                    {
                        File[] logFiles = getLogFiles( connection );
                        for ( int i = getFileCount(); i < logFiles.length; i++ )
                        {
                            if ( logFiles[i] != null && logFiles[i].exists() )
                            {
                                logFiles[i].delete();
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                    }
                }

                loggers.clear();
            }
        } );
    }


    /**
     * Inits the modification logger.
     */
    private void initModificationLogger( Connection connection )
    {
        Logger logger = Logger.getAnonymousLogger();
        loggers.put( connection.getId(), logger );
        logger.setLevel( Level.ALL );

        String logfileName = ConnectionManager.getModificationLogFileName( connection );
        try
        {
            FileHandler fileHandler = new FileHandler( logfileName, getFileSizeInKb() * 1000, getFileCount(), true );
            fileHandlers.put( connection.getId(), fileHandler );
            fileHandler.setFormatter( new Formatter()
            {
                public String format( LogRecord record )
                {
                    return record.getMessage();
                }
            } );
            logger.addHandler( fileHandler );
        }
        catch ( SecurityException e )
        {
            e.printStackTrace();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    /**
     * Disposes the modification logger of the given connection.
     * 
     * @param connection the connection
     */
    public void dispose( Connection connection )
    {
        String id = connection.getId();
        if ( loggers.containsKey( id ) )
        {
            Handler[] handlers = loggers.get( id ).getHandlers();
            for ( Handler handler : handlers )
            {
                handler.close();
            }

            loggers.remove( id );
        }
    }


    private void log( String text, StudioLdapException ex, Connection connection )
    {
        String id = connection.getId();
        if ( !loggers.containsKey( id ) )
        {
            if ( connection.getName() != null )
            {
                initModificationLogger( connection );
            }
        }

        if ( loggers.containsKey( id ) )
        {
            StringJoiner lines = new StringJoiner( "" );
            DateFormat df = new SimpleDateFormat( ConnectionCoreConstants.DATEFORMAT );
            df.setTimeZone( ConnectionCoreConstants.UTC_TIME_ZONE );

            if ( ex != null )
            {
                lines.add( LdifCommentLine
                    .create( "#!RESULT ERROR" ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$
            }
            else
            {
                lines.add( LdifCommentLine
                    .create( "#!RESULT OK" ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$
            }

            lines.add(
                LdifCommentLine
                    .create( "#!CONNECTION ldap://" + connection.getHost() + ":" + connection.getPort() ) //$NON-NLS-1$//$NON-NLS-2$
                    .toFormattedString( LdifFormatParameters.DEFAULT ) );
            lines.add( LdifCommentLine
                .create( "#!DATE " + df.format( new Date() ) ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$

            if ( ex != null )
            {
                String errorComment = "#!ERROR " + ex.getMessage(); //$NON-NLS-1$
                errorComment = errorComment.replaceAll( "\r", " " ); //$NON-NLS-1$ //$NON-NLS-2$
                errorComment = errorComment.replaceAll( "\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
                LdifCommentLine errorCommentLine = LdifCommentLine.create( errorComment );
                lines.add( errorCommentLine.toFormattedString( LdifFormatParameters.DEFAULT ) );
            }

            lines.add( text );
            Logger logger = loggers.get( id );
            logger.log( Level.ALL, lines.toString() );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void logChangetypeAdd( Connection connection, final Entry entry, final Control[] controls,
        StudioLdapException ex )
    {
        if ( !isModificationLogEnabled() )
        {
            return;
        }

        Set<String> maskedAttributes = getMaskedAttributes();
        LdifChangeAddRecord record = new LdifChangeAddRecord( LdifDnLine.create( entry.getDn().getName() ) );
        addControlLines( record, controls );
        record.setChangeType( LdifChangeTypeLine.createAdd() );
        for ( Attribute attribute : entry )
        {
            String attributeName = attribute.getUpId();
            for ( Value value : attribute )
            {
                if ( maskedAttributes.contains( Strings.toLowerCaseAscii( attributeName ) ) )
                {
                    record.addAttrVal( LdifAttrValLine.create( attributeName, "**********" ) ); //$NON-NLS-1$
                }
                else
                {
                    if ( value.isHumanReadable() )
                    {
                        record.addAttrVal( LdifAttrValLine.create( attributeName, value.getString() ) );
                    }
                    else
                    {
                        record.addAttrVal( LdifAttrValLine.create( attributeName, value.getBytes() ) );
                    }
                }
            }
        }
        record.finish( LdifSepLine.create() );

        String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
        log( formattedString, ex, connection );
    }


    /**
     * {@inheritDoc}
     */
    public void logChangetypeDelete( Connection connection, final Dn dn, final Control[] controls,
        StudioLdapException ex )
    {
        if ( !isModificationLogEnabled() )
        {
            return;
        }

        LdifChangeDeleteRecord record = new LdifChangeDeleteRecord( LdifDnLine.create( dn.getName() ) );
        addControlLines( record, controls );
        record.setChangeType( LdifChangeTypeLine.createDelete() );
        record.finish( LdifSepLine.create() );

        String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
        log( formattedString, ex, connection );
    }


    /**
     * {@inheritDoc}
     */
    public void logChangetypeModify( Connection connection, final Dn dn,
        final Collection<Modification> modifications, final Control[] controls, StudioLdapException ex )
    {
        if ( !isModificationLogEnabled() )
        {
            return;
        }

        Set<String> maskedAttributes = getMaskedAttributes();
        LdifChangeModifyRecord record = new LdifChangeModifyRecord( LdifDnLine.create( dn.getName() ) );
        addControlLines( record, controls );
        record.setChangeType( LdifChangeTypeLine.createModify() );
        for ( Modification item : modifications )
        {
            String attributeName = item.getAttribute().getUpId();
            LdifModSpec modSpec;
            switch ( item.getOperation() )
            {
                case ADD_ATTRIBUTE:
                    modSpec = LdifModSpec.createAdd( attributeName );
                    break;
                case REMOVE_ATTRIBUTE:
                    modSpec = LdifModSpec.createDelete( attributeName );
                    break;
                case REPLACE_ATTRIBUTE:
                    modSpec = LdifModSpec.createReplace( attributeName );
                    break;
                default:
                    continue;
            }
            for ( Value value : item.getAttribute() )
            {
                if ( maskedAttributes.contains( Strings.toLowerCaseAscii( attributeName ) ) )
                {
                    modSpec.addAttrVal( LdifAttrValLine.create( attributeName, "**********" ) ); //$NON-NLS-1$
                }
                else
                {
                    if ( value.isHumanReadable() )
                    {
                        modSpec.addAttrVal( LdifAttrValLine.create( attributeName, value.getString() ) );
                    }
                    else
                    {
                        modSpec.addAttrVal( LdifAttrValLine.create( attributeName, value.getBytes() ) );
                    }
                }
            }
            modSpec.finish( LdifModSpecSepLine.create() );

            record.addModSpec( modSpec );
        }
        record.finish( LdifSepLine.create() );

        String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
        log( formattedString, ex, connection );
    }


    /**
     * {@inheritDoc}
     */
    public void logChangetypeModDn( Connection connection, final Dn oldDn, final Dn newDn,
        final boolean deleteOldRdn, final Control[] controls, StudioLdapException ex )
    {
        if ( !isModificationLogEnabled() )
        {
            return;
        }

        Rdn newrdn = newDn.getRdn();
        Dn newsuperior = newDn.getParent();

        LdifChangeModDnRecord record = new LdifChangeModDnRecord( LdifDnLine.create( oldDn.getName() ) );
        addControlLines( record, controls );
        record.setChangeType( LdifChangeTypeLine.createModDn() );
        record.setNewrdn( LdifNewrdnLine.create( newrdn.getName() ) );
        record.setDeloldrdn( deleteOldRdn ? LdifDeloldrdnLine.create1() : LdifDeloldrdnLine.create0() );
        record.setNewsuperior( LdifNewsuperiorLine.create( newsuperior.getName() ) );
        record.finish( LdifSepLine.create() );

        String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
        log( formattedString, ex, connection );
    }


    /**
     * Adds control lines to the record
     *
     * @param record the recored
     * @param controls the controls
     */
    private static void addControlLines( LdifChangeRecord record, Control[] controls )
    {
        if ( controls != null )
        {
            for ( Control control : controls )
            {
                String oid = control.getOid();
                boolean isCritical = control.isCritical();
                byte[] bytes = Controls.getEncodedValue( control );
                LdifControlLine controlLine = LdifControlLine.create( oid, isCritical, bytes );
                record.addControl( controlLine );
            }
        }
    }


    /**
     * Gets the files.
     * 
     * @param connection the connection
     * 
     * @return the files
     */
    public File[] getFiles( Connection connection )
    {
        String id = connection.getId();
        if ( !loggers.containsKey( id ) )
        {
            if ( connection.getName() != null )
            {
                initModificationLogger( connection );
            }
        }

        try
        {
            return getLogFiles( connection );
        }
        catch ( Exception e )
        {
            return new File[0];
        }
    }


    /**
     * Gets the log files.
     * 
     * @param fileHandler the file handler
     * 
     * @return the log files
     */
    private static File[] getLogFiles( Connection connection )
    {
        String logfileNamePattern = ConnectionManager.getModificationLogFileName( connection );
        File file = new File( logfileNamePattern );
        String pattern = file.getName().replace( "%u", "\\d+" ).replace( "%g", "\\d+" );
        File dir = file.getParentFile();
        File[] files = dir.listFiles( ( d, f ) -> {
            return f.matches( pattern );
        } );
        Arrays.sort( files );
        return files;
    }


    /**
     * Checks if modification log is enabled.
     * 
     * @return true, if modification log is enabled
     */
    private boolean isModificationLogEnabled()
    {
        return Platform.getPreferencesService().getBoolean( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_ENABLE, true, null );
    }


    /**
     * Gets the number of log files to use.
     * 
     * @return the number of log files to use
     */
    private int getFileCount()
    {
        return Platform.getPreferencesService().getInt( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_COUNT, 10, null );
    }


    /**
     * Gets the maximum file size in kB.
     * 
     * @return the maximum file size in kB
     */
    private int getFileSizeInKb()
    {
        return Platform.getPreferencesService().getInt( ConnectionCoreConstants.PLUGIN_ID,
            ConnectionCoreConstants.PREFERENCE_MODIFICATIONLOGS_FILE_SIZE, 100, null );
    }


    public String getId()
    {
        return id;
    }


    public void setId( String id )
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }


    public void setName( String name )
    {
        this.name = name;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }

}
