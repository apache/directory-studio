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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.ldapstudio.browser.common.BrowserCommonConstants;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;

import org.eclipse.jface.contentassist.IContentAssistSubjectControl;
import org.eclipse.jface.contentassist.ISubjectControlContentAssistProcessor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;


// TODO: Refactor Filter Editor
public class FilterContentAssistProcessor extends TemplateCompletionProcessor implements
    ISubjectControlContentAssistProcessor
{

    private LdapFilterParser parser;

    private ISourceViewer sourceViewer;

    private char[] autoActivationCharacters;

    private String[] possibleAttributeTypes;


    public FilterContentAssistProcessor( LdapFilterParser parser )
    {
        this( null, parser );
    }


    public FilterContentAssistProcessor( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
        super();
        this.parser = parser;
        this.sourceViewer = sourceViewer;

        this.autoActivationCharacters = new char[1 + 26 + 26];
        this.autoActivationCharacters[0] = '(';
        int i = 1;
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
        return this.computeCompletionProposals( offset );
    }


    public ICompletionProposal[] computeCompletionProposals( IContentAssistSubjectControl contentAssistSubjectControl,
        int documentOffset )
    {
        String filter = contentAssistSubjectControl.getDocument().get();
        this.parser.parse( filter );
        return this.computeCompletionProposals( documentOffset );
    }


    public IContextInformation[] computeContextInformation( IContentAssistSubjectControl contentAssistSubjectControl,
        int documentOffset )
    {
        return null;
    }


    private ICompletionProposal[] computeCompletionProposals( int offset )
    {
        List proposalList = new ArrayList();
        LdapFilter filter = this.parser.getModel().getFilter( offset );
        if ( filter != null )
        {
            if ( filter.getStartToken() != null && filter.getFilterComponent() == null )
            {

                if ( sourceViewer != null )
                {
                    ICompletionProposal[] templateProposals = super.computeCompletionProposals( sourceViewer, offset );
                    if ( templateProposals != null )
                    {
                        proposalList.addAll( Arrays.asList( templateProposals ) );
                    }
                }

                for ( int k = 0; k < this.possibleAttributeTypes.length; k++ )
                {
                    ICompletionProposal proposal = new CompletionProposal( this.possibleAttributeTypes[k], offset, 0,
                        this.possibleAttributeTypes[k].length() );
                    proposalList.add( proposal );
                }
            }
            else if ( filter.getFilterComponent() instanceof LdapFilterItemComponent
                && filter.getFilterComponent().getStartToken().getOffset() <= offset
                && offset <= filter.getFilterComponent().getStartToken().getOffset()
                    + filter.getFilterComponent().getStartToken().getLength() )
            {
                // show matching attribute types
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();
                for ( int k = 0; k < this.possibleAttributeTypes.length; k++ )
                {
                    if ( this.possibleAttributeTypes[k].startsWith( fc.getAttributeToken().getValue() ) )
                    {
                        ICompletionProposal proposal = new CompletionProposal( this.possibleAttributeTypes[k], fc
                            .getAttributeToken().getOffset(), fc.getAttributeToken().getLength(),
                            this.possibleAttributeTypes[k].length() );
                        proposalList.add( proposal );
                    }
                }
            }
            else
            {
                // no proposals
            }
        }
        return ( ICompletionProposal[] ) proposalList.toArray( new ICompletionProposal[0] );

    }


    protected Template[] getTemplates( String contextTypeId )
    {
        Template[] templates = BrowserCommonActivator.getDefault().getFilterTemplateStore().getTemplates(
            BrowserCommonConstants.FILTER_TEMPLATE_ID );
        return templates;
    }


    protected TemplateContextType getContextType( ITextViewer viewer, IRegion region )
    {
        TemplateContextType contextType = BrowserCommonActivator.getDefault().getFilterTemplateContextTypeRegistry()
            .getContextType( BrowserCommonConstants.FILTER_TEMPLATE_ID );
        return contextType;
    }


    protected Image getImage( Template template )
    {
        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_TEMPLATE );
    }

}
