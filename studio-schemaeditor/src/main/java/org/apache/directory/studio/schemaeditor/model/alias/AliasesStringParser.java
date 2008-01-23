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


import java.util.ArrayList;
import java.util.List;


/**
 * The AliasesParser implements a parser for Aliases {@link String}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AliasesStringParser
{
    /** The scanner */
    private AliasesStringScanner scanner;

    /** The parsed aliases */
    private List<Alias> aliases;


    /**
     * Creates a new instance of LdapFilterParser.
     */
    public AliasesStringParser()
    {
        this.scanner = new AliasesStringScanner();
        this.aliases = new ArrayList<Alias>();
    }


    /**
     * Gets the parsed aliases.
     * 
     * @return the parsed aliases
     */
    public List<Alias> getAliases()
    {
        return aliases;
    }


    /**
     * Parses the given aliases String.
     * 
     * @param str the aliases String
     */
    public void parse( String str )
    {
        // reset state
        scanner.reset( str );

        // handle error tokens before filter
        AliasesStringToken token = scanner.nextToken();

        // loop till aliases end or EOF
        do
        {
            switch ( token.getType() )
            {
                case AliasesStringToken.ALIAS:
                {
                    aliases.add( new DefaultAlias( token.getValue() ) );
                    break;
                }
                case AliasesStringToken.ERROR_ALIAS_START:
                {
                    String previousTokenValue = token.getValue();

                    token = scanner.nextToken();
                    if ( token.getType() == AliasesStringToken.ERROR_ALIAS_SUBSTRING )
                    {
                        aliases.add( new AliasWithStartError( previousTokenValue + token.getValue(), previousTokenValue
                            .charAt( 0 ) ) );
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
                case AliasesStringToken.ERROR_ALIAS_PART:
                {
                    String previousTokenValue = token.getValue();

                    token = scanner.nextToken();
                    if ( token.getType() == AliasesStringToken.ERROR_ALIAS_SUBSTRING )
                    {
                        aliases.add( new AliasWithPartError( previousTokenValue + token.getValue(), previousTokenValue
                            .charAt( previousTokenValue.length() - 1 ) ) );
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
                default:
                {
                    break;
                }
            }

            // next token
            token = scanner.nextToken();
        }
        while ( token.getType() != AliasesStringToken.EOF );
    }
}
