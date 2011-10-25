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


import org.apache.directory.studio.schemaeditor.model.Schema;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to wrap an Schema in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaWrapper extends AbstractTreeNode
{
    /** The wrapped Schema */
    private Schema schema;


    /**
     * Creates a new instance of SchemaWrapper.
     *
     * @param schema
     *      the wrapped Schema
     */
    public SchemaWrapper( Schema schema )
    {
        super( null );
        this.schema = schema;
    }


    /**
     * Creates a new instance of SchemaWrapper.
     * 
     * @param at
     *      the wrapped Schema
     * @param parent
     *      the parent TreeNode
     */
    public SchemaWrapper( Schema schema, TreeNode parent )
    {
        super( parent );
        this.schema = schema;
    }


    /**
     * Gets the wrapped Schema.
     *
     * @return
     *      the wrapped Schema
     */
    public Schema getSchema()
    {
        return schema;
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof SchemaWrapper )
        {
            if ( super.equals( obj ) )
            {
                SchemaWrapper sw = ( SchemaWrapper ) obj;

                if ( ( schema != null ) && ( !schema.equals( sw.getSchema() ) ) )
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

        if ( schema != null )
        {
            result = 37 * result + schema.hashCode();
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return NLS.bind( Messages.getString( "SchemaWrapper.SchemaWrapper" ), new Object[] { schema, fParent } ); //$NON-NLS-1$
    }
}
