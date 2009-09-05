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

import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldifeditor.editor.LdifDocumentProvider;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;


/**
 * The document provider for the LDIF entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class LdifEntryEditorDocumentProvider extends LdifDocumentProvider
{

    @Override
    protected void doSaveDocument( IProgressMonitor monitor, Object element, IDocument document, boolean overwrite )
        throws CoreException
    {
        IEntry entry = getResolvedEntry( element );
        IBrowserConnection browserConnection = entry.getBrowserConnection();

        LdifChangeModifyRecord diff = getDiff( entry );
        if ( diff != null )
        {
            // save by executing the LDIF
            ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( browserConnection, diff
                .toFormattedString( LdifFormatParameters.DEFAULT ), false, false );
            IStatus status = RunnableContextRunner.execute( runnable, null, true );
            if ( !status.isOK() )
            {
                return;
            }
        }

        // if no difference or if saved successful: refresh input and clean dirty state
        entry = getResolvedEntry( element );
        LdifContentRecord record = ModelConverter.entryToLdifContentRecord( entry );
        String content = record.toFormattedString( Utils.getLdifFormatParameters() );
        getDocument( element ).set( content );
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


    /**
     * Gets the difference between the original entry and the modified entry.
     * 
     * @return the difference
     */
    private LdifChangeModifyRecord getDiff( IEntry originalEntry ) throws CoreException
    {
        LdifRecord[] records = getLdifModel().getRecords();
        if ( records.length != 1 || !( records[0] instanceof LdifContentRecord ) )
        {
            throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID,
                "Expected exactly one LDIF content record." ) );
        }

        try
        {
            LdifContentRecord modifiedRecord = ( LdifContentRecord ) records[0];
            DummyEntry modifiedEntry = ModelConverter.ldifContentRecordToEntry( modifiedRecord, originalEntry
                .getBrowserConnection() );
            LdifChangeModifyRecord record = Utils.computeDiff( originalEntry, modifiedEntry );
            return record;
        }
        catch ( InvalidNameException e )
        {
            throw new RuntimeException( "Failed to set input", e );
        }
    }


    @Override
    protected IDocument createDocument( Object element ) throws CoreException
    {
        IEntry entry = getResolvedEntry( element );
        LdifContentRecord record = ModelConverter.entryToLdifContentRecord( entry );
        String content = record.toFormattedString( Utils.getLdifFormatParameters() );

        IDocument document = new Document();
        document.set( content );
        setupDocument( document );
        return document;
    }


    private IEntry getResolvedEntry( Object element ) throws CoreException
    {
        if ( element instanceof EntryEditorInput )
        {
            EntryEditorInput input = ( EntryEditorInput ) element;
            IEntry entry = input.getResolvedEntry();
            EntryEditorUtils.ensureAttributesInitialized( entry );
            return entry;
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
