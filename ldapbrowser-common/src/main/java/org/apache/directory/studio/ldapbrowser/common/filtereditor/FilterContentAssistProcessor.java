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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.parsers.AttributeTypeDescription;
import org.apache.directory.shared.ldap.schema.parsers.MatchingRuleDescription;
import org.apache.directory.shared.ldap.schema.parsers.ObjectClassDescription;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterExtensibleComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilterItemComponent;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldapbrowser.core.model.schema.SchemaUtils;
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

    private static final Comparator<String> nameAndOidComparator = new Comparator<String>()
    {
        public int compare( String s1, String s2 )
        {
            if ( s1.matches( "[0-9\\.]+" ) && !s2.matches( "[0-9\\.]+" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return 1;
            }
            else if ( !s1.matches( "[0-9\\.]+" ) && s2.matches( "[0-9\\.]+" ) ) //$NON-NLS-1$ //$NON-NLS-2$
            {
                return -1;
            }
            else
            {
                return s1.compareToIgnoreCase( s2 );
            }
        }
    };

    /** The parser. */
    private LdapFilterParser parser;

    /** The source viewer, may be null. */
    private ISourceViewer sourceViewer;

    /** The auto activation characters. */
    private char[] autoActivationCharacters;

    /** The schema, used to retrieve attributeType and objectClass information. */
    private Schema schema;

    /** The possible attribute types. */
    private Map<String, AttributeTypeDescription> possibleAttributeTypes;

    /** The possible filter types. */
    private Map<String, String> possibleFilterTypes;

    /** The possible object classes. */
    private Map<String, ObjectClassDescription> possibleObjectClasses;

    /** The possible matching rules. */
    private Map<String, MatchingRuleDescription> possibleMatchingRules;


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

        this.autoActivationCharacters = new char[7 + 10 + 26 + 26];
        this.autoActivationCharacters[0] = '(';
        this.autoActivationCharacters[1] = ')';
        this.autoActivationCharacters[2] = '&';
        this.autoActivationCharacters[3] = '|';
        this.autoActivationCharacters[4] = '!';
        this.autoActivationCharacters[5] = ':';
        this.autoActivationCharacters[6] = '.';
        int i = 7;
        for ( char c = 'a'; c <= 'z'; c++, i++ )
        {
            this.autoActivationCharacters[i] = c;
        }
        for ( char c = 'A'; c <= 'Z'; c++, i++ )
        {
            this.autoActivationCharacters[i] = c;
        }
        for ( char c = '0'; c <= '9'; c++, i++ )
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

        possibleAttributeTypes = new TreeMap<String, AttributeTypeDescription>( nameAndOidComparator );
        possibleFilterTypes = new LinkedHashMap<String, String>();
        possibleObjectClasses = new TreeMap<String, ObjectClassDescription>( nameAndOidComparator );
        possibleMatchingRules = new TreeMap<String, MatchingRuleDescription>( nameAndOidComparator );

        if ( schema != null )
        {
            Collection<AttributeTypeDescription> attributeTypeDescriptions = schema.getAttributeTypeDescriptions();
            for ( AttributeTypeDescription atd : attributeTypeDescriptions )
            {
                possibleAttributeTypes.put( atd.getNumericOid(), atd );
                for ( String atdName : atd.getNames() )
                {
                    possibleAttributeTypes.put( atdName, atd );
                }
            }

            possibleFilterTypes.put( "=", Messages.getString( "FilterContentAssistProcessor.Equals" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            possibleFilterTypes.put( "=*", Messages.getString( "FilterContentAssistProcessor.Present" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            possibleFilterTypes.put( "<=", Messages.getString( "FilterContentAssistProcessor.LessThanOrEquals" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            possibleFilterTypes.put( ">=", Messages.getString( "FilterContentAssistProcessor.GreaterThanOrEquals" ) ); //$NON-NLS-1$ //$NON-NLS-2$
            possibleFilterTypes.put( "~=", Messages.getString( "FilterContentAssistProcessor.Approximately" ) ); //$NON-NLS-1$ //$NON-NLS-2$

            Collection<ObjectClassDescription> ocds = schema.getObjectClassDescriptions();
            for ( ObjectClassDescription ocd : ocds )
            {
                possibleObjectClasses.put( ocd.getNumericOid(), ocd );
                for ( String name : ocd.getNames() )
                {
                    possibleObjectClasses.put( name, ocd );
                }
            }

            Collection<MatchingRuleDescription> matchingRuleDescriptions = schema.getMatchingRuleDescriptions();
            for ( MatchingRuleDescription description : matchingRuleDescriptions )
            {
                possibleMatchingRules.put( description.getNumericOid(), description );
                for ( String name : description.getNames() )
                {
                    possibleMatchingRules.put( name, description );
                }
            }
        }
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
        String[] possibleObjectClasses = schema == null ? new String[0] : SchemaUtils.getNamesAsArray( schema
            .getObjectClassDescriptions() );
        Arrays.sort( possibleObjectClasses );

        List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
        LdapFilter filter = parser.getModel().getFilter( offset );
        if ( filter != null && offset > 0 )
        {
            // case 0: open curly started, show templates and all attribute types
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
                addPossibleAttributeTypes( proposalList, "", offset ); //$NON-NLS-1$
            }

            // case A: simple filter
            if ( filter.getFilterComponent() != null && filter.getFilterComponent() instanceof LdapFilterItemComponent )
            {
                LdapFilterItemComponent fc = ( LdapFilterItemComponent ) filter.getFilterComponent();

                // case A1: editing attribute type: show matching attribute types
                if ( fc.getStartToken().getOffset() <= offset
                    && offset <= fc.getStartToken().getOffset() + fc.getStartToken().getLength() )
                {
                    addPossibleAttributeTypes( proposalList, fc.getAttributeToken().getValue(), fc.getAttributeToken()
                        .getOffset() );
                }

                String attributeType = null;
                if ( schema != null && schema.hasAttributeTypeDescription( fc.getAttributeToken().getValue() ) )
                {
                    attributeType = fc.getAttributeToken().getValue();
                }

                // case A2: after attribte type: show possible filter types and extensible match options
                if ( attributeType != null )
                {
                    if ( ( fc.getAttributeToken().getOffset() <= offset || fc.getFilterToken() != null )
                        && offset <= fc.getAttributeToken().getOffset() + fc.getAttributeToken().getLength()
                            + ( fc.getFilterToken() != null ? fc.getFilterToken().getLength() : 0 ) )
                    {
                        //String attributeType = fc.getAttributeToken().getValue();
                        String filterType = fc.getFilterToken() != null ? fc.getFilterToken().getValue() : ""; //$NON-NLS-1$
                        int filterTypeOffset = fc.getAttributeToken().getOffset() + fc.getAttributeToken().getLength();
                        addPossibleFilterTypes( proposalList, attributeType, filterType, filterTypeOffset );
                    }
                }

                // case A3: editing objectClass attribute: show matching object classes
                if ( attributeType != null && SchemaConstants.OBJECT_CLASS_AT.equalsIgnoreCase( attributeType ) )
                {
                    if ( ( fc.getValueToken() != null && fc.getValueToken().getOffset() <= offset || fc
                        .getFilterToken() != null )
                        && offset <= fc.getAttributeToken().getOffset() + fc.getAttributeToken().getLength()
                            + ( fc.getFilterToken() != null ? fc.getFilterToken().getLength() : 0 )
                            + ( fc.getValueToken() != null ? fc.getValueToken().getLength() : 0 ) )
                    {
                        addPossibleObjectClasses( proposalList, fc.getValueToken() == null ? "" : fc.getValueToken() //$NON-NLS-1$
                            .getValue(), fc.getValueToken() == null ? offset : fc.getValueToken().getOffset() );
                    }
                }
            }

            // case B: extensible filter
            if ( filter.getFilterComponent() != null
                && filter.getFilterComponent() instanceof LdapFilterExtensibleComponent )
            {
                LdapFilterExtensibleComponent fc = ( LdapFilterExtensibleComponent ) filter.getFilterComponent();

                // case B1: editing extensible attribute type: show matching attribute types
                if ( fc.getAttributeToken() != null && fc.getAttributeToken().getOffset() <= offset
                    && offset <= fc.getAttributeToken().getOffset() + fc.getAttributeToken().getLength() )
                {
                    addPossibleAttributeTypes( proposalList, fc.getAttributeToken().getValue(), fc.getAttributeToken()
                        .getOffset() );
                }

                // case B2: editing dn
                if ( fc.getDnAttrToken() != null && fc.getDnAttrToken().getOffset() <= offset
                    && offset <= fc.getDnAttrToken().getOffset() + fc.getDnAttrToken().getLength() )
                {
                    addDnAttr( proposalList, fc.getDnAttrToken().getValue(), fc.getDnAttrToken().getOffset() );
                }

                // case B3: editing matching rule
                if ( fc.getMatchingRuleColonToken() != null
                    && fc.getMatchingRuleToken() == null
                    && fc.getMatchingRuleColonToken().getOffset() <= offset
                    && offset <= fc.getMatchingRuleColonToken().getOffset()
                        + fc.getMatchingRuleColonToken().getLength() )
                {
                    if ( fc.getDnAttrColonToken() == null )
                    {
                        addDnAttr( proposalList, "", offset ); //$NON-NLS-1$
                    }
                    addPossibleMatchingRules( proposalList, "", offset, fc.getEqualsColonToken(), fc.getEqualsToken() ); //$NON-NLS-1$
                }
                if ( fc.getMatchingRuleToken() != null && fc.getMatchingRuleToken().getOffset() <= offset
                    && offset <= fc.getMatchingRuleToken().getOffset() + fc.getMatchingRuleToken().getLength() )
                {
                    if ( fc.getDnAttrColonToken() == null )
                    {
                        addDnAttr( proposalList, fc.getMatchingRuleToken().getValue(), fc.getMatchingRuleToken()
                            .getOffset() );
                    }

                    String matchingRuleValue = fc.getMatchingRuleToken().getValue();
                    addPossibleMatchingRules( proposalList, matchingRuleValue, fc.getMatchingRuleToken().getOffset(),
                        fc.getEqualsColonToken(), fc.getEqualsToken() );
                }
            }
        }

        return proposalList.toArray( new ICompletionProposal[0] );
    }


    /**
     * Adds the possible attribute types to the proposal list.
     * 
     * @param proposalList the proposal list
     * @param attributeType the current attribute type
     * @param offset the offset
     */
    private void addPossibleAttributeTypes( List<ICompletionProposal> proposalList, String attributeType, int offset )
    {
        if ( schema != null )
        {
            for ( String possibleAttributeType : possibleAttributeTypes.keySet() )
            {
                AttributeTypeDescription description = possibleAttributeTypes.get( possibleAttributeType );
                if ( possibleAttributeType.toUpperCase().startsWith( attributeType.toUpperCase() ) )
                {
                    String replacementString = possibleAttributeType;
                    String displayString = possibleAttributeType;
                    if ( displayString.equals( description.getNumericOid() ) )
                    {
                        displayString += " (" + SchemaUtils.toString( description ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        displayString += " (" + description.getNumericOid() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    String info = SchemaUtils.getLdifLine( description );
                    ICompletionProposal proposal = new CompletionProposal( replacementString, offset, attributeType
                        .length(), replacementString.length(), getAttributeTypeImage(), displayString, null, info );
                    proposalList.add( proposal );
                }
            }
        }
    }


    /**
     * Adds the possible attribute types to the proposal list.
     * 
     * @param proposalList the proposal list
     * @param attributeType the current attribute type
     * @param offset the offset
     */
    private void addPossibleFilterTypes( List<ICompletionProposal> proposalList, String attributeType,
        String filterType, int offset )
    {
        if ( schema != null )
        {
            Map<String, String> copy = new LinkedHashMap<String, String>( possibleFilterTypes );
            if ( SchemaUtils.getEqualityMatchingRuleNameOrNumericOidTransitive( schema
                .getAttributeTypeDescription( attributeType ), schema ) == null )
            {
                copy.remove( "=" ); //$NON-NLS-1$
                copy.remove( "~=" ); //$NON-NLS-1$
            }
            if ( SchemaUtils.getOrderingMatchingRuleNameOrNumericOidTransitive( schema
                .getAttributeTypeDescription( attributeType ), schema ) == null )
            {
                copy.remove( "<=" ); //$NON-NLS-1$
                copy.remove( ">=" ); //$NON-NLS-1$
            }

            for ( String possibleFilterType : copy.keySet() )
            {
                String replacementString = possibleFilterType;
                String displayString = copy.get( possibleFilterType );

                ICompletionProposal proposal = new CompletionProposal( replacementString, offset, filterType.length(),
                    possibleFilterType.length(), getFilterTypeImage(), displayString, null, null );
                proposalList.add( proposal );
            }
        }
    }


    /**
     * Adds the possible object classes to the proposal list.
     * 
     * @param proposalList the proposal list
     * @param objectClasses the object class
     * @param offset the offset
     */
    private void addPossibleObjectClasses( List<ICompletionProposal> proposalList, String objectClass, int offset )
    {
        if ( schema != null )
        {
            for ( String possibleObjectClass : possibleObjectClasses.keySet() )
            {
                ObjectClassDescription description = possibleObjectClasses.get( possibleObjectClass );
                if ( possibleObjectClass.toUpperCase().startsWith( objectClass.toUpperCase() ) )
                {
                    String replacementString = possibleObjectClass;
                    String displayString = possibleObjectClass;
                    if ( displayString.equals( description.getNumericOid() ) )
                    {
                        displayString += " (" + SchemaUtils.toString( description ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        displayString += " (" + description.getNumericOid() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }

                    ICompletionProposal proposal = new CompletionProposal( replacementString, offset, objectClass
                        .length(), replacementString.length(), getObjectClassImage(), displayString, null, SchemaUtils
                        .getLdifLine( schema.getObjectClassDescription( possibleObjectClass ) ) );
                    proposalList.add( proposal );
                }
            }
        }
    }


    /**
     * Adds the possible matching rules (that fits to the given attribute type) to the proposal list.
     * 
     * @param proposalList the proposal list
     * @param matchingRule the matching rule
     * @param offset the offset
     */
    private void addPossibleMatchingRules( List<ICompletionProposal> proposalList, String matchingRule, int offset,
        LdapFilterToken equalsColonToken, LdapFilterToken equalsToken )
    {
        if ( schema != null )
        {
            for ( String possibleMatchingRule : possibleMatchingRules.keySet() )
            {
                if ( possibleMatchingRule.toUpperCase().startsWith( matchingRule.toUpperCase() ) )
                {
                    MatchingRuleDescription description = schema.getMatchingRuleDescription( possibleMatchingRule );
                    String replacementString = possibleMatchingRule;
                    if ( equalsColonToken == null )
                    {
                        replacementString += ":"; //$NON-NLS-1$
                    }
                    if ( equalsToken == null )
                    {
                        replacementString += "="; //$NON-NLS-1$
                    }
                    String displayString = possibleMatchingRule;
                    if ( displayString.equals( description.getNumericOid() ) )
                    {
                        displayString += " (" + SchemaUtils.toString( description ) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    else
                    {
                        displayString += " (" + description.getNumericOid() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    String info = SchemaUtils.getLdifLine( description );
                    ICompletionProposal proposal = new CompletionProposal( replacementString, offset, matchingRule
                        .length(), replacementString.length(), getMatchingRuleImage(), displayString, null, info );
                    proposalList.add( proposal );
                }
            }
        }
    }


    /**
     * Adds the dn: proposal to the proposal list.
     * 
     * @param proposalList the proposal list
     * @param dnAttr the dn attr
     * @param offset the offset
     */
    private void addDnAttr( List<ICompletionProposal> proposalList, String dnAttr, int offset )
    {
        if ( "dn".toUpperCase().startsWith( dnAttr.toUpperCase() ) ) //$NON-NLS-1$
        {
            String replacementString = "dn:"; //$NON-NLS-1$
            String displayString = "dn: ()"; //$NON-NLS-1$
            ICompletionProposal proposal = new CompletionProposal( replacementString, offset, dnAttr.length(),
                replacementString.length(), null, displayString, null, null );
            proposalList.add( proposal );
        }
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
     * Gets the filter type image.
     * 
     * @return the filter type image
     */
    private Image getFilterTypeImage()
    {
        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_FILTER_EDITOR );
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
     * Gets the matching rule image.
     * 
     * @return the matching rule image
     */
    private Image getMatchingRuleImage()
    {
        return BrowserCommonActivator.getDefault().getImage( BrowserCommonConstants.IMG_MRD );
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
