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


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.apache.directory.studio.schemaeditor.view.wrappers.AttributeTypeWrapper;
import org.apache.directory.studio.schemaeditor.view.wrappers.ObjectClassWrapper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the LabelProvider for the Hierarchy View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class HierarchyViewLabelProvider extends LabelProvider
{
    /** The preferences store */
    private IPreferenceStore store;

    /** The TreeViewer */
    private TreeViewer viewer;


    /**
     * Creates a new instance of SchemasViewLabelProvider.
     */
    public HierarchyViewLabelProvider( TreeViewer viewer )
    {
        store = Activator.getDefault().getPreferenceStore();
        this.viewer = viewer;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object obj )
    {
        String label = ""; //$NON-NLS-1$

        int labelValue = store.getInt( PluginConstants.PREFS_HIERARCHY_VIEW_LABEL );
        boolean abbreviate = store.getBoolean( PluginConstants.PREFS_HIERARCHY_VIEW_ABBREVIATE );
        int abbreviateMaxLength = store.getInt( PluginConstants.PREFS_HIERARCHY_VIEW_ABBREVIATE_MAX_LENGTH );
        boolean secondaryLabelDisplay = store.getBoolean( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_DISPLAY );
        int secondaryLabelValue = store.getInt( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL );
        boolean secondaryLabelAbbreviate = store
            .getBoolean( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_ABBREVIATE );
        int secondaryLabelAbbreviateMaxLength = store
            .getInt( PluginConstants.PREFS_HIERARCHY_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );

        if ( obj instanceof AttributeTypeWrapper )
        {
            AttributeTypeImpl at = ( ( AttributeTypeWrapper ) obj ).getAttributeType();

            // Label
            if ( labelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_FIRST_NAME )
            {
                String[] names = at.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_ALL_ALIASES )
            {
                String[] names = at.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = ViewUtils.concateAliases( names );
                }
                else
                {
                    label = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_OID )
            {
                label = at.getOid();
            }
            else
            // Default
            {
                String[] names = at.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                }
            }

            // Abbreviate
            if ( abbreviate && ( abbreviateMaxLength < label.length() ) )
            {
                label = label.substring( 0, abbreviateMaxLength ) + "..."; //$NON-NLS-1$
            }
        }
        else if ( obj instanceof ObjectClassWrapper )
        {
            ObjectClassImpl oc = ( ( ObjectClassWrapper ) obj ).getObjectClass();

            // Label
            if ( labelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_FIRST_NAME )
            {
                String[] names = oc.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_ALL_ALIASES )
            {
                String[] names = oc.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = ViewUtils.concateAliases( names );
                }
                else
                {
                    label = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_OID )
            {
                label = oc.getOid();
            }
            else
            // Default
            {
                String[] names = oc.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                }
            }

            // Abbreviate
            if ( abbreviate && ( abbreviateMaxLength < label.length() ) )
            {
                label = label.substring( 0, abbreviateMaxLength ) + "..."; //$NON-NLS-1$
            }
        }

        // Secondary Label
        if ( secondaryLabelDisplay )
        {
            String secondaryLabel = ""; //$NON-NLS-1$
            if ( obj instanceof AttributeTypeWrapper )
            {
                AttributeTypeImpl at = ( ( AttributeTypeWrapper ) obj ).getAttributeType();

                if ( secondaryLabelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_FIRST_NAME )
                {
                    String[] names = at.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = names[0];
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_ALL_ALIASES )
                {
                    String[] names = at.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = ViewUtils.concateAliases( names );
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_OID )
                {
                    secondaryLabel = at.getOid();
                }
            }
            else if ( obj instanceof ObjectClassWrapper )
            {
                ObjectClassImpl oc = ( ( ObjectClassWrapper ) obj ).getObjectClass();

                if ( secondaryLabelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_FIRST_NAME )
                {
                    String[] names = oc.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = names[0];
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_ALL_ALIASES )
                {
                    String[] names = oc.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = ViewUtils.concateAliases( names );
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "HierarchyViewLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_HIERARCHY_VIEW_LABEL_OID )
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

        return label;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object obj )
    {
        if ( obj instanceof AttributeTypeWrapper )
        {
            if ( ( ( AttributeTypeWrapper ) obj ).getAttributeType().equals( viewer.getInput() ) )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE_HIERARCHY_SELECTED );
            }
            else
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
            }
        }
        else if ( obj instanceof ObjectClassWrapper )
        {

            if ( ( ( ObjectClassWrapper ) obj ).getObjectClass().equals( viewer.getInput() ) )
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_OBJECT_CLASS_HIERARCHY_SELECTED );
            }
            else
            {
                return Activator.getDefault().getImage( PluginConstants.IMG_OBJECT_CLASS );
            }
        }

        // Default
        return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK );
    }
}
