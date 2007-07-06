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


public class MatchingRuleImpl extends AbstractMatchingRule implements MutableSchemaObject
{
    private static final long serialVersionUID = 1L;
    
    /** The OID of the syntax */
    private String syntaxtOid;


    public String getSyntaxtOid()
    {
        return syntaxtOid;
    }


    public void setSyntaxtOid( String syntaxtOid )
    {
        this.syntaxtOid = syntaxtOid;
    }


    public MatchingRuleImpl( String oid )
    {
        super( oid );
    }


    public Comparator<?> getComparator() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Normalizer getNormalizer() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    public Syntax getSyntax() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setDescription( String description )
    {
        // TODO Auto-generated method stub
        super.setDescription( description );
    }


    @Override
    public void setNames( String[] names )
    {
        // TODO Auto-generated method stub
        super.setNames( names );
    }


    @Override
    public void setObsolete( boolean obsolete )
    {
        // TODO Auto-generated method stub
        super.setObsolete( obsolete );
    }
}