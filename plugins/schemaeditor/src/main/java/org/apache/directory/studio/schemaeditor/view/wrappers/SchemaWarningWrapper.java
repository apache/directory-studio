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
package org.apache.directory.studio.schemaeditor.view.wrappers;


import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaWarning;


/**
 * This class is used to wrap a SchemaWarning in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaWarningWrapper extends AbstractTreeNode
{
    /** The wrapped SchemaWarning */
    private SchemaWarning schemaWarning;


    /**
     * Creates a new instance of SchemaWarningWrapper.
     *
     * @param warning
     *      the wrapped SchemaWarning
     */
    public SchemaWarningWrapper( SchemaWarning warning )
    {
        super( null );
        schemaWarning = warning;
    }


    /**
     * Creates a new instance of SchemaErrorWrapper.
     * 
     * @param error
     *      the wrapped SchemaError
     * @param parent
     *      the parent TreeNode
     */
    public SchemaWarningWrapper( SchemaWarning warning, TreeNode parent )
    {
        super( parent );
        schemaWarning = warning;
    }


    /**
     * Gets the wrapped SchemaWarning.
     *
     * @return
     *      the wrapped SchemaWarning
     */
    public SchemaWarning getSchemaWarning()
    {
        return schemaWarning;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasChildren()
    {
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof SchemaWarningWrapper )
        {
            if ( super.equals( obj ) )
            {
                SchemaWarningWrapper sww = ( SchemaWarningWrapper ) obj;

                if ( ( schemaWarning != null ) && ( !schemaWarning.equals( sww.getSchemaWarning() ) ) )
                {
                    return false;
                }

                return true;
            }
        }

        // Default
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        int result = super.hashCode();

        if ( schemaWarning != null )
        {
            result = 37 * result + schemaWarning.hashCode();
        }

        return result;
    }
}
