/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.model;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.openldap.config.OpenLdapConfigurationPlugin;

/**
 * A class used to store the WhatClause attributes with a qualifier (either '!' or '@').
 * We store either an AttributeType, or an ObjectClass (prefixed with '@' or '!'), or one
 * of the two special values : 'entry' and 'children'
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AclAttribute
{
    /** The special "entry" constant */
    public static final String ENTRY = "entry";
    
    /** The special "children" constant */
    public static final String CHILDREN = "children";
    
    /** The special ExtensibleObject constant*/
    public static final String EXTENSIBLE_OBJECT = "extensibleObject";
    
    /** The prefix for ObjectClass */
    public static final char OC = '@';
    
    /** The prefix for ObjectClass exclusion */
    public static final char OC_EX = '!';
    
    /** The AttributeType, if we know about it */
    private AttributeType attributeType;
    
    /** The ObjectClass, if we know about it */
    private ObjectClass objectClass;
    
    /** The attributeName */
    private String name;
    
    /** A flag set when we are storing an AttributeType */
    private boolean isAttributeType = false;
    
    /** A flag set when we are storing an ObjectClass */
    private boolean isObjectClass = false;
    
    /** A flag set when we are storing an ObjectClass with control on non allowed attributes */
    private boolean isObjectClassNotAllowed = false;
    
    /** A flag set when we stored the special 'entry' attribute */
    private boolean isEntry = false;
    
    /** A flag set when we stored the special 'children' attribute */
    private boolean isChildren = false;
    
    /** The Connection to the LDAP server */
    private IBrowserConnection connection;
    
    /**
     * Create a new AclAttribute with no name
     * 
     * @param connection The Connection on the LDAP Server 
     */
    public AclAttribute( IBrowserConnection connection )
    {
        this.connection = connection;
        setName( "" );
    }
    
    
    /**
     * Create a new AclAttribute with specific name
     * 
     * @param name The AlcAttribute name
     */
    public AclAttribute( String name )
    {
        this.connection = null;
        setName( name );
    }
    
    
    /**
     * Create a new AclAttribute with specific name
     * 
     * @param name The AlcAttribute name
     */
    public AclAttribute( String name, IBrowserConnection connection )
    {
        this.connection = connection;
        setName( name );
    }
    

    /**
     * @return the attributeType
     */
    public AttributeType getAttributeType()
    {
        return attributeType;
    }

    
    /**
     * @return the objectClass
     */
    public ObjectClass getObjectClass()
    {
        return objectClass;
    }

    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    
    /**
     * @param name the name to set
     */
    public void setName( String name )
    {
        if ( Strings.isEmpty( name ) )
        {
            //The default is externalObject ObjectClass
            isObjectClass = true;
            this.name = EXTENSIBLE_OBJECT;
        }
        else
        {
            if ( Strings.isCharASCII( name, 0, OC ) )
            {
                isObjectClass = true;
                this.name = name.substring( 1 );
            }
            else if ( Strings.isCharASCII( name, 0, OC_EX ) )
            {
                isObjectClassNotAllowed = true;
                this.name = name.substring( 1 );
            }
            else
            {
                if ( ENTRY.equalsIgnoreCase( name ) )
                {
                    isEntry = true;
                }
                else if ( CHILDREN.equalsIgnoreCase( name ) )
                {
                    isChildren = true;
                }
                else
                {
                    isAttributeType = true;
                }

                this.name = name;
            }
        }
        
        if ( ( isAttributeType || isObjectClass || isObjectClassNotAllowed ) && ( connection != null ) )
        {
            // Try to find the element in the schema
            try
            {  
                SchemaManager schemaManager = OpenLdapConfigurationPlugin.getDefault().getSchemaManager();
                
                if ( schemaManager != null )
                {
                    if ( isAttributeType )
                    {
                        attributeType = schemaManager.lookupAttributeTypeRegistry( this.name );
                    }
                    else
                    {
                        // It's an ObjectClass
                        objectClass = schemaManager.lookupObjectClassRegistry( this.name );
                    }
                }
            }
            catch ( Exception e )
            {
                // Nothing to do
            }
        }
    }


    /**
     * @return the isAttributeType
     */
    public boolean isAttributeType()
    {
        return isAttributeType;
    }

    
    /**
     * @return the isObjectClass
     */
    public boolean isObjectClass()
    {
        return isObjectClass;
    }


    /**
     * @return the isEntry
     */
    public boolean isEntry()
    {
        return isEntry;
    }


    /**
     * @return the isChildren
     */
    public boolean isChildren()
    {
        return isChildren;
    }


    /**
     * @return the isObjectClassNotAllowed
     */
    public boolean isObjectClassNotAllowed()
    {
        return isObjectClassNotAllowed;
    }


    /**
     * @See Object#toString()
     */
    public String toString()
    {
        if ( isEntry || isChildren || isAttributeType )
        {
            return name;
        }

        StringBuilder buffer = new StringBuilder();
        
        if ( isObjectClass )
        {
            buffer.append( OC );
        }
        else
        {
            buffer.append( OC_EX );
        }
        
        buffer.append( name );
        return buffer.toString();
    }
}
