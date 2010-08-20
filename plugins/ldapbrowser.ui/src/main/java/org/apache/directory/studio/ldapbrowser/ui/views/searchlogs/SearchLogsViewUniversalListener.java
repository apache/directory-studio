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

package org.apache.directory.studio.ldapbrowser.ui.views.searchlogs;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.directory.studio.connection.core.Connection;
import org.apache.directory.studio.connection.core.ConnectionCorePlugin;
import org.apache.directory.studio.connection.core.io.jndi.LdifSearchLogger;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonActivator;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.events.AttributesInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.BrowserConnectionUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.ChildrenInitializedEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryModificationEvent;
import org.apache.directory.studio.ldapbrowser.core.events.EntryUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.events.EventRegistry;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateEvent;
import org.apache.directory.studio.ldapbrowser.core.events.SearchUpdateListener;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.ui.views.connection.ConnectionView;
import org.apache.directory.studio.ldifparser.model.container.LdifContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.INullSelectionListener;
import org.eclipse.ui.IWorkbenchPart;


/**
 * The SearchLogsViewUniversalListener manages all events for the search logs view.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchLogsViewUniversalListener implements BrowserConnectionUpdateListener, SearchUpdateListener,
    EntryUpdateListener
{

    /** The search log view. */
    private SearchLogsView view;

    /** The current input */
    private SearchLogsViewInput input;

    /** The last refresh timestamp. */
    private long lastRefreshTimestamp;

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
                    Connection[] connections = BrowserSelectionUtils.getConnections( selection );
                    if ( connections.length == 1 )
                    {
                        IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                            .getBrowserConnectionById( connections[0].getId() );
                        SearchLogsViewInput input = new SearchLogsViewInput( connection, 0 );
                        setInput( input );
                        scrollToNewest();
                    }
                }
            }
        }
    };


    /**
     * Creates a new instance of SearchLogsViewUniversalListener.
     *
     * @param view the search logs view
     */
    public SearchLogsViewUniversalListener( SearchLogsView view )
    {
        this.view = view;
        this.input = null;

        EventRegistry.addEntryUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addSearchUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
        EventRegistry.addBrowserConnectionUpdateListener( this, BrowserCommonActivator.getDefault().getEventRunner() );
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
            EventRegistry.removeSearchUpdateListener( this );
            EventRegistry.removeBrowserConnectionUpdateListener( this );
            view = null;
        }
    }


    /**
     * Refreshes the input.
     */
    void refreshInput()
    {
        SearchLogsViewInput newInput = input;
        input = null;
        setInput( newInput );
    }


    /**
     * Sets the input.
     *
     * @param input the input
     */
    void setInput( SearchLogsViewInput input )
    {
        // only if another connection is selected
        if ( this.input != input && input.getBrowserConnection().getConnection() != null )
        {
            this.input = input;

            LdifSearchLogger searchLogger = ConnectionCorePlugin.getDefault().getLdifSearchLogger();

            if ( ( input != null ) && ( input.getBrowserConnection() != null )
                && ( input.getBrowserConnection().getConnection() != null ) && ( searchLogger != null ) )
            {
                // load file %u %g
                StringBuffer sb = new StringBuffer();
                File[] files = searchLogger.getFiles( input.getBrowserConnection().getConnection() );
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
//                view.getMainWidget().getSourceViewer().getDocument().set( sb.toString() );
                view.getActionGroup().setInput( input );
            }
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the input.
     */
    public void entryUpdated( EntryModificationEvent event )
    {
        if ( event instanceof AttributesInitializedEvent || event instanceof ChildrenInitializedEvent )
        {
            updateInput();
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the input.
     */
    public void searchUpdated( SearchUpdateEvent searchUpdateEvent )
    {
        if ( searchUpdateEvent.getDetail() == SearchUpdateEvent.EventDetail.SEARCH_PERFORMED )
        {
            updateInput();
        }
    }


    /**
     * {@inheritDoc}
     *
     * This implementation refreshes the input.
     */
    public void browserConnectionUpdated( BrowserConnectionUpdateEvent browserConnectionUpdateEvent )
    {
        if ( browserConnectionUpdateEvent.getDetail() == BrowserConnectionUpdateEvent.Detail.BROWSER_CONNECTION_OPENED
            || browserConnectionUpdateEvent.getDetail() == BrowserConnectionUpdateEvent.Detail.SCHEMA_UPDATED )
        {
            updateInput();
        }
    }


    private void updateInput()
    {
        // performance optimization: refresh only once per second
        long now = System.currentTimeMillis();
        if ( lastRefreshTimestamp + 1000 < now )
        {
            refreshInput();
            scrollToNewest();
            lastRefreshTimestamp = now;
        }
    }


    /**
     * Scroll to oldest log entry.
     */
    public void scrollToOldest()
    {
//        view.getMainWidget().getSourceViewer().setTopIndex( 0 );
    }


    /**
     * Scroll to newest log entry.
     */
    public void scrollToNewest()
    {
//        try
//        {
//            LdifContainer record = view.getMainWidget().getLdifModel().getLastContainer();
//            int offset = record.getOffset();
//            int line = view.getMainWidget().getSourceViewer().getDocument().getLineOfOffset( offset );
//            if ( line > 3 )
//                line -= 3;
//            view.getMainWidget().getSourceViewer().setTopIndex( line );
//        }
//        catch ( Exception e )
//        {
//        }
    }


    /**
     * Clears the input and deletes the logfiles for it.
     */
    public void clearInput()
    {
        if ( input.getBrowserConnection().getConnection() != null )
        {
            StringBuffer sb = new StringBuffer( "" ); //$NON-NLS-1$
            FileWriter fw = null;
            LdifSearchLogger searchLogger = ConnectionCorePlugin.getDefault().getLdifSearchLogger();
            File[] files = searchLogger.getFiles( input.getBrowserConnection().getConnection() );
            searchLogger.dispose( input.getBrowserConnection().getConnection() );
            for ( int i = 0; i < files.length; i++ )
            {
                try
                {
                    if ( files[i] != null && files[i].exists() && !files[i].delete() )
                    {
                        fw = new FileWriter( files[i] );
                        fw.write( "" ); //$NON-NLS-1$
                    }

                }
                catch ( Exception e )
                {
                    sb.append( e.getMessage() );
                }
            }
//            view.getMainWidget().getSourceViewer().setTopIndex( 0 );
//            view.getMainWidget().getSourceViewer().getDocument().set( sb.toString() );
        }
    }

}
