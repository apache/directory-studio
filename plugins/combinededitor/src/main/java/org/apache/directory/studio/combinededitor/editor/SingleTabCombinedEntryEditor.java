package org.apache.directory.studio.combinededitor.editor;


/**
 * An entry editor the opens all entries in one single editor tab.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SingleTabCombinedEntryEditor extends CombinedEntryEditor
{
    /**
    * {@inheritDoc}
    */
    public boolean isAutoSave()
    {
        return false;
    }
}
