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

package org.apache.directory.ldapstudio.ldifeditor.editor;


import org.apache.directory.ldapstudio.ldifeditor.LdifEditorConstants;
import org.apache.directory.ldapstudio.ldifeditor.LdifEditorActivator;
import org.apache.directory.ldapstudio.ldifeditor.editor.reconciler.LdifReconcilingStrategy;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifAnnotationHover;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifAutoEditStrategy;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifCompletionProcessor;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifDamagerRepairer;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifDoubleClickStrategy;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifPartitionScanner;
import org.apache.directory.ldapstudio.ldifeditor.editor.text.LdifTextHover;
import org.apache.directory.ldapstudio.browser.common.widgets.DialogContentAssistant;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

/**
 * This class enables the features of the editor (Syntax coloring, code completion, etc.)
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifSourceViewerConfiguration extends SourceViewerConfiguration
{
    private ILdifEditor editor;

    // Error hover and annotations
    private LdifAnnotationHover annotationHover;

    private LdifTextHover textHover;

    // Presentation Reconciler (syntax highlight)
    private PresentationReconciler presentationReconciler;

    private LdifDamagerRepairer damagerRepairer;

    // Content Assistent
    private boolean contentAssistEnabled;

    private ContentAssistant contentAssistant;

    private IContentAssistProcessor contentAssistProcessor;

    private LdifDoubleClickStrategy doubleClickStrategy;

    // Asynchronous Reconciler (annotations)
    private MonoReconciler reconciler;

    private LdifReconcilingStrategy reconcilingStrategy;

    private IAutoEditStrategy[] autoEditStrategies;


    /**
     * Creates a new instance of LdifSourceViewerConfiguration.
     *
     * @param editor
     * @param contentAssistEnabled
     */
    public LdifSourceViewerConfiguration( ILdifEditor editor, boolean contentAssistEnabled )
    {
        super();
        this.editor = editor;

        this.contentAssistEnabled = contentAssistEnabled;
    }


    /**
     * Overwrites the style set in preference store
     *
     * @param key
     *      the key
     * @param rgb
     *      the color
     * @param style
     *      the stule
     */
    public void setTextAttribute( String key, RGB rgb, int style )
    {
        damagerRepairer.setTextAttribute( key, rgb, style );
    }


    /**
     * {@inheritDoc}
     */
    public String getConfiguredDocumentPartitioning( ISourceViewer sourceViewer )
    {
        return LdifDocumentSetupParticipant.LDIF_PARTITIONING;
    }


    /**
     * {@inheritDoc}
     */
    public String[] getConfiguredContentTypes( ISourceViewer sourceViewer )
    {
        return new String[]
            { IDocument.DEFAULT_CONTENT_TYPE, LdifPartitionScanner.LDIF_RECORD };
    }


    /**
     * {@inheritDoc}
     */
    public ITextDoubleClickStrategy getDoubleClickStrategy( ISourceViewer sourceViewer, String contentType )
    {
        if ( this.doubleClickStrategy == null )
        {
            this.doubleClickStrategy = new LdifDoubleClickStrategy();
        }
        return this.doubleClickStrategy;
    }


    /**
     * {@inheritDoc}
     */
    public IPresentationReconciler getPresentationReconciler( ISourceViewer sourceViewer )
    {

        if ( this.presentationReconciler == null )
        {
            this.presentationReconciler = new PresentationReconciler();
            this.presentationReconciler.setDocumentPartitioning( getConfiguredDocumentPartitioning( sourceViewer ) );

            damagerRepairer = new LdifDamagerRepairer( this.editor );

            this.presentationReconciler.setDamager( damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE );
            this.presentationReconciler.setRepairer( damagerRepairer, IDocument.DEFAULT_CONTENT_TYPE );

            this.presentationReconciler.setDamager( damagerRepairer, LdifPartitionScanner.LDIF_RECORD );
            this.presentationReconciler.setRepairer( damagerRepairer, LdifPartitionScanner.LDIF_RECORD );
        }

        return this.presentationReconciler;
    }


    /**
     * {@inheritDoc}
     */
    public IReconciler getReconciler( ISourceViewer sourceViewer )
    {
        if ( this.reconciler == null )
        {
            this.reconcilingStrategy = new LdifReconcilingStrategy( editor );

            // Reconciler reconciler = new Reconciler();
            // reconciler.setIsIncrementalReconciler(true);
            // reconciler.setReconcilingStrategy(strategy,
            // LdifPartitionScanner.LDIF_RECORD);
            // reconciler.setReconcilingStrategy(strategy,
            // IDocument.DEFAULT_CONTENT_TYPE);
            // reconciler.setProgressMonitor(new NullProgressMonitor());
            // reconciler.setDelay(500);
            // return reconciler;

            this.reconciler = new MonoReconciler( this.reconcilingStrategy, true );
            this.reconciler.setProgressMonitor( new NullProgressMonitor() );
            this.reconciler.setDelay( 500 );
        }

        return this.reconciler;
    }


    /**
     * {@inheritDoc}
     */
    public IContentAssistant getContentAssistant( ISourceViewer sourceViewer )
    {
        if ( this.contentAssistEnabled )
        {
            if ( this.contentAssistant == null )
            {
                // this.contentAssistant = new ContentAssistant();
                this.contentAssistant = new DialogContentAssistant();

                this.contentAssistProcessor = new LdifCompletionProcessor( editor, contentAssistant );
                this.contentAssistant.setContentAssistProcessor( this.contentAssistProcessor,
                    LdifPartitionScanner.LDIF_RECORD );
                this.contentAssistant.setContentAssistProcessor( this.contentAssistProcessor,
                    IDocument.DEFAULT_CONTENT_TYPE );
                this.contentAssistant.setDocumentPartitioning( LdifDocumentSetupParticipant.LDIF_PARTITIONING );

                this.contentAssistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );
                this.contentAssistant.setInformationControlCreator( getInformationControlCreator( sourceViewer ) );

                IPreferenceStore store = LdifEditorActivator.getDefault().getPreferenceStore();
                this.contentAssistant.enableAutoInsert( store
                    .getBoolean( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_INSERTSINGLEPROPOSALAUTO ) );
                this.contentAssistant.enableAutoActivation( store
                    .getBoolean( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_ENABLEAUTOACTIVATION ) );
                this.contentAssistant.setAutoActivationDelay( store
                    .getInt( LdifEditorConstants.PREFERENCE_LDIFEDITOR_CONTENTASSIST_AUTOACTIVATIONDELAY ) );
                // this.contentAssistant.enableAutoInsert(true);
                // this.contentAssistant.enableAutoActivation(true);
                // this.contentAssistant.setAutoActivationDelay(100);
            }
            return this.contentAssistant;
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public IAnnotationHover getAnnotationHover( ISourceViewer sourceViewer )
    {
        if ( this.annotationHover == null )
        {
            this.annotationHover = new LdifAnnotationHover( this.editor );
        }
        return this.annotationHover;
    }


    /**
     * {@inheritDoc}
     */
    public ITextHover getTextHover( ISourceViewer sourceViewer, String contentType )
    {
        if ( this.textHover == null )
        {
            this.textHover = new LdifTextHover( this.editor );
        }
        return this.textHover;
    }


    /**
     * {@inheritDoc}
     */
    public IAutoEditStrategy[] getAutoEditStrategies( ISourceViewer sourceViewer, String contentType )
    {
        if ( autoEditStrategies == null )
        {
            this.autoEditStrategies = new IAutoEditStrategy[2];
            this.autoEditStrategies[0] = new DefaultIndentLineAutoEditStrategy();
            this.autoEditStrategies[1] = new LdifAutoEditStrategy( this.editor );
        }

        return autoEditStrategies;
    }
}
