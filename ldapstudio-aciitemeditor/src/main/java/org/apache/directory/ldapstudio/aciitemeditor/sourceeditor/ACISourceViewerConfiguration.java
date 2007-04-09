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
package org.apache.directory.ldapstudio.aciitemeditor.sourceeditor;


import org.apache.directory.ldapstudio.aciitemeditor.Activator;
import org.eclipse.jface.text.IAutoEditStrategy;
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


/**
 * This class enables the features of the editor (Syntax coloring, code completion, etc.)
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ACISourceViewerConfiguration extends SourceViewerConfiguration
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer )
    {
        PresentationReconciler reconciler = new PresentationReconciler();
        reconciler.setDocumentPartitioning( getConfiguredDocumentPartitioning( sourceViewer ) );

        // Creating the damager/repairer for code
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer( Activator.getDefault().getAciCodeScanner() );
        reconciler.setDamager( dr, IDocument.DEFAULT_CONTENT_TYPE );
        reconciler.setRepairer( dr, IDocument.DEFAULT_CONTENT_TYPE );

        return reconciler;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
    {
        //        ContentAssistant assistant = new ContentAssistant();
        ContentAssistant assistant = new DialogContentAssistant();
        IContentAssistProcessor aciContentAssistProcessor = new ACIContentAssistProcessor();

        assistant.setContentAssistProcessor( aciContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE );
        assistant.setDocumentPartitioning( "org.apache.directory.ldapstudio.aci" ); //$NON-NLS-1$
        assistant.enableAutoActivation( true );
        assistant.setAutoActivationDelay( 500 );
        assistant.setProposalPopupOrientation( IContentAssistant.PROPOSAL_STACKED );
        assistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );

        return assistant;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentFormatter(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IContentFormatter getContentFormatter( ISourceViewer sourceViewer )
    {
        ContentFormatter formatter = new ContentFormatter();
        IFormattingStrategy formattingStrategy = new ACIFormattingStrategy( sourceViewer );
        formatter.enablePartitionAwareFormatting( false );
        formatter.setFormattingStrategy( formattingStrategy, IDocument.DEFAULT_CONTENT_TYPE );
        return formatter;
    }
    
}
