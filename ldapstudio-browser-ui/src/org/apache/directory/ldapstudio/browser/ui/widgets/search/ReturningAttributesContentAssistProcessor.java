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

package org.apache.directory.ldapstudio.browser.ui.widgets.search;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.contentassist.ISubjectControlContentAssistProcessor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;


public class ReturningAttributesContentAssistProcessor implements ISubjectControlContentAssistProcessor
{

    private char[] autoActivationCharacters;

    private String[] possibleAttributeTypes;


    public ReturningAttributesContentAssistProcessor( String[] possibleAttributeNames )
    {
        super();
        this.setPossibleAttributeTypes( possibleAttributeNames );

        this.autoActivationCharacters = new char[26 + 26];
        int i = 0;
        for ( char c = 'a'; c <= 'z'; c++, i++ )
        {
            this.autoActivationCharacters[i] = c;
        }
        for ( char c = 'A'; c <= 'Z'; c++, i++ )
        {
            this.autoActivationCharacters[i] = c;
        }
    }


    public void setPossibleAttributeTypes( String[] possibleAttributeTypes )
    {
        Arrays.sort( possibleAttributeTypes );
        this.possibleAttributeTypes = possibleAttributeTypes;
    }


    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return this.autoActivationCharacters;
    }


    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        return null;
    }


    public ICompletionProposal[] computeCompletionProposals( IContentAssistSubjectControl contentAssistSubjectControl,
        int documentOffset )
    {
        IDocument document = contentAssistSubjectControl.getDocument();
        String text = document.get();
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

        List proposalList = new ArrayList();
        for ( int k = 0; k < this.possibleAttributeTypes.length; k++ )
        {
            if ( this.possibleAttributeTypes[k].startsWith( attribute ) )
            {
                ICompletionProposal proposal = new CompletionProposal( this.possibleAttributeTypes[k] + ", ", start,
                    documentOffset - start, this.possibleAttributeTypes[k].length() + 2, null,
                    this.possibleAttributeTypes[k], null, null );
                proposalList.add( proposal );
            }
        }
        return ( ICompletionProposal[] ) proposalList.toArray( new ICompletionProposal[0] );
    }


    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }


    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }


    public IContextInformation[] computeContextInformation( IContentAssistSubjectControl contentAssistSubjectControl,
        int documentOffset )
    {
        return null;
    }


    public String getErrorMessage()
    {
        return null;
    }


    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }
}
