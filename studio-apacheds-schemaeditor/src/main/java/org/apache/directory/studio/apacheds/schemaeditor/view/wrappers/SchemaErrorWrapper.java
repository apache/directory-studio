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
package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers;


import org.apache.directory.studio.apacheds.schemaeditor.model.schemachecker.SchemaError;


/**
 * This class is used to wrap a SchemaError in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaErrorWrapper extends AbstractTreeNode
{
    /** The wrapped SchemaError */
    private SchemaError schemaError;


    /**
     * Creates a new instance of SchemaErrorWrapper.
     *
     * @param error
     *      the wrapped SchemaError
     */
    public SchemaErrorWrapper( SchemaError error )
    {
        super( null );
        schemaError = error;
    }


    /**
     * Creates a new instance of SchemaErrorWrapper.
     * 
     * @param error
     *      the wrapped SchemaError
     * @param parent
     *      the parent TreeNode
     */
    public SchemaErrorWrapper( SchemaError error, TreeNode parent )
    {
        super( parent );
        schemaError = error;
    }


    /**
     * Gets the wrapped SchemaError.
     *
     * @return
     *      the wrapped SchemaError
     */
    public SchemaError getSchemaError()
    {
        return schemaError;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AbstractTreeNode#hasChildren()
     */
    public boolean hasChildren()
    {
        return false;
    }
}
