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
package org.apache.directory.studio.apacheds.schemaeditor.controller;

import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl;


/**
 * This adapter class provides default implementations for the methods 
 * described by the SchemaListener interface.
 * <p>
 * Classes that wish to deal with schema events can extend this class 
 * and override only the methods which they are interested in. 
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class SchemaAdapter implements SchemaListener
{
    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#attributeTypeAdded(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeAdded( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#attributeTypeModified(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeModified( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#attributeTypeRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl)
     */
    public void attributeTypeRemoved( AttributeTypeImpl at )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#matchingRuleAdded(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleAdded( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#matchingRuleModified(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleModified( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#matchingRuleRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.MatchingRuleImpl)
     */
    public void matchingRuleRemoved( MatchingRuleImpl mr )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#objectClassAdded(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassAdded( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#objectClassModified(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassModified( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#objectClassRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl)
     */
    public void objectClassRemoved( ObjectClassImpl oc )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#syntaxAdded(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxAdded( SyntaxImpl syntax )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#syntaxModified(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxModified( SyntaxImpl syntax )
    {
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.model.SchemaListener#syntaxRemoved(org.apache.directory.studio.apacheds.schemaeditor.model.SyntaxImpl)
     */
    public void syntaxRemoved( SyntaxImpl syntax )
    {
    }
}
