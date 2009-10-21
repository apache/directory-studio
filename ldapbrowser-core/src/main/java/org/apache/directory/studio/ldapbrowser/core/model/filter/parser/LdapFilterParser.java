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

package org.apache.directory.studio.ldapbrowser.core.model.filter.parser;


import java.util.Stack;

import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapAndFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterExtensibleComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapNotFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapOrFilterComponent;


/**
 * The LdapFilterParser implements a parser for LDAP filters.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdapFilterParser
{

    /** The scanner. */
    private LdapFilterScanner scanner;

    /** The filter stack. */
    private Stack<LdapFilter> filterStack;

    /** The parsed LDAP filter model. */
    private LdapFilter model;


    /**
     * Creates a new instance of LdapFilterParser.
     */
    public LdapFilterParser()
    {
        this.scanner = new LdapFilterScanner();
        this.model = new LdapFilter();
    }


    /**
     * Gets the parsed LDAP filter model.
     * 
     * @return the parsed model
     */
    public LdapFilter getModel()
    {
        return model;
    }


    /**
     * Parses the given LDAP filter.
     * 
     * @param ldapFilter the LDAP filter
     */
    public void parse( String ldapFilter )
    {
        // reset state
        filterStack = new Stack<LdapFilter>();
        scanner.reset( ldapFilter );
        model = new LdapFilter();

        // handle error tokens before filter
        LdapFilterToken token = scanner.nextToken();
        while ( token.getType() != LdapFilterToken.LPAR && token.getType() != LdapFilterToken.EOF )
        {
            handleError( false, token, model );
            token = scanner.nextToken();
        }

        // check filter start
        if ( token.getType() == LdapFilterToken.LPAR )
        {
            // start top level filter
            model.setStartToken( token );
            filterStack.push( model );

            // loop till filter end or EOF
            do
            {
                // next token
                token = scanner.nextToken();

                switch ( token.getType() )
                {
                    case LdapFilterToken.LPAR:
                    {
                        LdapFilter newFilter = new LdapFilter();
                        newFilter.setStartToken( token );

                        LdapFilter currentFilter = filterStack.peek();
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
                        LdapFilter currentFilter = filterStack.pop();
                        handleError( currentFilter.setStopToken( token ), token, currentFilter );
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
                        LdapFilter currentFilter = filterStack.peek();
                        LdapAndFilterComponent filterComponent = new LdapAndFilterComponent( currentFilter );
                        filterComponent.setStartToken( token );
                        handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.OR:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        LdapOrFilterComponent filterComponent = new LdapOrFilterComponent( currentFilter );
                        filterComponent.setStartToken( token );
                        handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.NOT:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        LdapNotFilterComponent filterComponent = new LdapNotFilterComponent( currentFilter );
                        filterComponent.setStartToken( token );
                        handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.ATTRIBUTE:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        LdapFilterItemComponent filterComponent = new LdapFilterItemComponent( currentFilter );
                        filterComponent.setAttributeToken( token );
                        handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.VALUE:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        if ( filterComponent instanceof LdapFilterItemComponent )
                        {
                            handleError( ( filterComponent instanceof LdapFilterItemComponent )
                                && ( ( LdapFilterItemComponent ) filterComponent ).setValueToken( token ), token,
                                currentFilter );
                        }
                        else if ( filterComponent instanceof LdapFilterExtensibleComponent )
                        {
                            handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                                && ( ( LdapFilterExtensibleComponent ) filterComponent ).setValueToken( token ), token,
                                currentFilter );
                        }
                        else
                        {
                            handleError( false, token, currentFilter );
                        }
                        break;
                    }
                    case LdapFilterToken.EQUAL:
                    case LdapFilterToken.GREATER:
                    case LdapFilterToken.LESS:
                    case LdapFilterToken.APROX:
                    case LdapFilterToken.PRESENT:
                    case LdapFilterToken.SUBSTRING:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        if ( filterComponent instanceof LdapFilterItemComponent )
                        {
                            handleError( ( filterComponent instanceof LdapFilterItemComponent )
                                && ( ( LdapFilterItemComponent ) filterComponent ).setFiltertypeToken( token ), token,
                                currentFilter );
                        }
                        else if ( filterComponent instanceof LdapFilterExtensibleComponent )
                        {
                            handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                                && ( ( LdapFilterExtensibleComponent ) filterComponent ).setEqualsToken( token ),
                                token, currentFilter );
                        }
                        else
                        {
                            handleError( false, token, currentFilter );
                        }
                        break;
                    }
                    case LdapFilterToken.WHITESPACE:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        currentFilter.addOtherToken( token );
                        break;
                    }
                    case LdapFilterToken.EXTENSIBLE_ATTRIBUTE:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterExtensibleComponent filterComponent = new LdapFilterExtensibleComponent(
                            currentFilter );
                        filterComponent.setAttributeToken( token );
                        handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.EXTENSIBLE_DNATTR_COLON:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        if ( filterComponent == null )
                        {
                            filterComponent = new LdapFilterExtensibleComponent( currentFilter );
                            ( ( LdapFilterExtensibleComponent ) filterComponent ).setDnAttrColonToken( token );
                            handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        }
                        else
                        {
                            handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                                && ( ( LdapFilterExtensibleComponent ) filterComponent ).setDnAttrColonToken( token ),
                                token, currentFilter );
                        }
                        break;
                    }
                    case LdapFilterToken.EXTENSIBLE_DNATTR:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                            && ( ( LdapFilterExtensibleComponent ) filterComponent ).setDnAttrToken( token ), token,
                            currentFilter );
                        break;
                    }
                    case LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID_COLON:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        if ( filterComponent == null )
                        {
                            filterComponent = new LdapFilterExtensibleComponent( currentFilter );
                            ( ( LdapFilterExtensibleComponent ) filterComponent ).setMatchingRuleColonToken( token );
                            handleError( currentFilter.setFilterComponent( filterComponent ), token, currentFilter );
                        }
                        else
                        {
                            handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                                && ( ( LdapFilterExtensibleComponent ) filterComponent )
                                    .setMatchingRuleColonToken( token ), token, currentFilter );
                        }
                        break;
                    }
                    case LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                            && ( ( LdapFilterExtensibleComponent ) filterComponent ).setMatchingRuleToken( token ),
                            token, currentFilter );
                        break;
                    }
                    case LdapFilterToken.EXTENSIBLE_EQUALS_COLON:
                    {
                        LdapFilter currentFilter = ( LdapFilter ) filterStack.peek();
                        LdapFilterComponent filterComponent = currentFilter.getFilterComponent();
                        handleError( ( filterComponent instanceof LdapFilterExtensibleComponent )
                            && ( ( LdapFilterExtensibleComponent ) filterComponent ).setEqualsColonToken( token ),
                            token, currentFilter );
                        break;
                    }

                    case LdapFilterToken.EOF:
                    {
                        model.addOtherToken( token );
                        break;
                    }
                    default:
                    {
                        LdapFilter currentFilter = filterStack.peek();
                        handleError( false, token, currentFilter );
                    }
                }
            }
            while ( !filterStack.isEmpty() && token.getType() != LdapFilterToken.EOF );
        }

        // handle error token after filter
        token = scanner.nextToken();
        while ( token.getType() != LdapFilterToken.EOF )
        {
            handleError( false, token, model );
            token = scanner.nextToken();
        }
    }


    /**
     * Helper method to handle parse errors.
     * 
     * @param success the success flag
     * @param filter the filter
     * @param token the token
     */
    private void handleError( boolean success, LdapFilterToken token, LdapFilter filter )
    {
        if ( !success )
        {
            filter.addOtherToken( new LdapFilterToken( LdapFilterToken.ERROR, token.getValue(), token.getOffset() ) );
        }
    }

}
