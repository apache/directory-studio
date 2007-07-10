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
package org.apache.directory.studio.apacheds.schemaeditor.model.io;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;

import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.apache.directory.server.core.tools.schema.OpenLdapSchemaParser;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;
import org.apache.directory.studio.apacheds.schemaeditor.model.SchemaImpl;


/**
 * This class is used to import a Schema file in the OpenLDAP Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class OpenLdapSchemaFileImporter
{
    /**
     * Extracts the Schema from the given path.
     *
     * @param path
     *      the path of the file.
     * @return
     *      the corresponding schema
     * @throws OpenLdapSchemaFileImportException
     *      if an error occurrs when importing the schema
     */
    public static Schema getSchema( String path ) throws OpenLdapSchemaFileImportException
    {
        File file = new File( path );

        // Checking the file properties
        if ( !file.exists() )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' does not exist." );
        }
        else if ( !file.canRead() )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' can not be read." );
        }

        InputStream in = null;
        try
        {
            in = file.toURL().openStream();
        }
        catch ( MalformedURLException e )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
        }
        catch ( IOException e )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
        }

        OpenLdapSchemaParser parser = null;
        try
        {
            parser = new OpenLdapSchemaParser();
        }
        catch ( IOException e )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
        }

        try
        {
            parser.parse( in );
        }
        catch ( IOException e )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
        }
        catch ( ParseException e )
        {
            throw new OpenLdapSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
        }

        String schemaName = getNameFromPath( path );

        Schema schema = new SchemaImpl( schemaName );

        List<?> ats = parser.getAttributeTypes();
        for ( int i = 0; i < ats.size(); i++ )
        {
            AttributeTypeImpl at = convertAttributeType( ( AttributeTypeLiteral ) ats.get( i ) );
            at.setSchema( schemaName );
        }

        List<?> ocs = parser.getObjectClassTypes();
        for ( int i = 0; i < ats.size(); i++ )
        {
            ObjectClassImpl oc = convertObjectClass( ( ObjectClassLiteral ) ocs.get( i ) );
            oc.setSchema( schemaName );
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
        String separator = File.separator;

        String[] splFileName = path.split( separator );
        String fileNoPath = splFileName[splFileName.length - 1];

        if ( fileNoPath.endsWith( ".schema" ) ) //$NON-NLS-1$
        {
            String[] fileName = fileNoPath.split( "\\." ); //$NON-NLS-1$
            return fileName[0];
        }

        return fileNoPath;
    }


    /**
     * Convert the given AttributeTypeLiteral into its AttributeTypeImpl representation.
     *
     * @param at
     *      the AttributeTypeLiteral
     * @return
     *      the corresponding AttributeTypeImpl
     */
    private static final AttributeTypeImpl convertAttributeType( AttributeTypeLiteral at )
    {
        AttributeTypeImpl newAT = new AttributeTypeImpl( at.getOid() );
        newAT.setNames( at.getNames() );
        newAT.setDescription( newAT.getDescription() );
        newAT.setSuperiorName( at.getSuperior() );
        newAT.setUsage( at.getUsage() );
        newAT.setSyntaxOid( at.getSyntax() );
        newAT.setLength( at.getLength() );
        newAT.setObsolete( at.isObsolete() );
        newAT.setSingleValue( at.isSingleValue() );
        newAT.setCollective( at.isCollective() );
        newAT.setCanUserModify( !at.isNoUserModification() );
        newAT.setEqualityName( at.getEquality() );
        newAT.setOrderingName( at.getOrdering() );
        newAT.setSubstrName( at.getSubstr() );

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
    private static final ObjectClassImpl convertObjectClass( ObjectClassLiteral oc )
    {
        ObjectClassImpl newOC = new ObjectClassImpl( oc.getOid() );
        newOC.setNames( oc.getNames() );
        newOC.setDescription( oc.getDescription() );
        newOC.setSuperClassesNames( oc.getSuperiors() );
        newOC.setType( oc.getClassType() );
        newOC.setObsolete( oc.isObsolete() );
        newOC.setMustNamesList( oc.getMust() );
        newOC.setMayNamesList( oc.getMay() );

        return newOC;
    }
}
