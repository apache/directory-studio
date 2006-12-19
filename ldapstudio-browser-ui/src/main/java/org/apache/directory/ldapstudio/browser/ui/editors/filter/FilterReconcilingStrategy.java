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

package org.apache.directory.ldapstudio.browser.ui.editors.filter;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.core.model.filter.LdapFilter;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.ldapstudio.browser.core.model.filter.parser.LdapFilterToken;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.PaintManager;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.RGB;


public class FilterReconcilingStrategy implements IReconcilingStrategy
{

    private SourceViewer sourceViewer;

    private LdapFilterParser parser;

    private IDocument document;

    private PaintManager paintManager;


    public FilterReconcilingStrategy( SourceViewer sourceViewer, LdapFilterParser parser )
    {
        super();
        this.sourceViewer = sourceViewer;
        this.parser = parser;
        this.document = null;
        this.paintManager = null;
    }


    public void setDocument( IDocument document )
    {
        this.document = document;

        if ( this.sourceViewer.getAnnotationModel() == null )
        {
            IAnnotationModel model = new AnnotationModel();
            this.sourceViewer.setDocument( this.sourceViewer.getDocument(), model );
        }

        // add annotation painter
        if ( this.paintManager == null && this.sourceViewer.getAnnotationModel() instanceof IAnnotationModelExtension )
        {
            AnnotationPainter ap = new AnnotationPainter( this.sourceViewer, null );
            ap.addAnnotationType( "DEFAULT" );
            ap.setAnnotationTypeColor( "DEFAULT", BrowserUIPlugin.getDefault().getColor( new RGB( 255, 0, 0 ) ) );
            this.sourceViewer.getAnnotationModel().addAnnotationModelListener( ap );

            FilterCharacterPairMatcher cpm = new FilterCharacterPairMatcher( this.sourceViewer, this.parser );
            MatchingCharacterPainter mcp = new MatchingCharacterPainter( this.sourceViewer, cpm );
            mcp.setColor( BrowserUIPlugin.getDefault().getColor( new RGB( 159, 159, 159 ) ) );

            this.paintManager = new PaintManager( this.sourceViewer );
            this.paintManager.addPainter( ap );
            this.paintManager.addPainter( mcp );
        }

    }


    public void reconcile( DirtyRegion dirtyRegion, IRegion subRegion )
    {
        this.reconcile( dirtyRegion );
    }


    public void reconcile( IRegion partition )
    {

        /*
         * Display.getDefault().syncExec(new Runnable(){ public void run() {
         * if(sourceViewer.canDoOperation(SourceViewer.FORMAT)) {
         * sourceViewer.doOperation(SourceViewer.FORMAT); } } });
         */

        LdapFilterToken[] tokens = this.parser.getModel().getTokens();

        // annotations
        if ( this.sourceViewer.getAnnotationModel() instanceof IAnnotationModelExtension )
        {
            ( ( IAnnotationModelExtension ) this.sourceViewer.getAnnotationModel() ).removeAllAnnotations();

            List positionList = new ArrayList();

            LdapFilter[] invalidFilters = this.parser.getModel().getInvalidFilters();
            for ( int i = 0; i < invalidFilters.length; i++ )
            {
                if ( invalidFilters[i].getStartToken() != null )
                {
                    int start = invalidFilters[i].getStartToken().getOffset();
                    int stop = invalidFilters[i].getStopToken() != null ? invalidFilters[i].getStopToken().getOffset()
                        + invalidFilters[i].getStopToken().getLength() : start
                        + invalidFilters[i].getStartToken().getLength();

                    Annotation annotation = new Annotation( "DEFAULT", true, invalidFilters[i].toString() );
                    Position position = new Position( start, stop - start );
                    positionList.add( position );
                    this.sourceViewer.getAnnotationModel().addAnnotation( annotation, position );
                }
            }

            for ( int i = 0; i < tokens.length; i++ )
            {
                if ( tokens[i].getType() == LdapFilterToken.ERROR )
                {

                    boolean overlaps = false;
                    for ( int k = 0; k < positionList.size(); k++ )
                    {
                        Position pos = ( Position ) positionList.get( k );
                        if ( pos.overlapsWith( tokens[i].getOffset(), tokens[i].getLength() ) )
                        {
                            overlaps = true;
                            break;
                        }
                    }
                    if ( !overlaps )
                    {
                        Annotation annotation = new Annotation( "DEFAULT", true, tokens[i].getValue() );
                        Position position = new Position( tokens[i].getOffset(), tokens[i].getLength() );
                        this.sourceViewer.getAnnotationModel().addAnnotation( annotation, position );
                    }
                }
            }
        }

    }

}
