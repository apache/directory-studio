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

package org.apache.directory.studio.ldifeditor.editor.reconciler;


import org.apache.directory.studio.ldifeditor.editor.ILdifEditor;
import org.apache.directory.studio.ldifeditor.editor.LdifOutlinePage;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


public class LdifReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension
{

    private ILdifEditor editor;

    // private IDocument document;
    // private IProgressMonitor progressMonitor;

    private LdifFoldingRegionUpdater foldingUpdater;

    private LdifAnnotationUpdater annotationUpdater;


    public LdifReconcilingStrategy( ILdifEditor editor )
    {
        this.editor = editor;

        this.annotationUpdater = new LdifAnnotationUpdater( this.editor );
        this.foldingUpdater = new LdifFoldingRegionUpdater( this.editor );

    }


    public void dispose()
    {
        this.annotationUpdater.dispose();
        this.foldingUpdater.dispose();
    }


    public void setDocument( IDocument document )
    {
        // this.document = document;
    }


    public void setProgressMonitor( IProgressMonitor monitor )
    {
        // this.progressMonitor = monitor;
    }


    public void reconcile( DirtyRegion dirtyRegion, IRegion subRegion )
    {
        reconcile();
    }


    public void reconcile( IRegion partition )
    {
        reconcile();
    }


    public void initialReconcile()
    {
        reconcile();
    }


    private void reconcile()
    {
        notifyEnvironment();
    }


    private void notifyEnvironment()
    {

        Display.getDefault().asyncExec( new Runnable()
        {
            public void run()
            {

                // notify outline
                IContentOutlinePage outline = ( IContentOutlinePage ) editor.getAdapter( IContentOutlinePage.class );
                if ( outline != null && outline instanceof LdifOutlinePage )
                {
                    ( ( LdifOutlinePage ) outline ).refresh();
                }

                // notify annotation updater
                annotationUpdater.updateAnnotations();

                // notify folding updater
                foldingUpdater.updateFoldingRegions();

            }
        } );
    }

}
