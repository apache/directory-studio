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
package org.apache.directory.studio.apacheds.schemaeditor;


import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;


/**
 * This class contains helper methods.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class PluginUtils
{
    /**
     * Verifies that the given name is syntaxely correct according to the RFC 2252 
     * (Lightweight Directory Access Protocol (v3): Attribute Syntax Definitions).
     *
     * @param name
     *      the name to test
     * @return
     *      true if the name is correct, false if the name is not correct.
     */
    public static boolean verifyName( String name )
    {
        return name.matches( "[a-zA-Z]+[a-zA-Z0-9;-]*" ); //$NON-NLS-1$
    }


    /**
     * Returns a clone of the given attribute type.
     *
     * @param at
     *      the attribute type to clone
     * @return
     *      a clone of the given attribute type
     */
    public static AttributeTypeImpl getClone( AttributeTypeImpl at )
    {
        AttributeTypeImpl clone = new AttributeTypeImpl( at.getOid() );
        clone.setNames( at.getNames() );
        clone.setSchema( at.getSchema() );
        clone.setDescription( at.getDescription() );
        clone.setSuperiorName( at.getSubstrName() );
        clone.setUsage( clone.getUsage() );
        clone.setSyntaxOid( at.getSyntaxOid() );
        clone.setLength( at.getLength() );
        clone.setObsolete( at.isObsolete() );
        clone.setSingleValue( at.isSingleValue() );
        clone.setCollective( at.isCollective() );
        clone.setCanUserModify( clone.isCanUserModify() );
        clone.setEqualityName( at.getEqualityName() );
        clone.setOrderingName( at.getOrderingName() );
        clone.setSubstrName( at.getSubstrName() );

        return clone;
    }


    /**
     * Returns a clone of the given object class.
     *
     * @param oc
     *      the object class to clone
     * @return
     *      a clone of the given object class
     */
    public static ObjectClassImpl getClone( ObjectClassImpl oc )
    {
        ObjectClassImpl clone = new ObjectClassImpl( oc.getOid() );
        clone.setNames( oc.getNames() );
        clone.setSchema( oc.getSchema() );
        clone.setDescription( oc.getDescription() );
        clone.setSuperClassesNames( oc.getSuperClassesNames() );
        clone.setType( oc.getType() );
        clone.setObsolete( oc.isObsolete() );
        clone.setMustNamesList( oc.getMustNamesList() );
        clone.setMayNamesList( oc.getMayNamesList() );

        return clone;
    }
}
