package org.apache.directory.studio.schemaeditor.view.widget;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.directory.studio.schemaeditor.model.difference.Difference;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;


/**
 * This used to represent a folder in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class Folder
{
    /** The children */
    protected List<Difference> children;

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


    /**
     * Creates a new instance of Folder.
     *
     * @param type
     *      the type of the Folder
     * @param parent
     *      the parent TreeNode
     */
    public Folder( FolderType type )
    {
        this.type = type;

        switch ( type )
        {
            case ATTRIBUTE_TYPE:
                name = NAME_AT;
                break;
            case OBJECT_CLASS:
                name = NAME_OC;
                break;
        }
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


    public boolean hasChildren()
    {
        if ( children == null )
        {
            return false;
        }

        return !children.isEmpty();
    }


    public List<Difference> getChildren()
    {
        if ( children == null )
        {
            children = new ArrayList<Difference>();
        }

        return children;
    }


    public void addChild( Difference diff )
    {
        if ( children == null )
        {
            children = new ArrayList<Difference>();
        }

        if ( !children.contains( diff ) )
        {
            children.add( diff );
        }
    }


    public void removeChild( TreeNode node )
    {
        if ( children != null )
        {
            children.remove( node );
        }
    }


    public boolean addAllChildren( Collection<? extends Difference> c )
    {
        if ( children == null )
        {
            children = new ArrayList<Difference>();
        }

        return children.addAll( c );
    }
}
