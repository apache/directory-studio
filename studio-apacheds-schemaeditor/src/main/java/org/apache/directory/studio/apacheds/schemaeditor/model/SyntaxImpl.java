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


import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractSyntax;
import org.apache.directory.shared.ldap.schema.syntax.SyntaxChecker;


public class SyntaxImpl extends AbstractSyntax
{
    private static final long serialVersionUID = 1L;


    public SyntaxImpl( String oid )
    {
        super( oid );
        // TODO Auto-generated constructor stub
    }


    @Override
    public void setHumanReadible( boolean isHumanReadible )
    {
        // TODO Auto-generated method stub
        super.setHumanReadible( isHumanReadible );
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


    public SyntaxChecker getSyntaxChecker() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
