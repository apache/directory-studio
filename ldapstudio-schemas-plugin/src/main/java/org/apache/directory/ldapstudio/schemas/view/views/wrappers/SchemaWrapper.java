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

package org.apache.directory.ldapstudio.schemas.view.views.wrappers;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.model.Schema;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Nasty trick to display object-classes attributes in the tree-viewer
 */
public class SchemaWrapper extends TreeNode
{
    /** The associated schema */
    private Schema mySchema;


    /**
     * Default constructor
     * @param mySchema
     * @param parent
     */
    public SchemaWrapper( Schema mySchema, ITreeNode parent )
    {
        super( parent );
        this.mySchema = mySchema;
    }


    /**
     * @return the wrapped schema
     */
    public Schema getMySchema()
    {
        return mySchema;
    }


    /* (non-Javadoc)
     * @see org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.TreeNode#getImage()
     */
    public Image getImage()
    {
        if ( this.mySchema.type.equals( Schema.SchemaType.coreSchema ) )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA_CORE )
                .createImage();
        }
        else
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA )
                .createImage();
        }
    }



    /**
     * Gets the name of the schema.
     *
     * @return
     *      the name of the schema
     */
    public String getName()
    {
        String res = ""; //$NON-NLS-1$
        if ( mySchema.hasBeenModified() )
            res += "*"; //$NON-NLS-1$
        return res + mySchema.getName();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof SchemaWrapper )
        {
            SchemaWrapper compared = ( SchemaWrapper ) obj;
            return compared.getName().equals( this.getName() );
        }
        return false;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return mySchema.getName() + " wrapper"; //$NON-NLS-1$
    }
}
