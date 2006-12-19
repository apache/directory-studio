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

package org.apache.directory.ldapstudio.browser.ui.widgets.ldifeditor;


import org.apache.directory.ldapstudio.browser.core.model.IConnection;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.ILdifEditor;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifDocumentProvider;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.LdifSourceViewerConfiguration;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.NonExistingLdifEditorInput;
import org.apache.directory.ldapstudio.browser.ui.widgets.BrowserWidget;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class LdifEditorWidget extends BrowserWidget implements ILdifEditor, ITextListener
{

    private IConnection connection;

    private String initialLdif;

    private boolean contentAssistEnabled;

    private NonExistingLdifEditorInput editorInput;

    private LdifDocumentProvider documentProvider;

    private SourceViewer sourceViewer;

    private LdifSourceViewerConfiguration sourceViewerConfiguration;


    public LdifEditorWidget( IConnection connection, String ldif, boolean contentAssistEnabled )
    {
        this.connection = connection;
        this.initialLdif = ldif;
        this.contentAssistEnabled = contentAssistEnabled;
    }


    public void dispose()
    {
        if ( editorInput != null )
        {
            sourceViewer.removeTextListener( this );
            documentProvider.disconnect( editorInput );
            // documentProvider = null;
            editorInput = null;
        }
    }


    public void createWidget( Composite parent )
    {

        Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayoutData( new GridData( GridData.FILL_BOTH ) );
        GridLayout layout = new GridLayout( 1, false );
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout( layout );

        // create source viewer
        // sourceViewer = new ProjectionViewer(parent, ruler,
        // getOverviewRuler(), true, styles);
        sourceViewer = new SourceViewer( composite, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
        sourceViewer.getControl().setLayoutData( new GridData( GridData.FILL_BOTH ) );

        // configure
        sourceViewerConfiguration = new LdifSourceViewerConfiguration( this, this.contentAssistEnabled );
        sourceViewer.configure( sourceViewerConfiguration );

        // set font
        Font font = JFaceResources.getFont( JFaceResources.TEXT_FONT );
        sourceViewer.getTextWidget().setFont( font );

        // setup document
        try
        {
            editorInput = new NonExistingLdifEditorInput();
            documentProvider = new LdifDocumentProvider();
            documentProvider.connect( editorInput );

            IDocument document = documentProvider.getDocument( editorInput );
            document.set( initialLdif );
            sourceViewer.setDocument( document );
        }
        catch ( CoreException e )
        {
            e.printStackTrace();
        }

        // listener
        sourceViewer.addTextListener( this );

        // focus
        sourceViewer.getControl().setFocus();

    }


    public IConnection getConnection()
    {
        return connection;
    }


    public LdifFile getLdifModel()
    {
        return documentProvider.getLdifModel();
    }


    public Object getAdapter( Class adapter )
    {
        return null;
    }


    public void textChanged( TextEvent event )
    {
        super.notifyListeners();
    }


    public SourceViewer getSourceViewer()
    {
        return sourceViewer;
    }


    public LdifSourceViewerConfiguration getSourceViewerConfiguration()
    {
        return sourceViewerConfiguration;
    }

}
