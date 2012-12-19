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
package org.apache.directory.studio.schemaeditor.model;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.registries.DefaultSchema;


/**
 * This class represents a schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Schema extends DefaultSchema
{
    /** The project */
    private Project project;

    /** The AttributeType List */
    private List<AttributeType> attributeTypes = new ArrayList<AttributeType>();

    /** The ObjectClass List */
    private List<MutableObjectClass> objectClasses = new ArrayList<MutableObjectClass>();

    /** The MatchingRule List */
    private List<MatchingRule> matchingRules = new ArrayList<MatchingRule>();

    /** The Syntax List */
    private List<LdapSyntax> syntaxes = new ArrayList<LdapSyntax>();


    /**
     * Creates a new instance of Schema.
     *
     * @param name
     *      the name of the schema
     */
    public Schema( String name )
    {
        super( name );
    }


    /**
     * Adds an AttributeType to the Schema.
     * 
     * @param at
     *      the AttributeType
     */
    public boolean addAttributeType( AttributeType at )
    {
        return attributeTypes.add( at );
    }


    /**
     * Adds a MatchingRule from the Schema.
     * 
     * @param mr
     *      the MatchingRule
     */
    public boolean addMatchingRule( MatchingRule mr )
    {
        return matchingRules.add( mr );
    }


    /**
     * Adds an ObjectClass to the Schema.
     * 
     * @param oc
     *      the ObjectClass
     */
    public boolean addObjectClass( MutableObjectClass oc )
    {
        return objectClasses.add( oc );
    }


    /**
     * Adds a Syntax from the Schema.
     * 
     * @param syntax
     *      the Syntax
     */
    public boolean addSyntax( LdapSyntax syntax )
    {
        return syntaxes.add( syntax );
    }


    /**
     * Gets the AttributeType identified by the given id.
     * 
     * @param id
     *      the name or the oid of the AttributeType
     * @return
     *      the AttributeType identified by the given id, or null if the
     * AttributeType has not been found
     */
    public AttributeType getAttributeType( String id )
    {
        for ( AttributeType at : attributeTypes )
        {
            List<String> aliases = at.getNames();
            if ( aliases != null )
            {
                for ( String alias : aliases )
                {
                    if ( alias.equalsIgnoreCase( id ) )
                    {
                        return at;
                    }
                }
            }
            if ( at.getOid().equalsIgnoreCase( id ) )
            {
                return at;
            }
        }

        return null;
    }


    /**
     * Gets all the AttributeType objects contained in the Schema.
     * 
     * @return
     *      all the AttributeType objects contained in the Schema
     */
    public List<AttributeType> getAttributeTypes()
    {
        return attributeTypes;
    }


    /**
     * Gets the MatchingRule identified by the given id.
     * 
     * @param id
     *      the name or the oid of the MatchingRule
     * @return
     *      the MatchingRule identified by the given id, or null if the
     * MatchingRule has not been found
     */
    public MatchingRule getMatchingRule( String id )
    {
        for ( MatchingRule mr : matchingRules )
        {
            List<String> aliases = mr.getNames();
            if ( aliases != null )
            {
                for ( String alias : aliases )
                {
                    if ( alias.equalsIgnoreCase( id ) )
                    {
                        return mr;
                    }
                }
            }
            if ( mr.getOid().equalsIgnoreCase( id ) )
            {
                return mr;
            }
        }

        return null;
    }


    /**
     * Gets all the MatchingRule objects contained in the Schema.
     * 
     * @return
     *      all the MatchingRule objects contained in the Schema
     */
    public List<MatchingRule> getMatchingRules()
    {
        return matchingRules;
    }


    /**
     * Gets the project of the Schema.
     * 
     * @return
     *      the project of the Schema
     */
    public Project getProject()
    {
        return project;
    }


    /**
     * Gets the ObjectClass identified by the given id.
     * 
     * @param id
     *      the name or the oid of the ObjectClass
     * @return
     *      the ObjectClass identified by the given id, or null if the
     * ObjectClass has not been found
     */
    public ObjectClass getObjectClass( String id )
    {
        for ( ObjectClass oc : objectClasses )
        {
            List<String> aliases = oc.getNames();
            if ( aliases != null )
            {
                for ( String alias : aliases )
                {
                    if ( alias.equalsIgnoreCase( id ) )
                    {
                        return oc;
                    }
                }
            }
            if ( oc.getOid().equalsIgnoreCase( id ) )
            {
                return oc;
            }
        }

        return null;
    }


    /**
     * Gets all the ObjectClass objects contained in the Schema.
     * 
     * @return
     *      all the ObjectClass objects contained in the Schema
     */
    public List<MutableObjectClass> getObjectClasses()
    {
        return objectClasses;
    }


    /**
     * Gets the Syntax identified by the given id.
     * 
     * @param id
     *      the name or the oid of the Syntax
     * @return
     *      the Syntax identified by the given id, or null if the
     * Syntax has not been found
     */
    public LdapSyntax getSyntax( String id )
    {
        for ( LdapSyntax syntax : syntaxes )
        {
            List<String> aliases = syntax.getNames();
            if ( aliases != null )
            {
                for ( String alias : aliases )
                {
                    if ( alias.equalsIgnoreCase( id ) )
                    {
                        return syntax;
                    }
                }
            }
            if ( syntax.getOid().equalsIgnoreCase( id ) )
            {
                return syntax;
            }
        }

        return null;
    }


    /**
     * Gets all the Syntax objects contained in the Schema.
     * 
     * @return
     *      all the Syntax objects contained in the Schema
     */
    public List<LdapSyntax> getSyntaxes()
    {
        return syntaxes;
    }


    /**
     * Removes an AttributeType from the Schema.
     * 
     * @param at
     *      the AttributeType
     */
    public boolean removeAttributeType( AttributeType at )
    {
        return attributeTypes.remove( at );
    }


    /**
     * Removes a MatchingRule from the Schema.
     * 
     * @param mr
     *      the MatchingRule
     */
    public boolean removeMatchingRule( MatchingRule mr )
    {
        return matchingRules.remove( mr );
    }


    /**
     * Removes an ObjectClass from the Schema.
     *
     * @param oc
     *      the ObjectClass
     */
    public boolean removeObjectClass( ObjectClass oc )
    {
        return objectClasses.remove( oc );
    }


    /**
     * Removes a Syntax from the Schema.
     * 
     * @param syntax
     *      the Syntax
     */
    public boolean removeSyntax( LdapSyntax syntax )
    {
        return syntaxes.remove( syntax );
    }


    /**
     * Sets the name of the schema.
     *
     * @param schemaName
     *      the name of the schema
     */
    public void setSchemaName( String schemaName )
    {
        this.name = schemaName;
    }


    /**
     * Sets the project of the Schema.
     * 
     * @param name
     *      the project of the schema
     */
    public void setProject( Project project )
    {
        this.project = project;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return getSchemaName();
    }
}
