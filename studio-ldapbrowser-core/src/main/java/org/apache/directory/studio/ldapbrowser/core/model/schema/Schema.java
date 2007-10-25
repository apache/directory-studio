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
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.model.AttributeDescription;
import org.apache.directory.studio.ldapbrowser.core.model.DN;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.LdifEnumeration;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContentRecord;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.lines.LdifAttrValLine;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.parser.LdifParser;
import org.apache.directory.studio.ldapbrowser.core.model.schema.parser.SchemaLexer;
import org.apache.directory.studio.ldapbrowser.core.model.schema.parser.SchemaParser;


public class Schema implements Serializable
{

    private static final long serialVersionUID = 2439355717760227167L;

    public static final String SCHEMA_FILTER = "(objectClass=subschema)";
    
    public static final String SCHEMA_ATTRIBUTE_OBJECTCLASSES = "objectClasses";

    public static final String SCHEMA_ATTRIBUTE_ATTRIBUTETYPES = "attributeTypes";

    public static final String SCHEMA_ATTRIBUTE_LDAPSYNTAXES = "ldapSyntaxes";

    public static final String SCHEMA_ATTRIBUTE_MATCHINGRULES = "matchingRules";

    public static final String SCHEMA_ATTRIBUTE_MATCHINGRULEUSE = "matchingRuleUse";

    public static final Schema DEFAULT_SCHEMA;
    static
    {
        Schema defaultSchema = null;

        try
        {
            URL url = Schema.class.getClassLoader().getResource(
                "default_schema.ldif" );
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

    private DN dn;

    private String[] objectClasses;

    private String createTimestamp;

    private String modifyTimestamp;

    private Map ocdMapByName;

    private Map atdMapByName;

    private Map lsdMapByNumericOID;

    private Map mrdMapByName;

    private Map mrdMapByNumericOID;

    private Map mrudMapByName;

    private Map mrudMapByNumericOID;


    public Schema()
    {
        this.schemaRecord = null;
        this.dn = null;
        this.objectClasses = new String[0];
        this.createTimestamp = null;
        this.modifyTimestamp = null;
        this.ocdMapByName = new HashMap();
        this.atdMapByName = new HashMap();
        this.lsdMapByNumericOID = new HashMap();
        this.mrdMapByName = new HashMap();
        this.mrdMapByNumericOID = new HashMap();
        this.mrudMapByName = new HashMap();
        this.mrudMapByNumericOID = new HashMap();
    }


    /**
     * Loads all schema elements from the given reader. The input must be in
     * LDIF format.
     * 
     * @param reader
     */
    public void loadFromLdif( Reader reader )
    {
        try
        {
            LdifParser parser = new LdifParser();
            LdifEnumeration enumeration = parser.parse( reader );
            if ( enumeration.hasNext( null ) )
            {
                LdifContainer container = enumeration.next( null );
                if ( container instanceof LdifContentRecord )
                {
                    LdifContentRecord schemaRecord = ( LdifContentRecord ) container;
                    this.parseSchemaRecord( schemaRecord );
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "Schema#loadFromLdif: " + e.toString() );
        }
    }


    public void loadFromRecord( LdifContentRecord schemaRecord )
    {
        try
        {
            this.parseSchemaRecord( schemaRecord );
        }
        catch ( Exception e )
        {
            System.out.println( "Schema#loadFromRecord: " + e.toString() );
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
            writer.write( this.getSchemaRecord().toFormattedString() );
        }
        catch ( Exception e )
        {
            System.out.println( "Schema#saveToLdif: " + e.toString() );
        }
    }


    private void parseSchemaRecord( LdifContentRecord schemaRecord ) throws Exception
    {

        this.setSchemaRecord( schemaRecord );
        this.setDn( new DN( schemaRecord.getDnLine().getValueAsString() ) );

        LdifAttrValLine[] lines = schemaRecord.getAttrVals();
        for ( int i = 0; i < lines.length; i++ )
        {
            LdifAttrValLine line = lines[i];
            String attributeName = line.getUnfoldedAttributeDescription();
            String value = line.getValueAsString();

            SchemaLexer lexer = new SchemaLexer( new StringReader( value ) );
            SchemaParser parser = new SchemaParser( lexer );

            try
            {
                if ( attributeName.equalsIgnoreCase( Schema.SCHEMA_ATTRIBUTE_OBJECTCLASSES ) )
                {
                    ObjectClassDescription ocd = parser.objectClassDescription();
                    ocd.setSchema( this );
                    ocd.setLine( line );
                    this.addObjectClassDescription( ocd );
                }
                else if ( attributeName.equalsIgnoreCase( Schema.SCHEMA_ATTRIBUTE_ATTRIBUTETYPES ) )
                {
                    AttributeTypeDescription atd = parser.attributeTypeDescription();
                    atd.setSchema( this );
                    atd.setLine( line );
                    this.addAttributeTypeDescription( atd );
                }
                else if ( attributeName.equalsIgnoreCase( Schema.SCHEMA_ATTRIBUTE_LDAPSYNTAXES ) )
                {
                    LdapSyntaxDescription lsd = parser.syntaxDescription();
                    lsd.setSchema( this );
                    lsd.setLine( line );
                    this.addLdapSyntaxDescription( lsd );
                }
                else if ( attributeName.equalsIgnoreCase( Schema.SCHEMA_ATTRIBUTE_MATCHINGRULES ) )
                {
                    MatchingRuleDescription mrd = parser.matchingRuleDescription();
                    mrd.setSchema( this );
                    mrd.setLine( line );
                    this.addMatchingRuleDescription( mrd );
                }
                else if ( attributeName.equalsIgnoreCase( Schema.SCHEMA_ATTRIBUTE_MATCHINGRULEUSE ) )
                {
                    MatchingRuleUseDescription mrud = parser.matchingRuleUseDescription();
                    mrud.setSchema( this );
                    mrud.setLine( line );
                    this.addMatchingRuleUseDescription( mrud );
                }
                else if ( attributeName.equalsIgnoreCase( IAttribute.OPERATIONAL_ATTRIBUTE_CREATE_TIMESTAMP ) )
                {
                    this.setCreateTimestamp( value );
                }
                else if ( attributeName.equalsIgnoreCase( IAttribute.OPERATIONAL_ATTRIBUTE_MODIFY_TIMESTAMP ) )
                {
                    this.setModifyTimestamp( value );
                }
            }
            catch ( Exception e )
            {
                System.out.println( e.getMessage() + ": " + attributeName + " - " + value );
                e.printStackTrace();
            }
        }

        // set extensibleObject may attributes
        ObjectClassDescription extensibleObjectOcd = this
            .getObjectClassDescription( ObjectClassDescription.EXTENSIBLEOBJECT_OBJECTCLASSNAME );
        AttributeTypeDescription[] userAtds = SchemaUtils.getUserAttributeDescriptions( this );
        String[] attributeTypeDescriptionNames = SchemaUtils.getAttributeTypeDescriptionNames( userAtds );
        extensibleObjectOcd.setMayAttributeTypeDescriptionNames( attributeTypeDescriptionNames );
    }


    /**
     * 
     * @return the schema record when the schema was created using the
     *         loadFromLdif() method, null otherwise
     */
    public LdifContentRecord getSchemaRecord()
    {
        return schemaRecord;
    }


    public void setSchemaRecord( LdifContentRecord schemaRecord )
    {
        this.schemaRecord = schemaRecord;
    }


    /**
     * 
     * @return the dn of the schema record, may be null
     */
    public DN getDn()
    {
        return dn;
    }


    public void setDn( DN dn )
    {
        this.dn = dn;
    }


    /**
     * 
     * @return the create timestamp of the schema record, may be null
     */
    public String getCreateTimestamp()
    {
        return createTimestamp;
    }


    public void setCreateTimestamp( String createTimestamp )
    {
        this.createTimestamp = createTimestamp;
    }


    /**
     * 
     * @return the modify timestamp of the schema record, may be null
     */
    public String getModifyTimestamp()
    {
        return modifyTimestamp;
    }


    public void setModifyTimestamp( String modifyTimestamp )
    {
        this.modifyTimestamp = modifyTimestamp;
    }


    /**
     * 
     * @return the object classes of the schema record, may be an empty
     *         array.
     */
    public String[] getObjectClasses()
    {
        return objectClasses;
    }


    public void setObjectClasses( String[] objectClasses )
    {
        this.objectClasses = objectClasses;
    }


    /**
     * 
     * @return a Map of name to attribute type description
     */
    Map getAtdMapByName()
    {
        return atdMapByName;
    }


    void setAtdMapByName( Map atdMapByName )
    {
        this.atdMapByName = atdMapByName;
    }


    public void addAttributeTypeDescription( AttributeTypeDescription atd )
    {
        if ( atd.getNames() != null && atd.getNames().length > 0 )
        {
            for ( int i = 0; i < atd.getNames().length; i++ )
            {
                this.atdMapByName.put( atd.getNames()[i].toLowerCase(), atd );
            }
        }
        if ( atd.getNumericOID() != null )
        {
            this.atdMapByName.put( atd.getNumericOID().toLowerCase(), atd );
        }
    }


    /**
     * 
     * @return an array of all attribute type description names
     */
    public String[] getAttributeTypeDescriptionNames()
    {
        Set set = new HashSet();
        for ( Iterator it = this.atdMapByName.values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            for ( int i = 0; i < atd.getNames().length; i++ )
            {
                set.add( atd.getNames()[i] );
            }
        }
        return ( String[] ) set.toArray( new String[set.size()] );
    }


    public AttributeTypeDescription[] getAttributeTypeDescriptions()
    {
        Set set = new HashSet();
        for ( Iterator it = this.atdMapByName.values().iterator(); it.hasNext(); )
        {
            AttributeTypeDescription atd = ( AttributeTypeDescription ) it.next();
            set.add( atd );
        }
        return ( AttributeTypeDescription[] ) set.toArray( new AttributeTypeDescription[set.size()] );
    }


    /**
     * 
     * @return a Map of oid to syntax description
     */
    public Map getLsdMapByNumericOID()
    {
        return lsdMapByNumericOID;
    }


    public void setLsdMapByNumericOID( Map lsdMapByNumericOID )
    {
        this.lsdMapByNumericOID = lsdMapByNumericOID;
    }


    public void addLdapSyntaxDescription( LdapSyntaxDescription lsd )
    {
        if ( lsd.getNumericOID() != null )
        {
            this.lsdMapByNumericOID.put( lsd.getNumericOID().toLowerCase(), lsd );
        }
    }


    /**
     * 
     * @return an array of all syntax description oids
     */
    public String[] getLdapSyntaxDescriptionOids()
    {
        Set set = new HashSet();
        for ( Iterator it = this.lsdMapByNumericOID.values().iterator(); it.hasNext(); )
        {
            LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) it.next();
            set.add( lsd.getNumericOID() );
        }
        return ( String[] ) set.toArray( new String[set.size()] );
    }


    public LdapSyntaxDescription[] getLdapSyntaxDescriptions()
    {
        Set set = new HashSet();
        for ( Iterator it = this.lsdMapByNumericOID.values().iterator(); it.hasNext(); )
        {
            LdapSyntaxDescription lsd = ( LdapSyntaxDescription ) it.next();
            set.add( lsd );
        }
        return ( LdapSyntaxDescription[] ) set.toArray( new LdapSyntaxDescription[set.size()] );
    }


    /**
     * 
     * @return a Map of name to matching rule description
     */
    public Map getMrdMapByName()
    {
        return mrdMapByName;
    }


    public void setMrdMapByName( Map mrdMapByName )
    {
        this.mrdMapByName = mrdMapByName;
    }

    
    /**
     * 
     * @return an array of all matching rule description names
     */
    public String[] getMatchingRuleDescriptionNames()
    {
        Set set = new HashSet();
        for ( Iterator it = this.mrdMapByName.values().iterator(); it.hasNext(); )
        {
            MatchingRuleDescription mrd = ( MatchingRuleDescription ) it.next();
            for ( int i = 0; i < mrd.getNames().length; i++ )
            {
                set.add( mrd.getNames()[i] );
            }
        }
        return ( String[] ) set.toArray( new String[set.size()] );
    }


    public MatchingRuleDescription[] getMatchingRuleDescriptions()
    {
        Set set = new HashSet();
        for ( Iterator it = this.mrdMapByName.values().iterator(); it.hasNext(); )
        {
            MatchingRuleDescription mrd = ( MatchingRuleDescription ) it.next();
            set.add( mrd );
        }
        return ( MatchingRuleDescription[] ) set.toArray( new MatchingRuleDescription[set.size()] );
    }

    public void addMatchingRuleDescription( MatchingRuleDescription mrd )
    {
        if ( mrd.getNames() != null && mrd.getNames().length > 0 )
        {
            for ( int i = 0; i < mrd.getNames().length; i++ )
            {
                this.mrdMapByName.put( mrd.getNames()[i].toLowerCase(), mrd );
            }
        }
        if ( mrd.getNumericOID() != null )
        {
            this.mrdMapByNumericOID.put( mrd.getNumericOID().toLowerCase(), mrd );
        }
    }


    /**
     * 
     * @return a Map of oid to matching rule description
     */
    public Map getMrdMapByNumericOID()
    {
        return mrdMapByNumericOID;
    }


    public void setMrdMapByNumericOID( Map mrdMapByNumericOID )
    {
        this.mrdMapByNumericOID = mrdMapByNumericOID;
    }


    /**
     * 
     * @return a Map of name to matching rule use description
     */
    public Map getMrudMapByName()
    {
        return mrudMapByName;
    }


    public void setMrudMapByName( Map mrudMapByName )
    {
        this.mrudMapByName = mrudMapByName;
    }


    public void addMatchingRuleUseDescription( MatchingRuleUseDescription mrud )
    {
        if ( mrud.getNames() != null && mrud.getNames().length > 0 )
        {
            for ( int i = 0; i < mrud.getNames().length; i++ )
            {
                this.mrudMapByName.put( mrud.getNames()[i].toLowerCase(), mrud );
            }
        }
        if ( mrud.getNumericOID() != null )
        {
            this.mrudMapByNumericOID.put( mrud.getNumericOID().toLowerCase(), mrud );
        }
    }


    /**
     * 
     * @return a Map of oid to matching rule use description
     */
    public Map getMrudMapByNumericOID()
    {
        return mrudMapByNumericOID;
    }


    public void setMrduMapByNumericOID( Map mrudMapByNumericOID )
    {
        this.mrudMapByNumericOID = mrudMapByNumericOID;
    }


    /**
     * 
     * @return a Map of name to object class description
     */
    Map getOcdMapByName()
    {
        return ocdMapByName;
    }


    void setOcdMapByName( Map ocdMapByName )
    {
        this.ocdMapByName = ocdMapByName;
    }


    public void addObjectClassDescription( ObjectClassDescription ocd )
    {
        if ( ocd.getNames() != null && ocd.getNames().length > 0 )
        {
            for ( int i = 0; i < ocd.getNames().length; i++ )
            {
                this.ocdMapByName.put( ocd.getNames()[i].toLowerCase(), ocd );
            }
        }
        if ( ocd.getNumericOID() != null )
        {
            this.ocdMapByName.put( ocd.getNumericOID().toLowerCase(), ocd );
        }
    }


    /**
     * 
     * @return an array of all object class names
     */
    public String[] getObjectClassDescriptionNames()
    {
        Set set = new HashSet();
        for ( Iterator it = this.ocdMapByName.values().iterator(); it.hasNext(); )
        {
            ObjectClassDescription ocd = ( ObjectClassDescription ) it.next();
            for ( int i = 0; i < ocd.getNames().length; i++ )
            {
                set.add( ocd.getNames()[i] );
            }
        }
        return ( String[] ) set.toArray( new String[set.size()] );
    }


    public ObjectClassDescription[] getObjectClassDescriptions()
    {
        Set set = new HashSet();
        for ( Iterator it = this.ocdMapByName.values().iterator(); it.hasNext(); )
        {
            ObjectClassDescription ocd = ( ObjectClassDescription ) it.next();
            set.add( ocd );
        }
        return ( ObjectClassDescription[] ) set.toArray( new ObjectClassDescription[set.size()] );
    }


    /**
     * 
     * @param name
     * @return true if a object class description with the given name
     *         exists.
     */
    public boolean hasObjectClassDescription( String name )
    {
        return this.ocdMapByName.containsKey( name.toLowerCase() );
    }


    /**
     * Returns the object class description of the given name. If no such
     * object exists the default or a dummy object class description is
     * returned.
     * 
     * @param name
     *                the object class name
     * @return the object class description, the default or a dummy
     */
    public ObjectClassDescription getObjectClassDescription( String name )
    {
        if ( this.ocdMapByName.containsKey( name.toLowerCase() ) )
        {
            return ( ObjectClassDescription ) this.ocdMapByName.get( name.toLowerCase() );
        }
        else if ( !this.isDefault() )
        {
            return DEFAULT_SCHEMA.getObjectClassDescription( name );
        }
        else
        {
            // DUMMY
            ObjectClassDescription ocd = new ObjectClassDescription();
            ocd.setSchema( this );
            ocd.setNumericOID( name );
            ocd.setNames( new String[]
                { name } );
            return ocd;
        }
    }


    /**
     * 
     * @param name
     * @return true if a attribute type description with the given name
     *         exists.
     */
    public boolean hasAttributeTypeDescription( String name )
    {
        return this.atdMapByName.containsKey( name.toLowerCase() );
    }


    /**
     * Returns the attribute type description of the given name. If no such
     * object exists the default or a dummy attribute type description is
     * returned.
     * 
     * @param description
     *                the attribute description
     * @return the attribute type description, or the default or a dummy
     */
    public AttributeTypeDescription getAttributeTypeDescription( String description )
    {

        AttributeDescription ad = new AttributeDescription( description );
        String attributeType = ad.getParsedAttributeType();

        if ( this.atdMapByName.containsKey( attributeType.toLowerCase() ) )
        {
            return ( AttributeTypeDescription ) this.atdMapByName.get( attributeType.toLowerCase() );
        }
        else if ( !this.isDefault() )
        {
            return DEFAULT_SCHEMA.getAttributeTypeDescription( attributeType );
        }
        else
        {
            // DUMMY
            AttributeTypeDescription atd = new AttributeTypeDescription();
            atd.setSchema( this );
            atd.setNumericOID( attributeType );
            atd.setNames( new String[]
                { attributeType } );
            atd.setNoUserModification( true );
            atd.setUsage( "" );
            return atd;
        }
    }


    /**
     * 
     * @param name
     * @return true if a syntax description with the given name exists.
     */
    public boolean hasLdapSyntaxDescription( String numericOID )
    {
        return this.lsdMapByNumericOID.containsKey( numericOID.toLowerCase() );
    }


    /**
     * Returns the syntax description of the given name. If no such object
     * exists the default or a dummy syntax description is returned.
     * 
     * @param name
     *                the attribute name
     * @return the attribute type description, or the default or a dummy
     */
    public LdapSyntaxDescription getLdapSyntaxDescription( String numericOID )
    {
        if ( this.lsdMapByNumericOID.containsKey( numericOID.toLowerCase() ) )
        {
            return ( LdapSyntaxDescription ) this.lsdMapByNumericOID.get( numericOID.toLowerCase() );
        }
        else if ( !this.isDefault() )
        {
            return DEFAULT_SCHEMA.getLdapSyntaxDescription( numericOID );
        }
        else
        {
            // DUMMY
            LdapSyntaxDescription lsd = new LdapSyntaxDescription();
            lsd.setSchema( this );
            lsd.setNumericOID( numericOID );
            return lsd;
        }
    }


    /**
     * 
     * @param name
     * @return true if a matching rule description with the given name or
     *         oid exists.
     */
    public boolean hasMatchingRuleDescription( String nameOrOID )
    {
        return this.mrdMapByName.containsKey( nameOrOID.toLowerCase() )
            || this.mrdMapByNumericOID.containsKey( nameOrOID.toLowerCase() );
    }


    /**
     * Returns the matching rule description of the given name or oid. If no
     * such object exists the default or a dummy matching rule description
     * is returned.
     * 
     * @param name
     *                the matching rule
     * @return the matching rule description, or the default or a dummy
     */
    public MatchingRuleDescription getMatchingRuleDescription( String nameOrOID )
    {
        if ( this.mrdMapByName.containsKey( nameOrOID.toLowerCase() ) )
        {
            return ( MatchingRuleDescription ) this.mrdMapByName.get( nameOrOID.toLowerCase() );
        }
        else if ( this.mrdMapByNumericOID.containsKey( nameOrOID.toLowerCase() ) )
        {
            return ( MatchingRuleDescription ) this.mrdMapByNumericOID.get( nameOrOID.toLowerCase() );
        }
        else if ( !this.isDefault() )
        {
            return DEFAULT_SCHEMA.getMatchingRuleDescription( nameOrOID );
        }
        else
        {
            // DUMMY
            MatchingRuleDescription mrd = new MatchingRuleDescription();
            mrd.setSchema( this );
            mrd.setNumericOID( nameOrOID );
            return mrd;
        }
    }


    /**
     * 
     * @param name
     * @return true if a matching rule use description with the given name
     *         or oid exists.
     */
    public boolean hasMatchingRuleUseDescription( String nameOrOID )
    {
        return this.mrudMapByName.containsKey( nameOrOID.toLowerCase() )
            || this.mrudMapByNumericOID.containsKey( nameOrOID.toLowerCase() );
    }


    /**
     * Returns the matching rule description of the given name or oid. If no
     * such object exists the default or a dummy matching rule description
     * is returned.
     * 
     * @param name
     *                the matching rule
     * @return the matching rule description, or the default or a dummy
     */
    public MatchingRuleUseDescription getMatchingRuleUseDescription( String nameOrOID )
    {
        if ( this.mrudMapByName.containsKey( nameOrOID.toLowerCase() ) )
        {
            return ( MatchingRuleUseDescription ) this.mrudMapByName.get( nameOrOID.toLowerCase() );
        }
        else if ( this.mrudMapByNumericOID.containsKey( nameOrOID.toLowerCase() ) )
        {
            return ( MatchingRuleUseDescription ) this.mrudMapByNumericOID.get( nameOrOID.toLowerCase() );
        }
        else if ( !this.isDefault() )
        {
            return DEFAULT_SCHEMA.getMatchingRuleUseDescription( nameOrOID );
        }
        else
        {
            // DUMMY
            MatchingRuleUseDescription mrud = new MatchingRuleUseDescription();
            mrud.setSchema( this );
            mrud.setNumericOID( nameOrOID );
            return mrud;
        }
    }


    static String[] addValue( String[] array, String value )
    {
        List list = new ArrayList( Arrays.asList( array ) );
        list.add( value );
        return ( String[] ) list.toArray( new String[list.size()] );
    }

}
