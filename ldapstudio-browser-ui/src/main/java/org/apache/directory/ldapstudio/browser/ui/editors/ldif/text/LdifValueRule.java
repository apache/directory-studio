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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif.text;


import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


public class LdifValueRule implements IRule
{

    private IToken token;


    public LdifValueRule( IToken token )
    {
        this.token = token;
    }


    public IToken evaluate( ICharacterScanner scanner )
    {

        if ( matchContent( scanner ) )
        {
            return this.token;
        }
        else
        {
            return Token.UNDEFINED;
        }

    }


    protected boolean matchContent( ICharacterScanner scanner )
    {

        int count = 0;

        int c = scanner.read();
        while ( c != ICharacterScanner.EOF )
        {

            // check for folding
            if ( c == '\n' || c == '\r' )
            {
                StringBuffer temp = new StringBuffer( 3 );
                if ( c == '\r' )
                {
                    c = scanner.read();
                    if ( c == '\n' )
                    {
                        temp.append( c );
                    }
                    else
                    {
                        scanner.unread();
                    }
                }
                else if ( c == '\n' )
                {
                    c = scanner.read();
                    if ( c == '\r' )
                    {
                        temp.append( c );
                    }
                    else
                    {
                        scanner.unread();
                    }
                }

                c = scanner.read();
                if ( c == ' ' && c != ICharacterScanner.EOF )
                {
                    // space after newline, continue
                    temp.append( c );
                    count += temp.length();
                    c = scanner.read();
                }
                else
                {
                    for ( int i = 0; i < temp.length(); i++ )
                        scanner.unread();
                    break;
                }
            }
            else
            {
                count++;
                c = scanner.read();
            }
        }
        scanner.unread();

        return count > 0;
    }

}
