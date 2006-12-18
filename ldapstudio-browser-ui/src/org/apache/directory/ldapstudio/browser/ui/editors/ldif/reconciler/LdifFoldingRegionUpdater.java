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

package org.apache.directory.ldapstudio.browser.ui.editors.ldif.reconciler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifFile;
import org.apache.directory.ldapstudio.browser.core.model.ldif.LdifPart;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifCommentContainer;
import org.apache.directory.ldapstudio.browser.core.model.ldif.container.LdifContainer;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifNonEmptyLineBase;
import org.apache.directory.ldapstudio.browser.core.model.ldif.lines.LdifSepLine;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.editors.ldif.ILdifEditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;


public class LdifFoldingRegionUpdater implements IPropertyChangeListener
{

    private ILdifEditor editor;


    public LdifFoldingRegionUpdater( ILdifEditor editor )
    {
        this.editor = editor;

        BrowserUIPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
    }


    public void dispose()
    {
        BrowserUIPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
    }


    public void propertyChange( PropertyChangeEvent event )
    {
        if ( BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE.equals( event.getProperty() )
            || BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS.equals( event.getProperty() )
            || BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS.equals( event.getProperty() )
            || BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES.equals( event.getProperty() ) )
        {
            this.updateFoldingRegions();
        }
    }


    public void updateFoldingRegions()
    {

        ISourceViewer viewer = ( ISourceViewer ) editor.getAdapter( ISourceViewer.class );
        if ( viewer == null )
            return;

        IDocument document = viewer.getDocument();

        try
        {
            ProjectionAnnotationModel projectionAnnotationModel = ( ProjectionAnnotationModel ) editor
                .getAdapter( ProjectionAnnotationModel.class );
            if ( projectionAnnotationModel == null )
                return;

            // create folding regions of current LDIF model; mark comments
            // and
            // folded lines as collapsed
            Map positionToAnnotationMap = createFoldingRegions( editor.getLdifModel(), document );

            // compare with current annotation model (--> toAdd, toDelete)
            List annotationsToDeleteList = new ArrayList();
            Map annotationsToAddMap = new HashMap();
            this.computeDifferences( projectionAnnotationModel, positionToAnnotationMap, annotationsToDeleteList,
                annotationsToAddMap );
            Annotation[] annotationsToDelete = ( Annotation[] ) annotationsToDeleteList
                .toArray( new Annotation[annotationsToDeleteList.size()] );

            // update annotation model
            if ( !annotationsToDeleteList.isEmpty() || !annotationsToAddMap.isEmpty() )
            {
                projectionAnnotationModel.modifyAnnotations( annotationsToDelete, annotationsToAddMap,
                    new Annotation[0] );
            }

        }
        catch ( BadLocationException e )
        {
            e.printStackTrace();
        }
    }


    private void computeDifferences( ProjectionAnnotationModel model, Map positionToAnnotationMap,
        List annotationsToDeleteList, Map annotationsToAddMap )
    {

        for ( Iterator iter = model.getAnnotationIterator(); iter.hasNext(); )
        {
            Object annotation = iter.next();
            if ( annotation instanceof ProjectionAnnotation )
            {
                Position position = model.getPosition( ( Annotation ) annotation );
                if ( positionToAnnotationMap.containsKey( position ) )
                    positionToAnnotationMap.remove( position );
                else
                    annotationsToDeleteList.add( annotation );
            }
        }

        for ( Iterator iter = positionToAnnotationMap.keySet().iterator(); iter.hasNext(); )
        {
            Position position = ( Position ) iter.next();
            ProjectionAnnotation annotation = ( ProjectionAnnotation ) positionToAnnotationMap.get( position );
            annotationsToAddMap.put( annotation, position );
        }
    }


    /**
     * Creates all folding region of the given LDIF model.
     * LdifCommentContainers and wrapped lines are marked as collapsed.
     * 
     * @param model
     * @param document
     * @return a map with positions as keys to annotations as values
     * @throws BadLocationException
     */
    private Map createFoldingRegions( LdifFile model, IDocument document ) throws BadLocationException
    {
        Map positionToAnnotationMap = new HashMap();
        LdifContainer[] containers = model.getContainers();

        boolean ENABLE_FOLDING = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_ENABLE );
        boolean FOLD_COMMENTS = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDCOMMENTS );
        boolean FOLD_RECORDS = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDRECORDS );
        boolean FOLD_WRAPPEDLINES = BrowserUIPlugin.getDefault().getPreferenceStore().getBoolean(
            BrowserUIConstants.PREFERENCE_LDIFEDITOR_FOLDING_INITIALLYFOLDWRAPPEDLINES );

        if ( ENABLE_FOLDING )
        {
            for ( int i = 0; i < containers.length; i++ )
            {
                LdifContainer container = containers[i];
                int containerStartLine = document.getLineOfOffset( container.getOffset() );
                int containerEndLine = -1;
                LdifPart[] parts = container.getParts();
                for ( int j = parts.length - 1; j >= 0; j-- )
                {
                    if ( containerEndLine == -1
                        && ( !( parts[j] instanceof LdifSepLine ) || ( container instanceof LdifCommentContainer && j < parts.length - 1 ) ) )
                    {
                        containerEndLine = document.getLineOfOffset( parts[j].getOffset() + parts[j].getLength() - 1 );
                        // break;
                    }
                    if ( parts[j] instanceof LdifNonEmptyLineBase )
                    {
                        LdifNonEmptyLineBase line = ( LdifNonEmptyLineBase ) parts[j];
                        if ( line.isFolded() )
                        {
                            Position position = new Position( line.getOffset(), line.getLength() );
                            // ProjectionAnnotation annotation = new
                            // ProjectionAnnotation(true);
                            ProjectionAnnotation annotation = new ProjectionAnnotation( FOLD_WRAPPEDLINES );
                            positionToAnnotationMap.put( position, annotation );
                        }
                    }
                }

                if ( containerStartLine < containerEndLine )
                {
                    int start = document.getLineOffset( containerStartLine );
                    int end = document.getLineOffset( containerEndLine ) + document.getLineLength( containerEndLine );
                    Position position = new Position( start, end - start );
                    // ProjectionAnnotation annotation = new
                    // ProjectionAnnotation(container instanceof
                    // LdifCommentContainer);
                    ProjectionAnnotation annotation = new ProjectionAnnotation( FOLD_RECORDS
                        || ( FOLD_COMMENTS && container instanceof LdifCommentContainer ) );
                    positionToAnnotationMap.put( position, annotation );
                }
            }
        }

        return positionToAnnotationMap;
    }

}
