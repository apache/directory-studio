package org.apache.directory.studio.combinededitor.editor;


import org.eclipse.swt.custom.CTabItem;


/**
 * This interface defines a page for the editor.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface ICombinedEntryEditorPage
{
    /**
     * Disposes any allocated resource.
     */
    public void dispose();


    /**
     * This method is called when editor input has changed.
     */
    public void editorInputChanged();


    /**
     * Gets the associated editor.
     *
     * @return
     *      the associated editor
     */
    public CombinedEntryEditor getEditor();


    /**
     * Gets the {@link CTabItem} associated with the editor page.
     *
     * @return
     *      the {@link CTabItem} associated with the editor page
     */
    public CTabItem getTabItem();


    /**
     * Initializes the control of the page.
     */
    public void init();


    /**
     * Returns whether or not the editor page has been initialized.
     *
     * @return
     *      <code>true</code> if the editor page has been initialized,
     *      <code>false</code> if not.
     */
    public boolean isInitialized();


    /**
     * Asks this part to take focus within the workbench. Parts must
     * assign focus to one of the controls contained in the part's
     * parent composite.
     */
    public void setFocus();


    /**
     * This method is called when then editor page needs to be updated. 
     */
    public void update();
}
