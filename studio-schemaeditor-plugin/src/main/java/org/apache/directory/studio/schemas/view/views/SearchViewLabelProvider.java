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

package org.apache.directory.studio.schemas.view.views;


import org.apache.directory.studio.schemas.Activator;
import org.apache.directory.studio.schemas.PluginConstants;
import org.apache.directory.studio.schemas.model.AttributeType;
import org.apache.directory.studio.schemas.model.ObjectClass;
import org.apache.directory.studio.schemas.model.SchemaElement;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * Label provider for the search view
 *
 */
public class SearchViewLabelProvider extends LabelProvider implements ITableLabelProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage( Object element, int columnIndex )
    {
        if ( columnIndex == 0 )
        {
            if ( element instanceof ObjectClass )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS ).createImage();
            }

            if ( element instanceof AttributeType )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_ATTRIBUTE_TYPE ).createImage();
            }
        }

        return null;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText( Object element, int columnIndex )
    {
        String result = ""; //$NON-NLS-1$
        if ( element instanceof SchemaElement )
        {
            SchemaElement schemaElement = ( SchemaElement ) element;
            switch ( columnIndex )
            {
                case 0: // COMPLETED_COLUMN
                    break;
                case 1:
                    result = schemaElement.getNames()[0];
                    break;
                case 2:
                    result = schemaElement.getOriginatingSchema().getName();
                    break;
                default:
                    break;
            }
        }
        return result;
    }

}
