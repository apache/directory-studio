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
package org.apache.directory.studio.openldap.config.acl.sourceeditor;


import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.ContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import org.apache.directory.studio.openldap.config.acl.OpenLdapAclEditorPlugin;


/**
 * This class enables the features of the editor (Syntax coloring, code completion, etc.)
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class OpenLdapAclSourceViewerConfiguration extends SourceViewerConfiguration
{
    /**
     * {@inheritDoc}
     */
    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer )
    {
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning( getConfiguredDocumentPartitioning( sourceViewer ) );

        // Creating the damager/repairer for code
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer( OpenLdapAclEditorPlugin.getDefault()
            .getCodeScanner() );
        reconciler.setDamager( dr, IDocument.DEFAULT_CONTENT_TYPE );
        reconciler.setRepairer( dr, IDocument.DEFAULT_CONTENT_TYPE );

        return reconciler;
    }


    /**
     * {@inheritDoc}
     */
    public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
    {
        //        ContentAssistant assistant = new ContentAssistant();
        ContentAssistant assistant = new DialogContentAssistant();
        IContentAssistProcessor aciContentAssistProcessor = new OpenLdapContentAssistProcessor();

        assistant.setContentAssistProcessor( aciContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
        assistant.enableAutoActivation( true );
        assistant.setAutoActivationDelay( 500 );
        assistant.setProposalPopupOrientation( IContentAssistant.PROPOSAL_STACKED );
        assistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );

        return assistant;
    }


    /**
     * {@inheritDoc}
     */
    public IContentFormatter getContentFormatter( ISourceViewer sourceViewer )
    {
        ContentFormatter formatter = new ContentFormatter();
        IFormattingStrategy formattingStrategy = new OpenLdapAclFormattingStrategy( sourceViewer );
        formatter.enablePartitionAwareFormatting( false );
        formatter.setFormattingStrategy( formattingStrategy, IDocument.DEFAULT_CONTENT_TYPE );
        
        return formatter;
    }
}
