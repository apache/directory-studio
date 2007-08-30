package org.apache.directory.studio.schemaeditor.view.wrappers;


/**
 * This used to represent a folder in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Folder extends AbstractTreeNode
{
    /**
     * This enum represents the different types of folders.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
     * @version $Rev$, $Date$
     */
    public enum FolderType
    {
        NONE, ATTRIBUTE_TYPE, OBJECT_CLASS, ERROR, WARNING
    }

    /** The type of the Folder */
    private FolderType type = FolderType.NONE;

    /** The name of the Folder */
    private String name = "";

    private static final String NAME_AT = "Attribute Types";
    private static final String NAME_OC = "Object Classes";
    private static final String NAME_ERROR = "Errors";
    private static final String NAME_WARNING = "Warnings";


    /**
     * Creates a new instance of Folder.
     *
     * @param type
     *      the type of the Folder
     * @param parent
     *      the parent TreeNode
     */
    public Folder( FolderType type, TreeNode parent )
    {
        super( parent );
        this.type = type;

        switch ( type )
        {
            case ATTRIBUTE_TYPE:
                name = NAME_AT;
                break;
            case OBJECT_CLASS:
                name = NAME_OC;
                break;
            case ERROR:
                name = NAME_ERROR;
                break;
            case WARNING:
                name = NAME_WARNING;
                break;
        }
    }


    /**
     * Creates a new instance of Folder.
     *
     * @param type
     *      the type of the Folder
     * @param name
     *      the name of the Folder
     * @param parent
     *      the parent TreeNode
     */
    public Folder( FolderType type, String name, TreeNode parent )
    {
        super( parent );
        this.type = type;
        this.name = name;
    }


    /**
     * Get the type of the Folder.
     *
     * @return
     *      the type of the Folder
     */
    public FolderType getType()
    {
        return type;
    }


    /**
     * Gets the name of the Folder.
     * 
     * @return
     *      the name of the Folder
     */
    public String getName()
    {
        return name;
    }
}
