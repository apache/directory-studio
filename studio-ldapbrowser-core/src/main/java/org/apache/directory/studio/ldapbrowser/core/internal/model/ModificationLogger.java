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

package org.apache.directory.studio.ldapbrowser.core.internal.model;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.Control;

import org.apache.directory.studio.connection.core.IModificationLogger;
import org.apache.directory.studio.ldapbrowser.core.BrowserConnectionManager;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.NameException;
import org.apache.directory.studio.ldapbrowser.core.model.RDN;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeAddRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeDeleteRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeModDnRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifModSpec;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifCommentLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifDeloldrdnLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifNewrdnLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifNewsuperiorLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifSepLine;


/**
 * The ModificationLogger is used to log modifications into a file.
 *
 * TODO: LDIF of DSML logging
 * TODO: switch off logging
 * TODO: log controls
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModificationLogger implements IModificationLogger
{

    /** The browser connection. */
    private BrowserConnection browserConnection;

    /** The file handler. */
    private FileHandler fileHandler;

    /** The logger. */
    private Logger logger;


    /**
     * Creates a new instance of ModificationLogger.
     * 
     * @param browserConnection the browser connection
     */
    public ModificationLogger( BrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;
    }


    /**
     * Inits the modification logger.
     */
    private void initModificationLogger()
    {
        this.logger = Logger.getAnonymousLogger();
        this.logger.setLevel( Level.ALL );

        String logfileName = BrowserConnectionManager.getModificationLogFileName( browserConnection );
        try
        {
            fileHandler = new FileHandler( logfileName, 100000, 10, true );
            fileHandler.setFormatter( new Formatter()
            {
                public String format( LogRecord record )
                {
                    return record.getMessage();
                }
            } );
            this.logger.addHandler( fileHandler );
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
     * Disposes the modification logger.
     */
    public void dispose()
    {
        if ( this.logger != null )
        {
            Handler[] handlers = this.logger.getHandlers();
            for ( int i = 0; i < handlers.length; i++ )
            {
                handlers[i].close();
            }

            this.logger = null;
        }
    }


    /**
     * Logs the given text.
     * 
     * @param text the text to log
     * @param ex the naming exception if an error occurred, null otherwise
     */
    private void log( String text, NamingException ex )
    {
        if ( logger == null )
        {
            if ( browserConnection.getConnection().getName() != null )
            {
                initModificationLogger();
            }
        }

        if ( logger != null )
        {
            DateFormat df = new SimpleDateFormat( BrowserCoreConstants.DATEFORMAT );

            if ( ex != null )
            {
                logger.log( Level.ALL, LdifCommentLine.create( "#!RESULT ERROR" ).toFormattedString() ); //$NON-NLS-1$
            }
            else
            {
                logger.log( Level.ALL, LdifCommentLine.create( "#!RESULT OK" ).toFormattedString() ); //$NON-NLS-1$
            }

            logger
                .log(
                    Level.ALL,
                    LdifCommentLine
                        .create(
                            "#!CONNECTION ldap://" + browserConnection.getConnection().getHost() + ":" + browserConnection.getConnection().getPort() ).toFormattedString() ); //$NON-NLS-1$ //$NON-NLS-2$
            logger.log( Level.ALL, LdifCommentLine.create( "#!DATE " + df.format( new Date() ) ).toFormattedString() ); //$NON-NLS-1$

            if ( ex != null )
            {
                String errorComment = "#!ERROR " + ex.getMessage(); //$NON-NLS-1$
                errorComment = errorComment.replaceAll( "\r", " " ); //$NON-NLS-1$ //$NON-NLS-2$
                errorComment = errorComment.replaceAll( "\n", " " ); //$NON-NLS-1$ //$NON-NLS-2$
                LdifCommentLine errorCommentLine = LdifCommentLine.create( errorComment );
                logger.log( Level.ALL, errorCommentLine.toFormattedString() );
            }

            logger.log( Level.ALL, text );
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeAdd(java.lang.String, javax.naming.directory.Attributes, javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeAdd( final String dn, final Attributes attributes, final Control[] controls,
        NamingException ex )
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

            String formattedString = record.toFormattedString();
            log( formattedString, ex );
        }
        catch ( NamingException e )
        {
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeDelete(java.lang.String, javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeDelete( final String dn, final Control[] controls, NamingException ex )
    {
        LdifChangeDeleteRecord record = LdifChangeDeleteRecord.create( dn );
        //record.addControl( controlLine );
        record.finish( LdifSepLine.create() );

        String formattedString = record.toFormattedString();
        log( formattedString, ex );
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeModify(java.lang.String, javax.naming.directory.ModificationItem[], javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeModify( final String dn, final ModificationItem[] modificationItems,
        final Control[] controls, NamingException ex )
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

            String formattedString = record.toFormattedString();
            log( formattedString, ex );
        }
        catch ( NamingException e )
        {
        }
    }


    /**
     * @see org.apache.directory.studio.connection.core.IModificationLogger#logChangetypeModDn(java.lang.String, java.lang.String, boolean, javax.naming.ldap.Control[], javax.naming.NamingException)
     */
    public void logChangetypeModDn( final String oldDn, final String newDn, final boolean deleteOldRdn,
        final Control[] controls, NamingException ex )
    {
        try
        {
            DN dn = new DN( newDn );
            RDN newrdn = dn.getRdn();
            DN newsuperior = dn.getParentDn();

            LdifChangeModDnRecord record = LdifChangeModDnRecord.create( oldDn );
            //record.addControl( controlLine );
            record.setNewrdn( LdifNewrdnLine.create( newrdn.toString() ) );
            record.setDeloldrdn( deleteOldRdn ? LdifDeloldrdnLine.create1() : LdifDeloldrdnLine.create0() );
            record.setNewsuperior( LdifNewsuperiorLine.create( newsuperior.toString() ) );
            record.finish( LdifSepLine.create() );

            String formattedString = record.toFormattedString();
            log( formattedString, ex );
        }
        catch ( NameException e )
        {
        }
    }


    /**
     * Gets the files.
     * 
     * @return the files
     */
    public File[] getFiles()
    {
        if ( this.logger == null )
        {
            if ( browserConnection.getConnection().getName() != null )
            {
                this.initModificationLogger();
            }
        }

        try
        {
            return getLogFiles( this.fileHandler );
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

}
