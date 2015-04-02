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

package org.apache.directory.studio.openldap.config.acl.sourceeditor;


import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


/**
 * Rule to detect a "dn[.type[,modifier]]=" clause.
 */
public class StarRule extends AbstractRule
{


    /**
     * Creates a new instance of StarRule.
     *
     * @param token the associated token
     */
    public StarRule( IToken token )
    {
        super( token );
    }


    /**
     * {@inheritDoc}
     */
    public IToken evaluate( ICharacterScanner scanner, boolean resume )
    {
        // Looking for '*'
        if ( matchChar( scanner, '*' ) )
        {
            // Token evaluation complete
            return token;
        }
        else
        {
            // Not what was expected
            return Token.UNDEFINED;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IToken evaluate( ICharacterScanner scanner )
    {
        return this.evaluate( scanner, false );
    }
}
