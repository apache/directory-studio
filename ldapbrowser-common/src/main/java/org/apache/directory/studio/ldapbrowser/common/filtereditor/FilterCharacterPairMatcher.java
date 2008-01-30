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


import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;


/**
 * The FilterCharacterPairMatcher implements the ICharacterPairMatcher interface
 * to match the peer opening and closing parentesis.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterCharacterPairMatcher implements ICharacterPairMatcher
{

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The anchor. */
    private int anchor;


    /**
     * Creates a new instance of FilterCharacterPairMatcher.
     * 
     * @param sourceViewer the source viewer
     * @param parser the filter parser
     */
    public FilterCharacterPairMatcher( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
        this.parser = parser;
        this.clear();
    }


    /**
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#dispose()
     */
    public void dispose()
    {
    }


    /**
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#clear()
     */
    public void clear()
    {
        anchor = LEFT;
    }


    /**
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#match(org.eclipse.jface.text.IDocument, int)
     */
    public IRegion match( IDocument document, int offset )
    {
        LdapFilter model = parser.getModel();
        if ( model != null )
        {
            LdapFilter filter = parser.getModel().getFilter( offset - 1 );

            if ( filter != null && filter.getStartToken() != null && filter.getStopToken() != null )
            {

                int left = filter.getStartToken().getOffset();
                int right = filter.getStopToken().getOffset();

                if ( left == offset - 1 )
                {
                    anchor = LEFT;
                    IRegion region = new Region( left, right - left + 1 );
                    return region;
                }
                if ( right == offset - 1 )
                {
                    anchor = RIGHT;
                    IRegion region = new Region( left, right - left + 1 );
                    return region;
                }
            }
        }

        return null;
    }


    /**
     * @see org.eclipse.jface.text.source.ICharacterPairMatcher#getAnchor()
     */
    public int getAnchor()
    {
        return anchor;
    }

}
