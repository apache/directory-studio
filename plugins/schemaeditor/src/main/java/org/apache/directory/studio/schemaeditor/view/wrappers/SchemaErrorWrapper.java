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


import org.apache.directory.api.ldap.model.exception.LdapSchemaException;


/**
 * This class is used to wrap a SchemaError in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaErrorWrapper extends AbstractTreeNode
{
    /** The wrapper {@link LdapSchemaException} */
    private LdapSchemaException ldapSchemaException;


    /**
     * Creates a new instance of SchemaErrorWrapper.
     *
     * @param error
     *      the wrapped SchemaError
     */
    public SchemaErrorWrapper( LdapSchemaException ldapSchemaException )
    {
        super( null );
        this.ldapSchemaException = ldapSchemaException;
    }


    /**
     * Creates a new instance of SchemaErrorWrapper.
     * 
     * @param error
     *      the wrapped SchemaError
     * @param parent
     *      the parent TreeNode
     */
    public SchemaErrorWrapper( LdapSchemaException ldapSchemaException, TreeNode parent )
    {
        super( parent );
        this.ldapSchemaException = ldapSchemaException;
    }


    /**
     * Gets the wrapped {@link LdapSchemaException}.
     *
     * @return
     *      the wrapped SchemaError
     */
    public LdapSchemaException getLdapSchemaException()
    {
        return ldapSchemaException;
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
        if ( obj instanceof SchemaErrorWrapper )
        {
            if ( super.equals( obj ) )
            {
                SchemaErrorWrapper sww = ( SchemaErrorWrapper ) obj;

                if ( ( ldapSchemaException != null ) && ( !ldapSchemaException.equals( sww.getLdapSchemaException() ) ) )
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

        if ( ldapSchemaException != null )
        {
            result = 37 * result + ldapSchemaException.hashCode();
        }

        return result;
    }
}
