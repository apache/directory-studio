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

package org.apache.directory.ldapstudio.browser.core.model.ldif.container;


import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifCommentLine;


public class LdifCommentContainer extends LdifContainer
{

    private static final long serialVersionUID = 5193234573866495240L;


    protected LdifCommentContainer()
    {
    }


    public LdifCommentContainer( LdifCommentLine comment )
    {
        super( comment );
    }


    public void addComment( LdifCommentLine comment )
    {
        if ( comment == null )
            throw new IllegalArgumentException( "null argument" );
        this.parts.add( comment );
    }


    public boolean isValid()
    {
        if ( !super.isAbstractValid() )
        {
            return false;
        }

        // return getLastPart() instanceof LdifCommentLine &&
        // getLastPart().isValid();
        return true;
    }

}
