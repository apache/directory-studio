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
package org.apache.directory.studio.schemaeditor.model.schemamanager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.registries.AbstractSchemaLoader;
import org.apache.directory.api.ldap.model.schema.registries.Schema;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.Project;


/**
 * Loads schema data from schema files (OpenLDAP and XML formats).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaEditorSchemaLoader extends AbstractSchemaLoader
{
    /** The currently open project */
    private Project project;


    /**
     * Creates a new instance of SchemaEditorSchemaLoader.
     *
     * @throws Exception
     */
    public SchemaEditorSchemaLoader()
    {
        initializeSchemas();
    }


    /**
     * Initialize schemas.
     */
    private void initializeSchemas()
    {
        project = Activator.getDefault().getProjectsHandler().getOpenProject();
        if ( project != null )
        {
            List<org.apache.directory.studio.schemaeditor.model.Schema> schemaObjects = project.getSchemaHandler()
                .getSchemas();
            for ( org.apache.directory.studio.schemaeditor.model.Schema schemaObject : schemaObjects )
            {
                schemaMap.put( schemaObject.getSchemaName(), schemaObject );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxCheckers( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNormalizers( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRuleList = new ArrayList<Entry>();

        if ( project != null )
        {
            for ( Schema schema : schemas )
            {
                org.apache.directory.studio.schemaeditor.model.Schema schemaHandlerSchema = project.getSchemaHandler()
                    .getSchema( schema.getSchemaName() );

                if ( schemaHandlerSchema != null )
                {
                    List<MatchingRule> matchingRules = schemaHandlerSchema.getMatchingRules();

                    for ( MatchingRule matchingRule : matchingRules )
                    {
                        matchingRuleList.add( SchemaEditorSchemaLoaderUtils.toEntry( matchingRule ) );
                    }
                }
            }
        }

        return matchingRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxList = new ArrayList<Entry>();

        if ( project != null )
        {
            for ( Schema schema : schemas )
            {
                org.apache.directory.studio.schemaeditor.model.Schema schemaHandlerSchema = project.getSchemaHandler()
                    .getSchema( schema.getSchemaName() );

                if ( schemaHandlerSchema != null )
                {
                    List<LdapSyntax> syntaxes = schemaHandlerSchema.getSyntaxes();

                    for ( LdapSyntax syntax : syntaxes )
                    {
                        syntaxList.add( SchemaEditorSchemaLoaderUtils.toEntry( syntax ) );
                    }
                }
            }
        }

        return syntaxList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> attributeTypeList = new ArrayList<Entry>();

        if ( project != null )
        {
            for ( Schema schema : schemas )
            {
                org.apache.directory.studio.schemaeditor.model.Schema schemaHandlerSchema = project.getSchemaHandler()
                    .getSchema( schema.getSchemaName() );

                if ( schemaHandlerSchema != null )
                {
                    List<AttributeType> attributeTypes = schemaHandlerSchema.getAttributeTypes();

                    for ( AttributeType attributeType : attributeTypes )
                    {
                        attributeTypeList.add( SchemaEditorSchemaLoaderUtils.toEntry( attributeType ) );
                    }
                }
            }
        }

        return attributeTypeList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRuleUses( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNameForms( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitContentRules( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitStructureRules( Schema... schemas ) throws LdapException, IOException
    {
        return new ArrayList<Entry>();
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> objectClassList = new ArrayList<Entry>();

        if ( project != null )
        {
            for ( Schema schema : schemas )
            {
                org.apache.directory.studio.schemaeditor.model.Schema schemaHandlerSchema = project.getSchemaHandler()
                    .getSchema( schema.getSchemaName() );

                if ( schemaHandlerSchema != null )
                {
                    List<MutableObjectClass> objectClasses = schemaHandlerSchema.getObjectClasses();

                    for ( ObjectClass objectClass : objectClasses )
                    {
                        objectClassList.add( SchemaEditorSchemaLoaderUtils.toEntry( objectClass ) );
                    }
                }
            }
        }

        return objectClassList;
    }
}
