package org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.difference;


import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.TreeNode;


/**
 * This class represent the wrapper for an attribute type.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributeTypeDifferenceWrapper extends AbstractDifferenceWrapper
{
    /**
     * Creates a new instance of AttributeTypeDifferenceWrapper.
     *
     * @param originalObject
     *      the original attribute type
     * @param modifiedObject
     *      the modified attribute type
     * @param parent
     *      the parent TreeNode
     */
    public AttributeTypeDifferenceWrapper( Object originalObject, Object modifiedObject, TreeNode parent )
    {
        super( originalObject, modifiedObject, parent );
    }


    /**
     * Creates a new instance of AttributeTypeDifferenceWrapper.
     *
     * @param originalObject
     *      the original attribute type
     * @param modifiedObject
     *      the modified attribute type
     * @param state
     *      the state of the wrapper
     * @param parent
     *      the parent TreeNode
     */
    public AttributeTypeDifferenceWrapper( Object originalObject, Object modifiedObject, WrapperState state, TreeNode parent )
    {
        super( originalObject, modifiedObject, state, parent );
    }
}
