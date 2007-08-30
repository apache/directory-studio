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
package org.apache.directory.studio.apacheds.schemaeditor.model.difference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.Schema;


/**
 * This class represents the difference engine.
 * It is used to generate the difference between two Objects.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferenceEngine
{
    /**
     * Gets the differences between two Lists of Schemas.
     *
     * @param l1
     *      the first list
     * @param l2
     *      the second list
     * @return
     *      the differences between the two schema Lists
     */
    public static List<SchemaDifference> getDifferences( List<Schema> l1, List<Schema> l2 )
    {
        List<SchemaDifference> differences = new ArrayList<SchemaDifference>();

        // Building Maps for schemas
        Map<String, Schema> mapL1 = new HashMap<String, Schema>();
        for ( Schema schema : l1 )
        {
            mapL1.put( schema.getName().toLowerCase(), schema );
        }
        Map<String, Schema> mapL2 = new HashMap<String, Schema>();
        for ( Schema schema : l2 )
        {
            mapL2.put( schema.getName().toLowerCase(), schema );
        }

        // Looping on schemas from the first list
        for ( Schema schemaFromL1 : l1 )
        {
            Schema schemaFromL2 = mapL2.get( schemaFromL1.getName().toLowerCase() );
            if ( schemaFromL2 == null )
            {
                SchemaDifference schemaDifference = new SchemaDifference( schemaFromL1, null, DifferenceType.REMOVED );
                differences.add( schemaDifference );

                // Adding attribute types
                for ( AttributeTypeImpl at : schemaFromL1.getAttributeTypes() )
                {
                    schemaDifference.addAttributeTypeDifference( new AttributeTypeDifference( null, at,
                        DifferenceType.REMOVED ) );
                }

                // Adding object classes
                for ( ObjectClassImpl oc : schemaFromL1.getObjectClasses() )
                {
                    schemaDifference.addObjectClassDifference( new ObjectClassDifference( null, oc,
                        DifferenceType.REMOVED ) );
                }
            }
            else
            {
                SchemaDifference schemaDifference = new SchemaDifference( schemaFromL1, schemaFromL2,
                    DifferenceType.IDENTICAL );
                differences.add( schemaDifference );

                // Building Maps for attribute types
                Map<String, AttributeTypeImpl> atMapL1 = new HashMap<String, AttributeTypeImpl>();
                for ( AttributeTypeImpl at : schemaFromL1.getAttributeTypes() )
                {
                    atMapL1.put( at.getOid(), at );
                }
                Map<String, AttributeTypeImpl> atMapL2 = new HashMap<String, AttributeTypeImpl>();
                for ( AttributeTypeImpl at : schemaFromL2.getAttributeTypes() )
                {
                    atMapL2.put( at.getOid(), at );
                }

                // Looping on the attribute types from the Schema from the first list
                for ( AttributeTypeImpl atFromL1 : schemaFromL1.getAttributeTypes() )
                {
                    AttributeTypeImpl atFromL2 = atMapL2.get( atFromL1.getOid() );
                    if ( atFromL2 == null )
                    {
                        AttributeTypeDifference attributeTypeDifference = new AttributeTypeDifference( atFromL1, null,
                            DifferenceType.REMOVED );
                        schemaDifference.addAttributeTypeDifference( attributeTypeDifference );
                        schemaDifference.setType( DifferenceType.MODIFIED );
                    }
                    else
                    {
                        AttributeTypeDifference attributeTypeDifference = new AttributeTypeDifference( atFromL1,
                            atFromL2, DifferenceType.IDENTICAL );
                        schemaDifference.addAttributeTypeDifference( attributeTypeDifference );

                        List<PropertyDifference> atDifferences = getDifferences( atFromL1, atFromL2 );
                        if ( atDifferences.size() > 0 )
                        {
                            attributeTypeDifference.setType( DifferenceType.MODIFIED );
                            attributeTypeDifference.addDifferences( atDifferences );
                            schemaDifference.setType( DifferenceType.MODIFIED );
                        }
                    }
                }

                // Looping on the attribute types from the Schema from the second list
                for ( AttributeTypeImpl atFromL2 : schemaFromL2.getAttributeTypes() )
                {
                    AttributeTypeImpl atFromL1 = atMapL1.get( atFromL2.getOid() );
                    if ( atFromL1 == null )
                    {
                        AttributeTypeDifference attributeTypeDifference = new AttributeTypeDifference( null, atFromL2,
                            DifferenceType.ADDED );
                        schemaDifference.addAttributeTypeDifference( attributeTypeDifference );
                        schemaDifference.setType( DifferenceType.MODIFIED );
                    }
                    // If atFromL1 exists, then it has already been processed when looping on the first list. 
                }

                // Building Maps for object classes
                Map<String, ObjectClassImpl> ocMapL1 = new HashMap<String, ObjectClassImpl>();
                for ( ObjectClassImpl oc : schemaFromL1.getObjectClasses() )
                {
                    ocMapL1.put( oc.getOid(), oc );
                }
                Map<String, ObjectClassImpl> ocMapL2 = new HashMap<String, ObjectClassImpl>();
                for ( ObjectClassImpl oc : schemaFromL2.getObjectClasses() )
                {
                    ocMapL2.put( oc.getOid(), oc );
                }

                // Looping on the object classes from the Schema from the first list
                for ( ObjectClassImpl ocFromL1 : schemaFromL1.getObjectClasses() )
                {
                    ObjectClassImpl ocFromL2 = ocMapL2.get( ocFromL1.getOid() );
                    if ( ocFromL2 == null )
                    {
                        ObjectClassDifference objectClassDifference = new ObjectClassDifference( ocFromL1, null,
                            DifferenceType.REMOVED );
                        schemaDifference.addObjectClassDifference( objectClassDifference );
                        schemaDifference.setType( DifferenceType.MODIFIED );
                    }
                    else
                    {
                        ObjectClassDifference objectClassDifference = new ObjectClassDifference( ocFromL1, ocFromL2,
                            DifferenceType.IDENTICAL );
                        schemaDifference.addObjectClassDifference( objectClassDifference );

                        List<PropertyDifference> ocDifferences = getDifferences( ocFromL1, ocFromL2 );
                        if ( ocDifferences.size() > 0 )
                        {
                            objectClassDifference.setType( DifferenceType.MODIFIED );
                            objectClassDifference.addDifferences( ocDifferences );
                            schemaDifference.setType( DifferenceType.MODIFIED );
                        }
                    }
                }

                // Looping on the object classes from the Schema from the second list
                for ( ObjectClassImpl ocFromL2 : schemaFromL2.getObjectClasses() )
                {
                    ObjectClassImpl ocFromL1 = ocMapL1.get( ocFromL2.getOid() );
                    if ( ocFromL1 == null )
                    {
                        ObjectClassDifference objectClassDifference = new ObjectClassDifference( null, ocFromL2,
                            DifferenceType.ADDED );
                        schemaDifference.addObjectClassDifference( objectClassDifference );
                        schemaDifference.setType( DifferenceType.MODIFIED );
                    }
                    // If ocFromL1 exists, then it has already been processed when looping on the first list. 
                }
            }
        }

        // Looping on schemas from the second list
        for ( Schema schemaFromL2 : l2 )
        {
            Schema schemaFromL1 = mapL1.get( schemaFromL2.getName().toLowerCase() );
            if ( schemaFromL1 == null )
            {
                SchemaDifference schemaDifference = new SchemaDifference( null, schemaFromL2, DifferenceType.ADDED );
                differences.add( schemaDifference );

                // Adding attribute types
                for ( AttributeTypeImpl at : schemaFromL2.getAttributeTypes() )
                {
                    schemaDifference.addAttributeTypeDifference( new AttributeTypeDifference( null, at,
                        DifferenceType.ADDED ) );
                }

                // Adding object classes
                for ( ObjectClassImpl oc : schemaFromL2.getObjectClasses() )
                {
                    schemaDifference.addObjectClassDifference( new ObjectClassDifference( null, oc,
                        DifferenceType.ADDED ) );
                }
            }
        }

        return differences;
    }


    /**
     * Gets the differences between two ObjectClassImpl Objects.
     *
     * @param oc1
     *      the source ObjectClassImpl Object
     * @param oc2
     *      the destination ObjectClassImpl Object
     * @return
     *      the differences between two ObjectClassImpl Objects.
     */
    public static List<PropertyDifference> getDifferences( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        List<PropertyDifference> differences = new ArrayList<PropertyDifference>();

        // Aliases
        differences.addAll( getAliasesDifferences( oc1, oc2 ) );

        // Description
        PropertyDifference descriptionDifference = getDescriptionDifference( oc1, oc2 );
        if ( descriptionDifference != null )
        {
            differences.add( descriptionDifference );
        }

        // Obsolete
        PropertyDifference obsoleteDifference = getObsoleteDifference( oc1, oc2 );
        if ( obsoleteDifference != null )
        {
            differences.add( obsoleteDifference );
        }

        // Class type
        PropertyDifference classTypeDifference = getClassTypeDifference( oc1, oc2 );
        if ( classTypeDifference != null )
        {
            differences.add( classTypeDifference );
        }

        // Superior classes
        differences.addAll( getSuperiorClassesDifferences( oc1, oc2 ) );

        // Mandatory attribute types
        differences.addAll( getMandatoryAttributeTypesDifferences( oc1, oc2 ) );

        // Optional attribute types
        differences.addAll( getOptionalAttributeTypesDifferences( oc1, oc2 ) );

        return differences;
    }


    /**
     * Gets the differences between two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the differences between two AttributeTypeImpl Objects.
     */
    public static List<PropertyDifference> getDifferences( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        List<PropertyDifference> differences = new ArrayList<PropertyDifference>();

        // Aliases
        differences.addAll( getAliasesDifferences( at1, at2 ) );

        // Description
        PropertyDifference descriptionDifference = getDescriptionDifference( at1, at2 );
        if ( descriptionDifference != null )
        {
            differences.add( descriptionDifference );
        }

        // Obsolete
        PropertyDifference obsoleteDifference = getObsoleteDifference( at1, at2 );
        if ( obsoleteDifference != null )
        {
            differences.add( obsoleteDifference );
        }

        // Usage
        PropertyDifference usageDifference = getUsageDifference( at1, at2 );
        if ( usageDifference != null )
        {
            differences.add( usageDifference );
        }

        // Superior
        PropertyDifference superiorDifference = getSuperiorDifference( at1, at2 );
        if ( superiorDifference != null )
        {
            differences.add( superiorDifference );
        }

        // Syntax
        PropertyDifference syntaxDifference = getSyntaxDifference( at1, at2 );
        if ( syntaxDifference != null )
        {
            differences.add( syntaxDifference );
        }

        // Syntax length
        PropertyDifference syntaxLengthDifference = getSyntaxLengthDifference( at1, at2 );
        if ( syntaxLengthDifference != null )
        {
            differences.add( syntaxLengthDifference );
        }

        // Single value
        PropertyDifference singleValueDifference = getSingleValueDifference( at1, at2 );
        if ( singleValueDifference != null )
        {
            differences.add( singleValueDifference );
        }

        // Collective
        PropertyDifference collectiveDifference = getCollectiveDifference( at1, at2 );
        if ( collectiveDifference != null )
        {
            differences.add( collectiveDifference );
        }

        // No user modification
        PropertyDifference noUserModificationDifference = getNoUserModificationDifference( at1, at2 );
        if ( noUserModificationDifference != null )
        {
            differences.add( noUserModificationDifference );
        }

        // Equality
        PropertyDifference equalityDifference = getEqualityDifference( at1, at2 );
        if ( equalityDifference != null )
        {
            differences.add( equalityDifference );
        }

        // Ordering
        PropertyDifference orderingDifference = getOrderingDifference( at1, at2 );
        if ( orderingDifference != null )
        {
            differences.add( orderingDifference );
        }

        // Substring
        PropertyDifference substringDifference = getSubstringDifference( at1, at2 );
        if ( substringDifference != null )
        {
            differences.add( substringDifference );
        }

        return differences;
    }


    /**
     * Gets the 'Aliases' differences between the two SchemaObject Objects.
     *
     * @param so1
     *      the source SchemaObject Object
     * @param so2
     *      the destination SchemaObject Object
     * @return
     *      the 'Aliases' differences between the two SchemaObject Objects
     */
    private static List<PropertyDifference> getAliasesDifferences( SchemaObject so1, SchemaObject so2 )
    {
        List<PropertyDifference> differences = new ArrayList<PropertyDifference>();

        String[] so1Names = so1.getNames();
        List<String> so1NamesList = new ArrayList<String>();
        if ( so1Names != null )
        {
            for ( String name : so1Names )
            {
                so1NamesList.add( name );
            }
        }

        String[] so2Names = so2.getNames();
        List<String> so2NamesList = new ArrayList<String>();
        if ( so2Names != null )
        {
            for ( String name : so2Names )
            {
                so2NamesList.add( name );
            }
        }

        for ( String name : so1NamesList )
        {
            if ( !so2NamesList.contains( name ) )
            {
                PropertyDifference diff = new AliasDifference( so1, so2, DifferenceType.REMOVED );
                diff.setOldValue( name );
                differences.add( diff );
            }
        }

        for ( String name : so2NamesList )
        {
            if ( !so1NamesList.contains( name ) )
            {
                PropertyDifference diff = new AliasDifference( so1, so2, DifferenceType.ADDED );
                diff.setNewValue( name );
                differences.add( diff );
            }
        }

        return differences;
    }


    /**
     * Gets the 'Description' difference between the two SchemaObject Objects.
     *
     * @param so1
     *      the source SchemaObject Object
     * @param so2
     *      the destination SchemaObject Object
     * @return
     *      the 'Description' difference between the two SchemaObject Objects
     */
    private static PropertyDifference getDescriptionDifference( SchemaObject so1, SchemaObject so2 )
    {
        String so1Description = so1.getDescription();
        String so2Description = so2.getDescription();

        if ( ( so1Description == null ) && ( so2Description != null ) )
        {
            PropertyDifference diff = new DescriptionDifference( so1, so2, DifferenceType.ADDED );
            diff.setNewValue( so2Description );
            return diff;
        }
        else if ( ( so1Description != null ) && ( so2Description == null ) )
        {
            PropertyDifference diff = new DescriptionDifference( so1, so2, DifferenceType.REMOVED );
            diff.setOldValue( so1Description );
            return diff;
        }
        else if ( ( so1Description != null ) && ( so2Description != null ) )
        {
            if ( !so1Description.equals( so2Description ) )
            {
                PropertyDifference diff = new DescriptionDifference( so1, so2, DifferenceType.MODIFIED );
                diff.setOldValue( so1Description );
                diff.setNewValue( so2Description );
                return diff;
            }
        }

        return null;
    }


    /**
     * Gets the 'Obsolete' difference between the two SchemaObject Objects.
     *
     * @param so1
     *      the source SchemaObject Object
     * @param so2
     *      the destination SchemaObject Object
     * @return
     *      the 'Obsolete' difference between the two SchemaObject Objects
     */
    private static PropertyDifference getObsoleteDifference( SchemaObject so1, SchemaObject so2 )
    {
        boolean so1Obsolete = so1.isObsolete();
        boolean so2Obsolete = so2.isObsolete();

        if ( so1Obsolete != so2Obsolete )
        {
            PropertyDifference diff = new ObsoleteDifference( so1, so2 );
            diff.setOldValue( so1Obsolete );
            diff.setNewValue( so2Obsolete );
            return diff;
        }

        return null;
    }


    /**
     * Gets the 'Class type' difference between the two ObjectClassImpl Objects.
     *
     * @param oc1
     *      the source ObjectClassImpl Object
     * @param oc2
     *      the destination ObjectClassImpl Object
     * @return
     *      the 'Class type' difference between the two ObjectClassImpl Objects
     */
    private static PropertyDifference getClassTypeDifference( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        ObjectClassTypeEnum oc1ClassType = oc1.getType();
        ObjectClassTypeEnum oc2ClassType = oc2.getType();

        if ( oc1ClassType != oc2ClassType )
        {
            PropertyDifference diff = new ClassTypeDifference( oc1, oc2 );
            diff.setOldValue( oc1ClassType );
            diff.setNewValue( oc2ClassType );
            return diff;
        }

        return null;
    }


    /**
     * Gets the 'Superior Classes' differences between the two ObjectClassImpl Objects.
     *
     * @param oc1
     *      the source ObjectClassImpl Object
     * @param oc2
     *      the destination ObjectClassImpl Object
     * @return
     *      the 'Superior Classes' differences between the two ObjectClassImpl Objects
     */
    private static List<PropertyDifference> getSuperiorClassesDifferences( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        List<PropertyDifference> differences = new ArrayList<PropertyDifference>();

        String[] oc1Sups = oc1.getSuperClassesNames();
        List<String> oc1SupsList = new ArrayList<String>();
        if ( oc1Sups != null )
        {
            for ( String name : oc1Sups )
            {
                oc1SupsList.add( name );
            }
        }

        String[] oc2Sups = oc2.getSuperClassesNames();
        List<String> oc2SupsList = new ArrayList<String>();
        if ( oc2Sups != null )
        {
            for ( String name : oc2Sups )
            {
                oc2SupsList.add( name );
            }
        }

        for ( String name : oc1SupsList )
        {
            if ( !oc2SupsList.contains( name ) )
            {
                PropertyDifference diff = new SuperiorOCDifference( oc1, oc2, DifferenceType.REMOVED );
                diff.setOldValue( name );
                differences.add( diff );
            }
        }

        for ( String name : oc2SupsList )
        {
            if ( !oc1SupsList.contains( name ) )
            {
                PropertyDifference diff = new SuperiorOCDifference( oc1, oc2, DifferenceType.ADDED );
                diff.setNewValue( name );
                differences.add( diff );
            }
        }

        return differences;
    }


    /**
     * Gets the 'Mandatory attribute types' differences between the two ObjectClassImpl Objects.
     *
     * @param oc1
     *      the source ObjectClassImpl Object
     * @param oc2
     *      the destination ObjectClassImpl Object
     * @return
     *      the 'Mandatory attribute types' differences between the two ObjectClassImpl Objects
     */
    private static List<PropertyDifference> getMandatoryAttributeTypesDifferences( ObjectClassImpl oc1,
        ObjectClassImpl oc2 )
    {
        List<PropertyDifference> differences = new ArrayList<PropertyDifference>();

        String[] oc1Musts = oc1.getMustNamesList();
        List<String> oc1MustsList = new ArrayList<String>();
        if ( oc1Musts != null )
        {
            for ( String name : oc1Musts )
            {
                oc1MustsList.add( name );
            }
        }

        String[] oc2Musts = oc2.getMustNamesList();
        List<String> oc2MustsList = new ArrayList<String>();
        if ( oc2Musts != null )
        {
            for ( String name : oc2Musts )
            {
                oc2MustsList.add( name );
            }
        }

        for ( String name : oc1MustsList )
        {
            if ( !oc2MustsList.contains( name ) )
            {
                PropertyDifference diff = new MandatoryATDifference( oc1, oc2, DifferenceType.REMOVED );
                diff.setOldValue( name );
                differences.add( diff );
            }
        }

        for ( String name : oc2MustsList )
        {
            if ( !oc1MustsList.contains( name ) )
            {
                PropertyDifference diff = new MandatoryATDifference( oc1, oc2, DifferenceType.ADDED );
                diff.setNewValue( name );
                differences.add( diff );
            }
        }

        return differences;
    }


    /**
     * Gets the 'Optional attribute types' differences between the two ObjectClassImpl Objects.
     *
     * @param oc1
     *      the source ObjectClassImpl Object
     * @param oc2
     *      the destination ObjectClassImpl Object
     * @return
     *      the 'Optional attribute types' differences between the two ObjectClassImpl Objects
     */
    private static List<PropertyDifference> getOptionalAttributeTypesDifferences( ObjectClassImpl oc1,
        ObjectClassImpl oc2 )
    {
        List<PropertyDifference> differences = new ArrayList<PropertyDifference>();

        String[] oc1Mays = oc1.getMayNamesList();
        List<String> oc1MaysList = new ArrayList<String>();
        if ( oc1Mays != null )
        {
            for ( String name : oc1Mays )
            {
                oc1MaysList.add( name );
            }
        }

        String[] oc2Mays = oc2.getMayNamesList();
        List<String> oc2MaysList = new ArrayList<String>();
        if ( oc2Mays != null )
        {
            for ( String name : oc2Mays )
            {
                oc2MaysList.add( name );
            }
        }

        for ( String name : oc1MaysList )
        {
            if ( !oc2MaysList.contains( name ) )
            {
                PropertyDifference diff = new OptionalATDifference( oc1, oc2, DifferenceType.REMOVED );
                diff.setOldValue( name );
                differences.add( diff );
            }
        }

        for ( String name : oc2MaysList )
        {
            if ( !oc1MaysList.contains( name ) )
            {
                PropertyDifference diff = new OptionalATDifference( oc1, oc2, DifferenceType.ADDED );
                diff.setNewValue( name );
                differences.add( diff );
            }
        }

        return differences;
    }


    /**
     * Gets the 'Usage' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Usage' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getUsageDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        UsageEnum at1Usage = at1.getUsage();
        UsageEnum at2Usage = at2.getUsage();

        if ( at1Usage != at2Usage )
        {
            PropertyDifference diff = new UsageDifference( at1, at2 );
            diff.setOldValue( at1Usage );
            diff.setNewValue( at2Usage );
            return diff;
        }

        return null;
    }


    /**
     * Gets the 'Superior' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Superior' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getSuperiorDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        String at1Superior = at1.getSuperiorName();
        String at2Superior = at2.getSuperiorName();

        if ( ( at1Superior == null ) && ( at2Superior != null ) )
        {
            PropertyDifference diff = new SuperiorATDifference( at1, at2, DifferenceType.ADDED );
            diff.setNewValue( at2Superior );
            return diff;
        }
        else if ( ( at1Superior != null ) && ( at2Superior == null ) )
        {
            PropertyDifference diff = new SuperiorATDifference( at1, at2, DifferenceType.REMOVED );
            diff.setOldValue( at1Superior );
            return diff;
        }
        else if ( ( at1Superior != null ) && ( at2Superior != null ) )
        {
            if ( !at1Superior.equals( at2Superior ) )
            {
                PropertyDifference diff = new SuperiorATDifference( at1, at2, DifferenceType.MODIFIED );
                diff.setOldValue( at1Superior );
                diff.setNewValue( at2Superior );
                return diff;
            }
        }

        return null;
    }


    /**
     * Gets the 'Syntax' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Syntax' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getSyntaxDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        String at1Syntax = at1.getSyntaxOid();
        String at2Syntax = at2.getSyntaxOid();

        if ( ( at1Syntax == null ) && ( at2Syntax != null ) )
        {
            PropertyDifference diff = new SyntaxDifference( at1, at2, DifferenceType.ADDED );
            diff.setNewValue( at2Syntax );
            return diff;
        }
        else if ( ( at1Syntax != null ) && ( at2Syntax == null ) )
        {
            PropertyDifference diff = new SyntaxDifference( at1, at2, DifferenceType.REMOVED );
            diff.setOldValue( at1Syntax );
            return diff;
        }
        else if ( ( at1Syntax != null ) && ( at2Syntax != null ) )
        {
            if ( !at1Syntax.equals( at2Syntax ) )
            {
                PropertyDifference diff = new SyntaxDifference( at1, at2, DifferenceType.MODIFIED );
                diff.setOldValue( at1Syntax );
                diff.setNewValue( at2Syntax );
                return diff;
            }
        }

        return null;
    }


    /**
     * Gets the 'Syntax length' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Syntax length' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getSyntaxLengthDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        int at1SyntaxLength = at1.getLength();
        int at2SyntaxLength = at2.getLength();

        if ( ( at1SyntaxLength == -1 ) && ( at2SyntaxLength != -1 ) )
        {
            PropertyDifference diff = new SyntaxLengthDifference( at1, at2, DifferenceType.ADDED );
            diff.setNewValue( at2SyntaxLength );
            return diff;
        }
        else if ( ( at1SyntaxLength != -1 ) && ( at2SyntaxLength == -1 ) )
        {
            PropertyDifference diff = new SyntaxLengthDifference( at1, at2, DifferenceType.REMOVED );
            diff.setOldValue( at1SyntaxLength );
            return diff;
        }
        else if ( ( at1SyntaxLength != -1 ) && ( at2SyntaxLength != -1 ) )
        {
            if ( at1SyntaxLength != at2SyntaxLength )
            {
                PropertyDifference diff = new SyntaxLengthDifference( at1, at2, DifferenceType.MODIFIED );
                diff.setOldValue( at1SyntaxLength );
                diff.setNewValue( at2SyntaxLength );
                return diff;
            }
        }

        return null;
    }


    /**
     * Gets the 'Single value' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Single value' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getSingleValueDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        boolean at1SingleValue = at1.isSingleValue();
        boolean at2SingleValue = at2.isSingleValue();

        if ( at1SingleValue != at2SingleValue )
        {
            PropertyDifference diff = new SingleValueDifference( at1, at2 );
            diff.setOldValue( at1SingleValue );
            diff.setNewValue( at2SingleValue );
            return diff;
        }

        return null;
    }


    /**
     * Gets the 'Collective' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Collective' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getCollectiveDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        boolean at1Collective = at1.isCollective();
        boolean at2Collective = at2.isCollective();

        if ( at1Collective != at2Collective )
        {
            PropertyDifference diff = new CollectiveDifference( at1, at2 );
            diff.setOldValue( at1Collective );
            diff.setNewValue( at2Collective );
            return diff;
        }

        return null;
    }


    /**
     * Gets the 'No user modification' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'No user modification' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getNoUserModificationDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        boolean at1CanUserModify = at1.isCanUserModify();
        boolean at2CanUserModify = at2.isCanUserModify();

        if ( at1CanUserModify != at2CanUserModify )
        {
            PropertyDifference diff = new NoUserModificationDifference( at1, at2 );
            diff.setOldValue( at1CanUserModify );
            diff.setNewValue( at2CanUserModify );
            return diff;
        }

        return null;
    }


    /**
     * Gets the 'Equality' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Equality' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getEqualityDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        String at1Equality = at1.getEqualityName();
        String at2Equality = at2.getEqualityName();

        if ( ( at1Equality == null ) && ( at2Equality != null ) )
        {
            PropertyDifference diff = new EqualityDifference( at1, at2, DifferenceType.ADDED );
            diff.setNewValue( at2Equality );
            return diff;
        }
        else if ( ( at1Equality != null ) && ( at2Equality == null ) )
        {
            PropertyDifference diff = new EqualityDifference( at1, at2, DifferenceType.REMOVED );
            diff.setOldValue( at1Equality );
            return diff;
        }
        else if ( ( at1Equality != null ) && ( at2Equality != null ) )
        {
            if ( !at1Equality.equals( at2Equality ) )
            {
                PropertyDifference diff = new EqualityDifference( at1, at2, DifferenceType.MODIFIED );
                diff.setOldValue( at1Equality );
                diff.setNewValue( at2Equality );
                return diff;
            }
        }

        return null;
    }


    /**
     * Gets the 'Ordering' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Ordering' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getOrderingDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        String at1Ordering = at1.getOrderingName();
        String at2Ordering = at2.getOrderingName();

        if ( ( at1Ordering == null ) && ( at2Ordering != null ) )
        {
            PropertyDifference diff = new OrderingDifference( at1, at2, DifferenceType.ADDED );
            diff.setNewValue( at2Ordering );
            return diff;
        }
        else if ( ( at1Ordering != null ) && ( at2Ordering == null ) )
        {
            PropertyDifference diff = new OrderingDifference( at1, at2, DifferenceType.REMOVED );
            diff.setOldValue( at1Ordering );
            return diff;
        }
        else if ( ( at1Ordering != null ) && ( at2Ordering != null ) )
        {
            if ( !at1Ordering.equals( at2Ordering ) )
            {
                PropertyDifference diff = new OrderingDifference( at1, at2, DifferenceType.MODIFIED );
                diff.setOldValue( at1Ordering );
                diff.setNewValue( at2Ordering );
                return diff;
            }
        }

        return null;
    }


    /**
     * Gets the 'Substring' difference between the two AttributeTypeImpl Objects.
     *
     * @param at1
     *      the source AttributeTypeImpl Object
     * @param at2
     *      the destination AttributeTypeImpl Object
     * @return
     *      the 'Substring' difference between the two AttributeTypeImpl Objects
     */
    private static PropertyDifference getSubstringDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        String at1Substring = at1.getSubstrName();
        String at2Substring = at2.getSubstrName();

        if ( ( at1Substring == null ) && ( at2Substring != null ) )
        {
            PropertyDifference diff = new SubstringDifference( at1, at2, DifferenceType.ADDED );
            diff.setNewValue( at2Substring );
            return diff;
        }
        else if ( ( at1Substring != null ) && ( at2Substring == null ) )
        {
            PropertyDifference diff = new SubstringDifference( at1, at2, DifferenceType.REMOVED );
            diff.setOldValue( at1Substring );
            return diff;
        }
        else if ( ( at1Substring != null ) && ( at2Substring != null ) )
        {
            if ( !at1Substring.equals( at2Substring ) )
            {
                PropertyDifference diff = new SubstringDifference( at1, at2, DifferenceType.MODIFIED );
                diff.setOldValue( at1Substring );
                diff.setNewValue( at2Substring );
                return diff;
            }
        }

        return null;
    }
}
