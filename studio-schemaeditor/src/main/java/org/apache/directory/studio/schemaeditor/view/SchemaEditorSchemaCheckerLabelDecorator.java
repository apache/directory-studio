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
package org.apache.directory.studio.schemaeditor.view;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.schemachecker.SchemaChecker;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder.FolderType;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is the Schemas Editor Schema Checker Label Decorator. 
 * It displays specific icons overlays for attribute types and object classes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaEditorSchemaCheckerLabelDecorator extends LabelProvider implements ILightweightLabelDecorator
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    public void decorate( Object element, IDecoration decoration )
    {
        SchemaChecker schemaChecker = Activator.getDefault().getSchemaChecker();

        if ( element instanceof AttributeTypeWrapper )
        {
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) element ).getAttributeType();

            if ( schemaChecker.hasErrors( at ) )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OVERLAY_ERROR ), IDecoration.BOTTOM_LEFT );
                return;
            }

            if ( schemaChecker.hasWarnings( at ) )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OVERLAY_WARNING ), IDecoration.BOTTOM_LEFT );
            }
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) element ).getObjectClass();

            if ( schemaChecker.hasErrors( oc ) )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OVERLAY_ERROR ), IDecoration.BOTTOM_LEFT );
                return;
            }

            if ( schemaChecker.hasWarnings( oc ) )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OVERLAY_WARNING ), IDecoration.BOTTOM_LEFT );
            }
        }
        else if ( element instanceof SchemaWrapper )
        {
            Schema schema = ( ( SchemaWrapper ) element ).getSchema();

            for ( AttributeTypeImpl at : schema.getAttributeTypes() )
            {
                if ( schemaChecker.hasErrors( at ) )
                {
                    decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_OVERLAY_ERROR ), IDecoration.BOTTOM_LEFT );
                    return;
                }

                if ( schemaChecker.hasWarnings( at ) )
                {
                    decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_OVERLAY_WARNING ), IDecoration.BOTTOM_LEFT );
                }
            }

            for ( ObjectClassImpl oc : schema.getObjectClasses() )
            {
                if ( schemaChecker.hasErrors( oc ) )
                {
                    decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_OVERLAY_ERROR ), IDecoration.BOTTOM_LEFT );
                    return;
                }

                if ( schemaChecker.hasWarnings( oc ) )
                {
                    decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_OVERLAY_WARNING ), IDecoration.BOTTOM_LEFT );
                }
            }
        }
        else if ( element instanceof Folder )
        {
            //TODO Add a workaround
//            Folder folder = ( Folder ) element;
//            
//            Schema schema = ( ( SchemaWrapper ) folder.getParent() ).getSchema();
//
//            if ( folder.getType().equals( FolderType.ATTRIBUTE_TYPE ) )
//            {
//                for ( AttributeTypeImpl at : schema.getAttributeTypes() )
//                {
//                    if ( schemaChecker.hasErrors( at ) )
//                    {
//                        decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
//                            PluginConstants.IMG_OVERLAY_ERROR ), IDecoration.BOTTOM_LEFT );
//                        return;
//                    }
//
//                    if ( schemaChecker.hasWarnings( at ) )
//                    {
//                        decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
//                            PluginConstants.IMG_OVERLAY_WARNING ), IDecoration.BOTTOM_LEFT );
//                    }
//                }
//            }
//            else if ( folder.getType().equals( FolderType.OBJECT_CLASS ) )
//            {
//                for ( ObjectClassImpl oc : schema.getObjectClasses() )
//                {
//                    if ( schemaChecker.hasErrors( oc ) )
//                    {
//                        decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
//                            PluginConstants.IMG_OVERLAY_ERROR ), IDecoration.BOTTOM_LEFT );
//                        return;
//                    }
//
//                    if ( schemaChecker.hasWarnings( oc ) )
//                    {
//                        decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
//                            PluginConstants.IMG_OVERLAY_WARNING ), IDecoration.BOTTOM_LEFT );
//                    }
//                }
//            }
        }
    }
}
