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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.LdapSyntax;
import org.apache.directory.api.ldap.model.schema.MatchingRule;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.dom4j.Branch;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 * This class is used to export a Schema file into the XML Format.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class XMLSchemaFileExporter
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
     * Converts the given schema to its source code representation
     * in XML file format.
     *
     * @param schema
     *      the schema to convert
     * @return
     *      the corresponding source code representation
     * @throws IOException
     */
    public static String toXml( Schema schema ) throws IOException
    {
        // Creating the Document
        Document document = DocumentHelper.createDocument();

        // Adding the schema
        addSchema( schema, document );

        // Creating the output stream we're going to put the XML in
        OutputStream os = new ByteArrayOutputStream();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$

        // Writing the XML.
        XMLWriter writer = new XMLWriter( os, outformat );
        writer.write( document );
        writer.flush();
        writer.close();

        return os.toString();
    }


    /**
     * Converts the given schemas to their source code representation
     * in one XML file format.
     *
     * @param schemas
     *      the array of schemas to convert
     * @return
     *      the corresponding source code representation
     * @throws IOException
     */
    public static String toXml( Schema[] schemas ) throws IOException
    {
        // Creating the Document and the 'root' Element
        Document document = DocumentHelper.createDocument();

        addSchemas( schemas, document );

        // Creating the output stream we're going to put the XML in
        OutputStream os = new ByteArrayOutputStream();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding( "UTF-8" ); //$NON-NLS-1$

        // Writing the XML.
        XMLWriter writer = new XMLWriter( os, outformat );
        writer.write( document );
        writer.flush();
        writer.close();

        return os.toString();
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
            String name = schema.getSchemaName();
            if ( ( name != null ) && ( !name.equals( "" ) ) ) //$NON-NLS-1$
            {
                element.addAttribute( NAME_TAG, name );
            }

            // Attribute Types
            List<AttributeType> ats = schema.getAttributeTypes();
            if ( ( ats != null ) && ( ats.size() >= 1 ) )
            {
                Element attributeTypesNode = element.addElement( ATTRIBUTE_TYPES_TAG );
                for ( AttributeType at : ats )
                {
                    toXml( at, attributeTypesNode );
                }
            }

            // Object Classes
            List<ObjectClass> ocs = schema.getObjectClasses();
            if ( ( ocs != null ) && ( ocs.size() >= 1 ) )
            {
                Element objectClassesNode = element.addElement( OBJECT_CLASSES_TAG );
                for ( ObjectClass oc : ocs )
                {
                    toXml( oc, objectClassesNode );
                }
            }

            // Matching Rules
            List<MatchingRule> mrs = schema.getMatchingRules();
            if ( ( mrs != null ) && ( mrs.size() >= 1 ) )
            {
                Element matchingRulesNode = element.addElement( MATCHING_RULES_TAG );
                for ( MatchingRule mr : mrs )
                {
                    toXml( mr, matchingRulesNode );
                }
            }

            // Syntaxes
            List<LdapSyntax> syntaxes = schema.getSyntaxes();
            if ( ( syntaxes != null ) && ( syntaxes.size() >= 1 ) )
            {
                Element syntaxesNode = element.addElement( SYNTAXES_TAG );
                for ( LdapSyntax syntax : syntaxes )
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
    private static void toXml( AttributeType at, Element root )
    {
        Element atNode = root.addElement( ATTRIBUTE_TYPE_TAG );

        // OID
        String oid = at.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) ) //$NON-NLS-1$
        {
            atNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        List<String> aliases = at.getNames();
        if ( ( aliases != null ) && ( aliases.size() >= 1 ) )
        {
            Element aliasesNode = atNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = at.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) ) //$NON-NLS-1$
        {
            atNode.addElement( DESCRIPTION_TAG ).setText( description );
        }

        // Superior
        String superior = at.getSuperiorOid();
        if ( ( superior != null ) && ( !superior.equals( "" ) ) ) //$NON-NLS-1$
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
        if ( ( syntax != null ) && ( !syntax.equals( "" ) ) ) //$NON-NLS-1$
        {
            atNode.addElement( SYNTAX_TAG ).setText( syntax );
        }

        // Syntax Length
        long syntaxLength = at.getSyntaxLength();
        if ( syntaxLength > 0 )
        {
            atNode.addElement( SYNTAX_LENGTH_TAG ).setText( "" + syntaxLength ); //$NON-NLS-1$
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
        if ( at.isSingleValued() )
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
        if ( at.isUserModifiable() )
        {
            atNode.addAttribute( NO_USER_MODIFICATION_TAG, BOOLEAN_FALSE );
        }
        else
        {
            atNode.addAttribute( NO_USER_MODIFICATION_TAG, BOOLEAN_TRUE );
        }

        // Equality
        String equality = at.getEqualityOid();
        if ( ( equality != null ) && ( !equality.equals( "" ) ) ) //$NON-NLS-1$
        {
            atNode.addElement( EQUALITY_TAG ).setText( equality );
        }

        // Ordering
        String ordering = at.getOrderingOid();
        if ( ( ordering != null ) && ( !ordering.equals( "" ) ) ) //$NON-NLS-1$
        {
            atNode.addElement( ORDERING_TAG ).setText( ordering );
        }

        // Substring
        String substring = at.getSubstringOid();
        if ( ( substring != null ) && ( !substring.equals( "" ) ) ) //$NON-NLS-1$
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
    private static void toXml( ObjectClass oc, Element root )
    {
        Element ocNode = root.addElement( OBJECT_CLASS_TAG );

        // OID
        String oid = oc.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) ) //$NON-NLS-1$
        {
            ocNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        List<String> aliases = oc.getNames();
        if ( ( aliases != null ) && ( aliases.size() >= 1 ) )
        {
            Element aliasesNode = ocNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = oc.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) ) //$NON-NLS-1$
        {
            ocNode.addElement( DESCRIPTION_TAG ).setText( description );
        }

        // Superiors
        List<String> superiors = oc.getSuperiorOids();
        if ( ( superiors != null ) && ( superiors.size() >= 1 ) )
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
        List<String> mandatoryATs = oc.getMustAttributeTypeOids();
        if ( ( mandatoryATs != null ) && ( mandatoryATs.size() >= 1 ) )
        {
            Element mandatoryNode = ocNode.addElement( MANDATORY_TAG );
            for ( String mandatoryAT : mandatoryATs )
            {
                mandatoryNode.addElement( ATTRIBUTE_TYPE_TAG ).setText( mandatoryAT );
            }
        }

        // Optional Attribute Types
        List<String> optionalATs = oc.getMayAttributeTypeOids();
        if ( ( optionalATs != null ) && ( optionalATs.size() >= 1 ) )
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
    private static void toXml( MatchingRule mr, Element root )
    {
        Element mrNode = root.addElement( MATCHING_RULE_TAG );

        // OID
        String oid = mr.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) ) //$NON-NLS-1$
        {
            mrNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        List<String> aliases = mr.getNames();
        if ( ( aliases != null ) && ( aliases.size() >= 1 ) )
        {
            Element aliasesNode = mrNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = mr.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) ) //$NON-NLS-1$
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
        if ( ( syntaxOid != null ) && ( !syntaxOid.equals( "" ) ) ) //$NON-NLS-1$
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
    private static void toXml( LdapSyntax syntax, Element root )
    {
        Element syntaxNode = root.addElement( SYNTAX_TAG );

        // OID
        String oid = syntax.getOid();
        if ( ( oid != null ) && ( !oid.equals( "" ) ) ) //$NON-NLS-1$
        {
            syntaxNode.addAttribute( OID_TAG, oid );
        }

        // Aliases
        List<String> aliases = syntax.getNames();
        if ( ( aliases != null ) && ( aliases.size() >= 1 ) )
        {
            Element aliasesNode = syntaxNode.addElement( ALIASES_TAG );
            for ( String alias : aliases )
            {
                aliasesNode.addElement( ALIAS_TAG ).setText( alias );
            }
        }

        // Description
        String description = syntax.getDescription();
        if ( ( description != null ) && ( !description.equals( "" ) ) ) //$NON-NLS-1$
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
}
