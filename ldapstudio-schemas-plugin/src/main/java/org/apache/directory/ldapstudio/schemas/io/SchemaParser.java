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

package org.apache.directory.ldapstudio.schemas.io;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;

import org.apache.directory.ldapstudio.schemas.Messages;
import org.apache.directory.server.core.tools.schema.AttributeTypeLiteral;
import org.apache.directory.server.core.tools.schema.ObjectClassLiteral;
import org.apache.directory.server.core.tools.schema.OpenLdapSchemaParser;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;


/**
 * This class is a schema-parser factory.
 * You should use one of the static generation methods to get a parser. 
 *
 */
public class SchemaParser
{

    private static Logger logger = Logger.getLogger( SchemaParser.class );
    /******************************************
     *               Fields                   *
     ******************************************/

    private URL fileURL = null;
    private AttributeTypeLiteral[] attributeTypes = null;
    private ObjectClassLiteral[] objectClasses = null;


    /******************************************
     *               Accessors                *
     ******************************************/

    /**
     * Use this method to access generated attribute types
     * @return the attribute types stored in an array
     */
    public AttributeTypeLiteral[] getAttributeTypes()
    {
        return attributeTypes;
    }


    /**
     * Use this method to access generated object classes
     * @return the object classes stored in an array
     */
    public ObjectClassLiteral[] getObjectClasses()
    {
        return objectClasses;
    }


    private void setAttributeTypes( AttributeTypeLiteral[] attributeTypes )
    {
        this.attributeTypes = attributeTypes;
    }


    private void setObjectClasses( ObjectClassLiteral[] objectClasses )
    {
        this.objectClasses = objectClasses;
    }


    /******************************************
     *              Initialization            *
     ******************************************/

    private SchemaParser( URL url )
    {
        this.fileURL = url;
    }


    /**
     * Use this method to obtain a schema parser from an URL.
     * @param url the location of the schema that will be parsed.
     * @return the schema parser
     */
    public static SchemaParser parserFromURL( URL url )
    {
        return new SchemaParser( url );

    }


    /******************************************
     *                 Logic                  *
     ******************************************/

    /**
     * Launch schema parsing
     * @throws IOException if error opening the .schema file
     * @throws ParseException if error during parsing of the .schema file
     */
    public void parse() throws IOException, ParseException
    {
        InputStream in = null;
        in = fileURL.openStream();

        if ( in == null )
            throw new FileNotFoundException( Messages.getString( "SchemaParser.No_path_or_url_specified" ) ); //$NON-NLS-1$

        OpenLdapSchemaParser parser = new OpenLdapSchemaParser();
        try
        {
            parser.parse( in );
        }
        catch ( ParseException e )
        {
            logger.error( "An error occured when parsing the file - " + e.getMessage() ); //$NON-NLS-1$ 
            MessageBox messageBox = new MessageBox( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                SWT.OK | SWT.ICON_ERROR );
            messageBox
                .setMessage( Messages.getString( "SchemaParser.An_error_has_occurred_when_parsing_the_file" ) + fileURL.toString() + Messages.getString( "SchemaParser.The_schema_cannot_be_opened" ) + " " + "See log file for debug information about the schema." ); //$NON-NLS-1$ //$NON-NLS-2$
            messageBox.open();
            throw e;
        }

        generateAttributeTypes( parser );
        generateObjectClasses( parser );
    }


    /**
     * Generate all attributeTypes from schema
     * @param parser the schema parser
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    private void generateAttributeTypes( OpenLdapSchemaParser parser )
    {
        int size = parser.getAttributeTypes().size();
        setAttributeTypes( new AttributeTypeLiteral[size] );
        setAttributeTypes( ( AttributeTypeLiteral[] ) parser.getAttributeTypes().toArray( getAttributeTypes() ) );
    }


    /**
     * Generate all objectClasses from schema
     * @param parser the schema parser 
     */
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    private void generateObjectClasses( OpenLdapSchemaParser parser )
    {
        int size = parser.getObjectClassTypes().size();
        setObjectClasses( new ObjectClassLiteral[size] );
        setObjectClasses( ( ObjectClassLiteral[] ) parser.getObjectClassTypes().toArray( getObjectClasses() ) );
    }
}
