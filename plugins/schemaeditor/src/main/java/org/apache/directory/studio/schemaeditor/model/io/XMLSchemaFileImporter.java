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
package org.apache.directory.studio.schemaeditor.model.io;


import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableMatchingRule;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.osgi.util.NLS;


/**
 * This class is used to import a Schema file from the XML Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class XMLSchemaFileImporter
{
    // The Tags
    private static final String ALIAS_TAG = "alias"; //$NON-NLS-1$
    private static final String ALIASES_TAG = "aliases"; //$NON-NLS-1$
    private static final String ATTRIBUTE_TYPE_TAG = "attributetype"; //$NON-NLS-1$
    private static final String ATTRIBUTE_TYPES_TAG = "attributetypes"; //$NON-NLS-1$
    private static final String BOOLEAN_FALSE = "false"; //$NON-NLS-1$
    private static final String BOOLEAN_TRUE = "true"; //$NON-NLS-1$
    private static final String COLLECTIVE_TAG = "collective"; //$NON-NLS-1$
    private static final String DESCRIPTION_TAG = "description"; //$NON-NLS-1$
    private static final String EQUALITY_TAG = "equality"; //$NON-NLS-1$
    private static final String HUMAN_READABLE_TAG = "humanreadable"; //$NON-NLS-1$
    private static final String MANDATORY_TAG = "mandatory"; //$NON-NLS-1$
    private static final String MATCHING_RULE_TAG = "matchingrule"; //$NON-NLS-1$
    private static final String MATCHING_RULES_TAG = "matchingrules"; //$NON-NLS-1$
    private static final String NAME_TAG = "name"; //$NON-NLS-1$
    private static final String NO_USER_MODIFICATION_TAG = "nousermodification"; //$NON-NLS-1$
    private static final String OBJECT_CLASS_TAG = "objectclass"; //$NON-NLS-1$
    private static final String OBJECT_CLASSES_TAG = "objectclasses"; //$NON-NLS-1$
    private static final String OBSOLETE_TAG = "obsolete"; //$NON-NLS-1$
    private static final String OID_TAG = "oid"; //$NON-NLS-1$
    private static final String OPTIONAL_TAG = "optional"; //$NON-NLS-1$
    private static final String ORDERING_TAG = "ordering"; //$NON-NLS-1$
    private static final String SCHEMA_TAG = "schema"; //$NON-NLS-1$
    private static final String SCHEMAS_TAG = "schemas"; //$NON-NLS-1$
    private static final String SINGLE_VALUE_TAG = "singlevalue"; //$NON-NLS-1$
    private static final String SUBSTRING_TAG = "substring"; //$NON-NLS-1$
    private static final String SUPERIOR_TAG = "superior"; //$NON-NLS-1$
    private static final String SUPERIORS_TAG = "superiors"; //$NON-NLS-1$
    private static final String SYNTAX_LENGTH_TAG = "syntaxlength"; //$NON-NLS-1$
    private static final String SYNTAX_OID_TAG = "syntaxoid"; //$NON-NLS-1$
    private static final String SYNTAX_TAG = "syntax"; //$NON-NLS-1$
    private static final String SYNTAXES_TAG = "syntaxes"; //$NON-NLS-1$
    private static final String TYPE_TAG = "type"; //$NON-NLS-1$
    private static final String USAGE_TAG = "usage"; //$NON-NLS-1$


    /**
     * Extracts the Schemas from the given path.
     *
     * @param inputStream
     *      the {@link InputStream} of the file
     * @param path
     *      the path of the file.
     * @return
     *      the corresponding schema
     * @throws XMLSchemaFileImportException
     *      if an error occurs when importing the schema
     */
    public static Schema[] getSchemas( InputStream inputStream, String path ) throws XMLSchemaFileImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( inputStream );
        }
        catch ( DocumentException e )
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotReadCorrectly" ), new String[] { path } ), e ); //$NON-NLS-1$
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( SCHEMAS_TAG ) )
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotValidSchema" ), new String[] { path } ) ); //$NON-NLS-1$
        }

        return readSchemas( rootElement, path );
    }


    /**
     * Extracts the Schema from the given path.
     *
     * @param inputStream
     *      the {@link InputStream} of the file
     * @param path
     *      the path of the file.
     * @return
     *      the corresponding schema
     * @throws XMLSchemaFileImportException
     *      if an error occurs when importing the schema
     */
    public static Schema getSchema( InputStream inputStream, String path ) throws XMLSchemaFileImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( inputStream );
        }
        catch ( DocumentException e )
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotReadCorrectly" ), new String[] { path } ), e ); //$NON-NLS-1$
        }

        Element rootElement = document.getRootElement();

        return readSchema( rootElement, path );
    }


    /**
     * Reads schemas.
     *
     * @param element
     *      the element
     * @param path
     *      the path of the file
     * @throws XMLSchemaFileImportException
     *      if an error occurs when importing the schema
     * @return
     *      the corresponding schemas
     */
    public static Schema[] readSchemas( Element element, String path ) throws XMLSchemaFileImportException
    {
        List<Schema> schemas = new ArrayList<Schema>();

        if ( !element.getName().equals( SCHEMAS_TAG ) )
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotValidSchema" ), new String[] { path } ) ); //$NON-NLS-1$
        }

        for ( Iterator<?> i = element.elementIterator( SCHEMA_TAG ); i.hasNext(); )
        {
            Element schemaElement = ( Element ) i.next();
            schemas.add( readSchema( schemaElement, path ) );
        }

        return schemas.toArray( new Schema[0] );
    }


    /**
     * Reads a schema.
     *
     * @param element
     *      the element
     * @param path
     *      the path of the file
     * @throws XMLSchemaFileImportException
     *      if an error occurs when importing the schema
     * @return
     *      the corresponding schema
     */
    public static Schema readSchema( Element element, String path ) throws XMLSchemaFileImportException
    {
        // Creating the schema with an empty name
        Schema schema = new Schema( getSchemaName( element, path ) );

        // Attribute Types
        readAttributeTypes( element, schema );

        // Object Classes
        readObjectClasses( element, schema );

        // Matching Rules
        readMatchingRules( element, schema );

        // Syntaxes
        readSyntaxes( element, schema );

        return schema;
    }


    /**
     * Gets the name of the schema.
     *
     * @param element
     *      the element
     * @param path
     *      the path
     * @return
     *      the name of the schema
     * @throws XMLSchemaFileImportException
     *      if an error occurs when reading the file
     */
    private static String getSchemaName( Element element, String path ) throws XMLSchemaFileImportException
    {
        if ( !element.getName().equals( SCHEMA_TAG ) )
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotValidSchema" ), new String[] { path } ) ); //$NON-NLS-1$
        }

        Attribute nameAttribute = element.attribute( NAME_TAG );
        if ( ( nameAttribute != null ) && ( !nameAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            return nameAttribute.getValue();
        }
        else
        {
            return getNameFromPath( path );
        }
    }


    /**
     * Gets the name of the file.
     *
     * @param path
     *      the path
     * @return
     *      the name of the file.
     */
    private static String getNameFromPath( String path )
    {
        File file = new File( path );
        String fileName = file.getName();
        if ( fileName.endsWith( ".xml" ) ) //$NON-NLS-1$
        {
            String[] fileNameSplitted = fileName.split( "\\." ); //$NON-NLS-1$
            return fileNameSplitted[0];
        }

        return fileName;
    }


    /**
     * Reads the attribute types.
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readAttributeTypes( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        for ( Iterator<?> i = element.elementIterator( ATTRIBUTE_TYPES_TAG ); i.hasNext(); )
        {
            Element attributesTypesElement = ( Element ) i.next();
            for ( Iterator<?> i2 = attributesTypesElement.elementIterator( ATTRIBUTE_TYPE_TAG ); i2.hasNext(); )
            {
                readAttributeType( ( Element ) i2.next(), schema );
            }
        }
    }


    /**
     * Reads an attribute type.
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     */
    private static void readAttributeType( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        MutableAttributeType at = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            at = new MutableAttributeType( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( Messages.getString( "XMLSchemaFileImporter.NoOIDInAttribute" ) ); //$NON-NLS-1$
        }

        // Schema
        at.setSchemaName( schema.getSchemaName() );

        // Aliases
        Element aliasesElement = element.element( ALIASES_TAG );
        if ( aliasesElement != null )
        {
            List<String> aliases = new ArrayList<String>();
            for ( Iterator<?> i = aliasesElement.elementIterator( ALIAS_TAG ); i.hasNext(); )
            {
                Element aliasElement = ( Element ) i.next();
                aliases.add( aliasElement.getText() );
            }
            if ( aliases.size() >= 1 )
            {
                at.setNames( aliases.toArray( new String[0] ) );
            }
        }

        // Description
        Element descriptionElement = element.element( DESCRIPTION_TAG );
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setDescription( descriptionElement.getText() );
        }

        // Superior
        Element superiorElement = element.element( SUPERIOR_TAG );
        if ( ( superiorElement != null ) && ( !superiorElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setSuperiorOid( superiorElement.getText() );
        }

        // Usage
        Element usageElement = element.element( USAGE_TAG );
        if ( ( usageElement != null ) && ( !usageElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            try
            {
                at.setUsage( UsageEnum.valueOf( usageElement.getText() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new XMLSchemaFileImportException( Messages
                    .getString( "XMLSchemaFileImporter.UnceonvertableAttribute" ), e ); //$NON-NLS-1$
            }
        }

        // Syntax
        Element syntaxElement = element.element( SYNTAX_TAG );
        if ( ( syntaxElement != null ) && ( !syntaxElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setSyntaxOid( syntaxElement.getText() );
        }

        // Syntax Length
        Element syntaxLengthElement = element.element( SYNTAX_LENGTH_TAG );
        if ( ( syntaxLengthElement != null ) && ( !syntaxLengthElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            try
            {
                at.setSyntaxLength( Long.parseLong( syntaxLengthElement.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new XMLSchemaFileImportException( Messages
                    .getString( "XMLSchemaFileImporter.UnconvertableInteger" ), e ); //$NON-NLS-1$
            }
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Single Value
        Attribute singleValueAttribute = element.attribute( SINGLE_VALUE_TAG );
        if ( ( singleValueAttribute != null ) && ( !singleValueAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setSingleValued( readBoolean( singleValueAttribute.getValue() ) );
        }

        // Collective
        Attribute collectiveAttribute = element.attribute( COLLECTIVE_TAG );
        if ( ( collectiveAttribute != null ) && ( !collectiveAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setCollective( readBoolean( collectiveAttribute.getValue() ) );
        }

        // No User Modification
        Attribute noUserModificationAttribute = element.attribute( NO_USER_MODIFICATION_TAG );
        if ( ( noUserModificationAttribute != null ) && ( !noUserModificationAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setUserModifiable( !readBoolean( noUserModificationAttribute.getValue() ) );
        }

        // Equality
        Element equalityElement = element.element( EQUALITY_TAG );
        if ( ( equalityElement != null ) && ( !equalityElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setEqualityOid( equalityElement.getText() );
        }

        // Ordering
        Element orderingElement = element.element( ORDERING_TAG );
        if ( ( orderingElement != null ) && ( !orderingElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setOrderingOid( orderingElement.getText() );
        }

        // Substring
        Element substringElement = element.element( SUBSTRING_TAG );
        if ( ( substringElement != null ) && ( !substringElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            at.setSubstringOid( substringElement.getText() );
        }

        // Adding the attribute type to the schema
        schema.addAttributeType( at );
    }


    /**
     * Reads the object classes
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readObjectClasses( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        for ( Iterator<?> i = element.elementIterator( OBJECT_CLASSES_TAG ); i.hasNext(); )
        {
            Element objectClassesElement = ( Element ) i.next();
            for ( Iterator<?> i2 = objectClassesElement.elementIterator( OBJECT_CLASS_TAG ); i2.hasNext(); )
            {
                readObjectClass( ( Element ) i2.next(), schema );
            }
        }
    }


    /**
     * Reads an object class
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readObjectClass( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        MutableObjectClass oc = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            oc = new MutableObjectClass( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( Messages.getString( "XMLSchemaFileImporter.NoOIDInClass" ) ); //$NON-NLS-1$
        }

        // Schema
        oc.setSchemaName( schema.getSchemaName() );

        // Aliases
        Element aliasesElement = element.element( ALIASES_TAG );
        if ( aliasesElement != null )
        {
            List<String> aliases = new ArrayList<String>();
            for ( Iterator<?> i = aliasesElement.elementIterator( ALIAS_TAG ); i.hasNext(); )
            {
                Element aliasElement = ( Element ) i.next();
                aliases.add( aliasElement.getText() );
            }
            if ( aliases.size() >= 1 )
            {
                oc.setNames( aliases.toArray( new String[0] ) );
            }
        }

        // Description
        Element descriptionElement = element.element( DESCRIPTION_TAG );
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            oc.setDescription( descriptionElement.getText() );
        }

        // Superiors
        Element superiorsElement = element.element( SUPERIORS_TAG );
        if ( superiorsElement != null )
        {
            List<String> superiors = new ArrayList<String>();
            for ( Iterator<?> i = superiorsElement.elementIterator( SUPERIOR_TAG ); i.hasNext(); )
            {
                Element superiorElement = ( Element ) i.next();
                superiors.add( superiorElement.getText() );
            }
            if ( superiors.size() >= 1 )
            {
                oc.setSuperiorOids( superiors );
            }
        }

        // Class Type
        Element classTypeElement = element.element( TYPE_TAG );
        if ( ( classTypeElement != null ) && ( !classTypeElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            try
            {
                oc.setType( ObjectClassTypeEnum.valueOf( classTypeElement.getText() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new XMLSchemaFileImportException(
                    Messages.getString( "XMLSchemaFileImporter.UnconvertableValue" ), e ); //$NON-NLS-1$
            }
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            oc.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Mandatory Attribute Types
        Element mandatoryElement = element.element( MANDATORY_TAG );
        if ( mandatoryElement != null )
        {
            List<String> mandatoryATs = new ArrayList<String>();
            for ( Iterator<?> i = mandatoryElement.elementIterator( ATTRIBUTE_TYPE_TAG ); i.hasNext(); )
            {
                Element attributeTypeElement = ( Element ) i.next();
                mandatoryATs.add( attributeTypeElement.getText() );
            }
            if ( mandatoryATs.size() >= 1 )
            {
                oc.setMustAttributeTypeOids( mandatoryATs );
            }
        }

        // Optional Attribute Types
        Element optionalElement = element.element( OPTIONAL_TAG );
        if ( optionalElement != null )
        {
            List<String> optionalATs = new ArrayList<String>();
            for ( Iterator<?> i = optionalElement.elementIterator( ATTRIBUTE_TYPE_TAG ); i.hasNext(); )
            {
                Element attributeTypeElement = ( Element ) i.next();
                optionalATs.add( attributeTypeElement.getText() );
            }
            if ( optionalATs.size() >= 1 )
            {
                oc.setMayAttributeTypeOids( optionalATs );
            }
        }

        // Adding the object class to the schema
        schema.addObjectClass( oc );
    }


    /**
     * Reads the matching rules.
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readMatchingRules( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        for ( Iterator<?> i = element.elementIterator( MATCHING_RULES_TAG ); i.hasNext(); )
        {
            Element matchingRulesElement = ( Element ) i.next();
            for ( Iterator<?> i2 = matchingRulesElement.elementIterator( MATCHING_RULE_TAG ); i2.hasNext(); )
            {
                readMatchingRule( ( Element ) i2.next(), schema );
            }
        }
    }


    /**
     * Reads a matching rule.
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readMatchingRule( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        MutableMatchingRule mr = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            mr = new MutableMatchingRule( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( Messages.getString( "XMLSchemaFileImporter.NoMatchingRuleForOID" ) ); //$NON-NLS-1$
        }

        // Schema
        mr.setSchemaName( schema.getSchemaName() );

        // Aliases
        Element aliasesElement = element.element( ALIASES_TAG );
        if ( aliasesElement != null )
        {
            List<String> aliases = new ArrayList<String>();
            for ( Iterator<?> i = aliasesElement.elementIterator( ALIAS_TAG ); i.hasNext(); )
            {
                Element aliasElement = ( Element ) i.next();
                aliases.add( aliasElement.getText() );
            }
            if ( aliases.size() >= 1 )
            {
                mr.setNames( aliases.toArray( new String[0] ) );
            }
        }

        // Description
        Element descriptionElement = element.element( DESCRIPTION_TAG );
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            mr.setDescription( descriptionElement.getText() );
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            mr.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Syntax OID
        Element syntaxOidElement = element.element( SYNTAX_OID_TAG );
        if ( ( syntaxOidElement != null ) && ( !syntaxOidElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            mr.setSyntaxOid( syntaxOidElement.getText() );
        }

        // Adding the matching rule to the schema
        schema.addMatchingRule( mr );
    }


    /**
     * Reads the syntaxes
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readSyntaxes( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        for ( Iterator<?> i = element.elementIterator( SYNTAXES_TAG ); i.hasNext(); )
        {
            Element syntaxElement = ( Element ) i.next();
            for ( Iterator<?> i2 = syntaxElement.elementIterator( SYNTAX_TAG ); i2.hasNext(); )
            {
                readSyntax( ( Element ) i2.next(), schema );
            }
        }
    }


    /**
     * Reads a syntax.
     *
     * @param element
     *      the element
     * @param schema
     *      the schema
     * @throws XMLSchemaFileImportException
     */
    private static void readSyntax( Element element, Schema schema ) throws XMLSchemaFileImportException
    {
        LdapSyntax syntax = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            syntax = new LdapSyntax( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( Messages.getString( "XMLSchemaFileImporter.InvalidSyntaxForOID" ) ); //$NON-NLS-1$
        }

        // Schema
        syntax.setSchemaName( schema.getSchemaName() );

        // Aliases
        Element aliasesElement = element.element( ALIASES_TAG );
        if ( aliasesElement != null )
        {
            List<String> aliases = new ArrayList<String>();
            for ( Iterator<?> i = aliasesElement.elementIterator( ALIAS_TAG ); i.hasNext(); )
            {
                Element aliasElement = ( Element ) i.next();
                aliases.add( aliasElement.getText() );
            }
            if ( aliases.size() >= 1 )
            {
                syntax.setNames( aliases.toArray( new String[0] ) );
            }
        }

        // Description
        Element descriptionElement = element.element( DESCRIPTION_TAG );
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) ) //$NON-NLS-1$
        {
            syntax.setDescription( descriptionElement.getText() );
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            syntax.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Human Readible
        Attribute humanReadibleAttribute = element.attribute( HUMAN_READABLE_TAG );
        if ( ( humanReadibleAttribute != null ) && ( !humanReadibleAttribute.getValue().equals( "" ) ) ) //$NON-NLS-1$
        {
            syntax.setHumanReadable( readBoolean( humanReadibleAttribute.getValue() ) );
        }

        // Adding the syntax to the schema
        schema.addSyntax( syntax );
    }


    /**
     * Reads a boolean value
     *
     * @param value
     *      the value
     * @return
     *      the boolean value
     * @throws XMLSchemaFileImportException
     *      if the boolean could not be read
     */
    private static boolean readBoolean( String value ) throws XMLSchemaFileImportException
    {
        if ( value.equals( BOOLEAN_TRUE ) )
        {
            return true;
        }
        else if ( value.equals( BOOLEAN_FALSE ) )
        {
            return false;
        }
        else
        {
            throw new XMLSchemaFileImportException( Messages.getString( "XMLSchemaFileImporter.76" ) ); //$NON-NLS-1$
        }
    }

    /**
     * This enum represents the different types of schema files.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     */
    public enum SchemaFileType
    {
        SINGLE, MULTIPLE
    };


    /**
     * Gets the type of file.
     *
     * @param path
     *      the path of the file
     * @return
     *      the type of the file
     * @throws XMLSchemaFileImportException
     */
    public static SchemaFileType getSchemaFileType( InputStream inputStream, String path )
        throws XMLSchemaFileImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( inputStream );
        }
        catch ( DocumentException e )
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotReadCorrectly" ), new String[] { path } ), e ); //$NON-NLS-1$
        }

        Element rootElement = document.getRootElement();
        if ( rootElement.getName().equals( SCHEMA_TAG ) )
        {
            return SchemaFileType.SINGLE;
        }
        else if ( rootElement.getName().equals( SCHEMAS_TAG ) )
        {
            return SchemaFileType.MULTIPLE;
        }
        else
        {
            throw new XMLSchemaFileImportException( NLS.bind( Messages
                .getString( "XMLSchemaFileImporter.NotValidSchema" ), new String[] { path } ) ); //$NON-NLS-1$
        }
    }
}
