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

import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;


/**
 * This class represents a schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaImpl implements Schema
{
    /** The name */
    private String name;

    /** The project */
    private Project project;

    /** The AttributeType List */
    private List<AttributeType> attributeTypes = new ArrayList<AttributeType>();

    /** The ObjectClass List */
    private List<ObjectClass> objectClasses = new ArrayList<ObjectClass>();

    /** The MatchingRule List */
    private List<MatchingRule> matchingRules = new ArrayList<MatchingRule>();

    /** The Syntax List */
    private List<LdapSyntax> syntaxes = new ArrayList<LdapSyntax>();


    /**
     * Creates a new instance of SchemaImpl.
     *
     * @param name
     *      the name of the schema
     */
    public SchemaImpl( String name )
    {
        this.name = name;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#addAttributeType(org.apache.directory.shared.ldap.schema.AttributeType)
     */
    public boolean addAttributeType( AttributeType at )
    {
        return attributeTypes.add( at );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#addMatchingRule(org.apache.directory.shared.ldap.schema.MatchingRule)
     */
    public boolean addMatchingRule( MatchingRule mr )
    {
        return matchingRules.add( mr );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#addObjectClass(org.apache.directory.shared.ldap.schema.ObjectClass)
     */
    public boolean addObjectClass( ObjectClass oc )
    {
        return objectClasses.add( oc );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#addSyntax(org.apache.directory.shared.ldap.schema.Syntax)
     */
    public boolean addSyntax( LdapSyntax syntax )
    {
        return syntaxes.add( syntax );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getAttributeType(java.lang.String)
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


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getAttributeTypes()
     */
    public List<AttributeType> getAttributeTypes()
    {
        return attributeTypes;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getMatchingRule(java.lang.String)
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


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getMatchingRules()
     */
    public List<MatchingRule> getMatchingRules()
    {
        return matchingRules;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getName()
     */
    public String getName()
    {
        return name;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getProject()
     */
    public Project getProject()
    {
        return project;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getObjectClass(java.lang.String)
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


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getObjectClasses()
     */
    public List<ObjectClass> getObjectClasses()
    {
        return objectClasses;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getSyntax(java.lang.String)
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


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#getSyntaxes()
     */
    public List<LdapSyntax> getSyntaxes()
    {
        return syntaxes;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#removeAttributeType(org.apache.directory.shared.ldap.schema.AttributeType)
     */
    public boolean removeAttributeType( AttributeType at )
    {
        return attributeTypes.remove( at );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#removeMatchingRule(org.apache.directory.shared.ldap.schema.MatchingRule)
     */
    public boolean removeMatchingRule( MatchingRule mr )
    {
        return matchingRules.remove( mr );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#removeObjectClass(org.apache.directory.shared.ldap.schema.ObjectClass)
     */
    public boolean removeObjectClass( ObjectClass oc )
    {
        return objectClasses.remove( oc );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#removeSyntax(org.apache.directory.shared.ldap.schema.Syntax)
     */
    public boolean removeSyntax( LdapSyntax syntax )
    {
        return syntaxes.remove( syntax );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#setName(java.lang.String)
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.model.Schema#setProject(org.apache.directory.studio.schemaeditor.model.Project)
     */
    public void setProject( Project project )
    {
        this.project = project;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return getName();
    }
}
