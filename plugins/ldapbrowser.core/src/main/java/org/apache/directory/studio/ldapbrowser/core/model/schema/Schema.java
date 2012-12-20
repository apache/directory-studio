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


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.constants.SchemaConstants;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.MatchingRuleUse;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.api.ldap.model.schema.parsers.AttributeTypeDescriptionSchemaParser;
import org.apache.directory.api.ldap.model.schema.parsers.LdapSyntaxDescriptionSchemaParser;
import org.apache.directory.api.ldap.model.schema.parsers.MatchingRuleDescriptionSchemaParser;
import org.apache.directory.api.ldap.model.schema.parsers.MatchingRuleUseDescriptionSchemaParser;
import org.apache.directory.api.ldap.model.schema.parsers.ObjectClassDescriptionSchemaParser;
import org.apache.directory.api.util.Strings;
import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.core.model.AttributeDescription;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifEnumeration;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.parser.LdifParser;


/**
 * The schema is the central access point to all schema information.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Schema
{

    public static final String SCHEMA_FILTER = "(objectClass=subschema)"; //$NON-NLS-1$

    public static final String RAW_SCHEMA_DEFINITION_LDIF_VALUE = "RAW_SCHEMA_DEFINITION_LDIF_VALUE"; //$NON-NLS-1$

    public static final String DN_SYNTAX_OID = "1.3.6.1.4.1.1466.115.121.1.12"; //$NON-NLS-1$

    public static final LdapSyntax DUMMY_LDAP_SYNTAX;
    static
    {
        DUMMY_LDAP_SYNTAX = new LdapSyntax( "", "" ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static final HashMap<String, List<String>> DUMMY_EXTENSIONS;
    static
    {
        DUMMY_EXTENSIONS = new HashMap<String, List<String>>();
        List<String> dummyValues = new ArrayList<String>();
        dummyValues.add( "DUMMY" ); //$NON-NLS-1$
        DUMMY_EXTENSIONS.put( "X-DUMMY", dummyValues ); //$NON-NLS-1$
    }

    public static final Schema DEFAULT_SCHEMA;
    static
    {
        Schema defaultSchema = null;

        try
        {
            URL url = Schema.class.getClassLoader().getResource( "default_schema.ldif" ); //$NON-NLS-1$
            InputStream is = url.openStream();
            Reader reader = new InputStreamReader( is );

            defaultSchema = new Schema();
            defaultSchema.defaultSchema = true;
            defaultSchema.loadFromLdif( reader );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        DEFAULT_SCHEMA = defaultSchema;
    }

    private boolean defaultSchema = false;


    public boolean isDefault()
    {
        return this.defaultSchema;
    }

    private LdifContentRecord schemaRecord;

    private Dn dn;

    private String createTimestamp;

    private String modifyTimestamp;

    private Map<String, MutableObjectClass> ocdMapByNameOrNumericOid;

    private Map<String, AttributeType> atdMapByNameOrNumericOid;

    private Map<String, LdapSyntax> lsdMapByNumericOid;

    private Map<String, MatchingRule> mrdMapByNameOrNumericOid;

    private Map<String, MatchingRuleUse> mrudMapByNameOrNumericOid;


    /**
     * Creates a new instance of Schema.
     */
    public Schema()
    {
        this.schemaRecord = null;
        this.dn = null;
        this.createTimestamp = null;
        this.modifyTimestamp = null;
        this.ocdMapByNameOrNumericOid = new HashMap<String, MutableObjectClass>();
        this.atdMapByNameOrNumericOid = new HashMap<String, AttributeType>();
        this.lsdMapByNumericOid = new HashMap<String, LdapSyntax>();
        this.mrdMapByNameOrNumericOid = new HashMap<String, MatchingRule>();
        this.mrudMapByNameOrNumericOid = new HashMap<String, MatchingRuleUse>();
    }


    /**
     * Loads all schema elements from the given reader. The input must be in
     * LDIF format.
     * 
     * @param reader the reader
     */
    public void loadFromLdif( Reader reader )
    {
        try
        {
            LdifParser parser = new LdifParser();
            LdifEnumeration enumeration = parser.parse( reader );
            if ( enumeration.hasNext() )
            {
                LdifContainer container = enumeration.next();
                if ( container instanceof LdifContentRecord )
                {
                    LdifContentRecord schemaRecord = ( LdifContentRecord ) container;
                    parseSchemaRecord( schemaRecord );
                }
            }
        }
        catch ( Exception e )
        {
            // TODO: exception handling
            System.out.println( "Schema#loadFromLdif: " + e.toString() ); //$NON-NLS-1$
        }
    }


    /**
     * Load all schema elements from the given schema record.
     * 
     * @param schemaRecord the schema record
     */
    public void loadFromRecord( LdifContentRecord schemaRecord )
    {
        try
        {
            parseSchemaRecord( schemaRecord );
        }
        catch ( Exception e )
        {
            // TODO: exception handling
            System.out.println( "Schema#loadFromRecord: " + e.toString() ); //$NON-NLS-1$
        }
    }


    /**
     * Saves the schema in LDIF format to the given writer.
     * 
     * @param writer
     */
    public void saveToLdif( Writer writer )
    {
        try
        {
            writer.write( getSchemaRecord().toFormattedString( LdifFormatParameters.DEFAULT ) );
        }
        catch ( Exception e )
        {
            // TODO: exception handling
            System.out.println( "Schema#saveToLdif: " + e.toString() ); //$NON-NLS-1$
        }
    }


    /**
     * Parses the schema record.
     * 
     * @param schemaRecord the schema record
     * 
     * @throws Exception the exception
     */
    private void parseSchemaRecord( LdifContentRecord schemaRecord ) throws Exception
    {
        setSchemaRecord( schemaRecord );
        setDn( new Dn( schemaRecord.getDnLine().getValueAsString() ) );

        ObjectClassDescriptionSchemaParser ocdPparser = new ObjectClassDescriptionSchemaParser();
        ocdPparser.setQuirksMode( true );
        AttributeTypeDescriptionSchemaParser atdParser = new AttributeTypeDescriptionSchemaParser();
        atdParser.setQuirksMode( true );
        LdapSyntaxDescriptionSchemaParser lsdParser = new LdapSyntaxDescriptionSchemaParser();
        lsdParser.setQuirksMode( true );
        MatchingRuleDescriptionSchemaParser mrdParser = new MatchingRuleDescriptionSchemaParser();
        mrdParser.setQuirksMode( true );
        MatchingRuleUseDescriptionSchemaParser mrudParser = new MatchingRuleUseDescriptionSchemaParser();
        mrudParser.setQuirksMode( true );

        LdifAttrValLine[] lines = schemaRecord.getAttrVals();
        for ( int i = 0; i < lines.length; i++ )
        {
            LdifAttrValLine line = lines[i];
            String attributeName = line.getUnfoldedAttributeDescription();
            String value = line.getValueAsString();
            List<String> ldifValues = new ArrayList<String>( 1 );
            ldifValues.add( value );

            try
            {
                if ( attributeName.equalsIgnoreCase( SchemaConstants.OBJECT_CLASSES_AT ) )
                {
                    MutableObjectClass ocd = ocdPparser.parseObjectClassDescription( value );
                    ocd.addExtension( RAW_SCHEMA_DEFINITION_LDIF_VALUE, ldifValues );
                    addObjectClass( ocd );
                }
                else if ( attributeName.equalsIgnoreCase( SchemaConstants.ATTRIBUTE_TYPES_AT ) )
                {
                    AttributeType atd = atdParser.parseAttributeTypeDescription( value );
                    atd.addExtension( RAW_SCHEMA_DEFINITION_LDIF_VALUE, ldifValues );
                    addAttributeType( atd );
                }
                else if ( attributeName.equalsIgnoreCase( SchemaConstants.LDAP_SYNTAXES_AT ) )
                {
                    LdapSyntax lsd = lsdParser.parseLdapSyntaxDescription( value );
                    if ( StringUtils.isEmpty( lsd.getDescription() )
                        && Utils.getOidDescription( lsd.getOid() ) != null )
                    {
                        lsd.setDescription( Utils.getOidDescription( lsd.getOid() ) );
                    }
                    lsd.addExtension( RAW_SCHEMA_DEFINITION_LDIF_VALUE, ldifValues );
                    addLdapSyntax( lsd );
                }
                else if ( attributeName.equalsIgnoreCase( SchemaConstants.MATCHING_RULES_AT ) )
                {
                    MatchingRule mrd = mrdParser.parseMatchingRuleDescription( value );
                    mrd.addExtension( RAW_SCHEMA_DEFINITION_LDIF_VALUE, ldifValues );
                    addMatchingRule( mrd );
                }
                else if ( attributeName.equalsIgnoreCase( SchemaConstants.MATCHING_RULE_USE_AT ) )
                {
                    MatchingRuleUse mrud = mrudParser.parseMatchingRuleUseDescription( value );
                    mrud.addExtension( RAW_SCHEMA_DEFINITION_LDIF_VALUE, ldifValues );
                    addMatchingRuleUse( mrud );
                }
                else if ( attributeName.equalsIgnoreCase( SchemaConstants.CREATE_TIMESTAMP_AT ) )
                {
                    setCreateTimestamp( value );
                }
                else if ( attributeName.equalsIgnoreCase( SchemaConstants.MODIFY_TIMESTAMP_AT ) )
                {
                    setModifyTimestamp( value );
                }
            }
            catch ( Exception e )
            {
                // TODO: exception handling
                System.out.println( "Error reading schema: " + attributeName + " = " + value ); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println( e.getMessage() );
            }
        }

        for ( AttributeType atd : getAttributeTypeDescriptions() )
        {
            // assume all received syntaxes in attributes are valid -> create pseudo syntaxes if missing
            String syntaxOid = atd.getSyntaxOid();
            if ( syntaxOid != null && !hasLdapSyntaxDescription( syntaxOid ) )
            {
                LdapSyntax lsd = new LdapSyntax( syntaxOid );
                lsd.setDescription( Utils.getOidDescription( syntaxOid ) );
                addLdapSyntax( lsd );
            }

            // assume all received matching rules in attributes are valid -> create pseudo matching rules if missing
            String emr = atd.getEqualityOid();
            String omr = atd.getOrderingOid();
            String smr = atd.getSubstringOid();
            checkMatchingRules( emr, omr, smr );
        }

        // set extensibleObject may attributes
        MutableObjectClass extensibleObjectOcd = this
            .getObjectClassDescription( SchemaConstants.EXTENSIBLE_OBJECT_OC );
        Collection<AttributeType> userAtds = SchemaUtils.getUserAttributeDescriptions( this );
        Collection<String> atdNames = SchemaUtils.getNames( userAtds );
        List<String> atdNames2 = new ArrayList<String>( atdNames );
        extensibleObjectOcd.setMayAttributeTypeOids( atdNames2 );
    }


    private void checkMatchingRules( String... matchingRules )
    {
        for ( String matchingRule : matchingRules )
        {
            if ( matchingRule != null && !hasMatchingRuleDescription( matchingRule ) )
            {
                MatchingRule mrd = new MatchingRule( matchingRule );
                mrd.addName( matchingRule );
                addMatchingRule( mrd );
            }
        }
    }


    /**
     * Gets the schema record.
     * 
     * @return the schema record when the schema was created using the
     *         loadFromLdif() method, null otherwise
     */
    public LdifContentRecord getSchemaRecord()
    {
        return schemaRecord;
    }


    /**
     * Sets the schema record.
     * 
     * @param schemaRecord the new schema record
     */
    public void setSchemaRecord( LdifContentRecord schemaRecord )
    {
        this.schemaRecord = schemaRecord;
    }


    /**
     * Gets the Dn of the schema record, may be null.
     * 
     * @return the Dn of the schema record, may be null
     */
    public Dn getDn()
    {
        return dn;
    }


    /**
     * Sets the Dn.
     * 
     * @param dn the new Dn
     */
    public void setDn( Dn dn )
    {
        this.dn = dn;
    }


    /**
     * Gets the create timestamp of the schema record, may be null.
     * 
     * @return the create timestamp of the schema record, may be null
     */
    public String getCreateTimestamp()
    {
        return createTimestamp;
    }


    /**
     * Sets the creates the timestamp.
     * 
     * @param createTimestamp the new creates the timestamp
     */
    public void setCreateTimestamp( String createTimestamp )
    {
        this.createTimestamp = createTimestamp;
    }


    /**
     * Gets the modify timestamp of the schema record, may be null.
     * 
     * @return the modify timestamp of the schema record, may be null
     */
    public String getModifyTimestamp()
    {
        return modifyTimestamp;
    }


    /**
     * Sets the modify timestamp.
     * 
     * @param modifyTimestamp the new modify timestamp
     */
    public void setModifyTimestamp( String modifyTimestamp )
    {
        this.modifyTimestamp = modifyTimestamp;
    }


    ////////////////////// Object Class Description //////////////////////

    /**
     * Adds the object class description.
     * 
     * @param ocd the object class description
     */
    private void addObjectClass( MutableObjectClass ocd )
    {
        if ( ocd.getOid() != null )
        {
            ocdMapByNameOrNumericOid.put( Strings.toLowerCase( ocd.getOid() ), ocd );
        }
        if ( ocd.getNames() != null && !ocd.getNames().isEmpty() )
        {
            for ( String ocdName : ocd.getNames() )
            {
                ocdMapByNameOrNumericOid.put( Strings.toLowerCase( ocdName ), ocd );
            }
        }
    }


    /**
     * Gets the object class descriptions.
     * 
     * @return the object class descriptions
     */
    public Collection<ObjectClass> getObjectClassDescriptions()
    {
        Set<ObjectClass> set = new HashSet<ObjectClass>( ocdMapByNameOrNumericOid.values() );
        return set;
    }


    /**
     * Checks if an object class descriptions with the given name or OID exists.
     * 
     * @param nameOrOid the name numeric OID of the object class description
     * 
     * @return true if an object class description with the given name
     *         or OID exists.
     */
    public boolean hasObjectClassDescription( String nameOrOid )
    {
        if ( nameOrOid != null )
        {
            return ocdMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) );
        }

        return false;
    }


    /**
     * Returns the object class description of the given name. If no such
     * object exists the default or a dummy object class description is
     * returned.
     * 
     * @param nameOrOid the name numeric OID of the object class description
     * 
     * @return the object class description, or the default or a dummy
     */
    public MutableObjectClass getObjectClassDescription( String nameOrOid )
    {
        if ( ocdMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) ) )
        {
            return ocdMapByNameOrNumericOid.get( Strings.toLowerCase( nameOrOid ) );
        }
        else if ( !isDefault() )
        {
            return DEFAULT_SCHEMA.getObjectClassDescription( nameOrOid );
        }
        else
        {
            // DUMMY
            List<String> names = new ArrayList<String>();
            names.add( nameOrOid );
            MutableObjectClass ocd = new MutableObjectClass( nameOrOid );
            ocd.setNames( names );
            ocd.setExtensions( DUMMY_EXTENSIONS );
            return ocd;
        }
    }


    ////////////////////// Attribute Type Description //////////////////////

    /**
     * Adds the attribute type description.
     * 
     * @param atd the attribute type description
     */
    private void addAttributeType( AttributeType atd )
    {
        if ( atd.getOid() != null )
        {
            atdMapByNameOrNumericOid.put( Strings.toLowerCase( atd.getOid() ), atd );
        }
        if ( atd.getNames() != null && !atd.getNames().isEmpty() )
        {
            for ( String atdName : atd.getNames() )
            {
                atdMapByNameOrNumericOid.put( Strings.toLowerCase( atdName ), atd );
            }
        }
    }


    /**
     * Gets the attribute type descriptions.
     * 
     * @return the attribute type descriptions
     */
    public Collection<AttributeType> getAttributeTypeDescriptions()
    {
        Set<AttributeType> set = new HashSet<AttributeType>( atdMapByNameOrNumericOid.values() );
        return set;
    }


    /**
     * Checks if an attribute type descriptions with the given name or OID exists.
     * 
     * @param nameOrOid the name numeric OID of the attribute type description
     * 
     * @return true if an attribute type description with the given name
     *         or OID exists.
     */
    public boolean hasAttributeTypeDescription( String nameOrOid )
    {
        if ( nameOrOid != null )
        {
            return atdMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) );
        }

        return false;
    }


    /**
     * Returns the attribute type description of the given name. If no such
     * object exists the default or a dummy attribute type description is
     * returned.
     * 
     * @param nameOrOid the name numeric OID of the attribute type description
     * 
     * @return the attribute type description, or the default or a dummy
     */
    public AttributeType getAttributeTypeDescription( String nameOrOid )
    {
        AttributeDescription ad = new AttributeDescription( nameOrOid );
        String attributeType = ad.getParsedAttributeType();

        if ( atdMapByNameOrNumericOid.containsKey( Strings.toLowerCase( attributeType ) ) )
        {
            return atdMapByNameOrNumericOid.get( Strings.toLowerCase( attributeType ) );
        }
        else if ( !isDefault() )
        {
            return DEFAULT_SCHEMA.getAttributeTypeDescription( attributeType );
        }
        else
        {
            // DUMMY
            List<String> attributeTypes = new ArrayList<String>();
            attributeTypes.add( attributeType );
            MutableAttributeType atd = new MutableAttributeType( attributeType );
            atd.setNames( attributeTypes );
            atd.setUserModifiable( true );
            atd.setUsage( UsageEnum.USER_APPLICATIONS );
            atd.setExtensions( DUMMY_EXTENSIONS );
            return atd;
        }
    }


    //////////////////////// LDAP Syntax Description ////////////////////////

    /**
     * Adds the LDAP syntax description.
     * 
     * @param lsd the LDAP syntax description
     */
    private void addLdapSyntax( LdapSyntax lsd )
    {
        if ( lsd.getOid() != null )
        {
            lsdMapByNumericOid.put( Strings.toLowerCase( lsd.getOid() ), lsd );
        }
    }


    /**
     * Gets the LDAP syntax descriptions.
     * 
     * @return the LDAP syntax descriptions
     */
    public Collection<LdapSyntax> getLdapSyntaxDescriptions()
    {
        Set<LdapSyntax> set = new HashSet<LdapSyntax>( lsdMapByNumericOid.values() );
        return set;
    }


    /**
     * Checks if an LDAP syntax descriptions with the given OID exists.
     * 
     * @param numericOid the numeric OID of the LDAP syntax description
     * 
     * @return true if an LDAP syntax description with the given OID exists.
     */
    public boolean hasLdapSyntaxDescription( String numericOid )
    {
        if ( numericOid != null )
        {
            return lsdMapByNumericOid.containsKey( Strings.toLowerCase( numericOid ) );
        }

        return false;
    }


    /**
     * Returns the syntax description of the given OID. If no such object
     * exists the default or a dummy syntax description is returned.
     * 
     * @param numericOid the numeric OID of the LDAP syntax description
     * 
     * @return the attribute type description or the default or a dummy
     */
    public LdapSyntax getLdapSyntaxDescription( String numericOid )
    {
        if ( numericOid == null )
        {
            return DUMMY_LDAP_SYNTAX;
        }
        else if ( lsdMapByNumericOid.containsKey( Strings.toLowerCase( numericOid ) ) )
        {
            return lsdMapByNumericOid.get( Strings.toLowerCase( numericOid ) );
        }
        else if ( !isDefault() )
        {
            return DEFAULT_SCHEMA.getLdapSyntaxDescription( numericOid );
        }
        else
        {
            // DUMMY
            LdapSyntax lsd = new LdapSyntax( numericOid );
            lsd.setExtensions( DUMMY_EXTENSIONS );
            return lsd;
        }
    }


    ////////////////////////// Matching Rule Description //////////////////////////

    /**
     * Adds the matching rule description.
     * 
     * @param mrud the matching rule description
     */
    private void addMatchingRule( MatchingRule mrd )
    {
        if ( mrd.getOid() != null )
        {
            mrdMapByNameOrNumericOid.put( Strings.toLowerCase( mrd.getOid() ), mrd );
        }
        if ( mrd.getNames() != null && !mrd.getNames().isEmpty() )
        {
            for ( String mrdName : mrd.getNames() )
            {
                mrdMapByNameOrNumericOid.put( Strings.toLowerCase( mrdName ), mrd );
            }
        }
    }


    /**
     * Gets the matching rule descriptions.
     * 
     * @return the matching rule descriptions
     */
    public Collection<MatchingRule> getMatchingRuleDescriptions()
    {
        Set<MatchingRule> set = new HashSet<MatchingRule>( mrdMapByNameOrNumericOid.values() );
        return set;
    }


    /**
     * Checks if an matching rule descriptions with the given name or OID exists.
     * 
     * @param nameOrOid the name numeric OID of the matching rule description
     * 
     * @return true if a matching rule description with the given name
     *         or OID exists.
     */
    public boolean hasMatchingRuleDescription( String nameOrOid )
    {
        if ( nameOrOid != null )
        {
            return mrdMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) );
        }

        return false;
    }


    /**
     * Returns the matching rule description of the given name or OID. If no
     * such object exists the default or a dummy matching rule description
     * is returned.
     * 
     * @param nameOrOid the name or numeric OID of the matching rule description
     * 
     * @return the matching rule description or the default or a dummy
     */
    public MatchingRule getMatchingRuleDescription( String nameOrOid )
    {
        if ( mrdMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) ) )
        {
            return mrdMapByNameOrNumericOid.get( Strings.toLowerCase( nameOrOid ) );
        }
        else if ( !isDefault() )
        {
            return DEFAULT_SCHEMA.getMatchingRuleDescription( nameOrOid );
        }
        else
        {
            // DUMMY
            MatchingRule mrd = new MatchingRule( nameOrOid );
            mrd.setExtensions( DUMMY_EXTENSIONS );
            return mrd;
        }
    }


    //////////////////////// Matching Rule Use Description ////////////////////////

    /**
     * Adds the matching rule use description.
     * 
     * @param mrud the matching rule use description
     */
    private void addMatchingRuleUse( MatchingRuleUse mrud )
    {
        if ( mrud.getOid() != null )
        {
            mrudMapByNameOrNumericOid.put( Strings.toLowerCase( mrud.getOid() ), mrud );
        }
        if ( mrud.getNames() != null && !mrud.getNames().isEmpty() )
        {
            for ( String mrudName : mrud.getNames() )
            {
                mrudMapByNameOrNumericOid.put( Strings.toLowerCase( mrudName ), mrud );
            }
        }
    }


    /**
     * Gets the matching rule use descriptions.
     * 
     * @return the matching rule use descriptions
     */
    public Collection<MatchingRuleUse> getMatchingRuleUseDescriptions()
    {
        Set<MatchingRuleUse> set = new HashSet<MatchingRuleUse>( mrudMapByNameOrNumericOid
            .values() );
        return set;
    }


    /**
     * Checks if an matching rule use descriptions with the given name or OID exists.
     * 
     * @param nameOrOid the name numeric OID of the matching rule use description
     * 
     * @return true if a matching rule use description with the given name
     *         or OID exists.
     */
    public boolean hasMatchingRuleUseDescription( String nameOrOid )
    {
        if ( nameOrOid != null )
        {
            return mrudMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) );
        }

        return false;
    }


    /**
     * Returns the matching rule use description of the given name or OID. If no
     * such object exists the default or a dummy matching rule use description
     * is returned.
     * 
     * @param nameOrOid the name or numeric OID of the matching rule use description
     * 
     * @return the matching rule use description or the default or a dummy
     */
    public MatchingRuleUse getMatchingRuleUseDescription( String nameOrOid )
    {
        if ( mrudMapByNameOrNumericOid.containsKey( Strings.toLowerCase( nameOrOid ) ) )
        {
            return mrudMapByNameOrNumericOid.get( Strings.toLowerCase( nameOrOid ) );
        }
        else if ( !isDefault() )
        {
            return DEFAULT_SCHEMA.getMatchingRuleUseDescription( nameOrOid );
        }
        else
        {
            // DUMMY
            MatchingRuleUse mrud = new MatchingRuleUse( nameOrOid );
            mrud.setExtensions( DUMMY_EXTENSIONS );
            return mrud;
        }
    }

}
