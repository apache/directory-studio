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


import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;


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
    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeAdded(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeAdded( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeModified(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeModified( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#attributeTypeRemoved(org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeRemoved( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleAdded(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleAdded( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleModified(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleModified( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#matchingRuleRemoved(org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleRemoved( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassAdded(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassAdded( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassModified(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassModified( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#objectClassRemoved(org.apache.directory.studio.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassRemoved( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaAdded(org.apache.directory.studio.schemaeditor.model.Schema)
     */
    public void schemaAdded( Schema schema )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#schemaRemoved(org.apache.directory.studio.schemaeditor.model.Schema)
     */
    public void schemaRemoved( Schema schema )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxAdded(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxAdded( SyntaxImpl syntax )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxModified(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxModified( SyntaxImpl syntax )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.schemaeditor.controller.SchemaHandlerListener#syntaxRemoved(org.apache.directory.studio.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxRemoved( SyntaxImpl syntax )
    {
    }
}
