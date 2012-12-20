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

package org.apache.directory.studio.ldapbrowser.core.model.schema;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.schema.AbstractSchemaObject;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MatchingRuleUse;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeHierarchy;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.eclipse.osgi.util.NLS;


/**
 * Utility class for Schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SchemaUtils
{

    /** The well-known operational attributes */
    public static final Set<String> OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES = new HashSet<String>();
    static
    {
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATE_TIMESTAMP_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATE_TIMESTAMP_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATORS_NAME_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATORS_NAME_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFY_TIMESTAMP_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFY_TIMESTAMP_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFIERS_NAME_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFIERS_NAME_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.SUBSCHEMA_SUBENTRY_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.SUBSCHEMA_SUBENTRY_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.STRUCTURAL_OBJECT_CLASS_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.STRUCTURAL_OBJECT_CLASS_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.GOVERNING_STRUCTURE_RULE_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.GOVERNING_STRUCTURE_RULE_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_UUID_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_UUID_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_CSN_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_DN_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_DN_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.OBJECT_CLASSES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.OBJECT_CLASSES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ATTRIBUTE_TYPES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ATTRIBUTE_TYPES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.LDAP_SYNTAXES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.LDAP_SYNTAXES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MATCHING_RULES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MATCHING_RULES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MATCHING_RULE_USE_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MATCHING_RULE_USE_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.DIT_CONTENT_RULES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.DIT_CONTENT_RULES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.DIT_STRUCTURE_RULES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.DIT_STRUCTURE_RULES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.NAME_FORMS_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.NAME_FORMS_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.HAS_SUBORDINATES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.HAS_SUBORDINATES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.NUM_SUBORDINATES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.SUBORDINATE_COUNT_AT ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_NAME_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_NAME_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_VERSION_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_VERSION_AT_OID ) );
    }

    /** The well-known non-modifiable attributes */
    public static final Set<String> NON_MODIFIABLE_ATTRIBUTE_OIDS_AND_NAMES = new HashSet<String>();
    static
    {
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATE_TIMESTAMP_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATE_TIMESTAMP_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATORS_NAME_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.CREATORS_NAME_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFY_TIMESTAMP_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFY_TIMESTAMP_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFIERS_NAME_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.MODIFIERS_NAME_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.SUBSCHEMA_SUBENTRY_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.SUBSCHEMA_SUBENTRY_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.STRUCTURAL_OBJECT_CLASS_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.STRUCTURAL_OBJECT_CLASS_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.GOVERNING_STRUCTURE_RULE_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.GOVERNING_STRUCTURE_RULE_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_UUID_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_UUID_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_CSN_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_DN_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.ENTRY_DN_AT_OID ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.HAS_SUBORDINATES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.HAS_SUBORDINATES_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.NUM_SUBORDINATES_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.SUBORDINATE_COUNT_AT ) );

        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_NAME_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_NAME_AT_OID ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_VERSION_AT ) );
        OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES.add( Strings.toLowerCase( SchemaConstants.VENDOR_VERSION_AT_OID ) );
    }

    private static final Comparator<String> nameAndOidComparator = new Comparator<String>()
    {
        public int compare( String s1, String s2 )
        {
            if ( s1.matches( "[0-9\\.]+" ) && !s2.matches( "[0-9\\.]+" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return 1;
            }
            else if ( !s1.matches( "[0-9\\.]+" ) && s2.matches( "[0-9\\.]+" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return -1;
            }
            else
            {
                return s1.compareToIgnoreCase( s2 );
            }
        }
    };

    private static final Comparator<AbstractSchemaObject> schemaElementNameComparator = new Comparator<AbstractSchemaObject>()
    {
        public int compare( AbstractSchemaObject s1, AbstractSchemaObject s2 )
        {
            return SchemaUtils.toString( s1 ).compareToIgnoreCase( SchemaUtils.toString( s2 ) );
        }
    };


    /**
     * Gets the names of the given schema elements.
     * 
     * @param asds the schema elements
     * 
     * @return the names
     */
    public static Collection<String> getNames( Collection<? extends AbstractSchemaObject> asds )
    {
        Set<String> nameSet = new TreeSet<String>( nameAndOidComparator );
        for ( AbstractSchemaObject asd : asds )
        {
            nameSet.addAll( asd.getNames() );
        }
        return nameSet;
    }


    /**
     * Gets the names of the given schema elements.
     * 
     * @param asds the schema elements
     * 
     * @return the names
     */
    public static String[] getNamesAsArray( Collection<? extends AbstractSchemaObject> asds )
    {
        return getNames( asds ).toArray( new String[0] );
    }


    /**
     * Get the numeric OIDs of the given schema descriptions.
     * 
     * @return the numeric OIDs of the given schema descriptions
     */
    public static Collection<String> getNumericOids( Collection<? extends AbstractSchemaObject> descriptions )
    {
        Set<String> oids = new HashSet<String>();
        for ( AbstractSchemaObject asd : descriptions )
        {
            oids.add( asd.getOid() );

        }
        return oids;
    }


    /**
     * Gets the identifiers of the given schema descriptions.
     * 
     * @param asd the schema descriptions
     * 
     * @return the identifiers
     */
    public static Collection<String> getLowerCaseIdentifiers( AbstractSchemaObject asd )
    {
        Set<String> identiers = new HashSet<String>();
        if ( asd.getOid() != null )
        {
            identiers.add( Strings.toLowerCase( asd.getOid() ) );
        }
        if ( asd.getNames() != null && !asd.getNames().isEmpty() )
        {
            for ( String name : asd.getNames() )
            {
                if ( name != null )
                {
                    identiers.add( Strings.toLowerCase( name ) );
                }
            }
        }
        return identiers;
    }


    /**
     * Gets the friendly identifier of the given schema description.
     * This is the first name, if there is no name the numeric OID is returned.
     * 
     * @param asd the schema description
     * 
     * @return the friendly identifier
     */
    public static String getFriendlyIdentifier( AbstractSchemaObject asd )
    {
        if ( asd.getNames() != null && !asd.getNames().isEmpty() )
        {
            return asd.getNames().get( 0 );
        }
        return asd.getOid();
    }


    /**
     * Gets all operational attribute type descriptions.
     * 
     * @param schema the schema
     * 
     * @return all operational attributes types
     */
    public static Collection<AttributeType> getOperationalAttributeDescriptions( Schema schema )
    {
        Set<AttributeType> operationalAtds = new HashSet<AttributeType>();
        for ( AttributeType atd : schema.getAttributeTypeDescriptions() )
        {
            if ( isOperational( atd ) )
            {
                operationalAtds.add( atd );
            }
        }
        return operationalAtds;
    }


    /**
     * Gets all user (non-operational) attribute type descriptions.
     * 
     * @param schema the schema
     * 
     * @return all user attributes type descriptions
     */
    public static Collection<AttributeType> getUserAttributeDescriptions( Schema schema )
    {
        Set<AttributeType> userAtds = new HashSet<AttributeType>();
        for ( AttributeType atd : schema.getAttributeTypeDescriptions() )
        {
            if ( !isOperational( atd ) )
            {
                userAtds.add( atd );
            }
        }
        return userAtds;
    }


    /**
     * An attribute type is marked as operational if either
     * <ul>
     * <li>the usage differs from userApplications or</li>
     * <li>it is a well-known operational attribute or 
     *     (we need this because M$ AD and Samba4 don't set the USAGE flag)</li>
     * <li>it is not declared in the schema and contains the dummy extension</li>
     * </ul>
     * 
     * @param atd the attribute type description
     * 
     * @return true, if is operational
     */
    public static boolean isOperational( AttributeType atd )
    {
        return !UsageEnum.USER_APPLICATIONS.equals( atd.getUsage() )
            || Schema.DUMMY_EXTENSIONS.equals( atd.getExtensions() )
            || CollectionUtils.containsAny( OPERATIONAL_ATTRIBUTES_OIDS_AND_NAMES, getLowerCaseIdentifiers( atd ) );
    }


    public static boolean isModifiable( AttributeType atd )
    {
        if ( atd == null )
        {
            return false;
        }

        if ( !atd.isUserModifiable() )
        {
            return false;
        }

        // Check some default no-user-modification attributes
        // e.g. Siemens DirX doesn't provide a good schema.
        // TODO: make default no-user-modification attributes configurable
        if ( CollectionUtils.containsAny( NON_MODIFIABLE_ATTRIBUTE_OIDS_AND_NAMES, getLowerCaseIdentifiers( atd ) ) )
        {
            return false;
        }

        return true;
    }


    /**
     * Gets the must attribute type descriptions of all object class descriptions of the given entry.
     * 
     * param entry the entry
     * 
     * @return the must attribute type descriptions of all object class descriptions of the given entry.
     */
    public static Collection<AttributeType> getMustAttributeTypeDescriptions( IEntry entry )
    {
        Schema schema = entry.getBrowserConnection().getSchema();
        Collection<AttributeType> atds = new HashSet<AttributeType>();
        Collection<ObjectClass> ocds = entry.getObjectClassDescriptions();
        if ( ocds != null )
        {
            for ( ObjectClass ocd : entry.getObjectClassDescriptions() )
            {
                Collection<String> musts = getMustAttributeTypeDescriptionNamesTransitive( ocd, schema );
                for ( String must : musts )
                {
                    AttributeType atd = schema.getAttributeTypeDescription( must );
                    atds.add( atd );
                }
            }
        }
        return atds;
    }


    /**
     * Gets the may attribute type descriptions of all object class descriptions of the given entry.
     * 
     * @param entry the entry
     * 
     * @return the may attribute type descriptions of all object class descriptions of the given entry.
     */
    public static Collection<AttributeType> getMayAttributeTypeDescriptions( IEntry entry )
    {
        Schema schema = entry.getBrowserConnection().getSchema();
        Collection<AttributeType> atds = new HashSet<AttributeType>();
        Collection<ObjectClass> ocds = entry.getObjectClassDescriptions();
        if ( ocds != null )
        {
            for ( ObjectClass ocd : entry.getObjectClassDescriptions() )
            {
                Collection<String> mays = getMayAttributeTypeDescriptionNamesTransitive( ocd, schema );
                for ( String may : mays )
                {
                    AttributeType atd = schema.getAttributeTypeDescription( may );
                    atds.add( atd );
                }
            }
        }
        return atds;
    }


    /**
     * Gets all attribute type descriptions of all object class descriptions of the given entry.
     * 
     * @param entry the entry
     * 
     * @return all attribute type descriptions of all object class descriptions of the given entry.
     */
    public static Collection<AttributeType> getAllAttributeTypeDescriptions( IEntry entry )
    {
        Collection<AttributeType> atds = new HashSet<AttributeType>();
        atds.addAll( getMustAttributeTypeDescriptions( entry ) );
        atds.addAll( getMayAttributeTypeDescriptions( entry ) );
        return atds;
    }


    ////////////////////////////////////////////////////////
    /**
     * Checks the pre-defined and user-defined binary syntax OIDs. If this
     * syntax OID is defined as binary, false is returned..
     * 
     * @param lsd the LDAP syntax description
     * 
     * @return false if the syntax is defined as binary
     */
    public static boolean isString( LdapSyntax lsd )
    {
        return !isBinary( lsd );
    }


    /**
     * Checks the pre-defined and user-defined binary syntax OIDs. If this
     * syntax OID is defined as binary, true is returned..
     * 
     * @param lsd the LDAP syntax description
     * 
     * @return true if the syntax is defined as binary
     */
    public static boolean isBinary( LdapSyntax lsd )
    {
        // check user-defined binary syntaxes
        Set<String> binarySyntaxOids = BrowserCorePlugin.getDefault().getCorePreferences()
            .getUpperCasedBinarySyntaxOids();
        return binarySyntaxOids.contains( lsd.getOid().toUpperCase() );
    }


    /**
     * Checks the pre-defined and user-defined binary attribute types. If this
     * attribute type is defined as binary, false is returned..
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return false if the attribute type is defined as binary
     */
    public static boolean isString( AttributeType atd, Schema schema )
    {
        return !isBinary( atd, schema );
    }


    /**
     * Checks the pre-defined and user-defined binary attribute types. If this
     * attribute type is defined as binary, true is returned..
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return true if the attribute type is defined as binary
     */
    public static boolean isBinary( AttributeType atd, Schema schema )
    {
        // check user-defined binary attribute types
        Set<String> binaryAttributeOidsAndNames = BrowserCorePlugin.getDefault().getCorePreferences()
            .getUpperCasedBinaryAttributeOidsAndNames();
        if ( binaryAttributeOidsAndNames.contains( atd.getOid().toUpperCase() ) )
        {
            return true;
        }
        for ( String name : atd.getNames() )
        {
            if ( binaryAttributeOidsAndNames.contains( name.toUpperCase() ) )
            {
                return true;
            }
        }

        // check user-defined binary syntaxes
        String syntax = getSyntaxNumericOidTransitive( atd, schema );
        if ( syntax != null && schema.hasLdapSyntaxDescription( syntax ) )
        {
            LdapSyntax lsd = schema.getLdapSyntaxDescription( syntax );
            return isBinary( lsd );
        }

        return false;
    }


    /**
     * Gets all attribute type descriptions using the given syntax description.
     * 
     * @param lsd the LDAP syntax description
     * @param schema the schema
     * 
     * @return all attribute type description using this syntax description
     */
    public static Collection<AttributeType> getUsedFromAttributeTypeDescriptions( LdapSyntax lsd,
        Schema schema )
    {
        Set<AttributeType> usedFroms = new TreeSet<AttributeType>( schemaElementNameComparator );
        for ( AttributeType atd : schema.getAttributeTypeDescriptions() )
        {
            String syntax = getSyntaxNumericOidTransitive( atd, schema );
            if ( syntax != null && lsd.getOid() != null
                && Strings.toLowerCase( syntax ).equals( Strings.toLowerCase( lsd.getOid() ) ) )
            {
                usedFroms.add( atd );
            }
        }
        return usedFroms;
    }


    /**
     * Gets all attribute type descriptions using the given matching rule description.
     * 
     * @param mrd the matching rule description
     * @param schema the schema
     * 
     * @return all attribute type descriptions using this matching rule for
     * equality, substring or ordering matching
     */
    public static Collection<AttributeType> getUsedFromAttributeTypeDescriptions(
        MatchingRule mrd, Schema schema )
    {
        Set<AttributeType> usedFromSet = new TreeSet<AttributeType>( schemaElementNameComparator );
        for ( AttributeType atd : schema.getAttributeTypeDescriptions() )
        {
            Collection<String> lowerCaseIdentifiers = getLowerCaseIdentifiers( mrd );
            String emr = getEqualityMatchingRuleNameOrNumericOidTransitive( atd, schema );
            String smr = getSubstringMatchingRuleNameOrNumericOidTransitive( atd, schema );
            String omr = getOrderingMatchingRuleNameOrNumericOidTransitive( atd, schema );
            if ( emr != null && lowerCaseIdentifiers.contains( Strings.toLowerCase( emr ) ) )
            {
                usedFromSet.add( atd );
            }
            if ( smr != null && lowerCaseIdentifiers.contains( Strings.toLowerCase( smr ) ) )
            {
                usedFromSet.add( atd );
            }
            if ( omr != null && lowerCaseIdentifiers.contains( Strings.toLowerCase( omr ) ) )
            {
                usedFromSet.add( atd );
            }
        }
        return usedFromSet;
    }


    /**
     * Gets the equality matching rule description name or OID of the given or the
     * superior attribute type description.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return the equality matching rule description name or OID of the given or the
     *         superior attribute type description, may be null
     */
    public static String getEqualityMatchingRuleNameOrNumericOidTransitive( AttributeType atd, Schema schema )
    {
        if ( atd.getEqualityOid() != null )
        {
            return atd.getEqualityOid();
        }

        if ( atd.getSuperiorOid() != null && schema.hasAttributeTypeDescription( atd.getSuperiorOid() ) )
        {
            AttributeType superior = schema.getAttributeTypeDescription( atd.getSuperiorOid() );
            return getEqualityMatchingRuleNameOrNumericOidTransitive( superior, schema );
        }

        return null;
    }


    /**
     * Gets the substring matching rule description name or OID of the given or the
     * superior attribute type description.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return the substring matching rule description name or OID of the given or the
     *         superior attribute type description, may be null
     */
    public static String getSubstringMatchingRuleNameOrNumericOidTransitive( AttributeType atd, Schema schema )
    {
        if ( atd.getSubstringOid() != null )
        {
            return atd.getSubstringOid();
        }

        if ( atd.getSuperiorOid() != null && schema.hasAttributeTypeDescription( atd.getSubstringOid() ) )
        {
            AttributeType superior = schema.getAttributeTypeDescription( atd.getSubstringOid() );
            return getSubstringMatchingRuleNameOrNumericOidTransitive( superior, schema );
        }

        return null;
    }


    /**
     * Gets the ordering matching rule description name or OID of the given or the
     * superior attribute type description.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return the ordering matching rule description name or OID of the given or the
     *         superior attribute type description, may be null
     */
    public static String getOrderingMatchingRuleNameOrNumericOidTransitive( AttributeType atd, Schema schema )
    {
        if ( atd.getOrderingOid() != null )
        {
            return atd.getOrderingOid();
        }

        if ( atd.getSuperiorOid() != null && schema.hasAttributeTypeDescription( atd.getSuperiorOid() ) )
        {
            AttributeType superior = schema.getAttributeTypeDescription( atd.getSuperiorOid() );
            return getOrderingMatchingRuleNameOrNumericOidTransitive( superior, schema );
        }

        return null;
    }


    /**
     * Gets the syntax description OID of the given or the
     * superior attribute type description.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return the syntax description OID of the given or the
     *         superior attribute type description, may be null
     */
    public static String getSyntaxNumericOidTransitive( AttributeType atd, Schema schema )
    {
        if ( atd.getSyntaxOid() != null )
        {
            return atd.getSyntaxOid();
        }

        if ( atd.getSuperiorOid() != null && schema.hasAttributeTypeDescription( atd.getSuperiorOid() ) )
        {
            AttributeType superior = schema.getAttributeTypeDescription( atd.getSuperiorOid() );
            return getSyntaxNumericOidTransitive( superior, schema );
        }

        return null;
    }


    /**
     * Gets the syntax length of the given or the
     * superior attribute type description.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return the syntax length of the given or the
     *         superior attribute type description, may be null
     */
    public static long getSyntaxLengthTransitive( AttributeType atd, Schema schema )
    {
        if ( atd.getSyntaxLength() != 0 )
        {
            return atd.getSyntaxLength();
        }

        if ( atd.getSuperiorOid() != null && schema.hasAttributeTypeDescription( atd.getSuperiorOid() ) )
        {
            AttributeType superior = schema.getAttributeTypeDescription( atd.getSuperiorOid() );
            return getSyntaxLengthTransitive( superior, schema );
        }

        return -1;
    }


    /**
     * Gets all matching rule description names the given attribute type
     * description applies to according to the schema's matchin rul use
     * descritpions.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return all matching rule description names this attribute type
     *         description applies to according to the schema's matching 
     *         rule use descriptions
     */
    public static Collection<String> getOtherMatchingRuleDescriptionNames( AttributeType atd, Schema schema )
    {
        Set<String> otherMatchingRules = new TreeSet<String>( nameAndOidComparator );
        for ( MatchingRuleUse mrud : schema.getMatchingRuleUseDescriptions() )
        {
            Collection<String> atdSet = toLowerCaseSet( mrud.getApplicableAttributeOids() );
            if ( atdSet.removeAll( getLowerCaseIdentifiers( atd ) ) )
            {
                otherMatchingRules.addAll( mrud.getNames() );
            }
        }
        return otherMatchingRules;
    }


    /**
     * Gets all attribute type descriptions using the given attribute type
     * descriptions as superior.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return all attribute type descriptions using this attribute type
     *         description as superior
     */
    public static Collection<AttributeType> getDerivedAttributeTypeDescriptions(
        AttributeType atd, Schema schema )
    {
        Set<AttributeType> derivedAtds = new TreeSet<AttributeType>( schemaElementNameComparator );
        for ( AttributeType derivedAtd : schema.getAttributeTypeDescriptions() )
        {
            String superType = derivedAtd.getSuperiorOid();
            if ( superType != null && getLowerCaseIdentifiers( atd ).contains( Strings.toLowerCase( superType ) ) )
            {
                derivedAtds.add( derivedAtd );
            }
        }
        return derivedAtds;
    }


    /**
     * Gets all object class description using the given attribute type
     * description as must attribute.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return all object class description using the given attribute type
     *         description as must attribute
     */
    public static Collection<ObjectClass> getUsedAsMust( AttributeType atd, Schema schema )
    {
        Collection<String> lowerCaseIdentifiers = getLowerCaseIdentifiers( atd );
        Set<ObjectClass> ocds = new TreeSet<ObjectClass>( schemaElementNameComparator );
        for ( ObjectClass ocd : schema.getObjectClassDescriptions() )
        {
            Collection<String> mustSet = toLowerCaseSet( getMustAttributeTypeDescriptionNamesTransitive( ocd, schema ) );
            if ( mustSet.removeAll( lowerCaseIdentifiers ) )
            {
                ocds.add( ocd );
            }
        }
        return ocds;
    }


    /**
     * Gets all object class description using the given attribute type
     * description as may attribute.
     * 
     * @param atd the attribute type description
     * @param schema the schema
     * 
     * @return all object class description using the given attribute type
     *         description as may attribute
     */
    public static Collection<ObjectClass> getUsedAsMay( AttributeType atd, Schema schema )
    {
        Collection<String> lowerCaseIdentifiers = getLowerCaseIdentifiers( atd );
        Set<ObjectClass> ocds = new TreeSet<ObjectClass>( schemaElementNameComparator );
        for ( ObjectClass ocd : schema.getObjectClassDescriptions() )
        {
            Collection<String> mustSet = toLowerCaseSet( getMayAttributeTypeDescriptionNamesTransitive( ocd, schema ) );
            if ( mustSet.removeAll( lowerCaseIdentifiers ) )
            {
                ocds.add( ocd );
            }
        }
        return ocds;
    }


    private static Collection<ObjectClass> getExistingSuperiorObjectClassDescription(
        ObjectClass ocd, Schema schema )
    {
        List<ObjectClass> superiorList = new ArrayList<ObjectClass>();
        for ( String superior : ocd.getSuperiorOids() )
        {
            if ( schema.hasObjectClassDescription( superior ) )
            {
                superiorList.add( schema.getObjectClassDescription( superior ) );
            }
        }
        return superiorList;
    }


    /**
     * Gets the superior object class descriptions of the given object class description.
     * 
     * @param ocd the bject class descriptio
     * @param schema the schema
     * 
     * @return the superior object class descriptions
     */
    public static List<ObjectClass> getSuperiorObjectClassDescriptions( ObjectClass ocd,
        Schema schema )
    {
        List<ObjectClass> superiorList = new ArrayList<ObjectClass>();
        for ( String superior : ocd.getSuperiorOids() )
        {
            superiorList.add( schema.getObjectClassDescription( superior ) );
        }
        return superiorList;
    }


    /**
     * Gets the sub object class descriptions of the given object class description.
     * 
     * @param ocd the object class description
     * @param schema the schema
     * 
     * @return the sub object class descriptions
     */
    public static List<ObjectClass> getSubObjectClassDescriptions( ObjectClass ocd, Schema schema )
    {
        List<ObjectClass> subOcds = new ArrayList<ObjectClass>();
        for ( ObjectClass testOcd : schema.getObjectClassDescriptions() )
        {
            Collection<String> superiorNames = toLowerCaseSet( testOcd.getSuperiorOids() );
            if ( superiorNames.removeAll( getLowerCaseIdentifiers( ocd ) ) )
            {
                subOcds.add( testOcd );
            }
        }
        return subOcds;
    }


    /**
     * Gets the must attribute type description names of the given
     * and all superior object class description, transitively.
     * 
     * @param ocd the object class description
     * @param schema the schema
     * 
     * @return the must attribute type description names of the given
     *         and all superior object class description, transitively
     */
    public static Collection<String> getMustAttributeTypeDescriptionNamesTransitive( ObjectClass ocd,
        Schema schema )
    {
        Set<String> musts = new TreeSet<String>( nameAndOidComparator );
        musts.addAll( ocd.getMustAttributeTypeOids() );
        Collection<ObjectClass> superiors = getExistingSuperiorObjectClassDescription( ocd, schema );
        for ( ObjectClass superior : superiors )
        {
            musts.addAll( getMustAttributeTypeDescriptionNamesTransitive( superior, schema ) );
        }
        return musts;
    }


    /**
     * Gets the may attribute type description names of the given
     * and all superior object class description, transitively.
     * 
     * @param ocd the object class description
     * @param schema the schema
     * 
     * @return the may attribute type description names of the given
     *         and all superior object class description, transitively
     */
    public static Collection<String> getMayAttributeTypeDescriptionNamesTransitive( ObjectClass ocd,
        Schema schema )
    {
        Set<String> mays = new TreeSet<String>( nameAndOidComparator );
        mays.addAll( ocd.getMayAttributeTypeOids() );
        Collection<ObjectClass> superiors = getExistingSuperiorObjectClassDescription( ocd, schema );
        for ( ObjectClass superior : superiors )
        {
            mays.addAll( getMayAttributeTypeDescriptionNamesTransitive( superior, schema ) );
        }
        return mays;
    }


    /**
     * Gets the LDIF line of the given schema element, may be null.
     *
     * @param asd the schema element
     * @return the LDIF line of the given schema element, may be null
     */
    public static String getLdifLine( AbstractSchemaObject asd )
    {
        List<String> ldifLines = asd.getExtensions().get( Schema.RAW_SCHEMA_DEFINITION_LDIF_VALUE );
        String ldifLine = ldifLines != null && !ldifLines.isEmpty() ? ldifLines.get( 0 ) : null;
        return ldifLine;
    }


    private static Collection<String> toLowerCaseSet( Collection<String> names )
    {
        Set<String> set = new HashSet<String>();
        if ( names != null )
        {
            for ( String name : names )
            {
                set.add( Strings.toLowerCase( name ) );
            }
        }
        return set;
    }


    /**
     * Gets the string representation of the given schema element.
     * 
     * @param asd the schema element
     * 
     * @return the string representation of the given schema element
     */
    public static String toString( AbstractSchemaObject asd )
    {
        StringBuffer sb = new StringBuffer();
        if ( asd instanceof LdapSyntax )
        {
            if ( asd.getDescription() != null && asd.getDescription().length() > 0 )
            {
                sb.append( asd.getDescription() );
            }
            else
            {
                sb.append( asd.getOid() );
            }
        }
        else
        {
            boolean first = true;
            for ( String name : asd.getNames() )
            {
                if ( !first )
                {
                    sb.append( ", " ); //$NON-NLS-1$
                }
                sb.append( name );
                first = false;
            }
        }
        return sb.toString();
    }


    /**
     * Checks if the given entry with its attributes is complete and return
     * useful messages if it is not complete. The following checks are performed:
     * 
     * <ul>
     * <li>The objectClass attribute must be present</li>
     * <li>A structural object class must be present</li>
     * <li>All mandatory attributes must be present</li>
     * <li>All attribute must be allowed according to the object classes</li>
     * <li>There mustn't be any empty value</li>
     * </ul>
     * 
     * @return a collection with warn messages if the entry is complete, empty if the entry is complete
     */
    public static Collection<String> getEntryIncompleteMessages( IEntry entry )
    {
        Collection<String> messages = new ArrayList<String>();
        if ( entry != null )
        {
            // check objectClass attribute
            IAttribute ocAttribute = entry.getAttribute( SchemaConstants.OBJECT_CLASS_AT );
            if ( ocAttribute == null )
            {
                messages.add( Messages.getString( "SchemaUtils.NoObjectClass" ) ); //$NON-NLS-1$
            }
            String[] ocValues = ocAttribute.getStringValues();
            boolean structuralObjectClassAvailable = false;
            for ( String ocValue : ocValues )
            {
                ObjectClass ocd = entry.getBrowserConnection().getSchema().getObjectClassDescription(
                    ocValue );
                if ( ocd.getType() == ObjectClassTypeEnum.STRUCTURAL )
                {
                    structuralObjectClassAvailable = true;
                    break;
                }
            }
            if ( !structuralObjectClassAvailable )
            {
                messages.add( Messages.getString( "SchemaUtils.NoStructuralObjectClass" ) ); //$NON-NLS-1$
            }

            // check must-attributes
            Collection<AttributeType> mustAtds = getMustAttributeTypeDescriptions( entry );
            for ( AttributeType mustAtd : mustAtds )
            {
                AttributeHierarchy ah = entry.getAttributeWithSubtypes( mustAtd.getOid() );
                if ( ah == null )
                {
                    messages.add( NLS.bind( Messages.getString( "SchemaUtils.MandatoryAttributeIsMissing" ), //$NON-NLS-1$ 
                        getLowerCaseIdentifiers( mustAtd ) ) );
                }
            }

            // check unallowed attributes
            Collection<AttributeType> allAtds = getAllAttributeTypeDescriptions( entry );
            for ( IAttribute attribute : entry.getAttributes() )
            {
                if ( !attribute.isOperationalAttribute() )
                {
                    AttributeType atd = attribute.getAttributeTypeDescription();
                    if ( !allAtds.contains( atd ) )
                    {
                        messages.add( NLS.bind( Messages.getString( "SchemaUtils.AttributeNotAllowed" ), attribute //$NON-NLS-1$
                            .getDescription() ) );
                    }
                }
            }

            // check empty attributes and empty values
            for ( IAttribute attribute : entry.getAttributes() )
            {
                for ( IValue value : attribute.getValues() )
                {
                    if ( value.isEmpty() )
                    {
                        messages.add( NLS.bind( Messages.getString( "SchemaUtils.EmptyValue" ), //$NON-NLS-1$
                            attribute.getDescription() ) );
                    }
                }
            }
        }

        return messages;
    }

}
