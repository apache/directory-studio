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


import java.lang.reflect.InvocationTargetException;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.jobs.ExecuteLdifRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.impl.BrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.utils.ModelConverter;
import org.apache.directory.studio.ldapbrowser.core.utils.Utils;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIConstants;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifContentRecord;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IEditorInput;


/**
 * An entry editor the opens a new editor tab for each entry.
 * 
 * TODO: Configurable modificaton mode
 * Right now the modification mode is fixed: no immediate commit of changes, 
 * instead the editor follows the open-save-close lifecycle.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MultiTabEntryEditor extends EntryEditor
{

    private EntryUpdateListener entryUpdateListener = new EntryUpdateListener()
    {
        public void entryUpdated( EntryModificationEvent event )
        {
            if ( mainWidget.getViewer() == null || mainWidget.getViewer().getInput() == null
                || event.getModifiedEntry() != mainWidget.getViewer().getInput() )
            {
                return;
            }

            firePropertyChange( PROP_DIRTY );
        }
    };


    /**
     * Gets the ID of the MultiTabEntryEditor.
     * 
     * @return the id of the MultiTabEntryEditor
     */
    public static String getId()
    {
        return BrowserUIConstants.EDITOR_MULTI_TAB_ENTRY_EDITOR;
    }


    @Override
    public void setInput( IEditorInput input )
    {
        super.setInput( input );

        setEntryEditorWidgetInput();

        // make the editor dirty when the entry is modified
        EventRegistry
            .addEntryUpdateListener( entryUpdateListener, BrowserCommonActivator.getDefault().getEventRunner() );

        // use the entry's DN as tab label
        if ( input instanceof EntryEditorInput )
        {
            EntryEditorInput entryEditorInput = ( EntryEditorInput ) input;
            setPartName( entryEditorInput.getName() );
        }
    }


    @Override
    public boolean isDirty()
    {
        LdifChangeModifyRecord diff = getDiff();
        if ( diff != null )
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    @Override
    public void doSave( final IProgressMonitor monitor )
    {
        IEditorInput input = getEditorInput();
        if ( input instanceof EntryEditorInput && mainWidget != null )
        {
            EntryEditorInput eei = ( EntryEditorInput ) input;
            IEntry entry = eei.getResolvedEntry();
            initAttributes( entry );
            IBrowserConnection browserConnection = entry.getBrowserConnection();

            LdifChangeModifyRecord diff = getDiff();
            if ( diff != null )
            {
                IRunnableContext runnableContext = new IRunnableContext()
                {
                    public void run( boolean fork, boolean cancelable, IRunnableWithProgress runnable )
                        throws InvocationTargetException, InterruptedException
                    {
                        runnable.run( monitor );
                    }
                };

                // save
                ExecuteLdifRunnable runnable = new ExecuteLdifRunnable( browserConnection, diff
                    .toFormattedString( LdifFormatParameters.DEFAULT ), false, false );
                IStatus status = RunnableContextRunner.execute( runnable, runnableContext, true );
                if ( status.isOK() )
                {
                    // set new input and refresh the dirty state
                    setEntryEditorWidgetInput();
                    firePropertyChange( PROP_DIRTY );
                }
            }
        }
    }


    /**
     * Sets the entry editor widget input. A clone of the real entry
     * with a read-only connection is used for that.
     */
    private void setEntryEditorWidgetInput()
    {
        IEditorInput input = getEditorInput();
        if ( input instanceof EntryEditorInput && universalListener != null )
        {
            EntryEditorInput eei = ( EntryEditorInput ) input;
            IEntry entry = eei.getResolvedEntry();
            initAttributes( entry );
            IBrowserConnection browserConnection = entry.getBrowserConnection();

            try
            {
                EventRegistry.suspendEventFiringInCurrentThread();

                // clone connection and set read-only
                Connection readOnlyConnection = ( Connection ) browserConnection.getConnection().clone();
                readOnlyConnection.getConnectionParameter().setReadOnly( true );
                BrowserConnection readOnlyBrowserConnection = new BrowserConnection( readOnlyConnection );

                // clone entry
                LdifContentRecord record = ModelConverter.entryToLdifContentRecord( entry );
                IEntry clonedEntry = ModelConverter.ldifContentRecordToEntry( record, readOnlyBrowserConnection );

                // set input
                universalListener.setInput( clonedEntry );
            }
            catch ( Exception e )
            {
                throw new RuntimeException( "Failed to set input", e );
            }
            finally
            {
                EventRegistry.resumeEventFiringInCurrentThread();
            }
        }
    }


    /**
     * Gets the difference between the original entry and the modified entry.
     * 
     * @return the difference
     */
    private LdifChangeModifyRecord getDiff()
    {
        IEditorInput input = getEditorInput();
        if ( input instanceof EntryEditorInput && mainWidget != null )
        {
            EntryEditorInput eei = ( EntryEditorInput ) input;
            IEntry originalEntry = eei.getResolvedEntry();
            initAttributes( originalEntry );
            IEntry modifiedEntry = ( IEntry ) mainWidget.getViewer().getInput();

            LdifChangeModifyRecord record = Utils.computeDiff( originalEntry, modifiedEntry );
            return record;
        }
        return null;
    }


    /**
     * Initializes the attributes.
     * 
     * @param entry the entry
     */
    private void initAttributes( IEntry entry )
    {
        if ( !entry.isAttributesInitialized() )
        {
            boolean foa = entry.getBrowserConnection().isFetchOperationalAttributes();
            InitializeAttributesRunnable iar = new InitializeAttributesRunnable( new IEntry[]
                { entry }, foa );
            RunnableContextRunner.execute( iar, null, true );
        }
    }


    @Override
    public void dispose()
    {
        // remove the listener
        EventRegistry.removeEntryUpdateListener( entryUpdateListener );
        super.dispose();
    }

}
