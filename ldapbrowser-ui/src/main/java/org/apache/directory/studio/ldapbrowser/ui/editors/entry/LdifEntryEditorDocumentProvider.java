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

package org.apache.directory.studio.ldapbrowser.ui.editors.entry;


import javax.naming.InvalidNameException;

import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldifeditor.editor.LdifDocumentProvider;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;


/**
 * The document provider for the LDIF entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifEntryEditorDocumentProvider extends LdifDocumentProvider
{

    private EntryEditorInput input;
    private boolean inSetContent = false;

    private LdifEntryEditor editor;


    public LdifEntryEditorDocumentProvider( LdifEntryEditor editor )
    {
        this.editor = editor;
    }


    @Override
    protected void doSaveDocument( IProgressMonitor monitor, Object element, IDocument document, boolean overwrite )
        throws CoreException
    {
        EntryEditorInput input = getEntryEditorInput( element );
        IStatus status = input.saveSharedWorkingCopy( false, editor );
        if ( status != null && !status.isOK() )
        {
            throw new CoreException( status );
        }
    }


    @Override
    public void documentChanged( DocumentEvent event )
    {
        super.documentChanged( event );

        if ( input == null )
        {
            return;
        }
        LdifRecord[] records = getLdifModel().getRecords();
        if ( records.length != 1 || !( records[0] instanceof LdifContentRecord ) || !records[0].isValid() )
        {
            // can't continue
            return;
        }

        // the document change was caused by the model update
        // no need to update the model again, don't fire more events
        if ( inSetContent )
        {
            return;
        }

        // update shared working copy
        try
        {
            LdifContentRecord modifiedRecord = ( LdifContentRecord ) records[0];
            IBrowserConnection browserConnection = input.getSharedWorkingCopy( editor ).getBrowserConnection();
            DummyEntry modifiedEntry = ModelConverter.ldifContentRecordToEntry( modifiedRecord, browserConnection );
            new CompoundModification().replaceAttributes( modifiedEntry, input.getSharedWorkingCopy( editor ) );
        }
        catch ( InvalidNameException e )
        {
            throw new RuntimeException( "Failed to set input", e );
        }
    }


    public void setContent( EntryEditorInput input )
    {
        IEntry sharedWorkingCopy = input.getSharedWorkingCopy( editor );
        LdifContentRecord record = ModelConverter.entryToLdifContentRecord( sharedWorkingCopy );
        String newContent = record.toFormattedString( Utils.getLdifFormatParameters() );

        IDocument document = getDocument( input );
        if ( document != null )
        {
            inSetContent = true;
            document.set( newContent );

            // reset dirty state
            if ( !input.isSharedWorkingCopyDirty( editor ) )
            {
                try
                {
                    doResetDocument( input, null );
                }
                catch ( CoreException e )
                {
                    throw new RuntimeException( e );
                }
            }
            inSetContent = false;
        }
    }


    public IDocument getDocument( Object element )
    {
        if ( element instanceof EntryEditorInput )
        {
            EntryEditorInput input = ( EntryEditorInput ) element;
            if ( input.getExtension() == null )
            {
                // this is a performance optimization
                return null;
            }
        }

        return super.getDocument( element );
    }


    @Override
    protected IDocument createDocument( Object element ) throws CoreException
    {
        input = getEntryEditorInput( element );

        IEntry entry = getEntryEditorInput( element ).getSharedWorkingCopy( editor );
        LdifContentRecord record = ModelConverter.entryToLdifContentRecord( entry );
        String content = record.toFormattedString( Utils.getLdifFormatParameters() );

        IDocument document = new Document();
        document.set( content );
        setupDocument( document );
        return document;
    }


    private EntryEditorInput getEntryEditorInput( Object element ) throws CoreException
    {
        if ( element instanceof EntryEditorInput )
        {
            EntryEditorInput input = ( EntryEditorInput ) element;
            return input;
        }
        else
        {
            throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID,
                "Expected MultiTabLdifEntryEditorInput, was " + element ) );
        }
    }


    @Override
    public boolean isModifiable( Object element )
    {
        return element instanceof EntryEditorInput;
    }

}
