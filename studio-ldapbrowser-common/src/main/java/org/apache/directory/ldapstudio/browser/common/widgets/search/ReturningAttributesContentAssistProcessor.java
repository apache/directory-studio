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

package org.apache.directory.ldapstudio.browser.common.widgets.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.contentassist.ISubjectControlContentAssistProcessor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;


/**
 * The ReturningAttributesContentAssistProcessor provides proposals for the 
 * {@link ReturningAttributesWidget}. It splits the comma separted text input
 * into separate regions.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ReturningAttributesContentAssistProcessor implements ISubjectControlContentAssistProcessor
{

    /** The auto activation characters */
    private char[] autoActivationCharacters;

    /** The possible attribute types */
    private String[] possibleAttributeTypes;


    /**
     * Creates a new instance of ReturningAttributesContentAssistProcessor.
     *
     * @param possibleAttributeNames
     */
    public ReturningAttributesContentAssistProcessor( String[] possibleAttributeNames )
    {
        super();
        setPossibleAttributeTypes( possibleAttributeTypes );
    }


    /**
     * {@inheritDoc}
     */
    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return autoActivationCharacters;
    }


    /**
     * Sets the possible attribute types.
     * 
     * @param possibleAttributeTypes the possible strings
     */
    public void setPossibleAttributeTypes( String[] possibleAttributeTypes )
    {
        if ( possibleAttributeTypes == null )
        {
            possibleAttributeTypes = new String[0];
        }

        // set possible strings
        Arrays.sort( possibleAttributeTypes );
        this.possibleAttributeTypes = possibleAttributeTypes;

        // set auto activation characters
        Set<Character> characterSet = new HashSet<Character>();
        for ( int i = 0; i < possibleAttributeTypes.length; i++ )
        {
            String string = possibleAttributeTypes[i];
            for ( int k = 0; k < string.length(); k++ )
            {
                char ch = string.charAt( k );
                characterSet.add( Character.toLowerCase( ch ) );
                characterSet.add( Character.toUpperCase( ch ) );
            }
        }
        autoActivationCharacters = new char[characterSet.size()];
        int i = 0;
        for ( Iterator<Character> it = characterSet.iterator(); it.hasNext(); )
        {
            Character ch = it.next();
            autoActivationCharacters[i] = ch.charValue();
            i++;
        }
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always returns null.
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public ICompletionProposal[] computeCompletionProposals( IContentAssistSubjectControl contentAssistSubjectControl,
        int documentOffset )
    {
        IDocument document = contentAssistSubjectControl.getDocument();
        String text = document.get();
       
        // search start of current attribute type
        int start = 0;
        for ( int i = documentOffset - 1; i >= 0; i-- )
        {
            char c = text.charAt( i );
            if ( c == ',' || Character.isWhitespace( c ) )
            {
                start = i + 1;
                break;
            }
        }
        String attribute = text.substring( start, documentOffset );

        // create proposal list
        List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
        for ( int k = 0; k < possibleAttributeTypes.length; k++ )
        {
            if ( possibleAttributeTypes[k].toUpperCase().startsWith( attribute.toUpperCase() ) )
            {
                ICompletionProposal proposal = new CompletionProposal( possibleAttributeTypes[k] + ", ", start,
                    documentOffset - start, possibleAttributeTypes[k].length() + 2, null, possibleAttributeTypes[k],
                    null, null );
                proposalList.add( proposal );
            }
        }
        return proposalList.toArray( new ICompletionProposal[proposalList.size()] );
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always returns null.
     */
    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always returns null.
     */
    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always returns null.
     */
    public IContextInformation[] computeContextInformation( IContentAssistSubjectControl contentAssistSubjectControl,
        int documentOffset )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always returns null.
     */
    public String getErrorMessage()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * This implementation always returns null.
     */
    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

}
