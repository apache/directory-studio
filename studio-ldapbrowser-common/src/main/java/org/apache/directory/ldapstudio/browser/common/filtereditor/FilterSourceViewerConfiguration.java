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


import org.apache.directory.ldapstudio.browser.common.widgets.DialogContentAssistant;
import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;


/**
 * The FilterSourceViewerConfiguration implements the configuration of
 * the source viewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterSourceViewerConfiguration extends SourceViewerConfiguration
{

    /** The current connection, used to retrieve schema information. */
    private IConnection connection;

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The presentation reconciler, used for syntax highlighting. */
    private PresentationReconciler presentationReconciler;

    /** The damager repairer, used for syntax highlighting. */
    private FilterDamagerRepairer damagerRepairer;

    /** The reconciler, used to maintain error annotations. */
    private MonoReconciler reconciler;

    /** The reconciling strategy, used to maintain error annotations. */
    private FilterReconcilingStrategy reconcilingStrategy;

    /** The text hover, used to display error message tooltips. */
    private FilterTextHover textHover;

    /** The auto edit strategy, used for smart parentesis handling. */
    private FilterAutoEditStrategy[] autoEditStrategies;

    /** The formatter, used to format the filter. */
    private ContentFormatter formatter;

    /** The formatting strategy, used to format the filter. */
    private FilterFormattingStrategy formattingStrategy;

    /** The content assistant, used for content proposals. */
    private DialogContentAssistant contentAssistant;

    /** The content assist processor, used for content proposals. */
    private FilterContentAssistProcessor contentAssistProcessor;


    /**
     * Creates a new instance of FilterSourceViewerConfiguration.
     * 
     * @param parser the filer parser
     * @param connection the connection
     */
    public FilterSourceViewerConfiguration( LdapFilterParser parser, IConnection connection )
    {
        this.parser = parser;
        this.connection = connection;
    }


    /**
     * Sets the connection.
     * 
     * @param connection the connection
     */
    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        contentAssistProcessor.setSchema( connection == null ? null : connection.getSchema() );
        textHover.setSchema( connection == null ? null : connection.getSchema() );
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer )
    {
        if ( damagerRepairer == null )
        {
            damagerRepairer = new FilterDamagerRepairer( parser );
        }
        if ( presentationReconciler == null )
        {
            presentationReconciler = new PresentationReconciler();
            presentationReconciler.setDamager( damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE );
            presentationReconciler.setRepairer( damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE );
        }
        return presentationReconciler;
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    public ITextHover getTextHover( ISourceViewer sourceViewer, String contentType )
    {
        if ( textHover == null )
        {
            textHover = new FilterTextHover( parser );
            textHover.setSchema( connection == null ? null : connection.getSchema() );
        }
        return textHover;
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IReconciler getReconciler( ISourceViewer sourceViewer )
    {
        if ( reconcilingStrategy == null )
        {
            reconcilingStrategy = new FilterReconcilingStrategy( sourceViewer, parser );
        }
        if ( reconciler == null )
        {
            reconciler = new MonoReconciler( reconcilingStrategy, false );
        }
        return reconciler;
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
     */
    public IAutoEditStrategy[] getAutoEditStrategies( ISourceViewer sourceViewer, String contentType )
    {
        if ( autoEditStrategies == null )
        {
            autoEditStrategies = new FilterAutoEditStrategy[]
                { new FilterAutoEditStrategy( parser ) };
        }
        return autoEditStrategies;
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentFormatter(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IContentFormatter getContentFormatter( ISourceViewer sourceViewer )
    {
        if ( formattingStrategy == null )
        {
            formattingStrategy = new FilterFormattingStrategy( sourceViewer, parser );
        }
        if ( formatter == null )
        {
            formatter = new ContentFormatter();
            formatter.enablePartitionAwareFormatting( false );
            formatter.setFormattingStrategy( formattingStrategy, IDocument.DEFAULT_CONTENT_TYPE );
        }
        return formatter;
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
    {
        if ( contentAssistProcessor == null )
        {
            contentAssistProcessor = new FilterContentAssistProcessor( sourceViewer, parser );
            contentAssistProcessor.setSchema( connection == null ? null : connection.getSchema() );
        }
        if ( contentAssistant == null )
        {
            contentAssistant = new DialogContentAssistant();
            contentAssistant.enableAutoInsert( true );
            contentAssistant.setContentAssistProcessor( contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
            contentAssistant.enableAutoActivation( true );
            contentAssistant.setAutoActivationDelay( 100 );

            contentAssistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );
            contentAssistant.setInformationControlCreator( getInformationControlCreator( sourceViewer ) );

        }
        return contentAssistant;
    }


    /**
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getInformationControlCreator(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IInformationControlCreator getInformationControlCreator( ISourceViewer sourceViewer )
    {
        return new IInformationControlCreator()
        {
            public IInformationControl createInformationControl( Shell parent )
            {
                return new DefaultInformationControl( parent, SWT.WRAP, null );
            }
        };
    }
}
