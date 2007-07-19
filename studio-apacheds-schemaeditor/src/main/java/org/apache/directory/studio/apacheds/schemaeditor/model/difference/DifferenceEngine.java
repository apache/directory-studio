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
import java.util.List;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.SchemaObject;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;


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
     * Gets the differences between two ObjectClassImpl Objects.
     *
     * @param oc1
     *      the source ObjectClassImpl Object
     * @param oc2
     *      the destination ObjectClassImpl Object
     * @return
     *      the differences between two ObjectClassImpl Objects.
     */
    public static List<Difference> getDifferences( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        List<Difference> differences = new ArrayList<Difference>();

        // Aliases
        differences.addAll( getAliasesDifferences( oc1, oc2 ) );

        // Description
        Difference descriptionDifference = getDescriptionDifference( oc1, oc2 );
        if ( descriptionDifference != null )
        {
            differences.add( descriptionDifference );
        }

        // Obsolete
        Difference obsoleteDifference = getObsoleteDifference( oc1, oc2 );
        if ( obsoleteDifference != null )
        {
            differences.add( obsoleteDifference );
        }

        // Class type
        Difference classTypeDifference = getClassTypeDifference( oc1, oc2 );
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
    public static List<Difference> getDifferences( AttributeTypeImpl at1, AttributeTypeImpl at2 )
    {
        List<Difference> differences = new ArrayList<Difference>();

        // Aliases
        differences.addAll( getAliasesDifferences( at1, at2 ) );

        // Description
        Difference descriptionDifference = getDescriptionDifference( at1, at2 );
        if ( descriptionDifference != null )
        {
            differences.add( descriptionDifference );
        }

        // Obsolete
        Difference obsoleteDifference = getObsoleteDifference( at1, at2 );
        if ( obsoleteDifference != null )
        {
            differences.add( obsoleteDifference );
        }

        // Usage
        Difference usageDifference = getUsageDifference( at1, at2 );
        if ( usageDifference != null )
        {
            differences.add( usageDifference );
        }

        // Superior
        Difference superiorDifference = getSuperiorDifference( at1, at2 );
        if ( superiorDifference != null )
        {
            differences.add( superiorDifference );
        }

        // Syntax
        Difference syntaxDifference = getSyntaxDifference( at1, at2 );
        if ( syntaxDifference != null )
        {
            differences.add( syntaxDifference );
        }

        // Syntax length
        Difference syntaxLengthDifference = getSyntaxLengthDifference( at1, at2 );
        if ( syntaxLengthDifference != null )
        {
            differences.add( syntaxLengthDifference );
        }

        // Single value
        Difference singleValueDifference = getSingleValueDifference( at1, at2 );
        if ( singleValueDifference != null )
        {
            differences.add( singleValueDifference );
        }

        // Collective
        Difference collectiveDifference = getCollectiveDifference( at1, at2 );
        if ( collectiveDifference != null )
        {
            differences.add( collectiveDifference );
        }

        // No user modification
        Difference noUserModificationDifference = getNoUserModificationDifference( at1, at2 );
        if ( noUserModificationDifference != null )
        {
            differences.add( noUserModificationDifference );
        }

        // Equality
        Difference equalityDifference = getEqualityDifference( at1, at2 );
        if ( equalityDifference != null )
        {
            differences.add( equalityDifference );
        }

        // Ordering
        Difference orderingDifference = getOrderingDifference( at1, at2 );
        if ( orderingDifference != null )
        {
            differences.add( orderingDifference );
        }

        // Substring
        Difference substringDifference = getSubstringDifference( at1, at2 );
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
    private static List<Difference> getAliasesDifferences( SchemaObject so1, SchemaObject so2 )
    {
        List<Difference> differences = new ArrayList<Difference>();

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
    private static Difference getDescriptionDifference( SchemaObject so1, SchemaObject so2 )
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
    private static Difference getObsoleteDifference( SchemaObject so1, SchemaObject so2 )
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
    private static Difference getClassTypeDifference( ObjectClassImpl oc1, ObjectClassImpl oc2 )
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
    private static List<Difference> getSuperiorClassesDifferences( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        List<Difference> differences = new ArrayList<Difference>();

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
    private static List<Difference> getMandatoryAttributeTypesDifferences( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        List<Difference> differences = new ArrayList<Difference>();

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
    private static List<Difference> getOptionalAttributeTypesDifferences( ObjectClassImpl oc1, ObjectClassImpl oc2 )
    {
        List<Difference> differences = new ArrayList<Difference>();

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
    private static Difference getUsageDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getSuperiorDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getSyntaxDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getSyntaxLengthDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getSingleValueDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getCollectiveDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getNoUserModificationDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getEqualityDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getOrderingDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
    private static Difference getSubstringDifference( AttributeTypeImpl at1, AttributeTypeImpl at2 )
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
