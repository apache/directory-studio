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


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.ldapstudio.browser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.model.filter.LdapFilter;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterParser;
import org.apache.directory.studio.ldapbrowser.core.model.filter.parser.LdapFilterToken;
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
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.swt.graphics.RGB;


/**
 * The FilterReconcilingStrategy is used to maintain the error annotations 
 * (red squirrels).
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class FilterReconcilingStrategy implements IReconcilingStrategy
{

    /** The source viewer. */
    private ISourceViewer sourceViewer;

    /** The filter parser. */
    private LdapFilterParser parser;

    /** The paint manager. */
    private PaintManager paintManager;


    /**
     * Creates a new instance of FilterReconcilingStrategy.
     * 
     * @param sourceViewer the source viewer
     * @param parser the filter parser
     */
    public FilterReconcilingStrategy( ISourceViewer sourceViewer, LdapFilterParser parser )
    {
        this.sourceViewer = sourceViewer;
        this.parser = parser;
        this.paintManager = null;
    }


    /**
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
     */
    public void setDocument( IDocument document )
    {
        if ( sourceViewer.getAnnotationModel() == null )
        {
            IAnnotationModel model = new AnnotationModel();
            sourceViewer.setDocument( sourceViewer.getDocument(), model );
        }

        // add annotation painter
        if ( paintManager == null && sourceViewer.getAnnotationModel() instanceof IAnnotationModelExtension )
        {
            AnnotationPainter ap = new AnnotationPainter( sourceViewer, null );
            ap.addAnnotationType( "DEFAULT" );
            ap.setAnnotationTypeColor( "DEFAULT", BrowserCommonActivator.getDefault().getColor( new RGB( 255, 0, 0 ) ) );
            sourceViewer.getAnnotationModel().addAnnotationModelListener( ap );

            FilterCharacterPairMatcher cpm = new FilterCharacterPairMatcher( sourceViewer, parser );
            MatchingCharacterPainter mcp = new MatchingCharacterPainter( sourceViewer, cpm );
            mcp.setColor( BrowserCommonActivator.getDefault().getColor( new RGB( 159, 159, 159 ) ) );

            paintManager = new PaintManager( sourceViewer );
            paintManager.addPainter( ap );
            paintManager.addPainter( mcp );
        }
    }


    /**
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion, org.eclipse.jface.text.IRegion)
     */
    public void reconcile( DirtyRegion dirtyRegion, IRegion subRegion )
    {
        reconcile( dirtyRegion );
    }


    /**
     * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
     */
    public void reconcile( IRegion partition )
    {

        LdapFilterToken[] tokens = parser.getModel().getTokens();

        // annotations
        if ( sourceViewer.getAnnotationModel() instanceof IAnnotationModelExtension )
        {
            ( ( IAnnotationModelExtension ) sourceViewer.getAnnotationModel() ).removeAllAnnotations();

            List<Position> positionList = new ArrayList<Position>();

            LdapFilter[] invalidFilters = parser.getModel().getInvalidFilters();
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
                    sourceViewer.getAnnotationModel().addAnnotation( annotation, position );
                }
            }

            for ( int i = 0; i < tokens.length; i++ )
            {
                if ( tokens[i].getType() == LdapFilterToken.ERROR )
                {

                    boolean overlaps = false;
                    for ( int k = 0; k < positionList.size(); k++ )
                    {
                        Position pos = positionList.get( k );
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
                        sourceViewer.getAnnotationModel().addAnnotation( annotation, position );
                    }
                }
            }
        }
    }

}
