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
package org.apache.directory.studio.schemaeditor.view.wrappers.difference;


import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;


/**
 * This class represent the wrapper for a schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaDifferenceWrapper extends AbstractDifferenceWrapper
{
    /**
     * Creates a new instance of SchemaDifferenceWrapper.
     *
     * @param originalObject
     *      the original schema
     * @param modifiedObject
     *      the modified schema
     * @param parent
     *      the parent TreeNode
     */
    public SchemaDifferenceWrapper( Object originalObject, Object modifiedObject, TreeNode parent )
    {
        super( originalObject, modifiedObject, parent );
    }


    /**
     * Creates a new instance of SchemaDifferenceWrapper.
     *
     * @param originalObject
     *      the original schema
     * @param modifiedObject
     *      the modified schema
     * @param state
     *      the state of the wrapper
     * @param parent
     *      the parent TreeNode
     */
    public SchemaDifferenceWrapper( Object originalObject, Object modifiedObject, WrapperState state, TreeNode parent )
    {
        super( originalObject, modifiedObject, state, parent );
    }
}
