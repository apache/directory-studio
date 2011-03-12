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
package org.apache.directory.studio.schemaeditor.model.schemamanager;


import java.util.List;

import org.apache.directory.shared.ldap.model.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.entry.DefaultEntry;
import org.apache.directory.shared.ldap.model.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.apache.directory.shared.ldap.model.schema.AttributeType;
import org.apache.directory.shared.ldap.model.schema.MutableLdapSyntaxImpl;
import org.apache.directory.shared.ldap.model.schema.MutableMatchingRuleImpl;
import org.apache.directory.shared.ldap.model.schema.ObjectClass;
import org.apache.directory.shared.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.model.schema.MutableSchemaObject;
import org.apache.directory.shared.ldap.model.schema.SchemaObject;
import org.apache.directory.shared.ldap.model.schema.UsageEnum;
import org.apache.directory.shared.util.Strings;


/**
 * This class is a helper class for the {@link SchemaEditorSchemaLoader} class.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaEditorSchemaLoaderUtils
{
    private static final String M_COLLECTIVE = "m-collective";
    private static final String M_DESCRIPTION = "m-description";
    private static final String M_EQUALITY = "m-equality";
    private static final String M_LENGTH = "m-length";
    private static final String M_MAY = "m-may";
    private static final String M_MUST = "m-must";
    private static final String M_NAME = "m-name";
    private static final String M_NO_USER_MODIFICATION = "m-noUserModification";
    private static final String M_OBSOLETE = "m-obsolete";
    private static final String M_OID = "m-oid";
    private static final String M_ORDERING = "m-ordering";
    private static final String M_SINGLE_VALUE = "m-singleValue";
    private static final String M_SUBSTR = "m-substr";
    private static final String M_SUP_ATTRIBUTE_TYPE = "m-supAttributeType";
    private static final String M_SUP_OBJECT_CLASS = "m-supObjectClass";
    private static final String M_SYNTAX = "m-syntax";
    private static final String M_TYPE_OBJECT_CLASS = "m-typeObjectClass";
    private static final String M_USAGE = "m-usage";
    private static final String TRUE = "TRUE";


    /**
     * Converts the given attribute type to an equivalent entry representation.
     *
     * @param attributeType
     *      the attribute type
     * @return
     *      the attribute type converted to an equivalent entry representation
     * @throws LdapException
     */
    public static Entry toEntry( AttributeType attributeType ) throws LdapException
    {
        // Creating a blank entry
        Entry entry = new DefaultEntry();

        // Setting calculated DN
        entry.setDn( getDn( attributeType, SchemaConstants.ATTRIBUTES_TYPE_PATH ) );

        // Values common to all schema objects
        addSchemaObjectValues( attributeType, SchemaConstants.META_ATTRIBUTE_TYPE_OC, entry );

        // Superior value
        addSuperiorValue( attributeType, entry );

        // Equality matching rule value
        addEqualityValue( attributeType, entry );

        // Ordering matching rule value
        addOrderingValue( attributeType, entry );

        // Substrings matching rule value
        addSubstrValue( attributeType, entry );

        // Syntax value
        addSyntaxValue( attributeType, entry );

        // Single value value
        addSingleValueValue( attributeType, entry );

        // Collective value
        addCollectiveValue( attributeType, entry );

        // No user modification value
        addNoUserModificationValue( attributeType, entry );

        // Usage value
        addUsageValue( attributeType, entry );

        return entry;
    }


    /**
     * Converts the given object class to an equivalent entry representation.
     *
     * @param matchingRule
     *      the matching rule
     * @return
     *      the object class converted to an equivalent entry representation
     * @throws LdapException
     */
    public static Entry toEntry( MutableMatchingRuleImpl matchingRule ) throws LdapException
    {
        // Creating a blank entry
        Entry entry = new DefaultEntry();

        // Setting calculated DN
        entry.setDn( getDn( matchingRule, SchemaConstants.MATCHING_RULES_PATH ) );

        // Values common to all schema objects
        addSchemaObjectValues( matchingRule, SchemaConstants.META_MATCHING_RULE_OC, entry );

        String syntax = matchingRule.getSyntaxOid();
        if ( !Strings.isEmpty( syntax ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_SYNTAX, syntax );
            entry.add( attribute );
        }

        return entry;
    }


    /**
     * Converts the given object class to an equivalent entry representation.
     *
     * @param objectClass
     *      the object class
     * @return
     *      the object class converted to an equivalent entry representation
     * @throws LdapException
     */
    public static Entry toEntry( ObjectClass objectClass ) throws LdapException
    {
        // Creating a blank entry
        Entry entry = new DefaultEntry();

        // Setting calculated DN
        entry.setDn( getDn( objectClass, SchemaConstants.OBJECT_CLASSES_PATH ) );

        // Values common to all schema objects
        addSchemaObjectValues( objectClass, SchemaConstants.META_OBJECT_CLASS_OC, entry );

        // Superiors value
        addSuperiorsValue( objectClass, entry );

        // Class type value
        addClassTypeValue( objectClass, entry );

        // Musts value
        addMustsValue( objectClass, entry );

        // Mays value
        addMaysValue( objectClass, entry );

        return entry;
    }


    /**
     * Converts the given object class to an equivalent entry representation.
     *
     * @param syntax
     *      the syntax
     * @return
     *      the object class converted to an equivalent entry representation
     * @throws LdapException
     */
    public static Entry toEntry( MutableLdapSyntaxImpl syntax ) throws LdapException
    {
        // Creating a blank entry
        Entry entry = new DefaultEntry();

        // Setting calculated DN
        entry.setDn( getDn( syntax, SchemaConstants.MATCHING_RULES_PATH ) );

        // Values common to all schema objects
        addSchemaObjectValues( syntax, SchemaConstants.META_MATCHING_RULE_OC, entry );

        return entry;
    }


    /**
     * Returns the DN for the given schema object in the given object path.
     *
     * @param schemaObject
     *      the schema object
     * @param objectPath
     *      the object path
     * @return
     *      the DN for the given schema object in the given object path
     * @throws LdapInvalidDnException
     */
    private static Dn getDn( SchemaObject schemaObject, String objectPath ) throws LdapInvalidDnException
    {
        return Dn.EMPTY_DN
            .add( new Rdn( M_OID, schemaObject.getOid() ) )
            .add( new Rdn( objectPath ) )
            .add( new Rdn( SchemaConstants.CN_AT, schemaObject.getSchemaName() ) )
            .add( new Rdn( SchemaConstants.OU_SCHEMA ) );
    }


    /**
     * Adds the values common to all {@link MutableSchemaObject}(s) to the entry.
     *
     * @param schemaObject
     *      the schema object
     * @param objectClassValue
     *      the value for the objectClass attribute
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addSchemaObjectValues( SchemaObject schemaObject, String objectClassValue, Entry entry )
        throws LdapException
    {
        // ObjectClass
        addObjectClassValue( schemaObject, objectClassValue, entry );

        // OID
        addOidValue( schemaObject, entry );

        // Names
        addNamesValue( schemaObject, entry );

        // Description
        addDescriptionValue( schemaObject, entry );

        // Obsolete
        addObsoleteValue( schemaObject, entry );
    }


    /**
     * Adds the objectClass value to the entry.
     *
     * @param schemaObject
     *      the schema object
     * @param objectClassValue
     *      the value for the objectClass attribute
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addObjectClassValue( SchemaObject schemaObject, String objectClassValue, Entry entry )
        throws LdapException
    {
        EntryAttribute objectClassAttribute = new DefaultEntryAttribute( SchemaConstants.OBJECT_CLASS_AT );
        entry.add( objectClassAttribute );
        objectClassAttribute.add( SchemaConstants.TOP_OC );
        objectClassAttribute.add( SchemaConstants.META_TOP_OC );
        objectClassAttribute.add( objectClassValue );
    }


    /**
     * Adds the OID value to the entry.
     *
     * @param schemaObject
     *      the schema object
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addOidValue( SchemaObject schemaObject, Entry entry ) throws LdapException
    {
        String oid = schemaObject.getOid();
        if ( !Strings.isEmpty( oid ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_OID, oid );
            entry.add( attribute );
        }
    }


    /**
     * Adds the names value to the entry.
     *
     * @param schemaObject
     *      the schema object
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addNamesValue( SchemaObject schemaObject, Entry entry ) throws LdapException
    {
        List<String> names = schemaObject.getNames();
        if ( ( names != null ) && ( names.size() > 0 ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_NAME );
            entry.add( attribute );

            for ( String name : names )
            {
                attribute.add( name );
            }
        }
    }


    /**
     * Adds the description value to the entry.
     *
     * @param schemaObject
     *      the schema object
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addDescriptionValue( SchemaObject schemaObject, Entry entry ) throws LdapException
    {
        String description = schemaObject.getDescription();
        if ( !Strings.isEmpty( description ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_DESCRIPTION, description );
            entry.add( attribute );
        }
    }


    /**
     * Adds the obsolete value to the entry.
     *
     * @param schemaObject
     *      the schema object
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addObsoleteValue( SchemaObject schemaObject, Entry entry ) throws LdapException
    {
        if ( schemaObject.isObsolete() )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_OBSOLETE, TRUE );
            entry.add( attribute );
        }
    }


    /**
     * Adds the superior value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addSuperiorValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        String superior = attributeType.getSuperiorName();
        if ( !Strings.isEmpty( superior ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_SUP_ATTRIBUTE_TYPE, superior );
            entry.add( attribute );
        }
    }


    /**
     * Adds the equality matching rule value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addEqualityValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        String equality = attributeType.getEqualityName();
        if ( !Strings.isEmpty( equality ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_EQUALITY, equality );
            entry.add( attribute );
        }
    }


    /**
     * Adds the ordering matching rule value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addOrderingValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        String ordering = attributeType.getOrderingName();
        if ( !Strings.isEmpty( ordering ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_ORDERING, ordering );
            entry.add( attribute );
        }
    }


    /**
     * Adds the substring matching rule value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addSubstrValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        String substr = attributeType.getSubstringName();
        if ( !Strings.isEmpty( substr ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_SUBSTR, substr );
            entry.add( attribute );
        }
    }


    /**
     * Adds the syntax value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addSyntaxValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        String syntax = attributeType.getSyntaxName();
        if ( !Strings.isEmpty( syntax ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_SYNTAX, syntax );
            entry.add( attribute );

            long syntaxLength = attributeType.getSyntaxLength();
            if ( syntaxLength != -1 )
            {
                attribute = new DefaultEntryAttribute( M_LENGTH, "" + syntaxLength );
                entry.add( attribute );
            }
        }
    }


    /**
     * Adds the single value value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addSingleValueValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        if ( attributeType.isSingleValued() )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_SINGLE_VALUE, TRUE );
            entry.add( attribute );
        }
    }


    /**
     * Adds the collective value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addCollectiveValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        if ( attributeType.isCollective() )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_COLLECTIVE, TRUE );
            entry.add( attribute );
        }
    }


    /**
     * Adds the no user modification value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addNoUserModificationValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        if ( !attributeType.isUserModifiable() )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_NO_USER_MODIFICATION, TRUE );
            entry.add( attribute );
        }
    }


    /**
     * Adds the usage value.
     *
     * @param attributeType
     *      the attribute type
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addUsageValue( AttributeType attributeType, Entry entry ) throws LdapException
    {
        UsageEnum usage = attributeType.getUsage();
        if ( usage != UsageEnum.USER_APPLICATIONS )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_USAGE, usage.render() );
            entry.add( attribute );
        }
    }


    /**
     * Adds the superiors value.
     *
     * @param objectClass
     *      the object class
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addSuperiorsValue( ObjectClass objectClass, Entry entry ) throws LdapException
    {
        List<String> superiors = objectClass.getSuperiorOids();
        if ( ( superiors != null ) && ( superiors.size() > 0 ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_SUP_OBJECT_CLASS );
            entry.add( attribute );

            for ( String superior : superiors )
            {
                attribute.add( superior );
            }
        }
    }


    /**
     * Adds class type value.
     *
     * @param objectClass
     *      the object class
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addClassTypeValue( ObjectClass objectClass, Entry entry ) throws LdapException
    {
        ObjectClassTypeEnum classType = objectClass.getType();
        if ( classType != ObjectClassTypeEnum.STRUCTURAL )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_TYPE_OBJECT_CLASS, classType.toString() );
            entry.add( attribute );
        }
    }


    /**
     * Adds musts value.
     *
     * @param objectClass
     *      the object class
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addMustsValue( ObjectClass objectClass, Entry entry ) throws LdapException
    {
        List<String> musts = objectClass.getMustAttributeTypeOids();
        if ( ( musts != null ) && ( musts.size() > 0 ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_MUST );
            entry.add( attribute );

            for ( String must : musts )
            {
                attribute.add( must );
            }
        }
    }


    /**
     * Adds mays value.
     *
     * @param objectClass
     *      the object class
     * @param entry
     *      the entry
     * @throws LdapException
     */
    private static void addMaysValue( ObjectClass objectClass, Entry entry ) throws LdapException
    {
        List<String> mays = objectClass.getMayAttributeTypeOids();
        if ( ( mays != null ) && ( mays.size() > 0 ) )
        {
            EntryAttribute attribute = new DefaultEntryAttribute( M_MAY );
            entry.add( attribute );

            for ( String may : mays )
            {
                attribute.add( may );
            }
        }
    }
}
