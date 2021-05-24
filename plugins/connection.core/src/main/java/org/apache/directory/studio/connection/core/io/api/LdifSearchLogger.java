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
import java.util.ArrayList;
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

import javax.naming.directory.SearchControls;

import org.apache.commons.lang3.StringUtils;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.apache.directory.api.ldap.model.message.Control;
import org.apache.directory.api.ldap.model.message.Referral;
import org.apache.directory.api.ldap.model.url.LdapUrl;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.Connection.AliasDereferencingMethod;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.ILdapLogger;
import org.apache.directory.studio.connection.core.ReferralsInfo;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.connection.core.io.StudioLdapException;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifLineBase;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;


/**
 * The LdifSearchLogger is used to log searches into a file.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifSearchLogger implements ILdapLogger
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
     * Creates a new instance of LdifSearchLogger.
     */
    public LdifSearchLogger()
    {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode( ConnectionCoreConstants.PLUGIN_ID );
        prefs.addPreferenceChangeListener( event -> {
            if ( ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_COUNT.equals( event.getKey() )
                || ConnectionCoreConstants.PREFERENCE_SEARCHLOGS_FILE_SIZE.equals( event.getKey() ) )
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
     * Inits the search logger.
     */
    private void initSearchLogger( Connection connection )
    {
        Logger logger = Logger.getAnonymousLogger();
        loggers.put( connection.getId(), logger );
        logger.setLevel( Level.ALL );

        String logfileName = ConnectionManager.getSearchLogFileName( connection );
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
     * Disposes the search logger of the given connection.
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

            File[] files = getLogFiles( connection );
            for ( File file : files )
            {
                deleteFileWithRetry( file );
            }

            loggers.remove( id );
        }
    }


    private void log( String text, String type, StudioLdapException ex, Connection connection )
    {
        String id = connection.getId();
        if ( !loggers.containsKey( id ) )
        {
            if ( connection.getName() != null )
            {
                initSearchLogger( connection );
            }
        }

        if ( loggers.containsKey( id ) )
        {
            StringJoiner lines = new StringJoiner( "" );
            DateFormat df = new SimpleDateFormat( ConnectionCoreConstants.DATEFORMAT );
            df.setTimeZone( ConnectionCoreConstants.UTC_TIME_ZONE );

            if ( ex != null )
            {
                lines.add( LdifCommentLine.create( "#!" + type + " ERROR" ) //$NON-NLS-1$//$NON-NLS-2$
                    .toFormattedString( LdifFormatParameters.DEFAULT ) );
            }
            else
            {
                lines.add( LdifCommentLine.create( "#!" + type + " OK" ) //$NON-NLS-1$ //$NON-NLS-2$
                    .toFormattedString( LdifFormatParameters.DEFAULT ) );
            }

            lines.add(
                LdifCommentLine
                    .create( "#!CONNECTION ldap://" + connection.getHost() + ":" + connection.getPort() ) //$NON-NLS-1$//$NON-NLS-2$
                    .toFormattedString( LdifFormatParameters.DEFAULT ) );
            lines.add( LdifCommentLine.create( "#!DATE " + df.format( new Date() ) ) //$NON-NLS-1$
                .toFormattedString( LdifFormatParameters.DEFAULT ) );

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
    public void logSearchRequest( Connection connection, String searchBase, String filter,
        SearchControls searchControls, AliasDereferencingMethod aliasesDereferencingMethod,
        Control[] controls, long requestNum, StudioLdapException ex )
    {
        if ( !isSearchRequestLogEnabled() )
        {
            return;
        }

        String scopeAsString = searchControls.getSearchScope() == SearchControls.SUBTREE_SCOPE ? "wholeSubtree (2)" //$NON-NLS-1$
            : searchControls.getSearchScope() == SearchControls.ONELEVEL_SCOPE ? "singleLevel (1)" : "baseObject (0)"; //$NON-NLS-1$ //$NON-NLS-2$
        String attributesAsString = searchControls.getReturningAttributes() == null ? "*" //$NON-NLS-1$
            : searchControls
                .getReturningAttributes().length == 0 ? "1.1" //$NON-NLS-1$
                    : StringUtils.join( searchControls.getReturningAttributes(),
                        " " );
        String aliasAsString = aliasesDereferencingMethod == AliasDereferencingMethod.ALWAYS ? "derefAlways (3)" //$NON-NLS-1$
            : aliasesDereferencingMethod == AliasDereferencingMethod.FINDING ? "derefFindingBaseObj (2)" //$NON-NLS-1$
                : aliasesDereferencingMethod == AliasDereferencingMethod.SEARCH ? "derefInSearching (1)" //$NON-NLS-1$
                    : "neverDerefAliases (0)"; //$NON-NLS-1$

        // build LDAP URL
        LdapUrl url = Utils.getLdapURL( connection, searchBase, searchControls.getSearchScope(), filter, searchControls
            .getReturningAttributes() );

        // build command line
        String cmdLine = Utils.getLdapSearchCommandLine( connection, searchBase, searchControls.getSearchScope(),
            aliasesDereferencingMethod, searchControls.getCountLimit(), searchControls.getTimeLimit(), filter,
            searchControls.getReturningAttributes() );

        // build 
        Collection<LdifLineBase> lines = new ArrayList<LdifLineBase>();
        lines.add( LdifCommentLine.create( "# LDAP URL     : " + url.toString() ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# command line : " + cmdLine.toString() ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# baseObject   : " + searchBase ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# scope        : " + scopeAsString ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# derefAliases : " + aliasAsString ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# sizeLimit    : " + searchControls.getCountLimit() ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# timeLimit    : " + searchControls.getTimeLimit() ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# typesOnly    : " + "False" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        lines.add( LdifCommentLine.create( "# filter       : " + filter ) ); //$NON-NLS-1$
        lines.add( LdifCommentLine.create( "# attributes   : " + attributesAsString ) ); //$NON-NLS-1$
        if ( controls != null )
        {
            for ( Control control : controls )
            {
                lines.add( LdifCommentLine.create( "# control      : " + control.getOid() ) ); //$NON-NLS-1$
            }
        }
        lines.add( LdifSepLine.create() );

        String formattedString = ""; //$NON-NLS-1$
        for ( LdifLineBase line : lines )
        {
            formattedString += line.toFormattedString( LdifFormatParameters.DEFAULT );
        }

        log( formattedString, "SEARCH REQUEST (" + requestNum + ")", ex, connection ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public void logSearchResultEntry( Connection connection, StudioSearchResult studioSearchResult, long requestNum,
        StudioLdapException ex )
    {
        if ( !isSearchResultEntryLogEnabled() )
        {
            return;
        }

        String formattedString;
        if ( studioSearchResult != null )
        {
            Set<String> maskedAttributes = getMaskedAttributes();
            Entry entry = studioSearchResult.getEntry();

            LdifContentRecord record = new LdifContentRecord( LdifDnLine.create( entry.getDn().getName() ) );
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
            formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
        }
        else
        {
            formattedString = LdifFormatParameters.DEFAULT.getLineSeparator();
        }

        log( formattedString, "SEARCH RESULT ENTRY (" + requestNum + ")", ex, connection ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public void logSearchResultReference( Connection connection, Referral referral,
        ReferralsInfo referralsInfo, long requestNum, StudioLdapException ex )
    {
        if ( !isSearchResultEntryLogEnabled() )
        {
            return;
        }

        Collection<LdifLineBase> lines = new ArrayList<LdifLineBase>();
        lines
            .add( LdifCommentLine.create( "# reference : " + ( referral != null ? referral.getLdapUrls() : "null" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        lines.add( LdifSepLine.create() );

        String formattedString = ""; //$NON-NLS-1$
        for ( LdifLineBase line : lines )
        {
            formattedString += line.toFormattedString( LdifFormatParameters.DEFAULT );
        }
        log( formattedString, "SEARCH RESULT REFERENCE (" + requestNum + ")", ex, connection ); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * {@inheritDoc}
     */
    public void logSearchResultDone( Connection connection, long count, long requestNum, StudioLdapException ex )
    {
        if ( !isSearchRequestLogEnabled() )
        {
            return;
        }

        Collection<LdifLineBase> lines = new ArrayList<LdifLineBase>();
        lines.add( LdifCommentLine.create( "# numEntries : " + count ) ); //$NON-NLS-1$
        lines.add( LdifSepLine.create() );

        String formattedString = ""; //$NON-NLS-1$
        for ( LdifLineBase line : lines )
        {
            formattedString += line.toFormattedString( LdifFormatParameters.DEFAULT );
        }
        log( formattedString, "SEARCH RESULT DONE (" + requestNum + ")", ex, connection ); //$NON-NLS-1$ //$NON-NLS-2$
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
                initSearchLogger( connection );
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
        String logfileNamePattern = ConnectionManager.getSearchLogFileName( connection );
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
     * Checks if search request log is enabled.
     * 
     * @return true, if search request log is enabled
     */
    private boolean isSearchRequestLogEnabled()
    {
        return ConnectionCorePlugin.getDefault().isSearchRequestLogsEnabled();
    }


    /**
     * Checks if search result entry log is enabled.
     * 
     * @return true, if search result log is enabled
     */
    private boolean isSearchResultEntryLogEnabled()
    {
        return ConnectionCorePlugin.getDefault().isSearchResultEntryLogsEnabled();
    }


    /**
     * Gets the number of log files to use.
     * 
     * @return the number of log files to use
     */
    private int getFileCount()
    {
        return ConnectionCorePlugin.getDefault().getSearchLogsFileCount();
    }


    /**
     * Gets the maximum file size in kB.
     * 
     * @return the maximum file size in kB
     */
    private int getFileSizeInKb()
    {
        return ConnectionCorePlugin.getDefault().getSearchLogsFileSize();
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
