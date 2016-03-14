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
package org.apache.directory.studio.schemaeditor.view.wrappers;


import org.eclipse.osgi.util.NLS;


/**
 * This used to represent a folder in a TreeViewer.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class Folder extends AbstractTreeNode
{
    /**
     * This enum represents the different types of folders.
     *
     * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
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
    public Folder( FolderType type, TreeNode parent )
    {
        super( parent );
        this.type = type;

        switch ( type )
        {
            case ATTRIBUTE_TYPE:
                name = Messages.getString( "Folder.AttributeTypes" ); //$NON-NLS-1$
                break;
            case OBJECT_CLASS:
                name = Messages.getString( "Folder.ObjectClasses" ); //$NON-NLS-1$
                break;
            case ERROR:
                name = Messages.getString( "Folder.Errors" ); //$NON-NLS-1$
                break;
            case WARNING:
                name = Messages.getString( "Folder.Warnings" ); //$NON-NLS-1$
                break;
            default:
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


    /**
     * {@inheritDoc}
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof Folder )
        {
            Folder folder = ( Folder ) obj;

            if ( ( getParent() != null ) && ( !getParent().equals( folder.getParent() ) ) )
            {
                return false;
            }

            if ( !getType().equals( folder.getType() ) )
            {
                return false;
            }

            if ( ( getName() != null ) && ( !getName().equals( folder.getName() ) ) )
            {
                return false;
            }

            return true;
        }

        return false;
    }


    /**
     * {@inheritDoc}
     */
    public int hashCode()
    {
        int result = super.hashCode();

        if ( name != null )
        {
            result = 37 * result + name.hashCode();
        }

        if ( type != null )
        {
            result = 37 * result + type.hashCode();
        }

        return result;
    }


    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return NLS.bind( Messages.getString( "Folder.Folder" ), new Object[] { type, fParent } ); //$NON-NLS-1$
    }
}
