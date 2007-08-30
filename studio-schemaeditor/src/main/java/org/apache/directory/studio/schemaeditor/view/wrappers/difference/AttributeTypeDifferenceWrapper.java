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
 * This class represent the wrapper for an attribute type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeDifferenceWrapper extends AbstractDifferenceWrapper
{
    /**
     * Creates a new instance of AttributeTypeDifferenceWrapper.
     *
     * @param originalObject
     *      the original attribute type
     * @param modifiedObject
     *      the modified attribute type
     * @param parent
     *      the parent TreeNode
     */
    public AttributeTypeDifferenceWrapper( Object originalObject, Object modifiedObject, TreeNode parent )
    {
        super( originalObject, modifiedObject, parent );
    }


    /**
     * Creates a new instance of AttributeTypeDifferenceWrapper.
     *
     * @param originalObject
     *      the original attribute type
     * @param modifiedObject
     *      the modified attribute type
     * @param state
     *      the state of the wrapper
     * @param parent
     *      the parent TreeNode
     */
    public AttributeTypeDifferenceWrapper( Object originalObject, Object modifiedObject, WrapperState state,
        TreeNode parent )
    {
        super( originalObject, modifiedObject, state, parent );
    }
}
