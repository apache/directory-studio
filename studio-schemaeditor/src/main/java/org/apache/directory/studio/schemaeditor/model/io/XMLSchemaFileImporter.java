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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SchemaImpl;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * This class is used to import a Schema file from the XML Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class XMLSchemaFileImporter
{
    // The Tags
    private static final String ALIAS_TAG = "alias";
    private static final String ALIASES_TAG = "aliases";
    private static final String ATTRIBUTE_TYPE_TAG = "attributetype";
    private static final String ATTRIBUTE_TYPES_TAG = "attributetypes";
    private static final String BOOLEAN_FALSE = "false";
    private static final String BOOLEAN_TRUE = "true";
    private static final String COLLECTIVE_TAG = "collective";
    private static final String DESCRIPTION_TAG = "description";
    private static final String EQUALITY_TAG = "equality";
    private static final String HUMAN_READABLE_TAG = "humanreadable";
    private static final String MANDATORY_TAG = "mandatory";
    private static final String MATCHING_RULE_TAG = "matchingrule";
    private static final String MATCHING_RULES_TAG = "matchingrules";
    private static final String NAME_TAG = "name";
    private static final String NO_USER_MODIFICATION_TAG = "nousermodification";
    private static final String OBJECT_CLASS_TAG = "objectclass";
    private static final String OBJECT_CLASSES_TAG = "objectclasses";
    private static final String OBSOLETE_TAG = "obsolete";
    private static final String OID_TAG = "oid";
    private static final String OPTIONAL_TAG = "optional";
    private static final String ORDERING_TAG = "ordering";
    private static final String SCHEMA_TAG = "schema";
    private static final String SCHEMAS_TAG = "schemas";
    private static final String SINGLE_VALUE_TAG = "singlevalue";
    private static final String SUBSTRING_TAG = "substring";
    private static final String SUPERIOR_TAG = "superior";
    private static final String SUPERIORS_TAG = "superiors";
    private static final String SYNTAX_LENGTH_TAG = "syntaxlength";
    private static final String SYNTAX_OID_TAG = "syntaxoid";
    private static final String SYNTAX_TAG = "syntax";
    private static final String SYNTAXES_TAG = "syntaxes";
    private static final String TYPE_TAG = "type";
    private static final String USAGE_TAG = "usage";


    /**
     * Extracts the Schemas from the given path.
     *
     * @param path
     *      the path of the file.
     * @return
     *      the corresponding schema
     * @throws XMLSchemaFileImportException
     *      if an error occurs when importing the schema
     */
    public static Schema[] getSchemas( String path ) throws XMLSchemaFileImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( path );
        }
        catch ( DocumentException e )
        {
            throw new XMLSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
        }

        Element rootElement = document.getRootElement();
        if ( !rootElement.getName().equals( SCHEMAS_TAG ) )
        {
            throw new XMLSchemaFileImportException( "The file '" + path + "' does not seem to be a valid Schema file." );
        }

        return readSchemas( rootElement, path );
    }


    /**
     * Extracts the Schema from the given path.
     *
     * @param path
     *      the path of the file.
     * @return
     *      the corresponding schema
     * @throws XMLSchemaFileImportException
     *      if an error occurs when importing the schema
     */
    public static Schema getSchema( String path ) throws XMLSchemaFileImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( path );
        }
        catch ( DocumentException e )
        {
            throw new XMLSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
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
            throw new XMLSchemaFileImportException( "The file '" + path + "' does not seem to be a valid Schema file." );
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
        Schema schema = new SchemaImpl( null );

        // Name
        schema.setName( getSchemaName( element, path ) );

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
            throw new XMLSchemaFileImportException( "The file '" + path + "' does not seem to be a valid Schema file." );
        }

        Attribute nameAttribute = element.attribute( NAME_TAG );
        if ( ( nameAttribute != null ) && ( !nameAttribute.getValue().equals( "" ) ) )
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
    private static final String getNameFromPath( String path )
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
        AttributeTypeImpl at = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) )
        {
            at = new AttributeTypeImpl( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException(
                "An attribute type definition must contain an attribute for the OID." );
        }

        // Schema
        at.setSchema( schema.getName() );

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
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) )
        {
            at.setDescription( descriptionElement.getText() );
        }

        // Superior
        Element superiorElement = element.element( SUPERIOR_TAG );
        if ( ( superiorElement != null ) && ( !superiorElement.getText().equals( "" ) ) )
        {
            at.setSuperiorName( superiorElement.getText() );
        }

        // Usage
        Element usageElement = element.element( USAGE_TAG );
        if ( ( usageElement != null ) && ( !usageElement.getText().equals( "" ) ) )
        {
            try
            {
                at.setUsage( UsageEnum.valueOf( usageElement.getText() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new XMLSchemaFileImportException(
                    "The parser was not able to convert the usage value of the attribute type." );
            }
        }

        // Syntax
        Element syntaxElement = element.element( SYNTAX_TAG );
        if ( ( syntaxElement != null ) && ( !syntaxElement.getText().equals( "" ) ) )
        {
            at.setSyntaxOid( syntaxElement.getText() );
        }

        // Syntax Length
        Element syntaxLengthElement = element.element( SYNTAX_LENGTH_TAG );
        if ( ( syntaxLengthElement != null ) && ( !syntaxLengthElement.getText().equals( "" ) ) )
        {
            try
            {
                at.setLength( Integer.parseInt( syntaxLengthElement.getText() ) );
            }
            catch ( NumberFormatException e )
            {
                throw new XMLSchemaFileImportException(
                    "The parser was not able to convert the syntax length value of the attribute type to an integer." );
            }
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) )
        {
            at.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Single Value
        Attribute singleValueAttribute = element.attribute( SINGLE_VALUE_TAG );
        if ( ( singleValueAttribute != null ) && ( !singleValueAttribute.getValue().equals( "" ) ) )
        {
            at.setSingleValue( readBoolean( singleValueAttribute.getValue() ) );
        }

        // Collective
        Attribute collectiveAttribute = element.attribute( COLLECTIVE_TAG );
        if ( ( collectiveAttribute != null ) && ( !collectiveAttribute.getValue().equals( "" ) ) )
        {
            at.setCollective( readBoolean( collectiveAttribute.getValue() ) );
        }

        // No User Modification
        Attribute noUserModificationAttribute = element.attribute( NO_USER_MODIFICATION_TAG );
        if ( ( noUserModificationAttribute != null ) && ( !noUserModificationAttribute.getValue().equals( "" ) ) )
        {
            at.setCanUserModify( !readBoolean( noUserModificationAttribute.getValue() ) );
        }

        // Equality
        Element equalityElement = element.element( EQUALITY_TAG );
        if ( ( equalityElement != null ) && ( !equalityElement.getText().equals( "" ) ) )
        {
            at.setEqualityName( equalityElement.getText() );
        }

        // Ordering
        Element orderingElement = element.element( ORDERING_TAG );
        if ( ( orderingElement != null ) && ( !orderingElement.getText().equals( "" ) ) )
        {
            at.setOrderingName( orderingElement.getText() );
        }

        // Substring
        Element substringElement = element.element( SUBSTRING_TAG );
        if ( ( substringElement != null ) && ( !substringElement.getText().equals( "" ) ) )
        {
            at.setSubstrName( substringElement.getText() );
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
        ObjectClassImpl oc = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) )
        {
            oc = new ObjectClassImpl( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( "An object class definition must contain an attribute for the OID." );
        }

        // Schema
        oc.setSchema( schema.getName() );

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
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) )
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
                oc.setSuperClassesNames( superiors.toArray( new String[0] ) );
            }
        }

        // Class Type
        Element classTypeElement = element.element( TYPE_TAG );
        if ( ( classTypeElement != null ) && ( !classTypeElement.getText().equals( "" ) ) )
        {
            try
            {
                oc.setType( ObjectClassTypeEnum.valueOf( classTypeElement.getText() ) );
            }
            catch ( IllegalArgumentException e )
            {
                throw new XMLSchemaFileImportException(
                    "The parser was not able to convert the usage value of the attribute type." );
            }
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) )
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
                oc.setMustNamesList( mandatoryATs.toArray( new String[0] ) );
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
                oc.setMayNamesList( optionalATs.toArray( new String[0] ) );
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
        MatchingRuleImpl mr = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) )
        {
            mr = new MatchingRuleImpl( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( "A matching rule definition must contain an attribute for the OID." );
        }

        // Schema
        mr.setSchema( schema.getName() );

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
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) )
        {
            mr.setDescription( descriptionElement.getText() );
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) )
        {
            mr.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Syntax OID
        Element syntaxOidElement = element.element( SYNTAX_OID_TAG );
        if ( ( syntaxOidElement != null ) && ( !syntaxOidElement.getText().equals( "" ) ) )
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
        SyntaxImpl syntax = null;

        // OID
        Attribute oidAttribute = element.attribute( OID_TAG );
        if ( ( oidAttribute != null ) && ( !oidAttribute.getValue().equals( "" ) ) )
        {
            syntax = new SyntaxImpl( oidAttribute.getValue() );
        }
        else
        {
            throw new XMLSchemaFileImportException( "A syntax definition must contain an attribute for the OID." );
        }

        // Schema
        syntax.setSchema( schema.getName() );

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
        if ( ( descriptionElement != null ) && ( !descriptionElement.getText().equals( "" ) ) )
        {
            syntax.setDescription( descriptionElement.getText() );
        }

        // Obsolete
        Attribute obsoleteAttribute = element.attribute( OBSOLETE_TAG );
        if ( ( obsoleteAttribute != null ) && ( !obsoleteAttribute.getValue().equals( "" ) ) )
        {
            syntax.setObsolete( readBoolean( obsoleteAttribute.getValue() ) );
        }

        // Human Readible
        Attribute humanReadibleAttribute = element.attribute( HUMAN_READABLE_TAG );
        if ( ( humanReadibleAttribute != null ) && ( !humanReadibleAttribute.getValue().equals( "" ) ) )
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
            throw new XMLSchemaFileImportException( "The parser was not able to convert a boolean value." );
        }
    }

    /**
     * This enum represents the different types of schema files.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
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
    public static SchemaFileType getSchemaFileType( String path ) throws XMLSchemaFileImportException
    {
        SAXReader reader = new SAXReader();
        Document document = null;
        try
        {
            document = reader.read( path );
        }
        catch ( DocumentException e )
        {
            throw new XMLSchemaFileImportException( "The file '" + path + "' can not be read correctly." );
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
            throw new XMLSchemaFileImportException( "The file '" + path + "' does not seem to be a valid Schema file." );
        }
    }
}
