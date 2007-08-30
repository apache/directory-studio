package org.apache.directory.studio.schemaeditor.view.wrappers.difference;


import org.apache.directory.studio.schemaeditor.view.wrappers.AbstractTreeNode;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;


/**
 * This class represents an abstract difference wrapper.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public abstract class AbstractDifferenceWrapper extends AbstractTreeNode
{
    /** The original object */
    private Object originalObject;

    /** The modified object */
    private Object modifiedObject;

    /** The state */
    private WrapperState state;


    /**
     * Creates a new instance of AbstractDifferenceWrapper.
     *
     * @param originalObject
     *      the original object
     * @param modifiedObject
     *      the modified object
     * @param parent
     *      the parent TreeNode
     */
    public AbstractDifferenceWrapper( Object originalObject, Object modifiedObject, TreeNode parent )
    {
        super( parent );
        this.originalObject = originalObject;
        this.modifiedObject = modifiedObject;
    }


    /**
     * Creates a new instance of AbstractDifferenceWrapper.
     *
     * @param originalObject
     *      the original object
     * @param modifiedObject
     *      the modified object
     * @param state
     *      the state of the wrapper
     * @param parent
     *      the parent TreeNode
     */
    public AbstractDifferenceWrapper( Object originalObject, Object modifiedObject, WrapperState state, TreeNode parent )
    {
        super( parent );
        this.originalObject = originalObject;
        this.modifiedObject = modifiedObject;
        this.state = state;
    }


    /**
     * Gets the original object.
     *
     * @return
     *      the original object
     */
    public Object getOriginalObject()
    {
        return originalObject;
    }


    /**
     * Sets the original object.
     *
     * @param originalObject
     *      the original object
     */
    public void setOriginalObject( Object originalObject )
    {
        this.originalObject = originalObject;
    }


    /**
     * Gets the modified object.
     *
     * @return
     *      the modified object
     */
    public Object getModifiedObject()
    {
        return modifiedObject;
    }


    /**
     * Sets the modified object
     *
     * @param modifiedObject
     *      the modified object
     */
    public void setModifiedObject( Object modifiedObject )
    {
        this.modifiedObject = modifiedObject;
    }


    /**
     * Gets the state.
     *
     * @return
     *      the state
     */
    public WrapperState getState()
    {
        return state;
    }


    /**
     * Sets the state.
     *
     * @param state
     *      the state
     */
    public void setState( WrapperState state )
    {
        this.state = state;
    }
}
