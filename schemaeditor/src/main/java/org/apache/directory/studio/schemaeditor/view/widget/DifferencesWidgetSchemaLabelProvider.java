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
package org.apache.directory.studio.schemaeditor.view.widget;


import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.model.AttributeTypeImpl;
import org.apache.directory.studio.schemaeditor.model.ObjectClassImpl;
import org.apache.directory.studio.schemaeditor.model.Schema;
import org.apache.directory.studio.schemaeditor.model.difference.AttributeTypeDifference;
import org.apache.directory.studio.schemaeditor.model.difference.ObjectClassDifference;
import org.apache.directory.studio.schemaeditor.model.difference.SchemaDifference;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This class implements the LabelProvider for the SchemaView.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DifferencesWidgetSchemaLabelProvider extends LabelProvider
{
    /** The preferences store */
    private IPreferenceStore store;


    /**
     * Creates a new instance of DifferencesWidgetSchemaLabelProvider.
     */
    public DifferencesWidgetSchemaLabelProvider()
    {
        store = Activator.getDefault().getPreferenceStore();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object element )
    {
        String label = ""; //$NON-NLS-1$

        int labelValue = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_LABEL );
        boolean abbreviate = store.getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE );
        int abbreviateMaxLength = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_ABBREVIATE_MAX_LENGTH );
        boolean secondaryLabelDisplay = store.getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_DISPLAY );
        int secondaryLabelValue = store.getInt( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL );
        boolean secondaryLabelAbbreviate = store
            .getBoolean( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE );
        int secondaryLabelAbbreviateMaxLength = store
            .getInt( PluginConstants.PREFS_SCHEMA_VIEW_SECONDARY_LABEL_ABBREVIATE_MAX_LENGTH );

        if ( element instanceof SchemaDifference )
        {
            SchemaDifference sd = ( SchemaDifference ) element;

            switch ( sd.getType() )
            {
                case ADDED:
                    return ( ( Schema ) sd.getDestination() ).getName();
                case MODIFIED:
                    return ( ( Schema ) sd.getDestination() ).getName();
                case REMOVED:
                    return ( ( Schema ) sd.getSource() ).getName();
                case IDENTICAL:
                    return ( ( Schema ) sd.getDestination() ).getName();
            }
        }
        else if ( element instanceof AttributeTypeDifference )
        {
            AttributeTypeDifference atd = ( AttributeTypeDifference ) element;

            AttributeTypeImpl at = null;

            switch ( atd.getType() )
            {
                case ADDED:
                    at = ( ( AttributeTypeImpl ) atd.getDestination() );
                    break;
                case MODIFIED:
                    at = ( ( AttributeTypeImpl ) atd.getDestination() );
                    break;
                case REMOVED:
                    at = ( ( AttributeTypeImpl ) atd.getSource() );
                    break;
                case IDENTICAL:
                    at = ( ( AttributeTypeImpl ) atd.getDestination() );
                    break;
            }

            // Label
            if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
            {
                String[] names = at.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
            {
                String[] names = at.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = ViewUtils.concateAliases( names );
                }
                else
                {
                    label = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
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
                    label = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                }
            }

            // Abbreviate
            if ( abbreviate && ( abbreviateMaxLength < label.length() ) )
            {
                label = label.substring( 0, abbreviateMaxLength ) + "..."; //$NON-NLS-1$
            }
        }
        else if ( element instanceof ObjectClassDifference )
        {
            ObjectClassDifference ocd = ( ObjectClassDifference ) element;

            ObjectClassImpl oc = null;

            switch ( ocd.getType() )
            {
                case ADDED:
                    oc = ( ( ObjectClassImpl ) ocd.getDestination() );
                    break;
                case MODIFIED:
                    oc = ( ( ObjectClassImpl ) ocd.getDestination() );
                    break;
                case REMOVED:
                    oc = ( ( ObjectClassImpl ) ocd.getSource() );
                    break;
                case IDENTICAL:
                    oc = ( ( ObjectClassImpl ) ocd.getDestination() );
                    break;
            }

            // Label
            if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
            {
                String[] names = oc.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = names[0];
                }
                else
                {
                    label = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
            {
                String[] names = oc.getNamesRef();
                if ( ( names != null ) && ( names.length > 0 ) )
                {
                    label = ViewUtils.concateAliases( names );
                }
                else
                {
                    label = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                }
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
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
                    label = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
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

            return folder.getName() + " (" + folder.getChildren().size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Secondary Label
        if ( secondaryLabelDisplay )
        {
            String secondaryLabel = ""; //$NON-NLS-1$
            if ( element instanceof AttributeTypeDifference )
            {
                AttributeTypeDifference atd = ( AttributeTypeDifference ) element;

                AttributeTypeImpl at = null;

                switch ( atd.getType() )
                {
                    case ADDED:
                        at = ( ( AttributeTypeImpl ) atd.getDestination() );
                        break;
                    case MODIFIED:
                        at = ( ( AttributeTypeImpl ) atd.getDestination() );
                        break;
                    case REMOVED:
                        at = ( ( AttributeTypeImpl ) atd.getSource() );
                        break;
                    case IDENTICAL:
                        at = ( ( AttributeTypeImpl ) atd.getDestination() );
                        break;
                }

                if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
                {
                    String[] names = at.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = names[0];
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
                {
                    String[] names = at.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = ViewUtils.concateAliases( names );
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_OID )
                {
                    secondaryLabel = at.getOid();
                }
            }
            else if ( element instanceof ObjectClassDifference )
            {
                ObjectClassDifference ocd = ( ObjectClassDifference ) element;

                ObjectClassImpl oc = null;

                switch ( ocd.getType() )
                {
                    case ADDED:
                        oc = ( ( ObjectClassImpl ) ocd.getDestination() );
                        break;
                    case MODIFIED:
                        oc = ( ( ObjectClassImpl ) ocd.getDestination() );
                        break;
                    case REMOVED:
                        oc = ( ( ObjectClassImpl ) ocd.getSource() );
                        break;
                    case IDENTICAL:
                        oc = ( ( ObjectClassImpl ) ocd.getDestination() );
                        break;
                }

                if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_FIRST_NAME )
                {
                    String[] names = oc.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = names[0];
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
                    }
                }
                else if ( secondaryLabelValue == PluginConstants.PREFS_SCHEMA_VIEW_LABEL_ALL_ALIASES )
                {
                    String[] names = oc.getNamesRef();
                    if ( ( names != null ) && ( names.length > 0 ) )
                    {
                        secondaryLabel = ViewUtils.concateAliases( names );
                    }
                    else
                    {
                        secondaryLabel = Messages.getString( "DifferencesWidgetSchemaLabelProvider.None" ); //$NON-NLS-1$
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

        return label;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object element )
    {
        if ( element instanceof SchemaDifference )
        {
            SchemaDifference sd = ( SchemaDifference ) element;
            switch ( sd.getType() )
            {
                case ADDED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_SCHEMA_ADD );
                case MODIFIED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_SCHEMA_MODIFY );
                case REMOVED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_SCHEMA_REMOVE );
                case IDENTICAL:
                    return Activator.getDefault().getImage( PluginConstants.IMG_SCHEMA );
            }
        }
        else if ( element instanceof AttributeTypeDifference )
        {
            AttributeTypeDifference atd = ( AttributeTypeDifference ) element;
            switch ( atd.getType() )
            {
                case ADDED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_ATTRIBUTE_TYPE_ADD );
                case MODIFIED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_ATTRIBUTE_TYPE_MODIFY );
                case REMOVED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_ATTRIBUTE_TYPE_REMOVE );
                case IDENTICAL:
                    return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
            }
        }
        else if ( element instanceof ObjectClassDifference )
        {
            ObjectClassDifference ocd = ( ObjectClassDifference ) element;
            switch ( ocd.getType() )
            {
                case ADDED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_OBJECT_CLASS_ADD );
                case MODIFIED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_OBJECT_CLASS_MODIFY );
                case REMOVED:
                    return Activator.getDefault().getImage( PluginConstants.IMG_DIFFERENCE_OBJECT_CLASS_REMOVE );
                case IDENTICAL:
                    return Activator.getDefault().getImage( PluginConstants.IMG_OBJECT_CLASS );
            }
        }
        else if ( element instanceof Folder )
        {
            Folder folder = ( Folder ) element;

            switch ( folder.getType() )
            {
                case ATTRIBUTE_TYPE:
                    return Activator.getDefault().getImage( PluginConstants.IMG_FOLDER_AT );
                case OBJECT_CLASS:
                    return Activator.getDefault().getImage( PluginConstants.IMG_FOLDER_OC );
                case NONE:
                    return Activator.getDefault().getImage( PluginConstants.IMG_FOLDER );
            }
        }

        // Default
        return null;
    }
}
