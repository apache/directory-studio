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
package org.apache.directory.studio.schemaeditor.model;


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractSyntax;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;


/**
 * This class represents a syntax.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SyntaxImpl extends AbstractSyntax
{
    private static final long serialVersionUID = 1L;

    /** The schema object */
    private Schema schemaObject;

    /**
     * Creates a new instance of SyntaxImpl.
     *
     * @param oid
     *      the OID
     */
    public SyntaxImpl( String oid )
    {
        super( oid );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSyntax#setHumanReadable(boolean)
     */
    public void setHumanReadable( boolean isHumanReadable )
    {
        super.setHumanReadable( isHumanReadable );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setDescription(java.lang.String)
     */
    public void setDescription( String description )
    {
        super.setDescription( description );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setNames(java.lang.String[])
     */
    public void setNames( String[] names )
    {
        super.setNames( names );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.AbstractSchemaObject#setObsolete(boolean)
     */
    public void setObsolete( boolean obsolete )
    {
        super.setObsolete( obsolete );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.Syntax#getSyntaxChecker()
     */
    public SyntaxChecker getSyntaxChecker() throws NamingException
    {
        return null;
    }


    public Schema getSchemaObject()
    {
        return schemaObject;
    }


    public void setSchemaObject( Schema schemaObject )
    {
        this.schemaObject = schemaObject;
    }
}
