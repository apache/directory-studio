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
import org.apache.directory.shared.ldap.model.schema.MatchingRule;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.studio.schemaeditor.model.Schema;


/**
 * This adapter class provides default implementations for the methods 
 * described by the SchemaHandlerListener interface.
 * <p>
 * Classes that wish to deal with schema handling events can extend this class 
 * and override only the methods which they are interested in. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class SchemaHandlerAdapter implements SchemaHandlerListener
{
    /**
     * {@inheritDoc}
     */
    public void attributeTypeAdded( AttributeType at )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void attributeTypeModified( AttributeType at )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void attributeTypeRemoved( AttributeType at )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void matchingRuleAdded( MatchingRule mr )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void matchingRuleModified( MatchingRule mr )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void matchingRuleRemoved( MatchingRule mr )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void objectClassAdded( ObjectClass oc )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void objectClassModified( ObjectClass oc )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void objectClassRemoved( ObjectClass oc )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void schemaAdded( Schema schema )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void schemaRemoved( Schema schema )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void syntaxAdded( LdapSyntax syntax )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void syntaxModified( LdapSyntax syntax )
    {
    }


    /**
     * {@inheritDoc}
     */
    public void syntaxRemoved( LdapSyntax syntax )
    {
    }
}
