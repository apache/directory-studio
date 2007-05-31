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

package org.apache.directory.ldapstudio.browser.core.model.filter.parser;


import java.util.Stack;

import org.apache.directory.ldapstudio.browser.core.model.filter.LdapAndFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapNotFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapOrFilterComponent;


public class LdapFilterParser
{

    private LdapFilterScanner scanner;

    private Stack filterStack;

    private LdapFilter model;


    public LdapFilterParser()
    {
        this.scanner = new LdapFilterScanner();
        this.model = new LdapFilter();
    }


    public LdapFilter getModel()
    {
        return this.model;
    }


    public void parse( String ldapFilter )
    {

        // reset state
        this.filterStack = new Stack();
        this.scanner.reset( ldapFilter );
        this.model = new LdapFilter();

        // handle error tokens before filter
        LdapFilterToken token = this.scanner.nextToken();
        while ( token.getType() != LdapFilterToken.LPAR && token.getType() != LdapFilterToken.EOF )
        {
            this.handleError( false, token, this.model );
            token = scanner.nextToken();
        }

        // check filter start
        if ( token.getType() == LdapFilterToken.LPAR )
        {

            // start top level filter
            this.model.setStartToken( token );
            filterStack.push( this.model );

            // loop till filter end or EOF
            do
            {

                // next token
                token = this.scanner.nextToken();

                switch ( token.getType() )
                {
                    case LdapFilterToken.LPAR:
                    {
                        LdapFilter newFilter = new LdapFilter();
                        newFilter.setStartToken( token );

                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        if ( filterComponent != null && filterComponent.addFilter( newFilter ) )
                        {
                            filterStack.push( newFilter );
                        }
                        else
                        {
                            currentFilter.addOtherToken( token );
                        }

                        break;
                    }
                    case LdapFilterToken.RPAR:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.pop();
                        this.handleError( currentFilter.setStopToken( token ), token, currentFilter );
                        /*
                         * if(!filterStack.isEmpty()) { LdapFilter parentFilter =
                         * (LdapFilter) filterStack.peek(); LdapFilterComponent
                         * filterComponent = parentFilter.getFilterComponent();
                         * filterComponent.addFilter(currentFilter); }
                         */
                        break;
                    }
                    case LdapFilterToken.AND:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapAndFilterComponent filterComponent = new LdapAndFilterComponent( currentFilter );
                        filterComponent.setStartToken( token );
                        this.handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.OR:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapOrFilterComponent filterComponent = new LdapOrFilterComponent( currentFilter );
                        filterComponent.setStartToken( token );
                        this.handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.NOT:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapNotFilterComponent filterComponent = new LdapNotFilterComponent( currentFilter );
                        filterComponent.setStartToken( token );
                        this.handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.ATTRIBUTE:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterItemComponent filterComponent = new LdapFilterItemComponent( currentFilter );
                        filterComponent.setAttributeToken( token );
                        this.handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.VALUE:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        this.handleError( ( filterComponent instanceof LdapFilterItemComponent )
                            && ( ( LdapFilterItemComponent ) filterComponent ).setValueToken( token ), token,
                            currentFilter );
                        break;
                    }
                    case LdapFilterToken.EQUAL:
                    case LdapFilterToken.GREATER:
                    case LdapFilterToken.LESS:
                    case LdapFilterToken.APROX:
                    case LdapFilterToken.PRESENT:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        this.handleError( ( filterComponent instanceof LdapFilterItemComponent )
                            && ( ( LdapFilterItemComponent ) filterComponent ).setFiltertypeToken( token ), token,
                            currentFilter );
                        break;
                    }
                    case LdapFilterToken.WHITESPACE:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        currentFilter.addOtherToken( token );
                        break;
                    }
                    case LdapFilterToken.EOF:
                    {
                        this.model.addOtherToken( token );
                        break;
                    }
                    default:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        this.handleError( false, token, currentFilter );
                    }
                }
            }
            while ( !filterStack.isEmpty() && token.getType() != LdapFilterToken.EOF );

        }

        // handle error token after filter
        token = scanner.nextToken();
        while ( token.getType() != LdapFilterToken.EOF )
        {
            this.handleError( false, token, this.model );
            token = scanner.nextToken();
        }

    }


    private void handleError( boolean success, LdapFilterToken token, LdapFilter filter )
    {
        if ( !success )
        {
            filter.addOtherToken( new LdapFilterToken( LdapFilterToken.ERROR, token.getValue(), token.getOffset() ) );
        }
    }

}
