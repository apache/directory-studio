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
package org.apache.directory.studio.schemaeditor.model.difference;


import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.api.ldap.model.schema.MutableObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClass;
import org.apache.directory.api.ldap.model.schema.ObjectClassTypeEnum;
import org.apache.directory.api.ldap.model.schema.UsageEnum;


/**
 * This class tests the DifferenceEngine class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DifferenceEngineTest extends TestCase
{
    /**
     * Tests the AddAliasDifference.
     *
     * @throws Exception
     */
    public void testAddAliasDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setNames( new String[]
            { "alias" } ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AliasDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "alias", ( ( AliasDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddDescriptionDifference.
     *
     * @throws Exception
     */
    public void testAddDescriptionDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setDescription( "Description" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof DescriptionDifference )
            || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "Description", ( ( DescriptionDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddEqualityDifference.
     *
     * @throws Exception
     */
    public void testAddEqualityDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setEqualityOid( "Equality" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof EqualityDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "Equality", ( ( EqualityDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddMandatoryATDifference.
     *
     * @throws Exception
     */
    public void testAddMandatoryATDifference() throws Exception
    {
        ObjectClass o1 = new ObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setMustAttributeTypeOids( Arrays.asList( new String[]
            { "must" } ) ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof MandatoryATDifference )
            || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "must", ( ( MandatoryATDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddOptionalATDifference.
     *
     * @throws Exception
     */
    public void testAddOptionalATDifference() throws Exception
    {
        ObjectClass o1 = new ObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setMayAttributeTypeOids( Arrays.asList( new String[]
            { "may" } ) ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof OptionalATDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "may", ( ( OptionalATDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddOrderingDifference.
     *
     * @throws Exception
     */
    public void testAddOrderingDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setOrderingOid( "Ordering" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof OrderingDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "Ordering", ( ( OrderingDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddSubstringDifference.
     *
     * @throws Exception
     */
    public void testAddSubstringDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSubstringOid( "Substring" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SubstringDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "Substring", ( ( SubstringDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddSuperiorATDifference.
     *
     * @throws Exception
     */
    public void testAddSuperiorATDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSuperiorOid( "superiorAT" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SuperiorATDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "superiorAT", ( ( SuperiorATDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddSuperiorOCDifference.
     *
     * @throws Exception
     */
    public void testAddSuperiorOCDifference() throws Exception
    {
        ObjectClass o1 = new ObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSuperiorOids( Arrays.asList( new String[]
            { "superiorOC" } ) ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SuperiorOCDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "superiorOC", ( ( SuperiorOCDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddSyntaxDifference.
     *
     * @throws Exception
     */
    public void testAddSyntaxDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSyntaxOid( "1.2.3.4.5" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SyntaxDifference ) || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( "1.2.3.4.5", ( ( SyntaxDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the AddSyntaxLengthDifference.
     *
     * @throws Exception
     */
    public void testAddSyntaxLengthDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSyntaxLength( 1234 );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SyntaxLengthDifference )
            || ( !difference.getType().equals( DifferenceType.ADDED ) ) )
        {
            fail();
        }

        assertEquals( 1234L, ( ( SyntaxLengthDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyClassTypeDifference.
     *
     * @throws Exception
     */
    public void testModifyClassTypeDifference() throws Exception
    {
        MutableObjectClass o1 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o1.setType( ObjectClassTypeEnum.STRUCTURAL );
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setType( ObjectClassTypeEnum.ABSTRACT );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ClassTypeDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( ObjectClassTypeEnum.STRUCTURAL, ( ( ClassTypeDifference ) difference ).getOldValue() );
        assertEquals( ObjectClassTypeEnum.ABSTRACT, ( ( ClassTypeDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyCollectiveDifference.
     *
     * @throws Exception
     */
    public void testModifyCollectiveDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setCollective( true );
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setCollective( false );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof CollectiveDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( true, ( ( CollectiveDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( CollectiveDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyDescriptionDifference.
     *
     * @throws Exception
     */
    public void testModifyDescriptionDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setDescription( "Description" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setDescription( "New Description" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof DescriptionDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( "Description", ( ( DescriptionDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertEquals( "New Description", ( ( DescriptionDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the ModifyEqualityDifference.
     *
     * @throws Exception
     */
    public void testModifyEqualityDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setEqualityOid( "equalityName" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setEqualityOid( "newEqualityName" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof EqualityDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( "equalityName", ( ( EqualityDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertEquals( "newEqualityName", ( ( EqualityDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the ModifyNoUserModificationDifference.
     *
     * @throws Exception
     */
    public void testModifyNoUserModificationDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setUserModifiable( true );
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setUserModifiable( false );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof NoUserModificationDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( true, ( ( NoUserModificationDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( NoUserModificationDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyObsoleteDifference.
     *
     * @throws Exception
     */
    public void testModifyObsoleteDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setObsolete( true );
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setObsolete( false );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ObsoleteDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( true, ( ( ObsoleteDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( ObsoleteDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyOrderingDifference.
     *
     * @throws Exception
     */
    public void testModifyOrderingDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setOrderingOid( "orderingName" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setOrderingOid( "newOrderingName" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof OrderingDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( "orderingName", ( ( OrderingDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertEquals( "newOrderingName", ( ( OrderingDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the ModifySingleValueDifference.
     *
     * @throws Exception
     */
    public void testModifySingleValueDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSingleValued( true );
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSingleValued( false );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SingleValueDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( true, ( ( SingleValueDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( SingleValueDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifySubstringDifference.
     *
     * @throws Exception
     */
    public void testModifySubstringDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSubstringOid( "substrName" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSubstringOid( "newSubstrName" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SubstringDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( "substrName", ( ( SubstringDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertEquals( "newSubstrName", ( ( SubstringDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the ModifySuperiorATDifference.
     *
     * @throws Exception
     */
    public void testModifySuperiorATDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSuperiorOid( "superiorName" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSuperiorOid( "newSuperiorName" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SuperiorATDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( "superiorName", ( ( SuperiorATDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertEquals( "newSuperiorName", ( ( SuperiorATDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the ModifySyntaxDifference.
     *
     * @throws Exception
     */
    public void testModifySyntaxDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSyntaxOid( "1.2.3.4.5" ); //$NON-NLS-1$
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSyntaxOid( "1.2.3.4.6" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SyntaxDifference ) || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( "1.2.3.4.5", ( ( SyntaxDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertEquals( "1.2.3.4.6", ( ( SyntaxDifference ) difference ).getNewValue() ); //$NON-NLS-1$
    }


    /**
     * Tests the ModifySyntaxLengthDifference.
     *
     * @throws Exception
     */
    public void testModifySyntaxLengthDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSyntaxLength( 1234 );
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSyntaxLength( 12345 );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SyntaxLengthDifference )
            || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( 1234L, ( ( SyntaxLengthDifference ) difference ).getOldValue() );
        assertEquals( 12345L, ( ( SyntaxLengthDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyUsageDifference.
     *
     * @throws Exception
     */
    public void testModifyUsageDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setUsage( UsageEnum.DISTRIBUTED_OPERATION );
        MutableAttributeType o2 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setUsage( UsageEnum.DIRECTORY_OPERATION );

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof UsageDifference ) || ( !difference.getType().equals( DifferenceType.MODIFIED ) ) )
        {
            fail();
        }

        assertEquals( UsageEnum.DISTRIBUTED_OPERATION, ( ( UsageDifference ) difference ).getOldValue() );
        assertEquals( UsageEnum.DIRECTORY_OPERATION, ( ( UsageDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveAliasDifference.
     *
     * @throws Exception
     */
    public void testRemoveAliasDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setNames( new String[]
            { "name1", "name2" } ); //$NON-NLS-1$ //$NON-NLS-2$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o2.setNames( new String[]
            { "name2" } ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AliasDifference ) || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "name1", ( ( AliasDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( AliasDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveDescriptionDifference.
     *
     * @throws Exception
     */
    public void testRemoveDescriptionDifference() throws Exception
    {
        AttributeType o1 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setDescription( "Description" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof DescriptionDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "Description", ( ( DescriptionDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( DescriptionDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveEqualityDifference.
     *
     * @throws Exception
     */
    public void testRemoveEqualityDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setEqualityOid( "equalityName" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof EqualityDifference ) || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "equalityName", ( ( EqualityDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( EqualityDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveMandatoryATDifference.
     *
     * @throws Exception
     */
    public void testRemoveMandatoryATDifference() throws Exception
    {
        MutableObjectClass o1 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o1.setMustAttributeTypeOids( Arrays.asList( new String[]
            { "must1", "must2" } ) ); //$NON-NLS-1$ //$NON-NLS-2$
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setMustAttributeTypeOids( Arrays.asList( new String[]
            { "must2" } ) ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof MandatoryATDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "must1", ( ( MandatoryATDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( MandatoryATDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveOptionalATDifference.
     *
     * @throws Exception
     */
    public void testRemoveOptionalATDifference() throws Exception
    {
        MutableObjectClass o1 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o1.setMayAttributeTypeOids( Arrays.asList( new String[]
            { "may1", "may2" } ) ); //$NON-NLS-1$ //$NON-NLS-2$
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setMayAttributeTypeOids( Arrays.asList( new String[]
            { "may2" } ) ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof OptionalATDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "may1", ( ( OptionalATDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( OptionalATDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveOrderingDifference.
     *
     * @throws Exception
     */
    public void testRemoveOrderingDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setOrderingOid( "orderingName" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof OrderingDifference ) || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "orderingName", ( ( OrderingDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( OrderingDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveSubstringDifference.
     *
     * @throws Exception
     */
    public void testRemoveSubstringDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSubstringOid( "substrName" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SubstringDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "substrName", ( ( SubstringDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( SubstringDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveSuperiorATDifference.
     *
     * @throws Exception
     */
    public void testRemoveSuperiorATDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSuperiorOid( "superiorName" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SuperiorATDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "superiorName", ( ( SuperiorATDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( SuperiorATDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveSuperiorOCDifference.
     *
     * @throws Exception
     */
    public void testRemoveSuperiorOCDifference() throws Exception
    {
        MutableObjectClass o1 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSuperiorOids( Arrays.asList( new String[]
            { "sup1", "sup2" } ) ); //$NON-NLS-1$ //$NON-NLS-2$
        MutableObjectClass o2 = new MutableObjectClass( "1.2.3.4" ); //$NON-NLS-1$
        o2.setSuperiorOids( Arrays.asList( new String[]
            { "sup2" } ) ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SuperiorOCDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "sup1", ( ( SuperiorOCDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( SuperiorOCDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveSyntaxDifference.
     *
     * @throws Exception
     */
    public void testRemoveSyntaxDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSyntaxOid( "1.2.3.4.5" ); //$NON-NLS-1$
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SyntaxDifference ) || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( "1.2.3.4.5", ( ( SyntaxDifference ) difference ).getOldValue() ); //$NON-NLS-1$
        assertNull( ( ( SyntaxDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveSyntaxLengthDifference.
     *
     * @throws Exception
     */
    public void testRemoveSyntaxLengthDifference() throws Exception
    {
        MutableAttributeType o1 = new MutableAttributeType( "1.2.3.4" ); //$NON-NLS-1$
        o1.setSyntaxLength( 1234 );
        AttributeType o2 = new AttributeType( "1.2.3.4" ); //$NON-NLS-1$

        List<PropertyDifference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof SyntaxLengthDifference )
            || ( !difference.getType().equals( DifferenceType.REMOVED ) ) )
        {
            fail();
        }

        assertEquals( 1234L, ( ( SyntaxLengthDifference ) difference ).getOldValue() );
        assertNull( ( ( SyntaxLengthDifference ) difference ).getNewValue() );
    }
}
