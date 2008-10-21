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
package org.apache.directory.studio.ldapbrowser.common.widgets;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;


/**
 * ListContentProposalProvider is a class designed to map a dynamic list of
 * Strings to content proposals.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ListContentProposalProvider implements IContentProposalProvider
{

    /** The dynamic list of proposals */
    private List<String> proposals;


    /**
     * Creates a new instance of ListContentProposalProvider.
     *
     * @param proposals the dynamic list of proposals
     */
    public ListContentProposalProvider( List<String> proposals )
    {
        setProposals( proposals );
    }


    /**
     * Creates a new instance of ListContentProposalProvider.
     *
     * @param proposals the proposals
     */
    public ListContentProposalProvider( String[] proposals )
    {
        setProposals( new ArrayList<String>( Arrays.asList( proposals ) ) );
    }


    /**
     * {@inheritDoc}
     */
    public IContentProposal[] getProposals( String contents, int position )
    {
        String string = contents.substring( 0, position );

        Collections.sort( proposals );

        List<IContentProposal> proposalList = new ArrayList<IContentProposal>();
        for ( int k = 0; k < proposals.size(); k++ )
        {
            final String proposal = proposals.get( k );
            if ( proposal.toUpperCase().startsWith( string.toUpperCase() ) && !proposal.equalsIgnoreCase( string )
                && !"".equals( string ) )
            {
                IContentProposal p = new IContentProposal()
                {
                    public String getContent()
                    {
                        return proposal;
                    }


                    public String getDescription()
                    {
                        return proposal;
                    }


                    public String getLabel()
                    {
                        return proposal;
                    }


                    public int getCursorPosition()
                    {
                        return proposal.length();
                    }
                };
                proposalList.add( p );
            }
        }
        return proposalList.toArray( new IContentProposal[proposalList.size()] );
    }


    /**
     * Sets the possible strings.
     * 
     * @param proposals the possible strings
     */
    public void setProposals( List<String> proposals )
    {
        if ( proposals == null )
        {
            proposals = new ArrayList<String>();
        }

        this.proposals = proposals;
    }

}
