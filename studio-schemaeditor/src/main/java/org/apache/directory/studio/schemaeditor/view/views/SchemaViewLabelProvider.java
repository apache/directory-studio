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
package org.apache.directory.studio.schemaeditor.view.views;


import java.util.List;

import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.Folder;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.SchemaWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.TreeNode;
import org.eclipse.jface.preference.IPreferenceStore;
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
    private static final String NONE = "(None)";

    /** The preferences store */
    private IPreferenceStore store;


    /**
     * Creates a new instance of DifferencesWidgetSchemaLabelProvider.
     */
    public SchemaViewLabelProvider()
    {
        store = Activator.getDefault().getPreferenceStore();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element )
    {
        String label = ""; //$NON-NLS-1$

        int presentation = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION );
        int labelValue = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_LABEL );
        boolean abbreviate = store.getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE );
        int abbreviateMaxLength = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE_MAX_LENGTH );
        boolean secondaryLabelDisplay = store.getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_DISPLAY );
        int secondaryLabelValue = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL );
        boolean secondaryLabelAbbreviate = store
            .getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE );
        int secondaryLabelAbbreviateMaxLength = store
            .getInt( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );
        boolean schemaLabelDisplay = store.getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_LABEL_DISPLAY );

        if ( element instanceof SchemaWrapper )
        {
            SchemaWrapper sw = ( SchemaWrapper ) element;

            return sw.getSchema().getName();
        }
        else if ( element instanceof AttributeTypeWrapper )
        {
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) element ).getAttributeType();

            // Label
            if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
            {
                String[] names = at.getNames();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = NONE;
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
            {
                String[] names = at.getNames();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = ViewUtils.concateAliases( names );
                }
                else
                {
                    label = NONE;
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
            {
                label = at.getOid();
            }
            else
            // Default
            {
                String[] names = at.getNames();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = NONE;
                }
            }

            // Abbreviate
            if ( abbreviate && ( abbreviateMaxLength < label.length() ) )
            {
                label = label.substring( 0, abbreviateMaxLength ) + "..."; //$NON-NLS-1$
            }
        }
        else if ( element instanceof ObjectClassWrapper )
        {
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) element ).getObjectClass();

            // Label
            if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
            {
                String[] names = oc.getNames();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = NONE;
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
            {
                String[] names = oc.getNames();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = ViewUtils.concateAliases( names );
                }
                else
                {
                    label = NONE;
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
            {
                label = oc.getOid();
            }
            else
            // Default
            {
                String[] names = oc.getNames();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = NONE;
                }
            }

            // Abbreviate
            if ( abbreviate && ( abbreviateMaxLength < label.length() ) )
            {
                label = label.substring( 0, abbreviateMaxLength ) + "..."; //$NON-NLS-1$
            }
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;

            if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_FLAT )
            {
                return folder.getName() + " (" + folder.getChildren().size() + ")";
            }
            else if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
            {
                return folder.getName();
            }
        }

        // Secondary Label
        if ( secondaryLabelDisplay )
        {
            String secondaryLabel = ""; //$NON-NLS-1$
            if ( element instanceof AttributeTypeWrapper )
            {
                AttributeTypeImpl at = ( ( AttributeTypeWrapper ) element ).getAttributeType();

                if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
                {
                    String[] names = at.getNames();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = names[0];
                    }
                    else
                    {
                        secondaryLabel = NONE;
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
                {
                    String[] names = at.getNames();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = ViewUtils.concateAliases( names );
                    }
                    else
                    {
                        secondaryLabel = NONE;
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
                {
                    secondaryLabel = at.getOid();
                }
            }
            else if ( element instanceof ObjectClassWrapper )
            {
                ObjectClassImpl oc = ( ( ObjectClassWrapper ) element ).getObjectClass();

                if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
                {
                    String[] names = oc.getNames();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = names[0];
                    }
                    else
                    {
                        secondaryLabel = NONE;
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
                {
                    String[] names = oc.getNames();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = ViewUtils.concateAliases( names );
                    }
                    else
                    {
                        secondaryLabel = NONE;
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
                {
                    secondaryLabel = oc.getOid();
                }
            }

            if ( secondaryLabelAbbreviate && ( secondaryLabelAbbreviateMaxLength < secondaryLabel.length() ) )
            {
                secondaryLabel = secondaryLabel.substring( 0, secondaryLabelAbbreviateMaxLength ) + "..."; //$NON-NLS-1$
            }

            label += "  [" + secondaryLabel + "]"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Number of children
        if ( presentation == PluginConstants.PREFS_SCHEMA_VIEW_SCHEMA_PRESENTATION_HIERARCHICAL )
        {
            if ( ( element instanceof AttributeTypeWrapper ) || ( element instanceof ObjectClassWrapper ) )
            {
                List<TreeNode> children = ( ( TreeNode ) element ).getChildren();

                if ( ( children != null ) && ( children.size() > 0 ) )
                {
                    label += "  (" + children.size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        // Schema Label
        if ( schemaLabelDisplay )
        {
            if ( element instanceof AttributeTypeWrapper )
            {
                label += "  [" + ( ( AttributeTypeWrapper ) element ).getAttributeType().getSchema() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if ( element instanceof ObjectClassWrapper )
            {
                label += "  [" + ( ( ObjectClassWrapper ) element ).getObjectClass().getSchema() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return label;
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
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_FOLDER_AT ).createImage();
                case OBJECT_CLASS:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID,
                        PluginConstants.IMG_FOLDER_OC ).createImage();
                case NONE:
                    return AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, PluginConstants.IMG_FOLDER )
                        .createImage();
            }
        }

        // Default
        return null;
    }
}
