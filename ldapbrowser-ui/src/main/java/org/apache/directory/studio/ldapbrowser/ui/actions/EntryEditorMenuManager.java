package org.apache.directory.studio.ldapbrowser.ui.actions;


import java.util.Collection;

import org.apache.directory.studio.entryeditors.EntryEditorExtension;
import org.apache.directory.studio.ldapbrowser.common.actions.BrowserSelectionUtils;
import org.apache.directory.studio.ldapbrowser.core.model.IBookmark;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearchResult;
import org.apache.directory.studio.ldapbrowser.ui.BrowserUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.PlatformUI;


public class EntryEditorMenuManager extends MenuManager implements IMenuListener
{
    protected ISelectionProvider selectionProvider;


    /**
     * Creates a menu manager.  The text and id are <code>null</code>.
     * Typically used for creating a context menu, where it doesn't need to be referred to by id.
     */
    public EntryEditorMenuManager( ISelectionProvider selectionProvider )
    {
        super( "Open With" );
        this.selectionProvider = selectionProvider;
        addMenuListener( this );
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    public void menuAboutToShow( IMenuManager manager )
    {
        // As the Menu Manager is dynamic, we need to 
        // remove all the previously added actions
        removeAll();

        // Getting the entry editors and creating an action for each
        Collection<EntryEditorExtension> entryEditors = BrowserUIPlugin.getDefault().getEntryEditorManager()
            .getSortedEntryEditorExtensions();
        for ( EntryEditorExtension entryEditorExtension : entryEditors )
        {
            add( createAction( entryEditorExtension ) );
        }

        // Separator
        add( new Separator() );

        // Preferences Action
        add( new Action( "Preferences..." )
        {
        } );
    }


    /**
     * Creates an action for the given entry editor.
     *
     * @param entryEditorExtension
     *      the entry editor
     * @return
     *      an action associated with the entry editor
     */
    private IAction createAction( EntryEditorExtension entryEditorExtension )
    {
        final EntryEditorExtension entryEditorExtension2 = entryEditorExtension;
        Action action = new Action( entryEditorExtension.getName() )
        {
            public void run()
            {
                MessageDialog.openInformation( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    entryEditorExtension2.getName(), "Instead of this window, the \"" + entryEditorExtension2.getName()
                        + "\" entry editor should be opened." );
            }
        };

        // TODO Add enable/disable action if the entry editor can "handle" the entry.
        // TODO Or do include this entry editor in the list of available entry editors.
        //action.setEnabled( false );

        return action;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVisible()
    {
        ISelection selection = selectionProvider.getSelection();

        IBookmark[] selectedBookMarks = BrowserSelectionUtils.getBookmarks( selection );
        IEntry[] selectedEntries = BrowserSelectionUtils.getEntries( selection );
        ISearchResult[] selectedSearchResults = BrowserSelectionUtils.getSearchResults( selection );

        return ( selectedSearchResults.length + selectedBookMarks.length + selectedEntries.length == 1 );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isDynamic()
    {
        return true;
    }
}
