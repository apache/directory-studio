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
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaErrorWrapper;
import org.apache.directory.studio.apacheds.schemaeditor.view.wrappers.SchemaWarningWrapper;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class implements the LabelProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ProblemsViewLabelProvider extends LabelProvider implements ITableLabelProvider
{
    public Image getColumnImage( Object element, int columnIndex )
    {
        if ( columnIndex == 0 )
        {
            if ( element instanceof SchemaErrorWrapper )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_PROBLEMS_ERROR ).createImage();
            }
            else if ( element instanceof SchemaWarningWrapper )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_PROBLEMS_WARNING ).createImage();
            }
            else if ( element instanceof Folder )
            {
                return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_PROBLEMS_GROUP ).createImage();
            }
        }

        // Default
        return null;
    }


    public String getColumnText( Object element, int columnIndex )
    {
        if ( element instanceof SchemaErrorWrapper )
        {
            SchemaErrorWrapper errorWrapper = ( SchemaErrorWrapper ) element;

            if ( columnIndex == 0 )
            {
                return errorWrapper.getSchemaError().toString();
            }
            else if ( columnIndex == 1 )
            {
                String name = errorWrapper.getSchemaError().getSource().getName();

                if ( ( name != null ) && ( !name.equals( "" ) ) )
                {
                    return name;
                }
                else
                {
                    return errorWrapper.getSchemaError().getSource().getOid();
                }
            }

        }
        else if ( element instanceof SchemaWarningWrapper )
        {
            SchemaWarningWrapper warningWrapper = ( SchemaWarningWrapper ) element;

            if ( columnIndex == 0 )
            {
                return warningWrapper.getSchemaWarning().toString();
            }
            else if ( columnIndex == 1 )
            {
                String name = warningWrapper.getSchemaWarning().getSource().getName();

                if ( ( name != null ) && ( !name.equals( "" ) ) )
                {
                    return name;
                }
                else
                {
                    return warningWrapper.getSchemaWarning().getSource().getOid();
                }
            }
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;
            if ( columnIndex == 0 )
            {
                return folder.getName() + " (" + folder.getChildren().size() + ")";
            }
            else
            {
                return "";
            }
        }

        // Default
        return element.toString();
    }
}
