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

package org.apache.directory.studio.schemaeditor.view.dialogs;


import java.util.List;

import org.apache.directory.api.ldap.model.schema.AttributeType;
import org.apache.directory.studio.schemaeditor.Activator;
import org.apache.directory.studio.schemaeditor.PluginConstants;
import org.apache.directory.studio.schemaeditor.view.ViewUtils;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;


/**
 * This class is the Label Provider for the Attribute Types Viewer of the AttributeTypeSelectionDialog.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AttributeTypeSelectionDialogLabelProvider extends LabelProvider
{
    /**
     * {@inheritDoc}
     */
    public Image getImage( Object element )
    {
        if ( element instanceof AttributeType )
        {
            return Activator.getDefault().getImage( PluginConstants.IMG_ATTRIBUTE_TYPE );
        }

        // Default
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getText( Object element )
    {
        if ( element instanceof AttributeType )
        {
            AttributeType at = ( AttributeType ) element;

            List<String> names = at.getNames();
            if ( ( names != null ) && ( names.size() > 0 ) )
            {
                return ViewUtils.concateAliases( names ) + "  -  (" + at.getOid() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
                return NLS
                    .bind(
                        Messages.getString( "AttributeTypeSelectionDialogLabelProvider.None" ), new String[] { at.getOid() } ); //$NON-NLS-1$
            }
        }

        // Default
        return null;
    }
}
