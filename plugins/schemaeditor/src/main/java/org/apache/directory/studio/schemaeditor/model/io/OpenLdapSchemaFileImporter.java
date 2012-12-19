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
package org.apache.directory.studio.schemaeditor.model.io;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.parsers.OpenLdapSchemaParser;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to import a Schema file in the OpenLDAP Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapSchemaFileImporter
{
    /**
     * Extracts the Schema from the given path.
     *
     * @param inputStream
     *      the {@link InputStream} of the file.
     * @param path
     *      the path of the file.
     * @return
     *      the corresponding schema
     * @throws OpenLdapSchemaFileImportException
     *      if an error occurrs when importing the schema
     */
    public static Schema getSchema( InputStream inputStream, String path ) throws OpenLdapSchemaFileImportException
    {
        OpenLdapSchemaParser parser = null;
        try
        {
            parser = new OpenLdapSchemaParser();
        }
        catch ( IOException e )
        {
            throw new OpenLdapSchemaFileImportException( NLS.bind( Messages
                .getString( "OpenLdapSchemaFileImporter.NotReadCorrectly" ), new String[] { path } ), e ); //$NON-NLS-1$
        }

        try
        {
            parser.parse( inputStream );
        }
        catch ( IOException e )
        {
            throw new OpenLdapSchemaFileImportException( NLS.bind( Messages
                .getString( "OpenLdapSchemaFileImporter.NotReadCorrectly" ), new String[] { path } ), e ); //$NON-NLS-1$
        }
        catch ( ParseException e )
        {
            ExceptionMessage exceptionMessage = parseExceptionMessage( e.getMessage() );
            throw new OpenLdapSchemaFileImportException( NLS.bind( Messages
                .getString( "OpenLdapSchemaFileImporter.NotReadCorrectly" ), new String[] //$NON-NLS-1$
                { path } )
                + ( exceptionMessage == null ? "" : NLS.bind( Messages //$NON-NLS-1$
                    .getString( "OpenLdapSchemaFileImporter.ErrorMessage" ), new String[] //$NON-NLS-1$
                    { exceptionMessage.lineNumber, exceptionMessage.columnNumber, exceptionMessage.cause } ) ), e );
        }

        String schemaName = getNameFromPath( path );

        Schema schema = new Schema( schemaName );

        List<?> ats = parser.getAttributeTypes();
        for ( int i = 0; i < ats.size(); i++ )
        {
            AttributeType at = convertAttributeType( ( AttributeType ) ats.get( i ) );
            at.setSchemaName( schemaName );
            schema.addAttributeType( at );
        }

        List<?> ocs = parser.getObjectClassTypes();
        for ( int i = 0; i < ocs.size(); i++ )
        {
            MutableObjectClass oc = convertObjectClass( ( ObjectClass ) ocs.get( i ) );
            oc.setSchemaName( schemaName );
            schema.addObjectClass( oc );
        }

        return schema;
    }


    /**
     * Gets the name of the file.
     *
     * @param path
     *      the path
     * @return
     *      the name of the file.
     */
    private static final String getNameFromPath( String path )
    {
        File file = new File( path );
        String fileName = file.getName();
        if ( fileName.endsWith( ".schema" ) ) //$NON-NLS-1$
        {
            String[] fileNameSplitted = fileName.split( "\\." ); //$NON-NLS-1$
            return fileNameSplitted[0];
        }

        return fileName;
    }


    /**
     * Convert the given AttributeTypeLiteral into its AttributeTypeImpl representation.
     *
     * @param at
     *      the AttributeTypeLiteral
     * @return
     *      the corresponding AttributeTypeImpl
     */
    private static final AttributeType convertAttributeType( AttributeType at )
    {
        MutableAttributeType newAT = new MutableAttributeType( at.getOid() );
        newAT.setNames( at.getNames() );
        newAT.setDescription( at.getDescription() );
        newAT.setSuperiorOid( at.getSuperiorOid() );
        newAT.setUsage( at.getUsage() );
        newAT.setSyntaxOid( at.getSyntaxOid() );
        newAT.setSyntaxLength( at.getSyntaxLength() );
        newAT.setObsolete( at.isObsolete() );
        newAT.setSingleValued( at.isSingleValued() );
        newAT.setCollective( at.isCollective() );
        newAT.setUserModifiable( at.isUserModifiable() );
        newAT.setEqualityOid( at.getEqualityOid() );
        newAT.setOrderingOid( at.getOrderingOid() );
        newAT.setSubstringOid( at.getSubstringOid() );

        return newAT;
    }


    /**
     * Convert the given ObjectClassLiteral into its ObjectClassImpl representation.
     *
     * @param oc
     *      the ObjectClassLiteral
     * @return
     *      the corresponding ObjectClassImpl
     */
    private static final MutableObjectClass convertObjectClass( ObjectClass oc )
    {
        MutableObjectClass newOC = new MutableObjectClass( oc.getOid() );
        newOC.setNames( oc.getNames() );
        newOC.setDescription( oc.getDescription() );
        newOC.setSuperiorOids( oc.getSuperiorOids() );
        newOC.setType( oc.getType() );
        newOC.setObsolete( oc.isObsolete() );
        newOC.setMustAttributeTypeOids( oc.getMustAttributeTypeOids() );
        newOC.setMayAttributeTypeOids( oc.getMayAttributeTypeOids() );

        return newOC;
    }


    /**
     * Parses the exception message and fills an {@link ExceptionMessage}.
     *
     * @param message
     *      the exception message to parse
     * @return
     *      the corresponding {@link ExceptionMessage}, or <code>null</code>
     */
    private static ExceptionMessage parseExceptionMessage( String message )
    {
        Scanner scanner = new Scanner( new ByteArrayInputStream( message.getBytes() ) );
        String foundString = scanner.findWithinHorizon( ".*line (\\d+):(\\d+): *([^\\n]*).*", message.length() ); //$NON-NLS-1$
        if ( foundString != null )
        {
            MatchResult result = scanner.match();
            if ( result.groupCount() == 3 )
            {
                ExceptionMessage exceptionMessage = new ExceptionMessage();
                exceptionMessage.lineNumber = result.group( 1 );
                exceptionMessage.columnNumber = result.group( 2 );
                exceptionMessage.cause = result.group( 3 );

                scanner.close();
                return exceptionMessage;
            }

        }

        scanner.close();
        return null;
    }

    private static class ExceptionMessage
    {
        String lineNumber;
        String columnNumber;
        String cause;
    }
}
