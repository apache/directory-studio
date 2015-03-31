package org.apache.directory.studio.combinededitor.editor;


/**
 * An entry editor the opens entries in a single editor for each entry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MultiTabCombinedEntryEditor extends CombinedEntryEditor
{
    /**
     * {@inheritDoc}
     */
    public boolean isAutoSave()
    {
        return false;
    }
}
