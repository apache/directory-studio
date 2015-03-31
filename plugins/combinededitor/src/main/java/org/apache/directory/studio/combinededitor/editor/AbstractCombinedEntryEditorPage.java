package org.apache.directory.studio.combinededitor.editor;


import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


/**
 * This interface defines a page for the editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractCombinedEntryEditorPage implements ICombinedEntryEditorPage
{
    /** The associated editor */
    private CombinedEntryEditor editor;

    /** The flag to know whether or not the editor page has been initialized */
    private boolean initialized = false;

    /** The {@link CTabItem} associated with the editor page */
    private CTabItem tabItem;


    /**
     * Creates a new instance of AbstractTemplateEntryEditorPage.
     *
     * @param editor
     *      the associated editor
     */
    public AbstractCombinedEntryEditorPage( CombinedEntryEditor editor )
    {
        this.editor = editor;
    }


    /**
     * {@inheritDoc}
     */
    public void dispose()
    {
        // Default implementation does nothing
    }


    /**
     * {@inheritDoc}
     */
    public void editorInputChanged()
    {
        // Default implementation does nothing
    }


    /**
     * {@inheritDoc}
     */
    public CombinedEntryEditor getEditor()
    {
        return editor;
    }


    /**
     * {@inheritDoc}
     */
    public CTabItem getTabItem()
    {
        return tabItem;
    }


    /**
     * {@inheritDoc}
     */
    public void init()
    {
        setInitialized( true );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isInitialized()
    {
        return initialized;
    }


    /**
     * {@inheritDoc}
     */
    public void setFocus()
    {
        // Default implementation does nothing
    }


    /**
     * Sets the flag to know whether or not the editor page has been initialized.
     *
     * @param initialized
     *      the value
     */
    protected void setInitialized( boolean initialized )
    {
        this.initialized = initialized;
    }


    /**
     * {@inheritDoc}
     */
    public void update()
    {
        // Default implementation does nothing
    }


    /**
     * Sets the {@link CTabItem} associated with the editor page.
     * 
     * @param tabItem
     *      the {@link CTabItem} associated with the editor page
     */
    protected void setTabItem( CTabItem tabItem )
    {
        this.tabItem = tabItem;

        // Registering a listener on the editor's tab folder
        if ( ( getEditor() != null ) && ( getEditor().getTabFolder() != null )
            && ( !getEditor().getTabFolder().isDisposed() ) )
        {
            getEditor().getTabFolder().addSelectionListener( new SelectionAdapter()
            {
                public void widgetSelected( SelectionEvent e )
                {
                    tabFolderSelectionChanged();
                }
            } );
        }
    }


    /**
     * This method is called when the TabFolder selection is changed.
     */
    private void tabFolderSelectionChanged()
    {
        if ( ( getEditor() != null ) && ( getEditor().getTabFolder() != null )
            && ( !getEditor().getTabFolder().isDisposed() ) )
        {
            // Getting the selected tab
            CTabItem selectedTab = getEditor().getTabFolder().getSelection();

            // Verifying if the selected tab is this page's tab
            if ( ( selectedTab != null ) && ( selectedTab.equals( tabItem ) ) )
            {
                // Checking if the page needs to be initialized or updated
                if ( !isInitialized() )
                {
                    // Initializing the page
                    init();
                }
                else
                {
                    // Updating the page
                    update();
                }

                // Setting the correct focus
                setFocus();
            }
        }
    }
}
