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
