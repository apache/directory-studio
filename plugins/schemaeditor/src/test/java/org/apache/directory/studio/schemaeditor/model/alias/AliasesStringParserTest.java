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
package org.apache.directory.studio.schemaeditor.model.alias;


import java.util.List;

import junit.framework.TestCase;


/**
 * This class tests the {@link AliasesStringParser} class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AliasesStringParserTest extends TestCase
{
    public void testNoAlias() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 0, aliases.size() );
    }


    public void testOneAlias() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "test" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 1, aliases.size() );
        assertEquals( "test", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
    }


    public void testTwoAliases() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "test1, test2" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 2, aliases.size() );
        assertEquals( "test1", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( "test2", aliases.get( 1 ).getAlias() ); //$NON-NLS-1$
    }


    public void testThreeAliases() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "test1, test2, test3" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 3, aliases.size() );
        assertEquals( "test1", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( "test2", aliases.get( 1 ).getAlias() ); //$NON-NLS-1$
        assertEquals( "test3", aliases.get( 2 ).getAlias() ); //$NON-NLS-1$
    }


    public void testMultipleCommasNoAlias() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( ",,,,,,,,,,,," ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 0, aliases.size() );
    }


    public void testMultipleCommasAndWhiteSpacesNoAlias() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( ", ,   ,    ,  , ,,,," ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 0, aliases.size() );
    }


    public void testTwoAliasesMultipleCommas() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( ",,test1,,,,,,test2,,,," ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 2, aliases.size() );
        assertEquals( "test1", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( "test2", aliases.get( 1 ).getAlias() ); //$NON-NLS-1$
    }


    public void testOneAliasWithStartError() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "1test" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 1, aliases.size() );
        assertEquals( "1test", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithStartError.class, aliases.get( 0 ).getClass() );
    }


    public void testOneAliasWithStartError2() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "1" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 1, aliases.size() );
        assertEquals( "1", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithStartError.class, aliases.get( 0 ).getClass() );
    }


    public void testOneAliasWithPartError() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "tes/t" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 1, aliases.size() );
        assertEquals( "tes/t", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithPartError.class, aliases.get( 0 ).getClass() );
    }


    public void testOneAliasWithPartError2() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "tes/" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 1, aliases.size() );
        assertEquals( "tes/", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithPartError.class, aliases.get( 0 ).getClass() );
    }


    public void testOneAliasWithPartErrorAndOneAliasWithStartError() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "tes/t, 1test" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 2, aliases.size() );
        assertEquals( "tes/t", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithPartError.class, aliases.get( 0 ).getClass() );
        assertEquals( "1test", aliases.get( 1 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithStartError.class, aliases.get( 1 ).getClass() );
    }


    public void testOneAliasWithPartErrorAndOneAliasWithStartError2() throws Exception
    {
        AliasesStringParser parser = new AliasesStringParser();

        parser.parse( "tes/, 1" ); //$NON-NLS-1$
        List<Alias> aliases = parser.getAliases();
        assertEquals( 2, aliases.size() );
        assertEquals( "tes/", aliases.get( 0 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithPartError.class, aliases.get( 0 ).getClass() );
        assertEquals( "1", aliases.get( 1 ).getAlias() ); //$NON-NLS-1$
        assertEquals( AliasWithStartError.class, aliases.get( 1 ).getClass() );
    }
}
