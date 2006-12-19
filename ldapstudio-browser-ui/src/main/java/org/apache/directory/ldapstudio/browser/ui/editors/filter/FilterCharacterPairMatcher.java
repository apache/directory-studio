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


// TODO: Refactor Filter Editor
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;


public class FilterCharacterPairMatcher implements ICharacterPairMatcher
{

    private ISourceViewer sourceViewer;

    private LdapFilterParser parser;

    private int anchor;


    public FilterCharacterPairMatcher( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
        super();
        this.sourceViewer = sourceViewer;
        this.parser = parser;
        this.clear();
    }


    public void dispose()
    {
    }


    public void clear()
    {
        this.anchor = LEFT;
    }


    public IRegion match( IDocument document, int offset )
    {

        LdapFilter model = this.parser.getModel();
        if ( model != null )
        {
            LdapFilter filter = this.parser.getModel().getFilter( offset - 1 );

            if ( filter != null && filter.getStartToken() != null && filter.getStopToken() != null )
            {

                int left = filter.getStartToken().getOffset();
                int right = filter.getStopToken().getOffset();

                if ( left == offset - 1 )
                {
                    this.anchor = LEFT;
                    IRegion region = new Region( left, right - left + 1 );
                    return region;
                }
                if ( right == offset - 1 )
                {
                    this.anchor = RIGHT;
                    IRegion region = new Region( left, right - left + 1 );
                    return region;
                }
            }
        }

        return null;
    }


    public int getAnchor()
    {
        return this.anchor;
    }

}
