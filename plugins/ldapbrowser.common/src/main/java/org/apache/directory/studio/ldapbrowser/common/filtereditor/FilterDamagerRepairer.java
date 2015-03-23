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


import org.apache.directory.studio.common.core.jobs.CommonCoreConstants;
import org.apache.directory.studio.common.ui.CommonUIConstants;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.RGB;


/**
 * The FilterDamagerRepairer is used for syntax highlighting.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class FilterDamagerRepairer implements IPresentationDamager, IPresentationRepairer
{

    private static final TextAttribute DEFAULT_TEXT_ATTRIBUTE = new TextAttribute( BrowserCommonActivator.getDefault()
        .getColor( CommonUIConstants.BLACK ) );

    private static final TextAttribute AND_OR_NOT_TEXT_ATTRIBUTE = new TextAttribute( BrowserCommonActivator
        .getDefault().getColor( CommonUIConstants.M_GREEN ), null, SWT.BOLD );

    private static final TextAttribute ATTRIBUTE_TEXT_ATTRIBUTE = new TextAttribute( BrowserCommonActivator
        .getDefault().getColor( new RGB( 128, 0, 96 ) ) );

    private static final TextAttribute FILTER_TYPE_TEXT_ATTRIBUTE = new TextAttribute( BrowserCommonActivator
        .getDefault().getColor( CommonUIConstants.RED ), null, SWT.BOLD );

    private static final TextAttribute VALUE_TEXT_ATTRIBUTE = new TextAttribute( BrowserCommonActivator.getDefault()
        .getColor( CommonUIConstants.M_BLUE ) );

    private static final TextAttribute PARENTHESIS_TEXT_ATTRIBUTE = new TextAttribute( BrowserCommonActivator
        .getDefault().getColor( CommonUIConstants.BLACK ), null, SWT.BOLD );

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The document. */
    private IDocument document;


    /**
     * Creates a new instance of FilterDamagerRepairer.
     * 
     * @param parser the filter parser
     */
    public FilterDamagerRepairer( LdapFilterParser parser )
    {
        this.parser = parser;
        this.document = null;
    }


    /**
     * @see org.eclipse.jface.text.presentation.IPresentationDamager#setDocument(org.eclipse.jface.text.IDocument)
     */
    public void setDocument( IDocument document )
    {
        this.document = document;
    }


    /**
     * @see org.eclipse.jface.text.presentation.IPresentationDamager#getDamageRegion(org.eclipse.jface.text.ITypedRegion, org.eclipse.jface.text.DocumentEvent, boolean)
     */
    public IRegion getDamageRegion( ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged )
    {
        return partition;
    }


    /**
     * @see org.eclipse.jface.text.presentation.IPresentationRepairer#createPresentation(org.eclipse.jface.text.TextPresentation, org.eclipse.jface.text.ITypedRegion)
     */
    public void createPresentation( TextPresentation presentation, ITypedRegion damage )
    {
        // parse the filter
        parser.parse( this.document.get() );

        // get tokens
        LdapFilterToken[] tokens = parser.getModel().getTokens();

        // syntax highlighting
        for ( int i = 0; i < tokens.length; i++ )
        {
            switch ( tokens[i].getType() )
            {
                case LdapFilterToken.LPAR:
                case LdapFilterToken.RPAR:
                    this.addStyleRange( presentation, tokens[i], PARENTHESIS_TEXT_ATTRIBUTE );
                    break;
                case LdapFilterToken.AND:
                case LdapFilterToken.OR:
                case LdapFilterToken.NOT:
                    this.addStyleRange( presentation, tokens[i], AND_OR_NOT_TEXT_ATTRIBUTE );
                    break;
                case LdapFilterToken.EQUAL:
                case LdapFilterToken.GREATER:
                case LdapFilterToken.LESS:
                case LdapFilterToken.APROX:
                case LdapFilterToken.PRESENT:
                case LdapFilterToken.SUBSTRING:
                case LdapFilterToken.EXTENSIBLE_DNATTR_COLON:
                case LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID_COLON:
                case LdapFilterToken.EXTENSIBLE_EQUALS_COLON:
                    this.addStyleRange( presentation, tokens[i], FILTER_TYPE_TEXT_ATTRIBUTE );
                    break;
                case LdapFilterToken.ATTRIBUTE:
                case LdapFilterToken.EXTENSIBLE_ATTRIBUTE:
                case LdapFilterToken.EXTENSIBLE_DNATTR:
                case LdapFilterToken.EXTENSIBLE_MATCHINGRULEOID:
                    this.addStyleRange( presentation, tokens[i], ATTRIBUTE_TEXT_ATTRIBUTE );
                    break;
                case LdapFilterToken.VALUE:
                    this.addStyleRange( presentation, tokens[i], VALUE_TEXT_ATTRIBUTE );
                    break;
                default:
                    this.addStyleRange( presentation, tokens[i], DEFAULT_TEXT_ATTRIBUTE );
            }
        }
    }


    /**
     * Adds the style range.
     * 
     * @param presentation the presentation
     * @param textAttribute the text attribute
     * @param token the token
     */
    private void addStyleRange( TextPresentation presentation, LdapFilterToken token, TextAttribute textAttribute )
    {
        if ( token.getLength() > 0 )
        {
            StyleRange range = new StyleRange( token.getOffset(), token.getLength(), textAttribute.getForeground(),
                textAttribute.getBackground(), textAttribute.getStyle() );
            presentation.addStyleRange( range );
        }
    }

}
