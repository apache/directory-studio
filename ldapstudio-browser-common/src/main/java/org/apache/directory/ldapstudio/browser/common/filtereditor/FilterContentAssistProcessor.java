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
import org.apache.directory.ldapstudio.browser.core.model.IAttribute;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterToken;
import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;


/**
 * The FilterContentAssistProcessor computes the content proposals for the filter editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterContentAssistProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor,
    IContentProposalProvider
{

    /** The parser. */
    private LdapFilterParser parser;

    /** The source viewer, may be null. */
    private ISourceViewer sourceViewer;

    /** The auto activation characters. */
    private char[] autoActivationCharacters;

    /** The schema, used to retrieve attributeType and objectClass information. */
    private Schema schema;


    /**
     * Creates a new instance of FilterContentAssistProcessor.
     * 
     * @param parser the parser
     */
    public FilterContentAssistProcessor( LdapFilterParser parser )
    {
        this( null, parser );
    }


    /**
     * Creates a new instance of FilterContentAssistProcessor.
     * 
     * @param sourceViewer the source viewer
     * @param parser the parser
     */
    public FilterContentAssistProcessor( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
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


    /**
     * Sets the schema, used to retrieve attributeType and objectClass information.
     * 
     * @param schema the schema
     */
    public void setSchema( Schema schema )
    {
        this.schema = schema;
    }


    /**
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getCompletionProposalAutoActivationCharacters()
     */
    public char[] getCompletionProposalAutoActivationCharacters()
    {
        return autoActivationCharacters;
    }


    /**
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
     */
    public ICompletionProposal[] computeCompletionProposals( ITextViewer viewer, int offset )
    {
        return computeCompletionProposals( offset );
    }


    /**
     * @see org.eclipse.jface.fieldassist.IContentProposalProvider#getProposals(java.lang.String, int)
     */
    public IContentProposal[] getProposals( final String contents, final int position )
    {
        parser.parse( contents );

        ICompletionProposal[] oldProposals = computeCompletionProposals( position );
        IContentProposal[] proposals = new IContentProposal[oldProposals.length];
        for ( int i = 0; i < oldProposals.length; i++ )
        {
            final ICompletionProposal oldProposal = oldProposals[i];
            final Document document = new Document( contents );
            oldProposal.apply( document );

            proposals[i] = new IContentProposal()
            {
                public String getContent()
                {
                    return document.get();
                }


                public int getCursorPosition()
                {
                    return oldProposal.getSelection( document ).x;
                }


                public String getDescription()
                {
                    return oldProposal.getAdditionalProposalInfo();
                }


                public String getLabel()
                {
                    return oldProposal.getDisplayString();
                }


                public String toString()
                {
                    return getContent();
                }
            };
        }

        return proposals;
    }


    /**
     * Computes completion proposals.
     * 
     * @param offset the offset
     * 
     * @return the matching completion proposals
     */
    private ICompletionProposal[] computeCompletionProposals( int offset )
    {
        String[] possibleAttributeTypes = schema == null ? new String[0] : schema.getAttributeTypeDescriptionNames();
        Arrays.sort( possibleAttributeTypes );
        String[] possibleObjectClasses = schema == null ? new String[0] : schema.getObjectClassDescriptionNames();
        Arrays.sort( possibleObjectClasses );

        List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
        LdapFilter filter = parser.getModel().getFilter( offset );
        if ( filter != null )
        {
            String attributeType = null;

            // case 1: open curly started, show templates and all attribute types
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

                for ( int k = 0; k < possibleAttributeTypes.length; k++ )
                {
                    ICompletionProposal proposal = new CompletionProposal( possibleAttributeTypes[k], offset, 0,
                        possibleAttributeTypes[k].length(), getAttributeTypeImage(), null, null, schema
                            .getAttributeTypeDescription( possibleAttributeTypes[k] ).getLine().getUnfoldedValue() );
                    proposalList.add( proposal );
                }
            }

            // case 2: editing attribute type: show matching attribute types
            else if ( filter.getFilterComponent() instanceof LdapFilterItemComponent
                && filter.getFilterComponent().getStartToken().getOffset() <= offset
                && offset <= filter.getFilterComponent().getStartToken().getOffset()
                    + filter.getFilterComponent().getStartToken().getLength() )
            {
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();
                for ( int k = 0; k < possibleAttributeTypes.length; k++ )
                {
                    if ( possibleAttributeTypes[k].equalsIgnoreCase( fc.getAttributeToken().getValue() ) )
                    {
                    }
                    else if ( possibleAttributeTypes[k].startsWith( fc.getAttributeToken().getValue() ) )
                    {
                        ICompletionProposal proposal = new CompletionProposal( possibleAttributeTypes[k], fc
                            .getAttributeToken().getOffset(), fc.getAttributeToken().getLength(),
                            possibleAttributeTypes[k].length(), getAttributeTypeImage(), null, null, schema
                                .getAttributeTypeDescription( possibleAttributeTypes[k] ).getLine().getUnfoldedValue() );
                        proposalList.add( proposal );
                    }
                }
            }

            if ( filter.getFilterComponent() instanceof LdapFilterItemComponent )
            {
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();
                for ( int k = 0; k < possibleAttributeTypes.length; k++ )
                {
                    if ( possibleAttributeTypes[k].equalsIgnoreCase( fc.getAttributeToken().getValue() ) )
                    {
                        attributeType = fc.getAttributeToken().getValue();
                        break;
                    }
                }
            }

            // case 3: after attribte type: show possible assertion types
            if ( attributeType != null && filter.getFilterComponent() instanceof LdapFilterItemComponent )
            {
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();
                if ( ( fc.getAttributeToken().getOffset() <= offset || fc.getFilterToken() != null )
                    && offset <= fc.getAttributeToken().getOffset() + fc.getAttributeToken().getLength()
                        + ( fc.getFilterToken() != null ? fc.getFilterToken().getLength() : 0 ) )
                {
                    LdapFilterToken filterTypeToken = fc.getFilterToken();

                    // determine matching assertion types depending on the schema's attribute type description
                    List<String> possibleAssertionTypes = new ArrayList<String>( Arrays.asList( new String[]
                        { "=", "=*", "<=", ">=", "~=" } ) );
                    if ( schema != null )
                    {
                        if ( schema.getAttributeTypeDescription( attributeType )
                            .getEqualityMatchingRuleDescriptionOIDTransitive() == null )
                        {
                            possibleAssertionTypes.remove( "=" );
                            possibleAssertionTypes.remove( "~=" );
                        }
                        if ( schema.getAttributeTypeDescription( attributeType )
                            .getOrderingMatchingRuleDescriptionOIDTransitive() == null )
                        {
                            possibleAssertionTypes.remove( "<=" );
                            possibleAssertionTypes.remove( ">=" );
                        }
                    }
                    for ( String possibleAssertionType : possibleAssertionTypes )
                    {
                        if ( filterTypeToken == null
                            || !possibleAssertionType.equalsIgnoreCase( filterTypeToken.getValue() ) )
                        {
                            ICompletionProposal proposal = new CompletionProposal( possibleAssertionType, fc
                                .getAttributeToken().getOffset()
                                + fc.getAttributeToken().getLength(), filterTypeToken != null ? filterTypeToken
                                .getLength() : 0, possibleAssertionType.length(), null, null, null, null );
                            proposalList.add( proposal );
                        }
                    }
                }
            }

            // case 4: editing objectClass attribute: show matching object classes
            if ( attributeType != null && IAttribute.OBJECTCLASS_ATTRIBUTE.equalsIgnoreCase( attributeType )
                && filter.getFilterComponent() instanceof LdapFilterItemComponent )
            {
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();
                if ( ( fc.getValueToken() != null && fc.getValueToken().getOffset() <= offset || fc.getFilterToken() != null )
                    && offset <= fc.getAttributeToken().getOffset() + fc.getAttributeToken().getLength()
                        + ( fc.getFilterToken() != null ? fc.getFilterToken().getLength() : 0 )
                        + ( fc.getValueToken() != null ? fc.getValueToken().getLength() : 0 ) )
                {
                    LdapFilterToken valueToken = fc.getValueToken();
                    for ( int k = 0; k < possibleObjectClasses.length; k++ )
                    {
                        if ( fc.getValueToken() == null
                            || possibleObjectClasses[k].startsWith( fc.getValueToken().getValue() ) )
                        {
                            ICompletionProposal proposal = new CompletionProposal( possibleObjectClasses[k], fc
                                .getAttributeToken().getOffset()
                                + fc.getAttributeToken().getLength() + fc.getFilterToken().getLength(),
                                valueToken != null ? valueToken.getLength() : 0, possibleObjectClasses[k].length(),
                                getObjectClassImage(), null, null, schema.getObjectClassDescription(
                                    possibleObjectClasses[k] ).getLine().getUnfoldedValue() );
                            proposalList.add( proposal );
                        }
                    }
                }
            }
        }

        //System.out.println(proposalList);

        return proposalList.toArray( new ICompletionProposal[0] );
    }


    /**
     * Gets the attribute type image.
     * 
     * @return the attribute type image
     */
    private Image getAttributeTypeImage()
    {
        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_ATD );
    }


    /**
     * Gets the object class image.
     * 
     * @return the object class image
     */
    private Image getObjectClassImage()
    {
        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_OCD );
    }


    /**
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
     */
    protected Template[] getTemplates( String contextTypeId )
    {
        Template[] templates = BrowserCommonActivator.getDefault().getFilterTemplateStore().getTemplates(
            BrowserCommonConstants.FILTER_TEMPLATE_ID );
        return templates;
    }


    /**
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
     */
    protected TemplateContextType getContextType( ITextViewer viewer, IRegion region )
    {
        TemplateContextType contextType = BrowserCommonActivator.getDefault().getFilterTemplateContextTypeRegistry()
            .getContextType( BrowserCommonConstants.FILTER_TEMPLATE_ID );
        return contextType;
    }


    /**
     * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
     */
    protected Image getImage( Template template )
    {
        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_TEMPLATE );
    }


    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
     */
    public IContextInformation[] computeContextInformation( ITextViewer viewer, int documentOffset )
    {
        return null;
    }


    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
     */
    public char[] getContextInformationAutoActivationCharacters()
    {
        return null;
    }


    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
     */
    public String getErrorMessage()
    {
        return null;
    }


    /**
     * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
     */
    public IContextInformationValidator getContextInformationValidator()
    {
        return null;
    }

}
