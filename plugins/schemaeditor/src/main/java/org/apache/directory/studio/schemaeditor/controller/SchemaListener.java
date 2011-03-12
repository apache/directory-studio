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


import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.LdapSyntax;
import org.apache.directory.shared.ldap.model.schema.MutableMatchingRuleImpl;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;


/**
 * Classes which implement this interface provide methods that deal with the 
 * events that are generated when an event occurs on a schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface SchemaListener
{
    /**
     * Sent when the attribute type is added.
     *
     * @param at
     *      the added attribute type
     */
    public void attributeTypeAdded( AttributeType at );


    /**
     * Sent when the attribute type is modified.
     *
     * @param at
     *      the modified attribute type
     */
    public void attributeTypeModified( AttributeType at );


    /**
     * Sent when an attribute type is removed.
     *
     * @param at
     *      the removed attribute type
     */
    public void attributeTypeRemoved( AttributeType at );


    /**
     * Sent when a matching rule is added.
     *
     * @param mr
     *      the added matching rule
     */
    public void matchingRuleAdded( MutableMatchingRuleImpl mr );


    /**
     * Sent when a matching rule is modified.
     *
     * @param mr
     *      the modified matching rule
     */
    public void matchingRuleModified( MutableMatchingRuleImpl mr );


    /**
     * Sent when a matching rule is removed.
     *
     * @param mr
     *      the removed matching rule
     */
    public void matchingRuleRemoved( MutableMatchingRuleImpl mr );


    /**
     * Sent when an object class is added.
     *
     * @param oc
     *      the added object class
     */
    public void objectClassAdded( ObjectClass oc );


    /**
     * Sent when an object class is modified.
     *
     * @param oc
     *      the modified object class
     */
    public void objectClassModified( ObjectClass oc );


    /**
     * Sent when an object class is removed.
     *
     * @param oc
     *      the removed attribute type
     */
    public void objectClassRemoved( ObjectClass oc );


    /**
     * Sent when a syntax is added.
     *
     * @param syntax
     *      the added syntax
     */
    public void syntaxAdded( LdapSyntax syntax );


    /**
     * Sent when a syntax is modified.
     *
     * @param syntax
     *      the modified syntax
     */
    public void syntaxModified( LdapSyntax syntax );


    /**
     * Sent when a syntax is removed.
     *
     * @param syntax
     *      the removed syntax
     */
    public void syntaxRemoved( LdapSyntax syntax );
}
