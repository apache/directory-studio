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

import org.eclipse.jface.text.IAutoIndentStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


// TODO: Refactor Filter Editor
public class FilterSourceViewerConfiguration extends SourceViewerConfiguration
{

    private IConnection connection;

    private LdapFilterParser parser;

    private SourceViewer sourceViewer;

    // Presentation Reconciler (syntax highlight)
    private PresentationReconciler presentationReconciler;

    private FilterDamagerRepairer damagerRepairer;

    // Asynchronous Reconciler (annotations)
    private MonoReconciler reconciler;

    private FilterReconcilingStrategy reconcilingStrategy;

    // Hover
    private FilterTextHover textHover;

    // Auto Edit Strategy
    private FilterAutoEditStrategy autoEditStrategy;

    private ContentFormatter formatter;

    private FilterFormattingStrategy formattingStrategy;

    // Content Assistent
    private DialogContentAssistant contentAssistant;

    private FilterContentAssistProcessor contentAssistProcessor;


    public FilterSourceViewerConfiguration( SourceViewer sourceViewer, LdapFilterParser parser, IConnection connection )
    {
        super();
        this.sourceViewer = sourceViewer;
        this.parser = parser;
        this.connection = connection;
    }


    public void setConnection( IConnection connection )
    {
        this.connection = connection;
        this.contentAssistProcessor.setPossibleAttributeTypes( this.connection == null ? new String[0]
            : this.connection.getSchema().getAttributeTypeDescriptionNames() );
    }


    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer )
    {
        if ( this.damagerRepairer == null )
        {
            this.damagerRepairer = new FilterDamagerRepairer( this.sourceViewer, this.parser );
        }
        if ( this.presentationReconciler == null )
        {
            this.presentationReconciler = new PresentationReconciler();
            this.presentationReconciler.setDamager( this.damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE );
            this.presentationReconciler.setRepairer( this.damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE );
        }
        return this.presentationReconciler;
    }


    public ITextHover getTextHover( ISourceViewer sourceViewer, String contentType )
    {
        if ( this.textHover == null )
        {
            this.textHover = new FilterTextHover( this.sourceViewer, this.parser );
        }
        return this.textHover;
    }


    public IReconciler getReconciler( ISourceViewer sourceViewer )
    {
        if ( this.reconcilingStrategy == null )
        {
            this.reconcilingStrategy = new FilterReconcilingStrategy( this.sourceViewer, this.parser );
        }
        if ( this.reconciler == null )
        {
            this.reconciler = new MonoReconciler( this.reconcilingStrategy, false );
        }
        return this.reconciler;
    }


    public IAutoIndentStrategy getAutoIndentStrategy( ISourceViewer sourceViewer, String contentType )
    {
        if ( this.autoEditStrategy == null )
        {
            this.autoEditStrategy = new FilterAutoEditStrategy( this.sourceViewer, this.parser );
        }
        return this.autoEditStrategy;
    }


    public IContentFormatter getContentFormatter( ISourceViewer sourceViewer )
    {
        if ( this.formattingStrategy == null )
        {
            this.formattingStrategy = new FilterFormattingStrategy( this.sourceViewer, this.parser );
        }
        if ( this.formatter == null )
        {
            this.formatter = new ContentFormatter();
            this.formatter.enablePartitionAwareFormatting( false );
            this.formatter.setFormattingStrategy( this.formattingStrategy, IDocument.DEFAULT_CONTENT_TYPE );
        }
        return formatter;
    }


    public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
    {

        if ( this.contentAssistProcessor == null )
        {
            this.contentAssistProcessor = new FilterContentAssistProcessor( this.sourceViewer, this.parser );
            this.contentAssistProcessor.setPossibleAttributeTypes( this.connection == null ? new String[0]
                : this.connection.getSchema().getAttributeTypeDescriptionNames() );
        }
        if ( this.contentAssistant == null )
        {
            this.contentAssistant = new DialogContentAssistant();
            this.contentAssistant.enableAutoInsert( true );
            this.contentAssistant.setContentAssistProcessor( this.contentAssistProcessor,
                IDocument.DEFAULT_CONTENT_TYPE );
            this.contentAssistant.enableAutoActivation( true );
            this.contentAssistant.setAutoActivationDelay( 100 );
        }
        return this.contentAssistant;

    }

}
