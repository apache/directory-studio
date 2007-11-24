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

package org.apache.directory.studio.connection.core.io.jndi;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;

import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCoreConstants;
import org.apache.directory.studio.connection.core.ConnectionManager;
import org.apache.directory.studio.connection.core.DnUtils;
import org.apache.directory.studio.connection.core.IJndiLogger;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifNewsuperiorLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;


/**
 * The ModificationLogger is used to log modifications in LDIF format into a file.
 *
 * TODO: log controls
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifModificationLogger implements IJndiLogger
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
     * Creates a new instance of ModificationLogger.
     */
    public LdifModificationLogger()
    {
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
            FileHandler fileHandler = new FileHandler( logfileName, 100000, 10, true );
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


    /**
     * Logs the given text to the modification logger of the given connection.
     * 
     * @param text the text to log
     * @param ex the naming exception if an error occurred, null otherwise
     * @param connection the connection
     */
    private void log( String text, NamingException ex, Connection connection )
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
            Logger logger = loggers.get( id );
            DateFormat df = new SimpleDateFormat( ConnectionCoreConstants.DATEFORMAT );

            if ( ex != null )
            {
                logger.log( Level.ALL, LdifCommentLine
                    .create( "#!RESULT ERROR" ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$
            }
            else
            {
                logger.log( Level.ALL, LdifCommentLine
                    .create( "#!RESULT OK" ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$
            }

            logger
                .log(
                    Level.ALL,
                    LdifCommentLine
                        .create( "#!CONNECTION ldap://" + connection.getHost() + ":" + connection.getPort() ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$ //$NON-NLS-2$
            logger.log( Level.ALL, LdifCommentLine
                .create( "#!DATE " + df.format( new Date() ) ).toFormattedString( LdifFormatParameters.DEFAULT ) ); //$NON-NLS-1$

            if ( ex != null )
            {
                String errorComment = "#!ERROR " + ex.getMessage(); //$NON-NLS-1$
                errorComment = errorComment.replaceAll( "\r", " " ); //$NON-NLS-1$ //$NON-NLS-2$
                errorComment = errorComment.replaceAll( "\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
                LdifCommentLine errorCommentLine = LdifCommentLine.create( errorComment );
                logger.log( Level.ALL, errorCommentLine.toFormattedString( LdifFormatParameters.DEFAULT ) );
            }

            logger.log( Level.ALL, text );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeAdd(java.lang.String, javax.naming.directory.Attributes, javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeAdd( Connection connection, final String dn, final Attributes attributes,
        final Control[] controls, NamingException ex )
    {
        try
        {
            LdifChangeAddRecord record = LdifChangeAddRecord.create( dn );
            //record.addControl( controlLine );
            NamingEnumeration<? extends Attribute> attributeEnumeration = attributes.getAll();
            while ( attributeEnumeration.hasMore() )
            {
                Attribute attribute = attributeEnumeration.next();
                String attributeName = attribute.getID();
                NamingEnumeration<?> valueEnumeration = attribute.getAll();
                while ( valueEnumeration.hasMore() )
                {
                    Object o = valueEnumeration.next();
                    if ( o instanceof String )
                    {
                        record.addAttrVal( LdifAttrValLine.create( attributeName, ( String ) o ) );
                    }
                    if ( o instanceof byte[] )
                    {
                        record.addAttrVal( LdifAttrValLine.create( attributeName, ( byte[] ) o ) );
                    }
                }
            }
            record.finish( LdifSepLine.create() );

            String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
            log( formattedString, ex, connection );
        }
        catch ( NamingException e )
        {
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeDelete(java.lang.String, javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeDelete( Connection connection, final String dn, final Control[] controls,
        NamingException ex )
    {
        LdifChangeDeleteRecord record = LdifChangeDeleteRecord.create( dn );
        //record.addControl( controlLine );
        record.finish( LdifSepLine.create() );

        String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
        log( formattedString, ex, connection );
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeModify(java.lang.String, javax.naming.directory.ModificationItem[], javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeModify( Connection connection, final String dn,
        final ModificationItem[] modificationItems, final Control[] controls, NamingException ex )
    {
        try
        {
            LdifChangeModifyRecord record = LdifChangeModifyRecord.create( dn );
            //record.addControl( controlLine );
            for ( ModificationItem item : modificationItems )
            {
                Attribute attribute = item.getAttribute();
                String attributeDescription = attribute.getID();
                LdifModSpec modSpec;
                switch ( item.getModificationOp() )
                {
                    case DirContext.ADD_ATTRIBUTE:
                        modSpec = LdifModSpec.createAdd( attributeDescription );
                        break;
                    case DirContext.REMOVE_ATTRIBUTE:
                        modSpec = LdifModSpec.createDelete( attributeDescription );
                        break;
                    case DirContext.REPLACE_ATTRIBUTE:
                        modSpec = LdifModSpec.createReplace( attributeDescription );
                        break;
                    default:
                        continue;
                }
                NamingEnumeration<?> valueEnumeration = attribute.getAll();
                while ( valueEnumeration.hasMore() )
                {
                    Object o = valueEnumeration.next();
                    if ( o instanceof String )
                    {
                        modSpec.addAttrVal( LdifAttrValLine.create( attributeDescription, ( String ) o ) );
                    }
                    if ( o instanceof byte[] )
                    {
                        modSpec.addAttrVal( LdifAttrValLine.create( attributeDescription, ( byte[] ) o ) );
                    }
                }
                modSpec.finish( LdifModSpecSepLine.create() );

                record.addModSpec( modSpec );
            }
            record.finish( LdifSepLine.create() );

            String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
            log( formattedString, ex, connection );
        }
        catch ( NamingException e )
        {
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeModDn(java.lang.String, java.lang.String, boolean, javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeModDn( Connection connection, final String oldDn, final String newDn,
        final boolean deleteOldRdn, final Control[] controls, NamingException ex )
    {
        try
        {
            LdapDN dn = new LdapDN( newDn );
            Rdn newrdn = dn.getRdn();
            LdapDN newsuperior = DnUtils.getParent( dn );

            LdifChangeModDnRecord record = LdifChangeModDnRecord.create( oldDn );
            //record.addControl( controlLine );
            record.setNewrdn( LdifNewrdnLine.create( newrdn.getUpName() ) );
            record.setDeloldrdn( deleteOldRdn ? LdifDeloldrdnLine.create1() : LdifDeloldrdnLine.create0() );
            record.setNewsuperior( LdifNewsuperiorLine.create( newsuperior.getUpName() ) );
            record.finish( LdifSepLine.create() );

            String formattedString = record.toFormattedString( LdifFormatParameters.DEFAULT );
            log( formattedString, ex, connection );
        }
        catch ( InvalidNameException e )
        {
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
            return getLogFiles( fileHandlers.get( id ) );
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
     * 
     * @throws Exception the exception
     */
    private static File[] getLogFiles( FileHandler fileHandler ) throws Exception
    {
        Field field = getFieldFromClass( "java.util.logging.FileHandler", "files" ); //$NON-NLS-1$ //$NON-NLS-2$
        field.setAccessible( true );
        File[] files = ( File[] ) field.get( fileHandler );
        return files;
    }


    /**
     * Gets the field from class.
     * 
     * @param className the class name
     * @param fieldName the field name
     * 
     * @return the field from class
     * 
     * @throws Exception the exception
     */
    private static Field getFieldFromClass( String className, String fieldName ) throws Exception
    {
        Class<?> clazz = Class.forName( className );
        Field[] fields = clazz.getDeclaredFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            if ( fields[i].getName().equals( fieldName ) )
                return fields[i];
        }
        return null;
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
