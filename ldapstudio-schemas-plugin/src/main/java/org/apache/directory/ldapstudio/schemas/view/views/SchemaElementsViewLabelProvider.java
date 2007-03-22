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

package org.apache.directory.ldapstudio.schemas.view.views;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.view.ViewUtils;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.ITreeNode;
import org.apache.directory.ldapstudio.schemas.view.views.wrappers.ObjectClassWrapper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the label provider for the Schema Elements View.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemaElementsViewLabelProvider extends LabelProvider
{
    /** The preferences store */
    private IPreferenceStore store;


    /**
     * Creates a new instance of SchemaElementsViewLabelProvider.
     */
    public SchemaElementsViewLabelProvider()
    {
        store = Activator.getDefault().getPreferenceStore();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object obj )
    {
        String label = "";

        int labelValue = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL );
        boolean abbreviate = store.getBoolean( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_ABBREVIATE );
        int abbreviateMaxLength = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_ABBREVIATE_MAX_LENGTH );
        boolean secondaryLabelDisplay = store
            .getBoolean( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL_DISPLAY );
        int secondaryLabelValue = store.getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL );
        boolean secondaryLabelAbbreviate = store
            .getBoolean( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL_ABBREVIATE );
        int secondaryLabelAbbreviateMaxLength = store
            .getInt( PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );

        if ( obj instanceof AttributeTypeWrapper )
        {
            if ( labelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_FIRST_NAME )
            {
                label = ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getNames()[0];
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_ALL_ALIASES )
            {
                label = ViewUtils.concateAliases( ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getNames() );
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_OID )
            {
                label = ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getOid();
            }
            else
            // Default
            {
                label = ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getNames()[0];
            }
        }
        else if ( obj instanceof ObjectClassWrapper )
        {
            if ( labelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_FIRST_NAME )
            {
                label = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames()[0];
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_ALL_ALIASES )
            {
                label = ViewUtils.concateAliases( ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames() );
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_OID )
            {
                label = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getOid();
            }
            else
            // Default
            {
                label = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames()[0];
            }
        }
        else
        // Default
        {
            label = obj.toString();
        }

        if ( abbreviate && ( abbreviateMaxLength < label.length() ) )
        {
            label = label.substring( 0, abbreviateMaxLength ) + "...";
        }

        if ( secondaryLabelDisplay )
        {
            String secondaryLabel = "";
            if ( obj instanceof AttributeTypeWrapper )
            {
                if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_FIRST_NAME )
                {
                    secondaryLabel = ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getNames()[0];
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_ALL_ALIASES )
                {
                    secondaryLabel = ViewUtils.concateAliases( ( ( AttributeTypeWrapper ) obj ).getMyAttributeType()
                        .getNames() );
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_OID )
                {
                    secondaryLabel = ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getOid();
                }
            }
            else if ( obj instanceof ObjectClassWrapper )
            {
                if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_FIRST_NAME )
                {
                    secondaryLabel = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames()[0];
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_ALL_ALIASES )
                {
                    secondaryLabel = ViewUtils.concateAliases( ( ( ObjectClassWrapper ) obj ).getMyObjectClass()
                        .getNames() );
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_ELEMENTS_VIEW_LABEL_OID )
                {
                    secondaryLabel = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getOid();
                }
            }

            if ( secondaryLabelAbbreviate && ( secondaryLabelAbbreviateMaxLength < secondaryLabel.length() ) )
            {
                secondaryLabel = secondaryLabel.substring( 0, secondaryLabelAbbreviateMaxLength ) + "...";
            }

            label += "   [" + secondaryLabel + "]";
        }

        return label;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object obj )
    {
        if ( obj instanceof ITreeNode )
        {
            return ( ( ITreeNode ) obj ).getImage();
        }

        // Default
        return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK );
    }
}
