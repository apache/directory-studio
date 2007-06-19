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


import java.util.List;

import junit.framework.TestCase;

import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.apacheds.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.apacheds.schemaeditor.model.ObjectClassImpl;


/**
 * This class tests the DifferenceEngine class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
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
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setNames( new String[]
            { "alias" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddAliasDifference ) )
        {
            fail();
        }

        assertEquals( "alias", ( ( AddAliasDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddDescriptionDifference.
     *
     * @throws Exception
     */
    public void testAddDescriptionDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setDescription( "Description" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddDescriptionDifference ) )
        {
            fail();
        }

        assertEquals( "Description", ( ( AddDescriptionDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddEqualityDifference.
     *
     * @throws Exception
     */
    public void testAddEqualityDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setEqualityName( "Equality" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddEqualityDifference ) )
        {
            fail();
        }

        assertEquals( "Equality", ( ( AddEqualityDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddMandatoryATDifference.
     *
     * @throws Exception
     */
    public void testAddMandatoryATDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setMustNamesList( new String[]
            { "must" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddMandatoryATDifference ) )
        {
            fail();
        }

        assertEquals( "must", ( ( AddMandatoryATDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddOptionalATDifference.
     *
     * @throws Exception
     */
    public void testAddOptionalATDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setMayNamesList( new String[]
            { "may" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddOptionalATDifference ) )
        {
            fail();
        }

        assertEquals( "may", ( ( AddOptionalATDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddOrderingDifference.
     *
     * @throws Exception
     */
    public void testAddOrderingDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setOrderingName( "Ordering" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddOrderingDifference ) )
        {
            fail();
        }

        assertEquals( "Ordering", ( ( AddOrderingDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddSubstringDifference.
     *
     * @throws Exception
     */
    public void testAddSubstringDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSubstrName( "Substring" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddSubstringDifference ) )
        {
            fail();
        }

        assertEquals( "Substring", ( ( AddSubstringDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddSuperiorATDifference.
     *
     * @throws Exception
     */
    public void testAddSuperiorATDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSuperiorName( "superiorAT" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddSuperiorATDifference ) )
        {
            fail();
        }

        assertEquals( "superiorAT", ( ( AddSuperiorATDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddSuperiorOCDifference.
     *
     * @throws Exception
     */
    public void testAddSuperiorOCDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setSuperClassesNames( new String[]
            { "superiorOC" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddSuperiorOCDifference ) )
        {
            fail();
        }

        assertEquals( "superiorOC", ( ( AddSuperiorOCDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddSyntaxDifference.
     *
     * @throws Exception
     */
    public void testAddSyntaxDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSyntaxOid( "1.2.3.4.5" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddSyntaxDifference ) )
        {
            fail();
        }

        assertEquals( "1.2.3.4.5", ( ( AddSyntaxDifference ) difference ).getValue() );
    }


    /**
     * Tests the AddSyntaxLengthDifference.
     *
     * @throws Exception
     */
    public void testAddSyntaxLengthDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setLength( 1234 );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof AddSyntaxLengthDifference ) )
        {
            fail();
        }

        assertEquals( 1234, ( ( AddSyntaxLengthDifference ) difference ).getValue() );
    }


    /**
     * Tests the ModifyClassTypeDifference.
     *
     * @throws Exception
     */
    public void testModifyClassTypeDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        o1.setType( ObjectClassTypeEnum.STRUCTURAL );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setType( ObjectClassTypeEnum.ABSTRACT );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyClassTypeDifference ) )
        {
            fail();
        }

        assertEquals( ObjectClassTypeEnum.STRUCTURAL, ( ( ModifyClassTypeDifference ) difference ).getOldValue() );
        assertEquals( ObjectClassTypeEnum.ABSTRACT, ( ( ModifyClassTypeDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyCollectiveDifference.
     *
     * @throws Exception
     */
    public void testModifyCollectiveDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setCollective( true );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setCollective( false );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyCollectiveDifference ) )
        {
            fail();
        }

        assertEquals( true, ( ( ModifyCollectiveDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( ModifyCollectiveDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyDescriptionDifference.
     *
     * @throws Exception
     */
    public void testModifyDescriptionDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setDescription( "Description" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setDescription( "New Description" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyDescriptionDifference ) )
        {
            fail();
        }

        assertEquals( "Description", ( ( ModifyDescriptionDifference ) difference ).getOldValue() );
        assertEquals( "New Description", ( ( ModifyDescriptionDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyEqualityDifference.
     *
     * @throws Exception
     */
    public void testModifyEqualityDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setEqualityName( "equalityName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setEqualityName( "newEqualityName" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyEqualityDifference ) )
        {
            fail();
        }

        assertEquals( "equalityName", ( ( ModifyEqualityDifference ) difference ).getOldValue() );
        assertEquals( "newEqualityName", ( ( ModifyEqualityDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyNoUserModificationDifference.
     *
     * @throws Exception
     */
    public void testModifyNoUserModificationDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setCanUserModify( true );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setCanUserModify( false );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyNoUserModificationDifference ) )
        {
            fail();
        }

        assertEquals( true, ( ( ModifyNoUserModificationDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( ModifyNoUserModificationDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyObsoleteDifference.
     *
     * @throws Exception
     */
    public void testModifyObsoleteDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setObsolete( true );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setObsolete( false );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyObsoleteDifference ) )
        {
            fail();
        }

        assertEquals( true, ( ( ModifyObsoleteDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( ModifyObsoleteDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyOrderingDifference.
     *
     * @throws Exception
     */
    public void testModifyOrderingDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setOrderingName( "orderingName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setOrderingName( "newOrderingName" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyOrderingDifference ) )
        {
            fail();
        }

        assertEquals( "orderingName", ( ( ModifyOrderingDifference ) difference ).getOldValue() );
        assertEquals( "newOrderingName", ( ( ModifyOrderingDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifySingleValueDifference.
     *
     * @throws Exception
     */
    public void testModifySingleValueDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSingleValue( true );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSingleValue( false );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifySingleValueDifference ) )
        {
            fail();
        }

        assertEquals( true, ( ( ModifySingleValueDifference ) difference ).getOldValue() );
        assertEquals( false, ( ( ModifySingleValueDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifySubstringDifference.
     *
     * @throws Exception
     */
    public void testModifySubstringDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSubstrName( "substrName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSubstrName( "newSubstrName" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifySubstringDifference ) )
        {
            fail();
        }

        assertEquals( "substrName", ( ( ModifySubstringDifference ) difference ).getOldValue() );
        assertEquals( "newSubstrName", ( ( ModifySubstringDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifySuperiorATDifference.
     *
     * @throws Exception
     */
    public void testModifySuperiorATDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSuperiorName( "superiorName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSuperiorName( "newSuperiorName" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifySuperiorATDifference ) )
        {
            fail();
        }

        assertEquals( "superiorName", ( ( ModifySuperiorATDifference ) difference ).getOldValue() );
        assertEquals( "newSuperiorName", ( ( ModifySuperiorATDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifySyntaxDifference.
     *
     * @throws Exception
     */
    public void testModifySyntaxDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSyntaxOid( "1.2.3.4.5" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setSyntaxOid( "1.2.3.4.6" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifySyntaxDifference ) )
        {
            fail();
        }

        assertEquals( "1.2.3.4.5", ( ( ModifySyntaxDifference ) difference ).getOldValue() );
        assertEquals( "1.2.3.4.6", ( ( ModifySyntaxDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifySyntaxLengthDifference.
     *
     * @throws Exception
     */
    public void testModifySyntaxLengthDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setLength( 1234 );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setLength( 12345 );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifySyntaxLengthDifference ) )
        {
            fail();
        }

        assertEquals( 1234, ( ( ModifySyntaxLengthDifference ) difference ).getOldValue() );
        assertEquals( 12345, ( ( ModifySyntaxLengthDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the ModifyUsageDifference.
     *
     * @throws Exception
     */
    public void testModifyUsageDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setUsage( UsageEnum.DISTRIBUTED_OPERATION );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setUsage( UsageEnum.DIRECTORY_OPERATION );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof ModifyUsageDifference ) )
        {
            fail();
        }

        assertEquals( UsageEnum.DISTRIBUTED_OPERATION, ( ( ModifyUsageDifference ) difference ).getOldValue() );
        assertEquals( UsageEnum.DIRECTORY_OPERATION, ( ( ModifyUsageDifference ) difference ).getNewValue() );
    }


    /**
     * Tests the RemoveAliasDifference.
     *
     * @throws Exception
     */
    public void testRemoveAliasDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setNames( new String[]
            { "name1", "name2" } );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );
        o2.setNames( new String[]
            { "name2" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveAliasDifference ) )
        {
            fail();
        }

        assertEquals( "name1", ( ( RemoveAliasDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveDescriptionDifference.
     *
     * @throws Exception
     */
    public void testRemoveDescriptionDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setDescription( "Description" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveDescriptionDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveDescriptionDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveEqualityDifference.
     *
     * @throws Exception
     */
    public void testRemoveEqualityDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setEqualityName( "equalityName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveEqualityDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveEqualityDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveMandatoryATDifference.
     *
     * @throws Exception
     */
    public void testRemoveMandatoryATDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        o1.setMustNamesList( new String[]
            { "must1", "must2" } );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setMustNamesList( new String[]
            { "must2" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveMandatoryATDifference ) )
        {
            fail();
        }

        assertEquals( "must1", ( ( RemoveMandatoryATDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveOptionalATDifference.
     *
     * @throws Exception
     */
    public void testRemoveOptionalATDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        o1.setMayNamesList( new String[]
            { "may1", "may2" } );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setMayNamesList( new String[]
            { "may2" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveOptionalATDifference ) )
        {
            fail();
        }

        assertEquals( "may1", ( ( RemoveOptionalATDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveOrderingDifference.
     *
     * @throws Exception
     */
    public void testRemoveOrderingDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setOrderingName( "orderingName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveOrderingDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveOrderingDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveSubstringDifference.
     *
     * @throws Exception
     */
    public void testRemoveSubstringDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSubstrName( "substrName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveSubstringDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveSubstringDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveSuperiorATDifference.
     *
     * @throws Exception
     */
    public void testRemoveSuperiorATDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSuperiorName( "superiorName" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveSuperiorATDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveSuperiorATDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveSuperiorOCDifference.
     *
     * @throws Exception
     */
    public void testRemoveSuperiorOCDifference() throws Exception
    {
        ObjectClassImpl o1 = new ObjectClassImpl( "1.2.3.4" );
        o1.setSuperClassesNames( new String [] { "sup1", "sup2" } );
        ObjectClassImpl o2 = new ObjectClassImpl( "1.2.3.4" );
        o2.setSuperClassesNames( new String [] { "sup2" } );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveSuperiorOCDifference ) )
        {
            fail();
        }

        assertEquals( "sup1", ( ( RemoveSuperiorOCDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveSyntaxDifference.
     *
     * @throws Exception
     */
    public void testRemoveSyntaxDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setSyntaxOid( "1.2.3.4.5" );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveSyntaxDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveSyntaxDifference ) difference ).getValue() );
    }


    /**
     * Tests the RemoveSyntaxLengthDifference.
     *
     * @throws Exception
     */
    public void testRemoveSyntaxLengthDifference() throws Exception
    {
        AttributeTypeImpl o1 = new AttributeTypeImpl( "1.2.3.4" );
        o1.setLength( 1234 );
        AttributeTypeImpl o2 = new AttributeTypeImpl( "1.2.3.4" );

        List<Difference> differences = DifferenceEngine.getDifferences( o1, o2 );

        assertEquals( 1, differences.size() );

        Difference difference = differences.get( 0 );

        if ( !( difference instanceof RemoveSyntaxLengthDifference ) )
        {
            fail();
        }

        assertNull( ( ( RemoveSyntaxLengthDifference ) difference ).getValue() );
    }
}
