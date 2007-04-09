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

package org.apache.directory.ldapstudio.browser.common.filtereditor;


import org.apache.directory.ldapstudio.browser.core.model.filter.LdapAndFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapNotFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapOrFilterComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;

import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.source.SourceViewer;


public class FilterFormattingStrategy implements IFormattingStrategy
{

    private LdapFilterParser parser;

    private SourceViewer sourceViewer;


    public FilterFormattingStrategy( SourceViewer sourceViewer, LdapFilterParser parser )
    {
        super();
        this.parser = parser;
        this.sourceViewer = sourceViewer;
    }


    public void formatterStarts( String initialIndentation )
    {
    }


    public String format( String content, boolean isLineStart, String indentation, int[] positions )
    {
        // this.parser.parse(content);
        LdapFilter model = this.parser.getModel();
        if ( model != null && model.isValid() )
        {
            this.sourceViewer.getDocument().set( get( model, 0 ) );
        }

        return null;
    }


    private String get( LdapFilter filter, int indent )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < indent; i++ )
            sb.append( FilterAutoEditStrategy.INDENT_STRING );

        LdapFilterComponent fc = filter.getFilterComponent();
        if ( fc instanceof LdapFilterItemComponent )
        {
            sb.append( '(' ).append( ( ( LdapFilterItemComponent ) fc ).toString() ).append( ')' );
        }
        else if ( fc instanceof LdapNotFilterComponent )
        {
            sb.append( "(!" );
            LdapNotFilterComponent lnfc = ( LdapNotFilterComponent ) fc;
            if ( lnfc.getFilters().length > 0
                && lnfc.getFilters()[0].getFilterComponent() instanceof LdapFilterItemComponent )
            {
                sb.append( get( ( lnfc ).getFilters()[0], 0 ) );
            }
            else
            {
                sb.append( '\n' );
                sb.append( get( ( lnfc ).getFilters()[0], indent + 1 ) );
                sb.append( '\n' );
                for ( int i = 0; i < indent; i++ )
                    sb.append( FilterAutoEditStrategy.INDENT_STRING );
            }
            sb.append( ')' );
        }
        else if ( fc instanceof LdapAndFilterComponent )
        {
            sb.append( "(&" );
            sb.append( '\n' );
            LdapFilter[] filters = ( ( LdapAndFilterComponent ) fc ).getFilters();
            for ( int i = 0; i < filters.length; i++ )
            {
                sb.append( get( filters[i], indent + 1 ) );
                sb.append( '\n' );
            }
            for ( int i = 0; i < indent; i++ )
                sb.append( FilterAutoEditStrategy.INDENT_STRING );
            sb.append( ')' );
        }
        else if ( fc instanceof LdapOrFilterComponent )
        {
            sb.append( "(|" );
            sb.append( '\n' );
            LdapFilter[] filters = ( ( LdapOrFilterComponent ) fc ).getFilters();
            for ( int i = 0; i < filters.length; i++ )
            {
                sb.append( get( filters[i], indent + 1 ) );
                sb.append( '\n' );
            }
            for ( int i = 0; i < indent; i++ )
                sb.append( FilterAutoEditStrategy.INDENT_STRING );
            sb.append( ')' );
        }

        return sb.toString();
    }


    public void formatterStops()
    {
    }

}
