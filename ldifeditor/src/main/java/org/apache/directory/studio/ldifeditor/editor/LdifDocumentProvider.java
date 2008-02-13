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

package org.apache.directory.studio.ldifeditor.editor;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldifeditor.LdifEditorActivator;
import org.apache.directory.studio.ldifeditor.editor.text.LdifExternalAnnotationModel;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.parser.LdifParser;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;


/**
 * This class implements the LDIF Document Provider.
 * This class is used to share a LDIF Document and listen on document modifications.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifDocumentProvider extends AbstractDocumentProvider implements IDocumentListener
{

    private final LdifParser ldifParser;

    private final LdifDocumentSetupParticipant ldifDocumentSetupParticipant;

    private LdifFile ldifModel;


    /**
     * Creates a new instance of LdifDocumentProvider.
     */
    public LdifDocumentProvider()
    {
        super();
        this.ldifParser = new LdifParser();
        this.ldifDocumentSetupParticipant = new LdifDocumentSetupParticipant();
    }


    /**
     * {@inheritDoc}
     */
    public IDocument getDocument( Object element )
    {
        IDocument document = super.getDocument( element );
        return document;
    }


    /**
     * Gets the LDIF Model
     *
     * @return
     *      the LDIF Model
     */
    public LdifFile getLdifModel()
    {
        return ldifModel;
    }


    /**
     * {@inheritDoc}
     */
    public void documentAboutToBeChanged( DocumentEvent event )
    {
    }


    /**
     * Update the LDIF Model.
     */
    public void documentChanged( DocumentEvent event )
    {
        try
        {
            int changeOffset = event.getOffset();
            int replacedTextLength = event.getLength();
            int insertedTextLength = event.getText() != null ? event.getText().length() : 0;
            IDocument document = event.getDocument();
            // Region changeRegion = new Region(changeOffset,
            // replacedTextLength);
            Region changeRegion = new Region( changeOffset - BrowserCoreConstants.LINE_SEPARATOR.length(),
                replacedTextLength + ( 2 * BrowserCoreConstants.LINE_SEPARATOR.length() ) );

            // get containers to replace (from changeOffset till
            // changeOffset+replacedTextLength, check end of record)
            List<LdifContainer> oldContainerList = new ArrayList<LdifContainer>();
            LdifContainer[] containers = this.ldifModel.getContainers();
            for ( int i = 0; i < containers.length; i++ )
            {

                Region containerRegion = new Region( containers[i].getOffset(), containers[i].getLength() );

                boolean changeOffsetAtEOF = i == containers.length - 1
                    && changeOffset >= containerRegion.getOffset() + containerRegion.getLength();

                if ( TextUtilities.overlaps( containerRegion, changeRegion ) || changeOffsetAtEOF )
                {

                    // remember index
                    int index = i;

                    // add invalid containers and non-records before overlap
                    i--;
                    for ( ; i >= 0; i-- )
                    {
                        if ( !containers[i].isValid() || !( containers[i] instanceof LdifRecord ) )
                        {
                            oldContainerList.add( 0, containers[i] );
                        }
                        else
                        {
                            break;
                        }
                    }

                    // add all overlapping containers
                    i = index;
                    for ( ; i < containers.length; i++ )
                    {
                        containerRegion = new Region( containers[i].getOffset(), containers[i].getLength() );
                        if ( TextUtilities.overlaps( containerRegion, changeRegion ) || changeOffsetAtEOF )
                        {
                            oldContainerList.add( containers[i] );
                        }
                        else
                        {
                            break;
                        }
                    }

                    // add invalid containers and non-records after overlap
                    for ( ; i < containers.length; i++ )
                    {
                        if ( !containers[i].isValid() || !( containers[i] instanceof LdifRecord )
                            || !( oldContainerList.get( oldContainerList.size() - 1 ) instanceof LdifRecord ) )
                        {
                            oldContainerList.add( containers[i] );
                        }
                        else
                        {
                            break;
                        }
                    }
                }
            }
            LdifContainer[] oldContainers = ( LdifContainer[] ) oldContainerList
                .toArray( new LdifContainer[oldContainerList.size()] );
            int oldCount = oldContainers.length;
            int oldOffset = oldCount > 0 ? oldContainers[0].getOffset() : 0;
            int oldLength = oldCount > 0 ? ( oldContainers[oldContainers.length - 1].getOffset()
                + oldContainers[oldContainers.length - 1].getLength() - oldContainers[0].getOffset() ) : 0;

            // get new content
            int newOffset = oldOffset;
            int newLength = oldLength - replacedTextLength + insertedTextLength;
            String textToParse = document.get( newOffset, newLength );

            // parse partion content to containers (offset=0)
            LdifFile newModel = this.ldifParser.parse( textToParse );
            LdifContainer[] newContainers = newModel.getContainers();

            // replace old containers with new containers
            // must adjust offsets of all following containers in model
            this.ldifModel.replace( oldContainers, newContainers );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

    }


    /**
     * Creates an LDIF annotation model.
     */
    protected IAnnotationModel createAnnotationModel( Object element ) throws CoreException
    {
        return new LdifExternalAnnotationModel();
    }


    /**
     * Tries to read the file pointed at by <code>input</code> if it is an
     * <code>IPathEditorInput</code>. If the file does not exist, <code>true</code>
     * is returned.
     *  
     * @param document the document to fill with the contents of <code>input</code>
     * @param input the editor input
     * @return <code>true</code> if setting the content was successful or no file exists, <code>false</code> otherwise
     * @throws CoreException if reading the file fails
     */
    private boolean setDocumentContent( IDocument document, IEditorInput input ) throws CoreException
    {
        // TODO: handle encoding
        Reader reader;
        try
        {
            if ( input instanceof IPathEditorInput )
            {
                reader = new FileReader( ( ( IPathEditorInput ) input ).getPath().toFile() );
            }
            else
            {
                return false;
            }
        }
        catch ( FileNotFoundException e )
        {
            // return empty document and save later
            return true;
        }

        try
        {
            setDocumentContent( document, reader );
            return true;
        }
        catch ( IOException e )
        {
            throw new CoreException( new Status( IStatus.ERROR, LdifEditorActivator.PLUGIN_ID, IStatus.OK,
                "error reading file", e ) ); //$NON-NLS-1$
        }
    }


    /**
     * Reads in document content from a reader and fills <code>document</code>
     * 
     * @param document the document to fill
     * @param reader the source
     * @throws IOException if reading fails
     */
    private void setDocumentContent( IDocument document, Reader reader ) throws IOException
    {
        Reader in = new BufferedReader( reader );
        try
        {
            StringBuffer buffer = new StringBuffer( 512 );
            char[] readBuffer = new char[512];
            int n = in.read( readBuffer );
            while ( n > 0 )
            {
                buffer.append( readBuffer, 0, n );
                n = in.read( readBuffer );
            }

            document.set( buffer.toString() );

        }
        finally
        {
            in.close();
        }
    }


    /**
     * Set up the document: partitioning and incremental parser
     * 
     * @param document the new document
     */
    private void setupDocument( IDocument document )
    {

        // setup document partitioning
        ldifDocumentSetupParticipant.setup( document );

        // initial parsing of whole document
        this.ldifModel = this.ldifParser.parse( document.get() );

        // add listener for incremental parsing
        document.addDocumentListener( this );

    }


    /**
     * Remove document listener.
     */
    protected void disposeElementInfo( Object element, ElementInfo info )
    {
        IDocument document = info.fDocument;
        document.removeDocumentListener( this );

        super.disposeElementInfo( element, info );
    }


    /**
     * {@inheritDoc}
     */
    protected IDocument createDocument( Object element ) throws CoreException
    {
        if ( element instanceof IEditorInput )
        {
            IDocument document = new Document();
            if ( setDocumentContent( document, ( IEditorInput ) element ) )
            {
                setupDocument( document );
            }
            return document;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    protected void doSaveDocument( IProgressMonitor monitor, Object element, IDocument document, boolean overwrite )
        throws CoreException
    {

        if ( element instanceof IPathEditorInput )
        {
            IPathEditorInput pei = ( IPathEditorInput ) element;
            IPath path = pei.getPath();
            File file = path.toFile();

            try
            {
                file.createNewFile();

                if ( file.exists() )
                {
                    if ( file.canWrite() )
                    {
                        Writer writer = new FileWriter( file );
                        writeDocumentContent( document, writer, monitor );
                    }
                    else
                    {
                        throw new CoreException( new Status( IStatus.ERROR,
                            "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "file is read-only", null ) ); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                else
                {
                    throw new CoreException( new Status( IStatus.ERROR,
                        "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "error creating file", null ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            catch ( IOException e )
            {
                throw new CoreException( new Status( IStatus.ERROR,
                    "org.eclipse.ui.examples.rcp.texteditor", IStatus.OK, "error when saving file", e ) ); //$NON-NLS-1$ //$NON-NLS-2$
            }

        }
    }


    /**
     * Saves the document contents to a stream.
     * 
     * @param document the document to save
     * @param writer the stream to save it to
     * @param monitor a progress monitor to report progress
     * @throws IOException if writing fails
     */
    private void writeDocumentContent( IDocument document, Writer writer, IProgressMonitor monitor ) throws IOException
    {
        Writer out = new BufferedWriter( writer );
        try
        {
            out.write( document.get() );
        }
        finally
        {
            out.close();
        }
    }


    /**
     * {@inheritDoc}
     */
    protected IRunnableContext getOperationRunner( IProgressMonitor monitor )
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isModifiable( Object element )
    {
        if ( element instanceof IPathEditorInput )
        {
            IPathEditorInput pei = ( IPathEditorInput ) element;
            File file = pei.getPath().toFile();
            return file.canWrite() || !file.exists(); // Allow to edit new files
        }
        return false;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly( Object element )
    {
        return !isModifiable( element );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isStateValidated( Object element )
    {
        return true;
    }
}