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


import java.util.List;


/**
 * This interface represents a Schema.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public interface Schema
{
    /**
     * Gets the name of the Schema.
     * 
     * @return
     * 		the name of the Schema
     */
    public String getName();


    /**
     * Sets the name of the Schema.
     * 
     * @param name
     * 		the name of the schema
     */
    public void setName( String name );


    /**
     * Gets the project of the Schema.
     * 
     * @return
     * 		the project of the Schema
     */
    public Project getProject();


    /**
     * Sets the project of the Schema.
     * 
     * @param name
     * 		the project of the schema
     */
    public void setProject( Project project );


    /**
     * Gets all the ObjectClassImpl objects contained in the Schema.
     * 
     * @return
     * 		all the ObjectClassImpl objects contained in the Schema
     */
    public List<ObjectClassImpl> getObjectClasses();


    /**
     * Gets all the AttributeTypeImpl objects contained in the Schema.
     * 
     * @return
     * 		all the AttributeTypeImpl objects contained in the Schema
     */
    public List<AttributeTypeImpl> getAttributeTypes();


    /**
     * Gets all the MatchingRuleImpl objects contained in the Schema.
     * 
     * @return
     *      all the MatchingRuleImpl objects contained in the Schema
     */
    public List<MatchingRuleImpl> getMatchingRules();


    /**
     * Gets all the SyntaxImpl objects contained in the Schema.
     * 
     * @return
     *      all the SyntaxImpl objects contained in the Schema
     */
    public List<SyntaxImpl> getSyntaxes();


    /**
     * Gets the ObjectClassImpl identified by the given id.
     * 
     * @param id
     * 		the name or the oid of the ObjectClassImpl
     * @return
     * 		the ObjectClassImpl identified by the given id, or null if the 
     * ObjectClassImpl has not been found
     */
    public ObjectClassImpl getObjectClass( String id );


    /**
     * Gets the AttributeTypeImpl identified by the given id.
     * 
     * @param id
     * 		the name or the oid of the AttributeTypeImpl
     * @return
     * 		the AttributeTypeImpl identified by the given id, or null if the 
     * AttributeTypeImpl has not been found
     */
    public AttributeTypeImpl getAttributeType( String id );


    /**
     * Gets the MatchingRuleImpl identified by the given id.
     * 
     * @param id
     *      the name or the oid of the MatchingRuleImpl
     * @return
     *      the MatchingRuleImpl identified by the given id, or null if the 
     * MatchingRuleImpl has not been found
     */
    public MatchingRuleImpl getMatchingRule( String id );


    /**
     * Gets the SyntaxImpl identified by the given id.
     * 
     * @param id
     *      the name or the oid of the SyntaxImpl
     * @return
     *      the SyntaxImpl identified by the given id, or null if the 
     * SyntaxImpl has not been found
     */
    public SyntaxImpl getSyntax( String id );


    /**
     * Adds an ObjectClassImpl to the Schema.
     * 
     * @param oc
     *      the ObjectClassImpl
     */
    public boolean addObjectClass( ObjectClassImpl oc );


    /**
     * Adds an AttributeTypeImpl to the Schema.
     * 
     * @param at
     *      the AttributeTypeImpl
     */
    public boolean addAttributeType( AttributeTypeImpl at );


    /**
     * Adds a MatchingRuleImpl from the Schema.
     * 
     * @param mr
     *      the MatchingRuleImpl
     */
    public boolean addMatchingRule( MatchingRuleImpl mr );


    /**
     * Adds a SyntaxImpl from the Schema.
     * 
     * @param syntax
     *      the SyntaxImpl
     */
    public boolean addSyntax( SyntaxImpl syntax );


    /**
     * Removes an ObjectClassImpl from the Schema.
     *
     * @param oc
     *      the ObjectClassImpl
     */
    public boolean removeObjectClass( ObjectClassImpl oc );


    /**
     * Removes an AttributeTypeImpl from the Schema.
     * 
     * @param at
     *      the AttributeTypeImpl
     */
    public boolean removeAttributeType( AttributeTypeImpl at );


    /**
     * Removes a MatchingRuleImpl from the Schema.
     * 
     * @param mr
     *      the MatchingRuleImpl
     */
    public boolean removeMatchingRule( MatchingRuleImpl mr );


    /**
     * Removes a SyntaxImpl from the Schema.
     * 
     * @param syntax
     *      the SyntaxImpl
     */
    public boolean removeSyntax( SyntaxImpl syntax );
}
