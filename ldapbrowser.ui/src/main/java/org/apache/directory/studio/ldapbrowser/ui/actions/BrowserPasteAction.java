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


import org.apache.directory.studio.connection.core.Utils;
import org.apache.directory.studio.ldapbrowser.common.BrowserCommonConstants;
import org.apache.directory.studio.ldapbrowser.common.actions.PasteAction;
import org.apache.directory.studio.ldapbrowser.common.dialogs.EntryExistsCopyStrategyDialogImpl;
import org.apache.directory.studio.ldapbrowser.common.dialogs.ScopeDialog;
import org.apache.directory.studio.ldapbrowser.common.dnd.EntryTransfer;
import org.apache.directory.studio.ldapbrowser.common.dnd.SearchTransfer;
import org.apache.directory.studio.ldapbrowser.common.dnd.ValuesTransfer;
import org.apache.directory.studio.ldapbrowser.core.jobs.CopyEntriesRunnable;
import org.apache.directory.studio.ldapbrowser.core.jobs.StudioBrowserJob;
import org.apache.directory.studio.ldapbrowser.core.jobs.UpdateEntryRunnable;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.SearchParameter;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch.SearchScope;
import org.apache.directory.studio.ldapbrowser.core.model.impl.Search;
import org.apache.directory.studio.ldapbrowser.core.utils.CompoundModification;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.model.LdifFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * This class implements the paste action for the LDAP browser view.
 * It copies attribute-values to another entry.
 * It invokes an UpdateEntryRunnable.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BrowserPasteAction extends PasteAction
{
    /**
     * Creates a new instance of BrowserPasteAction.
     */
    public BrowserPasteAction()
    {
        super();
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        // entry
        IEntry[] entries = getEntriesToPaste();
        if ( entries != null )
        {
            return entries.length > 1 ? Messages.getString( "BrowserPasteAction.PasteEntries" ) : Messages.getString( "BrowserPasteAction.PasteEntry" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // searches
        ISearch[] searches = getSearchesToPaste();
        if ( searches != null )
        {
            return searches.length > 1 ? Messages.getString( "BrowserPasteAction.PasteSearches" ) : Messages.getString( "BrowserPasteAction.PasteSearch" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // value
        IValue[] values = getValuesToPaste();
        if ( values != null )
        {
            return values.length > 1 ? Messages.getString( "BrowserPasteAction.PasteValues" ) : Messages.getString( "BrowserPasteAction.PasteValue" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return Messages.getString( "BrowserPasteAction.Paste" ); //$NON-NLS-1$
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEnabled()
    {
        // entry
        if ( getEntriesToPaste() != null )
        {
            return true;
        }

        // search
        else if ( getSearchesToPaste() != null )
        {
            return true;
        }

        // value
        else if ( getValuesToPaste() != null )
        {
            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public void run()
    {
        // entry
        IEntry[] entries = getEntriesToPaste();
        if ( entries != null )
        {
            pasteEntries( getSelectedEntries()[0], entries );
            return;
        }

        // search
        ISearch[] searches = getSearchesToPaste();
        if ( searches != null )
        {
            pasteSearches( searches );
            return;
        }

        // value
        IValue[] values = getValuesToPaste();
        if ( values != null )
        {
            pasteValues( values );
            return;
        }

    }


    /**
     * Pastes the given entries
     *
     * @param parent
     *      the parent Entry
     * @param entriesToPaste
     *      the Entries to paste
     */
    private void pasteEntries( final IEntry parent, final IEntry[] entriesToPaste )
    {
        SearchScope scope = SearchScope.OBJECT;
        boolean askForScope = false;
        for ( int i = 0; i < entriesToPaste.length; i++ )
        {
            if ( entriesToPaste[i].hasChildren() )
            {
                askForScope = true;
                break;
            }
        }
        if ( askForScope )
        {
            ScopeDialog scopeDialog = new ScopeDialog( Display.getDefault().getActiveShell(),
                Messages.getString( "BrowserPasteAction.SelectCopyDepth" ), //$NON-NLS-1$
                entriesToPaste.length > 1 );
            scopeDialog.open();
            scope = scopeDialog.getScope();
        }

        new StudioBrowserJob( new CopyEntriesRunnable( parent, entriesToPaste, scope, new EntryExistsCopyStrategyDialogImpl(
            Display.getDefault().getActiveShell() ) ) ).execute();
    }


    private void pasteSearches( ISearch[] searches )
    {
        IBrowserConnection browserConnection = null;
        if ( getSelectedBrowserViewCategories().length > 0 )
        {
            browserConnection = getSelectedBrowserViewCategories()[0].getParent();
        }
        else if ( getSelectedSearches().length > 0 )
        {
            browserConnection = getSelectedSearches()[0].getBrowserConnection();
        }

        if ( browserConnection != null )
        {
            ISearch clone = null;
            for ( ISearch search : searches )
            {
                SearchParameter searchParameter = ( SearchParameter ) search.getSearchParameter().clone();
                clone = new Search( browserConnection, searchParameter );
                browserConnection.getSearchManager().addSearch( clone );
            }

            if ( searches.length == 1 )
            {
                IAdaptable element = ( IAdaptable ) clone;
                String pageId = BrowserCommonConstants.PROP_SEARCH;
                String title = clone.getName();

                PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn( getShell(), element, pageId, null,
                    null );
                if ( dialog != null )
                {
                    title = Utils.shorten( title, 30 );
                }
                dialog.getShell().setText( NLS.bind( Messages.getString( "PropertiesAction.PropertiesForX" ), title ) ); //$NON-NLS-1$
                dialog.open();
            }
        }
    }


    /**
     * Paste Values
     *
     * @param values
     *      the Values to paste
     */
    private void pasteValues( IValue[] values )
    {
        IEntry entry = null;
        if ( getSelectedEntries().length == 1 )
        {
            entry = getSelectedEntries()[0];
        }
        else if ( getSelectedSearchResults().length == 1 )
        {
            entry = getSelectedSearchResults()[0].getEntry();
        }
        else if ( getSelectedBookmarks().length == 1 )
        {
            entry = getSelectedBookmarks()[0].getEntry();
        }

        // always get the real entry in case it is a bookmark or search continuation
        if ( entry != null )
        {
            entry = entry.getBrowserConnection().getEntryFromCache( entry.getDn() );
        }

        if ( entry != null )
        {
            IEntry clone = new CompoundModification().cloneEntry( entry );
            new CompoundModification().createValues( clone, values );
            LdifFile diff = org.apache.directory.studio.ldapbrowser.core.utils.Utils.computeDiff( entry, clone );
            if ( diff != null )
            {
                UpdateEntryRunnable runnable = new UpdateEntryRunnable( entry,
                    diff.toFormattedString( LdifFormatParameters.DEFAULT ) );
                new StudioBrowserJob( runnable ).execute();
            }
        }

    }


    /**
     * Conditions: 
     * <li>an entry is selected</li>
     * <li>there are entries in clipboard</li>
     * 
     * @return
     */
    private IEntry[] getEntriesToPaste()
    {
        if ( getSelectedBookmarks().length + getSelectedSearchResults().length + getSelectedSearches().length
            + getSelectedConnections().length + getSelectedAttributes().length + getSelectedValues().length == 0
            && getSelectedEntries().length == 1 )
        {

            Object content = this.getFromClipboard( EntryTransfer.getInstance() );
            if ( content != null && content instanceof IEntry[] )
            {
                IEntry[] entries = ( IEntry[] ) content;
                return entries;
            }
        }

        return null;
    }


    /**
     * Conditions: 
     * <li>a search or category is selected</li> 
     * <li>there are searches in clipboard</li>
     * 
     * @return
     */
    private ISearch[] getSearchesToPaste()
    {
        if ( getSelectedBookmarks().length + getSelectedSearchResults().length + getSelectedEntries().length
            + getSelectedConnections().length + getSelectedAttributes().length + getSelectedValues().length == 0
            && ( getSelectedSearches().length + getSelectedBrowserViewCategories().length > 0 ) )
        {
            Object content = this.getFromClipboard( SearchTransfer.getInstance() );
            if ( content != null && content instanceof ISearch[] )
            {
                ISearch[] searches = ( ISearch[] ) content;
                return searches;
            }
        }

        return null;
    }


    /**
     * Conditions: 
     * <li>an entry, search result, or bookmark is seleted</li>
     * <li>there are values in clipboard</li>
     * 
     * @return
     */
    private IValue[] getValuesToPaste()
    {
        if ( ( getSelectedAttributes().length + getSelectedValues().length + getSelectedSearchResults().length
            + getSelectedBookmarks().length + getSelectedSearches().length + getSelectedConnections().length == 0 && ( getSelectedEntries().length == 1 ) )
            || ( getSelectedAttributes().length + getSelectedValues().length + getSelectedEntries().length
                + getSelectedSearchResults().length + getSelectedSearches().length + getSelectedConnections().length == 0 && ( getSelectedBookmarks().length == 1 ) )
            || ( getSelectedAttributes().length + getSelectedValues().length + getSelectedEntries().length
                + getSelectedBookmarks().length + getSelectedSearches().length + getSelectedConnections().length == 0 && ( getSelectedSearchResults().length == 1 ) ) )
        {
            Object content = this.getFromClipboard( ValuesTransfer.getInstance() );
            if ( content != null && content instanceof IValue[] )
            {
                IValue[] values = ( IValue[] ) content;
                return values;
            }
        }

        return null;
    }

}
