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

package org.apache.directory.studio.ldapbrowser.ui.actions;


import java.util.ArrayList;
import java.util.List;

import org.apache.directory.studio.connection.ui.RunnableContextRunner;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserAction;
import org.apache.directory.studio.ldapbrowser.core.jobs.InitializeAttributesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.ReadEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;


/**
 * This abstract class must be extended by each Action that <em>"Copies an Entry as..."</em>.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class CopyEntryAsAction extends BrowserAction
{
    /**
     * Returns DN only Mode.
     */
    public static final int MODE_DN_ONLY = 1;

    /**
     * Returns Attributes only Mode.
     */
    public static final int MODE_RETURNING_ATTRIBUTES_ONLY = 2;

    /**
     * Normal Mode
     */
    public static final int MODE_NORMAL = 3;

    /**
     * Includes Operational Attributes Mode.
     */
    public static final int MODE_INCLUDE_OPERATIONAL_ATTRIBUTES = 4;

    protected int mode;

    protected String type;

    protected String appendix;


    /**
     * Creates a new instance of CopyEntryAsAction.
     *
     * @param type
     *      the type of the target
     * @param mode
     *      the copy Mode
     */
    public CopyEntryAsAction( String type, int mode )
    {
        super();
        this.type = type;
        this.mode = mode;
        if ( this.mode == MODE_DN_ONLY )
        {
            this.appendix = Messages.getString( "CopyEntryAsAction.DNOnly" ); //$NON-NLS-1$
        }
        else if ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY )
        {
            this.appendix = Messages.getString( "CopyEntryAsAction.AttributesOnly" ); //$NON-NLS-1$
        }
        else if ( this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES )
        {
            this.appendix = Messages.getString( "CopyEntryAsAction.OperationalAttributes" ); //$NON-NLS-1$
        }
        else if ( this.mode == MODE_NORMAL )
        {
            this.appendix = Messages.getString( "CopyEntryAsAction.UserAttributes" ); //$NON-NLS-1$
        }
        else
        {
            appendix = ""; //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length > 0
            && getSelectedSearches().length == 0 )
        {
            String text = ( getSelectedEntries().length + getSelectedSearchResults().length
                + getSelectedBookmarks().length > 1 ? NLS.bind(
                Messages.getString( "CopyEntryAsAction.CopyEntries" ), new String[] { type } ) //$NON-NLS-1$
                : NLS.bind( Messages.getString( "CopyEntryAsAction.CopyEntry" ), new String[] { type } ) ) //$NON-NLS-1$ //$NON-NLS-2$
                + appendix;
            return text;
        }
        else if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedSearches().length == 1 && getSelectedSearches()[0].getSearchResults() != null
            && getSelectedSearches()[0].getSearchResults().length > 0 )
        {
            String text = ( getSelectedSearches()[0].getSearchResults().length > 1 ? NLS.bind( Messages
                .getString( "CopyEntryAsAction.CopyResults" ), new String[] { type } )//$NON-NLS-1$
                : NLS.bind( Messages.getString( "CopyEntryAsAction.CopyResult" ), new String[] { type } ) ) //$NON-NLS-1$
                + appendix;
            return text;
        }

        return NLS.bind( Messages.getString( "CopyEntryAsAction.CopyEntry" ), new String[] { type + appendix } ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public String getCommandId()
    {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // entries to copy
        List<IEntry> entryList = new ArrayList<IEntry>();
        for ( int i = 0; i < getSelectedEntries().length; i++ )
        {
            entryList.add( getSelectedEntries()[i] );
        }
        for ( int i = 0; i < getSelectedSearchResults().length; i++ )
        {
            entryList.add( getSelectedSearchResults()[i].getEntry() );
        }
        for ( int i = 0; i < getSelectedBookmarks().length; i++ )
        {
            IEntry entry = getSelectedBookmarks()[0].getBrowserConnection().getEntryFromCache(
                getSelectedBookmarks()[0].getDn() );
            if ( entry == null )
            {
                ReadEntryRunnable runnable = new ReadEntryRunnable( getSelectedBookmarks()[0].getBrowserConnection(),
                    getSelectedBookmarks()[0].getDn() );
                RunnableContextRunner.execute( runnable, null, true );
                entry = runnable.getReadEntry();
            }
            entryList.add( entry );
        }
        if ( getSelectedSearches().length == 1 )
        {
            ISearchResult[] results = getSelectedSearches()[0].getSearchResults();
            for ( int k = 0; k < results.length; k++ )
            {
                entryList.add( results[k].getEntry() );
            }
        }
        IEntry[] entries = ( IEntry[] ) entryList.toArray( new IEntry[entryList.size()] );

        // check uninitialized entries
        List<IEntry> uninitializedEntryList = new ArrayList<IEntry>();
        for ( int i = 0; entries != null && i < entries.length; i++ )
        {
            if ( !entries[i].isAttributesInitialized() )
            {
                uninitializedEntryList.add( entries[i] );
            }
        }
        if ( uninitializedEntryList.size() > 0
            && ( this.mode == MODE_NORMAL || this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES ) )
        {
            IEntry[] uninitializedEntries = ( IEntry[] ) uninitializedEntryList
                .toArray( new IEntry[uninitializedEntryList.size()] );

            InitializeAttributesRunnable runnable = new InitializeAttributesRunnable( uninitializedEntries );
            RunnableContextRunner.execute( runnable, null, true );

            // SyncInitializeEntryJob job = new
            // SyncInitializeEntryJob(uninitializedEntries,
            // InitializeEntryJob.INIT_ATTRIBUTES_MODE, null);
            // job.execute();
        }

        // serialize
        StringBuffer text = new StringBuffer();
        serialializeEntries( entries, text );
        copyToClipboard( text.toString() );
    }


    /**
     * Serializes Entries.
     *
     * @param entries
     *      the Entries to serialize
     * @param text
     *      the StringBuffer to serialize to
     */
    protected abstract void serialializeEntries( IEntry[] entries, StringBuffer text );


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        if ( getSelectedSearchResults().length > 0
            && getSelectedEntries().length + getSelectedBookmarks().length + getSelectedSearches().length == 0 )
        {
            return ( this.mode == MODE_RETURNING_ATTRIBUTES_ONLY || this.mode == MODE_NORMAL
                || this.mode == MODE_DN_ONLY || this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES );
        }
        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length > 0
            && getSelectedSearches().length == 0 )
        {
            return ( this.mode == MODE_NORMAL || this.mode == MODE_DN_ONLY || this.mode == MODE_INCLUDE_OPERATIONAL_ATTRIBUTES );
        }
        if ( getSelectedEntries().length + getSelectedSearchResults().length + getSelectedBookmarks().length == 0
            && getSelectedSearches().length == 1 && getSelectedSearches()[0].getSearchResults() != null
            && getSelectedSearches()[0].getSearchResults().length > 0 )
        {
            return true;
        }
        return false;
    }


    /**
     * Copies text to Clipboard
     *
     * @param text
     *      the Text to copy
     */
    protected void copyToClipboard( String text )
    {
        Clipboard clipboard = null;
        try
        {
            clipboard = new Clipboard( Display.getCurrent() );
            clipboard.setContents( new Object[]
                { text }, new Transfer[]
                { TextTransfer.getInstance() } );
        }
        finally
        {
            if ( clipboard != null )
                clipboard.dispose();
        }
    }
}
