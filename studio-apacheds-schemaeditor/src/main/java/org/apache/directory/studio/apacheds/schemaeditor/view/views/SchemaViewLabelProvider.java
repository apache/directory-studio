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
package org.apache.directory.studio.apacheds.schemaeditor.view.views;


import org.apache.directory.studio.apacheds.schemaeditor.Activator;
import org.apache.directory.studio.apacheds.schemaeditor.PluginConstants;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWrapper;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the LabelProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaViewLabelProvider extends LabelProvider
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element )
    {
        if ( element instanceof SchemaWrapper )
        {
            SchemaWrapper sw = ( SchemaWrapper ) element;

            return sw.getSchema().getName();
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            AttributeTypeWrapper atw = ( AttributeTypeWrapper ) element;

            return atw.getAttributeType().getOid();
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClassWrapper ocw = ( ObjectClassWrapper ) element;

            return ocw.getObjectClass().getOid();
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;

            return folder.getName() + " (" + folder.getChildren().size() + ")";
        }

        // Default
        return element.toString();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element )
    {
        if ( element instanceof SchemaWrapper )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_SCHEMA )
                .createImage();
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_ATTRIBUTE_TYPE )
                .createImage();
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_OBJECT_CLASS )
                .createImage();
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;
            
            switch ( folder.getType() )
            {
                case ATTRIBUTE_TYPE:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_FOLDER_AT )
                    .createImage();
                case OBJECT_CLASS:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_FOLDER_OC )
                    .createImage();
                case NONE:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_FOLDER )
                    .createImage();
            }
        }

        // Default
        return null;
    }
}
