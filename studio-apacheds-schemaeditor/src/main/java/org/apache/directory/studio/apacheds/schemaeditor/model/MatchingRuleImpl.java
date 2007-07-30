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
package org.apache.directory.studio.apacheds.schemaeditor.model;


import java.util.Comparator;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractMatchingRule;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.Syntax;


/**
 * This class represents a matching rule.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MatchingRuleImpl extends AbstractMatchingRule implements MutableSchemaObject
{
    private static final long serialVersionUID = 1L;

    /** The OID of the syntax */
    private String syntaxOid;


    /**
     * Gets the OID of the syntax.
     *
     * @return
     *      the OID of the syntax
     */
    public String getSyntaxOid()
    {
        return syntaxOid;
    }


    /**
     * Sets the OID of the syntax.
     *
     * @param syntaxOid
     *      the OID of the syntax
     */
    public void setSyntaxOid( String syntaxOid )
    {
        this.syntaxOid = syntaxOid;
    }


    /**
     * Creates a new instance of MatchingRuleImpl.
     *
     * @param oid
     *      the OID
     */
    public MatchingRuleImpl( String oid )
    {
        super( oid );
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.MatchingRule#getComparator()
     */
    public Comparator<?> getComparator() throws NamingException
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.MatchingRule#getNormalizer()
     */
    public Normalizer getNormalizer() throws NamingException
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.shared.ldap.schema.MatchingRule#getSyntax()
     */
    public Syntax getSyntax() throws NamingException
    {
        return null;
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
}