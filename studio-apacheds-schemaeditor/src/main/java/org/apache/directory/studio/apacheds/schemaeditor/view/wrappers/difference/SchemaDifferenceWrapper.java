package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.difference;


import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;


/**
 * This class represent the wrapper for a schema.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaDifferenceWrapper extends AbstractDifferenceWrapper
{
    /**
     * Creates a new instance of SchemaDifferenceWrapper.
     *
     * @param originalObject
     *      the original schema
     * @param modifiedObject
     *      the modified schema
     * @param parent
     *      the parent TreeNode
     */
    public SchemaDifferenceWrapper( Object originalObject, Object modifiedObject, TreeNode parent )
    {
        super( originalObject, modifiedObject, parent );
    }


    /**
     * Creates a new instance of SchemaDifferenceWrapper.
     *
     * @param originalObject
     *      the original schema
     * @param modifiedObject
     *      the modified schema
     * @param state
     *      the state of the wrapper
     * @param parent
     *      the parent TreeNode
     */
    public SchemaDifferenceWrapper( Object originalObject, Object modifiedObject, WrapperState state, TreeNode parent )
    {
        super( originalObject, modifiedObject, state, parent );
    }
}
