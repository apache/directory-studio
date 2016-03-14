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

package org.apache.directory.studio.ldapbrowser.common.widgets.entryeditor;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Attribute;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Value;
import org.junit.Before;
import org.junit.Test;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryEditorWidgetSorterTest
{
    private EntryEditorWidgetSorter sorter;
    private Value valueA1;
    private Value valueA2;
    private Value valueB;
    private Value empytValue1;
    private Value empytValue2;


    @Before
    public void setup()
    {
        sorter = new EntryEditorWidgetSorter( null );
        IAttribute attribute = new Attribute( new DummyEntry( null, null ), "x" );
        valueA1 = new Value( attribute, "a" );
        valueA2 = new Value( attribute, "a" );
        valueB = new Value( attribute, "b" );
        empytValue1 = new Value( attribute, "" );
        empytValue2 = new Value( attribute, "" );
    }


    @Test
    public void testEqual()
    {
        int result = sorter.compare( null, valueA1, valueA2 );
        assertThat( result, equalTo( 0 ) );
    }


    @Test
    public void testLeftIsSmaller()
    {
        int result = sorter.compare( null, valueA1, valueB );
        assertThat( result, equalTo( -1 ) );
    }


    @Test
    public void testRightIsSmaller()
    {
        int result = sorter.compare( null, valueB, valueA2 );
        assertThat( result, equalTo( 1 ) );
    }


    @Test
    public void testLeftIsEmpty()
    {
        int result = sorter.compare( null, empytValue1, valueA2 );
        assertThat( result, equalTo( -1 ) );
    }


    @Test
    public void testRightIsEmpty()
    {
        int result = sorter.compare( null, valueB, empytValue2 );
        assertThat( result, equalTo( 1 ) );
    }


    @Test
    public void testBothAreEmpty()
    {
        int result = sorter.compare( null, empytValue1, empytValue2 );
        assertThat( result, equalTo( 0 ) );
    }

}
