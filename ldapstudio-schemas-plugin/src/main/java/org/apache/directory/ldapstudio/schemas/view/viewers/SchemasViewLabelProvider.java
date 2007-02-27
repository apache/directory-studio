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

package org.apache.directory.ldapstudio.schemas.view.viewers;


import org.apache.directory.ldapstudio.schemas.Activator;
import org.apache.directory.ldapstudio.schemas.PluginConstants;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.AttributeTypeWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.DisplayableTreeElement;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.ObjectClassWrapper;
import org.apache.directory.ldapstudio.schemas.view.viewers.wrappers.SchemaWrapper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * This class implements the LabelProvider for the Schemas View.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class SchemasViewLabelProvider extends LabelProvider
{
    /** The preferences store */
    private IPreferenceStore store;


    /**
     * Creates a new instance of SchemasViewLabelProvider.
     */
    public SchemasViewLabelProvider()
    {
        store = Activator.getDefault().getPreferenceStore();
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText( Object obj )
    {
        String label = "";

        int labelValue = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_LABEL );
        boolean abbreviate = store.getBoolean( PluginConstants.PREFS_SCHEMAS_VIEW_ABBREVIATE );
        int abbreviateMaxLength = store.getInt( PluginConstants.PREFS_SCHEMAS_VIEW_ABBREVIATE_MAX_LENGTH );

        if ( obj instanceof AttributeTypeWrapper )
        {
            if ( labelValue == PluginConstants.PREFS_SCHEMAS_VIEW_LABEL_FIRST_NAME )
            {
                label = ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getNames()[0];
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMAS_VIEW_LABEL_ALL_ALIASES )
            {
                label = concateNames( ( ( AttributeTypeWrapper ) obj ).getMyAttributeType().getNames() );
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMAS_VIEW_LABEL_OID )
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
            if ( labelValue == PluginConstants.PREFS_SCHEMAS_VIEW_LABEL_FIRST_NAME )
            {
                label = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames()[0];
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMAS_VIEW_LABEL_ALL_ALIASES )
            {
                label = concateNames( ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames() );
            }
            else if ( labelValue == PluginConstants.PREFS_SCHEMAS_VIEW_LABEL_OID )
            {
                label = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getOid();
            }
            else
            // Default
            {
                label = ( ( ObjectClassWrapper ) obj ).getMyObjectClass().getNames()[0];
            }
        }
        else if ( obj instanceof SchemaWrapper )
        {
            label = ( ( SchemaWrapper ) obj ).getMySchema().getName();
        }
        else
        // Default
        {
            label = obj.toString();
        }

        if ( abbreviate && ( abbreviateMaxLength < label.length() )
            && ( ( obj instanceof ObjectClassWrapper ) || ( obj instanceof AttributeTypeWrapper ) ) )
        {
            label = label.substring( 0, abbreviateMaxLength ) + "...";
        }

        return label;
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
     */
    public Image getImage( Object obj )
    {
        if ( obj instanceof DisplayableTreeElement )
        {
            return ( ( DisplayableTreeElement ) obj ).getDisplayImage();
        }

        // Default
        return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK );
    }


    /**
     * Concatenates all aliases in a String format
     *
     * @param aliases
     *      the aliases to concatenate
     * @return
     *      a String representing all aliases
     */
    private String concateNames( String[] aliases )
    {
        StringBuffer sb = new StringBuffer();

        sb.append( aliases[0] );

        for ( int i = 1; i < aliases.length; i++ )
        {
            sb.append( ", " );
            sb.append( aliases[i] );
        }

        return sb.toString();
    }
}
