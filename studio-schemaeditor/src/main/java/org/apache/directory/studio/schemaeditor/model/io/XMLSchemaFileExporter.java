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


import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.MatchingRuleImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.SyntaxImpl;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;


/**
 * This class is used to export a Schema file into the XML Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class XMLSchemaFileExporter
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
     * Converts the given schema to its source code representation
     * in XML file format.
     *
     * @param schema
     *      the schema to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toXml( Schema schema )
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Adding the schema
        addSchema( schema, document );

        return styleDocument( document ).asXML();
    }


    /**
     * Converts the given schemas to their source code representation
     * in one XML file format.
     *
     * @param schemas
     *      the array of schemas to convert
     * @return
     *      the corresponding source code representation
     */
    public static String toXml( Schema[] schemas )
    {
        // Creating the Document and the 'root' Element
        Document document = DocumentHelper.createDocument();

        addSchemas( schemas, document );

        return styleDocument( document ).asXML();
    }


    /**
     * Add the XML representation of the given schemas
     * to the given branch.
     *
     * @param schemas
     *      the schemas
     * @param branch
     *      the branch
     */
    public static void addSchemas( Schema[] schemas, Branch branch )
    {
        Element element = branch.addElement( SCHEMAS_TAG );

        if ( schemas != null )
        {
            for ( Schema schema : schemas )
            {
                addSchema( schema, element );
            }
        }
    }


    /**
     * Add the XML representation of the given schema
     * to the given branch.
     *
     * @param schema
     *      the schema
     * @param branch
     *      the branch
     */
    public static void addSchema( Schema schema, Branch branch )
    {
        Element element = branch.addElement( SCHEMA_TAG );
        if ( schema != null )
        {
            // Name 
            String name = schema.getName();
            if ( ( name != null ) && ( !name.equals( "" ) ) )
            {
                element.addAttribute( NAME_TAG, name );
            }

            // Attribute Types
            List<AttributeTypeImpl> ats = schema.getAttributeTypes();
            if ( ( ats != null ) && ( ats.size() >= 1 ) )
            {
                Element attributeTypesNode = element.addElement( ATTRIBUTE_TYPES_TAG );
                for ( AttributeTypeImpl at : ats )
                {
                    toXml( at, attributeTypesNode );
                }
            }

            // Object Classes
            List<ObjectClassImpl> ocs = schema.getObjectClasses();
            if ( ( ocs != null ) && ( ocs.size() >= 1 ) )
            {
                Element objectClassesNode = element.addElement( OBJECT_CLASSES_TAG );
                for ( ObjectClassImpl oc : ocs )
                {
                    toXml( oc, objectClassesNode );
                }
            }

            // Matching Rules
            List<MatchingRuleImpl> mrs = schema.getMatchingRules();
            if ( ( mrs != null ) && ( mrs.size() >= 1 ) )
            {
                Element matchingRulesNode = element.addElement( MATCHING_RULES_TAG );
                for ( MatchingRuleImpl mr : mrs )
                {
                    toXml( mr, matchingRulesNode );
                }
            }

            // Syntaxes
            List<SyntaxImpl> syntaxes = schema.getSyntaxes();
            if ( ( syntaxes != null ) && ( syntaxes.size() >= 1 ) )
            {
                Element syntaxesNode = element.addElement( SYNTAXES_TAG );
                for ( SyntaxImpl syntax : syntaxes )
                {
                    toXml( syntax, syntaxesNode );
                }
            }
        }
    }


    /**
     * Adds the given attribute type to its root Element.
     *
     * @param at
     *      the attribute type
     * @param root
     *      the root Element
     */
    private static void toXml( AttributeTypeImpl at, Element root )
    {
        Element atNode = root.addElement( ATTRIBUTE_TYPE_TAG );

        // OID
        String oid = at.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) )
        {
            atNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        String[] aliases = at.getNames();
        if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            Element aliasesNode = atNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = at.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) )
        {
            atNode.addElement( DESCRIPTION_TAG ).setText( description );
        }

        // Superior
        String superior = at.getSuperiorName();
        if ( ( superior != null ) && ( !superior.equals( "" ) ) )
        {
            atNode.addElement( SUPERIOR_TAG ).setText( superior );
        }

        // Usage
        UsageEnum usage = at.getUsage();
        if ( usage != null )
        {
            atNode.addElement( USAGE_TAG ).setText( usage.toString() );
        }

        // Syntax
        String syntax = at.getSyntaxOid();
        if ( ( syntax != null ) && ( !syntax.equals( "" ) ) )
        {
            atNode.addElement( SYNTAX_TAG ).setText( syntax );
        }

        // Syntax Length
        int syntaxLength = at.getLength();
        if ( syntaxLength > 0 )
        {
            atNode.addElement( SYNTAX_LENGTH_TAG ).setText( "" + syntaxLength );
        }

        // Obsolete
        if ( at.isObsolete() )
        {
            atNode.addAttribute( OBSOLETE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            atNode.addAttribute( OBSOLETE_TAG, BOOLEAN_FALSE );
        }

        // Single Value
        if ( at.isSingleValue() )
        {
            atNode.addAttribute( SINGLE_VALUE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            atNode.addAttribute( SINGLE_VALUE_TAG, BOOLEAN_FALSE );
        }

        // Collective
        if ( at.isCollective() )
        {
            atNode.addAttribute( COLLECTIVE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            atNode.addAttribute( COLLECTIVE_TAG, BOOLEAN_FALSE );
        }

        // No User Modification
        if ( at.isCanUserModify() )
        {
            atNode.addAttribute( NO_USER_MODIFICATION_TAG, BOOLEAN_FALSE );
        }
        else
        {
            atNode.addAttribute( NO_USER_MODIFICATION_TAG, BOOLEAN_TRUE );
        }

        // Equality
        String equality = at.getEqualityName();
        if ( ( equality != null ) && ( !equality.equals( "" ) ) )
        {
            atNode.addElement( EQUALITY_TAG ).setText( equality );
        }

        // Ordering
        String ordering = at.getOrderingName();
        if ( ( ordering != null ) && ( !ordering.equals( "" ) ) )
        {
            atNode.addElement( ORDERING_TAG ).setText( ordering );
        }

        // Substring
        String substring = at.getSubstrName();
        if ( ( substring != null ) && ( !substring.equals( "" ) ) )
        {
            atNode.addElement( SUBSTRING_TAG ).setText( substring );
        }
    }


    /**
     * Adds the given object class to its root Element.
     *
     * @param oc
     *      the object class to convert
     * @param root
     *      the root Element
     */
    private static void toXml( ObjectClassImpl oc, Element root )
    {
        Element ocNode = root.addElement( OBJECT_CLASS_TAG );

        // OID
        String oid = oc.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) )
        {
            ocNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        String[] aliases = oc.getNames();
        if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            Element aliasesNode = ocNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = oc.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) )
        {
            ocNode.addElement( DESCRIPTION_TAG ).setText( description );
        }

        // Superiors
        String[] superiors = oc.getSuperClassesNames();
        if ( ( superiors != null ) && ( superiors.length >= 1 ) )
        {
            Element superiorsNode = ocNode.addElement( SUPERIORS_TAG );
            for ( String superior : superiors )
            {
                superiorsNode.addElement( SUPERIOR_TAG ).setText( superior );
            }
        }

        // Type
        ObjectClassTypeEnum type = oc.getType();
        if ( type != null )
        {
            ocNode.addElement( TYPE_TAG ).setText( type.toString() );
        }

        // Obsolete
        if ( oc.isObsolete() )
        {
            ocNode.addAttribute( OBSOLETE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            ocNode.addAttribute( OBSOLETE_TAG, BOOLEAN_FALSE );
        }

        // Mandatory Attribute Types
        String[] mandatoryATs = oc.getMustNamesList();
        if ( ( mandatoryATs != null ) && ( mandatoryATs.length >= 1 ) )
        {
            Element mandatoryNode = ocNode.addElement( MANDATORY_TAG );
            for ( String mandatoryAT : mandatoryATs )
            {
                mandatoryNode.addElement( ATTRIBUTE_TYPE_TAG ).setText( mandatoryAT );
            }
        }

        // Optional Attribute Types
        String[] optionalATs = oc.getMayNamesList();
        if ( ( optionalATs != null ) && ( optionalATs.length >= 1 ) )
        {
            Element optionalNode = ocNode.addElement( OPTIONAL_TAG );
            for ( String optionalAT : optionalATs )
            {
                optionalNode.addElement( ATTRIBUTE_TYPE_TAG ).setText( optionalAT );
            }
        }
    }


    /**
     * Adds the given matching rule to its root Element.
     *
     * @param mr
     *      the matching rule to convert
     * @param root
     *      the root Element
     */
    private static void toXml( MatchingRuleImpl mr, Element root )
    {
        Element mrNode = root.addElement( MATCHING_RULE_TAG );

        // OID
        String oid = mr.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) )
        {
            mrNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        String[] aliases = mr.getNames();
        if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            Element aliasesNode = mrNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = mr.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) )
        {
            mrNode.addElement( DESCRIPTION_TAG ).setText( description );
        }

        // Obsolete
        if ( mr.isObsolete() )
        {
            mrNode.addAttribute( OBSOLETE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            mrNode.addAttribute( OBSOLETE_TAG, BOOLEAN_FALSE );
        }

        // Syntax OID
        String syntaxOid = mr.getSyntaxOid();
        if ( ( syntaxOid != null ) && ( !syntaxOid.equals( "" ) ) )
        {
            mrNode.addElement( SYNTAX_OID_TAG ).setText( syntaxOid );
        }
    }


    /**
     * Converts the given syntax to its source code representation
     * in XML file format.
     *
     * @param syntax
     *      the syntax to convert
     * @param root
     *      the root Element
     * @return
     *      the corresponding source code representation
     */
    private static void toXml( SyntaxImpl syntax, Element root )
    {
        Element syntaxNode = root.addElement( SYNTAX_TAG );

        // OID
        String oid = syntax.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) )
        {
            syntaxNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        String[] aliases = syntax.getNames();
        if ( ( aliases != null ) && ( aliases.length >= 1 ) )
        {
            Element aliasesNode = syntaxNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = syntax.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) )
        {
            syntaxNode.addElement( DESCRIPTION_TAG ).setText( description );
        }

        // Obsolete
        if ( syntax.isObsolete() )
        {
            syntaxNode.addAttribute( OBSOLETE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            syntaxNode.addAttribute( OBSOLETE_TAG, BOOLEAN_FALSE );
        }

        // Human Readible
        if ( syntax.isHumanReadable() )
        {
            syntaxNode.addAttribute( HUMAN_READABLE_TAG, BOOLEAN_TRUE );
        }
        else
        {
            syntaxNode.addAttribute( HUMAN_READABLE_TAG, BOOLEAN_FALSE );
        }
    }


    /**
     * XML Pretty Printer XSLT Transformation
     * 
     * @param document
     *      the Dom4j Document
     * @return
     */
    private static Document styleDocument( Document document )
    {
        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try
        {
            transformer = factory.newTransformer( new StreamSource( Activator.class
                .getResourceAsStream( "XmlFileFormat.xslt" ) ) );
        }
        catch ( TransformerConfigurationException e1 )
        {
            // Will never occur
        }

        // now lets style the given document
        DocumentSource source = new DocumentSource( document );
        DocumentResult result = new DocumentResult();
        try
        {
            transformer.transform( source, result );
        }
        catch ( TransformerException e )
        {
            // Will never occur
        }

        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }
}
