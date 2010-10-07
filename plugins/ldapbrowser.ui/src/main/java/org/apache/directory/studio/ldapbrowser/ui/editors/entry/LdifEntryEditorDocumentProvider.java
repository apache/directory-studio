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


import java.util.Arrays;

import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.DummyEntry;
import org.apache.directory.studio.ldapbrowser.core.utils.AttributeComparator;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.apache.directory.studio.ldifeditor.editor.LdifDocumentProvider;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifInvalidContainer;
import org.apache.directory.studio.ldifparser.model.container.LdifRecord;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;


/**
 * The document provider for the LDIF entry editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
        LdifRecord[] records = getLdifModel().getRecords();
        if ( records.length != 1 || !( records[0] instanceof LdifContentRecord ) )
        {
            throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID, Messages
                .getString( "LdifEntryEditorDocumentProvider.InvalidRecordType" ) ) ); //$NON-NLS-1$
        }
        if ( !records[0].isValid() )
        {
            throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID, NLS.bind( Messages
                .getString( "LdifEntryEditorDocumentProvider.InvalidLdif" ), records[0].getInvalidString() ) ) ); //$NON-NLS-1$
        }
        for ( LdifContainer ldifContainer : getLdifModel().getContainers() )
        {
            if ( ldifContainer instanceof LdifInvalidContainer )
            {
                LdifInvalidContainer cont = ( LdifInvalidContainer ) ldifContainer;
                throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID, NLS.bind( Messages
                    .getString( "LdifEntryEditorDocumentProvider.InvalidLdif" ), cont.getInvalidString() ) ) ); //$NON-NLS-1$
            }
        }

        EntryEditorInput input = getEntryEditorInput( element );
        try
        {
            DN newDN = new DN( records[0].getDnLine().getValueAsString() );
            if ( !newDN.equals( input.getResolvedEntry().getDn() ) )
            {
                throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID, NLS.bind( Messages
                    .getString( "LdifEntryEditorDocumentProvider.ModDnNotSupported" ), records[0].getInvalidString() ) ) ); //$NON-NLS-1$
            }
        }
        catch ( LdapInvalidDnException e )
        {
            throw new CoreException( new Status( IStatus.ERROR, BrowserUIConstants.PLUGIN_ID, Messages
                .getString( "LdifEntryEditorDocumentProvider.InvalidDN" ) ) ); //$NON-NLS-1$
        }

        IStatus status = input.saveSharedWorkingCopy( false, editor );
        if ( status != null && !status.isOK() )
        {
            BrowserUIPlugin.getDefault().getLog().log( status );
            throw new CoreException( status );
        }
    }


    @Override
    public void documentChanged( DocumentEvent event )
    {
        super.documentChanged( event );

        // the document change was caused by the model update
        // no need to update the model again, don't fire more events
        if ( inSetContent )
        {
            return;
        }

        // only continue if the LDIF model is valid
        LdifRecord[] records = getLdifModel().getRecords();
        if ( records.length != 1 || !( records[0] instanceof LdifContentRecord ) || !records[0].isValid()
            || !records[0].getDnLine().isValid() )
        {
            return;
        }
        for ( LdifContainer ldifContainer : getLdifModel().getContainers() )
        {
            if ( ldifContainer instanceof LdifInvalidContainer )
            {
                return;
            }
        }

        // update shared working copy
        try
        {
            LdifContentRecord modifiedRecord = ( LdifContentRecord ) records[0];
            IBrowserConnection browserConnection = input.getSharedWorkingCopy( editor ).getBrowserConnection();
            DummyEntry modifiedEntry = ModelConverter.ldifContentRecordToEntry( modifiedRecord, browserConnection );
            ( ( DummyEntry ) input.getSharedWorkingCopy( editor ) ).setDn( modifiedEntry.getDn() );
            new CompoundModification().replaceAttributes( modifiedEntry, input.getSharedWorkingCopy( editor ), this );
        }
        catch ( LdapInvalidDnException e )
        {
            throw new RuntimeException( e );
        }
    }


    @Override
    protected void doResetDocument( Object element, IProgressMonitor monitor ) throws CoreException
    {
        // reset working copy first
        if ( input != null )
        {
            input.resetSharedWorkingCopy( editor );
        }

        super.doResetDocument( element, monitor );
    }


    public void workingCopyModified( EntryEditorInput input, Object source )
    {
        // the model change was caused by the document change
        // no need to set the content again, don't fire more events
        if ( source == this )
        {
            return;
        }

        IDocument document = getDocument( input );
        if ( document != null )
        {
            try
            {
                inSetContent = true;
                IEntry sharedWorkingCopy = input.getSharedWorkingCopy( editor );
                setDocumentInput( document, sharedWorkingCopy );

                // reset dirty state
                if ( !input.isSharedWorkingCopyDirty( editor ) )
                {
                    super.doResetDocument( input, null );
                }
            }
            catch ( CoreException e )
            {
                throw new RuntimeException( e );
            }
            finally
            {
                inSetContent = false;
            }
        }
    }


    private void setDocumentInput( IDocument document, IEntry entry )
    {
        LdifContentRecord record = ModelConverter.entryToLdifContentRecord( entry );

        // sort attribute-value lines
        AttributeComparator comparator = new AttributeComparator( entry );
        LdifAttrValLine[] attrValLines = record.getAttrVals();
        Arrays.sort( attrValLines, comparator );
        LdifContentRecord newRecord = new LdifContentRecord( record.getDnLine() );
        for ( LdifAttrValLine attrValLine : attrValLines )
        {
            newRecord.addAttrVal( attrValLine );
        }
        newRecord.finish( record.getSepLine() );

        // format
        String newContent = newRecord.toFormattedString( Utils.getLdifFormatParameters() );

        // set content
        document.set( newContent );
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
        IDocument document = new Document();
        if ( entry != null )
        {
            setDocumentInput( document, entry );
        }
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
                "Expected EntryEditorInput, was " + element ) ); //$NON-NLS-1$
        }
    }


    @Override
    public boolean isModifiable( Object element )
    {
        if ( element instanceof EntryEditorInput )
        {
            EntryEditorInput editorInput = ( EntryEditorInput ) element;
            IEntry entry = editorInput.getSharedWorkingCopy( editor );
            return ( entry != null );
        }

        return false;
    }
}
