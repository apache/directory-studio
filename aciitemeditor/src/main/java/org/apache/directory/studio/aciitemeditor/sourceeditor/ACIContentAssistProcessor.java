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
package org.apache.directory.studio.aciitemeditor.sourceeditor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.directory.studio.aciitemeditor.ACIITemConstants;
import org.apache.directory.studio.aciitemeditor.Activator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the Content Assist Processor for ACI Item
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ACIContentAssistProcessor extends TemplateCompletionProcessor
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

        // Add context dependend template proposals
        ICompletionProposal[] templateProposals = super.computeCompletionProposals( viewer, offset );
        if ( templateProposals != null )
        {
            proposalList.addAll( Arrays.asList( templateProposals ) );
        }

        return ( ICompletionProposal[] ) proposalList.toArray( new ICompletionProposal[0] );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation( ITextViewer viewer, int offset )
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters()
    {

        char[] chars = new char[52];
        for ( int i = 0; i < 26; i++ )
        {
            chars[i] = ( char ) ( 'a' + i );
        }
        for ( int i = 0; i < 26; i++ )
        {
            chars[i + 26] = ( char ) ( 'A' + i );
        }

        return chars;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getErrorMessage()
     */
    public String getErrorMessage()
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    @Override
    protected TemplateContextType getContextType( ITextViewer viewer, IRegion region )
    {
        return Activator.getDefault().getAciTemplateContextTypeRegistry().getContextType(
            ACIITemConstants.ACI_ITEM_TEMPLATE_ID );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
     */
    @Override
    protected Image getImage( Template template )
    {
        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
     */
    @Override
    protected Template[] getTemplates( String contextTypeId )
    {
        return Activator.getDefault().getAciTemplateStore().getTemplates( contextTypeId );
    }
}
