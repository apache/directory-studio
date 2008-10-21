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

package org.apache.directory.studio.ldapbrowser.common.filtereditor;


import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapAndFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterExtensibleComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapNotFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapOrFilterComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;


/**
 * The FilterFormattingStrategy is used to format the LDAP filter in the
 * filter editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterFormattingStrategy implements IFormattingStrategy
{

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The source viewer. */
    private ISourceViewer sourceViewer;


    /**
     * Creates a new instance of FilterFormattingStrategy.
     * 
     * @param sourceViewer the source viewer
     * @param parser the filter parser
     */
    public FilterFormattingStrategy( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
        this.parser = parser;
        this.sourceViewer = sourceViewer;
    }


    /**
     * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStarts(java.lang.String)
     */
    public void formatterStarts( String initialIndentation )
    {
    }


    /**
     * @see org.eclipse.jface.text.formatter.IFormattingStrategy#format(java.lang.String, boolean, java.lang.String, int[])
     */
    public String format( String content, boolean isLineStart, String indentation, int[] positions )
    {
        // this.parser.parse(content);
        LdapFilter model = parser.getModel();
        if ( model != null && model.isValid() )
        {
            sourceViewer.getDocument().set( getFormattedFilter( model, 0 ) );
        }

        return null;
    }


    /**
     * Gets the formatted string respresentation of the filter.
     * 
     * @param filter the filter
     * @param indent the indent
     * 
     * @return the formatted string respresentation of the filter
     */
    private String getFormattedFilter( LdapFilter filter, int indent )
    {
        StringBuffer sb = new StringBuffer();

        for ( int i = 0; i < indent; i++ )
        {
            sb.append( FilterAutoEditStrategy.INDENT_STRING );
        }

        LdapFilterComponent fc = filter.getFilterComponent();
        if ( fc instanceof LdapFilterItemComponent )
        {
            sb.append( '(' ).append( ( ( LdapFilterItemComponent ) fc ).toString() ).append( ')' );
        }
        else if ( fc instanceof LdapFilterExtensibleComponent )
        {
            sb.append( '(' ).append( ( ( LdapFilterExtensibleComponent ) fc ).toString() ).append( ')' );
        }
        else if ( fc instanceof LdapNotFilterComponent )
        {
            sb.append( "(!" );
            LdapNotFilterComponent lnfc = ( LdapNotFilterComponent ) fc;
            if ( lnfc.getFilters().length > 0
                && lnfc.getFilters()[0].getFilterComponent() instanceof LdapFilterItemComponent )
            {
                sb.append( getFormattedFilter( ( lnfc ).getFilters()[0], 0 ) );
            }
            else
            {
                sb.append( '\n' );
                sb.append( getFormattedFilter( ( lnfc ).getFilters()[0], indent + 1 ) );
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
                sb.append( getFormattedFilter( filters[i], indent + 1 ) );
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
                sb.append( getFormattedFilter( filters[i], indent + 1 ) );
                sb.append( '\n' );
            }
            for ( int i = 0; i < indent; i++ )
                sb.append( FilterAutoEditStrategy.INDENT_STRING );
            sb.append( ')' );
        }

        return sb.toString();
    }


    /**
     * @see org.eclipse.jface.text.formatter.IFormattingStrategy#formatterStops()
     */
    public void formatterStops()
    {
    }

}
