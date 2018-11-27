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

package org.apache.directory.studio.ldifparser.parser;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldifparser.model.LdifPart;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.lines.LdifCommentLine;


public class DummyLdifContainer extends LdifContainer
{
    public DummyLdifContainer( LdifPart part )
    {
        super( part );
    }


    public LdifCommentLine[] getComments()
    {
        List<LdifPart> ldifPartList = new ArrayList<LdifPart>();
        
        for ( LdifPart ldifPart : ldifParts )
        {
            if ( ldifPart instanceof LdifCommentLine )
            {
                ldifPartList.add( ldifPart );
            }
        }
        
        return ( LdifCommentLine[] ) ldifPartList.toArray( new LdifCommentLine[ldifPartList.size()] );
    }


    public boolean isValid()
    {
        return false;
    }
}
