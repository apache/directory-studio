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

package org.apache.directory.ldapstudio.browser.ui.editors.filter;


import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterToken;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;


public class FilterTextHover implements ITextHover
{

    private ISourceViewer sourceViewer;

    private LdapFilterParser parser;


    public FilterTextHover( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
        super();
        this.sourceViewer = sourceViewer;
        this.parser = parser;
    }


    public String getHoverInfo( ITextViewer textViewer, IRegion hoverRegion )
    {
        LdapFilter[] invalidFilters = this.parser.getModel().getInvalidFilters();
        for ( int i = 0; i < invalidFilters.length; i++ )
        {
            if ( invalidFilters[i].getStartToken() != null )
            {
                int start = invalidFilters[i].getStartToken().getOffset();
                int stop = invalidFilters[i].getStopToken() != null ? invalidFilters[i].getStopToken().getOffset()
                    + invalidFilters[i].getStopToken().getLength() : start
                    + invalidFilters[i].getStartToken().getLength();
                if ( start <= hoverRegion.getOffset() && hoverRegion.getOffset() < stop )
                {
                    return invalidFilters[i].getInvalidCause();
                }
            }
        }

        LdapFilterToken[] tokens = this.parser.getModel().getTokens();
        for ( int i = 0; i < tokens.length; i++ )
        {
            if ( tokens[i].getType() == LdapFilterToken.ERROR )
            {

                int start = tokens[i].getOffset();
                int stop = start + tokens[i].getLength();
                if ( start <= hoverRegion.getOffset() && hoverRegion.getOffset() < stop )
                {
                    return "Invalid characters";
                }
            }
        }
        return null;
    }


    public IRegion getHoverRegion( ITextViewer textViewer, int offset )
    {
        return new Region( offset, 1 );
    }

}
