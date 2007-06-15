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
package org.apache.directory.studio.model;

import javax.naming.NamingException;

import org.apache.directory.shared.ldap.schema.AbstractSchemaObject;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.schema.ObjectClass;
import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;

public class ObjectClassImpl extends AbstractSchemaObject implements MutableSchemaObject, ObjectClass
{
    private ObjectClassTypeEnum objectClassTypeEnum;
    
    private String[] mayNamesList;
    
    private String[] mustNamesList;
    
    private String[] superClassesNames;
    
    public ObjectClassImpl( String oid )
    {
        super( oid );
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AttributeType[] getMayList() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public AttributeType[] getMustList() throws NamingException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ObjectClass[] getSuperClasses() throws NamingException
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

    public String[] getMayNamesList()
    {
        return mayNamesList;
    }

    public void setMayNamesList( String[] mayNamesList )
    {
        this.mayNamesList = mayNamesList;
    }

    public String[] getMustNamesList()
    {
        return mustNamesList;
    }

    public void setMustNamesList( String[] mustNamesList )
    {
        this.mustNamesList = mustNamesList;
    }

    public String[] getSuperClassesNames()
    {
        return superClassesNames;
    }

    public void setSuperClassesNames( String[] superClassesNames )
    {
        this.superClassesNames = superClassesNames;
    }

    public ObjectClassTypeEnum getObjectClassTypeEnum()
    {
        return objectClassTypeEnum;
    }

    public void setObjectClassTypeEnum( ObjectClassTypeEnum objectClassTypeEnum )
    {
        this.objectClassTypeEnum = objectClassTypeEnum;
    }

    public ObjectClassTypeEnum getType()
    {
        return objectClassTypeEnum;
    }
    
    
    public void setType( ObjectClassTypeEnum objectClassTypeEnum )
    {
        this.objectClassTypeEnum = objectClassTypeEnum;
    }
}
