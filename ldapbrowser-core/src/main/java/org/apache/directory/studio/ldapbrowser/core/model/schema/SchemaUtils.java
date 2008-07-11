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

import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.shared.ldap.schema.syntax.AbstractSchemaDescription;
import org.apache.directory.shared.ldap.schema.syntax.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.syntax.LdapSyntaxDescription;
import org.apache.directory.shared.ldap.schema.syntax.MatchingRuleDescription;
import org.apache.directory.shared.ldap.schema.syntax.MatchingRuleUseDescription;
import org.apache.directory.shared.ldap.schema.syntax.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;


/**
 * Utility class for Schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaUtils
{

    private static final Comparator<String> nameAndOidComparator = new Comparator<String>()
    {
        public int compare( String s1, String s2 )
        {
            if ( s1.matches( "[0-9\\.]+" ) && !s2.matches( "[0-9\\.]+" ) )
            {
                return 1;
            }
            else if ( !s1.matches( "[0-9\\.]+" ) && s2.matches( "[0-9\\.]+" ) )
            {
                return -1;
            }
            else
            {
                return s1.compareToIgnoreCase( s2 );
            }
        }
    };
    
    private static final Comparator<AbstractSchemaDescription> schemaElementNameComparator = new Comparator<AbstractSchemaDescription>()
    {
        public int compare( AbstractSchemaDescription s1, AbstractSchemaDescription s2 )
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
    public static Collection<String> getNames( Collection<? extends AbstractSchemaDescription> asds )
    {
        Set<String> nameSet = new TreeSet<String>( nameAndOidComparator );
        for ( AbstractSchemaDescription asd : asds )
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
    public static String[] getNamesAsArray( Collection<? extends AbstractSchemaDescription> asds )
    {
        return getNames( asds ).toArray( new String[0] );
    }
    

    /**
     * Get the numeric OIDs of the given schema descriptions.
     * 
     * @return the numeric OIDs of the given schema descriptions
     */
    public Collection<String> getNumericOids( Collection<? extends AbstractSchemaDescription> descritpions )
    {
        Set<String> oids = new HashSet<String>();
        for ( AbstractSchemaDescription asd : descritpions )
        {
            oids.add( asd.getNumericOid() );
    
        }
        return oids;
    }


    /**
     * Gets the identifiers of the given schema descriptions.
     * 
     * @param asd the the schema description
     * 
     * @return the identifiers
     */
    public static Collection<String> getLowerCaseIdentifiers( AbstractSchemaDescription asd )
    {
        Set<String> identiers = new HashSet<String>();
        if ( asd.getNumericOid() != null )
        {
            identiers.add( asd.getNumericOid().toLowerCase() );
        }
        if ( asd.getNames() != null && !asd.getNames().isEmpty() )
        {
            for ( String name : asd.getNames() )
            {
                if ( name != null )
                {
                    identiers.add( name.toLowerCase() );
                }
            }
        }
        return identiers;
    }


    /**
     * Gets all operational attribute type descriptions.
     * 
     * @param schema the schema
     * 
     * @return all operational attributes types
     */
    public static Collection<AttributeTypeDescription> getOperationalAttributeDescriptions( Schema schema )
    {
        Set<AttributeTypeDescription> operationalAtds = new HashSet<AttributeTypeDescription>();
        for ( AttributeTypeDescription atd : schema.getAttributeTypeDescriptions() )
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
    public static Collection<AttributeTypeDescription> getUserAttributeDescriptions( Schema schema )
    {
        Set<AttributeTypeDescription> userAtds = new HashSet<AttributeTypeDescription>();
        for ( AttributeTypeDescription atd : schema.getAttributeTypeDescriptions() )
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
     * <li>the usage differs from USER_APPLICATIONS or
     * <li>if is not declared in the schema and contains the dummy extension
     * 
     * @param atd the attribute type description
     * 
     * @return true, if is operational
     */
    public static boolean isOperational( AttributeTypeDescription atd )
    {
        return atd.getUsage() != UsageEnum.USER_APPLICATIONS || atd.getExtensions() == Schema.DUMMY_EXTENSIONS;
    }


    public static boolean isModifyable( AttributeTypeDescription atd )
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
        String[] nonModifyableAttributes = new String[]
            { IAttribute.OPERATIONAL_ATTRIBUTE_CREATORS_NAME, IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP,
                IAttribute.OPERATIONAL_ATTRIBUTE_MODIFIERS_NAME, IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP,
                IAttribute.OPERATIONAL_ATTRIBUTE_STRUCTURAL_OBJECT_CLASS,
                IAttribute.OPERATIONAL_ATTRIBUTE_GOVERNING_STRUCTURE_RULE,

                IAttribute.OPERATIONAL_ATTRIBUTE_SUBSCHEMA_SUBENTRY, IAttribute.OPERATIONAL_ATTRIBUTE_VENDOR_NAME,
                IAttribute.OPERATIONAL_ATTRIBUTE_VENDOR_VERSION,

                IAttribute.OPERATIONAL_ATTRIBUTE_ENTRY_UUID, IAttribute.OPERATIONAL_ATTRIBUTE_HAS_SUBORDINATES,
                IAttribute.OPERATIONAL_ATTRIBUTE_SUBORDINATE_COUNT, IAttribute.OPERATIONAL_ATTRIBUTE_NUM_SUBORDINATES

            };
        for ( int i = 0; i < nonModifyableAttributes.length; i++ )
        {
            String att = nonModifyableAttributes[i];
            if ( att.equalsIgnoreCase( atd.getNumericOid() ) )
            {
                return false;
            }
            for ( String name : atd.getNames() )
            {
                if ( att.equalsIgnoreCase( name ) )
                {
                    return false;
                }
            }
        }

        return true;
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
    public static boolean isString( LdapSyntaxDescription lsd )
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
    public static boolean isBinary( LdapSyntaxDescription lsd )
    {
        // check user-defined binary syntaxes
        Set<String> binarySyntaxOids = BrowserCorePlugin.getDefault().getCorePreferences()
            .getUpperCasedBinarySyntaxOids();
        return binarySyntaxOids.contains( lsd.getNumericOid().toUpperCase() );
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
    public static boolean isString( AttributeTypeDescription atd, Schema schema )
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
    public static boolean isBinary( AttributeTypeDescription atd, Schema schema )
    {
        // check user-defined binary attribute types
        Set<String> binaryAttributeOidsAndNames = BrowserCorePlugin.getDefault().getCorePreferences()
            .getUpperCasedBinaryAttributeOidsAndNames();
        if ( binaryAttributeOidsAndNames.contains( atd.getNumericOid().toUpperCase() ) )
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
            LdapSyntaxDescription lsd = schema.getLdapSyntaxDescription( syntax );
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
    public static Collection<AttributeTypeDescription> getUsedFromAttributeTypeDescriptions( LdapSyntaxDescription lsd,
        Schema schema )
    {
        Set<AttributeTypeDescription> usedFroms = new TreeSet<AttributeTypeDescription>( schemaElementNameComparator );
        for ( AttributeTypeDescription atd : schema.getAttributeTypeDescriptions() )
        {
            String syntax = getSyntaxNumericOidTransitive( atd, schema );
            if ( syntax != null && lsd.getNumericOid() != null
                && syntax.toLowerCase().equals( lsd.getNumericOid().toLowerCase() ) )
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
    public static Collection<AttributeTypeDescription> getUsedFromAttributeTypeDescriptions( MatchingRuleDescription mrd,
        Schema schema )
    {
        Set<AttributeTypeDescription> usedFromSet = new TreeSet<AttributeTypeDescription>( schemaElementNameComparator );
        for ( AttributeTypeDescription atd : schema.getAttributeTypeDescriptions() )
        {
            Collection<String> lowerCaseIdentifiers = getLowerCaseIdentifiers( mrd );
            String emr = getEqualityMatchingRuleNameOrNumericOidTransitive( atd, schema );
            String smr = getSubstringMatchingRuleNameOrNumericOidTransitive( atd, schema );
            String omr = getOrderingMatchingRuleNameOrNumericOidTransitive( atd, schema );
            if ( emr != null && lowerCaseIdentifiers.contains( emr.toLowerCase() ) )
            {
                usedFromSet.add( atd );
            }
            if ( smr != null && lowerCaseIdentifiers.contains( smr.toLowerCase() ) )
            {
                usedFromSet.add( atd );
            }
            if ( omr != null && lowerCaseIdentifiers.contains( omr.toLowerCase() ) )
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
    public static String getEqualityMatchingRuleNameOrNumericOidTransitive( AttributeTypeDescription atd, Schema schema )
    {
        if ( atd.getEqualityMatchingRule() != null )
        {
            return atd.getEqualityMatchingRule();
        }

        if ( atd.getSuperType() != null && schema.hasAttributeTypeDescription( atd.getSuperType() ) )
        {
            AttributeTypeDescription superior = schema.getAttributeTypeDescription( atd.getSuperType() );
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
    public static String getSubstringMatchingRuleNameOrNumericOidTransitive( AttributeTypeDescription atd, Schema schema )
    {
        if ( atd.getSubstringsMatchingRule() != null )
        {
            return atd.getSubstringsMatchingRule();
        }

        if ( atd.getSuperType() != null && schema.hasAttributeTypeDescription( atd.getSuperType() ) )
        {
            AttributeTypeDescription superior = schema.getAttributeTypeDescription( atd.getSuperType() );
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
    public static String getOrderingMatchingRuleNameOrNumericOidTransitive( AttributeTypeDescription atd, Schema schema )
    {
        if ( atd.getOrderingMatchingRule() != null )
        {
            return atd.getOrderingMatchingRule();
        }

        if ( atd.getSuperType() != null && schema.hasAttributeTypeDescription( atd.getSuperType() ) )
        {
            AttributeTypeDescription superior = schema.getAttributeTypeDescription( atd.getSuperType() );
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
    public static String getSyntaxNumericOidTransitive( AttributeTypeDescription atd, Schema schema )
    {
        if ( atd.getSyntax() != null )
        {
            return atd.getSyntax();
        }

        if ( atd.getSuperType() != null && schema.hasAttributeTypeDescription( atd.getSuperType() ) )
        {
            AttributeTypeDescription superior = schema.getAttributeTypeDescription( atd.getSuperType() );
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
    public static int getSyntaxLengthTransitive( AttributeTypeDescription atd, Schema schema )
    {
        if ( atd.getSyntaxLength() != -1 )
        {
            return atd.getSyntaxLength();
        }

        if ( atd.getSuperType() != null && schema.hasAttributeTypeDescription( atd.getSuperType() ) )
        {
            AttributeTypeDescription superior = schema.getAttributeTypeDescription( atd.getSuperType() );
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
    public static Collection<String> getOtherMatchingRuleDescriptionNames( AttributeTypeDescription atd, Schema schema )
    {
        Set<String> otherMatchingRules = new TreeSet<String>( nameAndOidComparator ); 
        for ( MatchingRuleUseDescription mrud : schema.getMatchingRuleUseDescriptions() )
        {
            Collection<String> atdSet = toLowerCaseSet( mrud.getApplicableAttributes() );
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
    public static Collection<AttributeTypeDescription> getDerivedAttributeTypeDescriptions( AttributeTypeDescription atd, Schema schema )
    {
        Set<AttributeTypeDescription> derivedAtds = new TreeSet<AttributeTypeDescription>( schemaElementNameComparator );
        for ( AttributeTypeDescription derivedAtd : schema.getAttributeTypeDescriptions() )
        {
            String superType = derivedAtd.getSuperType();
            if ( superType != null && getLowerCaseIdentifiers( atd ).contains( superType.toLowerCase() ) )
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
    public static Collection<ObjectClassDescription> getUsedAsMust( AttributeTypeDescription atd, Schema schema )
    {
        Collection<String> lowerCaseIdentifiers = getLowerCaseIdentifiers( atd );
        Set<ObjectClassDescription> ocds = new TreeSet<ObjectClassDescription>( schemaElementNameComparator );
        for ( ObjectClassDescription ocd : schema.getObjectClassDescriptions() )
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
    public static Collection<ObjectClassDescription> getUsedAsMay( AttributeTypeDescription atd, Schema schema )
    {
        Collection<String> lowerCaseIdentifiers = getLowerCaseIdentifiers( atd );
        Set<ObjectClassDescription> ocds = new TreeSet<ObjectClassDescription>( schemaElementNameComparator );
        for ( ObjectClassDescription ocd : schema.getObjectClassDescriptions() )
        {
            Collection<String> mustSet = toLowerCaseSet( getMayAttributeTypeDescriptionNamesTransitive( ocd, schema ) );
            if ( mustSet.removeAll( lowerCaseIdentifiers ) )
            {
                ocds.add( ocd );
            }
        }
        return ocds;
    }
    
    
    

    private static Collection<ObjectClassDescription> getExistingSuperiorObjectClassDescription( ObjectClassDescription ocd, Schema schema )
    {
        List<ObjectClassDescription> superiorList = new ArrayList<ObjectClassDescription>();
        for ( String superior : ocd.getSuperiorObjectClasses() )
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
    public static List<ObjectClassDescription> getSuperiorObjectClassDescriptions( ObjectClassDescription ocd, Schema schema )
    {
        List<ObjectClassDescription> superiorList = new ArrayList<ObjectClassDescription>();
        for ( String superior : ocd.getSuperiorObjectClasses() )
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
    public static List<ObjectClassDescription> getSubObjectClassDescriptions( ObjectClassDescription ocd, Schema schema )
    {
        List<ObjectClassDescription> subOcds = new ArrayList<ObjectClassDescription>();
        for ( ObjectClassDescription testOcd : schema.getObjectClassDescriptions() )
        {
            Collection<String> superiorNames = toLowerCaseSet( testOcd.getSuperiorObjectClasses() );
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
    public static Collection<String> getMustAttributeTypeDescriptionNamesTransitive( ObjectClassDescription ocd, Schema schema )
    {
        Set<String> musts = new TreeSet<String>( nameAndOidComparator ); 
        musts.addAll( ocd.getMustAttributeTypes() );
        Collection<ObjectClassDescription> superiors = getExistingSuperiorObjectClassDescription( ocd, schema );
        for ( ObjectClassDescription superior : superiors )
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
    public static Collection<String> getMayAttributeTypeDescriptionNamesTransitive( ObjectClassDescription ocd, Schema schema )
    {
        Set<String> mays= new TreeSet<String>( nameAndOidComparator ); 
        mays.addAll( ocd.getMayAttributeTypes() );
        Collection<ObjectClassDescription> superiors = getExistingSuperiorObjectClassDescription( ocd, schema );
        for ( ObjectClassDescription superior : superiors )
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
    public static String getLdifLine( AbstractSchemaDescription asd )
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
                set.add( name.toLowerCase() );
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
    public static String toString( AbstractSchemaDescription asd )
    {
        StringBuffer sb = new StringBuffer();
        if ( asd instanceof LdapSyntaxDescription )
        {
            if ( asd.getDescription() != null && asd.getDescription().length() > 0 )
            {
                sb.append( asd.getDescription() );
            }
            else
            {
                sb.append( asd.getNumericOid() );
            }
        }
        else
        {
            boolean first = true;
            for ( String name : asd.getNames() )
            {
                if ( !first )
                {
                    sb.append( ", " );
                }
                sb.append( name );
                first = false;
            }
        }
        return sb.toString();
    }
    

}
