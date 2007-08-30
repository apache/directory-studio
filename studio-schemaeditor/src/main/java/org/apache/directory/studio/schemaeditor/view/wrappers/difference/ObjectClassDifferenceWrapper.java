package org.apache.directory.studio.schemaeditor.view.wrappers.difference;


import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;


/**
 * This class represent the wrapper for an object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassDifferenceWrapper extends AbstractDifferenceWrapper
{
    /**
     * Creates a new instance of ObjectClassDifferenceWrapper.
     *
     * @param originalObject
     *      the original object class
     * @param modifiedObject
     *      the modified object class
     * @param parent
     *      the parent TreeNode
     */
    public ObjectClassDifferenceWrapper( Object originalObject, Object modifiedObject, TreeNode parent )
    {
        super( originalObject, modifiedObject, parent );
    }


    /**
     * Creates a new instance of ObjectClassDifferenceWrapper.
     *
     * @param originalObject
     *      the original object class
     * @param modifiedObject
     *      the modified object class
     * @param state
     *      the state of the wrapper
     * @param parent
     *      the parent TreeNode
     */
    public ObjectClassDifferenceWrapper( Object originalObject, Object modifiedObject, WrapperState state,
        TreeNode parent )
    {
        super( originalObject, modifiedObject, state, parent );
    }
}
