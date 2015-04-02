/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.studio.openldap.config.acl.widgets;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;


public class AttributesWidgetContentProposalProvider implements IContentProposalProvider
{
    /** The content proposal adapter */
    private ContentProposalAdapter proposalAdapter;

    /** The browser connection */
    private IBrowserConnection browserConnection;

    /** The proposals */
    private List<AttributesWidgetContentProposal> proposals;


    public AttributesWidgetContentProposalProvider()
    {
        // Initializing the proposals list
        proposals = new ArrayList<AttributesWidgetContentProposal>();

        // Building the proposals list
        buildProposals();
    }


    public IContentProposal[] getProposals( String contents, int position )
    {
        String value = getCurrentValue( contents, position );

        List<AttributesWidgetContentProposal> matchingProposals = new ArrayList<AttributesWidgetContentProposal>();
        for ( AttributesWidgetContentProposal proposal : proposals )
        {
            if ( proposal.getLabel().toUpperCase().startsWith( value.toUpperCase() ) )
            {
                matchingProposals.add( proposal );
                proposal.setStartPosition( value.length() );
            }
        }

        return matchingProposals.toArray( new AttributesWidgetContentProposal[0] );
    }


    /**
     * {@inheritDoc}
     */
    public char[] getAutoActivationCharacters()
    {
        return new char[0];
    }


    /**
     * Gets the current value.
     *
     * @param contents the contents
     * @param position the position
     * @return the current value
     */
    private String getCurrentValue( String contents, int position )
    {
        int start = 0;

        for ( int i = position - 1; i >= 0; i-- )
        {
            char c = contents.charAt( i );
            if ( c == ',' || Character.isWhitespace( c ) )
            {
                start = i + 1;
                break;
            }
        }

        return contents.substring( start, position );
    }


    /**
     * Builds the proposals list
     */
    private void buildProposals()
    {
        // Reseting previous proposals
        proposals.clear();

        // Adding proposals
        addKeywordProposals();
        addConnectionProposals();

        // Sorting the proposals
        sortProposals();

        // Setting auto-activation characters
        setAutoActivationChars();
    }


    /**
     * Sets the auto-activation characters
     */
    private void setAutoActivationChars()
    {
        if ( proposalAdapter != null )
        {
            Set<Character> characterSet = new HashSet<Character>();
            for ( IContentProposal proposal : proposals )
            {
                String string = proposal.getLabel();
                for ( int k = 0; k < string.length(); k++ )
                {
                    char ch = string.charAt( k );
                    characterSet.add( Character.toLowerCase( ch ) );
                    characterSet.add( Character.toUpperCase( ch ) );
                }
            }

            char[] autoActivationCharacters = new char[characterSet.size() + 1];
            autoActivationCharacters[0] = ',';
            int i = 1;
            for ( Iterator<Character> it = characterSet.iterator(); it.hasNext(); )
            {
                Character ch = it.next();
                autoActivationCharacters[i] = ch.charValue();
                i++;
            }

            proposalAdapter.setAutoActivationCharacters( autoActivationCharacters );
        }
    }


    /**
     * Adding the keyword proposals
     */
    private void addKeywordProposals()
    {
        proposals.add( new KeywordContentProposal( "entry" ) );
        proposals.add( new KeywordContentProposal( "children" ) );
    }


    /**
     * Adding the connection proposals (attribute types and object classes).
     */
    private void addConnectionProposals()
    {
        if ( browserConnection != null )
        {
            // Attribute types
            Collection<String> atNames = SchemaUtils.getNames( browserConnection.getSchema().getAttributeTypeDescriptions() );
            for ( String atName : atNames )
            {
                proposals.add( new AttributeTypeContentProposal( atName ) );
            }

            // Object classes
            Collection<String> ocNames = SchemaUtils.getNames( browserConnection.getSchema().getObjectClassDescriptions() );
            for ( String ocName : ocNames )
            {
                proposals.add( new ObjectClassContentProposal( "@" + ocName ) );
                proposals.add( new ObjectClassContentProposal( "!" + ocName ) );
            }
        }
    }


    /**
     * Sorts the proposals.
     */
    private void sortProposals()
    {
        Comparator<? super AttributesWidgetContentProposal> comparator = new Comparator<AttributesWidgetContentProposal>()
        {
            public int compare( AttributesWidgetContentProposal o1, AttributesWidgetContentProposal o2 )
            {
                if ( ( o1 instanceof KeywordContentProposal ) && !( o2 instanceof KeywordContentProposal ) )
                {
                    return -2;
                }
                else if ( !( o1 instanceof KeywordContentProposal ) && ( o2 instanceof KeywordContentProposal ) )
                {
                    return 2;
                }

                else if ( ( o1 instanceof AttributeTypeContentProposal )
                    && !( o2 instanceof AttributeTypeContentProposal ) )
                {
                    return -3;
                }
                else if ( !( o1 instanceof AttributeTypeContentProposal )
                    && ( o2 instanceof AttributeTypeContentProposal ) )
                {
                    return 3;
                }

                else if ( ( o1 instanceof ObjectClassContentProposal )
                    && !( o2 instanceof ObjectClassContentProposal ) )
                {
                    return -3;
                }
                else if ( !( o1 instanceof ObjectClassContentProposal )
                    && ( o2 instanceof ObjectClassContentProposal ) )
                {
                    return 3;
                }

                return o1.getLabel().compareToIgnoreCase( o2.getLabel() );
            }
        };
        Collections.sort( proposals, comparator );
    }


    /**
     * @param browserConnection the browser connection to set
     */
    public void setBrowserConnection( IBrowserConnection browserConnection )
    {
        this.browserConnection = browserConnection;

        // Re-building proposals
        buildProposals();
    }


    /**
     * @param proposalAdapter the proposalAdapter to set
     */
    public void setProposalAdapter( ContentProposalAdapter proposalAdapter )
    {
        this.proposalAdapter = proposalAdapter;
    }
}
