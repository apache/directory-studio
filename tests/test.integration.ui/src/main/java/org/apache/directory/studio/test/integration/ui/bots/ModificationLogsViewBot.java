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
package org.apache.directory.studio.test.integration.ui.bots;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifParserConstants;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifCommentContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.parser.LdifParser;


public class ModificationLogsViewBot extends AbstractLogsViewBot
{
    public ModificationLogsViewBot()
    {
        super( "Modification Logs" );
    }


    public String getModificationLogsText()
    {
        return super.getLogsText();
    }


    public void assertContainsOk( String... lines )
    {
        List<String> parts = new ArrayList<>();
        parts.add( "#!RESULT OK" );
        parts.addAll( Arrays.asList( lines ) );
        assertContains( parts );
    }


    public void assertContainsError( String... lines )
    {
        List<String> parts = new ArrayList<>();
        parts.add( "#!RESULT ERROR" );
        parts.addAll( Arrays.asList( lines ) );
        assertContains( parts );
    }


    private void assertContains( List<String> parts )
    {
        String text = getModificationLogsText();
        LdifFile ldif = new LdifParser().parse( text );
        Iterator<LdifContainer> ldifContainers = ldif.getContainers().iterator();
        while ( ldifContainers.hasNext() )
        {
            LdifContainer container = ldifContainers.next();
            LdifFormatParameters ldifFormatParameters = new LdifFormatParameters( true, 1024,
                LdifParserConstants.LINE_SEPARATOR );
            String ldifRecordText = container.toFormattedString( ldifFormatParameters );
            if ( container instanceof LdifCommentContainer )
            {
                LdifContainer record = ldifContainers.next();
                ldifRecordText += record.toFormattedString( ldifFormatParameters );
            }

            if ( containsAll( ldifRecordText, parts ) )
            {
                return;
            }
        }
        throw new AssertionError( "Expeted to find all parts " + parts + " in\n" + text );
    }


    private boolean containsAll( String ldifRecordText, List<String> parts )
    {
        for ( String part : parts )
        {
            if ( !ldifRecordText.contains( part ) )
            {
                return false;
            }
        }
        return true;
    }

}
