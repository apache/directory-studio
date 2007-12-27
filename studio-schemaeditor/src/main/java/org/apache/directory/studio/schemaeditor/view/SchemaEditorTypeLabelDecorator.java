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


import org.apache.directory.shared.ldap.schema.ObjectClassTypeEnum;
import org.apache.directory.shared.ldap.schema.UsageEnum;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * This class is the Schemas Editor Type Label Decorator. 
 * It displays specific icons overlays for attribute types and object classes.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaEditorTypeLabelDecorator extends LabelProvider implements ILightweightLabelDecorator
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    public void decorate( Object element, IDecoration decoration )
    {
        if ( element instanceof AttributeTypeWrapper )
        {
            UsageEnum usage = ( ( AttributeTypeWrapper ) element ).getAttributeType().getUsage();
            if ( usage == UsageEnum.USER_APPLICATIONS )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_ATTRIBUTE_TYPE_OVERLAY_USER_APPLICATION ), IDecoration.BOTTOM_RIGHT );
            }
            else if ( ( usage == UsageEnum.DIRECTORY_OPERATION ) || ( usage == UsageEnum.DISTRIBUTED_OPERATION )
                || ( usage == UsageEnum.DSA_OPERATION ) )
            {
                decoration.addOverlay( Activator.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_ATTRIBUTE_TYPE_OVERLAY_OPERATION ), IDecoration.BOTTOM_RIGHT );
            }
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClassTypeEnum classType = ( ( ObjectClassWrapper ) element ).getObjectClass().getType();
            if ( classType == ObjectClassTypeEnum.ABSTRACT )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS_OVERLAY_ABSTRACT ), IDecoration.BOTTOM_RIGHT );
            }
            else if ( classType == ObjectClassTypeEnum.STRUCTURAL )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS_OVERLAY_STRUCTURAL ), IDecoration.BOTTOM_RIGHT );
            }
            else if ( classType == ObjectClassTypeEnum.AUXILIARY )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS_OVERLAY_AUXILIARY ), IDecoration.BOTTOM_RIGHT );
            }
        }
        else if ( element instanceof AttributeTypeImpl )
        {
            UsageEnum usage = ( ( AttributeTypeImpl ) element ).getUsage();
            if ( usage == UsageEnum.USER_APPLICATIONS )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_ATTRIBUTE_TYPE_OVERLAY_USER_APPLICATION ), IDecoration.BOTTOM_RIGHT );
            }
            else if ( ( usage == UsageEnum.DIRECTORY_OPERATION ) || ( usage == UsageEnum.DISTRIBUTED_OPERATION )
                || ( usage == UsageEnum.DSA_OPERATION ) )
            {
                decoration.addOverlay( Activator.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_ATTRIBUTE_TYPE_OVERLAY_OPERATION ), IDecoration.BOTTOM_RIGHT );
            }
        }
        else if ( element instanceof ObjectClassImpl )
        {
            ObjectClassTypeEnum classType = ( ( ObjectClassImpl ) element ).getType();
            if ( classType == ObjectClassTypeEnum.ABSTRACT )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS_OVERLAY_ABSTRACT ), IDecoration.BOTTOM_RIGHT );
            }
            else if ( classType == ObjectClassTypeEnum.STRUCTURAL )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS_OVERLAY_STRUCTURAL ), IDecoration.BOTTOM_RIGHT );
            }
            else if ( classType == ObjectClassTypeEnum.AUXILIARY )
            {
                decoration.addOverlay( AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                    PluginConstants.IMG_OBJECT_CLASS_OVERLAY_AUXILIARY ), IDecoration.BOTTOM_RIGHT );
            }
        }
    }
}
