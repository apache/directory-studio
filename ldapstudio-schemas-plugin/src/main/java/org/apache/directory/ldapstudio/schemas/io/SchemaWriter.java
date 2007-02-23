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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Date;

import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.core.runtime.Platform;


/**
 * This class is a LDAP schema generator
 *
 */
public class SchemaWriter
{
    private VelocityEngine ve;


    public SchemaWriter()
    {
        ve = new VelocityEngine();
    }


    /**
     * Launch schema generation
     * @param schema the schema
     * @param src the file-path where it will be generated
     * @throws Exception if an exception during the generation of the schema
     */
    @SuppressWarnings("deprecation")//$NON-NLS-1$
    public void write( Schema schema, String src ) throws Exception
    {
        // VelocityEngine initialization
        ve.init( new java.util.Properties() );

        // VelocityContext initialization and variables setup
        VelocityContext context = new VelocityContext();
        context.put( "schemaName", schema.getName() ); //$NON-NLS-1$
        Date date = new Date();
        context.put( "date", date.toLocaleString() ); //$NON-NLS-1$
        context.put( "attributeTypes", schema.getAttributeTypesAsArray() ); //$NON-NLS-1$
        context.put( "objectClasses", schema.getObjectClassesAsArray() ); //$NON-NLS-1$

        // Schema generation
        URL template = Platform.getBundle( Activator.PLUGIN_ID ).getResource( "ressources/templates/Schema.template" ); //$NON-NLS-1$
        Reader fileIn = new BufferedReader( new InputStreamReader( template.openStream() ) );
        Writer writer = new FileWriter( new File( src ) );
        ve.evaluate( context, writer, "LOG", fileIn ); //$NON-NLS-1$

        writer.flush();
        writer.close();
    }
}
