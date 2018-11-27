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
package org.apache.directory.studio.schemaeditor.controller;


import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * Classes which implement this interface provide methods that deal with the 
 * events that are generated when an event occurs on a schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface SchemaHandlerListener
{
    /**
     * Sent when the attribute type is added.
     *
     * @param at
     *      the added attribute type
     */
    void attributeTypeAdded( AttributeType at );


    /**
     * Sent when the attribute type is modified.
     *
     * @param at
     *      the modified attribute type
     */
    void attributeTypeModified( AttributeType at );


    /**
     * Sent when an attribute type is removed.
     *
     * @param at
     *      the removed attribute type
     */
    void attributeTypeRemoved( AttributeType at );


    /**
     * Sent when a matching rule is added.
     *
     * @param mr
     *      the added matching rule
     */
    void matchingRuleAdded( MatchingRule mr );


    /**
     * Sent when a matching rule is modified.
     *
     * @param mr
     *      the modified matching rule
     */
    void matchingRuleModified( MatchingRule mr );


    /**
     * Sent when a matching rule is removed.
     *
     * @param mr
     *      the removed matching rule
     */
    void matchingRuleRemoved( MatchingRule mr );


    /**
     * Sent when an object class is added.
     *
     * @param oc
     *      the added object class
     */
    void objectClassAdded( ObjectClass oc );


    /**
     * Sent when an object class is modified.
     *
     * @param oc
     *      the modified object class
     */
    void objectClassModified( ObjectClass oc );


    /**
     * Sent when an object class is removed.
     *
     * @param oc
     *      the removed attribute type
     */
    void objectClassRemoved( ObjectClass oc );


    /**
     * Sent when a schema is added.
     *
     * @param schema
     *      the added syntax
     */
    void schemaAdded( Schema schema );


    /**
     * Sent when a schema is removed.
     *
     * @param schema
     *      the removed syntax
     */
    void schemaRemoved( Schema schema );


    /**
     * Sent when a schema is renamed.
     *
     * @param schema
     *      the removed syntax
     */
    void schemaRenamed( Schema schema );


    /**
     * Sent when a syntax is added.
     *
     * @param syntax
     *      the added syntax
     */
    void syntaxAdded( LdapSyntax syntax );


    /**
     * Sent when a syntax is modified.
     *
     * @param syntax
     *      the modified syntax
     */
    void syntaxModified( LdapSyntax syntax );


    /**
     * Sent when a syntax is removed.
     *
     * @param syntax
     *      the removed syntax
     */
    void syntaxRemoved( LdapSyntax syntax );
}