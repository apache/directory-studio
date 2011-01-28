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

import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.schema.registries.AbstractSchemaLoader;
import org.apache.directory.shared.ldap.model.schema.registries.Schema;


/**
 * Loads schema data from schema files (OpenLDAP and XML formats).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaEditorSchemaLoader extends AbstractSchemaLoader
{
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
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadComparators( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> comparatorList = new ArrayList<Entry>();

        return comparatorList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxCheckers( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxCheckerList = new ArrayList<Entry>();

        return syntaxCheckerList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNormalizers( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> normalizerList = new ArrayList<Entry>();

        return normalizerList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRuleList = new ArrayList<Entry>();

        return matchingRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadSyntaxes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> syntaxList = new ArrayList<Entry>();

        return syntaxList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadAttributeTypes( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> attributeTypeList = new ArrayList<Entry>();

        return attributeTypeList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadMatchingRuleUses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> matchingRuleUseList = new ArrayList<Entry>();

        return matchingRuleUseList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadNameForms( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> nameFormList = new ArrayList<Entry>();

        return nameFormList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitContentRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> ditContentRuleList = new ArrayList<Entry>();

        return ditContentRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadDitStructureRules( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> ditStructureRuleList = new ArrayList<Entry>();

        return ditStructureRuleList;
    }


    /**
     * {@inheritDoc}
     */
    public List<Entry> loadObjectClasses( Schema... schemas ) throws LdapException, IOException
    {
        List<Entry> objectClassList = new ArrayList<Entry>();

        return objectClassList;
    }
}
