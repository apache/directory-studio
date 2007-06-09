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

package org.apache.directory.ldapstudio.browser.ui.views.modificationlogs;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.directory.ldapstudio.browser.ui.views.connection.ConnectionView;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.actions.SelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.model.IConnection;
import org.apache.directory.studio.ldapbrowser.core.model.ldif.container.LdifContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IWorkbenchPart;


/**
 * The ModificationLogsViewUniversalListener manages all events for the modification logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ModificationLogsViewUniversalListener implements EntryUpdateListener
{

    /** The modification log view. */
    private ModificationLogsView view;

    /** The current input */
    private ModificationLogsViewInput input;

    /** Listener that listens for selections of connections */
    private INullSelectionListener connectionSelectionListener = new INullSelectionListener()
    {
        /**
         * {@inheritDoc}
         *
         * This implementation sets the input when another connection was selected.
         */
        public void selectionChanged( IWorkbenchPart part, ISelection selection )
        {
            if ( view != null && part != null )
            {
                if ( view.getSite().getWorkbenchWindow() == part.getSite().getWorkbenchWindow() )
                {
                    IConnection[] connections = SelectionUtils.getConnections( selection );
                    if ( connections.length == 1 )
                    {
                        ModificationLogsViewInput input = new ModificationLogsViewInput( connections[0], 0 );
                        setInput( input );
                        scrollToNewest();
                    }
                }
            }
        }
    };


    /**
     * Creates a new instance of ModificationLogsViewUniversalListener.
     *
     * @param view the modification logs view
     */
    public ModificationLogsViewUniversalListener( ModificationLogsView view )
    {
        this.view = view;
        this.input = null;

        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        view.getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener( ConnectionView.getId(),
            connectionSelectionListener );
    }


    /**
     * Disposed this listener
     */
    public void dispose()
    {
        if ( view != null )
        {
            view.getSite().getWorkbenchWindow().getSelectionService().removePostSelectionListener(
                ConnectionView.getId(), connectionSelectionListener );

            EventRegistry.removeEntryUpdateListener( this );
            view = null;
        }
    }


    /**
     * Refreshes the input.
     */
    void refreshInput()
    {
        ModificationLogsViewInput newInput = input;
        input = null;
        setInput( newInput );
    }


    /**
     * Sets the input.
     *
     * @param input the input
     */
    void setInput( ModificationLogsViewInput input )
    {
        // only if another connection is selected
        if ( this.input != input )
        {
            this.input = input;

            // load file %u %g
            StringBuffer sb = new StringBuffer();
            File[] files = input.getConnection().getModificationLogger().getFiles();
            int i = input.getIndex();
            if ( 0 <= i && i < files.length && files[i] != null && files[i].exists() && files[i].canRead() )
            {
                try
                {
                    FileReader fr = new FileReader( files[i] );
                    char[] cbuf = new char[4096];
                    for ( int length = fr.read( cbuf ); length > 0; length = fr.read( cbuf ) )
                    {
                        sb.append( cbuf, 0, length );
                    }
                }
                catch ( Exception e )
                {
                    sb.append( e.getMessage() );
                }
            }

            // change input
            view.getMainWidget().getSourceViewer().getDocument().set( sb.toString() );
            view.getActionGroup().setInput( input );
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the input.
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        if ( !( event instanceof AttributesInitializedEvent ) && !( event instanceof ChildrenInitializedEvent ) )
        {
            refreshInput();
            scrollToNewest();
        }
    }


    /**
     * Scroll to oldest log entry.
     */
    public void scrollToOldest()
    {
        view.getMainWidget().getSourceViewer().setTopIndex( 0 );
    }


    /**
     * Scroll to newest log entry.
     */
    public void scrollToNewest()
    {
        try
        {
            LdifContainer record = view.getMainWidget().getLdifModel().getLastContainer();
            int offset = record.getOffset();
            int line = view.getMainWidget().getSourceViewer().getDocument().getLineOfOffset( offset );
            if ( line > 3 )
                line -= 3;
            view.getMainWidget().getSourceViewer().setTopIndex( line );
        }
        catch ( Exception e )
        {
        }

    }
    
	/**
     * Clears the input and deletes the logfiles for it
     * 
     */
    public void clearInput()
    {
        StringBuffer sb = new StringBuffer( "" );
        FileWriter fw = null;
        File[] files = input.getConnection().getModificationLogger().getFiles();
        input.getConnection().getModificationLogger().dispose();
        for ( int i = 0; i < files.length; i++ )
        {
            try
            {
                if ( files[i] != null && files[i].exists() && !files[i].delete() )
                {
                    fw = new FileWriter( files[i] );
                    fw.write( "" );
                }

            }
            catch ( Exception e )
            {
                sb.append( e.getMessage() );
            }
        }
        view.getMainWidget().getSourceViewer().setTopIndex( 0 );
        view.getMainWidget().getSourceViewer().getDocument().set( sb.toString() );
    }

}
