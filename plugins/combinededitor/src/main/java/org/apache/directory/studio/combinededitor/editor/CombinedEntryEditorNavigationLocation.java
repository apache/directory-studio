package org.apache.directory.studio.combinededitor.editor;


import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.entryeditors.EntryEditorInput;
import org.apache.directory.studio.entryeditors.EntryEditorManager;
import org.apache.directory.studio.entryeditors.EntryEditorUtils;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.INavigationLocation;
import org.eclipse.ui.NavigationLocation;


/**
 * This class is used to mark the entry editor input to the navigation history.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CombinedEntryEditorNavigationLocation extends NavigationLocation
{
    private static final String BOOKMARK_TAG = "BOOKMARK"; //$NON-NLS-1$
    private static final String CONNECTION_TAG = "CONNECTION"; //$NON-NLS-1$
    private static final String DN_TAG = "DN"; //$NON-NLS-1$
    private static final String EXTENSION_TAG = "EXTENSION"; //$NON-NLS-1$
    private static final String SEARCH_TAG = "SEARCH"; //$NON-NLS-1$
    private static final String TYPE_BOOKMARK_VALUE = "IBookmark"; //$NON-NLS-1$
    private static final String TYPE_SEARCHRESULT_VALUE = "ISearchResult"; //$NON-NLS-1$
    private static final String TYPE_TAG = "TYPE"; //$NON-NLS-1$
    private static final String TYPE_ENTRY_VALUE = "IEntry"; //$NON-NLS-1$


    /**
     * Creates a new instance of EntryEditorNavigationLocation.
     *
     * @param editor the entry editor
     */
    protected CombinedEntryEditorNavigationLocation( IEditorPart editor )
    {
        super( editor );
    }


    /**
     * {@inheritDoc}
     */
    public String getText()
    {
        String text = EntryEditorUtils.getHistoryNavigationText( getEntryEditorInput() );
        return text != null ? text : super.getText();
    }


    /**
     * {@inheritDoc}
     */
    public void saveState( IMemento memento )
    {
        EntryEditorInput eei = getEntryEditorInput();
        if ( eei != null )
        {
            memento.putString( EXTENSION_TAG, eei.getExtension().getId() );
            if ( eei.getEntryInput() != null )
            {
                IEntry entry = eei.getEntryInput();
                memento.putString( TYPE_TAG, TYPE_ENTRY_VALUE );
                memento.putString( DN_TAG, entry.getDn().getName() );
                memento.putString( CONNECTION_TAG, entry.getBrowserConnection().getConnection().getId() );
            }
            else if ( eei.getSearchResultInput() != null )
            {
                ISearchResult searchResult = eei.getSearchResultInput();
                memento.putString( TYPE_TAG, TYPE_SEARCHRESULT_VALUE );
                memento.putString( DN_TAG, searchResult.getDn().getName() );
                memento.putString( SEARCH_TAG, searchResult.getSearch().getName() );
                memento.putString( CONNECTION_TAG, searchResult.getSearch().getBrowserConnection().getConnection()
                    .getId() );
            }
            else if ( eei.getBookmarkInput() != null )
            {
                IBookmark bookmark = eei.getBookmarkInput();
                memento.putString( TYPE_TAG, TYPE_BOOKMARK_VALUE );
                memento.putString( BOOKMARK_TAG, bookmark.getName() );
                memento.putString( CONNECTION_TAG, bookmark.getBrowserConnection().getConnection().getId() );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void restoreState( IMemento memento )
    {
        try
        {
            String type = memento.getString( TYPE_TAG );
            String extensionId = memento.getString( EXTENSION_TAG );
            EntryEditorManager entryEditorManager = BrowserUIPlugin.getDefault().getEntryEditorManager();
            EntryEditorExtension entryEditorExtension = entryEditorManager.getEntryEditorExtension( extensionId );
            if ( TYPE_ENTRY_VALUE.equals( type ) )
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnectionById( memento.getString( CONNECTION_TAG ) );
                Dn dn = new Dn( memento.getString( DN_TAG ) );
                IEntry entry = connection.getEntryFromCache( dn );
                super.setInput( new EntryEditorInput( entry, entryEditorExtension ) );
            }
            else if ( TYPE_SEARCHRESULT_VALUE.equals( type ) )
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnectionById( memento.getString( CONNECTION_TAG ) );
                ISearch search = connection.getSearchManager().getSearch( memento.getString( SEARCH_TAG ) );
                ISearchResult[] searchResults = search.getSearchResults();
                Dn dn = new Dn( memento.getString( DN_TAG ) );
                for ( int i = 0; i < searchResults.length; i++ )
                {
                    if ( dn.equals( searchResults[i].getDn() ) )
                    {
                        super.setInput( new EntryEditorInput( searchResults[i], entryEditorExtension ) );
                        break;
                    }
                }
            }
            else if ( TYPE_BOOKMARK_VALUE.equals( type ) )
            {
                IBrowserConnection connection = BrowserCorePlugin.getDefault().getConnectionManager()
                    .getBrowserConnectionById( memento.getString( CONNECTION_TAG ) );
                IBookmark bookmark = connection.getBookmarkManager().getBookmark( memento.getString( BOOKMARK_TAG ) );
                super.setInput( new EntryEditorInput( bookmark, entryEditorExtension ) );
            }
        }
        catch ( LdapInvalidDnException e )
        {
            e.printStackTrace();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void restoreLocation()
    {
    }


    /**
     * {@inheritDoc}
     */
    public boolean mergeInto( INavigationLocation currentLocation )
    {
        if ( currentLocation == null )
        {
            return false;
        }

        if ( getClass() != currentLocation.getClass() )
        {
            return false;
        }

        CombinedEntryEditorNavigationLocation location = ( CombinedEntryEditorNavigationLocation ) currentLocation;
        Object other = location.getEntryEditorInput().getInput();
        Object entry = getEntryEditorInput().getInput();

        if ( other == null && entry == null )
        {
            return true;
        }
        else if ( other == null || entry == null )
        {
            return false;
        }
        else
        {
            return entry.equals( other );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
    }


    /**
     * Gets the input.
     *
     * @return the input
     */
    private EntryEditorInput getEntryEditorInput()
    {
        Object editorInput = getInput();
        if ( editorInput != null && editorInput instanceof EntryEditorInput )
        {
            EntryEditorInput entryEditorInput = ( EntryEditorInput ) editorInput;
            return entryEditorInput;
        }

        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return "" + getEntryEditorInput().getInput(); //$NON-NLS-1$
    }
}
