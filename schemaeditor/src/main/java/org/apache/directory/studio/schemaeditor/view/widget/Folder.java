/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
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
    private String name = ""; //$NON-NLS-1$


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
                name = Messages.getString( "Folder.AttributeTypes" ); //$NON-NLS-1$
                break;
            case OBJECT_CLASS:
                name = Messages.getString( "Folder.ObjectClasses" ); //$NON-NLS-1$
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
