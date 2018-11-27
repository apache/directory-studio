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


import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaObjectRenderer;
import org.apache.directory.api.ldap.model.schema.SchemaObjectSorter;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This class is used to export a Schema file into the OpenLDAP Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapSchemaFileExporter
{
    /**
     * Converts the given schema to its source code representation
     * in OpenLDAP Schema file format.
     *
     * @param schema
     *      the schema to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toSourceCode( Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        for ( AttributeType at : SchemaObjectSorter.hierarchicalOrdered( schema.getAttributeTypes() ) )
        {
            sb.append( toSourceCode( at ) );
            sb.append( "\n" ); //$NON-NLS-1$
        }

        for ( ObjectClass oc : SchemaObjectSorter.sortObjectClasses( schema.getObjectClasses() ) )
        {
            sb.append( toSourceCode( oc ) );
            sb.append( "\n" ); //$NON-NLS-1$
        }

        return sb.toString();
    }


    /**
     * Converts the given attribute type to its source code representation
     * in OpenLDAP Schema file format.
     *
     * @param at
     *      the attribute type to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toSourceCode( AttributeType at )
    {
        return SchemaObjectRenderer.OPEN_LDAP_SCHEMA_RENDERER.render( at );
    }


    /**
     * Converts the given object class to its source code representation
     * in OpenLDAP Schema file format.
     *
     * @param at
     *      the object class to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toSourceCode( ObjectClass oc )
    {
        return SchemaObjectRenderer.OPEN_LDAP_SCHEMA_RENDERER.render( oc );
    }
}
